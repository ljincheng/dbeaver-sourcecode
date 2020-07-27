package org.jkiss.dbeaver.model.sourcecode.ui.preferences;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.editors.sql.templates.SQLTemplateStore;
import org.jkiss.dbeaver.ui.editors.sql.templates.SQLTemplatesRegistry;
import org.jkiss.dbeaver.ui.preferences.PreferenceStoreDelegate;

public class PrefPageSourceCodeTemplates extends TemplatePreferencePage implements IWorkbenchPropertyPage {

    public static final String PAGE_ID = "org.jkiss.dbeaver.preferences.sourcecode.main.codetemplate";

    public PrefPageSourceCodeTemplates()
    {
        setPreferenceStore(new PreferenceStoreDelegate(DBWorkbench.getPlatform().getPreferenceStore()));
        setTemplateStore(SourceCodeTemplatesRegistry.getInstance().getTemplateStore());
        setContextTypeRegistry(SourceCodeTemplatesRegistry.getInstance().getTemplateContextRegistry());
    }

    protected String getFormatterPreferenceKey() {
        return SourceCodeTemplateStore.PREF_STORE_KEY;
    }

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