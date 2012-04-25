package org.overturetool.scanner.imp;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;
import org.overturetool.ast.imp.OmlLexem;

public class TestScanner {

	// simple application to test the scanner from a file
	// uncomment debug line in scanner.l for more detailed information
	public static void main(String[] args) {
		try {
			OvertureScanner scanner = new OvertureScanner(new java.io.FileReader("test.txt"));
			scanner.setLexems(new Vector());
			OmlLexem token = null;
			while ((token = scanner.getNextToken()) != null) {
				System.out.println(token);
			}
			System.out.println("<<EOF>>");
		}
		catch (java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		
		try {
			OvertureScanner anotherScanner = new OvertureScanner(" operations ");
			assert( anotherScanner.getNextToken().getLexval() == OvertureScanner.LEX_OPERATIONS);
		}
		catch (CGException cge) {
			cge.printStackTrace();
		}
		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
