//
// TREE DESCRIPTION LANGUAGE - SCANNER
//
// input file for jflex scanner generator version 1.4.3
//

// define the package name
package org.overture.tools.treegen;

// imports
import java.io.*;

%%

// define the lexer state for processing comments
%xstates SCOMM, DCOMM, TCOMM

%{

// place holder for the parser instance
private TreeParser theParser = null;

// constructor
public TreeScanner (TreeParser pParser, String fname)
{
    // create the link back to the parser
    theParser = pParser;
    
    // open the file
	try {
		// open the file input reader
		this.zzReader = new FileReader(fname);
	}
	catch(FileNotFoundException fnfe) {
		System.out.println(fnfe.getMessage());
		System.out.println("Switch reading to standard input");
		// fall-back open the standard input stream
		this.zzReader = new InputStreamReader(System.in);
	}
}

// auxiliary operation to get the position in the file
public String atPosition ()
{
	String retval = " at line " + (yyline + 1) + ", column " + (yycolumn+1);
	return retval;
}

%}

%class TreeScanner
%line
%column
%int
%implements TreeParserTokens
%eofclose

lower			= [a-z]
upper			= [A-Z]
char 			= ({lower}|{upper}|[_])+
digit 			= [0-9]
ident			= {char}({char}|{digit})*
quote   		= "<"({digit}|{upper}|[_])+">"
string  		= \"[^\"]+\"
ws				= \n | \r | \r\n | \t | " "

%%

/* normal production rules for lexems */
<YYINITIAL> {
	"--++"			{ yybegin(TCOMM); }
	"--"			{ yybegin(SCOMM); }
	"/*"			{ yybegin(DCOMM); }
	"mk_token"		{ return MK_TOKEN; }
	"subclass"		{ return SUBCLASS; }
	"class"			{ return CLASS; }
	"is"			{ return IS; }
	"of"			{ return OF; }
	"end"			{ return END; }
	"map"			{ return MAP; }
	"to"			{ return TO; }
	"seq"			{ return SEQ; }
	"set"			{ return SET; }
	"values"		{ return VALUES; }
	"types"			{ return TYPES; }
	"instance"		{ return INSTANCE; }
	"variables"		{ return VARIABLES; }
	"::"			{ return DCOLON; }
	":="			{ return ASSIGN; }
	":"				{ return COLON; }
	";"				{ return SCOLON; }
	"="				{ return EQ; }
	"["				{ return LB; }
	"]"				{ return RB; }
	"("				{ return LP; }
	")"				{ return RP; }
	"|"				{ return BAR; }
	{quote}			{ theParser.yylval = new TreeParserVal(yytext().substring(1,yylength()-1)); return QUOTE; }
	{string}		{ theParser.yylval = new TreeParserVal(yytext().substring(1,yylength()-1)); return STRING; }
	{ident}			{ theParser.yylval = new TreeParserVal(yytext()); return IDENT; }
	{ws}			{ /* ignore all whitespace characters */ }
	.				{ return yytext().charAt(0); }
}

/* processing single-line comments */
<SCOMM> {
	\n				{ yybegin(YYINITIAL); }
	.				{ /* ignore all other characters */ }
}

/* processing multi-line comments */
<DCOMM> {
	"*/"			{ yybegin(YYINITIAL); }
	{ws}			{ /* ignore all other characters */ }
	.				{ /* ignore all other characters */ }
}

/* processing single-line comment for constructing tokens in records */
<TCOMM> {
	":="			{ return ASSIGN; }
	"mk_token"		{ return MK_TOKEN; }
	"("				{ return LP; }
	")"				{ return RP; }
	{string}		{ theParser.yylval = new TreeParserVal(yytext().substring(1,yylength()-1)); return STRING; }
	\n				{ yybegin(YYINITIAL); }
	.				{ /* ignore all other characters */ }
}
