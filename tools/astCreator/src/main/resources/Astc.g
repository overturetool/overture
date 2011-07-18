grammar Astc;
//http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
options{
	language=Java;
	output=AST;
}

tokens {
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

@lexer::members{
    private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
   
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();
      
    public boolean hasExceptions()
    {
        return mExceptions.size() > 0;
    }

    public List<RecognitionException> getExceptions()
    {
        return mExceptions;
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames)
    {
        String msg = super.getErrorMessage(e, tokenNames);
        mExceptions.add(e);
        return msg;
    }

    /**
     *  Switches error message collection on or of.
     *
     *  The standard destination for parser error messages is <code>System.err</code>.
     *  However, if <code>true</code> gets passed to this method this default
     *  behaviour will be switched off and all error messages will be collected
     *  instead of written to anywhere.
     *
     *  The default value is <code>false</code>.
     *
     *  @param pNewState  <code>true</code> if error messages should be collected.
     */
    public void enableErrorMessageCollection(boolean pNewState) {
        mMessageCollectionEnabled = pNewState;
        if (mMessages == null && mMessageCollectionEnabled) {
            mMessages = new ArrayList<String>();
        }
    }
    
    /**
     *  Collects an error message or passes the error message to <code>
     *  super.emitErrorMessage(...)</code>.
     *
     *  The actual behaviour depends on whether collecting error messages
     *  has been enabled or not.
     *
     *  @param pMessage  The error message.
     */
     @Override
    public void emitErrorMessage(String pMessage) {
        if (mMessageCollectionEnabled) {
            mMessages.add(pMessage);
        } else {
            super.emitErrorMessage(pMessage);
        }
    }
    
    /**
     *  Returns collected error messages.
     *
     *  @return  A list holding collected error messages or <code>null</code> if
     *           collecting error messages hasn't been enabled. Of course, this
     *           list may be empty if no error message has been emited.
     */
    public List<String> getMessages() {
        return mMessages;
    }
    
    /**
     *  Tells if parsing a Java source has caused any error messages.
     *
     *  @return  <code>true</code> if parsing a Java source has caused at least one error message.
     */
    public boolean hasErrors() {
        return mHasErrors;
    }
} 

@header {
package com.lausdahl.ast.creator.parser;
}

@members {
    private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();

    public boolean hasExceptions()
    {
        return mExceptions.size() > 0;
    }

    public List<RecognitionException> getExceptions()
    {
        return mExceptions;
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames)
    {
        String msg = super.getErrorMessage(e, tokenNames);
        mExceptions.add(e);
        return msg;
    }

    /**
     *  Switches error message collection on or of.
     *
     *  The standard destination for parser error messages is <code>System.err</code>.
     *  However, if <code>true</code> gets passed to this method this default
     *  behaviour will be switched off and all error messages will be collected
     *  instead of written to anywhere.
     *
     *  The default value is <code>false</code>.
     *
     *  @param pNewState  <code>true</code> if error messages should be collected.
     */
    public void enableErrorMessageCollection(boolean pNewState) {
        mMessageCollectionEnabled = pNewState;
        if (mMessages == null && mMessageCollectionEnabled) {
            mMessages = new ArrayList<String>();
        }
    }
    
    /**
     *  Collects an error message or passes the error message to <code>
     *  super.emitErrorMessage(...)</code>.
     *
     *  The actual behaviour depends on whether collecting error messages
     *  has been enabled or not.
     *
     *  @param pMessage  The error message.
     */
     @Override
    public void emitErrorMessage(String pMessage) {
        mHasErrors=true;
        if (mMessageCollectionEnabled) {
            mMessages.add(pMessage);
        } else {
            super.emitErrorMessage(pMessage);
        }
    }
    
    /**
     *  Returns collected error messages.
     *
     *  @return  A list holding collected error messages or <code>null</code> if
     *           collecting error messages hasn't been enabled. Of course, this
     *           list may be empty if no error message has been emited.
     */
    public List<String> getMessages() {
        return mMessages;
    }
    
    /**
     *  Tells if parsing a Java source has caused any error messages.
     *
     *  @return  <code>true</code> if parsing a Java source has caused at least one error message.
     */
    public boolean hasErrors() {
        return mHasErrors;
    }
}
 
QUOTE   :      '\'';
    
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
 
WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\\')
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
  : '%'^ dd 
  ;
  
dd 
  :aspectName ASSIGN (definitions)* -> ^(ID["ASPECT"] aspectName (definitions)*)
  ;
 
aspectName
  : ID^ ('->' name)*
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
  : (('[' ID ']'! ':'!)?| ('(' ID ')'! ':'!)) (ID |JAVANAME)^ (repeat)?
  
  ;
  
//typeName
//  : ID '.' ID
//  ;
  
repeat
  : '?'
  | '*'
  | '**'
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