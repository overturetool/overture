/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
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

package org.overture.codegen.runtime.traces;

public class StatementTraceNode extends TraceNode
{
	private CallStatement statement;

	public StatementTraceNode(CallStatement statement)
	{
		this.statement = statement;
	}

	@Override
	public String toString()
	{
		return statement.toString();
	}

	@Override
	public TestSequence getTests()
	{
		TestSequence tests = new TestSequence();
		CallSequence seq = getVars();
		seq.add(statement);
		tests.add(seq);

		return tests;
	}
}
