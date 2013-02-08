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
public class RSS extends Link implements Serializable {

    public static final String REL_ALTERNATE = "alternate";
    public static final String TYPE_RSS = "application/rss+xml";

    public RSS() {
    }

    public RSS(String url, String title) {
        setUrl(url);
        setTitle(title);
        setRel(REL_ALTERNATE);
        setType(TYPE_RSS);
    }

}