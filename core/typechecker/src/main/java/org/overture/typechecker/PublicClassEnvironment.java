/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overture.typechecker;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Define the type checking environment for a set of classes, as observed from the outside.
 */

public class PublicClassEnvironment extends Environment
{

	private final List<SClassDefinition> classes;

	public List<PDefinition> getDefinitions()
	{
		List<PDefinition> res = new LinkedList<PDefinition>();
		for (PDefinition d : classes)
		{
			res.add(d);
		}
		return res;
	}

	public PublicClassEnvironment(ITypeCheckerAssistantFactory af,
			List<SClassDefinition> classes)
	{
		super(af, null);
		this.classes = classes;
	}

	public PublicClassEnvironment(ITypeCheckerAssistantFactory af,
			List<SClassDefinition> classes, Environment env)
	{
		super(af, env);
		this.classes = classes;
	}

	public PublicClassEnvironment(ITypeCheckerAssistantFactory af,
			SClassDefinition one)
	{
		super(af, null);
		this.classes = new Vector<SClassDefinition>();
		this.classes.add(one);
	}

	public PublicClassEnvironment(ITypeCheckerAssistantFactory af,
			SClassDefinition one, Environment env)
	{
		super(af, env);
		this.classes = new Vector<SClassDefinition>();
		this.classes.add(one);
	}

	@Override
	public PDefinition findName(ILexNameToken name, NameScope scope)
	{
		PDefinition def = af.createSClassDefinitionAssistant().findName(classes, name, scope);

		if (def != null
				&& af.createPAccessSpecifierAssistant().isPublic(def.getAccess())
				&& !ExcludedDefinitions.isExcluded(def))
		{
			return def;
		}

		return outer == null ? null : outer.findName(name, scope);
	}

	@Override
	public PDefinition findType(ILexNameToken name, String fromModule)
	{
		PDefinition def = af.createSClassDefinitionAssistant().findType(classes, name);

		if (def != null
				&& af.createPAccessSpecifierAssistant().isPublic(def.getAccess()))
		{
			return def;
		}

		return outer == null ? null : outer.findType(name, null);
	}

	@Override
	public Set<PDefinition> findMatches(ILexNameToken name)
	{
		Set<PDefinition> defs = af.createSClassDefinitionAssistant().findMatches(classes, name);

		if (outer != null)
		{
			defs.addAll(outer.findMatches(name));
		}

		return defs;
	}

	@Override
	public void unusedCheck()
	{
		af.createSClassDefinitionAssistant().unusedCheck(classes);
	}

	@Override
	public AStateDefinition findStateDefinition()
	{
		return null;
	}

	@Override
	public boolean isVDMPP()
	{
		return true;
	}

	@Override
	public boolean isSystem()
	{
		return false; // See PrivateClassEnvironment
	}

	@Override
	public SClassDefinition findClassDefinition()
	{
		return null;
	}

	@Override
	public boolean isStatic()
	{
		return false;
	}

}
