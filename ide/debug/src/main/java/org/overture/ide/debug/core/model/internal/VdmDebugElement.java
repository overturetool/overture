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
package org.overture.ide.debug.core.model.internal;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IVdmDebugElement;
import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.internal.ui.viewers.update.VdmModelProxyFactory;

public abstract class VdmDebugElement extends PlatformObject implements
		IVdmDebugElement
{

	public IVdmDebugTarget getVdmDebugTarget()
	{
		return (IVdmDebugTarget) getDebugTarget();
	}

	public ILaunch getLaunch()
	{
		return getDebugTarget().getLaunch();
	}

	public String getModelIdentifier()
	{
		return getDebugTarget().getModelIdentifier();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if (adapter == IDebugElement.class)
		{
			return this;
		}

		/*
		 * Not implemented currently if (adapter == IStepFilters.class) { return getDebugTarget(); }
		 */

		if (adapter == IDebugTarget.class)
		{
			return getDebugTarget();
		}

		if (adapter == ITerminate.class)
		{
			return getDebugTarget();
		}

		if (adapter == IVdmDebugTarget.class)
		{
			return getVdmDebugTarget();
		}

		if (adapter == ILaunch.class)
		{
			return getLaunch();
		}
		if (adapter == IModelProxyFactory.class)
		{
			return new VdmModelProxyFactory();
		}

		return super.getAdapter(adapter);
	}

	protected void abort(String message, Throwable e) throws DebugException
	{
		throw new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, message, e));
	}

	protected DebugException makeNotSupported(String message, Throwable e)
			throws DebugException
	{
		return new DebugException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, DebugException.NOT_SUPPORTED, message, e));
	}

	protected DebugException wrapDbgpException(String message, DbgpException e)
	{
		return new DebugException(new Status(IStatus.ERROR, DebugPlugin.getUniqueIdentifier(), DebugException.INTERNAL_ERROR, message, e));
	}

	protected DebugException wrapIOException(String message, IOException e)
	{
		return new DebugException(new Status(IStatus.ERROR, DebugPlugin.getUniqueIdentifier(), DebugException.INTERNAL_ERROR, message, e));
	}
}
