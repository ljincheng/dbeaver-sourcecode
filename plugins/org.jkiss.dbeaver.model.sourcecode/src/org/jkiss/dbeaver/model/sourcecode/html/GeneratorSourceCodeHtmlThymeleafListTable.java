package org.jkiss.dbeaver.model.sourcecode.html;

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

/**
 * Html list_table
 * @author ljc
 *
 */
public class GeneratorSourceCodeHtmlThymeleafListTable extends GeneratorSourceCode{
	 
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
		        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/list_table.html"), code.toString(),table.getDataSource().getName());
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
		        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/list_table.html"), code.toString(),table.getDataSource().getName());
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
	        		boolean genResult=codeExport.exportHtml(javaFileName(table,"/list_table.html"), code.toString(),table.getDataSource().getName());
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
   
	     StringBuilder html_theadList=new StringBuilder();
	     StringBuilder html_thbodyList=new StringBuilder();
	     
	     if(attrs!=null)
		 {
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 String codeName_upperCamelCase=CodeHelper.toUpperCamelCase(columnName);
				 
				 CodeHelper.addCodeLine(html_theadList, String.format("<th th:text=\"#{%s.%s.%s}\">%s</th>", mGroupName,tableName_lowerCamelCase,codeName,attr.getDescription()));
				 CodeHelper.addCodeLine(html_thbodyList, String.format("<td th:text=\"#{modelObj?.%s}\"></td>", codeName));
			 }
		 }
	    
		 String html= "<div xmlns:th=\"http://www.thymeleaf.org\" xmlns:shiro=\"http://www.pollix.at/thymeleaf/shiro\">\n" + 
		 		"    <table class=\"table table-hover\" >\n" + 
		 		"        <thead>\n" + 
		 		"        <tr >\n" + 
		 		"            <th style=\"width:60px; min-width: 60px;\" th:text=\"#{i18n.serial}\"></th>\n" + 
		 		"            %s" + 
		 		"            <th th:text=\"#{i18n.actions}\">操作</th>\n" + 
		 		"        </tr>\n" + 
		 		"        </thead>\n" + 
		 		"        <tbody>\n" + 
		 		"\n" + 
		 		"        <tr th:each=\"modelObj,status: ${pagedata.page}\">\n" + 
		 		"            <td th:text=\"${pagedata.totalNum - pagedata.startOfPage - status.index }\"></td>\n" + 
		 		"           %s" + 
		 		"            <td >\n" + 
		 		"                <div class=\"dropdown\">\n" + 
		 		"                    <a class=\"btn-link dropdown-toggle\" data-toggle=\"dropdown\" ><th:block th:text=\"#{i18n.actions}\" /> <span\n" + 
		 		"                            class=\"caret\"></span></a>\n" + 
		 		"                    <ul class=\"dropdown-menu pull-right\" role=\"menu\">\n" + 
		 		"                        <li shiro:hasPermission=\"shop_category_edit\"><a class=\"btn-link\"  data-toggle=\"modal\" th:href=\"@{add(id=${modelObj.id })}\" th:text=\"#{i18n.edit}\">修改</a></li>\n" + 
		 		"                        <li shiro:hasPermission=\"shop_category_delete\" ><a class=\"btn-link\" th:onclick=\"deleteData('[[${modelObj.id}]]',[[${modelObj.status}]])\" th:text=\"#{i18n.delete}\" >删除</a></li>\n" + 
		 		"                    </ul>\n" + 
		 		"                </div>\n" + 
		 		"            </td>\n" + 
		 		"        </tr>\n" + 
		 		"\n" + 
		 		"        </tbody>\n" + 
		 		"    </table>\n" + 
		 		"\n" + 
		 		"\n" + 
		 		"    <ul class=\"pager\" id=\"pager\">\n" + 
		 		"    </ul>\n" + 
		 		"    <script type=\"text/javascript\" th:inline=\"javascript\">\n" + 
		 		"        <!--\n" + 
		 		"        $(function(){\n" + 
		 		"            jqPager({id:\"pager\",firstBt:/*[[#{sys.page.first}]]*/\"首页\",preBt:/*[[#{sys.page.previous}]]*/\"上一页\",nextBt:/*[[#{sys.page.next}]]*/\"下一页\",lastBt:/*[[#{sys.page.last}]]*/\"末页\",totalPages:[[${pagedata.totalPage}]],pageIndex:[[${pagedata.pageIndex}]],change:toPage});\n" + 
		 		"        });\n" + 
		 		"        //-->\n" + 
		 		"    </script>\n" + 
		 		"</div>";
		 sql.append(String.format(html, html_theadList.toString(),html_thbodyList.toString()));
		 

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    

}