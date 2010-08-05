// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/ari/Documents/antlr/vdmpp.g 2010-08-05 00:30:50

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class vdmppLexer extends Lexer {
    public static final int CLASS=5;
    public static final int RB=23;
    public static final int TO=10;
    public static final int RP=25;
    public static final int LP=24;
    public static final int VALUES=13;
    public static final int CHAR=29;
    public static final int SUBCLASS=4;
    public static final int TYPES=14;
    public static final int SET=12;
    public static final int EOF=-1;
    public static final int OF=7;
    public static final int COLON=19;
    public static final int QUOTE=32;
    public static final int DCOLON=17;
    public static final int WS=34;
    public static final int SEQ=11;
    public static final int IS=6;
    public static final int INSTANCE=15;
    public static final int MAP=9;
    public static final int ASSIGN=18;
    public static final int VARIABLES=16;
    public static final int IDENT=31;
    public static final int LB=22;
    public static final int LOWER=27;
    public static final int DIGIT=30;
    public static final int EQ=21;
    public static final int END=8;
    public static final int BAR=26;
    public static final int UPPER=28;
    public static final int STRING=33;
    public static final int SCOLON=20;

    // delegates
    // delegators

    public vdmppLexer() {;} 
    public vdmppLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public vdmppLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/ari/Documents/antlr/vdmpp.g"; }

    // $ANTLR start "SUBCLASS"
    public final void mSUBCLASS() throws RecognitionException {
        try {
            int _type = SUBCLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:3:10: ( 'subclass' )
            // /Users/ari/Documents/antlr/vdmpp.g:3:12: 'subclass'
            {
            match("subclass"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUBCLASS"

    // $ANTLR start "CLASS"
    public final void mCLASS() throws RecognitionException {
        try {
            int _type = CLASS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:4:7: ( 'class' )
            // /Users/ari/Documents/antlr/vdmpp.g:4:9: 'class'
            {
            match("class"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CLASS"

    // $ANTLR start "IS"
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:5:4: ( 'is' )
            // /Users/ari/Documents/antlr/vdmpp.g:5:6: 'is'
            {
            match("is"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "OF"
    public final void mOF() throws RecognitionException {
        try {
            int _type = OF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:6:4: ( 'of' )
            // /Users/ari/Documents/antlr/vdmpp.g:6:6: 'of'
            {
            match("of"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OF"

    // $ANTLR start "END"
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:7:5: ( 'end' )
            // /Users/ari/Documents/antlr/vdmpp.g:7:7: 'end'
            {
            match("end"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "MAP"
    public final void mMAP() throws RecognitionException {
        try {
            int _type = MAP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:8:5: ( 'map' )
            // /Users/ari/Documents/antlr/vdmpp.g:8:7: 'map'
            {
            match("map"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAP"

    // $ANTLR start "TO"
    public final void mTO() throws RecognitionException {
        try {
            int _type = TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:9:4: ( 'to' )
            // /Users/ari/Documents/antlr/vdmpp.g:9:6: 'to'
            {
            match("to"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "SEQ"
    public final void mSEQ() throws RecognitionException {
        try {
            int _type = SEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:10:5: ( 'seq' )
            // /Users/ari/Documents/antlr/vdmpp.g:10:7: 'seq'
            {
            match("seq"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEQ"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:11:5: ( 'set' )
            // /Users/ari/Documents/antlr/vdmpp.g:11:7: 'set'
            {
            match("set"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "VALUES"
    public final void mVALUES() throws RecognitionException {
        try {
            int _type = VALUES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:12:8: ( 'values' )
            // /Users/ari/Documents/antlr/vdmpp.g:12:10: 'values'
            {
            match("values"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VALUES"

    // $ANTLR start "TYPES"
    public final void mTYPES() throws RecognitionException {
        try {
            int _type = TYPES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:13:7: ( 'types' )
            // /Users/ari/Documents/antlr/vdmpp.g:13:9: 'types'
            {
            match("types"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TYPES"

    // $ANTLR start "INSTANCE"
    public final void mINSTANCE() throws RecognitionException {
        try {
            int _type = INSTANCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:14:10: ( 'instance' )
            // /Users/ari/Documents/antlr/vdmpp.g:14:12: 'instance'
            {
            match("instance"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INSTANCE"

    // $ANTLR start "VARIABLES"
    public final void mVARIABLES() throws RecognitionException {
        try {
            int _type = VARIABLES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:15:11: ( 'variables' )
            // /Users/ari/Documents/antlr/vdmpp.g:15:13: 'variables'
            {
            match("variables"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VARIABLES"

    // $ANTLR start "DCOLON"
    public final void mDCOLON() throws RecognitionException {
        try {
            int _type = DCOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:16:8: ( '::' )
            // /Users/ari/Documents/antlr/vdmpp.g:16:10: '::'
            {
            match("::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DCOLON"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:17:8: ( ':=' )
            // /Users/ari/Documents/antlr/vdmpp.g:17:10: ':='
            {
            match(":="); 


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
            // /Users/ari/Documents/antlr/vdmpp.g:18:7: ( ':' )
            // /Users/ari/Documents/antlr/vdmpp.g:18:9: ':'
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

    // $ANTLR start "SCOLON"
    public final void mSCOLON() throws RecognitionException {
        try {
            int _type = SCOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:19:8: ( ';' )
            // /Users/ari/Documents/antlr/vdmpp.g:19:10: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SCOLON"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:20:4: ( '=' )
            // /Users/ari/Documents/antlr/vdmpp.g:20:6: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "LB"
    public final void mLB() throws RecognitionException {
        try {
            int _type = LB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:21:4: ( '[' )
            // /Users/ari/Documents/antlr/vdmpp.g:21:6: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LB"

    // $ANTLR start "RB"
    public final void mRB() throws RecognitionException {
        try {
            int _type = RB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:22:4: ( ']' )
            // /Users/ari/Documents/antlr/vdmpp.g:22:6: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RB"

    // $ANTLR start "LP"
    public final void mLP() throws RecognitionException {
        try {
            int _type = LP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:23:4: ( '(' )
            // /Users/ari/Documents/antlr/vdmpp.g:23:6: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LP"

    // $ANTLR start "RP"
    public final void mRP() throws RecognitionException {
        try {
            int _type = RP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:24:4: ( ')' )
            // /Users/ari/Documents/antlr/vdmpp.g:24:6: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RP"

    // $ANTLR start "BAR"
    public final void mBAR() throws RecognitionException {
        try {
            int _type = BAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:25:5: ( '|' )
            // /Users/ari/Documents/antlr/vdmpp.g:25:7: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "LOWER"
    public final void mLOWER() throws RecognitionException {
        try {
            // /Users/ari/Documents/antlr/vdmpp.g:33:7: ( 'a' .. 'z' )
            // /Users/ari/Documents/antlr/vdmpp.g:33:9: 'a' .. 'z'
            {
            matchRange('a','z'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "LOWER"

    // $ANTLR start "UPPER"
    public final void mUPPER() throws RecognitionException {
        try {
            // /Users/ari/Documents/antlr/vdmpp.g:35:7: ( 'A' .. 'Z' )
            // /Users/ari/Documents/antlr/vdmpp.g:35:9: 'A' .. 'Z'
            {
            matchRange('A','Z'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UPPER"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            // /Users/ari/Documents/antlr/vdmpp.g:37:6: ( ( LOWER | UPPER | '_' ) )
            // /Users/ari/Documents/antlr/vdmpp.g:37:8: ( LOWER | UPPER | '_' )
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
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
    // $ANTLR end "CHAR"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            int _type = DIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:38:8: ( '0' .. '9' )
            // /Users/ari/Documents/antlr/vdmpp.g:38:10: '0' .. '9'
            {
            matchRange('0','9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "IDENT"
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:39:7: ( CHAR ( CHAR | DIGIT )* )
            // /Users/ari/Documents/antlr/vdmpp.g:39:9: CHAR ( CHAR | DIGIT )*
            {
            mCHAR(); 
            // /Users/ari/Documents/antlr/vdmpp.g:39:14: ( CHAR | DIGIT )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/ari/Documents/antlr/vdmpp.g:
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
    // $ANTLR end "IDENT"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            int _type = QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:40:7: ( '<' ( DIGIT | UPPER | '_' )+ '>' )
            // /Users/ari/Documents/antlr/vdmpp.g:40:9: '<' ( DIGIT | UPPER | '_' )+ '>'
            {
            match('<'); 
            // /Users/ari/Documents/antlr/vdmpp.g:40:12: ( DIGIT | UPPER | '_' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')||(LA2_0>='A' && LA2_0<='Z')||LA2_0=='_') ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/ari/Documents/antlr/vdmpp.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUOTE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:41:9: ( '\"' (~ ( '\\\\' | '\"' ) )* '\"' )
            // /Users/ari/Documents/antlr/vdmpp.g:41:11: '\"' (~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // /Users/ari/Documents/antlr/vdmpp.g:41:15: (~ ( '\\\\' | '\"' ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>='\u0000' && LA3_0<='!')||(LA3_0>='#' && LA3_0<='[')||(LA3_0>=']' && LA3_0<='\uFFFF')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/ari/Documents/antlr/vdmpp.g:41:16: ~ ( '\\\\' | '\"' )
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
            	    break loop3;
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
    // $ANTLR end "STRING"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/ari/Documents/antlr/vdmpp.g:42:4: ( ( '\\n' | '\\r' | '\\t' | ' ' )+ )
            // /Users/ari/Documents/antlr/vdmpp.g:42:6: ( '\\n' | '\\r' | '\\t' | ' ' )+
            {
            // /Users/ari/Documents/antlr/vdmpp.g:42:6: ( '\\n' | '\\r' | '\\t' | ' ' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/ari/Documents/antlr/vdmpp.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /Users/ari/Documents/antlr/vdmpp.g:1:8: ( SUBCLASS | CLASS | IS | OF | END | MAP | TO | SEQ | SET | VALUES | TYPES | INSTANCE | VARIABLES | DCOLON | ASSIGN | COLON | SCOLON | EQ | LB | RB | LP | RP | BAR | DIGIT | IDENT | QUOTE | STRING | WS )
        int alt5=28;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:10: SUBCLASS
                {
                mSUBCLASS(); 

                }
                break;
            case 2 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:19: CLASS
                {
                mCLASS(); 

                }
                break;
            case 3 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:25: IS
                {
                mIS(); 

                }
                break;
            case 4 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:28: OF
                {
                mOF(); 

                }
                break;
            case 5 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:31: END
                {
                mEND(); 

                }
                break;
            case 6 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:35: MAP
                {
                mMAP(); 

                }
                break;
            case 7 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:39: TO
                {
                mTO(); 

                }
                break;
            case 8 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:42: SEQ
                {
                mSEQ(); 

                }
                break;
            case 9 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:46: SET
                {
                mSET(); 

                }
                break;
            case 10 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:50: VALUES
                {
                mVALUES(); 

                }
                break;
            case 11 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:57: TYPES
                {
                mTYPES(); 

                }
                break;
            case 12 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:63: INSTANCE
                {
                mINSTANCE(); 

                }
                break;
            case 13 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:72: VARIABLES
                {
                mVARIABLES(); 

                }
                break;
            case 14 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:82: DCOLON
                {
                mDCOLON(); 

                }
                break;
            case 15 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:89: ASSIGN
                {
                mASSIGN(); 

                }
                break;
            case 16 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:96: COLON
                {
                mCOLON(); 

                }
                break;
            case 17 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:102: SCOLON
                {
                mSCOLON(); 

                }
                break;
            case 18 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:109: EQ
                {
                mEQ(); 

                }
                break;
            case 19 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:112: LB
                {
                mLB(); 

                }
                break;
            case 20 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:115: RB
                {
                mRB(); 

                }
                break;
            case 21 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:118: LP
                {
                mLP(); 

                }
                break;
            case 22 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:121: RP
                {
                mRP(); 

                }
                break;
            case 23 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:124: BAR
                {
                mBAR(); 

                }
                break;
            case 24 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:128: DIGIT
                {
                mDIGIT(); 

                }
                break;
            case 25 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:134: IDENT
                {
                mIDENT(); 

                }
                break;
            case 26 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:140: QUOTE
                {
                mQUOTE(); 

                }
                break;
            case 27 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:146: STRING
                {
                mSTRING(); 

                }
                break;
            case 28 :
                // /Users/ari/Documents/antlr/vdmpp.g:1:153: WS
                {
                mWS(); 

                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\1\uffff\10\22\1\43\14\uffff\3\22\1\50\1\22\1\52\2\22\1\55\2\22"+
        "\3\uffff\1\22\1\62\1\63\1\22\1\uffff\1\22\1\uffff\1\66\1\67\1\uffff"+
        "\4\22\2\uffff\2\22\2\uffff\4\22\1\102\1\22\1\104\3\22\1\uffff\1"+
        "\22\1\uffff\1\111\3\22\1\uffff\1\22\1\116\1\117\1\22\2\uffff\1\121"+
        "\1\uffff";
    static final String DFA5_eofS =
        "\122\uffff";
    static final String DFA5_minS =
        "\1\11\1\145\1\154\1\156\1\146\1\156\1\141\1\157\1\141\1\72\14\uffff"+
        "\1\142\1\161\1\141\1\60\1\163\1\60\1\144\1\160\1\60\1\160\1\154"+
        "\3\uffff\1\143\2\60\1\163\1\uffff\1\164\1\uffff\2\60\1\uffff\1\145"+
        "\1\165\1\151\1\154\2\uffff\1\163\1\141\2\uffff\1\163\1\145\2\141"+
        "\1\60\1\156\1\60\1\163\1\142\1\163\1\uffff\1\143\1\uffff\1\60\1"+
        "\154\1\163\1\145\1\uffff\1\145\2\60\1\163\2\uffff\1\60\1\uffff";
    static final String DFA5_maxS =
        "\1\174\1\165\1\154\1\163\1\146\1\156\1\141\1\171\1\141\1\75\14\uffff"+
        "\1\142\1\164\1\141\1\172\1\163\1\172\1\144\1\160\1\172\1\160\1\162"+
        "\3\uffff\1\143\2\172\1\163\1\uffff\1\164\1\uffff\2\172\1\uffff\1"+
        "\145\1\165\1\151\1\154\2\uffff\1\163\1\141\2\uffff\1\163\1\145\2"+
        "\141\1\172\1\156\1\172\1\163\1\142\1\163\1\uffff\1\143\1\uffff\1"+
        "\172\1\154\1\163\1\145\1\uffff\1\145\2\172\1\163\2\uffff\1\172\1"+
        "\uffff";
    static final String DFA5_acceptS =
        "\12\uffff\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
        "\1\34\13\uffff\1\16\1\17\1\20\4\uffff\1\3\1\uffff\1\4\2\uffff\1"+
        "\7\4\uffff\1\10\1\11\2\uffff\1\5\1\6\12\uffff\1\2\1\uffff\1\13\4"+
        "\uffff\1\12\4\uffff\1\1\1\14\1\uffff\1\15";
    static final String DFA5_specialS =
        "\122\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\25\2\uffff\1\25\22\uffff\1\25\1\uffff\1\24\5\uffff\1\16\1"+
            "\17\6\uffff\12\21\1\11\1\12\1\23\1\13\3\uffff\32\22\1\14\1\uffff"+
            "\1\15\1\uffff\1\22\1\uffff\2\22\1\2\1\22\1\5\3\22\1\3\3\22\1"+
            "\6\1\22\1\4\3\22\1\1\1\7\1\22\1\10\4\22\1\uffff\1\20",
            "\1\27\17\uffff\1\26",
            "\1\30",
            "\1\32\4\uffff\1\31",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\36\11\uffff\1\37",
            "\1\40",
            "\1\41\2\uffff\1\42",
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
            "\1\44",
            "\1\45\2\uffff\1\46",
            "\1\47",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\51",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\53",
            "\1\54",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\56",
            "\1\57\5\uffff\1\60",
            "",
            "",
            "",
            "\1\61",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\64",
            "",
            "\1\65",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "",
            "",
            "\1\74",
            "\1\75",
            "",
            "",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\103",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\105",
            "\1\106",
            "\1\107",
            "",
            "\1\110",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\112",
            "\1\113",
            "\1\114",
            "",
            "\1\115",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\120",
            "",
            "",
            "\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( SUBCLASS | CLASS | IS | OF | END | MAP | TO | SEQ | SET | VALUES | TYPES | INSTANCE | VARIABLES | DCOLON | ASSIGN | COLON | SCOLON | EQ | LB | RB | LP | RP | BAR | DIGIT | IDENT | QUOTE | STRING | WS );";
        }
    }
 

}