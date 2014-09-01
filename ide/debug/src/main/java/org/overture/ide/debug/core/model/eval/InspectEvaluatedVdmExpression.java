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
package org.overture.ide.debug.core.model.eval;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugElement;

public class InspectEvaluatedVdmExpression extends EvaluatedVdmExpression
		implements IDebugEventSetListener
{

	public InspectEvaluatedVdmExpression(IVdmEvaluationResult result)
	{
		super(result);

		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	public void handleDebugEvents(DebugEvent[] events)
	{
		for (int i = 0; i < events.length; i++)
		{
			DebugEvent event = events[i];
			switch (event.getKind())
			{
				case DebugEvent.TERMINATE:
					if (event.getSource().equals(getDebugTarget()))
					{
						DebugPlugin.getDefault().getExpressionManager().removeExpression(this);
					}
					break;
				case DebugEvent.SUSPEND:
					if (event.getDetail() != DebugEvent.EVALUATION_IMPLICIT)
					{
						if (event.getSource() instanceof IDebugElement)
						{
							IDebugElement source = (IDebugElement) event.getSource();
							if (source.getDebugTarget().equals(getDebugTarget()))
							{
								DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { new DebugEvent(this, DebugEvent.CHANGE, DebugEvent.CONTENT) });
							}
						}
					}
					break;
			}
		}
	}

	public void dispose()
	{
		super.dispose();

		DebugPlugin.getDefault().removeDebugEventListener(this);
	}
}
