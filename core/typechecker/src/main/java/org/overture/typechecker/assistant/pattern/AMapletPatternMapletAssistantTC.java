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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapletPatternMapletAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AMapletPatternMapletAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	// TODO: Cannot be deleted because it is used in AllDefinitionLocator visitor.
	public Collection<? extends PDefinition> getDefinitions(
			AMapletPatternMaplet p, SMapType map, NameScope scope)
	{

		List<PDefinition> list = new Vector<PDefinition>();
		list.addAll(af.createPPatternAssistant().getDefinitions(p.getFrom(), map.getFrom(), scope));
		list.addAll(af.createPPatternAssistant().getDefinitions(p.getTo(), map.getTo(), scope));
		return list;
	}

}
