package org.overture.codegen.ooast;

import org.overture.codegen.analysis.vdm.AbstractAnalysis;
import org.overture.codegen.analysis.vdm.QuoteAnalysis;
import org.overture.codegen.analysis.vdm.UtilAnalysis;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class OoAstAnalysis
{	
	public static boolean usesQuoteLiterals(AClassDeclCG classDecl)
	{
		return hasDependency(classDecl, new QuoteAnalysis());
	}

	public static boolean usesUtils(AClassDeclCG classDecl)
	{
		return hasDependency(classDecl, new UtilAnalysis());
	}
	
	private static boolean hasDependency(AClassDeclCG classDecl, AbstractAnalysis analysis)
	{
		try
		{
			classDecl.apply(analysis);
		}catch(AnalysisException e)
		{
			//If we find what we are looking for an exception will
			//be thrown to terminate the visitor analysis
		}
		
		return analysis.isFound();
	}
}
