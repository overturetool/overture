package org.overture.codegen.utils;

import java.util.List;

import org.overture.codegen.analysis.violations.InvalidNamesResult;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private GeneratedModule quoteValues;
	private InvalidNamesResult invalidNamesResult;

	public GeneratedData(List<GeneratedModule> classes,
			GeneratedModule quoteValues, InvalidNamesResult invalidNamesResult)
	{
		super();
		this.classes = classes;
		this.quoteValues = quoteValues;
		this.invalidNamesResult = invalidNamesResult;
	}
	
	public List<GeneratedModule> getClasses()
	{
		return classes;
	}
	
	public GeneratedModule getQuoteValues()
	{
		return quoteValues;
	}

	public InvalidNamesResult getInvalidNamesResult()
	{
		return invalidNamesResult;
	}
}
