/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.openkoncept.vroom.plugin.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.openkoncept.vroom.plugin.Constants;
import org.netbeans.modules.web.api.webmodule.ExtenderController;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebFrameworkProvider;
import org.netbeans.modules.web.spi.webmodule.WebModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author fijaz
 */
public class VroomFrameworkProvider extends WebFrameworkProvider {

    public VroomFrameworkProvider() {
        this("Vroom Framework", "An Open Source Web Application Development Framework");
    }
    
    public VroomFrameworkProvider(String name, String description) {
        super(name, description);
    }

    @Override
    public WebModuleExtender createWebModuleExtender(WebModule webModule, ExtenderController ec) {
        return new VroomFrameworkWebModuleExtender(webModule);
    }
    
    @Override
    public boolean isInWebModule(WebModule webModule) {
        File[] files = getConfigurationFiles(webModule);
        if(files != null) {
            return true;
        }
        return false;
    }

    @Override
    public File[] getConfigurationFiles(WebModule webModule) {
        FileObject webInf = webModule.getWebInf();
        List<FileObject> fobs = new ArrayList<FileObject>();
        FileObject vroomConfigXml = webInf.getFileObject(Constants.VROOM_CONFIG_XML);
        if(vroomConfigXml != null) {
            fobs.add(vroomConfigXml);
        }
        if(fobs.size() > 0) {
            File[] files = new File[fobs.size()];
            int i = 0;
            for(FileObject fob : fobs) {
                files[i++] = FileUtil.toFile(fob);
            }
            return files;
        }
        return null;
    }

}
