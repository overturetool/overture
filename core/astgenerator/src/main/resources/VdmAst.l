package nl.marcelverhoef.vdm.ast;

import java.io.*;

%%

%{

public AstParser theParser = null;

public AstScanner (AstParser pAstParser, String fname)
{
    // create the link back to the parser
    theParser = pAstParser;
    
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

public String atPosition ()
{
	String retval = " at line " + (yyline + 1) + ", column " + yycolumn;
	return retval;
}

%}

%class AstScanner
%line
%column
%int
%implements AstParserTokens
%eofclose

lower	= [a-z]
upper	= [A-Z]
char 	= ({lower}|{upper}|[_])+
digit 	= [0-9]
ident	= {char}({char}|{digit})*
quote   = "<"({digit}|{upper}|[_])+">"
string  = \"[^\"]+\"
ws		= \n | \r | \r\n | \t | " "

%%
"%directory"	{ return DIRECTORY; }
"%package"		{ return PACKAGE; }
"%prefix"		{ return PREFIX; }
"%top"			{ return TOP; }
--.*			{}
"map"			{ return MAP; }
"to"			{ return TO; }
"seq of"		{ return SEQ_OF; }
"set of"		{ return SET_OF; }
"::"			{ return DCOLON; }
":"				{ return COLON; }
";"				{ return SCOLON; }
"="				{ return EQ; }
"["				{ return LB; }
"]"				{ return RB; }
"|"				{ return BAR; }
"."				{ return DOT; }
{quote}			{ theParser.yylval = new AstParserVal(yytext().substring(1,yylength()-1)); return QUOTE; }
{string}		{ theParser.yylval = new AstParserVal(yytext().substring(1,yylength()-1)); return STRING; }
{ident}			{ theParser.yylval = new AstParserVal(yytext()); return IDENT; }
{ws}			{}
.				{ return yytext().charAt(0); }
