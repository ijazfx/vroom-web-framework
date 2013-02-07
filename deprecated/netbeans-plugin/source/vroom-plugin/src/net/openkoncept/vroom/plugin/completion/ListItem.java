/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.completion;

import org.openide.filesystems.FileObject;

/**
 *
 * @author ijazfx
 */
public class ListItem {

    private String text;
    private String description;
    private FileObject fileObject;

    public ListItem(String text, String description) {
        this.text = text;
        this.description = description;
    }

    public ListItem(String text, String description, FileObject fileObject) {
        this.text = text;
        this.description = description;
        this.fileObject = fileObject;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

}
