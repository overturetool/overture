/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.assistant.pattern;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PPattern;

public class PPatternAssistant implements IAstAssistant
{
	protected static IAstAssistantFactory af;
	protected final String fromModule;

	@SuppressWarnings("static-access")
	public PPatternAssistant(IAstAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	public LexNameList getAllVariableNames(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getAllVariableNameLocator(fromModule));
		}
		catch (AnalysisException e)
		{
			return null;
		}
	}
	
	public LexNameList getVariableNames(PPattern pattern)
	{
		return af.createPPatternAssistant(pattern.getLocation().getModule()).getVariableNamesBaseCase(pattern);
	}

	public LexNameList getVariableNamesBaseCase(PPattern pattern)
	{
		Set<ILexNameToken> set = new HashSet<ILexNameToken>();
		set.addAll(af.createPPatternAssistant(pattern.getLocation().getModule()).getAllVariableNames(pattern));
		LexNameList list = new LexNameList();
		list.addAll(set);
		return list;
	}

}
