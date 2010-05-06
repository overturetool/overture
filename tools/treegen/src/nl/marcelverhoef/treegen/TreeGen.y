//
// TREE DESCRIPTION LANGUAGE - GRAMMAR
//
// input file for the byaccj parser generator version 1.15
// 

// java imports required for the generated parser semantics actions
%{
import nl.marcelverhoef.treegen.ast.imp.*;
%}

// definition of the parser tokens
%token CLASS IS SUBCLASS OF END VALUES
%token DCOLON SCOLON COLON ASSIGN
%token SEQ SET MAP OF TO BAR
%token IDENT EQ LB RB LP RP
%token QUOTE STRING
%token TYPES
%token INSTANCE VARIABLES

// operator precedence and associativity	
%left MAP TO
%left SEQ SET OF BAR

// top-level grammar entry
%start Document

%%

//
// START OF GRAMMAR
//

Document :
	  /* empty file is supplied */
	  {
	  	yyerror ("no class definition found");
	  }
	  
	| ClassDefinitionList
	  {
	  }
;

ClassDefinitionList:
	  ClassDefinition
	  {
	  	// add parsed class definition to the list
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) $1.obj;
	  	tgacdl.add(tgacd);
	  }
	  
	| ClassDefinitionList ClassDefinition
	  {
	  	// add parsed class definition to the list
	  	TreeGenAstClassDefinition tgacd = (TreeGenAstClassDefinition) $2.obj;
	  	tgacdl.add(tgacd);
	  }
;

//
// CLASS DEFINITIONS
//

ClassDefinition:
	  CLASS IDENT END IDENT
	  {
	  	// check whether class names are identical
	  	if ($2.sval.compareTo($4.sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	// create the class definition
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName($2.sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(new java.util.Vector<TreeGenAstDefinitions>());
	  	$$.obj = tgacd;
	  }
	  
	| CLASS IDENT DefinitionList END IDENT
	  {
	  	// check whether class names are identical
	  	if ($2.sval.compareTo($5.sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	// create the class definition
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	java.util.Vector<TreeGenAstDefinitions> tgadl = (java.util.Vector<TreeGenAstDefinitions>) $3.obj;
	  	tgacd.setClassName($2.sval);
	  	tgacd.setSuperClass(new String());
	  	tgacd.setDefs(tgadl);
	  	$$.obj = tgacd;
	  }
	  
	| CLASS IDENT IS SUBCLASS OF IDENT END IDENT
	  {
	  	// check whether class names are identical
	  	if ($2.sval.compareTo($8.sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	// create the class definition
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	tgacd.setClassName($2.sval);
	  	tgacd.setSuperClass($6.sval);
	  	tgacd.setDefs(new java.util.Vector<TreeGenAstDefinitions>());
	  	$$.obj = tgacd;
	  }
	  
	| CLASS IDENT IS SUBCLASS OF IDENT DefinitionList END IDENT
	  {
	  	// check whether class names are identical
	  	if ($2.sval.compareTo($9.sval) != 0) {
	  		yyerror("class name is not identical");
	  	}
	  	
	  	// create the class definition
	  	TreeGenAstClassDefinition tgacd = new TreeGenAstClassDefinition();
	  	java.util.Vector<TreeGenAstDefinitions> tgadl = (java.util.Vector<TreeGenAstDefinitions>) $7.obj;
	  	tgacd.setClassName($2.sval);
	  	tgacd.setSuperClass($6.sval);
	  	tgacd.setDefs(tgadl);
	  	$$.obj = tgacd;
	  }
;

DefinitionList:
	  error
	  {
	  	// pass an empty list upwards
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
	  
	| DefinitionBlock
	  {
	  	$$.obj = $1.obj;
	  }
	  
	| DefinitionList DefinitionBlock
	  {
	  	// concatenate the lists
	  	java.util.Vector<TreeGenAstDefinitions> lhs = (java.util.Vector<TreeGenAstDefinitions>) $1.obj;
	  	java.util.Vector<TreeGenAstDefinitions> rhs = (java.util.Vector<TreeGenAstDefinitions>) $2.obj;
	  	lhs.addAll(rhs);	  	
	  	$$.obj = lhs;
	  }
;

DefinitionBlock:
	  ValueDefinitions
	  {
	  	$$.obj = $1.obj;
	  }
	  
	| InstanceVariableDefinitions
	  {
	  	$$.obj = $1.obj;
	  }
	  
	| TypeDefinitions
	  {
	  	$$.obj = $1.obj;
	  }
;

//
// VALUE DEFINITIONS
//

ValueDefinitions:
	  VALUES
	  {
	  	// pass an empty list upwards
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
	  
	| VALUES ValueDefinitionList
	  {
	  	// pass the value definition list upwards
	  	$$.obj = $2.obj;
	  }
;

ValueDefinitionList:
	  error
	  {
	  	// pass an empty list upwards
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
	  
	| ValueDefinition
	  {
	  	// create a new list and add the value definition to the list
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) $1.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }

	| ValueDefinitionList SCOLON ValueDefinition
	  {
	  	// retrieve the partial list and add the value definition to the list
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) $1.obj;
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) $3.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }
;

ValueDefinition:
	  IDENT EQ STRING
	  {
	  	// check whether the value definition is allowed
	  	if (!values.contains($1.sval)) {
	  		// flag illegal value setting
	  		yyerror("value '" + $1.sval + "' is not allowed");
	  	}
	  	
	  	// create the value definition
	  	TreeGenAstValueDefinition tgavd = new TreeGenAstValueDefinition();
	  	tgavd.setKey($1.sval);
	  	tgavd.setValue($3.sval);
	  	$$.obj = tgavd;
	  }
;

//
// INSTANCE VARIABLE DEFINITIONS
//

InstanceVariableDefinitions:
	  INSTANCE VARIABLES
	  {
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
	  
	| INSTANCE VARIABLES InstanceVariableDefinitionList
	  {
	  	$$.obj = $3.obj;
	  }
;

InstanceVariableDefinitionList:
	  error
	  {
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>();
	  }
	  
	| InstanceVariable
	  {
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) $1.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }
	  
	| InstanceVariableDefinitionList SCOLON InstanceVariable
	  {
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) $1.obj;
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) $3.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }
;

InstanceVariable:
	  IDENT COLON Type
	  {
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) $3.obj;
	  	tgavd.setKey($1.sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue(new String());
	  	$$.obj = tgavd;
	  }
	  
	|
	  IDENT COLON Type ASSIGN STRING
	  {
	  	TreeGenAstVariableDefinition tgavd = new TreeGenAstVariableDefinition();
	  	TreeGenAstTypeSpecification tgatp = (TreeGenAstTypeSpecification) $3.obj;
	  	tgavd.setKey($1.sval);
	  	tgavd.setType(tgatp);
	  	tgavd.setValue($5.sval);
	  	$$.obj = tgavd;
	  }
;

//
// TYPE DEFINITIONS
//

TypeDefinitions:
	  TYPES
	  {
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>(); 
	  }
	  
	| TYPES TypeDefinitionList
	  {
	  	$$.obj = $2.obj;
	  }
;

TypeDefinitionList:
	  error
	  {
	  	$$.obj = new java.util.Vector<TreeGenAstDefinitions>(); 
	  }
	  
	| TypeDefinition
	  {
	  	java.util.Vector<TreeGenAstDefinitions> res = new java.util.Vector<TreeGenAstDefinitions>();
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) $1.obj;
	  	res.add(tpd);
	  	$$.obj = res; 
	  }
	  
	| TypeDefinitionList SCOLON TypeDefinition
	  {
	  	java.util.Vector<TreeGenAstDefinitions> res = (java.util.Vector<TreeGenAstDefinitions>) $1.obj;
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) $3.obj;
	  	res.add(tpd);
	  	$$.obj = res; 
	  }
;	  

TypeDefinition:

	//
	// shorthand definition
	//
	
	  IDENT EQ Type
	  {
	  	TreeGenAstShorthandDefinition tgash = new TreeGenAstShorthandDefinition();
	  	TreeGenAstTypeSpecification tps = (TreeGenAstTypeSpecification) $3.obj;
	  	tgash.setShorthandName($1.sval);
	  	tgash.setType(tps);
	  	$$.obj = tgash;
	  }
	  
	//
	// empty record definition
	//
	
	| IDENT DCOLON
      {
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	java.util.Vector<TreeGenAstCompositeField> cfv = new java.util.Vector<TreeGenAstCompositeField>();
	  	tgac.setCompositeName($1.sval);
	  	tgac.setFields(cfv);
		$$.obj = tgac;	  	
      }

	//
	// full record definition
	//
	      
	| IDENT DCOLON FieldList
	  {
	  	TreeGenAstCompositeDefinition tgac = new TreeGenAstCompositeDefinition();
	  	java.util.Vector<TreeGenAstCompositeField> cfv = (java.util.Vector<TreeGenAstCompositeField>) $3.obj;
	  	tgac.setCompositeName($1.sval);
	  	tgac.setFields(cfv);
		$$.obj = tgac;	  	
	  }
;
	  
FieldList:
	  Field
	  {
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) $1.obj;
	  	java.util.Vector<TreeGenAstCompositeField> res = new java.util.Vector<TreeGenAstCompositeField>();
	  	res.add(tgacf);
	  	$$.obj = res;
	  }
	  
	| FieldList Field
	  {
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) $2.obj;
	  	java.util.Vector<TreeGenAstCompositeField> res = (java.util.Vector<TreeGenAstCompositeField>) $1.obj;
	  	res.add(tgacf);
	  	$$.obj = res;
	  }
;

Field:
	  IDENT COLON Type
	  {
	  	TreeGenAstCompositeField tgacf = new TreeGenAstCompositeField();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) $3.obj;
	  	tgacf.setFieldName($1.sval);
	  	tgacf.setType(et);
	  	$$.obj = tgacf;
	  }
;

Type:
	  IDENT
	  { 
	    TreeGenAstTypeName tgatn = new TreeGenAstTypeName();
	  	tgatn.setName($1.sval);
	  	$$.obj = tgatn;
	  }
	  
	| QUOTE
	  {
	  	TreeGenAstQuotedType tgaqt = new TreeGenAstQuotedType();
	  	tgaqt.setQuote($1.sval);
	  	$$.obj = tgaqt;
	  }
	  
	| Type BAR Type
	  {
	  	TreeGenAstUnionType tgaut = new TreeGenAstUnionType();
	  	TreeGenAstTypeSpecification lhs = (TreeGenAstTypeSpecification) $1.obj; 
	  	TreeGenAstTypeSpecification rhs = (TreeGenAstTypeSpecification) $3.obj;
	  	tgaut.setLhs(lhs);
	  	tgaut.setRhs(rhs);
	  	$$.obj = tgaut; 
	  }
	  
	| LB Type RB
	  {
	  	TreeGenAstOptionalType tgaot = new TreeGenAstOptionalType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) $2.obj;
	  	tgaot.setType(et);
	  	$$.obj = tgaot;
	  }
	  
	| LP Type RP
	  {
	  	$$.obj = $1.obj;
	  }
	  
	| SEQ OF Type
	  {
	  	TreeGenAstSeqType tgast = new TreeGenAstSeqType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) $3.obj;
	  	tgast.setType(et);
	  	$$.obj = tgast;
	  }
	  
	| SET OF Type
	  {
	  	TreeGenAstSetType tgast = new TreeGenAstSetType();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) $3.obj;
	  	tgast.setType(et);
	  	$$.obj = tgast;
	  }
	  
	| MAP Type TO Type
	  {
	  	TreeGenAstMapType tgamt = new TreeGenAstMapType();
	  	TreeGenAstTypeSpecification etd = (TreeGenAstTypeSpecification) $2.obj;
	  	TreeGenAstTypeSpecification etr = (TreeGenAstTypeSpecification) $4.obj;
	  	tgamt.setDomType(etd);
	  	tgamt.setRngType(etr);
	  	$$.obj = tgamt;
	  }
	  
;

//
// END OF GRAMMAR
//

%%

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
