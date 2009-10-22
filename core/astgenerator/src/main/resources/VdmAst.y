%{
import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
%}

%token DCOLON SCOLON COLON SEQ_OF SET_OF IDENT EQ LB RB QUOTE
%token BAR PACKAGE PREFIX DOT DIRECTORY STRING MAP TO TOP

%left MAP TO
%left SEQ_OF SET_OF BAR

%start Document

%%

Document:
	| error
	| DefinitionList
	;

DefinitionList:
	  Definition
	  { AstComposite theComposite = (AstComposite) $1.obj;
	    theAst.addComposite(theComposite); }
	    
	| ShortHand
	  { AstShorthand theShorthand = (AstShorthand) $1.obj;
	    theAst.addShorthand(theShorthand); }
	
	| PrefixSpecification
	
	| PackageSpecification
	
	| DirectorySpecification
	
	| TopSpecification
	    
	| DefinitionList SCOLON Definition
	  { AstComposite theComposite = (AstComposite) $3.obj;
	    theAst.addComposite(theComposite); }	    
	    
	| DefinitionList SCOLON ShortHand
	  { AstShorthand theShorthand = (AstShorthand) $3.obj;
	    theAst.addShorthand(theShorthand); }

	| DefinitionList SCOLON PrefixSpecification
	
	| DefinitionList SCOLON PackageSpecification
	    
	| DefinitionList SCOLON DirectorySpecification
	
	| DefinitionList SCOLON TopSpecification
	    
	;
	
PrefixSpecification:
	  PREFIX IDENT
	  { theAst.setPrefix($2.sval); }
	;
	
PackageSpecification:
	  PACKAGE PackageList
	  { Vector thePackageList = (Vector) $2.obj;
	    theAst.setPackage(thePackageList); }
	  ;
	  
DirectorySpecification:
      DIRECTORY STRING
      { String theArg = $2.sval;
        theAst.setDirectory(theArg); }
      ;

TopSpecification:
	  TOP IdentifierList
	  { Vector theTopList = (Vector) $2.obj;
	    theAst.setTop(theTopList); }
	  ;
	        
PackageList:
	  IDENT
	  { Vector theRes = new Vector();
	    theRes.add($1.sval);
	    $$.obj = theRes; }
	    
	| PackageList DOT IDENT
	  { Vector theRes = (Vector) $1.obj;
	    theRes.add($3.sval);
	    $$.obj = theRes; }
	;
	
ShortHand:
	  IDENT EQ Type
	  { AstType theArg = (AstType) $3.obj;
	    AstShorthand theShorthand = new AstShorthand($1.sval,theArg);
	    $$.obj = theShorthand; }
	;
	
Definition:	    
	  IDENT DCOLON
	  { Vector theArg = new Vector();
	    AstComposite theComposite = new AstComposite($1.sval,theArg);
	    $$.obj = theComposite; }
	  
	| IDENT DCOLON FieldList
	  { Vector theArg = (Vector) $3.obj;
	    AstComposite theComposite = new AstComposite($1.sval,theArg);
	    $$.obj = theComposite; }
	;
	
FieldList:
	  Field
	  { AstField theArg = (AstField) $1.obj;
	    Vector theRes = new Vector();
	    theRes.add(theArg);
	    $$.obj = theRes; }
	    
	| FieldList Field
	  { AstField theArg = (AstField) $2.obj;
	    Vector theRes = (Vector) $1.obj;
	    theRes.add(theArg);
	    $$.obj = theRes; }
	;
	
Field:
	  IDENT COLON Type
	  { AstType theArg = (AstType) $3.obj;
	    AstField theField = new AstField($1.sval,theArg);
	    $$.obj = theField; }
	;
	
Type:
	  IDENT
	  { AstTypeName theTypeName = new AstTypeName($1.sval);
	    $$.obj = theTypeName; }
	
	| QUOTE
	  { AstQuotedType theQuotedType = new AstQuotedType($1.sval);
	    $$.obj = theQuotedType; }
	
	| Type BAR Type
	  { AstType theLhs = (AstType) $1.obj;
	    AstType theRhs = (AstType) $3.obj;
	    AstUnionType theUnionType = new AstUnionType(theLhs,theRhs);
	    $$.obj = theUnionType; }
	
	| LB Type RB
	  { AstType theArg = (AstType) $2.obj;
	    AstOptionalType theOptionalType = new AstOptionalType(theArg);
	    $$.obj = theOptionalType; }
	    	  
	| SEQ_OF Type
	  { AstType theArg = (AstType) $2.obj;
	    AstSeqOfType theSeqOfType = new AstSeqOfType(theArg);
	    $$.obj = theSeqOfType; }
	    
	| SET_OF Type
	  { AstType theArg = (AstType) $2.obj;
	    AstSetOfType theSetOfType = new AstSetOfType(theArg);
	    $$.obj = theSetOfType; }
	    
	| MAP Type TO Type
	  { AstType theArg1 = (AstType) $2.obj;
	    AstType theArg2 = (AstType) $4.obj;
	    AstMapType theMapType = new AstMapType(theArg1,theArg2);
	    $$.obj = theMapType; }
	;	
	
IdentifierList:
	  IDENT
	  { Vector res = new Vector();
	    res.add($1.sval);
	    $$.obj = res; }
	    
	| IdentifierList IDENT
	  { Vector res = (Vector) $1.obj;
	    res.add($2.sval);
	    $$.obj = res; }
	;
	
%%

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
