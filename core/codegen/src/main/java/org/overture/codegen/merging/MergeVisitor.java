package org.overture.codegen.merging;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.logging.Logger;

public class MergeVisitor extends QuestionAdaptor<StringWriter>
{
	private TemplateManager templates;

	private CodeGenContext context;
	
	public MergeVisitor(TemplateStructure templateStructure, TemplateCallable[] templateCallables)
	{
		this.templates = new TemplateManager(templateStructure);
		initCodeGenContext(templateCallables); 
	}
	
	private void initCodeGenContext(TemplateCallable[] templateCallables)
	{
		this.context = new CodeGenContext();
		
		for (TemplateCallable callable : templateCallables)
		{
			this.context.put(callable.getKey(), callable.getCallable());
		}
	}

	@Override
	public void defaultINode(INode node, StringWriter question)
			throws AnalysisException
	{
		context.put("node", node);

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
			e.printStackTrace();
		}
	}
}
