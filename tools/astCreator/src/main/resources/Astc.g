grammar Astc;
//http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
options{
	language=Java;
	output=AST;
}

tokens {
//	CONTRACT = 'contract';
//	DESIGN_PARAMETER = 'shared_design_parameter';
//	SDP = 'sdp';
//	MONITORED = 'monitored';
//	CONTROLLED = 'controlled';
//	REAL = 'real';
//	BOOL = 'bool';
//	EVENT = 'event';
//	END = 'end';
	ASSIGN = '=';
	COLON =':';
	AST = 'Abstract Syntax Tree';
	TOKENS ='Tokens';
	ASPECT_DCL='Aspect Declaration';
	FIELD_DCL ='->';
}

@lexer::header{  
package com.lausdahl.ast.creator.parser;
}  

@header {
package com.lausdahl.ast.creator.parser;
}
 
QUOTE   :      '\'';
    
    
//BOOL_VAL 
//	: 'true'
//	| 'false'
//	;

COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;
	
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

JAVANAME 
  : ID ('.' ID)*
  ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;
    
 
WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

//CHAR:   ( ESC_SEQ | ~('\\') ) 
//    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
    
    
  
root
  : toks ast aspectdcl   
  ;
  
ast
  : AST^ ((production)*)
  ;
  
toks
  : TOKENS^ ((token)*)
  ;
  
aspectdcl
  : ASPECT_DCL^ (aspectdcla ';'!)*
  ;
  
aspectdcla
  :  ID^ ASSIGN! ((definitions)*)
  ;
  


production
  : name productionfields? ASSIGN alternative ('|' alternative)* ';' -> ^(ID["P"] name productionfields? (alternative)*) 
  ;
  
name 
  : ID^ 
  | '#' ID->^('#' ID)
  ;

 
   
productionfields
  : '{'! FIELD_DCL^ productionfield* '}'!
  ;
  
productionfield
  : ID^ ASSIGN! QUOTE! stringLiteral QUOTE!
  ; 

alternative
  : ('{' ID '}')? (definitions)* -> ^(ID (definitions)*)
  | '#' ID -> ^(ID["ALTERNATIVE_SUB_ROOT"] ID)
  ;
  
definitions
  : ('['! ID ']'! ':'!)? (ID |JAVANAME)^ (repeat)?
  ;
  
//typeName
//  : ID '.' ID
//  ;
  
repeat
  : '?'
  | '*'
  | '+'
  ;


token
  : ID^ ASSIGN! QUOTE! stringLiteral QUOTE! ';'!
  ;
  
stringLiteral
    :     (ID | NormalChar | '+'|'||'|'&&'|(':')!| JAVANAME )*  
    ;
    
SpecialChar
    :     '"' | '\\' | '$'
    ;
NormalChar
    :    ~SpecialChar
    ;