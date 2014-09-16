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
package org.overture.codegen.analysis.violations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;

public class VdmAstAnalysis
{
	private AssistantManager assistantManager;

	public VdmAstAnalysis(AssistantManager assistantManager)
	{
		this.assistantManager = assistantManager;
	}

	public Set<Violation> usesIllegalNames(List<? extends INode> nodes,
			NamingComparison comparison) throws AnalysisException
	{
		NameViolationAnalysis namingAnalysis = new NameViolationAnalysis(assistantManager, comparison);
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(namingAnalysis);

		return findViolations(nodes, application);
	}

	public Set<Violation> usesUnsupportedModelingConstructs(
			List<? extends INode> nodes) throws AnalysisException
	{
		ModelingViolationAnalysis modelingAnalysis = new ModelingViolationAnalysis(assistantManager);
		ViolationAnalysisApplication application = new ViolationAnalysisApplication(modelingAnalysis);

		return findViolations(nodes, application);
	}

	private Set<Violation> findViolations(List<? extends INode> nodes,
			ViolationAnalysisApplication application) throws AnalysisException
	{
		Set<Violation> allViolations = new HashSet<Violation>();

		for (INode currentNode : nodes)
		{
			List<Violation> currentViolations = application.execute(currentNode);

			if (!currentViolations.isEmpty())
			{
				allViolations.addAll(currentViolations);
			}
		}

		return allViolations;
	}

	private class ViolationAnalysisApplication
	{
		private ViolationAnalysis violationAnalysis;

		public ViolationAnalysisApplication(ViolationAnalysis violationAnalysis)
		{
			this.violationAnalysis = violationAnalysis;
		}

		public List<Violation> execute(INode node) throws AnalysisException
		{
			return applyViolationVisitor(node, violationAnalysis);
		}

		private List<Violation> applyViolationVisitor(INode node,
				ViolationAnalysis analysis) throws AnalysisException
		{
			try
			{
				node.apply(analysis);
			} catch (AnalysisException e)
			{
				throw e;
			}

			return analysis.getViolations();
		}
	}
}
