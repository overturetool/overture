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

package org.overturetool.vdmj.traces;

import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.statements.CallObjectStatement;

public class StatementTraceNode extends TraceNode
{
	public final CallObjectStatement statement;
	public final int hash;
	public final boolean copyCtxt;
	public final Context ctxt;

	public StatementTraceNode(
		CallObjectStatement statement, int hash, boolean copyCtxt, Context ctxt)
	{
		this.ctxt = ctxt;
		this.hash = hash;
		this.copyCtxt = copyCtxt;
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
		CallSequence seq = new CallSequence();
		seq.add(statement);
		seq.setContext(ctxt, copyCtxt);
		seq.addHash(hash);
		tests.add(seq);
		return tests;
	}
}
