/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin;

import javax.swing.ImageIcon;
import org.openide.util.Utilities;

/**
 *
 * @author ijazfx
 */
public interface Constants {
    
    enum ItemType {
        URI, BEAN_CLASS, SCOPE, METHOD, NAME, STYLESHEET, STYLESHEET_TYPE, SCRIPT, SCRIPT_TYPE, EVENT, EVENT_TYPE,
        ID, TAG, CALL_TYPE, CALL_TYPE_SCRIPT, CALL_TYPE_UPDATE, VALUE, SCRIPT_VALUE, UPDATE_VALUE, SCRIPT_CDATA, UPDATE_CDATA
    }

    String WEB_INF = "WEB-INF";
    
    String EMPTY_STRING = "";
    String WS = " ";
    String NEW_LINE = "\n";
    String DQUOTE = "\"";
    String EQUAL = "=";
    String SLASH = "/";
    String LT = "<";
    String GT = ">";
    String SLASH_GT = "/>";
    String LT_SLASH = "</";
    String DOT = ".";
    String COLON = ":";
    String COMMA = ",";
    String SEMI_COLON = ";";
    String LPAREN = "(";
    String RPAREN = ")";
    String LBRACE = "{";
    String RBRACE = "}";
    String LBRACKET = "[";
    String RBRACKET = "]";
    String PIPE = "|";
    String PUBLIC = "public";
    String CONTEXT_PATH_EXPR = "#{contextPath}";
    String STYLECLASS_REGEXP = "[^0-9 ]{1}[a-zA-Z0-9_\\-]*((?:\\[[a-zA-Z0-9_\\-'\"]+\\])*)";
//    String URI_REGEXP = "/|(/a-zA-Z0-9-_)*.(jsp|html)";
    
    String[] INVALID_TAGS = {"<%", "<@", "<!", "</"};
    String HTML_ID = "id=";
    String HTML_NAME = "name=";
    String HTML_TYPE = "type";
    
    String VROOM_CONFIG_ICON_BASE = "net/openkoncept/vroom/plugin/res/vroom-config.png";
    ImageIcon VROOM_CONFIG_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/vroom-config.png"));
    ImageIcon URI_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/html.png"));    
    ImageIcon PACKAGE_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/package.gif"));
    ImageIcon CLASS_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/class_16.png"));
    ImageIcon METHOD_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/method_16.png"));
    ImageIcon SCOPE_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/scope.png"));
    ImageIcon TAG_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/html-tag.png"));
    ImageIcon ID_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/attribute.png"));
    ImageIcon NAME_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/class_16.png"));
    ImageIcon ATTRIBUTE_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/attribute.png"));
    ImageIcon STYLESHEET_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/css.png"));
    ImageIcon SCRIPT_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/script.png"));
    ImageIcon EVENT_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/event.png"));    
    ImageIcon CALL_ICON = new ImageIcon(Utilities.loadImage("net/openkoncept/vroom/plugin/res/attribute.png"));    
    
    String WEBPAGE = "webpage";
    String META = "meta";
    String STYLESHEET = "stylesheet";
    String SCRIPT = "script";
    String OBJECT = "object";
    String FORM = "form";
    String ELEMENT = "element";
    String EVENT = "event";
    String CALL = "call";
    String NAVIGATION = "navigation";
    String CDATA = "<![CDATA[";
    String CDATA_CLOSE = "]]>";
    
    String[] TAG_LIST = {
        WEBPAGE, META, STYLESHEET, SCRIPT, OBJECT, FORM, ELEMENT, EVENT, CALL, NAVIGATION
    };
    
    String URI = "uri";
    String BEAN_CLASS = "bean-class";
    String VAR = "var";
    String SCOPE = "scope";
    String METHOD = "method";
    String ID = "id";
    String TAG = "tag";
    String NAME = "name";
    String ATTRIBUTE = "attribute";
    String OUTCOME = "outcome";
    String FORWARD = "forward";
    String URL = "url";
    String TYPE = "type";
    String VALUE = "value";
    String HTTP_EQUIV = "http-equiv";
    
    String[] ATTR_LIST = {
        URI, BEAN_CLASS, VAR, SCOPE, METHOD, ID, TAG, NAME, ATTRIBUTE, OUTCOME,
        URL, TYPE, VALUE
    };
    
    String HTM = "htm";
    String HTML = "html";
    String XHTML = "xhtml";
    String JSP = "jsp";
    String JSPX = "jspx";
    String JAVA = "java";
    
    String[] URI_EXT_LIST = {
        HTM, HTML, XHTML, JSP, JSPX
    };

    String CSS = "css";    
    
    String[] STYLESHEET_EXT_LIST = {
        CSS
    };

    String JS = "js";    
    
    String[] SCRIPT_EXT_LIST = {
        JS
    };
    
    String FUNCTION = "function";
    
    String HTTP = "http://";
    String HTTPS = "https://";
    
    String[] WORD_TERMINATORS = {
        DOT, WS, DQUOTE, COLON, SEMI_COLON, NEW_LINE, LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET, RBRACKET
    };
    
    String[] STYLE_TERMINATORS = {
        COMMA, WS, LBRACE, SEMI_COLON, NEW_LINE
    };
    
    String VROOM_CONFIG_XML = "vroom-config.xml";
    String VROOM_CONFIG_XSD = "vroom-config-2.1.xsd";
    String VROOM_JS = "vroom.js";
    
    String VROOM_CONFIG_XML_URL = "net/openkoncept/vroom/plugin/framework/res/vroom-config.xml";
    String VROOM_CONFIG_XSD_URL = "net/openkoncept/vroom/plugin/framework/res/vroom-config-2.1.xsd";
    String VROOM_JS_URL = "net/openkoncept/vroom/plugin/framework/res/vroom.js";
    
}
