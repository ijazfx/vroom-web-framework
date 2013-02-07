/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.util;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.openkoncept.vroom.plugin.Constants;
import org.openide.util.Exceptions;

/**
 *
 * @author ijazfx
 */
public class XMLUtilities {

    public static String findAttributeValue(Document doc, int caretOffset, String tag, String attrName) {
        String value = Constants.EMPTY_STRING;
        try {
            String text = doc.getText(0, caretOffset);
            int index = text.lastIndexOf(tag);
            if (index == -1) {
                return value;
            }
            text = text.substring(index);
            index = text.indexOf(attrName + Constants.EQUAL);
            if (index == -1) {
                return value;
            }
            int offset = index + attrName.length() + 2;
            String[] pair = TextUtilities.getAttrPairAtOffset(text, offset);
            if (pair.length > 1) {
                value = pair[1];
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return value;
    }

    public static String findAttributeValueInTag(String text, int offset, String tag, String attrName) {
        int start = XMLUtilities.indexOfTag(text, offset, tag);
        String value = Constants.EMPTY_STRING;
        if(start == -1) {
            return value;
        }
        int end = text.indexOf(Constants.GT, start);
        if(end == -1) {
            return value;
        }
        String word = text.substring(start, end);
        start = word.indexOf(attrName);
        if(start == -1) {
            return value;
        }
        String[] pair = TextUtilities.getAttrPairAtOffset(word, start);
        if(pair.length == 2) {
            value = pair[1];
        }
        return value;
    }
    
    public static String findAttributeValue(String text, int caretOffset, String tag, String attrName) {
        String value = Constants.EMPTY_STRING;
        int index = text.lastIndexOf(tag);
        if (index == -1) {
            return value;
        }
        text = text.substring(index);
        index = text.indexOf(attrName + Constants.EQUAL);
        if (index == -1) {
            return value;
        }
        int offset = index + attrName.length() + 2;
        String[] pair = TextUtilities.getAttrPairAtOffset(text, offset);
        if (pair.length > 1) {
            value = pair[1];
        }
        return value;
    }

    public static int indexOfEnclosingTag(Document doc, int offset) {
        int index = -1;
        try {
            String text = doc.getText(0, doc.getLength() - 1);
            index = indexOfEnclosingTag(text, offset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return index;
    }

    public static String getEnclosingTag(Document doc, int offset) {
        String tag = Constants.EMPTY_STRING;
        try {
            String text = doc.getText(0, doc.getLength() - 1);
            tag = getEnclosingTag(text, offset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return tag;
    }

    /**
     * This method returns the enclosing tag with in the text around the offset.
     * @param text
     * @param offset
     * @return Tag name or empty string
     */
    public static String getEnclosingTag(String text, int offset) {
        String tag = Constants.EMPTY_STRING;
        int start = offset;
        boolean found = false;
        while (start >= 0) {
            tag = text.substring(start);
            if (tag.startsWith(Constants.LT) && tag.length() > 1 && (tag.charAt(1) != Constants.WS.charAt(0) || tag.charAt(1) != Constants.NEW_LINE.charAt(0))) {
                start++;
                tag = text.substring(start);
                found = true;
                break;
            }
            start--;
        }
        tag = text.substring(start);
        if (found) {
            int end = tag.indexOf(Constants.WS);
            if (end == -1) {
                end = tag.indexOf(Constants.NEW_LINE);
            }
            if (end != -1) {
                tag = tag.substring(0, end);
            }
            end = tag.indexOf(Constants.SLASH_GT);
            if (end == -1) {
                end = tag.indexOf(Constants.GT);
            }
            if (end != -1) {
                tag = tag.substring(0, end);
            }
        }
        return tag;
    }

    public static int indexOfTag(String text, int offset, String tag) {
        int index = -1;
        int start = offset;
        while(start >= 0) {
            if(text.startsWith(Constants.LT + tag, start)) {
                index = start;
                break;
            }
            start--;
        }
        return index;
    }
    
    public static String getTagInnerData(String text, int offset, String tag) {
        String data = Constants.EMPTY_STRING;
        int start = indexOfTag(text, offset, tag);
        if(start != -1) {
            start = text.indexOf(Constants.GT, start);
            if(start != -1) {
                start++;
                if(text.charAt(start-1) == Constants.SLASH.charAt(0)) {
                    return data;
                }
                int end = text.indexOf(Constants.LT_SLASH + tag + Constants.GT, start);
                if(end == -1) {
                    return data;
                }
                data = text.substring(start, end);
            }
        }
        return data;
    }
    
    public static String getParentTag(Document doc, int offset, int level) {
        String word = TextUtilities.getWordAtOffset(doc, offset);
        return getParentTag(word, offset, level);
    }

    public static String getParentTag(String text, int offset, int level) {
        String tag = Constants.EMPTY_STRING;
        int pos = offset;
        for (int i = 0; i < level; i++) {
            int index = XMLUtilities.indexOfEnclosingTag(text, pos);
            if (index == -1) {
                break;
            }
            pos = index - 2;
            tag = XMLUtilities.getEnclosingTag(text, pos);
            
        }
        return tag;
    }

    public static int indexOfEnclosingTag(String text, int offset) {
        int index = -1;
        String tag = Constants.EMPTY_STRING;
        int start = offset;
        boolean found = false;
        while (start >= 0) {
            tag = text.substring(start);
            if (tag.startsWith(Constants.LT) && tag.length() > 1 && (tag.charAt(1) != Constants.WS.charAt(0) || tag.charAt(1) != Constants.NEW_LINE.charAt(0))) {
                start++;
                tag = text.substring(start);
                found = true;
                break;
            }
            start--;
        }
        tag = text.substring(start);
        if (found) {
            index = start;
            int end = tag.indexOf(Constants.WS);
            if (end == -1) {
                end = tag.indexOf(Constants.NEW_LINE);
            }
            if (end != -1) {
                tag = tag.substring(0, end);
            }
            end = tag.indexOf(Constants.SLASH_GT);
            if (end == -1) {
                end = tag.indexOf(Constants.GT);
            }
            if (end != -1) {
                tag = tag.substring(0, end);
            }
        }
        return index;
    }
}
