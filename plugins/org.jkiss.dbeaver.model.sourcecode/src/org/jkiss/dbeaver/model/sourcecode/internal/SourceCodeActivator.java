package org.jkiss.dbeaver.model.sourcecode.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jkiss.dbeaver.model.impl.preferences.BundlePreferenceStore;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.ui.editors.sql.internal.SQLEditorActivator;
import org.osgi.framework.BundleContext;

public class SourceCodeActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.jkiss.dbeaver.model.sourcecode";

    // The shared instance
    private static SourceCodeActivator plugin;
    private DBPPreferenceStore preferences;

    public SourceCodeActivator() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        preferences = new BundlePreferenceStore(getBundle());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static SourceCodeActivator getDefault() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public DBPPreferenceStore getPreferences() {
        return preferences;
    }
}
