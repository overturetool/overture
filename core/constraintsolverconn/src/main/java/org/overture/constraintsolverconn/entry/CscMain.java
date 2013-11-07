package org.overture.constraintsolverconn.entry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

public class CscMain
{

	public static void main(String[] args) throws AnalysisException
	{

		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10; // clearing to display "VDM classic"

		Csc csc = new Csc();
		String result;

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/java/org/overture/constraintsolverconn/visitor/Sample.vpp".replace('/', '\\')), "UTF-8"));
			String line;

			while ((line = br.readLine()) != null)
			{
				if (!line.substring(0, 2).equals("--"))
				{
					result = csc.visitExp(line);
					System.out.println("Result is: " + result);
				}
			}
			br.close();
		} catch (IOException e)
		{
			System.out.println("IOException: " + e);
		}
		// Csc csc = new Csc();
		// String result;
		// String result = csc.visitExp("( 1 + ( 2  - 3 ) * 4 ) / 5");
		// String result = csc.visitExp("- (1 ** (5 mod 3) + true)");

		// System.out.println("Result is: " + result);

		// result = csc.visitExp("1+2+3");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1} \\ {2,3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("dom {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("rng {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1|->2, 3|->3} = {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("{1|->2, 3|->3} <> {1|->2, 3|->3}");
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("reverse [1,2,3]");
		// unable to parse expression
		// reverse not available in VDM classic
		// System.out.println("Result is: " + result);

		// result = csc.visitExp("len [1,2,3] ^ [2,4]");
		// unable to type check expression
		// System.out.println("Result is: " + result);

		/*
		 * result = csc.visitExp("{}"); System.out.println("Result is: " + result); result = csc.visitExp("{1, 2, 3}");
		 * System.out.println("Result is: " + result); result = csc.visitExp("{x | x in set nat1 & x <= 3}");
		 * System.out.println("Result is: " + result);
		 */

		System.out.println("... Done.");
	}

}