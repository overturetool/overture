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

import java.util.Arrays;
import java.util.List;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.codegen.ir.IRInfo;

public abstract class NamingComparison
{
	protected List<String> names;
	protected IRInfo irInfo;
	protected String correctionPrefix;

	public NamingComparison(String[] names, IRInfo irInfo,
			String correctionPrefix)
	{
		this.names = Arrays.asList(names);
		this.irInfo = irInfo;
		this.correctionPrefix = correctionPrefix;
	}

	public abstract boolean mustHandleNameToken(ILexNameToken nameToken);
	
	public abstract boolean mustHandleLexIdentifierToken(LexIdentifierToken lexId);
	
	public boolean isModuleViolation(ILexNameToken nameToken)
	{
		return false;
	}

	public void correctNameToken(ILexNameToken nameToken)
	{
		String newModule = nameToken.getModule();
		if(names.contains(nameToken.getModule()))
		{
			newModule = correctionPrefix + newModule;
		}
		
		String newName = nameToken.getName();
		if(names.contains(nameToken.getName()))
		{
			newName = correctionPrefix + newName;
		}
		
		ILexLocation location = nameToken.getLocation();
		boolean old = nameToken.getOld();
		boolean explicit = nameToken.getExplicit();

		LexNameToken replaceMent = new LexNameToken(newModule, newName, location, old, explicit);
		nameToken.parent().replaceChild(nameToken, replaceMent);
	}

	public List<String> getNames()
	{
		return this.names;
	}

	public void correctLexIdentifierToken(LexIdentifierToken lexId)
	{
		String newName = correctionPrefix + lexId.getName();

		boolean old = lexId.getOld();
		ILexLocation location = lexId.getLocation();

		LexIdentifierToken replacement = new LexIdentifierToken(newName, old, location);
		lexId.parent().replaceChild(lexId, replacement);
	}
}
