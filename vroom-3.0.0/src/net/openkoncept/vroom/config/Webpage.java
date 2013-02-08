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
public class Webpage implements Serializable {

    protected String uri;
    protected String beanClass;
    protected String var;
    protected String scope;
    protected List metas = new ArrayList();
    protected List links = new ArrayList();
    protected List stylesheets = new ArrayList();
    protected List scripts = new ArrayList();
    protected List objects = new ArrayList();
    protected List forms = new ArrayList();
    protected List elements = new ArrayList();
    protected List events = new ArrayList();

    public Meta addMeta(String content, String httpEquiv) {
        Meta m = new Meta(content, httpEquiv);
        getMetas().add(m);
        return m;
    }

    public Meta addMeta(String name, String httpEquiv, String content) {
        Meta m = new Meta(name, httpEquiv, content);
        getMetas().add(m);
        return m;
    }

    public RSS addRSS(String url, String title) {
        RSS r = new RSS(url, title);
        getLinks().add(r);
        return r;
    }

    public RDF addRDF(String url, String title) {
        RDF r = new RDF(url, title);
        getLinks().add(r);
        return r;
    }

    public Link addLink(String url, String urlLang, String media, String rel, String rev, String target, String type, String title) {
        Link l = new Link(url, urlLang, media, rel, rev, target, type, title);
        getLinks().add(l);
        return l;
    }

    public Stylesheet addStylesheet(String type, String url) {
        Stylesheet s = new Stylesheet(type, url);
        getStylesheets().add(s);
        return s;
    }

    public Stylesheet addStylesheet(String type, String url, String text) {
        Stylesheet s = new Stylesheet(type, url, text);
        getStylesheets().add(s);
        return s;
    }

    public Script addScript(String type, String url) {
        Script s = new Script(type, url);
        getScripts().add(s);
        return s;
    }

    public Script addScript(String type, String url, String text) {
        Script s = new Script(type, url, text);
        getScripts().add(s);
        return s;
    }

    public Obj addObject(String name) {
        Obj object = new Obj(name);
        getObjects().add(object);
        return object;
    }

    public Obj addObject(String name, String beanClass, String var, String scope) {
        Obj object = new Obj(name, beanClass, var, scope);
        getObjects().add(object);
        return object;
    }

    public Form addForm(String id) {
        Form form = new Form(id);
        forms.add(form);
        return form;
    }

    public Form addForm(String id, String method) {
        Form form = new Form(id, method);
        forms.add(form);
        return form;
    }

    public Form addForm(String id, String beanClass, String var, String scope) {
        Form form = new Form(id, beanClass, var, scope);
        forms.add(form);
        return form;
    }

    public Form addForm(String id, String method, String beanClass, String var, String scope) {
        Form form = new Form(id, method, beanClass, var, scope);
        forms.add(form);
        return form;
    }

    public Element addElement(String id) {
        Element element = new Element(id);
        elements.add(element);
        return element;
    }

    public Element addElement(String id, String beanClass, String var, String scope) {
        Element element = new Element(id, beanClass, var, scope);
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

    public Webpage() {
    }

    public Webpage(String uri) {
        this(uri, null, null, null);
    }

    public Webpage(String uri, String beanClass, String var, String scope) {
        setUri(uri);
        setBeanClass(beanClass);
        setVar(var);
        setScope(scope);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public List getForms() {
        return forms;
    }

    public List getObjects() {
        return objects;
    }

    public List getStylesheets() {
        return stylesheets;
    }

    public List getScripts() {
        return scripts;
    }

    public List getMetas() {
        return metas;
    }

    public List getLinks() {
        return links;
    }
}
