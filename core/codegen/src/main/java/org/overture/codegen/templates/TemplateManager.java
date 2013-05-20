package org.overture.codegen.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.commons.collections.map.HashedMap;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.expressions.AMinusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.AMinusCGUnaryExp;
import org.overture.codegen.cgast.expressions.AMulCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGUnaryExp;
import org.overture.codegen.constants.ITextConstants;

public class TemplateManager
{
	private static final String TEMPLATE_FILE_EXTENSION = ".vm";
	
	private static final String ROOT = "Templates" + ITextConstants.PATH_SEPARATOR;
	private static final String NUMERIC_BINARY_EXP_PATH = ROOT + "NumericBinaryExp" + ITextConstants.PATH_SEPARATOR;
	private static final String UNARY_EXP_PATH = ROOT + "UnaryExp" + ITextConstants.PATH_SEPARATOR;
	
	private HashMap<Class<? extends INode>, String> fileNames;
	
	public TemplateManager()
	{
		initFileNames();
	}
	
	public void initFileNames()
	{
		fileNames = new HashMap<Class<? extends INode>, String>();
		
		fileNames.put(AClassCG.class, ROOT + "Class");
		fileNames.put(AFieldCG.class, ROOT + "Field");
		
		fileNames.put(APlusCGUnaryExp.class, UNARY_EXP_PATH + "Plus");
		fileNames.put(AMinusCGUnaryExp.class, UNARY_EXP_PATH + "Minus");
		
		fileNames.put(AMulCGNumericBinaryExp.class, NUMERIC_BINARY_EXP_PATH + "Mul");
		fileNames.put(APlusCGNumericBinaryExp.class, NUMERIC_BINARY_EXP_PATH + "Plus");
		fileNames.put(AMinusCGNumericBinaryExp.class, NUMERIC_BINARY_EXP_PATH + "Minus");
		
		
	}
	
	public Template getTemplate(Class<? extends INode> nodeClass)
	{
		Template template = null;

		try
		{
			StringBuffer buffer = readFromFile(getTemplateFileRelativePath(nodeClass));
			RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
			StringReader reader = new StringReader(buffer.toString());
			SimpleNode node = runtimeServices.parse(reader, "Template name");

			template = new Template();
			template.setRuntimeServices(runtimeServices);
			template.setData(node);
			template.initDocument();

		} catch (Exception e)
		{
		}

		return template;
	}
	
	private String getTemplateFileRelativePath(Class<? extends INode> nodeClass)
	{
		return fileNames.get(nodeClass) + TEMPLATE_FILE_EXTENSION;
	}
	
	private StringBuffer readFromFile(String relativepath)
			throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(relativepath);
		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			data.append((char) c);
		}
		input.close();

		return data;
	}
}
