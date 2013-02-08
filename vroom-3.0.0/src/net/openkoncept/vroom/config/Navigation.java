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
public class Navigation implements Serializable {

    protected String outcome;
    protected String url;
    protected Boolean forward;

    public Navigation() {
    }

    public Navigation(String outcome, String url) {
        this(outcome, url, null);
    }

    public Navigation(String outcome, String url, Boolean redirect) {
        setOutcome(outcome);
        setUrl(url);
        setForward(redirect);
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getForward() {
        return forward;
    }

    public void setForward(Boolean forward) {
        if (forward == null) {
            this.forward = Boolean.FALSE;
        } else {
            this.forward = forward;
        }
    }
}