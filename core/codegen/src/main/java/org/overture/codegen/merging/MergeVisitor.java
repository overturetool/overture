package org.overture.codegen.merging;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.visitor.CodeGenContext;

public class MergeVisitor extends QuestionAdaptor<StringWriter>
{

	private static final long serialVersionUID = 1356835559835525016L;

	private TemplateManager templates;

	public MergeVisitor()
	{
		this.templates = new TemplateManager();
	}

	public TemplateManager getTemplateManager()
	{
		return templates;
	}
	
	@Override
	public void defaultINode(INode node, StringWriter question)
			throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();
		context.put("node", node);
		context.put("CG", CG.class);
		
		Template template = templates.getTemplate(node.getClass());
		
		if(template == null)
			return;
		
		template.merge(context.getVelocityContext(), question);
	}
}
