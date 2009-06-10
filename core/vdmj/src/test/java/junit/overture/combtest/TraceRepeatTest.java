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

public class TraceRepeatTest extends OvertureTest
{
	public void test_Traces1() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-01", "new UseStack().PushBeforePop()");
	}

	public void test_Traces2() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-02", "new UseStack().PushBeforePop()");
	}

	public void test_Traces3() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-03", "new UseStack().PushBeforePop()");
	}

	public void test_Traces4() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-04", "new UseStack().PushBeforePop()");
	}

	public void test_Traces5() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-051", "new UseStack().trace1()");
		combtest("traces/tracerepeat/tracerepeat-052", "new UseStack().trace2()");
		combtest("traces/tracerepeat/tracerepeat-053", "new UseStack().trace3()");
		combtest("traces/tracerepeat/tracerepeat-054", "new UseStack().trace4()");
		combtest("traces/tracerepeat/tracerepeat-055", "new UseStack().trace5()");
		combtest("traces/tracerepeat/tracerepeat-056", "new UseStack().trace6()");
		combtest("traces/tracerepeat/tracerepeat-057", "new UseStack().trace7()");
		combtest("traces/tracerepeat/tracerepeat-058", "new UseStack().trace8()");
		combtest("traces/tracerepeat/tracerepeat-059", "new UseStack().trace9()");
		combtest("traces/tracerepeat/tracerepeat-0510", "new UseStack().trace10()");
		combtest("traces/tracerepeat/tracerepeat-0511", "new UseStack().trace11()");
		combtest("traces/tracerepeat/tracerepeat-0512", "new UseStack().trace12()");
		combtest("traces/tracerepeat/tracerepeat-0513", "new UseStack().trace13()");
	}

	public void test_Traces6() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-06", "new UseA().trace1()");
	}

	public void test_Traces7() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-07", "new UseA().trace1()");
	}

	public void test_Traces8() throws Exception
	{
		combtest("traces/tracerepeat/tracerepeat-08", "new UseA().trace1()");
	}
}
