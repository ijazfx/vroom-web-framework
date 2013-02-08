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

/**
 * @author Farrukh Ijaz Muhammad Riaz (ijazfx@yahoo.com)
 */
public class Call implements Serializable {

    protected String type;
    protected String name;
    protected String tag;
    protected String id;
    protected String attribute;
    protected String value;
    protected String url;

    public Call() {
    }

    public Call(String value) {
        this(Constants.CALL_TYPE_SCRIPT, null, null, null, null, value);
    }

    public Call(String id, String tag, String name, String attribute, String value) {
        this(Constants.CALL_TYPE_UPDATE, id, tag, name, attribute, value);
    }

    public Call(String type, String id, String tag, String name, String attribute, String value) {
        setType(type);
        setId(id);
        setTag(tag);
        setName(name);
        setAttribute(attribute);
        setValue(value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
