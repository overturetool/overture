/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.log;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class VdmDebugLogExecutionFilter extends ViewerFilter
{

	String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	String[] ALLOWED_DATA = { "CREATE", "<init appid=", "run -i", "step_over",
			"step_into", "<response command=\"run\" status=\"break\"",
			"<response command=\"run\" status=\"stopped\"",
			"<response command=\"step_over\" status=\"break\"",
			"<response command=\"step_into\" status=\"break\"" };

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof VdmDebugLogItem)
		{
			// return !((VdmDebugLogItem) element).getType().equals("Event");
			for (String allowedStart : ALLOWED_DATA)
			{
				if (((VdmDebugLogItem) element).getMessage().replace(header, "").startsWith(allowedStart))
				{
					return true;
				}
			}
		}
		return false;
	}

}
