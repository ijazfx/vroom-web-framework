/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin.util;

import javax.swing.text.BadLocationException;
import net.openkoncept.vroom.plugin.Constants;
import java.util.StringTokenizer;
import javax.swing.text.Document;
import org.openide.util.Exceptions;

/**
 *
 * @author ijazfx
 */
public class TextUtilities {

    public static String getWordAtOffset(Document doc, int offset) {
        String word = Constants.EMPTY_STRING;
        try {
            String text = doc.getText(0, doc.getLength());
            word = getWordAtOffset(text, offset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return word;
    }

    public static String getWordAtOffset(Document doc, int offset, String[] terminators) {
        String word = Constants.EMPTY_STRING;
        try {
            String text = doc.getText(0, doc.getLength());
            word = getWordAtOffset(text, offset, terminators);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return word;
    }

    /**
     * This method returns the word of at the at the offset of the text.
     * @param text
     * @param offset
     * @return
     */
    public static String getWordAtOffset(String text, int offset) {
        String word = Constants.EMPTY_STRING;
        word = text.substring(offset, offset + 1);
        if (word.equals(Constants.WS) || word.equals(Constants.NEW_LINE)) {
            return word;
        }
        int start = offset;
        String symbol = Constants.EMPTY_STRING;
        while (start > 0) {
            word = text.substring(start);
            if (word.startsWith(Constants.WS) || word.startsWith(Constants.NEW_LINE)) {
                symbol = word.substring(0, 1);
                start++;
                break;
            }
            start--;
        }
        int end = text.indexOf(Constants.WS, start);
        if (end == -1) {
            word = text.substring(start);
        } else {
            word = text.substring(start, end);
        }
        end = word.indexOf(Constants.NEW_LINE);
        if (end != -1) {
            word = word.substring(0, end);
        }
        return word;
    }

    /**
     * This method returns the word of at the at the offset of the text.
     * @param text
     * @param offset
     * @return
     */
    public static String getWordAtOffset(String text, int offset, String[] terminators) {
        String word = Constants.EMPTY_STRING;
        int start = offset;        
        outer:
        while (start >= 0) {
            word = text.substring(start, start + 1);
            for (String t : terminators) {
                if (word.equals(t)) {
                    start++;
                    word = text.substring(start);
                    break outer;
                }
            }
            start--;
        }
        int end = -1;
        for (String t : terminators) {
            end = word.indexOf(t);
            if (end != -1) {
                word = word.substring(0, end);
                break;
            }
        }
        return word;
    }

    public static String[] getAttrPairAtOffset(Document doc, int offset) {
        String[] pair = {Constants.EMPTY_STRING, Constants.EMPTY_STRING};
        try {
            String text = doc.getText(0, doc.getLength());
            pair = getAttrPairAtOffset(text, offset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return pair;
    }

    /**
     * This method returns a pair of attribute and value if the offset of the
     * text contains any. Otherwise the pair contains empty strings.
     * @param text
     * @param offset
     * @return label/value pair, the pair can be empty.
     */
    public static String[] getAttrPairAtOffset(String text, int offset) {
        String word = getWordAtOffset(text, offset);
        String[] pair = {Constants.EMPTY_STRING, Constants.EMPTY_STRING};
        if (word.contains("=")) {
            StringTokenizer tokenizer = new StringTokenizer(word, Constants.EQUAL, true);
            pair[0] = tokenizer.nextToken();
            if (!tokenizer.nextToken().equals(Constants.EQUAL)) {
                pair[0] = Constants.EMPTY_STRING;
            } else {
                if (!tokenizer.hasMoreTokens()) {
                    return pair;
                }
                String value = tokenizer.nextToken();
                if (value.startsWith(Constants.DQUOTE)) {
                    value = value.substring(1);
                    int index = value.lastIndexOf(Constants.DQUOTE);
                    if (index != -1) {
                        value = value.substring(0, index);
                    }
                } else {
                    int index = value.lastIndexOf(Constants.SLASH_GT);
                    if (index == -1) {
                        index = value.lastIndexOf(Constants.GT);
                    }
                    if (index != -1) {
                        value = value.substring(0, index);
                    }
                }
                pair[1] = value;
            }
        }
        return pair;
    }

    public static boolean isOffsetOnAttributeValue(Document doc, int offset) {
        try {
            String text = doc.getText(0, doc.getLength());
            return isOffsetOnAttributeValue(text, offset);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    public static boolean isOffsetOnAttributeValue(String text, int offset) {
        String[] pair = getAttrPairAtOffset(text, offset);
        if (pair[0].equals(Constants.EMPTY_STRING)) {
            return false;
        }
        String word = getWordAtOffset(text, offset);
        int index = text.indexOf(word);
        int start = index + word.indexOf(pair[1]);
        int end = (start) + pair[1].length();
        if (offset >= start && offset <= end) {
            return true;
        }
        return false;
    }

    public static void replaceTextBetweenSymbols(Document doc, int offset, String replaceWith, String leftSymbol, String rightSymbol) {
        try {
            int start = offset - 1;
            int lpos = -1;
            int rpos = -1;
            String text = doc.getText(0, doc.getLength());
            while (start >= 0) {
                if (text.substring(start).startsWith(leftSymbol)) {
                    lpos = start + leftSymbol.length();
                    rpos = text.indexOf(rightSymbol, offset);
                    if (rpos != -1) {
                        int length = rpos - lpos;
                        doc.remove(lpos, length);
                    }
                    doc.insertString(lpos, replaceWith, null);
                    break;
                }
                start--;
            }

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public static void replaceTextBetweenSymbols(Document doc, int offset, String replaceWith, String[] symbols) {
        try {
            int start = offset - 1;
            int lpos = -1;
            int rpos = -1;
            String text = doc.getText(0, doc.getLength());
            outer:
            while (start >= 0) {
                String character = text.substring(start, start+1);
                for(String symbol : symbols) {
                    if(character.equals(symbol)) {
                        start++;
                        break outer;
                    }
                }
                start--;
            }
            int end = -1;
            for(String symbol : symbols) {
                end = text.indexOf(symbol, start);
                if(end != -1) {
                    break;
                }
            }
            doc.remove(start, end-start);
            doc.insertString(start, replaceWith, null);            
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
