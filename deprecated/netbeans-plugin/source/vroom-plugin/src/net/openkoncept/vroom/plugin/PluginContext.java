/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.openkoncept.vroom.plugin.completion.ListItem;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.project.classpath.ProjectClassPathModifier;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author ijazfx
 */
public class PluginContext {

    private static Map<Project, PluginContext> ctxMap = new HashMap<Project, PluginContext>();

    public static boolean addLibrary(Project project, String libraryName) {
        Library library = LibraryManager.getDefault().getLibrary(libraryName);
        Sources s = project.getLookup().lookup(Sources.class);
        SourceGroup[] sources = null;
        boolean extended = false;
        if (null != s) {
            sources = s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        }
        if (library != null) {
            Library[] libraries = new Library[1];
            libraries[0] = library;            
            if (sources != null && sources.length > 0) {
                try {
                    ProjectClassPathModifier.addLibraries(libraries,
                            sources[0].getRootFolder(), ClassPath.COMPILE);
                    extended = true;
                } catch (IOException e) {
                } catch (UnsupportedOperationException e) {
                }
            }
        }
        return extended;
    }

    public static Project findProject(FileObject fob) {
        FileObject pfob = fob;
        while (pfob != null) {
            if (pfob.isFolder() && ProjectManager.getDefault().isProject(pfob)) {
                try {
                    return ProjectManager.getDefault().findProject(pfob);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            pfob = pfob.getParent();
        }
        return null;
    }

    public static FileObject findFileObject(FileObject folder, String fileName) {
        for (FileObject fob : folder.getChildren()) {
            if (fob.getNameExt().equals(fileName)) {
                return fob;
            } else if (fob.isFolder()) {
                return findFileObject(fob, fileName);
            }
        }
        return null;
    }

    public static PluginContext getContext() {
        Node node = TopComponent.getRegistry().getCurrentNodes()[0];
        DataObject dob = node.getCookie(DataObject.class);
        Project project = PluginContext.findProject(dob.getPrimaryFile());
        return getContext(project);// OpenProjects.getDefault().getMainProject());
    }

    public static PluginContext getContext(Project project) {
        PluginContext ctx = ctxMap.get(project);
        if (ctx == null) {
            ctx = new PluginContext(project);
            ctxMap.put(project, ctx);
        }
        return ctx;
    }
    private Project project;
    private List<ListItem> attributeList = new ArrayList<ListItem>();
    private List<ListItem> scopeList = new ArrayList<ListItem>();
    private List<ListItem> eventTypeList = new ArrayList<ListItem>();
    private List<ListItem> nameList = new ArrayList<ListItem>();
    private List<ListItem> stylesheetTypeList = new ArrayList<ListItem>();
    private List<ListItem> scriptTypeList = new ArrayList<ListItem>();
    private List<ListItem> uriList = new ArrayList<ListItem>();
    private List<ListItem> stylesheetUrlList = new ArrayList<ListItem>();
    private List<ListItem> scriptUrlList = new ArrayList<ListItem>();
    private List<ListItem> beanClassList = new ArrayList<ListItem>();
    private List<ListItem> callTypeList = new ArrayList<ListItem>();
    private List<ListItem> serverVarList = new ArrayList<ListItem>();
    private List<ListItem> outcomeList = new ArrayList<ListItem>();
    private List<ListItem> booleanList = new ArrayList<ListItem>();
    private List<ListItem> metaNameList = new ArrayList<ListItem>();
    private List<ListItem> metaHttpEquivList = new ArrayList<ListItem>();
    private Map<String, List<ListItem>> cssItemListMap = new HashMap<String, List<ListItem>>();
    private Map<String, List<ListItem>> jsFuncListMap = new HashMap<String, List<ListItem>>();
    private Map<String, List<ListItem>> methodListMap = new HashMap<String, List<ListItem>>();
    private Map<String, List<ListItem>> htmlIdListMap = new HashMap<String, List<ListItem>>();
    private Map<String, List<ListItem>> htmlTagListMap = new HashMap<String, List<ListItem>>();

    private PluginContext(Project project) {
        this.project = project;
    }

    public List<ListItem> getAttributeList(boolean refresh) {
        if(refresh || attributeList.size() == 0) {
            attributeList.clear();
            attributeList.add(new ListItem("className", "Style Class"));
            attributeList.add(new ListItem("dir", "Direction")); 
            attributeList.add(new ListItem("id", "ID attribute of the element"));
            attributeList.add(new ListItem("innerHTML", "HTML contents of the element"));
            attributeList.add(new ListItem("lang", "Language of the contents/value of the element"));
            attributeList.add(new ListItem("style.attribute", "Attribute of the element's style, e.g. style.display"));
            attributeList.add(new ListItem("tabIndex", "Tab order of the element"));
            attributeList.add(new ListItem("src", "File/Resource source location e.g. Source of Image or IFRAME"));            
            attributeList.add(new ListItem("location", "Document Location"));
            attributeList.add(new ListItem("title", "Title of the document or element"));
        }
        return attributeList;
    }
    
    public List<ListItem> getCssItemList(List<String> cssFiles, boolean refresh) {
        List<ListItem> cssItemList = new ArrayList<ListItem>();
        for (String url : cssFiles) {
            List<ListItem> list = cssItemListMap.get(url);
            if (refresh || list == null || list.size() == 0) {
                if (list == null) {
                    list = new ArrayList<ListItem>();
                }
                if (url.startsWith(Constants.HTTP) || url.startsWith(Constants.HTTPS)) {
                    StringBuffer code = getWebData(url);
                    list = parseStylesheetForItems(code);
                } else {
                    List<ListItem> uList = getStylesheetUrlList(false);
                    for (ListItem u : uList) {
                        String key = u.getText();
                        if (u.getDescription() != null) {
                            if (u.getDescription().startsWith(Constants.SLASH)) {
                                key = Constants.CONTEXT_PATH_EXPR + u.getDescription() + u.getText();
                            } else {
                                key = u.getDescription() + u.getText();
                            }
                            if (key.equals(url)) {
                                StringBuffer code = getFileData(u.getFileObject());
                                list = parseStylesheetForItems(code);
                                if (list != null && list.size() > 0) {
                                    cssItemListMap.put(key, list);
                                    cssItemList.addAll(list);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return cssItemList;
    }

    public List<ListItem> getBooleanList(boolean refresh) {
        if (refresh || booleanList.size() == 0) {
            booleanList.clear();
            booleanList.add(new ListItem("false", null));
            booleanList.add(new ListItem("true", null));
        }
        return booleanList;
    }

    public List<ListItem> getMetaNameList(boolean refresh) {
        if (refresh || metaNameList.size() == 0) {
            metaNameList.clear();
            metaNameList.add(new ListItem("Author", null));
            metaNameList.add(new ListItem("Copyright", null));
            metaNameList.add(new ListItem("Keywords", null));
            metaNameList.add(new ListItem("Date", null));
            metaNameList.add(new ListItem("Description", null));
            metaNameList.add(new ListItem("Robots", null));
        }
        return metaNameList;
    }

    public List<ListItem> getMetaHttpEquivList(boolean refresh) {
        if (refresh || metaHttpEquivList.size() == 0) {
            metaHttpEquivList.clear();
            metaHttpEquivList.add(new ListItem("Content-Type", null));
            metaHttpEquivList.add(new ListItem("Content-Language", null));
            metaHttpEquivList.add(new ListItem("Refresh", null));
            metaHttpEquivList.add(new ListItem("Pragma", null));
            metaHttpEquivList.add(new ListItem("Expires", null));
            metaHttpEquivList.add(new ListItem("Cache-Control", null));
        }
        return metaHttpEquivList;
    }

    public List<ListItem> getOutcomeList(boolean refresh) {
        if (refresh || outcomeList.size() == 0) {
            outcomeList.clear();
            outcomeList.add(new ListItem("default", "Default Outcome"));
        }
        return outcomeList;
    }

    public List<ListItem> getScopeList(boolean refresh) {
        if (refresh || scopeList.size() == 0) {
            scopeList.clear();
            scopeList.add(new ListItem("application", "Application Scope"));
            scopeList.add(new ListItem("session", "Session Scope"));
            scopeList.add(new ListItem("request", "Request Scope"));
        }
        return scopeList;
    }

    public List<ListItem> getNameList(boolean refresh) {
        if (refresh || nameList.size() == 0) {
            nameList.clear();
            nameList.add(new ListItem("document", "HTML Document"));
            nameList.add(new ListItem("window", "Browser Window"));
            nameList.add(new ListItem("navigator", "Browser"));
        }
        return nameList;
    }

    public List<ListItem> getStylesheetTypeList(boolean refresh) {
        if (refresh || stylesheetTypeList.size() == 0) {
            stylesheetTypeList.clear();
            stylesheetTypeList.add(new ListItem("text/css", null));
        }
        return stylesheetTypeList;
    }

    public List<ListItem> getScriptTypeList(boolean refresh) {
        if (refresh || scriptTypeList.size() == 0) {
            scriptTypeList.clear();
            scriptTypeList.add(new ListItem("text/javascript", null));
        }
        return scriptTypeList;
    }

    public List<ListItem> getCallTypeList(boolean refresh) {
        if (refresh || callTypeList.size() == 0) {
            callTypeList.clear();
            callTypeList.add(new ListItem("update", null));
            callTypeList.add(new ListItem("script", null));
        }
        return callTypeList;
    }

    public List<ListItem> getServerVarList(boolean refresh) {
        if (refresh || serverVarList.size() == 0) {
            serverVarList.clear();
            serverVarList.add(new ListItem("#{contextPath}", "Context Path"));
            serverVarList.add(new ListItem("#{sessionId}", "Session ID"));
            serverVarList.add(new ListItem("#{locale.language}", "Language"));            
            serverVarList.add(new ListItem("#{locale}", "Locale (JSON String)"));
            serverVarList.add(new ListItem("#{value}", "Method should return basic data type e.g String, Long, Date etc."));
            serverVarList.add(new ListItem("#{array}", "Method should return list or array"));
            serverVarList.add(new ListItem("#{propName}", "Method should return a java bean"));
            serverVarList.add(new ListItem("#{key}", "Method should return object of Properties/ResourceBundle"));
            serverVarList.add(new ListItem("#{key.subKey}", "Method should return Map of beans/Properties/ResourceBundle or combination"));            
        }
        return serverVarList;
    }

    public List<ListItem> getEventTypeList(boolean refresh) {
        if (refresh || eventTypeList.size() == 0) {
            eventTypeList.clear();
            eventTypeList.add(new ListItem("onclick", null));
            eventTypeList.add(new ListItem("ondblclick", null));
            eventTypeList.add(new ListItem("onmousedown", null));
            eventTypeList.add(new ListItem("onmouseup", null));
            eventTypeList.add(new ListItem("onmouseover", null));
            eventTypeList.add(new ListItem("onmousemove", null));
            eventTypeList.add(new ListItem("onmouseout", null));
            eventTypeList.add(new ListItem("onkeypress", null));
            eventTypeList.add(new ListItem("onkeydown", null));
            eventTypeList.add(new ListItem("onkeyup", null));
            eventTypeList.add(new ListItem("onload", null));
            eventTypeList.add(new ListItem("onunload", null));
            eventTypeList.add(new ListItem("onabort", null));
            eventTypeList.add(new ListItem("onerror", null));
            eventTypeList.add(new ListItem("onresize", null));
            eventTypeList.add(new ListItem("onscroll", null));
            eventTypeList.add(new ListItem("onselect", null));
            eventTypeList.add(new ListItem("onchange", null));
            eventTypeList.add(new ListItem("onsubmit", null));
            eventTypeList.add(new ListItem("onreset", null));
            eventTypeList.add(new ListItem("onfocus", null));
            eventTypeList.add(new ListItem("onblur", null));
            eventTypeList.add(new ListItem("ondomfocusin", null));
            eventTypeList.add(new ListItem("ondomfocusout", null));
            eventTypeList.add(new ListItem("ondomactivate", null));
            eventTypeList.add(new ListItem("onsubtreemodified", null));
            eventTypeList.add(new ListItem("onnodeinserted", null));
            eventTypeList.add(new ListItem("onnoderemoved", null));
            eventTypeList.add(new ListItem("ondomnoderemovedfromdocument", null));
            eventTypeList.add(new ListItem("ondomnodeinsertedintodocument", null));
            eventTypeList.add(new ListItem("onattrmodified", null));
            eventTypeList.add(new ListItem("oncharacterdatamodified", null));
            eventTypeList.add(new ListItem("ms-oncut", null));
            eventTypeList.add(new ListItem("ms-oncopy", null));
            eventTypeList.add(new ListItem("ms-onpaste", null));
            eventTypeList.add(new ListItem("ms-onbeforecut", null));
            eventTypeList.add(new ListItem("ms-onbeforecopy", null));
            eventTypeList.add(new ListItem("ms-onbeforepaste", null));
            eventTypeList.add(new ListItem("ms-onafterupdate", null));
            eventTypeList.add(new ListItem("ms-onbeforeupdate", null));
            eventTypeList.add(new ListItem("ms-oncellchange", null));
            eventTypeList.add(new ListItem("ms-ondataavailable", null));
            eventTypeList.add(new ListItem("ms-ondatasetchanged", null));
            eventTypeList.add(new ListItem("ms-ondatasetcomplete", null));
            eventTypeList.add(new ListItem("ms-onerrorupdate", null));
            eventTypeList.add(new ListItem("ms-onrowenter", null));
            eventTypeList.add(new ListItem("ms-onrowexit", null));
            eventTypeList.add(new ListItem("ms-onrowsdeleted", null));
            eventTypeList.add(new ListItem("ms-onrowinserted", null));
            eventTypeList.add(new ListItem("ms-oncontextmenu", null));
            eventTypeList.add(new ListItem("ms-ondrag", null));
            eventTypeList.add(new ListItem("ms-ondragstart", null));
            eventTypeList.add(new ListItem("ms-ondragenter", null));
            eventTypeList.add(new ListItem("ms-ondragover", null));
            eventTypeList.add(new ListItem("ms-ondragleave", null));
            eventTypeList.add(new ListItem("ms-ondragend", null));
            eventTypeList.add(new ListItem("ms-ondrop", null));
            eventTypeList.add(new ListItem("ms-onselectstart", null));
            eventTypeList.add(new ListItem("ms-onhelp", null));
            eventTypeList.add(new ListItem("ms-onbeforeunload", null));
            eventTypeList.add(new ListItem("ms-onstop", null));
            eventTypeList.add(new ListItem("ms-onbeforeeditfocus", null));
            eventTypeList.add(new ListItem("ms-onstart", null));
            eventTypeList.add(new ListItem("ms-onfinish", null));
            eventTypeList.add(new ListItem("ms-onbounce", null));
            eventTypeList.add(new ListItem("ms-onbeforeprint", null));
            eventTypeList.add(new ListItem("ms-onafterprint", null));
            eventTypeList.add(new ListItem("ms-onpropertychange", null));
            eventTypeList.add(new ListItem("ms-onfilterchange", null));
            eventTypeList.add(new ListItem("ms-onreadystatechange", null));
            eventTypeList.add(new ListItem("ms-onlosecapture", null));
            eventTypeList.add(new ListItem("xul-DOMMouseScroll", null));
            eventTypeList.add(new ListItem("xul-ondragdrop", null));
            eventTypeList.add(new ListItem("xul-ondragenter", null));
            eventTypeList.add(new ListItem("xul-ondragexit", null));
            eventTypeList.add(new ListItem("xul-ondraggesture", null));
            eventTypeList.add(new ListItem("xul-ondragover", null));
            eventTypeList.add(new ListItem("xul-CheckboxStateChange", null));
            eventTypeList.add(new ListItem("xul-RadioStateChange", null));
            eventTypeList.add(new ListItem("xul-onclose", null));
            eventTypeList.add(new ListItem("xul-oncommand", null));
            eventTypeList.add(new ListItem("xul-oninput", null));
            eventTypeList.add(new ListItem("xul-DOMMenuItemActive", null));
            eventTypeList.add(new ListItem("xul-DOMMenuItemInactive", null));
            eventTypeList.add(new ListItem("xul-oncontextmenu", null));
            eventTypeList.add(new ListItem("xul-onoverflow", null));
            eventTypeList.add(new ListItem("xul-onoverflowchanged", null));
            eventTypeList.add(new ListItem("xul-onunderflow", null));
            eventTypeList.add(new ListItem("xul-onpopuphidden", null));
            eventTypeList.add(new ListItem("xul-onpopuphiding", null));
            eventTypeList.add(new ListItem("xul-onpopupshowing", null));
            eventTypeList.add(new ListItem("xul-onpopupshown", null));
            eventTypeList.add(new ListItem("xul-onbroadcast", null));
            eventTypeList.add(new ListItem("xul-oncommandupdate", null));
        }
        return eventTypeList;
    }

    public List<ListItem> getUriList(boolean refresh) {
        if (refresh || uriList.size() == 0) {
            buildUriList();
//            uriList.add(new ListItem(Constants.URI_REGEXP, Constants.EMPTY_STRING));
        }
        return uriList;
    }

    public List<ListItem> getStylesheetUrlList(boolean refresh) {
        if (refresh || stylesheetUrlList.size() == 0) {
            buildStylesheetUrlList();
        }
        return stylesheetUrlList;
    }

    public List<ListItem> getScriptUrlList(boolean refresh) {
        if (refresh || scriptUrlList.size() == 0) {
            buildScriptUrlList();
        }
        return scriptUrlList;
    }

    public List<ListItem> getBeanClassList(boolean refresh) {
        if (refresh || beanClassList.size() == 0) {
            buildBeanClassList();
        }
        return beanClassList;
    }

    public List<ListItem> getMethodList(String beanClass, boolean refresh) {
        List<ListItem> methodList = methodListMap.get(beanClass);
        if (refresh || methodList == null || methodList.size() == 0) {
            List<ListItem> bcList = getBeanClassList(false);
            for (ListItem bc : bcList) {
                String key = bc.getText();
                if (bc.getDescription() != null) {
                    key = bc.getDescription() + Constants.DOT + bc.getText();
                    if (key.equals(beanClass)) {
                        StringBuffer code = getFileData(bc.getFileObject());
                        methodList = parseBeanClassForMethods(code);
                        for(ListItem item : methodList) {
                            if(item.getText().startsWith("get") || item.getText().startsWith("is")) {
                                methodList.add(new ListItem("getProperties", "Map containing all the properties"));
                                break;
                            }
                        }
                        methodListMap.put(key, methodList);
                        break;
                    }
                }
            }
        }
        return methodList;
    }

    public List<ListItem> getHtmlIdList(String uri, boolean refresh) {
        List<ListItem> htmlIdList = htmlIdListMap.get(uri);
        if(htmlIdList == null) {
            htmlIdList = new ArrayList<ListItem>();
        }
        if (refresh || htmlIdList.size() == 0) {
            htmlIdList.clear();
            List<ListItem> uList = getUriList(true);
            for (ListItem u : uList) {
                String key = u.getText();
                if (u.getDescription() != null) {
                    key = u.getDescription() + u.getText();
                    if (key.equals(uri)) {
                        StringBuffer code = getFileData(u.getFileObject());
                        htmlIdList.addAll(parseUriForHtmlIds(code, null));
                    } else if (key.matches(uri)) {
                        StringBuffer code = getFileData(u.getFileObject());
                        htmlIdList.addAll(parseUriForHtmlIds(code, uri));                    
                    }
                }
            }
            if(htmlIdList.size() > 0) {
                htmlIdListMap.put(uri, htmlIdList);
            }
        }
        return htmlIdList;
    }

    public List<ListItem> getFormIdList(String uri, boolean refresh) {
        List<ListItem> formIdList = new ArrayList<ListItem>();
        List<ListItem> idList = getHtmlIdList(uri, refresh);
        for (ListItem li : idList) {
            if (li.getDescription().contains(Constants.FORM)) {
                formIdList.add(new ListItem(li.getText(), li.getDescription()));
            }
        }
        return formIdList;
    }

    public List<ListItem> getJsFunctionList(List<String> jsFiles, boolean refresh) {
        List<ListItem> jsFuncList = new ArrayList<ListItem>();
        for (String url : jsFiles) {
            List<ListItem> list = jsFuncListMap.get(url);
            if (refresh || list == null || list.size() == 0) {
                if (list == null) {
                    list = new ArrayList<ListItem>();
                }
                if (url.startsWith(Constants.HTTP) || url.startsWith(Constants.HTTPS)) {
                    StringBuffer code = getWebData(url);
                    list = parseScriptForFunctions(code);
                } else {
                    List<ListItem> uList = getScriptUrlList(false);
                    for (ListItem u : uList) {
                        String key = u.getText();
                        if (u.getDescription() != null) {
                            if (u.getDescription().startsWith(Constants.SLASH)) {
                                key = Constants.CONTEXT_PATH_EXPR + u.getDescription() + u.getText();
                            } else {
                                key = u.getDescription() + u.getText();
                            }
                            if (key.equals(url)) {
                                StringBuffer code = getFileData(u.getFileObject());
                                list = parseScriptForFunctions(code);
                                if (list != null && list.size() > 0) {
                                    jsFuncListMap.put(key, list);
                                    jsFuncList.addAll(list);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return jsFuncList;
    }

    public List<ListItem> getHtmlTagList(String uri, boolean refresh) {
        List<ListItem> htmlTagList = htmlTagListMap.get(uri);
        if(htmlTagList == null) {
            htmlTagList = new ArrayList<ListItem>();
        }
        if (refresh || htmlTagList.size() == 0) {
            htmlTagList.clear();
            List<ListItem> uList = getUriList(true);
            for (ListItem u : uList) {
                String key = u.getText();
                if (u.getDescription() != null) {
                    key = u.getDescription() + u.getText();
                    if (key.equals(uri)) {
                        StringBuffer code = getFileData(u.getFileObject());
                        htmlTagList.addAll(parseUriForHtmlTags(code, null));
                        break;
                    } else if(key.matches(uri)) {
                        StringBuffer code = getFileData(u.getFileObject());
                        htmlTagList.addAll(parseUriForHtmlTags(code, key));
                    }
                }
            }
            if(htmlTagList.size() > 0) {
                htmlTagListMap.put(uri, htmlTagList);
            }
        }
        return htmlTagList;
    }

    private void buildBeanClassList() {
        beanClassList.clear();
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] sgs = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (SourceGroup sg : sgs) {
            FileObject rootFolder = sg.getRootFolder();
            for (FileObject fob : rootFolder.getChildren()) {
                buildBeanClassList(fob, null);
            }
        }
    }

    public FileObject findWebFolder(FileObject fob) {
        FileObject webFolder = null;
        if (fob.isFolder() && Constants.WEB_INF.equals(fob.getName())) {
            webFolder = fob.getParent();
        } else {
            for (FileObject cfob : fob.getChildren()) {
                webFolder = findWebFolder(cfob);
                if (webFolder != null) {
                    break;
                }
            }
        }
        return webFolder;
    }

    private void buildBeanClassList(FileObject fob, String fqpn) {
        if (fob.isFolder()) {
            for (FileObject c : fob.getChildren()) {
                if (fqpn == null) {
                    buildBeanClassList(c, fob.getName());
                } else {
                    buildBeanClassList(c, fqpn + Constants.DOT + fob.getName());
                }
            }
        } else {
            if (Constants.JAVA.equals(fob.getExt())) {
                beanClassList.add(new ListItem(fob.getName(), fqpn, fob));
            }
        }
    }

    private void buildStylesheetUrlList() {
        stylesheetUrlList.clear();
        FileObject webFolder = findWebFolder(project.getProjectDirectory());
        if (webFolder != null) {
            buildStylesheetUrlList(webFolder, null);
        }
    }

    private void buildStylesheetUrlList(FileObject fob, String context) {
        if (fob.isFolder()) {
            for (FileObject c : fob.getChildren()) {
                if (context == null) {
                    buildStylesheetUrlList(c, Constants.SLASH);
                } else {
                    buildStylesheetUrlList(c, context + fob.getName() + Constants.SLASH);
                }
            }
        } else {
            for (String ext : Constants.STYLESHEET_EXT_LIST) {
                if (ext.equals(fob.getExt())) {
                    stylesheetUrlList.add(new ListItem(fob.getNameExt(), context, fob));
                }
            }
        }
    }

    private void buildScriptUrlList() {
        scriptUrlList.clear();
        FileObject webFolder = findWebFolder(project.getProjectDirectory());
        if (webFolder != null) {
            buildScriptUrlList(webFolder, null);
        }
    }

    private void buildScriptUrlList(FileObject fob, String context) {
        if (fob.isFolder()) {
            for (FileObject c : fob.getChildren()) {
                if (context == null) {
                    buildScriptUrlList(c, Constants.SLASH);
                } else {
                    buildScriptUrlList(c, context + fob.getName() + Constants.SLASH);
                }
            }
        } else {
            for (String ext : Constants.SCRIPT_EXT_LIST) {
                if (ext.equals(fob.getExt())) {
                    scriptUrlList.add(new ListItem(fob.getNameExt(), context, fob));
                }
            }
        }
    }

    private void buildUriList() {
        uriList.clear();
        FileObject webFolder = findWebFolder(project.getProjectDirectory());
        if (webFolder != null) {
            buildUriList(webFolder, null);
        }
    }

    private void buildUriList(FileObject fob, String context) {
        if (fob.isFolder()) {
            for (FileObject c : fob.getChildren()) {
                if (context == null) {
                    buildUriList(c, Constants.SLASH);
                } else {
                    buildUriList(c, context + fob.getName() + Constants.SLASH);
                }
            }
        } else {
            for (String ext : Constants.URI_EXT_LIST) {
                if (ext.equals(fob.getExt())) {
                    uriList.add(new ListItem(fob.getNameExt(), context, fob));
                }
            }
        }
    }

    public StringBuffer getFileData(FileObject fileObject) {
        StringBuffer code = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(fileObject.getInputStream()));
            String line = br.readLine();
            while (line != null) {
                code.append(line).append(Constants.NEW_LINE);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return code;
    }

    private StringBuffer getWebData(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            URL _url = new URL(url);
            URLConnection conn = _url.openConnection(Proxy.NO_PROXY);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append(Constants.NEW_LINE);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return sb;
    }

    private boolean isNumber(String className) {
        try {
            Double.parseDouble(className);
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    private List<ListItem> parseBeanClassForMethods(StringBuffer code) {
        List<ListItem> methodList = new ArrayList<ListItem>();
        String[] parts = code.toString().split(Constants.WS);
        List<String> tokens = new ArrayList<String>();
        for (String part : parts) {
            boolean validToken = !(Constants.WS.equals(part) || Constants.NEW_LINE.equals(part));
            if (validToken) {
                tokens.add(part);
            }
        }
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.indexOf(Constants.LPAREN) != -1) {
                String methodName, returnType, accessModifier;
                if (token.charAt(0) == Constants.LPAREN.charAt(0)) {
                    methodName = tokens.get(i - 1);
                    returnType = tokens.get(i - 2);
                    accessModifier = tokens.get(i - 3);
                } else {
                    methodName = token.split("[" + Constants.LPAREN + "]")[0];
                    returnType = tokens.get(i - 1);
                    accessModifier = tokens.get(i - 2);
                }
                if (Constants.PUBLIC.equals(accessModifier)) {
                    methodList.add(new ListItem(methodName, returnType));
                }
            }
        }
        return methodList;
    }

    private List<ListItem> parseScriptForFunctions(StringBuffer code) {
        List<ListItem> jsFunctionList = new ArrayList<ListItem>();
        StringBuffer sb = new StringBuffer(code);
        String[] parts = sb.toString().split(Constants.WS);
        List<String> tokens = new ArrayList<String>();
        for (String part : parts) {
            if (part != null && !part.equals(Constants.EMPTY_STRING)) {
                tokens.add(part.replaceAll(Constants.NEW_LINE, Constants.EMPTY_STRING));
            }
        }
        outer:
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String signature = Constants.EMPTY_STRING;
            String className = "JavaScript Function";
            int index = -1;
            if (token.startsWith(Constants.FUNCTION)) {
                index = i + 1;
                token = tokens.get(index);
                if (token.startsWith(Constants.LPAREN)) {// Type 1 - abc : function(a, b, c)
//                    signature = token;
//                    if (!signature.contains(Constants.RPAREN)) {
//                        inner:
//                        for (int j = index; j < tokens.size(); j++) {
//                            String ntoken = tokens.get(j);
//                            signature = signature + Constants.WS;
//                            if (ntoken.contains(Constants.RPAREN)) {
//                                signature = signature + ntoken.substring(0, ntoken.indexOf(Constants.RPAREN));
//                                break inner;
//                            }
//                        }
//                    }
//                    index = i - 1;
//                    token = tokens.get(index);
//                    if (token.equals(Constants.COLON)) {
//                        index--;
//                        token = tokens.get(index);
//                        signature = token + signature;
//                    } else {
//                        signature = Constants.EMPTY_STRING;
//                    }
                } else {// Type 2 - function abc(a, b, c) or function abc (a, b, c)
                    if (!token.contains(Constants.LPAREN)) {
                        index++;
                        token = tokens.get(index);
                    }
                    if (token.contains(Constants.LPAREN)) {
                        signature = token;
                        if (!signature.contains(Constants.RPAREN)) {
                            inner:
                            for (int j = index + 1; j < tokens.size(); j++) {
                                String ntoken = tokens.get(j);
                                signature = signature + Constants.WS;
                                if (!ntoken.contains(Constants.RPAREN)) {
                                    signature = signature + ntoken;
                                } else {
                                    signature = signature + ntoken.substring(0, ntoken.indexOf(Constants.RPAREN) + 1);
                                    break inner;
                                }
                            }
                        }
                        if (signature.endsWith(Constants.SEMI_COLON)) {
                            signature = signature.substring(0, signature.length() - 1);
                        }
                    }
                }
                if (!signature.equals(Constants.EMPTY_STRING) && !signature.startsWith(Constants.LPAREN)) {
                    jsFunctionList.add(new ListItem(signature, className));
                }
            }
        }
        return jsFunctionList;
    }

    private List<ListItem> parseStylesheetForItems(StringBuffer code) {
        String[] parts = code.toString().split(Constants.LBRACKET + Constants.DOT + Constants.RBRACKET);
        List<ListItem> list = new ArrayList<ListItem>();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String className = null;
            int index = -1;
            for (String t : Constants.STYLE_TERMINATORS) {
                index = part.indexOf(t);
                if (index != -1) {
                    break;
                }
            }
            if (index == -1) {
                index = 0;
            }
            className = part.substring(0, index);
            if (className.matches(Constants.STYLECLASS_REGEXP)) {
                list.add(new ListItem(className, "Style Class"));
            }
        }
        return list;
    }

    private List<ListItem> parseUriForHtmlIds(StringBuffer html, String uri) {
        StringBuffer sb = new StringBuffer(html);
        int lastIndex = 0;
        while (true) {
            int scriptStart = sb.indexOf("<script", lastIndex);
            if (scriptStart == -1) {
                break;
            }
            lastIndex = scriptStart;
            int scriptEnd = sb.indexOf("/>", scriptStart);
            if (scriptEnd == -1) {
                scriptEnd = sb.indexOf("</script>", scriptStart);
            }
            if (scriptEnd != -1) {
                sb.delete(scriptStart, scriptEnd);
            }
        }
        List<ListItem> ids = new ArrayList<ListItem>();
        int startIndex = 0, endIndex = 0;
        lastIndex = 0;
        while (startIndex != -1) {
            startIndex = sb.indexOf(Constants.LT, lastIndex);
            if (startIndex == -1) {
                break;
            }
            endIndex = sb.indexOf(Constants.GT, startIndex);
            String temp = sb.substring(startIndex + 1, endIndex);
            if (isValidTag(temp)) {
                String tag = null;
                String id = null;
                String type = null;
                String[] parts = temp.split(Constants.WS);
                if (!parts[0].startsWith(Constants.SLASH)) {
                    tag = parts[0];
                    for (String part : parts) {
                        if (part.startsWith(Constants.HTML_ID) || part.startsWith(Constants.HTML_NAME)) {
                            String[] pair = part.split(Constants.EQUAL);
                            id = pair[1];
                            if (id.startsWith(Constants.DQUOTE)) {
                                id = id.substring(1);
                            }
                            if (id.endsWith(Constants.SLASH)) {
                                id = id.substring(0, id.length() - 1);
                            }
                            if (id.endsWith(Constants.DQUOTE)) {
                                id = id.substring(0, id.length() - 1);
                            }
                        }
                        if (part.startsWith(Constants.HTML_TYPE)) {
                            String[] pair = part.split(Constants.EQUAL);
                            type = pair[1];
                            if (type.startsWith(Constants.DQUOTE)) {
                                type = type.substring(1);
                            }
                            if (type.endsWith(Constants.SLASH)) {
                                type = type.substring(0, type.length() - 1);
                            }
                            if (type.endsWith(Constants.DQUOTE)) {
                                type = type.substring(0, type.length() - 1);
                            }
                        }
                    }
                    if (id != null) {
                        if (type != null) {
                            tag = tag + Constants.COLON + type;
                        }
                        tag = "&lt;" + tag + "&gt;";
                        if (!id.contains("%")) {
                            if(uri == null) {
                                ids.add(new ListItem(id, tag));
                            } else {
                                ids.add(new ListItem(id, tag + "&nbsp;(" + uri + ")"));
                            }
                        }
                    }
                }
            }
            lastIndex = endIndex;
        }
        return ids;
    }

    private List<ListItem> parseUriForHtmlTags(StringBuffer html, String uri) {
        ArrayList<ListItem> tags = new ArrayList<ListItem>();
        String[] parts = html.toString().split(Constants.WS);
        List<String> tokens = new ArrayList<String>();
        for (String part : parts) {
            boolean validToken = (!(Constants.WS.equals(part) || Constants.NEW_LINE.equals(part)) && isValidTag(part));
            if (validToken) {
                if (!tokens.contains(part)) {
                    tokens.add(part);
                }
            }
        }
        String temp = null;
        String tag = null;
        for (int j = 0; j < tokens.size(); j++) {
            temp = tokens.get(j);
            if (temp.startsWith(Constants.LT)) {
                int end = temp.indexOf(Constants.WS);
                if (end == -1) {
                    end = temp.indexOf(Constants.GT);
                    if (end == -1) {
                        tag = temp.substring(1);
                    } else {
                        if (temp.charAt(end) != Constants.SLASH.charAt(0)) {
                            tag = temp.substring(1, end);
                        } else {
                            tag = temp.substring(1, end - 1);
                        }
                    }
                }
                if (tag != null) {
                    boolean found = false;
                    for (ListItem item : tags) {
                        if (item.getText().equals(tag)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        tags.add(new ListItem(tag, uri));
                    }
                }
            }
        }
        return tags;
    }

    private boolean isValidTag(String tag) {
        for (int i = 0; i < Constants.INVALID_TAGS.length; i++) {
            if (tag.startsWith(Constants.INVALID_TAGS[i])) {
                return false;
            }
        }
        return true;
    }
}
