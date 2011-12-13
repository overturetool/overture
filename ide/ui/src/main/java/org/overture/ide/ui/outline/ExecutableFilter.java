/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InheritedDefinition;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.modules.Module;

public class ExecutableFilter extends ViewerFilter
{

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return element instanceof ClassDefinition || 
				element instanceof Module || 
				(
						parentElement instanceof ClassDefinition
				&& (element instanceof ExplicitOperationDefinition || element instanceof ExplicitFunctionDefinition || element instanceof InheritedDefinition)
				&& (element instanceof Definition && ((Definition) element).accessSpecifier.access == Token.PUBLIC)
				)
				
				
				|| (parentElement instanceof Module &&  (element instanceof ExplicitFunctionDefinition||element instanceof ExplicitOperationDefinition)	);

	}

}