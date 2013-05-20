package org.overture.codegen.mergevisitor;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.SBinaryExp;
import org.overture.codegen.cgast.expressions.SUnaryExp;
import org.overture.codegen.templates.TemplateParameters;
import org.overture.codegen.visitor.CodeGenContext;

public class MergeAssistant
{

	private MergeVisitor mergeVisitor;
	
	public MergeAssistant(MergeVisitor mergeVisitor)
	{
		this.mergeVisitor = mergeVisitor;
	}

	public void handleUnaryExp(SUnaryExp node, StringWriter question)
			throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();
		
		StringWriter value = new StringWriter();
		node.getExp().apply(mergeVisitor, value);
		
		context.put(TemplateParameters.UNARY_EXP_VALUE, value.toString());
		
		Template plusUnaryTemplate = mergeVisitor.getTemplateManager().getTemplate(node.getClass());
		plusUnaryTemplate.merge(context.getVelocityContext(), question);
	}
	
	public void handleBinaryExp(SBinaryExp node, StringWriter question)
		throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();
		
		StringWriter leftValue = new StringWriter();
		node.getLeft().apply(mergeVisitor, leftValue);	
		context.put(TemplateParameters.BIN_EXP_LEFT_OPERAND, leftValue.toString());
		
		
		StringWriter rightValue = new StringWriter();
		node.getRight().apply(mergeVisitor, rightValue);	
		context.put(TemplateParameters.BIN_EXP_RIGHT_OPERAND, rightValue.toString());
		
		
		Template plusBinaryTemplate = mergeVisitor.getTemplateManager().getTemplate(node.getClass());
		plusBinaryTemplate.merge(context.getVelocityContext(), question);
	}
	
}
