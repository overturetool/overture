/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
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
		IDbgpFeatureCommands {

	private static final String FEATURE_SET_COMMAND = "feature_set"; //$NON-NLS-1$

	private static final String FEATURE_GET_COMMAND = "feature_get"; //$NON-NLS-1$

	public DbgpFeatureCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	public IDbgpFeature getFeature(String featureName) throws DbgpException {
		DbgpRequest request = createRequest(FEATURE_GET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		return DbgpXmlEntityParser.parseFeature(communicate(request));
	}

	public boolean setFeature(String featureName, String featureValue)
			throws DbgpException {
		DbgpRequest request = createRequest(FEATURE_SET_COMMAND);
		request.addOption("-n", featureName); //$NON-NLS-1$
		request.addOption("-v", featureValue); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}
}
