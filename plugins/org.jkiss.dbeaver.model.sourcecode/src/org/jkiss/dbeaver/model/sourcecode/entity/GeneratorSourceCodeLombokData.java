package org.jkiss.dbeaver.model.sourcecode.entity;

import java.util.ArrayList;
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
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;

public class GeneratorSourceCodeLombokData extends GeneratorSourceCode{
	 
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
		        	boolean genResult=codeExport.exportEntity(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
		        	boolean genResult=codeExport.exportEntity(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
	        		boolean genResult=codeExport.exportEntity(javaFileName(table,".java"), code.toString(),table.getDataSource().getName());
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
		 String mClassFullName=getClassFullName(mTableName_upperCamelCase, getRuleEntityLombokData());//类全称
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
		 String mClassFullName=getClassFullName(mTableName_upperCamelCase, getRuleEntityLombokData());//类全称
		 String mClassPackage=CodeHelper.getPackageName(mClassFullName);//类包名
		 String mClassSimpleName=CodeHelper.getClassSimpleName(mClassFullName);//类简称
		 String mClassSimpleName_lowerCamelCase=CodeHelper.toLowerCamelCase(mClassSimpleName);//类简称
		  
//		 String primaryKey=null;//主键
//		 DBSTableColumn primaryTableColumn=null;//主键
//		 String primaryKey_upperCamelCase=null;
//		 String primaryKey_lowerCamelCase=null;

		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)table.getAttributes(monitor);
//		 boolean useGeneratedKeys=false;
//		 Collection<? extends DBSTableIndex> indexes = table.getIndexes(monitor);
//       if (!CommonUtils.isEmpty(indexes)) {
//           for (DBSTableIndex index : indexes) {
//          	 if( index.isPrimary())
//          	 {
//          		 List<? extends DBSTableIndexColumn>  tableColumn=index.getAttributeReferences(monitor);
//          		 for(DBSTableIndexColumn column:tableColumn)
//          		 {
//          			 DBSTableColumn primaryColumn= column.getTableColumn();
//          			 if(primaryColumn!=null)
//          			 {
//          				 primaryTableColumn=primaryColumn;
//	            			 useGeneratedKeys=primaryColumn.isAutoGenerated();
//	            			 primaryKey=primaryColumn.getName();
//	            			 primaryKey_upperCamelCase=CodeHelper.toUpperCamelCase(primaryKey);
//	            			 primaryKey_lowerCamelCase=CodeHelper.toLowerCamelCase(primaryKey);
//          			 }
//          		 }
//          		 
//          	 }
//           }
//       }
       
		 CodeHelper.addCodeLine(sql,String.format("package %s;", mClassPackage));
		 CodeHelper.addCodeLine(sql, "import org.apache.ibatis.type.Alias;");
		 CodeHelper.addCodeLine(sql, "import java.io.Serializable;");
		 CodeHelper.addCodeLine(sql, "import lombok.Data;");
		 CodeHelper.importJavaPackage(sql, attrs);
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql,String.format("/**%s * %s%s * @author %s%s * @version v1.0%s */",lf,description,lf,getAuthor(),lf,lf));
		 CodeHelper.addCodeLine(sql, "@Alias(\""+mClassSimpleName_lowerCamelCase+"\")");
		 CodeHelper.addCodeLine(sql,String.format("@Data%spublic class %s implements Serializable {", lf,mClassSimpleName) );
		 sql.append(lf);
		 CodeHelper.addCodeLine(sql, "private static final long serialVersionUID = 1L;");
		 sql.append(lf);
		 
		 List<String> columnNameList=new ArrayList<String>();
		 List<String> entityNameList=new ArrayList<String>();
		 StringBuilder privateStr=new StringBuilder();
		 StringBuilder publicStr=new StringBuilder();
		 if(attrs!=null)
		 {
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 String codeName_upperCamelCase=CodeHelper.toUpperCamelCase(columnName);
				 columnNameList.add(columnName);
				 entityNameList.add(codeName);
				
				 CodeHelper.addCodeLine(privateStr,String.format("/** %s */", attr.getDescription()));
				 CodeHelper.addCodeLine(privateStr,String.format("private %s %s;",CodeHelper.columnType2JavaType(attr),codeName));
				  
			 }
		 }
		  
		 CodeHelper.addCodeLine(sql, privateStr.toString());
		 sql.append(lf);
		
		 CodeHelper.addCodeLine(sql, "}");
		 

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}
	    
	 
	

}