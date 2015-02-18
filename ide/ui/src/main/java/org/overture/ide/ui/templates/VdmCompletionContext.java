package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;

public class VdmCompletionContext
{
	// boolean isEmpty = false;
	// // SearchType type = SearchType.Proposal;
	// StringBuffer proposal = new StringBuffer();
	// StringBuffer field = new StringBuffer();
	// StringBuffer fieldType = new StringBuffer();
	// boolean afterNew = false;
	// boolean afterMk = false;
	// public StringBuffer prefix = new StringBuffer();

	// new
	public StringBuffer rawScan;
	public StringBuffer processedScan;
	public int offset;
	SearchType type = SearchType.Types;

	public String proposalPrefix;

	public List<String> root;

	public VdmCompletionContext(StringBuffer rawScan)
	{
		this.rawScan = rawScan;
		init();
	}

	private void init()
	{
		calcSearchType();

	}

	private void calcSearchType()
	{
		int index = rawScan.toString().lastIndexOf("<");

		if (index !=-1)
		{
			// quote
			processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
			proposalPrefix = processedScan.toString();
			offset = index;
			type = SearchType.Quote;
		}
	}

	@Override
	public String toString()
	{
		return type + " - Root: " + getQualifiedSource() + " Proposal: "
				+ proposalPrefix;
	}

	String getQualifiedSource()
	{
		String res = "";
		if (root != null && !root.isEmpty())
		{
			for (Iterator<String> itr = root.iterator(); itr.hasNext();)
			{
				res += itr.next();
				if (itr.hasNext())
					res += ".";
			}
		}
		return res;
	}

	// @Override
	// public String toString()
	// {
	// return "Type: \"" + fieldType + "\" " + (afterMk ? "mk_" : "")
	// + (afterNew ? "new " : "") + "\""
	// + (field.length() != 0 ? field + "." : "") + proposal + "\"";
	// }
}