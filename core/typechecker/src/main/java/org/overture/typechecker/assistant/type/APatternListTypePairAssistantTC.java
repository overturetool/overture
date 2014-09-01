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
package org.overture.typechecker.assistant.type;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class APatternListTypePairAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public APatternListTypePairAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	// TODO: Used in the TypeCheckerDefinitionVisitor.
	public Collection<? extends PDefinition> getDefinitions(
			APatternListTypePair pltp, NameScope scope)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p : pltp.getPatterns())
		{
			list.addAll(af.createPPatternAssistant().getDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}

}
