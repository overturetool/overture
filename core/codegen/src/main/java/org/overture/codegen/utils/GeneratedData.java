package org.overture.codegen.utils;

import java.util.ArrayList;
import java.util.List;

public class GeneratedData
{
	private List<GeneratedModule> classes;
	
	private GeneratedModule quoteValues;

	public GeneratedData()
	{
		super();
		this.classes = new ArrayList<GeneratedModule>();
		this.quoteValues = null;
	}
	
	public void addClass(GeneratedModule classCg)
	{
		this.classes.add(classCg);
	}

	public List<GeneratedModule> getClasses()
	{
		return classes;
	}
	
	public void setQuoteValues(GeneratedModule quoteValues)
	{
		this.quoteValues = quoteValues;
	}

	public GeneratedModule getQuotes()
	{
		return quoteValues;
	}
}
