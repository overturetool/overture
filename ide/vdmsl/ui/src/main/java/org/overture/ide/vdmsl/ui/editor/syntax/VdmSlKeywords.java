package org.overture.ide.vdmsl.ui.editor.syntax;

import org.overture.ide.ui.editor.syntax.IVdmKeywords;

public class VdmSlKeywords implements IVdmKeywords {

	public static final String[] reservedwords = { "abs", "all", "always",
			"and", "as", "be", "bool", "by", "card", "cases", "char", "comp",
			"compose", "conc", "dcl", "def", "definitions", "dinter", "div",
			"dlmodule", "do", "dom", "dunion", "elems", "else", "elseif",
			"end", "error", "errs", "exists", "exists1", "exit", "exports",
			"ext", "false", "floor", "for", "forall", "from", "functions",
			"hd", "if", "imports", "in", "inds", "init", "inmap", "int",
			"inter", "inv", "inverse", "iota", "lambda", "len", "let", "map",
			"measure", "merge", "mod", "module", "mu", "munion", "nat", "nat1",
			"nil", "not", "of", "operations", "or", "others", "post", "power",
			"pre", "psubset", "rat", "rd", "real", "rem", "renamed", "return",
			"reverse", "rng", "seq", "seq1", "set", "skip", "specified", "st",
			"state", "struct", "subset", "then", "tixe", "tl", "to", "token",
			"trap", "traces", "true", "types", "undefined", "union", "uselib",
			"values", "while", "with", "wr", "yet", "RESULT" };

	public static final String[] multipleKeywords = { "is not yet specified",
			"for all", "in set", "be st", "not in set" };

	public static final String[] binaryoperators = { "comp", "and", "or",
			"in set", "not in set", "union", "inter", "munion", "subset",
			"psubset", "=>", "<=>", "=", "<>", "+", "-", "*", "/", "div",
			"mod", "rem", "<", ">", "<=", ">=", "<>", "\\", "++", "<:", "<-:",
			":>", ":->", ".#", "**" };

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

	public String[] getAllKeywords() {
		int offset = 0;
		String[] all = new String[basictypes.length 
		                          + binaryoperators.length 
		                          + multipleKeywords.length 
		                          + reservedwords.length 
		                          + textvalues.length 
		                          + unaryoperators.length];
		
		System.arraycopy(basictypes, 0, all, offset, basictypes.length);
		offset = offset + basictypes.length;
		
		System.arraycopy(binaryoperators, 0, all, offset, binaryoperators.length);
		offset = offset + binaryoperators.length;
		
		System.arraycopy(multipleKeywords, 0, all, offset, multipleKeywords.length);
		offset = offset + multipleKeywords.length;
		
		System.arraycopy(reservedwords, 0, all, offset, reservedwords.length);
		offset = offset + reservedwords.length;
		
		System.arraycopy(textvalues, 0, all, offset, textvalues.length);
		offset = offset + textvalues.length;
		
		System.arraycopy(unaryoperators, 0, all, offset, unaryoperators.length);
		offset = offset + binaryoperators.length;
		
		return all;
	}

}
