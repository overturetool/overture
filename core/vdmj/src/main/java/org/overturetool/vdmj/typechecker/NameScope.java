/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.typechecker;

import java.io.Serializable;

/**
 * An enum to represent name scoping. When names are being looked up with
 * {@link org.overturetool.vdmj.definitions.Definition#findName findName}, the scope is used
 * to indicate what sorts of names are being sought. When a specification is
 * being type checked, the {@link org.overturetool.vdmj.definitions.Definition#typeCheck
 * typeCheck} method uses NameScope to indicate what names are permitted when
 * checking the content. For example, an operation would be type checked under
 * the NAMESANDSTATE scope, but when its post condition is checked, it would
 * be checked under NAMESANDANYSTATE to include the "old" names.
 */

public enum NameScope implements Serializable
{
	LOCAL(1),		// Let definitions and parameters
	GLOBAL(2),		// Eg. module and class func/ops/values
	STATE(4),		// Module state or object instance values
	OLDSTATE(8),	// State names with a "~" modifier
	TYPENAME(16),	// The names of types
	CLASSNAME(32),	// The names of classes

	NAMES(3),
	NAMESANDSTATE(7),
	NAMESANDANYSTATE(15);

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
