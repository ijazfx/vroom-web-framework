/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.openkoncept.vroom.plugin;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class VroomConfigDataLoader extends UniFileLoader {

    public static final String REQUIRED_MIME = "text/vroom-config+xml";
    private static final long serialVersionUID = 1L;

    public VroomConfigDataLoader() {
        super("net.openkoncept.vroom.plugin.VroomConfigDataObject");
    }

    @Override
    protected String defaultDisplayName() {
        return NbBundle.getMessage(VroomConfigDataLoader.class, "LBL_VroomConfig_loader_name");
    }

    @Override
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }

    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new VroomConfigDataObject(primaryFile, this);
    }

    @Override
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions";
    }
}
