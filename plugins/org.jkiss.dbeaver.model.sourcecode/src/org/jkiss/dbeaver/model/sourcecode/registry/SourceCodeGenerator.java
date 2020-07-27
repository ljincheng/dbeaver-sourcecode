package org.jkiss.dbeaver.model.sourcecode.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithResult;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.DBSEntity;

public abstract class SourceCodeGenerator<OBJECT> extends DBRRunnableWithResult<String> {
    protected List<OBJECT> objects;
    
    private int generatorType=0;//生成代码处理类型：（0: console即直接显示, 1: file 生成文件保存）
    private boolean generatorResult=true;//生成代码处理结果:true成功，false失败
    private String rootPath;//代码存放根目录
    private String packageName;//package 全路径
    private boolean nameWithDo=true;//entity名称以Do后缀命名？
    private String entitySuffix;
    private String pageClassFullName;
    private String author;
    private String groupName;
    private String ruleEntity;
    private String ruleEntityLombokData;
    private String ruleDao;
    private String ruleComponent;
    private String ruleComponentImpl;
    private String ruleService;
    private String ruleServiceImpl;
    private String ruleController;
    private boolean camelCaseNames=true;//驼峰命名
    
   
    
    private Map<String, Object> generatorOptions = new LinkedHashMap<>();

    public void initGenerator(List<OBJECT> objects) {
        this.objects = objects;
    }

    public List<OBJECT> getObjects() {
        return objects;
    }
  

    public Object getGeneratorOption(String name) {
        return generatorOptions.get(name);
    }

    public void setGeneratorOption(String name, Object value) {
        if (value == null) {
            generatorOptions.remove(name);
        } else {
            generatorOptions.put(name, value);
        }
    }

   

    protected String getEntityName(DBSEntity entity) {
            return DBUtils.getQuotedIdentifier(entity);
    }

    protected void addOptions(Map<String, Object> options) {
        options.putAll(generatorOptions);
    }

    @Override
    public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
        StringBuilder code = new StringBuilder(100);
        try {
            for (OBJECT object : objects) {
            	generateCode(monitor, code, object);
            }
        } catch (DBException e) {
            throw new InvocationTargetException(e);
        }
        result = code.toString();
    }

    protected abstract void generateCode(DBRProgressMonitor monitor, StringBuilder code, OBJECT object)
        throws DBException;
    
//    public abstract String getFileRule();
    
    public void setGeneratorType(int type)
    {
    	this.generatorType=type;
    }
    
    public int getGeneratorType()
    {
    	return this.generatorType;
    }
    
    public boolean getGeneratorResult()
    {
    	return generatorResult;
    }
    
    public void setGeneratorResult(boolean result)
    {
    	this.generatorResult=result;
    }

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isNameWithDo() {
		return nameWithDo;
	}

	public void setNameWithDo(boolean nameWithDo) {
		this.nameWithDo = nameWithDo;
	}

	public boolean isCamelCaseNames() {
		return camelCaseNames;
	}

	public void setCamelCaseNames(boolean camelCaseNames) {
		this.camelCaseNames = camelCaseNames;
	}

	public String getEntitySuffix() {
		return entitySuffix;
	}

	public void setEntitySuffix(String entitySuffix) {
		this.entitySuffix = entitySuffix;
	}

	public String getPageClassFullName() {
		return pageClassFullName;
	}

	public void setPageClassFullName(String pageClassFullName) {
		this.pageClassFullName = pageClassFullName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
    
	
    public String getRuleEntity()
    {
    	return this.ruleEntity;
    }
    
    public void setRuleEntity(String value)
    {
    	this.ruleEntity=value;
    }

	public String getRuleDao() {
		return ruleDao;
	}

	public void setRuleDao(String ruleDao) {
		this.ruleDao = ruleDao;
	}

	public String getRuleComponent() {
		return ruleComponent;
	}

	public void setRuleComponent(String ruleComponent) {
		this.ruleComponent = ruleComponent;
	}

	public String getRuleComponentImpl() {
		return ruleComponentImpl;
	}

	public void setRuleComponentImpl(String ruleComponentImpl) {
		this.ruleComponentImpl = ruleComponentImpl;
	}

	public String getRuleService() {
		return ruleService;
	}

	public void setRuleService(String ruleService) {
		this.ruleService = ruleService;
	}

	public String getRuleServiceImpl() {
		return ruleServiceImpl;
	}

	public void setRuleServiceImpl(String ruleServiceImpl) {
		this.ruleServiceImpl = ruleServiceImpl;
	}

	public String getRuleController() {
		return ruleController;
	}

	public void setRuleController(String ruleController) {
		this.ruleController = ruleController;
	}
    
    
	public String getClassFullName(String tableName,String rule)
	{
		if(CodeHelper.isEmpty(rule))
		{
			return tableName;
		}
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("package_name", getPackageName());
		params.put("group_name", getGroupName());
		params.put("table_name", tableName);
		return CodeHelper.processTemplate(rule, params);
	}

	public String getRuleEntityLombokData() {
		return ruleEntityLombokData;
	}

	public void setRuleEntityLombokData(String ruleEntityLombokData) {
		this.ruleEntityLombokData = ruleEntityLombokData;
	} 

}