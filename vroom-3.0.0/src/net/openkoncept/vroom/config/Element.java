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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class Element implements Serializable {

    protected String id;
    protected String beanClass;
    protected String var;
    protected String scope;
    protected List events = new ArrayList();

    // added for form level elements only...
    protected String property;
    protected String format;


    public Event addEvent(String type, String method) {
        Event event = new Event(type, method);
        events.add(event);
        return event;
    }

    public Event addEvent(String type, String method, Boolean sync) {
        Event event = new Event(type, method, sync);
        events.add(event);
        return event;
    }

    public Event addEvent(String type, String method, Boolean sync, String beanClass, String var, String scope) {
        Event event = new Event(type, method, sync, beanClass, var, scope);
        events.add(event);
        return event;
    }

    public Element() {
    }

    public Element(String id) {
        this(id, null, null, null);
    }

    public Element(String id, String beanClass, String var, String scope) {
        setId(id);
        setBeanClass(beanClass);
        setVar(var);
        setScope(scope);
    }

    public Element(String id, String property, String format) {
        this(id, property, format, null, null, null);
    }

    public Element(String id, String property, String format, String beanClass, String var, String scope) {
        setId(id);
        setProperty(property);
        setFormat(format);
        setBeanClass(beanClass);
        setVar(var);
        setScope(scope);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        if (scope == null) {
            this.scope = Constants.SCOPE_SESSION;
        } else {
            this.scope = scope;
        }
    }

    public List getEvents() {
        return events;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
