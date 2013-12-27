package org.overture.codegen.merging;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AAddrEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AFloorUnaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.AMethodInstantiationExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APowerNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.AXorBoolBinaryExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.utils.GeneralUtils;

public class TemplateManager
{
	private HashMap<Class<? extends INode>, String> nodeTemplateFileNames;

	private TemplateStructure templateStructure;
	
	public TemplateManager(TemplateStructure templateStructure)
	{
		this.templateStructure = templateStructure;
		initNodeTemplateFileNames();
	}

	private void initNodeTemplateFileNames()
	{
		nodeTemplateFileNames = new HashMap<Class<? extends INode>, String>();

				
		// Declarations
		nodeTemplateFileNames.put(AClassDeclCG.class, templateStructure.DECL_PATH
				+ "Class");
		
		nodeTemplateFileNames.put(ARecordDeclCG.class, templateStructure.DECL_PATH
				+ "Record");
		
		nodeTemplateFileNames.put(AFieldDeclCG.class, templateStructure.DECL_PATH + "Field");
		
		nodeTemplateFileNames.put(AMethodDeclCG.class, templateStructure.DECL_PATH
				+ "Method");
		
		nodeTemplateFileNames.put(ALocalVarDeclCG.class, templateStructure.DECL_PATH + "LocalVar");
		
		// Local declarations

		nodeTemplateFileNames.put(AFormalParamLocalDeclCG.class, templateStructure.LOCAL_DECLS_PATH + "FormalParam");
		
		// Type
		nodeTemplateFileNames.put(AClassTypeCG.class, templateStructure.TYPE_PATH + "ClassType");//TODO: Rename to Class
		
		nodeTemplateFileNames.put(ARecordTypeCG.class, templateStructure.TYPE_PATH + "Record");
		
		nodeTemplateFileNames.put(AObjectTypeCG.class, templateStructure.TYPE_PATH + "Object");
		
		nodeTemplateFileNames.put(AVoidTypeCG.class, templateStructure.TYPE_PATH + "Void");
		
		nodeTemplateFileNames.put(AStringTypeCG.class, templateStructure.TYPE_PATH + "String");
		
		nodeTemplateFileNames.put(ATemplateTypeCG.class, templateStructure.TYPE_PATH + "Template");
		
		nodeTemplateFileNames.put(ATupleTypeCG.class, templateStructure.TYPE_PATH + "Tuple");
		
		//Basic type wrappers
		
		nodeTemplateFileNames.put(AIntBasicTypeWrappersTypeCG.class, templateStructure.BASIC_TYPE_WRAPPERS_PATH
				+ "Integer");

		nodeTemplateFileNames.put(ARealBasicTypeWrappersTypeCG.class, templateStructure.BASIC_TYPE_WRAPPERS_PATH
				+ "Real");

		nodeTemplateFileNames.put(ABoolBasicTypeWrappersTypeCG.class, templateStructure.BASIC_TYPE_WRAPPERS_PATH
				+ "Bool");
		
		nodeTemplateFileNames.put(ACharBasicTypeWrappersTypeCG.class, templateStructure.BASIC_TYPE_WRAPPERS_PATH
				+ "Char");
		
		// Set types
		
		nodeTemplateFileNames.put(ASetSetTypeCG.class, templateStructure.SET_TYPE_PATH + "Set");
		
		// Seq types
		
		nodeTemplateFileNames.put(ASeqSeqTypeCG.class, templateStructure.SEQ_TYPE_PATH + "Seq");
		
		// Basic types
		
		nodeTemplateFileNames.put(ABoolBasicTypeCG.class, templateStructure.BASIC_TYPE_PATH
				+ "Bool");

		nodeTemplateFileNames.put(ACharBasicTypeCG.class, templateStructure.BASIC_TYPE_PATH
				+ "Char");
		
		// Basic numeric types
		nodeTemplateFileNames.put(AIntNumericBasicTypeCG.class, templateStructure.BASIC_TYPE_PATH
				+ "Integer");
		nodeTemplateFileNames.put(ARealNumericBasicTypeCG.class, templateStructure.BASIC_TYPE_PATH
				+ "Real");

		// Statements
		nodeTemplateFileNames.put(AIfStmCG.class, templateStructure.STM_PATH + "If");

		nodeTemplateFileNames.put(AReturnStmCG.class, templateStructure.STM_PATH + "Return");
		
		nodeTemplateFileNames.put(ASkipStmCG.class, templateStructure.STM_PATH + "Skip");

		nodeTemplateFileNames.put(ALetDefStmCG.class, templateStructure.STM_PATH + "LetDef");
		
		nodeTemplateFileNames.put(AAssignmentStmCG.class, templateStructure.STM_PATH + "Assignment");
		
		nodeTemplateFileNames.put(ABlockStmCG.class, templateStructure.STM_PATH + "Block");
		
		nodeTemplateFileNames.put(ACallStmCG.class, templateStructure.STM_PATH + "Call");
		
		nodeTemplateFileNames.put(ANotImplementedStmCG.class, templateStructure.STM_PATH + "NotImplemented");
		
		// Expressions
		
		nodeTemplateFileNames.put(AApplyExpCG.class, templateStructure.EXPS_PATH + "Apply");
		
		nodeTemplateFileNames.put(AFieldExpCG.class, templateStructure.EXPS_PATH + "Field");
		
		nodeTemplateFileNames.put(ANewExpCG.class, templateStructure.EXPS_PATH + "New");
		
		nodeTemplateFileNames.put(AVariableExpCG.class, templateStructure.EXPS_PATH + "Variable");
		
		nodeTemplateFileNames.put(AExplicitVariableExpCG.class, templateStructure.EXPS_PATH + "ExplicitVariable");
		
		nodeTemplateFileNames.put(AInstanceofExpCG.class, templateStructure.EXPS_PATH + "InstanceOf");
		
		nodeTemplateFileNames.put(ASelfExpCG.class, templateStructure.EXPS_PATH + "Self");
		
		nodeTemplateFileNames.put(ANullExpCG.class, templateStructure.EXPS_PATH + "Null");
		
		nodeTemplateFileNames.put(ALetDefExpCG.class, templateStructure.EXPS_PATH + "LetDef");
		
		nodeTemplateFileNames.put(AMethodInstantiationExpCG.class, templateStructure.EXPS_PATH + "MethodInstantiation");
		
		nodeTemplateFileNames.put(ATupleExpCG.class, templateStructure.EXPS_PATH + "Tuple");
		
		nodeTemplateFileNames.put(AFieldNumberExpCG.class, templateStructure.EXPS_PATH + "FieldNumber");
		
		nodeTemplateFileNames.put(ATernaryIfExpCG.class, templateStructure.EXPS_PATH + "TernaryIf");
		
		// Unary expressions

		nodeTemplateFileNames.put(APlusUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(AMinusUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ACastUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH
				+ "Cast");

		nodeTemplateFileNames.put(AIsolationUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH
				+ "Isolation");
		
		nodeTemplateFileNames.put(ALenUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Len");
		
		nodeTemplateFileNames.put(AElemsUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Elems");
		
		nodeTemplateFileNames.put(AHeadUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Head");
		
		nodeTemplateFileNames.put(ATailUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Tail");
		
		nodeTemplateFileNames.put(AFloorUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Floor");
		
		nodeTemplateFileNames.put(AAbsUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Abs");
		
		nodeTemplateFileNames.put(ANotUnaryExpCG.class, templateStructure.UNARY_EXPS_PATH + "Not");

		// Binary expressions
		
		nodeTemplateFileNames.put(AAddrEqualsBinaryExpCG.class, templateStructure.BINARY_EXPS_PATH + "AddrEquals");

		nodeTemplateFileNames.put(AAddrNotEqualsBinaryExpCG.class, templateStructure.BINARY_EXPS_PATH + "AddrNotEquals");
		
		nodeTemplateFileNames.put(AEqualsBinaryExpCG.class, templateStructure.BINARY_EXPS_PATH + "Equals");
		
		nodeTemplateFileNames.put(ANotEqualsBinaryExpCG.class, templateStructure.BINARY_EXPS_PATH + "NotEquals");
		
		nodeTemplateFileNames.put(ASeqConcatBinaryExpCG.class, templateStructure.BINARY_EXPS_PATH + "SeqConcat");
		
		// Numeric binary expressions

		nodeTemplateFileNames.put(ATimesNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Mul");
		nodeTemplateFileNames.put(APlusNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Plus");
		nodeTemplateFileNames.put(ASubtractNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Minus");

		nodeTemplateFileNames.put(ADivideNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Divide");

		nodeTemplateFileNames.put(AGreaterEqualNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "GreaterEqual");

		nodeTemplateFileNames.put(AGreaterNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Greater");

		nodeTemplateFileNames.put(ALessEqualNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "LessEqual");

		nodeTemplateFileNames.put(ALessNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Less");
		
		nodeTemplateFileNames.put(APowerNumericBinaryExpCG.class, templateStructure.NUMERIC_BINARY_EXPS_PATH
				+ "Power");

		// Connective binary expressions

		nodeTemplateFileNames.put(AOrBoolBinaryExpCG.class, templateStructure.BOOL_BINARY_EXPS_PATH
				+ "Or");
		
		nodeTemplateFileNames.put(AAndBoolBinaryExpCG.class, templateStructure.BOOL_BINARY_EXPS_PATH
				+ "And");
		
		nodeTemplateFileNames.put(AXorBoolBinaryExpCG.class, templateStructure.BOOL_BINARY_EXPS_PATH
				+ "Xor");
		
		// Literal expressions

		nodeTemplateFileNames.put(AIntLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "IntLiteral");
		nodeTemplateFileNames.put(ARealLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "RealLiteral");
		
		nodeTemplateFileNames.put(ABoolLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "BoolLiteral");
		
		nodeTemplateFileNames.put(ACharLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "CharLiteral");
		
		nodeTemplateFileNames.put(AStringLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "StringLiteral");
		
		nodeTemplateFileNames.put(AQuoteLiteralExpCG.class, templateStructure.EXPS_PATH
				+ "QuoteLiteral");
		
		//Seq expressions
		nodeTemplateFileNames.put(AEnumSeqExpCG.class, templateStructure.SEQ_EXPS_PATH
				+ "Enum");
		
		//State designators
		nodeTemplateFileNames.put(AFieldStateDesignatorCG.class, templateStructure.STATE_DESIGNATOR_PATH + "Field");
		nodeTemplateFileNames.put(AIdentifierStateDesignatorCG.class, templateStructure.STATE_DESIGNATOR_PATH + "Identifier");
		
		nodeTemplateFileNames.put(AInterfaceDeclCG.class, templateStructure.DECL_PATH + "Interface");
	}

	public Template getTemplate(Class<? extends INode> nodeClass)
	{
		try
		{
			StringBuffer buffer = GeneralUtils.readFromFile(getTemplateFileRelativePath(nodeClass));

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
				+ TemplateStructure.TEMPLATE_FILE_EXTENSION;
	}
}
