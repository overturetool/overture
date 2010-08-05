// $ANTLR 3.2 Sep 23, 2009 12:02:23 /Users/ari/Documents/antlr/vdmpp.g 2010-08-05 00:30:50

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.antlr.runtime.debug.*;
import java.io.IOException;
public class vdmppParser extends DebugParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SUBCLASS", "CLASS", "IS", "OF", "END", "MAP", "TO", "SEQ", "SET", "VALUES", "TYPES", "INSTANCE", "VARIABLES", "DCOLON", "ASSIGN", "COLON", "SCOLON", "EQ", "LB", "RB", "LP", "RP", "BAR", "LOWER", "UPPER", "CHAR", "DIGIT", "IDENT", "QUOTE", "STRING", "WS"
    };
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
    public static final int OF=7;
    public static final int EOF=-1;
    public static final int COLON=19;
    public static final int QUOTE=32;
    public static final int DCOLON=17;
    public static final int WS=34;
    public static final int SEQ=11;
    public static final int IS=6;
    public static final int INSTANCE=15;
    public static final int MAP=9;
    public static final int VARIABLES=16;
    public static final int ASSIGN=18;
    public static final int IDENT=31;
    public static final int LB=22;
    public static final int DIGIT=30;
    public static final int LOWER=27;
    public static final int EQ=21;
    public static final int END=8;
    public static final int BAR=26;
    public static final int UPPER=28;
    public static final int STRING=33;
    public static final int SCOLON=20;

    // delegates
    // delegators

    public static final String[] ruleNames = new String[] {
        "invalidRule", "start", "classDefinitionList", "synpred22_vdmpp", 
        "synpred33_vdmpp", "definitionBlock", "synpred10_vdmpp", "type", 
        "synpred19_vdmpp", "synpred11_vdmpp", "fieldList", "document", "synpred25_vdmpp", 
        "synpred24_vdmpp", "synpred1_vdmpp", "synpred34_vdmpp", "synpred12_vdmpp", 
        "synpred6_vdmpp", "synpred27_vdmpp", "classDefinition", "instanceVariable", 
        "synpred18_vdmpp", "synpred30_vdmpp", "typeDefinitions", "synpred20_vdmpp", 
        "valueDefinition", "valueDefinitions", "valueDefinitionList", "instanceVariableDefinitions", 
        "instanceVariableDefinitionList", "synpred29_vdmpp", "synpred7_vdmpp", 
        "synpred16_vdmpp", "synpred17_vdmpp", "synpred14_vdmpp", "synpred32_vdmpp", 
        "synpred13_vdmpp", "synpred15_vdmpp", "synpred2_vdmpp", "synpred4_vdmpp", 
        "synpred26_vdmpp", "synpred5_vdmpp", "synpred23_vdmpp", "definitionList", 
        "synpred28_vdmpp", "synpred31_vdmpp", "synpred9_vdmpp", "synpred21_vdmpp", 
        "synpred3_vdmpp", "typeDefinition", "synpred8_vdmpp", "typeDefinitionList"
    };
     
        public int ruleLevel = 0;
        public int getRuleLevel() { return ruleLevel; }
        public void incRuleLevel() { ruleLevel++; }
        public void decRuleLevel() { ruleLevel--; }
        public vdmppParser(TokenStream input) {
            this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT, new RecognizerSharedState());
        }
        public vdmppParser(TokenStream input, int port, RecognizerSharedState state) {
            super(input, state);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this, port, null);
            setDebugListener(proxy);
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }
        }
    public vdmppParser(TokenStream input, DebugEventListener dbg) {
        super(input, dbg, new RecognizerSharedState());

    }
    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }


    public String[] getTokenNames() { return vdmppParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/ari/Documents/antlr/vdmpp.g"; }



    // $ANTLR start "start"
    // /Users/ari/Documents/antlr/vdmpp.g:44:1: start : document EOF ;
    public final void start() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "start");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(44, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:44:7: ( document EOF )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:44:9: document EOF
            {
            dbg.location(44,9);
            pushFollow(FOLLOW_document_in_start287);
            document();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(44,18);
            match(input,EOF,FOLLOW_EOF_in_start289); if (state.failed) return ;
            dbg.location(44,22);
            if ( state.backtracking==0 ) {
              System.out.println("Document parsed");
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(45, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "start");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "start"


    // $ANTLR start "document"
    // /Users/ari/Documents/antlr/vdmpp.g:47:1: document : ( classDefinitionList | );
    public final void document() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "document");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(47, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:47:9: ( classDefinitionList | )
            int alt1=2;
            try { dbg.enterDecision(1);

            int LA1_0 = input.LA(1);

            if ( (LA1_0==CLASS) ) {
                alt1=1;
            }
            else if ( (LA1_0==EOF) ) {
                alt1=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(1);}

            switch (alt1) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:47:11: classDefinitionList
                    {
                    dbg.location(47,11);
                    pushFollow(FOLLOW_classDefinitionList_in_document300);
                    classDefinitionList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:48:4: 
                    {
                    dbg.location(48,4);
                    if ( state.backtracking==0 ) {
                      System.out.println("empty document");
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(49, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "document");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "document"


    // $ANTLR start "classDefinitionList"
    // /Users/ari/Documents/antlr/vdmpp.g:51:1: classDefinitionList : classDefinition ( classDefinition )* ;
    public final void classDefinitionList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "classDefinitionList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(51, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:52:2: ( classDefinition ( classDefinition )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:52:4: classDefinition ( classDefinition )*
            {
            dbg.location(52,4);
            pushFollow(FOLLOW_classDefinition_in_classDefinitionList317);
            classDefinition();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(52,20);
            // /Users/ari/Documents/antlr/vdmpp.g:52:20: ( classDefinition )*
            try { dbg.enterSubRule(2);

            loop2:
            do {
                int alt2=2;
                try { dbg.enterDecision(2);

                int LA2_0 = input.LA(1);

                if ( (LA2_0==CLASS) ) {
                    alt2=1;
                }


                } finally {dbg.exitDecision(2);}

                switch (alt2) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:0:0: classDefinition
            	    {
            	    dbg.location(52,20);
            	    pushFollow(FOLLOW_classDefinition_in_classDefinitionList319);
            	    classDefinition();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);
            } finally {dbg.exitSubRule(2);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(53, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "classDefinitionList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "classDefinitionList"


    // $ANTLR start "classDefinition"
    // /Users/ari/Documents/antlr/vdmpp.g:55:1: classDefinition : ( CLASS nameStart= IDENT END nameEnd= IDENT | CLASS IDENT definitionList END IDENT | CLASS IDENT IS SUBCLASS OF IDENT END IDENT | CLASS IDENT IS SUBCLASS OF IDENT definitionList END IDENT );
    public final void classDefinition() throws RecognitionException {
        Token nameStart=null;
        Token nameEnd=null;

        try { dbg.enterRule(getGrammarFileName(), "classDefinition");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(55, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:56:2: ( CLASS nameStart= IDENT END nameEnd= IDENT | CLASS IDENT definitionList END IDENT | CLASS IDENT IS SUBCLASS OF IDENT END IDENT | CLASS IDENT IS SUBCLASS OF IDENT definitionList END IDENT )
            int alt3=4;
            try { dbg.enterDecision(3);

            try {
                isCyclicDecision = true;
                alt3 = dfa3.predict(input);
            }
            catch (NoViableAltException nvae) {
                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(3);}

            switch (alt3) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:56:4: CLASS nameStart= IDENT END nameEnd= IDENT
                    {
                    dbg.location(56,4);
                    match(input,CLASS,FOLLOW_CLASS_in_classDefinition332); if (state.failed) return ;
                    dbg.location(56,19);
                    nameStart=(Token)match(input,IDENT,FOLLOW_IDENT_in_classDefinition336); if (state.failed) return ;
                    dbg.location(56,26);
                    match(input,END,FOLLOW_END_in_classDefinition338); if (state.failed) return ;
                    dbg.location(56,37);
                    nameEnd=(Token)match(input,IDENT,FOLLOW_IDENT_in_classDefinition342); if (state.failed) return ;
                    dbg.location(57,2);
                    if ( state.backtracking==0 ) {
                       System.out.println(nameStart.getText());
                    }

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:58:4: CLASS IDENT definitionList END IDENT
                    {
                    dbg.location(58,4);
                    match(input,CLASS,FOLLOW_CLASS_in_classDefinition351); if (state.failed) return ;
                    dbg.location(58,10);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition353); if (state.failed) return ;
                    dbg.location(58,16);
                    pushFollow(FOLLOW_definitionList_in_classDefinition355);
                    definitionList();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(58,31);
                    match(input,END,FOLLOW_END_in_classDefinition357); if (state.failed) return ;
                    dbg.location(58,35);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition359); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Users/ari/Documents/antlr/vdmpp.g:59:4: CLASS IDENT IS SUBCLASS OF IDENT END IDENT
                    {
                    dbg.location(59,4);
                    match(input,CLASS,FOLLOW_CLASS_in_classDefinition364); if (state.failed) return ;
                    dbg.location(59,10);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition366); if (state.failed) return ;
                    dbg.location(59,16);
                    match(input,IS,FOLLOW_IS_in_classDefinition368); if (state.failed) return ;
                    dbg.location(59,19);
                    match(input,SUBCLASS,FOLLOW_SUBCLASS_in_classDefinition370); if (state.failed) return ;
                    dbg.location(59,28);
                    match(input,OF,FOLLOW_OF_in_classDefinition372); if (state.failed) return ;
                    dbg.location(59,31);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition374); if (state.failed) return ;
                    dbg.location(59,37);
                    match(input,END,FOLLOW_END_in_classDefinition376); if (state.failed) return ;
                    dbg.location(59,41);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition378); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Users/ari/Documents/antlr/vdmpp.g:60:4: CLASS IDENT IS SUBCLASS OF IDENT definitionList END IDENT
                    {
                    dbg.location(60,4);
                    match(input,CLASS,FOLLOW_CLASS_in_classDefinition383); if (state.failed) return ;
                    dbg.location(60,10);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition385); if (state.failed) return ;
                    dbg.location(60,16);
                    match(input,IS,FOLLOW_IS_in_classDefinition387); if (state.failed) return ;
                    dbg.location(60,19);
                    match(input,SUBCLASS,FOLLOW_SUBCLASS_in_classDefinition389); if (state.failed) return ;
                    dbg.location(60,28);
                    match(input,OF,FOLLOW_OF_in_classDefinition391); if (state.failed) return ;
                    dbg.location(60,31);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition393); if (state.failed) return ;
                    dbg.location(60,37);
                    pushFollow(FOLLOW_definitionList_in_classDefinition395);
                    definitionList();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(60,52);
                    match(input,END,FOLLOW_END_in_classDefinition397); if (state.failed) return ;
                    dbg.location(60,56);
                    match(input,IDENT,FOLLOW_IDENT_in_classDefinition399); if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(61, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "classDefinition");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "classDefinition"


    // $ANTLR start "definitionList"
    // /Users/ari/Documents/antlr/vdmpp.g:63:1: definitionList : definitionBlock ( definitionBlock )* ;
    public final void definitionList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "definitionList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(63, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:64:2: ( definitionBlock ( definitionBlock )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:64:4: definitionBlock ( definitionBlock )*
            {
            dbg.location(64,4);
            pushFollow(FOLLOW_definitionBlock_in_definitionList411);
            definitionBlock();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(64,20);
            // /Users/ari/Documents/antlr/vdmpp.g:64:20: ( definitionBlock )*
            try { dbg.enterSubRule(4);

            loop4:
            do {
                int alt4=2;
                try { dbg.enterDecision(4);

                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=VALUES && LA4_0<=INSTANCE)) ) {
                    alt4=1;
                }


                } finally {dbg.exitDecision(4);}

                switch (alt4) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:0:0: definitionBlock
            	    {
            	    dbg.location(64,20);
            	    pushFollow(FOLLOW_definitionBlock_in_definitionList413);
            	    definitionBlock();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);
            } finally {dbg.exitSubRule(4);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(65, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "definitionList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "definitionList"


    // $ANTLR start "definitionBlock"
    // /Users/ari/Documents/antlr/vdmpp.g:67:1: definitionBlock : ( valueDefinitions | instanceVariableDefinitions | typeDefinitions );
    public final void definitionBlock() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "definitionBlock");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(67, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:68:2: ( valueDefinitions | instanceVariableDefinitions | typeDefinitions )
            int alt5=3;
            try { dbg.enterDecision(5);

            switch ( input.LA(1) ) {
            case VALUES:
                {
                alt5=1;
                }
                break;
            case INSTANCE:
                {
                alt5=2;
                }
                break;
            case TYPES:
                {
                alt5=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(5);}

            switch (alt5) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:68:4: valueDefinitions
                    {
                    dbg.location(68,4);
                    pushFollow(FOLLOW_valueDefinitions_in_definitionBlock426);
                    valueDefinitions();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:69:4: instanceVariableDefinitions
                    {
                    dbg.location(69,4);
                    pushFollow(FOLLOW_instanceVariableDefinitions_in_definitionBlock431);
                    instanceVariableDefinitions();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Users/ari/Documents/antlr/vdmpp.g:70:4: typeDefinitions
                    {
                    dbg.location(70,4);
                    pushFollow(FOLLOW_typeDefinitions_in_definitionBlock436);
                    typeDefinitions();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(71, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "definitionBlock");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "definitionBlock"


    // $ANTLR start "valueDefinitions"
    // /Users/ari/Documents/antlr/vdmpp.g:73:1: valueDefinitions : ( VALUES | VALUES valueDefinitionList ( SCOLON )? );
    public final void valueDefinitions() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "valueDefinitions");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(73, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:74:2: ( VALUES | VALUES valueDefinitionList ( SCOLON )? )
            int alt7=2;
            try { dbg.enterDecision(7);

            int LA7_0 = input.LA(1);

            if ( (LA7_0==VALUES) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==IDENT) ) {
                    alt7=2;
                }
                else if ( (LA7_1==EOF||LA7_1==END||(LA7_1>=VALUES && LA7_1<=INSTANCE)) ) {
                    alt7=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(7);}

            switch (alt7) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:74:4: VALUES
                    {
                    dbg.location(74,4);
                    match(input,VALUES,FOLLOW_VALUES_in_valueDefinitions448); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:75:4: VALUES valueDefinitionList ( SCOLON )?
                    {
                    dbg.location(75,4);
                    match(input,VALUES,FOLLOW_VALUES_in_valueDefinitions453); if (state.failed) return ;
                    dbg.location(75,11);
                    pushFollow(FOLLOW_valueDefinitionList_in_valueDefinitions455);
                    valueDefinitionList();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(75,31);
                    // /Users/ari/Documents/antlr/vdmpp.g:75:31: ( SCOLON )?
                    int alt6=2;
                    try { dbg.enterSubRule(6);
                    try { dbg.enterDecision(6);

                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==SCOLON) ) {
                        alt6=1;
                    }
                    } finally {dbg.exitDecision(6);}

                    switch (alt6) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /Users/ari/Documents/antlr/vdmpp.g:0:0: SCOLON
                            {
                            dbg.location(75,31);
                            match(input,SCOLON,FOLLOW_SCOLON_in_valueDefinitions457); if (state.failed) return ;

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(6);}


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(76, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "valueDefinitions");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "valueDefinitions"


    // $ANTLR start "valueDefinitionList"
    // /Users/ari/Documents/antlr/vdmpp.g:78:1: valueDefinitionList : valueDefinition ( SCOLON valueDefinition )* ;
    public final void valueDefinitionList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "valueDefinitionList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(78, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:79:2: ( valueDefinition ( SCOLON valueDefinition )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:79:4: valueDefinition ( SCOLON valueDefinition )*
            {
            dbg.location(79,4);
            pushFollow(FOLLOW_valueDefinition_in_valueDefinitionList469);
            valueDefinition();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(79,20);
            // /Users/ari/Documents/antlr/vdmpp.g:79:20: ( SCOLON valueDefinition )*
            try { dbg.enterSubRule(8);

            loop8:
            do {
                int alt8=2;
                try { dbg.enterDecision(8);

                int LA8_0 = input.LA(1);

                if ( (LA8_0==SCOLON) ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1==IDENT) ) {
                        alt8=1;
                    }


                }


                } finally {dbg.exitDecision(8);}

                switch (alt8) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:79:21: SCOLON valueDefinition
            	    {
            	    dbg.location(79,21);
            	    match(input,SCOLON,FOLLOW_SCOLON_in_valueDefinitionList472); if (state.failed) return ;
            	    dbg.location(79,28);
            	    pushFollow(FOLLOW_valueDefinition_in_valueDefinitionList474);
            	    valueDefinition();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);
            } finally {dbg.exitSubRule(8);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(80, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "valueDefinitionList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "valueDefinitionList"


    // $ANTLR start "valueDefinition"
    // /Users/ari/Documents/antlr/vdmpp.g:82:1: valueDefinition : IDENT EQ STRING ;
    public final void valueDefinition() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "valueDefinition");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(82, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:83:2: ( IDENT EQ STRING )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:83:4: IDENT EQ STRING
            {
            dbg.location(83,4);
            match(input,IDENT,FOLLOW_IDENT_in_valueDefinition488); if (state.failed) return ;
            dbg.location(83,10);
            match(input,EQ,FOLLOW_EQ_in_valueDefinition490); if (state.failed) return ;
            dbg.location(83,13);
            match(input,STRING,FOLLOW_STRING_in_valueDefinition492); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(84, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "valueDefinition");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "valueDefinition"


    // $ANTLR start "instanceVariableDefinitions"
    // /Users/ari/Documents/antlr/vdmpp.g:86:1: instanceVariableDefinitions : ( INSTANCE VARIABLES | INSTANCE VARIABLES instanceVariableDefinitionList ( SCOLON )? );
    public final void instanceVariableDefinitions() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "instanceVariableDefinitions");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(86, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:87:2: ( INSTANCE VARIABLES | INSTANCE VARIABLES instanceVariableDefinitionList ( SCOLON )? )
            int alt10=2;
            try { dbg.enterDecision(10);

            int LA10_0 = input.LA(1);

            if ( (LA10_0==INSTANCE) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==VARIABLES) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==IDENT) ) {
                        alt10=2;
                    }
                    else if ( (LA10_2==EOF||LA10_2==END||(LA10_2>=VALUES && LA10_2<=INSTANCE)) ) {
                        alt10=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(10);}

            switch (alt10) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:87:4: INSTANCE VARIABLES
                    {
                    dbg.location(87,4);
                    match(input,INSTANCE,FOLLOW_INSTANCE_in_instanceVariableDefinitions504); if (state.failed) return ;
                    dbg.location(87,13);
                    match(input,VARIABLES,FOLLOW_VARIABLES_in_instanceVariableDefinitions506); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:88:4: INSTANCE VARIABLES instanceVariableDefinitionList ( SCOLON )?
                    {
                    dbg.location(88,4);
                    match(input,INSTANCE,FOLLOW_INSTANCE_in_instanceVariableDefinitions511); if (state.failed) return ;
                    dbg.location(88,13);
                    match(input,VARIABLES,FOLLOW_VARIABLES_in_instanceVariableDefinitions513); if (state.failed) return ;
                    dbg.location(88,23);
                    pushFollow(FOLLOW_instanceVariableDefinitionList_in_instanceVariableDefinitions515);
                    instanceVariableDefinitionList();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(88,54);
                    // /Users/ari/Documents/antlr/vdmpp.g:88:54: ( SCOLON )?
                    int alt9=2;
                    try { dbg.enterSubRule(9);
                    try { dbg.enterDecision(9);

                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==SCOLON) ) {
                        alt9=1;
                    }
                    } finally {dbg.exitDecision(9);}

                    switch (alt9) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /Users/ari/Documents/antlr/vdmpp.g:0:0: SCOLON
                            {
                            dbg.location(88,54);
                            match(input,SCOLON,FOLLOW_SCOLON_in_instanceVariableDefinitions517); if (state.failed) return ;

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(9);}


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(89, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "instanceVariableDefinitions");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "instanceVariableDefinitions"


    // $ANTLR start "instanceVariableDefinitionList"
    // /Users/ari/Documents/antlr/vdmpp.g:91:1: instanceVariableDefinitionList : instanceVariable ( SCOLON instanceVariable )* ;
    public final void instanceVariableDefinitionList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "instanceVariableDefinitionList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(91, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:92:2: ( instanceVariable ( SCOLON instanceVariable )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:92:4: instanceVariable ( SCOLON instanceVariable )*
            {
            dbg.location(92,4);
            pushFollow(FOLLOW_instanceVariable_in_instanceVariableDefinitionList530);
            instanceVariable();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(92,21);
            // /Users/ari/Documents/antlr/vdmpp.g:92:21: ( SCOLON instanceVariable )*
            try { dbg.enterSubRule(11);

            loop11:
            do {
                int alt11=2;
                try { dbg.enterDecision(11);

                int LA11_0 = input.LA(1);

                if ( (LA11_0==SCOLON) ) {
                    int LA11_1 = input.LA(2);

                    if ( (LA11_1==IDENT) ) {
                        alt11=1;
                    }


                }


                } finally {dbg.exitDecision(11);}

                switch (alt11) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:92:22: SCOLON instanceVariable
            	    {
            	    dbg.location(92,22);
            	    match(input,SCOLON,FOLLOW_SCOLON_in_instanceVariableDefinitionList533); if (state.failed) return ;
            	    dbg.location(92,29);
            	    pushFollow(FOLLOW_instanceVariable_in_instanceVariableDefinitionList535);
            	    instanceVariable();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);
            } finally {dbg.exitSubRule(11);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(93, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "instanceVariableDefinitionList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "instanceVariableDefinitionList"


    // $ANTLR start "instanceVariable"
    // /Users/ari/Documents/antlr/vdmpp.g:95:1: instanceVariable : IDENT COLON type ( ASSIGN STRING )? ;
    public final void instanceVariable() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "instanceVariable");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(95, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:96:2: ( IDENT COLON type ( ASSIGN STRING )? )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:96:4: IDENT COLON type ( ASSIGN STRING )?
            {
            dbg.location(96,4);
            match(input,IDENT,FOLLOW_IDENT_in_instanceVariable549); if (state.failed) return ;
            dbg.location(96,10);
            match(input,COLON,FOLLOW_COLON_in_instanceVariable551); if (state.failed) return ;
            dbg.location(96,16);
            pushFollow(FOLLOW_type_in_instanceVariable553);
            type();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(96,21);
            // /Users/ari/Documents/antlr/vdmpp.g:96:21: ( ASSIGN STRING )?
            int alt12=2;
            try { dbg.enterSubRule(12);
            try { dbg.enterDecision(12);

            int LA12_0 = input.LA(1);

            if ( (LA12_0==ASSIGN) ) {
                alt12=1;
            }
            } finally {dbg.exitDecision(12);}

            switch (alt12) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:96:22: ASSIGN STRING
                    {
                    dbg.location(96,22);
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_instanceVariable556); if (state.failed) return ;
                    dbg.location(96,29);
                    match(input,STRING,FOLLOW_STRING_in_instanceVariable558); if (state.failed) return ;

                    }
                    break;

            }
            } finally {dbg.exitSubRule(12);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(97, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "instanceVariable");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "instanceVariable"


    // $ANTLR start "typeDefinitions"
    // /Users/ari/Documents/antlr/vdmpp.g:99:1: typeDefinitions : ( TYPES | TYPES typeDefinitionList ( SCOLON )? );
    public final void typeDefinitions() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "typeDefinitions");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(99, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:100:2: ( TYPES | TYPES typeDefinitionList ( SCOLON )? )
            int alt14=2;
            try { dbg.enterDecision(14);

            int LA14_0 = input.LA(1);

            if ( (LA14_0==TYPES) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==IDENT) ) {
                    alt14=2;
                }
                else if ( (LA14_1==EOF||LA14_1==END||(LA14_1>=VALUES && LA14_1<=INSTANCE)) ) {
                    alt14=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(14);}

            switch (alt14) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:100:4: TYPES
                    {
                    dbg.location(100,4);
                    match(input,TYPES,FOLLOW_TYPES_in_typeDefinitions573); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:101:4: TYPES typeDefinitionList ( SCOLON )?
                    {
                    dbg.location(101,4);
                    match(input,TYPES,FOLLOW_TYPES_in_typeDefinitions579); if (state.failed) return ;
                    dbg.location(101,10);
                    pushFollow(FOLLOW_typeDefinitionList_in_typeDefinitions581);
                    typeDefinitionList();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(101,29);
                    // /Users/ari/Documents/antlr/vdmpp.g:101:29: ( SCOLON )?
                    int alt13=2;
                    try { dbg.enterSubRule(13);
                    try { dbg.enterDecision(13);

                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==SCOLON) ) {
                        alt13=1;
                    }
                    } finally {dbg.exitDecision(13);}

                    switch (alt13) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /Users/ari/Documents/antlr/vdmpp.g:0:0: SCOLON
                            {
                            dbg.location(101,29);
                            match(input,SCOLON,FOLLOW_SCOLON_in_typeDefinitions583); if (state.failed) return ;

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(13);}


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(102, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "typeDefinitions");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "typeDefinitions"


    // $ANTLR start "typeDefinitionList"
    // /Users/ari/Documents/antlr/vdmpp.g:104:1: typeDefinitionList : typeDefinition ( SCOLON typeDefinition )* ;
    public final void typeDefinitionList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "typeDefinitionList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(104, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:105:2: ( typeDefinition ( SCOLON typeDefinition )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:105:4: typeDefinition ( SCOLON typeDefinition )*
            {
            dbg.location(105,4);
            pushFollow(FOLLOW_typeDefinition_in_typeDefinitionList596);
            typeDefinition();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(105,19);
            // /Users/ari/Documents/antlr/vdmpp.g:105:19: ( SCOLON typeDefinition )*
            try { dbg.enterSubRule(15);

            loop15:
            do {
                int alt15=2;
                try { dbg.enterDecision(15);

                int LA15_0 = input.LA(1);

                if ( (LA15_0==SCOLON) ) {
                    int LA15_1 = input.LA(2);

                    if ( (LA15_1==IDENT) ) {
                        alt15=1;
                    }


                }


                } finally {dbg.exitDecision(15);}

                switch (alt15) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:105:20: SCOLON typeDefinition
            	    {
            	    dbg.location(105,20);
            	    match(input,SCOLON,FOLLOW_SCOLON_in_typeDefinitionList599); if (state.failed) return ;
            	    dbg.location(105,27);
            	    pushFollow(FOLLOW_typeDefinition_in_typeDefinitionList601);
            	    typeDefinition();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);
            } finally {dbg.exitSubRule(15);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(105, 43);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "typeDefinitionList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "typeDefinitionList"


    // $ANTLR start "typeDefinition"
    // /Users/ari/Documents/antlr/vdmpp.g:107:1: typeDefinition : ( IDENT EQ type | IDENT DCOLON | IDENT DCOLON fieldList );
    public final void typeDefinition() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "typeDefinition");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(107, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:108:2: ( IDENT EQ type | IDENT DCOLON | IDENT DCOLON fieldList )
            int alt16=3;
            try { dbg.enterDecision(16);

            int LA16_0 = input.LA(1);

            if ( (LA16_0==IDENT) ) {
                int LA16_1 = input.LA(2);

                if ( (LA16_1==EQ) ) {
                    alt16=1;
                }
                else if ( (LA16_1==DCOLON) ) {
                    int LA16_3 = input.LA(3);

                    if ( (LA16_3==IDENT) ) {
                        alt16=3;
                    }
                    else if ( (LA16_3==EOF||LA16_3==END||(LA16_3>=VALUES && LA16_3<=INSTANCE)||LA16_3==SCOLON) ) {
                        alt16=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 16, 3, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(16);}

            switch (alt16) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:108:4: IDENT EQ type
                    {
                    dbg.location(108,4);
                    match(input,IDENT,FOLLOW_IDENT_in_typeDefinition613); if (state.failed) return ;
                    dbg.location(108,10);
                    match(input,EQ,FOLLOW_EQ_in_typeDefinition615); if (state.failed) return ;
                    dbg.location(108,13);
                    pushFollow(FOLLOW_type_in_typeDefinition617);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:109:4: IDENT DCOLON
                    {
                    dbg.location(109,4);
                    match(input,IDENT,FOLLOW_IDENT_in_typeDefinition622); if (state.failed) return ;
                    dbg.location(109,10);
                    match(input,DCOLON,FOLLOW_DCOLON_in_typeDefinition624); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Users/ari/Documents/antlr/vdmpp.g:110:4: IDENT DCOLON fieldList
                    {
                    dbg.location(110,4);
                    match(input,IDENT,FOLLOW_IDENT_in_typeDefinition629); if (state.failed) return ;
                    dbg.location(110,10);
                    match(input,DCOLON,FOLLOW_DCOLON_in_typeDefinition631); if (state.failed) return ;
                    dbg.location(110,17);
                    pushFollow(FOLLOW_fieldList_in_typeDefinition633);
                    fieldList();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(111, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "typeDefinition");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "typeDefinition"


    // $ANTLR start "fieldList"
    // /Users/ari/Documents/antlr/vdmpp.g:113:1: fieldList : IDENT COLON type ( IDENT COLON type )* ;
    public final void fieldList() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "fieldList");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(113, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:114:2: ( IDENT COLON type ( IDENT COLON type )* )
            dbg.enterAlt(1);

            // /Users/ari/Documents/antlr/vdmpp.g:114:4: IDENT COLON type ( IDENT COLON type )*
            {
            dbg.location(114,4);
            match(input,IDENT,FOLLOW_IDENT_in_fieldList644); if (state.failed) return ;
            dbg.location(114,10);
            match(input,COLON,FOLLOW_COLON_in_fieldList646); if (state.failed) return ;
            dbg.location(114,16);
            pushFollow(FOLLOW_type_in_fieldList648);
            type();

            state._fsp--;
            if (state.failed) return ;
            dbg.location(114,21);
            // /Users/ari/Documents/antlr/vdmpp.g:114:21: ( IDENT COLON type )*
            try { dbg.enterSubRule(17);

            loop17:
            do {
                int alt17=2;
                try { dbg.enterDecision(17);

                int LA17_0 = input.LA(1);

                if ( (LA17_0==IDENT) ) {
                    alt17=1;
                }


                } finally {dbg.exitDecision(17);}

                switch (alt17) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /Users/ari/Documents/antlr/vdmpp.g:114:22: IDENT COLON type
            	    {
            	    dbg.location(114,22);
            	    match(input,IDENT,FOLLOW_IDENT_in_fieldList651); if (state.failed) return ;
            	    dbg.location(114,28);
            	    match(input,COLON,FOLLOW_COLON_in_fieldList653); if (state.failed) return ;
            	    dbg.location(114,34);
            	    pushFollow(FOLLOW_type_in_fieldList655);
            	    type();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);
            } finally {dbg.exitSubRule(17);}


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(115, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "fieldList");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "fieldList"


    // $ANTLR start "type"
    // /Users/ari/Documents/antlr/vdmpp.g:118:1: type : ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type | ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type ) BAR type );
    public final void type() throws RecognitionException {
        try { dbg.enterRule(getGrammarFileName(), "type");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(118, 1);

        try {
            // /Users/ari/Documents/antlr/vdmpp.g:118:7: ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type | ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type ) BAR type )
            int alt19=8;
            try { dbg.enterDecision(19);

            try {
                isCyclicDecision = true;
                alt19 = dfa19.predict(input);
            }
            catch (NoViableAltException nvae) {
                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(19);}

            switch (alt19) {
                case 1 :
                    dbg.enterAlt(1);

                    // /Users/ari/Documents/antlr/vdmpp.g:118:9: IDENT
                    {
                    dbg.location(118,9);
                    match(input,IDENT,FOLLOW_IDENT_in_type670); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /Users/ari/Documents/antlr/vdmpp.g:119:4: QUOTE
                    {
                    dbg.location(119,4);
                    match(input,QUOTE,FOLLOW_QUOTE_in_type676); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /Users/ari/Documents/antlr/vdmpp.g:120:4: LB type RB
                    {
                    dbg.location(120,4);
                    match(input,LB,FOLLOW_LB_in_type682); if (state.failed) return ;
                    dbg.location(120,7);
                    pushFollow(FOLLOW_type_in_type684);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(120,12);
                    match(input,RB,FOLLOW_RB_in_type686); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /Users/ari/Documents/antlr/vdmpp.g:121:4: LP type RP
                    {
                    dbg.location(121,4);
                    match(input,LP,FOLLOW_LP_in_type692); if (state.failed) return ;
                    dbg.location(121,7);
                    pushFollow(FOLLOW_type_in_type694);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(121,12);
                    match(input,RP,FOLLOW_RP_in_type696); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /Users/ari/Documents/antlr/vdmpp.g:122:4: SEQ OF type
                    {
                    dbg.location(122,4);
                    match(input,SEQ,FOLLOW_SEQ_in_type702); if (state.failed) return ;
                    dbg.location(122,8);
                    match(input,OF,FOLLOW_OF_in_type704); if (state.failed) return ;
                    dbg.location(122,11);
                    pushFollow(FOLLOW_type_in_type706);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /Users/ari/Documents/antlr/vdmpp.g:123:4: SET OF type
                    {
                    dbg.location(123,4);
                    match(input,SET,FOLLOW_SET_in_type712); if (state.failed) return ;
                    dbg.location(123,8);
                    match(input,OF,FOLLOW_OF_in_type714); if (state.failed) return ;
                    dbg.location(123,11);
                    pushFollow(FOLLOW_type_in_type716);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // /Users/ari/Documents/antlr/vdmpp.g:124:4: MAP type TO type
                    {
                    dbg.location(124,4);
                    match(input,MAP,FOLLOW_MAP_in_type722); if (state.failed) return ;
                    dbg.location(124,8);
                    pushFollow(FOLLOW_type_in_type724);
                    type();

                    state._fsp--;
                    if (state.failed) return ;
                    dbg.location(124,13);
                    match(input,TO,FOLLOW_TO_in_type726); if (state.failed) return ;
                    dbg.location(124,16);
                    pushFollow(FOLLOW_type_in_type728);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    dbg.enterAlt(8);

                    // /Users/ari/Documents/antlr/vdmpp.g:125:4: ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type ) BAR type
                    {
                    dbg.location(125,4);
                    // /Users/ari/Documents/antlr/vdmpp.g:125:4: ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type )
                    int alt18=7;
                    try { dbg.enterSubRule(18);
                    try { dbg.enterDecision(18);

                    switch ( input.LA(1) ) {
                    case IDENT:
                        {
                        alt18=1;
                        }
                        break;
                    case QUOTE:
                        {
                        alt18=2;
                        }
                        break;
                    case LB:
                        {
                        alt18=3;
                        }
                        break;
                    case LP:
                        {
                        alt18=4;
                        }
                        break;
                    case SEQ:
                        {
                        alt18=5;
                        }
                        break;
                    case SET:
                        {
                        alt18=6;
                        }
                        break;
                    case MAP:
                        {
                        alt18=7;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 0, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }

                    } finally {dbg.exitDecision(18);}

                    switch (alt18) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /Users/ari/Documents/antlr/vdmpp.g:125:5: IDENT
                            {
                            dbg.location(125,5);
                            match(input,IDENT,FOLLOW_IDENT_in_type734); if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            dbg.enterAlt(2);

                            // /Users/ari/Documents/antlr/vdmpp.g:126:5: QUOTE
                            {
                            dbg.location(126,5);
                            match(input,QUOTE,FOLLOW_QUOTE_in_type741); if (state.failed) return ;

                            }
                            break;
                        case 3 :
                            dbg.enterAlt(3);

                            // /Users/ari/Documents/antlr/vdmpp.g:127:5: LB type RB
                            {
                            dbg.location(127,5);
                            match(input,LB,FOLLOW_LB_in_type748); if (state.failed) return ;
                            dbg.location(127,8);
                            pushFollow(FOLLOW_type_in_type750);
                            type();

                            state._fsp--;
                            if (state.failed) return ;
                            dbg.location(127,13);
                            match(input,RB,FOLLOW_RB_in_type752); if (state.failed) return ;

                            }
                            break;
                        case 4 :
                            dbg.enterAlt(4);

                            // /Users/ari/Documents/antlr/vdmpp.g:128:5: LP type RP
                            {
                            dbg.location(128,5);
                            match(input,LP,FOLLOW_LP_in_type759); if (state.failed) return ;
                            dbg.location(128,8);
                            pushFollow(FOLLOW_type_in_type761);
                            type();

                            state._fsp--;
                            if (state.failed) return ;
                            dbg.location(128,13);
                            match(input,RP,FOLLOW_RP_in_type763); if (state.failed) return ;

                            }
                            break;
                        case 5 :
                            dbg.enterAlt(5);

                            // /Users/ari/Documents/antlr/vdmpp.g:129:5: SEQ OF type
                            {
                            dbg.location(129,5);
                            match(input,SEQ,FOLLOW_SEQ_in_type770); if (state.failed) return ;
                            dbg.location(129,9);
                            match(input,OF,FOLLOW_OF_in_type772); if (state.failed) return ;
                            dbg.location(129,12);
                            pushFollow(FOLLOW_type_in_type774);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 6 :
                            dbg.enterAlt(6);

                            // /Users/ari/Documents/antlr/vdmpp.g:130:5: SET OF type
                            {
                            dbg.location(130,5);
                            match(input,SET,FOLLOW_SET_in_type781); if (state.failed) return ;
                            dbg.location(130,9);
                            match(input,OF,FOLLOW_OF_in_type783); if (state.failed) return ;
                            dbg.location(130,12);
                            pushFollow(FOLLOW_type_in_type785);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;
                        case 7 :
                            dbg.enterAlt(7);

                            // /Users/ari/Documents/antlr/vdmpp.g:131:5: MAP type TO type
                            {
                            dbg.location(131,5);
                            match(input,MAP,FOLLOW_MAP_in_type792); if (state.failed) return ;
                            dbg.location(131,9);
                            pushFollow(FOLLOW_type_in_type794);
                            type();

                            state._fsp--;
                            if (state.failed) return ;
                            dbg.location(131,14);
                            match(input,TO,FOLLOW_TO_in_type796); if (state.failed) return ;
                            dbg.location(131,17);
                            pushFollow(FOLLOW_type_in_type798);
                            type();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(18);}

                    dbg.location(132,5);
                    match(input,BAR,FOLLOW_BAR_in_type805); if (state.failed) return ;
                    dbg.location(132,9);
                    pushFollow(FOLLOW_type_in_type807);
                    type();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        dbg.location(133, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "type");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return ;
    }
    // $ANTLR end "type"

    // $ANTLR start synpred22_vdmpp
    public final void synpred22_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:118:9: ( IDENT )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:118:9: IDENT
        {
        dbg.location(118,9);
        match(input,IDENT,FOLLOW_IDENT_in_synpred22_vdmpp670); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred22_vdmpp

    // $ANTLR start synpred23_vdmpp
    public final void synpred23_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:119:4: ( QUOTE )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:119:4: QUOTE
        {
        dbg.location(119,4);
        match(input,QUOTE,FOLLOW_QUOTE_in_synpred23_vdmpp676); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred23_vdmpp

    // $ANTLR start synpred24_vdmpp
    public final void synpred24_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:120:4: ( LB type RB )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:120:4: LB type RB
        {
        dbg.location(120,4);
        match(input,LB,FOLLOW_LB_in_synpred24_vdmpp682); if (state.failed) return ;
        dbg.location(120,7);
        pushFollow(FOLLOW_type_in_synpred24_vdmpp684);
        type();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(120,12);
        match(input,RB,FOLLOW_RB_in_synpred24_vdmpp686); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred24_vdmpp

    // $ANTLR start synpred25_vdmpp
    public final void synpred25_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:121:4: ( LP type RP )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:121:4: LP type RP
        {
        dbg.location(121,4);
        match(input,LP,FOLLOW_LP_in_synpred25_vdmpp692); if (state.failed) return ;
        dbg.location(121,7);
        pushFollow(FOLLOW_type_in_synpred25_vdmpp694);
        type();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(121,12);
        match(input,RP,FOLLOW_RP_in_synpred25_vdmpp696); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred25_vdmpp

    // $ANTLR start synpred26_vdmpp
    public final void synpred26_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:122:4: ( SEQ OF type )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:122:4: SEQ OF type
        {
        dbg.location(122,4);
        match(input,SEQ,FOLLOW_SEQ_in_synpred26_vdmpp702); if (state.failed) return ;
        dbg.location(122,8);
        match(input,OF,FOLLOW_OF_in_synpred26_vdmpp704); if (state.failed) return ;
        dbg.location(122,11);
        pushFollow(FOLLOW_type_in_synpred26_vdmpp706);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred26_vdmpp

    // $ANTLR start synpred27_vdmpp
    public final void synpred27_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:123:4: ( SET OF type )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:123:4: SET OF type
        {
        dbg.location(123,4);
        match(input,SET,FOLLOW_SET_in_synpred27_vdmpp712); if (state.failed) return ;
        dbg.location(123,8);
        match(input,OF,FOLLOW_OF_in_synpred27_vdmpp714); if (state.failed) return ;
        dbg.location(123,11);
        pushFollow(FOLLOW_type_in_synpred27_vdmpp716);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred27_vdmpp

    // $ANTLR start synpred28_vdmpp
    public final void synpred28_vdmpp_fragment() throws RecognitionException {   
        // /Users/ari/Documents/antlr/vdmpp.g:124:4: ( MAP type TO type )
        dbg.enterAlt(1);

        // /Users/ari/Documents/antlr/vdmpp.g:124:4: MAP type TO type
        {
        dbg.location(124,4);
        match(input,MAP,FOLLOW_MAP_in_synpred28_vdmpp722); if (state.failed) return ;
        dbg.location(124,8);
        pushFollow(FOLLOW_type_in_synpred28_vdmpp724);
        type();

        state._fsp--;
        if (state.failed) return ;
        dbg.location(124,13);
        match(input,TO,FOLLOW_TO_in_synpred28_vdmpp726); if (state.failed) return ;
        dbg.location(124,16);
        pushFollow(FOLLOW_type_in_synpred28_vdmpp728);
        type();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred28_vdmpp

    // Delegated rules

    public final boolean synpred28_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred28_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred24_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred24_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred27_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred27_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred22_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred22_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred26_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred26_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred23_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred23_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred25_vdmpp() {
        state.backtracking++;
        dbg.beginBacktrack(state.backtracking);
        int start = input.mark();
        try {
            synpred25_vdmpp_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        dbg.endBacktrack(state.backtracking, success);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA3 dfa3 = new DFA3(this);
    protected DFA19 dfa19 = new DFA19(this);
    static final String DFA3_eotS =
        "\13\uffff";
    static final String DFA3_eofS =
        "\13\uffff";
    static final String DFA3_minS =
        "\1\5\1\37\1\6\1\uffff\1\4\1\uffff\1\7\1\37\1\10\2\uffff";
    static final String DFA3_maxS =
        "\1\5\1\37\1\17\1\uffff\1\4\1\uffff\1\7\1\37\1\17\2\uffff";
    static final String DFA3_acceptS =
        "\3\uffff\1\1\1\uffff\1\2\3\uffff\1\3\1\4";
    static final String DFA3_specialS =
        "\13\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\1",
            "\1\2",
            "\1\4\1\uffff\1\3\4\uffff\3\5",
            "",
            "\1\6",
            "",
            "\1\7",
            "\1\10",
            "\1\11\4\uffff\3\12",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "55:1: classDefinition : ( CLASS nameStart= IDENT END nameEnd= IDENT | CLASS IDENT definitionList END IDENT | CLASS IDENT IS SUBCLASS OF IDENT END IDENT | CLASS IDENT IS SUBCLASS OF IDENT definitionList END IDENT );";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
    static final String DFA19_eotS =
        "\20\uffff";
    static final String DFA19_eofS =
        "\20\uffff";
    static final String DFA19_minS =
        "\1\11\7\0\10\uffff";
    static final String DFA19_maxS =
        "\1\40\7\0\10\uffff";
    static final String DFA19_acceptS =
        "\10\uffff\1\1\1\10\1\2\1\3\1\4\1\5\1\6\1\7";
    static final String DFA19_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\10\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\7\1\uffff\1\5\1\6\11\uffff\1\3\1\uffff\1\4\6\uffff\1\1\1"+
            "\2",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "118:1: type : ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type | ( IDENT | QUOTE | LB type RB | LP type RP | SEQ OF type | SET OF type | MAP type TO type ) BAR type );";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_1 = input.LA(1);

                         
                        int index19_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred22_vdmpp()) ) {s = 8;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA19_2 = input.LA(1);

                         
                        int index19_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred23_vdmpp()) ) {s = 10;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA19_3 = input.LA(1);

                         
                        int index19_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred24_vdmpp()) ) {s = 11;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA19_4 = input.LA(1);

                         
                        int index19_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred25_vdmpp()) ) {s = 12;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA19_5 = input.LA(1);

                         
                        int index19_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred26_vdmpp()) ) {s = 13;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA19_6 = input.LA(1);

                         
                        int index19_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred27_vdmpp()) ) {s = 14;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA19_7 = input.LA(1);

                         
                        int index19_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred28_vdmpp()) ) {s = 15;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index19_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_document_in_start287 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDefinitionList_in_document300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_classDefinition_in_classDefinitionList317 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_classDefinition_in_classDefinitionList319 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_CLASS_in_classDefinition332 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition336 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_END_in_classDefinition338 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_classDefinition351 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition353 = new BitSet(new long[]{0x000000000000E000L});
    public static final BitSet FOLLOW_definitionList_in_classDefinition355 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_END_in_classDefinition357 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_classDefinition364 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition366 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_IS_in_classDefinition368 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SUBCLASS_in_classDefinition370 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_classDefinition372 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition374 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_END_in_classDefinition376 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CLASS_in_classDefinition383 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition385 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_IS_in_classDefinition387 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SUBCLASS_in_classDefinition389 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_classDefinition391 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition393 = new BitSet(new long[]{0x000000000000E000L});
    public static final BitSet FOLLOW_definitionList_in_classDefinition395 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_END_in_classDefinition397 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_IDENT_in_classDefinition399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_definitionBlock_in_definitionList411 = new BitSet(new long[]{0x000000000000E002L});
    public static final BitSet FOLLOW_definitionBlock_in_definitionList413 = new BitSet(new long[]{0x000000000000E002L});
    public static final BitSet FOLLOW_valueDefinitions_in_definitionBlock426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_instanceVariableDefinitions_in_definitionBlock431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinitions_in_definitionBlock436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUES_in_valueDefinitions448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUES_in_valueDefinitions453 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_valueDefinitionList_in_valueDefinitions455 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_valueDefinitions457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_valueDefinition_in_valueDefinitionList469 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_valueDefinitionList472 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_valueDefinition_in_valueDefinitionList474 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_IDENT_in_valueDefinition488 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_EQ_in_valueDefinition490 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_STRING_in_valueDefinition492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSTANCE_in_instanceVariableDefinitions504 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VARIABLES_in_instanceVariableDefinitions506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSTANCE_in_instanceVariableDefinitions511 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_VARIABLES_in_instanceVariableDefinitions513 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_instanceVariableDefinitionList_in_instanceVariableDefinitions515 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_instanceVariableDefinitions517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_instanceVariable_in_instanceVariableDefinitionList530 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_instanceVariableDefinitionList533 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_instanceVariable_in_instanceVariableDefinitionList535 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_IDENT_in_instanceVariable549 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_COLON_in_instanceVariable551 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_instanceVariable553 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_ASSIGN_in_instanceVariable556 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_STRING_in_instanceVariable558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPES_in_typeDefinitions573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPES_in_typeDefinitions579 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_typeDefinitionList_in_typeDefinitions581 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_typeDefinitions583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDefinition_in_typeDefinitionList596 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_SCOLON_in_typeDefinitionList599 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_typeDefinition_in_typeDefinitionList601 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_IDENT_in_typeDefinition613 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_EQ_in_typeDefinition615 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_typeDefinition617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_typeDefinition622 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DCOLON_in_typeDefinition624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_typeDefinition629 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_DCOLON_in_typeDefinition631 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_fieldList_in_typeDefinition633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_fieldList644 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_COLON_in_fieldList646 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_fieldList648 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_IDENT_in_fieldList651 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_COLON_in_fieldList653 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_fieldList655 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_IDENT_in_type670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_type676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LB_in_type682 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type684 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RB_in_type686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LP_in_type692 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type694 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RP_in_type696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEQ_in_type702 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_type704 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_type712 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_type714 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAP_in_type722 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type724 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_TO_in_type726 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_type734 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_QUOTE_in_type741 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LB_in_type748 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type750 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RB_in_type752 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LP_in_type759 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type761 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RP_in_type763 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SEQ_in_type770 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_type772 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type774 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_SET_in_type781 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_type783 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type785 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_MAP_in_type792 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type794 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_TO_in_type796 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type798 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_BAR_in_type805 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_type807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_synpred22_vdmpp670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUOTE_in_synpred23_vdmpp676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LB_in_synpred24_vdmpp682 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred24_vdmpp684 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RB_in_synpred24_vdmpp686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LP_in_synpred25_vdmpp692 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred25_vdmpp694 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_RP_in_synpred25_vdmpp696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEQ_in_synpred26_vdmpp702 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_synpred26_vdmpp704 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred26_vdmpp706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_synpred27_vdmpp712 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OF_in_synpred27_vdmpp714 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred27_vdmpp716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAP_in_synpred28_vdmpp722 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred28_vdmpp724 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_TO_in_synpred28_vdmpp726 = new BitSet(new long[]{0x0000000181401A00L});
    public static final BitSet FOLLOW_type_in_synpred28_vdmpp728 = new BitSet(new long[]{0x0000000000000002L});

}