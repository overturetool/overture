// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-07-04 22:54:34
  
package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class AstcLexer extends Lexer {
    public static final int EXPONENT=15;
    public static final int T__29=29;
    public static final int T__28=28;
    public static final int T__27=27;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int UNICODE_ESC=19;
    public static final int OCTAL_ESC=20;
    public static final int HEX_DIGIT=18;
    public static final int TOKENS=7;
    public static final int FLOAT=16;
    public static final int INT=14;
    public static final int NormalChar=22;
    public static final int ID=12;
    public static final int ASPECT_DCL=8;
    public static final int EOF=-1;
    public static final int COLON=5;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int QUOTE=10;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int WS=17;
    public static final int ESC_SEQ=21;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int JAVANAME=13;
    public static final int ASSIGN=4;
    public static final int SpecialChar=23;
    public static final int AST=6;
    public static final int COMMENT=11;
    public static final int FIELD_DCL=9;

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:11:8: ( '=' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:11:10: '='
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:12:7: ( ':' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:12:9: ':'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:13:5: ( 'Abstract Syntax Tree' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:13:7: 'Abstract Syntax Tree'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:14:8: ( 'Tokens' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:14:10: 'Tokens'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:15:12: ( 'Aspect Declaration' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:15:14: 'Aspect Declaration'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:16:11: ( '->' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:16:13: '->'
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

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:17:7: ( ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:17:9: ';'
            {
            match(';'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:18:7: ( '|' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:18:9: '|'
            {
            match('|'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:19:7: ( '#' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:19:9: '#'
            {
            match('#'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:20:7: ( '{' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:20:9: '{'
            {
            match('{'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:21:7: ( '}' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:21:9: '}'
            {
            match('}'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:22:7: ( '[' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:22:9: '['
            {
            match('['); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:23:7: ( ']' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:23:9: ']'
            {
            match(']'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:24:7: ( '?' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:24:9: '?'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:25:7: ( '*' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:25:9: '*'
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:26:7: ( '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:26:9: '+'
            {
            match('+'); 

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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:27:7: ( '||' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:27:9: '||'
            {
            match("||"); 


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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:28:7: ( '&&' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:28:9: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:34:9: ( '\\'' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:34:16: '\\''
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:5: ( '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '/*' ( options {greedy=false; } : . )* '*/' )
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
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:9: '--' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("--"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:14: (~ ( '\\n' | '\\r' ) )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( ((LA1_0>='\u0000' && LA1_0<='\t')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:14: ~ ( '\\n' | '\\r' )
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

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:28: ( '\\r' )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0=='\r') ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:34: ( '\\n' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\n') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:43:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("//"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0>='\u0000' && LA4_0<='\t')||(LA4_0>='\u000B' && LA4_0<='\f')||(LA4_0>='\u000E' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:14: ~ ( '\\n' | '\\r' )
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

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:34: ( '\\n' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='\n') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:44:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 3 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:45:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:45:14: ( options {greedy=false; } : . )*
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
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:45:42: .
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:48:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:48:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:48:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:52:3: ( ID ( '.' ID )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:52:5: ID ( '.' ID )*
            {
            mID(); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:52:8: ( '.' ID )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0=='.') ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:52:9: '.' ID
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

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:55:5: ( ( '0' .. '9' )+ )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:55:7: ( '0' .. '9' )+
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:55:7: ( '0' .. '9' )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:55:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt18=3;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:9: ( '0' .. '9' )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);

                    match('.'); 
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:25: ( '0' .. '9' )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:37: ( EXPONENT )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0=='E'||LA14_0=='e') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:59:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:60:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:60:13: ( '0' .. '9' )+
                    int cnt15=0;
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( ((LA15_0>='0' && LA15_0<='9')) ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:60:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt15 >= 1 ) break loop15;
                                EarlyExitException eee =
                                    new EarlyExitException(15, input);
                                throw eee;
                        }
                        cnt15++;
                    } while (true);

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:60:25: ( EXPONENT )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='E'||LA16_0=='e') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:60:25: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:61:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:61:9: ( '0' .. '9' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:61:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt17 >= 1 ) break loop17;
                                EarlyExitException eee =
                                    new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:65:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:65:9: ( ' ' | '\\t' | '\\r' | '\\n' )
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

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:76:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:76:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:76:22: ( '+' | '-' )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='+'||LA19_0=='-') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:76:33: ( '0' .. '9' )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:76:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:79:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:79:13: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
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
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:83:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt21=3;
            int LA21_0 = input.LA(1);

            if ( (LA21_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt21=1;
                    }
                    break;
                case 'u':
                    {
                    alt21=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt21=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:83:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\\\' )
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
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:84:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 3 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:85:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt22=3;
            int LA22_0 = input.LA(1);

            if ( (LA22_0=='\\') ) {
                int LA22_1 = input.LA(2);

                if ( ((LA22_1>='0' && LA22_1<='3')) ) {
                    int LA22_2 = input.LA(3);

                    if ( ((LA22_2>='0' && LA22_2<='7')) ) {
                        int LA22_5 = input.LA(4);

                        if ( ((LA22_5>='0' && LA22_5<='7')) ) {
                            alt22=1;
                        }
                        else {
                            alt22=2;}
                    }
                    else {
                        alt22=3;}
                }
                else if ( ((LA22_1>='4' && LA22_1<='7')) ) {
                    int LA22_3 = input.LA(3);

                    if ( ((LA22_3>='0' && LA22_3<='7')) ) {
                        alt22=2;
                    }
                    else {
                        alt22=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:14: ( '0' .. '3' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:25: ( '0' .. '7' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:36: ( '0' .. '7' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:90:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:91:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:91:14: ( '0' .. '7' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:91:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:91:25: ( '0' .. '7' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:91:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:92:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:92:14: ( '0' .. '7' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:92:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:97:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 
            match('u'); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "SpecialChar"
    public final void mSpecialChar() throws RecognitionException {
        try {
            int _type = SpecialChar;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:172:5: ( '\"' | '\\\\' | '$' )
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:175:5: (~ SpecialChar )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:175:10: ~ SpecialChar
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='\u0016')||(input.LA(1)>='\u0018' && input.LA(1)<='\uFFFF') ) {
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
        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:8: ( ASSIGN | COLON | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | QUOTE | COMMENT | ID | JAVANAME | INT | FLOAT | WS | SpecialChar | NormalChar )
        int alt23=27;
        alt23 = dfa23.predict(input);
        switch (alt23) {
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
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:55: T__24
                {
                mT__24(); 

                }
                break;
            case 8 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:61: T__25
                {
                mT__25(); 

                }
                break;
            case 9 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:67: T__26
                {
                mT__26(); 

                }
                break;
            case 10 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:73: T__27
                {
                mT__27(); 

                }
                break;
            case 11 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:79: T__28
                {
                mT__28(); 

                }
                break;
            case 12 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:85: T__29
                {
                mT__29(); 

                }
                break;
            case 13 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:91: T__30
                {
                mT__30(); 

                }
                break;
            case 14 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:97: T__31
                {
                mT__31(); 

                }
                break;
            case 15 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:103: T__32
                {
                mT__32(); 

                }
                break;
            case 16 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:109: T__33
                {
                mT__33(); 

                }
                break;
            case 17 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:115: T__34
                {
                mT__34(); 

                }
                break;
            case 18 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:121: T__35
                {
                mT__35(); 

                }
                break;
            case 19 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:127: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 20 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:133: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 21 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:141: ID
                {
                mID(); 

                }
                break;
            case 22 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:144: JAVANAME
                {
                mJAVANAME(); 

                }
                break;
            case 23 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:153: INT
                {
                mINT(); 

                }
                break;
            case 24 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:157: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 25 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:163: WS
                {
                mWS(); 

                }
                break;
            case 26 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:166: SpecialChar
                {
                mSpecialChar(); 

                }
                break;
            case 27 :
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:1:178: NormalChar
                {
                mNormalChar(); 

                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    protected DFA23 dfa23 = new DFA23(this);
    static final String DFA18_eotS =
        "\5\uffff";
    static final String DFA18_eofS =
        "\5\uffff";
    static final String DFA18_minS =
        "\2\56\3\uffff";
    static final String DFA18_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA18_specialS =
        "\5\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "58:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA23_eotS =
        "\3\uffff\2\35\1\30\1\uffff\1\45\10\uffff\1\30\1\uffff\1\30\1\35"+
        "\1\60\1\30\5\uffff\2\35\1\uffff\1\35\1\uffff\1\35\20\uffff\1\60"+
        "\2\uffff\13\35\1\102\1\35\2\uffff\1\35\1\uffff";
    static final String DFA23_eofS =
        "\105\uffff";
    static final String DFA23_minS =
        "\1\0\2\uffff\2\56\1\55\1\uffff\1\174\10\uffff\1\46\1\uffff\1\52"+
        "\2\56\1\60\5\uffff\2\56\1\uffff\1\56\1\uffff\1\56\20\uffff\1\56"+
        "\2\uffff\12\56\1\40\2\56\2\uffff\1\40\1\uffff";
    static final String DFA23_maxS =
        "\1\uffff\2\uffff\2\172\1\76\1\uffff\1\174\10\uffff\1\46\1\uffff"+
        "\1\57\1\172\1\145\1\71\5\uffff\2\172\1\uffff\1\172\1\uffff\1\172"+
        "\20\uffff\1\145\2\uffff\15\172\2\uffff\1\172\1\uffff";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\1\2\3\uffff\1\7\1\uffff\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\uffff\1\23\4\uffff\1\31\1\32\1\33\1\1\1\2\2\uffff"+
        "\1\25\1\uffff\1\26\1\uffff\1\6\1\24\1\7\1\21\1\10\1\11\1\12\1\13"+
        "\1\14\1\15\1\16\1\17\1\20\1\22\1\23\1\27\1\uffff\1\30\1\31\15\uffff"+
        "\1\5\1\4\1\uffff\1\3";
    static final String DFA23_specialS =
        "\1\0\104\uffff}>";
    static final String[] DFA23_transitionS = {
            "\11\30\2\26\2\30\1\26\22\30\1\26\1\30\1\27\1\10\1\27\1\30\1"+
            "\20\1\21\2\30\1\16\1\17\1\30\1\5\1\25\1\22\12\24\1\2\1\6\1\30"+
            "\1\1\1\30\1\15\1\30\1\3\22\23\1\4\6\23\1\13\1\27\1\14\1\30\1"+
            "\23\1\30\32\23\1\11\1\7\1\12\uff82\30",
            "",
            "",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\1\36"+
            "\1\33\20\36\1\34\7\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\16"+
            "\36\1\40\13\36",
            "\1\42\20\uffff\1\41",
            "",
            "\1\44",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\56",
            "",
            "\1\42\4\uffff\1\42",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\32"+
            "\36",
            "\1\62\1\uffff\12\61\13\uffff\1\62\37\uffff\1\62",
            "\12\62",
            "",
            "",
            "",
            "",
            "",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\22"+
            "\36\1\64\7\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\17"+
            "\36\1\65\12\36",
            "",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\32"+
            "\36",
            "",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\12"+
            "\36\1\66\17\36",
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
            "\1\62\1\uffff\12\61\13\uffff\1\62\37\uffff\1\62",
            "",
            "",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\23"+
            "\36\1\67\6\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\4\36"+
            "\1\70\25\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\4\36"+
            "\1\71\25\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\21"+
            "\36\1\72\10\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\2\36"+
            "\1\73\27\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\15"+
            "\36\1\74\14\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\1\75"+
            "\31\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\23"+
            "\36\1\76\6\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\22"+
            "\36\1\77\7\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\2\36"+
            "\1\100\27\36",
            "\1\101\15\uffff\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1"+
            "\36\1\uffff\32\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\32"+
            "\36",
            "\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1\36\1\uffff\23"+
            "\36\1\103\6\36",
            "",
            "",
            "\1\104\15\uffff\1\37\1\uffff\12\36\7\uffff\32\36\4\uffff\1"+
            "\36\1\uffff\32\36",
            ""
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ASSIGN | COLON | AST | TOKENS | ASPECT_DCL | FIELD_DCL | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | QUOTE | COMMENT | ID | JAVANAME | INT | FLOAT | WS | SpecialChar | NormalChar );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA23_0 = input.LA(1);

                        s = -1;
                        if ( (LA23_0=='=') ) {s = 1;}

                        else if ( (LA23_0==':') ) {s = 2;}

                        else if ( (LA23_0=='A') ) {s = 3;}

                        else if ( (LA23_0=='T') ) {s = 4;}

                        else if ( (LA23_0=='-') ) {s = 5;}

                        else if ( (LA23_0==';') ) {s = 6;}

                        else if ( (LA23_0=='|') ) {s = 7;}

                        else if ( (LA23_0=='#') ) {s = 8;}

                        else if ( (LA23_0=='{') ) {s = 9;}

                        else if ( (LA23_0=='}') ) {s = 10;}

                        else if ( (LA23_0=='[') ) {s = 11;}

                        else if ( (LA23_0==']') ) {s = 12;}

                        else if ( (LA23_0=='?') ) {s = 13;}

                        else if ( (LA23_0=='*') ) {s = 14;}

                        else if ( (LA23_0=='+') ) {s = 15;}

                        else if ( (LA23_0=='&') ) {s = 16;}

                        else if ( (LA23_0=='\'') ) {s = 17;}

                        else if ( (LA23_0=='/') ) {s = 18;}

                        else if ( ((LA23_0>='B' && LA23_0<='S')||(LA23_0>='U' && LA23_0<='Z')||LA23_0=='_'||(LA23_0>='a' && LA23_0<='z')) ) {s = 19;}

                        else if ( ((LA23_0>='0' && LA23_0<='9')) ) {s = 20;}

                        else if ( (LA23_0=='.') ) {s = 21;}

                        else if ( ((LA23_0>='\t' && LA23_0<='\n')||LA23_0=='\r'||LA23_0==' ') ) {s = 22;}

                        else if ( (LA23_0=='\"'||LA23_0=='$'||LA23_0=='\\') ) {s = 23;}

                        else if ( ((LA23_0>='\u0000' && LA23_0<='\b')||(LA23_0>='\u000B' && LA23_0<='\f')||(LA23_0>='\u000E' && LA23_0<='\u001F')||LA23_0=='!'||LA23_0=='%'||(LA23_0>='(' && LA23_0<=')')||LA23_0==','||LA23_0=='<'||LA23_0=='>'||LA23_0=='@'||LA23_0=='^'||LA23_0=='`'||(LA23_0>='~' && LA23_0<='\uFFFF')) ) {s = 24;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 23, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}