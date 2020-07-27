package org.jkiss.dbeaver.model.sourcecode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

public class GeneratorSourceCodeExport {
	
	private String outputFolder;
	private final String rootFolderName="src";
	
	private boolean useCamelCase=true;
	
	public GeneratorSourceCodeExport() {
		// TODO Auto-generated constructor stub
	}
	
	public GeneratorSourceCodeExport(String outputFolder)
	{
		this.outputFolder=outputFolder;
	}
	 
	public void setOutputFolder(String outputFolder) {
		this.outputFolder=outputFolder;
	}
	
	public String getOutputFolder()
	{
		return this.outputFolder;
	}
	

	public boolean getUseCamelCase()
	{
		return useCamelCase;
	}
	
	public void setUseCamelCase(boolean value)
	{
		this.useCamelCase=value;
	}
	

	public boolean saveFile(String filePath,String content)
	{
		if(CodeHelper.isEmpty(outputFolder))
		{
			DBWorkbench.getPlatformUI().showMessageBox(UIMessages.dbeaver_generate_sourcecode_msg_selectOutPutFolder, UIMessages.dbeaver_generate_sourcecode_msg_selectOutPutFolder_detailTip, true);
			
			return false;
		}
		try {
			String rootPath=null;
			if(!outputFolder.endsWith("/") && !outputFolder.endsWith("\\"))
			{
				rootPath=outputFolder.replaceAll("/", File.separator)+File.separator;
			}else {
				rootPath=outputFolder.replaceAll("/", File.separator);
			}
			String systemFilePath=filePath.replace("/", File.separator);
			String fileDir=systemFilePath.substring(0, filePath.lastIndexOf(File.separator));
			String systemFileDir=rootPath+ fileDir;
			String systemFile=rootPath+ systemFilePath;
			File codeFileDir=new File(systemFileDir);
			if(!codeFileDir.exists())
			{
				codeFileDir.mkdirs();
			}
			File codeFile=new File(systemFile);
			BufferedWriter bw = new BufferedWriter(new FileWriter(codeFile));
			bw.write(content);
			bw.close();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			DBWorkbench.getPlatformUI().showError(UIMessages.dbeaver_generate_sourcecode_error,UIMessages.dbeaver_generate_sourcecode_generatorSourceCodeError,e);
			return false;
		}
		
	}
	
	public boolean exportMybatis(String filePath,String content,String database)
	{
			if(!CodeHelper.isEmpty(content))
			{
				String mybatisFilePath=rootFolderName+"/"+database+"/mapper/"+filePath;
				return saveFile(mybatisFilePath,content);
			}
			return false;
	}
	public boolean exportEntity(String filePath,String content,String database)
	{
			if(!CodeHelper.isEmpty(content))
			{
				String mybatisFilePath=rootFolderName+"/"+database+"/entity/"+filePath;
				return saveFile(mybatisFilePath,content);
			}
			return false;
	}
	public boolean exportDao(String filePath,String content,String database)
	{
		if(!CodeHelper.isEmpty(content))
		{
			String mybatisFilePath=rootFolderName+"/"+database+"/dao/"+filePath;
			return saveFile(mybatisFilePath,content);
		}
		return false;
	}
	public boolean exportComponent(String filePath,String content,String database)
	{
		if(!CodeHelper.isEmpty(content))
		{
			String mybatisFilePath=rootFolderName+"/"+database+"/component/"+filePath;
			return saveFile(mybatisFilePath,content);
		}
		return false;
	}
	public boolean exportService(String filePath,String content,String database)
	{
		if(!CodeHelper.isEmpty(content))
		{
			String mybatisFilePath=rootFolderName+"/"+database+"/service/"+filePath;
			return saveFile(mybatisFilePath,content);
		}
		return false;
	}
	public boolean exportController(String filePath,String content,String database)
	{
		if(!CodeHelper.isEmpty(content))
		{
			String mybatisFilePath=rootFolderName+"/"+database+"/controller/"+filePath;
			return saveFile(mybatisFilePath,content);
		}
		return false;
	}

}
