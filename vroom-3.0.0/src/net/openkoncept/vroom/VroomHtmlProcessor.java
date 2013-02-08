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

import net.openkoncept.vroom.config.*;
import net.openkoncept.json.JSONException;
import net.openkoncept.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * <p> This class contains some utility methods used by Vroom framework. The
 * purpose of these methods is to modify the request html output to include
 * required scripts and stylesheets whichever applicable. </p>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomHtmlProcessor {

    /**
     * <p> This method adds all the meta tags in the &lt;head&gt; section. These
     * tags are defined in the vroom's configuration file. </p>
     *
     * @param sbHtml - Source HTML
     * @param uri - Request URI
     * @param vc - VroomConfig object
     */
    public static void addHeadMetaTags(StringBuffer sbHtml, String uri, VroomConfig vc) {
        for (Webpage wp : vc.getWebpage()) {
            if (uri.matches(wp.getUri())) {
                StringBuffer sbMetas = new StringBuffer();
                for (Meta meta : wp.getMeta()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("<meta content=\"").append(meta.getContent()).append("\"");
                    if (meta.getName() != null && meta.getName().length() > 0) {
                        sb.append(" name=\"").append(meta.getName()).append("\"");
                    }
                    if (meta.getHttpEquiv() != null && meta.getHttpEquiv().length() > 0) {
                        sb.append(" http-equiv=\"").append(meta.getHttpEquiv()).append("\"");
                    }
                    sb.append("/>\n");
                    sbMetas.append(sb);
                }
                insertAfter(sbHtml, "<head>", sbMetas.toString());
            }
        }
    }

    /**
     * This function is generates &lt;link&gt; tag for all link, rss and rdf
     * definitions in vroom-config file.
     *
     * @param sbHtml - String buffer of HTML code
     * @param uri - The web page uri
     * @param vc - VroomConfig object
     */
    public static void addHeadLinks(StringBuffer sbHtml, String uri, VroomConfig vc) {
        for (Webpage wp : vc.getWebpage()) {
            if (uri.matches(wp.getUri())) {
                StringBuffer sbLinks = new StringBuffer();
                for (Link link : wp.getLink()) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("<link rel=\"").append(link.getRel()).append("\"");
                    if (link.getMedia() != null && link.getMedia().length() > 0) {
                        sb.append(" media=\"").append(link.getMedia()).append("\"");
                    }
                    if (link.getRev() != null && link.getRev().length() > 0) {
                        sb.append(" rev=\"").append(link.getRev()).append("\"");
                    }
                    if (link.getTarget() != null && link.getTarget().length() > 0) {
                        sb.append(" target=\"").append(link.getTarget()).append("\"");
                    }
                    if (link.getTitle() != null && link.getTitle().length() > 0) {
                        sb.append(" title=\"").append(link.getTitle()).append("\"");
                    }
                    if (link.getType() != null && link.getType().length() > 0) {
                        sb.append(" type=\"").append(link.getType()).append("\"");
                    }
                    if (link.getUrl() != null && link.getUrl().length() > 0) {
                        sb.append(" href=\"").append(link.getUrl()).append("\"");
                    }
                    if (link.getUrlLang() != null && link.getUrlLang().length() > 0) {
                        sb.append(" hreflang=\"").append(link.getMedia()).append("\"");
                    }
                    sb.append("/>\n");
                    sbLinks.append(sb);
                }
                insertAfter(sbHtml, "<head>", sbLinks.toString());
            }
        }
    }

    /**
     * <p> This method adds required script tags such as to access "vroom.js"
     * file. </p>
     *
     * @param sbHtml - Source HTML
     * @param hreq - Request
     */
    public static void addRequiredScripts(StringBuffer sbHtml, HttpServletRequest hreq) {
        String contextPath = hreq.getContextPath();
        String servletName = "/vroom?";
        String srcPrefix = contextPath + servletName + "v-rt=js&amp;v-method=file&amp;v-src=";
        String[] scripts = VroomFilter.scripts;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < scripts.length; i++) {
            sb.append("<script type=\"text/javascript\" src=\"").append(srcPrefix).append(scripts[i]).append("\"></script>\n");
        }
        insertBefore(sbHtml, "</head>", sb.toString());
    }

    /**
     * <p> This method adds all the applicable script tags in the &lt;head&gt;
     * section. These tags are defined in the vroom's configuration file. </p>
     *
     * @param sbHtml - HTML source code of the webpage
     * @param uri - URI of the webpage
     * @param vc - VroomConfig object
     * @param req - Request object
     */
    public static void addHeadScripts(StringBuffer sbHtml, String uri, VroomConfig vc, HttpServletRequest req) {
        for (Webpage wp : vc.getWebpage()) {
            if (uri.matches(wp.getUri())) {
                StringBuffer sbScripts = new StringBuffer();
                for (Script script : wp.getScript()) {
                    String url = script.getUrl();
                    if (url != null && url.startsWith("#{contextPath}")) {
                        url = url.replaceAll("#\\{contextPath}", req.getContextPath());
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("<script type=\"").append(script.getType()).append("\"");
                    if (script.getUrl() != null) {
                        sb.append(" src=\"").append(url).append("\">");
                    } else {
                        sb.append(">\n").append(script.getValue()).append("\n");
                    }
                    sb.append("</script>\n");
                    sbScripts.append(sb);
                }
                insertBefore(sbHtml, "</head>", sbScripts.toString());
            }
        }
    }

    /**
     * <p> This method adds &lt;link&gt; tag in the head section for all the
     * stylesheets defined for the request in vroom's configuration file. </p>
     *
     * @param sbHtml - HTML source code
     * @param uri - Webpage URI
     * @param vc - VroomConfig object
     * @param req - Request object
     */
    public static void addStylesheets(StringBuffer sbHtml, String uri, VroomConfig vc, HttpServletRequest req) {
        for (Webpage wp : vc.getWebpage()) {
            if (uri.matches(wp.getUri())) {
                StringBuffer sbStylesheets = new StringBuffer();
                for (Stylesheet stylesheet : wp.getStylesheet()) {
                    StringBuffer sb = new StringBuffer();
                    String url = stylesheet.getUrl();
                    if (url != null && url.startsWith("#{contextPath}")) {
                        url = url.replaceAll("#\\{contextPath}", req.getContextPath());
                    }
                    if (stylesheet.getUrl() != null) {
                        sb.append("<link type=\"").append(stylesheet.getType()).append("\" rel=\"stylesheet\" href=\"").append(url).append("\"/>\n");
                    } else {
                        sb.append("<style>\n").append(stylesheet.getValue()).append("\n</style>\n");
                    }
                    sbStylesheets.append(sb);
                }
                insertBefore(sbHtml, "</head>", sbStylesheets.toString());
            }
        }
    }

    /**
     * <p> This method adds the &lt;script&gt; tag before the &lt;/html&gt; tag
     * to call request sepcific init() method. All event bindings happen init()
     * method of the request. </p>
     *
     * @param sbHtml - Source HTML
     * @param uri - The request URI
     * @param hreq - Request
     */
    public static void addInitScript(StringBuffer sbHtml, String uri, HttpServletRequest hreq) {
        String contextPath = hreq.getContextPath();
        String servletName = "/vroom?";
        String _uri = (uri == null) ? VroomUtilities.getUriWithoutContext(hreq) : uri;
        String src = contextPath + servletName + "v-rt=js&amp;v-method=generate&amp;v-uri=" + _uri;
        String tag = "<script type=\"text/javascript\" src=\"" + src + "\"></script>\n";
        insertBefore(sbHtml, "</head>", tag);
        String uriInitMethod = _uri.replace('/', '_').replace('.', '_').replace('-', '_') + "_init()";
        tag = "<script type=\"text/javascript\">" + uriInitMethod + ";</script>\n";
        if (sbHtml.indexOf("body") != -1) {
            insertBefore(sbHtml, "</html>", tag);
        } else {
            insertBefore(sbHtml, "</head>", tag);
        }
    }

    /**
     * <p> This method inserts a text in a string buffer before a specific text.
     * </p>
     *
     * @param sb - Source String Buffer
     * @param text - Text to find
     * @param textToInsert - Text to insert before the found text
     */
    public static void insertBefore(StringBuffer sb, String text, String textToInsert) {
        insert(sb, text, textToInsert, false);
    }

    /**
     * <p> This method inserts a text in a string buffer after a specific text.
     * </p>
     *
     * @param sb - Source String Buffer
     * @param text - Text to find
     * @param textToInsert - Text to insert after the found text
     */
    public static void insertAfter(StringBuffer sb, String text, String textToInsert) {
        insert(sb, text, textToInsert, true);
    }

    /**
     * <p> This method inserts a text in a string buffer before or after a
     * specific text based on the value of after flag. </p>
     *
     * @param sb - Source String Buffer
     * @param text - Text to find
     * @param textToInsert - Text to insert before or after the found text
     * @param after - if true the value is inserted after the found text
     * otherwise before.
     */
    private static void insert(StringBuffer sb, String text, String textToInsert, boolean after) {
        int index = sb.indexOf(text);
        if (index >= 0) {
            if (after) {
                sb.insert(index + text.length(), textToInsert);
            } else {
                sb.insert(index, textToInsert);
            }
        }
    }

    /**
     * <p> This method is used to replace a text with some other text. </p>
     *
     * @param sb - Source String Buffer
     * @param text - Text to find
     * @param replaceWith - Text to replace found text
     */
    public static void replace(StringBuffer sb, String text, String replaceWith) {
        int index = sb.indexOf(text);
        while (index >= 0) {
            sb.replace(index, index + text.length(), replaceWith);
            index = sb.indexOf(text);
        }
    }

    /**
     * <p> This method generates the postback script which is used to update
     * form fields. </p>
     *
     * @param sbHtml - HTML source code
     * @param uri - Webpage URI
     * @param hreq - Request Object
     * @param postbackMap - Map of post back elements and their values.
     */
    public static void addPostbackScript(StringBuffer sbHtml, String uri, HttpServletRequest hreq, Map postbackMap) {
        String _uri = (uri == null) ? VroomUtilities.getUriWithoutContext(hreq) : uri;
        _uri = _uri.replace('/', '_').replace('.', '_').replace('-', '_');
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">\n");
        String function = "function " + _uri + "_postback() {\n";
        sb.append(function);
        Iterator iter = postbackMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            java.lang.Object value = postbackMap.get(key);
            JSONObject jo;
            try {
                jo = VroomUtilities.convertObjectToJSONObject(value);
                String stmt = "VroomUtils.updateElement('" + key + "', '" + jo.toString() + "');\n";
                sb.append(stmt);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sb.append("}\n");
        sb.append("</script>\n");
        insertBefore(sbHtml, "</head>", sb.toString());
    }

    public static void addDummyPostbackScript(StringBuffer sbHtml, String uri, HttpServletRequest hreq) {
        String _uri = (uri == null) ? VroomUtilities.getUriWithoutContext(hreq) : uri;
        _uri = _uri.replace('/', '_').replace('.', '_').replace('-', '_');
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">\n");
//        String function = "var " + _uri + "_postback = function() {\n";
        String function = "function " + _uri + "_postback() {}\n";
        sb.append(function);
        sb.append("</script>\n");
        insertBefore(sbHtml, "</head>", sb.toString());
    }
}
