/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin.completion;

import javax.swing.text.JTextComponent;
import net.openkoncept.vroom.plugin.dialog.VroomMiniEditorPane;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author ijazfx
 */
public class VroomEditorCompletionProvider implements CompletionProvider {

    public CompletionTask createTask(int queryType, JTextComponent comp) {
        if((queryType & COMPLETION_QUERY_TYPE) != 0 && comp instanceof VroomMiniEditorPane) {
            VroomMiniEditorPane ep = (VroomMiniEditorPane) comp;
            String contentType = ep.getContentType();
            if("text/javascript".equals(contentType)) {
                VroomJSCompletionQuery query = new VroomJSCompletionQuery();
                query.setCssFiles(ep.getCssFiles());
                query.setJsFiles(ep.getJsFiles());
                query.setUri(ep.getUri());
                return new AsyncCompletionTask(query, comp);
            } else if("text/html".equals(contentType)) {
                VroomHtmlCompletionQuery query = new VroomHtmlCompletionQuery();
                query.setCssFiles(ep.getCssFiles());
                query.setUri(ep.getUri());
                return new AsyncCompletionTask(query, comp);
            }            
        }
        return null;
    }

    public int getAutoQueryTypes(JTextComponent arg0, String arg1) {
        return 0;
    }

}
