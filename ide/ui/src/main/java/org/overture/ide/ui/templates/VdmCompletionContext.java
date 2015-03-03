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

	/**
	 * Index where replacement must start. For example, if the
	 * rawScan is '<q' then the offset is -2 since the complete text '<q' should be replaced
	 */
	public int offset;
	public SearchType type = SearchType.Types;

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
		System.out.println("Computed completion context: " + toString());
	}

	private void calcSearchType()
	{
		int index = rawScan.toString().lastIndexOf("<");

		if (index != -1)
		{
			// Completion of quote, e.g. <Green>
			consQuoteContext(index);
			return;
		}

		index = rawScan.toString().indexOf("new");

		// New must appear at the first index of the raw scan
		if (index == 0)
		{
			// Completion of constructors
			consConstructorCallContext();
			return;
		}
	}

	/**
	 * Constructs the completion context for a constructor call
	 * 
	 * @param index
	 */
	private void consConstructorCallContext()
	{
		// The processed scan contains what
		final int NEW_LENGTH = "new".length();

		// This gives us everything after new, e.g. ' MyClass' if you type 'new MyClass'
		CharSequence subSeq = rawScan.subSequence(NEW_LENGTH, rawScan.length());

		processedScan = new StringBuffer(subSeq);
		proposalPrefix = processedScan.toString().trim();

		for (int i = NEW_LENGTH; i < rawScan.length(); i++)
		{
			if (Character.isJavaIdentifierStart(rawScan.charAt(i)))
			{
				offset = -(rawScan.length() - i);
				break;
			}
		}

		type = SearchType.New;
	}

/**
	 * Constructs the completion context for quotes
	 * 
	 * @param index The index of the '<' character
	 */
	private void consQuoteContext(int index)
	{
		processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
		proposalPrefix = processedScan.toString();
		offset = -(rawScan.length() - index);
		type = SearchType.Quote;
	}

	private String getQualifiedSource()
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

	@Override
	public String toString()
	{
		return type + " - Root: '" + getQualifiedSource() + "' Proposal: '"
				+ proposalPrefix + "'" + " offset: " + offset;
	}
}