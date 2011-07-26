// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g 2011-07-22 11:56:57

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcToStringParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "TOSTRING_DCL", "WS", "ID", "JAVANAME", "StringLiteral", "RawJava", "EscapeSequence", "LINE_COMMENT", "'import'", "';'", "'%'", "'+'", "'['", "']'", "'->'", "'#'"
    };
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

    // delegates
    // delegators


        public AstcToStringParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public AstcToStringParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return AstcToStringParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g"; }


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


    public static class root_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "root"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:205:1: root : top EOF ;
    public final AstcToStringParser.root_return root() throws RecognitionException {
        AstcToStringParser.root_return retval = new AstcToStringParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        AstcToStringParser.top_return top1 = null;


        Object EOF2_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:206:3: ( top EOF )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:206:5: top EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_top_in_root220);
            top1=top();

            state._fsp--;

            adaptor.addChild(root_0, top1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_root222); 
            EOF2_tree = (Object)adaptor.create(EOF2);
            adaptor.addChild(root_0, EOF2_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "root"

    public static class top_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "top"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:210:1: top : TOSTRING_DCL ( imports )* ( toString_ )* ;
    public final AstcToStringParser.top_return top() throws RecognitionException {
        AstcToStringParser.top_return retval = new AstcToStringParser.top_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOSTRING_DCL3=null;
        AstcToStringParser.imports_return imports4 = null;

        AstcToStringParser.toString__return toString_5 = null;


        Object TOSTRING_DCL3_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:3: ( TOSTRING_DCL ( imports )* ( toString_ )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:5: TOSTRING_DCL ( imports )* ( toString_ )*
            {
            root_0 = (Object)adaptor.nil();

            TOSTRING_DCL3=(Token)match(input,TOSTRING_DCL,FOLLOW_TOSTRING_DCL_in_top240); 
            TOSTRING_DCL3_tree = (Object)adaptor.create(TOSTRING_DCL3);
            root_0 = (Object)adaptor.becomeRoot(TOSTRING_DCL3_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:19: ( imports )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:19: imports
            	    {
            	    pushFollow(FOLLOW_imports_in_top243);
            	    imports4=imports();

            	    state._fsp--;

            	    adaptor.addChild(root_0, imports4.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:28: ( toString_ )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==15) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:28: toString_
            	    {
            	    pushFollow(FOLLOW_toString__in_top246);
            	    toString_5=toString_();

            	    state._fsp--;

            	    adaptor.addChild(root_0, toString_5.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "top"

    public static class imports_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "imports"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:214:1: imports : 'import' JAVANAME ';' ;
    public final AstcToStringParser.imports_return imports() throws RecognitionException {
        AstcToStringParser.imports_return retval = new AstcToStringParser.imports_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token string_literal6=null;
        Token JAVANAME7=null;
        Token char_literal8=null;

        Object string_literal6_tree=null;
        Object JAVANAME7_tree=null;
        Object char_literal8_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:215:3: ( 'import' JAVANAME ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:215:5: 'import' JAVANAME ';'
            {
            root_0 = (Object)adaptor.nil();

            string_literal6=(Token)match(input,13,FOLLOW_13_in_imports262); 
            string_literal6_tree = (Object)adaptor.create(string_literal6);
            root_0 = (Object)adaptor.becomeRoot(string_literal6_tree, root_0);

            JAVANAME7=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_imports265); 
            JAVANAME7_tree = (Object)adaptor.create(JAVANAME7);
            adaptor.addChild(root_0, JAVANAME7_tree);

            char_literal8=(Token)match(input,14,FOLLOW_14_in_imports267); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "imports"

    public static class toString__return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "toString_"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:219:1: toString_ : '%' aspectName ASSIGN ( StringLiteral ( '+' RawJava )? | field ( ( '+' )? RawJava )? | '+' ( StringLiteral | field ) )* ( ( ';' )? ) ;
    public final AstcToStringParser.toString__return toString_() throws RecognitionException {
        AstcToStringParser.toString__return retval = new AstcToStringParser.toString__return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal9=null;
        Token ASSIGN11=null;
        Token StringLiteral12=null;
        Token char_literal13=null;
        Token RawJava14=null;
        Token char_literal16=null;
        Token RawJava17=null;
        Token char_literal18=null;
        Token StringLiteral19=null;
        Token char_literal21=null;
        AstcToStringParser.aspectName_return aspectName10 = null;

        AstcToStringParser.field_return field15 = null;

        AstcToStringParser.field_return field20 = null;


        Object char_literal9_tree=null;
        Object ASSIGN11_tree=null;
        Object StringLiteral12_tree=null;
        Object char_literal13_tree=null;
        Object RawJava14_tree=null;
        Object char_literal16_tree=null;
        Object RawJava17_tree=null;
        Object char_literal18_tree=null;
        Object StringLiteral19_tree=null;
        Object char_literal21_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:3: ( '%' aspectName ASSIGN ( StringLiteral ( '+' RawJava )? | field ( ( '+' )? RawJava )? | '+' ( StringLiteral | field ) )* ( ( ';' )? ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:5: '%' aspectName ASSIGN ( StringLiteral ( '+' RawJava )? | field ( ( '+' )? RawJava )? | '+' ( StringLiteral | field ) )* ( ( ';' )? )
            {
            root_0 = (Object)adaptor.nil();

            char_literal9=(Token)match(input,15,FOLLOW_15_in_toString_286); 
            char_literal9_tree = (Object)adaptor.create(char_literal9);
            root_0 = (Object)adaptor.becomeRoot(char_literal9_tree, root_0);

            pushFollow(FOLLOW_aspectName_in_toString_289);
            aspectName10=aspectName();

            state._fsp--;

            adaptor.addChild(root_0, aspectName10.getTree());
            ASSIGN11=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_toString_291); 
            ASSIGN11_tree = (Object)adaptor.create(ASSIGN11);
            adaptor.addChild(root_0, ASSIGN11_tree);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:28: ( StringLiteral ( '+' RawJava )? | field ( ( '+' )? RawJava )? | '+' ( StringLiteral | field ) )*
            loop7:
            do {
                int alt7=4;
                switch ( input.LA(1) ) {
                case StringLiteral:
                    {
                    alt7=1;
                    }
                    break;
                case 17:
                    {
                    alt7=2;
                    }
                    break;
                case 16:
                    {
                    alt7=3;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:30: StringLiteral ( '+' RawJava )?
            	    {
            	    StringLiteral12=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_toString_295); 
            	    StringLiteral12_tree = (Object)adaptor.create(StringLiteral12);
            	    adaptor.addChild(root_0, StringLiteral12_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:44: ( '+' RawJava )?
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==16) ) {
            	        int LA3_1 = input.LA(2);

            	        if ( (LA3_1==RawJava) ) {
            	            alt3=1;
            	        }
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:45: '+' RawJava
            	            {
            	            char_literal13=(Token)match(input,16,FOLLOW_16_in_toString_298); 
            	            char_literal13_tree = (Object)adaptor.create(char_literal13);
            	            adaptor.addChild(root_0, char_literal13_tree);

            	            RawJava14=(Token)match(input,RawJava,FOLLOW_RawJava_in_toString_300); 
            	            RawJava14_tree = (Object)adaptor.create(RawJava14);
            	            adaptor.addChild(root_0, RawJava14_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:61: field ( ( '+' )? RawJava )?
            	    {
            	    pushFollow(FOLLOW_field_in_toString_306);
            	    field15=field();

            	    state._fsp--;

            	    adaptor.addChild(root_0, field15.getTree());
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:67: ( ( '+' )? RawJava )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==16) ) {
            	        int LA5_1 = input.LA(2);

            	        if ( (LA5_1==RawJava) ) {
            	            alt5=1;
            	        }
            	    }
            	    else if ( (LA5_0==RawJava) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:68: ( '+' )? RawJava
            	            {
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:68: ( '+' )?
            	            int alt4=2;
            	            int LA4_0 = input.LA(1);

            	            if ( (LA4_0==16) ) {
            	                alt4=1;
            	            }
            	            switch (alt4) {
            	                case 1 :
            	                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:68: '+'
            	                    {
            	                    char_literal16=(Token)match(input,16,FOLLOW_16_in_toString_309); 
            	                    char_literal16_tree = (Object)adaptor.create(char_literal16);
            	                    adaptor.addChild(root_0, char_literal16_tree);


            	                    }
            	                    break;

            	            }

            	            RawJava17=(Token)match(input,RawJava,FOLLOW_RawJava_in_toString_312); 
            	            RawJava17_tree = (Object)adaptor.create(RawJava17);
            	            adaptor.addChild(root_0, RawJava17_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:85: '+' ( StringLiteral | field )
            	    {
            	    char_literal18=(Token)match(input,16,FOLLOW_16_in_toString_318); 
            	    char_literal18_tree = (Object)adaptor.create(char_literal18);
            	    adaptor.addChild(root_0, char_literal18_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:89: ( StringLiteral | field )
            	    int alt6=2;
            	    int LA6_0 = input.LA(1);

            	    if ( (LA6_0==StringLiteral) ) {
            	        alt6=1;
            	    }
            	    else if ( (LA6_0==17) ) {
            	        alt6=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 6, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt6) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:90: StringLiteral
            	            {
            	            StringLiteral19=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_toString_321); 
            	            StringLiteral19_tree = (Object)adaptor.create(StringLiteral19);
            	            adaptor.addChild(root_0, StringLiteral19_tree);


            	            }
            	            break;
            	        case 2 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:104: field
            	            {
            	            pushFollow(FOLLOW_field_in_toString_323);
            	            field20=field();

            	            state._fsp--;

            	            adaptor.addChild(root_0, field20.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:114: ( ( ';' )? )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:115: ( ';' )?
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:118: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==14) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:220:118: ';'
                    {
                    char_literal21=(Token)match(input,14,FOLLOW_14_in_toString_330); 

                    }
                    break;

            }


            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "toString_"

    public static class field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "field"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:231:1: field : '[' ID ']' ;
    public final AstcToStringParser.field_return field() throws RecognitionException {
        AstcToStringParser.field_return retval = new AstcToStringParser.field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal22=null;
        Token ID23=null;
        Token char_literal24=null;

        Object char_literal22_tree=null;
        Object ID23_tree=null;
        Object char_literal24_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:232:3: ( '[' ID ']' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:232:5: '[' ID ']'
            {
            root_0 = (Object)adaptor.nil();

            char_literal22=(Token)match(input,17,FOLLOW_17_in_field424); 
            ID23=(Token)match(input,ID,FOLLOW_ID_in_field427); 
            ID23_tree = (Object)adaptor.create(ID23);
            root_0 = (Object)adaptor.becomeRoot(ID23_tree, root_0);

            char_literal24=(Token)match(input,18,FOLLOW_18_in_field430); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "field"

    public static class aspectName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectName"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:255:1: aspectName : ID ( '->' ( ID | '#' ID ) )* ;
    public final AstcToStringParser.aspectName_return aspectName() throws RecognitionException {
        AstcToStringParser.aspectName_return retval = new AstcToStringParser.aspectName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID25=null;
        Token string_literal26=null;
        Token ID27=null;
        Token char_literal28=null;
        Token ID29=null;

        Object ID25_tree=null;
        Object string_literal26_tree=null;
        Object ID27_tree=null;
        Object char_literal28_tree=null;
        Object ID29_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:3: ( ID ( '->' ( ID | '#' ID ) )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:5: ID ( '->' ( ID | '#' ID ) )*
            {
            root_0 = (Object)adaptor.nil();

            ID25=(Token)match(input,ID,FOLLOW_ID_in_aspectName486); 
            ID25_tree = (Object)adaptor.create(ID25);
            root_0 = (Object)adaptor.becomeRoot(ID25_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:9: ( '->' ( ID | '#' ID ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==19) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:10: '->' ( ID | '#' ID )
            	    {
            	    string_literal26=(Token)match(input,19,FOLLOW_19_in_aspectName490); 
            	    string_literal26_tree = (Object)adaptor.create(string_literal26);
            	    adaptor.addChild(root_0, string_literal26_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:15: ( ID | '#' ID )
            	    int alt9=2;
            	    int LA9_0 = input.LA(1);

            	    if ( (LA9_0==ID) ) {
            	        alt9=1;
            	    }
            	    else if ( (LA9_0==20) ) {
            	        alt9=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt9) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:16: ID
            	            {
            	            ID27=(Token)match(input,ID,FOLLOW_ID_in_aspectName493); 
            	            ID27_tree = (Object)adaptor.create(ID27);
            	            adaptor.addChild(root_0, ID27_tree);


            	            }
            	            break;
            	        case 2 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:256:21: '#' ID
            	            {
            	            char_literal28=(Token)match(input,20,FOLLOW_20_in_aspectName497); 
            	            char_literal28_tree = (Object)adaptor.create(char_literal28);
            	            adaptor.addChild(root_0, char_literal28_tree);

            	            ID29=(Token)match(input,ID,FOLLOW_ID_in_aspectName499); 
            	            ID29_tree = (Object)adaptor.create(ID29);
            	            adaptor.addChild(root_0, ID29_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "aspectName"

    // Delegated rules


 

    public static final BitSet FOLLOW_top_in_root220 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_root222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOSTRING_DCL_in_top240 = new BitSet(new long[]{0x000000000000A002L});
    public static final BitSet FOLLOW_imports_in_top243 = new BitSet(new long[]{0x000000000000A002L});
    public static final BitSet FOLLOW_toString__in_top246 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_13_in_imports262 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_JAVANAME_in_imports265 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_imports267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_toString_286 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_aspectName_in_toString_289 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_toString_291 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_StringLiteral_in_toString_295 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_16_in_toString_298 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_RawJava_in_toString_300 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_field_in_toString_306 = new BitSet(new long[]{0x0000000000034602L});
    public static final BitSet FOLLOW_16_in_toString_309 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_RawJava_in_toString_312 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_16_in_toString_318 = new BitSet(new long[]{0x0000000000020200L});
    public static final BitSet FOLLOW_StringLiteral_in_toString_321 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_field_in_toString_323 = new BitSet(new long[]{0x0000000000034202L});
    public static final BitSet FOLLOW_14_in_toString_330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_field424 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_field427 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_field430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_aspectName486 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_aspectName490 = new BitSet(new long[]{0x0000000000100080L});
    public static final BitSet FOLLOW_ID_in_aspectName493 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_20_in_aspectName497 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_aspectName499 = new BitSet(new long[]{0x0000000000080002L});

}