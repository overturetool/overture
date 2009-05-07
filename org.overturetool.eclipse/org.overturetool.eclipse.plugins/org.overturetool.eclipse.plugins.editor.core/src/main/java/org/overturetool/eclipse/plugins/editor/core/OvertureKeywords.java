package org.overturetool.eclipse.plugins.editor.core;

public class OvertureKeywords {
	  private static final String[] reservedwords = {
		  "if", "then", "else", "elseif",
		  "let", "def", 
		  "cases", "others",
		  "pre", "post", "inv", 
		  "compose",
		  "forall", "exists", "exists1", "iota",
		  "lambda",
		  "undefined", "skip",
		  "dcl",
		  "ext", "rd", "wr", "errs",
		  "while", "do", "by", "reverse",
		  "return",
		  "always", "trap", "with", "tixe", "exit",
		  "error",
		  "static", "public", "private", "protected", "new",
		  "sync", "per", "thread", "periodic", "threadid",
		  "self",
		  "atomic",
		  "bool", "int", "nat", "nat1", "real", "rat", "char", "token",
		  "true", "false", "nil",
		  "mu","not", "and", "or",
		  "abs", "floor", "div", "mod", "rem", "union", "inter", "subset",
		  "psubset", "card", "dunion", "dinter", "power",
		  "hd", "tl", "len", "elems", "inds", "conc", "dom", "rng", "munion",
		  "merge", "comp", "inverse", "isofbaseclass", "isofclass", 
		  "samebaseclass", "sameclass", "start", "startlist", "class", "end",
		  "types", "functions", "operations", "values", "from", "mk_",
		  "post", "pre", "inv", "init", "is",
		  "for", "all", "match", "mutex", "to", "in", "of",
		  "variables", "instance", "measure", "traces", 
		  //TODO multiple hack.. :D
		  "subclass", "specified", "yet", "all", "instance", 
		  "variables", "set", "responsibility", "be", "st", "seq"
		  };
	  //TODO init VDM-SL
	  
	  
	  private static final String[] multipleKeywords = {
		  "is subclass of", "is not yet specified", "for all", "instance variables",
		  "in set", "is subclass responsibility", "be st"
	  };
	  
	  private static String[] historyCounters = {
		  "#act", "#fin", "#active", "#req", "#waiting"
	  };

	  private static final String[] predicates = {
	      "mu","not", "and", "or",
		  "union", "inter", "subset",
		  "psubset", "card", "dunion", "dinter", "power", "forall", "exists", "exists1", "iota"    
	  };
	  
	  private static final String[] types = {"bool", "int", "nat", "nat1", "real", "rat", "char", "token", "map", "to","inmap"};

	  private static final String[] constants = {"true", "false", "nil"};
	 
	  
	  private static final String[] functions = { "abs", "floor", "div", "mod", "rem", 	  
		  "hd", "tl", "len", "elems", "inds", "conc", "dom", "rng", "munion",
		  "merge", "comp", "inverse", "isofbaseclass", "isofclass", 
		  "samebaseclass", "sameclass", "start", "startlist"};
	  
	  private static final String[] operators = {"\\.",":","::","->","+>","==","=>","<=>","=","<>","+",
		  	"-","*","/","*\\*","<",">","<=",">=","&","|","\\^",
		  	"\\\\","++","|->","<:","<-:",":>",":->",".#",":-",
		  	"==>",":=","||",":-","\\*\\*"};
	 


	public static String[] getOperators() {
		return operators;
	}


	public static String[] getFunctions() {
		return functions;
	}


	public static String[] getPredicates() {
		return predicates;
	}


	public static void setHistoryCounters(String[] historyCounters) {
		OvertureKeywords.historyCounters = historyCounters;
	}


	public static String[] getHistoryCounters() {
		return historyCounters;
	}


	public static String[] getMultipleKeywords() {
		return multipleKeywords;
	}


	public static String[] getReservedwords() {
		return reservedwords;
	}


	public static String[] getTypes() {
		return types;
	}

	public static String[] getConstants() {
		return constants;
	}
}
