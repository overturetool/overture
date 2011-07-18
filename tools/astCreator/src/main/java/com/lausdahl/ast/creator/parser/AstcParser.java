// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-07-18 12:11:34

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "COLON", "AST", "TOKENS", "ASPECT_DCL", "FIELD_DCL", "QUOTE", "COMMENT", "ID", "JAVANAME", "WS", "ESC_SEQ", "NormalChar", "SpecialChar", "';'", "'%'", "'|'", "'#'", "'{'", "'}'", "'['", "']'", "'('", "')'", "'?'", "'*'", "'**'", "'+'", "'||'", "'&&'"
    };
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
    public static final int T__19=19;
    public static final int T__30=30;
    public static final int QUOTE=10;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int ESC_SEQ=15;
    public static final int WS=14;
    public static final int T__33=33;
    public static final int T__18=18;
    public static final int JAVANAME=13;
    public static final int SpecialChar=17;
    public static final int ASSIGN=4;
    public static final int AST=6;
    public static final int COMMENT=11;
    public static final int FIELD_DCL=9;

    // delegates
    // delegators


        public AstcParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public AstcParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return AstcParser.tokenNames; }
    public String getGrammarFileName() { return "C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g"; }


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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:217:1: root : toks ast aspectdcl ;
    public final AstcParser.root_return root() throws RecognitionException {
        AstcParser.root_return retval = new AstcParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        AstcParser.toks_return toks1 = null;

        AstcParser.ast_return ast2 = null;

        AstcParser.aspectdcl_return aspectdcl3 = null;



        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:218:3: ( toks ast aspectdcl )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:218:5: toks ast aspectdcl
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_toks_in_root397);
            toks1=toks();

            state._fsp--;

            adaptor.addChild(root_0, toks1.getTree());
            pushFollow(FOLLOW_ast_in_root399);
            ast2=ast();

            state._fsp--;

            adaptor.addChild(root_0, ast2.getTree());
            pushFollow(FOLLOW_aspectdcl_in_root401);
            aspectdcl3=aspectdcl();

            state._fsp--;

            adaptor.addChild(root_0, aspectdcl3.getTree());

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

    public static class ast_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ast"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:1: ast : AST ( ( production )* ) ;
    public final AstcParser.ast_return ast() throws RecognitionException {
        AstcParser.ast_return retval = new AstcParser.ast_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AST4=null;
        AstcParser.production_return production5 = null;


        Object AST4_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:3: ( AST ( ( production )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:5: AST ( ( production )* )
            {
            root_0 = (Object)adaptor.nil();

            AST4=(Token)match(input,AST,FOLLOW_AST_in_ast419); 
            AST4_tree = (Object)adaptor.create(AST4);
            root_0 = (Object)adaptor.becomeRoot(AST4_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:10: ( ( production )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:11: ( production )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:11: ( production )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||LA1_0==21) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:222:12: production
            	    {
            	    pushFollow(FOLLOW_production_in_ast424);
            	    production5=production();

            	    state._fsp--;

            	    adaptor.addChild(root_0, production5.getTree());

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


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
    // $ANTLR end "ast"

    public static class toks_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "toks"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:1: toks : TOKENS ( ( token )* ) ;
    public final AstcParser.toks_return toks() throws RecognitionException {
        AstcParser.toks_return retval = new AstcParser.toks_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOKENS6=null;
        AstcParser.token_return token7 = null;


        Object TOKENS6_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:3: ( TOKENS ( ( token )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:5: TOKENS ( ( token )* )
            {
            root_0 = (Object)adaptor.nil();

            TOKENS6=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_toks442); 
            TOKENS6_tree = (Object)adaptor.create(TOKENS6);
            root_0 = (Object)adaptor.becomeRoot(TOKENS6_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:13: ( ( token )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:14: ( token )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:14: ( token )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:15: token
            	    {
            	    pushFollow(FOLLOW_token_in_toks447);
            	    token7=token();

            	    state._fsp--;

            	    adaptor.addChild(root_0, token7.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


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
    // $ANTLR end "toks"

    public static class aspectdcl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectdcl"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:229:1: aspectdcl : ASPECT_DCL ( aspectdcla ';' )* ;
    public final AstcParser.aspectdcl_return aspectdcl() throws RecognitionException {
        AstcParser.aspectdcl_return retval = new AstcParser.aspectdcl_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASPECT_DCL8=null;
        Token char_literal10=null;
        AstcParser.aspectdcla_return aspectdcla9 = null;


        Object ASPECT_DCL8_tree=null;
        Object char_literal10_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:230:3: ( ASPECT_DCL ( aspectdcla ';' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:230:5: ASPECT_DCL ( aspectdcla ';' )*
            {
            root_0 = (Object)adaptor.nil();

            ASPECT_DCL8=(Token)match(input,ASPECT_DCL,FOLLOW_ASPECT_DCL_in_aspectdcl465); 
            ASPECT_DCL8_tree = (Object)adaptor.create(ASPECT_DCL8);
            root_0 = (Object)adaptor.becomeRoot(ASPECT_DCL8_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:230:17: ( aspectdcla ';' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==19) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:230:18: aspectdcla ';'
            	    {
            	    pushFollow(FOLLOW_aspectdcla_in_aspectdcl469);
            	    aspectdcla9=aspectdcla();

            	    state._fsp--;

            	    adaptor.addChild(root_0, aspectdcla9.getTree());
            	    char_literal10=(Token)match(input,18,FOLLOW_18_in_aspectdcl471); 

            	    }
            	    break;

            	default :
            	    break loop3;
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
    // $ANTLR end "aspectdcl"

    public static class aspectdcla_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectdcla"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:1: aspectdcla : '%' dd ;
    public final AstcParser.aspectdcla_return aspectdcla() throws RecognitionException {
        AstcParser.aspectdcla_return retval = new AstcParser.aspectdcla_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal11=null;
        AstcParser.dd_return dd12 = null;


        Object char_literal11_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:234:3: ( '%' dd )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:234:5: '%' dd
            {
            root_0 = (Object)adaptor.nil();

            char_literal11=(Token)match(input,19,FOLLOW_19_in_aspectdcla489); 
            char_literal11_tree = (Object)adaptor.create(char_literal11);
            root_0 = (Object)adaptor.becomeRoot(char_literal11_tree, root_0);

            pushFollow(FOLLOW_dd_in_aspectdcla492);
            dd12=dd();

            state._fsp--;

            adaptor.addChild(root_0, dd12.getTree());

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
    // $ANTLR end "aspectdcla"

    public static class dd_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "dd"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:1: dd : aspectName ASSIGN ( definitions )* -> ^( ID[\"ASPECT\"] aspectName ( definitions )* ) ;
    public final AstcParser.dd_return dd() throws RecognitionException {
        AstcParser.dd_return retval = new AstcParser.dd_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN14=null;
        AstcParser.aspectName_return aspectName13 = null;

        AstcParser.definitions_return definitions15 = null;


        Object ASSIGN14_tree=null;
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_aspectName=new RewriteRuleSubtreeStream(adaptor,"rule aspectName");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:3: ( aspectName ASSIGN ( definitions )* -> ^( ID[\"ASPECT\"] aspectName ( definitions )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:4: aspectName ASSIGN ( definitions )*
            {
            pushFollow(FOLLOW_aspectName_in_dd508);
            aspectName13=aspectName();

            state._fsp--;

            stream_aspectName.add(aspectName13.getTree());
            ASSIGN14=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_dd510);  
            stream_ASSIGN.add(ASSIGN14);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:22: ( definitions )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=ID && LA4_0<=JAVANAME)||LA4_0==24||LA4_0==26) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:23: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_dd513);
            	    definitions15=definitions();

            	    state._fsp--;

            	    stream_definitions.add(definitions15.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);



            // AST REWRITE
            // elements: definitions, aspectName
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 238:37: -> ^( ID[\"ASPECT\"] aspectName ( definitions )* )
            {
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:40: ^( ID[\"ASPECT\"] aspectName ( definitions )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "ASPECT"), root_1);

                adaptor.addChild(root_1, stream_aspectName.nextTree());
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:66: ( definitions )*
                while ( stream_definitions.hasNext() ) {
                    adaptor.addChild(root_1, stream_definitions.nextTree());

                }
                stream_definitions.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
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
    // $ANTLR end "dd"

    public static class aspectName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectName"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:241:1: aspectName : ID ( '->' name )* ;
    public final AstcParser.aspectName_return aspectName() throws RecognitionException {
        AstcParser.aspectName_return retval = new AstcParser.aspectName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID16=null;
        Token string_literal17=null;
        AstcParser.name_return name18 = null;


        Object ID16_tree=null;
        Object string_literal17_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:3: ( ID ( '->' name )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:5: ID ( '->' name )*
            {
            root_0 = (Object)adaptor.nil();

            ID16=(Token)match(input,ID,FOLLOW_ID_in_aspectName543); 
            ID16_tree = (Object)adaptor.create(ID16);
            root_0 = (Object)adaptor.becomeRoot(ID16_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:9: ( '->' name )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==FIELD_DCL) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:10: '->' name
            	    {
            	    string_literal17=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_aspectName547); 
            	    string_literal17_tree = (Object)adaptor.create(string_literal17);
            	    adaptor.addChild(root_0, string_literal17_tree);

            	    pushFollow(FOLLOW_name_in_aspectName549);
            	    name18=name();

            	    state._fsp--;

            	    adaptor.addChild(root_0, name18.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
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

    public static class production_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "production"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:246:1: production : name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) ;
    public final AstcParser.production_return production() throws RecognitionException {
        AstcParser.production_return retval = new AstcParser.production_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN21=null;
        Token char_literal23=null;
        Token char_literal25=null;
        AstcParser.name_return name19 = null;

        AstcParser.productionfields_return productionfields20 = null;

        AstcParser.alternative_return alternative22 = null;

        AstcParser.alternative_return alternative24 = null;


        Object ASSIGN21_tree=null;
        Object char_literal23_tree=null;
        Object char_literal25_tree=null;
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_18=new RewriteRuleTokenStream(adaptor,"token 18");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_productionfields=new RewriteRuleSubtreeStream(adaptor,"rule productionfields");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:3: ( name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:5: name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';'
            {
            pushFollow(FOLLOW_name_in_production565);
            name19=name();

            state._fsp--;

            stream_name.add(name19.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:10: ( productionfields )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==22) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:10: productionfields
                    {
                    pushFollow(FOLLOW_productionfields_in_production567);
                    productionfields20=productionfields();

                    state._fsp--;

                    stream_productionfields.add(productionfields20.getTree());

                    }
                    break;

            }

            ASSIGN21=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_production570);  
            stream_ASSIGN.add(ASSIGN21);

            pushFollow(FOLLOW_alternative_in_production572);
            alternative22=alternative();

            state._fsp--;

            stream_alternative.add(alternative22.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:47: ( '|' alternative )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==20) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:48: '|' alternative
            	    {
            	    char_literal23=(Token)match(input,20,FOLLOW_20_in_production575);  
            	    stream_20.add(char_literal23);

            	    pushFollow(FOLLOW_alternative_in_production577);
            	    alternative24=alternative();

            	    state._fsp--;

            	    stream_alternative.add(alternative24.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            char_literal25=(Token)match(input,18,FOLLOW_18_in_production581);  
            stream_18.add(char_literal25);



            // AST REWRITE
            // elements: productionfields, alternative, name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 247:70: -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
            {
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:73: ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "P"), root_1);

                adaptor.addChild(root_1, stream_name.nextTree());
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:88: ( productionfields )?
                if ( stream_productionfields.hasNext() ) {
                    adaptor.addChild(root_1, stream_productionfields.nextTree());

                }
                stream_productionfields.reset();
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:106: ( alternative )*
                while ( stream_alternative.hasNext() ) {
                    adaptor.addChild(root_1, stream_alternative.nextTree());

                }
                stream_alternative.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
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
    // $ANTLR end "production"

    public static class name_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "name"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:250:1: name : ( ID | '#' ID -> ^( '#' ID ) );
    public final AstcParser.name_return name() throws RecognitionException {
        AstcParser.name_return retval = new AstcParser.name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID26=null;
        Token char_literal27=null;
        Token ID28=null;

        Object ID26_tree=null;
        Object char_literal27_tree=null;
        Object ID28_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:3: ( ID | '#' ID -> ^( '#' ID ) )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ID) ) {
                alt8=1;
            }
            else if ( (LA8_0==21) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:5: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID26=(Token)match(input,ID,FOLLOW_ID_in_name615); 
                    ID26_tree = (Object)adaptor.create(ID26);
                    root_0 = (Object)adaptor.becomeRoot(ID26_tree, root_0);


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:252:5: '#' ID
                    {
                    char_literal27=(Token)match(input,21,FOLLOW_21_in_name623);  
                    stream_21.add(char_literal27);

                    ID28=(Token)match(input,ID,FOLLOW_ID_in_name625);  
                    stream_ID.add(ID28);



                    // AST REWRITE
                    // elements: ID, 21
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 252:11: -> ^( '#' ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:252:13: ^( '#' ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_21.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

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
    // $ANTLR end "name"

    public static class productionfields_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "productionfields"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:257:1: productionfields : '{' FIELD_DCL ( productionfield )* '}' ;
    public final AstcParser.productionfields_return productionfields() throws RecognitionException {
        AstcParser.productionfields_return retval = new AstcParser.productionfields_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal29=null;
        Token FIELD_DCL30=null;
        Token char_literal32=null;
        AstcParser.productionfield_return productionfield31 = null;


        Object char_literal29_tree=null;
        Object FIELD_DCL30_tree=null;
        Object char_literal32_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:258:3: ( '{' FIELD_DCL ( productionfield )* '}' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:258:5: '{' FIELD_DCL ( productionfield )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal29=(Token)match(input,22,FOLLOW_22_in_productionfields650); 
            FIELD_DCL30=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_productionfields653); 
            FIELD_DCL30_tree = (Object)adaptor.create(FIELD_DCL30);
            root_0 = (Object)adaptor.becomeRoot(FIELD_DCL30_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:258:21: ( productionfield )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ID) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:258:21: productionfield
            	    {
            	    pushFollow(FOLLOW_productionfield_in_productionfields656);
            	    productionfield31=productionfield();

            	    state._fsp--;

            	    adaptor.addChild(root_0, productionfield31.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            char_literal32=(Token)match(input,23,FOLLOW_23_in_productionfields659); 

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
    // $ANTLR end "productionfields"

    public static class productionfield_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "productionfield"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:1: productionfield : ID ASSIGN QUOTE stringLiteral QUOTE ;
    public final AstcParser.productionfield_return productionfield() throws RecognitionException {
        AstcParser.productionfield_return retval = new AstcParser.productionfield_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID33=null;
        Token ASSIGN34=null;
        Token QUOTE35=null;
        Token QUOTE37=null;
        AstcParser.stringLiteral_return stringLiteral36 = null;


        Object ID33_tree=null;
        Object ASSIGN34_tree=null;
        Object QUOTE35_tree=null;
        Object QUOTE37_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:262:3: ( ID ASSIGN QUOTE stringLiteral QUOTE )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:262:5: ID ASSIGN QUOTE stringLiteral QUOTE
            {
            root_0 = (Object)adaptor.nil();

            ID33=(Token)match(input,ID,FOLLOW_ID_in_productionfield675); 
            ID33_tree = (Object)adaptor.create(ID33);
            root_0 = (Object)adaptor.becomeRoot(ID33_tree, root_0);

            ASSIGN34=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_productionfield678); 
            QUOTE35=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield681); 
            pushFollow(FOLLOW_stringLiteral_in_productionfield684);
            stringLiteral36=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral36.getTree());
            QUOTE37=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield686); 

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
    // $ANTLR end "productionfield"

    public static class alternative_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alternative"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:265:1: alternative : ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) );
    public final AstcParser.alternative_return alternative() throws RecognitionException {
        AstcParser.alternative_return retval = new AstcParser.alternative_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal38=null;
        Token ID39=null;
        Token char_literal40=null;
        Token char_literal42=null;
        Token ID43=null;
        AstcParser.definitions_return definitions41 = null;


        Object char_literal38_tree=null;
        Object ID39_tree=null;
        Object char_literal40_tree=null;
        Object char_literal42_tree=null;
        Object ID43_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_22=new RewriteRuleTokenStream(adaptor,"token 22");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:3: ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( ((LA12_0>=ID && LA12_0<=JAVANAME)||LA12_0==18||LA12_0==20||LA12_0==22||LA12_0==24||LA12_0==26) ) {
                alt12=1;
            }
            else if ( (LA12_0==21) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:5: ( '{' ID '}' )? ( definitions )*
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:5: ( '{' ID '}' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==22) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:6: '{' ID '}'
                            {
                            char_literal38=(Token)match(input,22,FOLLOW_22_in_alternative702);  
                            stream_22.add(char_literal38);

                            ID39=(Token)match(input,ID,FOLLOW_ID_in_alternative704);  
                            stream_ID.add(ID39);

                            char_literal40=(Token)match(input,23,FOLLOW_23_in_alternative706);  
                            stream_23.add(char_literal40);


                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:19: ( definitions )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0>=ID && LA11_0<=JAVANAME)||LA11_0==24||LA11_0==26) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:20: definitions
                    	    {
                    	    pushFollow(FOLLOW_definitions_in_alternative711);
                    	    definitions41=definitions();

                    	    state._fsp--;

                    	    stream_definitions.add(definitions41.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: definitions, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 266:34: -> ^( ID ( definitions )* )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:37: ^( ID ( definitions )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:42: ( definitions )*
                        while ( stream_definitions.hasNext() ) {
                            adaptor.addChild(root_1, stream_definitions.nextTree());

                        }
                        stream_definitions.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:267:5: '#' ID
                    {
                    char_literal42=(Token)match(input,21,FOLLOW_21_in_alternative730);  
                    stream_21.add(char_literal42);

                    ID43=(Token)match(input,ID,FOLLOW_ID_in_alternative732);  
                    stream_ID.add(ID43);



                    // AST REWRITE
                    // elements: ID, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 267:12: -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:267:15: ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "ALTERNATIVE_SUB_ROOT"), root_1);

                        adaptor.addChild(root_1, stream_ID.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

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
    // $ANTLR end "alternative"

    public static class definitions_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "definitions"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:270:1: definitions : ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )? ;
    public final AstcParser.definitions_return definitions() throws RecognitionException {
        AstcParser.definitions_return retval = new AstcParser.definitions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal44=null;
        Token ID45=null;
        Token char_literal46=null;
        Token char_literal47=null;
        Token char_literal48=null;
        Token ID49=null;
        Token char_literal50=null;
        Token char_literal51=null;
        Token set52=null;
        AstcParser.repeat_return repeat53 = null;


        Object char_literal44_tree=null;
        Object ID45_tree=null;
        Object char_literal46_tree=null;
        Object char_literal47_tree=null;
        Object char_literal48_tree=null;
        Object ID49_tree=null;
        Object char_literal50_tree=null;
        Object char_literal51_tree=null;
        Object set52_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:3: ( ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )? )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:5: ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )?
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:5: ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=ID && LA14_0<=JAVANAME)||LA14_0==24) ) {
                alt14=1;
            }
            else if ( (LA14_0==26) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:6: ( '[' ID ']' ':' )?
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:6: ( '[' ID ']' ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==24) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:7: '[' ID ']' ':'
                            {
                            char_literal44=(Token)match(input,24,FOLLOW_24_in_definitions758); 
                            char_literal44_tree = (Object)adaptor.create(char_literal44);
                            adaptor.addChild(root_0, char_literal44_tree);

                            ID45=(Token)match(input,ID,FOLLOW_ID_in_definitions760); 
                            ID45_tree = (Object)adaptor.create(ID45);
                            adaptor.addChild(root_0, ID45_tree);

                            char_literal46=(Token)match(input,25,FOLLOW_25_in_definitions762); 
                            char_literal47=(Token)match(input,COLON,FOLLOW_COLON_in_definitions765); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:27: ( '(' ID ')' ':' )
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:27: ( '(' ID ')' ':' )
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:28: '(' ID ')' ':'
                    {
                    char_literal48=(Token)match(input,26,FOLLOW_26_in_definitions772); 
                    char_literal48_tree = (Object)adaptor.create(char_literal48);
                    adaptor.addChild(root_0, char_literal48_tree);

                    ID49=(Token)match(input,ID,FOLLOW_ID_in_definitions774); 
                    ID49_tree = (Object)adaptor.create(ID49);
                    adaptor.addChild(root_0, ID49_tree);

                    char_literal50=(Token)match(input,27,FOLLOW_27_in_definitions776); 
                    char_literal51=(Token)match(input,COLON,FOLLOW_COLON_in_definitions779); 

                    }


                    }
                    break;

            }

            set52=(Token)input.LT(1);
            set52=(Token)input.LT(1);
            if ( (input.LA(1)>=ID && input.LA(1)<=JAVANAME) ) {
                input.consume();
                root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set52), root_0);
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:63: ( repeat )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=28 && LA15_0<=31)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:64: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_definitions793);
                    repeat53=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat53.getTree());

                    }
                    break;

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
    // $ANTLR end "definitions"

    public static class repeat_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "repeat"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:1: repeat : ( '?' | '*' | '**' | '+' );
    public final AstcParser.repeat_return repeat() throws RecognitionException {
        AstcParser.repeat_return retval = new AstcParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set54=null;

        Object set54_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:280:3: ( '?' | '*' | '**' | '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            {
            root_0 = (Object)adaptor.nil();

            set54=(Token)input.LT(1);
            if ( (input.LA(1)>=28 && input.LA(1)<=31) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set54));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
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
    // $ANTLR end "repeat"

    public static class token_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "token"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:287:1: token : ID ASSIGN QUOTE stringLiteral QUOTE ';' ;
    public final AstcParser.token_return token() throws RecognitionException {
        AstcParser.token_return retval = new AstcParser.token_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID55=null;
        Token ASSIGN56=null;
        Token QUOTE57=null;
        Token QUOTE59=null;
        Token char_literal60=null;
        AstcParser.stringLiteral_return stringLiteral58 = null;


        Object ID55_tree=null;
        Object ASSIGN56_tree=null;
        Object QUOTE57_tree=null;
        Object QUOTE59_tree=null;
        Object char_literal60_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:288:3: ( ID ASSIGN QUOTE stringLiteral QUOTE ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:288:5: ID ASSIGN QUOTE stringLiteral QUOTE ';'
            {
            root_0 = (Object)adaptor.nil();

            ID55=(Token)match(input,ID,FOLLOW_ID_in_token851); 
            ID55_tree = (Object)adaptor.create(ID55);
            root_0 = (Object)adaptor.becomeRoot(ID55_tree, root_0);

            ASSIGN56=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_token854); 
            QUOTE57=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token857); 
            pushFollow(FOLLOW_stringLiteral_in_token860);
            stringLiteral58=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral58.getTree());
            QUOTE59=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token862); 
            char_literal60=(Token)match(input,18,FOLLOW_18_in_token865); 

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
    // $ANTLR end "token"

    public static class stringLiteral_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "stringLiteral"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:291:1: stringLiteral : ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* ;
    public final AstcParser.stringLiteral_return stringLiteral() throws RecognitionException {
        AstcParser.stringLiteral_return retval = new AstcParser.stringLiteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID61=null;
        Token NormalChar62=null;
        Token char_literal63=null;
        Token string_literal64=null;
        Token string_literal65=null;
        Token char_literal66=null;
        Token JAVANAME67=null;

        Object ID61_tree=null;
        Object NormalChar62_tree=null;
        Object char_literal63_tree=null;
        Object string_literal64_tree=null;
        Object string_literal65_tree=null;
        Object char_literal66_tree=null;
        Object JAVANAME67_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:5: ( ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            loop16:
            do {
                int alt16=8;
                switch ( input.LA(1) ) {
                case ID:
                    {
                    alt16=1;
                    }
                    break;
                case NormalChar:
                    {
                    alt16=2;
                    }
                    break;
                case 31:
                    {
                    alt16=3;
                    }
                    break;
                case 32:
                    {
                    alt16=4;
                    }
                    break;
                case 33:
                    {
                    alt16=5;
                    }
                    break;
                case COLON:
                    {
                    alt16=6;
                    }
                    break;
                case JAVANAME:
                    {
                    alt16=7;
                    }
                    break;

                }

                switch (alt16) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:12: ID
            	    {
            	    ID61=(Token)match(input,ID,FOLLOW_ID_in_stringLiteral888); 
            	    ID61_tree = (Object)adaptor.create(ID61);
            	    adaptor.addChild(root_0, ID61_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:17: NormalChar
            	    {
            	    NormalChar62=(Token)match(input,NormalChar,FOLLOW_NormalChar_in_stringLiteral892); 
            	    NormalChar62_tree = (Object)adaptor.create(NormalChar62);
            	    adaptor.addChild(root_0, NormalChar62_tree);


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:30: '+'
            	    {
            	    char_literal63=(Token)match(input,31,FOLLOW_31_in_stringLiteral896); 
            	    char_literal63_tree = (Object)adaptor.create(char_literal63);
            	    adaptor.addChild(root_0, char_literal63_tree);


            	    }
            	    break;
            	case 4 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:34: '||'
            	    {
            	    string_literal64=(Token)match(input,32,FOLLOW_32_in_stringLiteral898); 
            	    string_literal64_tree = (Object)adaptor.create(string_literal64);
            	    adaptor.addChild(root_0, string_literal64_tree);


            	    }
            	    break;
            	case 5 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:39: '&&'
            	    {
            	    string_literal65=(Token)match(input,33,FOLLOW_33_in_stringLiteral900); 
            	    string_literal65_tree = (Object)adaptor.create(string_literal65);
            	    adaptor.addChild(root_0, string_literal65_tree);


            	    }
            	    break;
            	case 6 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:44: ( ':' )
            	    {
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:44: ( ':' )
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:45: ':'
            	    {
            	    char_literal66=(Token)match(input,COLON,FOLLOW_COLON_in_stringLiteral903); 
            	    char_literal66_tree = (Object)adaptor.create(char_literal66);
            	    adaptor.addChild(root_0, char_literal66_tree);


            	    }


            	    }
            	    break;
            	case 7 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:292:52: JAVANAME
            	    {
            	    JAVANAME67=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_stringLiteral908); 
            	    JAVANAME67_tree = (Object)adaptor.create(JAVANAME67);
            	    adaptor.addChild(root_0, JAVANAME67_tree);


            	    }
            	    break;

            	default :
            	    break loop16;
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
    // $ANTLR end "stringLiteral"

    // Delegated rules


 

    public static final BitSet FOLLOW_toks_in_root397 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ast_in_root399 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_aspectdcl_in_root401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_ast419 = new BitSet(new long[]{0x0000000000201002L});
    public static final BitSet FOLLOW_production_in_ast424 = new BitSet(new long[]{0x0000000000201002L});
    public static final BitSet FOLLOW_TOKENS_in_toks442 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_token_in_toks447 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ASPECT_DCL_in_aspectdcl465 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_aspectdcla_in_aspectdcl469 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_aspectdcl471 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_aspectdcla489 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_dd_in_aspectdcla492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aspectName_in_dd508 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_dd510 = new BitSet(new long[]{0x0000000005003002L});
    public static final BitSet FOLLOW_definitions_in_dd513 = new BitSet(new long[]{0x0000000005003002L});
    public static final BitSet FOLLOW_ID_in_aspectName543 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_FIELD_DCL_in_aspectName547 = new BitSet(new long[]{0x0000000000201000L});
    public static final BitSet FOLLOW_name_in_aspectName549 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_name_in_production565 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_productionfields_in_production567 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_production570 = new BitSet(new long[]{0x0000000005743000L});
    public static final BitSet FOLLOW_alternative_in_production572 = new BitSet(new long[]{0x0000000000140000L});
    public static final BitSet FOLLOW_20_in_production575 = new BitSet(new long[]{0x0000000005743000L});
    public static final BitSet FOLLOW_alternative_in_production577 = new BitSet(new long[]{0x0000000000140000L});
    public static final BitSet FOLLOW_18_in_production581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_name623 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_name625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_productionfields650 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_FIELD_DCL_in_productionfields653 = new BitSet(new long[]{0x0000000000801000L});
    public static final BitSet FOLLOW_productionfield_in_productionfields656 = new BitSet(new long[]{0x0000000000801000L});
    public static final BitSet FOLLOW_23_in_productionfields659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_productionfield675 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_productionfield678 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield681 = new BitSet(new long[]{0x0000000380013420L});
    public static final BitSet FOLLOW_stringLiteral_in_productionfield684 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_alternative702 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative704 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_alternative706 = new BitSet(new long[]{0x0000000005003002L});
    public static final BitSet FOLLOW_definitions_in_alternative711 = new BitSet(new long[]{0x0000000005003002L});
    public static final BitSet FOLLOW_21_in_alternative730 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_definitions758 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions760 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_definitions762 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions765 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_26_in_definitions772 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions774 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_definitions776 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions779 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_set_in_definitions784 = new BitSet(new long[]{0x00000000F0000002L});
    public static final BitSet FOLLOW_repeat_in_definitions793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_repeat0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_token851 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_token854 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token857 = new BitSet(new long[]{0x0000000380013420L});
    public static final BitSet FOLLOW_stringLiteral_in_token860 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token862 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_token865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stringLiteral888 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_NormalChar_in_stringLiteral892 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_31_in_stringLiteral896 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_32_in_stringLiteral898 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_33_in_stringLiteral900 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_COLON_in_stringLiteral903 = new BitSet(new long[]{0x0000000380013022L});
    public static final BitSet FOLLOW_JAVANAME_in_stringLiteral908 = new BitSet(new long[]{0x0000000380013022L});

}