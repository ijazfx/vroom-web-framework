/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.action;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.openkoncept.vroom.plugin.Constants;
import net.openkoncept.vroom.plugin.VroomConfigDataObject;
import net.openkoncept.vroom.plugin.dialog.VroomMiniEditorDialog;
import net.openkoncept.vroom.plugin.util.TextUtilities;
import net.openkoncept.vroom.plugin.util.XMLUtilities;
import org.netbeans.modules.editor.indent.api.Indent;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.TopComponent;

public final class VroomConfigEditAction extends CookieAction {

    private static VroomMiniEditorDialog dialog;
    
    protected void performAction(Node[] activatedNodes) {
        VroomConfigDataObject vroomConfigDataObject = activatedNodes[0].getLookup().lookup(VroomConfigDataObject.class);
        CloneableEditor cedit = null;
        if (TopComponent.getRegistry().getActivated() instanceof CloneableEditor) {
            cedit = (CloneableEditor) TopComponent.getRegistry().getActivated();
            JEditorPane jep = cedit.getEditorPane();
            if (jep != null) {
                Document doc = jep.getDocument();
                int offset = jep.getCaretPosition();
                String text = Constants.EMPTY_STRING;
                try {
                    text = doc.getText(0, doc.getLength());
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
                String attrValue = XMLUtilities.findAttributeValueInTag(text, offset, Constants.CALL, Constants.TYPE);
                int tagIndex = XMLUtilities.indexOfTag(text, offset, Constants.CALL);
                String ocdata = XMLUtilities.getTagInnerData(text, offset, Constants.CALL);
                String cdata = ocdata.trim();
                if (cdata.startsWith(Constants.CDATA)) {
                    cdata = cdata.substring(Constants.CDATA.length());
                }
                if (cdata.endsWith(Constants.CDATA_CLOSE)) {
                    cdata = cdata.substring(0, cdata.length() - Constants.CDATA_CLOSE.length());
                }
                cdata = cdata.trim();
                if (tagIndex != -1) {
                    if(dialog == null) {
                        dialog = new VroomMiniEditorDialog(null, true);
                    }
                    String uri = identifyUri(jep.getDocument(), offset);
                    dialog.setUri(uri);
                    dialog.setJsFiles(identifyJsFiles(doc, offset));
                    dialog.setCssFiles(identifyCssFiles(doc, offset));
                    if (attrValue.equals("script")) {
                        dialog.setContentType("text/javascript");
                        dialog.setTitle(org.openide.util.NbBundle.getMessage(VroomConfigEditAction.class, "VroomMiniJSEditor.title"));
                        dialog.setIconImage(Constants.SCRIPT_ICON.getImage());
                    } else if (attrValue.equals("update")) {
                        dialog.setContentType("text/html");
                        dialog.setIconImage(Constants.TAG_ICON.getImage());
                        dialog.setTitle(org.openide.util.NbBundle.getMessage(VroomConfigEditAction.class, "VroomMiniHTMLEditor.title"));
                    }
                    dialog.setContent(cdata);
                    dialog.setVisible(true);
                    if (dialog.getReturnStatus() == VroomMiniEditorDialog.RET_OK) {
                        int start = text.indexOf(Constants.GT, tagIndex);
                        if (start != -1) {
                            start++;
                            int end = text.indexOf(Constants.LT_SLASH + Constants.CALL + Constants.GT, start);
                            if (end != 1) {
                                try {                                    
                                    doc.remove(start, ocdata.length());
                                } catch (BadLocationException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                                try {
                                    StringBuffer sb = new StringBuffer();
                                    sb.append(Constants.NEW_LINE).append(Constants.CDATA).append(Constants.NEW_LINE);
                                    sb.append(dialog.getContent());
                                    sb.append(Constants.NEW_LINE).append(Constants.CDATA_CLOSE).append(Constants.NEW_LINE);
                                    cdata = sb.toString();
                                    doc.insertString(start, cdata, null);
                                    Reformat rf = Reformat.get(doc);
                                    rf.lock();
                                    rf.reformat(start, start + cdata.length());
                                    rf.unlock();
                                    Indent ind = Indent.get(doc);
                                    ind.lock();
                                    ind.reindent(start);
                                    ind.unlock();
                                } catch (BadLocationException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private String identifyUri(Document doc, int caretOffset) {
        String uri = Constants.EMPTY_STRING;
        int start = caretOffset - 1;
        while (start >= 0) {
            try {
                String text = doc.getText(start, caretOffset - start);
                if (text.startsWith(Constants.URI + Constants.EQUAL)) {
                    String[] pair = TextUtilities.getAttrPairAtOffset(doc, start);
                    uri = pair[1];
                    break;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            start--;
        }
        return uri;
    }

    private List<String> identifyCssFiles(Document doc, int caretOffset) {
        List<String> cssFiles = new ArrayList<String>();
        int start = caretOffset - 1;
        String text = null;
        while (start >= 0) {
            try {
                text = doc.getText(start, caretOffset - start);
                if (text.startsWith(Constants.WEBPAGE)) {
                    break;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            start--;
        }
        if(start == -1) {
            return cssFiles;
        }        
        int cssIndex = 0;
        while (cssIndex != -1) {
            cssIndex = text.indexOf(Constants.STYLESHEET, cssIndex);
            if (cssIndex == -1) {
                break;
            }
            int cssCloseIndex = text.indexOf(Constants.GT, cssIndex);
            if (cssCloseIndex == -1) {
                break;
            }
            String temp = text.substring(cssIndex, cssCloseIndex);
            String attr = Constants.URL + Constants.EQUAL + Constants.DQUOTE;
            int spos = temp.indexOf(attr);
            if (spos != -1) {
                spos += attr.length();
                int epos = temp.indexOf(Constants.DQUOTE, spos);
                if (epos != -1) {
                    String url = temp.substring(spos, epos);
                    cssFiles.add(url);
                    cssIndex += temp.length();
                }
            }
            cssIndex++;
        }
        return cssFiles;

    }

    private List<String> identifyJsFiles(Document doc, int caretOffset) {
        List<String> jsFiles = new ArrayList<String>();
        int start = caretOffset - 1;
        String text = null;
        while (start >= 0) {
            try {
                text = doc.getText(start, caretOffset - start);
                if (text.startsWith(Constants.WEBPAGE)) {
                    break;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            start--;
        }
        if(start == -1) {
            return jsFiles;
        }        
        int jsIndex = 0;
        while (jsIndex != -1) {
            jsIndex = text.indexOf(Constants.SCRIPT, jsIndex);
            if (jsIndex == -1) {
                break;
            }
            int jsCloseIndex = text.indexOf(Constants.GT, jsIndex);
            if (jsCloseIndex == -1) {
                break;
            }
            String temp = text.substring(jsIndex, jsCloseIndex);
            String attr = Constants.URL + Constants.EQUAL + Constants.DQUOTE;
            int spos = temp.indexOf(attr);
            if (spos != -1) {
                spos += attr.length();
                int epos = temp.indexOf(Constants.DQUOTE, spos);
                if (epos != -1) {
                    String url = temp.substring(spos, epos);
                    jsFiles.add(url);
                    jsIndex += temp.length();
                }
            }
            jsIndex++;
        }
        return jsFiles;
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(VroomConfigEditAction.class, "CTL_VroomConfigEditAction");
    }

    protected Class[] cookieClasses() {
        return new Class[]{VroomConfigDataObject.class};
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() Javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

