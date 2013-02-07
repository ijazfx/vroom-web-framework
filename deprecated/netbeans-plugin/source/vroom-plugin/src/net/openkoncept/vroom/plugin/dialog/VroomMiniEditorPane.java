/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.dialog;

import java.util.List;
import javax.swing.JEditorPane;

/**
 *
 * @author ijazfx
 */
public class VroomMiniEditorPane extends JEditorPane {

    private String uri;
    private List<String> jsFiles;
    private List<String> cssFiles;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getJsFiles() {
        return jsFiles;
    }

    public void setJsFiles(List<String> jsFiles) {
        this.jsFiles = jsFiles;
    }

    public List<String> getCssFiles() {
        return cssFiles;
    }

    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }
}
