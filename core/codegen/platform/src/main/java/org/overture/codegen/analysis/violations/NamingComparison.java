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

	public void correctNameToken(ILexNameToken nameToken)
	{
		String module = nameToken.getModule();
		String correctedName = correctionPrefix + nameToken.getName();
		ILexLocation location = nameToken.getLocation();
		boolean old = nameToken.getOld();
		boolean explicit = nameToken.getExplicit();

		LexNameToken replaceMent = new LexNameToken(module, correctedName, location, old, explicit);
		nameToken.parent().replaceChild(nameToken, replaceMent);
	}

	public List<String> getNames()
	{
		return this.names;
	}
}
