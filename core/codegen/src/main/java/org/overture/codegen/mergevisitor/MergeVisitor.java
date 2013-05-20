package org.overture.codegen.mergevisitor;

import java.io.StringWriter;
import java.util.LinkedList;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.cgast.expressions.AIntLiteralCGExp;
import org.overture.codegen.cgast.expressions.AMinusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.AMinusCGUnaryExp;
import org.overture.codegen.cgast.expressions.AMulCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGNumericBinaryExp;
import org.overture.codegen.cgast.expressions.APlusCGUnaryExp;
import org.overture.codegen.cgast.expressions.ARealLiteralCGExp;
import org.overture.codegen.constants.ITextConstants;
import org.overture.codegen.templates.TemplateManager;
import org.overture.codegen.templates.TemplateParameters;
import org.overture.codegen.visitor.CodeGenContext;


public class MergeVisitor extends QuestionAdaptor<StringWriter>
{

	private static final long serialVersionUID = 1356835559835525016L;
	
	private TemplateManager templates;
		
	private MergeAssistant mergeAssistant;
	
	public MergeVisitor()
	{
		this.templates = new TemplateManager();
		this.mergeAssistant = new MergeAssistant(this);
	}
	
	public TemplateManager getTemplateManager()
	{
		return templates;
	}
		
	@Override
	public void caseAClassCG(AClassCG node, StringWriter question)
			throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();
		
		context.put(TemplateParameters.CLASS_NAME, node.getName());
		context.put(TemplateParameters.CLASS_ACCESS, node.getAccess());
		
		LinkedList<AFieldCG> fields = node.getFields();
		StringWriter generatedField = new StringWriter();
		
		for (AFieldCG field : fields)
		{
			field.apply(this, generatedField);
		}
		
		context.put(TemplateParameters.FIELDS, generatedField.toString());
		
		Template classTemplate = templates.getTemplate(node.getClass());
		classTemplate.merge(context.getVelocityContext(), question);
	}
	
	@Override
	public void caseAFieldCG(AFieldCG node, StringWriter question)
			throws AnalysisException
	{
		String access = node.getAccess();
		boolean isStatic = node.getStatic();
		boolean isFinal = node.getFinal();
		String type = node.getType();
		String name = node.getName();
		
		StringWriter expWriter = new StringWriter();
		node.getInitial().apply(this, expWriter);
		String initial = expWriter.toString();
		
		CodeGenContext context = new CodeGenContext();
		
		context.put(TemplateParameters.FIELD_ACCESS, access);
		context.put(TemplateParameters.FIELD_STATIC, isStatic);
		context.put(TemplateParameters.FIELD_FINAL, isFinal);
		context.put(TemplateParameters.FIELD_TYPE, type);
		context.put(TemplateParameters.FIELD_NAME, name);
		context.put(TemplateParameters.FIELD_INITIAL, initial);
		
		Template classTemplate = templates.getTemplate(node.getClass());
		
		question.append(ITextConstants.INDENT);
		classTemplate.merge(context.getVelocityContext(), question);
		question.append(ITextConstants.NEW_LINE);
	}
	
	@Override
	public void caseAMulCGNumericBinaryExp(AMulCGNumericBinaryExp node,
			StringWriter question) throws AnalysisException
	{
		mergeAssistant.handleBinaryExp(node, question);
	}
	
	@Override
	public void caseAPlusCGNumericBinaryExp(APlusCGNumericBinaryExp node,
			StringWriter question) throws AnalysisException
	{
		mergeAssistant.handleBinaryExp(node, question);
	}
	
	@Override
	public void caseAMinusCGNumericBinaryExp(AMinusCGNumericBinaryExp node,
			StringWriter question) throws AnalysisException
	{
		mergeAssistant.handleBinaryExp(node, question);
	}
	
	@Override
	public void caseAPlusCGUnaryExp(APlusCGUnaryExp node, StringWriter question)
			throws AnalysisException
	{
		mergeAssistant.handleUnaryExp(node, question);
	}
	
	@Override
	public void caseAMinusCGUnaryExp(AMinusCGUnaryExp node,
			StringWriter question) throws AnalysisException
	{
		mergeAssistant.handleUnaryExp(node, question);
	}
	
	@Override
	public void caseAIntLiteralCGExp(AIntLiteralCGExp node,
			StringWriter question) throws AnalysisException
	{
		question.append(node.getValue());
	}
	
	@Override
	public void caseARealLiteralCGExp(ARealLiteralCGExp node,
			StringWriter question) throws AnalysisException
	{
		question.append(node.getValue());
	}
	
}
