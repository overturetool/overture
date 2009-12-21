package org.overture.ide.vdmsl.ui.internal.outline;

import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.overture.ide.ui.dltk.outline.VdmSourceElementRequestor;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;

public class SourceElementParser extends AbstractSourceElementParser
{
	

	@Override
	protected String getNatureId()
	{
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

	@Override
	protected SourceElementRequestVisitor createVisitor()
	{
		return new VdmSourceElementRequestor(getRequestor());
	}
	
	

}
