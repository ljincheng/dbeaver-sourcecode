package org.jkiss.dbeaver.model.sourcecode.ui.preferences;

import java.io.IOException;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ui.editors.sql.templates.SQLContextTypeRegistry;
import org.jkiss.dbeaver.ui.editors.sql.templates.SQLTemplateStore;
import org.jkiss.dbeaver.ui.editors.sql.templates.SQLTemplatesRegistry;

public class SourceCodeTemplatesRegistry {


    private static final Log log = Log.getLog(SourceCodeTemplatesRegistry.class);
    private static SourceCodeTemplatesRegistry instance;

    private ContextTypeRegistry templateContextTypeRegistry;
    private TemplateStore templateStore;

    public synchronized static SourceCodeTemplatesRegistry getInstance()
    {
        if (instance == null) {
            instance = new SourceCodeTemplatesRegistry();
        }
        return instance;
    }

    public synchronized ContextTypeRegistry getTemplateContextRegistry() {
        if (templateContextTypeRegistry == null) {
            //SQLContextTypeRegistry registry = new SQLContextTypeRegistry();

            //TemplateContextType contextType= registry.getContextType("sql");

            templateContextTypeRegistry = new SQLContextTypeRegistry();
        }

        return templateContextTypeRegistry;
    }

    public TemplateStore getTemplateStore() {
        if (templateStore == null) {
            templateStore = new SourceCodeTemplateStore(getTemplateContextRegistry());

            try {
                templateStore.load();
            } catch (IOException e) {
                log.error("Can't load template store", e);
            }
            templateStore.startListeningForPreferenceChanges();
        }

        return templateStore;
    }
}
