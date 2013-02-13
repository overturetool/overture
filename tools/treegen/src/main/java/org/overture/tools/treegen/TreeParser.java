//### This file created by BYACC 1.8(/Java extension  1.15)
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



package org.overture.tools.treegen;



//#line 9 "TreeGen.y"
import org.overture.tools.treegen.ast.itf.*;
import org.overture.tools.treegen.ast.imp.*;
import java.util.*;
//#line 21 "TreeParser.java"




public class TreeParser
             implements TreeParserTokens
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
//public class TreeParserVal is defined in TreeParserVal.java


String   yytext;//user variable to return contextual strings
TreeParserVal yyval; //used to return semantic vals from action routines
TreeParserVal yylval;//the 'lval' (result) I got from yylex()
TreeParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new TreeParserVal[YYSTACKSIZE];
  yyval=new TreeParserVal();
  yylval=new TreeParserVal();
  valptr=-1;
}
void val_push(TreeParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
TreeParserVal val_pop()
{
  if (valptr<0)
    return new TreeParserVal();
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
TreeParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new TreeParserVal();
  return valstk[ptr];
}
final TreeParserVal dup_yyval(TreeParserVal val)
{
  TreeParserVal dup = new TreeParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    1,    1,    2,    2,    2,    2,    3,    3,
    3,    4,    4,    4,    5,    5,    5,    8,    8,    8,
    9,    9,    6,    6,    6,   10,   10,   10,   11,   11,
    7,    7,    7,   13,   13,   13,   14,   14,   14,   15,
   15,   16,   16,   12,   12,   12,   12,   12,   12,   12,
   12,
};
final static short yylen[] = {                            2,
    0,    1,    1,    2,    4,    5,    8,    9,    1,    1,
    2,    1,    1,    1,    1,    2,    3,    1,    1,    3,
    3,    3,    2,    3,    4,    1,    1,    3,    3,    8,
    1,    2,    3,    1,    1,    3,    3,    2,    3,    1,
    2,    3,    8,    1,    1,    3,    3,    3,    3,    3,
    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    4,    9,    0,    0,    0,
    0,    0,    0,   10,   12,   13,   14,    0,    5,   18,
    0,    0,   19,   34,    0,    0,   35,    0,    0,   11,
    0,    0,    0,    0,    0,    0,   26,    0,    0,   27,
    6,    0,   21,   22,   20,    0,    0,   40,    0,    0,
    0,   44,    0,    0,   45,    0,   36,    0,    0,    0,
    0,    0,   41,    0,    0,    0,    0,    0,    0,    0,
   28,    7,    0,    0,   49,   50,    0,   47,   48,   46,
    0,    8,    0,    0,    0,    0,    0,    0,    0,    0,
   30,   43,
};
final static short yydgoto[] = {                          2,
    3,    4,   13,   14,   15,   16,   17,   22,   23,   39,
   40,   56,   26,   27,   47,   48,
};
final static short yysindex[] = {                      -244,
 -219,    0, -244,    0, -255,    0,    0, -204, -215, -254,
 -248, -225, -183,    0,    0,    0,    0, -199,    0,    0,
 -205, -188,    0,    0, -258, -184,    0, -242, -173,    0,
 -170, -257, -165, -161, -112, -159,    0, -181, -167,    0,
    0, -252,    0,    0,    0, -177, -161,    0, -137, -135,
 -112,    0, -112, -112,    0, -154,    0, -112, -142, -138,
 -180, -112,    0, -112, -112, -259, -238, -230, -112, -222,
    0,    0, -136, -176,    0,    0, -112,    0,    0,    0,
 -160,    0, -141, -154, -132, -131, -133, -128, -127, -124,
    0,    0,
};
final static short yyrindex[] = {                       142,
    0,    0,  148,    0,    0,    0,    0,    0,    0, -175,
 -169,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -157,    0,    0,    0, -152,    0, -146,    0,    0,
    0,    0, -143, -241,    0, -140,    0,    0, -134,    0,
    0,    0,    0,    0,    0,    0, -198,    0,    0,    0,
    0,    0,    0,    0,    0, -192,    0,    0, -129,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, -187,
    0,    0,    0, -245,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -210,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
    0,  155,  117,  -13,    0,    0,    0,    0,  128,    0,
  104,  -19,    0,  129,    0,  120,
};
final static int YYTABLESIZE=167;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         30,
    7,   20,    8,    7,   34,    9,   10,   24,   60,   10,
   77,   69,    1,   37,   35,   42,   42,   21,   42,   38,
   38,   43,   38,   25,   44,   11,   42,   12,   11,   38,
   12,   66,   69,   67,   68,   42,   78,   42,   70,   38,
   69,   38,   74,   81,   75,   76,   79,   30,   69,   80,
   51,   51,    5,   51,   18,   51,   19,   84,   28,   51,
   31,   51,   39,   39,   51,   39,   51,   32,   37,   37,
   51,   37,   51,   29,   29,   33,   29,   29,   10,   36,
   73,   10,   39,   58,   39,   15,   15,   62,   37,   83,
   37,   31,   31,   29,   69,   29,   59,   11,   41,   12,
   11,   42,   12,   16,   16,   15,   21,   15,   32,   32,
   46,   31,   25,   31,   23,   23,   69,   17,   17,   85,
   33,   33,   64,   16,   65,   16,   24,   24,   32,   38,
   32,   25,   25,   72,   23,   82,   23,   17,   86,   17,
   33,    1,   33,   87,   88,   89,   24,    2,   24,   91,
   90,   25,   92,   25,   49,   50,   51,    6,   61,   52,
   45,   53,   71,   54,   57,   55,   63,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         13,
  256,  256,  258,  256,  263,  261,  262,  256,  261,  262,
  270,  271,  257,  256,  273,  261,  262,  272,  264,  261,
  262,  279,  264,  272,  282,  281,  272,  283,  281,  272,
  283,   51,  271,   53,   54,  281,  275,  283,   58,  281,
  271,  283,   62,  266,   64,   65,  277,   61,  271,   69,
  261,  262,  272,  264,  259,  266,  272,   77,  284,  270,
  260,  272,  261,  262,  275,  264,  277,  273,  261,  262,
  281,  264,  283,  261,  262,  264,  264,  261,  262,  264,
  261,  262,  281,  265,  283,  261,  262,  265,  281,  266,
  283,  261,  262,  281,  271,  283,  264,  281,  272,  283,
  281,  272,  283,  261,  262,  281,  272,  283,  261,  262,
  272,  281,  272,  283,  261,  262,  271,  261,  262,  280,
  261,  262,  260,  281,  260,  283,  261,  262,  281,  272,
  283,  261,  262,  272,  281,  272,  283,  281,  280,  283,
  281,    0,  283,  276,  276,  279,  281,    0,  283,  277,
  279,  281,  277,  283,  267,  268,  269,    3,   42,  272,
   33,  274,   59,  276,   36,  278,   47,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=284;
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
null,null,null,"CLASS","IS","SUBCLASS","OF","END","VALUES","DCOLON","SCOLON",
"COLON","ASSIGN","SEQ","SET","MAP","TO","BAR","IDENT","EQ","LB","RB","LP","RP",
"QUOTE","STRING","MK_TOKEN","TYPES","BOOLEAN","INSTANCE","VARIABLES",
};
final static String yyrule[] = {
"$accept : Document",
"Document :",
"Document : ClassDefinitionList",
"ClassDefinitionList : ClassDefinition",
"ClassDefinitionList : ClassDefinitionList ClassDefinition",
"ClassDefinition : CLASS IDENT END IDENT",
"ClassDefinition : CLASS IDENT DefinitionList END IDENT",
"ClassDefinition : CLASS IDENT IS SUBCLASS OF IDENT END IDENT",
"ClassDefinition : CLASS IDENT IS SUBCLASS OF IDENT DefinitionList END IDENT",
"DefinitionList : error",
"DefinitionList : DefinitionBlock",
"DefinitionList : DefinitionList DefinitionBlock",
"DefinitionBlock : ValueDefinitions",
"DefinitionBlock : InstanceVariableDefinitions",
"DefinitionBlock : TypeDefinitions",
"ValueDefinitions : VALUES",
"ValueDefinitions : VALUES ValueDefinitionList",
"ValueDefinitions : VALUES ValueDefinitionList SCOLON",
"ValueDefinitionList : error",
"ValueDefinitionList : ValueDefinition",
"ValueDefinitionList : ValueDefinitionList SCOLON ValueDefinition",
"ValueDefinition : IDENT EQ STRING",
"ValueDefinition : IDENT EQ BOOLEAN",
"InstanceVariableDefinitions : INSTANCE VARIABLES",
"InstanceVariableDefinitions : INSTANCE VARIABLES InstanceVariableDefinitionList",
"InstanceVariableDefinitions : INSTANCE VARIABLES InstanceVariableDefinitionList SCOLON",
"InstanceVariableDefinitionList : error",
"InstanceVariableDefinitionList : InstanceVariable",
"InstanceVariableDefinitionList : InstanceVariableDefinitionList SCOLON InstanceVariable",
"InstanceVariable : IDENT COLON Type",
"InstanceVariable : IDENT COLON Type ASSIGN MK_TOKEN LP STRING RP",
"TypeDefinitions : TYPES",
"TypeDefinitions : TYPES TypeDefinitionList",
"TypeDefinitions : TYPES TypeDefinitionList SCOLON",
"TypeDefinitionList : error",
"TypeDefinitionList : TypeDefinition",
"TypeDefinitionList : TypeDefinitionList SCOLON TypeDefinition",
"TypeDefinition : IDENT EQ Type",
"TypeDefinition : IDENT DCOLON",
"TypeDefinition : IDENT DCOLON FieldList",
"FieldList : Field",
"FieldList : FieldList Field",
"Field : IDENT COLON Type",
"Field : IDENT COLON Type ASSIGN MK_TOKEN LP STRING RP",
"Type : IDENT",
"Type : QUOTE",
"Type : Type BAR Type",
"Type : LB Type RB",
"Type : LP Type RP",
"Type : SEQ OF Type",
"Type : SET OF Type",
"Type : MAP Type TO Type",
};

//#line 525 "TreeGen.y"

//
// PARSER CLASS CONSTRUCTORS AND OPERATIONS
//

// keep a reference to the scanner
private TreeScanner theScanner;

// keep a reference to the file name
private String theFileName;

// keep a list of reserved value names
private static HashSet<String> values;

// keep track of the current class definition
private List <ITreeGenAstClassDefinition> tgacdl;

static {
	values = new HashSet<String>();
	values.add("package");				// name of the Java package
	values.add("javadir");				// top-level directory where Java is generated
	values.add("vdmppdir");				// top-level directory where VDM++ is generated
	values.add("toplevel");				// top-level entry point in the abstract syntax
	values.add("split");				// option to split the VPP files
}

// keep track of the number of parse errors
public int errors =0;

// constructor for the parser
public TreeParser (String fname)
{
	// initialize the scanner
	theScanner = new TreeScanner(this, fname);
	
	// consistency check
	assert (theScanner != null);
	
	// reset parser to debug mode
	yydebug = false;
	
	// remember the file name
	theFileName = fname;
}

// constructor for the parser
public TreeParser (String fname, boolean debug)
{
	// initialize the scanner
	theScanner = new TreeScanner(this, fname);
	
	// consistency check
	assert (theScanner != null);
	
	// set parser to debug mode
	yydebug = debug;
	
	// remember the file name
	theFileName = fname;
}

public List<ITreeGenAstClassDefinition> parse ()
{
	// initialize the top-level class definition
	tgacdl = new Vector<ITreeGenAstClassDefinition>();
	
	// call the embedded parse routine
	try {
		yyparse();
	}

	// handle any IO errors found during read	
	catch (java.io.IOException e) {
		yyerror (e.getMessage());
	}
	
	// announce the result
	System.out.println(errors + " error(s) found in file '" + theFileName + "'");
	
	// return the result
	return tgacdl;
}

// the internal function that calls the scanner
private int yylex () throws java.io.IOException
{
	// consistency check
	assert (theScanner != null);
	
	// call the scanner
	return theScanner.yylex();
}

// the internal function that displays the parse errors found
private void yyerror (String errmsg)
{
	// consistency check
	assert (theScanner != null);
	
	// output to stdout
	System.out.println(errmsg + theScanner.atPosition());
	
	// increase the error counter
	errors++;
}
//#line 410 "TreeParser.java"
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
throws java.io.IOException
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
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
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
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
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
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
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 38 "TreeGen.y"
{
	  	yyerror ("no class definition found");
	  }
break;
case 2:
//#line 43 "TreeGen.y"
{
	  }
break;
case 3:
//#line 49 "TreeGen.y"
{
	  	/* add parsed class definition to the list*/
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) val_peek(0).obj;
	  	tgacdl.add(tgacd);
	  }
break;
case 4:
//#line 56 "TreeGen.y"
{
	  	/* add parsed class definition to the list*/
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) val_peek(0).obj;
	  	tgacdl.add(tgacd);
	  }
break;
case 5:
//#line 69 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(2).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName(val_peek(2).sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(new Vector<ITreeGenAstDefinitions>());
	  	yyval.obj = tgacd;
	  }
break;
case 6:
//#line 84 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(3).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	List<ITreeGenAstDefinitions> tgadl = (Vector<ITreeGenAstDefinitions>) val_peek(2).obj;
	  	tgacd.setClassName(val_peek(3).sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(tgadl);
	  	yyval.obj = tgacd;
	  }
break;
case 7:
//#line 100 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(6).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName(val_peek(6).sval);
	  	tgacd.setSuperClass(val_peek(2).sval);
	  	tgacd.setDefs(new Vector<ITreeGenAstDefinitions>());
	  	yyval.obj = tgacd;
	  }
break;
case 8:
//#line 115 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(7).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	List <ITreeGenAstDefinitions> tgadl = (Vector<ITreeGenAstDefinitions>) val_peek(2).obj;
	  	tgacd.setClassName(val_peek(7).sval);
	  	tgacd.setSuperClass(val_peek(3).sval);
	  	tgacd.setDefs(tgadl);
	  	yyval.obj = tgacd;
	  }
break;
case 9:
//#line 133 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>();
	  }
break;
case 10:
//#line 139 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 11:
//#line 144 "TreeGen.y"
{
	  	/* concatenate the lists*/
	  	List<ITreeGenAstDefinitions> lhs = (Vector<ITreeGenAstDefinitions>) val_peek(1).obj;
	  	List<ITreeGenAstDefinitions> rhs = (Vector<ITreeGenAstDefinitions>) val_peek(0).obj;
	  	lhs.addAll(rhs);	  	
	  	yyval.obj = lhs;
	  }
break;
case 12:
//#line 155 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 13:
//#line 160 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 14:
//#line 165 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 15:
//#line 176 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>();
	  }
break;
case 16:
//#line 182 "TreeGen.y"
{
	  	/* pass the value definition list upwards*/
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 17:
//#line 188 "TreeGen.y"
{
	  	/* pass the value definition list upwards*/
	  	yyval.obj = val_peek(1).obj;
	  }
break;
case 18:
//#line 196 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>();
	  }
break;
case 19:
//#line 202 "TreeGen.y"
{
	  	/* create a new list and add the value definition to the list*/
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 20:
//#line 211 "TreeGen.y"
{
	  	/* retrieve the partial list and add the value definition to the list*/
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 21:
//#line 222 "TreeGen.y"
{
	  	/* check whether the value definition is allowed*/
	  	if (!values.contains(val_peek(2).sval)) {
	  		/* flag illegal value setting*/
	  		yyerror("value '" + val_peek(2).sval + "' is not allowed");
	  	}
	  	
	  	/* create the value definition*/
	  	TreeGenAstValueDefinition tgavd = new TreeGenAstValueDefinition();
	  	tgavd.setKey(val_peek(2).sval);
	  	tgavd.setValue(val_peek(0).sval);
	  	yyval.obj = tgavd;
	  }
break;
case 22:
//#line 237 "TreeGen.y"
{
	  	/* check whether the value definition is allowed*/
	  	if (!values.contains(val_peek(2).sval)) {
	  		/* flag illegal value setting*/
	  		yyerror("value '" + val_peek(2).sval + "' is not allowed");
	  	}
	  	
	  	/* create the value definition*/
	  	TreeGenAstValueDefinition tgavd = new TreeGenAstValueDefinition();
	  	tgavd.setKey(val_peek(2).sval);
	  	tgavd.setValue(val_peek(0).sval);
	  	yyval.obj = tgavd;
	  }
break;
case 23:
//#line 258 "TreeGen.y"
{
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>();
	  }
break;
case 24:
//#line 263 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 25:
//#line 268 "TreeGen.y"
{
	  	yyval.obj = val_peek(1).obj;
	  }
break;
case 26:
//#line 275 "TreeGen.y"
{
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>();
	  }
break;
case 27:
//#line 280 "TreeGen.y"
{
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 28:
//#line 288 "TreeGen.y"
{
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 29:
//#line 298 "TreeGen.y"
{
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgavd.setKey(val_peek(2).sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue(new String());
	  	yyval.obj = tgavd;
	  }
break;
case 30:
//#line 309 "TreeGen.y"
{
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) val_peek(5).obj;
	  	tgavd.setKey(val_peek(7).sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue(val_peek(1).sval);
	  	yyval.obj = tgavd;
	  }
break;
case 31:
//#line 325 "TreeGen.y"
{
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>(); 
	  }
break;
case 32:
//#line 330 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 33:
//#line 335 "TreeGen.y"
{
	  	yyval.obj = val_peek(1).obj;
	  }
break;
case 34:
//#line 342 "TreeGen.y"
{
	  	yyval.obj = new Vector<ITreeGenAstDefinitions>(); 
	  }
break;
case 35:
//#line 347 "TreeGen.y"
{
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) val_peek(0).obj;
	  	res.add(tpd);
	  	yyval.obj = res; 
	  }
break;
case 36:
//#line 355 "TreeGen.y"
{
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) val_peek(0).obj;
	  	res.add(tpd);
	  	yyval.obj = res; 
	  }
break;
case 37:
//#line 370 "TreeGen.y"
{
	  	TreeGenAstShorthandDefinition tgash = new TreeGenAstShorthandDefinition();
	  	TreeGenAstTypeSpecification tps = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgash.setShorthandName(val_peek(2).sval);
	  	tgash.setType(tps);
	  	yyval.obj = tgash;
	  }
break;
case 38:
//#line 383 "TreeGen.y"
{
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	List <ITreeGenAstCompositeField> cfv = new Vector<ITreeGenAstCompositeField>();
	  	tgac.setCompositeName(val_peek(1).sval);
	  	tgac.setFields(cfv);
		yyval.obj = tgac;	  	
      }
break;
case 39:
//#line 396 "TreeGen.y"
{
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	List <ITreeGenAstCompositeField> cfv = (Vector<ITreeGenAstCompositeField>) val_peek(0).obj;
	  	tgac.setCompositeName(val_peek(2).sval);
	  	tgac.setFields(cfv);
		yyval.obj = tgac;	  	
	  }
break;
case 40:
//#line 407 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) val_peek(0).obj;
	  	List <ITreeGenAstCompositeField> res = new Vector<ITreeGenAstCompositeField>();
	  	res.add(tgacf);
	  	yyval.obj = res;
	  }
break;
case 41:
//#line 415 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) val_peek(0).obj;
	  	List <ITreeGenAstCompositeField> res = (Vector<ITreeGenAstCompositeField>) val_peek(1).obj;
	  	res.add(tgacf);
	  	yyval.obj = res;
	  }
break;
case 42:
//#line 425 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = new TreeGenAstCompositeField();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgacf.setFieldName(val_peek(2).sval);
	  	tgacf.setType(et);
	  	tgacf.setValue(new String());
	  	yyval.obj = tgacf;
	  }
break;
case 43:
//#line 444 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = new TreeGenAstCompositeField();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(5).obj;
	  	tgacf.setFieldName(val_peek(7).sval);
	  	tgacf.setType(et);
	  	tgacf.setValue(val_peek(1).sval);
	  	yyval.obj = tgacf;
	  }
break;
case 44:
//#line 456 "TreeGen.y"
{ 
	    TreeGenAstTypeName tgatn = new TreeGenAstTypeName();
	  	tgatn.setName(val_peek(0).sval);
	  	yyval.obj = tgatn;
	  }
break;
case 45:
//#line 463 "TreeGen.y"
{
	  	TreeGenAstQuotedType tgaqt = new TreeGenAstQuotedType();
	  	tgaqt.setQuote(val_peek(0).sval);
	  	yyval.obj = tgaqt;
	  }
break;
case 46:
//#line 470 "TreeGen.y"
{
	  	TreeGenAstUnionType tgaut = new TreeGenAstUnionType();
	  	TreeGenAstTypeSpecification lhs = (TreeGenAstTypeSpecification) val_peek(2).obj; 
	  	TreeGenAstTypeSpecification rhs = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgaut.setLhs(lhs);
	  	tgaut.setRhs(rhs);
	  	yyval.obj = tgaut; 
	  }
break;
case 47:
//#line 480 "TreeGen.y"
{
	  	TreeGenAstOptionalType tgaot = new TreeGenAstOptionalType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(1).obj;
	  	tgaot.setType(et);
	  	yyval.obj = tgaot;
	  }
break;
case 48:
//#line 488 "TreeGen.y"
{
	  	yyval.obj = val_peek(2).obj;
	  }
break;
case 49:
//#line 493 "TreeGen.y"
{
	  	TreeGenAstSeqType tgast = new TreeGenAstSeqType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgast.setType(et);
	  	yyval.obj = tgast;
	  }
break;
case 50:
//#line 501 "TreeGen.y"
{
	  	TreeGenAstSetType tgast = new TreeGenAstSetType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgast.setType(et);
	  	yyval.obj = tgast;
	  }
break;
case 51:
//#line 509 "TreeGen.y"
{
	  	TreeGenAstMapType tgamt = new TreeGenAstMapType();
	  	TreeGenAstTypeSpecification etd = (TreeGenAstTypeSpecification) val_peek(2).obj;
	  	TreeGenAstTypeSpecification etr = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgamt.setDomType(etd);
	  	tgamt.setRngType(etr);
	  	yyval.obj = tgamt;
	  }
break;
//#line 1020 "TreeParser.java"
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
public TreeParser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public TreeParser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
