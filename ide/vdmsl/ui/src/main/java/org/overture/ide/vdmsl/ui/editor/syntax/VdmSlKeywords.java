/*
 * #%~
 * org.overture.ide.vdmsl.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.vdmsl.ui.editor.syntax;

import org.overture.ide.ui.editor.syntax.IVdmKeywords;

public class VdmSlKeywords implements IVdmKeywords {

	public static final String[] reservedwords = { "abs", "all", "always",
			"and", "as", "atomic","be", "bool", "by", "card", "cases", "char", "comp",
			"compose", "conc", "dcl", "def", "definitions", "dinter", "div",
			"dlmodule", "do", "dom", "dunion", "elems", "else", "elseif",
			"end", "error", "errs", "exists", "exists1", "exit", "exports",
			"ext", "false", "floor", "for", "forall", "from", "functions",
			"hd", "if", "imports", "in", "inds", "init", "inmap", "int",
			"inter", "inv", "inverse", "iota", "is","lambda", "len", "let", "map",
			"measure", "merge", "mod", "module", "mu", "munion", "nat", "nat1",
			"nil", "not", "of", "operations", "or", "others", "post", "power",
			"pre", "psubset", "pure", "rat", "rd", "real", "rem", "renamed", "return",
			"reverse", "rng", "seq", "seq1", "set", "skip", "specified", "st",
			"state", "struct", "subset", "then", "tixe", "tl", "to", "token",
			"trap", "traces", "true", "types", "undefined", "union", "uselib",
			"values", "while", "with", "wr", "yet", "RESULT" };

	/*
	 * It does not make sense to distinguish between keywords like this since
	 * words like 'specified' are already reserved. This means that:
	 * specified is not a valid identifier
	 */
	public static final String[] multipleKeywords = { "is not yet specified",
			"for all", "in set", "be st", "not in set" };

	public static final String[] binaryoperators = { "comp", "and", "or",
			"in set", "not in set", "union", "inter", "munion", "subset",
			"psubset", "div", "mod", "rem"};//, "=>", "<=>", "=", "<>", "+", "-", "*", "/",
			// "<", ">", "<=", ">=", "<>", "\\", "++", "<:", "<-:",
			//":>", ":->",  "**" }; //handled in a different way ".#",
	
	public static final String[] binarySymbolOperators = { "=>", "<=>", "=", "<>", "+", "-", "*", "/",
		 "<", ">", "<=", ">=", "<>", "\\", "++", "<:", "<-:",
		":>", ":->",  "**" }; //handled in a different way ".#",

	public static final String[] basictypes = { "bool", "int", "nat", "nat1",
			"real", "rat", "char", "token" };

	public static final String[] textvalues = { "true", "false", "nil" };

	public static final String[] unaryoperators = { "abs", "card", "floor",
			"hd", "tl", "len", "elems", "inds", "conc", "dom", "rng", "merge",
			"not", "inverse", "dunion", "dinter", "power" };

	public String[] getBasictypes() {
		return basictypes;
	}

	public String[] getBinaryoperators() {
		return binaryoperators;
	}

	public String[] getMultipleKeywords() {
		return multipleKeywords;
	}

	public String[] getReservedwords() {
		return reservedwords;
	}

	public String[] getTextvalues() {
		return textvalues;
	}

	public String[] getUnaryoperators() {
		return unaryoperators;
	}

	public String[] getHistoryCounters() {
		return new String[0];
	}

	public String[] getAllSingleWordKeywords() {
		int offset = 0;
		String[] all = new String[basictypes.length 
		                          + binaryoperators.length 
		                         // + multipleKeywords.length 
		                          + reservedwords.length 
		                          + textvalues.length 
		                          + unaryoperators.length];
		
		System.arraycopy(basictypes, 0, all, offset, basictypes.length);
		offset = offset + basictypes.length;
		
		System.arraycopy(binaryoperators, 0, all, offset, binaryoperators.length);
		offset = offset + binaryoperators.length;
		
//		System.arraycopy(multipleKeywords, 0, all, offset, multipleKeywords.length);
//		offset = offset + multipleKeywords.length;
		
		System.arraycopy(reservedwords, 0, all, offset, reservedwords.length);
		offset = offset + reservedwords.length;
		
		System.arraycopy(textvalues, 0, all, offset, textvalues.length);
		offset = offset + textvalues.length;
		
		System.arraycopy(unaryoperators, 0, all, offset, unaryoperators.length);
		offset = offset + binaryoperators.length;
		
		return all;
	}

	public String[] getBinarySymbolOperators()
	{
		return binarySymbolOperators;
	}

	public String[] getUnderscorePrefixKeywords()
	{
		return new String[]{"is","mk"};
	}

	public String[] getUnderscorePrefixReservedWords()
	{
		return new String[]{"init","inv", "pre", "post"};
	}

	public boolean supportsQuoteTypes()
	{
		return true;
	}

	public boolean supportsTypleSelect()
	{
		return true;
	}

}
