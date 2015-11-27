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

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.runtime.Utils;

public abstract class TraceNode
{
	private static final int ENCLOSING_MODULE_ID = -1;

	@Override
	abstract public String toString();

	abstract public TestSequence getTests();

	// SL
	public static void executeTests(TraceNode trace, TestAccumulator acc, Store store)
	{
		executeTests(trace, null, acc, store);

	}
	
	// PP
	public static void executeTests(TraceNode trace, Class<?> instanceType,
			TestAccumulator acc, Store store)
	{
		try
		{
			if(instanceType != null)
			{
				store.register(ENCLOSING_MODULE_ID, instanceType.newInstance());
			}
			
			int testNo = 1;

			TestSequence tests = trace.getTests();

			tests.size();

			for (CallSequence test : tests)
			{
				List<String> callStms = new LinkedList<String>();
				List<Object> callStmResults = new LinkedList<Object>();

				boolean failureOccured = false;

				/*
				 * TODO: Type check missing here If the type check fails we would also have to do filtering
				 */

				if (test.getFilter() > 0)
				{
					acc.registerTest(new TraceTest(testNo, test.toString(), "", Verdict.SKIPPED));
				} else
				{
					int callStmIdx = 0;
					for (; callStmIdx < test.size(); callStmIdx++)
					{
						CallStatementSl callStm = test.get(callStmIdx);
						try
						{
							callStms.add(callStm.toString());
							
							if(callStm instanceof CallStatementPp)
							{
								((CallStatementPp) callStm).setInstance(store.getValue(ENCLOSING_MODULE_ID));
							}
							
							Object result = callStm.execute();
							callStmResults.add(result);

						} catch (RuntimeException e)
						{
							for (int p = callStmIdx + 1; p < test.size(); p++)
							{
								CallStatementSl notCalled = test.get(callStmIdx);
								callStms.add(notCalled.toString());
							}

							boolean preCondViolation = e.getMessage() != null
									&& e.getMessage().contains("Precondition failure");

							for (; callStmIdx < test.size(); callStmIdx++)
							{
								if (e.getMessage() != null)
								{
									callStmResults.add(e.getClass().getSimpleName()
											+ ": " + e.getMessage());
								} else
								{
									callStmResults.add(e.getClass().getSimpleName());// Happens for null pointer
																						// exceptions
								}
							}

							Verdict verdict = preCondViolation ? Verdict.INCONCLUSIVE
									: Verdict.FAILED;

							// Filter
							callStmResults.add(verdict);
							tests.filter(callStmResults, test, testNo);

							failureOccured = true;

							acc.registerTest(new TraceTest(testNo, getResultStr(callStms, "; "), getResultStr(callStmResults.subList(0, callStmResults.size() - 1), " ; "), verdict));
						}

					}

					if (!failureOccured)
					{
						// Filter
						// TODO: but filtering a passed test should be useless)
						callStmResults.add(Verdict.PASSED);
						tests.filter(callStmResults, test, testNo);

						acc.registerTest(new TraceTest(testNo, getResultStr(callStms, "; "), getResultStr(callStmResults.subList(0, callStmResults.size() - 1), " ; "), Verdict.PASSED));
					}
				}

				callStms.clear();
				callStmResults.clear();

				testNo++;
				store.reset();
			}

		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static String getResultStr(List<? extends Object> results,
			String sep)
	{
		StringBuilder sb = new StringBuilder();

		if (!results.isEmpty())
		{
			sb.append(toStr(results.get(0)));
		}

		for (int i = 1; i < results.size(); i++)
		{
			sb.append(sep + toStr(results.get(i)));
		}

		return sb.toString();
	}

	public static void showResults(List<TraceTest> results)
	{
		for (TraceTest testResult : results)
		{
			System.out.println(testResult.toString());
		}
	}

	private static String toStr(Object obj)
	{
		if (obj instanceof String)
		{
			return obj.toString();
		} else
		{
			return Utils.toString(obj);
		}
	}
}
