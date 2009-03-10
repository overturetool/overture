/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.lex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * An enumeration for the basic token types recognised by the lexical analyser.
 */

public enum Token implements Serializable
{
	// Variables
	IDENTIFIER(null, "id", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NAME(null, "name", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Literals
	NUMBER(null, "number", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	REALNUMBER(null, "real", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	CHARACTER(null, "char", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	STRING(null, "string", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	QUOTE(null, "<quote>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Basic types
	BOOL("bool", "bool", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NAT("nat", "nat", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NAT1("nat1", "nat1", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INT("int", "int", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	RAT("rat", "rat", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	REAL("real", "real", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	CHAR("char", "char", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TOKEN("token", "token", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Operators
	PLUS(null, "+", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MINUS(null, "-", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TIMES(null, "*", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DIVIDE(null, "/", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	REM("rem", "rem", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MOD("mod", "mod", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DIV("div", "div", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	LT(null, "<", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	LE(null, "<=", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	GT(null, ">", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	GE(null, ">=", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NE(null, "<>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EQUALS(null, "=", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EQUALSEQUALS(null, "==", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EQUIVALENT(null, "<=>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	IMPLIES(null, "=>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SUBSET("subset", "subset", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	PSUBSET("psubset", "psubset", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INSET("in set", "in set", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NOTINSET("not in set", "not in set", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SETDIFF(null, "\\", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MUNION("munion", "munion", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	PLUSPLUS(null, "++", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	STARSTAR(null, "**", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	UNION("union", "union", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INTER("inter", "inter", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INVERSE("inverse", "inverse", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	CONCATENATE(null, "^", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MAPLET(null, "|->", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	RANGE(null, "...", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DOMRESTO(null, "<:", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DOMRESBY(null, ":>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	RANGERESTO(null, ":->", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	RANGERESBY(null, "<-:", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	CARD("card", "card", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DOM("dom", "dom", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	LEN("len", "len", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	POWER("power", "power", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	RNG("rng", "rng", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ELEMS("elems", "elems", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ABS("abs", "abs", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DINTER("dinter", "dinter", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MERGE("merge", "merge", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	HEAD("hd", "hd", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TAIL("tl", "tl", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	FLOOR("floor", "floor", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DUNION("dunion", "dunion", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DISTCONC("conc", "conc", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INDS("inds", "inds", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	POINT(null, ".", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	COMP("comp", "comp", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	FORALL("forall", "forall", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EXISTS("exists", "exists", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EXISTS1("exists1", "exists1", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	IOTA("iota", "iota", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	LAMBDA("lambda", "lambda", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Functions
	TOTAL_FUNCTION(null, "+>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ARROW(null, "->", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	OPDEF(null, "==>", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MEASURE("measure", "measure", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Various
	BRA(null, "(", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	KET(null, ")", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	COMMA(null, ",", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SEMICOLON(null, ";", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	QMARK(null, "?", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	COLON(null, ":", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	COLONCOLON(null, "::", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	AMPERSAND(null, "&", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EQABST(null, ":-", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	PIPE(null, "|", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	PIPEPIPE(null, "||", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	HASH(null, "#", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	AT(null, "@", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SET_OPEN(null, "{", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SET_CLOSE(null, "}", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SEQ_OPEN(null, "[", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SEQ_CLOSE(null, "]", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ASSIGN(null, ":=", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Keywords
	COMPOSE("compose", "compose", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	END("end", "end", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	MAP("map", "map", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INMAP("inmap", "inmap", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SET("set", "set", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SEQ("seq", "seq", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SEQ1("seq1", "seq1", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	OF("of", "of", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TO("to", "to", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	PRE("pre", "pre", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	POST("post", "post", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INV("inv", "inv", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	INIT("init", "init", Dialect.VDM_SL),
	TRUE("true", "true", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	FALSE("false", "false", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	AND("and", "and", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	OR("or", "or", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NOT("not", "not", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	NIL("nil", "nil", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	UNDEFINED("undefined", "undefined", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EXTERNAL("ext", "ext", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	READ("rd", "rd", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	WRITE("wr", "wr", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ERRS("errs", "errs", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DCL("dcl", "dcl", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DEF("def", "def", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	IS("is", "is", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	YET("yet", "yet", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SPECIFIED("specified", "specified", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Expressions
	LET("let", "let", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	IN("in", "in", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	BE("be", "be", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ST("st", "st", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	IF("if", "if", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	THEN("then", "then", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ELSE("else", "else", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ELSEIF("elseif", "elseif", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	CASES("cases", "cases", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	OTHERS("others", "others", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Statements
	RETURN("return", "return", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	FOR("for", "for", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ALL("all", "all", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	REVERSE("reverse", "reverse", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	DO("do", "do", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	BY("by", "by", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	WHILE("while", "while", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	USING("using", "using", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ALWAYS("always", "always", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ATOMIC("atomic", "atomic", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TRAP("trap", "trap", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	WITH("with", "with", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	TIXE("tixe", "tixe", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	EXIT("exit", "exit", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	ERROR("error", "error", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	SKIP("skip", "skip", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),

	// Sections
	TYPES("types", "types", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	VALUES("values", "values", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	FUNCTIONS("functions", "functions", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	OPERATIONS("operations", "operations", Dialect.VDM_SL, Dialect.VDM_PP, Dialect.VDM_RT),
	THREAD("thread", "thread", Dialect.VDM_PP, Dialect.VDM_RT),
	SYNC("sync", "sync", Dialect.VDM_PP, Dialect.VDM_RT),
	TRACES("traces", "traces", Dialect.VDM_PP, Dialect.VDM_RT),
	INSTANCE("instance", "instance", Dialect.VDM_PP, Dialect.VDM_RT),
	VARIABLES("variables", "variables", Dialect.VDM_PP, Dialect.VDM_RT),

	// Modules (VDM-SL only)
	MODULE("module", "module", Dialect.VDM_SL),
	DLMODULE("dlmodule", "dlmodule", Dialect.VDM_SL),
	USELIB("uselib", "uselib", Dialect.VDM_SL),
	IMPORTS("imports", "imports", Dialect.VDM_SL),
	FROM("from", "from", Dialect.VDM_SL),
	RENAMED("renamed", "renamed", Dialect.VDM_SL),
	EXPORTS("exports", "exports", Dialect.VDM_SL),
	DEFINITIONS("definitions", "definitions", Dialect.VDM_SL),
	STRUCT("struct", "struct", Dialect.VDM_SL),
	STATE("state", "state", Dialect.VDM_SL),

	// VDM++ extra tokens
	CLASS("class", "class", Dialect.VDM_PP, Dialect.VDM_RT),
	SUBCLASS("subclass", "subclass", Dialect.VDM_PP, Dialect.VDM_RT),
	STATIC("static", "static", Dialect.VDM_PP, Dialect.VDM_RT),
	PUBLIC("public", "public", Dialect.VDM_PP, Dialect.VDM_RT),
	PRIVATE("private", "private", Dialect.VDM_PP, Dialect.VDM_RT),
	PROTECTED("protected", "protected", Dialect.VDM_PP, Dialect.VDM_RT),
	SELF("self", "self", Dialect.VDM_PP, Dialect.VDM_RT),
	NEW("new", "new", Dialect.VDM_PP, Dialect.VDM_RT),
	RESPONSIBILITY("responsibility", "responsibility", Dialect.VDM_PP, Dialect.VDM_RT),
	ISOFBASECLASS("isofbaseclass", "isofbaseclass", Dialect.VDM_PP, Dialect.VDM_RT),
	ISOFCLASS("isofclass", "isofclass", Dialect.VDM_PP, Dialect.VDM_RT),
	SAMEBASECLASS("samebaseclass", "samebaseclass", Dialect.VDM_PP, Dialect.VDM_RT),
	SAMECLASS("sameclass", "sameclass", Dialect.VDM_PP, Dialect.VDM_RT),
	THREADID("threadid", "threadid", Dialect.VDM_PP, Dialect.VDM_RT),
	PERIODIC("periodic", "periodic", Dialect.VDM_PP, Dialect.VDM_RT),
	PER("per", "per", Dialect.VDM_PP, Dialect.VDM_RT),
	MUTEX("mutex", "mutex", Dialect.VDM_PP, Dialect.VDM_RT),
	REQ("#req", "#req", Dialect.VDM_PP, Dialect.VDM_RT),
	ACT("#act", "#act", Dialect.VDM_PP, Dialect.VDM_RT),
	FIN("#fin", "#fin", Dialect.VDM_PP, Dialect.VDM_RT),
	ACTIVE("#active", "#active", Dialect.VDM_PP, Dialect.VDM_RT),
	WAITING("#waiting", "#waiting", Dialect.VDM_PP, Dialect.VDM_RT),
	START("start", "start", Dialect.VDM_PP, Dialect.VDM_RT),
	STARTLIST("startlist", "startlist", Dialect.VDM_PP, Dialect.VDM_RT),

	// VICE extra tokens
	TIME("time", "time", Dialect.VDM_RT),
	ASYNC("async", "async", Dialect.VDM_RT),
	CYCLES("cycles", "cycles", Dialect.VDM_RT),
	DURATION("duration", "duration", Dialect.VDM_RT),
	SYSTEM("system", "system", Dialect.VDM_RT),

	// No more tokens
	EOF(null, null, Dialect.VDM_SL);


	/** The keyword associated with a token, if any. */
	private String keyword = null;

	/** The displayable form of the token. */
	private String display = null;

	/** The dialect(s) of the keyword, VDM-SL, VDM++ or VICE. */
	private List<Dialect> dialects = null;

	/**
	 * Construct a token with the associated keyword, display and dialect.
	 */

	private Token(String keyword, String display, Dialect... dialects)
	{
		this.keyword = keyword;
		this.display = display;
		this.dialects = Arrays.asList(dialects);
	}

	/**
	 * Lookup a keyword/dialect and return a Token, or null.
	 */

	public static Token lookup(String word, Dialect dialect)
	{
		for (Token token: values())
		{
			if (token.keyword != null &&
				token.keyword.equals(word) &&
				token.dialects.contains(dialect))
			{
				return token;
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		return display;
	}
}
