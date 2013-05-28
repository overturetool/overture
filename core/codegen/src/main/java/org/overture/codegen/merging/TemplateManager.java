package org.overture.codegen.merging;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.AClassTypeDeclCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.constants.IText;

public class TemplateManager
{
	private HashMap<Class<? extends INode>, String> nodeTemplateFileNames;

	public TemplateManager()
	{
		initNodeTemplateFileNames();
	}

	private void initNodeTemplateFileNames()
	{
		nodeTemplateFileNames = new HashMap<Class<? extends INode>, String>();

				
		// Type declarations
		nodeTemplateFileNames.put(AClassTypeDeclCG.class, IText.TYPE_DECLS_PATH
				+ "Class");

		// Declarations
		
		nodeTemplateFileNames.put(AFieldDeclCG.class, IText.DECL_PATH + "Field");
		
		nodeTemplateFileNames.put(AMethodDeclCG.class, IText.DECL_PATH
				+ "Method");
		
		nodeTemplateFileNames.put(ALocalVarDeclCG.class, IText.DECL_PATH + "LocalVar");

		//nodeTemplateFileNames.put(AConstructorDeclCG.class, IText.DECL_PATH + "Constructor");
		
		// Local declarations

		//TODO: Template not currently used
		nodeTemplateFileNames.put(AFormalParamLocalDeclCG.class, IText.LOCAL_DECLS_PATH
				+ "FormalParam");

		// Type
		nodeTemplateFileNames.put(AClassTypeCG.class, IText.TYPE_PATH + "ClassType");
		
		nodeTemplateFileNames.put(AVoidTypeCG.class, IText.TYPE_PATH + "Void");

		// Statements
		nodeTemplateFileNames.put(AIfStmCG.class, IText.STM_PATH + "If");

		nodeTemplateFileNames.put(AReturnStmCG.class, IText.STM_PATH + "Return");
		
		nodeTemplateFileNames.put(ASkipStmCG.class, IText.STM_PATH + "Skip");

		nodeTemplateFileNames.put(ALetDefStmCG.class, IText.STM_PATH + "LetDef");
		
		nodeTemplateFileNames.put(AAssignmentStmCG.class, IText.STM_PATH + "Assignment");
		
		nodeTemplateFileNames.put(ABlockStmCG.class, IText.STM_PATH + "Block");
		
		// Expressions
		
		nodeTemplateFileNames.put(AApplyExpCG.class, IText.EXPS_PATH + "Apply");
		
		nodeTemplateFileNames.put(AFieldExpCG.class, IText.EXPS_PATH + "Field");
		
		nodeTemplateFileNames.put(ANewExpCG.class, IText.EXPS_PATH + "New");
		
		nodeTemplateFileNames.put(AVariableExpCG.class, IText.EXPS_PATH + "Variable");
		
		// Unary expressions

		nodeTemplateFileNames.put(APlusUnaryExpCG.class, IText.UNARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(AMinusUnaryExpCG.class, IText.UNARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ACastUnaryExpCG.class, IText.UNARY_EXPS_PATH
				+ "Cast");

		nodeTemplateFileNames.put(AIsolationUnaryExpCG.class, IText.UNARY_EXPS_PATH
				+ "Isolation");

		// Numeric binary expressions

		nodeTemplateFileNames.put(ATimesNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Mul");
		nodeTemplateFileNames.put(APlusNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(ASubtractNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ADivideNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Divide");

		nodeTemplateFileNames.put(AGreaterEqualNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "GreaterEqual");

		nodeTemplateFileNames.put(AGreaterNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Greater");

		nodeTemplateFileNames.put(ALessEqualNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "LessEqual");

		nodeTemplateFileNames.put(ALessNumericBinaryExpCG.class, IText.NUMERIC_BINARY_EXPS_PATH
				+ "Less");

		// Literal expressions

		nodeTemplateFileNames.put(AIntLiteralExpCG.class, IText.EXPS_PATH
				+ "IntLiteral");
		nodeTemplateFileNames.put(ARealLiteralExpCG.class, IText.EXPS_PATH
				+ "RealLiteral");
		nodeTemplateFileNames.put(ACharLiteralExpCG.class, IText.EXPS_PATH
				+ "CharLiteral");

		// Basic types
		nodeTemplateFileNames.put(ACharBasicTypeCG.class, IText.BASIC_TYPE_PATH
				+ "Char");

		// Basic numeric types
		nodeTemplateFileNames.put(AIntNumericBasicTypeCG.class, IText.BASIC_TYPE_PATH
				+ "Integer");
		nodeTemplateFileNames.put(ARealNumericBasicTypeCG.class, IText.BASIC_TYPE_PATH
				+ "Real");

		nodeTemplateFileNames.put(ABoolBasicTypeCG.class, IText.BASIC_TYPE_PATH
				+ "Bool");
		
		//State designators
		nodeTemplateFileNames.put(AFieldStateDesignatorCG.class, IText.STATE_DESIGNATOR_PATH + "Field");
		nodeTemplateFileNames.put(AIdentifierStateDesignatorCG.class, IText.STATE_DESIGNATOR_PATH + "Identifier");
	}

	public Template getTemplate(Class<? extends INode> nodeClass)
	{
		try
		{
			StringBuffer buffer = readFromFile(getTemplateFileRelativePath(nodeClass));

			if (buffer == null)
				return null;

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
		return nodeTemplateFileNames.get(nodeClass)
				+ IText.TEMPLATE_FILE_EXTENSION;
	}

	private StringBuffer readFromFile(String relativepath) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(relativepath);

		if (input == null)
			return null;

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
