package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.expressions.PExp;

public class FieldVisitor extends QuestionAnswerAdaptor<CodeGenInfo, AFieldCG>
{
	private static final long serialVersionUID = -7968170190668212627L;

	@Override
	public AFieldCG caseAValueDefinition(AValueDefinition node, CodeGenInfo question) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		String type = "theType";//node.getType().apply(rootVisitor.getTypeVisitor(), null);
		PExp exp = node.getExpression().apply(question.getExpVisitor(), question);
		
		AFieldCG field = new AFieldCG();//new AFieldCG(access_, name_, static_, final_, type_, initial_)
		field.setAccess(access);
		field.setName(name);
		field.setStatic(isStatic);
		field.setFinal(isFinal);
		field.setType(type);
		field.setInitial(exp);
		
		return field;
	}
}
