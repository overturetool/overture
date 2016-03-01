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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;

public class NameViolationAnalysis extends ViolationAnalysis
{
	private NamingComparison comparison;

	public NameViolationAnalysis(AssistantManager assistantManager,
			NamingComparison comparison)
	{
		super(assistantManager);
		this.comparison = comparison;
	}

	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if (node instanceof ILexNameToken)
		{
			ILexNameToken nameToken = (ILexNameToken) node;

			if (comparison.mustHandleNameToken(nameToken))
			{
				comparison.correctNameToken(nameToken);

				SClassDefinition enclosingClass = node.getAncestor(SClassDefinition.class);

				if (moduleNeedsHandling(enclosingClass))
				{
					if (!comparison.isModuleViolation(nameToken))
					{
						registerViolation(nameToken.getName(), nameToken.getLocation());
					}
				}
			}
		}
		else if(node instanceof LexIdentifierToken)
		{
			LexIdentifierToken lexId = (LexIdentifierToken) node;
			
			if(comparison.mustHandleLexIdentifierToken(lexId))
			{
				comparison.correctLexIdentifierToken(lexId);
				
				SClassDefinition enclosingClass = node.getAncestor(SClassDefinition.class);
				
				if(moduleNeedsHandling(enclosingClass))
				{
					registerViolation(lexId.getName(), lexId.getLocation());
				}
			}
		}
	}

	private boolean moduleNeedsHandling(SClassDefinition enclosingClass)
	{
		return enclosingClass != null
				&& !assistantManager.getDeclAssistant().isLibrary(enclosingClass);
	}

	private void registerViolation(String name, ILexLocation location)
	{
		Violation violation = new Violation(name, location, assistantManager.getLocationAssistant());
		addViolation(violation);
	}
}
