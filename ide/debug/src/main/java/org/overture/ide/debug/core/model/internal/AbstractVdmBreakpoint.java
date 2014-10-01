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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.model.IVdmBreakpoint;

public abstract class AbstractVdmBreakpoint extends Breakpoint implements
		IVdmBreakpoint
{

	/**
	 * Debugging engine breakpoint identifier (available only during debug session)
	 * 
	 * @deprecated
	 */
	public static final String ENGINE_IDENTIFIER = VdmDebugPlugin.PLUGIN_ID
			+ ".id"; //$NON-NLS-1$

	/**
	 * The number of breakpoint hits during debug session (available only during debug session)
	 * 
	 * @deprecated
	 */
	public static final String HIT_COUNT = VdmDebugPlugin.PLUGIN_ID
			+ ".hit_count"; //$NON-NLS-1$

	/**
	 * Condition expression that should be valid for suspend on this breakpoint
	 */
	public static final String EXPRESSION = VdmDebugPlugin.PLUGIN_ID
			+ ".expression"; //$NON-NLS-1$

	/**
	 * State of condition expression (enabled or disabled)
	 */
	public static final String EXPRESSION_STATE = EXPRESSION + ".state"; //$NON-NLS-1$

	/**
	 * The number of hits for suspend on this breakpoint
	 */
	public static final String HIT_VALUE = VdmDebugPlugin.PLUGIN_ID
			+ ".hit_value"; //$NON-NLS-1$

	/**
	 * The hit condition related to hit value
	 */
	public static final String HIT_CONDITION = VdmDebugPlugin.PLUGIN_ID
			+ ".hit_condition"; //$NON-NLS-1$

	public static final String PATH_DELIMITER = ";"; //$NON-NLS-1$
	private static final char SEPARATOR = '/';

	private static boolean isFull(IPath path)
	{
		String device = path.getDevice();
		return device != null && device.indexOf(SEPARATOR) >= 0;
	}

	private static IPath getLocalPath(IPath path)
	{
		// if( path.segment(0).startsWith("#special#")) {
		// return path;
		// }
		if (!isFull(path))
		{
			return path;
			// throw new RuntimeException("Invalid path");
		}

		String device = path.getDevice();
		int index = device.indexOf(SEPARATOR);
		Assert.isTrue(index >= 0);
		device = device.substring(index + 1);
		if (device.length() == 1 && device.charAt(0) == IPath.DEVICE_SEPARATOR)
		{
			device = null;
		}

		return path.setDevice(device);
	}

	public static URI makeUri(IPath location)
	{
		try
		{
			String path = getLocalPath(location).toString();
			if (path.length() != 0 && path.charAt(0) != '/')
			{
				path = '/' + path;
			}
			return new URI("file", "", path, null); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (URISyntaxException e)
		{
			VdmDebugPlugin.log(e);
		}
		return null;
	}

	private String debugModelId;

	protected void addVdmBreakpointAttributes(Map<String, Object> attributes,
			String debugModelId, boolean enabled)
	{
		this.debugModelId = debugModelId;
		attributes.put(IBreakpoint.ID, debugModelId);
		attributes.put(IBreakpoint.ENABLED, Boolean.valueOf(enabled));
	}

	public AbstractVdmBreakpoint()
	{

	}

	public String getModelIdentifier()
	{
		if (debugModelId == null)
		{
			try
			{
				debugModelId = ensureMarker().getAttribute(IBreakpoint.ID, null);
			} catch (DebugException e)
			{
				if (VdmDebugPlugin.DEBUG)
				{
					e.printStackTrace();
				}
				return null;
			}
		}
		return debugModelId;
	}

	private static class PerSessionInfo
	{
		String identifier;
		int hitCount = -1;
	}

	private final Map<IDbgpSession, PerSessionInfo> sessions = new IdentityHashMap<IDbgpSession, PerSessionInfo>(1);

	/*
	 * @see IScriptBreakpoint#getId(IDbgpSession)
	 */
	public String getId(IDbgpSession session)
	{
		final PerSessionInfo info;
		synchronized (sessions)
		{
			info = (PerSessionInfo) sessions.get(session);
		}
		return info != null ? info.identifier : null;
	}

	/*
	 * @see IScriptBreakpoint#setId(IDbgpSession, java.lang.String)
	 */
	public void setId(IDbgpSession session, String identifier)
	{
		synchronized (sessions)
		{
			PerSessionInfo info = (PerSessionInfo) sessions.get(session);
			if (info == null)
			{
				info = new PerSessionInfo();
				sessions.put(session, info);
			}
			info.identifier = identifier;
		}
	}

	/*
	 * @see IScriptBreakpoint#removeId(IDbgpSession)
	 */
	public String removeId(IDbgpSession session)
	{
		final PerSessionInfo info;
		synchronized (sessions)
		{
			info = (PerSessionInfo) sessions.remove(session);
		}
		return info != null ? info.identifier : null;
	}

	/*
	 * @see IScriptBreakpoint#clearSessionInfo()
	 */
	public void clearSessionInfo()
	{
		synchronized (sessions)
		{
			sessions.clear();
		}
	}

	/*
	 * @see IScriptBreakpoint#getIdentifiers()
	 */
	public String[] getIdentifiers()
	{
		final PerSessionInfo[] infos;
		synchronized (sessions)
		{
			infos = (PerSessionInfo[]) sessions.values().toArray(new PerSessionInfo[sessions.size()]);
		}
		int count = 0;
		for (int i = 0; i < infos.length; ++i)
		{
			if (infos[i] != null && infos[i].identifier != null)
			{
				++count;
			}
		}
		if (count > 0)
		{
			final String[] result = new String[count];
			int index = 0;
			for (int i = 0; i < infos.length; ++i)
			{
				if (infos[i] != null && infos[i].identifier != null)
				{
					result[index++] = infos[i].identifier;
				}
			}
			return result;
		} else
		{
			return null;
		}
	}

	/*
	 * @see IScriptBreakpoint#setHitCount(IDbgpSession, int)
	 */
	public void setHitCount(IDbgpSession session, int value)
			throws CoreException
	{
		synchronized (sessions)
		{
			PerSessionInfo info = (PerSessionInfo) sessions.get(session);
			if (info == null)
			{
				info = new PerSessionInfo();
				sessions.put(session, info);
			}
			info.hitCount = value;
		}
	}

	/*
	 * @see IScriptBreakpoint#getHitCount(IDbgpSession)
	 */
	public int getHitCount(IDbgpSession session) throws CoreException
	{
		final PerSessionInfo info;
		synchronized (sessions)
		{
			info = (PerSessionInfo) sessions.get(session);
		}
		return info != null ? info.hitCount : -1;
	}

	// Identifier
	public String getIdentifier() throws CoreException
	{
		return null;
	}

	public void setIdentifier(String id) throws CoreException
	{
		//
	}

	// Message
	public String getMessage() throws CoreException
	{
		return ensureMarker().getAttribute(IMarker.MESSAGE, null);
	}

	public void setMessage(String message) throws CoreException
	{
		setAttribute(IMarker.MESSAGE, message);
	}

	// Hit count
	public int getHitCount() throws CoreException
	{
		synchronized (sessions)
		{
			if (sessions.isEmpty())
			{
				return -1;
			}
			int result = 0;
			for (Iterator<PerSessionInfo> i = sessions.values().iterator(); i.hasNext();)
			{
				PerSessionInfo info = (PerSessionInfo) i.next();
				if (info.hitCount > 0)
				{
					result += info.hitCount;
				}
			}
			return result > 0 ? result : -1;
		}
	}

	public void setHitCount(int value) throws CoreException
	{
		//
	}

	// Hit value
	public int getHitValue() throws CoreException
	{
		return ensureMarker().getAttribute(HIT_VALUE, -1);
	}

	public void setHitValue(int hitValue) throws CoreException
	{
		if (getHitValue() != hitValue)
		{
			setAttribute(HIT_VALUE, hitValue);
		}
	}

	// Hit condition
	public int getHitCondition() throws CoreException
	{
		return ensureMarker().getAttribute(HIT_CONDITION, -1);
	}

	public void setHitCondition(int condition) throws CoreException
	{
		if (getHitCondition() != condition)
		{
			setAttribute(HIT_CONDITION, condition);
		}
	}

	// Resource name
	public String getResourceName() throws CoreException
	{
		return ensureMarker().getResource().getName();
	}

	// Expression
	public String getExpression() throws CoreException
	{
		return ensureMarker().getAttribute(EXPRESSION, null);
	}

	public void setExpression(String expression) throws CoreException
	{
		if (!StrUtils.equals(getExpression(), expression))
		{
			setAttribute(EXPRESSION, expression);
		}
	}

	public boolean getExpressionState() throws CoreException
	{
		return ensureMarker().getAttribute(EXPRESSION_STATE, false);
	}

	public void setExpressionState(boolean state) throws CoreException
	{
		if (getExpressionState() != state)
		{
			setAttribute(EXPRESSION_STATE, state);
		}
	}

	/**
	 * Add this breakpoint to the breakpoint manager, or sets it as unregistered.
	 */
	public void register(boolean register) throws CoreException
	{
		DebugPlugin plugin = DebugPlugin.getDefault();
		if (plugin != null && register)
		{
			plugin.getBreakpointManager().addBreakpoint(this);
		} else
		{
			setRegistered(false);
		}
	}
}
