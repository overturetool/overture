package org.overture.ide.debug.internal.ui.viewers.update;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.update.DefaultModelProxyFactory;
import org.eclipse.debug.ui.IDebugUIConstants;

@SuppressWarnings("restriction")
public class VdmModelProxyFactory extends DefaultModelProxyFactory
{
@Override
public IModelProxy createModelProxy(Object element,
		IPresentationContext context)
{
	String id = context.getId();
	if (IDebugUIConstants.ID_DEBUG_VIEW.equals(id)) {
		if (element instanceof IDebugTarget) {
			return new VdmDebugTargetProxy((IDebugTarget)element);
		}
	}
	return super.createModelProxy(element, context);
}
}
