/*******************************************************************************
 *
 *	Copyright (c) 2009 IHA
 *
 *	Author: Peter Gorm Larsen
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

package junit.overture.combtest;

import junit.overture.OvertureTest;

public class TraceBracketTest extends OvertureTest
{
	public void test_Traces1()
	{
		combtest("traces/tracebracket/tracebracket-01", "new UseStack().PushBeforePop()");
	}

	public void test_Traces2()
	{
		combtest("traces/tracebracket/tracebracket-02", "new UseStack().PushBeforePop()");
	}
}
