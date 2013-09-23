package org.overture.constraintsolverconn.entry;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;

public class CscMain {

	public static void main(String[] args) throws AnalysisException {
		
		Settings.dialect = Dialect.VDM_RT;
		
		Csc csc = new Csc();
		
		String result = csc.visitExp("1 + 2");
		
		System.out.println("Result is: " + result);		
	}
	
}
