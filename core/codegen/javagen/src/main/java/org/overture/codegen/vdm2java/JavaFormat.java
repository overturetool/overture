/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.vdm2java;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.ast.lex.Dialect;
import org.overture.ast.types.PType;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AHistoryExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASeqToStringUnaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringToSeqUnaryExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SLiteralExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.statements.AStartStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AInterfaceTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRAnalysis;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Settings;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class JavaFormat
{
	public static final String TYPE_DECL_PACKAGE_SUFFIX = "types";
	public static final String CLASS_EXTENSION = ".class";
	public static final String UTILS_FILE = "Utils";
	public static final String SEQ_UTIL_FILE = "SeqUtil";
	public static final String SET_UTIL_FILE = "SetUtil";
	public static final String MAP_UTIL_FILE = "MapUtil";

	private IRInfo info;

	private FuncValAssistant funcValAssist;
	private MergeVisitor mergeVisitor;
	private JavaValueSemantics valueSemantics;
	private JavaFormatAssistant javaFormatAssistant;
	private JavaRecordCreator recCreator;
	private JavaVarPrefixManager varPrefixManager;
	
	public JavaFormat(JavaVarPrefixManager varPrefixManager,
			TemplateStructure templateStructure, IRInfo info)
	{
		this.varPrefixManager = varPrefixManager;
		this.valueSemantics = new JavaValueSemantics(this);
		this.recCreator = new JavaRecordCreator(this);
		TemplateCallable[] templateCallables = TemplateCallableManager.constructTemplateCallables(this, IRAnalysis.class, valueSemantics, recCreator);
		this.mergeVisitor = new MergeVisitor(templateStructure, templateCallables);
		this.funcValAssist = null;
		this.info = info;
		this.javaFormatAssistant = new JavaFormatAssistant(this.info);
	}
	
	public JavaValueSemantics getValueSemantics()
	{
		return valueSemantics;
	}
	
	public void setValueSemantics(JavaValueSemantics valueSemantics)
	{
		this.valueSemantics = valueSemantics;
	}

	public JavaRecordCreator getRecCreator()
	{
		return recCreator;
	}
	
	public JavaFormatAssistant getJavaFormatAssistant()
	{
		return javaFormatAssistant;
	}

	public String getJavaNumber()
	{
		return "Number";
	}

	public IRInfo getIrInfo()
	{
		return info;
	}

	public void setFunctionValueAssistant(
			FuncValAssistant functionValueAssistant)
	{
		this.funcValAssist = functionValueAssistant;
	}

	public void clearFunctionValueAssistant()
	{
		this.funcValAssist = null;
	}

	public void setJavaSettings(JavaSettings javaSettings)
	{
		valueSemantics.setJavaSettings(javaSettings);
	}
	
	public JavaSettings getJavaSettings()
	{
		return valueSemantics.getJavaSettings();
	}

	public void init()
	{
		mergeVisitor.init();
	}
	
	public void clear()
	{
		init();
		valueSemantics.clear();
	}

	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}

	public String format(AMethodTypeCG methodType) throws AnalysisException
	{
		final String OBJ = "Object";

		if (funcValAssist == null)
		{
			return OBJ;
		}

		AInterfaceDeclCG methodTypeInterface = funcValAssist.findInterface(methodType);

		if (methodTypeInterface == null)
		{
			return OBJ; // Should not happen
		}

		AInterfaceTypeCG methodClass = new AInterfaceTypeCG();
		methodClass.setName(methodTypeInterface.getName());

		LinkedList<STypeCG> params = methodType.getParams();

		for (STypeCG param : params)
		{
			methodClass.getTypes().add(param.clone());
		}

		methodClass.getTypes().add(methodType.getResult().clone());

		return methodClass != null ? format(methodClass) : OBJ;
	}

	public String format(INode node) throws AnalysisException
	{
		return format(node, false);
	}

	public String formatIgnoreContext(INode node) throws AnalysisException
	{
		return format(node, true);
	}

	private String format(INode node, boolean ignoreContext)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString() + getNumberDereference(node, ignoreContext);
	}

	private String findNumberDereferenceCall(STypeCG type)
	{
		if (type == null || type.parent() instanceof AHistoryExpCG)
		{
			return "";
		}
		
		final String DOUBLE_VALUE = ".doubleValue()";
		final String LONG_VALUE = ".longValue()";

		if (info.getAssistantManager().getTypeAssistant().isInt(type))
		{
			return LONG_VALUE; 
		} else if (info.getAssistantManager().getTypeAssistant().isRealOrRat(type))
		{
			return DOUBLE_VALUE;
		} else
		{
			PTypeAssistantTC typeAssistant = info.getTcFactory().createPTypeAssistant();
			SourceNode sourceNode = type.getSourceNode();

			if (sourceNode != null
					&& !(sourceNode.getVdmNode() instanceof PType))
			{
				PType vdmType = (PType) sourceNode.getVdmNode();

				if (typeAssistant.isNumeric(vdmType))
				{
					return DOUBLE_VALUE;
				}
			}

			return "";
		}
	}

	public static boolean isMapSeq(SStateDesignatorCG stateDesignator)
	{
		return stateDesignator instanceof AMapSeqStateDesignatorCG;
	}

	private String getNumberDereference(INode node, boolean ignoreContext)
	{
		if (ignoreContext && node instanceof SExpCG)
		{
			SExpCG exp = (SExpCG) node;
			STypeCG type = exp.getType();

			if (isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}

		INode parent = node.parent();

		if (parent instanceof SNumericBinaryExpCG
				|| parent instanceof AAbsUnaryExpCG
				|| parent instanceof AMinusUnaryExpCG
				|| parent instanceof APlusUnaryExpCG)
		{
			SExpCG exp = (SExpCG) node;
			STypeCG type = exp.getType();

			if (isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}

		// No dereference is needed
		return "";
	}

	private static boolean isNumberDereferenceCandidate(SExpCG node)
	{
		boolean fitsCategory = !(node instanceof SNumericBinaryExpCG)
				&& !(node instanceof SLiteralExpCG)
				&& !(node instanceof AIsolationUnaryExpCG)
				&& !(node instanceof SUnaryExpCG);

		boolean isException = node instanceof AHeadUnaryExpCG
				|| node instanceof AQuoteLiteralExpCG
				|| node instanceof ACastUnaryExpCG;

		return fitsCategory || isException;
	}

	public String formatName(INode node) throws AnalysisException
	{
		if (node instanceof ANewExpCG)
		{
			ANewExpCG newExp = (ANewExpCG) node;

			return formatTypeName(node, newExp.getName());
		} else if (node instanceof ARecordTypeCG)
		{
			ARecordTypeCG record = (ARecordTypeCG) node;
			ATypeNameCG typeName = record.getName();

			return formatTypeName(node, typeName);
		}

		throw new AnalysisException("Unexpected node in formatName: "
				+ node.getClass().getName());
	}

	public String formatTypeName(INode node, ATypeNameCG typeName)
	{
		// Type names are also used for quotes, which do not have a defining class.
		if(typeName.getDefiningClass() != null && !getJavaSettings().genRecsAsInnerClasses())
		{
			String typeNameStr = "";
			
			if(JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
			{
				typeNameStr += getJavaSettings().getJavaRootPackage() + ".";
			}
			
			typeNameStr += typeName.getDefiningClass() + TYPE_DECL_PACKAGE_SUFFIX + ".";
			
			typeNameStr += typeName.getName();
			
			return typeNameStr;
		}
		
		ADefaultClassDeclCG classDef = node.getAncestor(ADefaultClassDeclCG.class);

		String definingClass = typeName.getDefiningClass() != null
				&& classDef != null
				&& !classDef.getName().equals(typeName.getDefiningClass()) ? typeName.getDefiningClass()
				+ "."
				: "";

		return definingClass + typeName.getName();
	}

	public String format(SExpCG exp, boolean leftChild)
			throws AnalysisException
	{
		String formattedExp = format(exp);

		JavaPrecedence precedence = new JavaPrecedence();

		INode parent = exp.parent();

		if (!(parent instanceof SExpCG))
		{
			return formattedExp;
		}

		boolean isolate = precedence.mustIsolate((SExpCG) parent, exp, leftChild);

		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}

	public String formatUnary(SExpCG exp) throws AnalysisException
	{
		return format(exp, false);
	}

	public String formatNotUnary(SExpCG exp) throws AnalysisException
	{
		String formattedExp = format(exp, false);

		boolean doNotWrap = exp instanceof ABoolLiteralExpCG
				|| formattedExp.startsWith("(") && formattedExp.endsWith(")");

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}

	public String formatTemplateTypes(List<STypeCG> types)
			throws AnalysisException
	{
		if (types.isEmpty())
		{
			return "";
		}

		return "<" + formattedTypes(types, "") + ">";
	}

	private String formattedTypes(List<STypeCG> types, String typePostFix)
			throws AnalysisException
	{
		STypeCG firstType = types.get(0);

		if (info.getAssistantManager().getTypeAssistant().isBasicType(firstType))
		{
			firstType = info.getAssistantManager().getTypeAssistant().getWrapperType((SBasicTypeCG) firstType);
		}

		StringWriter writer = new StringWriter();
		writer.append(format(firstType) + typePostFix);

		for (int i = 1; i < types.size(); i++)
		{
			STypeCG currentType = types.get(i);

			if (info.getAssistantManager().getTypeAssistant().isBasicType(currentType))
			{
				currentType = info.getAssistantManager().getTypeAssistant().getWrapperType((SBasicTypeCG) currentType);
			}

			writer.append(", " + format(currentType) + typePostFix);
		}

		String result = writer.toString();
		
		return result;
	}

	public String formatTypeArg(STypeCG type) throws AnalysisException
	{
		if(type == null)
		{
			return null;
		}
		else
		{
			List<STypeCG> types = new LinkedList<STypeCG>();
			types.add(type);
			
			return formattedTypes(types, CLASS_EXTENSION);
		}
	}
	
	public String formatTypeArgs(ATupleTypeCG tupleType) throws AnalysisException
	{
		return formatTypeArgs(tupleType.getTypes());
	}
	
	public String formatTypeArgs(List<STypeCG> types) throws AnalysisException
	{
		if (types.isEmpty())
		{
			return "";
		}

		return formattedTypes(types, CLASS_EXTENSION);
	}

	public String formatEqualsBinaryExp(AEqualsBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG leftNodeType = node.getLeft().getType();

		if (leftNodeType instanceof SSeqTypeCG || leftNodeType instanceof SSetTypeCG || leftNodeType instanceof SMapTypeCG)
		{
			return handleCollectionComparison(node);
		}
		else
		{
			return handleEquals(node);
		}
	}

	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node)
			throws AnalysisException
	{
		ANotUnaryExpCG transformed = transNotEquals(node);
		return formatNotUnary(transformed.getExp());
	}

	private ANotUnaryExpCG transNotEquals(ANotEqualsBinaryExpCG notEqual)
	{
		ANotUnaryExpCG notUnary = new ANotUnaryExpCG();
		notUnary.setType(new ABoolBasicTypeCG());

		AEqualsBinaryExpCG equal = new AEqualsBinaryExpCG();
		equal.setType(new ABoolBasicTypeCG());
		equal.setLeft(notEqual.getLeft().clone());
		equal.setRight(notEqual.getRight().clone());

		notUnary.setExp(equal);

		// Replace the "notEqual" expression with the transformed expression
		INode parent = notEqual.parent();

		// It may be the case that the parent is null if we execute e.g. [1] <> [1] in isolation
		if (parent != null)
		{
			parent.replaceChild(notEqual, notUnary);
			notEqual.parent(null);
		}

		return notUnary;
	}

	private String handleEquals(AEqualsBinaryExpCG valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", UTILS_FILE, format(valueType.getLeft()), format(valueType.getRight()));
	}

	private String handleCollectionComparison(SBinaryExpCG node) throws AnalysisException
	{
		// In VDM the types of the equals are compatible when the AST passes the type check
		SExpCG leftNode = node.getLeft();
		SExpCG rightNode = node.getRight();

		String empty = "Utils.empty(%s)";
		
		if (isEmptyCollection(leftNode.getType()))
		{
			return String.format(empty, format(node.getRight()));
		} else if (isEmptyCollection(rightNode.getType()))
		{
			return String.format(empty, format(node.getLeft()));
		}

		return UTILS_FILE + ".equals(" + format(node.getLeft()) + ", "
				+ format(node.getRight()) + ")";
	}

	private boolean isEmptyCollection(STypeCG type)
	{
		if (type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seq = (SSeqTypeCG) type;

			return seq.getEmpty();
		} else if (type instanceof SSetTypeCG)
		{
			SSetTypeCG set = (SSetTypeCG) type;

			return set.getEmpty();
		} else if (type instanceof SMapTypeCG)
		{
			SMapTypeCG map = (SMapTypeCG) type;

			return map.getEmpty();
		}

		return false;
	}

	public String format(List<AFormalParamLocalParamCG> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
			return "";
		}

		final String finalPrefix = " final ";

		AFormalParamLocalParamCG firstParam = params.get(0);
		writer.append(finalPrefix);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamCG param = params.get(i);
			writer.append(", ");
			writer.append(finalPrefix);
			writer.append(format(param));
		}
		return writer.toString();
	}

	public String formatSuperType(ADefaultClassDeclCG classDecl)
	{
		return classDecl.getSuperName() == null ? "" : "extends "
				+ classDecl.getSuperName();
	}
	
	public String formatInterfaces(ADefaultClassDeclCG classDecl)
	{
		LinkedList<AInterfaceDeclCG> interfaces = classDecl.getInterfaces();
		
		if(interfaces == null)
		{
			return "";
		}
		
		String implementsClause = "implements";
		String sep = " ";
		
		if(interfaces.isEmpty())
		{
			// All classes must be declared Serializable when traces are being generated.
			if(info.getSettings().generateTraces() || getJavaSettings().makeClassesSerializable())
			{
				return implementsClause + sep + java.io.Serializable.class.getName();
			}
			else
			{
				return "";
			}
		}
		
		for(int i = 0; i < interfaces.size(); i++)
		{
			implementsClause += sep + interfaces.get(i).getName();
			sep = ", ";
		}
		
		if(info.getSettings().generateTraces() || getJavaSettings().makeClassesSerializable())
		{
			implementsClause += sep + java.io.Serializable.class.getName();
		}
		
		return implementsClause;
	}

	public String formatArgs(List<? extends SExpCG> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		SExpCG firstExp = exps.get(0);
		writer.append(format(firstExp));

		for (int i = 1; i < exps.size(); i++)
		{
			SExpCG exp = exps.get(i);
			writer.append(", " + format(exp));
		}

		return writer.toString();
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public boolean isVoidType(STypeCG node)
	{
		return node instanceof AVoidTypeCG;
	}

	public String formatInitialExp(SExpCG exp) throws AnalysisException
	{
		// Examples:
		// private int a; (exp == null || exp instanceof AUndefinedExpCG)
		// private int a = 2; (otherwise)

		return exp == null || exp instanceof AUndefinedExpCG ? "" : " = " + format(exp);
	}

	public String formatOperationBody(SStmCG body) throws AnalysisException
	{
		String NEWLINE = "\n";
		if (body == null)
		{
			return ";";
		}

		StringWriter generatedBody = new StringWriter();

		generatedBody.append("{" + NEWLINE + NEWLINE);
		generatedBody.append(handleOpBody(body));
		generatedBody.append(NEWLINE + "}");

		return generatedBody.toString();
	}

	private String handleOpBody(SStmCG body) throws AnalysisException
	{
		AMethodDeclCG method = body.getAncestor(AMethodDeclCG.class);
		
		if(method == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing method when formatting operation body. Got: " + body);
		}
		else if(method.getAsync() != null && method.getAsync())
		{
			return "new VDMThread(){ "
			+ "\tpublic void run() {"
			+ "\t " + format(body)
			+ "\t} "
			+ "}.start();";
		}
		
		return format(body);
	}
	
	public String formatTemplateParam(INode potentialBasicType)
			throws AnalysisException
	{
		if (potentialBasicType == null)
		{
			return "";
		}

		TypeAssistantCG typeAssistant = info.getAssistantManager().getTypeAssistant();
		
		if (potentialBasicType instanceof STypeCG && typeAssistant.isNumericType((STypeCG) potentialBasicType))
		{
			return "Number";
		} else if (potentialBasicType instanceof ABoolBasicTypeCG)
		{
			return "Boolean";
		} else if (potentialBasicType instanceof ACharBasicTypeCG)
		{
			return "Character";
		} else
		{
			return format(potentialBasicType);
		}
	}

	public boolean isStringLiteral(SExpCG exp)
	{
		return exp instanceof AStringLiteralExpCG;
	}

	public boolean isSeqType(SExpCG exp)
	{
		return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
	}

	public boolean isMapType(SExpCG exp)
	{
		return info.getAssistantManager().getTypeAssistant().isMapType(exp);
	}

	public boolean isStringType(STypeCG type)
	{
		return info.getAssistantManager().getTypeAssistant().isStringType(type);
	}

	public boolean isStringType(SExpCG exp)
	{
		return info.getAssistantManager().getTypeAssistant().isStringType(exp);
	}

	public boolean isCharType(STypeCG type)
	{
		return type instanceof ACharBasicTypeCG;
	}

	public String buildString(List<SExpCG> exps) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();

		sb.append("new String(new char[]{");

		if (exps.size() > 0)
		{
			sb.append(format(exps.get(0)));

			for (int i = 1; i < exps.size(); i++)
			{
				sb.append(", " + format(exps.get(i)));
			}
		}

		sb.append("})");

		return sb.toString();
	}

	public String formatElementType(STypeCG type) throws AnalysisException
	{
		if (type instanceof SSetTypeCG)
		{
			SSetTypeCG setType = (SSetTypeCG) type;

			return format(setType.getSetOf());
		} else if (type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seqType = (SSeqTypeCG) type;

			return format(seqType.getSeqOf());
		}

		throw new AnalysisException("Expected set or seq type when trying to format element type");
	}

	public String nextVarName(String prefix)
	{
		return info.getTempVarNameGen().nextVarName(prefix);
	}
	
	public boolean isLoopVar(AVarDeclCG localVar)
	{
		return localVar.parent() instanceof AForLoopStmCG;
	}

	public boolean isLambda(AApplyExpCG applyExp)
	{
		SExpCG root = applyExp.getRoot();

		if (root instanceof AApplyExpCG
				&& root.getType() instanceof AMethodTypeCG)
		{
			return true;
		}

		if (!(root instanceof SVarExpCG))
		{
			return false;
		}

		SVarExpCG varExp = (SVarExpCG) root;

		return varExp.getIsLambda() != null && varExp.getIsLambda();
	}

	public String escapeStr(String str)
	{
		String escaped = "";
		for (int i = 0; i < str.length(); i++)
		{
			char currentChar = str.charAt(i);
			escaped += GeneralUtils.isEscapeSequence(currentChar) ? StringEscapeUtils.escapeJava(currentChar
					+ "")
					: currentChar + "";
		}

		return escaped;
	}

	public static boolean castNotNeeded(STypeCG type)
	{
		return type instanceof AObjectTypeCG || type instanceof AUnknownTypeCG || type instanceof AUnionTypeCG;
	}
	
	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c) ? StringEscapeUtils.escapeJavaScript(c
				+ "")
				: c + "";
	}
	
	public boolean isInnerClass(ADefaultClassDeclCG node)
	{
		return info.getDeclAssistant().isInnerClass(node);
	}
	
	public String formatStartStmExp(AStartStmCG node) throws AnalysisException
	{
		String str = format(node.getExp());

		if (node.getExp().getType() instanceof AClassTypeCG)
		{
			return str;
		} else
		{
			return "((Thread)" + str + ")";
		}
	}
	
	public boolean genDecl(ATypeDeclCG node)
	{
		return !(node.getDecl() instanceof ANamedTypeDeclCG);
	}
	
	public boolean genTypeDecl(ATypeDeclCG node)
	{
		if(node.getDecl() instanceof ARecordDeclCG)
		{
			return getJavaSettings().genRecsAsInnerClasses();
		}
		else
		{
			return info.getSettings().generateInvariants();
		}
	}
	
	public static boolean isSeqConversion(AFieldNumberExpCG node)
	{
		INode parent = node.parent();
		return parent instanceof ASeqToStringUnaryExpCG || parent instanceof AStringToSeqUnaryExpCG;
	}
	
	public static boolean isScoped(ABlockStmCG block)
	{
		return block != null && block.getScoped() != null && block.getScoped();
	}

	public static boolean isMainClass(ADefaultClassDeclCG clazz)
	{
		return clazz != null && clazz.getTag() instanceof JavaMainTag;
	}
	
	public String getQuotePackagePrefix()
	{
		String settings = getJavaSettings().getJavaRootPackage();
		
		if(settings != null && !settings.trim().isEmpty())
		{
			return settings + "." + JavaCodeGen.JAVA_QUOTES_PACKAGE + ".";
		}
		else
		{
			return JavaCodeGen.JAVA_QUOTES_PACKAGE + ".";
		}
	}

	public boolean genClassInvariant(ADefaultClassDeclCG clazz)
	{
		if(!info.getSettings().generateInvariants())
		{
			return false;
		}
		
		return clazz.getInvariant() != null;
	}
	
	public static String formatMetaData(List<ClonableString> metaData)
	{
		if(metaData == null || metaData.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(ClonableString str : metaData)
		{
			sb.append(str.value).append('\n');
		}
		
		return sb.append('\n').toString();
	}
	
	public static boolean isVdmSl()
	{
		return Settings.dialect == Dialect.VDM_SL;
	}
	
	public String genIteratorName()
	{
		return info.getTempVarNameGen().nextVarName(varPrefixManager.getIteVarPrefixes().iterator());
	}

	public String genThreadName()
	{
		return info.getTempVarNameGen().nextVarName("nextThread_");
	}

	public String genForIndexToVarName()
	{
		return info.getTempVarNameGen().nextVarName(varPrefixManager.getIteVarPrefixes().forIndexToVar());
	}

	public String genForIndexByVarName()
	{
		return info.getTempVarNameGen().nextVarName(varPrefixManager.getIteVarPrefixes().forIndexByVar());
	}
	
	public static String getString(ClonableString c)
	{
		return c.value;
	}
	
	public boolean isUndefined(ACastUnaryExpCG cast)
	{
		return info.getExpAssistant().isUndefined(cast);
	}
}
