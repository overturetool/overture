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

package org.overture.parser.syntax;

import java.util.List;
import java.util.Vector;

import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexCommentList;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.LocatedException;

/**
 * A syntax analyser to parse class definitions.
 */

public class ClassReader extends SyntaxReader
{
	public ClassReader(LexTokenReader reader)
	{
		super(reader);
	}

	public ClassList readClasses()
	{
		ClassList list = new ClassList();

		try
		{
			if (lastToken().is(VDMToken.EOF))
			{
				return list; // The file is empty
			}

			if (lastToken().isNot(VDMToken.CLASS)
					&& lastToken().isNot(VDMToken.SYSTEM))
			{
				warning(5015, "LaTeX source should start with %comment, \\document, \\section or \\subsection", lastToken().location);

				throwMessage(2005, Settings.dialect == Dialect.VDM_RT ? "Expecting list of 'class' or 'system' definitions"
						: "Expecting list of 'class' definitions");
			}

			while (lastToken().is(VDMToken.CLASS)
					|| lastToken().is(VDMToken.SYSTEM))
			{
				ILexCommentList comments = getComments();
				List<PAnnotation> annotations = readAnnotations(comments);
				beforeAnnotations(this, annotations);
				SClassDefinition clazz = null;

				if (lastToken().is(VDMToken.CLASS))
				{
					clazz = readClass();
				}
				else
				{
					clazz = readSystem();
				}
				
				afterAnnotations(this, annotations, clazz);
				clazz.setAnnotations(annotations);
				clazz.setComments(comments);
				list.add(clazz);
			}

			if (lastToken().isNot(VDMToken.EOF))
			{
				throwMessage(2006, "Found tokens after class definitions");
			}
		} catch (LocatedException e)
		{
			VDMToken[] end = new VDMToken[0];
			report(e, end, end);
		}

		return list;
	}

	private SClassDefinition readClass() throws ParserException, LexException
	{
		LexNameList superclasses = new LexNameList();

		if (lastToken().is(VDMToken.CLASS))
		{
			setCurrentModule("");
			nextToken();
			LexIdentifierToken classId = readIdToken("Expecting class ID");
			ILexNameToken className = classId.getClassName();
			setCurrentModule(classId.getName());

			if (lastToken().is(VDMToken.IS))
			{
				nextToken();
				checkFor(VDMToken.SUBCLASS, 2075, "Expecting 'is subclass of'");
				checkFor(VDMToken.OF, 2076, "Expecting 'is subclass of'");

				LexIdentifierToken id = readIdToken("Expecting class identifier");
				superclasses.add(id.getClassName());

				while (ignore(VDMToken.COMMA))
				{
					id = readIdToken("Expecting class identifier");
					superclasses.add(id.getClassName());
				}
			}

			List<PDefinition> members = getDefinitionReader().readDefinitions();
			checkFor(VDMToken.END, 2077, "Expecting 'end' after class members");

			LexIdentifierToken endname = readIdToken("Expecting 'end <name>' after class members");

			if (classId != null && !classId.equals(endname))
			{
				throwMessage(2007, "Expecting 'end " + classId.getName() + "'");
			}

			return AstFactory.newAClassClassDefinition(className, superclasses, members);

			// SClassDefinition def = new
			// AClassClassDefinition(className.location,className,NameScope.CLASSNAME,true,null,new
			// AAccessSpecifierAccessSpecifier(new APublicAccess(), null, null),null,null, null, superclasses,
			// members,null,null,false,ClassDefinitionSettings.UNSET , null, false, null, false,false,false,
			// null,false,null);
			// PDefinitionAssistant.setClassDefinition(def,def);
			// return def;

		} else
		{
			throwMessage(2008, "Class does not start with 'class'");
		}

		return null;
	}

	private ASystemClassDefinition readSystem() throws ParserException,
			LexException
	{
		if (lastToken().is(VDMToken.SYSTEM))
		{
			setCurrentModule("");
			nextToken();
			LexIdentifierToken classId = readIdToken("Expecting class ID");
			ILexNameToken className = classId.getClassName();
			setCurrentModule(classId.getName());

			if (lastToken().is(VDMToken.IS))
			{
				nextToken();
				checkFor(VDMToken.SUBCLASS, 2075, "Expecting 'is subclass of'");
				checkFor(VDMToken.OF, 2076, "Expecting 'is subclass of'");

				throwMessage(2280, "System class cannot be a subclass");
			}

			List<PDefinition> members = new Vector<PDefinition>();
			DefinitionReader dr = getDefinitionReader();

			while (lastToken().is(VDMToken.INSTANCE)
					|| lastToken().is(VDMToken.OPERATIONS))
			{
				if (lastToken().is(VDMToken.INSTANCE))
				{
					members.addAll(dr.readInstanceVariables());
				} else
				{
					members.addAll(dr.readOperations());
				}
			}

			switch (lastToken().type)
			{
				case TYPES:
				case VALUES:
				case FUNCTIONS:
				case THREAD:
				case SYNC:
					throwMessage(2290, "System class can only define instance variables and a constructor");
					break;

				case END:
					nextToken();
					break;

				default:
					throwMessage(2077, "Expecting 'end' after system members");
			}

			LexIdentifierToken endname = readIdToken("Expecting 'end <name>' after system members");

			if (classId != null && !classId.equals(endname))
			{
				throwMessage(2007, "Expecting 'end " + classId.getName() + "'");
			}

			return AstFactory.newASystemClassDefinition(className, members);// new
																			// ASystemClassDefinition(className.location,className,NameScope.CLASSNAME,true,null,new
																			// AAccessSpecifierAccessSpecifier(new
																			// APublicAccess(), null, null),null,
																			// null,null, new LexNameList(),
																			// members,null,null,false,null, null,
																			// false, null,
																			// false,false,false,null,false,null);
			// for (PDefinition pDefinition : def.getDefinitions())
			// {
			// pDefinition.setClassDefinition(def);
			// }
			// return def;
		} else
		{
			throwMessage(2008, "System class does not start with 'system'");
		}

		return null;
	}
}
