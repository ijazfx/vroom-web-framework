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
import net.openkoncept.json.JSONArray;
import org.w3c.dom.Node;
import org.apache.commons.beanutils.PropertyUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyDescriptor;

/**
 * <p>
 * This utility class contains some useful methods used by Vroom framework.
 * </p>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomUtilities {

    /**
     * <p>
     * This method returns the attribute value of an XML node.
     * </p>
     *
     * @param node     - The XML node
     * @param attrName - Attribute name
     * @return - Value of XML attribute
     */
    public static String getXmlAttributeValue(Node node, String attrName) {

        if (node == null) {
            return null;
        }

        Node attrNode = node.getAttributes().getNamedItem(attrName);
        if (attrNode == null) {
            return null;
        }

        return attrNode.getNodeValue().trim();

    }

    /**
     * <p>
     * This method is used to return the content of an XMl element.
     * </p>
     *
     * @param node - XML element
     * @return - the content or empty string in case of exception.
     */
    public static String getXmlText(Node node) {
        try {
            String value = node.getFirstChild().getNodeValue().trim(); // node.getTextContent();
            if (value == null || value.length() == 0) {
                value = node.getFirstChild().getNextSibling().getNodeValue().trim();
            }
            return value;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * <p>
     * This method returns the URI after removing the context from the URI.
     * </p>
     *
     * @param request - The request for which the URI is requested.
     * @return - URI without the context path.
     */
    public static String getUriWithoutContext(ServletRequest request) {
        String uri = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest) request;
            String uriWithContext = hreq.getRequestURI();
            String context = hreq.getContextPath();
            uri = uriWithContext.substring(uriWithContext.indexOf(context) + context.length());
        }
        return uri;
    }

    /**
     * <p>
     * This useful method converts a map to JSONObject.
     * </p>
     *
     * @param map - Any map (preferrably with key/value pairs)
     * @return - JSON object.
     */
    public static JSONObject mapToJSONObject(Map map) {
        JSONObject jo = new JSONObject();
        if (map != null) {
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                try {
                    jo.append(key, map.get(key));
                } catch (JSONException ex) {
                    // Not required!
                }
            }
        }
        return jo;
    }

    /**
     * <p>
     * This method converts an object to JSON object. Use the "string" attribute of the json object to access the value.
     * </p>
     *
     * @param object - Any object
     * @return - JSONOjbect
     */
    public static JSONObject objectToJSONObject(Object object) {
        JSONObject jo = new JSONObject(object);
        try {
            String _string = new String((byte[]) jo.get("bytes"));
            if (object instanceof String) {
                jo = new JSONObject();
                jo.append("string", _string);
            }
        } catch (JSONException ex) {
            // Not required!
        }
        return jo;

    }

    /**
     * <p>
     * This useful method converts a map to JSONObject.
     * </p>
     *
     * @param bundle - ResourceBundle (preferrably with key/value pairs)
     * @return - JSON object.
     */
    public static JSONObject bundleToJSONObject(ResourceBundle bundle) {
        JSONObject jo = new JSONObject();
        if (bundle != null) {
            Enumeration e = bundle.getKeys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                try {
                    jo.append(key, bundle.getString(key));
                } catch (JSONException ex) {
                    // Not required!
                }
            }
        }
        return jo;
    }

    public static JSONObject convertObjectToJSONObject(Object object) throws JSONException {
        JSONObject jo = new JSONObject();
        if (object instanceof Character || object instanceof String || object instanceof Boolean
                || object instanceof Short || object instanceof Integer || object instanceof Long
                || object instanceof Float || object instanceof Double || object instanceof java.util.Date
                || object instanceof java.math.BigDecimal || object instanceof java.math.BigInteger) {
            jo.put("value", getValueForJSONObject(object));
        } else if (object instanceof java.util.Map) {
            Map m = (Map) object;
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                jo.put(key, getValueForJSONObject(m.get(key)));
            }
        } else if (object instanceof Collection) {
            Collection c = (Collection) object;
            Iterator iter = c.iterator();
            JSONArray ja = new JSONArray();
            while (iter.hasNext()) {
                ja.put(getValueForJSONObject(iter.next()));
            }
            jo.put("array", ja);
        } else if (object != null && object.getClass().isArray()) {
            Object[] oa = (Object[]) object;
            JSONArray ja = new JSONArray();
            for (int i = 0; i < oa.length; i++) {
                ja.put(getValueForJSONObject(oa[i]));
            }
            jo.put("array", ja);
        } else if (object instanceof ResourceBundle) {
            ResourceBundle rb = (ResourceBundle) object;
            Enumeration e = rb.getKeys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                Object value = getValueForJSONObject(rb.getObject(key));
                jo.put(key, value);
            }
        } else if (object != null) {
            Class clazz = object.getClass();
            PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(object);
            for (int i = 0; i < pds.length; i++) {
                PropertyDescriptor pd = pds[i];
                String name = pd.getName();
                if (!"class".equals(name) && PropertyUtils.isReadable(object, pd.getName())) {
                    try {
                        Object value = PropertyUtils.getProperty(object, name);
                        jo.put(name, getValueForJSONObject(value));
                    } catch (Exception e) {
                        // Useless...
                    }
                }
            }
        } else {
            jo.put("value", "");
        }
        return jo;
    }

    private static Object getValueForJSONObject(Object object) throws JSONException {
        if (object instanceof Character || object instanceof String || object instanceof Boolean
                || object instanceof Short || object instanceof Integer || object instanceof Long
                || object instanceof Float || object instanceof Double || object instanceof java.util.Date
                || object instanceof java.math.BigDecimal || object instanceof java.math.BigInteger) {
            return object.toString();
        } else {
            return convertObjectToJSONObject(object);
        }
    }

    public static Object safeCall(Object obj, String methodName, Class[] signature, Object[] params) {
        if(obj == null) {
            return null;
        }
        try {
            Method m = obj.getClass().getMethod(methodName, signature);
            return m.invoke(obj, params);
        } catch (Exception e) {
            return null;
        }
    }

}
