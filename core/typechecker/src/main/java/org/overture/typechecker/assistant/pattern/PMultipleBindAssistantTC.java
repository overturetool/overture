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
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PMultipleBindAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PMultipleBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public Collection<? extends PDefinition> getDefinitions(PMultipleBind mb,
			PType type, TypeCheckInfo question)
	{

		List<PDefinition> defs = new ArrayList<PDefinition>();

		for (PPattern p : mb.getPlist())
		{
			defs.addAll(af.createPPatternAssistant().getDefinitions(p, type, question.scope));
		}

		return defs;
	}

	public List<PMultipleBind> getMultipleBindList(PMultipleBind bind)
	{
		List<PMultipleBind> list = new ArrayList<PMultipleBind>();
		list.add(bind);
		return list;
	}

	public PType getPossibleType(PMultipleBind mb)
	{
		try
		{
			return mb.apply(af.getPossibleBindTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}

	}

}
