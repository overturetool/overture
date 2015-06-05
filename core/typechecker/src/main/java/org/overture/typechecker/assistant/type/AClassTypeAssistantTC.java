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

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AClassTypeAssistantTC implements IAstAssistant
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

	/**
	 * Test whether a definition is a class constructor.
	 */
	public boolean isConstructor(PDefinition def)
	{
		if (def instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinition op = (AExplicitOperationDefinition)def;
			return op.getIsConstructor();
		}
		else if (def instanceof AImplicitOperationDefinition)
		{
			AImplicitOperationDefinition op = (AImplicitOperationDefinition)def;
			return op.getIsConstructor();
		}
		else if (def instanceof AInheritedDefinition)
		{
			AInheritedDefinition op = (AInheritedDefinition)def;
			return isConstructor(op.getSuperdef());
		}
		
		return false;
	}

	/**
	 * Test whether the calling environment indicates that we are within a constructor.
	 */
	public boolean inConstructor(Environment env)
	{
		PDefinition encl = env.getEnclosingDefinition();
	
		if (encl != null)
		{
			return isConstructor(encl);
		}
		
		return false;
	}
}
