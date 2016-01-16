package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
	private StringBuffer rawScan;
	private StringBuffer processedScan;
	public int offset;
	SearchType type = SearchType.Types;

	public String proposalPrefix = "";

	public List<String> root = new Vector<String>();

	public VdmCompletionContext(StringBuffer rawScan)
	{
		this.rawScan = rawScan;
		init();
	}

	private void init()
	{
		calcSearchType();

		System.out.println("Computed completion context: "+toString());
	}

	private void calcSearchType()
	{
		int index = rawScan.toString().lastIndexOf("<");

		if (index != -1)
		{
			// quote
			processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
			proposalPrefix = processedScan.toString();
			offset = -(rawScan.length() - index);
			type = SearchType.Quote;
			return;
		}

		index = rawScan.toString().indexOf("new");

		if (index == 0)
		{
			// quote
			processedScan = new StringBuffer(rawScan.subSequence(index
					+ "new".length(), rawScan.length()));
			proposalPrefix = processedScan.toString().trim();

			for (int i = index + "new".length(); i < rawScan.length(); i++)
			{
				if (Character.isJavaIdentifierStart(rawScan.charAt(i)))
				{
					offset = -(rawScan.length() - i);
					break;
				}
			}

			type = SearchType.New;
			return;
		}
		//Default
		index = 0;
		processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
		proposalPrefix = processedScan.toString().trim();
		offset = -proposalPrefix.length();
		
		return;
		
	}

	@Override
	public String toString()
	{
		return type + " - Root: '" + getQualifiedSource() + "' Proposal: '"
				+ proposalPrefix+"'" +" offset: "+offset;
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