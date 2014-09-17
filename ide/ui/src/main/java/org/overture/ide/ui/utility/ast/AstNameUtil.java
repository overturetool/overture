/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.utility.ast;

import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.PType;

public class AstNameUtil
{
	public static String getName(INode node)
	{
		if(node instanceof PDefinition)
		{
			if(node instanceof AValueDefinition)
			{
				return ((AValueDefinition) node).getPattern().toString();
			}
			return ((PDefinition) node).getName().getName();
		}else if(node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getName()==null?null:((AModuleModules) node).getName().getName();
		}else if(node instanceof PStm)
		{
			return ((PStm) node).getLocation().getModule();
		}else if(node instanceof PExp)
		{
			return ((PExp) node).getLocation().getModule();
		}else if(node instanceof PType)
		{
			return ((PType) node).getLocation().getModule();
		}else if(node instanceof AFieldField)
		{
			return ((AFieldField) node).getTagname().getName();
		}

		return "Unresolved Name";
	}
}
