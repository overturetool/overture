// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-11-29 13:07:59
  
package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AstcLexer extends Lexer {
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int TOKENS=8;
    public static final int NormalChar=17;
    public static final int ID=13;
    public static final int ASPECT_DCL=9;
    public static final int EOF=-1;
    public static final int COLON=5;
    public static final int T__30=30;
    public static final int T__19=19;
    public static final int T__31=31;
    public static final int QUOTE=11;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=15;
    public static final int ESC_SEQ=16;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int JAVANAME=14;
    public static final int ASSIGN=4;
    public static final int SpecialChar=18;
    public static final int PACKAGES=6;
    public static final int AST=7;
    public static final int COMMENT=12;
    public static final int FIELD_DCL=10;

        @SuppressWarnings({ "unused", "rawtypes" })
        private Stack myStack = null;
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


    // delegates
    // delegators

    public AstcLexer() {;} 
    public AstcLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AstcLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g"; }

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:94:8: ( '=' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:94:10: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:95:7: ( ':' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:95:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "PACKAGES"
    public final void mPACKAGES() throws RecognitionException {
        try {
            int _type = PACKAGES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:96:10: ( 'Packages' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:96:12: 'Packages'
            {
            match("Packages"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PACKAGES"

    // $ANTLR start "AST"
    public final void mAST() throws RecognitionException {
        try {
            int _type = AST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:5: ( 'Abstract Syntax Tree' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:7: 'Abstract Syntax Tree'
            {
            match("Abstract Syntax Tree"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AST"

    // $ANTLR start "TOKENS"
    public final void mTOKENS() throws RecognitionException {
        try {
            int _type = TOKENS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:98:8: ( 'Tokens' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:98:10: 'Tokens'
            {
            match("Tokens"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TOKENS"

    // $ANTLR start "ASPECT_DCL"
    public final void mASPECT_DCL() throws RecognitionException {
        try {
            int _type = ASPECT_DCL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:99:12: ( 'Aspect Declaration' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:99:14: 'Aspect Declaration'
            {
            match("Aspect Declaration"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASPECT_DCL"

    // $ANTLR start "FIELD_DCL"
    public final void mFIELD_DCL() throws RecognitionException {
        try {
            int _type = FIELD_DCL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:100:11: ( '->' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:100:13: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FIELD_DCL"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:101:7: ( 'base' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:101:9: 'base'
            {
            match("base"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:102:7: ( ';' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:102:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:7: ( 'analysis' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:9: 'analysis'
            {
            match("analysis"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:104:7: ( '%' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:104:9: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:105:7: ( '|' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:105:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:106:7: ( '#' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:106:9: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:7: ( '{' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:108:7: ( '}' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:108:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:109:7: ( '[' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:109:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:110:7: ( ']' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:110:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:7: ( '(' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:112:7: ( ')' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:112:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:113:7: ( '?' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:113:9: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:114:7: ( '*' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:114:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:7: ( '**' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:9: '**'
            {
            match("**"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:116:7: ( '+' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:116:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:117:7: ( '||' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:117:9: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:118:7: ( '&&' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:118:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:9: ( '\\'' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:16: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUOTE"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:5: ( '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt8=3;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='-') ) {
                alt8=1;
            }
            else if ( (LA8_0=='/') ) {
                int LA8_2 = input.LA(2);

                if ( (LA8_2=='/') ) {
                    alt8=2;
                }
                else if ( (LA8_2=='*') ) {
                    alt8=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:9: '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("--"); 

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:14: (~ ( '\\n' | '\\r' ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='\u0000' && LA1_0<='\t')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop1;
                        }
                    } while (true);

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:28: ( '\\r' )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0=='\r') ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:34: ( '\\n' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\n') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:195:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("//"); 

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:34: ( '\\n' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='\n') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 3 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:197:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:197:14: ( options {greedy=false; } : . )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0=='*') ) {
                            int LA7_1 = input.LA(2);

                            if ( (LA7_1=='/') ) {
                                alt7=2;
                            }
                            else if ( ((LA7_1>='\u0000' && LA7_1<='.')||(LA7_1>='0' && LA7_1<='\uFFFF')) ) {
                                alt7=1;
                            }


                        }
                        else if ( ((LA7_0>='\u0000' && LA7_0<=')')||(LA7_0>='+' && LA7_0<='\uFFFF')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:197:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match("*/"); 

                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>='0' && LA9_0<='9')||(LA9_0>='A' && LA9_0<='Z')||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "JAVANAME"
    public final void mJAVANAME() throws RecognitionException {
        try {
            int _type = JAVANAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:3: ( ID ( '.' ( '#' )? ID )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:5: ID ( '.' ( '#' )? ID )*
            {
            mID(); 
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:8: ( '.' ( '#' )? ID )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0=='.') ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:9: '.' ( '#' )? ID
            	    {
            	    match('.'); 
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:13: ( '#' )?
            	    int alt10=2;
            	    int LA10_0 = input.LA(1);

            	    if ( (LA10_0=='#') ) {
            	        alt10=1;
            	    }
            	    switch (alt10) {
            	        case 1 :
            	            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:204:13: '#'
            	            {
            	            match('#'); 

            	            }
            	            break;

            	    }

            	    mID(); 

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "JAVANAME"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:213:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:213:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' )
            {
            match('\\'); 
            if ( input.LA(1)=='\"'||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "SpecialChar"
    public final void mSpecialChar() throws RecognitionException {
        try {
            int _type = SpecialChar;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:315:5: ( '\"' | '\\\\' | '$' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            {
            if ( input.LA(1)=='\"'||input.LA(1)=='$'||input.LA(1)=='\\' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SpecialChar"

    // $ANTLR start "NormalChar"
    public final void mNormalChar() throws RecognitionException {
        try {
            int _type = NormalChar;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:318:5: (~ SpecialChar )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:318:10: ~ SpecialChar
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u0011')||(input.LA(1)>='\u0013' && input.LA(1)<='\uFFFF') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NormalChar"

    public void mTokens() throws RecognitionException {
        // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:8: ( ASSIGN | COLON | PACKAGES | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | QUOTE | COMMENT | ID | JAVANAME | WS | SpecialChar | NormalChar )
        int alt12=32;
        alt12 = dfa12.predict(input);
        switch (alt12) {
            case 1 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:10: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 2 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:17: COLON
                {
                mCOLON(); 

                }
                break;
            case 3 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:23: PACKAGES
                {
                mPACKAGES(); 

                }
                break;
            case 4 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:32: AST
                {
                mAST(); 

                }
                break;
            case 5 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:36: TOKENS
                {
                mTOKENS(); 

                }
                break;
            case 6 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:43: ASPECT_DCL
                {
                mASPECT_DCL(); 

                }
                break;
            case 7 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:54: FIELD_DCL
                {
                mFIELD_DCL(); 

                }
                break;
            case 8 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:64: T__19
                {
                mT__19(); 

                }
                break;
            case 9 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:70: T__20
                {
                mT__20(); 

                }
                break;
            case 10 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:76: T__21
                {
                mT__21(); 

                }
                break;
            case 11 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:82: T__22
                {
                mT__22(); 

                }
                break;
            case 12 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:88: T__23
                {
                mT__23(); 

                }
                break;
            case 13 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:94: T__24
                {
                mT__24(); 

                }
                break;
            case 14 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:100: T__25
                {
                mT__25(); 

                }
                break;
            case 15 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:106: T__26
                {
                mT__26(); 

                }
                break;
            case 16 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:112: T__27
                {
                mT__27(); 

                }
                break;
            case 17 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:118: T__28
                {
                mT__28(); 

                }
                break;
            case 18 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:124: T__29
                {
                mT__29(); 

                }
                break;
            case 19 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:130: T__30
                {
                mT__30(); 

                }
                break;
            case 20 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:136: T__31
                {
                mT__31(); 

                }
                break;
            case 21 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:142: T__32
                {
                mT__32(); 

                }
                break;
            case 22 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:148: T__33
                {
                mT__33(); 

                }
                break;
            case 23 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:154: T__34
                {
                mT__34(); 

                }
                break;
            case 24 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:160: T__35
                {
                mT__35(); 

                }
                break;
            case 25 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:166: T__36
                {
                mT__36(); 

                }
                break;
            case 26 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:172: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 27 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:178: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 28 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:186: ID
                {
                mID(); 

                }
                break;
            case 29 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:189: JAVANAME
                {
                mJAVANAME(); 

                }
                break;
            case 30 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:198: WS
                {
                mWS(); 

                }
                break;
            case 31 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:201: SpecialChar
                {
                mSpecialChar(); 

                }
                break;
            case 32 :
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:213: NormalChar
                {
                mNormalChar(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA12_eotS =
        "\3\uffff\3\41\1\34\1\41\1\uffff\1\41\1\uffff\1\55\10\uffff\1\67"+
        "\1\uffff\1\34\1\uffff\1\34\1\41\5\uffff\2\41\2\uffff\3\41\2\uffff"+
        "\1\41\1\uffff\1\41\21\uffff\12\41\1\114\5\41\1\uffff\4\41\1\126"+
        "\3\41\2\uffff\1\41\1\133\1\41\1\135\3\uffff";
    static final String DFA12_eofS =
        "\136\uffff";
    static final String DFA12_minS =
        "\1\0\2\uffff\3\56\1\55\1\56\1\uffff\1\56\1\uffff\1\174\10\uffff"+
        "\1\52\1\uffff\1\46\1\uffff\1\52\1\56\5\uffff\2\56\2\uffff\3\56\2"+
        "\uffff\1\56\1\uffff\1\56\21\uffff\20\56\1\uffff\3\56\1\40\4\56\2"+
        "\uffff\2\56\1\40\1\56\3\uffff";
    static final String DFA12_maxS =
        "\1\uffff\2\uffff\3\172\1\76\1\172\1\uffff\1\172\1\uffff\1\174\10"+
        "\uffff\1\52\1\uffff\1\46\1\uffff\1\57\1\172\5\uffff\2\172\2\uffff"+
        "\3\172\2\uffff\1\172\1\uffff\1\172\21\uffff\20\172\1\uffff\10\172"+
        "\2\uffff\4\172\3\uffff";
    static final String DFA12_acceptS =
        "\1\uffff\1\1\1\2\5\uffff\1\11\1\uffff\1\13\1\uffff\1\15\1\16\1"+
        "\17\1\20\1\21\1\22\1\23\1\24\1\uffff\1\27\1\uffff\1\32\2\uffff\1"+
        "\36\1\37\1\40\1\1\1\2\2\uffff\1\34\1\35\3\uffff\1\7\1\33\1\uffff"+
        "\1\11\1\uffff\1\13\1\30\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
        "\1\24\1\26\1\25\1\27\1\31\1\32\1\36\20\uffff\1\10\10\uffff\1\6\1"+
        "\5\4\uffff\1\3\1\4\1\12";
    static final String DFA12_specialS =
        "\1\0\135\uffff}>";
    static final String[] DFA12_transitionS = {
            "\11\34\2\32\2\34\1\32\22\34\1\32\1\34\1\33\1\14\1\33\1\12\1"+
            "\26\1\27\1\21\1\22\1\24\1\25\1\34\1\6\1\34\1\30\12\34\1\2\1"+
            "\10\1\34\1\1\1\34\1\23\1\34\1\4\16\31\1\3\3\31\1\5\6\31\1\17"+
            "\1\33\1\20\1\34\1\31\1\34\1\11\1\7\30\31\1\15\1\13\1\16\uff82"+
            "\34",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\37"+
            "\31\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\40"+
            "\1\43\20\40\1\44\7\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\16"+
            "\40\1\45\13\40",
            "\1\47\20\uffff\1\46",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\50"+
            "\31\40",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\15"+
            "\40\1\52\14\40",
            "",
            "\1\54",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\66",
            "",
            "\1\71",
            "",
            "\1\47\4\uffff\1\47",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "",
            "",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\2\40"+
            "\1\74\27\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\75\7\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\17"+
            "\40\1\76\12\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\12"+
            "\40\1\77\17\40",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\100\7\40",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\101"+
            "\31\40",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\12"+
            "\40\1\102\17\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\23"+
            "\40\1\103\6\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\4\40"+
            "\1\104\25\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\4\40"+
            "\1\105\25\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\4\40"+
            "\1\106\25\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\13"+
            "\40\1\107\16\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\110"+
            "\31\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\21"+
            "\40\1\111\10\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\2\40"+
            "\1\112\27\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\15"+
            "\40\1\113\14\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\30"+
            "\40\1\115\1\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\6\40"+
            "\1\116\23\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\1\117"+
            "\31\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\23"+
            "\40\1\120\6\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\121\7\40",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\122\7\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\4\40"+
            "\1\123\25\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\2\40"+
            "\1\124\27\40",
            "\1\125\15\uffff\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1"+
            "\40\1\uffff\32\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\10"+
            "\40\1\127\21\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\130\7\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\23"+
            "\40\1\131\6\40",
            "",
            "",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\22"+
            "\40\1\132\7\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "\1\134\15\uffff\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1"+
            "\40\1\uffff\32\40",
            "\1\42\1\uffff\12\40\7\uffff\32\40\4\uffff\1\40\1\uffff\32"+
            "\40",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ASSIGN | COLON | PACKAGES | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | QUOTE | COMMENT | ID | JAVANAME | WS | SpecialChar | NormalChar );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_0 = input.LA(1);

                        s = -1;
                        if ( (LA12_0=='=') ) {s = 1;}

                        else if ( (LA12_0==':') ) {s = 2;}

                        else if ( (LA12_0=='P') ) {s = 3;}

                        else if ( (LA12_0=='A') ) {s = 4;}

                        else if ( (LA12_0=='T') ) {s = 5;}

                        else if ( (LA12_0=='-') ) {s = 6;}

                        else if ( (LA12_0=='b') ) {s = 7;}

                        else if ( (LA12_0==';') ) {s = 8;}

                        else if ( (LA12_0=='a') ) {s = 9;}

                        else if ( (LA12_0=='%') ) {s = 10;}

                        else if ( (LA12_0=='|') ) {s = 11;}

                        else if ( (LA12_0=='#') ) {s = 12;}

                        else if ( (LA12_0=='{') ) {s = 13;}

                        else if ( (LA12_0=='}') ) {s = 14;}

                        else if ( (LA12_0=='[') ) {s = 15;}

                        else if ( (LA12_0==']') ) {s = 16;}

                        else if ( (LA12_0=='(') ) {s = 17;}

                        else if ( (LA12_0==')') ) {s = 18;}

                        else if ( (LA12_0=='?') ) {s = 19;}

                        else if ( (LA12_0=='*') ) {s = 20;}

                        else if ( (LA12_0=='+') ) {s = 21;}

                        else if ( (LA12_0=='&') ) {s = 22;}

                        else if ( (LA12_0=='\'') ) {s = 23;}

                        else if ( (LA12_0=='/') ) {s = 24;}

                        else if ( ((LA12_0>='B' && LA12_0<='O')||(LA12_0>='Q' && LA12_0<='S')||(LA12_0>='U' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='c' && LA12_0<='z')) ) {s = 25;}

                        else if ( ((LA12_0>='\t' && LA12_0<='\n')||LA12_0=='\r'||LA12_0==' ') ) {s = 26;}

                        else if ( (LA12_0=='\"'||LA12_0=='$'||LA12_0=='\\') ) {s = 27;}

                        else if ( ((LA12_0>='\u0000' && LA12_0<='\b')||(LA12_0>='\u000B' && LA12_0<='\f')||(LA12_0>='\u000E' && LA12_0<='\u001F')||LA12_0=='!'||LA12_0==','||LA12_0=='.'||(LA12_0>='0' && LA12_0<='9')||LA12_0=='<'||LA12_0=='>'||LA12_0=='@'||LA12_0=='^'||LA12_0=='`'||(LA12_0>='~' && LA12_0<='\uFFFF')) ) {s = 28;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}
