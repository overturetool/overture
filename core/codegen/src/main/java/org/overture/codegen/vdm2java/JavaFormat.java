package org.overture.codegen.vdm2java;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExternalExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCGBase;
import org.overture.codegen.cgast.expressions.SLiteralExpCGBase;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.PObjectDesignatorCG;
import org.overture.codegen.cgast.statements.PStateDesignatorCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AInterfaceTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCGBase;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.ir.IRAnalysis;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.ITempVarGen;

public class JavaFormat
{
	private static final String JAVA_NUMBER = "Number";
	public static final String ADD_ELEMENT_TO_MAP = "put";
	
	public static final String UTILS_FILE = "Utils";
	public static final String SEQ_UTIL_FILE = "SeqUtil";
	public static final String SET_UTIL_FILE = "SetUtil";
	public static final String MAP_UTIL_FILE = "MapUtil";
	
	public String getJavaNumber()
	{
		return JAVA_NUMBER;
	}
	
	private static final String JAVA_PUBLIC = "public";
	private static final String JAVA_INT = "int";
	
	private List<AClassDeclCG> classes;
	private ITempVarGen tempVarNameGen;
	private AssistantManager assistantManager;
	private MergeVisitor mergeVisitor;
	private FunctionValueAssistant functionValueAssistant;
	
	private ValueSemantics valueSemantics;
	
	public JavaFormat(TempVarPrefixes varPrefixes,ITempVarGen tempVarNameGen, AssistantManager assistantManager)
	{
		this.tempVarNameGen = tempVarNameGen;
		this.assistantManager = assistantManager;
		this.valueSemantics = new ValueSemantics(this);
		
		this.mergeVisitor = new MergeVisitor(JavaCodeGen.JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.constructTemplateCallables(this, IRAnalysis.class, varPrefixes, valueSemantics));
		this.functionValueAssistant = null;
	}
	
	public void setFunctionValueAssistant(FunctionValueAssistant functionValueAssistant)
	{
		this.functionValueAssistant = functionValueAssistant;
	}
	
	public void clearFunctionValueAssistant()
	{
		this.functionValueAssistant = null;
	}
	
	public AssistantManager getAssistantManager()
	{
		return assistantManager;
	}
	
	public List<AClassDeclCG> getClasses()
	{
		return classes;
	}
	
	public void setJavaSettings(JavaSettings javaSettings)
	{
		valueSemantics.setJavaSettings(javaSettings);
	}
	
	public String format(AMethodTypeCG methodType) throws AnalysisException
	{
		final String OBJ = "Object";
		
		if(functionValueAssistant == null)
			return OBJ;

		AInterfaceDeclCG methodTypeInterface = functionValueAssistant.findInterface(methodType);

		if(methodTypeInterface == null)
			return OBJ; //Should not happen
		
		AInterfaceTypeCG methodClass = new AInterfaceTypeCG();
		methodClass.setName(methodTypeInterface.getName());
		
		LinkedList<PTypeCG> params = methodType.getParams();
		
		for(PTypeCG param : params)
		{
			methodClass.getTypes().add(param.clone());
		}
		
		methodClass.getTypes().add(methodType.getResult().clone());
		
		return methodClass != null ? format(methodClass) : OBJ;
	}
	
	public void init()
	{
		mergeVisitor.dropMergeErrors();
	}
	
	public void setClasses(List<AClassDeclCG> classes)
	{
		this.classes = classes != null ? classes : new LinkedList<AClassDeclCG>();
	}
	
	public void clearClasses()
	{
		if(classes != null)
			classes.clear();
		else
			classes = new LinkedList<AClassDeclCG>();
	}
	
	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}
	
	public String format(INode node) throws AnalysisException
	{		
		return format(node, false);
	}
	
	public String formatIgnoreContext(INode node) throws AnalysisException
	{
		return format(node, true);
	}
	
	private String format(INode node, boolean ignoreContext) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString() + getNumberDereference(node, ignoreContext);
	}
	
	private static String findNumberDereferenceCall(PTypeCG type)
	{
		if (type instanceof ARealNumericBasicTypeCG
				|| type instanceof ARealBasicTypeWrappersTypeCG)
		{
			return ".doubleValue()";
		} else if (type instanceof AIntNumericBasicTypeCG
				|| type instanceof AIntBasicTypeWrappersTypeCG)
		{
			return ".longValue()";
		}
		else
		{
			return "";
		}
	}
	
	public static boolean isMapSeq(PStateDesignatorCG stateDesignator)
	{
		return stateDesignator instanceof AMapSeqStateDesignatorCG;
	}
	
	public String formatMapSeqStateDesignator(AMapSeqStateDesignatorCG mapSeq) throws AnalysisException
	{
		INode parent = mapSeq.parent();
		
		if(!(parent instanceof AAssignmentStmCG))
			throw new AnalysisException("Generation of map sequence state designator was expecting an assignment statement as parent. Got : " + parent);
		
		AAssignmentStmCG assignment = (AAssignmentStmCG) parent;
		
		PStateDesignatorCG stateDesignator = mapSeq.getMapseq();
		PExpCG domValue = mapSeq.getExp();
		PExpCG rngValue = assignment.getExp();
		
		String stateDesignatorStr = format(stateDesignator);
		String domValStr = format(domValue);
		String rngValStr = format(rngValue);
		
		//e.g. counters.put("c1", 4);
		return stateDesignatorStr + "." + ADD_ELEMENT_TO_MAP + "(" + domValStr + ", " + rngValStr + ")";
	}
	
	private static String getNumberDereference(INode node, boolean ignoreContext)
	{
		if(ignoreContext && node instanceof PExpCG)
		{
			PExpCG exp = (PExpCG) node;
			PTypeCG type = exp.getType();
			
			if(isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}
		
		INode parent = node.parent();
			
		if (parent instanceof SNumericBinaryExpCG ||
			parent instanceof AEqualsBinaryExpCG ||
			parent instanceof ANotEqualsBinaryExpCG)
		{
			PExpCG exp = (PExpCG) node;
			PTypeCG type = exp.getType();
			
			if(isNumberDereferenceCandidate(exp))
			{
				return findNumberDereferenceCall(type);
			}
		}

		// No dereference is needed
		return "";
	}
	
	private static boolean isNumberDereferenceCandidate(PExpCG node)
	{
		return (!(node instanceof SNumericBinaryExpCG)
				&& !(node instanceof SLiteralExpCGBase)
				&& !(node instanceof AIsolationUnaryExpCG)
				&& !(node instanceof SUnaryExpCG))
				|| node instanceof AHeadUnaryExpCG;
	}

	public String formatName(INode node) throws AnalysisException
	{
		if(node instanceof ANewExpCG)
		{
			ANewExpCG newExp = (ANewExpCG) node;
			
			return formatTypeName(node, newExp.getName());
		}
		else if(node instanceof ARecordTypeCG)
		{
			ARecordTypeCG record = (ARecordTypeCG) node;
			ATypeNameCG typeName = record.getName();
			
			return formatTypeName(node, typeName);
		}
		
		throw new AnalysisException("Unexpected node in formatName: " + node.getClass().getName());
	}
	
	public String formatTypeName(INode node, ATypeNameCG typeName)
	{
		AClassDeclCG classDef = node.getAncestor(AClassDeclCG.class);
		
		String definingClass = typeName.getDefiningClass() != null &&
							   (classDef != null && !classDef.getName().equals(typeName.getDefiningClass())) ? 
									   typeName.getDefiningClass() + "." : "";
		
		String name = typeName.getName();
		
		return definingClass + name;
	}
	
	public String format(PExpCG exp, boolean leftChild) throws AnalysisException
	{
		String formattedExp = format(exp);
		
		JavaPrecedence precedence = new JavaPrecedence();
		
		INode parent = exp.parent();
		
		if(!(parent instanceof PExpCG))
			return formattedExp;
		
		boolean isolate = precedence.mustIsolate((PExpCG) parent, exp, leftChild);
		
		return isolate ? "(" + formattedExp + ")" : formattedExp;
	}
	
	public String formatUnary(PExpCG exp) throws AnalysisException
	{
		return format(exp, false);
	}
	
	public String formatNotUnary(PExpCG exp) throws AnalysisException
	{
		String formattedExp = format(exp, false);

		boolean doNotWrap = exp instanceof ABoolLiteralExpCG
				|| (formattedExp.startsWith("(") && formattedExp.endsWith(")"));

		return doNotWrap ? "!" + formattedExp : "!(" + formattedExp + ")";
	}
	
	public String generateCloneMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG method = new AMethodDeclCG();

		method.setAccess(JAVA_PUBLIC);
		method.setName("clone");
		
		AClassDeclCG defClass = record.getAncestor(AClassDeclCG.class);
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());
		
		ARecordTypeCG returnType = new ARecordTypeCG();
		returnType.setName(typeName);
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
		
		method.setMethodType(methodType);
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(returnType.clone());
		newExp.setName(typeName.clone());
		LinkedList<PExpCG> args = newExp.getArgs();
		
		LinkedList<AFieldDeclCG> fields = record.getFields();
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setOriginal(name);
			varExp.setType(field.getType().clone());
			args.add(varExp);
		}
		
		AReturnStmCG body = new AReturnStmCG();
		body.setExp(newExp);
		method.setBody(body);

		record.getMethods().add(method);
		
		return format(method);
	}
	
	public String formatRecordConstructor(ARecordDeclCG record) throws AnalysisException
	{
		LinkedList<AFieldDeclCG> fields = record.getFields();
		
		AMethodDeclCG constructor = new AMethodDeclCG();
		//Since Java does not have records but the OO AST does a record is generated as a Java class.
		//To make sure that the record can be instantiated we must explicitly add a constructor.
		constructor.setAccess(JAVA_PUBLIC);
		constructor.setIsConstructor(true);
		constructor.setName(record.getName());
		LinkedList<AFormalParamLocalDeclCG> formalParams = constructor.getFormalParams();
	
		ABlockStmCG body = new ABlockStmCG();
		LinkedList<PStmCG> bodyStms = body.getStatements();
		constructor.setBody(body); 
		
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			PTypeCG type = field.getType().clone();
			
			String paramName = "_" + name;
			
			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(paramName);

			//Construct formal parameter of the constructor
			AFormalParamLocalDeclCG formalParam = new AFormalParamLocalDeclCG();
			formalParam.setPattern(idPattern);
			formalParam.setType(type);
			formalParams.add(formalParam);
			
			//Construct the initialization of the record field using the
			//corresponding formal parameter.
			AAssignmentStmCG assignment = new AAssignmentStmCG();
			AIdentifierStateDesignatorCG id = new AIdentifierStateDesignatorCG();
			id.setName(name);
			
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			varExp.setType(field.getType().clone());
			varExp.setOriginal(paramName);

			assignment.setTarget(id);
			
			if (!assistantManager.getTypeAssistant().isBasicType(varExp.getType()))
			{
				//Example: b = (_b != null) ? _b.clone() : null;
				ATernaryIfExpCG checkedAssignment = new ATernaryIfExpCG();
				checkedAssignment.setType(new ABoolBasicTypeCG());
				checkedAssignment.setCondition(JavaFormatAssistant.consParamNotNullComp(varExp));
				checkedAssignment.setTrueValue(varExp);
				checkedAssignment.setFalseValue(new ANullExpCG());
				assignment.setExp(checkedAssignment);
			}
			else
			{
				assignment.setExp(varExp);
			}
			
			bodyStms.add(assignment);
		}
		
		record.getMethods().add(constructor);
		
		return format(constructor);
	}
	
	public String formatTemplateTypes(LinkedList<PTypeCG> types) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(types.size() <= 0)
			return "";
		
		PTypeCG firstType = types.get(0);
		
		if(assistantManager.getTypeAssistant().isBasicType(firstType))
			firstType = assistantManager.getTypeAssistant().getWrapperType((SBasicTypeCGBase) firstType);
		
		writer.append(format(firstType));
		
		for(int i = 1; i < types.size(); i++)
		{
			PTypeCG currentType = types.get(i);
			
			if(assistantManager.getTypeAssistant().isBasicType(currentType))
				currentType = assistantManager.getTypeAssistant().getWrapperType((SBasicTypeCGBase) currentType);
			
			writer.append(", " + format(currentType));
		}
		
		return "<" + writer.toString() + ">";
	}
	
	public String formatEqualsBinaryExp(AEqualsBinaryExpCG node) throws AnalysisException
	{
		PTypeCG leftNodeType = node.getLeft().getType();

		if (isTupleOrRecord(leftNodeType)
				|| leftNodeType instanceof AStringTypeCG
				|| leftNodeType instanceof ATokenBasicTypeCG)
		{
			return handleEquals(node);
		}
		else if(leftNodeType instanceof SSeqTypeCG)
		{
			return handleSeqComparison(node);
		}
		else if(leftNodeType instanceof SSetTypeCG)
		{
			return handleSetComparison(node);
		}
		else if(leftNodeType instanceof SMapTypeCG)
		{
			return handleMapComparison(node);
		}
		
		return format(node.getLeft()) + " == " + format(node.getRight());
	}
	
	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node) throws AnalysisException
	{
		PTypeCG leftNodeType = node.getLeft().getType();

		if (isTupleOrRecord(leftNodeType)
				|| leftNodeType instanceof AStringTypeCG
				|| leftNodeType instanceof ATokenBasicTypeCG
				|| leftNodeType instanceof SSeqTypeCG
				|| leftNodeType instanceof SSetTypeCG
				|| leftNodeType instanceof SMapTypeCG)
		{
			ANotUnaryExpCG transformed = transNotEquals(node);
			return formatNotUnary(transformed.getExp());
		}
		
		return format(node.getLeft()) + " != " + format(node.getRight());
	}
	
	private static boolean isTupleOrRecord(PTypeCG type)
	{
		return type instanceof ARecordTypeCG || 
				type instanceof ATupleTypeCG;
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
		
		//Replace the "notEqual" expression with the transformed expression
		INode parent = notEqual.parent();

		//It may be the case that the parent is null if we execute e.g. [1] <> [1] in isolation
		if (parent != null)
		{
			parent.replaceChild(notEqual, notUnary);
			notEqual.parent(null);
		}
		
		return notUnary;
	}
	
	private String handleEquals(AEqualsBinaryExpCG valueType) throws AnalysisException
	{
		return format(valueType.getLeft()) + ".equals(" + format(valueType.getRight()) + ")";
	}
	
	private String handleSetComparison(AEqualsBinaryExpCG node) throws AnalysisException
	{
		return handleCollectionComparison(node, SET_UTIL_FILE);
	}
	
	private String handleSeqComparison(SBinaryExpCGBase node) throws AnalysisException
	{
		return handleCollectionComparison(node, SEQ_UTIL_FILE);
	}
	
	private String handleMapComparison(SBinaryExpCGBase node) throws AnalysisException
	{
		return handleCollectionComparison(node, MAP_UTIL_FILE);
	}
	
	private String handleCollectionComparison(SBinaryExpCGBase node, String className) throws AnalysisException
	{
		//In VDM the types of the equals are compatible when the AST passes the type check
		PExpCG leftNode = node.getLeft();
		PExpCG rightNode = node.getRight();
		
		final String EMPTY = ".isEmpty()";
		
		if(isEmptyCollection(leftNode.getType()))
		{
			return format(node.getRight()) + EMPTY;
		}
		else if(isEmptyCollection(rightNode.getType()))
		{
			return format(node.getLeft()) + EMPTY;
		}
	
		return className + ".equals(" + format(node.getLeft()) + ", " + format(node.getRight()) + ")";

	}
	
	private boolean isEmptyCollection(PTypeCG type)
	{
		if(type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seq = (SSeqTypeCG) type;

			return seq.getEmpty();
		}
		else if(type instanceof SSetTypeCG)
		{
			SSetTypeCG set = (SSetTypeCG) type;
			
			return set.getEmpty();
		}
		else if(type instanceof SMapTypeCG)
		{
			SMapTypeCG map = (SMapTypeCG) type;
			
			return map.getEmpty();
		}
		
		return false;
	}
	
	public String format(List<AFormalParamLocalDeclCG> params) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(params.size() <= 0)
			return "";
		
		final String finalPrefix = " final ";

		AFormalParamLocalDeclCG firstParam = params.get(0);
		writer.append(finalPrefix);
		writer.append(format(firstParam));
		
		for(int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalDeclCG param = params.get(i);
			writer.append(", ");
			writer.append(finalPrefix);
			writer.append(format(param));
		}
		return writer.toString();
	}
	
	public String formatSuperType(AClassDeclCG classDecl)
	{
		return classDecl.getSuperName() == null ? "" : "extends " + classDecl.getSuperName(); 
	}
	
	public String formatMaplets(AEnumMapExpCG mapEnum) throws AnalysisException
	{
		LinkedList<AMapletExpCG> members = mapEnum.getMembers();
		
		return "new Maplet[]{" + formatArgs(members) + "}";
	}
	
	public String formatArgs(List<? extends PExpCG> exps) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(exps.size() <= 0)
			return "";
		
		PExpCG firstExp = exps.get(0);
		writer.append(format(firstExp));
		
		for(int i = 1; i < exps.size(); i++)
		{
			PExpCG exp = exps.get(i);
			writer.append(", " + format(exp));
		}
		
		return writer.toString();
	}
	
	public boolean isNull(INode node)
	{
		return node == null;
	}
	
	public boolean isVoidType(PTypeCG node)
	{
		return node instanceof AVoidTypeCG;
	}
	
	public String formatInitialExp(PExpCG exp) throws AnalysisException
	{
		//private int a = 2; (when exp != null)
		//private int a; (when exp == null)
		
		return exp == null ? "" : " = " + format(exp);
	}
	
	public String formatOperationBody(PStmCG body) throws AnalysisException
	{
		String NEWLINE = "\n";
		if(body == null)
			return ";";
		
		StringWriter generatedBody = new StringWriter();
		
		generatedBody.append("{" + NEWLINE + NEWLINE);
		generatedBody.append(format(body));
		generatedBody.append(NEWLINE + "}");
		
		return generatedBody.toString();
	}
	
	public String formatTemplateParam(INode potentialBasicType) throws AnalysisException
	{
		if(potentialBasicType == null)
			return "";
		
		if(potentialBasicType instanceof AIntNumericBasicTypeCG ||
		   potentialBasicType instanceof ARealNumericBasicTypeCG)
			return "Number";
		else if(potentialBasicType instanceof ABoolBasicTypeCG)
			return "Boolean";
		else if(potentialBasicType instanceof ACharBasicTypeCG)
			return "Character";
		else
			return format(potentialBasicType);
	}
	
	public String generateEqualsMethod(ARecordDeclCG record) throws AnalysisException
	{
		//Construct equals method to be used for comparing records using
		//"structural" equivalence
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.getParams().add(new AObjectTypeCG());
		
		AExternalTypeCG returnType = new AExternalTypeCG();
		returnType.setInfo(null);
		returnType.setName("boolean");
		
		methodType.setResult(returnType);
		
		equalsMethod.setAccess(JAVA_PUBLIC);
		equalsMethod.setName("equals");
		equalsMethod.setMethodType(methodType);
		
		//Add the formal parameter "Object obj" to the method
		AFormalParamLocalDeclCG formalParam = new AFormalParamLocalDeclCG();
		
		String paramName = "obj";
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(paramName);
		
		formalParam.setPattern(idPattern);
		AObjectTypeCG paramType = new AObjectTypeCG();
		formalParam.setType(paramType);
		equalsMethod.getFormalParams().add(formalParam);
		
		ABlockStmCG equalsMethodBody = new ABlockStmCG();
		LinkedList<PStmCG> equalsStms = equalsMethodBody.getStatements();
		
		AReturnStmCG returnTypeComp = new AReturnStmCG();
		if (record.getFields().isEmpty())
		{
			//If the record has no fields equality is simply:
			//return obj instanceof RecordType
			returnTypeComp.setExp(JavaFormatAssistant.consInstanceOf(record, paramName));
			equalsStms.add(returnTypeComp);
			
		} else
		{

			// Construct the initial check:
			// if ((!obj instanceof RecordType))
			// return false;
			AIfStmCG ifStm = new AIfStmCG();
			ANotUnaryExpCG negated = new ANotUnaryExpCG();
			negated.setType(new ABoolBasicTypeCG());
			negated.setExp(JavaFormatAssistant.consInstanceOf(record, paramName));
			ifStm.setIfExp(negated);
			

			returnTypeComp.setExp(assistantManager.getExpAssistant().consBoolLiteral(false));
			ifStm.setThenStm(returnTypeComp);

			// If the inital check is passed we can safely cast the formal parameter
			// To the record type: RecordType other = ((RecordType) obj);
			String localVarName = "other";
			ABlockStmCG formalParamCasted = JavaFormatAssistant.consVarFromCastedExp(record, paramName, localVarName);

			// Next compare the fields of the instance with the fields of the formal parameter "obj":
			// return (field1 == obj.field1) && (field2 == other.field2)...
			LinkedList<AFieldDeclCG> fields = record.getFields();
			PExpCG previousComparisons = JavaFormatAssistant.consFieldComparison(record, fields.get(0), localVarName);

			for (int i = 1; i < fields.size(); i++)
			{
				previousComparisons = JavaFormatAssistant.extendAndExp(record, fields.get(i), previousComparisons, localVarName);
			}

			AReturnStmCG fieldsComparison = new AReturnStmCG();
			fieldsComparison.setExp(previousComparisons);
			
			equalsStms.add(ifStm);
			equalsStms.add(formalParamCasted);
			equalsStms.add(fieldsComparison);
		}

		equalsMethod.setBody(equalsMethodBody);
		
		record.getMethods().add(equalsMethod);
		
		return format(equalsMethod);
	}
	
	public String generateHashcodeMethod(ARecordDeclCG record) throws AnalysisException
	{
		String hashCode = "hashCode";
		
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
		
		hashcodeMethod.setAccess(JAVA_PUBLIC);
		hashcodeMethod.setName(hashCode);

		String intTypeName = JAVA_INT;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		intBasicType.setName(intTypeName);
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(intBasicType);
		
		hashcodeMethod.setMethodType(methodType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		if(record.getFields().isEmpty())
		{
			AExternalExpCG zero = new AExternalExpCG();
			zero.setType(intBasicType);
			zero.setTargetLangExp("0");
			returnStm.setExp(zero);
		}
		else
		{
			returnStm.setExp(JavaFormatAssistant.consUtilCallUsingRecFields(record, intBasicType, hashCode));
		}
		
		hashcodeMethod.setBody(returnStm);

		record.getMethods().add(hashcodeMethod);
		
		return format(hashcodeMethod);
	}
	
	public String generateToStringMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG toStringMethod = new AMethodDeclCG();
		
		toStringMethod.setAccess(JAVA_PUBLIC);
		toStringMethod.setName("toString");

		AStringTypeCG returnType = new AStringTypeCG();
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType);
		
		toStringMethod.setMethodType(methodType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		if (record.getFields().isEmpty())
		{
			AStringLiteralExpCG emptyRecStr = new AStringLiteralExpCG();
			emptyRecStr.setIsNull(false);
			emptyRecStr.setType(returnType);
			emptyRecStr.setValue(String.format("mk_%s()", record.getName()));
			
			returnStm.setExp(emptyRecStr);
		} else
		{
			returnStm.setExp(JavaFormatAssistant.consRecToStringCall(record, returnType, "recordToString"));
		}
		
		toStringMethod.setBody(returnStm);
		
		record.getMethods().add(toStringMethod);
		
		return format(toStringMethod);
	}

	public boolean isStringLiteral(PExpCG exp)
	{
		return exp instanceof AStringLiteralExpCG;
	}
	
	public boolean isSeqType(PExpCG exp)
	{
		return exp.getType() instanceof SSeqTypeCG;
	}
	
	public boolean isMapType(PExpCG exp)
	{
		return exp.getType() instanceof SMapTypeCG;
	}
	
	public boolean isStringType(PTypeCG type)
	{
		return type instanceof AStringTypeCG; 
	}
	
	public boolean isStringType(PExpCG exp)
	{
		return exp.getType() instanceof AStringTypeCG; 
	}
	
	public boolean isCharType(PTypeCG type)
	{
		return type instanceof ACharBasicTypeCG; 
	}
	
	public String buildString(List<PExpCG> exps) throws AnalysisException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("new String(new char[]{");
		
		if(exps.size() > 0)
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
	
	public String formatElementType(PTypeCG type) throws AnalysisException
	{
		if(type instanceof SSetTypeCG)
		{
			SSetTypeCG setType = (SSetTypeCG) type;
			
			return format(setType.getSetOf());
		}
		else if(type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seqType = (SSeqTypeCG) type;
			
			return format(seqType.getSeqOf());
		}
		
		throw new AnalysisException("Expected set or seq type when trying to format element type");
		
	}
	
	public String nextVarName(String prefix)
	{
		return tempVarNameGen.nextVarName(prefix);
	}
	
	public PTypeCG findElementType(AApplyObjectDesignatorCG designator)
	{
		int appliesCount = 0;
		
		PObjectDesignatorCG object = designator.getObject();

		while(object != null)
		{
			if(object instanceof AIdentifierObjectDesignatorCG)
			{
				AIdentifierObjectDesignatorCG id = (AIdentifierObjectDesignatorCG) object;
			
				PTypeCG type = id.getExp().getType();
				
				int methodTypesCount = 0;
				
				while (type instanceof AMethodTypeCG)
				{
					methodTypesCount++;
					AMethodTypeCG methodType = (AMethodTypeCG) type;
					type = methodType.getResult();
				}
				
				while(type instanceof SSeqTypeCG || type instanceof SMapTypeCG)
				{
					if(type instanceof SSeqTypeCG)
					{
						type = ((SSeqTypeCG) type).getSeqOf();
					}

					if(type instanceof SMapTypeCG)
					{
						type = ((SMapTypeCG) type).getTo();
					}
					
					if (appliesCount == methodTypesCount)
					{
						return type;						
					}
					
					methodTypesCount++;
				}

				return null;
			}
			else if(object instanceof AApplyObjectDesignatorCG)
			{
				AApplyObjectDesignatorCG applyObj = (AApplyObjectDesignatorCG) object;
				appliesCount++;
				object = applyObj.getObject();
			}
			else
			{
				return null;
			}
		}
		
		return null;
	}
	
	public boolean isLoopVar(AVarLocalDeclCG localVar)
	{
		return localVar.parent() instanceof AForLoopStmCG;
	}
	
	public boolean isLambda(AApplyExpCG applyExp)
	{
		PExpCG root = applyExp.getRoot();
		
		if(root instanceof AApplyExpCG && root.getType() instanceof AMethodTypeCG)
			return true;
		
		if(!(root instanceof SVarExpCG))
			return false;
		
		SVarExpCG varExp = (SVarExpCG) root;
		
		return varExp.getIsLambda() != null && varExp.getIsLambda();
	}
	
	public String escapeStr(String str)
	{
		String escaped = "";
		for(int i = 0; i < str.length(); i++)
		{
			char currentChar = str.charAt(i);
			escaped += GeneralUtils.isEscapeSequence(currentChar) ? StringEscapeUtils.escapeJava(currentChar + "") : (currentChar + "");
		}
		
		return escaped;
	}
	
	public String escapeChar(char c)
	{
		return GeneralUtils.isEscapeSequence(c) ? StringEscapeUtils.escapeJavaScript(c + "") : c + "";
	}
}
