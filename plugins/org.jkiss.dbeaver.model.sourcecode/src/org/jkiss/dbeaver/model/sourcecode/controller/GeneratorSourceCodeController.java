package org.jkiss.dbeaver.model.sourcecode.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

public class GeneratorSourceCodeController extends GeneratorSourceCode{
	 
	private GeneratorSourceCodeExport codeExport;
	 @Override
	public void generateTableListCode(DBRProgressMonitor monitor, StringBuilder sql, List<DBSTable> tablesOrViews,Map<String, Object> options) throws DBException {
		 
		 
		 	List<DBSTable> goodTableList = new ArrayList<>();
	        List<DBSTable> cycleTableList = new ArrayList<>();
	        List<DBSTable> viewList = new ArrayList<>();

	        DBStructUtils.sortTableList(monitor, tablesOrViews, goodTableList, cycleTableList, viewList);

	        // Good tables: generate full DDL
	        if(getGeneratorType()==1)
	        {
	        	boolean runGenResult=true;
	        	for (DBSTable table : goodTableList) { 
	        		StringBuilder code=new StringBuilder(100);
		        	generateTableMybatisCode(code,monitor, table, options);
		        	codeExport=new GeneratorSourceCodeExport(getRootPath());
		        	boolean genResult=codeExport.exportController(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
		        	if(!genResult)
		        	{
		        		if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
		        		{
		        			runGenResult=false;
		        			setGeneratorResult(runGenResult);
		        			monitor.done();
		        			return ;
		        		}
		        		runGenResult=false;
		        	}
		        }
	        	for (DBSTable table : cycleTableList) { 
	        		StringBuilder code=new StringBuilder(100);
		        	generateTableMybatisCode(code,monitor, table, options);
		        	codeExport=new GeneratorSourceCodeExport(getRootPath());
		        	boolean genResult=codeExport.exportController(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
		        	if(!genResult)
		        	{
		        		if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
		        		{
		        			runGenResult=false;
		        			setGeneratorResult(runGenResult);
		        			monitor.done();
		        			return ;
		        		}
		        		runGenResult=false;
		        	}
		        }
	        	for (DBSTable table : viewList) { 
	        		StringBuilder code=new StringBuilder(100);
	        		generateTableMybatisCode(code,monitor, table, options);
	        		codeExport=new GeneratorSourceCodeExport(getRootPath());
	        		boolean genResult=codeExport.exportController(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
	        		if(!genResult)
	        		{
	        			if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
	        			{
	        				runGenResult=false;
	        				setGeneratorResult(runGenResult);
	        				monitor.done();
	        				return ;
	        			}
	        			runGenResult=false;
	        		}
	        	}
	        	setGeneratorResult(runGenResult);
	        }else {
	        	for (DBSTable table : goodTableList) { 
		        	generateTableMybatisCode(sql,monitor, table, options);
		        }
	        	for (DBSTable table : cycleTableList) { 
	        		generateTableMybatisCode(sql,monitor, table, options);
	        	}
	        	for (DBSTable table : viewList) { 
	        		generateTableMybatisCode(sql,monitor, table, options);
	        	}
	        }
	        
	        monitor.done();
		 
	 }
	 
	 private String javaFileName(DBSTable table,String extFileName)
	 {
		 String tableName=table.getName();
		 String mTableName_upperCamelCase=CodeHelper.toUpperCamelCase(tableName);//表名大写开头
		 String mClassFullName=getClassFullName(mTableName_upperCamelCase, getRuleController());//类全称
		 String mClassSimpleName=CodeHelper.getClassSimpleName(mClassFullName);//类简称
		 return mClassSimpleName+extFileName;
	 }
	 
	 private void generateTableMybatisCode( StringBuilder sql,DBRProgressMonitor monitor,DBSTable table, Map<String, Object> options)throws DBException
	 {
		 String lf = GeneralUtils.getDefaultLineSeparator();
		 String tableName=table.getName();
		 String description=CodeHelper.emptyString(table.getDescription(),false);
		 String mTableName_upperCamelCase=CodeHelper.toUpperCamelCase(tableName);//表名大写开头
//		 String mTableName_lowerCamelCase=CodeHelper.toLowerCamelCase(tableName);//表名小写开头
		 String mEntity_fullName=getClassFullName(mTableName_upperCamelCase, getRuleEntity());//类全称
		 String mEntity_package=CodeHelper.getPackageName(mEntity_fullName);//类包名
		 String mEntity_typeName=CodeHelper.getClassSimpleName(mEntity_fullName);//类简称
		 String mEntity_paramName=CodeHelper.toLowerCamelCase(mEntity_typeName);//类简称
		 
		 String mDao_fullName=getClassFullName(mTableName_upperCamelCase, getRuleDao());//Dao类全称
		 String mDao_package=CodeHelper.getPackageName(mDao_fullName);//Dao类包名
		 String mDao_typeName=CodeHelper.getClassSimpleName(mDao_fullName);//Dao类型名称
		 String mDao_paramName=CodeHelper.toLowerCamelCase(mDao_typeName);//Dao类变量
		 
		 String mComponent_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponent());//ComponentImpl类全称
		 String mComponent_package=CodeHelper.getPackageName(mComponent_fullName);//ComponentImpl类包名
		 String mComponent_typeName=CodeHelper.getClassSimpleName(mComponent_fullName);//ComponentImpl类型名称
		 String mComponent_paramName=CodeHelper.toLowerCamelCase(mComponent_typeName);//ComponentImpl类变量
		 
		 String mComponentImpl_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponentImpl());//ComponentImpl类全称
		 String mComponentImpl_package=CodeHelper.getPackageName(mComponentImpl_fullName);//ComponentImpl类包名
		 String mComponentImpl_typeName=CodeHelper.getClassSimpleName(mComponentImpl_fullName);//ComponentImpl类型名称
		 String mComponentImpl_paramName=CodeHelper.toLowerCamelCase(mComponentImpl_typeName);//ComponentImpl类变量
		 
		 String mService_fullName=getClassFullName(mTableName_upperCamelCase, getRuleService());//ComponentImpl类全称
		 String mService_package=CodeHelper.getPackageName(mService_fullName);//ComponentImpl类包名
		 String mService_typeName=CodeHelper.getClassSimpleName(mService_fullName);//ComponentImpl类型名称
		 String mService_paramName=CodeHelper.toLowerCamelCase(mService_typeName);//ComponentImpl类变量
		 
		 String mController_fullName=getClassFullName(mTableName_upperCamelCase, getRuleController());//ComponentImpl类全称
		 String mController_package=CodeHelper.getPackageName(mController_fullName);//ComponentImpl类包名
		 String mController_typeName=CodeHelper.getClassSimpleName(mController_fullName);//ComponentImpl类型名称
		 
		 String pageClassFullName=getPageClassFullName();
		 String pageClassSimpleName=null;
		 
		 if(!CodeHelper.isEmpty(pageClassFullName))
		 {
			 pageClassSimpleName=CodeHelper.getClassSimpleName(pageClassFullName);
		 }
		 String packageName=getPackageName();
		 String tableName_upperCamelCase=CodeHelper.toUpperCamelCase(table.getName());
		 String tableName_lowerCamelCase=CodeHelper.toLowerCamelCase(table.getName());
		 
		 String mGroupName=CodeHelper.emptyString(getGroupName(), true).trim();
		 
		 String primaryKey=null;//主键
		 DBSTableColumn primaryTableColumn=null;//主键
		 String primaryKey_upperCamelCase=null;
		 String primaryKey_lowerCamelCase=null;

		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)table.getAttributes(monitor);
		 boolean useGeneratedKeys=false;
		 Collection<? extends DBSTableIndex> indexes = table.getIndexes(monitor);
     if (!CommonUtils.isEmpty(indexes)) {
         for (DBSTableIndex index : indexes) {
        	 if( index.isPrimary())
        	 {
        		 List<? extends DBSTableIndexColumn>  tableColumn=index.getAttributeReferences(monitor);
        		 for(DBSTableIndexColumn column:tableColumn)
        		 {
        			 DBSTableColumn primaryColumn= column.getTableColumn();
        			 if(primaryColumn!=null)
        			 {
        				 primaryTableColumn=primaryColumn;
	            			 useGeneratedKeys=primaryColumn.isAutoGenerated();
	            			 primaryKey=primaryColumn.getName();
	            			 primaryKey_upperCamelCase=CodeHelper.toUpperCamelCase(primaryKey);
	            			 primaryKey_lowerCamelCase=CodeHelper.toLowerCamelCase(primaryKey);
        			 }
        		 }
        		 
        	 }
         }
     }
     
		 CodeHelper.addCodeLine(sql,String.format("package %s;", mController_package) );
		 CodeHelper.addCodeLine(sql, "import java.util.List;");
		 CodeHelper.addCodeLine(sql, "import java.util.Map;");
		 CodeHelper.importJavaPackage(sql, attrs);
		 sql.append(lf);
		 
		 if(!CodeHelper.isEmpty(pageClassFullName))
		 {
			 CodeHelper.addCodeLine(sql, String.format("import %s;", pageClassFullName));
		 }
		 CodeHelper.addCodeLine(sql, String.format("import %s;",mEntity_fullName));
		 CodeHelper.addCodeLine(sql, String.format("import %s;",mService_fullName));
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format("/**%s * %s%s * @author %s%s * @version v1.0%s */",lf,description,lf,getAuthor(),lf,lf));
		 CodeHelper.addCodeLine(sql,"@Controller");
		 CodeHelper.addCodeLine(sql,String.format( "public class %s extends BaseAction{",mController_typeName));
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format( "@Autowired%sprivate %s %s;",lf,mService_typeName,mService_paramName));
		 sql.append(lf);
				 
		 CodeHelper.addCodeLine(sql, String.format("@GetMapping(\"/%s/%sList.do\")",mGroupName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql,String.format("public ModelAndView %sList_methodGet(){%sModelAndView view=new ModelAndView(\"%s/%sList\");%sreturn view;%s}",tableName_lowerCamelCase,lf, mGroupName,mEntity_paramName,lf,lf) );
		 sql.append(lf);
		 
		 CodeHelper.addCodeLine(sql, String.format("@PostMapping(\"/%s/%sList.do\")",mGroupName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql,String.format("public ModelAndView %sList_methodPost(HttpServletRequest request,Long pageIndex,Integer pageSize,String startDate,String endDate){%sModelAndView view=new ModelAndView(\"%s/%sList_table\");%sreturn view;%s}",tableName_lowerCamelCase,lf, mGroupName,mEntity_paramName,lf,lf) );
		 sql.append(lf);
		  
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql, "}");
		 

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    

}