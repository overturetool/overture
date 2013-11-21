package org.overture.codegen.analysis;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class OoAstAnalysis
{

	public static boolean usesSets(AClassDeclCG classDecl)
	{
		SetAnalysis setAnalysis = new SetAnalysis();
		
		try
		{
			classDecl.apply(setAnalysis);
		}catch(AnalysisException e)
		{
			//If found an exception will be thrown to terminate
			//the visitor analysis
		}
		
		return setAnalysis.isFound();
	}
	
	public static boolean usesSequences(AClassDeclCG classDecl)
	{
		SequenceAnalysis seqAnalysis = new SequenceAnalysis();
		
		try
		{
			classDecl.apply(seqAnalysis);
		}catch(AnalysisException e)
		{
			//If found an exception will be thrown to terminate
			//the visitor analysis
		}
		
		return seqAnalysis.isFound();
	}
	
	public static boolean usesQuoteLiterals(AClassDeclCG classDecl)
	{
		QuoteAnalysis quoteAnalysis = new QuoteAnalysis();
		
		try
		{
			classDecl.apply(quoteAnalysis);
		}catch(AnalysisException e)
		{
			//If found an exception will be thrown to terminate
			//the visitor analysis
		}
		
		return quoteAnalysis.isFound();
	}
	
	public static boolean usesTuples(AClassDeclCG classDecl)
	{
		TupleAnalysis tupleAnalysis = new TupleAnalysis();
		
		try
		{
			classDecl.apply(tupleAnalysis);
		}catch(AnalysisException e)
		{
			//If found an exception will be thrown to terminate
			//the visitor analysis
		}
		
		return tupleAnalysis.isFound();
	}
}
