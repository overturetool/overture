package org.overture.ide.debug.ui.actions;

import org.eclipse.debug.ui.actions.ContextualLaunchAction;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class VdmDebugContextualLaunchAction extends ContextualLaunchAction implements IViewActionDelegate
{

	public VdmDebugContextualLaunchAction()
	{
		super("debug");
	}

	public void init(IViewPart view)
	{
		
	}

}
