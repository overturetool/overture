/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.typechecker;

import java.io.Serializable;

public enum NameScope implements Serializable
{
	LOCAL(1), // Let definitions and parameters
	GLOBAL(2), // Eg. module and class func/ops/values
	STATE(4), // Module state or object instance values
	OLDSTATE(8), // State names with a "~" modifier
	TYPENAME(16), // The names of types
	CLASSNAME(32), // The names of classes
	PROCESSNAME(64), // dirty hack to remove split packaging. FIXME must be changed to propper extensible enums
	VARSTATE(128), // Class instance variables also carry this bit	


	NAMES(3), NAMESANDSTATE(7), NAMESANDANYSTATE(15),
	VARSANDSTATE(128 + 4), VARSANDNAMES(128 + 3);

	private int mask;

	NameScope(int level)
	{
		this.mask = level;
	}

	public boolean matches(NameScope other)
	{
		return (mask & other.mask) != 0;
	}

}
