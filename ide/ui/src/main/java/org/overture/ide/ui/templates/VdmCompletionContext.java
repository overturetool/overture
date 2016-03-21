package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class VdmCompletionContext
{
	private StringBuffer rawScan;
	private StringBuffer processedScan;

/**
	 * Index where replacement must start. For example, if the
	 * rawScan is '<q' then the offset is -2 since the complete text '<q' should be replaced
	 */
	private SearchType type = SearchType.Types;

	private String proposalPrefix = "";

	private List<String> root = new Vector<String>();

	public VdmCompletionContext(StringBuffer rawScan)
	{
		this.rawScan = rawScan;
		init();
	}

	private void init()
	{
		int index = rawScan.toString().lastIndexOf("<");

		if (index != -1)
		{
			// Completion of quote, e.g. <Green>
			consQuoteContext(index);
			return;
		}

		index = rawScan.toString().indexOf("new");

		// 'new' must appear at the first index of the raw scan
		if (index == 0)
		{
			// Completion of constructors
			consConstructorCallContext();
			return;
		}
		
		index = rawScan.toString().indexOf("mk_");
		
		// 'mk_' must appear at the first index of the raw scan
		if(index == 0)
		{
			consMkContext();
			return;
		}

		// Completion for foo.bar. This covers things such as instance variables,
		// values, a record, a tuple, operations and functions

		String[] split = rawScan.toString().split("\\.");
		if (split.length == 2)
		{ // only works for one . atm
			consDotContext(split);
			return;
		}
		//Default
		index = 0;
		processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
		proposalPrefix = processedScan.toString();
		if( proposalPrefix == null || proposalPrefix.isEmpty()){
			offset = -proposalPrefix.length();
			return;
		}
		
		char charMatch = proposalPrefix.charAt(proposalPrefix.length() - 1); //checks the end of the string
		if( charMatch == ' ' || charMatch == '\r' || charMatch == '\n' || charMatch == '\t' )
		{
			proposalPrefix = "";
		}
		else{
			String[] sep_list = { " ", "\n", "\r", "\t", "."};
			StringBuffer regexp = new StringBuffer("");
			regexp.append("[");
			for(String s : sep_list) {
			    regexp.append("[");
			    regexp.append(Pattern.quote(s));
			    regexp.append("]");
			}
			regexp.append("]");
			String[] bits = proposalPrefix.split(regexp.toString());
			proposalPrefix = bits[bits.length-1].trim();
		}
		
		offset = -proposalPrefix.length();
		
		char[] arr = proposalPrefix.trim().toCharArray(); 
		if(arr.length > 0 && arr[arr.length-1] == '('){
			type = SearchType.CallParam;
		}
		
		return;
	} 

	private void consDotContext(String[] split)
	{
		this.type = SearchType.Dot;
		this.proposalPrefix = split[1];

		this.root = new Vector<>();
		root.add(split[0]);
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

		type = SearchType.New;
	}
	
	/**
	 * Constructs the completion context for a 'mk_' call
	 * 
	 */
	private void consMkContext() {
		
		final int MK_LENGTH = "mk_".length();
		
		CharSequence subSeq = rawScan.subSequence(MK_LENGTH, rawScan.length());
		processedScan = new StringBuffer(subSeq);
		proposalPrefix = processedScan.toString().trim();
		
		type = SearchType.Mk;
	}
	
	/**
	 * Contrusts the completion context for the 'mk_token' call
	 */
//	private void consMK_tokenContext() {
//		final int MK_LENGTH = "mk_t".length();
//		
//		CharSequence subSeq = rawScan.subSequence(MK_LENGTH, rawScan.length());
//		processedScan = new StringBuffer(subSeq);
//		proposalPrefix = processedScan.toString().trim();
//		
//		type = SearchType.Mk;
//		
//	}

/**
	 * Constructs the completion context for quotes
	 * 
	 * @param index The index of the '<' character
	 */
	private void consQuoteContext(int index)
	{
		processedScan = new StringBuffer(rawScan.subSequence(index, rawScan.length()));
		proposalPrefix = processedScan.toString();
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

	public int getReplacementOffset()
	{
		return -proposalPrefix.length();
	}
	
	public SearchType getType()
	{
		return type;
	}

	public String getProposalPrefix()
	{
		return proposalPrefix;
	}

	public List<String> getRoot()
	{
		return root;
	}

	@Override
	public String toString()
	{
		return type + " - Root: '" + getQualifiedSource() + "' Proposal: '"
				+ proposalPrefix + "'" + " offset: ";
	}
}