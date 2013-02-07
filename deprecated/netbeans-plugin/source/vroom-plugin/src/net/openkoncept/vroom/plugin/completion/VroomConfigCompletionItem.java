/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.completion;

import org.netbeans.spi.editor.completion.CompletionItem;

/**
 *
 * @author ijazfx
 */
public interface VroomConfigCompletionItem extends CompletionItem {

    String getText();

    String getFullText();

}
