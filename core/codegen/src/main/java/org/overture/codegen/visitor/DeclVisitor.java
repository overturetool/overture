package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class DeclVisitor extends QuestionAnswerAdaptor<CodeGenInfo, PDeclCG>
{
	private static final long serialVersionUID = -7968170190668212627L;
	
	@Override
	public PDeclCG caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, CodeGenInfo question)
			throws AnalysisException
	{	
		
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = false;
		String operationName = node.getName().getName();
		PTypeCG returnType = node.getActualResult().apply(question.getTypeVisitor(), question);
		PStmCG body = node.getBody().apply(question.getStatementVisitor(), question);
		boolean isConstructor = node.getIsConstructor();
		
		AMethodDeclCG method = new AMethodDeclCG();
		
		method.setAccess(access);
		method.setStatic(isStatic);
		method.setReturnType(returnType);
		method.setName(operationName);
		method.setBody(body);
		method.setIsConstructor(isConstructor);
		
		
		LinkedList<PDefinition> paramDefs = node.getParamDefinitions();
		LinkedList<PPattern> paramPatterns = node.getParameterPatterns();
		
		LinkedList<AFormalParamLocalDeclCG> formalParameters = method.getFormalParams();
		
		for(int i = 0; i < paramPatterns.size(); i++)
		{
			PDefinition def = paramDefs.get(i);
			PPattern pattern = paramPatterns.get(i);
			
			PTypeCG type = def.getType().apply(question.getTypeVisitor(), question);
			String name = pattern.toString();
			
			AFormalParamLocalDeclCG param = new AFormalParamLocalDeclCG();
			param.setType(type);
			param.setName(name);
			
			formalParameters.add(param);
		}
		
		return method;
	}
	
	@Override
	public PDeclCG caseAValueDefinition(AValueDefinition node, CodeGenInfo question) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		
		AFieldDeclCG field = new AFieldDeclCG();
		field.setAccess(access);
		field.setName(name);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);
		
		return field;
	}
}
