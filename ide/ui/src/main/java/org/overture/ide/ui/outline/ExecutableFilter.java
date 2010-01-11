package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
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
				&& (element instanceof ExplicitOperationDefinition || element instanceof ExplicitFunctionDefinition)
				&& (element instanceof Definition && ((Definition) element).accessSpecifier.access == Token.PUBLIC)
				)
				
				
				|| (parentElement instanceof Module &&  (element instanceof ExplicitFunctionDefinition||element instanceof ExplicitOperationDefinition)	);

	}

}