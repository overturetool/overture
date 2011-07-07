// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-07-06 23:31:32
  
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
    public static final int TOKENS=7;
    public static final int NormalChar=16;
    public static final int ID=12;
    public static final int ASPECT_DCL=8;
    public static final int EOF=-1;
    public static final int COLON=5;
    public static final int T__30=30;
    public static final int T__19=19;
    public static final int QUOTE=10;
    public static final int WS=14;
    public static final int ESC_SEQ=15;
    public static final int T__18=18;
    public static final int JAVANAME=13;
    public static final int ASSIGN=4;
    public static final int SpecialChar=17;
    public static final int AST=6;
    public static final int COMMENT=11;
    public static final int FIELD_DCL=9;

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
    public String getGrammarFileName() { return "C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g"; }

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:92:8: ( '=' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:92:10: '='
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:93:7: ( ':' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:93:9: ':'
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

    // $ANTLR start "AST"
    public final void mAST() throws RecognitionException {
        try {
            int _type = AST;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:94:5: ( 'Abstract Syntax Tree' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:94:7: 'Abstract Syntax Tree'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:95:8: ( 'Tokens' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:95:10: 'Tokens'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:96:12: ( 'Aspect Declaration' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:96:14: 'Aspect Declaration'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:11: ( '->' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:13: '->'
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

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:98:7: ( ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:98:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:99:7: ( '%' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:99:9: '%'
            {
            match('%'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:100:7: ( '#' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:100:9: '#'
            {
            match('#'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:101:7: ( '|' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:101:9: '|'
            {
            match('|'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:102:7: ( '{' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:102:9: '{'
            {
            match('{'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:7: ( '}' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:9: '}'
            {
            match('}'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:104:7: ( '[' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:104:9: '['
            {
            match('['); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:105:7: ( ']' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:105:9: ']'
            {
            match(']'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:106:7: ( '?' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:106:9: '?'
            {
            match('?'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:7: ( '*' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:9: '*'
            {
            match('*'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:108:7: ( '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:108:9: '+'
            {
            match('+'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:109:7: ( '||' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:109:9: '||'
            {
            match("||"); 


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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:110:7: ( '&&' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:110:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:188:9: ( '\\'' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:188:16: '\\''
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:5: ( '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '/*' ( options {greedy=false; } : . )* '*/' )
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
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:9: '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("--"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:14: (~ ( '\\n' | '\\r' ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='\u0000' && LA1_0<='\t')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:14: ~ ( '\\n' | '\\r' )
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

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:28: ( '\\r' )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0=='\r') ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:34: ( '\\n' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\n') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:191:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("//"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:14: ~ ( '\\n' | '\\r' )
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

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:34: ( '\\n' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='\n') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:192:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 3 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:193:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:193:14: ( options {greedy=false; } : . )*
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
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:193:42: .
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:196:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>='0' && LA9_0<='9')||(LA9_0>='A' && LA9_0<='Z')||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:3: ( ID ( '.' ID )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:5: ID ( '.' ID )*
            {
            mID(); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:8: ( '.' ID )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='.') ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:200:9: '.' ID
            	    {
            	    match('.'); 
            	    mID(); 

            	    }
            	    break;

            	default :
            	    break loop10;
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:203:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:203:9: ( ' ' | '\\t' | '\\r' | '\\n' )
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:212:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:212:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' )
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:289:5: ( '\"' | '\\\\' | '$' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:5: (~ SpecialChar )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:10: ~ SpecialChar
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u0010')||(input.LA(1)>='\u0012' && input.LA(1)<='\uFFFF') ) {
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
        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:8: ( ASSIGN | COLON | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | QUOTE | COMMENT | ID | JAVANAME | WS | SpecialChar | NormalChar )
        int alt11=26;
        alt11 = dfa11.predict(input);
        switch (alt11) {
            case 1 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:10: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 2 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:17: COLON
                {
                mCOLON(); 

                }
                break;
            case 3 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:23: AST
                {
                mAST(); 

                }
                break;
            case 4 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:27: TOKENS
                {
                mTOKENS(); 

                }
                break;
            case 5 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:34: ASPECT_DCL
                {
                mASPECT_DCL(); 

                }
                break;
            case 6 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:45: FIELD_DCL
                {
                mFIELD_DCL(); 

                }
                break;
            case 7 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:55: T__18
                {
                mT__18(); 

                }
                break;
            case 8 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:61: T__19
                {
                mT__19(); 

                }
                break;
            case 9 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:67: T__20
                {
                mT__20(); 

                }
                break;
            case 10 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:73: T__21
                {
                mT__21(); 

                }
                break;
            case 11 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:79: T__22
                {
                mT__22(); 

                }
                break;
            case 12 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:85: T__23
                {
                mT__23(); 

                }
                break;
            case 13 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:91: T__24
                {
                mT__24(); 

                }
                break;
            case 14 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:97: T__25
                {
                mT__25(); 

                }
                break;
            case 15 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:103: T__26
                {
                mT__26(); 

                }
                break;
            case 16 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:109: T__27
                {
                mT__27(); 

                }
                break;
            case 17 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:115: T__28
                {
                mT__28(); 

                }
                break;
            case 18 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:121: T__29
                {
                mT__29(); 

                }
                break;
            case 19 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:127: T__30
                {
                mT__30(); 

                }
                break;
            case 20 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:133: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 21 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:139: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 22 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:147: ID
                {
                mID(); 

                }
                break;
            case 23 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:150: JAVANAME
                {
                mJAVANAME(); 

                }
                break;
            case 24 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:159: WS
                {
                mWS(); 

                }
                break;
            case 25 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:162: SpecialChar
                {
                mSpecialChar(); 

                }
                break;
            case 26 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:174: NormalChar
                {
                mNormalChar(); 

                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA11_eotS =
        "\3\uffff\2\34\1\27\3\uffff\1\46\7\uffff\1\27\1\uffff\1\27\1\34"+
        "\5\uffff\2\34\1\uffff\1\34\1\uffff\1\34\21\uffff\13\34\1\77\1\34"+
        "\2\uffff\1\34\1\uffff";
    static final String DFA11_eofS =
        "\102\uffff";
    static final String DFA11_minS =
        "\1\0\2\uffff\2\56\1\55\3\uffff\1\174\7\uffff\1\46\1\uffff\1\52"+
        "\1\56\5\uffff\2\56\1\uffff\1\56\1\uffff\1\56\21\uffff\12\56\1\40"+
        "\2\56\2\uffff\1\40\1\uffff";
    static final String DFA11_maxS =
        "\1\uffff\2\uffff\2\172\1\76\3\uffff\1\174\7\uffff\1\46\1\uffff"+
        "\1\57\1\172\5\uffff\2\172\1\uffff\1\172\1\uffff\1\172\21\uffff\15"+
        "\172\2\uffff\1\172\1\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\1\2\3\uffff\1\7\1\10\1\11\1\uffff\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\uffff\1\24\2\uffff\1\30\1\31\1\32\1\1\1\2\2"+
        "\uffff\1\26\1\uffff\1\27\1\uffff\1\6\1\25\1\7\1\10\1\11\1\22\1\12"+
        "\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\23\1\24\1\30\15\uffff\1\5"+
        "\1\4\1\uffff\1\3";
    static final String DFA11_specialS =
        "\1\0\101\uffff}>";
    static final String[] DFA11_transitionS = {
            "\11\27\2\25\2\27\1\25\22\27\1\25\1\27\1\26\1\10\1\26\1\7\1"+
            "\21\1\22\2\27\1\17\1\20\1\27\1\5\1\27\1\23\12\27\1\2\1\6\1\27"+
            "\1\1\1\27\1\16\1\27\1\3\22\24\1\4\6\24\1\14\1\26\1\15\1\27\1"+
            "\24\1\27\32\24\1\12\1\11\1\13\uff82\27",
            "",
            "",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\1\35"+
            "\1\32\20\35\1\33\7\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\16"+
            "\35\1\37\13\35",
            "\1\41\20\uffff\1\40",
            "",
            "",
            "",
            "\1\45",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\56",
            "",
            "\1\41\4\uffff\1\41",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32"+
            "\35",
            "",
            "",
            "",
            "",
            "",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\22"+
            "\35\1\61\7\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\17"+
            "\35\1\62\12\35",
            "",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32"+
            "\35",
            "",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\12"+
            "\35\1\63\17\35",
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
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\23"+
            "\35\1\64\6\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\4\35"+
            "\1\65\25\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\4\35"+
            "\1\66\25\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\21"+
            "\35\1\67\10\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\2\35"+
            "\1\70\27\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\15"+
            "\35\1\71\14\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\1\72"+
            "\31\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\23"+
            "\35\1\73\6\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\22"+
            "\35\1\74\7\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\2\35"+
            "\1\75\27\35",
            "\1\76\15\uffff\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1"+
            "\35\1\uffff\32\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\32"+
            "\35",
            "\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1\35\1\uffff\23"+
            "\35\1\100\6\35",
            "",
            "",
            "\1\101\15\uffff\1\36\1\uffff\12\35\7\uffff\32\35\4\uffff\1"+
            "\35\1\uffff\32\35",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ASSIGN | COLON | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | QUOTE | COMMENT | ID | JAVANAME | WS | SpecialChar | NormalChar );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA11_0 = input.LA(1);

                        s = -1;
                        if ( (LA11_0=='=') ) {s = 1;}

                        else if ( (LA11_0==':') ) {s = 2;}

                        else if ( (LA11_0=='A') ) {s = 3;}

                        else if ( (LA11_0=='T') ) {s = 4;}

                        else if ( (LA11_0=='-') ) {s = 5;}

                        else if ( (LA11_0==';') ) {s = 6;}

                        else if ( (LA11_0=='%') ) {s = 7;}

                        else if ( (LA11_0=='#') ) {s = 8;}

                        else if ( (LA11_0=='|') ) {s = 9;}

                        else if ( (LA11_0=='{') ) {s = 10;}

                        else if ( (LA11_0=='}') ) {s = 11;}

                        else if ( (LA11_0=='[') ) {s = 12;}

                        else if ( (LA11_0==']') ) {s = 13;}

                        else if ( (LA11_0=='?') ) {s = 14;}

                        else if ( (LA11_0=='*') ) {s = 15;}

                        else if ( (LA11_0=='+') ) {s = 16;}

                        else if ( (LA11_0=='&') ) {s = 17;}

                        else if ( (LA11_0=='\'') ) {s = 18;}

                        else if ( (LA11_0=='/') ) {s = 19;}

                        else if ( ((LA11_0>='B' && LA11_0<='S')||(LA11_0>='U' && LA11_0<='Z')||LA11_0=='_'||(LA11_0>='a' && LA11_0<='z')) ) {s = 20;}

                        else if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {s = 21;}

                        else if ( (LA11_0=='\"'||LA11_0=='$'||LA11_0=='\\') ) {s = 22;}

                        else if ( ((LA11_0>='\u0000' && LA11_0<='\b')||(LA11_0>='\u000B' && LA11_0<='\f')||(LA11_0>='\u000E' && LA11_0<='\u001F')||LA11_0=='!'||(LA11_0>='(' && LA11_0<=')')||LA11_0==','||LA11_0=='.'||(LA11_0>='0' && LA11_0<='9')||LA11_0=='<'||LA11_0=='>'||LA11_0=='@'||LA11_0=='^'||LA11_0=='`'||(LA11_0>='~' && LA11_0<='\uFFFF')) ) {s = 23;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 11, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}