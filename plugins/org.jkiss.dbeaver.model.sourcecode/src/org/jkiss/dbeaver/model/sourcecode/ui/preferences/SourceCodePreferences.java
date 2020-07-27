package org.jkiss.dbeaver.model.sourcecode.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.bundle.ModelActivator;
import org.jkiss.dbeaver.core.DBeaverActivator;
import org.jkiss.dbeaver.model.impl.preferences.BundlePreferenceStore;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.preferences.PreferenceStoreDelegate;
import org.jkiss.dbeaver.utils.PrefUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.StandardConstants;
//import org.osgi.framework.Bundle;

public final class SourceCodePreferences {
	
	
	public static final String SOURCECODE_CODEOUTPUTFOLDER = "sourcecode.default.codeOutputFolder"; //$NON-NLS-1$
	public static final String SOURCECODE_PACKAGENAME = "sourcecode.default.packagePath"; //$NON-NLS-1$
	public static final String SOURCECODE_PAGECLASSFULLNAME = "sourcecode.default.pageClassFullName"; //$NON-NLS-1$
	public static final String SOURCECODE_ENTITYSUFFIX = "sourcecode.default.entitySuffix"; //$NON-NLS-1$
	public static final String SOURCECODE_GROUPNAME = "sourcecode.default.groupName"; //$NON-NLS-1$
	public static final String SOURCECODE_AUTHOR = "sourcecode.default.author"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_ENTITY = "sourcecode.default.rule.entity"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_ENTITY_LOMBOKDATA = "sourcecode.default.rule.entity.lombokdata"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_DAO = "sourcecode.default.rule.dao"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_MYBATIS = "sourcecode.default.rule.mybatis"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_COMPONENT = "sourcecode.default.rule.component"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_COMPONENT_IMPL = "sourcecode.default.rule.component.impl"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_SERVICE = "sourcecode.default.rule.service"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_SERVICE_IMPL = "sourcecode.default.rule.service.impl"; //$NON-NLS-1$
	public static final String SOURCECODE_RULE_CONTROLLER = "sourcecode.default.rule.controller"; //$NON-NLS-1$
	 
	
	

    public static  PreferenceStoreDelegate getPreferences() {
        	DBPPreferenceStore store = new BundlePreferenceStore(DBeaverActivator.getInstance().getBundle());
        	initializeDefaultPreferences(store);
        	PreferenceStoreDelegate preferences=new PreferenceStoreDelegate(store);
        return preferences;
    }
    
    public static void initializeDefaultPreferences(DBPPreferenceStore store) {

//    	DBeaverActivator.getInstance().getPreferences()
//        DBPPreferenceStore store = new BundlePreferenceStore(DBeaverActivator.getInstance().getBundle());
        final String sysUserFolder = System.getProperty(StandardConstants.ENV_USER_HOME);
        final String sysUserName = System.getProperty(StandardConstants.ENV_USER_NAME);
        if (!CommonUtils.isEmpty(sysUserFolder)) {
        	File outPutDir=new File(sysUserFolder,"code");
           PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER, outPutDir.getPath());
        }
        
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_PACKAGENAME, "org.myproject");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME, "com.eic.manage.common.page.PageDo");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_ENTITYSUFFIX, "Do");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_AUTHOR, sysUserName);
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_GROUPNAME, "test");
        
        // rule setting
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_ENTITY, "${package_name}.model.${group_name}.${table_name}Do");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_ENTITY_LOMBOKDATA, "${package_name}.model.${group_name}.${table_name}DTO");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_DAO, "${package_name}.dao.${group_name}.${table_name}Dao");
//        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_MYBATIS, "{package_name}/mapping/{group_name}/{table_name}Dao");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_COMPONENT, "${package_name}.component.${group_name}.${table_name}Component");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL, "${package_name}.component.${group_name}.impl.${table_name}ComponentImpl");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_SERVICE, "${package_name}.service.${group_name}.${table_name}Service");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL, "${package_name}.service.${group_name}.impl.${table_name}Service");
        PrefUtils.setDefaultPreferenceValue(store, SourceCodePreferences.SOURCECODE_RULE_CONTROLLER, "${package_name}.controller.${group_name}.${table_name}Controller");
        
 
    }
    
    public static void saveDefaultPreferencesValue(String key,String value)
    {
     	DBPPreferenceStore store = new BundlePreferenceStore(DBeaverActivator.getInstance().getBundle());
     	PrefUtils.setDefaultPreferenceValue(store, key, value); 
    }
    
    public static void saveDefaultPreferencesValue(DBPPreferenceStore store,String key,String value)
    {
    		PrefUtils.setDefaultPreferenceValue(store, key, value); 
    }

	   
}
