package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.apache.commons.lang.StringEscapeUtils;
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
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFloorUnaryExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
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
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFloorUnaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
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
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.lookup.TypeLookup;

public class ExpVisitorCG extends AbstractVisitorCG<OoAstInfo, PExpCG>
{
	private TypeLookup typeLookup;
	
	private ExpAssistantCG expAssistant;
	
	public ExpVisitorCG()
	{
		this.typeLookup = new TypeLookup();
		this.expAssistant = new ExpAssistantCG(this.typeLookup);
	}
	
	@Override
	public PExpCG caseANilExp(ANilExp node, OoAstInfo question)
			throws AnalysisException
	{
		return new ANullExpCG();
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
	public PExpCG caseAFuncInstatiationExp(AFuncInstatiationExp node,
			OoAstInfo question) throws AnalysisException
	{
		if(node.getImpdef() != null)
			throw new AnalysisException("Implicit functions are not supported by the code generator");
		
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
			throw new AnalysisException("mk_ only supported for record types!");
		
		String typeName = node.getTypeName().getName();
		SClassDefinition enclosingClass = node.getAncestor(SClassDefinition.class);
		String enclosingClassName = enclosingClass.getName().getName();		
		ANewExpCG mkExp = new ANewExpCG();
		mkExp.setEnclosingClassName(enclosingClassName);
		
		LinkedList<PExp> nodeArgs = node.getArgs();
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setClassName(typeName);
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
	public PExpCG caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//Operator prec?
		return expAssistant.handleBinaryExp(node,  new ASeqConcatBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAEqualsBinaryExp(AEqualsBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{	
		//TODO: For records, classes etc.
		return expAssistant.handleBinaryExp(node, new AEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//TODO: For records, classes etc. (exactly same problems as for equals)
		return expAssistant.handleBinaryExp(node, new ANotEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASeqEnumSeqExp(ASeqEnumSeqExp node, OoAstInfo question)
			throws AnalysisException
	{	
		AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();
		
		PType type = node.getType();
		if(type instanceof SSeqType)
		{
			PTypeCG seqType = ((SSeqType) type).getSeqof().apply(question.getTypeVisitor(), question);
			enumSeq.setType(seqType);
		}
		else
		{
			throw new AnalysisException("Unexpected seq type");
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
		
		String memberName = "";
		
		if(node.getMemberName() != null)
			memberName = node.getMemberName().getFullName();
		else
			memberName = node.getField().getName();
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setObject(object);
		fieldExp.setMemberName(memberName);
		
		return fieldExp;
	}
	
	@Override
	public PExpCG caseAApplyExp(AApplyExp node, OoAstInfo question)
			throws AnalysisException
	{
		
		PExpCG root = node.getRoot().apply(question.getExpVisitor(), question);
		
		AApplyExpCG applyExp = new AApplyExpCG();		
		applyExp.setRoot(root);
		
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
			varExp.setOriginal(name);
			
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
		
		LinkedList<PExp> nodeArgs = node.getArgs();
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setClassName(className);
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
		
		if(expAssistant.isIntegerType(leftExp) && expAssistant.isIntegerType(rightExp))
		{
			ACastUnaryExpCG castExpr = new ACastUnaryExpCG();
			castExpr.setType(new ARealNumericBasicTypeCG());
			castExpr.setExp(leftExpCG);
			divide.setLeft(castExpr);
		}
		
		return divide;
	}
	
	@Override
	public PExpCG caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		if(!expAssistant.isIntegerType(leftExp) || !expAssistant.isIntegerType(rightExp))
			throw new AnalysisException("Operands must be guaranteed to be integers in 'div' expression");

		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		
		return div;
	}
	
	@Override
	public PExpCG caseAModNumericBinaryExp(AModNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//VDM Language Reference Manual:
		//x mod y = x - y * floor(x/y)
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		if(!expAssistant.isIntegerType(leftExp) || !expAssistant.isIntegerType(rightExp))
			throw new AnalysisException("Operands must be guaranteed to be integers in 'div' expression");
		
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
		
		if(!expAssistant.isIntegerType(leftExp) || !expAssistant.isIntegerType(rightExp))
			throw new AnalysisException("Operands must be guaranteed to be integers in 'div' expression");

		
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
		PExpCG leftExpCg = expAssistant.formatExp(node.getLeft(), question);
		PExpCG rightExpCg = expAssistant.formatExp(node.getRight(), question);
		
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
		//In fact these two isolations are not necessarily needed. It can result in expressions like: !((true) ^ (false))
		PExpCG leftExpCg = ExpAssistantCG.isolateExpression(node.getLeft().apply(question.getExpVisitor(), question));
		PExpCG rightExpCg = ExpAssistantCG.isolateExpression(node.getRight().apply(question.getExpVisitor(), question));

		AXorBoolBinaryExpCG xorExp = new AXorBoolBinaryExpCG();
		xorExp.setType(typeCg);
		xorExp.setLeft(leftExpCg);
		xorExp.setRight(rightExpCg);
		
		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg);
		notExp.setExp(ExpAssistantCG.isolateExpression(xorExp));

		return expAssistant.isolateOnOpPrecedence(node.parent(), ANotUnaryExp.class) ? ExpAssistantCG.isolateExpression(notExp) : notExp;
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
		ABoolLiteralExpCG boolLiteral = new ABoolLiteralExpCG();
		
		boolLiteral.setType(typeLookup.getType(node.getType()));
		boolLiteral.setValue(node.getValue().toString());
		
		return boolLiteral;
	}
	
	@Override
	public PExpCG caseARealLiteralExp(ARealLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		ARealLiteralExpCG realLiteral = new ARealLiteralExpCG();
		
		realLiteral.setType(typeLookup.getType(node.getType()));
		realLiteral.setValue(node.getValue().toString());
		
		return realLiteral;
	}
	
	@Override
	public PExpCG caseAIntLiteralExp(AIntLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		
		intLiteral.setType(typeLookup.getType(node.getType()));
		intLiteral.setValue(node.getValue().toString());
		
		return intLiteral;
	}
	
	@Override
	public PExpCG caseACharLiteralExp(ACharLiteralExp node, OoAstInfo question)
			throws AnalysisException
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		
		charLiteral.setType(typeLookup.getType(node.getType()));
		charLiteral.setValue(node.getValue().getValue() + "");
		
		return charLiteral;
	}
	
	@Override
	public PExpCG caseAStringLiteralExp(AStringLiteralExp node,
			OoAstInfo question) throws AnalysisException
	{
		AStringLiteralExpCG stringLiteral = new AStringLiteralExpCG();

		stringLiteral.setType(new AStringTypeCG());
		stringLiteral.setIsNull(true);
		String value = StringEscapeUtils.escapeJava(node.getValue().getValue());
		stringLiteral.setValue(value);
		
		return stringLiteral;
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
