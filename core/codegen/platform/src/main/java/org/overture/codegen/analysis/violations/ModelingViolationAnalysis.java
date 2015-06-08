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

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SNumericBinaryBase;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.ExpAssistantCG;

public class ModelingViolationAnalysis extends ViolationAnalysis
{
	public ModelingViolationAnalysis(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	@Override
	public void caseARenamedDefinition(ARenamedDefinition node)
			throws AnalysisException
	{
		addViolation(new Violation("Renaming of imported definitions is not currently supported", node.getLocation(), assistantManager.getLocationAssistant()));
	}
	
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if (node instanceof AClassClassDefinition)
		{
			AClassClassDefinition classDef = (AClassClassDefinition) node;

			if (classDef.getSupernames().size() > 1)
			{
				addViolation(new Violation("Multiple inheritance not supported.", classDef.getLocation(), assistantManager.getLocationAssistant()));
			}

			// Currently this is allowed
			// handleOverloadedMethods(classDef);

		} else if (node instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp exp = (AFuncInstatiationExp) node;

			if (exp.getImpdef() != null)
			{
				addViolation(new Violation("Implicit functions cannot be instantiated since they are not supported.", exp.getLocation(), assistantManager.getLocationAssistant()));
			}
		}
	}

	// Currently the call to this is commented out
	@SuppressWarnings("unused")
	private void handleOverloadedMethods(AClassClassDefinition classDef)
	{
		Set<ILexNameToken> overloadedNameTokens = assistantManager.getDeclAssistant().getOverloadedMethodNames(classDef);

		if (overloadedNameTokens.size() > 0)
		{
			for (ILexNameToken name : overloadedNameTokens)
			{
				addViolation(new Violation("Overloading of operation and function names is not allowed. Caused by: "
						+ classDef.getName() + "." + name.getName(), name.getLocation(), assistantManager.getLocationAssistant()));
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean operandsAreIntegerTypes(SNumericBinaryBase exp)
	{
		PExp leftExp = exp.getLeft();
		PExp rightExp = exp.getRight();

		ExpAssistantCG expAssistant = assistantManager.getExpAssistant();

		return !expAssistant.isIntegerType(leftExp)
				|| !expAssistant.isIntegerType(rightExp);
	}
}
