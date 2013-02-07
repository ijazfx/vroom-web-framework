/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin.completion;

import javax.swing.text.JTextComponent;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;

/**
 *
 * @author ijazfx
 */
public class VroomConfigCompletionProvider implements CompletionProvider {

    public CompletionTask createTask(int queryType, JTextComponent comp) {
        if((queryType & COMPLETION_QUERY_TYPE) != 0) {
            
            return new AsyncCompletionTask(new VroomConfigCompletionQuery(), comp);
        }
        return null;
    }

    public int getAutoQueryTypes(JTextComponent comp, String typedText) {
        return 0;
    }

}
