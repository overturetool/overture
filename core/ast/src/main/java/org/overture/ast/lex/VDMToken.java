package org.overture.ast.lex;

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

import static org.overture.ast.lex.Dialect.VDM_PP;
import static org.overture.ast.lex.Dialect.VDM_RT;
import static org.overture.ast.lex.Dialect.VDM_SL;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An enumeration for the basic token types recognised by the lexical analyser.
 */

public enum VDMToken implements Serializable
{
	// Variables
	IDENTIFIER(null, "id", VDM_SL, VDM_PP, VDM_RT), NAME(null, "name", VDM_SL,
			VDM_PP, VDM_RT),

	// Literals
	NUMBER(null, "number", VDM_SL, VDM_PP, VDM_RT), REALNUMBER(null, "real",
			VDM_SL, VDM_PP, VDM_RT), CHARACTER(null, "char", VDM_SL, VDM_PP,
			VDM_RT), STRING(null, "string", VDM_SL, VDM_PP, VDM_RT), QUOTE(
			null, "<quote>", VDM_SL, VDM_PP, VDM_RT),

	// Basic types
	BOOL("bool", "bool", VDM_SL, VDM_PP, VDM_RT), NAT("nat", "nat", VDM_SL,
			VDM_PP, VDM_RT), NAT1("nat1", "nat1", VDM_SL, VDM_PP, VDM_RT), INT(
			"int", "int", VDM_SL, VDM_PP, VDM_RT), RAT("rat", "rat", VDM_SL,
			VDM_PP, VDM_RT), REAL("real", "real", VDM_SL, VDM_PP, VDM_RT), CHAR(
			"char", "char", VDM_SL, VDM_PP, VDM_RT), TOKEN("token", "token",
			VDM_SL, VDM_PP, VDM_RT),

	// Operators
	PLUS(null, "+", VDM_SL, VDM_PP, VDM_RT), MINUS(null, "-", VDM_SL, VDM_PP,
			VDM_RT), TIMES(null, "*", VDM_SL, VDM_PP, VDM_RT), DIVIDE(null,
			"/", VDM_SL, VDM_PP, VDM_RT), REM("rem", "rem", VDM_SL, VDM_PP,
			VDM_RT), MOD("mod", "mod", VDM_SL, VDM_PP, VDM_RT), DIV("div",
			"div", VDM_SL, VDM_PP, VDM_RT), LT(null, "<", VDM_SL, VDM_PP,
			VDM_RT), LE(null, "<=", VDM_SL, VDM_PP, VDM_RT), GT(null, ">",
			VDM_SL, VDM_PP, VDM_RT), GE(null, ">=", VDM_SL, VDM_PP, VDM_RT), NE(
			null, "<>", VDM_SL, VDM_PP, VDM_RT), EQUALS(null, "=", VDM_SL,
			VDM_PP, VDM_RT), EQUALSEQUALS(null, "==", VDM_SL, VDM_PP, VDM_RT), EQUIVALENT(
			null, "<=>", VDM_SL, VDM_PP, VDM_RT), IMPLIES(null, "=>", VDM_SL,
			VDM_PP, VDM_RT), SUBSET("subset", "subset", VDM_SL, VDM_PP, VDM_RT), PSUBSET(
			"psubset", "psubset", VDM_SL, VDM_PP, VDM_RT), INSET("in set",
			"in set", VDM_SL, VDM_PP, VDM_RT), NOTINSET("not in set",
			"not in set", VDM_SL, VDM_PP, VDM_RT), SETDIFF(null, "\\", VDM_SL,
			VDM_PP, VDM_RT), MUNION("munion", "munion", VDM_SL, VDM_PP, VDM_RT), PLUSPLUS(
			null, "++", VDM_SL, VDM_PP, VDM_RT), STARSTAR(null, "**", VDM_SL,
			VDM_PP, VDM_RT), UNION("union", "union", VDM_SL, VDM_PP, VDM_RT), INTER(
			"inter", "inter", VDM_SL, VDM_PP, VDM_RT), INVERSE("inverse",
			"inverse", VDM_SL, VDM_PP, VDM_RT), CONCATENATE(null, "^", VDM_SL,
			VDM_PP, VDM_RT), MAPLET(null, "|->", VDM_SL, VDM_PP, VDM_RT), RANGE(
			null, "...", VDM_SL, VDM_PP, VDM_RT), DOMRESTO(null, "<:", VDM_SL,
			VDM_PP, VDM_RT), DOMRESBY(null, "<-:", VDM_SL, VDM_PP, VDM_RT), RANGERESTO(
			null, ":>", VDM_SL, VDM_PP, VDM_RT), RANGERESBY(null, ":->",
			VDM_SL, VDM_PP, VDM_RT), CARD("card", "card", VDM_SL, VDM_PP,
			VDM_RT), DOM("dom", "dom", VDM_SL, VDM_PP, VDM_RT), LEN("len",
			"len", VDM_SL, VDM_PP, VDM_RT), POWER("power", "power", VDM_SL,
			VDM_PP, VDM_RT), RNG("rng", "rng", VDM_SL, VDM_PP, VDM_RT), ELEMS(
			"elems", "elems", VDM_SL, VDM_PP, VDM_RT), ABS("abs", "abs",
			VDM_SL, VDM_PP, VDM_RT), DINTER("dinter", "dinter", VDM_SL, VDM_PP,
			VDM_RT), MERGE("merge", "merge", VDM_SL, VDM_PP, VDM_RT), HEAD(
			"hd", "hd", VDM_SL, VDM_PP, VDM_RT), TAIL("tl", "tl", VDM_SL,
			VDM_PP, VDM_RT), FLOOR("floor", "floor", VDM_SL, VDM_PP, VDM_RT), DUNION(
			"dunion", "dunion", VDM_SL, VDM_PP, VDM_RT), DISTCONC("conc",
			"conc", VDM_SL, VDM_PP, VDM_RT), INDS("inds", "inds", VDM_SL,
			VDM_PP, VDM_RT), POINT(null, ".", VDM_SL, VDM_PP, VDM_RT), COMP(
			"comp", "comp", VDM_SL, VDM_PP, VDM_RT), FORALL("forall", "forall",
			VDM_SL, VDM_PP, VDM_RT), EXISTS("exists", "exists", VDM_SL, VDM_PP,
			VDM_RT), EXISTS1("exists1", "exists1", VDM_SL, VDM_PP, VDM_RT), IOTA(
			"iota", "iota", VDM_SL, VDM_PP, VDM_RT), LAMBDA("lambda", "lambda",
			VDM_SL, VDM_PP, VDM_RT),

	// Functions
	TOTAL_FUNCTION(null, "+>", VDM_SL, VDM_PP, VDM_RT), ARROW(null, "->",
			VDM_SL, VDM_PP, VDM_RT), OPDEF(null, "==>", VDM_SL, VDM_PP, VDM_RT), MEASURE(
			"measure", "measure", VDM_SL, VDM_PP, VDM_RT),

	// Various
	BRA(null, "(", VDM_SL, VDM_PP, VDM_RT), KET(null, ")", VDM_SL, VDM_PP,
			VDM_RT), COMMA(null, ",", VDM_SL, VDM_PP, VDM_RT), SEMICOLON(null,
			";", VDM_SL, VDM_PP, VDM_RT), QMARK(null, "?", VDM_SL, VDM_PP,
			VDM_RT), COLON(null, ":", VDM_SL, VDM_PP, VDM_RT), COLONCOLON(null,
			"::", VDM_SL, VDM_PP, VDM_RT), AMPERSAND(null, "&", VDM_SL, VDM_PP,
			VDM_RT), EQABST(null, ":-", VDM_SL, VDM_PP, VDM_RT), PIPE(null,
			"|", VDM_SL, VDM_PP, VDM_RT), PIPEPIPE(null, "||", VDM_SL, VDM_PP,
			VDM_RT), HASH(null, "#", VDM_SL, VDM_PP, VDM_RT), AT(null, "@",
			VDM_SL, VDM_PP, VDM_RT), SET_OPEN(null, "{", VDM_SL, VDM_PP, VDM_RT), SET_CLOSE(
			null, "}", VDM_SL, VDM_PP, VDM_RT), SEQ_OPEN(null, "[", VDM_SL,
			VDM_PP, VDM_RT), SEQ_CLOSE(null, "]", VDM_SL, VDM_PP, VDM_RT), ASSIGN(
			null, ":=", VDM_SL, VDM_PP, VDM_RT),

	// Keywords
	COMPOSE("compose", "compose", VDM_SL, VDM_PP, VDM_RT), END("end", "end",
			VDM_SL, VDM_PP, VDM_RT), MAP("map", "map", VDM_SL, VDM_PP, VDM_RT), INMAP(
			"inmap", "inmap", VDM_SL, VDM_PP, VDM_RT), SET("set", "set",
			VDM_SL, VDM_PP, VDM_RT), SEQ("seq", "seq", VDM_SL, VDM_PP, VDM_RT), SEQ1(
			"seq1", "seq1", VDM_SL, VDM_PP, VDM_RT), OF("of", "of", VDM_SL,
			VDM_PP, VDM_RT), TO("to", "to", VDM_SL, VDM_PP, VDM_RT), PRE("pre",
			"pre", VDM_SL, VDM_PP, VDM_RT), POST("post", "post", VDM_SL,
			VDM_PP, VDM_RT), INV("inv", "inv", VDM_SL, VDM_PP, VDM_RT), INIT(
			"init", "init", VDM_SL), TRUE("true", "true", VDM_SL, VDM_PP,
			VDM_RT), FALSE("false", "false", VDM_SL, VDM_PP, VDM_RT), AND(
			"and", "and", VDM_SL, VDM_PP, VDM_RT), OR("or", "or", VDM_SL,
			VDM_PP, VDM_RT), NOT("not", "not", VDM_SL, VDM_PP, VDM_RT), NIL(
			"nil", "nil", VDM_SL, VDM_PP, VDM_RT), UNDEFINED("undefined",
			"undefined", VDM_SL, VDM_PP, VDM_RT), EXTERNAL("ext", "ext",
			VDM_SL, VDM_PP, VDM_RT), READ("rd", "rd", VDM_SL, VDM_PP, VDM_RT), WRITE(
			"wr", "wr", VDM_SL, VDM_PP, VDM_RT), ERRS("errs", "errs", VDM_SL,
			VDM_PP, VDM_RT), DCL("dcl", "dcl", VDM_SL, VDM_PP, VDM_RT), DEF(
			"def", "def", VDM_SL, VDM_PP, VDM_RT), IS("is", "is", VDM_SL,
			VDM_PP, VDM_RT), YET("yet", "yet", VDM_SL, VDM_PP, VDM_RT), SPECIFIED(
			"specified", "specified", VDM_SL, VDM_PP, VDM_RT),
			PURE("pure", "pure", VDM_SL, VDM_PP, VDM_RT),

	// Expressions
	LET("let", "let", VDM_SL, VDM_PP, VDM_RT), IN("in", "in", VDM_SL, VDM_PP,
			VDM_RT), BE("be", "be", VDM_SL, VDM_PP, VDM_RT), ST("st", "st",
			VDM_SL, VDM_PP, VDM_RT), IF("if", "if", VDM_SL, VDM_PP, VDM_RT), THEN(
			"then", "then", VDM_SL, VDM_PP, VDM_RT), ELSE("else", "else",
			VDM_SL, VDM_PP, VDM_RT), ELSEIF("elseif", "elseif", VDM_SL, VDM_PP,
			VDM_RT), CASES("cases", "cases", VDM_SL, VDM_PP, VDM_RT), OTHERS(
			"others", "others", VDM_SL, VDM_PP, VDM_RT),

	// Statements
	RETURN("return", "return", VDM_SL, VDM_PP, VDM_RT), FOR("for", "for",
			VDM_SL, VDM_PP, VDM_RT), ALL("all", "all", VDM_SL, VDM_PP, VDM_RT), REVERSE(
			"reverse", "reverse", VDM_SL, VDM_PP, VDM_RT), DO("do", "do",
			VDM_SL, VDM_PP, VDM_RT), BY("by", "by", VDM_SL, VDM_PP, VDM_RT), WHILE(
			"while", "while", VDM_SL, VDM_PP, VDM_RT), USING("using", "using",
			VDM_SL, VDM_PP, VDM_RT), ALWAYS("always", "always", VDM_SL, VDM_PP,
			VDM_RT), ATOMIC("atomic", "atomic", VDM_SL, VDM_PP, VDM_RT), TRAP(
			"trap", "trap", VDM_SL, VDM_PP, VDM_RT), WITH("with", "with",
			VDM_SL, VDM_PP, VDM_RT), TIXE("tixe", "tixe", VDM_SL, VDM_PP,
			VDM_RT), EXIT("exit", "exit", VDM_SL, VDM_PP, VDM_RT), ERROR(
			"error", "error", VDM_SL, VDM_PP, VDM_RT), SKIP("skip", "skip",
			VDM_SL, VDM_PP, VDM_RT),

	// Sections
	TYPES("types", "types", VDM_SL, VDM_PP, VDM_RT), VALUES("values", "values",
			VDM_SL, VDM_PP, VDM_RT), FUNCTIONS("functions", "functions",
			VDM_SL, VDM_PP, VDM_RT), OPERATIONS("operations", "operations",
			VDM_SL, VDM_PP, VDM_RT), THREAD("thread", "thread", VDM_PP, VDM_RT), SYNC(
			"sync", "sync", VDM_PP, VDM_RT), TRACES("traces", "traces", VDM_SL,
			VDM_PP, VDM_RT), INSTANCE("instance", "instance", VDM_PP, VDM_RT), VARIABLES(
			"variables", "variables", VDM_PP, VDM_RT),

	// Modules (VDM-SL only)
	MODULE("module", "module", VDM_SL), DLMODULE("dlmodule", "dlmodule", VDM_SL), USELIB(
			"uselib", "uselib", VDM_SL), IMPORTS("imports", "imports", VDM_SL), FROM(
			"from", "from", VDM_SL), RENAMED("renamed", "renamed", VDM_SL), EXPORTS(
			"exports", "exports", VDM_SL), DEFINITIONS("definitions",
			"definitions", VDM_SL), STRUCT("struct", "struct", VDM_SL), STATE(
			"state", "state", VDM_SL),

	// VDM++ extra tokens
	CLASS("class", "class", VDM_PP, VDM_RT), SUBCLASS("subclass", "subclass",
			VDM_PP, VDM_RT), STATIC("static", "static", VDM_PP, VDM_RT), PUBLIC(
			"public", "public", VDM_PP, VDM_RT), PRIVATE("private", "private",
			VDM_PP, VDM_RT), PROTECTED("protected", "protected", VDM_PP, VDM_RT), SELF(
			"self", "self", VDM_PP, VDM_RT), NEW("new", "new", VDM_PP, VDM_RT), RESPONSIBILITY(
			"responsibility", "responsibility", VDM_PP, VDM_RT), ISOFBASECLASS(
			"isofbaseclass", "isofbaseclass", VDM_PP, VDM_RT), ISOFCLASS(
			"isofclass", "isofclass", VDM_PP, VDM_RT), SAMEBASECLASS(
			"samebaseclass", "samebaseclass", VDM_PP, VDM_RT), SAMECLASS(
			"sameclass", "sameclass", VDM_PP, VDM_RT), THREADID("threadid",
			"threadid", VDM_PP, VDM_RT), PERIODIC("periodic", "periodic",
			VDM_PP, VDM_RT), SPORADIC("sporadic", "sporadic", VDM_PP, VDM_RT), PER(
			"per", "per", VDM_PP, VDM_RT), MUTEX("mutex", "mutex", VDM_PP,
			VDM_RT), REQ("#req", "#req", VDM_PP, VDM_RT), ACT("#act", "#act",
			VDM_PP, VDM_RT), FIN("#fin", "#fin", VDM_PP, VDM_RT), ACTIVE(
			"#active", "#active", VDM_PP, VDM_RT), WAITING("#waiting",
			"#waiting", VDM_PP, VDM_RT), START("start", "start", VDM_PP, VDM_RT), STARTLIST(
			"startlist", "startlist", VDM_PP, VDM_RT), STOP("stop", "stop",
			VDM_PP, VDM_RT), STOPLIST("stoplist", "stoplist", VDM_PP, VDM_RT),

	// VICE extra tokens
	TIME("time", "time", VDM_RT), ASYNC("async", "async", VDM_RT), CYCLES(
			"cycles", "cycles", VDM_RT), DURATION("duration", "duration",
			VDM_RT), SYSTEM("system", "system", VDM_RT),

	// No more tokens
	EOF(null, null, VDM_SL);

	/** The keyword associated with a token, if any. */
	private String keyword = null;

	/** The displayable form of the token. */
	private String display = null;

	/** The dialect(s) of the keyword, VDM-SL, VDM++ or VICE. */
	private List<Dialect> dialects = null;

	/** Maps to speed up the lookup of individual token strings. */
	private static Map<String, VDMToken> sltokens;
	private static Map<String, VDMToken> pptokens;
	private static Map<String, VDMToken> rttokens;

	static
	{
		// This has to happen in a static block because an enum always
		// initializes its members before any statics (or member inits)
		// are performed.

		sltokens = new HashMap<String, VDMToken>(256);
		pptokens = new HashMap<String, VDMToken>(256);
		rttokens = new HashMap<String, VDMToken>(256);

		for (VDMToken token : values())
		{
			if (token.keyword != null)
			{
				for (Dialect dialect : token.dialects)
				{
					switch (dialect)
					{
						case VDM_SL:
							sltokens.put(token.keyword, token);
							break;

						case VDM_PP:
							pptokens.put(token.keyword, token);
							break;

						case VDM_RT:
							rttokens.put(token.keyword, token);
							break;
					}
				}
			}
		}
	}

	/**
	 * Construct a token with the associated keyword, display and dialect.
	 */

	private VDMToken(String keyword, String display, Dialect... dialects)
	{
		this.keyword = keyword;
		this.display = display;
		this.dialects = Arrays.asList(dialects);
	}

	/**
	 * Lookup a keyword/dialect and return a Token, or null.
	 * 
	 * @param word
	 * @param dialect
	 * @return
	 */

	public static VDMToken lookup(String word, Dialect dialect)
	{
		switch (dialect)
		{
			case VDM_SL:
				return sltokens.get(word);

			case VDM_PP:
				return pptokens.get(word);

			case VDM_RT:
				return rttokens.get(word);
		}

		return null;
	}

	@Override
	public String toString()
	{
		return display;
	}
}
