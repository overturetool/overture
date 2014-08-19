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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AClassTypeAssistantTC
{

	protected ITypeCheckerAssistantFactory af;

	public AClassTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public LexNameToken getMemberName(AClassType cls, ILexIdentifierToken id)
	{
		// Note: not explicit
		return new LexNameToken(cls.getName().getName(), id.getName(), id.getLocation(), false, false);
	}

	// TODO:Used in the TypeCheckerExpVisitor.

	public PDefinition findName(AClassType cls, ILexNameToken tag,
			NameScope scope)
	{
		return af.createPDefinitionAssistant().findName(cls.getClassdef(), tag, scope);
	}

	// Used in the SClassDefinitionAssistantTC.
	public boolean hasSupertype(AClassType sclass, PType other)
	{
		return af.createSClassDefinitionAssistant().hasSupertype(sclass.getClassdef(), other);
	}

}
