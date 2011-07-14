grammar AstcToString;
//http://www.antlr.org/wiki/display/ANTLR3/Tree+construction
options{
	language=Java;
	output=AST;
}

tokens {
	ASSIGN = '=';
//	COLON =':';
//	AST = 'Abstract Syntax Tree';
//	TOKENS ='Tokens';
	TOSTRING_DCL='To String Extensions';
//	FIELD_DCL ='->';
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
 
 WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
 
 ID  :  ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

JAVANAME 
  : ID ('.' ID)*
  ;  
  
root
  : top EOF
  ;
  
  
top
  : TOSTRING_DCL^ imports* toString_*
  ;
  
imports
  : 'import'^ JAVANAME ';'!
  ;
  
  
toString_
  : '%'^ aspectName ASSIGN ( StringLiteral ('+' RawJava)? | field ('+'? RawJava)? | '+' (StringLiteral|field) )* (';'!?)
  ;
  //StringLiteral    | field | StringLiteral '+' RawJava | RawJava '+' StringLiteral | RawJava '+'? field '+'? RawJava | RawJava '+' field ('+'?)!(field |StringLiteral )
RawJava
    :  '$' ( EscapeSequence | ~('\\'|'$') )* '$'
    ; 
  
StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;
 
field
  : '['! ID^ ']'!
  ;
     
//SpecialChar
//    :     '"' | '\\' | '$' 
//    ;
//NormalChar
//    :    ~SpecialChar
//    ;
     
//STRINGLITERAL
//    :   '"' 
//        (   EscapeSequence
//        |   ~( '\\' | '"' | '\r' | '\n' )        
//        )* 
//        '"' 
//    ;
    
    

  

  
aspectName
  : ID^ ('->' (ID | '#' ID))*
  ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
//    |   UnicodeEscape
//    |   OctalEscape
    ;   


    
//COMMENT
//         @init{
//            boolean isJavaDoc = false;
//        }
//    :   '/*'
//            {
//                if((char)input.LA(1) == '*'){
//                    isJavaDoc = true;
//                }
//            }
//        (options {greedy=false;} : . )* 
//        '*/'
//            {
//                if(isJavaDoc==true){
//                    $channel=HIDDEN;
//                }else{
//                    skip();
//                }
//            }
//    ;

LINE_COMMENT
    :   '//' ~('\n'|'\r')*  ('\r\n' | '\r' | '\n') 
            {
                skip();
            }
    |   '//' ~('\n'|'\r')*     // a line comment could appear at the end of the file without CR/LF
            {
                skip();
            }
    ;   
        
 
              

