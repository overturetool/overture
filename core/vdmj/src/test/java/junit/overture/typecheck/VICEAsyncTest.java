/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package junit.overture.typecheck;

import junit.overture.OvertureTest;

public class VICEAsyncTest extends OvertureTest
{
	public void test_async1() throws Exception
	{
		typecheck("VICE/async/async-01");
	}

	public void test_async2() throws Exception
	{
		typecheck("VICE/async/async-02");
	}

	public void test_async3() throws Exception
	{
		typecheck("VICE/async/async-03");
	}

	public void test_async4() throws Exception
	{
		typecheck("VICE/async/async-04");
	}

	public void test_async5() throws Exception
	{
		typecheck("VICE/async/async-05");
	}
}
