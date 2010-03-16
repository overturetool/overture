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
	private IVdmModelManager manager;

	public DeltaProcessor(DeltaProcessingState state, IVdmModelManager manager) {
		this.state = state;
		this.manager = manager;
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
