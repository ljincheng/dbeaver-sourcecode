package org.jkiss.dbeaver.model.sourcecode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.DBPScriptObjectExt;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGenerator;
import org.jkiss.dbeaver.model.sql.SQLConstants;
import org.jkiss.dbeaver.model.sql.generator.SQLGenerator;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

public abstract class GeneratorSourceCode  extends SourceCodeGenerator<DBPScriptObject> {
	

    @Override
    public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        boolean allTables = true;
        List<DBSTable> tableList = new ArrayList<>();
        for (DBPScriptObject object : objects) {
            if (!(object instanceof DBSTable)) {
                allTables = false;
                break;
            } else {
                tableList.add((DBSTable) object);
            }
        }
        if (!allTables) {
            super.run(monitor);
            return;
        }

        StringBuilder sql = new StringBuilder(100);
        Map<String, Object> options = new HashMap<>();
        addOptions(options);
        try {
        	generateTableListCode(monitor, sql, tableList, options);
        } catch (DBException e) {
            throw new InvocationTargetException(e);
        }
        result = sql.toString().trim();
    }
    
     
    public abstract void generateTableListCode(@NotNull DBRProgressMonitor monitor, @NotNull StringBuilder sql, @NotNull List<DBSTable> tablesOrViews, Map<String, Object> options) throws DBException;
   
     
 
}