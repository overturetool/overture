package org.overture.codegen.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.constants.ITextConstants;

public class TemplateManager
{
	private static final String TEMPLATE_FILE_EXTENSION = ".vm";

	private static final String ROOT = "Templates"
			+ ITextConstants.SEPARATOR_CHAR;

	private static final String EXPS_PATH = ROOT + "Expressions"
			+ ITextConstants.SEPARATOR_CHAR;
	private static final String BINARY_EXPS_PATH = EXPS_PATH + "Binary"
			+ ITextConstants.SEPARATOR_CHAR;
	private static final String NUMERIC_BINARY_EXPS_PATH = BINARY_EXPS_PATH
			+ "Numeric" + ITextConstants.SEPARATOR_CHAR;
	private static final String UNARY_EXPS_PATH = EXPS_PATH + "Unary"
			+ ITextConstants.SEPARATOR_CHAR;

	private static final String TYPE_PATH = ROOT + "Types"
			+ ITextConstants.SEPARATOR_CHAR;
	private static final String BASIC_TYPE_PATH = TYPE_PATH + "BasicType"
			+ ITextConstants.SEPARATOR_CHAR;

	private HashMap<Class<? extends INode>, String> nodeTemplateFileNames;

	public TemplateManager()
	{
		initNodeTemplateFileNames();
	}

	private void initNodeTemplateFileNames()
	{
		nodeTemplateFileNames = new HashMap<Class<? extends INode>, String>();

		nodeTemplateFileNames.put(AClassCG.class, ROOT + "Class");
		nodeTemplateFileNames.put(AFieldCG.class, ROOT + "Field");

		nodeTemplateFileNames.put(APlusUnaryExpCG.class, UNARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(AMinusUnaryExpCG.class, UNARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ACastUnaryExpCG.class, UNARY_EXPS_PATH
				+ "Cast");

		nodeTemplateFileNames.put(AIsolationUnaryExpCG.class, UNARY_EXPS_PATH
				+ "Isolation");
		
		nodeTemplateFileNames.put(ATimesNumericBinaryExpCG.class, NUMERIC_BINARY_EXPS_PATH
				+ "Mul");
		nodeTemplateFileNames.put(APlusNumericBinaryExpCG.class, NUMERIC_BINARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(ASubtractNumericBinaryExpCG.class, NUMERIC_BINARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ADivideNumericBinaryExpCG.class, NUMERIC_BINARY_EXPS_PATH
				+ "Divide");

		// Basic types
		nodeTemplateFileNames.put(ACharBasicTypeCG.class, BASIC_TYPE_PATH
				+ "Char");

		// Basic numeric types
		nodeTemplateFileNames.put(AIntNumericBasicTypeCG.class, BASIC_TYPE_PATH
				+ "Integer");
		nodeTemplateFileNames.put(ARealNumericBasicTypeCG.class, BASIC_TYPE_PATH
				+ "Real");
	}

	public Template getTemplate(Class<? extends INode> nodeClass)
	{
		try
		{
			StringBuffer buffer = readFromFile(getTemplateFileRelativePath(nodeClass));
			return constructTemplate(buffer);

		} catch (IOException e)
		{
			return null;
		}

	}

	private Template constructTemplate(StringBuffer buffer)
	{

		Template template = new Template();
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(buffer.toString());

		try
		{
			SimpleNode simpleNode = runtimeServices.parse(reader, "Template name");
			template.setRuntimeServices(runtimeServices);
			template.setData(simpleNode);
			template.initDocument();

			return template;

		} catch (ParseException e)
		{
			return null;
		}
	}

	private String getTemplateFileRelativePath(Class<? extends INode> nodeClass)
	{
		return nodeTemplateFileNames.get(nodeClass) + TEMPLATE_FILE_EXTENSION;
	}

	private StringBuffer readFromFile(String relativepath) throws IOException
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
