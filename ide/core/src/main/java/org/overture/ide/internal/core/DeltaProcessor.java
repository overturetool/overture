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

import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.internal.core.ast.IVdmModelManager;

public class DeltaProcessor
{
	/*
	 * The global state of delta processing.
	 */
	private DeltaProcessingState state;
	//private IVdmModelManager manager;

	public DeltaProcessor(DeltaProcessingState state, IVdmModelManager manager) {
		this.state = state;
		//this.manager = manager;
	}
	
	public DeltaProcessingState getState()
	{
		return state;
	}

	public void fire(IVdmElement element, ElementChangedEvent event)
	{
		state.handleEvent(event);
	}
}
