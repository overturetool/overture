/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.assistant.pattern;

import java.util.List;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class APatternTypePairAssistant implements IAstAssistant
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public APatternTypePairAssistant(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	public List<PDefinition> getDefinitions(APatternTypePair result)
	{

		return af.createPPatternAssistant(fromModule).getDefinitions(result.getPattern(), result.getType(), NameScope.LOCAL);
	}

	public void typeResolve(APatternTypePair result,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{

		if (result.getResolved())
		{
			return;
		} else
		{
			result.setResolved(true);
		}
		result.setType(af.createPTypeAssistant().typeResolve(result.getType(), null, rootVisitor, question));

	}

}
