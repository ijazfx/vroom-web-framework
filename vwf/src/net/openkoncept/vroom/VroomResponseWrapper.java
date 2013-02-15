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

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * <p>
 * VroomResponseWrapper is one of the foundation classes of Vroom framework. It
 * is used by VroomFilter buffer response.
 * </p>
 *
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class VroomResponseWrapper extends HttpServletResponseWrapper {

    /**
     * <p>
     * The only constructor which takes response as an argument. This response
     * can the be obtained using getOriginalResponse() method.
     * </p>
     *
     * @param response      - HttpServletResponse
     * @param contentLength - Content Length, if passed 0 or negative sets to 4096
     */
    public VroomResponseWrapper(HttpServletResponse response, int contentLength) {
        super(response);
        caw = new CharArrayWriter(contentLength);
        writer = new PrintWriter(caw);
    }

    public String toString() {
        return caw.toString();
    }

    public VroomResponseWrapper(HttpServletResponse httpServletResponse) {
        this(httpServletResponse, 0);
    }

    public void addCookie(Cookie cookie) {
        super.addCookie(cookie);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean containsHeader(String s) {
        return super.containsHeader(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String encodeURL(String s) {
        return super.encodeURL(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String encodeRedirectURL(String s) {
        return super.encodeRedirectURL(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String encodeUrl(String s) {
        return super.encodeUrl(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String encodeRedirectUrl(String s) {
        return super.encodeRedirectUrl(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void sendError(int i, String s) throws IOException {
        super.sendError(i, s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void sendError(int i) throws IOException {
        super.sendError(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void sendRedirect(String s) throws IOException {
        super.sendRedirect(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setDateHeader(String s, long l) {
        super.setDateHeader(s, l);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addDateHeader(String s, long l) {
        super.addDateHeader(s, l);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setHeader(String s, String s1) {
        super.setHeader(s, s1);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addHeader(String s, String s1) {
        super.addHeader(s, s1);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setIntHeader(String s, int i) {
        super.setIntHeader(s, i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addIntHeader(String s, int i) {
        super.addIntHeader(s, i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setStatus(int i) {
        super.setStatus(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setStatus(int i, String s) {
        super.setStatus(i, s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    public ServletResponse getResponse() {
        return super.getResponse();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setResponse(ServletResponse servletResponse) {
        super.setResponse(servletResponse);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setCharacterEncoding(String s) {
        super.setCharacterEncoding(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getCharacterEncoding() {
        return super.getCharacterEncoding();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return super.getOutputStream();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setContentLength(int i) {
        super.setContentLength(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setContentType(String s) {
        super.setContentType(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getContentType() {
        return super.getContentType();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setBufferSize(int i) {
        super.setBufferSize(i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int getBufferSize() {
        return super.getBufferSize();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void flushBuffer() throws IOException {
        super.flushBuffer();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void reset() {
        super.reset();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void resetBuffer() {
        super.resetBuffer();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setLocale(Locale locale) {
        super.setLocale(locale);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Locale getLocale() {
        return super.getLocale();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected CharArrayWriter caw = null;
    protected PrintWriter writer = null;

    public boolean isCommitted() {
        return super.isCommitted();
    }
}
