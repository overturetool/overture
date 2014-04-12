package org.overture.codegen.merging;

import java.io.StringWriter;
import java.util.Stack;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.logging.Logger;

public class MergeVisitor extends QuestionAdaptor<StringWriter>
{
	private static final String NODE_KEY = "node";
	
	private TemplateManager templates;
	private TemplateCallable[] templateCallables;
	
	//Since generating code is done by merging templates recursively a stack is used to manage the node contexts used by the template engine.
	//This is needed to avoid overwriting variables already introduced by other templates e.g. #set( $type = ... ).
	private Stack<CodeGenContext> nodeContexts;
	
	public MergeVisitor(TemplateStructure templateStructure, TemplateCallable[] templateCallables)
	{
		this.templates = new TemplateManager(templateStructure);
		this.nodeContexts = new Stack<CodeGenContext>();
		this.templateCallables = templateCallables;
	}
	
	private void initCodeGenContext(INode node, TemplateCallable[] templateCallables)
	{
		CodeGenContext nodeContext = new CodeGenContext();
		nodeContext.put(NODE_KEY, node);
		
		for (TemplateCallable callable : templateCallables)
		{
			nodeContext.put(callable.getKey(), callable.getCallable());
		}
		
		nodeContexts.push(nodeContext);
	}

	@Override
	public void defaultINode(INode node, StringWriter question)
			throws AnalysisException
	{
		initCodeGenContext(node, templateCallables);

		Template template = templates.getTemplate(node.getClass());
		
		if(template == null)
		{
			String msg = "Template could not be found for node: " + node.getClass().getName();
			Logger.getLog().printErrorln(msg);
			throw new AnalysisException(msg);
		}
		
		try{
			template.merge(nodeContexts.pop().getVelocityContext(), question);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
