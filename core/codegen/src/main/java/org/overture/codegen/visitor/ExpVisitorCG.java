package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
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
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
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
import org.overture.codegen.cgast.expressions.ANullExpCG;
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
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
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
		this.expAssistant = new ExpAssistantCG(this);
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
		return expAssistant.handleBinaryExp(node,  new ASeqConcatBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAEqualsBinaryExp(AEqualsBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{	
		//TODO: For records, classes etc.
		return expAssistant.handleBinaryExp(node, new AEqualsBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		//TODO: For records, classes etc. (exactly same problems as for equals)
		return expAssistant.handleBinaryExp(node, new ANotEqualsBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseALenUnaryExp(ALenUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		ALenUnaryExpCG lenExp = new ALenUnaryExpCG();
		
		lenExp.setType(new AIntNumericBasicTypeCG());
		lenExp.setExp(node.getExp().apply(question.getExpVisitor(), question));
		
		return lenExp;
	}
	
	@Override
	public PExpCG caseAHeadUnaryExp(AHeadUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		AHeadUnaryExpCG headExp = new AHeadUnaryExpCG();
		
		headExp.setType(node.getType().apply(question.getTypeVisitor(), question));
		headExp.setExp(node.getExp().apply(question.getExpVisitor(), question));
		
		return headExp;
	}
	
	@Override
	public PExpCG caseATailUnaryExp(ATailUnaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		ATailUnaryExpCG tailExp = new ATailUnaryExpCG();
		
		tailExp.setType(node.getType().apply(question.getTypeVisitor(), question));
		tailExp.setExp(node.getExp().apply(question.getExpVisitor(), question));
		
		return tailExp;
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
		return expAssistant.handleBinaryExp(node, new ATimesNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new APlusNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ASubtractNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new AGreaterEqualNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAStarStarBinaryExp(AStarStarBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new APowerNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return expAssistant.handleBinaryExp(node, new AGreaterNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, OoAstInfo question)
			throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessEqualNumericBinaryExpCG(), question, typeLookup);
	}
	
	
	@Override
	public PExpCG caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		return expAssistant.handleBinaryExp(node, new ALessNumericBinaryExpCG(), question, typeLookup);
	}
	
	@Override
	public PExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divide = (ADivideNumericBinaryExpCG) expAssistant.handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question, typeLookup);
		
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
	
	//Unary
	
	@Override
	public PExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, OoAstInfo question) throws AnalysisException
	{
		APlusUnaryExpCG unaryPlus = new APlusUnaryExpCG();
		
		unaryPlus.setType(typeLookup.getType(node.getType()));
		unaryPlus.setExp(node.getExp().apply(this, question));
		
		return unaryPlus;
	}

	@Override
	public PExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			OoAstInfo question) throws AnalysisException
	{
		AMinusUnaryExpCG unaryMinus = new AMinusUnaryExpCG();
		
		unaryMinus.setType(typeLookup.getType(node.getType()));
		unaryMinus.setExp(node.getExp().apply(this, question));
		
		if(node.getExp() instanceof SBinaryExp)
		{
			PExpCG isolatedExp = ExpAssistantCG.isolateExpression(unaryMinus.getExp());
			unaryMinus.setExp(isolatedExp);
		}
		
		return unaryMinus;
	}
	
//
//	//Numeric binary
//	@Override
//	public String caseAModNumericBinaryExp(AModNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}
//	
//	@Override
//	public String caseADivNumericBinaryExp(ADivNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		throw new AnalysisException(IMessages.NOT_SUPPORTED_MSG + node.toString());
//	}		
//		return super.caseADivideNumericBinaryExp(node, question);
//	}
//	
//	@Override
//	public String caseARemNumericBinaryExp(ARemNumericBinaryExp node,
//			CodeGenContextMap question) throws AnalysisException
//	{
//		PExp leftNode = node.getLeft();
//		PExp rightNode = node.getRight();
//		
//		String left = expAssistant.formatExp(node, leftNode, question);
//		String operator = opLookup.find(node.getClass()).getMapping();
//		String right = expAssistant.formatExp(node, rightNode, question);
//		
//		
//		if(!expAssistant.isIntegerType(leftNode))
//			left = "new Double(" + leftNode + ").intValue()";
//		
//		if(!expAssistant.isIntegerType(rightNode))
//			right = "new Double(" + rightNode + ").intValue()";
//			
//		return left + " " + operator + " " + right;
//	}
//	
//	//Literal EXP:
	
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
		//TODO: Optimize similar literal expressions
		//Put the similar code in an assistant
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
	
}
