//### This file created by BYACC 1.8(/Java extension  1.11)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package org.overturetool.astgenerator.parser;



//#line 2 "VdmAst.y"
import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.astgenerator.*;
//#line 20 "AstParser.java"



@SuppressWarnings("all")
public class AstParser
             implements AstParserTokens
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class AstParserVal is defined in AstParserVal.java


String   yytext;//user variable to return contextual strings
AstParserVal yyval; //used to return semantic vals from action routines
AstParserVal yylval;//the 'lval' (result) I got from yylex()
AstParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new AstParserVal[YYSTACKSIZE];
  yyval=new AstParserVal();
  yylval=new AstParserVal();
  valptr=-1;
}
void val_push(AstParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
AstParserVal val_pop()
{
  if (valptr<0)
    return new AstParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
AstParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new AstParserVal();
  return valstk[ptr];
}
//#### end semantic value section ####
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    4,    5,    6,    7,    8,
    8,    3,    2,    2,   11,   11,   12,   10,   10,   10,
   10,   10,   10,   10,    9,    9,
};
final static short yylen[] = {                            2,
    0,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    3,    3,    3,    3,    3,    2,    2,    2,    2,    1,
    3,    3,    2,    3,    1,    2,    3,    1,    1,    3,
    3,    2,    2,    4,    1,    2,
};
final static short yydefred[] = {                         0,
    2,    0,    0,    0,    0,    0,    0,    0,    4,    5,
    6,    7,    8,    9,    0,    0,   20,    0,   16,   18,
   35,    0,    0,    0,    0,   25,    0,    0,   28,    0,
   29,    0,    0,    0,   36,   10,   11,   12,   13,   14,
   15,    0,   26,   32,   33,    0,    0,    0,   21,    0,
   31,    0,   30,    0,
};
final static short yydgoto[] = {                          7,
    8,    9,   10,   11,   12,   13,   14,   18,   22,   33,
   25,   26,
};
final static short yysindex[] = {                      -241,
    0, -240, -262, -257, -264, -237,    0, -220,    0,    0,
    0,    0,    0,    0, -233, -242,    0, -227,    0,    0,
    0, -221, -229, -215, -233,    0, -242, -242,    0, -242,
    0, -242, -222, -214,    0,    0,    0,    0,    0,    0,
    0, -242,    0,    0,    0, -230, -261, -242,    0, -222,
    0, -242,    0, -222,
};
final static short yyrindex[] = {                        47,
    0,    0,    0,    0,    0,    0,    0,   49,    0,    0,
    0,    0,    0,    0,    3,    0,    0,    4,    0,    0,
    0,    7,    0,    0,    9,    0,    0,    0,    0,    0,
    0,    0,   10,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    2,
    0,    0,    0,    1,
};
final static short yygindex[] = {                         0,
    0,   27,   28,   29,   30,   31,   32,    0,    0,  -16,
    0,   33,
};
final static int YYTABLESIZE=275;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         17,
   34,   27,   23,   17,   19,   48,   19,   20,   24,   22,
   44,   45,   52,   46,    1,   47,   15,   27,   28,   29,
    2,   30,   16,   31,   21,   50,    3,    4,   24,    5,
   32,   53,    2,    6,   51,   54,   48,   23,    3,    4,
   35,    5,   34,   42,   48,    6,    1,   49,    3,   36,
   37,   38,   39,   40,   41,    0,    0,   43,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   34,   27,
   23,   17,   34,   27,   19,   34,   24,   22,    0,    0,
    0,    0,    0,    0,   34,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        262,
    0,    0,    0,    0,  262,  267,    0,  272,    0,    0,
   27,   28,  274,   30,  256,   32,  257,  260,  261,  262,
  262,  264,  263,  266,  262,   42,  268,  269,  262,  271,
  273,   48,  262,  275,  265,   52,  267,  258,  268,  269,
  262,  271,  270,  259,  267,  275,    0,  262,    0,   23,
   23,   23,   23,   23,   23,   -1,   -1,   25,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  258,  258,
  258,  258,  262,  262,  258,  265,  258,  258,   -1,   -1,
   -1,   -1,   -1,   -1,  274,
};
}
final static short YYFINAL=7;
final static short YYMAXTOKEN=275;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"DCOLON","SCOLON","COLON","SEQ_OF","SET_OF","IDENT","EQ","LB",
"RB","QUOTE","BAR","PACKAGE","PREFIX","DOT","DIRECTORY","STRING","MAP","TO",
"TOP",
};
final static String yyrule[] = {
"$accept : Document",
"Document :",
"Document : error",
"Document : DefinitionList",
"DefinitionList : Definition",
"DefinitionList : ShortHand",
"DefinitionList : PrefixSpecification",
"DefinitionList : PackageSpecification",
"DefinitionList : DirectorySpecification",
"DefinitionList : TopSpecification",
"DefinitionList : DefinitionList SCOLON Definition",
"DefinitionList : DefinitionList SCOLON ShortHand",
"DefinitionList : DefinitionList SCOLON PrefixSpecification",
"DefinitionList : DefinitionList SCOLON PackageSpecification",
"DefinitionList : DefinitionList SCOLON DirectorySpecification",
"DefinitionList : DefinitionList SCOLON TopSpecification",
"PrefixSpecification : PREFIX IDENT",
"PackageSpecification : PACKAGE PackageList",
"DirectorySpecification : DIRECTORY STRING",
"TopSpecification : TOP IdentifierList",
"PackageList : IDENT",
"PackageList : PackageList DOT IDENT",
"ShortHand : IDENT EQ Type",
"Definition : IDENT DCOLON",
"Definition : IDENT DCOLON FieldList",
"FieldList : Field",
"FieldList : FieldList Field",
"Field : IDENT COLON Type",
"Type : IDENT",
"Type : QUOTE",
"Type : Type BAR Type",
"Type : LB Type RB",
"Type : SEQ_OF Type",
"Type : SET_OF Type",
"Type : MAP Type TO Type",
"IdentifierList : IDENT",
"IdentifierList : IdentifierList IDENT",
};

//#line 181 "VdmAst.y"

public AstScanner theScanner = null;

public AstDefinitions theAst = null;

public int errors = 0;

public AstParser (String fname) throws CGException
{
	theScanner = new AstScanner(this, fname);
	theAst = new AstDefinitions();
}

public AstParser (String fname, boolean debug) throws CGException
{
	theScanner = new AstScanner(this, fname);
	theAst = new AstDefinitions();
	yydebug = debug;
}

private int yylex () throws java.io.IOException
{
	return theScanner.yylex();
}

private void yyerror (String msg)
{
	System.out.println(msg + theScanner.atPosition());
	errors++;
}	
//#line 316 "AstParser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
throws java.io.IOException,jp.co.csk.vdm.toolbox.VDM.CGException
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 4:
//#line 23 "VdmAst.y"
{ AstComposite theComposite = (AstComposite) val_peek(0).obj;
	    theAst.addComposite(theComposite); }
break;
case 5:
//#line 27 "VdmAst.y"
{ AstShorthand theShorthand = (AstShorthand) val_peek(0).obj;
	    theAst.addShorthand(theShorthand); }
break;
case 10:
//#line 39 "VdmAst.y"
{ AstComposite theComposite = (AstComposite) val_peek(0).obj;
	    theAst.addComposite(theComposite); }
break;
case 11:
//#line 43 "VdmAst.y"
{ AstShorthand theShorthand = (AstShorthand) val_peek(0).obj;
	    theAst.addShorthand(theShorthand); }
break;
case 16:
//#line 58 "VdmAst.y"
{ theAst.setPrefix(val_peek(0).sval); }
break;
case 17:
//#line 63 "VdmAst.y"
{ Vector thePackageList = (Vector) val_peek(0).obj;
	    theAst.setPackage(thePackageList); }
break;
case 18:
//#line 69 "VdmAst.y"
{ String theArg = val_peek(0).sval;
        theAst.setDirectory(theArg); }
break;
case 19:
//#line 75 "VdmAst.y"
{ Vector theTopList = (Vector) val_peek(0).obj;
	    theAst.setTop(theTopList); }
break;
case 20:
//#line 81 "VdmAst.y"
{ Vector theRes = new Vector();
	    theRes.add(val_peek(0).sval);
	    yyval.obj = theRes; }
break;
case 21:
//#line 86 "VdmAst.y"
{ Vector theRes = (Vector) val_peek(2).obj;
	    theRes.add(val_peek(0).sval);
	    yyval.obj = theRes; }
break;
case 22:
//#line 93 "VdmAst.y"
{ AstType theArg = (AstType) val_peek(0).obj;
	    AstShorthand theShorthand = new AstShorthand(val_peek(2).sval,theArg);
	    yyval.obj = theShorthand; }
break;
case 23:
//#line 100 "VdmAst.y"
{ Vector theArg = new Vector();
	    AstComposite theComposite = new AstComposite(val_peek(1).sval,theArg);
	    yyval.obj = theComposite; }
break;
case 24:
//#line 105 "VdmAst.y"
{ Vector theArg = (Vector) val_peek(0).obj;
	    AstComposite theComposite = new AstComposite(val_peek(2).sval,theArg);
	    yyval.obj = theComposite; }
break;
case 25:
//#line 112 "VdmAst.y"
{ AstField theArg = (AstField) val_peek(0).obj;
	    Vector theRes = new Vector();
	    theRes.add(theArg);
	    yyval.obj = theRes; }
break;
case 26:
//#line 118 "VdmAst.y"
{ AstField theArg = (AstField) val_peek(0).obj;
	    Vector theRes = (Vector) val_peek(1).obj;
	    theRes.add(theArg);
	    yyval.obj = theRes; }
break;
case 27:
//#line 126 "VdmAst.y"
{ AstType theArg = (AstType) val_peek(0).obj;
	    AstField theField = new AstField(val_peek(2).sval,theArg);
	    yyval.obj = theField; }
break;
case 28:
//#line 133 "VdmAst.y"
{ AstTypeName theTypeName = new AstTypeName(val_peek(0).sval);
	    yyval.obj = theTypeName; }
break;
case 29:
//#line 137 "VdmAst.y"
{ AstQuotedType theQuotedType = new AstQuotedType(val_peek(0).sval);
	    yyval.obj = theQuotedType; }
break;
case 30:
//#line 141 "VdmAst.y"
{ AstType theLhs = (AstType) val_peek(2).obj;
	    AstType theRhs = (AstType) val_peek(0).obj;
	    AstUnionType theUnionType = new AstUnionType(theLhs,theRhs);
	    yyval.obj = theUnionType; }
break;
case 31:
//#line 147 "VdmAst.y"
{ AstType theArg = (AstType) val_peek(1).obj;
	    AstOptionalType theOptionalType = new AstOptionalType(theArg);
	    yyval.obj = theOptionalType; }
break;
case 32:
//#line 152 "VdmAst.y"
{ AstType theArg = (AstType) val_peek(0).obj;
	    AstSeqOfType theSeqOfType = new AstSeqOfType(theArg);
	    yyval.obj = theSeqOfType; }
break;
case 33:
//#line 157 "VdmAst.y"
{ AstType theArg = (AstType) val_peek(0).obj;
	    AstSetOfType theSetOfType = new AstSetOfType(theArg);
	    yyval.obj = theSetOfType; }
break;
case 34:
//#line 162 "VdmAst.y"
{ AstType theArg1 = (AstType) val_peek(2).obj;
	    AstType theArg2 = (AstType) val_peek(0).obj;
	    AstMapType theMapType = new AstMapType(theArg1,theArg2);
	    yyval.obj = theMapType; }
break;
case 35:
//#line 170 "VdmAst.y"
{ Vector res = new Vector();
	    res.add(val_peek(0).sval);
	    yyval.obj = res; }
break;
case 36:
//#line 175 "VdmAst.y"
{ Vector res = (Vector) val_peek(1).obj;
	    res.add(val_peek(0).sval);
	    yyval.obj = res; }
break;
//#line 596 "AstParser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public AstParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public AstParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
