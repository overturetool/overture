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



package nl.marcelverhoef.treegen;



//#line 9 "TreeGen.y"
import nl.marcelverhoef.treegen.ast.imp.*;
//#line 19 "TreeParser.java"




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
    3,    4,    4,    4,    5,    5,    8,    8,    8,    9,
    6,    6,   10,   10,   10,   11,   11,    7,    7,   13,
   13,   13,   14,   14,   14,   15,   15,   16,   12,   12,
   12,   12,   12,   12,   12,   12,
};
final static short yylen[] = {                            2,
    0,    1,    1,    2,    4,    5,    8,    9,    1,    1,
    2,    1,    1,    1,    1,    2,    1,    1,    3,    3,
    2,    3,    1,    1,    3,    3,    5,    1,    2,    1,
    1,    3,    3,    2,    3,    1,    2,    3,    1,    1,
    3,    3,    3,    3,    3,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    4,    9,    0,    0,    0,
    0,    0,    0,   10,   12,   13,   14,    0,    5,   17,
    0,    0,   18,   30,    0,    0,   31,    0,    0,   11,
    0,    0,    0,    0,    0,    0,   23,    0,    0,   24,
    6,    0,   20,   19,    0,    0,   36,    0,    0,    0,
   39,    0,    0,   40,    0,   32,    0,    0,    0,    0,
    0,   37,    0,    0,    0,    0,    0,    0,    0,   25,
    7,    0,    0,   44,   45,    0,   42,   43,   41,    0,
    8,    0,   27,
};
final static short yydgoto[] = {                          2,
    3,    4,   13,   14,   15,   16,   17,   22,   23,   39,
   40,   55,   26,   27,   46,   47,
};
final static short yysindex[] = {                      -244,
 -266,    0, -244,    0, -251,    0,    0, -228, -252, -254,
 -247, -237, -197,    0,    0,    0,    0, -207,    0,    0,
 -215, -177,    0,    0, -211, -161,    0, -239, -165,    0,
 -164, -157, -156, -149, -148, -147,    0, -150, -137,    0,
    0, -240,    0,    0, -136, -149,    0, -129, -128, -148,
    0, -148, -148,    0, -138,    0, -148, -135, -134, -188,
 -148,    0, -148, -148, -159, -169, -227, -148, -200,    0,
    0, -133, -138,    0,    0, -148,    0,    0,    0, -145,
    0, -138,    0,
};
final static short yyrindex[] = {                       135,
    0,    0,  136,    0,    0,    0,    0,    0,    0, -184,
 -180,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, -176,    0,    0,    0, -171,    0, -167,    0,    0,
    0,    0,    0, -213,    0,    0,    0,    0, -163,    0,
    0,    0,    0,    0,    0, -205,    0,    0,    0,    0,
    0,    0,    0,    0, -201,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, -192,    0,
    0,    0, -226,    0,    0,    0,    0,    0,    0,    0,
    0, -238,    0,
};
final static short yygindex[] = {                         0,
    0,  137,   99,  -13,    0,    0,    0,    0,  109,    0,
   85,  -49,    0,  108,    0,  100,
};
final static int YYTABLESIZE=146;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         30,
   65,   20,   66,   67,    7,    5,    8,   69,   24,    9,
   10,   73,    1,   74,   75,    7,   37,   21,   79,   19,
   59,   10,   46,   46,   25,   46,   82,   46,   11,   12,
   18,   46,   38,   46,   38,   38,   46,   38,   46,   11,
   12,   46,   46,   68,   28,   38,   30,   34,   34,   78,
   34,   34,   31,   38,   38,   35,   35,   32,   35,   33,
   33,   35,   33,   29,   10,   80,   34,   34,   26,   26,
   68,   26,   72,   10,   35,   35,   15,   15,   33,   33,
   28,   28,   11,   12,   16,   16,   33,   26,   26,   29,
   29,   11,   12,   21,   21,   15,   15,   22,   22,   28,
   28,   68,   36,   16,   16,   77,   41,   42,   29,   29,
   76,   68,   21,   21,   57,   21,   22,   22,   48,   49,
   50,   43,   45,   51,   25,   52,   58,   53,   61,   54,
   63,   64,   68,   83,    1,    2,   38,   71,   81,    6,
   60,   44,   70,   56,    0,   62,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         13,
   50,  256,   52,   53,  256,  272,  258,   57,  256,  261,
  262,   61,  257,   63,   64,  256,  256,  272,   68,  272,
  261,  262,  261,  262,  272,  264,   76,  266,  280,  281,
  259,  270,  272,  272,  261,  262,  275,  264,  277,  280,
  281,  280,  281,  271,  282,  272,   60,  261,  262,  277,
  264,  263,  260,  280,  281,  261,  262,  273,  264,  261,
  262,  273,  264,  261,  262,  266,  280,  281,  261,  262,
  271,  264,  261,  262,  280,  281,  261,  262,  280,  281,
  261,  262,  280,  281,  261,  262,  264,  280,  281,  261,
  262,  280,  281,  261,  262,  280,  281,  261,  262,  280,
  281,  271,  264,  280,  281,  275,  272,  272,  280,  281,
  270,  271,  280,  281,  265,  272,  280,  281,  267,  268,
  269,  279,  272,  272,  272,  274,  264,  276,  265,  278,
  260,  260,  271,  279,    0,    0,  272,  272,  272,    3,
   42,   33,   58,   36,   -1,   46,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=282;
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
"QUOTE","STRING","TYPES","INSTANCE","VARIABLES",
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
"ValueDefinitionList : error",
"ValueDefinitionList : ValueDefinition",
"ValueDefinitionList : ValueDefinitionList SCOLON ValueDefinition",
"ValueDefinition : IDENT EQ STRING",
"InstanceVariableDefinitions : INSTANCE VARIABLES",
"InstanceVariableDefinitions : INSTANCE VARIABLES InstanceVariableDefinitionList",
"InstanceVariableDefinitionList : error",
"InstanceVariableDefinitionList : InstanceVariable",
"InstanceVariableDefinitionList : InstanceVariableDefinitionList SCOLON InstanceVariable",
"InstanceVariable : IDENT COLON Type",
"InstanceVariable : IDENT COLON Type ASSIGN STRING",
"TypeDefinitions : TYPES",
"TypeDefinitions : TYPES TypeDefinitionList",
"TypeDefinitionList : error",
"TypeDefinitionList : TypeDefinition",
"TypeDefinitionList : TypeDefinitionList SCOLON TypeDefinition",
"TypeDefinition : IDENT EQ Type",
"TypeDefinition : IDENT DCOLON",
"TypeDefinition : IDENT DCOLON FieldList",
"FieldList : Field",
"FieldList : FieldList Field",
"Field : IDENT COLON Type",
"Type : IDENT",
"Type : QUOTE",
"Type : Type BAR Type",
"Type : LB Type RB",
"Type : LP Type RP",
"Type : SEQ OF Type",
"Type : SET OF Type",
"Type : MAP Type TO Type",
};

//#line 472 "TreeGen.y"

//
// PARSER CLASS CONSTRUCTORS AND OPERATIONS
//

// keep a reference to the scanner
private TreeScanner theScanner;

// keep a reference to the file name
private String theFileName;

// keep a list of reserved value names
private static java.util.HashSet<String> values;

// keep track of the current class definition
private java.util.Vector<TreeGenAstClassDefinition> tgacdl;

static {
	values = new java.util.HashSet<String>();
	values.add("package");
	values.add("directory");
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

public java.util.List<TreeGenAstClassDefinition> parse ()
{
	// initialize the top-level class definition
	tgacdl = new java.util.Vector<TreeGenAstClassDefinition>();
	
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
//#line 389 "TreeParser.java"
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
//#line 36 "TreeGen.y"
{
	  	yyerror ("no class definition found");
	  }
break;
case 2:
//#line 41 "TreeGen.y"
{
	  }
break;
case 3:
//#line 47 "TreeGen.y"
{
	  	/* add parsed class definition to the list*/
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) val_peek(0).obj;
	  	tgacdl.add(tgacd);
	  }
break;
case 4:
//#line 54 "TreeGen.y"
{
	  	/* add parsed class definition to the list*/
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) val_peek(0).obj;
	  	tgacdl.add(tgacd);
	  }
break;
case 5:
//#line 67 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(2).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName(val_peek(2).sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(new java.util.Vector<TreeGenAstDefinitions>());
	  	yyval.obj = tgacd;
	  }
break;
case 6:
//#line 82 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(3).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	java.util.Vector<TreeGenAstDefinitions> tgadl = (java.util.Vector<TreeGenAstDefinitions>) val_peek(2).obj;
	  	tgacd.setClassName(val_peek(3).sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(tgadl);
	  	yyval.obj = tgacd;
	  }
break;
case 7:
//#line 98 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(6).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName(val_peek(6).sval);
	  	tgacd.setSuperClass(val_peek(2).sval);
	  	tgacd.setDefs(new java.util.Vector<TreeGenAstDefinitions>());
	  	yyval.obj = tgacd;
	  }
break;
case 8:
//#line 113 "TreeGen.y"
{
	  	/* check whether class names are identical*/
	  	if (val_peek(7).sval.compareTo(val_peek(0).sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	/* create the class definition*/
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	java.util.Vector<TreeGenAstDefinitions> tgadl = (java.util.Vector<TreeGenAstDefinitions>) val_peek(2).obj;
	  	tgacd.setClassName(val_peek(7).sval);
	  	tgacd.setSuperClass(val_peek(3).sval);
	  	tgacd.setDefs(tgadl);
	  	yyval.obj = tgacd;
	  }
break;
case 9:
//#line 131 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
break;
case 10:
//#line 137 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 11:
//#line 142 "TreeGen.y"
{
	  	/* concatenate the lists*/
	  	java.util.Vector<TreeGenAstDefinitions> lhs = (java.util.Vector<TreeGenAstDefinitions>) val_peek(1).obj;
	  	java.util.Vector<TreeGenAstDefinitions> rhs = (java.util.Vector<TreeGenAstDefinitions>) val_peek(0).obj;
	  	lhs.addAll(rhs);	  	
	  	yyval.obj = lhs;
	  }
break;
case 12:
//#line 153 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 13:
//#line 158 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 14:
//#line 163 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 15:
//#line 174 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
break;
case 16:
//#line 180 "TreeGen.y"
{
	  	/* pass the value definition list upwards*/
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 17:
//#line 188 "TreeGen.y"
{
	  	/* pass an empty list upwards*/
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
break;
case 18:
//#line 194 "TreeGen.y"
{
	  	/* create a new list and add the value definition to the list*/
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 19:
//#line 203 "TreeGen.y"
{
	  	/* retrieve the partial list and add the value definition to the list*/
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 20:
//#line 214 "TreeGen.y"
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
case 21:
//#line 235 "TreeGen.y"
{
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
break;
case 22:
//#line 240 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 23:
//#line 247 "TreeGen.y"
{
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
break;
case 24:
//#line 252 "TreeGen.y"
{
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 25:
//#line 260 "TreeGen.y"
{
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) val_peek(0).obj;
	  	res.add(tgavd);
	  	yyval.obj = res;
	  }
break;
case 26:
//#line 270 "TreeGen.y"
{
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgavd.setKey(val_peek(2).sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue(new String());
	  	yyval.obj = tgavd;
	  }
break;
case 27:
//#line 281 "TreeGen.y"
{
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) val_peek(2).obj;
	  	tgavd.setKey(val_peek(4).sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue(val_peek(0).sval);
	  	yyval.obj = tgavd;
	  }
break;
case 28:
//#line 297 "TreeGen.y"
{
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>(); 
	  }
break;
case 29:
//#line 302 "TreeGen.y"
{
	  	yyval.obj = val_peek(0).obj;
	  }
break;
case 30:
//#line 309 "TreeGen.y"
{
	  	yyval.obj = new java.util.Vector<TreeGenAstDefinitions>(); 
	  }
break;
case 31:
//#line 314 "TreeGen.y"
{
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) val_peek(0).obj;
	  	res.add(tpd);
	  	yyval.obj = res; 
	  }
break;
case 32:
//#line 322 "TreeGen.y"
{
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) val_peek(2).obj;
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) val_peek(0).obj;
	  	res.add(tpd);
	  	yyval.obj = res; 
	  }
break;
case 33:
//#line 337 "TreeGen.y"
{
	  	TreeGenAstShorthandDefinition tgash = new TreeGenAstShorthandDefinition();
	  	TreeGenAstTypeSpecification tps = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgash.setShorthandName(val_peek(2).sval);
	  	tgash.setType(tps);
	  	yyval.obj = tgash;
	  }
break;
case 34:
//#line 350 "TreeGen.y"
{
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	java.util.Vector<TreeGenAstCompositeField> cfv = new java.util.Vector<TreeGenAstCompositeField>();
	  	tgac.setCompositeName(val_peek(1).sval);
	  	tgac.setFields(cfv);
		yyval.obj = tgac;	  	
      }
break;
case 35:
//#line 363 "TreeGen.y"
{
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	java.util.Vector<TreeGenAstCompositeField> cfv = (java.util.Vector<TreeGenAstCompositeField>) val_peek(0).obj;
	  	tgac.setCompositeName(val_peek(2).sval);
	  	tgac.setFields(cfv);
		yyval.obj = tgac;	  	
	  }
break;
case 36:
//#line 374 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) val_peek(0).obj;
	  	java.util.Vector<TreeGenAstCompositeField> res = new java.util.Vector<TreeGenAstCompositeField>();
	  	res.add(tgacf);
	  	yyval.obj = res;
	  }
break;
case 37:
//#line 382 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) val_peek(0).obj;
	  	java.util.Vector<TreeGenAstCompositeField> res = (java.util.Vector<TreeGenAstCompositeField>) val_peek(1).obj;
	  	res.add(tgacf);
	  	yyval.obj = res;
	  }
break;
case 38:
//#line 392 "TreeGen.y"
{
	  	TreeGenAstCompositeField tgacf = new TreeGenAstCompositeField();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgacf.setFieldName(val_peek(2).sval);
	  	tgacf.setType(et);
	  	yyval.obj = tgacf;
	  }
break;
case 39:
//#line 403 "TreeGen.y"
{ 
	    TreeGenAstTypeName tgatn = new TreeGenAstTypeName();
	  	tgatn.setName(val_peek(0).sval);
	  	yyval.obj = tgatn;
	  }
break;
case 40:
//#line 410 "TreeGen.y"
{
	  	TreeGenAstQuotedType tgaqt = new TreeGenAstQuotedType();
	  	tgaqt.setQuote(val_peek(0).sval);
	  	yyval.obj = tgaqt;
	  }
break;
case 41:
//#line 417 "TreeGen.y"
{
	  	TreeGenAstUnionType tgaut = new TreeGenAstUnionType();
	  	TreeGenAstTypeSpecification lhs = (TreeGenAstTypeSpecification) val_peek(2).obj; 
	  	TreeGenAstTypeSpecification rhs = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgaut.setLhs(lhs);
	  	tgaut.setRhs(rhs);
	  	yyval.obj = tgaut; 
	  }
break;
case 42:
//#line 427 "TreeGen.y"
{
	  	TreeGenAstOptionalType tgaot = new TreeGenAstOptionalType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(1).obj;
	  	tgaot.setType(et);
	  	yyval.obj = tgaot;
	  }
break;
case 43:
//#line 435 "TreeGen.y"
{
	  	yyval.obj = val_peek(2).obj;
	  }
break;
case 44:
//#line 440 "TreeGen.y"
{
	  	TreeGenAstSeqType tgast = new TreeGenAstSeqType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgast.setType(et);
	  	yyval.obj = tgast;
	  }
break;
case 45:
//#line 448 "TreeGen.y"
{
	  	TreeGenAstSetType tgast = new TreeGenAstSetType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgast.setType(et);
	  	yyval.obj = tgast;
	  }
break;
case 46:
//#line 456 "TreeGen.y"
{
	  	TreeGenAstMapType tgamt = new TreeGenAstMapType();
	  	TreeGenAstTypeSpecification etd = (TreeGenAstTypeSpecification) val_peek(2).obj;
	  	TreeGenAstTypeSpecification etr = (TreeGenAstTypeSpecification) val_peek(0).obj;
	  	tgamt.setDomType(etd);
	  	tgamt.setRngType(etr);
	  	yyval.obj = tgamt;
	  }
break;
//#line 952 "TreeParser.java"
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
