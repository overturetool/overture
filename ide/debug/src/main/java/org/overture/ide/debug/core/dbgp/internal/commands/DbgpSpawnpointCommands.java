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
package org.overture.ide.debug.core.dbgp.internal.commands;

import java.net.URI;

import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpFeature;
import org.overture.ide.debug.core.dbgp.IDbgpSpawnpoint;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpFeatureCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpSpawnpointCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpSpawnpointCommands extends DbgpBaseCommands implements
		IDbgpSpawnpointCommands
{

	private static final String ELEMENT_SPAWNPOINT = "spawnpoint"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_STATE = "state"; //$NON-NLS-1$
	private static final String ATTR_FILENAME = "filename"; //$NON-NLS-1$
	private static final String ATTR_LINENO = "lineno"; //$NON-NLS-1$

	private static final String CMD_GET = "spawnpoint_get"; //$NON-NLS-1$
	private static final String CMD_SET = "spawnpoint_set"; //$NON-NLS-1$
	private static final String CMD_LIST = "spawnpoint_list"; //$NON-NLS-1$
	private static final String CMD_REMOVE = "spawnpoint_remove"; //$NON-NLS-1$
	private static final String CMD_UPDATE = "spawnpoint_update"; //$NON-NLS-1$

	private static final String OPTION_ID = "-d"; //$NON-NLS-1$
	private static final String OPTION_FILENAME = "-f"; //$NON-NLS-1$
	private static final String OPTION_LINE = "-n"; //$NON-NLS-1$
	private static final String OPTION_STATE = "-s"; //$NON-NLS-1$

	private static final String STATE_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String STATE_DISABLED = "disabled"; //$NON-NLS-1$

	private final IDbgpCommands commands;

	/**
	 * @param communicator
	 * @param commands
	 */
	public DbgpSpawnpointCommands(IDbgpCommunicator communicator,
			IDbgpCommands commands)
	{
		super(communicator);
		this.commands = commands;
	}

	private boolean initialized = false;

	private void initSpawnpoints()
	{
		if (initialized)
		{
			return;
		}
		try
		{
			commands.getCoreCommands().setFeature(IDbgpFeatureCommands.MULTIPLE_SESSIONS, IDbgpFeature.ONE_VALUE);
		} catch (DbgpException e)
		{
			VdmDebugPlugin.logWarning("Error setting '" //$NON-NLS-1$
					+ IDbgpFeatureCommands.MULTIPLE_SESSIONS
					+ "' feature to '" + IDbgpFeature.ONE_VALUE + "'", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		initialized = true;
	}

	private static abstract class AbstractSpawnpoint implements IDbgpSpawnpoint
	{
		private final String id;
		private final boolean enabled;
		private final int lineNumber;

		/**
		 * @param id
		 * @param enabled
		 * @param lineNumber
		 */
		public AbstractSpawnpoint(String id, boolean enabled, int lineNumber)
		{
			this.id = id;
			this.enabled = enabled;
			this.lineNumber = lineNumber;
		}

		public String getId()
		{
			return id;
		}

		public int getLineNumber()
		{
			return lineNumber;
		}

		public boolean isEnabled()
		{
			return enabled;
		}

		public String toString()
		{
			return id + '/' + (enabled ? STATE_ENABLED : STATE_DISABLED) + '/'
					+ getFilename() + ':' + lineNumber;
		}

	}

	private static class DbgpSpawnpoint extends AbstractSpawnpoint
	{

		private final URI uri;

		/**
		 * @param id
		 * @param equals
		 * @param uri
		 * @param lineNumber
		 */
		public DbgpSpawnpoint(String id, boolean enabled, URI uri,
				int lineNumber)
		{
			super(id, enabled, lineNumber);
			this.uri = uri;
		}

		public String getFilename()
		{
			return uri.toString();
		}
	}

	private static class DbgpSpawnpointString extends AbstractSpawnpoint
	{

		private final String filename;

		/**
		 * @param id
		 * @param equals
		 * @param uri
		 * @param lineNumber
		 */
		public DbgpSpawnpointString(String id, boolean enabled,
				String filename, int lineNumber)
		{
			super(id, enabled, lineNumber);
			this.filename = filename;
		}

		public String getFilename()
		{
			return filename;
		}
	}

	public IDbgpSpawnpoint getSpawnpoint(String spawnpointId)
			throws DbgpException
	{
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_GET);
		request.addOption(OPTION_ID, spawnpointId);
		final Element response = communicate(request);
		final String id = response.getAttribute(ATTR_ID);
		if (id != null)
		{
			return new DbgpSpawnpointString(id, parseState(response), response.getAttribute(ATTR_FILENAME), parseLineNumber(response));
		} else
		{
			return null;
		}
	}

	public IDbgpSpawnpoint[] listSpawnpoints() throws DbgpException
	{
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_LIST);
		final Element response = communicate(request);
		final NodeList points = response.getElementsByTagName(ELEMENT_SPAWNPOINT);
		final IDbgpSpawnpoint[] result = new IDbgpSpawnpoint[points.getLength()];
		int count = 0;
		for (int i = 0; i < points.getLength(); ++i)
		{
			final Element point = (Element) points.item(i);
			final String id = point.getAttribute(ATTR_ID);
			if (id != null)
			{
				result[count++] = new DbgpSpawnpointString(id, parseState(point), point.getAttribute(ATTR_FILENAME), parseLineNumber(point));
			}
		}
		if (count < result.length)
		{
			final IDbgpSpawnpoint[] newResult = new IDbgpSpawnpoint[count];
			System.arraycopy(result, 0, newResult, 0, count);
			return newResult;
		} else
		{
			return result;
		}
	}

	public void removeSpawnpoint(String spawnpointId) throws DbgpException
	{
		if (spawnpointId == null)
		{
			return;
		}
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_REMOVE);
		request.addOption(OPTION_ID, spawnpointId);
		communicate(request);
	}

	public IDbgpSpawnpoint setSpawnpoint(URI uri, int lineNumber,
			boolean enabled) throws DbgpException
	{
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_SET);
		request.addOption(OPTION_FILENAME, uri.toString());
		request.addOption(OPTION_LINE, lineNumber);
		request.addOption(OPTION_STATE, enabled ? STATE_ENABLED
				: STATE_DISABLED);
		final Element response = communicate(request);
		final String id = response.getAttribute(ATTR_ID);
		if (id != null)
		{
			return new DbgpSpawnpoint(id, parseState(response), uri, lineNumber);
		} else
		{
			return null;
		}
	}

	public void updateSpawnpoint(String spawnpointId, boolean enabled)
			throws DbgpException
	{
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_UPDATE);
		request.addOption(OPTION_ID, spawnpointId);
		request.addOption(OPTION_STATE, enabled ? STATE_ENABLED
				: STATE_DISABLED);
		communicate(request);
	}

	private static boolean parseState(final Element response)
	{
		return STATE_ENABLED.equals(response.getAttribute(ATTR_STATE));
	}

	private static int parseLineNumber(final Element point)
	{
		try
		{
			return Integer.parseInt(point.getAttribute(ATTR_LINENO));
		} catch (NumberFormatException e)
		{
			return -1;
		}
	}

}
