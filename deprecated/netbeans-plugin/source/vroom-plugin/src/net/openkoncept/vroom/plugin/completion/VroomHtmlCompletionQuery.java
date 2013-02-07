/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.completion;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import net.openkoncept.vroom.plugin.Constants;
import net.openkoncept.vroom.plugin.PluginContext;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.util.Exceptions;

/**
 *
 * @author ijazfx
 */
public class VroomHtmlCompletionQuery extends AsyncCompletionQuery {

    private String uri;
    private List<String> cssFiles;
    
    int anchorOffset = -1;
    int caretOffset = -1;
    JTextComponent comp;
    String typedText = Constants.EMPTY_STRING;
    List<VroomEditorCompletionItem> items = new ArrayList<VroomEditorCompletionItem>();

    @Override
    protected boolean canFilter(JTextComponent comp) {
        Document doc = comp.getDocument();
        int start = comp.getSelectionStart();
        int end = comp.getSelectionEnd();
        try {
            typedText = doc.getText(start, end - start);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (typedText.equals(Constants.EMPTY_STRING)) {
            return false;
        }
        for (VroomEditorCompletionItem item : items) {
            if (typedText.equals(item.getText())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void filter(CompletionResultSet rs) {
       List<VroomEditorCompletionItem> filtered = new ArrayList<VroomEditorCompletionItem>();
        if (!typedText.equals(Constants.EMPTY_STRING)) {
            for (VroomEditorCompletionItem item : items) {
                if (item.getText().toLowerCase().startsWith(typedText.toLowerCase()) ||
                        item.getText().toLowerCase().startsWith(typedText.toLowerCase())) {
                    filtered.add(item);
                }
            }
            if (filtered.size() > 0) {
                rs.addAllItems(filtered);
            }
        }
        rs.finish();
    }

    @Override
    protected void query(CompletionResultSet rs, Document doc, int caretOffset) {
        PluginContext ctx = PluginContext.getContext();
        items.clear();
        List<ListItem> listItems = ctx.getServerVarList(false);
        for (ListItem item : listItems) {
            items.add(new VroomEditorCompletionItem(item.getText(), item.getDescription(), Constants.ID_ICON, 1000));
        }
        if(cssFiles != null) {
            listItems = ctx.getCssItemList(cssFiles, true);
            for (ListItem item : listItems) {
                items.add(new VroomEditorCompletionItem(item.getText(), item.getDescription(), Constants.ATTRIBUTE_ICON, 2000));
            }
        }
        if(items.size() > 0) {
            rs.addAllItems(items);
        }
        rs.setAnchorOffset(caretOffset);
        rs.finish();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setCssFiles(List<String> cssFiles) {
        this.cssFiles = cssFiles;
    }
}
