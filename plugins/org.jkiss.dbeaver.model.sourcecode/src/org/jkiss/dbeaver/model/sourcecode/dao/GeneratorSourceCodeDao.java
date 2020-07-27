package org.jkiss.dbeaver.model.sourcecode.dao;

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

public class GeneratorSourceCodeDao extends GeneratorSourceCode{
	 
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
		        	boolean genResult=codeExport.exportDao(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
		        	boolean genResult=codeExport.exportDao(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
	        		boolean genResult=codeExport.exportDao(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
		 String mClassFullName=getClassFullName(mTableName_upperCamelCase, getRuleDao());//类全称
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
//		 
//		 String lf = GeneralUtils.getDefaultLineSeparator();
//		 String packageName=getPackageName();
//		 String entitySuffix=CodeHelper.emptyString(getEntitySuffix(),true);
		 String mPage_FullName=getPageClassFullName();
//		 String tableName_upperCamelCase=CodeHelper.toUpperCamelCase(table.getName());
//		 String tableName_lowerCamelCase=CodeHelper.toLowerCamelCase(table.getName());
//		 String entityName=tableName_upperCamelCase+entitySuffix;//Entity Name
//		 String entityName_lowerCamelCase=tableName_lowerCamelCase+entitySuffix;//Entity Name
//		 String description=CodeHelper.emptyString(table.getDescription(),false);
		 String primaryKey=null;//主键
		 DBSTableColumn primaryTableColumn=null;//主键
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
	            			 mPrimaryKey_typeName=CodeHelper.toUpperCamelCase(primaryKey);
	            			 mPrimaryKey_paramName=CodeHelper.toLowerCamelCase(primaryKey);
          			 }
          		 }
          		 
          	 }
           }
       }
       
		 CodeHelper.addCodeLine(sql,String.format("package %s;", mDao_package));
		 CodeHelper.addCodeLine(sql, "import org.springframework.stereotype.Repository;");
		 CodeHelper.addCodeLine(sql, "import java.util.List;");
		 CodeHelper.addCodeLine(sql, "import java.util.Map;");
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format("import %s;",mEntity_fullName));
		 CodeHelper.importJavaPackage(sql, attrs);
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format("/**%s * %s%s * @author %s%s * @version v1.0%s */",lf,description,lf,getAuthor(),lf,lf));
		 CodeHelper.addCodeLine(sql,String.format("@Repository(\"%s\")", mDao_paramName));
		 CodeHelper.addCodeLine(sql,String.format("public interface %s {", mDao_typeName));
		 sql.append(lf);
		 
				
		 CodeHelper.addCodeLine(sql,"/**" + lf
		 		+"	 * 添加"+description + lf
		 		+"	 * @param "+mEntity_paramName + lf
		 		+"	 * @return" + lf
		 		+"	 */");
		 CodeHelper.addCodeLine(sql, String.format("public Integer insert(%s %s); ", mEntity_typeName,mEntity_paramName));
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,"/**" + lf
			 		+"	 * 获取"+description+"列表" + lf
			 		+"	 * @param selectItem" + lf
			 		+"	 * @return" + lf
			 		+"	 */");
		 CodeHelper.addCodeLine(sql,String.format( "public List<%s> queryList(Map<String,Object> selectItem);",mEntity_typeName));
		 sql.append(lf);
		 
		 if(!CodeHelper.isEmpty(mPage_FullName))
		 {
			 CodeHelper.addCodeLine(sql,"/**" + lf
				 		+"	 * 获取"+description+"分页列表" + lf
				 		+"	 * @param selectItem" + lf
				 		+"	 * @return" + lf
				 		+"	 */");
			 CodeHelper.addCodeLine(sql,String.format("public List<%s> queryListPage(Map<String,Object> selectItem);",mEntity_typeName));
			 sql.append(lf);
		 }
		 
		 if(!CodeHelper.isEmpty(primaryKey))
		 {
			 CodeHelper.addCodeLine(sql,"/**" + lf
				 		+"	 * 根据"+primaryKey+"修改"+description + lf
				 		+"	 * @param "+mEntity_paramName + lf
				 		+"	 * @return" + lf
				 		+"	 */");
			 CodeHelper.addCodeLine(sql, String.format("public Integer updateBy%s(%s %s);",mPrimaryKey_typeName,mEntity_typeName,mEntity_paramName));
			 sql.append(lf);
			 CodeHelper.addCodeLine(sql,"/**" + lf
				 		+"	 * 根据"+primaryKey+"删除"+description + lf
				 		+"	 * @param "+mPrimaryKey_paramName + lf
				 		+"	 * @return" + lf
				 		+"	 */");
			 CodeHelper.addCodeLine(sql,  String.format("public Integer deleteBy%s("+CodeHelper.columnType2JavaType(primaryTableColumn)+" %s); ",mPrimaryKey_typeName,mPrimaryKey_paramName));
			 sql.append(lf);
			 CodeHelper.addCodeLine(sql,"/**" + lf
				 		+"	 * 根据"+primaryKey+"查找"+description + lf
				 		+"	 * @param "+mPrimaryKey_paramName + lf
				 		+"	 * @return" + lf
				 		+"	 */");
			 CodeHelper.addCodeLine(sql, String.format("public %s findBy%s("+CodeHelper.columnType2JavaType(primaryTableColumn)+" %s); ",mEntity_typeName,mPrimaryKey_typeName,mPrimaryKey_paramName));
			 sql.append(lf);
		 }
			 
		  
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql, "}");
		 

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    
	 
	

}