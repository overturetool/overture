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
package org.overture.typechecker.util;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.LexNameTokenAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

//FIXME Remove this class when we figure out how to compare lexNametoken without using types
public class HackLexNameToken
{
	protected static ITypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();

	@SuppressWarnings("static-access")
	public HackLexNameToken(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static boolean isEqual(ILexNameToken one, Object other)
	{
		return af.getLexNameTokenAssistant().isEqual(one, other);
	}

	public static LexNameTokenAssistant getStaticLexNameTokenAssistant()
	{
		return af.getLexNameTokenAssistant();
	}

}
