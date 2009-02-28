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

package org.overturetool.vdmj.patterns;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.MultiBindListDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;

public class PatternBind
{
	public final LexLocation location;
	public final Pattern pattern;
	public final Bind bind;

	private DefinitionList defs = null;

	public PatternBind(LexLocation location, Object patternOrBind)
	{
		this.location = location;

		if (patternOrBind instanceof Pattern)
		{
			this.pattern = (Pattern)patternOrBind;
			this.bind = null;
		}
		else if (patternOrBind instanceof Bind)
		{
			this.pattern = null;
			this.bind = (Bind)patternOrBind;
		}
		else
		{
			throw new MessageException(
				"Internal 0003: PatternBind passed " + patternOrBind.getClass().getName());
		}
	}

	@Override
	public String toString()
	{
		return (pattern == null ? bind : pattern).toString();
	}

	public DefinitionList getDefinitions()
	{
		assert (defs != null) :
			"PatternBind must be type checked before getDefinitions";

		return defs;
	}

	public void typeCheck(Environment base, NameScope scope, Type type)
	{
		defs = null;

		if (bind != null)
		{
			if (bind instanceof TypeBind)
			{
				TypeBind typebind = (TypeBind)bind;
				typebind.typeResolve(base);

				if (!TypeComparator.compatible(typebind.type, type))
				{
					bind.report(3198, "Type bind not compatible with expression");
					bind.detail2("Bind", typebind.type, "Exp", type);
				}
			}
			else
			{
				SetBind setbind = (SetBind)bind;
				SetType settype = setbind.set.typeCheck(base, null, scope).getSet();

				if (!TypeComparator.compatible(type, settype.setof))
				{
					bind.report(3199, "Set bind not compatible with expression");
					bind.detail2("Bind", settype.setof, "Exp", type);
				}
			}

			Definition def =
				new MultiBindListDefinition(bind.location, bind.getMultipleBindList());

			def.typeCheck(base, scope);
			defs = new DefinitionList(def);
		}
		else
		{
			assert (type != null) :
					"Can't typecheck a pattern without a type";

			pattern.typeResolve(base);
			defs = pattern.getDefinitions(type, NameScope.LOCAL);
		}
	}
}
