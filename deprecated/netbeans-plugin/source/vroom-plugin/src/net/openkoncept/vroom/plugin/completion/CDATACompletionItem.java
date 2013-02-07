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
import net.openkoncept.vroom.plugin.Constants;
import net.openkoncept.vroom.plugin.util.TextUtilities;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

/**
 *
 * @author ijazfx
 */
public class CDATACompletionItem implements VroomConfigCompletionItem {

    String text;
    String description;
    ImageIcon icon;

    public CDATACompletionItem(String type, String description, ImageIcon icon) {
        this.text = type;
        this.description = description;
        this.icon = icon;
    }

    public void defaultAction(JTextComponent comp) {
        Completion.get().hideAll();
        Document doc = comp.getDocument();
        int offset = comp.getCaretPosition();
        try {
            doc.insertString(offset, getFullText(), null);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void processKeyEvent(KeyEvent evt) {
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(text, description, g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor, Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(icon, text, description, g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent comp) {
        return true;
    }

    public int getSortPriority() {
        return 0;
    }

    public CharSequence getSortText() {
        return text;
    }

    public CharSequence getInsertPrefix() {
        return text;
    }

    public String getText() {
        return text;
    }

    public String getFullText() {
        return text;
    }
    
}
