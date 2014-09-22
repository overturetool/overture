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
package org.overture.ide.debug.core.dbgp.commands;

import java.util.Map;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpDataTypeCommands
{

	final int BOOL_TYPE = 0;
	final int INT_TYPE = 1;
	final int FLOAT_TYPE = 2;
	final int STRING_TYPE = 3;
	final int NULL_TYPE = 4;
	final int ARRAY_TYPE = 5;
	final int HASH_TYPE = 6;
	final int OBJECT_TYPE = 8;
	final int RESOURCE_TYPE = 9;

	Map<String, Integer> getTypeMap() throws DbgpException;
}
