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
public class Obj implements Serializable {

    protected String name;
    protected String beanClass;
    protected String var;
    protected String scope;
    protected List events = new ArrayList();

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

    public Obj() {
    }

    public Obj(String name) {
        this(name, null, null, null);
    }

    public Obj(String name, String beanClass, String var, String scope) {
        setName(name);
        setBeanClass(beanClass);
        setVar(var);
        setScope(scope);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
