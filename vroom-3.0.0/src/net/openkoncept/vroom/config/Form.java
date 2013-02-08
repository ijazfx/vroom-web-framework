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
public class Form implements Serializable {

    protected String id;
    protected String method;
    protected String beanClass;
    protected String var;
    protected String scope;
    protected List navigations = new ArrayList();
    protected List elements = new ArrayList();
    protected List events = new ArrayList();

    public Navigation addNavigation(String outcome, String url) {
        Navigation n = new Navigation(outcome, url);
        navigations.add(n);
        return n;
    }

    public Navigation addNavigation(String outcome, String url, Boolean redirect) {
        Navigation n = new Navigation(outcome, url, redirect);
        navigations.add(n);
        return n;
    }

    public Element addElement(String id, String property, String format) {
        Element element = new Element(id, property, format);
        elements.add(element);
        return element;
    }

    public Element addElement(String id, String property, String format, String beanClass, String var, String scope) {
        Element element = new Element(id, property, format, beanClass, var, scope);
        elements.add(element);
        return element;
    }

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

    public Form() {
    }

    public Form(String id) {
        this(id, null, null, null, null);
    }

    public Form(String id, String method) {
        this(id, method, null, null, null);
    }

    public Form(String id, String beanClass, String var, String scope) {
        this(id, null, beanClass, var, scope);
    }

    public Form(String id, String method, String beanClass, String var, String scope) {
        setId(id);
        setMethod(method);
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public List getElements() {
        return elements;
    }

    public List getNavigations() {
        return navigations;
    }
}
