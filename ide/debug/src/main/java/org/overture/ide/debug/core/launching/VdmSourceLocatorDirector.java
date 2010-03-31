package org.overture.ide.debug.core.launching;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class VdmSourceLocatorDirector extends AbstractSourceLookupDirector
{

	public void initializeParticipants()
	{
		addParticipants(new ISourceLookupParticipant[] { new VdmSourceLookupParticipant() });

	}

}
