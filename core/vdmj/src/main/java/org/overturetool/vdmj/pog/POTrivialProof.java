/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.pog;

public enum POTrivialProof
{
	FORALL_IN_SET("^( *\\(+(forall|let)[^\\n]+\\n)*? *\\(forall (\\w+) in set \\(([^&]+)\\) &(.+?)?\\n *\\3 in set \\4\\)+\\n$", "forall x in set s & x in set s"),
	IMPLICATION("^( *\\(+(forall|let)[^\\n]+\\n?)*? *\\(+(\\w+) in set \\(([^&]+)\\)+ =>\\n *\\3 in set \\4\\)+\\n$", "x in set s => x in set s"),
	NOT_EQUALITY("^( *\\(+(forall|let)[^\\n]+\\n)*? *\\(+not \\((.+?) \\= (.+?)\\) =>\\n *\\3 \\<\\> \\4\\)+\\n$", "not x = y => x <> y");

	private String pattern;
	public String name;

	POTrivialProof(String pattern, String name)
	{
		this.pattern = pattern;
		this.name = name;
	}

	public boolean proves(String PO)
	{
		return PO.matches(pattern);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
