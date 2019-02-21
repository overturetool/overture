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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.assistant.TypeAssistantIR;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.*;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.ir.statements.AStartStmIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Settings;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFormat
{
	public static final String TYPE_DECL_PACKAGE_SUFFIX = "types";
	public static final String CLASS_EXTENSION = ".class";
	public static final String UTILS_FILE = "Utils";
	public static final String SEQ_UTIL_FILE = "SeqUtil";
	public static final String SET_UTIL_FILE = "SetUtil";
	public static final String MAP_UTIL_FILE = "MapUtil";

	protected IRInfo info;

	protected FuncValAssistant funcValAssist;
	protected MergeVisitor mergeVisitor;
	protected JavaValueSemantics valueSemantics;
	protected JavaFormatAssistant javaFormatAssistant;
	protected JavaRecordCreator recCreator;
	protected JavaVarPrefixManager varPrefixManager;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public JavaFormat(JavaVarPrefixManager varPrefixManager,
			String templateRoot, IRInfo info)
	{
		this.varPrefixManager = varPrefixManager;
		this.valueSemantics = new JavaValueSemantics(this);
		this.recCreator = new JavaRecordCreator(this);
		TemplateCallable[] templateCallables = TemplateCallableManager.constructTemplateCallables(this, valueSemantics);
		this.mergeVisitor = new MergeVisitor(new JavaTemplateManager(templateRoot), templateCallables);
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

	public void setJavaSettings(JavaSettings javaSettings)
	{
		valueSemantics.setJavaSettings(javaSettings);
	}

	public JavaSettings getJavaSettings()
	{
		return valueSemantics.getJavaSettings();
	}

	public void clear()
	{
		mergeVisitor.init();
		valueSemantics.clear();
		this.funcValAssist = null;
	}

	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}

	public String format(AMethodTypeIR methodType) throws AnalysisException
	{
		final String OBJ = "Object";

		if (funcValAssist == null)
		{
			return OBJ;
		}

		AInterfaceDeclIR methodTypeInterface = funcValAssist.findInterface(methodType);

		if (methodTypeInterface == null)
		{
			return OBJ; // Should not happen
		}

		AInterfaceTypeIR methodClass = new AInterfaceTypeIR();
		methodClass.setName(methodTypeInterface.getName());

		LinkedList<STypeIR> params = methodType.getParams();

		for (STypeIR param : params)
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

	private String findNumberDereferenceCall(STypeIR type)
	{
		if (type == null || type.parent() instanceof AHistoryExpIR)
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

	private String getNumberDereference(INode node, boolean ignoreContext)
	{
		if (ignoreContext && node instanceof SExpIR)
		{
			SExpIR exp = (SExpIR) node;
			STypeIR type = exp.getType();

			if (isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}

		INode parent = node.parent();

		if (parent instanceof SNumericBinaryExpIR
				|| parent instanceof AAbsUnaryExpIR
				|| parent instanceof AMinusUnaryExpIR
				|| parent instanceof APlusUnaryExpIR
				|| parent instanceof AIsolationUnaryExpIR)
		{
			SExpIR exp = (SExpIR) node;
			STypeIR type = exp.getType();

			if (isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}

		// No dereference is needed
		return "";
	}

	private static boolean isNumberDereferenceCandidate(SExpIR node)
	{
		boolean fitsCategory = !(node instanceof SNumericBinaryExpIR)
				&& !(node instanceof ATernaryIfExpIR)
				&& !(node instanceof SLiteralExpIR)
				&& !(node instanceof AIsolationUnaryExpIR)
				&& !(node instanceof SUnaryExpIR);

		boolean isException = node instanceof AHeadUnaryExpIR
				|| node instanceof AQuoteLiteralExpIR
				|| node instanceof ACastUnaryExpIR;

		return fitsCategory || isException;
	}

	public String formatName(INode node) throws AnalysisException
	{
		if (node instanceof ANewExpIR)
		{
			ANewExpIR newExp = (ANewExpIR) node;

			return formatTypeName(node, newExp.getName());
		} else if (node instanceof ARecordTypeIR)
		{
			ARecordTypeIR record = (ARecordTypeIR) node;
			ATypeNameIR typeName = record.getName();

			return formatTypeName(node, typeName);
		}

		throw new AnalysisException("Unexpected node in formatName: "
				+ node.getClass().getName());
	}

	public String formatTypeName(INode node, ATypeNameIR typeName)
	{
		// Type names are also used for quotes, which do not have a defining class.
		if (typeName.getDefiningClass() != null
				&& !getJavaSettings().genRecsAsInnerClasses())
		{
			String typeNameStr = "";

			if (JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
			{
				typeNameStr += getJavaSettings().getJavaRootPackage() + ".";
			}

			typeNameStr += typeName.getDefiningClass()
					+ TYPE_DECL_PACKAGE_SUFFIX + ".";

			typeNameStr += typeName.getName();

			return typeNameStr;
		}

		SClassDeclIR classDef = node.getAncestor(SClassDeclIR.class);

		String definingClass = typeName.getDefiningClass() != null
				&& classDef != null
				&& !classDef.getName().equals(typeName.getDefiningClass())
						? typeName.getDefiningClass() + "." : "";

		return definingClass + typeName.getName();
	}

	public String format(SExpIR exp, boolean leftChild) throws AnalysisException
	{
		String formattedExp = format(exp);

		JavaPrecedence precedence = new JavaPrecedence();

		INode parent = exp.parent();

		if (!(parent instanceof SExpIR))
		{
			return formattedExp;
		}

		boolean isolate = precedence.mustIsolate((SExpIR) parent, exp, leftChild);

		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}

	public String formatUnary(SExpIR exp) throws AnalysisException
	{
		return format(exp, false);
	}

	public String formatNotUnary(SExpIR exp) throws AnalysisException
	{
		String formattedExp = format(exp, false);

		boolean doNotWrap = exp instanceof ABoolLiteralExpIR
				|| formattedExp.startsWith("(") && formattedExp.endsWith(")");

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}

	public String formatTemplateTypes(List<STypeIR> types)
			throws AnalysisException
	{
		if (types.isEmpty())
		{
			return "";
		}

		return "<" + formattedTypes(types, "") + ">";
	}

	private String formattedTypes(List<STypeIR> types, String typePostFix)
			throws AnalysisException
	{
		STypeIR firstType = types.get(0);

		if (info.getAssistantManager().getTypeAssistant().isBasicType(firstType))
		{
			firstType = info.getAssistantManager().getTypeAssistant().getWrapperType((SBasicTypeIR) firstType);
		}

		StringWriter writer = new StringWriter();
		writer.append(format(firstType) + typePostFix);

		for (int i = 1; i < types.size(); i++)
		{
			STypeIR currentType = types.get(i);

			if (info.getAssistantManager().getTypeAssistant().isBasicType(currentType))
			{
				currentType = info.getAssistantManager().getTypeAssistant().getWrapperType((SBasicTypeIR) currentType);
			}

			writer.append(", " + format(currentType) + typePostFix);
		}

		String result = writer.toString();

		return result;
	}

	public String formatTypeArg(STypeIR type) throws AnalysisException
	{
		if (type == null)
		{
			return null;
		} else if(type instanceof AExternalTypeIR)
		{
			return ((AExternalTypeIR) type).getName();
		}
		else
		{
			List<STypeIR> types = new LinkedList<STypeIR>();
			types.add(type);

			return formattedTypes(types, CLASS_EXTENSION);
		}
	}

	public String formatTypeArgs(ATupleTypeIR tupleType)
			throws AnalysisException
	{
		return formatTypeArgs(tupleType.getTypes());
	}

	public String formatTypeArgs(List<STypeIR> types) throws AnalysisException
	{
		if (types.isEmpty())
		{
			return "";
		}

		return formattedTypes(types, CLASS_EXTENSION);
	}

	public String formatEqualsBinaryExp(AEqualsBinaryExpIR node)
			throws AnalysisException
	{
		STypeIR leftNodeType = node.getLeft().getType();

		if (leftNodeType instanceof SSeqTypeIR
				|| leftNodeType instanceof SSetTypeIR
				|| leftNodeType instanceof SMapTypeIR)
		{
			return handleCollectionComparison(node);
		} else
		{
			return handleEquals(node);
		}
	}

	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpIR node)
			throws AnalysisException
	{
		ANotUnaryExpIR transformed = transNotEquals(node);
		return formatNotUnary(transformed.getExp());
	}

	private ANotUnaryExpIR transNotEquals(ANotEqualsBinaryExpIR notEqual)
	{
		ANotUnaryExpIR notUnary = new ANotUnaryExpIR();
		notUnary.setType(new ABoolBasicTypeIR());

		AEqualsBinaryExpIR equal = new AEqualsBinaryExpIR();
		equal.setType(new ABoolBasicTypeIR());
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

	private String handleEquals(AEqualsBinaryExpIR valueType)
			throws AnalysisException
	{
		return String.format("%s.equals(%s, %s)", UTILS_FILE, format(valueType.getLeft()), format(valueType.getRight()));
	}

	private String handleCollectionComparison(SBinaryExpIR node)
			throws AnalysisException
	{
		// In VDM the types of the equals are compatible when the AST passes the type check
		SExpIR leftNode = node.getLeft();
		SExpIR rightNode = node.getRight();

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

	private boolean isEmptyCollection(STypeIR type)
	{
		if (type instanceof SSeqTypeIR)
		{
			SSeqTypeIR seq = (SSeqTypeIR) type;

			return seq.getEmpty();
		} else if (type instanceof SSetTypeIR)
		{
			SSetTypeIR set = (SSetTypeIR) type;

			return set.getEmpty();
		} else if (type instanceof SMapTypeIR)
		{
			SMapTypeIR map = (SMapTypeIR) type;

			return map.getEmpty();
		}

		return false;
	}

	public String format(List<AFormalParamLocalParamIR> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
			return "";
		}

		final String finalPrefix = " final ";

		AFormalParamLocalParamIR firstParam = params.get(0);
		writer.append(finalPrefix);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamIR param = params.get(i);
			writer.append(", ");
			writer.append(finalPrefix);
			writer.append(format(param));
		}
		return writer.toString();
	}

	public String formatSuperType(SClassDeclIR classDecl)
	{
		return classDecl.getSuperNames().isEmpty() ? ""
				: "extends " + classDecl.getSuperNames().get(0);
	}

	public String formatInterfaces(SClassDeclIR classDecl)
	{
		LinkedList<AInterfaceDeclIR> interfaces = classDecl.getInterfaces();

		if (interfaces == null)
		{
			return "";
		}

		List<String> interfaceNames = new LinkedList<>();
		for(AInterfaceDeclIR i : interfaces)
		{
			interfaceNames.add(i.getName());
		}
		
		return formatInterfaceNames(interfaceNames, "implements");
	}
	
	public String formatInterfaces(AInterfaceDeclIR inter)
	{
		if(inter.getExtension() == null || inter.getExtension().isEmpty())
		{
			return "";
		}
		
		List<String> interfaceNames = new LinkedList<>();
		for(ATokenNameIR e : inter.getExtension())
		{
			interfaceNames.add(e.getName());
		}
		
		return formatInterfaceNames(interfaceNames, "extends");
	}

	private String formatInterfaceNames(List<String> interfaceNames, String keyword) {
		String implementsClause = keyword;
		String sep = " ";

		if (interfaceNames.isEmpty())
		{
			// All classes must be declared Serializable when traces are being generated.
			if (info.getSettings().generateTraces()
					|| getJavaSettings().makeClassesSerializable())
			{
				return implementsClause + sep
						+ java.io.Serializable.class.getName();
			} else
			{
				return "";
			}
		}

		for (int i = 0; i < interfaceNames.size(); i++)
		{
			implementsClause += sep + interfaceNames.get(i);
			sep = ", ";
		}

		if (info.getSettings().generateTraces()
				|| getJavaSettings().makeClassesSerializable())
		{
			implementsClause += sep + java.io.Serializable.class.getName();
		}

		return implementsClause;
	}

	public String formatArgs(List<? extends SExpIR> exps)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (exps.size() <= 0)
		{
			return "";
		}

		SExpIR firstExp = exps.get(0);
		writer.append(format(firstExp));

		for (int i = 1; i < exps.size(); i++)
		{
			SExpIR exp = exps.get(i);
			writer.append(", " + format(exp));
		}

		return writer.toString();
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public boolean isVoidType(STypeIR node)
	{
		return node instanceof AVoidTypeIR;
	}

	public String formatInitialExp(SExpIR exp) throws AnalysisException
	{
		// Examples:
		// private int a; (exp == null || exp instanceof AUndefinedExpIR)
		// private int a = 2; (otherwise)

		return exp == null || exp instanceof AUndefinedExpIR ? ""
				: " = " + format(exp);
	}

	public String formatThrows(List<STypeIR> types) throws AnalysisException
	{
		if (!types.isEmpty())
		{
			StringBuilder sb = new StringBuilder();

			sb.append(IJavaConstants.THROWS);
			sb.append(' ');

			String sep = "";
			for (STypeIR t : types)
			{
				sb.append(sep);
				sb.append(format(t));
				sep = ", ";
			}

			return sb.toString();
		} else
		{
			return "";
		}
	}

	public String formatOperationBody(SStmIR body) throws AnalysisException
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

	private String handleOpBody(SStmIR body) throws AnalysisException
	{
		AMethodDeclIR method = body.getAncestor(AMethodDeclIR.class);

		if (method == null)
		{
			log.error("Could not find enclosing method when formatting operation body. Got: "
					+ body);
		} else if (method.getAsync() != null && method.getAsync())
		{
			return "new VDMThread(){ " + "\tpublic void run() {" + "\t "
					+ format(body) + "\t} " + "}.start();";
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

		TypeAssistantIR typeAssistant = info.getAssistantManager().getTypeAssistant();

		if (potentialBasicType instanceof STypeIR
				&& typeAssistant.isNumericType((STypeIR) potentialBasicType))
		{
			return "Number";
		} else if (potentialBasicType instanceof ABoolBasicTypeIR)
		{
			return "Boolean";
		} else if (potentialBasicType instanceof ACharBasicTypeIR)
		{
			return "Character";
		} else
		{
			return format(potentialBasicType);
		}
	}

	public boolean isSeqType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isSeqType(exp);
	}

	public boolean isMapType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isMapType(exp);
	}

	public boolean isStringType(STypeIR type)
	{
		return info.getAssistantManager().getTypeAssistant().isStringType(type);
	}

	public boolean isStringType(SExpIR exp)
	{
		return info.getAssistantManager().getTypeAssistant().isStringType(exp);
	}

	public String buildString(List<SExpIR> exps) throws AnalysisException
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

	public String formatElementType(STypeIR type) throws AnalysisException
	{
		if (type instanceof SSetTypeIR)
		{
			SSetTypeIR setType = (SSetTypeIR) type;

			return format(setType.getSetOf());
		} else if (type instanceof SSeqTypeIR)
		{
			SSeqTypeIR seqType = (SSeqTypeIR) type;

			return format(seqType.getSeqOf());
		} else if (type instanceof AStringTypeIR)
		{
			return format(new ACharBasicTypeIR());
		} else
		{
			String vdmNodeInfo = info.getLocationAssistant().consVdmNodeInfoStr(type);

			log.error("Expected set, seq or string type when trying to format element type. Got: "
					+ type + ". " + vdmNodeInfo);
			return format(new AUnknownTypeIR());
		}
	}

	public boolean isLoopVar(AVarDeclIR localVar)
	{
		return localVar.parent() instanceof AForLoopStmIR;
	}

	public boolean isLambda(AApplyExpIR applyExp)
	{
		SExpIR root = applyExp.getRoot();

		if (root instanceof AApplyExpIR
				&& root.getType() instanceof AMethodTypeIR)
		{
			return true;
		}

		if (!(root instanceof SVarExpIR))
		{
			return false;
		}

		SVarExpIR varExp = (SVarExpIR) root;

		return varExp.getIsLambda() != null && varExp.getIsLambda();
	}

	public String escapeStr(String str)
	{
		String escaped = "";
		for (int i = 0; i < str.length(); i++)
		{
			char currentChar = str.charAt(i);
			escaped += GeneralUtils.isEscapeSequence(currentChar)
					? StringEscapeUtils.escapeJava(currentChar + "")
					: currentChar + "";
		}

		return escaped;
	}

	public static boolean castNotNeeded(STypeIR type)
	{
		return type instanceof AObjectTypeIR || type instanceof AUnknownTypeIR
				|| type instanceof AUnionTypeIR;
	}

	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c)
				? StringEscapeUtils.escapeJavaScript(c + "") : c + "";
	}

	public boolean isInnerClass(SClassDeclIR node)
	{
		return info.getDeclAssistant().isInnerClass(node);
	}

	public String formatStartStmExp(AStartStmIR node) throws AnalysisException
	{
		String str = format(node.getExp());

		if (node.getExp().getType() instanceof AClassTypeIR)
		{
			return str;
		} else
		{
			return "((Thread)" + str + ")";
		}
	}

	public boolean genDecl(ATypeDeclIR node)
	{
		return !(node.getDecl() instanceof ANamedTypeDeclIR);
	}

	public boolean genTypeDecl(ATypeDeclIR node)
	{
		if (node.getDecl() instanceof ARecordDeclIR)
		{
			return getJavaSettings().genRecsAsInnerClasses();
		} else
		{
			return info.getSettings().generateInvariants();
		}
	}

	public static boolean isSeqConversion(AFieldNumberExpIR node)
	{
		INode parent = node.parent();
		return parent instanceof ASeqToStringUnaryExpIR
				|| parent instanceof AStringToSeqUnaryExpIR;
	}

	public static boolean isScoped(ABlockStmIR block)
	{
		return block != null && block.getScoped() != null && block.getScoped();
	}

	public static boolean isMainClass(SClassDeclIR clazz)
	{
		return clazz != null && clazz.getTag() instanceof JavaMainTag;
	}

	public String formatVdmSource(PIR irNode)
	{
		if (getJavaSettings().printVdmLocations() && irNode != null)
		{
			org.overture.ast.node.INode vdmNode = LocationAssistantIR.getVdmNode(irNode);

			if (vdmNode != null)
			{
				ILexLocation loc = info.getLocationAssistant().findLocation(vdmNode);

				if (loc != null)
				{
					return String.format("/* %s %d:%d */\n", loc.getFile().getName(), loc.getStartLine(), loc.getStartPos());
				}
			}

		}

		return "";
	}

	public String getQuotePackagePrefix()
	{
		String settings = getJavaSettings().getJavaRootPackage();

		if (settings != null && !settings.trim().isEmpty())
		{
			return settings + "." + JavaCodeGen.JAVA_QUOTES_PACKAGE + ".";
		} else
		{
			return JavaCodeGen.JAVA_QUOTES_PACKAGE + ".";
		}
	}

	public boolean genClassInvariant(SClassDeclIR clazz)
	{
		if (!info.getSettings().generateInvariants())
		{
			return false;
		}

		return clazz.getInvariant() != null;
	}

	public static String formatMetaData(List<ClonableString> metaData)
	{
		if (metaData == null || metaData.isEmpty())
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (ClonableString str : metaData)
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

	public boolean isUndefined(ACastUnaryExpIR cast)
	{
		return info.getExpAssistant().isUndefined(cast);
	}
	
	public String formatIdentifierVar(AIdentifierVarExpIR var)
	{
		String varName = "";
		
		if(!getJavaSettings().genRecsAsInnerClasses() && (var != null && !var.getIsLocal()))
		{
			// Only the VDM-SL-to-JML generator uses this strategy
			ADefaultClassDeclIR enclosingIrClass = var.getAncestor(ADefaultClassDeclIR.class);
			
			if(enclosingIrClass != null)
			{
				org.overture.ast.node.INode vdmNode = AssistantBase.getVdmNode(enclosingIrClass);
				
				if(!(vdmNode instanceof AModuleModules))
				{
					// The VDM node is a record (or state definition, which is also translated to a record class)
					// The variable  ('var') must belong to the VDM module enclosing 'vdmNode'	
					
					AModuleModules module = vdmNode.getAncestor(AModuleModules.class);
					
					if(module != null)
					{
						List<String> fieldNames = new LinkedList<>();
						
						if(vdmNode instanceof ARecordInvariantType)
						{
							ARecordInvariantType rec = (ARecordInvariantType) vdmNode;
							fieldNames = rec.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
						}
						else if(vdmNode instanceof AStateDefinition)
						{
							AStateDefinition stateDef = (AStateDefinition) vdmNode;
							fieldNames = stateDef.getFields().stream().map(f -> f.getTagname().getName()).collect(Collectors.toList());
						}
						else
						{
							log.error("Expected a record or statedefinition at this point, but got: " + vdmNode);
						}
						
						if(!fieldNames.contains(var.getName()))
						{
							// It's not one of the record field names
							
							if (JavaCodeGenUtil.isValidJavaPackage(getJavaSettings().getJavaRootPackage()))
							{
								varName += getJavaSettings().getJavaRootPackage() + ".";
							}

							varName += module.getName().getName() + "." + var.getName();
						}
						else
						{
							// We're accessing a record field, so there's no need to use a fully qualified name
							varName = var.getName();
						}
					}
					else
					{
						log.error("Expected 'vdmNode' to be enclosed by a module");
					}
				}
				else
				{
					// Orginary case, we're inside a module
					varName = var.getName();
				}
			}
		}
		else
		{
			varName = var.getName();
		}
		
		if(valueSemantics.shouldClone(var))
		{
			return "Utils.copy(" + varName + ")";
		}
		else
		{
			return varName;
		}
	}
}
