package org.jkiss.dbeaver.model.sourcecode.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBPDataSource;
import org.jkiss.dbeaver.model.DBPObject;
import org.jkiss.dbeaver.model.sql.generator.SQLGenerator;

public class GeneratorSourceCodeConfigurationRegistry {

	 private static final Log log = Log.getLog(GeneratorSourceCodeConfigurationRegistry.class);

	    private static final String TAG_GENERATOR = "generator"; //$NON-NLS-1$

	    private static GeneratorSourceCodeConfigurationRegistry instance = null;
	    private final List<SourceCodeGeneratorDescriptor> generators = new ArrayList<>();

	    public synchronized static GeneratorSourceCodeConfigurationRegistry getInstance()
	    {
	        if (instance == null) {
	            instance = new GeneratorSourceCodeConfigurationRegistry();
	            instance.loadExtensions(Platform.getExtensionRegistry());
	        }
	        return instance;
	    }

	    private GeneratorSourceCodeConfigurationRegistry()
	    {
	    }

	    private void loadExtensions(IExtensionRegistry registry)
	    {
	        IConfigurationElement[] extConfigs = registry.getConfigurationElementsFor(SourceCodeGeneratorDescriptor.EXTENSION_ID);
	        for (IConfigurationElement ext : extConfigs) {
	            // Load generators
	            if (TAG_GENERATOR.equals(ext.getName())) {
	                this.generators.add(new SourceCodeGeneratorDescriptor(ext));
	            }
	        }
	    }

	    public void dispose()
	    {
	        generators.clear();
	    }

	    public List<SourceCodeGeneratorDescriptor> getAllGenerators() {
	        return new ArrayList<>(generators);
	    }

	    public List<SourceCodeGeneratorDescriptor> getApplicableGenerators(Collection<?> objects, Object context) {
	        List<SourceCodeGeneratorDescriptor> result = new ArrayList<>();
	        for (SourceCodeGeneratorDescriptor gen : generators) {
	            for (Object object : objects) {
	                if (object instanceof DBPObject && gen.appliesTo((DBPObject) object, context)) {
	                    if (gen.isMultiObject() && objects.size() < 2) {
	                        continue;
	                    }
	                    result.add(gen);
	                    break;
	                }
	            }
	        }
	        result.sort(Comparator.comparingInt(SourceCodeGeneratorDescriptor::getOrder));
	        return result;
	    }

	    public SourceCodeGeneratorDescriptor getGenerator(String id) {
	        for (SourceCodeGeneratorDescriptor generator : generators) {
	            if (generator.getId().equalsIgnoreCase(id)) {
	                return generator;
	            }
	        }
	        return null;
	    }

	    @Nullable
	    public <T> SQLGenerator<T> createGenerator(DBPDataSource dataSource, List<T> objectsd) {
 
	        return null;
	    }
}
