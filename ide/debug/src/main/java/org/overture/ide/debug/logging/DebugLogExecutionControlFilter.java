package org.overture.ide.debug.logging;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class DebugLogExecutionControlFilter extends ViewerFilter
{

	String[] ALLOWED_DATA = {"<init thread=","run -i","<response status=\"break\"","<response status=\"stopped\""};
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if(element instanceof LogItem)
		{
			LogItem item = (LogItem) element;
			for (String  allowedStart : ALLOWED_DATA)
			{
				if(item.getData().startsWith(allowedStart))
				{
					return true;
				}
			}
		}
		return false;
	}

}
