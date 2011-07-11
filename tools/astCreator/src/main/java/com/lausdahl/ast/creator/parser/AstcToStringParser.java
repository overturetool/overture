// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g 2011-07-11 14:47:26

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcToStringParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "TOSTRING_DCL", "WS", "ID", "JAVANAME", "StringLiteral", "RawJava", "EscapeSequence", "LINE_COMMENT", "'%'", "'+'", "'['", "']'", "'->'", "'#'"
    };
    public static final int StringLiteral=9;
    public static final int WS=6;
    public static final int T__16=16;
    public static final int T__15=15;
    public static final int T__18=18;
    public static final int T__17=17;
    public static final int LINE_COMMENT=12;
    public static final int T__14=14;
    public static final int JAVANAME=8;
    public static final int T__13=13;
    public static final int ASSIGN=4;
    public static final int RawJava=10;
    public static final int TOSTRING_DCL=5;
    public static final int ID=7;
    public static final int EOF=-1;
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:202:1: root : top EOF ;
    public final AstcToStringParser.root_return root() throws RecognitionException {
        AstcToStringParser.root_return retval = new AstcToStringParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        AstcToStringParser.top_return top1 = null;


        Object EOF2_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:203:3: ( top EOF )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:203:5: top EOF
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:207:1: top : TOSTRING_DCL ( toString_ )* ;
    public final AstcToStringParser.top_return top() throws RecognitionException {
        AstcToStringParser.top_return retval = new AstcToStringParser.top_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOSTRING_DCL3=null;
        AstcToStringParser.toString__return toString_4 = null;


        Object TOSTRING_DCL3_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:208:3: ( TOSTRING_DCL ( toString_ )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:208:5: TOSTRING_DCL ( toString_ )*
            {
            root_0 = (Object)adaptor.nil();

            TOSTRING_DCL3=(Token)match(input,TOSTRING_DCL,FOLLOW_TOSTRING_DCL_in_top240); 
            TOSTRING_DCL3_tree = (Object)adaptor.create(TOSTRING_DCL3);
            root_0 = (Object)adaptor.becomeRoot(TOSTRING_DCL3_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:208:19: ( toString_ )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==13) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:208:19: toString_
            	    {
            	    pushFollow(FOLLOW_toString__in_top243);
            	    toString_4=toString_();

            	    state._fsp--;

            	    adaptor.addChild(root_0, toString_4.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
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

    public static class toString__return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "toString_"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:210:1: toString_ : '%' aspectName ASSIGN ( StringLiteral | field ( '+' )? | RawJava ( '+' )? )* ;
    public final AstcToStringParser.toString__return toString_() throws RecognitionException {
        AstcToStringParser.toString__return retval = new AstcToStringParser.toString__return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal5=null;
        Token ASSIGN7=null;
        Token StringLiteral8=null;
        Token char_literal10=null;
        Token RawJava11=null;
        Token char_literal12=null;
        AstcToStringParser.aspectName_return aspectName6 = null;

        AstcToStringParser.field_return field9 = null;


        Object char_literal5_tree=null;
        Object ASSIGN7_tree=null;
        Object StringLiteral8_tree=null;
        Object char_literal10_tree=null;
        Object RawJava11_tree=null;
        Object char_literal12_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:3: ( '%' aspectName ASSIGN ( StringLiteral | field ( '+' )? | RawJava ( '+' )? )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:5: '%' aspectName ASSIGN ( StringLiteral | field ( '+' )? | RawJava ( '+' )? )*
            {
            root_0 = (Object)adaptor.nil();

            char_literal5=(Token)match(input,13,FOLLOW_13_in_toString_256); 
            char_literal5_tree = (Object)adaptor.create(char_literal5);
            root_0 = (Object)adaptor.becomeRoot(char_literal5_tree, root_0);

            pushFollow(FOLLOW_aspectName_in_toString_259);
            aspectName6=aspectName();

            state._fsp--;

            adaptor.addChild(root_0, aspectName6.getTree());
            ASSIGN7=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_toString_261); 
            ASSIGN7_tree = (Object)adaptor.create(ASSIGN7);
            adaptor.addChild(root_0, ASSIGN7_tree);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:28: ( StringLiteral | field ( '+' )? | RawJava ( '+' )? )*
            loop4:
            do {
                int alt4=4;
                switch ( input.LA(1) ) {
                case StringLiteral:
                    {
                    alt4=1;
                    }
                    break;
                case 15:
                    {
                    alt4=2;
                    }
                    break;
                case RawJava:
                    {
                    alt4=3;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:30: StringLiteral
            	    {
            	    StringLiteral8=(Token)match(input,StringLiteral,FOLLOW_StringLiteral_in_toString_265); 
            	    StringLiteral8_tree = (Object)adaptor.create(StringLiteral8);
            	    adaptor.addChild(root_0, StringLiteral8_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:48: field ( '+' )?
            	    {
            	    pushFollow(FOLLOW_field_in_toString_271);
            	    field9=field();

            	    state._fsp--;

            	    adaptor.addChild(root_0, field9.getTree());
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:54: ( '+' )?
            	    int alt2=2;
            	    int LA2_0 = input.LA(1);

            	    if ( (LA2_0==14) ) {
            	        alt2=1;
            	    }
            	    switch (alt2) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:54: '+'
            	            {
            	            char_literal10=(Token)match(input,14,FOLLOW_14_in_toString_273); 
            	            char_literal10_tree = (Object)adaptor.create(char_literal10);
            	            adaptor.addChild(root_0, char_literal10_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:61: RawJava ( '+' )?
            	    {
            	    RawJava11=(Token)match(input,RawJava,FOLLOW_RawJava_in_toString_278); 
            	    RawJava11_tree = (Object)adaptor.create(RawJava11);
            	    adaptor.addChild(root_0, RawJava11_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:69: ( '+' )?
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==14) ) {
            	        alt3=1;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:211:69: '+'
            	            {
            	            char_literal12=(Token)match(input,14,FOLLOW_14_in_toString_280); 
            	            char_literal12_tree = (Object)adaptor.create(char_literal12);
            	            adaptor.addChild(root_0, char_literal12_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end "toString_"

    public static class field_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "field"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:222:1: field : '[' ID ']' ;
    public final AstcToStringParser.field_return field() throws RecognitionException {
        AstcToStringParser.field_return retval = new AstcToStringParser.field_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal13=null;
        Token ID14=null;
        Token char_literal15=null;

        Object char_literal13_tree=null;
        Object ID14_tree=null;
        Object char_literal15_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:223:3: ( '[' ID ']' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:223:5: '[' ID ']'
            {
            root_0 = (Object)adaptor.nil();

            char_literal13=(Token)match(input,15,FOLLOW_15_in_field375); 
            ID14=(Token)match(input,ID,FOLLOW_ID_in_field378); 
            ID14_tree = (Object)adaptor.create(ID14);
            root_0 = (Object)adaptor.becomeRoot(ID14_tree, root_0);

            char_literal15=(Token)match(input,16,FOLLOW_16_in_field381); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:246:1: aspectName : ID ( '->' ( ID | '#' ID ) )* ;
    public final AstcToStringParser.aspectName_return aspectName() throws RecognitionException {
        AstcToStringParser.aspectName_return retval = new AstcToStringParser.aspectName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID16=null;
        Token string_literal17=null;
        Token ID18=null;
        Token char_literal19=null;
        Token ID20=null;

        Object ID16_tree=null;
        Object string_literal17_tree=null;
        Object ID18_tree=null;
        Object char_literal19_tree=null;
        Object ID20_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:3: ( ID ( '->' ( ID | '#' ID ) )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:5: ID ( '->' ( ID | '#' ID ) )*
            {
            root_0 = (Object)adaptor.nil();

            ID16=(Token)match(input,ID,FOLLOW_ID_in_aspectName437); 
            ID16_tree = (Object)adaptor.create(ID16);
            root_0 = (Object)adaptor.becomeRoot(ID16_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:9: ( '->' ( ID | '#' ID ) )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==17) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:10: '->' ( ID | '#' ID )
            	    {
            	    string_literal17=(Token)match(input,17,FOLLOW_17_in_aspectName441); 
            	    string_literal17_tree = (Object)adaptor.create(string_literal17);
            	    adaptor.addChild(root_0, string_literal17_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:15: ( ID | '#' ID )
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==ID) ) {
            	        alt5=1;
            	    }
            	    else if ( (LA5_0==18) ) {
            	        alt5=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 5, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:16: ID
            	            {
            	            ID18=(Token)match(input,ID,FOLLOW_ID_in_aspectName444); 
            	            ID18_tree = (Object)adaptor.create(ID18);
            	            adaptor.addChild(root_0, ID18_tree);


            	            }
            	            break;
            	        case 2 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\AstcToString.g:247:21: '#' ID
            	            {
            	            char_literal19=(Token)match(input,18,FOLLOW_18_in_aspectName448); 
            	            char_literal19_tree = (Object)adaptor.create(char_literal19);
            	            adaptor.addChild(root_0, char_literal19_tree);

            	            ID20=(Token)match(input,ID,FOLLOW_ID_in_aspectName450); 
            	            ID20_tree = (Object)adaptor.create(ID20);
            	            adaptor.addChild(root_0, ID20_tree);


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
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
    public static final BitSet FOLLOW_TOSTRING_DCL_in_top240 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_toString__in_top243 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_toString_256 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_aspectName_in_toString_259 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_toString_261 = new BitSet(new long[]{0x0000000000008602L});
    public static final BitSet FOLLOW_StringLiteral_in_toString_265 = new BitSet(new long[]{0x0000000000008602L});
    public static final BitSet FOLLOW_field_in_toString_271 = new BitSet(new long[]{0x000000000000C602L});
    public static final BitSet FOLLOW_14_in_toString_273 = new BitSet(new long[]{0x0000000000008602L});
    public static final BitSet FOLLOW_RawJava_in_toString_278 = new BitSet(new long[]{0x000000000000C602L});
    public static final BitSet FOLLOW_14_in_toString_280 = new BitSet(new long[]{0x0000000000008602L});
    public static final BitSet FOLLOW_15_in_field375 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_field378 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_field381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_aspectName437 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_17_in_aspectName441 = new BitSet(new long[]{0x0000000000040080L});
    public static final BitSet FOLLOW_ID_in_aspectName444 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_18_in_aspectName448 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ID_in_aspectName450 = new BitSet(new long[]{0x0000000000020002L});

}