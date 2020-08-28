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
		 
		 String mJsonView_fullName=getTpl_jsonView();
		 String mJsonView_typeName=CodeHelper.getClassSimpleName(mJsonView_fullName);//JsonView类型名称
		 String mBusinessException_fullName=getTpl_businessException(); 
		 String mBusinessException_typeName=CodeHelper.getClassSimpleName(mBusinessException_fullName);//businessException类型名称
		 String mAssertUtils_fullName=getTpl_assertUtils();
		 String mAssertUtils_typeName=CodeHelper.getClassSimpleName(mAssertUtils_fullName);//assertUtils类型名称
		 String mBaseController_fullName=getTpl_baseController();
		 String mBaseController_typeName=CodeHelper.getClassSimpleName(mBaseController_fullName);//BaseController类型名称
		 
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
		 String mPrimaryKey_typeName=null;
		 String mPrimaryKey_paramName=null;
		

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
		            			 
		            			 mPrimaryKey_typeName=CodeHelper.columnType2JavaType(primaryTableColumn);
		            			 mPrimaryKey_paramName= primaryKey_lowerCamelCase;
	        			 }
	        		 }
	        		 
	        	 }
	         }
	         
	     }
     
		 CodeHelper.addCodeLine(sql,String.format("package %s;", mController_package) );
		 CodeHelper.addCodeLine(sql, "import java.util.List;");
		 CodeHelper.addCodeLine(sql, "import java.util.Map;");
		 CodeHelper.addCodeLine(sql, "import javax.servlet.http.HttpServletRequest;");
		 CodeHelper.addCodeLine(sql, "import org.slf4j.Logger;");
		 CodeHelper.addCodeLine(sql, "import org.slf4j.LoggerFactory;");
		 CodeHelper.addCodeLine(sql, "import org.apache.shiro.authz.annotation.RequiresPermissions;");
		 CodeHelper.addCodeLine(sql, "import org.springframework.beans.factory.annotation.Autowired;");
		 CodeHelper.addCodeLine(sql, "import org.springframework.stereotype.Controller;");
		 CodeHelper.addCodeLine(sql, "import org.springframework.web.bind.annotation.*;");
		 CodeHelper.addCodeLine(sql, "import org.springframework.web.servlet.ModelAndView;");
		 
		 CodeHelper.importJavaPackage(sql, attrs);
		 CodeHelper.addCodeLine(sql, String.format("import %s;", mBaseController_fullName));
		 CodeHelper.addCodeLine(sql, String.format("import %s;", mAssertUtils_fullName));
		 CodeHelper.addCodeLine(sql, String.format("import %s;", mBusinessException_fullName));
		 CodeHelper.addCodeLine(sql, String.format("import %s;", mJsonView_fullName));
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
		 CodeHelper.addCodeLine(sql,String.format( "public class %s extends %s{",mController_typeName,mBaseController_typeName));
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format( "private static Logger logger= LoggerFactory.getLogger(%s.class);",mController_typeName));
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format( "@Autowired%sprivate %s %s;",lf,mService_typeName,mService_paramName));
		 sql.append(lf);
		
		 //list
		 CodeHelper.addCodeLine(sql,"@GetMapping(\"/list\")");
		 CodeHelper.addCodeLine(sql,String.format("public ModelAndView list(){%sModelAndView view=new ModelAndView(\"/%s/%s/list\");%sreturn view;%s}",lf, mGroupName,tableName_lowerCamelCase,lf,lf) );
		 sql.append(lf);
		 
		 //listTable
		 CodeHelper.addCodeLine(sql, "@PostMapping(\"/list\")");
		 CodeHelper.addCodeLine(sql, String.format("@RequiresPermissions(\"%s:%s:list\")",mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql,String.format("public ModelAndView listTable(HttpServletRequest request,@RequestParam(required = false,defaultValue =\"1\")Long pageIndex,@RequestParam(required = false,defaultValue =\"20\")Integer pageSize,String startDate,String endDate){%sModelAndView view=new ModelAndView(\"/%s/%s/list_table\");%s Map<String,Object> selectItem = getRequestToParamMap(request); %s model.addObject(\"pagedata\",%s.query%sListPage(pageIndex,pageSize,selectItem));%s return view;%s}",lf, mGroupName,tableName_lowerCamelCase,lf,lf,mService_paramName,tableName_upperCamelCase,lf,lf) );
		 sql.append(lf);
		 
		 // add
		 CodeHelper.addCodeLine(sql, "@GetMapping(value=\"/add\")");
		 CodeHelper.addCodeLine(sql, "public ModelAndView add(HttpServletRequest request){");
		 CodeHelper.addCodeLine(sql, String.format("ModelAndView view=new ModelAndView(\"/%s/%s/add\");", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("return view;%s}", lf));
		 sql.append(lf);
		 
		 // add_POST
		 CodeHelper.addCodeLine(sql, "@PostMapping(value=\"/add\")");
		 CodeHelper.addCodeLine(sql, String.format("@RequiresPermissions(\"%s:%s:add\")", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("public ModelAndView add_POST(HttpServletRequest request,%s %s){",mEntity_typeName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, String.format("ModelAndView view=new ModelAndView(\"/%s/%s/add\");", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("try{%s", lf));
		 CodeHelper.addCodeLine(sql, String.format("Integer dbRes= %s.insert%s(%s);", mService_paramName,tableName_upperCamelCase,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, "AssertUtils.isPositiveNumber(dbRes,\"操作失败\");");
		 CodeHelper.addCodeLine(sql, String.format("}catch (%s e) {", mBusinessException_typeName));
		 CodeHelper.addCodeLine(sql, "setPromptException(view, e);");
		 CodeHelper.addCodeLine(sql, "}catch (Exception ex) {");
		 CodeHelper.addCodeLine(sql, String.format("logger.error(\"编辑%s异常\",ex);",description));
		 CodeHelper.addCodeLine(sql, String.format("setPromptException(view, ex);%s}",lf));
		 CodeHelper.addCodeLine(sql, String.format("return view;%s}", lf));
		 sql.append(lf);
		  
		 //view
		 CodeHelper.addCodeLine(sql, "@GetMapping(value=\"/view\")");
		 CodeHelper.addCodeLine(sql, String.format("@RequiresPermissions(\"%s:%s:view\")", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format(" public ModelAndView view(HttpServletRequest request,%s %s){", mPrimaryKey_typeName,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("ModelAndView view =new ModelAndView(\"/%s/%s/view\");", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("%s %s= %s.find%sBy%s(%s);", mEntity_typeName,mEntity_paramName,mService_paramName,tableName_upperCamelCase,primaryKey_upperCamelCase,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("view.addObject(\"%s\", %s);", mEntity_paramName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, String.format(" return view;%s}",lf));
		 sql.append(lf);
		 
		 //edit
		 CodeHelper.addCodeLine(sql, "@GetMapping(value=\"/edit\")");
		 CodeHelper.addCodeLine(sql, String.format("public ModelAndView edit(HttpServletRequest request,%s %s){", mPrimaryKey_typeName,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql,String.format( "ModelAndView view =new ModelAndView(\"/%s/%s/edit\");",mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql,String.format( "%s %s= %s.find%sBy%s(%s);",mEntity_typeName,mEntity_paramName,mService_paramName,tableName_upperCamelCase,primaryKey_upperCamelCase,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql,String.format( "view.addObject(\"%s\", %s);",mEntity_paramName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql,String.format( "return view;%s}",lf));
		 sql.append(lf);
		 
		 //edit_Post
		 CodeHelper.addCodeLine(sql,  String.format("@PostMapping(value=\"/edit/{%s}\")",mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("@RequiresPermissions(\"%s:%s:edit\")", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("public ModelAndView edit_POST(HttpServletRequest request,@PathVariable(\"%s\") %s %s, %s %s){", mPrimaryKey_paramName,mPrimaryKey_typeName,mPrimaryKey_paramName,mEntity_typeName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, String.format("ModelAndView view =new ModelAndView(\"/%s/%s/edit\");", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("try{%s", lf));
		 CodeHelper.addCodeLine(sql, String.format("%s.set%s(%s);", mEntity_paramName,primaryKey_upperCamelCase,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("Integer result= %s.update%sBy%s(%s);",mService_paramName,tableName_upperCamelCase,primaryKey_upperCamelCase,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, String.format("view.addObject(\"%s\", %s);",mEntity_paramName,mEntity_paramName));
		 CodeHelper.addCodeLine(sql, "AssertUtils.isPositiveNumber(dbRes,\"操作失败\");");
		 CodeHelper.addCodeLine(sql, String.format("}catch (%s e) {", mBusinessException_typeName));
		 CodeHelper.addCodeLine(sql, "setPromptException(view, e);");
		 CodeHelper.addCodeLine(sql, "}catch (Exception e) {");
		 CodeHelper.addCodeLine(sql, String.format("logger.error(\"编辑%s异常\",e);",description));
		 CodeHelper.addCodeLine(sql, String.format("setPromptException(view, e);%s}",lf));
		 CodeHelper.addCodeLine(sql, String.format("return view;%s}",lf));
		 sql.append(lf);
		 
		 //delete
		 CodeHelper.addCodeLine(sql, " @PostMapping(value=\"/delete\")");
		 CodeHelper.addCodeLine(sql, String.format("@RequiresPermissions(\"%s:%s:delete\")", mGroupName,tableName_lowerCamelCase));
		 CodeHelper.addCodeLine(sql, String.format("public %s<String> delete(HttpServletRequest request, %s %s){",mJsonView_typeName,mPrimaryKey_typeName,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("%s<String> view=new %s<String>();",mJsonView_typeName,mJsonView_typeName));
		 CodeHelper.addCodeLine(sql, String.format("try{%s", lf));
		 if("String".equals(mPrimaryKey_typeName))
		 {
			 CodeHelper.addCodeLine(sql, String.format("%s.isNotBlank(%s, \"参数无效\");",mAssertUtils_typeName, mPrimaryKey_paramName));
		 }else {
			 CodeHelper.addCodeLine(sql, String.format("%s.notNull(%s, \"参数无效\");", mAssertUtils_typeName,mPrimaryKey_paramName));
		 }
		 CodeHelper.addCodeLine(sql, String.format("Integer result=%s.delete%sBy%s(%s);", mService_paramName,tableName_upperCamelCase,primaryKey_upperCamelCase,mPrimaryKey_paramName));
		 CodeHelper.addCodeLine(sql, String.format("%s.isPositiveNumber(result,\"操作失败\");",mAssertUtils_typeName));
		 CodeHelper.addCodeLine(sql, "setPromptMessage(view, view.CODE_SUCCESS, \"操作成功\");");
		 CodeHelper.addCodeLine(sql, String.format("}catch (%s e) {", mBusinessException_typeName));
		 CodeHelper.addCodeLine(sql, "setPromptException(view, e);");
		 CodeHelper.addCodeLine(sql, "}catch (Exception ex) {");
		 CodeHelper.addCodeLine(sql, String.format("logger.error(\"删除%s异常\",ex);",description));
		 CodeHelper.addCodeLine(sql, String.format("setPromptException(view, ex);%s}",lf));
		 CodeHelper.addCodeLine(sql, String.format(" return view;%s}", lf));
		 
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql, "}");
		 

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    

}