package org.jkiss.dbeaver.model.sourcecode.ui.preferences;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.DBeaverPreferences;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.model.app.DBPPlatformLanguage;
import org.jkiss.dbeaver.model.app.DBPPlatformLanguageManager;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.registry.language.PlatformLanguageDescriptor;
import org.jkiss.dbeaver.registry.language.PlatformLanguageRegistry;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.ui.editors.sql.preferences.PrefPageSQLEditor;
import org.jkiss.dbeaver.ui.preferences.AbstractPrefPage;
import org.jkiss.dbeaver.ui.preferences.PrefPageEntityEditor;
import org.jkiss.dbeaver.ui.preferences.PreferenceStoreDelegate;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.dbeaver.utils.PrefUtils;
import org.jkiss.dbeaver.utils.RuntimeUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.StandardConstants;

public class PrefPageSourceCodeGeneral extends AbstractPrefPage implements IWorkbenchPreferencePage, IWorkbenchPropertyPage
{
    public static final String PAGE_ID = "org.jkiss.dbeaver.preferences.main.common"; //$NON-NLS-1$

    private Text sourceCodeOutPutDir;
    private Text sourceCodePackagePath;
    private Text sourceCodePageClassFullName;
    private Text sourceCodeEntitySuffix;
    private Text sourceCodeAuthor;
    private Text sourceCode_gourp_name;
    private Text sourceCode_rule_entity;
    private Text sourceCode_rule_entity_lombokdata;
    private Text sourceCode_rule_dao;
    private Text sourceCode_rule_component;
    private Text sourceCode_rule_component_impl;
    private Text sourceCode_rule_service;
    private Text sourceCode_rule_service_impl;
//    private Text sourceCode_rule_mybatis;
    private Text sourceCode_rule_controller;
    
    private Combo workspaceLanguage;

    private Button longOperationsCheck;
    private Spinner longOperationsTimeout;

    private Button notificationsEnabled;
    private Spinner notificationsCloseDelay;

    public PrefPageSourceCodeGeneral()
    {
        super();
//        setPreferenceStore(new PreferenceStoreDelegate(DBWorkbench.getPlatform().getPreferenceStore()));
        setPreferenceStore(SourceCodePreferences.getPreferences());
    }

    @Override
    public void init(IWorkbench workbench)
    {

    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = UIUtils.createPlaceholder(parent, 1, 5);

        {
            Group groupObjects = UIUtils.createControlGroup(composite, org.jkiss.dbeaver.model.sourcecode.internal.UIMessages.dbeaver_generate_sourcecode_preferences_generalSetting, 2, GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING, 0);
            
            
            sourceCodeOutPutDir=DialogUtils.createOutputFolderChooser(groupObjects, UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder, null,null);
            sourceCodePackagePath= UIUtils.createLabelText(groupObjects, UIMessages.dbeaver_generate_sourcecode_packageName, null,2);
            sourceCodePageClassFullName= UIUtils.createLabelText(groupObjects, UIMessages.dbeaver_generate_sourcecode_pageClassFullName,null,2);
//            sourceCodeEntitySuffix= UIUtils.createLabelText(groupObjects, "Entity suffix",null,2);
            sourceCodeAuthor= UIUtils.createLabelText(groupObjects, UIMessages.dbeaver_generate_sourcecode_preferences_author,null,2);
            sourceCode_gourp_name= UIUtils.createLabelText(groupObjects, UIMessages.dbeaver_generate_sourcecode_groupName,null,2);
            //automaticUpdateCheck.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
        }
        {
            Group fileRuleGroup = UIUtils.createControlGroup(composite, UIMessages.dbeaver_generate_sourcecode_preferences_generalFileRule, 2, GridData.VERTICAL_ALIGN_BEGINNING, 0);

            workspaceLanguage = UIUtils.createLabelCombo(fileRuleGroup, UIMessages.dbeaver_generate_sourcecode_preferences_templateParamName, CoreMessages.pref_page_ui_general_combo_language_tip, SWT.READ_ONLY | SWT.DROP_DOWN);
            workspaceLanguage.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
            workspaceLanguage.add("{group_name}  组名称");
            workspaceLanguage.add("{package_name} 包名");
            workspaceLanguage.add("{table_name} 表名");
            workspaceLanguage.add("{database_name} 库名");
             

       

//            Label tipLabel = UIUtils.createLabel(fileRuleGroup, "保存文件规则");
//            tipLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_BEGINNING, false, false , 2, 1));
//            
            sourceCode_rule_entity= UIUtils.createLabelText(fileRuleGroup, "Entity:",null,2);
            sourceCode_rule_entity_lombokdata= UIUtils.createLabelText(fileRuleGroup, "Lombok Data:",null,2);
            sourceCode_rule_dao= UIUtils.createLabelText(fileRuleGroup, "Dao:",null,2);
//            sourceCode_rule_mybatis= UIUtils.createLabelText(view_group_rule_setting, "Mybatis:",null,2);
            sourceCode_rule_component= UIUtils.createLabelText(fileRuleGroup, "Component:",null,2);
            sourceCode_rule_component_impl= UIUtils.createLabelText(fileRuleGroup, "Component Impl:",null,2);
            sourceCode_rule_service= UIUtils.createLabelText(fileRuleGroup, "Service:",null,2);
            sourceCode_rule_service_impl= UIUtils.createLabelText(fileRuleGroup, "Service Impl:",null,2);
            sourceCode_rule_controller= UIUtils.createLabelText(fileRuleGroup, "Controller:",null,2);
        
        }
 
 
        performDefaults();

        return composite;
    }

    @Override
    protected void performDefaults()
    {
        DBPPreferenceStore store = DBWorkbench.getPlatform().getPreferenceStore();

    
        sourceCodeOutPutDir.setText(store.getString(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER));
        sourceCodePackagePath.setText(store.getString(SourceCodePreferences.SOURCECODE_PACKAGENAME));
        sourceCodePageClassFullName.setText(store.getString(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME));
//        sourceCodeEntitySuffix.setText(store.getString(SourceCodePreferences.SOURCECODE_ENTITYSUFFIX));
        sourceCodeAuthor.setText(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
        sourceCode_gourp_name.setText(store.getString(SourceCodePreferences.SOURCECODE_GROUPNAME));
        sourceCode_rule_entity.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY));
        sourceCode_rule_entity_lombokdata.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY_LOMBOKDATA));
        sourceCode_rule_dao.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO));
//        sourceCode_rule_mybatis.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_MYBATIS));
        sourceCode_rule_component.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT));
        sourceCode_rule_component_impl.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL));
        sourceCode_rule_service.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE));
        sourceCode_rule_service_impl.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL));
        sourceCode_rule_controller.setText(store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER));
        
    }

    @Override
    public boolean performOk()
    {
        DBPPreferenceStore store = DBWorkbench.getPlatform().getPreferenceStore();

//        store.setValue(DBeaverPreferences.UI_AUTO_UPDATE_CHECK, automaticUpdateCheck.getSelection());

        store.setValue(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER, sourceCodeOutPutDir.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_PACKAGENAME, sourceCodePackagePath.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_AUTHOR, sourceCodeAuthor.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_GROUPNAME, sourceCode_gourp_name.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME, sourceCodePageClassFullName.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_ENTITY, sourceCode_rule_entity.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_ENTITY_LOMBOKDATA, sourceCode_rule_entity_lombokdata.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_DAO, sourceCode_rule_dao.getText());
//        store.setValue(SourceCodePreferences.SOURCECODE_RULE_MYBATIS, sourceCode_rule_mybatis.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_COMPONENT, sourceCode_rule_component.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL, sourceCode_rule_component_impl.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_SERVICE, sourceCode_rule_service.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL, sourceCode_rule_service_impl.getText());
        store.setValue(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER, sourceCode_rule_controller.getText());

        

        PrefUtils.savePreferenceStore(store);

//        if (workspaceLanguage.getSelectionIndex() >= 0) {
//            PlatformLanguageDescriptor language = PlatformLanguageRegistry.getInstance().getLanguages().get(workspaceLanguage.getSelectionIndex());
//            try {
//                DBPPlatformLanguage curLanguage = DBWorkbench.getPlatform().getLanguage();
//                if (curLanguage != language) {
//                    ((DBPPlatformLanguageManager)DBWorkbench.getPlatform()).setPlatformLanguage(language);
//
//                    if (UIUtils.confirmAction(
//                        getShell(),
//                        "Restart " + GeneralUtils.getProductName(),
//                        "You need to restart " + GeneralUtils.getProductName() + " to perform actual language change.\nDo you want to restart?"))
//                    {
//                        UIUtils.asyncExec(() -> PlatformUI.getWorkbench().restart());
//                    }
//                }
//            } catch (DBException e) {
//                DBWorkbench.getPlatformUI().showError("Change language", "Can't switch language to " + language, e);
//            }
//        }

        return true;
    }

    @Nullable
    @Override
    public IAdaptable getElement()
    {
        return null;
    }

    @Override
    public void setElement(IAdaptable element)
    {
    }

}