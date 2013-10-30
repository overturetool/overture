package org.overture.codegen.merging;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.utils.DependencyAnalysis;
import org.overture.codegen.visitor.CodeGenContext;

import org.overture.codegen.logging.Logger;

public class MergeVisitor extends QuestionAdaptor<StringWriter>
{

	private static final long serialVersionUID = 1356835559835525016L;

	private TemplateManager templates;

	public MergeVisitor()
	{
		this.templates = new TemplateManager();
	}

	@Override
	public void defaultINode(INode node, StringWriter question)
			throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();
		context.put("node", node);
		
		//TODO: This should not be put for every node..
		context.put("CG", CG.class);
		context.put("DependencyAnalysis", DependencyAnalysis.class);
		
		Template template = templates.getTemplate(node.getClass());
		
		if(template == null)
		{
			String msg = "Template could not be found for node: " + node.getClass().getName();
			Logger.getLog().printErrorln(msg);
			throw new AnalysisException(msg);
		}
		
		try{
			template.merge(context.getVelocityContext(), question);
		}
		catch(Exception e)
		{
			throw new AnalysisException("Could not merge template for node: " + node.getClass().getName());
		}
	}
}
