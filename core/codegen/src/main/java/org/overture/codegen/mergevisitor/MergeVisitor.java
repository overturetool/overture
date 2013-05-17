package org.overture.codegen.mergevisitor;

import java.io.StringWriter;
import java.util.LinkedList;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.AFieldCG;
import org.overture.codegen.cgast.analysis.AnalysisAdaptor;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.constants.ITextConstants;
import org.overture.codegen.naming.TemplateParameters;
import org.overture.codegen.newstuff.ContextManager;
import org.overture.codegen.visitor.CodeGenContext;


public class MergeVisitor extends QuestionAdaptor<StringWriter>
{

	private static final long serialVersionUID = 1356835559835525016L;
	
	private TemplateManager templates;
		
	public MergeVisitor(String templateFileSuffix)
	{
		this.templates = new TemplateManager(templateFileSuffix);
	}
		
	@Override
	public void caseAClassCG(AClassCG node, StringWriter writer)
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
		classTemplate.merge(context.getVelocityContext(), writer);
	}
	
	@Override
	public void caseAFieldCG(AFieldCG node, StringWriter writer)
			throws AnalysisException
	{
		String access = node.getAccess();
		boolean isStatic = node.getStatic();
		boolean isFinal = node.getFinal();
		String type = node.getType();
		String name = node.getName();
		String initial = node.getInitial();
		
		CodeGenContext context = new CodeGenContext();
		
		context.put(TemplateParameters.FIELD_ACCESS, access);
		context.put(TemplateParameters.FIELD_STATIC, isStatic);
		context.put(TemplateParameters.FIELD_FINAL, isFinal);
		context.put(TemplateParameters.FIELD_TYPE, type);
		context.put(TemplateParameters.FIELD_NAME, name);
		context.put(TemplateParameters.FIELD_INITIAL, initial);
		
		Template classTemplate = templates.getTemplate(node.getClass());
		
		writer.append(ITextConstants.INDENT);
		classTemplate.merge(context.getVelocityContext(), writer);
		writer.append(ITextConstants.NEW_LINE);
	}
}
