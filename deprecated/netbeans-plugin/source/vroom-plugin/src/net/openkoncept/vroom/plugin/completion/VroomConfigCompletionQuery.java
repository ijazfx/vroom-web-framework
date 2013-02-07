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
import net.openkoncept.vroom.plugin.Constants.ItemType;
import net.openkoncept.vroom.plugin.PluginContext;
import net.openkoncept.vroom.plugin.util.TextUtilities;
import net.openkoncept.vroom.plugin.util.XMLUtilities;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.api.project.Project;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;

/**
 *
 * @author ijazfx
 */
public class VroomConfigCompletionQuery extends AsyncCompletionQuery {

    int anchorOffset = -1;
    int caretOffset = -1;
    boolean inCDATABlock = false;
    JTextComponent comp;
    String typedText = Constants.EMPTY_STRING;
    List<VroomConfigCompletionItem> items = new ArrayList<VroomConfigCompletionItem>();

    @Override
    protected boolean canFilter(JTextComponent comp) {
        Document doc = comp.getDocument();
        try {
            int start = comp.getSelectionStart();
            int end = comp.getSelectionEnd();
            if(start == end) {
                typedText = TextUtilities.getWordAtOffset(comp.getDocument(), caretOffset, new String[] {Constants.PIPE, Constants.DQUOTE});            
            } else {
                typedText = doc.getText(start, end - start);
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (typedText.equals(Constants.EMPTY_STRING)) {
            return true;
        }
        for (VroomConfigCompletionItem item : items) {
            if (typedText.equalsIgnoreCase(item.getText()) || typedText.equalsIgnoreCase(item.getFullText())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void filter(CompletionResultSet rs) {
        List<VroomConfigCompletionItem> filtered = new ArrayList<VroomConfigCompletionItem>();
        if (!typedText.equals(Constants.EMPTY_STRING)) {
            for (VroomConfigCompletionItem item : items) {
                if (item.getText().toLowerCase().startsWith(typedText.toLowerCase()) ||
                        item.getFullText().toLowerCase().startsWith(typedText.toLowerCase())) {
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
    protected void preQueryUpdate(JTextComponent comp) {
        super.preQueryUpdate(comp);
    }

    @Override
    protected void prepareQuery(JTextComponent comp) {
        this.comp = comp;
        this.caretOffset = comp.getCaretPosition();        
    }

    @Override
    protected void query(CompletionResultSet rs, Document doc, int caretOffset) {
        items.clear();
        if (anchorOffset == -1) {
            anchorOffset = caretOffset;
        }
        this.caretOffset = caretOffset;
        String[] pair = TextUtilities.getAttrPairAtOffset(doc, caretOffset);
        String attr = pair[0];
        if (attr.equals(Constants.URI)) {
            queryUri(rs, doc, caretOffset);
        } else if (attr.equals(Constants.URL)) {
            queryUrl(rs, doc, caretOffset);
        } else if (attr.equals(Constants.BEAN_CLASS)) {
            queryBeanClass(rs, doc, caretOffset);
        } else if (attr.equals(Constants.METHOD)) {
            queryMethod(rs, doc, caretOffset);
        } else if (attr.equals(Constants.SCOPE)) {
            queryScope(rs, doc, caretOffset);
        } else if (attr.equals(Constants.OUTCOME)) {
            queryOutcome(rs, doc, caretOffset);
        } else if (attr.equals(Constants.FORWARD)) {
            queryBoolean(rs, doc, caretOffset);
        } else if (attr.equals(Constants.TYPE)) {
            queryType(rs, doc, caretOffset);
        } else if (attr.equals(Constants.ID)) {
            queryId(rs, doc, caretOffset);
        } else if (attr.equals(Constants.NAME)) {
            queryName(rs, doc, caretOffset);
        } else if (attr.equals(Constants.TAG)) {
            queryTag(rs, doc, caretOffset);
        } else if (attr.equals(Constants.ATTRIBUTE)) {
            queryAttribute(rs, doc, caretOffset);
        } else if (attr.equals(Constants.HTTP_EQUIV)) {
            queryHttpEquiv(rs, doc, caretOffset);
        } else if (attr.equals(Constants.VALUE)) {
            queryValue(rs, doc, caretOffset);
        } 
        if (items.size() > 0) {
            rs.addAllItems(items);
            rs.setAnchorOffset(anchorOffset);
        }
        rs.finish();
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

    private void queryAttribute(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.CALL);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getAttributeList(false), ItemType.ID);
    }

    private void queryBeanClass(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.WEBPAGE) || tag.equals(Constants.OBJECT) ||
                tag.equals(Constants.FORM) || tag.equals(Constants.ELEMENT) ||
                tag.equals(Constants.EVENT);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getBeanClassList(true), ItemType.BEAN_CLASS);
    }

    private String identifyBeanClass(Document doc, int caretOffset) {
        String beanClass = Constants.EMPTY_STRING;
        int start = caretOffset - 1;
        while (start >= 0) {
            try {
                String text = doc.getText(start, caretOffset - start);
                if (text.startsWith(Constants.BEAN_CLASS + Constants.EQUAL)) {
                    String[] pair = TextUtilities.getAttrPairAtOffset(doc, start);
                    beanClass = pair[1];
                    break;
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
            start--;
        }
        return beanClass;
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

    private void queryBoolean(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.NAVIGATION);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getBooleanList(false), ItemType.ID);
    }

    private void queryHttpEquiv(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.META);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getMetaHttpEquivList(false), ItemType.ID);
    }

    private void queryId(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.FORM) || tag.equals(Constants.ELEMENT) ||
                tag.equals(Constants.CALL);
        if (!allowed) {
            return;
        }
        String uri = identifyUri(doc, caretOffset);
        if(tag.equals(Constants.FORM)) {
            loadItemsFromList(PluginContext.getContext().getFormIdList(uri, true), ItemType.ID);
        } else {
            loadItemsFromList(PluginContext.getContext().getHtmlIdList(uri, true), ItemType.ID);
        }        
    }

    private void queryMethod(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.WEBPAGE) || tag.equals(Constants.OBJECT) ||
                tag.equals(Constants.FORM) || tag.equals(Constants.ELEMENT) ||
                tag.equals(Constants.EVENT);
        if (!allowed) {
            return;
        }
        String beanClass = identifyBeanClass(doc, caretOffset);
        if (beanClass.equals(Constants.EMPTY_STRING)) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getMethodList(beanClass, true), ItemType.METHOD);
    }

    private void queryName(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.OBJECT) || tag.equals(Constants.CALL) || tag.equals(Constants.META);
        if (!allowed) {
            return;
        }
        if(tag.equals(Constants.META)) {
            loadItemsFromList(PluginContext.getContext().getMetaNameList(false), ItemType.NAME);
        } else {
            loadItemsFromList(PluginContext.getContext().getNameList(false), ItemType.NAME);
        }
    }

    private void queryOutcome(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.NAVIGATION);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getOutcomeList(false), ItemType.ID);
    }

    private void queryScope(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.WEBPAGE) || tag.equals(Constants.OBJECT) ||
                tag.equals(Constants.FORM) || tag.equals(Constants.ELEMENT) ||
                tag.equals(Constants.EVENT);
        if (!allowed) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getScopeList(false), ItemType.SCOPE);
    }

    private void queryScript(CompletionResultSet rs, Document doc, int caretOffset) {
        List<ListItem> serverVarList = PluginContext.getContext().getServerVarList(false);
        for(ListItem li : serverVarList) {
            synchronized(items) {
                items.add(new CDATACompletionItem(li.getText(), li.getDescription(), Constants.ID_ICON));
            }        
        }
        List<String> jsFiles = identifyJsFiles(doc, caretOffset);
        List<ListItem> jsFunctionList = PluginContext.getContext().getJsFunctionList(jsFiles, true);
        for(ListItem li : jsFunctionList) {
            synchronized(items) {
                items.add(new CDATACompletionItem(li.getText(), li.getDescription(), Constants.METHOD_ICON));
            }
        }
    }

    private void queryTag(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.FORM) || tag.equals(Constants.ELEMENT) ||
                tag.equals(Constants.CALL);
        if (!allowed) {
            return;
        }
        String uri = identifyUri(doc, caretOffset);
        loadItemsFromList(PluginContext.getContext().getHtmlTagList(uri, true), ItemType.TAG);
    }

    private void queryType(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.STYLESHEET) || tag.equals(Constants.SCRIPT) ||
                tag.equals(Constants.EVENT) || tag.equals(Constants.CALL);
        if (!allowed) {
            return;
        }
        if (tag.equals(Constants.STYLESHEET)) {
            loadItemsFromList(PluginContext.getContext().getStylesheetTypeList(true), ItemType.STYLESHEET_TYPE);
        } else if (tag.equals(Constants.SCRIPT)) {
            loadItemsFromList(PluginContext.getContext().getScriptTypeList(true), ItemType.SCRIPT_TYPE);
        } else if (tag.equals(Constants.EVENT)) {
            loadItemsFromList(PluginContext.getContext().getEventTypeList(true), ItemType.EVENT_TYPE);
        } else if (tag.equals(Constants.CALL)) {
            loadItemsFromList(PluginContext.getContext().getCallTypeList(true), ItemType.CALL_TYPE);
        }
    }

    private void queryUpdate(CompletionResultSet rs, Document doc, int caretOffset) {
        List<ListItem> serverVarList = PluginContext.getContext().getServerVarList(false);
        for(ListItem li : serverVarList) {
            synchronized(items) {
                items.add(new CDATACompletionItem(li.getText(), li.getDescription(), Constants.ID_ICON));
            }        
        }
        List<String> cssFiles = identifyCssFiles(doc, caretOffset);
        List<ListItem> cssItemList = PluginContext.getContext().getCssItemList(cssFiles, true);
        for(ListItem li : cssItemList) {
            synchronized(items) {
                items.add(new CDATACompletionItem(li.getText(), li.getDescription(), Constants.ATTRIBUTE_ICON));
            }
        }
    }

    private void queryUri(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        if (!tag.equals(Constants.WEBPAGE)) {
            return;
        }
        loadItemsFromList(PluginContext.getContext().getUriList(true), ItemType.URI);
    }

    private void loadItemsFromList(List<ListItem> list, ItemType type) {
        for (ListItem li : list) {
            VroomConfigCompletionItem item = null;
            if (type == ItemType.SCOPE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.SCOPE_ICON);
            } else if (type == ItemType.URI) {
                item = new UriCompletionItem(li.getText(), li.getDescription(), Constants.URI_ICON);
            } else if (type == ItemType.BEAN_CLASS) {
                item = new BeanClassCompletionItem(li.getText(), li.getDescription(), Constants.CLASS_ICON);
            } else if (type == ItemType.METHOD) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.METHOD_ICON);
            } else if (type == ItemType.STYLESHEET) {
                item = new UrlCompletionItem(li.getText(), li.getDescription(), Constants.STYLESHEET_ICON);
            } else if (type == ItemType.SCRIPT) {
                item = new UrlCompletionItem(li.getText(), li.getDescription(), Constants.SCRIPT_ICON);
            } else if (type == ItemType.STYLESHEET_TYPE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.STYLESHEET_ICON);
            } else if (type == ItemType.SCRIPT_TYPE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.SCRIPT_ICON);
            } else if (type == ItemType.EVENT_TYPE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.EVENT_ICON);
            } else if (type == ItemType.CALL_TYPE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.CALL_ICON);
            } else if (type == ItemType.ID) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.ID_ICON);
            } else if (type == ItemType.TAG) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.TAG_ICON);
            } else if (type == ItemType.NAME) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.NAME_ICON);
            } else if (type == ItemType.VALUE) {
                item = new SimpleCompletionItem(li.getText(), li.getDescription(), Constants.ID_ICON);
            } 
            if (item != null) {
                items.add(item);
            }
        }
    }

    private void queryUrl(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.STYLESHEET) || tag.equals(Constants.SCRIPT) || tag.equals(Constants.NAVIGATION);
        if (!allowed) {
            return;
        }
        if (tag.equals(Constants.STYLESHEET)) {
            loadItemsFromList(PluginContext.getContext().getStylesheetUrlList(true), ItemType.STYLESHEET);
        } else if (tag.equals(Constants.SCRIPT)) {
            loadItemsFromList(PluginContext.getContext().getScriptUrlList(true), ItemType.SCRIPT);
        } else if (tag.equals(Constants.NAVIGATION)) {
            loadItemsFromList(PluginContext.getContext().getUriList(true), ItemType.URI);
        }
    }

    private void queryValue(CompletionResultSet rs, Document doc, int caretOffset) {
        String tag = XMLUtilities.getEnclosingTag(doc, caretOffset);
        boolean allowed = tag.equals(Constants.CALL);
        if (!allowed) {
            return;
        }
        String type = XMLUtilities.findAttributeValue(doc, caretOffset, tag, Constants.TYPE);
        if (type.equals("script")) {
            queryScript(rs, doc, caretOffset);
        } else if (type.equals("update")) {
            queryUpdate(rs, doc, caretOffset);
        }
    }

}
