// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-07-07 09:35:39

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "COLON", "AST", "TOKENS", "ASPECT_DCL", "FIELD_DCL", "QUOTE", "COMMENT", "ID", "JAVANAME", "WS", "ESC_SEQ", "NormalChar", "SpecialChar", "';'", "'%'", "'#'", "'|'", "'{'", "'}'", "'['", "']'", "'?'", "'*'", "'+'", "'||'", "'&&'"
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
    public static final int ESC_SEQ=15;
    public static final int WS=14;
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:216:1: root : toks ast aspectdcl ;
    public final AstcParser.root_return root() throws RecognitionException {
        AstcParser.root_return retval = new AstcParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        AstcParser.toks_return toks1 = null;

        AstcParser.ast_return ast2 = null;

        AstcParser.aspectdcl_return aspectdcl3 = null;



        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:217:3: ( toks ast aspectdcl )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:217:5: toks ast aspectdcl
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:220:1: ast : AST ( ( production )* ) ;
    public final AstcParser.ast_return ast() throws RecognitionException {
        AstcParser.ast_return retval = new AstcParser.ast_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AST4=null;
        AstcParser.production_return production5 = null;


        Object AST4_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:3: ( AST ( ( production )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:5: AST ( ( production )* )
            {
            root_0 = (Object)adaptor.nil();

            AST4=(Token)match(input,AST,FOLLOW_AST_in_ast419); 
            AST4_tree = (Object)adaptor.create(AST4);
            root_0 = (Object)adaptor.becomeRoot(AST4_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:10: ( ( production )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:11: ( production )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:11: ( production )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||LA1_0==20) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:221:12: production
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:224:1: toks : TOKENS ( ( token )* ) ;
    public final AstcParser.toks_return toks() throws RecognitionException {
        AstcParser.toks_return retval = new AstcParser.toks_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOKENS6=null;
        AstcParser.token_return token7 = null;


        Object TOKENS6_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:3: ( TOKENS ( ( token )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:5: TOKENS ( ( token )* )
            {
            root_0 = (Object)adaptor.nil();

            TOKENS6=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_toks442); 
            TOKENS6_tree = (Object)adaptor.create(TOKENS6);
            root_0 = (Object)adaptor.becomeRoot(TOKENS6_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:13: ( ( token )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:14: ( token )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:14: ( token )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:225:15: token
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:228:1: aspectdcl : ASPECT_DCL ( aspectdcla ';' )* ;
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:229:3: ( ASPECT_DCL ( aspectdcla ';' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:229:5: ASPECT_DCL ( aspectdcla ';' )*
            {
            root_0 = (Object)adaptor.nil();

            ASPECT_DCL8=(Token)match(input,ASPECT_DCL,FOLLOW_ASPECT_DCL_in_aspectdcl465); 
            ASPECT_DCL8_tree = (Object)adaptor.create(ASPECT_DCL8);
            root_0 = (Object)adaptor.becomeRoot(ASPECT_DCL8_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:229:17: ( aspectdcla ';' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==19) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:229:18: aspectdcla ';'
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:232:1: aspectdcla : '%' aspectName ASSIGN ( ( definitions )* ) ;
    public final AstcParser.aspectdcla_return aspectdcla() throws RecognitionException {
        AstcParser.aspectdcla_return retval = new AstcParser.aspectdcla_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal11=null;
        Token ASSIGN13=null;
        AstcParser.aspectName_return aspectName12 = null;

        AstcParser.definitions_return definitions14 = null;


        Object char_literal11_tree=null;
        Object ASSIGN13_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:3: ( '%' aspectName ASSIGN ( ( definitions )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:5: '%' aspectName ASSIGN ( ( definitions )* )
            {
            root_0 = (Object)adaptor.nil();

            char_literal11=(Token)match(input,19,FOLLOW_19_in_aspectdcla489); 
            char_literal11_tree = (Object)adaptor.create(char_literal11);
            root_0 = (Object)adaptor.becomeRoot(char_literal11_tree, root_0);

            pushFollow(FOLLOW_aspectName_in_aspectdcla492);
            aspectName12=aspectName();

            state._fsp--;

            adaptor.addChild(root_0, aspectName12.getTree());
            ASSIGN13=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_aspectdcla494); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:29: ( ( definitions )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:30: ( definitions )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:30: ( definitions )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=ID && LA4_0<=JAVANAME)||LA4_0==24) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:233:31: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_aspectdcla499);
            	    definitions14=definitions();

            	    state._fsp--;

            	    adaptor.addChild(root_0, definitions14.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // $ANTLR end "aspectdcla"

    public static class aspectName_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectName"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:236:1: aspectName : ID ( '->' ( ID | '#' ID ) )* ;
    public final AstcParser.aspectName_return aspectName() throws RecognitionException {
        AstcParser.aspectName_return retval = new AstcParser.aspectName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID15=null;
        Token string_literal16=null;
        Token ID17=null;
        Token char_literal18=null;
        Token ID19=null;

        Object ID15_tree=null;
        Object string_literal16_tree=null;
        Object ID17_tree=null;
        Object char_literal18_tree=null;
        Object ID19_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:3: ( ID ( '->' ( ID | '#' ID ) )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:5: ID ( '->' ( ID | '#' ID ) )*
            {
            root_0 = (Object)adaptor.nil();

            ID15=(Token)match(input,ID,FOLLOW_ID_in_aspectName516); 
            ID15_tree = (Object)adaptor.create(ID15);
            root_0 = (Object)adaptor.becomeRoot(ID15_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:9: ( '->' ( ID | '#' ID ) )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==FIELD_DCL) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:10: '->' ( ID | '#' ID )
            	    {
            	    string_literal16=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_aspectName520); 
            	    string_literal16_tree = (Object)adaptor.create(string_literal16);
            	    adaptor.addChild(root_0, string_literal16_tree);

            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:15: ( ID | '#' ID )
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==ID) ) {
            	        alt5=1;
            	    }
            	    else if ( (LA5_0==20) ) {
            	        alt5=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 5, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:16: ID
            	            {
            	            ID17=(Token)match(input,ID,FOLLOW_ID_in_aspectName523); 
            	            ID17_tree = (Object)adaptor.create(ID17);
            	            adaptor.addChild(root_0, ID17_tree);


            	            }
            	            break;
            	        case 2 :
            	            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:237:21: '#' ID
            	            {
            	            char_literal18=(Token)match(input,20,FOLLOW_20_in_aspectName527); 
            	            char_literal18_tree = (Object)adaptor.create(char_literal18);
            	            adaptor.addChild(root_0, char_literal18_tree);

            	            ID19=(Token)match(input,ID,FOLLOW_ID_in_aspectName529); 
            	            ID19_tree = (Object)adaptor.create(ID19);
            	            adaptor.addChild(root_0, ID19_tree);


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

    public static class production_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "production"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:241:1: production : name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) ;
    public final AstcParser.production_return production() throws RecognitionException {
        AstcParser.production_return retval = new AstcParser.production_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN22=null;
        Token char_literal24=null;
        Token char_literal26=null;
        AstcParser.name_return name20 = null;

        AstcParser.productionfields_return productionfields21 = null;

        AstcParser.alternative_return alternative23 = null;

        AstcParser.alternative_return alternative25 = null;


        Object ASSIGN22_tree=null;
        Object char_literal24_tree=null;
        Object char_literal26_tree=null;
        RewriteRuleTokenStream stream_21=new RewriteRuleTokenStream(adaptor,"token 21");
        RewriteRuleTokenStream stream_18=new RewriteRuleTokenStream(adaptor,"token 18");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_productionfields=new RewriteRuleSubtreeStream(adaptor,"rule productionfields");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:3: ( name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:5: name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';'
            {
            pushFollow(FOLLOW_name_in_production546);
            name20=name();

            state._fsp--;

            stream_name.add(name20.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:10: ( productionfields )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==22) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:10: productionfields
                    {
                    pushFollow(FOLLOW_productionfields_in_production548);
                    productionfields21=productionfields();

                    state._fsp--;

                    stream_productionfields.add(productionfields21.getTree());

                    }
                    break;

            }

            ASSIGN22=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_production551);  
            stream_ASSIGN.add(ASSIGN22);

            pushFollow(FOLLOW_alternative_in_production553);
            alternative23=alternative();

            state._fsp--;

            stream_alternative.add(alternative23.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:47: ( '|' alternative )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==21) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:48: '|' alternative
            	    {
            	    char_literal24=(Token)match(input,21,FOLLOW_21_in_production556);  
            	    stream_21.add(char_literal24);

            	    pushFollow(FOLLOW_alternative_in_production558);
            	    alternative25=alternative();

            	    state._fsp--;

            	    stream_alternative.add(alternative25.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            char_literal26=(Token)match(input,18,FOLLOW_18_in_production562);  
            stream_18.add(char_literal26);



            // AST REWRITE
            // elements: alternative, productionfields, name
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 242:70: -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
            {
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:73: ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "P"), root_1);

                adaptor.addChild(root_1, stream_name.nextTree());
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:88: ( productionfields )?
                if ( stream_productionfields.hasNext() ) {
                    adaptor.addChild(root_1, stream_productionfields.nextTree());

                }
                stream_productionfields.reset();
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:106: ( alternative )*
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:245:1: name : ( ID | '#' ID -> ^( '#' ID ) );
    public final AstcParser.name_return name() throws RecognitionException {
        AstcParser.name_return retval = new AstcParser.name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID27=null;
        Token char_literal28=null;
        Token ID29=null;

        Object ID27_tree=null;
        Object char_literal28_tree=null;
        Object ID29_tree=null;
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:246:3: ( ID | '#' ID -> ^( '#' ID ) )
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
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:246:5: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID27=(Token)match(input,ID,FOLLOW_ID_in_name596); 
                    ID27_tree = (Object)adaptor.create(ID27);
                    root_0 = (Object)adaptor.becomeRoot(ID27_tree, root_0);


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:5: '#' ID
                    {
                    char_literal28=(Token)match(input,20,FOLLOW_20_in_name604);  
                    stream_20.add(char_literal28);

                    ID29=(Token)match(input,ID,FOLLOW_ID_in_name606);  
                    stream_ID.add(ID29);



                    // AST REWRITE
                    // elements: 20, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 247:11: -> ^( '#' ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:13: ^( '#' ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_20.nextNode(), root_1);

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:252:1: productionfields : '{' FIELD_DCL ( productionfield )* '}' ;
    public final AstcParser.productionfields_return productionfields() throws RecognitionException {
        AstcParser.productionfields_return retval = new AstcParser.productionfields_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal30=null;
        Token FIELD_DCL31=null;
        Token char_literal33=null;
        AstcParser.productionfield_return productionfield32 = null;


        Object char_literal30_tree=null;
        Object FIELD_DCL31_tree=null;
        Object char_literal33_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:253:3: ( '{' FIELD_DCL ( productionfield )* '}' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:253:5: '{' FIELD_DCL ( productionfield )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal30=(Token)match(input,22,FOLLOW_22_in_productionfields631); 
            FIELD_DCL31=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_productionfields634); 
            FIELD_DCL31_tree = (Object)adaptor.create(FIELD_DCL31);
            root_0 = (Object)adaptor.becomeRoot(FIELD_DCL31_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:253:21: ( productionfield )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==ID) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:253:21: productionfield
            	    {
            	    pushFollow(FOLLOW_productionfield_in_productionfields637);
            	    productionfield32=productionfield();

            	    state._fsp--;

            	    adaptor.addChild(root_0, productionfield32.getTree());

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            char_literal33=(Token)match(input,23,FOLLOW_23_in_productionfields640); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:256:1: productionfield : ID ASSIGN QUOTE stringLiteral QUOTE ;
    public final AstcParser.productionfield_return productionfield() throws RecognitionException {
        AstcParser.productionfield_return retval = new AstcParser.productionfield_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID34=null;
        Token ASSIGN35=null;
        Token QUOTE36=null;
        Token QUOTE38=null;
        AstcParser.stringLiteral_return stringLiteral37 = null;


        Object ID34_tree=null;
        Object ASSIGN35_tree=null;
        Object QUOTE36_tree=null;
        Object QUOTE38_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:257:3: ( ID ASSIGN QUOTE stringLiteral QUOTE )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:257:5: ID ASSIGN QUOTE stringLiteral QUOTE
            {
            root_0 = (Object)adaptor.nil();

            ID34=(Token)match(input,ID,FOLLOW_ID_in_productionfield656); 
            ID34_tree = (Object)adaptor.create(ID34);
            root_0 = (Object)adaptor.becomeRoot(ID34_tree, root_0);

            ASSIGN35=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_productionfield659); 
            QUOTE36=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield662); 
            pushFollow(FOLLOW_stringLiteral_in_productionfield665);
            stringLiteral37=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral37.getTree());
            QUOTE38=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield667); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:1: alternative : ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) );
    public final AstcParser.alternative_return alternative() throws RecognitionException {
        AstcParser.alternative_return retval = new AstcParser.alternative_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal39=null;
        Token ID40=null;
        Token char_literal41=null;
        Token char_literal43=null;
        Token ID44=null;
        AstcParser.definitions_return definitions42 = null;


        Object char_literal39_tree=null;
        Object ID40_tree=null;
        Object char_literal41_tree=null;
        Object char_literal43_tree=null;
        Object ID44_tree=null;
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_22=new RewriteRuleTokenStream(adaptor,"token 22");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:3: ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=ID && LA13_0<=JAVANAME)||LA13_0==18||(LA13_0>=21 && LA13_0<=22)||LA13_0==24) ) {
                alt13=1;
            }
            else if ( (LA13_0==20) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:5: ( '{' ID '}' )? ( definitions )*
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:5: ( '{' ID '}' )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==22) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:6: '{' ID '}'
                            {
                            char_literal39=(Token)match(input,22,FOLLOW_22_in_alternative683);  
                            stream_22.add(char_literal39);

                            ID40=(Token)match(input,ID,FOLLOW_ID_in_alternative685);  
                            stream_ID.add(ID40);

                            char_literal41=(Token)match(input,23,FOLLOW_23_in_alternative687);  
                            stream_23.add(char_literal41);


                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:19: ( definitions )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>=ID && LA12_0<=JAVANAME)||LA12_0==24) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:20: definitions
                    	    {
                    	    pushFollow(FOLLOW_definitions_in_alternative692);
                    	    definitions42=definitions();

                    	    state._fsp--;

                    	    stream_definitions.add(definitions42.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: ID, definitions
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 261:34: -> ^( ID ( definitions )* )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:37: ^( ID ( definitions )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:261:42: ( definitions )*
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
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:262:5: '#' ID
                    {
                    char_literal43=(Token)match(input,20,FOLLOW_20_in_alternative711);  
                    stream_20.add(char_literal43);

                    ID44=(Token)match(input,ID,FOLLOW_ID_in_alternative713);  
                    stream_ID.add(ID44);



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
                    // 262:12: -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:262:15: ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:265:1: definitions : ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )? ;
    public final AstcParser.definitions_return definitions() throws RecognitionException {
        AstcParser.definitions_return retval = new AstcParser.definitions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal45=null;
        Token ID46=null;
        Token char_literal47=null;
        Token char_literal48=null;
        Token set49=null;
        AstcParser.repeat_return repeat50 = null;


        Object char_literal45_tree=null;
        Object ID46_tree=null;
        Object char_literal47_tree=null;
        Object char_literal48_tree=null;
        Object set49_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:3: ( ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )? )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:5: ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )?
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:5: ( '[' ID ']' ':' )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==24) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:6: '[' ID ']' ':'
                    {
                    char_literal45=(Token)match(input,24,FOLLOW_24_in_definitions738); 
                    ID46=(Token)match(input,ID,FOLLOW_ID_in_definitions741); 
                    ID46_tree = (Object)adaptor.create(ID46);
                    adaptor.addChild(root_0, ID46_tree);

                    char_literal47=(Token)match(input,25,FOLLOW_25_in_definitions743); 
                    char_literal48=(Token)match(input,COLON,FOLLOW_COLON_in_definitions746); 

                    }
                    break;

            }

            set49=(Token)input.LT(1);
            set49=(Token)input.LT(1);
            if ( (input.LA(1)>=ID && input.LA(1)<=JAVANAME) ) {
                input.consume();
                root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set49), root_0);
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:42: ( repeat )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=26 && LA15_0<=28)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:266:43: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_definitions760);
                    repeat50=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat50.getTree());

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:273:1: repeat : ( '?' | '*' | '+' );
    public final AstcParser.repeat_return repeat() throws RecognitionException {
        AstcParser.repeat_return retval = new AstcParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set51=null;

        Object set51_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:274:3: ( '?' | '*' | '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            {
            root_0 = (Object)adaptor.nil();

            set51=(Token)input.LT(1);
            if ( (input.LA(1)>=26 && input.LA(1)<=28) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set51));
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:280:1: token : ID ASSIGN QUOTE stringLiteral QUOTE ';' ;
    public final AstcParser.token_return token() throws RecognitionException {
        AstcParser.token_return retval = new AstcParser.token_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID52=null;
        Token ASSIGN53=null;
        Token QUOTE54=null;
        Token QUOTE56=null;
        Token char_literal57=null;
        AstcParser.stringLiteral_return stringLiteral55 = null;


        Object ID52_tree=null;
        Object ASSIGN53_tree=null;
        Object QUOTE54_tree=null;
        Object QUOTE56_tree=null;
        Object char_literal57_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:281:3: ( ID ASSIGN QUOTE stringLiteral QUOTE ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:281:5: ID ASSIGN QUOTE stringLiteral QUOTE ';'
            {
            root_0 = (Object)adaptor.nil();

            ID52=(Token)match(input,ID,FOLLOW_ID_in_token809); 
            ID52_tree = (Object)adaptor.create(ID52);
            root_0 = (Object)adaptor.becomeRoot(ID52_tree, root_0);

            ASSIGN53=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_token812); 
            QUOTE54=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token815); 
            pushFollow(FOLLOW_stringLiteral_in_token818);
            stringLiteral55=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral55.getTree());
            QUOTE56=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token820); 
            char_literal57=(Token)match(input,18,FOLLOW_18_in_token823); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:1: stringLiteral : ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* ;
    public final AstcParser.stringLiteral_return stringLiteral() throws RecognitionException {
        AstcParser.stringLiteral_return retval = new AstcParser.stringLiteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID58=null;
        Token NormalChar59=null;
        Token char_literal60=null;
        Token string_literal61=null;
        Token string_literal62=null;
        Token char_literal63=null;
        Token JAVANAME64=null;

        Object ID58_tree=null;
        Object NormalChar59_tree=null;
        Object char_literal60_tree=null;
        Object string_literal61_tree=null;
        Object string_literal62_tree=null;
        Object char_literal63_tree=null;
        Object JAVANAME64_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:5: ( ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
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
                case 28:
                    {
                    alt16=3;
                    }
                    break;
                case 29:
                    {
                    alt16=4;
                    }
                    break;
                case 30:
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
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:12: ID
            	    {
            	    ID58=(Token)match(input,ID,FOLLOW_ID_in_stringLiteral846); 
            	    ID58_tree = (Object)adaptor.create(ID58);
            	    adaptor.addChild(root_0, ID58_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:17: NormalChar
            	    {
            	    NormalChar59=(Token)match(input,NormalChar,FOLLOW_NormalChar_in_stringLiteral850); 
            	    NormalChar59_tree = (Object)adaptor.create(NormalChar59);
            	    adaptor.addChild(root_0, NormalChar59_tree);


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:30: '+'
            	    {
            	    char_literal60=(Token)match(input,28,FOLLOW_28_in_stringLiteral854); 
            	    char_literal60_tree = (Object)adaptor.create(char_literal60);
            	    adaptor.addChild(root_0, char_literal60_tree);


            	    }
            	    break;
            	case 4 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:34: '||'
            	    {
            	    string_literal61=(Token)match(input,29,FOLLOW_29_in_stringLiteral856); 
            	    string_literal61_tree = (Object)adaptor.create(string_literal61);
            	    adaptor.addChild(root_0, string_literal61_tree);


            	    }
            	    break;
            	case 5 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:39: '&&'
            	    {
            	    string_literal62=(Token)match(input,30,FOLLOW_30_in_stringLiteral858); 
            	    string_literal62_tree = (Object)adaptor.create(string_literal62);
            	    adaptor.addChild(root_0, string_literal62_tree);


            	    }
            	    break;
            	case 6 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:44: ( ':' )
            	    {
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:44: ( ':' )
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:45: ':'
            	    {
            	    char_literal63=(Token)match(input,COLON,FOLLOW_COLON_in_stringLiteral861); 
            	    char_literal63_tree = (Object)adaptor.create(char_literal63);
            	    adaptor.addChild(root_0, char_literal63_tree);


            	    }


            	    }
            	    break;
            	case 7 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:285:52: JAVANAME
            	    {
            	    JAVANAME64=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_stringLiteral866); 
            	    JAVANAME64_tree = (Object)adaptor.create(JAVANAME64);
            	    adaptor.addChild(root_0, JAVANAME64_tree);


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
    public static final BitSet FOLLOW_AST_in_ast419 = new BitSet(new long[]{0x0000000000101002L});
    public static final BitSet FOLLOW_production_in_ast424 = new BitSet(new long[]{0x0000000000101002L});
    public static final BitSet FOLLOW_TOKENS_in_toks442 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_token_in_toks447 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ASPECT_DCL_in_aspectdcl465 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_aspectdcla_in_aspectdcl469 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_aspectdcl471 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_aspectdcla489 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_aspectName_in_aspectdcla492 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_aspectdcla494 = new BitSet(new long[]{0x0000000001003002L});
    public static final BitSet FOLLOW_definitions_in_aspectdcla499 = new BitSet(new long[]{0x0000000001003002L});
    public static final BitSet FOLLOW_ID_in_aspectName516 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_FIELD_DCL_in_aspectName520 = new BitSet(new long[]{0x0000000000101000L});
    public static final BitSet FOLLOW_ID_in_aspectName523 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_20_in_aspectName527 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_aspectName529 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_name_in_production546 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_productionfields_in_production548 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_production551 = new BitSet(new long[]{0x0000000001743000L});
    public static final BitSet FOLLOW_alternative_in_production553 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_21_in_production556 = new BitSet(new long[]{0x0000000001743000L});
    public static final BitSet FOLLOW_alternative_in_production558 = new BitSet(new long[]{0x0000000000240000L});
    public static final BitSet FOLLOW_18_in_production562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_name604 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_name606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_productionfields631 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_FIELD_DCL_in_productionfields634 = new BitSet(new long[]{0x0000000000801000L});
    public static final BitSet FOLLOW_productionfield_in_productionfields637 = new BitSet(new long[]{0x0000000000801000L});
    public static final BitSet FOLLOW_23_in_productionfields640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_productionfield656 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_productionfield659 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield662 = new BitSet(new long[]{0x0000000070013420L});
    public static final BitSet FOLLOW_stringLiteral_in_productionfield665 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_alternative683 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative685 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_alternative687 = new BitSet(new long[]{0x0000000001003002L});
    public static final BitSet FOLLOW_definitions_in_alternative692 = new BitSet(new long[]{0x0000000001003002L});
    public static final BitSet FOLLOW_20_in_alternative711 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_definitions738 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions741 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_definitions743 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions746 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_set_in_definitions751 = new BitSet(new long[]{0x000000001C000002L});
    public static final BitSet FOLLOW_repeat_in_definitions760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_repeat0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_token809 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_token812 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token815 = new BitSet(new long[]{0x0000000070013420L});
    public static final BitSet FOLLOW_stringLiteral_in_token818 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token820 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_token823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stringLiteral846 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_NormalChar_in_stringLiteral850 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_28_in_stringLiteral854 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_29_in_stringLiteral856 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_30_in_stringLiteral858 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_COLON_in_stringLiteral861 = new BitSet(new long[]{0x0000000070013022L});
    public static final BitSet FOLLOW_JAVANAME_in_stringLiteral866 = new BitSet(new long[]{0x0000000070013022L});

}