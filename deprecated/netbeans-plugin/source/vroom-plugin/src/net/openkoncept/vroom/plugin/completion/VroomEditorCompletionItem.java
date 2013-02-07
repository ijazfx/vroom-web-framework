/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author ijazfx
 */
public class VroomEditorCompletionItem implements CompletionItem {

    private String text;
    String description;
    ImageIcon icon;
    int priority;
    
    public VroomEditorCompletionItem(String text, String description, ImageIcon icon, int priority) {
        this.text = text;
        this.description = description;
        this.icon = icon;
        this.priority = priority;
    }
    
    public void defaultAction(JTextComponent comp) {
        Completion.get().hideAll();
        Document doc = comp.getDocument();
        int offset = comp.getCaretPosition();
        try {
            int start = comp.getSelectionStart();
            int end = comp.getSelectionEnd();
            if(start != -1 && end != -1) {
                doc.remove(start, end-start);
                offset = comp.getCaretPosition();
            }
            doc.insertString(offset,getText(), null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void processKeyEvent(KeyEvent evt) {
        
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getText(),description, g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(icon,getText(), description, g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent arg0) {
        return false;
    }

    public int getSortPriority() {
        return priority;
    }

    public CharSequence getSortText() {
        return getText();
    }

    public CharSequence getInsertPrefix() {
        return getText();
    }

    public

    String getText() {
        return text;
    }

}
