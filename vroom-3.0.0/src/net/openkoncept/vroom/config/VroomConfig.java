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
package net.openkoncept.vroom.config;

import net.openkoncept.vroom.VroomUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * It is mapped to &lt;vroom-config&gt; element of the configuration file.
 * </p>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomConfig implements Serializable {

    private static VroomConfig instance;

    private long lastModified = 0;

    public static VroomConfig initialize(String configFile) {
        instance = new VroomConfig(configFile);
        return instance;
    }

    public static VroomConfig getInstance() {
        if (instance == null) {
            instance = new VroomConfig();
        }
        return instance;
    }

    private String configFile;
    protected List webpages = new ArrayList();

    public Webpage addWebpage(String uri) {
        Webpage webpage = new Webpage(uri);
        webpages.add(webpage);
        return webpage;
    }

    public Webpage addWebpage(String uri, String beanClass, String var, String scope) {
        Webpage webpage = new Webpage(uri, beanClass, var, scope);
        webpages.add(webpage);
        return webpage;
    }

    public void reload() {
        if (getConfigFile() == null) {
            return;
        }
        File _file = new File(getConfigFile());
        if (lastModified == _file.lastModified()) {
            return;
        }
        lastModified = _file.lastModified();
        InputStream is;
        Document document;
        try {
            is = new FileInputStream(_file);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            loadWebpages(document);
        } catch (Exception ex) {
            String msg = "Failed to load Vroom configuration file [" + getConfigFile() + "]";
            throw new RuntimeException(msg, ex);
        }
    }

    public VroomConfig() {
    }

    public VroomConfig(String configFile) {
        this.configFile = configFile;
        reload();
    }

    public List getWebpages() {
        return webpages;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setWebpages(List webpages) {
        this.webpages = webpages;
    }

    private void loadCalls(Node nEvent, Event event) {
        String type, id, tag, attribute, value, name, url;
        NodeList nlCall = nEvent.getChildNodes();
        for (int c = 0; c < nlCall.getLength(); c++) {
            Node nCall = nlCall.item(c);
            if (Constants.XML_ELEM_CALL.equals(nCall.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_TYPE);
                id = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_ID);
                tag = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_TAG);
                attribute = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_ATTRIBUTE);
                value = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_VALUE);
                if (value == null) {
                    value = VroomUtilities.getXmlText(nCall);
                    if (value != null) {
                        value = value.replaceAll("\n", "CRLF");
                    }
                }
                name = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_NAME);
                url = VroomUtilities.getXmlAttributeValue(nCall, Constants.XML_ATTR_URL);
                if (Constants.CALL_TYPE_SCRIPT.equals(type)) {
                    event.addScriptCall(value, url);
                } else if (Constants.CALL_TYPE_UPDATE.equals(type)) {
                    event.addUpdateCall(id, tag, name, attribute, value, url);
                }
            }
        }
    }

    private void loadFormElements(Node nForm, Form form) {
        String id, property, format, beanClass, var, scope;
        NodeList nlElement = nForm.getChildNodes();
        for (int n1 = 0; n1 < nlElement.getLength(); n1++) {
            Node nElement = nlElement.item(n1);
            if (Constants.XML_ELEM_ELEMENT.equals(nElement.getNodeName())) {
                id = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_ID);
                property = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_PROPERTY);
                format = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_FORMAT);
                beanClass = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_SCOPE);
                Element element = form.addElement(id, property, format, beanClass, var, scope);
                loadElementEvents(nElement, element);
            }
        }
    }

    private void loadFormNavigations(Node nForm, Form form) {
        String outcome, url;
        Boolean forward;
        NodeList nlNavigation = nForm.getChildNodes();
        for (int n1 = 0; n1 < nlNavigation.getLength(); n1++) {
            Node nNavigation = nlNavigation.item(n1);
            if (Constants.XML_ELEM_NAVIGATION.equals(nNavigation.getNodeName())) {
                outcome = VroomUtilities.getXmlAttributeValue(nNavigation, Constants.XML_ATTR_OUTCOME);
                url = VroomUtilities.getXmlAttributeValue(nNavigation, Constants.XML_ATTR_URL);
                forward = Boolean.valueOf(VroomUtilities.getXmlAttributeValue(nNavigation, Constants.XML_ATTR_FORWARD));
                form.addNavigation(outcome, url, forward);
            }
        }
    }


    private void loadWebpageElements(Node nWebpage, Webpage webpage) {
        String id, beanClass, var, scope;
        NodeList nlElement = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlElement.getLength(); n1++) {
            Node nElement = nlElement.item(n1);
            if (Constants.XML_ELEM_ELEMENT.equals(nElement.getNodeName())) {
                id = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_ID);
                beanClass = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nElement, Constants.XML_ATTR_SCOPE);
                Element element = webpage.addElement(id, beanClass, var, scope);
                loadElementEvents(nElement, element);
            }
        }
    }

    private void loadWebpageForms(Node nWebpage, Webpage webpage) {
        String id, method, beanClass, var, scope;
        NodeList nlForm = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlForm.getLength(); n1++) {
            Node nForm = nlForm.item(n1);
            if (Constants.XML_ELEM_FORM.equals(nForm.getNodeName())) {
                id = VroomUtilities.getXmlAttributeValue(nForm, Constants.XML_ATTR_ID);
                method = VroomUtilities.getXmlAttributeValue(nForm, Constants.XML_ATTR_METHOD);
                beanClass = VroomUtilities.getXmlAttributeValue(nForm, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nForm, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nForm, Constants.XML_ATTR_SCOPE);
                Form form = webpage.addForm(id, method, beanClass, var, scope);
                loadFormNavigations(nForm, form);
                loadFormElements(nForm, form);
                loadFormEvents(nForm, form);
            }
        }
    }

    private void loadWebpageEvents(Node nWebpage, Webpage webpage) {
        String type, method, beanClass, var, scope;
        Boolean sync;
        NodeList nlEvent = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlEvent.getLength(); n1++) {
            Node nEvent = nlEvent.item(n1);
            if (Constants.XML_ELEM_EVENT.equals(nEvent.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_TYPE);
                if (type.startsWith("ms-")) {
                    type = type.substring(3);
                } else if (type.startsWith("xul-")) {
                    type = type.substring(4);
                }
                method = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_METHOD);
                sync = Boolean.valueOf(VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SYNC));
                beanClass = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SCOPE);
                if (method == null || method.length() == 0) {
                    beanClass = "net.openkoncept.vroom.bean.DummyBean";
                    var = beanClass;
                    scope = Constants.SCOPE_APPLICATION;
                    method = "dummy";
                }
                Event event = webpage.addEvent(type, method, sync, beanClass, var, scope);
                loadCalls(nEvent, event);
            }
        }
    }

    private void loadElementEvents(Node nElement, Element element) {
        String type, method, beanClass, var, scope;
        Boolean sync;
        NodeList nlEvent = nElement.getChildNodes();
        for (int n1 = 0; n1 < nlEvent.getLength(); n1++) {
            Node nEvent = nlEvent.item(n1);
            if (Constants.XML_ELEM_EVENT.equals(nEvent.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_TYPE);
                if (type.startsWith("ms-")) {
                    type = type.substring(3);
                } else if (type.startsWith("xul-")) {
                    type = type.substring(4);
                }
                method = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_METHOD);
                sync = Boolean.valueOf(VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SYNC));
                beanClass = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SCOPE);
                if (method == null || method.length() == 0) {
                    beanClass = "net.openkoncept.vroom.bean.DummyBean";
                    var = beanClass;
                    scope = Constants.SCOPE_APPLICATION;
                    method = "dummy";
                }
                Event event = element.addEvent(type, method, sync, beanClass, var, scope);
                loadCalls(nEvent, event);
            }
        }
    }

    private void loadFormEvents(Node nForm, Form form) {
        String type, method, beanClass, var, scope;
        Boolean sync;
        NodeList nlEvent = nForm.getChildNodes();
        for (int n1 = 0; n1 < nlEvent.getLength(); n1++) {
            Node nEvent = nlEvent.item(n1);
            if (Constants.XML_ELEM_EVENT.equals(nEvent.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_TYPE);
                if (type.startsWith("ms-")) {
                    type = type.substring(3);
                } else if (type.startsWith("xul-")) {
                    type = type.substring(4);
                }
                method = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_METHOD);
                sync = Boolean.valueOf(VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SYNC));
                beanClass = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SCOPE);
                if (method == null || method.length() == 0) {
                    beanClass = "net.openkoncept.vroom.bean.DummyBean";
                    var = beanClass;
                    scope = Constants.SCOPE_APPLICATION;
                    method = "dummy";
                }
                Event event = form.addEvent(type, method, sync, beanClass, var, scope);
                loadCalls(nEvent, event);
            }
        }
    }

    private void loadObjectEvents(Node nObject, Obj object) {
        String type, method, beanClass, var, scope;
        Boolean sync;
        NodeList nlEvent = nObject.getChildNodes();
        for (int n1 = 0; n1 < nlEvent.getLength(); n1++) {
            Node nEvent = nlEvent.item(n1);
            if (Constants.XML_ELEM_EVENT.equals(nEvent.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_TYPE);
                if (type.startsWith("ms-")) {
                    type = type.substring(3);
                } else if (type.startsWith("xul-")) {
                    type = type.substring(4);
                }
                method = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_METHOD);
                sync = Boolean.valueOf(VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SYNC));
                beanClass = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nEvent, Constants.XML_ATTR_SCOPE);
                if (method == null || method.length() == 0) {
                    beanClass = "net.openkoncept.vroom.bean.DummyBean";
                    var = beanClass;
                    scope = Constants.SCOPE_APPLICATION;
                    method = "dummy";
                }
                Event event = object.addEvent(type, method, sync, beanClass, var, scope);
                loadCalls(nEvent, event);
            }
        }
    }

    private void loadWebpageObjects(Node nWebpage, Webpage webpage) {
        String name, beanClass, var, scope;
        NodeList nlObject = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlObject.getLength(); n1++) {
            Node nObject = nlObject.item(n1);
            if (Constants.XML_ELEM_OBJECT.equals(nObject.getNodeName())) {
                name = VroomUtilities.getXmlAttributeValue(nObject, Constants.XML_ATTR_NAME);
                beanClass = VroomUtilities.getXmlAttributeValue(nObject, Constants.XML_ATTR_BEAN_CLASS);
                var = VroomUtilities.getXmlAttributeValue(nObject, Constants.XML_ATTR_VAR);
                scope = VroomUtilities.getXmlAttributeValue(nObject, Constants.XML_ATTR_SCOPE);
                Obj object = webpage.addObject(name, beanClass, var, scope);
                loadObjectEvents(nObject, object);
            }
        }
    }

    private void loadWebpageStylesheets(Node nWebpage, Webpage webpage) {
        String type, url, text;
        NodeList nlStylesheet = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlStylesheet.getLength(); n1++) {
            Node nStylesheet = nlStylesheet.item(n1);
            if (Constants.XML_ELEM_STYLESHEET.equals(nStylesheet.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nStylesheet, Constants.XML_ATTR_TYPE);
                url = VroomUtilities.getXmlAttributeValue(nStylesheet, Constants.XML_ATTR_URL);
                text = VroomUtilities.getXmlText(nStylesheet);
                webpage.addStylesheet(type, url, text);
            }
        }
    }

    private void loadWebpageLinks(Node nWebpage, Webpage webpage) {
        String rel, rev, title, media, url, urlLang, target, type;
        NodeList nlLink = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlLink.getLength(); n1++) {
            Node nLink = nlLink.item(n1);
            if (Constants.XML_ELEM_LINK.equals(nLink.getNodeName())) {
                rel = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_REL);
                rev = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_REV);
                title = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_TITLE);
                url = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_URL);
                urlLang = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_URL_LANG);
                media = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_MEDIA);
                target = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_TARGET);
                type = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_TYPE);
                webpage.addLink(url, urlLang, media, rel, rev, target, type, title);
            } else if (Constants.XML_ELEM_RSS.equals(nLink.getNodeName())) {
                url = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_URL);
                title = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_TITLE);
                webpage.addRSS(url, title);
            } else if (Constants.XML_ELEM_RDF.equals(nLink.getNodeName())) {
                url = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_URL);
                title = VroomUtilities.getXmlAttributeValue(nLink, Constants.XML_ATTR_TITLE);
                webpage.addRDF(url, title);
            }
        }
    }

    private void loadWebpageMetas(Node nWebpage, Webpage webpage) {
        String name, content, httpEquiv;
        NodeList nlMeta = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlMeta.getLength(); n1++) {
            Node nMeta = nlMeta.item(n1);
            if (Constants.XML_ELEM_META.equals(nMeta.getNodeName())) {
                name = VroomUtilities.getXmlAttributeValue(nMeta, Constants.XML_ATTR_NAME);
                content = VroomUtilities.getXmlAttributeValue(nMeta, Constants.XML_ATTR_CONTENT);
                httpEquiv = VroomUtilities.getXmlAttributeValue(nMeta, Constants.XML_ATTR_HTTP_EQUIV);
                webpage.addMeta(name, httpEquiv, content);
            }
        }
    }

    private void loadWebpageScripts(Node nWebpage, Webpage webpage) {
        String type, url, text;
        NodeList nlScript = nWebpage.getChildNodes();
        for (int n1 = 0; n1 < nlScript.getLength(); n1++) {
            Node nScript = nlScript.item(n1);
            if (Constants.XML_ELEM_SCRIPT.equals(nScript.getNodeName())) {
                type = VroomUtilities.getXmlAttributeValue(nScript, Constants.XML_ATTR_TYPE);
                url = VroomUtilities.getXmlAttributeValue(nScript, Constants.XML_ATTR_URL);
                text = VroomUtilities.getXmlText(nScript);
                webpage.addScript(type, url, text);
            }
        }
    }

    private void loadWebpages(Document document) {
        webpages.clear();
        String uri, beanClass, var, scope;
        NodeList nlWebpage = document.getDocumentElement().getElementsByTagName(Constants.XML_ELEM_WEBPAGE);
        for (int n = 0; n < nlWebpage.getLength(); n++) {
            Node nWebpage = nlWebpage.item(n);
            uri = VroomUtilities.getXmlAttributeValue(nWebpage, Constants.XML_ATTR_URI);
            beanClass = VroomUtilities.getXmlAttributeValue(nWebpage, Constants.XML_ATTR_BEAN_CLASS);
            var = VroomUtilities.getXmlAttributeValue(nWebpage, Constants.XML_ATTR_VAR);
            scope = VroomUtilities.getXmlAttributeValue(nWebpage, Constants.XML_ATTR_SCOPE);
            Webpage webpage = addWebpage(uri, beanClass, var, scope);
            loadWebpageMetas(nWebpage, webpage);
            loadWebpageLinks(nWebpage, webpage);
            loadWebpageStylesheets(nWebpage, webpage);
            loadWebpageScripts(nWebpage, webpage);
            loadWebpageObjects(nWebpage, webpage);
            loadWebpageForms(nWebpage, webpage);
            loadWebpageElements(nWebpage, webpage);
            loadWebpageEvents(nWebpage, webpage);
        }
    }

    public boolean isUriInDefinition(String uri) {
        boolean defined = false;
        if (uri != null) {
            Iterator iter = webpages.iterator();
            while (iter.hasNext()) {
                Webpage webpage = (Webpage) iter.next();
                if (uri.matches(webpage.getUri())) {
                    defined = true;
                    break;
                }
            }
        }
        return defined;
    }

    public Navigation getNavigation(String uri, String id, String method, String beanClass, String var, String scope, String outcome) {
        Navigation navigation = null;
        Iterator iter = webpages.iterator();
        while (iter.hasNext()) {
            Webpage wp = (Webpage) iter.next();
            String wpBeanClass = wp.getBeanClass();
            String wpVar = wp.getVar();
            String wpScope = wp.getScope();
            if (uri.matches(wp.getUri())) {
                Iterator ifrm = wp.getForms().iterator();
                while (ifrm.hasNext()) {
                    Form f = (Form) ifrm.next();
                    String fBeanClass = (f.getBeanClass() != null) ? f.getBeanClass() : wpBeanClass;
                    String fVar = (f.getVar() != null) ? f.getVar() : wpVar;
                    String fScope = (f.getBeanClass() != null) ? f.getScope() : wpScope;
                    if (f.getId().equals(id) && method.equals(f.getMethod()) && beanClass.equals(fBeanClass) && var.equals(fVar) && scope.equals(fScope)) {
                        Iterator inav = f.getNavigations().iterator();
                        while (inav.hasNext()) {
                            Navigation n = (Navigation) inav.next();
                            if (n.getOutcome().equals(outcome)) {
                                navigation = n;
                            }
                        }
                    }
                }
            }
        }
        return navigation;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public List getElements(String uri, String id, String method, String beanClass, String var, String scope) {
        List elements = null;
        Iterator iter = webpages.iterator();
        while (iter.hasNext()) {
            Webpage wp = (Webpage) iter.next();
            String wpBeanClass = wp.getBeanClass();
            String wpVar = wp.getVar();
            String wpScope = wp.getScope();
            if (uri.matches(wp.getUri())) {
                Iterator ifrm = wp.getForms().iterator();
                while (ifrm.hasNext()) {
                    Form f = (Form) ifrm.next();
                    String fBeanClass = (f.getBeanClass() != null) ? f.getBeanClass() : wpBeanClass;
                    String fVar = (f.getVar() != null) ? f.getVar() : wpVar;
                    String fScope = (f.getBeanClass() != null) ? f.getScope() : wpScope;
                    if (f.getId().equals(id) && method.equals(f.getMethod()) && beanClass.equals(fBeanClass) && var.equals(fVar) && scope.equals(fScope)) {
                        elements = f.getElements();
                    }
                }
            }
        }
        return elements;
    }
}
