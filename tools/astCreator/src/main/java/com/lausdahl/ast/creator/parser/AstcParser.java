// $ANTLR 3.2 Sep 23, 2009 14:05:07 C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g 2011-07-04 22:54:34

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "COLON", "AST", "TOKENS", "ASPECT_DCL", "FIELD_DCL", "QUOTE", "COMMENT", "ID", "JAVANAME", "INT", "EXPONENT", "FLOAT", "WS", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "ESC_SEQ", "NormalChar", "SpecialChar", "';'", "'|'", "'#'", "'{'", "'}'", "'['", "']'", "'?'", "'*'", "'+'", "'||'", "'&&'"
    };
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
    public static final int QUOTE=10;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int ESC_SEQ=21;
    public static final int WS=17;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int JAVANAME=13;
    public static final int SpecialChar=23;
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


    public static class root_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "root"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:102:1: root : toks ast aspectdcl ;
    public final AstcParser.root_return root() throws RecognitionException {
        AstcParser.root_return retval = new AstcParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        AstcParser.toks_return toks1 = null;

        AstcParser.ast_return ast2 = null;

        AstcParser.aspectdcl_return aspectdcl3 = null;



        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:3: ( toks ast aspectdcl )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:103:5: toks ast aspectdcl
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_toks_in_root679);
            toks1=toks();

            state._fsp--;

            adaptor.addChild(root_0, toks1.getTree());
            pushFollow(FOLLOW_ast_in_root681);
            ast2=ast();

            state._fsp--;

            adaptor.addChild(root_0, ast2.getTree());
            pushFollow(FOLLOW_aspectdcl_in_root683);
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:106:1: ast : AST ( ( production )* ) ;
    public final AstcParser.ast_return ast() throws RecognitionException {
        AstcParser.ast_return retval = new AstcParser.ast_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AST4=null;
        AstcParser.production_return production5 = null;


        Object AST4_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:3: ( AST ( ( production )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:5: AST ( ( production )* )
            {
            root_0 = (Object)adaptor.nil();

            AST4=(Token)match(input,AST,FOLLOW_AST_in_ast701); 
            AST4_tree = (Object)adaptor.create(AST4);
            root_0 = (Object)adaptor.becomeRoot(AST4_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:10: ( ( production )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:11: ( production )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:11: ( production )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||LA1_0==26) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:107:12: production
            	    {
            	    pushFollow(FOLLOW_production_in_ast706);
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:110:1: toks : TOKENS ( ( token )* ) ;
    public final AstcParser.toks_return toks() throws RecognitionException {
        AstcParser.toks_return retval = new AstcParser.toks_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOKENS6=null;
        AstcParser.token_return token7 = null;


        Object TOKENS6_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:3: ( TOKENS ( ( token )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:5: TOKENS ( ( token )* )
            {
            root_0 = (Object)adaptor.nil();

            TOKENS6=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_toks724); 
            TOKENS6_tree = (Object)adaptor.create(TOKENS6);
            root_0 = (Object)adaptor.becomeRoot(TOKENS6_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:13: ( ( token )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:14: ( token )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:14: ( token )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:111:15: token
            	    {
            	    pushFollow(FOLLOW_token_in_toks729);
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:114:1: aspectdcl : ASPECT_DCL ( aspectdcla ';' )* ;
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
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:3: ( ASPECT_DCL ( aspectdcla ';' )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:5: ASPECT_DCL ( aspectdcla ';' )*
            {
            root_0 = (Object)adaptor.nil();

            ASPECT_DCL8=(Token)match(input,ASPECT_DCL,FOLLOW_ASPECT_DCL_in_aspectdcl747); 
            ASPECT_DCL8_tree = (Object)adaptor.create(ASPECT_DCL8);
            root_0 = (Object)adaptor.becomeRoot(ASPECT_DCL8_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:17: ( aspectdcla ';' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ID) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:115:18: aspectdcla ';'
            	    {
            	    pushFollow(FOLLOW_aspectdcla_in_aspectdcl751);
            	    aspectdcla9=aspectdcla();

            	    state._fsp--;

            	    adaptor.addChild(root_0, aspectdcla9.getTree());
            	    char_literal10=(Token)match(input,24,FOLLOW_24_in_aspectdcl753); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:118:1: aspectdcla : ID ASSIGN ( ( definitions )* ) ;
    public final AstcParser.aspectdcla_return aspectdcla() throws RecognitionException {
        AstcParser.aspectdcla_return retval = new AstcParser.aspectdcla_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID11=null;
        Token ASSIGN12=null;
        AstcParser.definitions_return definitions13 = null;


        Object ID11_tree=null;
        Object ASSIGN12_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:3: ( ID ASSIGN ( ( definitions )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:6: ID ASSIGN ( ( definitions )* )
            {
            root_0 = (Object)adaptor.nil();

            ID11=(Token)match(input,ID,FOLLOW_ID_in_aspectdcla772); 
            ID11_tree = (Object)adaptor.create(ID11);
            root_0 = (Object)adaptor.becomeRoot(ID11_tree, root_0);

            ASSIGN12=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_aspectdcla775); 
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:18: ( ( definitions )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:19: ( definitions )*
            {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:19: ( definitions )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=ID && LA4_0<=JAVANAME)||LA4_0==29) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:119:20: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_aspectdcla780);
            	    definitions13=definitions();

            	    state._fsp--;

            	    adaptor.addChild(root_0, definitions13.getTree());

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

    public static class production_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "production"
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:124:1: production : name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) ;
    public final AstcParser.production_return production() throws RecognitionException {
        AstcParser.production_return retval = new AstcParser.production_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ASSIGN16=null;
        Token char_literal18=null;
        Token char_literal20=null;
        AstcParser.name_return name14 = null;

        AstcParser.productionfields_return productionfields15 = null;

        AstcParser.alternative_return alternative17 = null;

        AstcParser.alternative_return alternative19 = null;


        Object ASSIGN16_tree=null;
        Object char_literal18_tree=null;
        Object char_literal20_tree=null;
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_productionfields=new RewriteRuleSubtreeStream(adaptor,"rule productionfields");
        RewriteRuleSubtreeStream stream_name=new RewriteRuleSubtreeStream(adaptor,"rule name");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:3: ( name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* ) )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:5: name ( productionfields )? ASSIGN alternative ( '|' alternative )* ';'
            {
            pushFollow(FOLLOW_name_in_production800);
            name14=name();

            state._fsp--;

            stream_name.add(name14.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:10: ( productionfields )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==27) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:10: productionfields
                    {
                    pushFollow(FOLLOW_productionfields_in_production802);
                    productionfields15=productionfields();

                    state._fsp--;

                    stream_productionfields.add(productionfields15.getTree());

                    }
                    break;

            }

            ASSIGN16=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_production805);  
            stream_ASSIGN.add(ASSIGN16);

            pushFollow(FOLLOW_alternative_in_production807);
            alternative17=alternative();

            state._fsp--;

            stream_alternative.add(alternative17.getTree());
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:47: ( '|' alternative )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==25) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:48: '|' alternative
            	    {
            	    char_literal18=(Token)match(input,25,FOLLOW_25_in_production810);  
            	    stream_25.add(char_literal18);

            	    pushFollow(FOLLOW_alternative_in_production812);
            	    alternative19=alternative();

            	    state._fsp--;

            	    stream_alternative.add(alternative19.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            char_literal20=(Token)match(input,24,FOLLOW_24_in_production816);  
            stream_24.add(char_literal20);



            // AST REWRITE
            // elements: alternative, name, productionfields
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 125:70: -> ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
            {
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:73: ^( ID[\"P\"] name ( productionfields )? ( alternative )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ID, "P"), root_1);

                adaptor.addChild(root_1, stream_name.nextTree());
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:88: ( productionfields )?
                if ( stream_productionfields.hasNext() ) {
                    adaptor.addChild(root_1, stream_productionfields.nextTree());

                }
                stream_productionfields.reset();
                // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:125:106: ( alternative )*
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:128:1: name : ( ID | '#' ID -> ^( '#' ID ) );
    public final AstcParser.name_return name() throws RecognitionException {
        AstcParser.name_return retval = new AstcParser.name_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID21=null;
        Token char_literal22=null;
        Token ID23=null;

        Object ID21_tree=null;
        Object char_literal22_tree=null;
        Object ID23_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:129:3: ( ID | '#' ID -> ^( '#' ID ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ID) ) {
                alt7=1;
            }
            else if ( (LA7_0==26) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:129:5: ID
                    {
                    root_0 = (Object)adaptor.nil();

                    ID21=(Token)match(input,ID,FOLLOW_ID_in_name850); 
                    ID21_tree = (Object)adaptor.create(ID21);
                    root_0 = (Object)adaptor.becomeRoot(ID21_tree, root_0);


                    }
                    break;
                case 2 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:130:5: '#' ID
                    {
                    char_literal22=(Token)match(input,26,FOLLOW_26_in_name858);  
                    stream_26.add(char_literal22);

                    ID23=(Token)match(input,ID,FOLLOW_ID_in_name860);  
                    stream_ID.add(ID23);



                    // AST REWRITE
                    // elements: 26, ID
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 130:11: -> ^( '#' ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:130:13: ^( '#' ID )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_26.nextNode(), root_1);

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:135:1: productionfields : '{' FIELD_DCL ( productionfield )* '}' ;
    public final AstcParser.productionfields_return productionfields() throws RecognitionException {
        AstcParser.productionfields_return retval = new AstcParser.productionfields_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal24=null;
        Token FIELD_DCL25=null;
        Token char_literal27=null;
        AstcParser.productionfield_return productionfield26 = null;


        Object char_literal24_tree=null;
        Object FIELD_DCL25_tree=null;
        Object char_literal27_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:136:3: ( '{' FIELD_DCL ( productionfield )* '}' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:136:5: '{' FIELD_DCL ( productionfield )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal24=(Token)match(input,27,FOLLOW_27_in_productionfields885); 
            FIELD_DCL25=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_productionfields888); 
            FIELD_DCL25_tree = (Object)adaptor.create(FIELD_DCL25);
            root_0 = (Object)adaptor.becomeRoot(FIELD_DCL25_tree, root_0);

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:136:21: ( productionfield )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ID) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:136:21: productionfield
            	    {
            	    pushFollow(FOLLOW_productionfield_in_productionfields891);
            	    productionfield26=productionfield();

            	    state._fsp--;

            	    adaptor.addChild(root_0, productionfield26.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            char_literal27=(Token)match(input,28,FOLLOW_28_in_productionfields894); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:139:1: productionfield : ID ASSIGN QUOTE stringLiteral QUOTE ;
    public final AstcParser.productionfield_return productionfield() throws RecognitionException {
        AstcParser.productionfield_return retval = new AstcParser.productionfield_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID28=null;
        Token ASSIGN29=null;
        Token QUOTE30=null;
        Token QUOTE32=null;
        AstcParser.stringLiteral_return stringLiteral31 = null;


        Object ID28_tree=null;
        Object ASSIGN29_tree=null;
        Object QUOTE30_tree=null;
        Object QUOTE32_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:140:3: ( ID ASSIGN QUOTE stringLiteral QUOTE )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:140:5: ID ASSIGN QUOTE stringLiteral QUOTE
            {
            root_0 = (Object)adaptor.nil();

            ID28=(Token)match(input,ID,FOLLOW_ID_in_productionfield910); 
            ID28_tree = (Object)adaptor.create(ID28);
            root_0 = (Object)adaptor.becomeRoot(ID28_tree, root_0);

            ASSIGN29=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_productionfield913); 
            QUOTE30=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield916); 
            pushFollow(FOLLOW_stringLiteral_in_productionfield919);
            stringLiteral31=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral31.getTree());
            QUOTE32=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield921); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:143:1: alternative : ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) );
    public final AstcParser.alternative_return alternative() throws RecognitionException {
        AstcParser.alternative_return retval = new AstcParser.alternative_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal33=null;
        Token ID34=null;
        Token char_literal35=null;
        Token char_literal37=null;
        Token ID38=null;
        AstcParser.definitions_return definitions36 = null;


        Object char_literal33_tree=null;
        Object ID34_tree=null;
        Object char_literal35_tree=null;
        Object char_literal37_tree=null;
        Object ID38_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:3: ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) | '#' ID -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=ID && LA11_0<=JAVANAME)||(LA11_0>=24 && LA11_0<=25)||LA11_0==27||LA11_0==29) ) {
                alt11=1;
            }
            else if ( (LA11_0==26) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:5: ( '{' ID '}' )? ( definitions )*
                    {
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:5: ( '{' ID '}' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==27) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:6: '{' ID '}'
                            {
                            char_literal33=(Token)match(input,27,FOLLOW_27_in_alternative937);  
                            stream_27.add(char_literal33);

                            ID34=(Token)match(input,ID,FOLLOW_ID_in_alternative939);  
                            stream_ID.add(ID34);

                            char_literal35=(Token)match(input,28,FOLLOW_28_in_alternative941);  
                            stream_28.add(char_literal35);


                            }
                            break;

                    }

                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:19: ( definitions )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0>=ID && LA10_0<=JAVANAME)||LA10_0==29) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:20: definitions
                    	    {
                    	    pushFollow(FOLLOW_definitions_in_alternative946);
                    	    definitions36=definitions();

                    	    state._fsp--;

                    	    stream_definitions.add(definitions36.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
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
                    // 144:34: -> ^( ID ( definitions )* )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:37: ^( ID ( definitions )* )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:144:42: ( definitions )*
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
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:145:5: '#' ID
                    {
                    char_literal37=(Token)match(input,26,FOLLOW_26_in_alternative965);  
                    stream_26.add(char_literal37);

                    ID38=(Token)match(input,ID,FOLLOW_ID_in_alternative967);  
                    stream_ID.add(ID38);



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
                    // 145:12: -> ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
                    {
                        // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:145:15: ^( ID[\"ALTERNATIVE_SUB_ROOT\"] ID )
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:148:1: definitions : ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )? ;
    public final AstcParser.definitions_return definitions() throws RecognitionException {
        AstcParser.definitions_return retval = new AstcParser.definitions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal39=null;
        Token ID40=null;
        Token char_literal41=null;
        Token char_literal42=null;
        Token set43=null;
        AstcParser.repeat_return repeat44 = null;


        Object char_literal39_tree=null;
        Object ID40_tree=null;
        Object char_literal41_tree=null;
        Object char_literal42_tree=null;
        Object set43_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:3: ( ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )? )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:5: ( '[' ID ']' ':' )? ( ID | JAVANAME ) ( repeat )?
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:5: ( '[' ID ']' ':' )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==29) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:6: '[' ID ']' ':'
                    {
                    char_literal39=(Token)match(input,29,FOLLOW_29_in_definitions992); 
                    ID40=(Token)match(input,ID,FOLLOW_ID_in_definitions995); 
                    ID40_tree = (Object)adaptor.create(ID40);
                    adaptor.addChild(root_0, ID40_tree);

                    char_literal41=(Token)match(input,30,FOLLOW_30_in_definitions997); 
                    char_literal42=(Token)match(input,COLON,FOLLOW_COLON_in_definitions1000); 

                    }
                    break;

            }

            set43=(Token)input.LT(1);
            set43=(Token)input.LT(1);
            if ( (input.LA(1)>=ID && input.LA(1)<=JAVANAME) ) {
                input.consume();
                root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(set43), root_0);
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:42: ( repeat )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0>=31 && LA13_0<=33)) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:149:43: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_definitions1014);
                    repeat44=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat44.getTree());

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:156:1: repeat : ( '?' | '*' | '+' );
    public final AstcParser.repeat_return repeat() throws RecognitionException {
        AstcParser.repeat_return retval = new AstcParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set45=null;

        Object set45_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:157:3: ( '?' | '*' | '+' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:
            {
            root_0 = (Object)adaptor.nil();

            set45=(Token)input.LT(1);
            if ( (input.LA(1)>=31 && input.LA(1)<=33) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set45));
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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:163:1: token : ID ASSIGN QUOTE stringLiteral QUOTE ';' ;
    public final AstcParser.token_return token() throws RecognitionException {
        AstcParser.token_return retval = new AstcParser.token_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID46=null;
        Token ASSIGN47=null;
        Token QUOTE48=null;
        Token QUOTE50=null;
        Token char_literal51=null;
        AstcParser.stringLiteral_return stringLiteral49 = null;


        Object ID46_tree=null;
        Object ASSIGN47_tree=null;
        Object QUOTE48_tree=null;
        Object QUOTE50_tree=null;
        Object char_literal51_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:164:3: ( ID ASSIGN QUOTE stringLiteral QUOTE ';' )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:164:5: ID ASSIGN QUOTE stringLiteral QUOTE ';'
            {
            root_0 = (Object)adaptor.nil();

            ID46=(Token)match(input,ID,FOLLOW_ID_in_token1063); 
            ID46_tree = (Object)adaptor.create(ID46);
            root_0 = (Object)adaptor.becomeRoot(ID46_tree, root_0);

            ASSIGN47=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_token1066); 
            QUOTE48=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token1069); 
            pushFollow(FOLLOW_stringLiteral_in_token1072);
            stringLiteral49=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral49.getTree());
            QUOTE50=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token1074); 
            char_literal51=(Token)match(input,24,FOLLOW_24_in_token1077); 

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
    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:167:1: stringLiteral : ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* ;
    public final AstcParser.stringLiteral_return stringLiteral() throws RecognitionException {
        AstcParser.stringLiteral_return retval = new AstcParser.stringLiteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID52=null;
        Token NormalChar53=null;
        Token char_literal54=null;
        Token string_literal55=null;
        Token string_literal56=null;
        Token char_literal57=null;
        Token JAVANAME58=null;

        Object ID52_tree=null;
        Object NormalChar53_tree=null;
        Object char_literal54_tree=null;
        Object string_literal55_tree=null;
        Object string_literal56_tree=null;
        Object char_literal57_tree=null;
        Object JAVANAME58_tree=null;

        try {
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:5: ( ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* )
            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            loop14:
            do {
                int alt14=8;
                switch ( input.LA(1) ) {
                case ID:
                    {
                    alt14=1;
                    }
                    break;
                case NormalChar:
                    {
                    alt14=2;
                    }
                    break;
                case 33:
                    {
                    alt14=3;
                    }
                    break;
                case 34:
                    {
                    alt14=4;
                    }
                    break;
                case 35:
                    {
                    alt14=5;
                    }
                    break;
                case COLON:
                    {
                    alt14=6;
                    }
                    break;
                case JAVANAME:
                    {
                    alt14=7;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:12: ID
            	    {
            	    ID52=(Token)match(input,ID,FOLLOW_ID_in_stringLiteral1100); 
            	    ID52_tree = (Object)adaptor.create(ID52);
            	    adaptor.addChild(root_0, ID52_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:17: NormalChar
            	    {
            	    NormalChar53=(Token)match(input,NormalChar,FOLLOW_NormalChar_in_stringLiteral1104); 
            	    NormalChar53_tree = (Object)adaptor.create(NormalChar53);
            	    adaptor.addChild(root_0, NormalChar53_tree);


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:30: '+'
            	    {
            	    char_literal54=(Token)match(input,33,FOLLOW_33_in_stringLiteral1108); 
            	    char_literal54_tree = (Object)adaptor.create(char_literal54);
            	    adaptor.addChild(root_0, char_literal54_tree);


            	    }
            	    break;
            	case 4 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:34: '||'
            	    {
            	    string_literal55=(Token)match(input,34,FOLLOW_34_in_stringLiteral1110); 
            	    string_literal55_tree = (Object)adaptor.create(string_literal55);
            	    adaptor.addChild(root_0, string_literal55_tree);


            	    }
            	    break;
            	case 5 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:39: '&&'
            	    {
            	    string_literal56=(Token)match(input,35,FOLLOW_35_in_stringLiteral1112); 
            	    string_literal56_tree = (Object)adaptor.create(string_literal56);
            	    adaptor.addChild(root_0, string_literal56_tree);


            	    }
            	    break;
            	case 6 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:44: ( ':' )
            	    {
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:44: ( ':' )
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:45: ':'
            	    {
            	    char_literal57=(Token)match(input,COLON,FOLLOW_COLON_in_stringLiteral1115); 
            	    char_literal57_tree = (Object)adaptor.create(char_literal57);
            	    adaptor.addChild(root_0, char_literal57_tree);


            	    }


            	    }
            	    break;
            	case 7 :
            	    // C:\\overture\\astV2\\tools\\astCreator\\src\\main\\resources\\Astc.g:168:52: JAVANAME
            	    {
            	    JAVANAME58=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_stringLiteral1120); 
            	    JAVANAME58_tree = (Object)adaptor.create(JAVANAME58);
            	    adaptor.addChild(root_0, JAVANAME58_tree);


            	    }
            	    break;

            	default :
            	    break loop14;
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


 

    public static final BitSet FOLLOW_toks_in_root679 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ast_in_root681 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_aspectdcl_in_root683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AST_in_ast701 = new BitSet(new long[]{0x0000000004001002L});
    public static final BitSet FOLLOW_production_in_ast706 = new BitSet(new long[]{0x0000000004001002L});
    public static final BitSet FOLLOW_TOKENS_in_toks724 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_token_in_toks729 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ASPECT_DCL_in_aspectdcl747 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_aspectdcla_in_aspectdcl751 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_aspectdcl753 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ID_in_aspectdcla772 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_aspectdcla775 = new BitSet(new long[]{0x0000000020003002L});
    public static final BitSet FOLLOW_definitions_in_aspectdcla780 = new BitSet(new long[]{0x0000000020003002L});
    public static final BitSet FOLLOW_name_in_production800 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_productionfields_in_production802 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_production805 = new BitSet(new long[]{0x000000002F003000L});
    public static final BitSet FOLLOW_alternative_in_production807 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_25_in_production810 = new BitSet(new long[]{0x000000002F003000L});
    public static final BitSet FOLLOW_alternative_in_production812 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_24_in_production816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_name858 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_name860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_productionfields885 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_FIELD_DCL_in_productionfields888 = new BitSet(new long[]{0x0000000010001000L});
    public static final BitSet FOLLOW_productionfield_in_productionfields891 = new BitSet(new long[]{0x0000000010001000L});
    public static final BitSet FOLLOW_28_in_productionfields894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_productionfield910 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_productionfield913 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield916 = new BitSet(new long[]{0x0000000E00403420L});
    public static final BitSet FOLLOW_stringLiteral_in_productionfield919 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_alternative937 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative939 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_alternative941 = new BitSet(new long[]{0x0000000020003002L});
    public static final BitSet FOLLOW_definitions_in_alternative946 = new BitSet(new long[]{0x0000000020003002L});
    public static final BitSet FOLLOW_26_in_alternative965 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_definitions992 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions995 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_definitions997 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions1000 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_set_in_definitions1005 = new BitSet(new long[]{0x0000000380000002L});
    public static final BitSet FOLLOW_repeat_in_definitions1014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_repeat0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_token1063 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_token1066 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token1069 = new BitSet(new long[]{0x0000000E00403420L});
    public static final BitSet FOLLOW_stringLiteral_in_token1072 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token1074 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_token1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stringLiteral1100 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_NormalChar_in_stringLiteral1104 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_33_in_stringLiteral1108 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_34_in_stringLiteral1110 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_35_in_stringLiteral1112 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_COLON_in_stringLiteral1115 = new BitSet(new long[]{0x0000000E00403022L});
    public static final BitSet FOLLOW_JAVANAME_in_stringLiteral1120 = new BitSet(new long[]{0x0000000E00403022L});

}