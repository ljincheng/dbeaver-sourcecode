package org.jkiss.dbeaver.model.sourcecode.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPartSite;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGenerator;
import org.jkiss.dbeaver.model.sourcecode.ui.preferences.SourceCodePreferences;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.ui.editors.sql.dialogs.ViewSQLDialog;
import org.jkiss.utils.CommonUtils;


public class GeneratorSourceCodeDialog extends ViewSQLDialog {

    private final SourceCodeGenerator<?> sqlGenerator;
    private Text directoryText;
    private Text packageNameText;
    private Text pageClassFullNameText;
//    private Text fileRuleText;
    private Text groupNameText;
    private DBPPreferenceStore store ;
  //  private GeneratorSourceCodeExport mGeneratorSourceCodeExport;

   public GeneratorSourceCodeDialog(IWorkbenchPartSite parentSite, DBCExecutionContext context, SourceCodeGenerator<?> sqlGenerator) {
        super(parentSite, () -> context,
            "Generated Source Code",
            null, "");
        this.sqlGenerator = sqlGenerator;
        store = DBWorkbench.getPlatform().getPreferenceStore();
       // mGeneratorSourceCodeExport=new GeneratorSourceCodeExport();
    }

   @Override
   protected boolean isLabelVisible() {
       return false;
   }
    @Override
    protected Composite createDialogArea(Composite parent) {

    	
        Composite composite = super.createDialogArea(parent);
        Group settings = UIUtils.createControlGroup(composite, UIMessages.dbeaver_generate_sourcecode_settings, 2, GridData.FILL_HORIZONTAL, SWT.DEFAULT);
        directoryText=DialogUtils.createOutputFolderChooser(settings, UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder,store.getString(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER), e->{
        	sqlGenerator.setRootPath(directoryText.getText());
        });
        packageNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_packageName, store.getString(SourceCodePreferences.SOURCECODE_PACKAGENAME));
        pageClassFullNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_pageClassFullName, store.getString(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME));
//        fileRuleText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_entitySuffix, sqlGenerator.getFileRule());
        groupNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_groupName, store.getString(SourceCodePreferences.SOURCECODE_GROUPNAME));
        
        sqlGenerator.setRootPath(directoryText.getText());
        sqlGenerator.setPackageName(packageNameText.getText());
//        sqlGenerator.setEntitySuffix(fileRuleText.getText());
        sqlGenerator.setPageClassFullName(pageClassFullNameText.getText());
        sqlGenerator.setAuthor(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
        sqlGenerator.setAuthor(groupNameText.getText());
        sqlGenerator.setGroupName(groupNameText.getText());
        
        //设置文件规则
        sqlGenerator.setRuleEntity(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY));
        sqlGenerator.setRuleEntityLombokData(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY_LOMBOKDATA));
        sqlGenerator.setRuleDao(store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO));
        sqlGenerator.setRuleComponent(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT));
        sqlGenerator.setRuleComponentImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL));
        sqlGenerator.setRuleService(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE));
        sqlGenerator.setRuleServiceImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL));
        sqlGenerator.setRuleController(store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER));
        
        UIUtils.runInUI(sqlGenerator);
        
        Object sql = sqlGenerator.getResult();
        if (sql != null) {
            setSQLText(CommonUtils.toString(sql));
            updateSQL();
        }
       
        
        return composite;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
    	createButton(parent, IDialogConstants.RETRY_ID, UIMessages.dbeaver_generate_sourcecode_refresh, false);
    	createButton(parent, IDialogConstants.NEXT_ID, UIMessages.dbeaver_generate_sourcecode_save, false);
    	super.createButtonsForButtonBar(parent);
    }
    
    @Override
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == IDialogConstants.RETRY_ID) {
        	 sqlGenerator.setGeneratorType(0);
        	 sqlGenerator.setRootPath(directoryText.getText());
             sqlGenerator.setPackageName(packageNameText.getText());
//             sqlGenerator.setEntitySuffix(fileRuleText.getText());
             sqlGenerator.setPageClassFullName(pageClassFullNameText.getText());
             sqlGenerator.setAuthor(groupNameText.getText());
             sqlGenerator.setGroupName(groupNameText.getText());
             SourceCodePreferences.saveDefaultPreferencesValue(store, SourceCodePreferences.SOURCECODE_GROUPNAME, groupNameText.getText());
        	UIUtils.runInUI(sqlGenerator);
        	 Object sql = sqlGenerator.getResult();
             if (sql != null) {
                 setSQLText(CommonUtils.toString(sql));
                 updateSQL();
             }
           // super.buttonPressed(IDialogConstants.CANCEL_ID);
        }else if(buttonId == IDialogConstants.NEXT_ID) {
//        	GeneratorSourceCodeExport export=new GeneratorSourceCodeExport(directoryText.getText());
//        	export.sa
        	sqlGenerator.setGeneratorType(1);
        	sqlGenerator.setRootPath(directoryText.getText());
            sqlGenerator.setPackageName(packageNameText.getText());
//            sqlGenerator.setEntitySuffix(entitySuffixText.getText());
            sqlGenerator.setPageClassFullName(pageClassFullNameText.getText());
            sqlGenerator.setAuthor(groupNameText.getText());
            sqlGenerator.setGroupName(groupNameText.getText());
            SourceCodePreferences.saveDefaultPreferencesValue(store, SourceCodePreferences.SOURCECODE_GROUPNAME, groupNameText.getText());
        	UIUtils.runInUI(sqlGenerator);
        	boolean result=sqlGenerator.getGeneratorResult();
        	if(result)
        	{
        		super.buttonPressed(IDialogConstants.CANCEL_ID);
        	}
        } else {
            super.buttonPressed(buttonId);
        }
    }
}
