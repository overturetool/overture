package org.overture.codegen.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.expressions.AMinusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMulNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.constants.ITextConstants;

public class TemplateManager
{
	private static final String TEMPLATE_FILE_EXTENSION = ".vm";
	
	private static final String ROOT = "Templates" + ITextConstants.SEPARATOR_CHAR;
	private static final String NUMERIC_BINARY_EXP_PATH = ROOT + "NumericBinaryExp" + ITextConstants.SEPARATOR_CHAR;
	private static final String UNARY_EXP_PATH = ROOT + "UnaryExp" + ITextConstants.SEPARATOR_CHAR;
	private static final String BASIC_TYPE_PATH = ROOT + "BasicType" + ITextConstants.SEPARATOR_CHAR;
	
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
		
		fileNames.put(APlusUnaryExpCG.class, UNARY_EXP_PATH + "Plus");
		fileNames.put(AMinusUnaryExpCG.class, UNARY_EXP_PATH + "Minus");
		
		fileNames.put(AMulNumericBinaryExpCG.class, NUMERIC_BINARY_EXP_PATH + "Mul");
		fileNames.put(APlusNumericBinaryExpCG.class, NUMERIC_BINARY_EXP_PATH + "Plus");
		fileNames.put(AMinusNumericBinaryExpCG.class, NUMERIC_BINARY_EXP_PATH + "Minus");
		
		//Basic types
		fileNames.put(ACharBasicTypeCG.class, BASIC_TYPE_PATH + "Char");
		
		//Basic numeric types
		fileNames.put(AIntNumericBasicTypeCG.class, BASIC_TYPE_PATH + "Integer");
		fileNames.put(ARealNumericBasicTypeCG.class, BASIC_TYPE_PATH + "Real");		
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
