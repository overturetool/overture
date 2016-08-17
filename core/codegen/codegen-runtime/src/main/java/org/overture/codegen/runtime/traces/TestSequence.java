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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("serial")
public class TestSequence extends Vector<CallSequence>
{
	private class TestData
	{
		public final int index;
		public final int stem;
		public final CallSequence test;

		public TestData(CallSequence test, int stem, int index)
		{
			this.test = test;
			this.stem = stem;
			this.index = index;
		}
	}

	final List<TestData> failedCallSeqs = new Vector<TestData>();

	/**
	 * Filter remaining tests based on one set of results. The result list passed in is the result of running the test,
	 * which is the n'th test in the TestSequence (from 1 - size).
	 * 
	 * @param result
	 * @param test
	 * @param n
	 */
	public void filter(List<Object> result, CallSequence test, int n)
	{
		if (result.get(result.size() - 1) != Verdict.PASSED)
		{
			int stem = result.size() - 1;
			failedCallSeqs.add(new TestData(test, stem, n));
		}
	}

	@Override
	public synchronized Iterator<CallSequence> iterator()
	{
		return new Iterator<CallSequence>()
		{
			Iterator<CallSequence> itr = TestSequence.this.iterator();

			@Override
			public boolean hasNext()
			{
				return itr.hasNext();
			}

			@Override
			public CallSequence next()
			{
				CallSequence other = itr.next();

				markFiltered(other);

				return other;
			}

			@Override
			public void remove()
			{
				// Not supported
			}
		};
	}

	/**
	 * Mark a call sequence filtered if a call sequence failed that is the prefix of this one
	 * 
	 * @param other
	 */
	protected void markFiltered(CallSequence other)
	{
		for (TestData testData : TestSequence.this.failedCallSeqs)
		{
			if (other.compareStem(testData.test, testData.stem))
			{
				other.setFilter(testData.index);
				return;
			}
		}
	}

	@Override
	public String toString()
	{
		String before = "";
		String separator = "";
		String after = "";

		StringBuilder sb = new StringBuilder();
		sb.append(before);

		if (!this.isEmpty())
		{
			sb.append(this.get(0).toString());

			for (int i = 1; i < this.size(); i++)
			{
				sb.append(separator);
				sb.append(this.get(i).toString());
			}
		}

		sb.append(after);
		return sb.toString();
	}
}