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

import org.overture.ide.debug.core.dbgp.DbgpBaseCommands;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpFeature;
import org.overture.ide.debug.core.dbgp.commands.IDbgpFeatureCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlParser;

public class DbgpFeatureCommands extends DbgpBaseCommands implements
		IDbgpFeatureCommands
{

	private static final String FEATURE_SET_COMMAND = "feature_set"; //$NON-NLS-1$

	private static final String FEATURE_GET_COMMAND = "feature_get"; //$NON-NLS-1$

	public DbgpFeatureCommands(IDbgpCommunicator communicator)
	{
		super(communicator);
	}

	public IDbgpFeature getFeature(String featureName) throws DbgpException
	{
		DbgpRequest request = createRequest(FEATURE_GET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		return DbgpXmlEntityParser.parseFeature(communicate(request));
	}

	public boolean setFeature(String featureName, String featureValue)
			throws DbgpException
	{
		DbgpRequest request = createRequest(FEATURE_SET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		request.addOption("-v", featureValue); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}
}
