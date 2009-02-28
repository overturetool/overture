/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.syntax;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;

/**
 * A syntax analyser to parse class definitions.
 */

public class ClassReader extends Reader
{
	public ClassReader(LexTokenReader reader)
	{
		super(reader);
	}

	public ClassList readClasses() throws ParserException, LexException
	{
		ClassList list = new ClassList();

		if (lastToken().isNot(Token.CLASS) && lastToken().isNot(Token.SYSTEM))
		{
			throwMessage(2005, "Expecting list of 'class' or 'system' definitions");
		}

		while (lastToken().is(Token.CLASS) || lastToken().is(Token.SYSTEM))
		{
			list.add(readClass());
		}

		if (lastToken().isNot(Token.EOF))
		{
			throwMessage(2006, "Found tokens after class definitions");
		}

		return list;
	}

	private ClassDefinition readClass() throws ParserException, LexException
	{
		LexNameList superclasses = new LexNameList();

		if (lastToken().is(Token.CLASS) || lastToken().is(Token.SYSTEM))
		{
			boolean system = lastToken().is(Token.SYSTEM);
			setCurrentModule("");
			nextToken();
			LexIdentifierToken classId = readIdToken("Expecting class ID");
			LexNameToken className = classId.getClassName();
			setCurrentModule(classId.name);

			if (lastToken().is(Token.IS))
			{
				nextToken();
				checkFor(Token.SUBCLASS, 2075, "Expecting 'is subclass of'");
				checkFor(Token.OF, 2076, "Expecting 'is subclass of'");

				LexIdentifierToken id = readIdToken("Expecting class identifier");
				superclasses.add(id.getClassName());

				while (ignore(Token.COMMA))
				{
					id = readIdToken("Expecting class identifier");
					superclasses.add(id.getClassName());
				}
			}

			DefinitionList members = getDefinitionReader().readDefinitions();
			checkFor(Token.END, 2077, "Expecting 'end' after class members");

			LexIdentifierToken endname =
				readIdToken("Expecting 'end <name>' after class members");

			if (classId != null && !classId.equals(endname))
			{
				throwMessage(2007, "Expecting 'end " + classId.name + "'");
			}

			return new ClassDefinition(className, superclasses, members, system);
		}
		else
		{
			throwMessage(2008, "Class does not start with 'class'");
		}

		return null;
	}
}
