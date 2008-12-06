package org.overturetool.jml.parser;

import java.io.PrintWriter;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		BatchCompiler comp = new BatchCompiler();
		
		comp.compile("/Users/vilhena/Desktop/Expert.java -d /Users/vilhena/Desktop", new PrintWriter(System.out), new PrintWriter(System.err), null);

	}

}
