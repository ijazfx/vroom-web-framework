//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.08 at 08:58:06 PM AST 
//
package net.openkoncept.vroom.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.openkoncept.net/schema/vroom-config/3.0}webpage" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "webpage"
})
@XmlRootElement(name = "vroom-config")
public class VroomConfig {

    protected List<Webpage> webpage;

    /**
     * Gets the value of the webpage property.
     *
     * <p> This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the webpage property.
     *
     * <p> For example, to add a new item, do as follows:
     * <pre>
     *    getWebpage().add(newItem);
     * </pre>
     *
     *
     * <p> Objects of the following type(s) are allowed in the list
     * {@link Webpage }
     *
     *
     */
    public List<Webpage> getWebpage() {
        if (webpage == null) {
            webpage = new ArrayList<Webpage>();
        }
        return this.webpage;
    }
    
    private static final Logger logger = Logger.getLogger(VroomConfig.class.getName());
    private static VroomConfig _instance;
    private static String _configFile = null;
    private static long _lastModified = 0L;
    
    @XmlTransient
    private Map<String, Boolean> uriInDefinition = Collections.synchronizedMap(new HashMap<String, Boolean>());

    public static VroomConfig initialize(String configFile) {
        _configFile = configFile;
        try {
            _reload();
        } catch (JAXBException ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, null, ex);
        }
        return _instance;
    }

    private static void _reload() throws JAXBException {
        File _file = new File(_configFile);
        if (_lastModified != _file.lastModified()) {
            _lastModified = _file.lastModified();
            JAXBContext ctx = JAXBContext.newInstance("net.openkoncept.vroom.config");
            Unmarshaller um = ctx.createUnmarshaller();
            _instance = (VroomConfig) um.unmarshal(new File(_configFile));
        }
    }

    public void reload() {
        if (_configFile != null) {
            try {
                VroomConfig._reload();
            } catch (JAXBException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static VroomConfig getInstance() {
        return _instance;
    }

    public boolean isUriInDefinition(String uri) {
        if (!uriInDefinition.containsKey(uri)) {
            for (Webpage wp : getWebpage()) {
                if (uri.equals(wp.getUri()) || uri.matches(wp.getUri())) {
                    uriInDefinition.put(uri, Boolean.TRUE);
                    break;
                }
            }
        }
        return uriInDefinition.containsKey(uri);
    }

    public List getElements(String uri, String id, String method, String beanClass, String var, String scope) {
        for(Webpage wp : getWebpage()) {
            String wpBeanClass = wp.getBeanClass();
            String wpVar = wp.getVar();
            ScopeType wpScope = wp.getScope();
            if (uri.matches(wp.getUri())) {
                for(Form f : wp.getForm()) {
                    String fBeanClass = (f.getBeanClass() != null) ? f.getBeanClass() : wpBeanClass;
                    String fVar = (f.getVar() != null) ? f.getVar() : wpVar;
                    String fScope = (f.getBeanClass() != null) ? f.getScope().value() : wpScope.value();
                    if (f.getId().equals(id) && method.equals(f.getMethod()) && beanClass.equals(fBeanClass) && var.equals(fVar) && scope.equals(fScope)) {
                        return f.getElement();
                    }
                }
            }
        }
        return new ArrayList<Element>();
    }
    
    public Navigation getNavigation(String uri, String id, String method, String beanClass, String var, String scope, String outcome) {
        for(Webpage wp : getWebpage()) {
            String wpBeanClass = wp.getBeanClass();
            String wpVar = wp.getVar();
            ScopeType wpScope = wp.getScope();
            if (uri.matches(wp.getUri())) {
                for(Form f : wp.getForm()) {
                    String fBeanClass = (f.getBeanClass() != null) ? f.getBeanClass() : wpBeanClass;
                    String fVar = (f.getVar() != null) ? f.getVar() : wpVar;
                    String fScope = (f.getBeanClass() != null) ? f.getScope().value() : wpScope.value();
                    if (f.getId().equals(id) && method.equals(f.getMethod()) && beanClass.equals(fBeanClass) && var.equals(fVar) && scope.equals(fScope)) {
                        for(Navigation n : f.getNavigation()) {
                            if (n.getOutcome().equals(outcome)) {
                                return n;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
}