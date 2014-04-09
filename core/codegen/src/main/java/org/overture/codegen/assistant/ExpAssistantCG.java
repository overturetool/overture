package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SQuantifierExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.patterns.PMultipleBindCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ooast.OoAstInfo;

public class ExpAssistantCG extends AssistantBase
{
	public ExpAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public PExpCG isolateExpression(PExpCG exp)
	{
		AIsolationUnaryExpCG isolationExp = new AIsolationUnaryExpCG();
		isolationExp.setExp(exp);
		isolationExp.setType(exp.getType());
		return isolationExp;
	}
	
	public ANotUnaryExpCG negate(PExpCG exp)
	{
		ANotUnaryExpCG negated = new ANotUnaryExpCG();
		negated.setType(new ABoolBasicTypeCG());
		negated.setExp(exp);

		return negated;
	}
	
	public PExpCG handleUnaryExp(SUnaryExp vdmExp, SUnaryExpCG codeGenExp, OoAstInfo question) throws AnalysisException
	{
		PExpCG expCg = vdmExp.getExp().apply(question.getExpVisitor(), question);
		PTypeCG typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);
		
		codeGenExp.setType(typeCg);
		codeGenExp.setExp(expCg);
		
		return codeGenExp;
	}
	
	public PExpCG handleBinaryExp(SBinaryExp vdmExp, SBinaryExpCG codeGenExp, OoAstInfo question) throws AnalysisException
	{	
		PType type = vdmExp.getType();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		codeGenExp.setType(typeCg);
		
		PExp vdmExpLeft = vdmExp.getLeft();
		PExp vdmExpRight = vdmExp.getRight();
		
		PExpCG leftExpCg = vdmExpLeft.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = vdmExpRight.apply(question.getExpVisitor(), question);
		
		codeGenExp.setLeft(leftExpCg);
		codeGenExp.setRight(rightExpCg);
		
		return codeGenExp;
	}
	
	public boolean isIntegerType(PExp exp)
	{	
		PType type = exp.getType();

		//Expressions like 1.0 are considered real literal expressions
		//of type NatOneNumericBasicType
		
		return (type instanceof ANatOneNumericBasicType 
				|| type instanceof ANatNumericBasicType
				|| type instanceof AIntNumericBasicType) 
				&& !(exp instanceof ARealLiteralExp);
	}
	
	public ABoolLiteralExpCG consBoolLiteral(boolean val)
	{
		ABoolLiteralExpCG boolLiteral = new ABoolLiteralExpCG();
		boolLiteral.setType(new ABoolBasicTypeCG());
		boolLiteral.setValue(val);
		
		return boolLiteral;
	}
	
	public AIntLiteralExpCG consIntLiteral(long value)
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		intLiteral.setType(new AIntNumericBasicTypeCG());
		intLiteral.setValue(value);
		
		return intLiteral;
	}
	
	public ARealLiteralExpCG consRealLiteral(double value)
	{
		ARealLiteralExpCG realLiteral = new ARealLiteralExpCG();
		realLiteral.setType(new ARealNumericBasicTypeCG());
		realLiteral.setValue(value);
		
		return realLiteral;
	}
	
	public ACharLiteralExpCG consCharLiteral(char value)
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		charLiteral.setType(new ACharBasicTypeCG());
		charLiteral.setValue(value);
		
		return charLiteral;
	}
	
	public AStringLiteralExpCG consStringLiteral(String value, boolean isNull)
	{
		AStringLiteralExpCG stringLiteral = new AStringLiteralExpCG();

		stringLiteral.setType(new AStringTypeCG());
		stringLiteral.setIsNull(isNull);
		stringLiteral.setValue(StringEscapeUtils.escapeJava(value));
		
		return stringLiteral;
	}
	
	public AIntLiteralExpCG getDefaultIntValue()
	{
		return consIntLiteral(0L);
	}
	
	public ARealLiteralExpCG getDefaultRealValue()
	{
		return consRealLiteral(0.0);
	}
	
	public ABoolLiteralExpCG getDefaultBoolValue()
	{
		return consBoolLiteral(false);
	}
	
	public ACharLiteralExpCG getDefaultCharlValue()
	{
		return consCharLiteral('0');
	}
	
	public AStringLiteralExpCG getDefaultStringlValue()
	{
		return consStringLiteral("", true);
	}
	
	public ANullExpCG getDefaultClassValue()
	{
		return new ANullExpCG();
	}
	
	public boolean isAssigned(PExp exp)
	{
		return exp.getAncestor(AInstanceVariableDefinition.class) != null ||
			   exp.getAncestor(AValueDefinition.class) != null ||
			   exp.getAncestor(AAssignmentDefinition.class) != null ||
			   exp.getAncestor(AAssignmentStm.class) != null;
	}
	
	public LinkedList<AIdentifierPatternCG> getIdsFromPatternList(List<PPattern> patternList)
	{
		LinkedList<AIdentifierPatternCG> idsCg = new LinkedList<AIdentifierPatternCG>();
		
		for (PPattern pattern : patternList)
		{
			if (!(pattern instanceof AIdentifierPattern))
			{
				return null;
			}
			
			AIdentifierPattern id = (AIdentifierPattern) pattern;
			
			AIdentifierPatternCG idCg = new AIdentifierPatternCG();
			idCg.setName(id.getName().getName());
			
			idsCg.add(idCg);
		}
		
		return idsCg;
	}
	
	public AHeaderLetBeStCG consHeader(ASetMultipleBindCG binding, PExpCG suchThat)
	{
		AHeaderLetBeStCG header = new AHeaderLetBeStCG();
		
		header.setBinding(binding);
		header.setSuchThat(suchThat);
		
		return header;
	}
	
	public boolean existsOutsideOpOrFunc(PExp exp)
	{
		return exp.getAncestor(SOperationDefinition.class) == null && exp.getAncestor(SFunctionDefinition.class) == null;
	}
	
	public PExpCG handleQuantifier(PExp node, List<PMultipleBind> bindings, PExp predicate, SQuantifierExpCG quantifier, String varCg, OoAstInfo question, String nodeStr)
			throws AnalysisException
	{
		if(question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, String.format("Generation of a %s is only supported within operations/functions", nodeStr));
			return null;
		}
		
		LinkedList<ASetMultipleBindCG> bindingsCg = new LinkedList<ASetMultipleBindCG>();
		for (PMultipleBind multipleBind : bindings)
		{
			if(!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, String.format("Generation of a %s is only supported for multiple set binds. Got: %s", nodeStr, multipleBind));
				return null;
			}
			
			PMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
			
			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, String.format("Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: %s", multipleBindCg));
				return null;
			}
			
			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}
		
		PType type = node.getType();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG predicateCg = predicate.apply(question.getExpVisitor(), question);
		
		quantifier.setType(typeCg);
		quantifier.setBindList(bindingsCg);
		quantifier.setPredicate(predicateCg);
		quantifier.setVar(varCg);
		
		return quantifier;
	}
}
