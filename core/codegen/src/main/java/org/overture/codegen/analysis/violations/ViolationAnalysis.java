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

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.assistant.AssistantManager;

public class ViolationAnalysis extends DepthFirstAnalysisAdaptor
{
	private List<Violation> violations;
	protected AssistantManager assistantManager;

	public ViolationAnalysis(AssistantManager assistantManager)
	{
		this.violations = new LinkedList<Violation>();
		this.assistantManager = assistantManager;
	}

	public List<Violation> getViolations()
	{
		return violations;
	}

	public void addViolation(Violation violation)
	{
		if (violations.contains(violation))
		{
			return;
		}

		violations.add(violation);
	}
}
