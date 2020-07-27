package org.jkiss.dbeaver.model.sourcecode.registry;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.impl.AbstractContextDescriptor;
import org.jkiss.dbeaver.model.sql.generator.SQLGenerator;
import org.jkiss.utils.CommonUtils;

public class SourceCodeGeneratorDescriptor extends AbstractContextDescriptor {

    public static final String EXTENSION_ID = "org.jkiss.dbeaver.sourceCodeGenerator"; //$NON-NLS-1$

    private final String id;
    private final String label;
    private final String description;
    private final int order;
    private final boolean multiObject;
    private final ObjectType generatorImplClass;

    SourceCodeGeneratorDescriptor(IConfigurationElement config) {
        super(config);
        this.id = config.getAttribute("id");
        this.label = config.getAttribute("label");
        this.description = config.getAttribute("description");
        this.generatorImplClass = new ObjectType(config.getAttribute("class"));
        this.order = CommonUtils.toInt(config.getAttribute("order"));
        this.multiObject = CommonUtils.toBoolean(config.getAttribute("multiObject"));
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }

    public boolean isMultiObject() {
        return multiObject;
    }

    @NotNull
    public <T> SourceCodeGenerator<T> createGenerator(List<T> objects)
        throws DBException
    {
        @SuppressWarnings("unchecked")
        SourceCodeGenerator<T> instance = generatorImplClass.createInstance(SourceCodeGenerator.class);
        instance.initGenerator(objects);
        return instance;
    }

    @Override
    public String toString() {
        return id;
    }
}