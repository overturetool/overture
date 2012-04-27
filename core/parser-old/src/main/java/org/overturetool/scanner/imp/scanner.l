// overture scanner implementation for jflex
// use JFLEX version 1.4.1 or later - see http://www.jflex.de

// define the scope of the (generated) Java source file
package org.overturetool.scanner.imp;

// *****************************
// *** LOCAL REQUIRED INPUTS ***
// *****************************

// import generic java definitions
import java.util.*;

// import the VDM toolbox definitions
import jp.co.csk.vdm.toolbox.VDM.*;

// import the parser token definitions
import org.overturetool.parser.imp.OvertureParserTokens;

// import lexem definition and implementation
import org.overturetool.ast.itf.IOmlLexem;
import org.overturetool.ast.imp.OmlLexem;

%%

%{

// placeholder for the list of scanned lexems
private Vector theLexems = null;

// placeholder for the reserved word (keyword) table
static private HashMap<String,Short> keywords = null;

// initialize the reserved word table as a static constructor
static {
	keywords = new HashMap<String,Short>();
	keywords.put("#act", LEX_ACT);
	keywords.put("#active", LEX_ACTIVE);
	keywords.put("#fin", LEX_FIN);
	keywords.put("#req", LEX_REQ);
	keywords.put("#waiting", LEX_WAITING);
	keywords.put("abs", LEX_ABS);
	keywords.put("all", LEX_ALL);
	keywords.put("always", LEX_ALWAYS);
	keywords.put("and", LEX_AND);
	keywords.put("atomic", LEX_ATOMIC);
	keywords.put("async", LEX_ASYNC);
	keywords.put("be", LEX_BE);
	keywords.put("bool", LEX_BOOL);
	keywords.put("by", LEX_BY);
	keywords.put("card", LEX_CARD);
	keywords.put("cases", LEX_CASES);
	keywords.put("char", LEX_CHAR);
	keywords.put("class", LEX_CLASS);
	keywords.put("comp", LEX_COMP);
	keywords.put("compose", LEX_COMPOSE);
	keywords.put("conc", LEX_CONC);
	keywords.put("cycles", LEX_CYCLES);
	keywords.put("dcl", LEX_DCL);
	keywords.put("def", LEX_DEF);
	keywords.put("dinter", LEX_DINTER);
	keywords.put("div", LEX_ARITHMETIC_INTEGER_DIVISION);
	keywords.put("do", LEX_DO);
	keywords.put("dom", LEX_DOM);
	keywords.put("dunion", LEX_DUNION);
	keywords.put("duration", LEX_DURATION);
	keywords.put("elems", LEX_ELEMS);
	keywords.put("else", LEX_ELSE);
	keywords.put("elseif", LEX_ELSEIF);
	keywords.put("end", LEX_END);
	keywords.put("error", LEX_ERROR);
	keywords.put("errs", LEX_ERRS);
	keywords.put("exists", LEX_EXISTS);
	keywords.put("exists1", LEX_EXISTS1);
	keywords.put("exit", LEX_EXIT);
	keywords.put("ext", LEX_EXT);
	keywords.put("false", LEX_bool_false);
	keywords.put("floor", LEX_FLOOR);
	keywords.put("for", LEX_FOR);
	keywords.put("forall", LEX_FORALL);
	keywords.put("from", LEX_FROM);
	keywords.put("functions", LEX_FUNCTIONS);
	keywords.put("hd", LEX_HD);
	keywords.put("if", LEX_IF);
	keywords.put("in", LEX_IN);
	keywords.put("inds", LEX_INDS);
	keywords.put("inmap", LEX_INMAP);
	keywords.put("instance", LEX_INSTANCE);
	keywords.put("int", LEX_INT);
	keywords.put("inter", LEX_SET_INTERSECTION);
	keywords.put("inv", LEX_INV);
	keywords.put("inverse", LEX_INVERSE);
	keywords.put("iota", LEX_IOTA);
	keywords.put("is", LEX_IS);
	keywords.put("is_", LEX_IS_);
	keywords.put("isofbaseclass", LEX_ISOFBASECLASS);
	keywords.put("isofclass", LEX_ISOFCLASS);
	keywords.put("lambda", LEX_LAMBDA);
	keywords.put("len", LEX_LEN);
	keywords.put("let", LEX_LET);
	keywords.put("map", LEX_MAP);
	keywords.put("merge", LEX_DMERGE);
	keywords.put("mk_", LEX_MK_);
	keywords.put("mod", LEX_MOD);
	keywords.put("mu", LEX_MU);
	keywords.put("munion", LEX_MAP_MERGE);
	keywords.put("mutex", LEX_MUTEX);
	keywords.put("nat", LEX_NAT);
	keywords.put("nat1", LEX_NATONE);
	keywords.put("new", LEX_NEW);
	keywords.put("nil", LEX_NIL);
	keywords.put("not", LEX_NOT);
	keywords.put("of", LEX_OF);
	keywords.put("operations", LEX_OPERATIONS);
	keywords.put("or", LEX_OR);
	keywords.put("others", LEX_OTHERS);
	keywords.put("per", LEX_PER);
	keywords.put("periodic", LEX_PERIODIC);
	keywords.put("post", LEX_POST);
	keywords.put("power", LEX_POWER);
	keywords.put("pre", LEX_PRE);
	keywords.put("pre_", LEX_PRECONDAPPLY);
	keywords.put("private", LEX_PRIVATE);
	keywords.put("protected", LEX_PROTECTED);
	keywords.put("psubset", LEX_PROPER_SUBSET);
	keywords.put("public", LEX_PUBLIC);
	keywords.put("rat", LEX_RAT);
	keywords.put("rd", LEX_RD);
	keywords.put("real", LEX_REAL);
	keywords.put("rem", LEX_REM);
	keywords.put("responsibility", LEX_RESPONSIBILITY);
	keywords.put("return", LEX_RETURN);
	keywords.put("reverse", LEX_REVERSE);
	keywords.put("rng", LEX_RNG);
	keywords.put("samebaseclass", LEX_SAMEBASECLASS);
	keywords.put("sameclass", LEX_SAMECLASS);
	keywords.put("self", LEX_SELF);
	keywords.put("seq", LEX_SEQ);
	keywords.put("seq1", LEX_SEQ1);
	keywords.put("set", LEX_SET);
	keywords.put("skip", LEX_SKIP);
	keywords.put("specified", LEX_SPECIFIED);
	keywords.put("sporadic", LEX_SPORADIC);		/* added for Marcel Verhoef */
	keywords.put("st", LEX_ST);
	keywords.put("start", LEX_START);
	keywords.put("startlist", LEX_STARTLIST);
	keywords.put("static", LEX_STATIC);
	keywords.put("subclass", LEX_SUBCLASS);
	keywords.put("subset", LEX_SUBSET);
	keywords.put("sync", LEX_SYNC);
	keywords.put("system", LEX_SYSTEM);
	keywords.put("then", LEX_THEN);
	keywords.put("thread", LEX_THREAD);
	keywords.put("threadid", LEX_THREADID);
	keywords.put("time", LEX_TIME);
	keywords.put("tixe", LEX_TIXE);
	keywords.put("tl", LEX_TL);
	keywords.put("to", LEX_TO);
	keywords.put("token", LEX_TOKEN);
	keywords.put("traces", LEX_TRACES);			/* added for Adriana Sucena */
	keywords.put("trap", LEX_TRAP);
	keywords.put("true", LEX_bool_true);
	keywords.put("types", LEX_TYPES);
	keywords.put("undefined", LEX_UNDEFINED);
	keywords.put("union", LEX_SET_UNION);
	keywords.put("values", LEX_VALUES);
	keywords.put("variables", LEX_VARIABLES);
	keywords.put("while", LEX_WHILE);
	keywords.put("with", LEX_WITH);
	keywords.put("wr", LEX_WR);
	keywords.put("yet", LEX_YET);
}

// *************************
// *** PUBLIC OPERATIONS ***
// *************************

// additional constructor - read from input buffer
public OvertureScanner (String inbuf)
{
	this(new java.io.StringReader(inbuf));
}

// the parser will call 'getNextToken'.
public OmlLexem getNextToken() throws java.io.IOException
{
	OmlLexem theLexem = yylex();
	if ( theLexem != null) theLexems.add(theLexem);
	return theLexem;
}

// the parser will initialise the sequence of lexems (from OmlDocument)
public void setLexems (Vector vec)
{
	assert (vec != null);
	theLexems = vec;
}

public int getLine()
{
	return yyline + 1;
}

public int getColumn()
{
	return yycolumn + 1;
}

// ************************************
// *** AUXILIARY PRIVATE OPERATIONS ***
// ************************************

// helper function for checking reserved words and identifiers
private OmlLexem checkIdentifier(String id) {
    Long line = new Long(yyline+1);
    Long column = new Long(yycolumn+1);
    try {
		if (keywords.containsKey(id)) {
			return new OmlLexem(line, column, new Long(keywords.get(id)), id, IOmlLexem.ILEXEMKEYWORD);
		} else {
		    //DEBUG String theText = yytext();
			//DEBUG System.out.print(theText + " = ");
			//DEBUG for (int idx=0; idx< theText.length(); idx++) System.out.format("%04x ", (int) theText.charAt(idx));
			//DEBUG System.out.println();
			return new OmlLexem(line, column, new Long(LEX_identifier), id, IOmlLexem.ILEXEMIDENTIFIER);
		}
	}
	catch (CGException cge) {
		cge.printStackTrace();
		return null;
	}
}

// helper function for default token creation
private OmlLexem defaultToken()
{
	return createToken((short) yytext().charAt(0));
}

// helper function to create a new token
private OmlLexem createToken(short lex)
{
    return createToken(lex, IOmlLexem.ILEXEMUNKNOWN);
}

private OmlLexem createToken(short lex, Long tp)
{
    Long line = new Long(yyline+1);
    Long column = new Long(yycolumn+1);
    Long lexem = new Long(lex);
    try {
		return new OmlLexem(line, column, lexem, yytext(), tp);
	}
	catch (CGException cge) {
		cge.printStackTrace();
		return null;
	}
}

%}

// ******************************************
// *** JFLEX SCANNER GENERATOR DIRECTIVES ***
// ******************************************

// the name of the scanner class will be 'OvertureScanner'
%class OvertureScanner

// the generated class is public
%public

// we want to trace the location of each token
%line
%column

// the input files will be unicode
%unicode

// we use the BYACCJ terminal definitions
%implements OvertureParserTokens

// all JFLEX internal operations, including yylex, are defined as private
%apiprivate

// yylex will return 'OmlLexem' in stead of int
%type OmlLexem

// automatically close the input file when EOF is reached
%eofclose

// uncomment next line if you need to debug the scanner
// %debug

// *****************************
// *** SHORTHAND DEFINITIONS ***
// *****************************

ucode					= [\u0100-\ufff0]
hexdigit 				= [0-9ABCDEF]|[0-9abcdef]
hexquad 				= {hexdigit}{hexdigit}{hexdigit}{hexdigit}
universalcharactername  = (\\u{hexquad})|(\\U{hexquad})
letter 					= [A-Za-z]|#[A-Za-z]|{universalcharactername}|{ucode}
digit 					= [0-9]
prime 					= \`
hook 					= \~
rtfuniversalcharacter	= \\u{hexquad}[A-Za-z]
identifierorkeyword		= {letter}([0-9\'_]|{letter})*

numericliteral 			= {digit}+
realliteral				= [0-9]+(("."[0-9]+)|([Ee]("+"|"-")?[0-9]+)|("."[0-9]+[Ee]("+"|"-")?[0-9]+))

embeddedctrlchar 		= [\000-\037]
backslashed				= \\c.|\\x..|\\[\\nrabtvef\'\"]|\\[0-3][0-7][0-7]
highbitchar				= [\200-\377]
deletechar				= \177
characterliteral		= "'"([\040-\133\135-\176]|{embeddedctrlchar}|{backslashed}|{deletechar}|{highbitchar}|{universalcharactername}|{rtfuniversalcharacter}|{ucode})"'"

textliteral				= \"([\040-\041\043-\133\135-\176]|{embeddedctrlchar}|{backslashed}|{deletechar}|{highbitchar}|{universalcharactername}|{ucode})*\"

quoteliteral 			= \<{identifierorkeyword}\>

eolc1					= --[^\r\n]*
eolc2					= \/\/[^\r\n]*
eolcomment				= {eolc1}|{eolc2}
comment					= "/*" [^*] ~"*/" | "/*" "*"+ "/"

separator 				= [ \t\r\n]
separators				= {separator}+

range					= ","({separator}*)"..."({separator}*)","

texbreak				= "##!!#!"

%%

// ********************************
// *** SCANNER PRODUCTION RULES ***
// ********************************

// in-line and multi-line comments
{eolcomment}							{ return createToken(LEX_COMMENT, OmlLexem.ILEXEMLINECOMMENT); }
{comment}								{ if (yytext().indexOf('\n') != -1) {
											return createToken(LEX_COMMENT, OmlLexem.ILEXEMBLOCKCOMMENT);
                                          } else {
											return createToken(LEX_COMMENT, OmlLexem.ILEXEMLINECOMMENT);
                                          }
                                        }

// production rules for literals
{characterliteral}						{ return createToken(LEX_char_lit); }
{quoteliteral}							{ return createToken(LEX_quote_lit); }
{textliteral}							{ return createToken(LEX_text_lit); }
{realliteral}							{ return createToken(LEX_real_lit); }
{numericliteral}						{ return createToken(LEX_num_lit); }

// keywords and identifiers
"not"{separators}"in"{separators}"set"	{ return createToken(LEX_NOT_IN_SET); }
"in"{separators}"set"					{ return createToken(LEX_IN_SET); }

"is_"/{identifierorkeyword} 			{ return createToken(LEX_IS_); }
"mk_"/{identifierorkeyword} 			{ return createToken(LEX_MK_); }
{identifierorkeyword}					{ return checkIdentifier(yytext()); }
\${identifierorkeyword} 				{ return createToken(LEX_dollar_identifier); }

// multi character tokens
{range}									{ return createToken(LEX_RANGE_OVER); }
{texbreak}								{ return createToken(LEX_TEXBREAK); }
"==>"									{ return createToken(LEX_OPERATION_ARROW); }
"<=>"									{ return createToken(LEX_LOGICAL_EQUIVALENCE); }
"|->"									{ return createToken(LEX_BAR_ARROW); }
"<-:"									{ return createToken(LEX_MAP_DOMAIN_RESTRICT_BY); }
":->"									{ return createToken(LEX_MAP_RANGE_RESTRICT_BY); }
":-"									{ return createToken(LEX_DONTCARE); }
".#"									{ return createToken(LEX_DOTHASH); }
"<="									{ return createToken(LEX_LESS_THAN_OR_EQUAL); }
">="									{ return createToken(LEX_GREATER_THAN_OR_EQUAL); }
"<>"									{ return createToken(LEX_NOT_EQUAL); }
"->"									{ return createToken(LEX_ARROW); }
"+>"									{ return createToken(LEX_TARROW); }
"=>"									{ return createToken(LEX_IMPLY); }
"=="									{ return createToken(LEX_IS_DEFINED_AS); }
"||"									{ return createToken(LEX_NONDET); }
":="									{ return createToken(LEX_ASSIGN); }
"::"									{ return createToken(LEX_DOUBLE_COLON); }
"**"									{ return createToken(LEX_EXP_OR_ITERATE); }
"++"									{ return createToken(LEX_MODIFY_BY); }
"<:"									{ return createToken(LEX_MAP_DOMAIN_RESTRICT_TO); }
":>"									{ return createToken(LEX_MAP_RANGE_RESTRICT_TO); }
"$$"									{ return createToken(LEX_LAST_RESULT); }

/* next two lines are added for Thomas Christensen */
"<["									{ return createToken(LEX_GENERIC_LEFT_BRACKET); }
"]>"									{ return createToken(LEX_GENERIC_RIGHT_BRACKET); }

// single character tokens
{hook}									{ return createToken(LEX_HOOK); }
{prime}									{ return createToken(LEX_PRIME); }
","										{ return defaultToken(); }
"!"										{ return defaultToken(); }
":"										{ return defaultToken(); }
";"										{ return defaultToken(); }
"="										{ return createToken(LEX_EQUAL); }
")"										{ return defaultToken(); }
"("										{ return defaultToken(); }
"|"										{ return defaultToken(); }
"-"										{ return defaultToken(); }
"["										{ return defaultToken(); }
"]"										{ return defaultToken(); }
"{"										{ return defaultToken(); }
"}"										{ return defaultToken(); }
"+"										{ return defaultToken(); }
"/"										{ return defaultToken(); }
"<"										{ return createToken(LEX_LESS_THAN); }
">"										{ return createToken(LEX_GREATER_THAN); }
"."										{ return defaultToken(); }
"&"										{ return createToken(LEX_RAISED_DOT); }
"*"										{ return defaultToken(); }
"^"										{ return createToken(LEX_SEQUENCE_CONCATENATE); }
"\\"									{ return createToken(LEX_SET_MINUS); }

// handle white space and new-lines
{separator}								{ /* IGNORE */ }

// default catch-all production rule is to return the current character
.										{ return defaultToken(); }

// production rule to handle end-of-file
<<EOF>>									{ return null; }
