package org.jkiss.dbeaver.model.sourcecode.ui.preferences;

//import java.io.File;
//import java.util.Arrays;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
//import org.eclipse.jface.preference.IPreferenceStore;
//import org.jkiss.dbeaver.DBeaverPreferences;
import org.jkiss.dbeaver.core.DBeaverActivator;
//import org.jkiss.dbeaver.model.exec.DBCExecutionPurpose;
import org.jkiss.dbeaver.model.impl.preferences.BundlePreferenceStore;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
//import org.jkiss.dbeaver.model.qm.QMConstants;
//import org.jkiss.dbeaver.model.qm.QMObjectType;
//import org.jkiss.dbeaver.utils.GeneralUtils;
//import org.jkiss.dbeaver.utils.PrefUtils;
//import org.jkiss.dbeaver.utils.RuntimeUtils;
//import org.jkiss.dbeaver.utils.SystemVariablesResolver;
//import org.jkiss.utils.CommonUtils;
//import org.jkiss.utils.StandardConstants;

public class SourceCodePreferencesInitializer extends AbstractPreferenceInitializer {

    public SourceCodePreferencesInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {

//    	DBeaverActivator.getInstance().getPreferences()
        DBPPreferenceStore store = new BundlePreferenceStore(DBeaverActivator.getInstance().getBundle());
        SourceCodePreferences.initializeDefaultPreferences(store);
//        final String sysUserFolder = System.getProperty(StandardConstants.ENV_USER_HOME);
//        if (!CommonUtils.isEmpty(sysUserFolder)) {
//        	File outPutDir=new File(sysUserFolder,"code");
//           PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER, outPutDir.getPath());
//        }
//        
//        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_PACKAGENAME, "org.myproject");
//        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME, "com.eic.manage.common.page.PageDo");
//        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_ENTITYSUFFIX, "Do");
 
    }

}
