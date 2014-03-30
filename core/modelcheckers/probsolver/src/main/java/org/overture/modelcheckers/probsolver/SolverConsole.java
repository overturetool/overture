package org.overture.modelcheckers.probsolver;

import java.io.PrintWriter;

public class SolverConsole
{
	public final PrintWriter out;
	public final PrintWriter err;

	public SolverConsole(PrintWriter out, PrintWriter err)
	{
		this.out = out;
		this.err = err;
	}

	public SolverConsole()
	{
		this(new PrintWriter(System.out, true), new PrintWriter(System.err, true));
	}
}