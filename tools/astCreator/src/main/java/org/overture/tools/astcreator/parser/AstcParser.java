// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-11-29 13:07:59

package org.overture.tools.astcreator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;
@SuppressWarnings("all")
public class AstcParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "COLON", "PACKAGES", "AST", "TOKENS", "ASPECT_DCL", "FIELD_DCL", "QUOTE", "COMMENT", "ID", "JAVANAME", "WS", "ESC_SEQ", "NormalChar", "SpecialChar", "'base'", "';'", "'analysis'", "'%'", "'|'", "'#'", "'{'", "'}'", "'['", "']'", "'('", "')'", "'?'", "'*'", "'**'", "'+'", "'||'", "'&&'"
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
    public static final int TOKENS=8;
    public static final int NormalChar=17;
    public static final int ID=13;
    public static final int ASPECT_DCL=9;
    public static final int EOF=-1;
    public static final int COLON=5;
    public static final int T__19=19;
    public static final int T__30=30;
    public static final int QUOTE=11;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int ESC_SEQ=16;
    public static final int WS=15;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int JAVANAME=14;
    public static final int SpecialChar=18;
    public static final int ASSIGN=4;
    public static final int PACKAGES=6;
    public static final int AST=7;
    public static final int COMMENT=12;
    public static final int FIELD_DCL=10;

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
    public String getGrammarFileName() { return "C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g"; }


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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:226:1: root : pkg toks ast aspectdcl ;
    public final AstcParser.root_return root() throws RecognitionException {
        AstcParser.root_return retval = new AstcParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        AstcParser.pkg_return pkg1 = null;

        AstcParser.toks_return toks2 = null;

        AstcParser.ast_return ast3 = null;

        AstcParser.aspectdcl_return aspectdcl4 = null;



        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:227:3: ( pkg toks ast aspectdcl )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:227:5: pkg toks ast aspectdcl
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_pkg_in_root416);
            pkg1=pkg();

            state._fsp--;

            adaptor.addChild(root_0, pkg1.getTree());
            pushFollow(FOLLOW_toks_in_root418);
            toks2=toks();

            state._fsp--;

            adaptor.addChild(root_0, toks2.getTree());
            pushFollow(FOLLOW_ast_in_root420);
            ast3=ast();

            state._fsp--;

            adaptor.addChild(root_0, ast3.getTree());
            pushFollow(FOLLOW_aspectdcl_in_root422);
            aspectdcl4=aspectdcl();

            state._fsp--;

            adaptor.addChild(root_0, aspectdcl4.getTree());

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

    public static class pkg_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pkg"
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:230:1: pkg : PACKAGES ( 'base' JAVANAME ';' )? ( 'analysis' JAVANAME ';' )? ;
    public final AstcParser.pkg_return pkg() throws RecognitionException {
        AstcParser.pkg_return retval = new AstcParser.pkg_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PACKAGES5=null;
        Token string_literal6=null;
        Token JAVANAME7=null;
        Token char_literal8=null;
        Token string_literal9=null;
        Token JAVANAME10=null;
        Token char_literal11=null;

        Object PACKAGES5_tree=null;
        Object string_literal6_tree=null;
        Object JAVANAME7_tree=null;
        Object char_literal8_tree=null;
        Object string_literal9_tree=null;
        Object JAVANAME10_tree=null;
        Object char_literal11_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:3: ( PACKAGES ( 'base' JAVANAME ';' )? ( 'analysis' JAVANAME ';' )? )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:5: PACKAGES ( 'base' JAVANAME ';' )? ( 'analysis' JAVANAME ';' )?
            {
            root_0 = (Object)adaptor.nil();

            PACKAGES5=(Token)match(input,PACKAGES,FOLLOW_PACKAGES_in_pkg440); 
            PACKAGES5_tree = (Object)adaptor.create(PACKAGES5);
            root_0 = (Object)adaptor.becomeRoot(PACKAGES5_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:15: ( 'base' JAVANAME ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==19) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:16: 'base' JAVANAME ';'
                    {
                    string_literal6=(Token)match(input,19,FOLLOW_19_in_pkg444); 
                    string_literal6_tree = (Object)adaptor.create(string_literal6);
                    adaptor.addChild(root_0, string_literal6_tree);

                    JAVANAME7=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_pkg446); 
                    JAVANAME7_tree = (Object)adaptor.create(JAVANAME7);
                    adaptor.addChild(root_0, JAVANAME7_tree);

                    char_literal8=(Token)match(input,20,FOLLOW_20_in_pkg448); 

                    }
                    break;

            }

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:40: ( 'analysis' JAVANAME ';' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==21) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:231:41: 'analysis' JAVANAME ';'
                    {
                    string_literal9=(Token)match(input,21,FOLLOW_21_in_pkg455); 
                    string_literal9_tree = (Object)adaptor.create(string_literal9);
                    adaptor.addChild(root_0, string_literal9_tree);

                    JAVANAME10=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_pkg457); 
                    JAVANAME10_tree = (Object)adaptor.create(JAVANAME10);
                    adaptor.addChild(root_0, JAVANAME10_tree);

                    char_literal11=(Token)match(input,20,FOLLOW_20_in_pkg459); 

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
    // $ANTLR end "pkg"

    public static class ast_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ast"
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:234:1: ast : AST ( ( production )* ) ;
    public final AstcParser.ast_return ast() throws RecognitionException {
        AstcParser.ast_return retval = new AstcParser.ast_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AST12=null;
        AstcParser.production_return production13 = null;


        Object AST12_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:3: ( AST ( ( production )* ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:5: AST ( ( production )* )
            {
            root_0 = (Object)adaptor.nil();

            AST12=(Token)match(input,AST,FOLLOW_AST_in_ast478); 
            AST12_tree = (Object)adaptor.create(AST12);
            root_0 = (Object)adaptor.becomeRoot(AST12_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:10: ( ( production )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:11: ( production )*
            {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:11: ( production )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ID||LA3_0==24) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:235:12: production
            	    {
            	    pushFollow(FOLLOW_production_in_ast483);
            	    production13=production();

            	    state._fsp--;

            	    adaptor.addChild(root_0, production13.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:238:1: toks : TOKENS ( ( token )* ) ;
    public final AstcParser.toks_return toks() throws RecognitionException {
        AstcParser.toks_return retval = new AstcParser.toks_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOKENS14=null;
        AstcParser.token_return token15 = null;


        Object TOKENS14_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:3: ( TOKENS ( ( token )* ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:5: TOKENS ( ( token )* )
            {
            root_0 = (Object)adaptor.nil();

            TOKENS14=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_toks501); 
            TOKENS14_tree = (Object)adaptor.create(TOKENS14);
            root_0 = (Object)adaptor.becomeRoot(TOKENS14_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:13: ( ( token )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:14: ( token )*
            {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:14: ( token )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ID) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:239:15: token
            	    {
            	    pushFollow(FOLLOW_token_in_toks506);
            	    token15=token();

            	    state._fsp--;

            	    adaptor.addChild(root_0, token15.getTree());

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
    // $ANTLR end "toks"

    public static class aspectdcl_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectdcl"
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:242:1: aspectdcl : ASPECT_DCL ( aspectdcla ';' )* ;
    public final AstcParser.aspectdcl_return aspectdcl() throws RecognitionException {
        AstcParser.aspectdcl_return retval = new AstcParser.aspectdcl_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASPECT_DCL16=null;
        Token char_literal18=null;
        AstcParser.aspectdcla_return aspectdcla17 = null;


        Object ASPECT_DCL16_tree=null;
        Object char_literal18_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:243:3: ( ASPECT_DCL ( aspectdcla ';' )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:243:5: ASPECT_DCL ( aspectdcla ';' )*
            {
            root_0 = (Object)adaptor.nil();

            ASPECT_DCL16=(Token)match(input,ASPECT_DCL,FOLLOW_ASPECT_DCL_in_aspectdcl524); 
            ASPECT_DCL16_tree = (Object)adaptor.create(ASPECT_DCL16);
            root_0 = (Object)adaptor.becomeRoot(ASPECT_DCL16_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:243:17: ( aspectdcla ';' )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==22) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:243:18: aspectdcla ';'
            	    {
            	    pushFollow(FOLLOW_aspectdcla_in_aspectdcl528);
            	    aspectdcla17=aspectdcla();

            	    state._fsp--;

            	    adaptor.addChild(root_0, aspectdcla17.getTree());
            	    char_literal18=(Token)match(input,20,FOLLOW_20_in_aspectdcl530); 

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
    // $ANTLR end "aspectdcl"

    public static class aspectdcla_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "aspectdcla"
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:246:1: aspectdcla : '%' dd ;
    public final AstcParser.aspectdcla_return aspectdcla() throws RecognitionException {
        AstcParser.aspectdcla_return retval = new AstcParser.aspectdcla_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal19=null;
        AstcParser.dd_return dd20 = null;


        Object char_literal19_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:3: ( '%' dd )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:247:5: '%' dd
            {
            root_0 = (Object)adaptor.nil();

            char_literal19=(Token)match(input,22,FOLLOW_22_in_aspectdcla548); 
            char_literal19_tree = (Object)adaptor.create(char_literal19);
            root_0 = (Object)adaptor.becomeRoot(char_literal19_tree, root_0);

            pushFollow(FOLLOW_dd_in_aspectdcla551);
            dd20=dd();

            state._fsp--;

            adaptor.addChild(root_0, dd20.getTree());

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:250:1: dd : aspectName ASSIGN ( definitions )* -> ^( ID[\"ASPECT\"] aspectName ( definitions )* ) ;
    public final AstcParser.dd_return dd() throws RecognitionException {
        AstcParser.dd_return retval = new AstcParser.dd_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN22=null;
        AstcParser.aspectName_return aspectName21 = null;

        AstcParser.definitions_return definitions23 = null;


        Object ASSIGN22_tree=null;
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_aspectName=new RewriteRuleSubtreeStream(adaptor,"rule aspectName");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:3: ( aspectName ASSIGN ( definitions )* -> ^( ID[\"ASPECT\"] aspectName ( definitions )* ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:4: aspectName ASSIGN ( definitions )*
            {
            pushFollow(FOLLOW_aspectName_in_dd567);
            aspectName21=aspectName();

            state._fsp--;

            stream_aspectName.add(aspectName21.getTree());
            ASSIGN22=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_dd569);  
            stream_ASSIGN.add(ASSIGN22);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:22: ( definitions )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0>=ID && LA6_0<=JAVANAME)||LA6_0==27||LA6_0==29) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:23: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_dd572);
            	    definitions23=definitions();

            	    state._fsp--;

            	    stream_definitions.add(definitions23.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
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
            // 251:37: -> ^( ID[\"ASPECT\"] aspectName ( definitions )* )
            {
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:40: ^( ID[\"ASPECT\"] aspectName ( definitions )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "ASPECT"), root_1);

                adaptor.addChild(root_1, stream_aspectName.nextTree());
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:251:66: ( definitions )*
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:254:1: aspectName : ID ( '->' name )* ;
    public final AstcParser.aspectName_return aspectName() throws RecognitionException {
        AstcParser.aspectName_return retval = new AstcParser.aspectName_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID24=null;
        Token string_literal25=null;
        AstcParser.name_return name26 = null;


        Object ID24_tree=null;
        Object string_literal25_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:255:3: ( ID ( '->' name )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:255:5: ID ( '->' name )*
            {
            root_0 = (Object)adaptor.nil();

            ID24=(Token)match(input,ID,FOLLOW_ID_in_aspectName602); 
            ID24_tree = (Object)adaptor.create(ID24);
            root_0 = (Object)adaptor.becomeRoot(ID24_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:255:9: ( '->' name )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==FIELD_DCL) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:255:10: '->' name
            	    {
            	    string_literal25=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_aspectName606); 
            	    string_literal25_tree = (Object)adaptor.create(string_literal25);
            	    adaptor.addChild(root_0, string_literal25_tree);

            	    pushFollow(FOLLOW_name_in_aspectName608);
            	    name26=name();

            	    state._fsp--;

            	    adaptor.addChild(root_0, name26.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:259:1: production : name ( productionfields )? ( ASSIGN alternative ( '|' alternative )* )? ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) ;
    public final AstcParser.production_return production() throws RecognitionException {
        AstcParser.production_return retval = new AstcParser.production_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN29=null;
        Token char_literal31=null;
        Token char_literal33=null;
        AstcParser.name_return name27 = null;

        AstcParser.productionfields_return productionfields28 = null;

        AstcParser.alternative_return alternative30 = null;

        AstcParser.alternative_return alternative32 = null;


        Object ASSIGN29_tree=null;
        Object char_literal31_tree=null;
        Object char_literal33_tree=null;
        RewriteRuleTokenStream stream_20=new RewriteRuleTokenStream(adaptor,"token 20");
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_productionfields=new RewriteRuleSubtreeStream(adaptor,"rule productionfields");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:3: ( name ( productionfields )? ( ASSIGN alternative ( '|' alternative )* )? ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:5: name ( productionfields )? ( ASSIGN alternative ( '|' alternative )* )? ';'
            {
            pushFollow(FOLLOW_name_in_production624);
            name27=name();

            state._fsp--;

            stream_name.add(name27.getTree());
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:10: ( productionfields )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==25) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:10: productionfields
                    {
                    pushFollow(FOLLOW_productionfields_in_production626);
                    productionfields28=productionfields();

                    state._fsp--;

                    stream_productionfields.add(productionfields28.getTree());

                    }
                    break;

            }

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:28: ( ASSIGN alternative ( '|' alternative )* )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ASSIGN) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:29: ASSIGN alternative ( '|' alternative )*
                    {
                    ASSIGN29=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_production630);  
                    stream_ASSIGN.add(ASSIGN29);

                    pushFollow(FOLLOW_alternative_in_production632);
                    alternative30=alternative();

                    state._fsp--;

                    stream_alternative.add(alternative30.getTree());
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:48: ( '|' alternative )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==23) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:49: '|' alternative
                    	    {
                    	    char_literal31=(Token)match(input,23,FOLLOW_23_in_production635);  
                    	    stream_23.add(char_literal31);

                    	    pushFollow(FOLLOW_alternative_in_production637);
                    	    alternative32=alternative();

                    	    state._fsp--;

                    	    stream_alternative.add(alternative32.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal33=(Token)match(input,20,FOLLOW_20_in_production643);  
            stream_20.add(char_literal33);



            // AST REWRITE
            // elements: productionfields, name, alternative
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 260:73: -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
            {
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:76: ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "P"), root_1);

                adaptor.addChild(root_1, stream_name.nextTree());
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:91: ( productionfields )?
                if ( stream_productionfields.hasNext() ) {
                    adaptor.addChild(root_1, stream_productionfields.nextTree());

                }
                stream_productionfields.reset();
                // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:260:109: ( alternative )*
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:263:1: name : ( ID | '#' ID -> ^( '#' ID ) );
    public final AstcParser.name_return name() throws RecognitionException {
        AstcParser.name_return retval = new AstcParser.name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID34=null;
        Token char_literal35=null;
        Token ID36=null;

        Object ID34_tree=null;
        Object char_literal35_tree=null;
        Object ID36_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:264:3: ( ID | '#' ID -> ^( '#' ID ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            else if ( (LA11_0==24) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:264:5: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID34=(Token)match(input,ID,FOLLOW_ID_in_name677); 
                    ID34_tree = (Object)adaptor.create(ID34);
                    root_0 = (Object)adaptor.becomeRoot(ID34_tree, root_0);


                    }
                    break;
                case 2 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:265:5: '#' ID
                    {
                    char_literal35=(Token)match(input,24,FOLLOW_24_in_name685);  
                    stream_24.add(char_literal35);

                    ID36=(Token)match(input,ID,FOLLOW_ID_in_name687);  
                    stream_ID.add(ID36);



                    // AST REWRITE
                    // elements: 24, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 265:11: -> ^( '#' ID )
                    {
                        // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:265:13: ^( '#' ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_24.nextNode(), root_1);

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:270:1: productionfields : '{' FIELD_DCL ( productionfield )* '}' ;
    public final AstcParser.productionfields_return productionfields() throws RecognitionException {
        AstcParser.productionfields_return retval = new AstcParser.productionfields_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal37=null;
        Token FIELD_DCL38=null;
        Token char_literal40=null;
        AstcParser.productionfield_return productionfield39 = null;


        Object char_literal37_tree=null;
        Object FIELD_DCL38_tree=null;
        Object char_literal40_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:3: ( '{' FIELD_DCL ( productionfield )* '}' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:5: '{' FIELD_DCL ( productionfield )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal37=(Token)match(input,25,FOLLOW_25_in_productionfields712); 
            FIELD_DCL38=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_productionfields715); 
            FIELD_DCL38_tree = (Object)adaptor.create(FIELD_DCL38);
            root_0 = (Object)adaptor.becomeRoot(FIELD_DCL38_tree, root_0);

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:21: ( productionfield )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==ID) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:271:21: productionfield
            	    {
            	    pushFollow(FOLLOW_productionfield_in_productionfields718);
            	    productionfield39=productionfield();

            	    state._fsp--;

            	    adaptor.addChild(root_0, productionfield39.getTree());

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            char_literal40=(Token)match(input,26,FOLLOW_26_in_productionfields721); 

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:274:1: productionfield : ID ASSIGN QUOTE stringLiteral QUOTE ;
    public final AstcParser.productionfield_return productionfield() throws RecognitionException {
        AstcParser.productionfield_return retval = new AstcParser.productionfield_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID41=null;
        Token ASSIGN42=null;
        Token QUOTE43=null;
        Token QUOTE45=null;
        AstcParser.stringLiteral_return stringLiteral44 = null;


        Object ID41_tree=null;
        Object ASSIGN42_tree=null;
        Object QUOTE43_tree=null;
        Object QUOTE45_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:275:3: ( ID ASSIGN QUOTE stringLiteral QUOTE )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:275:5: ID ASSIGN QUOTE stringLiteral QUOTE
            {
            root_0 = (Object)adaptor.nil();

            ID41=(Token)match(input,ID,FOLLOW_ID_in_productionfield737); 
            ID41_tree = (Object)adaptor.create(ID41);
            root_0 = (Object)adaptor.becomeRoot(ID41_tree, root_0);

            ASSIGN42=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_productionfield740); 
            QUOTE43=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield743); 
            pushFollow(FOLLOW_stringLiteral_in_productionfield746);
            stringLiteral44=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral44.getTree());
            QUOTE45=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield748); 

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:278:1: alternative : ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) );
    public final AstcParser.alternative_return alternative() throws RecognitionException {
        AstcParser.alternative_return retval = new AstcParser.alternative_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal46=null;
        Token ID47=null;
        Token char_literal48=null;
        Token char_literal50=null;
        Token ID51=null;
        AstcParser.definitions_return definitions49 = null;


        Object char_literal46_tree=null;
        Object ID47_tree=null;
        Object char_literal48_tree=null;
        Object char_literal50_tree=null;
        Object ID51_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:3: ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=ID && LA15_0<=JAVANAME)||LA15_0==20||LA15_0==23||LA15_0==25||LA15_0==27||LA15_0==29) ) {
                alt15=1;
            }
            else if ( (LA15_0==24) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:5: ( '{' ID '}' )? ( definitions )*
                    {
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:5: ( '{' ID '}' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==25) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:6: '{' ID '}'
                            {
                            char_literal46=(Token)match(input,25,FOLLOW_25_in_alternative764);  
                            stream_25.add(char_literal46);

                            ID47=(Token)match(input,ID,FOLLOW_ID_in_alternative766);  
                            stream_ID.add(ID47);

                            char_literal48=(Token)match(input,26,FOLLOW_26_in_alternative768);  
                            stream_26.add(char_literal48);


                            }
                            break;

                    }

                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:19: ( definitions )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>=ID && LA14_0<=JAVANAME)||LA14_0==27||LA14_0==29) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:20: definitions
                    	    {
                    	    pushFollow(FOLLOW_definitions_in_alternative773);
                    	    definitions49=definitions();

                    	    state._fsp--;

                    	    stream_definitions.add(definitions49.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
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
                    // 279:34: -> ^( ID ( definitions )* )
                    {
                        // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:37: ^( ID ( definitions )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                        // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:279:42: ( definitions )*
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
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:280:5: '#' ID
                    {
                    char_literal50=(Token)match(input,24,FOLLOW_24_in_alternative792);  
                    stream_24.add(char_literal50);

                    ID51=(Token)match(input,ID,FOLLOW_ID_in_alternative794);  
                    stream_ID.add(ID51);



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
                    // 280:12: -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
                    {
                        // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:280:15: ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:283:1: definitions : ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )? ;
    public final AstcParser.definitions_return definitions() throws RecognitionException {
        AstcParser.definitions_return retval = new AstcParser.definitions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal52=null;
        Token ID53=null;
        Token char_literal54=null;
        Token char_literal55=null;
        Token char_literal56=null;
        Token ID57=null;
        Token char_literal58=null;
        Token char_literal59=null;
        Token set60=null;
        AstcParser.repeat_return repeat61 = null;


        Object char_literal52_tree=null;
        Object ID53_tree=null;
        Object char_literal54_tree=null;
        Object char_literal55_tree=null;
        Object char_literal56_tree=null;
        Object ID57_tree=null;
        Object char_literal58_tree=null;
        Object char_literal59_tree=null;
        Object set60_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:3: ( ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )? )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:5: ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) ) ( ID | JAVANAME ) ( repeat )?
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:5: ( ( '[' ID ']' ':' )? | ( '(' ID ')' ':' ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=ID && LA17_0<=JAVANAME)||LA17_0==27) ) {
                alt17=1;
            }
            else if ( (LA17_0==29) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:6: ( '[' ID ']' ':' )?
                    {
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:6: ( '[' ID ']' ':' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==27) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:7: '[' ID ']' ':'
                            {
                            char_literal52=(Token)match(input,27,FOLLOW_27_in_definitions820); 
                            char_literal52_tree = (Object)adaptor.create(char_literal52);
                            adaptor.addChild(root_0, char_literal52_tree);

                            ID53=(Token)match(input,ID,FOLLOW_ID_in_definitions822); 
                            ID53_tree = (Object)adaptor.create(ID53);
                            adaptor.addChild(root_0, ID53_tree);

                            char_literal54=(Token)match(input,28,FOLLOW_28_in_definitions824); 
                            char_literal55=(Token)match(input,COLON,FOLLOW_COLON_in_definitions827); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:27: ( '(' ID ')' ':' )
                    {
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:27: ( '(' ID ')' ':' )
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:28: '(' ID ')' ':'
                    {
                    char_literal56=(Token)match(input,29,FOLLOW_29_in_definitions834); 
                    char_literal56_tree = (Object)adaptor.create(char_literal56);
                    adaptor.addChild(root_0, char_literal56_tree);

                    ID57=(Token)match(input,ID,FOLLOW_ID_in_definitions836); 
                    ID57_tree = (Object)adaptor.create(ID57);
                    adaptor.addChild(root_0, ID57_tree);

                    char_literal58=(Token)match(input,30,FOLLOW_30_in_definitions838); 
                    char_literal59=(Token)match(input,COLON,FOLLOW_COLON_in_definitions841); 

                    }


                    }
                    break;

            }

            set60=(Token)input.LT(1);
            set60=(Token)input.LT(1);
            if ( (input.LA(1)>=ID && input.LA(1)<=JAVANAME) ) {
                input.consume();
                root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set60), root_0);
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:64: ( repeat )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>=31 && LA18_0<=34)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:284:65: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_definitions856);
                    repeat61=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat61.getTree());

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:298:1: repeat : ( '?' | '*' | '**' | '+' );
    public final AstcParser.repeat_return repeat() throws RecognitionException {
        AstcParser.repeat_return retval = new AstcParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set62=null;

        Object set62_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:299:3: ( '?' | '*' | '**' | '+' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            {
            root_0 = (Object)adaptor.nil();

            set62=(Token)input.LT(1);
            if ( (input.LA(1)>=31 && input.LA(1)<=34) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set62));
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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:306:1: token : ID ASSIGN QUOTE stringLiteral QUOTE ';' ;
    public final AstcParser.token_return token() throws RecognitionException {
        AstcParser.token_return retval = new AstcParser.token_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID63=null;
        Token ASSIGN64=null;
        Token QUOTE65=null;
        Token QUOTE67=null;
        Token char_literal68=null;
        AstcParser.stringLiteral_return stringLiteral66 = null;


        Object ID63_tree=null;
        Object ASSIGN64_tree=null;
        Object QUOTE65_tree=null;
        Object QUOTE67_tree=null;
        Object char_literal68_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:307:3: ( ID ASSIGN QUOTE stringLiteral QUOTE ';' )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:307:5: ID ASSIGN QUOTE stringLiteral QUOTE ';'
            {
            root_0 = (Object)adaptor.nil();

            ID63=(Token)match(input,ID,FOLLOW_ID_in_token924); 
            ID63_tree = (Object)adaptor.create(ID63);
            root_0 = (Object)adaptor.becomeRoot(ID63_tree, root_0);

            ASSIGN64=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_token927); 
            QUOTE65=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token930); 
            pushFollow(FOLLOW_stringLiteral_in_token933);
            stringLiteral66=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral66.getTree());
            QUOTE67=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token935); 
            char_literal68=(Token)match(input,20,FOLLOW_20_in_token938); 

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
    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:310:1: stringLiteral : ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* ;
    public final AstcParser.stringLiteral_return stringLiteral() throws RecognitionException {
        AstcParser.stringLiteral_return retval = new AstcParser.stringLiteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID69=null;
        Token NormalChar70=null;
        Token char_literal71=null;
        Token string_literal72=null;
        Token string_literal73=null;
        Token char_literal74=null;
        Token JAVANAME75=null;

        Object ID69_tree=null;
        Object NormalChar70_tree=null;
        Object char_literal71_tree=null;
        Object string_literal72_tree=null;
        Object string_literal73_tree=null;
        Object char_literal74_tree=null;
        Object JAVANAME75_tree=null;

        try {
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:5: ( ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* )
            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            loop19:
            do {
                int alt19=8;
                switch ( input.LA(1) ) {
                case ID:
                    {
                    alt19=1;
                    }
                    break;
                case NormalChar:
                    {
                    alt19=2;
                    }
                    break;
                case 34:
                    {
                    alt19=3;
                    }
                    break;
                case 35:
                    {
                    alt19=4;
                    }
                    break;
                case 36:
                    {
                    alt19=5;
                    }
                    break;
                case COLON:
                    {
                    alt19=6;
                    }
                    break;
                case JAVANAME:
                    {
                    alt19=7;
                    }
                    break;

                }

                switch (alt19) {
            	case 1 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:12: ID
            	    {
            	    ID69=(Token)match(input,ID,FOLLOW_ID_in_stringLiteral961); 
            	    ID69_tree = (Object)adaptor.create(ID69);
            	    adaptor.addChild(root_0, ID69_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:17: NormalChar
            	    {
            	    NormalChar70=(Token)match(input,NormalChar,FOLLOW_NormalChar_in_stringLiteral965); 
            	    NormalChar70_tree = (Object)adaptor.create(NormalChar70);
            	    adaptor.addChild(root_0, NormalChar70_tree);


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:30: '+'
            	    {
            	    char_literal71=(Token)match(input,34,FOLLOW_34_in_stringLiteral969); 
            	    char_literal71_tree = (Object)adaptor.create(char_literal71);
            	    adaptor.addChild(root_0, char_literal71_tree);


            	    }
            	    break;
            	case 4 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:34: '||'
            	    {
            	    string_literal72=(Token)match(input,35,FOLLOW_35_in_stringLiteral971); 
            	    string_literal72_tree = (Object)adaptor.create(string_literal72);
            	    adaptor.addChild(root_0, string_literal72_tree);


            	    }
            	    break;
            	case 5 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:39: '&&'
            	    {
            	    string_literal73=(Token)match(input,36,FOLLOW_36_in_stringLiteral973); 
            	    string_literal73_tree = (Object)adaptor.create(string_literal73);
            	    adaptor.addChild(root_0, string_literal73_tree);


            	    }
            	    break;
            	case 6 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:44: ( ':' )
            	    {
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:44: ( ':' )
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:45: ':'
            	    {
            	    char_literal74=(Token)match(input,COLON,FOLLOW_COLON_in_stringLiteral976); 
            	    char_literal74_tree = (Object)adaptor.create(char_literal74);
            	    adaptor.addChild(root_0, char_literal74_tree);


            	    }


            	    }
            	    break;
            	case 7 :
            	    // C:\\overture\\ast\\tools\\astCreator\\src\\main\\resources\\Astc.g:311:52: JAVANAME
            	    {
            	    JAVANAME75=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_stringLiteral981); 
            	    JAVANAME75_tree = (Object)adaptor.create(JAVANAME75);
            	    adaptor.addChild(root_0, JAVANAME75_tree);


            	    }
            	    break;

            	default :
            	    break loop19;
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


 

    public static final BitSet FOLLOW_pkg_in_root416 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_toks_in_root418 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_ast_in_root420 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_aspectdcl_in_root422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGES_in_pkg440 = new BitSet(new long[]{0x0000000000280002L});
    public static final BitSet FOLLOW_19_in_pkg444 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_JAVANAME_in_pkg446 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_pkg448 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_21_in_pkg455 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_JAVANAME_in_pkg457 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_pkg459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_ast478 = new BitSet(new long[]{0x0000000001002002L});
    public static final BitSet FOLLOW_production_in_ast483 = new BitSet(new long[]{0x0000000001002002L});
    public static final BitSet FOLLOW_TOKENS_in_toks501 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_token_in_toks506 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ASPECT_DCL_in_aspectdcl524 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_aspectdcla_in_aspectdcl528 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_aspectdcl530 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_aspectdcla548 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_dd_in_aspectdcla551 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aspectName_in_dd567 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_dd569 = new BitSet(new long[]{0x0000000028006002L});
    public static final BitSet FOLLOW_definitions_in_dd572 = new BitSet(new long[]{0x0000000028006002L});
    public static final BitSet FOLLOW_ID_in_aspectName602 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_FIELD_DCL_in_aspectName606 = new BitSet(new long[]{0x0000000001002000L});
    public static final BitSet FOLLOW_name_in_aspectName608 = new BitSet(new long[]{0x0000000000000402L});
    public static final BitSet FOLLOW_name_in_production624 = new BitSet(new long[]{0x0000000002100010L});
    public static final BitSet FOLLOW_productionfields_in_production626 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_ASSIGN_in_production630 = new BitSet(new long[]{0x000000002B906000L});
    public static final BitSet FOLLOW_alternative_in_production632 = new BitSet(new long[]{0x0000000000900000L});
    public static final BitSet FOLLOW_23_in_production635 = new BitSet(new long[]{0x000000002B906000L});
    public static final BitSet FOLLOW_alternative_in_production637 = new BitSet(new long[]{0x0000000000900000L});
    public static final BitSet FOLLOW_20_in_production643 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_name685 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_name687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_productionfields712 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_FIELD_DCL_in_productionfields715 = new BitSet(new long[]{0x0000000004002000L});
    public static final BitSet FOLLOW_productionfield_in_productionfields718 = new BitSet(new long[]{0x0000000004002000L});
    public static final BitSet FOLLOW_26_in_productionfields721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_productionfield737 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_productionfield740 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield743 = new BitSet(new long[]{0x0000001C00026820L});
    public static final BitSet FOLLOW_stringLiteral_in_productionfield746 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_alternative764 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_alternative766 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_alternative768 = new BitSet(new long[]{0x0000000028006002L});
    public static final BitSet FOLLOW_definitions_in_alternative773 = new BitSet(new long[]{0x0000000028006002L});
    public static final BitSet FOLLOW_24_in_alternative792 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_alternative794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_definitions820 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_definitions822 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_definitions824 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions827 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_29_in_definitions834 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_ID_in_definitions836 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_definitions838 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions841 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_set_in_definitions846 = new BitSet(new long[]{0x0000000780000002L});
    public static final BitSet FOLLOW_repeat_in_definitions856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_repeat0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_token924 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_token927 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_QUOTE_in_token930 = new BitSet(new long[]{0x0000001C00026820L});
    public static final BitSet FOLLOW_stringLiteral_in_token933 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_QUOTE_in_token935 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_token938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stringLiteral961 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_NormalChar_in_stringLiteral965 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_34_in_stringLiteral969 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_35_in_stringLiteral971 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_36_in_stringLiteral973 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_COLON_in_stringLiteral976 = new BitSet(new long[]{0x0000001C00026022L});
    public static final BitSet FOLLOW_JAVANAME_in_stringLiteral981 = new BitSet(new long[]{0x0000001C00026022L});

}
