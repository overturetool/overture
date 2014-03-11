package org.overture.modelcheckers.probsolver;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;

public class UnsupportedTranslationException extends AnalysisException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedTranslationException(Set<String> unsupportedConstructs)
	{
		super("Reached unsupported construct(s) in translation: "
				+ unsupportedConstructs);
	}
	
	public UnsupportedTranslationException(String message,Throwable e)
	{
		super(message,e);
	}

}
