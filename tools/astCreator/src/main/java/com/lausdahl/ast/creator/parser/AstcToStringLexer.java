// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g 2011-07-14 13:07:12
  
package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AstcToStringLexer extends Lexer {
    public static final int LINE_COMMENT=12;
    public static final int T__20=20;
    public static final int RawJava=10;
    public static final int TOSTRING_DCL=5;
    public static final int ID=7;
    public static final int EOF=-1;
    public static final int StringLiteral=9;
    public static final int T__19=19;
    public static final int T__16=16;
    public static final int WS=6;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int T__14=14;
    public static final int T__13=13;
    public static final int JAVANAME=8;
    public static final int ASSIGN=4;
    public static final int EscapeSequence=11;

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

    public AstcToStringLexer() {;} 
    public AstcToStringLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AstcToStringLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g"; }

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:92:8: ( '=' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:92:10: '='
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

    // $ANTLR start "TOSTRING_DCL"
    public final void mTOSTRING_DCL() throws RecognitionException {
        try {
            int _type = TOSTRING_DCL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:93:14: ( 'To String Extensions' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:93:16: 'To String Extensions'
            {
            match("To String Extensions"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TOSTRING_DCL"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:94:7: ( 'import' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:94:9: 'import'
            {
            match("import"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:95:7: ( ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:95:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:96:7: ( '%' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:96:9: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:97:7: ( '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:97:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:98:7: ( '[' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:98:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:99:7: ( ']' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:99:9: ']'
            {
            match(']'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:100:7: ( '->' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:100:9: '->'
            {
            match("->"); 


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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:101:7: ( '#' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:101:9: '#'
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

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:189:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:189:9: ( ' ' | '\\t' | '\\r' | '\\n' )
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

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:196:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:196:8: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:196:32: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:
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
            	    break loop1;
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:200:3: ( ID ( '.' ID )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:200:5: ID ( '.' ID )*
            {
            mID(); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:200:8: ( '.' ID )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='.') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:200:9: '.' ID
            	    {
            	    match('.'); 
            	    mID(); 

            	    }
            	    break;

            	default :
            	    break loop2;
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

    // $ANTLR start "RawJava"
    public final void mRawJava() throws RecognitionException {
        try {
            int _type = RawJava;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:5: ( '$' ( EscapeSequence | ~ ( '\\\\' | '$' ) )* '$' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:8: '$' ( EscapeSequence | ~ ( '\\\\' | '$' ) )* '$'
            {
            match('$'); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:12: ( EscapeSequence | ~ ( '\\\\' | '$' ) )*
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='\\') ) {
                    alt3=1;
                }
                else if ( ((LA3_0>='\u0000' && LA3_0<='#')||(LA3_0>='%' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFF')) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:14: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:31: ~ ( '\\\\' | '$' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='#')||(input.LA(1)>='%' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RawJava"

    // $ANTLR start "StringLiteral"
    public final void mStringLiteral() throws RecognitionException {
        try {
            int _type = StringLiteral;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:226:5: ( '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:226:8: '\"' ( EscapeSequence | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:226:12: ( EscapeSequence | ~ ( '\\\\' | '\"' ) )*
            loop4:
            do {
                int alt4=3;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='\\') ) {
                    alt4=1;
                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='!')||(LA4_0>='#' && LA4_0<='[')||(LA4_0>=']' && LA4_0<='\uFFFF')) ) {
                    alt4=2;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:226:14: EscapeSequence
            	    {
            	    mEscapeSequence(); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:226:31: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
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

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "StringLiteral"

    // $ANTLR start "EscapeSequence"
    public final void mEscapeSequence() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:259:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:259:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
            {
            match('\\'); 
            if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
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
    // $ANTLR end "EscapeSequence"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* )
            int alt8=2;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' )
                    {
                    match("//"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:14: (~ ( '\\n' | '\\r' ) )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='\u0000' && LA5_0<='\t')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\uFFFF')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:14: ~ ( '\\n' | '\\r' )
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
                    	    break loop5;
                        }
                    } while (true);

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:29: ( '\\r\\n' | '\\r' | '\\n' )
                    int alt6=3;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='\r') ) {
                        int LA6_1 = input.LA(2);

                        if ( (LA6_1=='\n') ) {
                            alt6=1;
                        }
                        else {
                            alt6=2;}
                    }
                    else if ( (LA6_0=='\n') ) {
                        alt6=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 0, input);

                        throw nvae;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:30: '\\r\\n'
                            {
                            match("\r\n"); 


                            }
                            break;
                        case 2 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:39: '\\r'
                            {
                            match('\r'); 

                            }
                            break;
                        case 3 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:288:46: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }


                                    skip();
                                

                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:292:9: '//' (~ ( '\\n' | '\\r' ) )*
                    {
                    match("//"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:292:14: (~ ( '\\n' | '\\r' ) )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='\u0000' && LA7_0<='\t')||(LA7_0>='\u000B' && LA7_0<='\f')||(LA7_0>='\u000E' && LA7_0<='\uFFFF')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:292:14: ~ ( '\\n' | '\\r' )
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
                    	    break loop7;
                        }
                    } while (true);


                                    skip();
                                

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    public void mTokens() throws RecognitionException {
        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:8: ( ASSIGN | TOSTRING_DCL | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | WS | ID | JAVANAME | RawJava | StringLiteral | LINE_COMMENT )
        int alt9=16;
        alt9 = dfa9.predict(input);
        switch (alt9) {
            case 1 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:10: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 2 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:17: TOSTRING_DCL
                {
                mTOSTRING_DCL(); 

                }
                break;
            case 3 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:30: T__13
                {
                mT__13(); 

                }
                break;
            case 4 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:36: T__14
                {
                mT__14(); 

                }
                break;
            case 5 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:42: T__15
                {
                mT__15(); 

                }
                break;
            case 6 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:48: T__16
                {
                mT__16(); 

                }
                break;
            case 7 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:54: T__17
                {
                mT__17(); 

                }
                break;
            case 8 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:60: T__18
                {
                mT__18(); 

                }
                break;
            case 9 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:66: T__19
                {
                mT__19(); 

                }
                break;
            case 10 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:72: T__20
                {
                mT__20(); 

                }
                break;
            case 11 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:78: WS
                {
                mWS(); 

                }
                break;
            case 12 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:81: ID
                {
                mID(); 

                }
                break;
            case 13 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:84: JAVANAME
                {
                mJAVANAME(); 

                }
                break;
            case 14 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:93: RawJava
                {
                mRawJava(); 

                }
                break;
            case 15 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:101: StringLiteral
                {
                mStringLiteral(); 

                }
                break;
            case 16 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:1:115: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    static final String DFA8_eotS =
        "\2\uffff\2\5\2\uffff";
    static final String DFA8_eofS =
        "\6\uffff";
    static final String DFA8_minS =
        "\2\57\2\0\2\uffff";
    static final String DFA8_maxS =
        "\2\57\2\uffff\2\uffff";
    static final String DFA8_acceptS =
        "\4\uffff\1\1\1\2";
    static final String DFA8_specialS =
        "\2\uffff\1\1\1\0\2\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\1",
            "\1\2",
            "\12\3\1\4\2\3\1\4\ufff2\3",
            "\12\3\1\4\2\3\1\4\ufff2\3",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "287:1: LINE_COMMENT : ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r\\n' | '\\r' | '\\n' ) | '//' (~ ( '\\n' | '\\r' ) )* );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_3 = input.LA(1);

                        s = -1;
                        if ( ((LA8_3>='\u0000' && LA8_3<='\t')||(LA8_3>='\u000B' && LA8_3<='\f')||(LA8_3>='\u000E' && LA8_3<='\uFFFF')) ) {s = 3;}

                        else if ( (LA8_3=='\n'||LA8_3=='\r') ) {s = 4;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA8_2 = input.LA(1);

                        s = -1;
                        if ( ((LA8_2>='\u0000' && LA8_2<='\t')||(LA8_2>='\u000B' && LA8_2<='\f')||(LA8_2>='\u000E' && LA8_2<='\uFFFF')) ) {s = 3;}

                        else if ( (LA8_2=='\n'||LA8_2=='\r') ) {s = 4;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA9_eotS =
        "\2\uffff\2\22\10\uffff\1\22\3\uffff\2\22\2\uffff\1\22\1\uffff\3"+
        "\22\1\32\1\uffff";
    static final String DFA9_eofS =
        "\33\uffff";
    static final String DFA9_minS =
        "\1\11\1\uffff\2\56\10\uffff\1\56\3\uffff\1\40\1\56\2\uffff\1\56"+
        "\1\uffff\4\56\1\uffff";
    static final String DFA9_maxS =
        "\1\172\1\uffff\2\172\10\uffff\1\172\3\uffff\2\172\2\uffff\1\172"+
        "\1\uffff\4\172\1\uffff";
    static final String DFA9_acceptS =
        "\1\uffff\1\1\2\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\uffff"+
        "\1\16\1\17\1\20\2\uffff\1\14\1\15\1\uffff\1\2\4\uffff\1\3";
    static final String DFA9_specialS =
        "\33\uffff}>";
    static final String[] DFA9_transitionS = {
            "\2\13\2\uffff\1\13\22\uffff\1\13\1\uffff\1\16\1\12\1\15\1\5"+
            "\5\uffff\1\6\1\uffff\1\11\1\uffff\1\17\13\uffff\1\4\1\uffff"+
            "\1\1\3\uffff\23\14\1\2\6\14\1\7\1\uffff\1\10\1\uffff\1\14\1"+
            "\uffff\10\14\1\3\21\14",
            "",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\16"+
            "\21\1\20\13\21",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\14"+
            "\21\1\24\15\21",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32"+
            "\21",
            "",
            "",
            "",
            "\1\25\15\uffff\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1"+
            "\21\1\uffff\32\21",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32"+
            "\21",
            "",
            "",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\17"+
            "\21\1\26\12\21",
            "",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\16"+
            "\21\1\27\13\21",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\21"+
            "\21\1\30\10\21",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\23"+
            "\21\1\31\6\21",
            "\1\23\1\uffff\12\21\7\uffff\32\21\4\uffff\1\21\1\uffff\32"+
            "\21",
            ""
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ASSIGN | TOSTRING_DCL | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | WS | ID | JAVANAME | RawJava | StringLiteral | LINE_COMMENT );";
        }
    }
 

}