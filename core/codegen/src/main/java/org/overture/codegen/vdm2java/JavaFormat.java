package org.overture.codegen.vdm2java;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.analysis.OoAstAnalysis;
import org.overture.codegen.assistant.DeclAssistantCG;
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
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCGBase;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCGBase;
import org.overture.codegen.cgast.types.SSeqTypeCGBase;
import org.overture.codegen.merging.MergeVisitor;

public class JavaFormat
{
	private List<AClassDeclCG> classes;
	
	public JavaFormat(List<AClassDeclCG> classes)
	{
		this.classes = classes;
	}
	
	public JavaFormat()
	{
	}
	
	public String format(INode node) throws AnalysisException
	{		
		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.constructTemplateCallables(this, OoAstAnalysis.class));
		
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
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
		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.constructTemplateCallables(this, OoAstAnalysis.class));
		StringWriter writer = new StringWriter();

		exp.apply(mergeVisitor, writer);
		String formattedExp = writer.toString();
		
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
	
	public String formatElementType(AElemsUnaryExpCG exp) throws AnalysisException
	{
		PTypeCG type = exp.getType();
		
		if(type instanceof ASetSetTypeCG)
		{
			ASetSetTypeCG seqType = (ASetSetTypeCG) type;
			return formatTemplateParam(seqType.getSetOf());
		}
		
		throw new AnalysisException("Type was not a sequence type!");
	}
	
	public String generateCloneMethod(ARecordDeclCG record) throws AnalysisException
	{
		AMethodDeclCG method = new AMethodDeclCG();

		method.parent(record);
		method.setAccess("public");
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
		constructor.setAccess("public");
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
		//FIXME: Only works for simple types, i.e. not references
		//Operator pec?
		
		/*
		 * Things to consider:
		 * 
		 * Collections: sets, sequences and maps
		 * Classes: Maps to == 
		 * Type defs: Not supported anyway
		 * Records: Not supported anyway
		 * Primitive types: Maps to == 
		 * 
		 */
		PTypeCG leftNodeType = node.getLeft().getType();
		
		if(leftNodeType instanceof SSeqTypeCGBase)
		{
			return handleSeqComparison(node, false);
		}
		else if(leftNodeType instanceof ARecordTypeCG)
		{
			return handleRecordComparison(node);
		}
		
		return format(node.getLeft()) + " == " + format(node.getRight());
	}
	
	public String handleRecordComparison(AEqualsBinaryExpCG recordComparison) throws AnalysisException
	{
		return format(recordComparison.getLeft()) + ".equals(" + format(recordComparison.getRight()) + ")";
	}
	
	public String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node) throws AnalysisException
	{
		//FIXME: Same problems as for equals. In addition, this method lacks support for collections
		PTypeCG leftNodeType = node.getLeft().getType();

		if (leftNodeType instanceof SSeqTypeCGBase)
		{
			return handleSeqComparison(node, true);
		}
		
		return format(node.getLeft()) + " != " + format(node.getRight());
	}
	
	private String handleSeqComparison(SBinaryExpCGBase node, boolean notEquals) throws AnalysisException
	{
		String prefix = notEquals ? "!" : "";
		
		//In VDM the types of the equals are compatible when the AST passes the type check
		PExpCG leftNode = node.getLeft();
		PExpCG rightNode = node.getRight();
		
		if(isEmptySeq(leftNode))
		{
			return prefix + format(node.getRight()) + ".isEmpty()";
		}
		else if(isEmptySeq(rightNode))
		{
			return prefix + format(node.getLeft()) + ".isEmpty()";
		}
	
		return prefix + "Utils.seqEquals(" + format(node.getLeft()) + ", " + format(node.getRight()) + ")";

	}
	
	private boolean isEmptySeq(PExpCG exp)
	{
		if(exp instanceof AEnumSeqExpCG)
		{
			AEnumSeqExpCG v = (AEnumSeqExpCG) exp;

			return v.getMembers().size() == 0;
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
	
	public String formatArgs(List<PExpCG> exps) throws AnalysisException
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
		if(body == null)
			return ";";
		
		StringWriter generatedBody = new StringWriter();
		
		generatedBody.append("{\r\n\r\n");//TODO: USE PROPER CONSTANT
		generatedBody.append(format(body));
		generatedBody.append("\r\n}");//TODO: USE PROPER CONSTANT
		
		return generatedBody.toString();
	}
	
	public String formatTemplateParam(INode potentialBasicType) throws AnalysisException
	{
		if(potentialBasicType == null)
			return "";
		
		if(potentialBasicType instanceof AIntNumericBasicTypeCG)
			return "Long";
		else if(potentialBasicType instanceof ARealNumericBasicTypeCG)
			return "Double";
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
	
	public boolean shouldClone(AVariableExpCG exp)
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
	
	private boolean cloneNotNeeded(INode node)
	{
		return 	   node instanceof AFieldExpCG
				|| node instanceof AFieldNumberExpCG
				|| node instanceof AEqualsBinaryExpCG
				|| node instanceof ANotEqualsBinaryExpCG
				|| node instanceof AAddrEqualsBinaryExpCG
				|| node instanceof AAddrNotEqualsBinaryExpCG
				|| isCallToUtil(node);
	}
	
	private boolean isCallToUtil(INode node)
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
		return type instanceof ARecordTypeCG || type instanceof ATupleTypeCG;
	}
	
	public String generateEqualsMethod(ARecordDeclCG record) throws AnalysisException
	{
		//Construct equals method to be used for comparing records using
		//"structural" equivalence
		AMethodDeclCG equalsMethod = new AMethodDeclCG();
		
		equalsMethod.parent(record);
		equalsMethod.setAccess("public");
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
		returnIncompatibleTypes.setExp(JavaFormatAssistant.consBoolLiteral(false));
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
		//TODO: Put override annotations in record method overrides?
		AMethodDeclCG hashcodeMethod = new AMethodDeclCG();
		
		hashcodeMethod.parent(record);
		hashcodeMethod.setAccess("public");
		hashcodeMethod.setName("hashCode");

		String intTypeName = "int";
		AExternalTypeCG intBasicType = new AExternalTypeCG();
		intBasicType.setName(intTypeName);
		hashcodeMethod.setReturnType(intBasicType);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		LinkedList<AFieldDeclCG> fields = record.getFields();
		
		AExplicitVariableExpCG hashCodeMember = new AExplicitVariableExpCG();
		hashCodeMember.setType(intBasicType.clone());
		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(IJavaCodeGenConstants.UTILS_FILE);
		hashCodeMember.setClassType(classType);
		hashCodeMember.setName("hashCode");
		AApplyExpCG hashcodeCall = new AApplyExpCG();
		hashcodeCall.setType(intBasicType.clone());
		hashcodeCall.setRoot(hashCodeMember);
		LinkedList<PExpCG> args = hashcodeCall.getArgs();

		for (AFieldDeclCG field : fields)
		{
			AVariableExpCG nextArg = new AVariableExpCG();
			nextArg.setOriginal(field.getName());
			nextArg.setType(field.getType().clone());
			args.add(nextArg);
		}
		
		returnStm.setExp(hashcodeCall);
		
		hashcodeMethod.setBody(returnStm);
		
		return format(hashcodeMethod);
	}
}
