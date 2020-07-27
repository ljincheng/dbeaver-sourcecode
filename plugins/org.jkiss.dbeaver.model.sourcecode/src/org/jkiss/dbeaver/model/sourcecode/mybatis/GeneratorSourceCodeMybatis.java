package org.jkiss.dbeaver.model.sourcecode.mybatis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPDataKind;
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
 * 生成Mybatis文件
 * @author ljc
 *
 */
public class GeneratorSourceCodeMybatis extends GeneratorSourceCode{
	 
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
		        	boolean genResult=codeExport.exportMybatis(xmlFileName(table,".xml"), code.toString(),table.getDataSource().getName());
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
		        	boolean genResult=codeExport.exportMybatis(xmlFileName(table,".xml"), code.toString(),table.getDataSource().getName());
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
	        		boolean genResult=codeExport.exportMybatis(xmlFileName(table,".xml"), code.toString(),table.getDataSource().getName());
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
	 
	 private String xmlFileName(DBSTable table,String extFileName)
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
		 
		 String mComponent_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponent());//ComponentImpl类全称
		 String mComponent_package=CodeHelper.getPackageName(mComponent_fullName);//ComponentImpl类包名
		 String mComponent_typeName=CodeHelper.getClassSimpleName(mComponent_fullName);//ComponentImpl类型名称
		 String mComponent_paramName=CodeHelper.toLowerCamelCase(mComponent_typeName);//ComponentImpl类变量
		 
		 String mComponentImpl_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponentImpl());//ComponentImpl类全称
		 String mComponentImpl_package=CodeHelper.getPackageName(mComponentImpl_fullName);//ComponentImpl类包名
		 String mComponentImpl_typeName=CodeHelper.getClassSimpleName(mComponentImpl_fullName);//ComponentImpl类型名称
		 String mComponentImpl_paramName=CodeHelper.toLowerCamelCase(mComponentImpl_typeName);//ComponentImpl类变量
		 
		 String pageClassFullName=getPageClassFullName();
		 String packageName=getPackageName();
		 String tableName_upperCamelCase=CodeHelper.toUpperCamelCase(tableName);
		 String tableName_lowerCamelCase=CodeHelper.toLowerCamelCase(tableName);
		 
		 CodeHelper.addCodeLine(sql, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		 CodeHelper.addCodeLine(sql, "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"  \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");

		 StringBuilder whereClipCode=new StringBuilder(100);
		 StringBuilder updateParamCode=new StringBuilder();
		   
		 String primaryKey=null;//主键
		 DBSTableColumn primaryTableColumn=null;//主键
		 String primaryKey_upperCamelCase=null;
		 String primaryKey_lowerCamelCase=null;
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
		 
		 CodeHelper.addCodeLine(sql,String.format("<mapper namespace=\"%s\">",mDao_fullName) );
		 CodeHelper.addCodeLine(sql,String.format("<resultMap id=\"BaseResultMap\" type=\"%s\" >", mEntity_paramName ));
		 
		 
		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)table.getAttributes(monitor);
		 List<String> columnNameList=new ArrayList<String>();
		 List<String> entityNameList=new ArrayList<String>();
		 if(attrs!=null)
		 {
			 String splitStr=null;
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 columnNameList.add(columnName);
				 entityNameList.add(codeName);
				 
				
				 CodeHelper.addCodeLine(sql, "<result column=\""+columnName+"\" property=\""+codeName+"\" />");
				  
				 if(primaryKey!=null)
				 {
					 if(!columnName.equals(primaryKey))
					 {
						 if(updateParamCode.length()>0)
						 {
							 updateParamCode.append(",");
						 }
						 updateParamCode.append(columnName).append("=").append("#{").append(codeName).append("}");
					 }
				 }
				 
				 if(attr.getDataKind().compareTo(DBPDataKind.STRING)==0)
				 {
					 CodeHelper.addCodeLine(whereClipCode," <if test=\""+codeName+"!= null and "+codeName+" != '' \"> and "+attr.getName()+" = #{"+codeName+"} </if>");
				 }else {
					 CodeHelper.addCodeLine(whereClipCode," <if test=\""+codeName+"!= null \"> and "+attr.getName()+" = #{"+codeName+"} </if>");
				 }
				  
			 }
		 }
		 CodeHelper.addCodeLine(sql, "		</resultMap>");
		
		 
		 // 新增一行
		 String insertSQL=null;
		 if(useGeneratedKeys && !CodeHelper.isEmpty(primaryKey))
		 {
			 
			 insertSQL="<insert id=\"insert\" parameterType=\""+mEntity_paramName+"\"  keyProperty=\""+primaryKey+"\"  useGeneratedKeys=\"true\" >"+lf
				 		+ "INSERT INTO "+tableName+"("+CodeHelper.joinStrings("," ,new String[]{primaryKey}, columnNameList)+") VALUES (#{"+CodeHelper.joinStrings("},#{" ,new String[]{primaryKey}, entityNameList)+"})"+lf
				 				+ "</insert>";
		 }else{
			 insertSQL="<insert id=\"insert\" parameterType=\""+mEntity_paramName+"\"   >"+lf
						+ "INSERT INTO "+tableName+"("+CommonUtils.joinStrings("," , columnNameList)+") VALUES (#{"+CommonUtils.joinStrings("},#{" , entityNameList)+"})"+lf
				 				+ "</insert>";
		 }
		 CodeHelper.addCodeLine(sql, insertSQL);
		 
		 // queryList
		 String queryListSQL="<select id=\"queryList\" parameterType=\"map\" resultType=\""+mEntity_paramName+"\">"+lf
		 		+ "SELECT * FROM  "+tableName+"  WHERE 1=1 "+lf
		 		+whereClipCode.toString()
		 		+ "</select>";
		 CodeHelper.addCodeLine(sql, queryListSQL);
		 
		 // queryListPage 分页查询
		 if(!CodeHelper.isEmpty(pageClassFullName))
		 {
			 String queryListPageSQL="<select id=\"queryListPage\" parameterType=\"map\" resultType=\""+mEntity_paramName+"\">"+lf
			 		+ "SELECT * FROM  "+tableName+" WHERE 1=1 "+lf
			 		+whereClipCode.toString()
			 		+ "</select>";
			 CodeHelper.addCodeLine(sql, queryListPageSQL);
		 }
		 
		 
		 if(!CodeHelper.isEmpty(primaryKey))
		 {
			 // update
			 String updateSQL=String.format("<update id=\"updateBy%s\" parameterType=\"%s\"> %s update %s set %s where %s = #{%s} %s </update>", primaryKey_upperCamelCase,mEntity_paramName,lf
					 ,tableName,updateParamCode.toString(),primaryKey,primaryKey_lowerCamelCase,lf);
			 		 
			 CodeHelper.addCodeLine(sql, updateSQL);
			 
			 // delete
			 String deleteSQL=String.format("<delete id=\"deleteBy%s\" parameterType=\"%s\">%s DELETE FROM %s WHERE %s = #{%s}%s </delete>", primaryKey_upperCamelCase,CodeHelper.columnType2JavaType(primaryTableColumn),lf
					 ,tableName,primaryKey,primaryKey_lowerCamelCase,lf);
			 CodeHelper.addCodeLine(sql, deleteSQL);
			 
			 // find
			 String findSQL=String.format("<select id=\"findBy%s\" parameterType=\"%s\" resultType=\"%s\">%s SELECT * FROM %s WHERE %s = #{%s}%s </select>", primaryKey_upperCamelCase,CodeHelper.columnType2JavaType(primaryTableColumn),mEntity_paramName,lf
					 ,tableName,primaryKey,primaryKey_lowerCamelCase,lf);
			 CodeHelper.addCodeLine(sql, findSQL);
		 }
		 
		 CodeHelper.addCodeLine(sql, "</mapper>");
		 
 
	 }


 


	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    
	 
	

}
