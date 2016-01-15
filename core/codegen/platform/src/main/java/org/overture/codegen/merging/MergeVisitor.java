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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.ParseException;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.utils.GeneralUtils;

public class MergeVisitor extends QuestionAdaptor<StringWriter> implements
		MergeCoordinator
{
	private static final String NODE_KEY = "node";

	private TemplateManager templates;
	private TemplateCallable[] templateCallables;
	private Set<IrNodeInfo> unsupportedInTargLang;

	// Since generating code is done by merging templates recursively a stack is used to manage the node contexts used
	// by the template engine.
	// This is needed to avoid overwriting variables already introduced by other templates e.g. #set( $type = ... ).
	private Stack<MergeContext> nodeContexts;

	private List<Exception> mergeErrors;

	private MergerObserver mergeObserver;

	/**
	 * Extensible constructor.
	 * 
	 * @param templateManager
	 * @param templateCallables
	 */
	public MergeVisitor(TemplateManager templateManager,
			TemplateCallable[] templateCallables)
	{
		this.templates = templateManager;
		this.nodeContexts = new Stack<MergeContext>();
		this.templateCallables = addDefaults(templateCallables);
		this.mergeErrors = new LinkedList<Exception>();
		this.unsupportedInTargLang = new HashSet<IrNodeInfo>();
	}

	/**
	 * Enables the static methods of the java.lang.String class to be called from the templates. If the key "String" is
	 * already reserved by the user, this method simply returns the input parameter.
	 * 
	 * @param userCallables
	 * @return all the template callables
	 */
	public TemplateCallable[] addDefaults(TemplateCallable[] userCallables)
	{
		TemplateCallable strFunctionality = new TemplateCallable(String.class.getSimpleName(), String.class);

		for (TemplateCallable u : userCallables)
		{
			if (u.equals(strFunctionality))
			{
				return userCallables;
			}
		}

		return GeneralUtils.concat(userCallables, new TemplateCallable[] { strFunctionality });
	}

	public List<Exception> getMergeErrors()
	{
		return mergeErrors;
	}

	public boolean hasMergeErrors()
	{
		return !mergeErrors.isEmpty();
	}

	public Set<IrNodeInfo> getUnsupportedInTargLang()
	{
		return unsupportedInTargLang;
	}

	public boolean hasUnsupportedTargLangNodes()
	{
		return unsupportedInTargLang != null
				&& !unsupportedInTargLang.isEmpty();
	}

	public void init()
	{
		// Avoid clearing the data structures if others are using them
		mergeErrors = new LinkedList<Exception>();
		unsupportedInTargLang = new HashSet<IrNodeInfo>();
	}

	private void initCodeGenContext(INode node,
			TemplateCallable[] templateCallables)
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

		try
		{
			Template template = templates.getTemplate(node.getClass());

			if (template == null)
			{
				unsupportedInTargLang.add(new IrNodeInfo(node, "Template could not be found."));
			} else
			{
				try
				{
					if (mergeObserver != null)
					{
						mergeObserver.preMerging(node, question);
					}

					template.merge(nodeContexts.pop().getVelocityContext(), question);

					if (mergeObserver != null)
					{
						mergeObserver.nodeMerged(node, question);
					}
				} catch (Exception e)
				{
					mergeErrors.add(e);
				}
			}
		} catch (ParseException e)
		{
			unsupportedInTargLang.add(new IrNodeInfo(node, "Parse error in template.\n"
					+ e.getMessage()));
		}
	}

	@Override
	public void register(MergerObserver obs)
	{
		if (obs != null && mergeObserver == null)
		{
			mergeObserver = obs;
		}
	}

	@Override
	public void unregister(MergerObserver obs)
	{
		if (obs != null && mergeObserver == obs)
		{
			mergeObserver = null;
		}
	}
}
