//
// TREE DESCRIPTION LANGUAGE - GRAMMAR
//
// input file for the byaccj parser generator version 1.15
// 

// java imports required for the generated parser semantics actions
%{
import org.overture.tools.treegen.ast.itf.*;
import org.overture.tools.treegen.ast.imp.*;
import java.util.*;
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
	  	tgacd.setDefs(new Vector<ITreeGenAstDefinitions>());
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
	  	List<ITreeGenAstDefinitions> tgadl = (Vector<ITreeGenAstDefinitions>) $3.obj;
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
	  	tgacd.setDefs(new Vector<ITreeGenAstDefinitions>());
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
	  	List <ITreeGenAstDefinitions> tgadl = (Vector<ITreeGenAstDefinitions>) $7.obj;
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
	  	$$.obj = new Vector<ITreeGenAstDefinitions>();
	  }
	  
	| DefinitionBlock
	  {
	  	$$.obj = $1.obj;
	  }
	  
	| DefinitionList DefinitionBlock
	  {
	  	// concatenate the lists
	  	List<ITreeGenAstDefinitions> lhs = (Vector<ITreeGenAstDefinitions>) $1.obj;
	  	List<ITreeGenAstDefinitions> rhs = (Vector<ITreeGenAstDefinitions>) $2.obj;
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
	  	$$.obj = new Vector<ITreeGenAstDefinitions>();
	  }
	  
	| VALUES ValueDefinitionList
	  {
	  	// pass the value definition list upwards
	  	$$.obj = $2.obj;
	  }
	  
	| VALUES ValueDefinitionList SCOLON
	  {
	  	// pass the value definition list upwards
	  	$$.obj = $2.obj;
	  }
;

ValueDefinitionList:
	  error
	  {
	  	// pass an empty list upwards
	  	$$.obj = new Vector<ITreeGenAstDefinitions>();
	  }
	  
	| ValueDefinition
	  {
	  	// create a new list and add the value definition to the list
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstValueDefinition tgavd = (TreeGenAstValueDefinition) $1.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }

	| ValueDefinitionList SCOLON ValueDefinition
	  {
	  	// retrieve the partial list and add the value definition to the list
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) $1.obj;
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
	  	$$.obj = new Vector<ITreeGenAstDefinitions>();
	  }
	  
	| INSTANCE VARIABLES InstanceVariableDefinitionList
	  {
	  	$$.obj = $3.obj;
	  }
	  
	| INSTANCE VARIABLES InstanceVariableDefinitionList SCOLON
	  {
	  	$$.obj = $3.obj;
	  }
;

InstanceVariableDefinitionList:
	  error
	  {
	  	$$.obj = new Vector<ITreeGenAstDefinitions>();
	  }
	  
	| InstanceVariable
	  {
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstVariableDefinition tgavd = (TreeGenAstVariableDefinition) $1.obj;
	  	res.add(tgavd);
	  	$$.obj = res;
	  }
	  
	| InstanceVariableDefinitionList SCOLON InstanceVariable
	  {
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) $1.obj;
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
	  	$$.obj = new Vector<ITreeGenAstDefinitions>(); 
	  }
	  
	| TYPES TypeDefinitionList
	  {
	  	$$.obj = $2.obj;
	  }
	  
	| TYPES TypeDefinitionList SCOLON
	  {
	  	$$.obj = $2.obj;
	  }
;

TypeDefinitionList:
	  error
	  {
	  	$$.obj = new Vector<ITreeGenAstDefinitions>(); 
	  }
	  
	| TypeDefinition
	  {
	  	List <ITreeGenAstDefinitions> res = new Vector<ITreeGenAstDefinitions>();
	  	TreeGenAstDefinitions tpd = (TreeGenAstDefinitions) $1.obj;
	  	res.add(tpd);
	  	$$.obj = res; 
	  }
	  
	| TypeDefinitionList SCOLON TypeDefinition
	  {
	  	List <ITreeGenAstDefinitions> res = (Vector<ITreeGenAstDefinitions>) $1.obj;
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
	  	List <ITreeGenAstCompositeField> cfv = new Vector<ITreeGenAstCompositeField>();
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
	  	List <ITreeGenAstCompositeField> cfv = (Vector<ITreeGenAstCompositeField>) $3.obj;
	  	tgac.setCompositeName($1.sval);
	  	tgac.setFields(cfv);
		$$.obj = tgac;	  	
	  }
;
	  
FieldList:
	  Field
	  {
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) $1.obj;
	  	List <ITreeGenAstCompositeField> res = new Vector<ITreeGenAstCompositeField>();
	  	res.add(tgacf);
	  	$$.obj = res;
	  }
	  
	| FieldList Field
	  {
	  	TreeGenAstCompositeField tgacf = (TreeGenAstCompositeField) $2.obj;
	  	List <ITreeGenAstCompositeField> res = (Vector<ITreeGenAstCompositeField>) $1.obj;
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
	  	tgacf.setValue(new String());
	  	$$.obj = tgacf;
	  }

/**
 ** SPECIAL CASE (INITIALISE TOKEN EMBEDDED IN A COMMENT)
 **
 ** types
 **   some_record ::
 **     some_field : token --++ := "java.lang.String"
 **     another_field : seq of char
 **/
 		  
	| IDENT COLON Type ASSIGN STRING
	  {
	  	TreeGenAstCompositeField tgacf = new TreeGenAstCompositeField();
	  	TreeGenAstTypeSpecification et = (TreeGenAstTypeSpecification) $3.obj;
	  	tgacf.setFieldName($1.sval);
	  	tgacf.setType(et);
	  	tgacf.setValue($5.sval);
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
private static HashSet<String> values;

// keep track of the current class definition
private List <ITreeGenAstClassDefinition> tgacdl;

static {
	values = new HashSet<String>();
	values.add("package");
	values.add("directory");
	values.add("toplevel");
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
