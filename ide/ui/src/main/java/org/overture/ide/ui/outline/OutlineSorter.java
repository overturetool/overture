package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.types.Type;

class OutlineSorter extends ViewerSorter
{
	private final static int TYPES = 1;
	private final static int VALUES = 0;

	// private final static int INSTANCEVARIABLES = 2;
	// private final static int OPERATIONS = 3;
	// private final static int FUNCTIONS = 4;
	// private final static int THREADS = 5;
	// private final static int SYN = 6;
	// private final static int TRACES = 7;

	@Override
	public int category(Object element)
	{
		if (element instanceof Type)
		{
			return TYPES;
		} else if (element instanceof Definition)
		{
			return VALUES;
		} else
			return super.category(element);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 != cat2)
		{
			return cat1 - cat2;
		}
		
		if(e1 instanceof IAstNode && e2 instanceof IAstNode)
		{
			return collator.compare(((IAstNode)e1).getName(), ((IAstNode)e2).getName());
		}else
		return super.compare(viewer, e1, e2);
	}
}