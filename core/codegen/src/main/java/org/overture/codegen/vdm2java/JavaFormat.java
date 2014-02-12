package org.overture.codegen.vdm2java;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AAddrEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInSetBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIndicesUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ASetProperSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASizeUnaryExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCGBase;
import org.overture.codegen.cgast.expressions.SLiteralExpCGBase;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCGBase;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.ooast.OoAstAnalysis;

public class JavaFormat
{
	private static final String JAVA_NUMBER = "Number";
	
	public String getJavaNumber()
	{
		return JAVA_NUMBER;
	}
	
	private static final String JAVA_PUBLIC = "public";
	private static final String JAVA_INT = "int";
	
	private List<AClassDeclCG> classes;
	private JavaTempVarNameGen tempVarNameGen;
	
	public JavaFormat(List<AClassDeclCG> classes)
	{
		this();
		this.classes = classes;
	}
	
	public JavaFormat()
	{
		this.tempVarNameGen = new JavaTempVarNameGen();
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
		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.constructTemplateCallables(this, OoAstAnalysis.class));
		
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
			
		if (parent instanceof SNumericBinaryExpCG)
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
		return !(node instanceof SNumericBinaryExpCG)
				&& !(node instanceof SLiteralExpCGBase)
				&& !(node instanceof AIsolationUnaryExpCG)
				&& !(node instanceof SUnaryExpCG);
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

		method.parent(record);
		method.setAccess(JAVA_PUBLIC);
		method.setName("clone");
		
		AClassDeclCG defClass = record.getAncestor(AClassDeclCG.class);
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(defClass.getName());
		typeName.setName(record.getName());
		ARecordTypeCG returnType = new ARecordTypeCG();
		returnType.setName(typeName);
		method.setReturnType(returnType);
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(returnType.clone());
		newExp.setName(typeName.clone());
		LinkedList<PExpCG> args = newExp.getArgs();
		
		LinkedList<AFieldDeclCG> fields = record.getFields();
		for (AFieldDeclCG field : fields)
		{
			String name = field.getName();
			
			AVariableExpCG varExp = new AVariableExpCG();
			varExp.setOriginal(name);
			varExp.setType(field.getType().clone());
			args.add(varExp);
		}
		
		AReturnStmCG body = new AReturnStmCG();
		body.setExp(newExp);
		method.setBody(body);
		
		return format(method);
	}
	
	public String formatRecordConstructor(ARecordDeclCG record) throws AnalysisException
	{
		LinkedList<AFieldDeclCG> fields = record.getFields();
		
		AMethodDeclCG constructor = new AMethodDeclCG();
		//Since Java does not have records but the OO AST does a record is generated as a Java class.
		//To make sure that the record can be instantiated we must explicitly add a constructor.
		constructor.parent(record);
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

			//Construct formal parameter of the constructor
			AFormalParamLocalDeclCG formalParam = new AFormalParamLocalDeclCG();
			formalParam.setName(paramName);
			formalParam.setType(type);
			formalParams.add(formalParam);
			
			//Construct the initialization of the record field using the
			//corresponding formal parameter.
			AAssignmentStmCG assignment = new AAssignmentStmCG();
			AIdentifierStateDesignatorCG id = new AIdentifierStateDesignatorCG();
			id.setName(name);
			
			AVariableExpCG varExp = new AVariableExpCG();
			varExp.setType(field.getType().clone());
			varExp.setOriginal(paramName);

			assignment.setTarget(id);
			
			if (!TypeAssistantCG.isBasicType(varExp.getType()))
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
		
		return format(constructor);
	}
	
	public String formatTemplateTypes(LinkedList<PTypeCG> types) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(types.size() <= 0)
			return "";
		
		PTypeCG firstType = types.get(0);
		
		if(TypeAssistantCG.isBasicType(firstType))
			firstType = TypeAssistantCG.getWrapperType((SBasicTypeCGBase) firstType);
		
		writer.append(format(firstType));
		
		for(int i = 1; i < types.size(); i++)
		{
			PTypeCG currentType = types.get(i);
			
			if(TypeAssistantCG.isBasicType(currentType))
				currentType = TypeAssistantCG.getWrapperType((SBasicTypeCGBase) currentType);
			
			writer.append(", " + format(currentType));
		}
		
		return "<" + writer.toString() + ">";
	}
	
	public String formatEqualsBinaryExp(AEqualsBinaryExpCG node) throws AnalysisException
	{
		PTypeCG leftNodeType = node.getLeft().getType();
		
		if(isTupleOrRecord(leftNodeType) || leftNodeType instanceof AStringTypeCG)
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
		
		return format(node.getLeft()) + " == " + format(node.getRight());
	}
	
	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node) throws AnalysisException
	{
		PTypeCG leftNodeType = node.getLeft().getType();

		if (isTupleOrRecord(leftNodeType) || leftNodeType instanceof AStringTypeCG)
		{
			ANotUnaryExpCG transformed = transNotEquals(node);
			return formatNotUnary(transformed.getExp());
		}
		else if(leftNodeType instanceof SSeqTypeCG)
		{
			ANotUnaryExpCG transformed = transNotEquals(node);
			return formatNotUnary(transformed.getExp());
		}
		else if(leftNodeType instanceof SSetTypeCG)
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
		equal.setLeft(notEqual.getLeft());
		equal.setRight(notEqual.getRight());
		
		notUnary.setExp(equal);
		
		//Replace the "notEqual" expression with the transformed expression
		notUnary.parent(notEqual.parent());
		notEqual.parent(null);
		
		return notUnary;
	}
	
	private String handleEquals(AEqualsBinaryExpCG valueType) throws AnalysisException
	{
		return format(valueType.getLeft()) + ".equals(" + format(valueType.getRight()) + ")";
	}
	
	private String handleSetComparison(AEqualsBinaryExpCG node) throws AnalysisException
	{
		return handleCollectionComparison(node, IJavaCodeGenConstants.SET_UTIL_FILE);
	}
	
	private String handleSeqComparison(SBinaryExpCGBase node) throws AnalysisException
	{
		return handleCollectionComparison(node, IJavaCodeGenConstants.SEQ_UTIL_FILE);
	}
	
	private String handleCollectionComparison(SBinaryExpCGBase node, String className) throws AnalysisException
	{
		//In VDM the types of the equals are compatible when the AST passes the type check
		PExpCG leftNode = node.getLeft();
		PExpCG rightNode = node.getRight();
		
		if(isEmptyCollection(leftNode))
		{
			return format(node.getRight()) + ".isEmpty()";
		}
		else if(isEmptyCollection(rightNode))
		{
			return format(node.getLeft()) + ".isEmpty()";
		}
	
		return className + ".equals(" + format(node.getLeft()) + ", " + format(node.getRight()) + ")";

	}
	
	private boolean isEmptyCollection(PExpCG exp)
	{
		if(exp instanceof AEnumSeqExpCG)
		{
			AEnumSeqExpCG v = (AEnumSeqExpCG) exp;

			return v.getMembers().isEmpty();
		}
		else if(exp instanceof AEnumSetExpCG)
		{
			AEnumSetExpCG v = (AEnumSetExpCG) exp;
			
			return v.getMembers().isEmpty();
		}
		
		return false;
	}
	
	public String format(List<AFormalParamLocalDeclCG> params) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(params.size() <= 0)
			return "";
		
		AFormalParamLocalDeclCG firstParam = params.get(0);
		writer.append(format(firstParam));
		
		for(int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalDeclCG param = params.get(i);
			writer.append(", " + format(param));
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
		String NEWLINE = "\r\n";
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

	public boolean cloneMember(AFieldNumberExpCG exp)
	{
		//Generally tuples need to be cloned, for example, if they
		//contain a record field (that must be cloned)
		
		if(exp.parent() instanceof AFieldNumberExpCG)
			return false;
		
		PTypeCG type = exp.getTuple().getType();
		
		if(type instanceof ATupleTypeCG)
		{
			
			ATupleTypeCG tupleType = (ATupleTypeCG) type;
			
			long field = exp.getField();
			PTypeCG fieldType = tupleType.getTypes().get((int) (field - 1));
			
			if(usesStructuralEquivalence(fieldType))
				return true;
		}
		
		return false;
	}
	
	public boolean cloneMember(AFieldExpCG exp)
	{
		INode parent = exp.parent();
		if (cloneNotNeeded(parent))
			return false;
		
		PTypeCG type = exp.getObject().getType();
		
		if(type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) type;
			
			String memberName = exp.getMemberName();
			
			AFieldDeclCG memberField = DeclAssistantCG.getFieldDecl(classes, recordType, memberName);
			
			if (memberField != null && usesStructuralEquivalence(memberField.getType()))
				return true;
		}
		
		return false;
	}
	
	public boolean shouldClone(PExpCG exp)//Called with AVariableExpCG and AHeadUnaryExpCG 
	{
		INode parent = exp.parent();
		if (cloneNotNeeded(parent))
		{
			return false;
		}
		
		PTypeCG type = exp.getType();
		if(usesStructuralEquivalence(type))
		{
			if(parent instanceof ANewExpCG)
			{
				ANewExpCG newExp = (ANewExpCG) parent;
				PTypeCG newExpType = newExp.getType();
				
				if(usesStructuralEquivalence(newExpType))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean cloneNotNeeded(INode parent)
	{
		return 	   parent instanceof AFieldExpCG
				|| parent instanceof AFieldNumberExpCG
				|| parent instanceof AApplyExpCG
				|| parent instanceof AEqualsBinaryExpCG
				|| parent instanceof ANotEqualsBinaryExpCG
				|| parent instanceof AAddrEqualsBinaryExpCG
				|| parent instanceof AAddrNotEqualsBinaryExpCG
				|| parent instanceof AForAllStmCG
				|| cloneNotNeededCollectionOperator(parent)
				|| cloneNotNeededUtilCall(parent);
	}
	
	private boolean cloneNotNeededCollectionOperator(INode parent)
	{
		return cloneNotNeededSeqOperators(parent)
				|| cloneNotNeededSetOperators(parent);
	}

	private boolean cloneNotNeededSeqOperators(INode parent)
	{
		return parent instanceof ASizeUnaryExpCG
				|| parent instanceof AIndicesUnaryExpCG
				|| parent instanceof AHeadUnaryExpCG;
	}

	private boolean cloneNotNeededSetOperators(INode parent)
	{
		return parent instanceof AInSetBinaryExpCG
				|| parent instanceof ASetSubsetBinaryExpCG
				|| parent instanceof ASetProperSubsetBinaryExpCG;
	}
	
	private boolean cloneNotNeededUtilCall(INode node)
	{
		if(!(node instanceof AApplyExpCG))
			return false;
		
		AApplyExpCG applyExp = (AApplyExpCG) node;
		PExpCG root = applyExp.getRoot();
		
		if(!(root instanceof AExplicitVariableExpCG))
			return false;
		
		AExplicitVariableExpCG explicitVar = (AExplicitVariableExpCG) root;
		
		AClassTypeCG classType = explicitVar.getClassType();
		
		return classType != null && classType.getName().equals(IJavaCodeGenConstants.UTILS_FILE);
	}
	
	private boolean usesStructuralEquivalence(PTypeCG type)
	{
		return type instanceof ARecordTypeCG || type instanceof ATupleTypeCG
				|| type instanceof SSeqTypeCG || type instanceof SSetTypeCG
				|| type instanceof SMapTypeCG;
	}
	
	public String generateEqualsMethod(ARecordDeclCG record) throws AnalysisException
	{
		//Construct equals method to be used for comparing records using
		//"structural" equivalence
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		equalsMethod.parent(record);
		equalsMethod.setAccess(JAVA_PUBLIC);
		equalsMethod.setName("equals");
		equalsMethod.setReturnType(new ABoolBasicTypeCG());
		
		//Add the formal parameter "Object obj" to the method
		AFormalParamLocalDeclCG formalParam = new AFormalParamLocalDeclCG();
		String paramName = "obj";
		formalParam.setName(paramName);
		AObjectTypeCG paramType = new AObjectTypeCG();
		formalParam.setType(paramType);
		equalsMethod.getFormalParams().add(formalParam);
		
		//Construct the initial check:
		//if ((!obj instanceof RecordType))
		//	return false;
		AIfStmCG ifStm = new AIfStmCG();
		ANotUnaryExpCG negated = new ANotUnaryExpCG();
		negated.setType(new ABoolBasicTypeCG());
		negated.setExp(JavaFormatAssistant.consInstanceOf(record, paramName));
		ifStm.setIfExp(negated);
		AReturnStmCG returnIncompatibleTypes = new AReturnStmCG();
		returnIncompatibleTypes.setExp(ExpAssistantCG.consBoolLiteral(false));
		ifStm.setThenStm(returnIncompatibleTypes);
		
		//If the inital check is passed we can safely cast the formal parameter
		//To the record type: RecordType other = ((RecordType) obj);
		String localVarName = "other";
		ABlockStmCG formalParamCasted = JavaFormatAssistant.consVarFromCastedExp(record, paramName, localVarName);
		
		//Next compare the fields of the instance with the fields of the formal parameter "obj":
		//return (field1 == obj.field1) && (field2 == other.field2)...
		LinkedList<AFieldDeclCG> fields = record.getFields();
		PExpCG previousComparisons = JavaFormatAssistant.consFieldComparison(record, fields.get(0), localVarName); 

		for (int i = 1; i < fields.size(); i++)
		{
			previousComparisons = JavaFormatAssistant.extendAndExp(record, fields.get(i), previousComparisons, localVarName);
		}

		AReturnStmCG fieldsComparison = new AReturnStmCG();
		fieldsComparison.setExp(previousComparisons);

		//Finally add the constructed statements to the equals method body
		ABlockStmCG equalsMethodBody = new ABlockStmCG();
		LinkedList<PStmCG> equalsStms = equalsMethodBody.getStatements();
		equalsStms.add(ifStm);
		equalsStms.add(formalParamCasted);
		equalsStms.add(fieldsComparison);
		equalsMethod.setBody(equalsMethodBody);
		
		return format(equalsMethod);
	}
	
	public String generateHashcodeMethod(ARecordDeclCG record) throws AnalysisException
	{
		String hashCode = "hashCode";
		
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
		
		hashcodeMethod.parent(record);
		hashcodeMethod.setAccess(JAVA_PUBLIC);
		hashcodeMethod.setName(hashCode);

		String intTypeName = JAVA_INT;
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		intBasicType.setName(intTypeName);
		hashcodeMethod.setReturnType(intBasicType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(JavaFormatAssistant.consUtilCallUsingRecFields(record, intBasicType, hashCode));
		
		hashcodeMethod.setBody(returnStm);
		
		return format(hashcodeMethod);
	}
	
	public String generateToStringMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG toStringMethod = new AMethodDeclCG();
		
		toStringMethod.parent(record);
		toStringMethod.setAccess(JAVA_PUBLIC);
		toStringMethod.setName("toString");

		AStringTypeCG returnType = new AStringTypeCG();
		toStringMethod.setReturnType(returnType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		returnStm.setExp(JavaFormatAssistant.consRecToStringCall(record, returnType, "recordToString"));
		
		toStringMethod.setBody(returnStm);
		
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
	
	public String nextVarName()
	{
		return tempVarNameGen.nextVarName();
	}
}
