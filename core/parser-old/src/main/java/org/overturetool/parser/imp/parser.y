// overture parser implementation for byaccj
// use byaccj version v1.13 or later - see http://byaccj.sourceforge.net
// this grammar requires an ADAPTED version of byacc
// because the table sizes exceed the Java maximums

%{

// ******************************
// *** required local imports ***
// ******************************

// required standard Java definitions
import java.util.*;

// do not use the default byaccj semantic value
// byaccj is executed with -Jsemantic=Object

import org.overturetool.scanner.imp.OvertureScanner;
import org.overturetool.ast.imp.*;
import org.overturetool.ast.itf.*;

// import the definition of CGException
import jp.co.csk.vdm.toolbox.VDM.CGException;
%}

// *************************
// *** TOKEN DEFINITIONS ***
// *************************

%token LEX_ABS
%token LEX_ACT
%token LEX_ACTIVE
%token LEX_ALL
%token LEX_ALWAYS
%token LEX_AND
%token LEX_ARITHMETIC_INTEGER_DIVISION
%token LEX_ARROW
%token LEX_ASSIGN
%token LEX_ASYNC
%token LEX_ATOMIC
%token LEX_BAR_ARROW
%token LEX_BE
%token LEX_BOOL
%token LEX_BY
%token LEX_CARD
%token LEX_CASES
%token LEX_CHAR
%token LEX_CLASS
%token LEX_COMMENT
%token LEX_COMP
%token LEX_COMPOSE
%token LEX_CONC
%token LEX_CYCLES
%token LEX_DCL
%token LEX_DEF
%token LEX_DINTER
%token LEX_DMERGE
%token LEX_DO
%token LEX_DOM
%token LEX_DONTCARE
%token LEX_DOTHASH
%token LEX_DOUBLE_COLON
%token LEX_DUMMY_PRECEDENCE_SEQ1_OF
%token LEX_DUMMY_PRECEDENCE_SEQ_OF
%token LEX_DUMMY_PRECEDENCE_SET_OF
%token LEX_DUNION
%token LEX_DURATION
%token LEX_ELEMS
%token LEX_ELSE
%token LEX_ELSEIF
%token LEX_END
%token LEX_EQUAL
%token LEX_ERROR
%token LEX_ERRS
%token LEX_EXISTS
%token LEX_EXISTS1
%token LEX_EXIT
%token LEX_EXP_OR_ITERATE
%token LEX_EXT
%token LEX_FIN
%token LEX_FLOOR
%token LEX_FOR
%token LEX_FORALL
%token LEX_FROM
%token LEX_FUNCTIONS
%token LEX_GREATER_THAN
%token LEX_GREATER_THAN_OR_EQUAL
%token LEX_HD
%token LEX_HOOK
%token LEX_IF
%token LEX_IMPLY
%token LEX_IN
%token LEX_INDS
%token LEX_INIT
%token LEX_INMAP
%token LEX_INSTANCE
%token LEX_INT
%token LEX_INV
%token LEX_INVERSE
%token LEX_IN_SET
%token LEX_IOTA
%token LEX_IS
%token LEX_ISOFBASECLASS
%token LEX_ISOFCLASS
%token LEX_IS_
%token LEX_IS_DEFINED_AS
%token LEX_LAMBDA
%token LEX_LAST_RESULT
%token LEX_LEN
%token LEX_LESS_THAN
%token LEX_LESS_THAN_OR_EQUAL
%token LEX_LET LEX_LOGICAL_EQUIVALENCE
%token LEX_MAP
%token LEX_MAP_DOMAIN_RESTRICT_BY
%token LEX_MAP_DOMAIN_RESTRICT_TO
%token LEX_MAP_MERGE
%token LEX_MAP_RANGE_RESTRICT_BY
%token LEX_MAP_RANGE_RESTRICT_TO
%token LEX_MK_
%token LEX_MOD
%token LEX_MODIFY_BY
%token LEX_MU
%token LEX_MUTEX
%token LEX_NAT
%token LEX_NATONE
%token LEX_NEW
%token LEX_NIL
%token LEX_NONDET
%token LEX_NOT
%token LEX_NOT_EQUAL
%token LEX_NOT_IN_SET
%token LEX_OF
%token LEX_OPERATIONS
%token LEX_OPERATION_ARROW
%token LEX_OR
%token LEX_OTHERS
%token LEX_PER
%token LEX_PERIODIC
%token LEX_POST
%token LEX_POWER
%token LEX_PRE
%token LEX_PRECONDAPPLY
%token LEX_PRIME
%token LEX_PRIVATE
%token LEX_PROPER_SUBSET
%token LEX_PROTECTED
%token LEX_PUBLIC
%token LEX_RAISED_DOT
%token LEX_RANGE_OVER
%token LEX_RAT
%token LEX_RD
%token LEX_REAL
%token LEX_REM
%token LEX_REQ
%token LEX_RESPONSIBILITY
%token LEX_RETURN
%token LEX_REVERSE
%token LEX_RNG
%token LEX_SAMEBASECLASS
%token LEX_SAMECLASS
%token LEX_SELF
%token LEX_SEQ
%token LEX_SEQ1
%token LEX_SEQUENCE_CONCATENATE
%token LEX_SET
%token LEX_SET_INTERSECTION
%token LEX_SET_MINUS
%token LEX_SET_UNION
%token LEX_SKIP
%token LEX_SPECIFIED
%token LEX_SPORADIC 		/* added for Marcel Verhoef */
%token LEX_ST
%token LEX_START
%token LEX_STARTLIST
%token LEX_STATIC
%token LEX_SUBCLASS
%token LEX_SUBSET
%token LEX_SYNC
%token LEX_SYSTEM
%token LEX_TARROW
%token LEX_TEXBREAK
%token LEX_THEN
%token LEX_THREAD
%token LEX_THREADID
%token LEX_TIME
%token LEX_TIXE
%token LEX_TL
%token LEX_TO
%token LEX_TOKEN
%token LEX_TRACES			/* added for Adriana Sucena */
%token LEX_TRAP
%token LEX_TYPES
%token LEX_UNDEFINED
%token LEX_VALUES
%token LEX_VARIABLES
%token LEX_WAITING
%token LEX_WHILE
%token LEX_WITH
%token LEX_WR
%token LEX_YET
%token LEX_bool_false
%token LEX_bool_true
%token LEX_char_lit
%token LEX_dollar_identifier
%token LEX_identifier
%token LEX_quote_lit
%token LEX_num_lit
%token LEX_real_lit
%token LEX_text_lit

// *************************
// *** PRECEDENCE MATRIX ***
// *************************

%nonassoc error
%right LEX_FROM LEX_OF LEX_CASES LEX_DEF LEX_IF LEX_LET LEX_RAISED_DOT LEX_ASSIGN LEX_IN LEX_ARROW LEX_TARROW LEX_BAR_ARROW LEX_THEN LEX_ELSE LEX_ELSEIF LEX_BE LEX_OTHERS
%left '|'
%left LEX_LOGICAL_EQUIVALENCE
%right LEX_IMPLY
%left LEX_OR
%left LEX_AND
%nonassoc LEX_NOT
%nonassoc LEX_EQUAL LEX_NOT_EQUAL LEX_IN_SET LEX_NOT_IN_SET LEX_LESS_THAN LEX_LESS_THAN_OR_EQUAL LEX_GREATER_THAN LEX_GREATER_THAN_OR_EQUAL LEX_SUBSET LEX_PROPER_SUBSET
%nonassoc DUMMY_RELATION
%left '+' '-' LEX_SET_MINUS LEX_SET_UNION LEX_SEQUENCE_CONCATENATE LEX_MAP_MERGE LEX_MODIFY_BY
%right ':'
%left '/' '*' LEX_ARITHMETIC_INTEGER_DIVISION LEX_REM LEX_MOD LEX_SET_INTERSECTION
%right LEX_TO
%left LEX_INVERSE
%left LEX_WEAVE
%right LEX_DUMMY_PRECEDENCE_SET_OF LEX_DUMMY_PRECEDENCE_SEQ_OF LEX_DUMMY_PRECEDENCE_SEQ1_OF
%right LEX_MAP_DOMAIN_RESTRICT_TO LEX_MAP_DOMAIN_RESTRICT_BY
%left LEX_MAP_RANGE_RESTRICT_TO LEX_MAP_RANGE_RESTRICT_BY
%nonassoc LEX_ABS LEX_FLOOR LEX_CARD LEX_POWER LEX_DUNION LEX_DINTER LEX_DMERGE LEX_HD LEX_TL LEX_LEN LEX_ELEMS LEX_INDS LEX_CONC LEX_DOM LEX_RNG
%nonassoc LEX_GENERIC_LEFT_BRACKET LEX_GENERIC_RIGHT_BRACKET
%right '(' '.' '{' '['
%left LEX_DOTHASH
%left '!'
%right LEX_COMP
%right LEX_EXP_OR_ITERATE

// ************************
// *** START OF GRAMMAR ***
// ************************

%start Document

%%

Document:
	  /* empty file */
	  { yyerror ("this file is empty"); }
	  
	| error
	
	| Expression
	  { OmlExpression theExpression = (OmlExpression) $1;
		astDocument.setExpression(theExpression);
	  }
	  	    
	| Classes
	  { Vector theClassList = (Vector) $1;
	    OmlSpecifications theSpecifications = new OmlSpecifications();
	    theSpecifications.setClassList(theClassList);
	    astDocument.setSpecifications(theSpecifications);
	  }
	;
	
Classes:
	  Class
	  {	Vector res = new Vector();
	    OmlClass theClass = (OmlClass) $1;
	    if ( theClass != null ) res.add(theClass);
	    $$ = res; }
	  	
	| Classes Class
	  { Vector res = (Vector) $1;
	    OmlClass theClass = (OmlClass) $2;
	    if ( theClass != null ) res.add(theClass);
	    $$ = res; }
	;

Class:
	  ClassHeader Identifier LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    OmlLexem nm2 = (OmlLexem) $4;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }

	  /* next rule is added for Thomas Christensen */
    | ClassHeader Identifier GenericTypeList LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    Vector theGenerics = (Vector) $3;
	    OmlLexem nm2 = (OmlLexem) $5;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setGenericTypes(theGenerics);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }
    	    
	| ClassHeader Identifier InheritanceClause LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    OmlInheritanceClause theClause = (OmlInheritanceClause) $3;
	    OmlLexem nm2 = (OmlLexem) $5;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setInheritanceClause(theClause);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }

	  /* next rule is added for Thomas Christensen */
	| ClassHeader Identifier GenericTypeList InheritanceClause LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    Vector theGenerics = (Vector) $3;
	    OmlInheritanceClause theClause = (OmlInheritanceClause) $4;
	    OmlLexem nm2 = (OmlLexem) $6;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setGenericTypes(theGenerics);
	    	res.setInheritanceClause(theClause);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }
	  
	| ClassHeader Identifier DefinitionBlock LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    Vector theClassBody = (Vector) $3;
	    OmlLexem nm2 = (OmlLexem) $5;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setClassBody(theClassBody);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }

	  /* next rule is added for Thomas Christensen */
	| ClassHeader Identifier GenericTypeList DefinitionBlock LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    Vector theGenerics = (Vector) $3;
	    Vector theClassBody = (Vector) $4;
	    OmlLexem nm2 = (OmlLexem) $6;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setGenericTypes(theGenerics);
	    	res.setClassBody(theClassBody);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }
	  
	| ClassHeader Identifier InheritanceClause DefinitionBlock LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    OmlInheritanceClause theClause = (OmlInheritanceClause) $3;
	    Vector theClassBody = (Vector) $4;
	    OmlLexem nm2 = (OmlLexem) $6;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setInheritanceClause(theClause);
	    	res.setClassBody(theClassBody);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }
	    
	  /* next rule is added for Thomas Christensen */
	| ClassHeader Identifier GenericTypeList InheritanceClause DefinitionBlock LEX_END Identifier
	  { OmlClass res;
	    OmlLexem header = (OmlLexem) $1;
	    OmlLexem nm1 = (OmlLexem) $2;
	    Vector theGenerics = (Vector) $3;
	    OmlInheritanceClause theClause = (OmlInheritanceClause) $4;
	    Vector theClassBody = (Vector) $5;
	    OmlLexem nm2 = (OmlLexem) $7;
	    if ( checkClassName(nm1,nm2) ) {
	    	res = new OmlClass();
	    	res.setSystemSpec(checkClassHeader(header));
	    	res.setIdentifier(lexemToString(nm1));
	    	res.setGenericTypes(theGenerics);
	    	res.setInheritanceClause(theClause);
	    	res.setClassBody(theClassBody);
	    	res.setPosLexem(header);
	    } else {
	        res = null;
	    }
	    $$ = res; }
	;

ClassHeader:
	  LEX_CLASS
	  { $$ = $1; }
	  
	| LEX_SYSTEM
	  { $$ = $1; }
	;
	
InheritanceClause:
	  LEX_IS LEX_SUBCLASS LEX_OF IdentifierCommaList
	  { OmlInheritanceClause res = new OmlInheritanceClause();
	    OmlLexem lexIS = (OmlLexem) $1;
	    Vector theIdentifiers = (Vector) $4;
	    res.setIdentifierList(theIdentifiers);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	;

DefinitionBlock:
	  DefinitionBlockAlternative
	  { Vector res = new Vector();
	    if ($1 != null) res.add($1);
	    $$ = res; }
	    
	| DefinitionBlock DefinitionBlockAlternative
	  { Vector res = (Vector) $1;
	    if ($2 != null) res.add($2);
	    $$ = res; }
	;
	
DefinitionBlockAlternative:
	  TypeDefinitions
	  { $$ = $1; }
	  
	| ValueDefinitions
	  { $$ = $1; }
	  
	| FunctionDefinitions
	  { $$ = $1; }
	  
	| OperationDefinitions
	  { $$ = $1; }
	  
	| InstanceVariableDefinitions
	  { $$ = $1; }
	  
	| SynchronizationDefinitions
	  { $$ = $1; }
	  
	| ThreadDefinitions
	  { $$ = $1; }	  
	  
	| TraceDefinitions
	  { $$ = $1; }
	;

/************************/
/*** TYPE DEFINITIONS ***/
/************************/

TypeDefinitions:
	  LEX_TYPES
	  { OmlTypeDefinitions res = new OmlTypeDefinitions();
	    OmlLexem lexTPS = (OmlLexem) $1;
	    res.setPosLexem(lexTPS);
	    $$ = res; }
	  
	| LEX_TYPES ListOfTypeDefinitions
	  { OmlTypeDefinitions res = new OmlTypeDefinitions();
	    OmlLexem lexTPS = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setTypeList(theDefinitions);
	    res.setPosLexem(lexTPS);
	    $$ = res; }
	    
	| LEX_TYPES ListOfTypeDefinitions ';'
	  { OmlTypeDefinitions res = new OmlTypeDefinitions();
	    OmlLexem lexTPS = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setTypeList(theDefinitions);
	    res.setPosLexem(lexTPS);
	    $$ = res; }
	;
	
ListOfTypeDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| TypeDefinition
	  { Vector res = new Vector();
	    OmlAccessDefinition theAccessDefinition = new OmlAccessDefinition();
	    OmlScope theScope = new OmlScope();
	    OmlTypeDefinition theDefinition = (OmlTypeDefinition) $1;
	    theAccessDefinition.setStaticAccess(false);
	    theAccessDefinition.setAsyncAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQDEFAULT);
	    theScope.setPosNode(theDefinition);
	    theAccessDefinition.setScope(theScope);
	    theDefinition.setAccess(theAccessDefinition);
	    res.add(theDefinition);
	    $$ = res; }
	    
	| AccessDefinition TypeDefinition
	  { Vector res = new Vector();
	    OmlAccessDefinition theAccessDefinition = (OmlAccessDefinition) $1;
	    OmlTypeDefinition theDefinition = (OmlTypeDefinition) $2;
	    theDefinition.setAccess(theAccessDefinition);
	    res.add(theDefinition);
	    $$ = res; }
	    
	| ListOfTypeDefinitions ';' TypeDefinition
	  { Vector res = (Vector) $1;
	    OmlAccessDefinition theAccessDefinition = new OmlAccessDefinition();
	    OmlScope theScope = new OmlScope();
	    OmlTypeDefinition theDefinition = (OmlTypeDefinition) $3;
	    theAccessDefinition.setStaticAccess(false);
	    theAccessDefinition.setAsyncAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQDEFAULT);
	    theScope.setPosNode(theDefinition);
	    theAccessDefinition.setScope(theScope);
	    theDefinition.setAccess(theAccessDefinition);
	    res.add(theDefinition);
	    $$ = res; }
	    
	| ListOfTypeDefinitions ';' AccessDefinition TypeDefinition
	  { Vector res = (Vector) $1;
	    OmlAccessDefinition theAccessDefinition = (OmlAccessDefinition) $3;
	    OmlTypeDefinition theDefinition = (OmlTypeDefinition) $4;
	    theDefinition.setAccess(theAccessDefinition);
	    res.add(theDefinition);
	    $$ = res; }
	;
	
AccessDefinition:
	  LEX_PRIVATE
	  { OmlAccessDefinition res = new OmlAccessDefinition();
	    OmlLexem lexPR = (OmlLexem) $1;
	    OmlScope theScope = new OmlScope();
	    res.setStaticAccess(false);
	    res.setAsyncAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQPRIVATE);
	    theScope.setPosLexem(lexPR);
	    res.setScope(theScope);
	    res.setPosNode(theScope);
	    $$ = res; }
	    
	| LEX_PROTECTED
	  { OmlAccessDefinition res = new OmlAccessDefinition();
	    OmlLexem lexPR = (OmlLexem) $1;
	    OmlScope theScope = new OmlScope();
	    res.setStaticAccess(false);
	    res.setAsyncAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQPROTECTED);
	    theScope.setPosLexem(lexPR);
	    res.setScope(theScope);
	    res.setPosNode(theScope);
	    $$ = res; }
	    
	| LEX_PUBLIC
	  { OmlAccessDefinition res = new OmlAccessDefinition();
	    OmlLexem lexPB = (OmlLexem) $1;
	    OmlScope theScope = new OmlScope();
	    res.setStaticAccess(false);
	    res.setAsyncAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQPUBLIC);
	    theScope.setPosLexem(lexPB);
	    res.setScope(theScope);
	    res.setPosNode(theScope);
	    $$ = res; }
	;

TypeDefinition:
	  Identifier LEX_EQUAL Type
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlSimpleType theSimpleType = new OmlSimpleType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    theSimpleType.setIdentifier(lexemToString(nm));
	    theSimpleType.setType(theType);
	    theSimpleType.setPosNode(theType);
	    res.setShape(theSimpleType);
	    res.setPosLexem(nm);
	    $$ = res; }
	    
	| Identifier LEX_EQUAL Type Invariant
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlSimpleType theSimpleType = new OmlSimpleType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    OmlInvariant theInvariant = (OmlInvariant) $4;
	    theSimpleType.setIdentifier(lexemToString(nm));
	    theSimpleType.setType(theType);
	    theSimpleType.setInvariant(theInvariant);
	    theSimpleType.setPosNode(theType);
	    res.setShape(theSimpleType);
	    res.setPosLexem(nm);
	    $$ = res; }
	    
	| Identifier LEX_DOUBLE_COLON
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlComplexType theComplexType = new OmlComplexType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlLexem lexDC = (OmlLexem) $2;
	    theComplexType.setIdentifier(lexemToString(nm));
	    theComplexType.setPosLexem(lexDC);
	    res.setShape(theComplexType);
	    res.setPosLexem(nm);
	    $$ = res; }
	    
	| Identifier LEX_DOUBLE_COLON FieldList
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlComplexType theComplexType = new OmlComplexType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlLexem lexDC = (OmlLexem) $2;
	    Vector theFields = (Vector) $3;
	    theComplexType.setIdentifier(lexemToString(nm));
	    theComplexType.setFieldList(theFields);
	    theComplexType.setPosLexem(lexDC);
	    res.setShape(theComplexType);
	    res.setPosLexem(nm);
	    $$ = res; }
	    
	| Identifier LEX_DOUBLE_COLON Invariant
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlComplexType theComplexType = new OmlComplexType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlLexem lexDC = (OmlLexem) $2;
	    OmlInvariant theInvariant = (OmlInvariant) $3;
	    theComplexType.setIdentifier(lexemToString(nm));
	    theComplexType.setInvariant(theInvariant);
	    theComplexType.setPosLexem(lexDC);
	    res.setShape(theComplexType);
	    res.setPosLexem(nm);
	    $$ = res; }
	    
	| Identifier LEX_DOUBLE_COLON FieldList Invariant
	  { OmlTypeDefinition res = new OmlTypeDefinition();
	    OmlComplexType theComplexType = new OmlComplexType();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlLexem lexDC = (OmlLexem) $2;
	    Vector theFields = (Vector) $3;
	    OmlInvariant theInvariant = (OmlInvariant) $4;
	    theComplexType.setIdentifier(lexemToString(nm));
	    theComplexType.setFieldList(theFields);
	    theComplexType.setInvariant(theInvariant);
	    theComplexType.setPosLexem(lexDC);
	    res.setShape(theComplexType);
	    res.setPosLexem(nm);
	    $$ = res; }	    
	;
	
Type:
	  BracketedType
	  { $$ = $1; }
	  
	| BasicType
	  { $$ = $1; }
	  
	| QuotedType
	  { $$ = $1; }
	  
	| CompositeType
	  { $$ = $1; }
	  
	| UnionType
	  { $$ = $1; }
	  
	| ProductType
	  { $$ = $1; }
	  
	| OptionalType
	  { $$ = $1; }
	  
	| SetType
	  { $$ = $1; }
	  
	| SeqType
	  { $$ = $1; }
	  
	| MapType
	  { $$ = $1; }
	  
	| PartialFunctionType
	  { $$ = $1; }
	  
	| TypeName
	  { $$ = $1; }
	  
	| TypeVariable
	  { $$ = $1; }
	  
	| ClassTypeInstantiation /* added for Thomas Christensen */
	  { $$ = $1; }
	;

BracketedType:
	  '(' Type ')'
	  { OmlBracketedType res = new OmlBracketedType();
	    OmlType theType = (OmlType) $2;
	    res.setType(theType);
	    res.setPosNode(theType);
	    $$ = res; }
	;
	
BasicType:
	  LEX_BOOL
	  { OmlBoolType res = new OmlBoolType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_NAT
	  { OmlNatType res = new OmlNatType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_NATONE
	  { OmlNat1Type res = new OmlNat1Type();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_INT
	  { OmlIntType res = new OmlIntType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_RAT
	  { OmlRatType res = new OmlRatType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_REAL
	  { OmlRealType res = new OmlRealType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_CHAR
	  { OmlCharType res = new OmlCharType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	  
	| LEX_TOKEN
	  { OmlTokenType res = new OmlTokenType();
	    OmlLexem lexTP = (OmlLexem) $1;
	    res.setPosLexem(lexTP);
	    $$ = res; }
	;
	
QuotedType:
	  LEX_quote_lit
	  { OmlQuoteType res = new OmlQuoteType();
	    OmlQuoteLiteral theLiteral = new OmlQuoteLiteral();
	    OmlLexem lit = (OmlLexem) $1;
	    theLiteral.setVal(stripString(lit.getText()));
	    theLiteral.setPosLexem(lit);
	    res.setQuoteLiteral(theLiteral);
	    res.setPosNode(theLiteral);
	    $$ = res; }
	;

CompositeType:
	  LEX_COMPOSE Identifier LEX_OF LEX_END
	  { OmlCompositeType res = new OmlCompositeType();
	    OmlLexem lexCP = (OmlLexem) $1;
	    OmlLexem nm = (OmlLexem) $2;
	    res.setIdentifier(lexemToString(nm));
	    res.setPosLexem(lexCP);
	    $$ = res; }
	    
	| LEX_COMPOSE Identifier LEX_OF FieldList LEX_END
	  { OmlCompositeType res = new OmlCompositeType();
	    OmlLexem lexCP = (OmlLexem) $1;
	    OmlLexem nm = (OmlLexem) $2;
	    Vector theFields = (Vector) $4;
	    res.setIdentifier(lexemToString(nm));
	    res.setFieldList(theFields);
	    res.setPosLexem(lexCP);
	    $$ = res; }	    
	;
	
FieldList:
	  Field
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| FieldList Field
	  { Vector res = (Vector) $1;
	    res.add($2);
	    $$ = res; }
	;
	
Field:
	  Identifier ':' Type
	  { OmlField res = new OmlField();
	    OmlLexem id = (OmlLexem) $1;
	    OmlLexem lexCOL = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    res.setIdentifier(lexemToString(id));
	    res.setType(theType);
	    res.setIgnore(false);
	    res.setPosLexem(lexCOL);
	    $$ = res; }
	    
	| Identifier LEX_DONTCARE Type
	  { OmlField res = new OmlField();
	    OmlLexem id = (OmlLexem) $1;
	    OmlLexem lexDC = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    res.setIdentifier(lexemToString(id));
	    res.setType(theType);
	    res.setIgnore(true);
	    res.setPosLexem(lexDC);
	    $$ = res; }
	    
	| Type
	  { OmlField res = new OmlField();
	    OmlType theType = (OmlType) $1;
	    res.setType(theType);
	    res.setIgnore(false);
	    res.setPosNode(theType);
	    $$ = res; }
	;

UnionType:
	  Type '|' Type
	  { OmlUnionType res = new OmlUnionType();
	    OmlType lhs = (OmlType) $1;
	    OmlLexem lexTO = (OmlLexem) $2;
	    OmlType rhs = (OmlType) $3;
	    res.setLhsType(lhs);
	    res.setRhsType(rhs);
	    res.setPosLexem(lexTO);
	    $$ = res; }
	;
	
ProductType:
	  Type '*' Type
	  { OmlProductType res = new OmlProductType();
	    OmlType lhs = (OmlType) $1;
	    OmlLexem lexTO = (OmlLexem) $2;
	    OmlType rhs = (OmlType) $3;
	    res.setLhsType(lhs);
	    res.setRhsType(rhs);
	    res.setPosLexem(lexTO);
	    $$ = res; }
	;

OptionalType:
	  '[' Type ']'
	  { OmlOptionalType res = new OmlOptionalType();
	    OmlLexem lexLH = (OmlLexem) $1;
	    OmlType theType = (OmlType) $2;
	    res.setType(theType);
	    res.setPosLexem(lexLH);
	    $$ = res; }
	;
	
SetType:
	  LEX_SET LEX_OF Type %prec LEX_DUMMY_PRECEDENCE_SET_OF
	  { OmlSetType res = new OmlSetType();
	    OmlLexem lexST = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    res.setType(theType);
	    res.setPosLexem(lexST);
	    $$ = res; }
	;
	
SeqType:
	  LEX_SEQ LEX_OF Type %prec LEX_DUMMY_PRECEDENCE_SEQ_OF
	  { OmlSeq0Type res = new OmlSeq0Type();
	    OmlLexem lexSQ = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    res.setType(theType);
	    res.setPosLexem(lexSQ);
	    $$ = res; }
	    
	| LEX_SEQ1 LEX_OF Type %prec LEX_DUMMY_PRECEDENCE_SEQ1_OF
	  { OmlSeq1Type res = new OmlSeq1Type();
	    OmlLexem lexSQ = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    res.setType(theType);
	    res.setPosLexem(lexSQ);
	    $$ = res; }
	;

MapType:
	  LEX_MAP Type LEX_TO Type
	  { OmlGeneralMapType res = new OmlGeneralMapType();
	    OmlLexem lexMP = (OmlLexem) $1;
	    OmlType lhs = (OmlType) $2;
	    OmlType rhs = (OmlType) $4;
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosLexem(lexMP);
	    $$ = res; }
	    
	| LEX_INMAP Type LEX_TO Type
	  { OmlInjectiveMapType res = new OmlInjectiveMapType();
	    OmlLexem lexIMP = (OmlLexem) $1;
	    OmlType lhs = (OmlType) $2;
	    OmlType rhs = (OmlType) $4;
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosLexem(lexIMP);
	    $$ = res; }	    
	;
	
FunctionType:
	  PartialFunctionType
	  { $$ = $1; }
	  
	| TotalFunctionType
	  { $$ = $1; }
	;
	
PartialFunctionType:
	  Type LEX_ARROW Type
	  { OmlPartialFunctionType res = new OmlPartialFunctionType();
	    OmlType lhs = (OmlType) $1;
	    OmlType rhs = (OmlType) $3;
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosNode(lhs);
	    $$ = res; }
	    
 	| '(' ')' LEX_ARROW Type
	  { OmlPartialFunctionType res = new OmlPartialFunctionType();
	    OmlType lhs = new OmlEmptyType();
	    OmlLexem lexLP = (OmlLexem) $1;
	    OmlType rhs = (OmlType) $4;
	    lhs.setPosLexem(lexLP);
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosNode(lhs);
	    $$ = res; }
	;
 
TotalFunctionType:
	  Type LEX_TARROW Type
	  { OmlTotalFunctionType res = new OmlTotalFunctionType();
	    OmlType lhs = (OmlType) $1;
	    OmlType rhs = (OmlType) $3;
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosNode(lhs);
	    $$ = res; }
	    
	| '(' ')' LEX_TARROW Type
	  { OmlTotalFunctionType res = new OmlTotalFunctionType();
	    OmlType lhs = new OmlEmptyType();
	    OmlLexem lexLP = (OmlLexem) $1;
	    OmlType rhs = (OmlType) $4;
	    lhs.setPosLexem(lexLP);
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosNode(lhs);
	    $$ = res; }
	;

OperationType:
	  DiscretionaryType LEX_OPERATION_ARROW DiscretionaryType
	  { OmlOperationType res = new OmlOperationType();
	    OmlType lhs = (OmlType) $1;
	    OmlType rhs = (OmlType) $3;
	    res.setDomType(lhs);
	    res.setRngType(rhs);
	    res.setPosNode(lhs);
	    $$ = res; }	  
	;

DiscretionaryType:
	  Type
	  { $$ = $1; }
	  
	| '(' ')'
	  { OmlEmptyType res = new OmlEmptyType();
	    OmlLexem lexLP = (OmlLexem) $1;
	    res.setPosLexem(lexLP);
	    $$ = res; }
	;
	
TypeName:
	  Name
	  { OmlTypeName res = new OmlTypeName();
	    OmlName theName = (OmlName) $1;
	    res.setName(theName);
	    res.setPosNode(theName);
	    $$ = res; }
	;
	
TypeVariable:
	  '@' Identifier
	  { OmlTypeVariable res = new OmlTypeVariable();
	    OmlLexem lexAT = (OmlLexem) $1;
	    OmlLexem id = (OmlLexem) $2;
	    res.setIdentifier(lexemToString(id));
	    res.setPosLexem(lexAT);
	    $$ = res; }
	;

ClassTypeInstantiation:	/* added for Thomas Christensen */
	  Name GenericTypeList
	  { OmlClassTypeInstantiation res = new OmlClassTypeInstantiation();
	    OmlName theName = (OmlName) $1;
	    Vector theGenerics = (Vector) $2;
	    res.setName(theName);
	    res.setGenericTypes(theGenerics);
	    res.setPosNode(theName);
	    $$ = res; }
	;
	
GenericTypeList: /* added for Thomas Christensen */
	  LEX_GENERIC_LEFT_BRACKET TypeList LEX_GENERIC_RIGHT_BRACKET
	  { $$ = $2; }
	;
	  
Invariant:
	  LEX_INV Pattern LEX_IS_DEFINED_AS Expression
	  { OmlInvariant res = new OmlInvariant();
	    OmlLexem lexINV = (OmlLexem) $1;
	    OmlPattern thePattern = (OmlPattern) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setPattern(thePattern);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexINV);
	    $$ = res; }
	;		

/*************************/
/*** VALUE DEFINITIONS ***/
/*************************/

ValueDefinitions:
	  LEX_VALUES
	  { OmlValueDefinitions res = new OmlValueDefinitions();
	    OmlLexem lexVAL = (OmlLexem) $1;
	    res.setPosLexem(lexVAL);
	    $$ = res; }
	    
	| LEX_VALUES ListOfValueDefinitions
	  { OmlValueDefinitions res = new OmlValueDefinitions();
	    OmlLexem lexVAL = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setValueList(theDefinitions);
	    res.setPosLexem(lexVAL);
	    $$ = res; }
	    
	| LEX_VALUES ListOfValueDefinitions ';'
	  { OmlValueDefinitions res = new OmlValueDefinitions();
	    OmlLexem lexVAL = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setValueList(theDefinitions);
	    res.setPosLexem(lexVAL);
	    $$ = res; }
	;
	
ListOfValueDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| StaticAccess ValueDefinition
	  { Vector res = new Vector();
	    OmlValueDefinition theValueDef = new OmlValueDefinition();
	    OmlAccessDefinition theAccessDefinition = (OmlAccessDefinition) $1;
	    OmlValueShape theShape = (OmlValueShape) $2;
	    theValueDef.setAccess(theAccessDefinition);
	    theValueDef.setShape(theShape);
	    theValueDef.setPosNode(theShape);
	    res.add(theValueDef);
	    $$ = res; }
	    
	| ListOfValueDefinitions ';' StaticAccess ValueDefinition
	  { Vector res = (Vector) $1;
	    OmlValueDefinition theValueDef = new OmlValueDefinition();
	    OmlAccessDefinition theAccessDefinition = (OmlAccessDefinition) $3;
	    OmlValueShape theShape = (OmlValueShape) $4;
	    theValueDef.setAccess(theAccessDefinition);
	    theValueDef.setShape(theShape);
	    theValueDef.setPosNode(theShape);
	    res.add(theValueDef);
	    $$ = res; }
	;

StaticAccess:
      /* no access declaration */
	  { OmlAccessDefinition res = new OmlAccessDefinition();
	    OmlScope theScope = new OmlScope();
	    res.setAsyncAccess(false);
	    res.setStaticAccess(false);
	    theScope.setValue(OmlScopeQuotes.IQDEFAULT);
	    theScope.setPosition(new Long(getLine()),new Long(getColumn()));
	    res.setScope(theScope);
	    $$ = res; }

	| LEX_STATIC
	  { OmlAccessDefinition res = new OmlAccessDefinition();
	    OmlScope theScope = new OmlScope();
	    OmlLexem lexST = (OmlLexem) $1;
	    res.setAsyncAccess(false);
	    res.setStaticAccess(true);
	    theScope.setValue(OmlScopeQuotes.IQDEFAULT);
	    theScope.setPosLexem(lexST);
	    res.setScope(theScope);
	    res.setPosLexem(lexST);
	    $$ = res; }
	    
	| AccessDefinition
	  { $$ = $1; }

	| LEX_STATIC AccessDefinition
	  { OmlAccessDefinition res = (OmlAccessDefinition) $2;
	    OmlLexem lexST = (OmlLexem) $1;
	    res.setStaticAccess(true);
	    res.setPosLexem(lexST);
	    $$ = res; }

	| AccessDefinition LEX_STATIC
	  { OmlAccessDefinition res = (OmlAccessDefinition) $1;
	    res.setStaticAccess(true);
	    $$ = res; }
	;

ValueDefinition:
	  Pattern LEX_EQUAL Expression
	  { OmlValueShape res = new OmlValueShape();
	    OmlPattern thePattern = (OmlPattern) $1;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setPattern(thePattern);
	    res.setExpression(theExpression);
	    res.setPosNode(thePattern);
	    $$ = res; }
	    
	| Pattern ':' Type LEX_EQUAL Expression
	  { OmlValueShape res = new OmlValueShape();
	    OmlPattern thePattern = (OmlPattern) $1;
	    OmlType theType = (OmlType) $3;
	    OmlExpression theExpression = (OmlExpression) $5;
	    res.setPattern(thePattern);
	    res.setType(theType);
	    res.setExpression(theExpression);
	    res.setPosNode(thePattern);
	    $$ = res; }
	;

/****************************/
/*** FUNCTION DEFINITIONS ***/
/****************************/

FunctionDefinitions:
	  LEX_FUNCTIONS
	  { OmlFunctionDefinitions res = new OmlFunctionDefinitions();
	    OmlLexem lexFCT = (OmlLexem) $1;
	    res.setPosLexem(lexFCT);
	    $$ = res; }
	    
	| LEX_FUNCTIONS ListOfFunctionDefinitions
	  { OmlFunctionDefinitions res = new OmlFunctionDefinitions();
	    OmlLexem lexFCT = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setFunctionList(theDefinitions);
	    res.setPosLexem(lexFCT);
	    $$ = res; }
	    
	| LEX_FUNCTIONS ListOfFunctionDefinitions ';'
	  { OmlFunctionDefinitions res = new OmlFunctionDefinitions();
	    OmlLexem lexFCT = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    res.setFunctionList(theDefinitions);
	    res.setPosLexem(lexFCT);
	    $$ = res; }
	;

ListOfFunctionDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| StaticAccess FunctionDefinition
	  { Vector res = new Vector();
	    OmlAccessDefinition theAccess = (OmlAccessDefinition) $1;
	    OmlFunctionDefinition theFunction = (OmlFunctionDefinition) $2;
	    theFunction.setAccess(theAccess);
	    res.add(theFunction);
	    $$ = res; }
	    
	| ListOfFunctionDefinitions ';' StaticAccess FunctionDefinition
	  { Vector res = (Vector) $1;
	    OmlAccessDefinition theAccess = (OmlAccessDefinition) $3;
	    OmlFunctionDefinition theFunction = (OmlFunctionDefinition) $4;
	    theFunction.setAccess(theAccess);
	    res.add(theFunction);
	    $$ = res; }
	;
	
FunctionDefinition:
	  ImplFunctionDefinition
	  { OmlFunctionDefinition res = new OmlFunctionDefinition();
	    OmlFunctionShape theShape = (OmlFunctionShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }

	| ExplFunctionDefinition
	  { OmlFunctionDefinition res = new OmlFunctionDefinition();
	    OmlFunctionShape theShape = (OmlFunctionShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }

	| ExtExplFunctionDefinition
	  { OmlFunctionDefinition res = new OmlFunctionDefinition();
	    OmlFunctionShape theShape = (OmlFunctionShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }
	
	| TypelessExplFunctionDefinition
	  { OmlFunctionDefinition res = new OmlFunctionDefinition();
	    OmlFunctionShape theShape = (OmlFunctionShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }
	;
	
ImplFunctionDefinition:
	  Identifier ParameterTypes IdentifierTypePairList FunctionTrailer
	  { OmlImplicitFunction res = new OmlImplicitFunction();
	    OmlLexem id = (OmlLexem) $1;
	    Vector theParms = (Vector) $2;
	    Vector idTpPairs = (Vector) $3;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $4;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(theParms);
	    res.setIdentifierTypePairList(idTpPairs);
	    if ( !theTrailer.hasPostExpression() ) {
	    	yyerror("post condition is mandatory in an implicit function definition");
	    }
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier TypeVariableList ParameterTypes IdentifierTypePairList FunctionTrailer
	  { OmlImplicitFunction res = new OmlImplicitFunction();
	    OmlLexem id = (OmlLexem) $1;
	    Vector tpVarList = (Vector) $2;
	    Vector theParms = (Vector) $3;
	    Vector idTpPairs = (Vector) $4;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $5;
	    res.setIdentifier(lexemToString(id));
	    res.setTypeVariableList(tpVarList);
	    res.setPatternTypePairList(theParms);
	    res.setIdentifierTypePairList(idTpPairs);
	    if ( !theTrailer.hasPostExpression() ) {
	    	yyerror("post condition is mandatory in an implicit function definition");
	    }
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	;
	
ExplFunctionDefinition:
	  Identifier ':' FunctionType Identifier ParametersList LEX_IS_DEFINED_AS FunctionBody FunctionTrailer
	  { OmlExplicitFunction res = new OmlExplicitFunction();
	    OmlLexem id1 = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    OmlLexem id2 = (OmlLexem) $4;
	    Vector theParms = (Vector) $5;
	    OmlFunctionBody theBody = (OmlFunctionBody) $7;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $8;
	    if ( id1.getText().compareTo(id2.getText()) != 0 ) {
	    	yyerror("identifiers are different in explicit function definition");
	    }
	    res.setIdentifier(lexemToString(id1));
	    res.setType(theType);
	    res.setParameterList(theParms);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id1);
	    $$ = res; }
	    
	| Identifier TypeVariableList ':' FunctionType Identifier ParametersList LEX_IS_DEFINED_AS FunctionBody FunctionTrailer
	  { OmlExplicitFunction res = new OmlExplicitFunction();
	    OmlLexem id1 = (OmlLexem) $1;
	    Vector tpVarList = (Vector) $2;
	    OmlType theType = (OmlType) $4;
	    OmlLexem id2 = (OmlLexem) $5;
	    Vector theParms = (Vector) $6;
	    OmlFunctionBody theBody = (OmlFunctionBody) $8;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $9;
	    if ( id1.getText().compareTo(id2.getText()) != 0 ) {
	    	yyerror("identifiers are different in explicit function definition");
	    }
	    res.setIdentifier(lexemToString(id1));
	    res.setTypeVariableList(tpVarList);
	    res.setType(theType);
	    res.setParameterList(theParms);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id1);
	    $$ = res; }
	;
	
ExtExplFunctionDefinition:
	  Identifier ParameterTypes IdentifierTypePairList LEX_IS_DEFINED_AS FunctionBody FunctionTrailer
	  { OmlExtendedExplicitFunction res = new OmlExtendedExplicitFunction();
	    OmlLexem id = (OmlLexem) $1;
	    Vector theParms = (Vector) $2;
	    Vector idTpPairs = (Vector) $3;
	    OmlFunctionBody theBody = (OmlFunctionBody) $5;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $6;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(theParms);
	    res.setIdentifierTypePairList(idTpPairs);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier TypeVariableList ParameterTypes IdentifierTypePairList LEX_IS_DEFINED_AS FunctionBody FunctionTrailer
	  { OmlExtendedExplicitFunction res = new OmlExtendedExplicitFunction();
	    OmlLexem id = (OmlLexem) $1;
	    Vector theTypeVars = (Vector) $2;
	    Vector theParms = (Vector) $3;
	    Vector idTpPairs = (Vector) $4;
	    OmlFunctionBody theBody = (OmlFunctionBody) $6;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $7;
	    res.setIdentifier(lexemToString(id));
	    res.setTypeVariableList(theTypeVars);
	    res.setPatternTypePairList(theParms);
	    res.setIdentifierTypePairList(idTpPairs);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	;

TypelessExplFunctionDefinition:			/* added for Thomas Christensen */
	  Identifier ParametersList LEX_IS_DEFINED_AS FunctionBody FunctionTrailer
	  { OmlTypelessExplicitFunction res = new OmlTypelessExplicitFunction();
	    OmlLexem id1 = (OmlLexem) $1;
	    Vector theParms = (Vector) $2;
	    OmlFunctionBody theBody = (OmlFunctionBody) $4;
	    OmlFunctionTrailer theTrailer = (OmlFunctionTrailer) $5;
	    res.setIdentifier(lexemToString(id1));
	    res.setParameterList(theParms);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id1);
	    $$ = res; }
	;
		    
FunctionTrailer:
	  OptionalPrecondition OptionalPostcondition
	  { OmlFunctionTrailer res = new OmlFunctionTrailer();
	    OmlExpression thePreCond = (OmlExpression) $1;
	    OmlExpression thePostCond = (OmlExpression) $2;
	    // set the fields in reverse order to force the left-most known position information
	    if ( thePostCond != null ) {
	      res.setPostExpression(thePostCond);
	      res.setPosNode(thePostCond);
	    };
	    if ( thePreCond != null ) {
	      res.setPreExpression(thePreCond);
	      res.setPosNode(thePreCond);
	    };
	    $$ = res; }
	;
		
TypeVariableList:
	  '[' ListOfTypeVariables ']'
	  { $$ = $2; }
	;
	
ListOfTypeVariables:
	  TypeVariable
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| ListOfTypeVariables ',' TypeVariable
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;

IdentifierTypePairList:
	  Identifier ':' Type
	  { Vector res = new Vector();
	    OmlIdentifierTypePair theIdTpPair = new OmlIdentifierTypePair();
	    OmlLexem id = (OmlLexem) $1;
	    OmlLexem lexCOL = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    theIdTpPair.setIdentifier(lexemToString(id));
	    theIdTpPair.setType(theType);
	    theIdTpPair.setPosLexem(lexCOL);
	    res.add(theIdTpPair);
	    $$ = res; }
	    
	| IdentifierTypePairList ',' Identifier ':' Type
	  { Vector res = (Vector) $1;
	    OmlIdentifierTypePair theIdTpPair = new OmlIdentifierTypePair();
	    OmlLexem id = (OmlLexem) $3;
	    OmlLexem lexCOL = (OmlLexem) $4;
	    OmlType theType = (OmlType) $5;
	    theIdTpPair.setIdentifier(lexemToString(id));
	    theIdTpPair.setType(theType);
	    theIdTpPair.setPosLexem(lexCOL);
	    res.add(theIdTpPair);
	    $$ = res; }
	;

FunctionBody:
	  Expression
	  { OmlFunctionBody res = new OmlFunctionBody();
	    OmlExpression theExpr = (OmlExpression) $1;
	    res.setNotYetSpecified(false);
	    res.setSubclassResponsibility(false);
	    res.setFunctionBody(theExpr);
	    res.setPosNode(theExpr);
	    $$ = res; }
	    
	| LEX_IS LEX_NOT LEX_YET LEX_SPECIFIED
	  { OmlFunctionBody res = new OmlFunctionBody();
	    OmlLexem lexIS = (OmlLexem) $1;
	    res.setNotYetSpecified(true);
	    res.setSubclassResponsibility(false);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	    
	| LEX_IS LEX_SUBCLASS LEX_RESPONSIBILITY
	  { OmlFunctionBody res = new OmlFunctionBody();
	    OmlLexem lexIS = (OmlLexem) $1;
	    res.setNotYetSpecified(false);
	    res.setSubclassResponsibility(true);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	;

ParameterTypes:
	  '(' ')'
	  { $$ = new Vector(); }
	  
	| '(' PatternTypePairList ')'
	  { $$ = $2; }
	;
	
PatternTypePairList:
	  error
	  { $$ = new Vector(); }
	  
	| PatternList ':' Type
	  { Vector res = new Vector();
	    OmlPatternTypePair thePatTpPair = new OmlPatternTypePair();
	    Vector thePatList = (Vector) $1;
	    OmlLexem lexCOL = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    thePatTpPair.setPatternList(thePatList);
	    thePatTpPair.setType(theType);
	    thePatTpPair.setPosLexem(lexCOL);
	    res.add(thePatTpPair);
	    $$ = res; }
	    
	| PatternTypePairList ',' PatternList ':' Type
	  { Vector res = (Vector) $1;
	    OmlPatternTypePair thePatTpPair = new OmlPatternTypePair();
	    Vector thePatList = (Vector) $3;
	    OmlLexem lexCOL = (OmlLexem) $4;
	    OmlType theType = (OmlType) $5;
	    thePatTpPair.setPatternList(thePatList);
	    thePatTpPair.setType(theType);
	    thePatTpPair.setPosLexem(lexCOL);
	    res.add(thePatTpPair);
	    $$ = res; }
	;

ParametersList:
	  Parameters
	  { Vector res = new Vector();
	    OmlParameter theParm = (OmlParameter) $1;
	    res.add(theParm);
	    $$ = res; }
	    
	| ParametersList Parameters
	  { Vector res = (Vector) $1;
	    OmlParameter theParm = (OmlParameter) $2;
	    res.add(theParm);
	    $$ = res; }
	;
	
Parameters:
	  '(' ')'
	  { OmlParameter res = new OmlParameter();
	    OmlLexem lexLP = (OmlLexem) $1;
	    res.setPosLexem(lexLP);
	    $$ = res; }
	  
	| '(' PatternList ')'
	  { OmlParameter res = new OmlParameter();
	    OmlLexem lexLP = (OmlLexem) $1;
	    Vector thePatternList = (Vector) $2;
	    res.setPatternList(thePatternList);	    
	    res.setPosLexem(lexLP);
	    $$ = res; }
	;

/*****************************/
/*** OPERATION DEFINITIONS ***/
/*****************************/

OperationDefinitions:
	  LEX_OPERATIONS
	  { OmlOperationDefinitions res = new OmlOperationDefinitions();
	    OmlLexem lexOPR = (OmlLexem) $1;
	    res.setPosLexem(lexOPR);
	    $$ = res; }
	  
	| LEX_OPERATIONS ListOfOperationDefinitions
	  { OmlOperationDefinitions res = new OmlOperationDefinitions();
	    OmlLexem lexOPR = (OmlLexem) $1;
	    Vector theOperations = (Vector) $2;
	    res.setOperationList(theOperations);
	    res.setPosLexem(lexOPR);
	    $$ = res; }
	    
	| LEX_OPERATIONS ListOfOperationDefinitions ';'
	  { OmlOperationDefinitions res = new OmlOperationDefinitions();
	    OmlLexem lexOPR = (OmlLexem) $1;
	    Vector theOperations = (Vector) $2;
	    res.setOperationList(theOperations);
	    res.setPosLexem(lexOPR);
	    $$ = res; }
	;
	
ListOfOperationDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| AsyncStaticAccess OperationDefinition
	  { Vector res = new Vector();
	    OmlAccessDefinition theAccess = (OmlAccessDefinition) $1;
	    OmlOperationDefinition theOperation = (OmlOperationDefinition) $2;
	    theOperation.setAccess(theAccess);
	    res.add(theOperation);
	    $$ = res; }
	    
	| ListOfOperationDefinitions ';' AsyncStaticAccess OperationDefinition
	  { Vector res = (Vector) $1;
	    OmlAccessDefinition theAccess = (OmlAccessDefinition) $3;
	    OmlOperationDefinition theOperation = (OmlOperationDefinition) $4;
	    theOperation.setAccess(theAccess);
	    res.add(theOperation);
	    $$ = res; }
	;

AsyncStaticAccess:
	  StaticAccess
	  { $$ = $1; }
	  
	| LEX_ASYNC StaticAccess
	  { OmlAccessDefinition res = (OmlAccessDefinition) $2;
	    OmlLexem lexSNC = (OmlLexem) $1;
	    res.setAsyncAccess(true);
	    res.setPosLexem(lexSNC);
	    $$ = res; }
	;
		
OperationDefinition:
	  ImplOperationDefinition
	  { OmlOperationDefinition res = new OmlOperationDefinition();
	    OmlOperationShape theShape = (OmlOperationShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }

	| ExplOperationDefinition
	  { OmlOperationDefinition res = new OmlOperationDefinition();
	    OmlOperationShape theShape = (OmlOperationShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }

	| ExtExplOperationDefinition
	  { OmlOperationDefinition res = new OmlOperationDefinition();
	    OmlOperationShape theShape = (OmlOperationShape) $1;
	    res.setShape(theShape);
	    res.setPosNode(theShape);
	    $$ = res; }
	;
	
ImplOperationDefinition:
	  Identifier ParameterTypes OperationTrailer
	  { OmlImplicitOperation res = new OmlImplicitOperation();
	    OmlLexem id = (OmlLexem) $1;
	    Vector theParms = (Vector) $2;
	    OmlOperationTrailer theTrailer = (OmlOperationTrailer) $3;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(theParms);
	    if ( !theTrailer.hasPostExpression() ) {
	    	yyerror ("implicit operation definition requires a post condition");
	    }
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier ParameterTypes IdentifierTypePairList OperationTrailer
	  { OmlImplicitOperation res = new OmlImplicitOperation();
	    OmlLexem id = (OmlLexem) $1;
	    Vector theParms = (Vector) $2;
	    Vector idTpPairs = (Vector) $3;
	    OmlOperationTrailer theTrailer = (OmlOperationTrailer) $4;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(theParms);
	    res.setIdentifierTypePairList(idTpPairs);
	    if ( !theTrailer.hasPostExpression() ) {
	    	yyerror ("implicit operation definition requires a post condition");
	    }
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	;
	
ExplOperationDefinition:
	  Identifier ':' OperationType Identifier Parameters LEX_IS_DEFINED_AS OperationBody OperationTrailer
	  { OmlExplicitOperation res = new OmlExplicitOperation();
	    OmlLexem id1 = (OmlLexem) $1;
	    OmlOperationType theType = (OmlOperationType) $3;
	    OmlLexem id2 = (OmlLexem) $4;
	    OmlParameter theParm = (OmlParameter) $5;
	    OmlOperationBody theBody = (OmlOperationBody) $7;
	    OmlOperationTrailer theTrailer = (OmlOperationTrailer) $8;
	    if ( id1.getText().compareTo(id2.getText()) != 0) {
	    	yyerror ("identifiers are different in explicit operation definition");
	    }
	    res.setIdentifier(lexemToString(id1));
	    res.setType(theType);
	    res.setParameterList(theParm.getPatternList());
	    res.setBody(theBody);
	    if ( theTrailer.hasExternals() ) {
	    	yyerror ("externals are not allowed in an explicit operation definition");
	    }
	    if ( theTrailer.hasExceptions() ) {
	    	yyerror ("exceptions are not allowed in an explicit operation definition");
	    }
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id1);
	    $$ = res; }
	;
	
ExtExplOperationDefinition:
	  Identifier ParameterTypes LEX_IS_DEFINED_AS OperationBody OperationTrailer
	  { OmlExtendedExplicitOperation res = new OmlExtendedExplicitOperation();
	    OmlLexem id = (OmlLexem) $1;
	    Vector parmTypes = (Vector) $2;
	    OmlOperationBody theBody = (OmlOperationBody) $4;
	    OmlOperationTrailer theTrailer = (OmlOperationTrailer) $5;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(parmTypes);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier ParameterTypes IdentifierTypePairList LEX_IS_DEFINED_AS OperationBody OperationTrailer
	  { OmlExtendedExplicitOperation res = new OmlExtendedExplicitOperation();
	    OmlLexem id = (OmlLexem) $1;
	    Vector parmTypes = (Vector) $2;
	    Vector idTpPairs = (Vector) $3;
	    OmlOperationBody theBody = (OmlOperationBody) $5;
	    OmlOperationTrailer theTrailer = (OmlOperationTrailer) $6;
	    res.setIdentifier(lexemToString(id));
	    res.setPatternTypePairList(parmTypes);
	    res.setIdentifierTypePairList(idTpPairs);
	    res.setBody(theBody);
	    res.setTrailer(theTrailer);
	    res.setPosLexem(id);
	    $$ = res; }
	;

OperationTrailer:
	  OptionalExternals OptionalPrecondition OptionalPostcondition OptionalExceptions
	  { OmlOperationTrailer res = new OmlOperationTrailer();
	    OmlExternals theExternals = (OmlExternals) $1;
	    OmlExpression thePreCondition = (OmlExpression) $2;
	    OmlExpression thePostCondition = (OmlExpression) $3;
	    OmlExceptions theErrors = (OmlExceptions) $4;
	    // insert fields in reverse order to ensure left-most position information
	    if ( theErrors != null) {
	      res.setExceptions(theErrors);
	      res.setPosNode(theErrors);
	    };
	    if ( thePostCondition != null) {
	      res.setPostExpression(thePostCondition);
	      res.setPosNode(thePostCondition);
	    };
	    if ( thePreCondition != null) {
	      res.setPreExpression(thePreCondition);
	      res.setPosNode(thePreCondition);
	    };
	    if ( theExternals != null) {
	      res.setExternals(theExternals);
	      res.setPosNode(theExternals);
	    };
	    $$ = res; }
	;
	
OperationBody :
	  Statement
	  { OmlOperationBody res = new OmlOperationBody();
	    OmlStatement theStatement = (OmlStatement) $1;
	    res.setStatement(theStatement);
	    res.setNotYetSpecified(false);
	    res.setSubclassResponsibility(false);
	    res.setPosNode(theStatement);
	    $$ = res; }
	    
	| LEX_IS LEX_NOT LEX_YET LEX_SPECIFIED
	  { OmlOperationBody res = new OmlOperationBody();
	    OmlLexem lexIS = (OmlLexem) $1;
	    res.setNotYetSpecified(true);
	    res.setSubclassResponsibility(false);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	    
	| LEX_IS LEX_SUBCLASS LEX_RESPONSIBILITY
	  { OmlOperationBody res = new OmlOperationBody();
	    OmlLexem lexIS = (OmlLexem) $1;
	    res.setNotYetSpecified(false);
	    res.setSubclassResponsibility(true);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	;

OptionalPrecondition:
	  { $$ = null; }
	  
	| LEX_PRE Expression
	  { $$ = $2; }
	;
	
OptionalPostcondition:
	  { $$ = null; }
	  
	| LEX_POST Expression
	  { $$ = $2; }
	;
		
OptionalExternals:
	  { $$ = null; }
	  
	| LEX_EXT ListOfVarInformation
	  { OmlExternals res = new OmlExternals();
	    OmlLexem lexEXT = (OmlLexem) $1;
	    Vector theExternals = (Vector) $2;
	    res.setExtList(theExternals);
	    res.setPosLexem(lexEXT);
	    $$ = res; }
	;
	
ListOfVarInformation:
	  VarInformation
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| ListOfVarInformation VarInformation
	  { Vector res = (Vector) $1;
	    res.add($2);
	    $$ = res; }
	;
	
VarInformation:
	  Mode NameList
	  { OmlVarInformation res = new OmlVarInformation();
	    OmlLexem mode = (OmlLexem) $1;
	    OmlMode theMode = new OmlMode();
	    theMode.setPosLexem(mode);
	    if ( lexemToString(mode).compareTo("rd") == 0 ) {
	    	theMode.setValue(OmlModeQuotes.IQRD);
	    } else {
	    	theMode.setValue(OmlModeQuotes.IQWR);
	    }
	    res.setMode(theMode);
	    Vector theList = (Vector) $2;
	    res.setNameList(theList);
	    res.setPosLexem(mode);
	    $$ = res; }
	    
	| Mode NameList ':' Type
	  { OmlVarInformation res = new OmlVarInformation();
	    OmlLexem mode = (OmlLexem) $1;
	    OmlMode theMode = new OmlMode();
	    theMode.setPosLexem(mode);
	    if ( lexemToString(mode).compareTo("rd") == 0 ) {
	    	theMode.setValue(OmlModeQuotes.IQRD);
	    } else {
	    	theMode.setValue(OmlModeQuotes.IQWR);
	    }
	    res.setMode(theMode);
	    Vector theList = (Vector) $2;
	    res.setNameList(theList);
	    OmlType theType = (OmlType) $4;
	    res.setType(theType);
	    res.setPosLexem(mode);
	    $$ = res; }
	;
	
Mode:
	  LEX_RD
	  { $$ = $1; }
	  
	| LEX_WR
	  { $$ = $1; }
	;

OptionalExceptions:
	  { $$ = null; }
	  
	| LEX_ERRS ListOfErrors
	  { OmlExceptions res = new OmlExceptions();
	    OmlLexem lexRR = (OmlLexem) $1;
	    Vector theErrors = (Vector) $2;
	    res.setErrorList(theErrors);
	    res.setPosLexem(lexRR);
	    $$ = res; }
	;
	
ListOfErrors:
	  Identifier ':' Expression LEX_ARROW Expression
	  { Vector res = new Vector();
	    OmlLexem id = (OmlLexem) $1;
	    OmlExpression theLhs = (OmlExpression) $3;
	    OmlExpression theRhs = (OmlExpression) $5;
	    OmlError theError = new OmlError();
	    theError.setIdentifier(lexemToString(id));
	    theError.setLhs(theLhs);
	    theError.setRhs(theRhs);
	    theError.setPosLexem(id);
	    res.add(theError);
	    $$ = res; }
	    
	| ListOfErrors Identifier ':' Expression LEX_ARROW Expression
	  { Vector res = (Vector) $1;
	    OmlLexem id = (OmlLexem) $2;
	    OmlExpression theLhs = (OmlExpression) $4;
	    OmlExpression theRhs = (OmlExpression) $6;
	    OmlError theError = new OmlError();
	    theError.setIdentifier(lexemToString(id));
	    theError.setLhs(theLhs);
	    theError.setRhs(theRhs);
	    theError.setPosLexem(id);
	    res.add(theError);
	    $$ = res; }
	;

/*************************************/
/*** INSTANCE VARIABLE DEFINITIONS ***/
/*************************************/

InstanceVariableDefinitions:
	  LEX_INSTANCE LEX_VARIABLES
	  { OmlInstanceVariableDefinitions res = new OmlInstanceVariableDefinitions();
	    OmlLexem lexINST = (OmlLexem) $1;
	    res.setPosLexem(lexINST);
	    $$ = res; }
	  
	| LEX_INSTANCE LEX_VARIABLES InstanceVariableDefinitionList
	  { OmlInstanceVariableDefinitions res = new OmlInstanceVariableDefinitions();
	    OmlLexem lexINST = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $3;
	    res.setVariablesList(theDefinitions);
	    res.setPosLexem(lexINST);
	    $$ = res; }
	    
	| LEX_INSTANCE LEX_VARIABLES InstanceVariableDefinitionList ';'
	  { OmlInstanceVariableDefinitions res = new OmlInstanceVariableDefinitions();
	    OmlLexem lexINST = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $3;
	    res.setVariablesList(theDefinitions);
	    res.setPosLexem(lexINST);
	    $$ = res; }	    
	;
	
InstanceVariableDefinitionList:
	  error
	  { $$ = new Vector(); }
	  
	| InstanceVariableDefinition
	  { Vector res = new Vector();
	    OmlInstanceVariableShape theShape = (OmlInstanceVariableShape) $1;
	    res.add(theShape);
	    $$ = res; }
	    
	| InstanceVariableDefinitionList ';' InstanceVariableDefinition
	  { Vector res = (Vector) $1;
	    OmlInstanceVariableShape theShape = (OmlInstanceVariableShape) $3;
	    res.add(theShape);
	    $$ = res; }
	;
	
InstanceVariableDefinition:
	  InvariantDefinition
	  { $$ = $1; }
	  
	| StaticAccess AssignmentDefinition
	  { OmlInstanceVariable res = new OmlInstanceVariable();
	    OmlAccessDefinition theAccessDefinition = (OmlAccessDefinition) $1;
	    OmlAssignmentDefinition theAssignment = (OmlAssignmentDefinition) $2;
	    res.setAccess(theAccessDefinition);
	    res.setAssignmentDefinition(theAssignment);
	    res.setPosNode(theAccessDefinition);
	    $$ = res; }
	;

InvariantDefinition:
	  LEX_INV Expression
	  { OmlInstanceVariableInvariant res = new OmlInstanceVariableInvariant();
	    OmlLexem lexINV = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    res.setInvariant(theExpression);
	    res.setPosLexem(lexINV);
	    $$ = res; }
	;

AssignmentDefinition:
	  Identifier ':' Type
	  { OmlAssignmentDefinition res = new OmlAssignmentDefinition();
	    OmlLexem id = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    res.setIdentifier(lexemToString(id));
	    res.setType(theType);
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier ':' Type LEX_ASSIGN Expression
	  { OmlAssignmentDefinition res = new OmlAssignmentDefinition();
	    OmlLexem id = (OmlLexem) $1;
	    OmlType theType = (OmlType) $3;
	    OmlExpression theExpression = (OmlExpression) $5;
	    res.setIdentifier(lexemToString(id));
	    res.setType(theType);
	    res.setExpression(theExpression);
	    res.setPosLexem(id);
	    $$ = res; }
	;

/***********************************/
/*** SYNCHRONIZATION DEFINITIONS ***/
/***********************************/

SynchronizationDefinitions:
	  LEX_SYNC
	  { OmlSynchronizationDefinitions res = new OmlSynchronizationDefinitions();
	    OmlLexem lexSNC = (OmlLexem) $1;
	    res.setPosLexem(lexSNC);
	    $$ = res; }
	  
	| LEX_SYNC DeclarativeSyncList
	  { OmlSynchronizationDefinitions res = new OmlSynchronizationDefinitions();
	    OmlLexem lexSNC = (OmlLexem) $1;
	    Vector theDeclarations = (Vector) $2;
	    res.setSyncList(theDeclarations);
	    res.setPosLexem(lexSNC);
	    $$ = res; }
	    
	| LEX_SYNC DeclarativeSyncList ';'
	  { OmlSynchronizationDefinitions res = new OmlSynchronizationDefinitions();
	    OmlLexem lexSNC = (OmlLexem) $1;
	    Vector theDeclarations = (Vector) $2;
	    res.setSyncList(theDeclarations);
	    res.setPosLexem(lexSNC);
	    $$ = res; }
	;
	
DeclarativeSyncList:
	  error
	  { $$ = new Vector(); }
	  
	| DeclarativeSync
	  { Vector res = new Vector();
	    OmlSyncPredicate thePredicate = (OmlSyncPredicate) $1;
	    res.add(thePredicate);
	    $$ = res; }
	    
	| DeclarativeSyncList ';' DeclarativeSync
	  { Vector res = (Vector) $1;
	    OmlSyncPredicate thePredicate = (OmlSyncPredicate) $3;
	    res.add(thePredicate);
	    $$ = res; }
	;
	
DeclarativeSync:
	  PermissionStmt
	  { $$ = $1; }
	  
	| MutexStmt
	  { $$ = $1; }
	;
	
PermissionStmt:
	  LEX_PER Name LEX_IMPLY Expression
	  { OmlPermissionPredicate res = new OmlPermissionPredicate();
	    OmlLexem lexPER = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setName(theName);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexPER);
	    $$ = res; }
	;
	
MutexStmt:
	  LEX_MUTEX '(' LEX_ALL ')'
	  { OmlMutexAllPredicate res = new OmlMutexAllPredicate();
	    OmlLexem lexMTX = (OmlLexem) $1;
	    res.setPosLexem(lexMTX);
	    $$ = res; }
	  
	| LEX_MUTEX '(' NameList ')'
	  { OmlMutexPredicate res = new OmlMutexPredicate();
	    OmlLexem lexMTX = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexMTX);
	    $$ = res; }
	;

/**************************/
/*** THREAD DEFINITIONS ***/
/**************************/

ThreadDefinitions:
	  LEX_THREAD
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    res.setPosLexem(lexTHR);
	    $$ = res; }
	  
	| LEX_THREAD error
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    res.setPosLexem(lexTHR);
	    $$ = res; }
	  
	| LEX_THREAD PeriodicObligation
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlPeriodicThread theThreadSpec = (OmlPeriodicThread) $2;
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }
	    
	| LEX_THREAD PeriodicObligation ';'
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlPeriodicThread theThreadSpec = (OmlPeriodicThread) $2;
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }

	/* next rule is added for Marcel Verhoef */
	| LEX_THREAD SporadicObligation
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlSporadicThread theThreadSpec = (OmlSporadicThread) $2;
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }

	/* next rule is added for Marcel Verhoef */	    
	| LEX_THREAD SporadicObligation ';'
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlSporadicThread theThreadSpec = (OmlSporadicThread) $2;
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }

	| LEX_THREAD Statement
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlProcedureThread theThreadSpec = new OmlProcedureThread();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlStatement theStatement = (OmlStatement) $2;
	    theThreadSpec.setStatement(theStatement);
	    theThreadSpec.setPosLexem(lexTHR);
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }
	    
	| LEX_THREAD Statement ';'
	  { OmlThreadDefinition res = new OmlThreadDefinition();
	    OmlProcedureThread theThreadSpec = new OmlProcedureThread();
	    OmlLexem lexTHR = (OmlLexem) $1;
	    OmlStatement theStatement = (OmlStatement) $2;
	    theThreadSpec.setStatement(theStatement);
	    theThreadSpec.setPosLexem(lexTHR);
	    res.setThreadSpecification(theThreadSpec);
	    res.setPosLexem(lexTHR);
	    $$ = res; }
	;
	
PeriodicObligation:
	  LEX_PERIODIC '(' NonEmptyExpressionList ')' '(' Name ')'
	  { OmlPeriodicThread res = new OmlPeriodicThread();
	    OmlLexem lexPER = (OmlLexem) $1;
	    Vector theExpressions = (Vector) $3;
	    OmlName theName = (OmlName) $6;
	    res.setArgs(theExpressions);
	    res.setName(theName);
	    res.setPosLexem(lexPER);
	    $$ = res; }
	;

/* next rule is added for Marcel Verhoef */
SporadicObligation:
	  LEX_SPORADIC '(' NonEmptyExpressionList ')' '(' Name ')'
	  { OmlSporadicThread res = new OmlSporadicThread();
	    OmlLexem lexSP = (OmlLexem) $1;
	    Vector theExpressions = (Vector) $3;
	    OmlName theName = (OmlName) $6;
	    res.setArgs(theExpressions);
	    res.setName(theName);
	    res.setPosLexem(lexSP);
	    $$ = res; }
	;

/*************************/
/*** TRACE DEFINITIONS ***/
/*************************/

/* experimental rules added for Adriana Sucena */

TraceDefinitions:
	  LEX_TRACES
	  { OmlTraceDefinitions res = new OmlTraceDefinitions();
	    OmlLexem lexTR = (OmlLexem) $1;
	    res.setPosLexem(lexTR);
	    $$ = res;}
	  
	| LEX_TRACES NamedTraceList
	  { OmlTraceDefinitions res = new OmlTraceDefinitions();
	    OmlLexem lexTR = (OmlLexem) $1;
	    Vector ntl = (Vector) $2;
	    res.setTraces(ntl);
	    res.setPosLexem(lexTR);
	    $$ = res; }
	;

NamedTraceList:
	  NamedTrace
	  { Vector res = new Vector();
	    OmlNamedTrace nt = (OmlNamedTrace) $1;
	    res.add(nt);
	    $$ = res; }
	    
	| NamedTraceList NamedTrace
	  { Vector res = (Vector) $1;
	    OmlNamedTrace nt = (OmlNamedTrace) $2;
	    res.add(nt);
	    $$ = res; }
	;
	
NamedTrace:
	  Identifier ':' TraceDefinitionList
	  { OmlNamedTrace res = new OmlNamedTrace();
	    OmlLexem nm = (OmlLexem) $1;
	    OmlLexem lexSC = (OmlLexem) $2;
	    OmlTraceDefinition tdl = (OmlTraceDefinition) $3;
	    res.setName(lexemToString(nm));
	    res.setDefs(tdl);
	    res.setPosLexem(lexSC);
	    $$ = res; }
	;
	 
TraceDefinitionList:
	  TraceDefinitionTerm
	  { $$ = $1; }
	  
	| TraceDefinitionList ';' TraceDefinitionTerm
	  { OmlTraceDefinition td1 = (OmlTraceDefinition) $1;
	    OmlLexem lexSC = (OmlLexem) $2;
	    OmlTraceDefinition td2 = (OmlTraceDefinition) $3;
	    OmlTraceSequenceDefinition res;
	    if (td1 instanceof OmlTraceSequenceDefinition) {
	      res = (OmlTraceSequenceDefinition) td1;
	    } else {
	      res = new OmlTraceSequenceDefinition();
	      res.getDefs().add(td1);
	    }
	    res.getDefs().add(td2);
	    res.setPosLexem(lexSC);
	    $$ = res; }
	;
	
TraceDefinitionTerm:
	  TraceDefinition
	  { $$ = $1; }
	  
	| TraceDefinitionTerm '|' TraceDefinition
	  { OmlTraceDefinition td1 = (OmlTraceDefinition) $1;
	    OmlLexem lexVB = (OmlLexem) $2;
	    OmlTraceDefinitionItem tdi2 = (OmlTraceDefinitionItem) $3;
	    OmlTraceChoiceDefinition res;
	    if (td1 instanceof OmlTraceChoiceDefinition) {
	      res = (OmlTraceChoiceDefinition) td1;
	    } else {
	      res = new OmlTraceChoiceDefinition();
	      res.getDefs().add(td1);
	    }
	    res.getDefs().add(tdi2);
	    res.setPosLexem(lexVB);
	    $$ = res; }
	;
	
TraceDefinition:
	  TraceCoreDefinition
	  { OmlTraceDefinitionItem res = new OmlTraceDefinitionItem();
	    OmlTraceCoreDefinition core = (OmlTraceCoreDefinition) $1;
	    res.setTest(core);
	    res.setPosNode(core);
	    $$ = res; }
	    
	| TraceBindings TraceCoreDefinition
	  { OmlTraceDefinitionItem res = new OmlTraceDefinitionItem();
	    Vector binds = (Vector) $1;
	    OmlTraceCoreDefinition core = (OmlTraceCoreDefinition) $2;
	    res.setBind(binds);
	    res.setTest(core);
	    res.setPosNode(core);
	    $$ = res; }

	| TraceCoreDefinition TraceRepeatPattern
	  { OmlTraceDefinitionItem res = new OmlTraceDefinitionItem();
	    OmlTraceCoreDefinition core = (OmlTraceCoreDefinition) $1;
	    OmlTraceRepeatPattern pat = (OmlTraceRepeatPattern) $2;
	    res.setTest(core);
	    res.setRegexpr(pat);
	    res.setPosNode(core);
	    $$ = res; }
	    
	| TraceBindings TraceCoreDefinition TraceRepeatPattern
	  { OmlTraceDefinitionItem res = new OmlTraceDefinitionItem();
	    Vector binds = (Vector) $1;
	    OmlTraceCoreDefinition core = (OmlTraceCoreDefinition) $2;
	    OmlTraceRepeatPattern pat = (OmlTraceRepeatPattern) $3;
	    res.setBind(binds);
	    res.setTest(core);
	    res.setRegexpr(pat);
	    res.setPosNode(core);
	    $$ = res; }
	;

TraceCoreDefinition:
	  TraceApplyExpression
	  { $$ = $1; }
	  
	| TraceBracketedExpression
	  { $$ = $1; }
	;	
	
TraceApplyExpression:
	  Identifier '.' Identifier '(' ExpressionList ')'
	  { OmlTraceMethodApply res = new OmlTraceMethodApply();
	    OmlLexem vid = (OmlLexem) $1;
	    OmlLexem mid = (OmlLexem) $3;
	    Vector args = (Vector) $5;
	    res.setVariableName(lexemToString(vid));
	    res.setMethodName(lexemToString(mid));
	    res.setArgs(args);
	    res.setPosLexem(vid);
	    $$ = res; }
	;

TraceRepeatPattern:
	  '*'
	  { OmlTraceZeroOrMore res = new OmlTraceZeroOrMore();
	    OmlLexem lexSTAR = (OmlLexem) $1;
	    res.setPosLexem(lexSTAR);
	    $$ = res; }
	    
	| '+'
	  { OmlTraceOneOrMore res = new OmlTraceOneOrMore();
	    OmlLexem lexPLUS = (OmlLexem) $1;
	    res.setPosLexem(lexPLUS);
	    $$ = res; }
	    
	| '?'
	  { OmlTraceZeroOrOne res = new OmlTraceZeroOrOne();
	    OmlLexem lexQM = (OmlLexem) $1;
	    res.setPosLexem(lexQM);
	    $$ = res; }
	
	| '{' NumericLiteral '}'
	  { OmlTraceRange res = new OmlTraceRange();
	    OmlLexem lexLB = (OmlLexem) $1;
	    OmlNumericLiteral lower = (OmlNumericLiteral) $2;
	    res.setLower(lower);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	    
	| '{' NumericLiteral ',' NumericLiteral '}'
	  { OmlTraceRange res = new OmlTraceRange();
	    OmlLexem lexLB = (OmlLexem) $1;
	    OmlNumericLiteral lower = (OmlNumericLiteral) $2;
	    OmlNumericLiteral upper = (OmlNumericLiteral) $4;
	    res.setLower(lower);
	    res.setUpper(upper);
	    res.setPosLexem(lexLB);
	    $$ = res; }	    
	;	  
	
TraceBracketedExpression:
	  '(' TraceDefinitionList ')'
	  { OmlTraceBracketedDefinition res = new OmlTraceBracketedDefinition();
	    OmlLexem lexLP = (OmlLexem) $1;
	    OmlTraceDefinition theDef = (OmlTraceDefinition) $2;
	    res.setDefinition(theDef);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	;

TraceBindings:
	  TraceBinding
	  { Vector res = new Vector();
	    OmlTraceBinding testBind = (OmlTraceBinding) $1;
	    res.add(testBind);
	    $$ = res; }
	    
	| TraceBindings TraceBinding
	  { Vector res = (Vector) $1;
	    OmlTraceBinding testBind = (OmlTraceBinding) $2;
	    res.add(testBind);
	    $$ = res; }	
	;
	
TraceBinding:
	  LEX_LET ListOfLocalDefinitions LEX_IN
	  { OmlTraceLetBinding res = new OmlTraceLetBinding();
	    OmlLexem lexLET = (OmlLexem) $1;
	    Vector theDefs = (Vector) $2;
	    res.setDefinitionList(theDefs);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	  
	| LEX_LET Bind LEX_IN 
	  { OmlTraceLetBeBinding res = new OmlTraceLetBeBinding();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    res.setBind(theBind);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	
	| LEX_LET Bind LEX_BE LEX_ST Expression LEX_IN 
	  { OmlTraceLetBeBinding res = new OmlTraceLetBeBinding();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression bestExpr = (OmlExpression) $5;
	    res.setBind(theBind);
	    res.setBest(bestExpr);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	;
	 
/******************/
/*** STATEMENTS ***/
/******************/

Statement:
	  BlockStatement
	| AlwaysStatement
	| TrapStatement
	| RecursiveTrapStatement
	| CallStatement
	| AssignStatement
	| AtomicAssignStatement
	| ErrorStatement
	| NondeterministicStatement
	| ExitStatement
	| IdentityStatement
	| DefStatement 
	| LetStatement
	| LetBeStatement
	| SequenceForLoop
	| SetForLoop
	| IndexForLoop
	| WhileLoop
	| IfStatement
	| CasesStatement
	| ReturnStatement
	| SpecificationStatement
	| StartStatement
	| StartListStatement
	| DurationStmt
	| CyclesStmt
	;

/*** BLOCK STATEMENT ***/

BlockStatement:
	  '(' SequenceOfStatements ')'
	  { OmlBlockStatement res = new OmlBlockStatement();
	    OmlLexem lexLP = (OmlLexem) $1;
	    Vector theStatements = (Vector) $2;
	    res.setStatementList(theStatements);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	    
	| '(' SequenceOfStatements ';' ')'
	  { OmlBlockStatement res = new OmlBlockStatement();
	    OmlLexem lexLP = (OmlLexem) $1;
	    Vector theStatements = (Vector) $2;
	    res.setStatementList(theStatements);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	    
	| '(' DclStatementList SequenceOfStatements ')'
	  { OmlBlockStatement res = new OmlBlockStatement();
	    OmlLexem lexLP = (OmlLexem) $1;
	    Vector theDclStatements = (Vector) $2;
	    Vector theStatements = (Vector) $3;
	    res.setDclStatementList(theDclStatements);
	    res.setStatementList(theStatements);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	    
	| '(' DclStatementList SequenceOfStatements ';' ')'
	  { OmlBlockStatement res = new OmlBlockStatement();
	    OmlLexem lexLP = (OmlLexem) $1;
	    Vector theDclStatements = (Vector) $2;
	    Vector theStatements = (Vector) $3;
	    res.setDclStatementList(theDclStatements);
	    res.setStatementList(theStatements);
	    res.setPosLexem(lexLP);
	    $$ = res; }	    
	;

DclStatementList:
	  DclStatement
	  { Vector res = new Vector();
	    OmlDclStatement theDclStatement = (OmlDclStatement) $1;
	    res.add(theDclStatement);
	    $$ = res; }
	    
	| DclStatementList DclStatement
	  { Vector res = (Vector) $1;
	    OmlDclStatement theDclStatement = (OmlDclStatement) $2;
	    res.add(theDclStatement);
	    $$ = res; }
	;
	
DclStatement:
	  LEX_DCL AssignmentDefinitionList ';'
	  { OmlDclStatement res = new OmlDclStatement();
	    OmlLexem lexDCL = (OmlLexem) $1;
	    Vector theAssignmentDefinitions = (Vector) $2;
	    res.setDefinitionList(theAssignmentDefinitions);
	    res.setPosLexem(lexDCL);
	    $$ = res; }
	;

AssignmentDefinitionList:
	  AssignmentDefinition
	  { Vector res = new Vector();
	    OmlAssignmentDefinition theAssignmentDefinition = (OmlAssignmentDefinition) $1;
	    res.add(theAssignmentDefinition);
	    $$ = res; }
	    
	| AssignmentDefinitionList ',' AssignmentDefinition
	  { Vector res = (Vector) $1;
	    OmlAssignmentDefinition theAssignmentDefinition = (OmlAssignmentDefinition) $3;
	    res.add(theAssignmentDefinition);
	    $$ = res; }
	;
	
SequenceOfStatements:
	  error
	  { $$ = new Vector(); }
	  
	| Statement
	  { Vector res = new Vector();
	    OmlStatement theStatement = (OmlStatement) $1;
	    res.add(theStatement);
	    $$ = res; }
	    
	| SequenceOfStatements ';' Statement
	  { Vector res = (Vector) $1;
	    OmlStatement theStatement = (OmlStatement) $3;
	    res.add(theStatement);
	    $$ = res; }
	;

/*** ALWAYS STATEMENT ***/

AlwaysStatement:
	  LEX_ALWAYS Statement LEX_IN Statement
	  { OmlAlwaysStatement res = new OmlAlwaysStatement();
	    OmlLexem lexWYS = (OmlLexem) $1;
	    OmlStatement aStatement = (OmlStatement) $2;
	    OmlStatement iStatement = (OmlStatement) $4;
	    res.setAlwaysPart(aStatement);
	    res.setInPart(iStatement);
	    res.setPosLexem(lexWYS);
	    $$ = res; }
	;

/*** TRAP STATEMENT ***/

TrapStatement:
	  LEX_TRAP PatternBind LEX_WITH Statement LEX_IN Statement
	  { OmlTrapStatement res = new OmlTrapStatement();
	    OmlLexem lexTRP = (OmlLexem) $1;
	    OmlPatternBind thePatternBind = (OmlPatternBind) $2;
	    OmlStatement theWithStatement = (OmlStatement) $4;
	    OmlStatement theInStatement = (OmlStatement) $6;
	    res.setPatternBind(thePatternBind);
	    res.setWithPart(theWithStatement);
	    res.setInPart(theInStatement);
	    res.setPosLexem(lexTRP);
	    $$ = res; }
	;

/*** RECURSIVE TRAP STATEMENT ***/
	
RecursiveTrapStatement:
	  LEX_TIXE '{' ListOfPatternBindStatements '}' LEX_IN Statement
	  { OmlRecursiveTrapStatement res = new OmlRecursiveTrapStatement();
	    OmlLexem lexTIXE = (OmlLexem) $1;
	    Vector theTraps = (Vector) $3;
	    OmlStatement theInStatement = (OmlStatement) $6;
	    res.setTrapList(theTraps);
	    res.setInPart(theInStatement);
	    res.setPosLexem(lexTIXE);
	    $$ = res; }
	;
	
ListOfPatternBindStatements:
	  PatternBindStatement
	  { Vector res = new Vector();
	    OmlTrapDefinition theTraps = (OmlTrapDefinition) $1;
	    res.add(theTraps);
	    $$ = res; }
	    
	| ListOfPatternBindStatements ',' PatternBindStatement
	  { Vector res = (Vector) $1;
	    OmlTrapDefinition theTraps = (OmlTrapDefinition) $3;
	    res.add(theTraps);
	    $$ = res; }
	;
	
PatternBindStatement:
	  PatternBind LEX_BAR_ARROW Statement
	  { OmlTrapDefinition res = new OmlTrapDefinition();
	    OmlPatternBind thePatternBind = (OmlPatternBind) $1;
	    OmlLexem lexBA = (OmlLexem) $2;
	    OmlStatement theStatement = (OmlStatement) $3;
	    res.setPatternBind(thePatternBind);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexBA);
	    $$ = res; }
	;

/*** CALL STATEMENT ***/

CallStatement:
	  ObjectDesignator
	  { OmlObjectDesignator theDesignator = (OmlObjectDesignator) $1;
	    OmlCallStatement res = checkCallStatement(theDesignator);
	    res.setPosNode(theDesignator);
	    $$ = res; }
	  ;
	    
ObjectDesignator:
	  Name
	  { OmlObjectDesignatorExpression res = new OmlObjectDesignatorExpression();
	    OmlName theName = (OmlName) $1;
	    res.setExpression(theName);
	    res.setPosNode(theName);
	    $$ = res; }
	    
	| SelfExpression
	  { OmlObjectDesignatorExpression res = new OmlObjectDesignatorExpression();
	    OmlSelfExpression theSelfExpr = (OmlSelfExpression) $1;
	    res.setExpression(theSelfExpr);
	    res.setPosNode(theSelfExpr);
	    $$ = res; }
	    
	| NewExpression
	  { OmlObjectDesignatorExpression res = new OmlObjectDesignatorExpression();
	    OmlNewExpression theNewExpr = (OmlNewExpression) $1;
	    res.setExpression(theNewExpr);
	    res.setPosNode(theNewExpr);
	    $$ = res; }
	    
	| ObjectDesignator '.' Name
	  { OmlObjectFieldReference res = new OmlObjectFieldReference();
	    OmlObjectDesignator theDesignator = (OmlObjectDesignator) $1;
	    OmlLexem lexDOT = (OmlLexem) $2;
	    OmlName theName = (OmlName) $3;
	    res.setObjectDesignator(theDesignator);
	    res.setName(theName);
	    res.setPosLexem(lexDOT);
	    $$ = res; }
	    
	| ObjectDesignator '(' ExpressionList ')'
	  { OmlObjectApply res = new OmlObjectApply();
	    OmlObjectDesignator theDesignator = (OmlObjectDesignator) $1;
	    OmlLexem lexLP = (OmlLexem) $2;
	    Vector exprList = (Vector) $3;
	    res.setObjectDesignator(theDesignator);
	    res.setExpressionList(exprList);
	    res.setPosLexem(lexLP);	    
	    $$ = res; }
	;

/*** ASSIGN STATEMENT ***/
	  
AssignStatement:
	  ObjectDesignator LEX_ASSIGN Expression
	  { OmlAssignStatement res = new OmlAssignStatement();
	    OmlObjectDesignator theObjDesignator = (OmlObjectDesignator) $1;
	    OmlLexem lexASS = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setStateDesignator(checkStateDesignator(theObjDesignator));
	    res.setExpression(theExpression);
	    res.setPosLexem(lexASS);
	    $$ = res; }
	;
	
/*** ATOMIC ASSIGN STATEMENT ***/

AtomicAssignStatement:
	  LEX_ATOMIC '(' AssignStatementList ')'
	  { OmlAtomicStatement res = new OmlAtomicStatement();
	    OmlLexem lexATM = (OmlLexem) $1;
	    Vector theAssignments = (Vector) $3;
	    if ( theAssignments.size() <= 1 ) yyerror ("multiple assign statement shall have at least two assignments");
	    res.setAssignmentList(theAssignments);
	    res.setPosLexem(lexATM);
	    $$ = res; }
	    
	| LEX_ATOMIC '(' AssignStatementList ';' ')'
	  { OmlAtomicStatement res = new OmlAtomicStatement();
	    OmlLexem lexATM = (OmlLexem) $1;
	    Vector theAssignments = (Vector) $3;
	    if ( theAssignments.size() <= 1 ) yyerror ("multiple assign statement shall have at least two assignments");
	    res.setAssignmentList(theAssignments);
	    res.setPosLexem(lexATM);
	    $$ = res; }
	;

AssignStatementList:
	  error
	  { $$ = new Vector(); }
	  
	| AssignStatement
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| AssignStatementList ';' AssignStatement
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;

/*** ERROR STATEMENT ***/
	
ErrorStatement:
	  LEX_ERROR
	  { OmlErrorStatement res = new OmlErrorStatement();
	    OmlLexem lexRR = (OmlLexem) $1;
	    res.setPosLexem(lexRR);
	    $$ = res; }
	;

/*** NON DETERMINISTIC STATEMENT ***/

NondeterministicStatement:
	  LEX_NONDET '(' ListOfStatements ')'
	  { OmlNondeterministicStatement res = new OmlNondeterministicStatement();
	    OmlLexem lexND = (OmlLexem) $1;
	    Vector theStatements = (Vector) $3;
	    res.setStatementList(theStatements);
	    res.setPosLexem(lexND);
	    $$ = res; }
	  ;

ListOfStatements:
	  error
	  { $$ = new Vector(); }
	  
	| Statement
	  { Vector res = new Vector();
	    OmlStatement theStatement = (OmlStatement) $1;
	    res.add(theStatement);
	    $$ = res; }
	    
	| ListOfStatements ',' Statement
	  { Vector res = (Vector) $1;
	    OmlStatement theStatement = (OmlStatement) $3;
	    res.add(theStatement);
	    $$ = res; }
	;

/*** EXIT STATEMENT ***/

ExitStatement:
	  LEX_EXIT
	  { OmlExitStatement res = new OmlExitStatement();
	    OmlLexem lexXT = (OmlLexem) $1;
	    res.setPosLexem(lexXT);
	    $$ = res; }
	  
	| LEX_EXIT Expression
	  { OmlExitStatement res = new OmlExitStatement();
	    OmlLexem lexXT = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexXT);
	    $$ = res; }
	;

/*** IDENTITY STATEMENT ***/
	
IdentityStatement:
	  LEX_SKIP
	  { OmlSkipStatement res = new OmlSkipStatement();
	    OmlLexem lexSKP = (OmlLexem) $1;
	    res.setPosLexem(lexSKP);
	    $$ = res; }
	;

/*** DEF STATEMENT ***/

DefStatement:
	  LEX_DEF ListOfEqualsDefinitions LEX_IN Statement
	  { OmlDefStatement res = new OmlDefStatement();
	    OmlLexem lexDEF = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    OmlStatement theStatement = (OmlStatement) $4;
	    res.setDefinitionList(theDefinitions);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexDEF);
	    $$ = res; }
	    
	| LEX_DEF ListOfEqualsDefinitions ';' LEX_IN Statement
	  { OmlDefStatement res = new OmlDefStatement();
	    OmlLexem lexDEF = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    OmlStatement theStatement = (OmlStatement) $5;
	    res.setDefinitionList(theDefinitions);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexDEF);
	    $$ = res; }
	;
	
ListOfEqualsDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| EqualsDefinition
	  { Vector res = new Vector();
	    OmlEqualsDefinition theDefinition = (OmlEqualsDefinition) $1;
	    res.add(theDefinition);
	    $$ = res; }
	    
	| ListOfEqualsDefinitions ';' EqualsDefinition
	  { Vector res = (Vector) $1;
	    OmlEqualsDefinition theDefinition = (OmlEqualsDefinition) $3;
	    res.add(theDefinition);
	    $$ = res; }
	;
	
EqualsDefinition:
	  PatternBind LEX_EQUAL Expression
	  { OmlEqualsDefinition res = new OmlEqualsDefinition();
	    OmlPatternBind thePatternBind = (OmlPatternBind) $1;
	    OmlLexem lexEQ = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setPatternBind(thePatternBind);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexEQ);
	    $$ = res; }
	;

/*** LET STATEMENT ***/

LetStatement:
	  LEX_LET ListOfLocalDefinitions LEX_IN Statement
	  { OmlLetStatement res = new OmlLetStatement();
	    OmlLexem lexLET = (OmlLexem) $1;
	    Vector theDefinitions = (Vector) $2;
	    OmlStatement theStatement = (OmlStatement) $4;
	    res.setDefinitionList(theDefinitions);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	;

ListOfLocalDefinitions:
	  error
	  { $$ = new Vector(); }
	  
	| ValueDefinition
	  { Vector res = new Vector();
	    OmlValueShape theValueShape = (OmlValueShape) $1;
	    res.add(theValueShape);
	    $$ = res; }
	    
	| ListOfLocalDefinitions ',' ValueDefinition
	  { Vector res = (Vector) $1;
	    OmlValueShape theValueShape = (OmlValueShape) $3;
	    res.add(theValueShape);
	    $$ = res; }
	;
	
/*** LET (BE SUCH THAT) STATEMENT ***/

LetBeStatement:
	  LEX_LET Bind LEX_IN Statement
	  { OmlLetBeStatement res = new OmlLetBeStatement();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlStatement theStatement = (OmlStatement) $4;
	    res.setBind(theBind);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	    
	| LEX_LET Bind LEX_BE LEX_ST Expression LEX_IN Statement
	  { OmlLetBeStatement res = new OmlLetBeStatement();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression theExpression = (OmlExpression) $5;
	    OmlStatement theStatement = (OmlStatement) $7;
	    res.setBind(theBind);
	    res.setBest(theExpression);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	;
	
/*** SEQUENCE FOR LOOP ***/

SequenceForLoop:
	  LEX_FOR PatternBind LEX_IN Expression LEX_DO Statement
	  { OmlSequenceForLoop res = new OmlSequenceForLoop();
	    OmlLexem lexFOR = (OmlLexem) $1;
	    OmlPatternBind thePatternBind = (OmlPatternBind) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    OmlStatement theStatement = (OmlStatement) $6;
	    res.setPatternBind(thePatternBind);
	    res.setExpression(theExpression);
	    res.setInReverse(false);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexFOR);
	    $$ = res; }
	    
	| LEX_FOR PatternBind LEX_IN LEX_REVERSE Expression LEX_DO Statement
	  { OmlSequenceForLoop res = new OmlSequenceForLoop();
	    OmlLexem lexFOR = (OmlLexem) $1;
	    OmlPatternBind thePatternBind = (OmlPatternBind) $2;
	    OmlExpression theExpression = (OmlExpression) $5;
	    OmlStatement theStatement = (OmlStatement) $7;
	    res.setPatternBind(thePatternBind);
	    res.setExpression(theExpression);
	    res.setInReverse(true);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexFOR);
	    $$ = res; }
	;

/*** SET FOR LOOP ***/

SetForLoop:
	  LEX_FOR LEX_ALL Pattern LEX_IN_SET Expression LEX_DO Statement
	  { OmlSetForLoop res = new OmlSetForLoop();
	    OmlLexem lexFOR = (OmlLexem) $1;
	    OmlPattern thePattern = (OmlPattern) $3;
	    OmlExpression theExpression = (OmlExpression) $5;
	    OmlStatement theStatement = (OmlStatement) $7;
	    res.setPattern(thePattern);
	    res.setExpression(theExpression);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexFOR);
	    $$ = res; }
	;

/*** INDEX FOR LOOP ***/
IndexForLoop:
	  LEX_FOR Identifier LEX_EQUAL Expression LEX_TO Expression LEX_DO Statement
	  { OmlIndexForLoop res = new OmlIndexForLoop();
	    OmlLexem lexFOR = (OmlLexem) $1;
	    OmlLexem id = (OmlLexem) $2;
	    OmlExpression fromExpr = (OmlExpression) $4;
	    OmlExpression toExpr = (OmlExpression) $6;
	    OmlStatement theStatement = (OmlStatement) $8;
	    res.setIdentifier(lexemToString(id));
	    res.setInitExpression(fromExpr);
	    res.setLimitExpression(toExpr);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexFOR);
	    $$ = res; }
	    
	| LEX_FOR Identifier LEX_EQUAL Expression LEX_TO Expression LEX_BY Expression LEX_DO Statement
	  { OmlIndexForLoop res = new OmlIndexForLoop();
	    OmlLexem lexFOR = (OmlLexem) $1;
	    OmlLexem id = (OmlLexem) $2;
	    OmlExpression fromExpr = (OmlExpression) $4;
	    OmlExpression toExpr = (OmlExpression) $6;
	    OmlExpression byExpr = (OmlExpression) $8;
	    OmlStatement theStatement = (OmlStatement) $10;
	    res.setIdentifier(lexemToString(id));
	    res.setInitExpression(fromExpr);
	    res.setLimitExpression(toExpr);
	    res.setByExpression(byExpr);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexFOR);
	    $$ = res; }
	;

/*** WHILE LOOP ***/

WhileLoop:
	  LEX_WHILE Expression LEX_DO Statement
	  { OmlWhileLoop res = new OmlWhileLoop();
	    OmlLexem lexWHL = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    OmlStatement theStatement = (OmlStatement) $4;
	    res.setExpression(theExpression);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexWHL);
	    $$ = res; }
	;

/*** IF STATEMENT ***/

IfStatement:	    
	  LEX_IF Expression LEX_THEN Statement ListOfElsifStatements
	  { OmlIfStatement res = new OmlIfStatement();
	    OmlLexem lexIF = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    OmlStatement thenStatement = (OmlStatement) $4;
	    Vector elseifStatements = (Vector) $5;
	    res.setExpression(theExpression);
	    res.setThenStatement(thenStatement);
	    res.setElseifStatement(elseifStatements);
	    res.setPosLexem(lexIF);
	    $$ = res; }
	
	| LEX_IF Expression LEX_THEN Statement ListOfElsifStatements LEX_ELSE Statement
	  { OmlIfStatement res = new OmlIfStatement();
	    OmlLexem lexIF = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    OmlStatement thenStatement = (OmlStatement) $4;
	    Vector elseifStatements = (Vector) $5;
	    OmlStatement elseStatement = (OmlStatement) $7;
	    res.setExpression(theExpression);
	    res.setThenStatement(thenStatement);
	    res.setElseifStatement(elseifStatements);
	    res.setElseStatement(elseStatement);
	    res.setPosLexem(lexIF);
	    $$ = res; }
	;

ListOfElsifStatements:
	  { $$ = new Vector(); }
	  
	| ListOfElsifStatements LEX_ELSEIF Expression LEX_THEN Statement
	  { Vector res = (Vector) $1;
	    OmlElseIfStatement elseifStatement = new OmlElseIfStatement();
	    OmlLexem lexEIE = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    OmlStatement theStatement = (OmlStatement) $5;
	    elseifStatement.setExpression(theExpression);
	    elseifStatement.setStatement(theStatement);
	    elseifStatement.setPosLexem(lexEIE);
	    res.add(elseifStatement);
	    $$ = res; }
	;
	
/*** CASES STATEMENT ***/

CasesStatement:
	  LEX_CASES Expression ':' CasesStatementAlternatives LEX_END
	  { OmlCasesStatement res = new OmlCasesStatement();
	    OmlLexem lexCSS = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    Vector theAlts = (Vector) $4;
	    res.setMatchExpression(theExpression);
	    res.setAlternativeList(theAlts);
	    res.setPosLexem(lexCSS);
	    $$ = res; }
	    
	| LEX_CASES Expression ':' CasesStatementAlternatives ',' OthersStatement LEX_END
	  { OmlCasesStatement res = new OmlCasesStatement();
	    OmlLexem lexCSS = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    Vector theAlts = (Vector) $4;
	    OmlStatement othStatement = (OmlStatement) $6;
	    res.setMatchExpression(theExpression);
	    res.setAlternativeList(theAlts);
	    res.setOthersStatement(othStatement);
	    res.setPosLexem(lexCSS);
	    $$ = res; }
	;
	
CasesStatementAlternatives:
      error
      { $$ = new Vector(); }
      
	| CasesStatementAlternative
	  { Vector res = new Vector();
	    OmlCasesStatementAlternative altCases = (OmlCasesStatementAlternative) $1;
	    res.add(altCases);
	    $$ = res; }
	    
	| CasesStatementAlternatives ',' CasesStatementAlternative
	  { Vector res = (Vector) $1;
	    OmlCasesStatementAlternative altCases = (OmlCasesStatementAlternative) $3;
	    res.add(altCases);
	    $$ = res; }
	;
	
CasesStatementAlternative:
	  PatternList LEX_ARROW Statement
	  { OmlCasesStatementAlternative res = new OmlCasesStatementAlternative();
	    Vector thePatternList = (Vector) $1;
	    OmlLexem lexAR = (OmlLexem) $2;
	    OmlStatement theStatement = (OmlStatement) $3;
	    res.setPatternList(thePatternList);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexAR);
	    $$ = res; }
	;
	
OthersStatement:
	  LEX_OTHERS LEX_ARROW Statement
	  { $$ = $3; }
	;

/*** RETURN STATEMENT ***/

ReturnStatement:
	  LEX_RETURN
	  { OmlReturnStatement res = new OmlReturnStatement();
	    OmlLexem lexRTRN = (OmlLexem) $1;
	    res.setPosLexem(lexRTRN);
	    $$ = res; }
	  
	| LEX_RETURN Expression
	  { OmlReturnStatement res = new OmlReturnStatement();
	    OmlLexem lexRTRN = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexRTRN);
	    $$ = res; }
	;

/*** SPECIFICATION STATEMENT ***/

SpecificationStatement:
	  '[' OptionalExternals OptionalPrecondition OptionalPostcondition OptionalExceptions ']'
	  { OmlSpecificationStatement res = new OmlSpecificationStatement();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExternals theExternals = (OmlExternals) $2;
	    if ( theExternals != null) res.setExternals(theExternals);
	    OmlExpression thePre = (OmlExpression) $3;
	    if ( thePre != null) res.setPreExpression(thePre);
	    OmlExpression thePost = (OmlExpression) $4;
	    if ( thePost == null) {
	    	yyerror("post condition missing in specification statement");
	    } else {
	    	res.setPostExpression(thePost);
	    }
	    OmlExceptions theExceptions = (OmlExceptions) $5;
	    if ( theExceptions != null) res.setExceptions(theExceptions);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;

/*** START STATEMENT ***/
	
StartStatement:
	  LEX_START '(' Expression ')'
	  { OmlStartStatement res = new OmlStartStatement();
	    OmlLexem lexSTRT = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexSTRT);
	    $$ = res; }
	;

/*** START LIST STATEMENT ***/	

StartListStatement:
	  LEX_STARTLIST '(' Expression ')'
	  { OmlStartStatement res = new OmlStartStatement();
	    OmlLexem lexSTRT = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexSTRT);
	    $$ = res; }
	;

/*** DURATION STATEMENT ***/	

DurationStmt:
	  LEX_DURATION '(' NonEmptyExpressionList ')' Statement
	  { OmlDurationStatement res = new OmlDurationStatement();
	    OmlLexem lexDUR = (OmlLexem) $1;
	    Vector theExprs = (Vector) $3;
	    OmlStatement theStatement = (OmlStatement) $5;
	    res.setDurationExpression(theExprs);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexDUR);
	    $$ = res; }
	;

/*** CYCLES STATEMENT ***/
CyclesStmt:
	  LEX_CYCLES '(' NonEmptyExpressionList ')' Statement
	  { OmlCyclesStatement res = new OmlCyclesStatement();
	    OmlLexem lexCYC = (OmlLexem) $1;
	    Vector theExprs = (Vector) $3;
	    OmlStatement theStatement = (OmlStatement) $5;
	    res.setCyclesExpression(theExprs);
	    res.setStatement(theStatement);
	    res.setPosLexem(lexCYC);
	    $$ = res; }
	;
	
/*******************/
/*** EXPRESSIONS ***/
/*******************/

Expression:
	  BracketedExpression
	| LetExpression
	| DefExpression
	| LetBeExpression
	| IfExpression
	| CasesExpression
	| UnaryExpression
	| BinaryExpression
	| AllOrExistsExpression
	| ExistsUniqueExpression
	| IotaExpression
	| TokenExpression
	| LambdaExpression
	| FunctionTypeInstantiation
	| SetEnumeration
	| SetComprehension
	| SetRangeExpression
	| SequenceEnumeration
	| SequenceComprehension
	| SubSequence
	| SequenceOrMapModifier
	| MapEnumeration
	| MapComprehension
	| TupleExpression
	| RecordConstructor
	| RecordModifier
 	| ApplyExpression
 	| FieldSelectExpression
	| PreCondApply
	| IsExpression
	| IsOfClassExpression
	| IsOfBaseClassExpression
	| SameClassExpression
	| SameBaseClassExpression
	| UndefinedExpression
	| SymbolicLiteral
	  { OmlSymbolicLiteralExpression res = new OmlSymbolicLiteralExpression();
	    OmlLiteral theLiteral = (OmlLiteral) $1;
	    res.setLiteral(theLiteral);
	    res.setPosNode(theLiteral);
	    $$ = res; }
	| Name
	| OldName
	| TimeExpression
	| SelfExpression
	| ThreadIdExpression
	| NewExpression
	| PermissionExpression
	;

ExpressionList:
	  { $$ = new Vector(); }
	  
	| NonEmptyExpressionList
	  { $$ = $1; }
	;
	
NonEmptyExpressionList:
	  error
	  { $$ = new Vector(); }
	  
	| Expression
	  { Vector res = new Vector();
	    OmlExpression theExpression = (OmlExpression) $1;
	    res.add(theExpression);
	    $$ = res; }
	    
	| NonEmptyExpressionList ',' Expression
	  { Vector res = (Vector) $1;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.add(theExpression);
	    $$ = res; }	    
	;

/*** BRACKETED EXPRESSION ***/

BracketedExpression:
	  '(' Expression ')'
	  { OmlBracketedExpression res = new OmlBracketedExpression();
	    OmlLexem lexLP = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	;

/*** LET EXPRESSION ***/

LetExpression:
	  LEX_LET ListOfLocalDefinitions LEX_IN Expression
	  { OmlLetExpression res = new OmlLetExpression();
	    OmlLexem lexLET = (OmlLexem) $1;
	    Vector theDefs = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setDefinitionList(theDefs);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	;

/*** DEF EXPRESSION ***/

DefExpression:
	  LEX_DEF ListOfDefPatternBinds LEX_IN Expression
	  { OmlDefExpression res = new OmlDefExpression();
	    OmlLexem lexDEF = (OmlLexem) $1;
	    Vector theDefs = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setPatternBindList(theDefs);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexDEF);
	    $$ = res; }
	    
	| LEX_DEF ListOfDefPatternBinds ';' LEX_IN Expression
	  { OmlDefExpression res = new OmlDefExpression();
	    OmlLexem lexDEF = (OmlLexem) $1;
	    Vector theDefs = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $5;
	    res.setPatternBindList(theDefs);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexDEF);
	    $$ = res; }
	;

ListOfDefPatternBinds:
	  error
	  { $$ = new Vector(); }
	  
	| DefPatternBind
	  { Vector res = new Vector();
	    OmlPatternBindExpression pbExpr = (OmlPatternBindExpression) $1;
	    res.add(pbExpr);
	    $$ = res; }
	    
	| ListOfDefPatternBinds ';' DefPatternBind
	  { Vector res = (Vector) $1;
	    OmlPatternBindExpression pbExpr = (OmlPatternBindExpression) $3;
	    res.add(pbExpr);
	    $$ = res; }
	;
	
DefPatternBind:
	  PatternBind LEX_EQUAL Expression
	  { OmlPatternBindExpression res = new OmlPatternBindExpression();
	    OmlPatternBind thePatternBind = (OmlPatternBind) $1;
	    OmlLexem lexEQ = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setPatternBind(thePatternBind);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexEQ);
	    $$ = res; }
	;

/*** LET BE SUCH THAT EXPRESSION ***/

LetBeExpression:
	  LEX_LET Bind LEX_IN Expression
	  { OmlLetBeExpression res = new OmlLetBeExpression();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setBind(theBind);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	    
	| LEX_LET Bind LEX_BE LEX_ST Expression LEX_IN Expression
	  { OmlLetBeExpression res = new OmlLetBeExpression();
	    OmlLexem lexLET = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression bestExpr = (OmlExpression) $5;
	    OmlExpression theExpression = (OmlExpression) $7;
	    res.setBind(theBind);
	    res.setBest(bestExpr);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLET);
	    $$ = res; }
	;

/*** IF EXPRESSION ***/

IfExpression:
	  LEX_IF Expression LEX_THEN Expression ListOfElsifExpressions LEX_ELSE Expression
	  { OmlIfExpression res = new OmlIfExpression();
	    OmlLexem lexIF = (OmlLexem) $1;
	    OmlExpression ifExpression = (OmlExpression) $2;
	    OmlExpression thenExpression = (OmlExpression) $4;
	    Vector elseifExprList = (Vector) $5;
	    OmlExpression elseExpression = (OmlExpression) $7;
	    res.setIfExpression(ifExpression);
	    res.setThenExpression(thenExpression);
	    res.setElseifExpressionList(elseifExprList);
	    res.setElseExpression(elseExpression);
	    res.setPosLexem(lexIF);
	    $$ = res; }
	;

ListOfElsifExpressions:
	  { $$ = new Vector(); }
	  
	| ListOfElsifExpressions LEX_ELSEIF Expression LEX_THEN Expression
	  { Vector res = (Vector) $1;
	    OmlElseIfExpression elseIfExpr = new OmlElseIfExpression();
	    OmlLexem lexEIE = (OmlLexem) $2;
	    OmlExpression ifExpr = (OmlExpression) $3;
	    OmlExpression thenExpr = (OmlExpression) $5;
	    elseIfExpr.setElseifExpression(ifExpr);
	    elseIfExpr.setThenExpression(thenExpr);
	    elseIfExpr.setPosLexem(lexEIE);
	    res.add(elseIfExpr);
	    $$ = res; }
	;

/*** CASES EXPRESSION ***/

CasesExpression:
	  LEX_CASES Expression ':' CasesExpressionAlternatives LEX_END
	  { OmlCasesExpression res = new OmlCasesExpression();
	    OmlLexem lexCSS = (OmlLexem) $1;
	    OmlExpression matchExpr = (OmlExpression) $2;
	    Vector theAlts = (Vector) $4;
	    res.setMatchExpression(matchExpr);
	    res.setAlternativeList(theAlts);
	    res.setPosLexem(lexCSS);
	    $$ = res; }
	    
	| LEX_CASES Expression ':' CasesExpressionAlternatives ',' OthersExpression LEX_END
	  { OmlCasesExpression res = new OmlCasesExpression();
	    OmlLexem lexCSS = (OmlLexem) $1;
	    OmlExpression matchExpr = (OmlExpression) $2;
	    Vector theAlts = (Vector) $4;
	    OmlExpression othersExpr = (OmlExpression) $6;
	    res.setMatchExpression(matchExpr);
	    res.setAlternativeList(theAlts);
	    res.setOthersExpression(othersExpr);
	    res.setPosLexem(lexCSS);
	    $$ = res; }	    
	;
	
CasesExpressionAlternatives:
	  error
	  { $$ = new Vector(); }
	  
	| CasesExpressionAlternative
	  { Vector res = new Vector();
	    OmlCasesExpressionAlternative theAlt = (OmlCasesExpressionAlternative) $1;
	    res.add(theAlt);
	    $$ = res; }
	    
	| CasesExpressionAlternatives ',' CasesExpressionAlternative
	  { Vector res = (Vector) $1;
	    OmlCasesExpressionAlternative theAlt = (OmlCasesExpressionAlternative) $3;
	    res.add(theAlt);
	    $$ = res; }
	;
	
CasesExpressionAlternative:
	  PatternList LEX_ARROW Expression
	  { OmlCasesExpressionAlternative res = new OmlCasesExpressionAlternative();
	    Vector thePatternList = (Vector) $1;
	    OmlLexem lexAR = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setPatternList(thePatternList);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexAR);
	    $$ = res; }
	;
	
OthersExpression:
	  LEX_OTHERS LEX_ARROW Expression
	  { $$ = $3; }
	;

/*** UNARY EXPRESSION ***/

UnaryExpression:
	  '+' Expression %prec LEX_ABS
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQPLUS, lexUE, $2);
	    $$ = res; }
	  
	| '-' Expression %prec LEX_ABS
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQMINUS, lexUE, $2);
	    $$ = res; }
	  
	| LEX_ABS Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQABS, lexUE, $2);
	    $$ = res; }
	  
	| LEX_FLOOR Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQFLOOR, lexUE, $2);
	    $$ = res; }
	  
	| LEX_NOT Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQNOT, lexUE, $2);
	    $$ = res; }
	  
	| LEX_CARD Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQCARD, lexUE, $2);
	    $$ = res; }
	  
	| LEX_POWER Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQPOWER, lexUE, $2);
	    $$ = res; }
	  
	| LEX_DUNION Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQDUNION, lexUE, $2);
	    $$ = res; }
	  
	| LEX_DINTER Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQDINTER, lexUE, $2);
	    $$ = res; }
	  
	| LEX_DMERGE Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQDMERGE, lexUE, $2);
	    $$ = res; }
	  
	| LEX_HD Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQHD, lexUE, $2);
	    $$ = res; }
	  
	| LEX_TL Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQTL, lexUE, $2);
	    $$ = res; }
	  
	| LEX_LEN Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQLEN, lexUE, $2);
	    $$ = res; }
	  
	| LEX_ELEMS Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQELEMS, lexUE, $2);
	    $$ = res; }
	  
	| LEX_INDS Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQINDS, lexUE, $2);
	    $$ = res; }
	  
	| LEX_CONC Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQDCONC, lexUE, $2);
	    $$ = res; }
	  
	| LEX_DOM Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQDOM, lexUE, $2);
	    $$ = res; }
	  
	| LEX_RNG Expression
	  { OmlLexem lexUE = (OmlLexem) $1;;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQRNG, lexUE, $2);
	    $$ = res; }
	  
	| LEX_INVERSE Expression
	  { OmlLexem lexUE = (OmlLexem) $1;
	    OmlUnaryExpression res = createUnaryExpression(OmlUnaryOperatorQuotes.IQINVERSE, lexUE, $2);
	    $$ = res; }
	;

/*** BINARY EXPRESSION ***/

BinaryExpression:
	  Expression LEX_DOTHASH Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQTUPSEL, lexBE, $3); }
	  
	| Expression '+' Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQPLUS, lexBE, $3); }
	  
	| Expression '-' Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMINUS, lexBE, $3); }
	  
	| Expression '*' Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMULTIPLY, lexBE, $3); }
	  
	| Expression '/' Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQDIVIDE, lexBE, $3); }
	  
	| Expression LEX_REM Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQREM, lexBE, $3); }
	  
	| Expression LEX_MOD Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMOD, lexBE, $3); }
	  
	| Expression LEX_ARITHMETIC_INTEGER_DIVISION Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQDIV, lexBE, $3); }
	  
	| Expression LEX_SET_UNION Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQUNION, lexBE, $3); }
	  
	| Expression LEX_SET_MINUS Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQDIFFERENCE, lexBE, $3); }
	  
	| Expression LEX_SET_INTERSECTION Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQINTER, lexBE, $3); }
	  
	| Expression LEX_SUBSET Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQSUBSET, lexBE, $3); }
	  
	| Expression LEX_PROPER_SUBSET Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQPSUBSET, lexBE, $3); }
	  
	| Expression LEX_IN_SET Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQINSET, lexBE, $3); }
	  
	| Expression LEX_NOT_IN_SET Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQNOTINSET, lexBE, $3); }
	  
	| Expression LEX_SEQUENCE_CONCATENATE Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQCONC, lexBE, $3); }
	  
	| Expression LEX_MAP_MERGE Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMUNION, lexBE, $3); }
	  
	| Expression LEX_MAP_DOMAIN_RESTRICT_TO Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMAPDOMRESTO, lexBE, $3); }
	  
	| Expression LEX_MAP_DOMAIN_RESTRICT_BY Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMAPDOMRESBY, lexBE, $3); }
	  
	| Expression LEX_MAP_RANGE_RESTRICT_TO Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMAPRNGRESTO, lexBE, $3); }
	  
	| Expression LEX_MAP_RANGE_RESTRICT_BY Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMAPRNGRESBY, lexBE, $3); }
	  
	| Expression LEX_EXP_OR_ITERATE Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQITERATE, lexBE, $3); }
	  
	| Expression LEX_COMP Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQCOMP, lexBE, $3); }
	  
	| Expression LEX_AND Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQAND, lexBE, $3); }
	  
	| Expression LEX_OR Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQOR, lexBE, $3); }
	  
	| Expression LEX_IMPLY Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQIMPLY, lexBE, $3); }
	  
	| Expression LEX_LOGICAL_EQUIVALENCE Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQEQUIV, lexBE, $3); }
	  
	| Expression LEX_EQUAL Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQEQ, lexBE, $3); }
	  
	| Expression LEX_NOT_EQUAL Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQNE, lexBE, $3); }
	  
	| Expression LEX_LESS_THAN Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQLT, lexBE, $3); }
	  
	| Expression LEX_LESS_THAN_OR_EQUAL Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQLE, lexBE, $3); }
	  
	| Expression LEX_GREATER_THAN Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQGT, lexBE, $3); }
	  
	| Expression LEX_GREATER_THAN_OR_EQUAL Expression
	  { OmlLexem lexBE = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQGE, lexBE, $3); }	  
	;

/*** ALL OR EXIST EXPRESSION ***/

AllOrExistsExpression:
	  LEX_FORALL BindList LEX_RAISED_DOT Expression
	  { OmlForAllExpression res = new OmlForAllExpression();
	    OmlLexem lexFA = (OmlLexem) $1;
	    Vector theBindList = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setBindList(theBindList);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexFA);
	    $$ = res; }
	    
	| LEX_EXISTS BindList LEX_RAISED_DOT Expression
	  { OmlExistsExpression res = new OmlExistsExpression();
	    OmlLexem lexXSTS = (OmlLexem) $1;
	    Vector theBindList = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setBindList(theBindList);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexXSTS);
	    $$ = res; }	  
	;

BindList:
	  error
	  { $$ = new Vector(); }
	  
	| MultipleBind
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| BindList ',' MultipleBind
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;

/*** EXISTS UNIQUE EXPRESSION ***/

ExistsUniqueExpression:
	  LEX_EXISTS1 Bind LEX_RAISED_DOT Expression
	  { OmlExistsUniqueExpression res = new OmlExistsUniqueExpression();
	    OmlLexem lexXSTS = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setBind(theBind);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexXSTS);
	    $$ = res; }
	;

/*** IOTA EXPRESSION ***/

IotaExpression:
	  LEX_IOTA Bind LEX_RAISED_DOT Expression
	  { OmlIotaExpression res = new OmlIotaExpression();
	    OmlLexem lexIOTA = (OmlLexem) $1;
	    OmlBind theBind = (OmlBind) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setBind(theBind);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexIOTA);
	    $$ = res; }
	;

/*** TOKEN EXPRESSION ***/

TokenExpression:
	  LEX_MK_ LEX_TOKEN '(' Expression ')'
	  { OmlTokenExpression res = new OmlTokenExpression();
	    OmlLexem lexMK = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setExpression(theExpression);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	;

/*** LAMBDA EXPRESSION ***/

LambdaExpression:
	  LEX_LAMBDA TypeBindList LEX_RAISED_DOT Expression
	  { OmlLambdaExpression res = new OmlLambdaExpression();
	    OmlLexem lexLMBD = (OmlLexem) $1;
	    Vector tbList = (Vector) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setTypeBindList(tbList);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLMBD);
	    $$ = res; }
	;

TypeBindList:
	  TypeBind
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| TypeBindList ',' TypeBind
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;

/*** FUNCTION TYPE INSTANTIATION ***/

FunctionTypeInstantiation:
	  Name '[' TypeList ']'
	  { OmlFunctionTypeInstantiation res = new OmlFunctionTypeInstantiation();
	    OmlName theName = (OmlName) $1;
	    OmlLexem lexLB = (OmlLexem) $2;
	    Vector tpList = (Vector) $3;
	    res.setName(theName);
	    res.setTypeList(tpList);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	;

TypeList:
	  error
	  { $$ = new Vector(); }
	  
	| Type
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| TypeList ',' Type
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;

/*** SET ENUMERATION EXPRESSION ***/

SetEnumeration:
	  '{' ExpressionList '}'
	  { OmlSetEnumeration res = new OmlSetEnumeration();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    Vector theExpressions = (Vector) $2;
	    res.setExpressionList(theExpressions);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;

/*** SET COMPREHENSION EXPRESSION ***/

SetComprehension:
	  '{' Expression '|' BindList '}'
	  { OmlSetComprehension res = new OmlSetComprehension();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    Vector bindList = (Vector) $4;
	    res.setExpression(theExpression);
	    res.setBindList(bindList);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	    
	| '{' Expression '|' BindList LEX_RAISED_DOT Expression '}'
	  { OmlSetComprehension res = new OmlSetComprehension();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $2;
	    Vector bindList = (Vector) $4;
	    OmlExpression guardExpr = (OmlExpression) $6;
	    res.setExpression(theExpression);
	    res.setBindList(bindList);
	    res.setGuard(guardExpr);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;
	
/*** SET RANGE EXPRESSION ***/

SetRangeExpression:
	  '{' Expression LEX_RANGE_OVER Expression '}'
	  { OmlSetRangeExpression res = new OmlSetRangeExpression();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExpression lower = (OmlExpression) $2;
	    OmlExpression upper = (OmlExpression) $4;
	    res.setLower(lower);
	    res.setUpper(upper);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;

/*** SEQUENCE ENUMERATION ***/

SequenceEnumeration:
	  '[' ExpressionList ']'
	  { OmlSequenceEnumeration res = new OmlSequenceEnumeration();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    Vector exprList = (Vector) $2;
	    res.setExpressionList(exprList);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;

/*** SEQUENCE COMPREHENSION ***/
	
SequenceComprehension:
	  '[' Expression '|' SetBind ']'
	  { OmlSequenceComprehension res = new OmlSequenceComprehension();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExpression theExpr = (OmlExpression) $2;
	    OmlSetBind theBind = (OmlSetBind) $4;
	    res.setExpression(theExpr);
	    res.setSetBind(theBind);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	    
	| '[' Expression '|' SetBind LEX_RAISED_DOT Expression ']'
	  { OmlSequenceComprehension res = new OmlSequenceComprehension();
	    OmlLexem lexLBR = (OmlLexem) $1;
	    OmlExpression theExpr = (OmlExpression) $2;
	    OmlSetBind theBind = (OmlSetBind) $4;
	    OmlExpression guardExpr = (OmlExpression) $6;
	    res.setExpression(theExpr);
	    res.setSetBind(theBind);
	    res.setGuard(guardExpr);
	    res.setPosLexem(lexLBR);
	    $$ = res; }
	;

/*** SUBSEQUENCE ***/

SubSequence:
	  Expression '(' Expression LEX_RANGE_OVER Expression ')'
	  { OmlSubsequenceExpression res = new OmlSubsequenceExpression();
	    OmlExpression theExpression = (OmlExpression) $1;
	    OmlExpression theLower = (OmlExpression) $3;
	    OmlLexem lexRO = (OmlLexem) $4;
	    OmlExpression theUpper = (OmlExpression) $5;
	    res.setExpression(theExpression);
	    res.setLower(theLower);
	    res.setUpper(theUpper);
	    res.setPosLexem(lexRO);
	    $$ = res; }
	;

/*** SEQUENCE OR MAP MODIFIER ***/
	
SequenceOrMapModifier:
	  Expression LEX_MODIFY_BY Expression
	  { OmlLexem lexMB = (OmlLexem) $2;
	    $$ = createBinaryExpression($1, OmlBinaryOperatorQuotes.IQMODIFY, lexMB, $3); }
	;

/*** MAP ENUMERATION ***/

MapEnumeration:
	  '{' LEX_BAR_ARROW '}'
	  { OmlMapEnumeration res = new OmlMapEnumeration();
	    OmlLexem lexLB = (OmlLexem) $1;
	    res.setPosLexem(lexLB);
	    $$ = res; }
	    
	| '{' ListOfMaplets '}'
	  { OmlMapEnumeration res = new OmlMapEnumeration();
	    OmlLexem lexLB = (OmlLexem) $1;
	    Vector mapletList = (Vector) $2;
	    res.setMapletList(mapletList);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	;
	
ListOfMaplets: 
	  Maplet
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| ListOfMaplets ',' Maplet
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;
	
Maplet:
	  Expression LEX_BAR_ARROW Expression
	  { OmlMaplet res = new OmlMaplet();
	    OmlExpression domExpr = (OmlExpression) $1;
	    OmlLexem lexBA = (OmlLexem) $2;
	    OmlExpression rngExpr = (OmlExpression) $3;
	    res.setDomExpression(domExpr);
	    res.setRngExpression(rngExpr);
	    res.setPosLexem(lexBA);
	    $$ = res; }
	;

/*** MAP COMPREHENSION ***/
	
MapComprehension:
	  '{' Maplet '|' BindList '}'
	  { OmlMapComprehension res = new OmlMapComprehension();
	    OmlLexem lexLB = (OmlLexem) $1;
	    OmlMaplet maplet = (OmlMaplet) $2;
	    Vector bindlist = (Vector) $4;
	    res.setExpression(maplet);
	    res.setBindList(bindlist);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	    
	| '{' Maplet '|' BindList LEX_RAISED_DOT Expression '}'
	  { OmlMapComprehension res = new OmlMapComprehension();
	    OmlLexem lexLB = (OmlLexem) $1;
	    OmlMaplet maplet = (OmlMaplet) $2;
	    Vector bindlist = (Vector) $4;
	    OmlExpression guardExpr = (OmlExpression) $6;
	    res.setExpression(maplet);
	    res.setBindList(bindlist);
	    res.setGuard(guardExpr);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	;

/*** TUPLE EXPRESSION ***/

TupleExpression:
	  LEX_MK_ '(' ExpressionList ')'
	  { OmlTupleConstructor res = new OmlTupleConstructor();
	    OmlLexem lexMK = (OmlLexem) $1;
	    Vector theList = (Vector) $3;
	    if ( theList.size() <= 1 ) {
	    	yyerror ("tuple constructor requires at least two values");
	    }
	    res.setExpressionList(theList);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	;

/*** RECORD CONSTRUCTOR ***/

RecordConstructor:
	  LEX_MK_ Name '(' ExpressionList ')'
	  { OmlRecordConstructor res = new OmlRecordConstructor();
	    OmlLexem lexMK = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    Vector theList = (Vector) $4;
	    res.setName(theName);
	    res.setExpressionList(theList);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	;

/*** RECORD MODIFIER ***/
	
RecordModifier:
	  LEX_MU '(' Expression ',' ListOfRecordModifications ')'
	  { OmlMuExpression res = new OmlMuExpression();
	    OmlLexem lexMU = (OmlLexem) $1;
	    OmlExpression theExpr = (OmlExpression) $3;
	    Vector theModifications = (Vector) $5;
	    res.setExpression(theExpr);
	    res.setModifierList(theModifications);
	    res.setPosLexem(lexMU);
	    $$ = res; }
	;
	
ListOfRecordModifications:
	  error
	  { $$ = new Vector(); }
	  
	| RecordModification
	  { Vector res = new Vector();
	    res.add($1);
	    $$ = res; }
	    
	| ListOfRecordModifications ',' RecordModification
	  { Vector res = (Vector) $1;
	    res.add($3);
	    $$ = res; }
	;
	
RecordModification:
	  Identifier LEX_BAR_ARROW Expression
	  { OmlRecordModifier res = new OmlRecordModifier();
	    OmlLexem id = (OmlLexem) $1;
	    OmlLexem lexBA = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setIdentifier(lexemToString(id));
	    res.setExpression(theExpression);
	    res.setPosLexem(lexBA);
	    $$ = res; }
	;

/*** APPLY EXPRESSION ***/

ApplyExpression:
	  Expression '(' ExpressionList ')'
	  { OmlApplyExpression res = new OmlApplyExpression();
	    OmlExpression theExpr = (OmlExpression) $1;
	    OmlLexem lexLP = (OmlLexem) $2;
	    Vector theExprList = (Vector) $3;
	    res.setExpression(theExpr);
	    res.setExpressionList(theExprList);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	;

/*** FIELD SELECT EXPRESSION ***/
	
FieldSelectExpression:
	  Expression '.' Name
	  { OmlFieldSelect res = new OmlFieldSelect();
	    OmlExpression theExpr = (OmlExpression) $1;
	    OmlLexem lexPT = (OmlLexem) $2;
	    OmlName theName = (OmlName) $3;
	    res.setExpression(theExpr);
	    res.setName(theName);
	    res.setPosLexem(lexPT);
	    $$ = res; }
	    
	| Expression '.' FunctionTypeInstantiation
	  { OmlFunctionTypeSelect res = new OmlFunctionTypeSelect();
	    OmlExpression theExpr = (OmlExpression) $1;
	    OmlLexem lexPT = (OmlLexem) $2;
	    OmlFunctionTypeInstantiation theFuncTypeInst = (OmlFunctionTypeInstantiation) $3;
	    res.setExpression(theExpr);
	    res.setFunctionTypeInstantiation(theFuncTypeInst);
	    res.setPosLexem(lexPT);
	    $$ = res; }
	;

/*** PRE CONDITION APPLY ***/

PreCondApply:
	  LEX_PRECONDAPPLY '(' NonEmptyExpressionList ')'
	  { OmlPreconditionExpression res = new OmlPreconditionExpression();
	    OmlLexem lexPCA = (OmlLexem) $1;
	    Vector exprList = (Vector) $3;
	    res.setExpressionList(exprList);
	    res.setPosLexem(lexPCA);
	    $$ = res; }
	;

/*** IS EXPRESSION ***/

IsExpression:
	  LEX_IS_ Name '(' Expression ')'
	  { OmlIsExpression res = new OmlIsExpression();
	    OmlTypeName tpName = new OmlTypeName();
	    OmlLexem lexIS = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    tpName.setName(theName);
	    tpName.setPosNode(theName);
	    OmlExpression theExpr = (OmlExpression) $4;
	    res.setType(tpName);
	    res.setExpression(theExpr);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	    
	| LEX_IS_ BasicType '(' Expression ')'
	  { OmlIsExpression res = new OmlIsExpression();
	    OmlLexem lexIS = (OmlLexem) $1;
	    OmlType theType = (OmlType) $2;
	    OmlExpression theExpression = (OmlExpression) $4;
	    res.setType(theType);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	    
	| LEX_IS_ '(' Expression ',' Type ')'
	  { OmlIsExpression res = new OmlIsExpression();
	    OmlLexem lexIS = (OmlLexem) $1;
	    OmlExpression theExpression = (OmlExpression) $3;
	    OmlType theType = (OmlType) $5;
	    res.setType(theType);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexIS);
	    $$ = res; }
	;

/*** SAME CLASS EXPRESSION ***/

SameClassExpression:
	  LEX_SAMECLASS '(' Expression ',' Expression ')'
	  { OmlSameclassExpression res = new OmlSameclassExpression();
	    OmlLexem lexSC = (OmlLexem) $1;
	    OmlExpression lhsExpr = (OmlExpression) $3;
	    OmlExpression rhsExpr = (OmlExpression) $5;
	    res.setLhsExpression(lhsExpr);
	    res.setRhsExpression(rhsExpr); 
	    res.setPosLexem(lexSC);
	    $$ = res; }
	;

/*** SAME BASE CLASS EXPRESSION ***/

SameBaseClassExpression:
	  LEX_SAMEBASECLASS '(' Expression ',' Expression ')'
	  { OmlSamebaseclassExpression res = new OmlSamebaseclassExpression();
	    OmlLexem lexSBC = (OmlLexem) $1;
	    OmlExpression lhsExpr = (OmlExpression) $3;
	    OmlExpression rhsExpr = (OmlExpression) $5;
	    res.setLhsExpression(lhsExpr);
	    res.setRhsExpression(rhsExpr); 
	    res.setPosLexem(lexSBC);
	    $$ = res; }
	;

/*** IS OF CLASS EXPRESSION ***/
	
IsOfClassExpression:
	  LEX_ISOFCLASS '(' Name ',' Expression ')'
	  { OmlIsofclassExpression res = new OmlIsofclassExpression();
	    OmlLexem lexIOC = (OmlLexem) $1;
	    OmlName theName = (OmlName) $3;
	    OmlExpression theExpression = (OmlExpression) $5;
	    res.setName(theName);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexIOC);
	    $$ = res; }
	;

/*** IS OF BASE CLASS EXPRESSION ***/
	
IsOfBaseClassExpression:
	  LEX_ISOFBASECLASS '(' Name ',' Expression ')'
	  { OmlIsofbaseclassExpression res = new OmlIsofbaseclassExpression();
	    OmlLexem lexIOBC = (OmlLexem) $1;
	    OmlName theName = (OmlName) $3;
	    OmlExpression theExpression = (OmlExpression) $5;
	    res.setName(theName);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexIOBC);
	    $$ = res; }
	;

/*** UNDEFINED EXPRESSION ***/	

UndefinedExpression:
	  LEX_UNDEFINED
	  { OmlUndefinedExpression res = new OmlUndefinedExpression();
	    OmlLexem lexUNDEF = (OmlLexem) $1;
	    res.setPosLexem(lexUNDEF);
	    $$ = res; }
	;

/*** TIME EXPRESSION ***/

TimeExpression:
	  LEX_TIME
	  { OmlTimeExpression res = new OmlTimeExpression();
	    OmlLexem lexTIME = (OmlLexem) $1;
	    res.setPosLexem(lexTIME);
	    $$ = res; }
	;

/*** SELF EXPRESSION ***/

SelfExpression:
	  LEX_SELF
	  { OmlSelfExpression res = new OmlSelfExpression();
	    OmlLexem lexSELF = (OmlLexem) $1;
	    res.setPosLexem(lexSELF);
	    $$ = res; }
	;

/*** THREAD ID EXPRESSION ***/
	
ThreadIdExpression:
	  LEX_THREADID
	  { OmlThreadIdExpression res = new OmlThreadIdExpression();
	    OmlLexem lexTI = (OmlLexem) $1;
	    res.setPosLexem(lexTI);
	    $$ = res; }
	;

/*** NEW EXPRESSION ***/
	
NewExpression:
	  LEX_NEW Name '(' ExpressionList ')'
	  { OmlNewExpression res = new OmlNewExpression();
	    OmlLexem lexNEW = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    Vector theExpressionList = (Vector) $4;
	    res.setName(theName);
	    res.setExpressionList(theExpressionList);
	    res.setPosLexem(lexNEW);
	    $$ = res; }
	    
	| LEX_NEW Name GenericTypeList '(' ExpressionList ')'  /* added for Thomas Christensen */
	  { OmlNewExpression res = new OmlNewExpression();
	    OmlLexem lexNEW = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    Vector theGenerics = (Vector) $3;
	    Vector theExpressionList = (Vector) $5;
	    res.setName(theName);
	    res.setGenericTypes(theGenerics);
	    res.setExpressionList(theExpressionList);
	    res.setPosLexem(lexNEW);
	    $$ = res; }
	;

/*** PERMISSION EXPRESSION ***/

PermissionExpression:
	  LEX_ACT '(' NameList ')'
	  { OmlActExpression res = new OmlActExpression();
	    OmlLexem lexACT = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexACT);
	    $$ = res; }
	    
	| LEX_FIN '(' NameList ')'
	  { OmlFinExpression res = new OmlFinExpression();
	    OmlLexem lexFIN = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexFIN);
	    $$ = res; }

	| LEX_ACTIVE '(' NameList ')'
	  { OmlActiveExpression res = new OmlActiveExpression();
	    OmlLexem lexACTIVE = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexACTIVE);
	    $$ = res; }

	| LEX_WAITING '(' NameList ')'
	  { OmlWaitingExpression res = new OmlWaitingExpression();
	    OmlLexem lexWAIT = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexWAIT);
	    $$ = res; }

	| LEX_REQ '(' NameList ')'
	  { OmlReqExpression res = new OmlReqExpression();
	    OmlLexem lexREQ = (OmlLexem) $1;
	    Vector theNameList = (Vector) $3;
	    res.setNameList(theNameList);
	    res.setPosLexem(lexREQ);
	    $$ = res; }
	;
	
/****************/
/*** LITERALS ***/
/****************/

SymbolicLiteral:
	  CharacterLiteral
	| BooleanLiteral
	| QuoteLiteral
	| TextLiteral
	| NumericLiteral
	| RealLiteral
	| NilLiteral
	;
	
CharacterLiteral:
	  LEX_char_lit
	  { OmlCharacterLiteral res = new OmlCharacterLiteral();
	    OmlLexem chlit = (OmlLexem) $1;
	    res.setVal(chlit.getText().charAt(1));
	    res.setPosLexem(chlit);
	    $$ = res; }
	;

BooleanLiteral:	    
	  LEX_bool_true
	  { OmlBooleanLiteral res = new OmlBooleanLiteral();
	    OmlLexem blit = (OmlLexem) $1;
	    res.setVal(true);
	    res.setPosLexem(blit);
	    $$ = res; }
		    
	| LEX_bool_false
	  { OmlBooleanLiteral res = new OmlBooleanLiteral();
	    OmlLexem blit = (OmlLexem) $1;
	    res.setVal(false);
	    res.setPosLexem(blit);
	    $$ = res; }
	;
	
QuoteLiteral:	    
	  LEX_quote_lit
	  { OmlQuoteLiteral res = new OmlQuoteLiteral();
	    OmlLexem qlit = (OmlLexem) $1;
	    res.setVal(stripString(qlit.getText()));
	    res.setPosLexem(qlit);
	    $$ = res; }
	;
	
TextLiteral:	    
	  LEX_text_lit
	  { OmlTextLiteral res = new OmlTextLiteral();
	    OmlLexem tlit = (OmlLexem) $1;
	    res.setVal(stripString(tlit.getText()));
	    res.setPosLexem(tlit);
	    $$ = res; }
	;

NumericLiteral:		    
	  LEX_num_lit
	  { OmlNumericLiteral res = new OmlNumericLiteral();
	    OmlLexem nlit = (OmlLexem) $1;
	    res.setVal(Long.parseLong(nlit.getText()));
	    res.setPosLexem(nlit);
	    $$ = res; }
	;

RealLiteral:		  
	  LEX_real_lit
	  { OmlRealLiteral res = new OmlRealLiteral();
	    OmlLexem rlit = (OmlLexem) $1;
	    res.setVal(Double.parseDouble(rlit.getText()));
	    res.setPosLexem(rlit);
	    $$ = res; }
	;

NilLiteral:		  
	  LEX_NIL
	  { OmlNilLiteral res = new OmlNilLiteral();
	    OmlLexem nlit = (OmlLexem) $1;
	    res.setPosLexem(nlit);
	    $$ = res; }
	;

/****************/
/*** PATTERNS ***/
/****************/

Pattern:
	  PatternIdentifier
	| MatchValue
	| SetPattern
	| SequencePattern
	| TuplePattern
	| RecordPattern
	;

PatternList:
	  Pattern
	  { Vector res = new Vector();
	    OmlPattern thePattern = (OmlPattern) $1;
	    res.add(thePattern);
	    $$ = res; }
	    
	| PatternList ',' Pattern
	  { Vector res = (Vector) $1;
	    OmlPattern thePattern = (OmlPattern) $3;
	    res.add(thePattern);
	    $$ = res; }
	;

PatternIdentifier:
	  Identifier
	  { OmlPatternIdentifier res = new OmlPatternIdentifier();
	    OmlLexem id = (OmlLexem) $1;
	    res.setIdentifier(lexemToString(id));
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| '-'
	  { OmlDontCarePattern res = new OmlDontCarePattern();
	    OmlLexem lexHPN = (OmlLexem) $1;
	    res.setPosLexem(lexHPN);
	    $$ = res; }
	;

MatchValue:
	  '(' Expression ')'
	  { OmlMatchValue res = new OmlMatchValue();
	    OmlLexem lexLP = (OmlLexem) $1;
	    OmlExpression theExpr = (OmlExpression) $2;
	    res.setExpression(theExpr);
	    res.setPosLexem(lexLP);
	    $$ = res; }
	    
	| SymbolicLiteral
	  { OmlSymbolicLiteralPattern res = new OmlSymbolicLiteralPattern();
	    OmlLiteral theLiteral = (OmlLiteral) $1;
	    res.setLiteral(theLiteral);
	    res.setPosNode(theLiteral);
	    $$ = res; }
	;
	
SetPattern:
	  SetEnumPattern
	  { $$ = $1; }
	  
	| SetUnionPattern
	  { $$ = $1; }
	;
	
SetEnumPattern:
	  '{' '}' ;
	  { OmlSetEnumPattern res = new OmlSetEnumPattern();
	    OmlLexem lexLB = (OmlLexem) $1;
	    res.setPosLexem(lexLB);
	    $$ = res; }
	  
	| '{' PatternList '}'
	  { OmlSetEnumPattern res = new OmlSetEnumPattern();
	    OmlLexem lexLB = (OmlLexem) $1;
	    Vector patternList = (Vector) $2;
	    res.setPatternList(patternList);
	    res.setPosLexem(lexLB);
	    $$ = res; }
	;
	
SetUnionPattern:
	  Pattern LEX_SET_UNION Pattern
	  { OmlSetUnionPattern res = new OmlSetUnionPattern();
	    OmlPattern lhsPattern = (OmlPattern) $1;
	    OmlLexem lexSU = (OmlLexem) $2;
	    OmlPattern rhsPattern = (OmlPattern) $3;
	    res.setLhsPattern(lhsPattern);
	    res.setRhsPattern(rhsPattern);
	    res.setPosLexem(lexSU);
	    $$ = res; }
	;
	
SequencePattern:
	  SeqEnumPattern
	  { $$ = $1; }
	  
	| SeqConcPattern
	  { $$ = $1; }
	;
	
SeqEnumPattern:
	  '[' ']'
	  { OmlSeqEnumPattern res = new OmlSeqEnumPattern();
	    OmlLexem lexLH = (OmlLexem) $1;
	    res.setPosLexem(lexLH);
	    $$ = res; }
	  
	| '[' PatternList ']'
	  { OmlSeqEnumPattern res = new OmlSeqEnumPattern();
	    OmlLexem lexLH = (OmlLexem) $1;
	    Vector patternList = (Vector) $2;
	    res.setPatternList(patternList);
	    res.setPosLexem(lexLH);
	    $$ = res; }
	;
	
SeqConcPattern:
	  Pattern LEX_SEQUENCE_CONCATENATE Pattern
	  { OmlSeqConcPattern res = new OmlSeqConcPattern();
	    OmlPattern lhsPattern = (OmlPattern) $1;
	    OmlLexem lexSC = (OmlLexem) $2;
	    OmlPattern rhsPattern = (OmlPattern) $3;
	    res.setLhsPattern(lhsPattern);
	    res.setRhsPattern(rhsPattern);
	    res.setPosLexem(lexSC);
	    $$ = res; }
	;
	
TuplePattern:
	  LEX_MK_ '(' PatternList ')'
	  { OmlLexem lexMK = (OmlLexem) $1;
	    OmlTuplePattern res = new OmlTuplePattern();
	    Vector patternList = (Vector) $3;
	    if ( patternList.size() <= 1) {
	    	yyerror("tuple pattern requires at least two elements");
	    }
	    res.setPatternList(patternList);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	;
	
RecordPattern:
	  LEX_MK_ Name '(' ')'
	  { OmlRecordPattern res = new OmlRecordPattern();
	    OmlLexem lexMK = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    res.setName(theName);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	    
	| LEX_MK_ Name '(' PatternList ')'
	  { OmlRecordPattern res = new OmlRecordPattern();
	    OmlLexem lexMK = (OmlLexem) $1;
	    OmlName theName = (OmlName) $2;
	    Vector patternList = (Vector) $4;
	    res.setName(theName);
	    res.setPatternList(patternList);
	    res.setPosLexem(lexMK);
	    $$ = res; }
	;

/*************/
/*** BINDS ***/
/*************/

PatternBind:
	  Pattern
	  { $$ = $1; }
	    
	| Bind
	  { $$ = $1; }
	;
	
Bind:
	  SetBind
	  { $$ = $1; }
	  
	| TypeBind
	  { $$ = $1; }
	;
	
SetBind:
	  Pattern LEX_IN_SET Expression %prec DUMMY_RELATION
	  { OmlSetBind res = new OmlSetBind();
	    OmlPattern thePattern = (OmlPattern) $1;
	    OmlLexem lexLIS = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.addPattern(thePattern);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLIS);
	    $$ = res; }
	;
	
TypeBind:
	  Pattern ':' Type
	  { OmlTypeBind res = new OmlTypeBind();
	    OmlPattern thePattern = (OmlPattern) $1;
	    OmlLexem lexCOL = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    res.addPattern(thePattern);
	    res.setType(theType);
	    res.setPosLexem(lexCOL);
	    $$ = res; }
	;

MultipleBind:
	  PatternList LEX_IN_SET Expression
	  { /* MultipleSetBind */
	    OmlSetBind res = new OmlSetBind();
	    Vector thePatternList = (Vector) $1;
	    OmlLexem lexLIS = (OmlLexem) $2;
	    OmlExpression theExpression = (OmlExpression) $3;
	    res.setPattern(thePatternList);
	    res.setExpression(theExpression);
	    res.setPosLexem(lexLIS);
	    $$ = res; }
	  
	| PatternList ':' Type
	  { /* MultipleTypeBind */
	    OmlTypeBind res = new OmlTypeBind();
	    Vector thePatternList = (Vector) $1;
	    OmlLexem lexCOL = (OmlLexem) $2;
	    OmlType theType = (OmlType) $3;
	    res.setPattern(thePatternList);
	    res.setType(theType);
	    res.setPosLexem(lexCOL);
	    $$ = res; }
	;
	
/*****************************/
/*** IDENTIFIERS AND NAMES ***/
/*****************************/

Name:
	  Identifier
	  { OmlName res = new OmlName();
	    OmlLexem id = (OmlLexem) $1;
	    res.setIdentifier(lexemToString(id));
	    res.setPosLexem(id);
	    $$ = res; }
	    
	| Identifier LEX_PRIME Identifier
	  { OmlName res = new OmlName();
	    OmlLexem clid = (OmlLexem) $1;
	    OmlLexem id = (OmlLexem) $3;
	    res.setClassIdentifier(lexemToString(clid));
	    res.setIdentifier(lexemToString(id));
	    res.setPosLexem(clid);
	    $$ = res; }
 	;

NameList:
	  error
	  { $$ = new Vector(); }
	  
	| Name
	  { Vector res = new Vector();
	    OmlName theName = (OmlName) $1;
	    res.add(theName);
	    $$ = res; }
	    
	| NameList ',' Name
	  { Vector res = (Vector) $1;
	    OmlName theName = (OmlName) $3;
	    res.add(theName);
	    $$ = res; }
	;

OldName:
	  Identifier LEX_HOOK
	  { OmlOldName res = new OmlOldName();
	    OmlLexem id = (OmlLexem) $1;
	    res.setIdentifier(lexemToString(id));
	    res.setPosLexem(id);
	    $$ = res; }

/*    | Name
      { $$ = $1; } */
	;
	
IdentifierCommaList:
	  error
	  { $$ = new Vector(); }

	| Identifier
	  { Vector res = new Vector();
	  	OmlLexem theIdent = (OmlLexem) $1;
	  	res.add(lexemToString(theIdent));
	  	$$ = res; }
	  	
	| IdentifierCommaList ',' Identifier
	  { Vector res = (Vector) $1;
	  	OmlLexem theIdent = (OmlLexem) $3;
	  	res.add(lexemToString(theIdent));
	  	$$ = res; }
	;
	
Identifier:
	  LEX_identifier
	  { $$ = $1; }
	  
	| LEX_dollar_identifier
	  { $$ = $1; }
	;

// **********************
// *** END OF GRAMMAR ***
// **********************

%%

// ************************
// *** MEMBER VARIABLES ***
// ************************

// maintain a link to the scanner we're using
private OvertureScanner theScanner = null;

// maintain a counter for all errors
// note: MUST be incremented by yyerror (when overloaded)
public int errors = 0;

// the abstract syntax element
public OmlDocument astDocument = null;

// *************************
// *** PUBLIC OPERATIONS ***
// *************************

public OvertureParser (String in)
{
	theScanner = new OvertureScanner(in);
}

public OvertureParser (java.io.Reader in)
{
	theScanner = new OvertureScanner(in);
}

public OvertureParser (java.io.InputStream in)
{
	theScanner = new OvertureScanner(in);
}

public void parseDocument() throws CGException
{
	// create the top-level AST element
	astDocument = new OmlDocument();
	// link the scanner to the document (for the tokens)
	theScanner.setLexems(astDocument.getLexems());
	// go parse the file
	yyparse();
}

// ************************************
// *** AUXILIARY PRIVATE OPERATIONS ***
// ************************************

private int yylex () {
	try {
		while (true) {
			OmlLexem lval = theScanner.getNextToken();
			if (lval != null) {
				if (lval.isComment().booleanValue() == false) {
					yylval = lval;
					return lval.getLexval().intValue();
				} /* else read next token */
			} else {
				return 0;
			}
		}
	}
	catch (CGException cge) {
	    yyerror (cge.getMessage());
	    yylval = null;
	    return 0;
	}
	catch (java.io.IOException ioe) {
		yyerror (ioe.getMessage());
		yylval = null;
		return 0;
	}
}

private boolean checkClassName(OmlLexem cn1, OmlLexem cn2) throws CGException {
    // check whether the class name is identical
	boolean res = (cn1.getText().compareTo(cn2.getText()) == 0);
	if (!res) {
		yyerror ("class name is not identical to definition (\"" +cn1.getText()+
		         "\" on line " + cn1.getLine() + ")");
	}
	return res;
}

private boolean checkClassHeader(OmlLexem header) throws CGException {
    // return false if "class" true if "system"
	boolean res = (header.getText().compareTo("system") == 0);
	return res;
}

private OmlUnaryExpression createUnaryExpression (long qid, OmlLexem lex, Object rhs)
  throws CGException
{
	OmlUnaryExpression res = new OmlUnaryExpression();
	OmlUnaryOperator theOperator = new OmlUnaryOperator();
	OmlExpression theExpression = (OmlExpression) rhs;
	theOperator.setValue(qid);
	theOperator.setPosLexem(lex);
	res.setOperator(theOperator);
	res.setExpression(theExpression);
	res.setPosLexem(lex);
	return res;
}

private OmlBinaryExpression createBinaryExpression (Object lhs, long qid, OmlLexem lex, Object rhs)
  throws CGException
{
	OmlBinaryExpression res = new OmlBinaryExpression();
	OmlBinaryOperator theOperator = new OmlBinaryOperator();
	OmlExpression theLhsExpression = (OmlExpression) lhs;
	OmlExpression theRhsExpression = (OmlExpression) rhs;
	res.setLhsExpression(theLhsExpression);
	theOperator.setValue(qid);
	theOperator.setPosLexem(lex);
	res.setOperator(theOperator);
	res.setRhsExpression(theRhsExpression);
	res.setPosLexem(lex);
	return res;
}

/**********************************************************************/
/*** check and convert an object designator into a state designator ***/
/**********************************************************************/

private OmlStateDesignator checkStateDesignator (IOmlObjectDesignator theObjDesignator) throws CGException {
	if ( theObjDesignator instanceof OmlObjectDesignatorExpression ) {
		/* were are dealing with an expression as an object designator */
		OmlObjectDesignatorExpression theObjExpr = (OmlObjectDesignatorExpression) theObjDesignator;
		OmlExpression theExpr = (OmlExpression) theObjExpr.getExpression();
		if ( theExpr instanceof OmlName) {
			/* we are dealing with a name, which is ok */
			OmlStateDesignatorName res = new OmlStateDesignatorName();
			OmlName theName = (OmlName) theExpr;
			res.setName(theName);
			res.setPosNode(theName);
			return res;
		} else if (theExpr instanceof OmlNewExpression ) {
		 	/* we are dealing with a new expression, which is not ok */
			yyerror ("new expression is not allowed in a state designator");
			return null;
		} else {
			/* we are dealing with a self expression, which is not ok */
			yyerror ("self expression is not allowed in a state designator");
			return null;
		}
	} else if ( theObjDesignator instanceof OmlObjectFieldReference ) {
		/* we are dealing with a field reference as an object designator */
		OmlObjectFieldReference theObjFieldRef = (OmlObjectFieldReference) theObjDesignator;
		IOmlName theName = theObjFieldRef.getName();
		if ( theName.hasClassIdentifier() ) {
			yyerror ("class identifier \"" + theName.getClassIdentifier() + "\" is not allowed in a state designator");
		}
		OmlStateDesignator theDesignator = checkStateDesignator(theObjFieldRef.getObjectDesignator());
		OmlFieldReference res = new OmlFieldReference();
		res.setStateDesignator(theDesignator);
		res.setIdentifier(theName.getIdentifier());
		return res;
	} else {
	    /* we are dealing with an object apply as an object designator */
		OmlObjectApply theObjApply = (OmlObjectApply) theObjDesignator;
		OmlStateDesignator theDesignator = checkStateDesignator(theObjApply.getObjectDesignator());
		Vector exprList = theObjApply.getExpressionList();
		if ( exprList.size() == 0) {
			yyerror("an expression is required in the apply part of a state designator");
			return null;
		} else if ( exprList.size() > 1) {
			yyerror("only one expression allowed in the apply part of a state designator");
			return null;
		} else {
			OmlExpression theExpr = (OmlExpression) exprList.get(0);
			OmlMapOrSequenceReference res = new OmlMapOrSequenceReference();
			res.setStateDesignator(theDesignator);
			res.setExpression(theExpr);
			return res;
		}
	}
}

/********************************************************************/
/*** check and convert an object designator into a call statement ***/
/********************************************************************/

private OmlCallStatement checkCallStatement (IOmlObjectDesignator theObjDesignator) throws CGException {
	OmlCallStatement res = new OmlCallStatement();
	/* check for object apply first */
	if ( theObjDesignator instanceof OmlObjectApply) {
		OmlObjectApply theObjectApply = (OmlObjectApply) theObjDesignator;
		/* set the argument list for the call statement */
		res.setExpressionList(theObjectApply.getExpressionList());
		/* check the left-hand side of the object apply */
		if ( theObjectApply.getObjectDesignator() instanceof OmlObjectDesignatorExpression) {
			OmlObjectDesignatorExpression lvlOne = (OmlObjectDesignatorExpression) theObjectApply.getObjectDesignator();
			if ( lvlOne.getExpression() instanceof OmlName ) {
				OmlName theName = (OmlName) lvlOne.getExpression();
				res.setName(theName);
			} else {
				yyerror ("syntax error in call statement");
			}
		} else if ( theObjectApply.getObjectDesignator() instanceof OmlObjectFieldReference ) {
			OmlObjectFieldReference lvlOne = (OmlObjectFieldReference) theObjectApply.getObjectDesignator();
			OmlObjectDesignator theDesignator = (OmlObjectDesignator) lvlOne.getObjectDesignator();
			OmlName theName = (OmlName) lvlOne.getName();
			res.setObjectDesignator(theDesignator);
			res.setName(theName);
		} else {
			yyerror ("syntax error in call statement");
		}
	} else {
		yyerror ("call statement requires a parameter list");
	}
	return res;
}

private String stripString (final String istr) {
	int strlen = istr.length();
	if ( strlen == 2 ) {
		return new String();
	} else {
		return istr.substring(1,strlen-1);
	}
}  

private String lexemToString (OmlLexem lexem) throws CGException {
	return new String(lexem.getText());
}

// **************************************
// *** AUXILIARY PROTECTED OPERATIONS ***
// **************************************

protected void yyerror (String err)
{
	try  {
	    String msg = new String(err);
	    if (yylval != null) {
	    	OmlLexem currentToken = (OmlLexem) yylval;
	    	if (currentToken.getText().length() > 0) {
	    		// add position info iff the scanner found a lexem
	    	    msg += " at (" + getLine() + ", " + getColumn() + ")";
	    		msg += " after reading token \"" + currentToken.getText() + "\"";
	    	}
	    }
		System.out.println(msg);
		errors++;
	}
	catch (CGException cge) {
		cge.printStackTrace();
	}
}

protected int getLine()
{
	return theScanner.getLine();
}

protected int getColumn()
{
	return theScanner.getColumn();
}