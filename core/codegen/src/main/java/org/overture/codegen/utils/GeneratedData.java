package org.overture.codegen.utils;

import java.util.List;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	private GeneratedModule quoteValues;

	public GeneratedData(List<GeneratedModule> classes,
			GeneratedModule quoteValues)
	{
		super();
		this.classes = classes;
		this.quoteValues = quoteValues;
	}
	
	public List<GeneratedModule> getClasses()
	{
		return classes;
	}
	
	public GeneratedModule getQuoteValues()
	{
		return quoteValues;
	}
}
