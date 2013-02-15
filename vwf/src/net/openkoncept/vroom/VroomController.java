/*
 *  Copyright (C) 2008 Farrukh Ijaz, OpenKoncept Inc.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.openkoncept.vroom;

import net.openkoncept.json.JSONException;
import net.openkoncept.json.JSONObject;
import net.openkoncept.vroom.config.Element;
import net.openkoncept.vroom.config.Navigation;
import net.openkoncept.vroom.config.VroomConfig;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import net.openkoncept.vroom.config.Form;

/**
 * <p> VroomServlet is one of the core classes of Vroom framework. It's is
 * responsible to serve javasciprts as requested and to invoke methods of the
 * bean classes bound to forms and various events. </p> <p> VroomServlet accepts
 * following request parameters: <ul> <li>rt - Request Type, it can be "js" (to
 * serve javascript) or "invoke" (method in a java bean)</li> <li>method - It's
 * applicable only if the Request type is "js" and possible values are "src" (a
 * *.js file located on a the disk) or "generate" (to generate the javascript at
 * runtime for the request uri)</li> <li>uri - Request URI</li> <li>bean-class -
 * The java bean class which contains the method to invoke. This is applicable
 * only if the Request type is "invoke"</li> <li>handle - A method defined in
 * the java bean class. The valid signature of the method is <br/>
 * <code>public &lt;&gt;a valid java object or void&lt;method-name&gt;(HttpServletRequest, HttpServletResponse)</code>
 * <br/> A valid java object is String, Object, Map, Array, JSONObject </li>
 * <li>var - The variable name for the object to look for in a scope</li>
 * <li>scope - The scope of the object (valid values are "application",
 * "session" or "request")</li> </ul> </p>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomController extends HttpServlet {

    public static final String RT = "v-rt";
    public static final String METHOD = "v-method";
    public static final String RT_JS = "js";
    public static final String RT_INVOKE = "invoke";
    public static final String RT_SUBMIT = "submit";
    public static final String METHOD_FILE = "file";
    public static final String FILE_SRC = "v-src";
    public static final String METHOD_GENERATE = "generate";
    public static final String GENERATE_URI = "v-uri";
    public static final String ID = "v-id";
    public static final String BEAN_CLASS = "v-bean-class";
    public static final String CALLER_URI = "v-caller-uri";
    public static final String VAR = "v-var";
    public static final String SCOPE = "v-scope";
    public static final String SCOPE_APPLICATION = "application";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_REQUEST = "request";
    public static final String SESSION_ID = "jsessionid";
    public static final String $CONTEXT_PATH$ = "__CONTEXT_PATH__";
    public static final String $SERVLET_PATH$ = "__SERVLET_PATH__";
    public static final String $SESSION_ID$ = "__SESSION_ID__";
    public static final String $REQUEST_PATH$ = "__REQUEST_PATH__";
    public static final String HTML_MIME_TYPE = "text/html";
    public static final String JS_MIME_TYPE = "text/javascript";
    public static final String JSON_MIME_TYPE = "application/json";
    public static final String FILE_UPLOAD_SIZE_THRESHOLD = "file-upload-size-threshold";
    public static final String FILE_UPLOAD_TEMP_FOLDER = "file-upload-temp-folder";
    public static final String CHARSET_UTF8 = "charset=UTF-8";
    public static final String CHARSET = "charset=";
    public static Map appBeanMap = Collections.synchronizedMap(new LinkedHashMap());

    /**
     * <p> This method analyzes the request and delegate control to other
     * methods for processing. </p>
     *
     * @param request - HttpServletRequest
     * @param response - HttpServletResponse
     * @throws ServletException - ServletException
     * @throws IOException - IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String sessionId = request.getSession().getId();
        String requestPath = contextPath + servletPath + ";" + SESSION_ID + "=" + sessionId;
        String uri = request.getParameter(GENERATE_URI);
        String rt = request.getParameter(RT);
        String method = request.getParameter(METHOD);

        List parts = null;
        Map paramMap = null;
        if (ServletFileUpload.isMultipartContent(request)) {
            paramMap = new HashMap();
            DiskFileItemFactory factory = new DiskFileItemFactory(fileUploadSizeThreshold, fileUploadTempFolder);
            ServletFileUpload sfu = new ServletFileUpload(factory);
            try {
                parts = sfu.parseRequest(request);
                Iterator iter = parts.iterator();
                while (iter.hasNext()) {
                    FileItem fi = (FileItem) iter.next();
//                    if(fi.isFormField()) {
//                        List l = (List) paramMap.get(fi.getFieldName());
//                        if(l == null) {
//                            l = new ArrayList();
//                            paramMap.put(fi.getFieldName(), l);
//                        }
//                        l.add(fi.getString());
//                    } else {
//                        paramMap.put(fi.getFieldName(), fi);
//                    }
                    List l = (List) paramMap.get(fi.getFieldName());
                    if (l == null) {
                        l = new ArrayList();
                        paramMap.put(fi.getFieldName(), l);
                    }
                    if (fi.isFormField()) {
                        l.add(fi.getString());
                    } else {
                        l.add(fi);
                    }
                }
            } catch (FileUploadException e) {
                throw new ServletException("Unable to part multipart/form-data", e);
            }
            if (parts != null) {
                rt = (String) ((ArrayList) paramMap.get(RT)).get(0);
                method = (String) ((ArrayList) paramMap.get(METHOD)).get(0);
            }
        }

        if (RT_JS.equals(rt)) {
            if (METHOD_FILE.equals(method)) {
                processRequestJSFile(request, response, contextPath, servletPath, sessionId, requestPath);
            } else if (METHOD_GENERATE.equals(method)) {
                processRequestJSGenerate(request, response, contextPath, servletPath, sessionId, uri);
            }
        } else if (RT_INVOKE.equals(rt)) {
            processRequestInvoke(request, response);
        } else if (RT_SUBMIT.equals(rt)) {
            processRequestSubmit(request, response, paramMap);
        }
    }

    private void processRequestSubmit(HttpServletRequest request, HttpServletResponse response, Map paramMap) throws ServletException {
        // change the URI below with the actual page who invoked the request.

        String uri = request.getParameter(CALLER_URI);
        String id = request.getParameter(ID);
        String method = request.getParameter(METHOD);
        String beanClass = request.getParameter(BEAN_CLASS);
        String var = request.getParameter(VAR);
        String scope = request.getParameter(SCOPE);

        if (paramMap != null) {
            uri = (String) ((ArrayList) paramMap.get(CALLER_URI)).get(0);
            id = (String) ((ArrayList) paramMap.get(ID)).get(0);
            method = (String) ((ArrayList) paramMap.get(METHOD)).get(0);
            beanClass = (String) ((ArrayList) paramMap.get(BEAN_CLASS)).get(0);
            var = (String) ((ArrayList) paramMap.get(VAR)).get(0);
            scope = (String) ((ArrayList) paramMap.get(SCOPE)).get(0);
        }

        Object beanObject;
        String outcome = null;

        // call form method...

        try {
            Class clazz = Class.forName(beanClass);
            Class elemClass = clazz;
            String eId, eProperty, eBeanClass, eVar, eScope, eFormat;
            // Setting values to beans before calling the method...
            List<Element> elements = VroomConfig.getInstance().getElements(uri, id, method, beanClass, var, scope);
            for (Element e : elements) {
                eId = e.getId();
                eProperty = (e.getProperty() != null) ? e.getProperty() : eId;
                eBeanClass = (e.getBeanClass() != null) ? e.getBeanClass() : beanClass;
                eVar = (e.getVar() != null) ? e.getVar() : var;
                eScope = (e.getBeanClass() != null) ? e.getScope().value() : scope;
                eFormat = e.getFormat();
                if (!elemClass.getName().equals(eBeanClass)) {
                    try {
                        elemClass = Class.forName(eBeanClass);
                    } catch (ClassNotFoundException ex) {
                        throw new ServletException("Failed to load class for element [" + eId + "]", ex);
                    }
                }
                if (elemClass != null) {
                    try {
                        Object obj = getObjectFromScope(request, elemClass, eVar, eScope);
                        if (PropertyUtils.isWriteable(obj, eProperty)) {
                            Class propType = PropertyUtils.getPropertyType(obj, eProperty);
                            Object[] paramValues = request.getParameterValues(eId);
                            Object value = null;
                            value = getParameterValue(propType, eId, paramValues, eFormat, paramMap);
                            PropertyUtils.setProperty(obj, eProperty, value);
                        }
                    } catch (InstantiationException ex) {
                        throw new ServletException("Failed to create object of type [" + eBeanClass + "]", ex);
                    } catch (IllegalAccessException ex) {
                        throw new ServletException("Failed to create object of type [" + eBeanClass + "]", ex);
                    }
                }
            }
            // Calling the method...
            beanObject = getObjectFromScope(request, clazz, var, scope);
            Class[] signature = new Class[]{HttpServletRequest.class, HttpServletResponse.class};
            Method m = clazz.getMethod(method, signature);
            Object object = m.invoke(beanObject, new Object[]{request, response});
            // Getting updating values from beans to pass as postback for the forward calls.
            Map<String, Object> postbackMap = new HashMap<String, Object>();
            for(Element e : elements) {
                eId = e.getId();
                eProperty = (e.getProperty() != null) ? e.getProperty() : eId;
                eBeanClass = (e.getBeanClass() != null) ? e.getBeanClass() : beanClass;
                eVar = (e.getVar() != null) ? e.getVar() : var;
                eScope = (e.getBeanClass() != null) ? e.getScope().value() : scope;
                eFormat = e.getFormat();
                if (!elemClass.getName().equals(eBeanClass)) {
                    try {
                        elemClass = Class.forName(eBeanClass);
                    } catch (ClassNotFoundException ex) {
                        throw new ServletException("Failed to load class for element [" + eId + "]", ex);
                    }
                }
                if (elemClass != null) {
                    try {
                        Object obj = getObjectFromScope(request, elemClass, eVar, eScope);
                        if (PropertyUtils.isReadable(obj, eProperty)) {
//                            Class propType = PropertyUtils.getPropertyType(obj, eProperty);
//                            Object[] paramValues = request.getParameterValues(eId);
//                            Object value = null;
//                            value = getParameterValue(propType, eId, paramValues, eFormat, paramMap);
//                            PropertyUtils.setProperty(obj, eProperty, value);
                            Object value = PropertyUtils.getProperty(obj, eProperty);
                            postbackMap.put(eId, value);
                        }
                    } catch (InstantiationException ex) {
                        throw new ServletException("Failed to create object of type [" + eBeanClass + "]", ex);
                    } catch (IllegalAccessException ex) {
                        throw new ServletException("Failed to create object of type [" + eBeanClass + "]", ex);
                    }
                }
            }
            if (object == null || object instanceof String) {
                outcome = (String) object;
                Navigation n = VroomConfig.getInstance().getNavigation(uri, id, method, beanClass, var, scope, outcome);
                if (n == null) {
                    n = VroomConfig.getInstance().getNavigation(uri, id, method, beanClass, var, scope, "default");
                }
                if (n != null) {
                    String url = n.getUrl();
                    if (url == null) {
                        url = "/";
                    }
                    url = url.replaceAll("#\\{contextPath}", request.getContextPath());
                    if (n.isForward()) {
                        String ctxPath = request.getContextPath();
                        if (url.startsWith(ctxPath)) {
                            url = url.substring(ctxPath.length());
                        }
                        PrintWriter pw = response.getWriter();
                        VroomResponseWrapper responseWrapper = new VroomResponseWrapper(response);
                        RequestDispatcher rd = request.getRequestDispatcher(url);
                        rd.forward(request, responseWrapper);
                        StringBuffer sbHtml = new StringBuffer(responseWrapper.toString());
                        VroomHtmlProcessor.addHeadMetaTags(sbHtml, uri, VroomConfig.getInstance());
                        VroomHtmlProcessor.addHeadLinks(sbHtml, uri, VroomConfig.getInstance());
                        VroomHtmlProcessor.addRequiredScripts(sbHtml, request);
                        VroomHtmlProcessor.addInitScript(sbHtml, uri, request);
                        VroomHtmlProcessor.addHeadScripts(sbHtml, uri, VroomConfig.getInstance(), request);
                        VroomHtmlProcessor.addStylesheets(sbHtml, uri, VroomConfig.getInstance(), request);
                        // Add postback values to webpage through javascript...
                        VroomHtmlProcessor.addPostbackScript(sbHtml, uri, request, postbackMap);
                        String html = sbHtml.toString().replaceAll("#\\{contextPath}", request.getContextPath());
                        response.setContentLength(html.length());
                        pw.print(html);
                    } else {
                        response.sendRedirect(url);
                    }
                } else {
                    throw new ServletException("No navigation found for outcome [" + outcome + "]");
                }
            }

        } catch (ClassNotFoundException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (InstantiationException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (IllegalAccessException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (NoSuchMethodException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (InvocationTargetException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (IOException ex) {
            throw new ServletException("Navigation Failed for outcome [" + outcome + "]", ex);
        }
    }

    private Object getParameterValue(Class propType, String id, Object[] paramValues, String format, Map paramMap) {
        Object value = null;
        if (paramValues == null) {
            if (paramMap != null) {
                value = paramMap.get(id);
                if (value instanceof List) {
                    List l = (List) value;
                    paramValues = l.toArray();
                    value = null;
                }
            }
        }
        SimpleDateFormat sdf = null;
        try {
            String typeName = propType.getName();
            if (typeName.indexOf("java.lang.String") != -1) {
                if (!propType.isArray()) {
                    value = paramValues[0];
                } else {
                    value = new String[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = paramValues[i];
                    }
                }
            } else if (typeName.indexOf("java.lang.Character") != -1) {
                if (!propType.isArray()) {
                    value = new Character(((String) paramValues[0]).charAt(0));
                } else {
                    value = new Character[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = new Character(((String) paramValues[i]).charAt(0));
                    }
                }
            } else if (typeName.indexOf("java.lang.Short") != -1) {
                if (!propType.isArray()) {
                    value = Short.valueOf((String) paramValues[0]);
                } else {
                    value = new Short[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Short.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.lang.Integer") != -1) {
                if (!propType.isArray()) {
                    value = Integer.valueOf((String) paramValues[0]);
                } else {
                    value = new Integer[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Integer.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.lang.Long") != -1) {
                if (!propType.isArray()) {
                    value = Long.valueOf((String) paramValues[0]);
                } else {
                    value = new Long[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Long.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.lang.Float") != -1) {
                if (!propType.isArray()) {
                    value = Float.valueOf((String) paramValues[0]);
                } else {
                    value = new Float[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Float.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.lang.Double") != -1) {
                if (!propType.isArray()) {
                    value = Double.valueOf((String) paramValues[0]);
                } else {
                    value = new Double[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Double.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.lang.Boolean") != -1) {
                if (!propType.isArray()) {
                    value = Boolean.valueOf((String) paramValues[0]);
                } else {
                    value = new Boolean[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = Boolean.valueOf((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("java.util.Date") != -1) {
                if (format != null) {
                    sdf = new SimpleDateFormat(format);
                } else {
                    sdf = new SimpleDateFormat();
                }
                if (!propType.isArray()) {
                    value = sdf.parse((String) paramValues[0]);
                } else {
                    value = new Date[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = sdf.parse((String) paramValues[i]);
                    }
                }
            } else if (typeName.indexOf("org.apache.commons.fileupload.FileItem") != -1) {
                if (!propType.isArray()) {
                    value = paramValues[0];
                } else {
                    value = new org.apache.commons.fileupload.FileItem[paramValues.length];
                    for (int i = 0; i < paramValues.length; i++) {
                        ((Object[]) value)[i] = paramValues[i];
                    }
                }
            }
        } catch (Exception ex) {
            value = null;
        }
        return value;
    }

    private Object getObjectFromScope(HttpServletRequest request, Class clazz, String var, String scope) throws IllegalAccessException, InstantiationException {
        Object beanObject = null;
        if (SCOPE_APPLICATION.equals(scope)) {
            beanObject = appBeanMap.get(var);
            if (beanObject == null) {
                beanObject = clazz.newInstance();
                appBeanMap.put(var, beanObject);
            }
        } else if (SCOPE_SESSION.equals(scope)) {
            beanObject = request.getSession().getAttribute(var);
            if (beanObject == null) {
                beanObject = clazz.newInstance();
                request.getSession().setAttribute(var, beanObject);
            }
        } else if (SCOPE_REQUEST.equals(scope)) {
            beanObject = request.getAttribute(var);
            if (beanObject == null) {
                beanObject = clazz.newInstance();
                request.setAttribute(var, beanObject);
            }
        } else {
            beanObject = clazz.newInstance();
        }
        return beanObject;
    }

    private void processRequestInvoke(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String method = request.getParameter(METHOD);
        String beanClass = request.getParameter(BEAN_CLASS);
        String var = request.getParameter(VAR);
        if (var == null || var.trim().length() == 0) {
            var = beanClass;
        }
        String scope = request.getParameter(SCOPE);
        if (scope == null || scope.trim().length() == 0) {
            if (var.equals(beanClass)) {
                scope = SCOPE_APPLICATION;
            } else {
                scope = SCOPE_SESSION;
            }
        }
        Object beanObject;
        Object object = null;
        try {
            Class clazz = Class.forName(beanClass);
            if (SCOPE_APPLICATION.equals(scope)) {
                beanObject = appBeanMap.get(var);
                if (beanObject == null) {
                    beanObject = clazz.newInstance();
                    appBeanMap.put(var, beanObject);
                }
            } else if (SCOPE_SESSION.equals(scope)) {
                beanObject = request.getSession().getAttribute(var);
                if (beanObject == null) {
                    beanObject = clazz.newInstance();
                    request.getSession().setAttribute(var, beanObject);
                }
            } else if (SCOPE_REQUEST.equals(scope)) {
                beanObject = request.getAttribute(var);
                if (beanObject == null) {
                    beanObject = clazz.newInstance();
                    request.setAttribute(var, beanObject);
                }
            } else {
                beanObject = clazz.newInstance();
            }
            String output;
            if ("getProperties".equals(method)) {
                object = beanObject;
            } else {
                Class[] signature = new Class[]{HttpServletRequest.class, HttpServletResponse.class};
                Method m;
                try {
                    m = clazz.getMethod(method, signature);
                    object = m.invoke(beanObject, new Object[]{request, response});
                } catch (Exception ex) {
                    try {
                        m = clazz.getMethod(method, null);
                        object = m.invoke(beanObject, null);
                    } catch (Exception ex2) {
                        try {
                            signature = new Class[]{HttpServletRequest.class};
                            m = clazz.getMethod(method, signature);
                            object = m.invoke(beanObject, new Object[]{request});
                        } catch (NoSuchMethodException ex3) {
                            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
                        } catch (InvocationTargetException ex3) {
                            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
                        }
                    }
                }
                if (object == null) {
                    object = "";
                }
            }
            JSONObject jo = VroomUtilities.convertObjectToJSONObject(object);
            jo.put("contextPath", request.getContextPath());
            jo.put("sessionId", request.getSession().getId());
            jo.put("locale", VroomUtilities.convertObjectToJSONObject(request.getLocale()));
            output = jo.toString();
            String encoding = request.getHeader("response-encoding");
            if (encoding == null || encoding.trim().length() == 0) {
                encoding = request.getCharacterEncoding();
            }
            response.setContentType(JSON_MIME_TYPE);
            if (encoding != null) {
                VroomUtilities.safeCall(response, "setCharacterEncoding", new Class[]{String.class}, new Object[]{encoding});
//                response.setCharacterEncoding(encoding);
            }
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.write(output);
            } catch (IOException e) {
                throw new ServletException(e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (InstantiationException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (IllegalAccessException ex) {
            throw new ServletException("Invocation Failed for method [" + method + "]", ex);
        } catch (JSONException ex) {
            throw new ServletException("Invalid JSON Object [" + object + "]", ex);
        }
    }

    private void processRequestJSGenerate(HttpServletRequest request, HttpServletResponse response, String contextPath, String servletPath, String sessionId, String uri) throws IOException {
        StringBuffer sbScript = new StringBuffer();
        VroomScriptGenerator sg = new VroomScriptGenerator(VroomConfig.getInstance(), contextPath, servletPath, sessionId, uri);
        sbScript.append(sg.generateScript());
        String encoding = request.getHeader("response-encoding");
        if (encoding == null || encoding.trim().length() == 0) {
            encoding = request.getCharacterEncoding();
        }
        response.setContentType(JS_MIME_TYPE);
        if (encoding != null) {
            VroomUtilities.safeCall(response, "setCharacterEncoding", new Class[]{String.class}, new Object[]{encoding});
//            response.setCharacterEncoding(encoding);
        }
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(sbScript.toString());
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private void processRequestJSFile(HttpServletRequest request, HttpServletResponse response, String contextPath, String servletPath, String sessionId, String requestPath) throws IOException {
        String src = request.getParameter(FILE_SRC);
        if (src != null) {
            String realPath = getServletContext().getRealPath(src);
            BufferedReader reader = new BufferedReader(new FileReader(realPath));
            StringBuffer sbOutput = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                sbOutput.append(line);
                sbOutput.append("\n");
                line = reader.readLine();
            }
            reader.close();

            VroomHtmlProcessor.replace(sbOutput, $CONTEXT_PATH$, contextPath);
            VroomHtmlProcessor.replace(sbOutput, $SERVLET_PATH$, servletPath);
            VroomHtmlProcessor.replace(sbOutput, $SESSION_ID$, sessionId);
            VroomHtmlProcessor.replace(sbOutput, $REQUEST_PATH$, requestPath);
            String encoding = request.getHeader("response-encoding");
            if (encoding == null || encoding.trim().length() == 0) {
                encoding = request.getCharacterEncoding();
            }
            response.setContentType(JS_MIME_TYPE);
            if (encoding != null) {
                VroomUtilities.safeCall(response, "setCharacterEncoding", new Class[]{String.class}, new Object[]{encoding});
//                response.setCharacterEncoding(encoding);
            }
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.write(sbOutput.toString());
            } finally {
                if (out != null) {
                    out.close();
                }
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    protected int fileUploadSizeThreshold;
    protected File fileUploadTempFolder;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            fileUploadSizeThreshold = Integer.parseInt(getInitParameter(FILE_UPLOAD_SIZE_THRESHOLD));
        } catch (NumberFormatException ex) {
            fileUploadSizeThreshold = 32768;
        }
        String folderName = getInitParameter(FILE_UPLOAD_TEMP_FOLDER);
        if (folderName == null) {
            folderName = "/WEB-INF/temp";
        }
        fileUploadTempFolder = new File(servletConfig.getServletContext().getRealPath(folderName));
        if (!fileUploadTempFolder.isDirectory()) {
            try {
                fileUploadTempFolder.mkdirs();
            } catch (Exception ex) {
                throw new ServletException("Failed to create file upload temp folder [" + fileUploadTempFolder + "]", ex);
            }
        } else if (!(fileUploadTempFolder.canRead() && fileUploadTempFolder.canWrite())) {
            throw new ServletException("Application does not read/write permissions on file upload temp folder [" + fileUploadTempFolder + "]");
        }
    }

    public void destroy() {
        super.destroy();
        appBeanMap.clear();
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "VroomServlet";
    }
    // </editor-fold>
}
