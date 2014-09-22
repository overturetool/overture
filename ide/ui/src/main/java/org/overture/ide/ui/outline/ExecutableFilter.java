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
package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.modules.AModuleModules;

public class ExecutableFilter extends ViewerFilter
{

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return element instanceof SClassDefinition
				|| element instanceof AModuleModules
				|| parentElement instanceof SClassDefinition
				&& (element instanceof SOperationDefinition
						&& ((SOperationDefinition) element).getBody() != null
						|| element instanceof SFunctionDefinition
						&& ((SFunctionDefinition) element).getBody() != null || element instanceof AInheritedDefinition)
				&& element instanceof PDefinition
				&& ((PDefinition) element).getAccess().getAccess() instanceof APublicAccess
				|| parentElement instanceof AModuleModules
				&& (element instanceof SFunctionDefinition
						&& ((SFunctionDefinition) element).getBody() != null || element instanceof SOperationDefinition
						&& ((SOperationDefinition) element).getBody() != null);

	}

}
