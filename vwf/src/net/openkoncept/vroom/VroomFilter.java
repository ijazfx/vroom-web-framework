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

import net.openkoncept.vroom.config.VroomConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * VroomFilter is one of the foundation classes of Vroom framework. This filter
 * is used to by the framework to intercept the requests and alter response
 * based on definition in the config file.
 * </p>
 * <p>
 * VroomFilter support following init-params as part of web.xml configuration.
 * </p>
 * <ul>
 * <li>config-file - This is used to specify the configuration file. Typically
 * it is named as vroom-config.xml and placed in /WEB-INF/ folder of the web
 * application.</li>
 * <li>content-length - This parameter tells how much is the initial size of the
 * document that will be processed. The default value is 4096. This should be
 * the average of html/jsp file size.</li>
 * <li>caching - This is a boolean value which instruct the framework to cache
 * processed script files. The default value is false.</li>
 * <li>cache-folder - This is the path where the framework should store the
 * script files for caching and is only used if caching is set to true. Your
 * application must have a read/write/delete access to this folder.</li>
 * </ul>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomFilter implements Filter {

    public static final String CONFIG_FILE = "config-file";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String CACHING = "caching";
    public static final String CACHE_FOLDER = "cache-folder";
    public static final String DEBUG = "debug";

    public static final String INCLUDE_FIRST = "include-first";

    public static String[] scripts = null;

    public void destroy() {
        // not yet implemented.
    }

    /**
     * This method is called everytime the request uri matches with the pattern defined in web.xml for VroomFilter.
     * Only those request are intercepted where the uri matches with the pattern defined in vroom's configuration file.
     *
     * @param request  - HttpServletRequest
     * @param response - HttpServletResponse
     * @param chain    - FilterChain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        vconf.reload();
        String uri = VroomUtilities.getUriWithoutContext(request);
        if (uri.equals("/vroom")) {
            uri = request.getParameter("v-caller-uri");
        }
        if (uri != null && vconf.isUriInDefinition(uri)) {
            PrintWriter out = response.getWriter();
            VroomResponseWrapper responseWrapper = new VroomResponseWrapper((HttpServletResponse) response, contentLength);
            chain.doFilter(request, responseWrapper);
            StringBuffer sbHtml = new StringBuffer(responseWrapper.toString());
            VroomHtmlProcessor.addHeadMetaTags(sbHtml, uri, vconf);
            VroomHtmlProcessor.addHeadLinks(sbHtml, uri, vconf);
            VroomHtmlProcessor.addRequiredScripts(sbHtml, (HttpServletRequest) request);
            VroomHtmlProcessor.addInitScript(sbHtml, uri, (HttpServletRequest) request);
            VroomHtmlProcessor.addHeadScripts(sbHtml, uri, vconf, (HttpServletRequest) request);
            VroomHtmlProcessor.addStylesheets(sbHtml, uri, vconf, (HttpServletRequest) request);
            // Add dummy postback method...
            VroomHtmlProcessor.addDummyPostbackScript(sbHtml, uri, (HttpServletRequest) request);
            HttpServletRequest hreq = (HttpServletRequest) request;
            String html = sbHtml.toString().replaceAll("#\\{contextPath}", hreq.getContextPath());
            response.setContentLength(html.length());
            out.print(html);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * This method is called when the application loaded for the first time. The vroom's configuration file is loaded
     * at this stage.
     *
     * @param config - FilterConfig
     * @throws ServletException
     */
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        configFile = config.getServletContext().getRealPath(config.getInitParameter(CONFIG_FILE));
        try {
            contentLength = Integer.parseInt(config.getInitParameter(CONTENT_LENGTH));
        } catch (NumberFormatException ex) {
            contentLength = 4096;
        } finally {
            if (contentLength <= 0 || contentLength > 65536) {
                contentLength = 4096;
            }
        }
        caching = Boolean.valueOf(config.getInitParameter(CACHING)).booleanValue();
        cacheFolder = config.getInitParameter(CACHE_FOLDER);
        debug = Boolean.valueOf(config.getInitParameter(DEBUG)).booleanValue();

        includeFirst = config.getInitParameter(INCLUDE_FIRST);

        String[] additionalScripts = {};

        if(includeFirst != null) {
            additionalScripts = includeFirst.split(",");
        }

        scripts = new String[additionalScripts.length + 2];
        for(int i = 0; i < additionalScripts.length; i++) {
            scripts[i] = additionalScripts[i].trim();
        }

        if (debug) {
            scripts[scripts.length-2] = "/vroom/prototype.js";
            scripts[scripts.length-1] = "/vroom/vroom.js";
        } else {
            scripts[scripts.length-2] = "/vroom/prototype.js";
            scripts[scripts.length-1] = "/vroom/vroom.js";
        }
        vconf = VroomConfig.initialize(configFile);
    }

    VroomConfig vconf = null;
    FilterConfig config = null;
    String configFile = null;
    int contentLength = 0;
    boolean caching = false;
    String cacheFolder = null;
    boolean debug = false;

    String includeFirst = null;

}