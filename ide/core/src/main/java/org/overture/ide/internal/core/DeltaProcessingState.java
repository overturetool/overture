/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.internal.core;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IElementChangedListener;


public class DeltaProcessingState
{
	public List<IElementChangedListener> elementChangedListeners = new ArrayList<IElementChangedListener>();
	
	public synchronized void addElementChangedListener(IElementChangedListener listener)
	{
		elementChangedListeners.add(listener);

	}

	public synchronized void removeElementChangedListener(IElementChangedListener listener)
	{
		elementChangedListeners.remove(listener);

	}
	
	
	
	protected synchronized IElementChangedListener[] getListeners() {
		IElementChangedListener[] arr = new IElementChangedListener[elementChangedListeners.size()];
		return elementChangedListeners.toArray(arr);
		
	}
	
	public void handleEvent(final ElementChangedEvent event)
	{
		notify(getListeners(),event);
	}

	private void notify(IElementChangedListener[] resourceListeners,final ElementChangedEvent event)
	{
		//int type = event.getType();

		try
		{
			for (int i = 0; i < resourceListeners.length; i++)
			{

				final IElementChangedListener listener = resourceListeners[i];

				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e)
					{
						// exception logged in SafeRunner#run
					}

					public void run() throws Exception
					{
						listener.elementChanged(event);
					}
				});

			}

		} finally
		{

		}
	}
}
