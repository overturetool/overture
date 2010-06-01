package org.overture.ide.debug.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.overture.ide.debug.core.model.internal.VdmStackFrame;

public class VdmSourceLookupParticipant extends AbstractSourceLookupParticipant
{

	public String getSourceName(Object object) throws CoreException
	{
		if (object instanceof VdmStackFrame)
		{
			return ((VdmStackFrame) object).getOnlyFileName();
		}
		return null;
	}

}
