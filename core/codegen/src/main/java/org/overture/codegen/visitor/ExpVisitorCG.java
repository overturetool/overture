package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AFloorUnaryExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ADistConcatExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AFloorUnaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIndicesUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
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
import org.overture.codegen.cgast.expressions.AReverseUnaryExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqModificationBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.AXorBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.ooast.OoAstInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;

public class ExpVisitorCG extends AbstractVisitorCG<OoAstInfo, PExpCG>
{
	private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG()
	{
		this.expAssistant = new ExpAssistantCG();
	}
	
	@Override
	public PExpCG caseANilExp(ANilExp node, OoAstInfo question)
			throws AnalysisException
	{
		return new ANullExpCG();
	}
	
	@Override
	public PExpCG caseAIsOfClassExp(AIsOfClassExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		AClassType classType = node.getClassType();
		PExp objRef = node.getExp();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PTypeCG classTypeCg = classType.apply(question.getTypeVisitor(), question);

		if (!(classTypeCg instanceof AClassTypeCG))
			throw new AnalysisExceptionCG("Unexpected class type encountered for "
					+ AIsOfClassExp.class.getName() + ". Expected class type: "
					+ AClassTypeCG.class.getName() + ". Got: " + typeCg.getClass().getName(), node.getLocation());

		PExpCG objRefCg = objRef.apply(question.getExpVisitor(), question);

		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
		instanceOfExp.setType(typeCg);
		instanceOfExp.setClassType((AClassTypeCG) classTypeCg);
		instanceOfExp.setObjRef(objRefCg);
		
		return instanceOfExp;
	}
	
	@Override
	public PExpCG caseASetEnumSetExp(ASetEnumSetExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		if(!(type instanceof ASetType))
			throw new AnalysisExceptionCG("Unexpected set type for set enumeration expression: " + type.getClass().getName(), node.getLocation());
		
		LinkedList<PExp> members = node.getMembers();
		
		AEnumSetExpCG enumSet = new AEnumSetExpCG();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		enumSet.setType(typeCg);
		LinkedList<PExpCG> membersCg = enumSet.getMembers();
		
		for(PExp member : members)
		{
			membersCg.add(member.apply(question.getExpVisitor(), question));
		}
	
		return enumSet;
	}
	
	@Override
	public PExpCG caseAIfExp(AIfExp node, OoAstInfo question)
			throws AnalysisException
	{
		PExpCG testExp = node.getTest().apply(question.getExpVisitor(), question);
		PExpCG thenExp = node.getThen().apply(question.getExpVisitor(), question);
		PTypeCG expectedType = node.getType().apply(question.getTypeVisitor(), question);
		
		ATernaryIfExpCG ternaryIf = new ATernaryIfExpCG();
		
		ternaryIf.setCondition(testExp);
		ternaryIf.setTrueValue(thenExp);
		ternaryIf.setType(expectedType);
		
		LinkedList<AElseIfExp> elseExpList = node.getElseList();
		
		ATernaryIfExpCG nextTernaryIf = ternaryIf;
		
		for (AElseIfExp currentElseExp : elseExpList)
		{
			ATernaryIfExpCG tmp = new ATernaryIfExpCG();
			
			testExp = currentElseExp.getElseIf().apply(question.getExpVisitor(), question);
			thenExp = currentElseExp.getThen().apply(question.getExpVisitor(), question);
			expectedType = currentElseExp.getType().apply(question.getTypeVisitor(), question);
			
			tmp.setCondition(testExp);
			tmp.setTrueValue(thenExp);
			tmp.setType(expectedType);
			
			nextTernaryIf.setFalseValue(tmp);
			nextTernaryIf = tmp;
			
		}
		
		PExpCG elseExp = node.getElse().apply(question.getExpVisitor(), question);
		nextTernaryIf.setFalseValue(elseExp);
		
		if(node.parent() instanceof SBinaryExp)
			return ExpAssistantCG.isolateExpression(ternaryIf);
		
		return ternaryIf;
	}
	
	@Override
	public PExpCG caseATupleExp(ATupleExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		LinkedList<PExp> args = node.getArgs();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		ATupleExpCG tupleExp = new ATupleExpCG();
		tupleExp.setType(typeCg);
		
		for (PExp exp : args)
		{
			PExpCG expCg = exp.apply(question.getExpVisitor(), question);
			tupleExp.getArgs().add(expCg);
		}
		
		return tupleExp;
	}
	
	@Override
	public PExpCG caseAFieldNumberExp(AFieldNumberExp node, OoAstInfo question)
			throws AnalysisException
	{
		long fieldCg = node.getField().getValue();
		PType type = node.getType();
		PExp tuple = node.getTuple();

		AFieldNumberExpCG fieldNoExp = new AFieldNumberExpCG();
		PExpCG tupleCg = tuple.apply(question.getExpVisitor(), question);
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		fieldNoExp.setField(fieldCg);
		fieldNoExp.setType(typeCg);
		fieldNoExp.setTuple(tupleCg);
		
		return fieldNoExp;
	}
	
	@Override
	public PExpCG caseAFuncInstatiationExp(AFuncInstatiationExp node,
			OoAstInfo question) throws AnalysisException
	{
		String name = node.getExpdef().getName().getName();
		LinkedList<PType> actualTypes = node.getActualTypes();
		
		AMethodInstantiationExpCG methodInst = new AMethodInstantiationExpCG();
		methodInst.setType(null);
		methodInst.setName(name);
		
		for (PType type : actualTypes)
		{
			PTypeCG typeCG = type.apply(question.getTypeVisitor(), question);
			methodInst.getActualTypes().add(typeCG);
		}
		
		return methodInst;
	}
	
	@Override
	public PExpCG caseALetDefExp(ALetDefExp node, OoAstInfo question)
			throws AnalysisException
	{
		ALetDefExpCG localDefExp = new ALetDefExpCG();
	
		DeclAssistantCG.setLocalDefs(node.getLocalDefs(), localDefExp.getLocalDefs(), question);
		
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		localDefExp.setExp(exp);
		
		return localDefExp;
	}

	
	@Override
	public PExpCG caseAMkTypeExp(AMkTypeExp node, OoAstInfo question)
			throws AnalysisException
	{
		ARecordInvariantType recType = node.getRecordType();
		
		if(recType == null)
			throw new AnalysisExceptionCG("Expected record type for mk_<type> expression.", node.getLocation());
		
		PTypeCG typeCg = recType.apply(question.getTypeVisitor(), question);
		
		if(!(typeCg instanceof ARecordTypeCG))
			throw new AnalysisExceptionCG("Expected record type but got: " + typeCg.getClass().getName() + " in 'mk_' expression", node.getLocation());
		
		ARecordTypeCG recordTypeCg = (ARecordTypeCG) typeCg;
		
		LinkedList<PExp> nodeArgs = node.getArgs();
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(recordTypeCg);
		newExp.setName(recordTypeCg.getName().clone());

		LinkedList<PExpCG> newExpArgs = newExp.getArgs();
		
		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}
		
		return newExp;
	}
	
	@Override
	public PExpCG caseASelfExp(ASelfExp node, OoAstInfo question)
			throws AnalysisException
	{
		return new ASelfExpCG();
	}
	
	@Override
	public PExpCG caseAReverseUnaryExp(AReverseUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		PType type = node.getType();
		
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		AReverseUnaryExpCG reverse = new AReverseUnaryExpCG();
		reverse.setExp(expCg);
		
		if(type instanceof SSeqType)
		{
			PTypeCG seqType = ((SSeqType) type).getSeqof().apply(question.getTypeVisitor(), question);
			reverse.setType(seqType);
		}
		else
		{
			throw new AnalysisExceptionCG("Unexpected sequence type for reverse unary expression: " + type.getClass().getName(), node.getLocation());
		}

		return reverse;
	}
	
	@Override
	public PExpCG caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		PType type = node.getType();
		
		if(!(exp instanceof ASeqEnumSeqExp))
			throw new AnalysisExceptionCG("Unexpected expression for distributed concatenation: " + exp.getClass().getName(), node.getLocation());
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		ASeqEnumSeqExp seqEnumExp = (ASeqEnumSeqExp) exp;
		
		LinkedList<PExp> members = seqEnumExp.getMembers();
		LinkedList<PExpCG> membersCg = new LinkedList<PExpCG>();
		for(PExp member : members)
		{
			PExpCG memberCg = member.apply(question.getExpVisitor(), question);
			membersCg.add(memberCg);
		}
		
		ADistConcatExpCG distConcat = new ADistConcatExpCG();
		distConcat.setType(typeCg);
		distConcat.setMembers(membersCg);
		
		return distConcat;
	}
	
	@Override
	public PExpCG caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node,  new ASeqConcatBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ASeqModificationBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAMapEnumMapExp(AMapEnumMapExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		AEnumMapExpCG enumMap = new AEnumMapExpCG();
		enumMap.setType(typeCg);
		
		LinkedList<AMapletExp> members = node.getMembers();
		for (PExp member : members)
		{
			PExpCG exp = member.apply(question.getExpVisitor(), question);
			
			if(!(exp instanceof AMapletExpCG))
				throw new AnalysisExceptionCG("Got expected map enumeration member: " + exp, member.getLocation());
			
			enumMap.getMembers().add((AMapletExpCG) exp);
		}
		
		return enumMap;
	}
	
	@Override
	public PExpCG caseAMapletExp(AMapletExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		PExp left = node.getLeft();
		PExp right = node.getRight();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		PExpCG leftCg = left.apply(question.getExpVisitor(), question);
		PExpCG rightCg = right.apply(question.getExpVisitor(), question);
		
		AMapletExpCG maplet = new AMapletExpCG();
		maplet.setType(typeCg);
		maplet.setLeft(leftCg);
		maplet.setRight(rightCg);
		
		return maplet;
	}
	
	@Override
	public PExpCG caseAEqualsBinaryExp(AEqualsBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{	
		return expAssistant.handleBinaryExp(node, new AEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ANotEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAIndicesUnaryExp(AIndicesUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType expType = node.getExp().getType();
		
		if(!(expType instanceof SSeqType))
			throw new AnalysisExceptionCG("Expected sequence type for indices unary expression", node.getLocation());
		
		PExp exp = node.getExp();
		
		PTypeCG typeCg = expType.apply(question.getTypeVisitor(), question);
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		AIndicesUnaryExpCG indicesExp = new AIndicesUnaryExpCG();
		indicesExp.setType(typeCg);
		indicesExp.setExp(expCg);
		
		return indicesExp;
	}
	
	@Override
	public PExpCG caseASeqEnumSeqExp(ASeqEnumSeqExp node, OoAstInfo question)
			throws AnalysisException
	{	
		AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();
		
		PType type = node.getType();
		if(type instanceof SSeqType)
		{
			PTypeCG seqType = type.apply(question.getTypeVisitor(), question);
			enumSeq.setType(seqType);
		}
		else
		{
			throw new AnalysisExceptionCG("Unexpected sequence type for sequence enumeration expression: " + type.getClass().getName(), node.getLocation());
		}
		
		//TODO: For the empty sequence [] the type is the unknown type
		//This is a problem if the assignment var1 is a field
		//That has a declared type or we are talking about an assignment
		LinkedList<PExp> members = node.getMembers();
		for (PExp member : members)
		{
			enumSeq.getMembers().add(member.apply(question.getExpVisitor(), question));
		}
		
		return enumSeq;
	}
	
	@Override
	public PExpCG caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, OoAstInfo question)
			throws AnalysisException
	{
		return null;//Indicates an abstract body
	}
	
	@Override
	public PExpCG caseAFieldExp(AFieldExp node, OoAstInfo question)
			throws AnalysisException
	{
		PExpCG object = node.getObject().apply(question.getExpVisitor(), question);
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		
		String memberName = "";
		
		if(node.getMemberName() != null)
			memberName = node.getMemberName().getFullName();
		else
			memberName = node.getField().getName();
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setObject(object);
		fieldExp.setMemberName(memberName);
		fieldExp.setType(type);
		
		return fieldExp;
	}
	
	@Override
	public PExpCG caseAApplyExp(AApplyExp node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp root = node.getRoot();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG rootCg = root.apply(question.getExpVisitor(), question);

		AApplyExpCG applyExp = new AApplyExpCG();
		applyExp.setType(typeCg);
		applyExp.setRoot(rootCg);

		LinkedList<PExp> applyArgs = node.getArgs();

		for (int i = 0; i < applyArgs.size(); i++)
		{
			PExpCG arg = applyArgs.get(i).apply(question.getExpVisitor(), question);
			applyExp.getArgs().add(arg);
		}

		return applyExp;
	}
	
	@Override
	public PExpCG caseAVariableExp(AVariableExp node, OoAstInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();

		PDefinition varDef = node.getVardef();
		
		SClassDefinition owningClass = varDef.getAncestor(SClassDefinition.class);
		SClassDefinition nodeParentClass = node.getAncestor(SClassDefinition.class);

		boolean inOwningClass = owningClass == nodeParentClass;
		
		boolean isLocalDef = varDef instanceof ALocalDefinition;
		boolean isInstanceVarDef = varDef instanceof AInstanceVariableDefinition;
		boolean isAssignmentDef = varDef instanceof AAssignmentDefinition;
		
		boolean isDefInOwningClass = inOwningClass && (isLocalDef || isInstanceVarDef || isAssignmentDef);

		boolean isImplicit = !node.getName().getExplicit();
		
		if (isDefInOwningClass || isImplicit)
		{
			AVariableExpCG varExp = new AVariableExpCG();
			
			PTypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
			varExp.setOriginal(name);
			varExp.setType(typeCg);
			
			return varExp;
		}
		else if(node.getName().getExplicit())
		{
			AExplicitVariableExpCG varExp = new AExplicitVariableExpCG();
			
			String className = node.getName().getModule();
			
			AClassTypeCG classType = new AClassTypeCG();
			classType.setName(className);
			
			varExp.setClassType(classType);
			varExp.setName(name);
			
			return varExp;
		}
		else
			return null; 
	}
	
	@Override
	public PExpCG caseANewExp(ANewExp node, OoAstInfo question)
			throws AnalysisException
	{
		String className = node.getClassdef().getName().getName();
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(null);
		typeName.setName(className);
		
		PType type = node.getType();
		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpCG newExp = new ANewExpCG();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		newExp.setType(typeCg);
		newExp.setName(typeName);
		
		LinkedList<PExpCG> newExpArgs = newExp.getArgs();
		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}
		
		return newExp;
	}
		
	@Override
	public PExpCG caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ATimesNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new APlusNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ASubtractNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new AGreaterEqualNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAStarStarBinaryExp(AStarStarBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new APowerNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new AGreaterNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessEqualNumericBinaryExpCG(), question);
	}
	
	
	@Override
	public PExpCG caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divide = (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		PExpCG leftExpCG = divide.getLeft();
		
		if(ExpAssistantCG.isIntegerType(leftExp) && ExpAssistantCG.isIntegerType(rightExp))
		{
			ARealLiteralExpCG one = new ARealLiteralExpCG();
			one.setType(new ARealNumericBasicTypeCG());
			one.setValue(1.0);
			
			ATimesNumericBinaryExpCG neutralMul = new ATimesNumericBinaryExpCG();
			neutralMul.setType(new ARealNumericBasicTypeCG());
			neutralMul.setLeft(one);
			neutralMul.setRight(leftExpCG);
			
			divide.setLeft(ExpAssistantCG.isolateExpression(neutralMul));
		}
		
		return divide;
	}
	
	@Override
	public PExpCG caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAModNumericBinaryExp(AModNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//VDM Language Reference Manual:
		//x mod y = x - y * floor(x/y)
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		AFloorUnaryExpCG floor = new AFloorUnaryExpCG();
		floor.setExp(div);
		
		PExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);
		
		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setLeft(rightExpCg);
		times.setRight(floor);
		
		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setLeft(leftExpCg);
		sub.setRight(times);
		
		return (node.parent() instanceof SBinaryExp) ? ExpAssistantCG.isolateExpression(sub) : sub;
	}
	
	@Override
	public PExpCG caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//VDM Language Reference Manual:
		//x rem y = x - y * (x div y)
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		
		PExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);
		
		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setLeft(rightExpCg);
		times.setRight(ExpAssistantCG.isolateExpression(div));
		
		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setLeft(leftExpCg);
		sub.setRight(times);
		
		return (node.parent() instanceof SBinaryExp) ? ExpAssistantCG.isolateExpression(sub) : sub;
	}
				
	@Override
	public PExpCG caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//A => B is constructed as !A || B
		
		PTypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);
		
		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg);
		notExp.setExp(leftExpCg);
		
		AOrBoolBinaryExpCG orExp = new AOrBoolBinaryExpCG();
		orExp.setType(typeCg);
		orExp.setLeft(notExp);
		orExp.setRight(rightExpCg);
		
		return orExp;
	}
	
	@Override
	public PExpCG caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		//A <=> B is constructed as !(A ^ B)
		PTypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

		AXorBoolBinaryExpCG xorExp = new AXorBoolBinaryExpCG();
		xorExp.setType(typeCg);
		xorExp.setLeft(leftExpCg);
		xorExp.setRight(rightExpCg);
		
		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg);
		notExp.setExp(ExpAssistantCG.isolateExpression(xorExp));

		return notExp;
	}
	
	//Unary
	
	@Override
	public PExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new APlusUnaryExpCG(), question);
	}

	@Override
	public PExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new AMinusUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAFloorUnaryExp(AFloorUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new AFloorUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new AAbsUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotUnaryExp(ANotUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new ANotUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node,  new AOrBoolBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node,  new AAndBoolBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseALenUnaryExp(ALenUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new ALenUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAElementsUnaryExp(AElementsUnaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new AElemsUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAHeadUnaryExp(AHeadUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new AHeadUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseATailUnaryExp(ATailUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleUnaryExp(node, new ATailUnaryExpCG(), question);
	}
	
	//Literals
	//NOTE: The methods for handling of literals/constants look very similar and ideally should be
	//generalized in a method. However the nodes in the VDM AST don't share a parent with method
	//setValue at the current time of writing.
	
	@Override
	public PExpCG caseABooleanConstExp(ABooleanConstExp node,
			OoAstInfo question) throws AnalysisException
	{
		return ExpAssistantCG.consBoolLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseARealLiteralExp(ARealLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		return ExpAssistantCG.consRealLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseAIntLiteralExp(AIntLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		return ExpAssistantCG.consIntLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseACharLiteralExp(ACharLiteralExp node, OoAstInfo question)
			throws AnalysisException
	{
		return ExpAssistantCG.consCharLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseAStringLiteralExp(AStringLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		return ExpAssistantCG.consStringLiteral(node.getValue().getValue(), false);
	}
	
	@Override
	public PExpCG caseAQuoteLiteralExp(AQuoteLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		String value = node.getValue().getValue();
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		AQuoteLiteralExpCG quoteLit = new AQuoteLiteralExpCG();
		quoteLit.setValue(value);
		quoteLit.setType(type);

		question.registerQuoteValue(value);
		
		return quoteLit;
	}
}
