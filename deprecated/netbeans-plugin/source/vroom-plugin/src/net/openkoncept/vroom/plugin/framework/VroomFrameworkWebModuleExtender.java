/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import net.openkoncept.vroom.plugin.Constants;
import net.openkoncept.vroom.plugin.PluginContext;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.dd.api.common.InitParam;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.Filter;
import org.netbeans.modules.j2ee.dd.api.web.FilterMapping;
import org.netbeans.modules.j2ee.dd.api.web.Servlet;
import org.netbeans.modules.j2ee.dd.api.web.ServletMapping;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author fijaz
 */
public class VroomFrameworkWebModuleExtender extends WebModuleExtender {

    VroomFrameworkSettingPanel pnlSettings = new VroomFrameworkSettingPanel();
    
    WebModule webModule;
    
    public VroomFrameworkWebModuleExtender(WebModule webModule) {
        this.webModule = webModule;
    }
    
    @Override
    public void addChangeListener(ChangeListener arg0) {
        
    }

    @Override
    public void removeChangeListener(ChangeListener arg0) {
        
    }

    @Override
    public JComponent getComponent() {
        return pnlSettings;
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public void update() {
        try {
            if(webModule == null) return;
            String configFile = null;
            String mappings = null;
            String uploadSize = null;
            String tempFolder = null;            
            WebApp webapp = DDProvider.getDefault().getDDRoot(webModule.getDeploymentDescriptor());
            Filter filter = (Filter) webapp.findBeanByName("Filter", "FilterName", "VroomFilter");
            if(filter != null) {
                InitParam[] params = filter.getInitParam();
                for(InitParam param : params) {
                    if(param.getParamName().equals("config-file")) {
                        configFile = param.getParamValue();
                        break;
                    }
                }                
            }
            FilterMapping[] fms = webapp.getFilterMapping();
            int i = 0;
            for(FilterMapping fm : fms) {
                if(fm.getFilterName().equals("VroomFilter")) {
                    if(i == 0) {
                        mappings = fm.getUrlPattern();
                    } else {
                        mappings += ", " + fm.getUrlPattern();
                    }                    
                    i++;
                }
            }            
            Servlet servlet = (Servlet) webapp.findBeanByName("Servlet", "ServletName", "VroomController");
            if(servlet != null) {
                InitParam[] params = servlet.getInitParam();
                for(InitParam param : params) {
                    if(param.getParamName().equals("upload-file-size-threshold")) {
                        uploadSize = param.getParamValue();
                    } else if(param.getParamName().equals("upload-file-temp-folder")) {
                        tempFolder = param.getParamValue();
                    }
                }
            }
            if(mappings == null) {
                mappings = "*.jsp, *.html, *.do, /do/*, *.jsf, /faces/*";
            }
            if(configFile == null) {
                    configFile = "/WEB-INF/vroom-config.xml";
            }            
            if(uploadSize == null) {
                uploadSize = "10240000";
            }
            if(tempFolder == null) {
                tempFolder = "/WEB-INF/temp";
            }            
            pnlSettings.getTxtVroomConfigFileName().setText(configFile);
            pnlSettings.getTxtVroomFilterMapping().setText(mappings);
            pnlSettings.getTxtFileUploadMaxSize().setText(uploadSize);
            pnlSettings.getTxtFileUploadTempLocation().setText(tempFolder);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }        
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Set<FileObject> extend(WebModule webModule) {
        Project project = PluginContext.findProject(webModule.getDeploymentDescriptor());
        String libName = (String) pnlSettings.getCbxVroomLibrary().getSelectedItem();
        PluginContext.addLibrary(project, libName);
        String filterName = "VroomFilter";
        String mapping = pnlSettings.getTxtVroomFilterMapping().getText();
        String[] mappings = mapping.split(Constants.COMMA);
        String controllerName = "VroomController";
        String configFile = pnlSettings.getTxtVroomConfigFileName().getText();
        if(!configFile.startsWith("/WEB-INF/")) {
            configFile = "/WEB-INF/" + configFile;
            pnlSettings.getTxtVroomConfigFileName().setText(configFile);
        }
        String uploadSize = pnlSettings.getTxtFileUploadMaxSize().getText();
        String tempFolder = pnlSettings.getTxtFileUploadTempLocation().getText();
        try {
            FileObject webXml = webModule.getDeploymentDescriptor();
            WebApp webapp = DDProvider.getDefault().getDDRoot(webXml);
            Filter filter = (Filter) webapp.findBeanByName("Filter", "FilterName", filterName);
            if(filter == null) {
                filter = (Filter) webapp.createBean("Filter");
                webapp.addFilter(filter);
            }
            filter.setFilterName(filterName);
            filter.setFilterClass("net.openkoncept.vroom.VroomFilter");
            InitParam[] fparams = filter.getInitParam();
            for(InitParam fparam : fparams) {
                filter.removeInitParam(fparam);
            }
            InitParam fparam = (InitParam) filter.createBean("InitParam");
            fparam.setParamName("config-file");
            fparam.setParamValue(configFile);
            filter.addInitParam(fparam);
            FilterMapping[] fms = webapp.getFilterMapping();
            for(FilterMapping fm : fms) {
                if(fm.getFilterName().equals("VroomFilter")) {
                    webapp.removeFilterMapping(fm);
                }
            }
            for(String m : mappings) {
                FilterMapping fm = (FilterMapping) webapp.createBean("FilterMapping");
                fm.setFilterName(filterName);
                fm.setUrlPattern(m.trim());
                webapp.addFilterMapping(fm);
            }
            Servlet servlet = (Servlet) webapp.findBeanByName("Servlet", "ServletName", controllerName);
            if(servlet == null) {
                servlet = (Servlet) webapp.createBean("Servlet");
                webapp.addServlet(servlet);
            }
            servlet.setServletName(controllerName);
            servlet.setServletClass("net.openkoncept.vroom.VroomController");
            InitParam[] sparams = servlet.getInitParam();
            for(InitParam sparam : sparams) {
                servlet.removeInitParam(sparam);
            }
            InitParam sparam = (InitParam) servlet.createBean("InitParam");
            sparam.setParamName("upload-file-size-threshold");
            sparam.setParamValue(uploadSize);
            servlet.addInitParam(sparam);
            sparam = (InitParam) servlet.createBean("InitParam");
            sparam.setParamName("upload-file-temp-folder");
            sparam.setParamValue(tempFolder);
            servlet.addInitParam(sparam);
            ServletMapping[] sms = webapp.getServletMapping();
            for(ServletMapping sm : sms) {
                if(sm.getServletName().equals("VroomController")) {
                    webapp.removeServletMapping(sm);
                }
            }
            ServletMapping sm = (ServletMapping) webapp.createBean("ServletMapping");
            sm.setServletName("VroomController");
            sm.setUrlPattern("/vroom");
            webapp.addServletMapping(sm);
            webapp.write(webXml);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }        
        FileObject webInf = webModule.getWebInf();
        FileObject xsd = null;
        FileObject xml = null;
        FileObject js = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try { // copy xsd file...            
            URL url = cl.getResource(Constants.VROOM_CONFIG_XSD_URL);
            xsd = webInf.createData(Constants.VROOM_CONFIG_XSD);
            InputStream ins = url.openStream();
            OutputStream outs = xsd.getOutputStream();            
            FileUtil.copy(ins, outs);
            ins.close();
            outs.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        try { // copy xml file...
            URL url = cl.getResource(Constants.VROOM_CONFIG_XML_URL);
            xml = webInf.createData(Constants.VROOM_CONFIG_XML);
            InputStream ins = url.openStream();
            OutputStream outs = xml.getOutputStream();            
            FileUtil.copy(ins, outs);
            ins.close();
            outs.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        try { // copy js file...
            URL url = cl.getResource(Constants.VROOM_JS_URL);
            js = webInf.createData(Constants.VROOM_JS);
            InputStream ins = url.openStream();
            OutputStream outs = js.getOutputStream();            
            FileUtil.copy(ins, outs);
            ins.close();
            outs.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        Set<FileObject> fileSet = new HashSet<FileObject>();
        fileSet.add(xml);
        return fileSet;
    }

}
