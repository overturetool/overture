/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

import org.overture.ide.debug.core.IDebugConfigurable;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.w3c.dom.Element;

public interface IDbgpCommunicator extends IDebugConfigurable {
	Element communicate(DbgpRequest request) throws DbgpException;

	void send(DbgpRequest request) throws DbgpException;
}
