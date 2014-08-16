/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.merging;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.velocity.Template;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;

public class MergeVisitor extends QuestionAdaptor<StringWriter>
{
	private static final String NODE_KEY = "node";
	
	private TemplateManager templates;
	private TemplateCallable[] templateCallables;
	
	//Since generating code is done by merging templates recursively a stack is used to manage the node contexts used by the template engine.
	//This is needed to avoid overwriting variables already introduced by other templates e.g. #set( $type = ... ).
	private Stack<MergeContext> nodeContexts;
	
	private List<Exception> mergeErrors;
	
	public MergeVisitor(TemplateStructure templateStructure, TemplateCallable[] templateCallables)
	{
		this.templates = new TemplateManager(templateStructure);
		this.nodeContexts = new Stack<MergeContext>();
		this.templateCallables = templateCallables;
		this.mergeErrors = new LinkedList<Exception>();
	}
	
	public List<Exception> getMergeErrors()
	{
		return mergeErrors;
	}
	
	public boolean hasMergeErrors()
	{
		return !mergeErrors.isEmpty();
	}
	
	public void dropMergeErrors()
	{
		//Don't clear it if others are using the list
		mergeErrors = new LinkedList<Exception>();
	}
	
	private void initCodeGenContext(INode node, TemplateCallable[] templateCallables)
	{
		MergeContext nodeContext = new MergeContext();
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
			mergeErrors.add(new AnalysisException(msg));
		}
		else
		{
			try{
				template.merge(nodeContexts.pop().getVelocityContext(), question);
			}
			catch(Exception e)
			{
				mergeErrors.add(e);
			}

		}
	}
}
