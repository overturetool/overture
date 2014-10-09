/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.junit.Assume;

public class ProbCliSolver
{
	public static boolean solve(String exp)
	{
		return solve(exp, true);
	}

	public static boolean solve(String exp, boolean verbose)
	{
		try
		{
			if (System.getenv("PROBCLI") == null)
			{
				Assume.assumeTrue("No prob cli avaliable", false);
				return false;
			}

			File probcli = new File(System.getenv("PROBCLI"), "probcli");

			ProcessBuilder pb = new ProcessBuilder(probcli.getAbsolutePath(), "-p", "BOOL_AS_PREDICATE", "TRUE", "-p", "CLPFD", "TRUE", "-p", "MAXINT", "127", "-p", "MININT", "-128", "-p", "TIME_OUT", "500", "-eval", "\""
					+ exp + "\"");
			pb.redirectErrorStream(true);
			Process p = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = input.readLine()) != null)
			{
				sb.append("\n" + line);
			}

			final String RESULT_TOKENS = "Evaluation results:";
			final String SOLUTION_TOKENS = "Solution:";

			String result = "";
			String solution = "";

			if (sb.toString().contains(RESULT_TOKENS))
			{
				int resultIndex = sb.toString().indexOf(RESULT_TOKENS);
				result = sb.toString().substring(resultIndex
						+ RESULT_TOKENS.length());
				if (result.indexOf("\n") > 0)
				{
					result = result.substring(result.indexOf("\n")).trim();
				}
				result = result.trim();
				result = result.substring(1, result.length() - 3);

				if (sb.toString().contains(SOLUTION_TOKENS))
				{
					int beginIndexSolution = sb.toString().indexOf(SOLUTION_TOKENS)
							+ SOLUTION_TOKENS.length();
					solution = sb.toString().substring(beginIndexSolution, resultIndex);

					solution = solution.replaceAll("\\s+", " ").replace('\n', '\t');

					solution = "\n\t" + solution;
					solution = solution.replace("&", "&\n\t");
					solution += "\n\t";
				}
			}

			// System.out.println("Got this from the solver: " + result +" Solution: "+solution);
			if (verbose)
			{

				System.out.println(padRight("Solution: " + solution, 40)
						+ padRight(" Result: " + result, 30) + " \n\tInput: "
						+ padRight(exp, 40));
			}

			if (result.contains("ERROR"))
			{
				// Assert.fail("Syntax/Type reported by solver input: " + exp);
				return false;
			}
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	public static String padRight(String text, int count)
	{
		while (text.length() < count)
		{
			text += " ";
		}
		return text;
	}
}
