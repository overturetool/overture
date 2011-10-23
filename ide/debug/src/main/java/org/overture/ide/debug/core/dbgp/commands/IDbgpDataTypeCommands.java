/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import java.util.Map;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpDataTypeCommands {

	final int BOOL_TYPE = 0;
	final int INT_TYPE = 1;
	final int FLOAT_TYPE = 2;
	final int STRING_TYPE = 3;
	final int NULL_TYPE = 4;
	final int ARRAY_TYPE = 5;
	final int HASH_TYPE = 6;
	final int OBJECT_TYPE = 8;
	final int RESOURCE_TYPE = 9;

	Map<String,Integer> getTypeMap() throws DbgpException;
}
