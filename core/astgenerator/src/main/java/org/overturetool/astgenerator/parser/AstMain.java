package org.overturetool.astgenerator.parser;

import org.overturetool.astgenerator.codegenVisitor;
import org.overturetool.astgenerator.wfCheckVisitor;

public class AstMain {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("please supply a file name!");
			return;
		}
		try {
			// create the parser
			AstParser ast = new AstParser(args[0]);
			
			// parse the input file
			System.out.println("Start parse");
			ast.yyparse();
			System.out.println("Parsing finished");
	
			if (ast.errors == 0) {
				
				// print the abstract syntax
				// AsciiVisitor aVisit = new AsciiVisitor();
				// ast.theAst.accept(aVisit);
				// System.out.println(UTIL.ConvertToString(aVisit.res));

				// check the well-formedness
				wfCheckVisitor wVisit = new wfCheckVisitor();
				ast.theAst.accept(wVisit);
			
				if (wfCheckVisitor.errors.intValue() == 0) {
						
					// call the code generator
					codegenVisitor cgVisit = new codegenVisitor();
					cgVisit.setLong(); // generate Long in stead of Integer
					ast.theAst.accept(cgVisit);
				}
			}
			
			System.out.println ("Done");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	
}
