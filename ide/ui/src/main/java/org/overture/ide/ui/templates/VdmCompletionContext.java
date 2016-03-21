package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

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