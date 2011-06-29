// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\overture\\astV2\\astCreator\\Astc.g 2011-06-29 22:05:51

package com.lausdahl.ast.creator.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class AstcParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ASSIGN", "COLON", "AST", "TOKENS", "ASPECT_DCL", "FIELD_DCL", "QUOTE", "COMMENT", "ID", "JAVANAME", "INT", "EXPONENT", "FLOAT", "WS", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "ESC_SEQ", "NormalChar", "SpecialChar", "';'", "'|'", "'{'", "'}'", "'['", "']'", "'?'", "'*'", "'+'", "'||'", "'&&'"
    };
    public static final int EOF=-1;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int ASSIGN=4;
    public static final int COLON=5;
    public static final int AST=6;
    public static final int TOKENS=7;
    public static final int ASPECT_DCL=8;
    public static final int FIELD_DCL=9;
    public static final int QUOTE=10;
    public static final int COMMENT=11;
    public static final int ID=12;
    public static final int JAVANAME=13;
    public static final int INT=14;
    public static final int EXPONENT=15;
    public static final int FLOAT=16;
    public static final int WS=17;
    public static final int HEX_DIGIT=18;
    public static final int UNICODE_ESC=19;
    public static final int OCTAL_ESC=20;
    public static final int ESC_SEQ=21;
    public static final int NormalChar=22;
    public static final int SpecialChar=23;

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
    public String getGrammarFileName() { return "C:\\overture\\astV2\\astCreator\\Astc.g"; }


    public static class root_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "root"
    // C:\\overture\\astV2\\astCreator\\Astc.g:102:1: root : toks ast aspectdcl ;
    public final AstcParser.root_return root() throws RecognitionException {
        AstcParser.root_return retval = new AstcParser.root_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        AstcParser.toks_return toks1 = null;

        AstcParser.ast_return ast2 = null;

        AstcParser.aspectdcl_return aspectdcl3 = null;



        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:103:3: ( toks ast aspectdcl )
            // C:\\overture\\astV2\\astCreator\\Astc.g:103:5: toks ast aspectdcl
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
    // C:\\overture\\astV2\\astCreator\\Astc.g:106:1: ast : AST ( ( production )* ) ;
    public final AstcParser.ast_return ast() throws RecognitionException {
        AstcParser.ast_return retval = new AstcParser.ast_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AST4=null;
        AstcParser.production_return production5 = null;


        Object AST4_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:107:3: ( AST ( ( production )* ) )
            // C:\\overture\\astV2\\astCreator\\Astc.g:107:5: AST ( ( production )* )
            {
            root_0 = (Object)adaptor.nil();

            AST4=(Token)match(input,AST,FOLLOW_AST_in_ast701); 
            AST4_tree = (Object)adaptor.create(AST4);
            root_0 = (Object)adaptor.becomeRoot(AST4_tree, root_0);

            // C:\\overture\\astV2\\astCreator\\Astc.g:107:10: ( ( production )* )
            // C:\\overture\\astV2\\astCreator\\Astc.g:107:11: ( production )*
            {
            // C:\\overture\\astV2\\astCreator\\Astc.g:107:11: ( production )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:107:12: production
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
    // C:\\overture\\astV2\\astCreator\\Astc.g:110:1: toks : TOKENS ( ( token )* ) ;
    public final AstcParser.toks_return toks() throws RecognitionException {
        AstcParser.toks_return retval = new AstcParser.toks_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TOKENS6=null;
        AstcParser.token_return token7 = null;


        Object TOKENS6_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:111:3: ( TOKENS ( ( token )* ) )
            // C:\\overture\\astV2\\astCreator\\Astc.g:111:5: TOKENS ( ( token )* )
            {
            root_0 = (Object)adaptor.nil();

            TOKENS6=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_toks724); 
            TOKENS6_tree = (Object)adaptor.create(TOKENS6);
            root_0 = (Object)adaptor.becomeRoot(TOKENS6_tree, root_0);

            // C:\\overture\\astV2\\astCreator\\Astc.g:111:13: ( ( token )* )
            // C:\\overture\\astV2\\astCreator\\Astc.g:111:14: ( token )*
            {
            // C:\\overture\\astV2\\astCreator\\Astc.g:111:14: ( token )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:111:15: token
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
    // C:\\overture\\astV2\\astCreator\\Astc.g:114:1: aspectdcl : ASPECT_DCL aspectdcla ';' ;
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
            // C:\\overture\\astV2\\astCreator\\Astc.g:115:3: ( ASPECT_DCL aspectdcla ';' )
            // C:\\overture\\astV2\\astCreator\\Astc.g:115:5: ASPECT_DCL aspectdcla ';'
            {
            root_0 = (Object)adaptor.nil();

            ASPECT_DCL8=(Token)match(input,ASPECT_DCL,FOLLOW_ASPECT_DCL_in_aspectdcl747); 
            ASPECT_DCL8_tree = (Object)adaptor.create(ASPECT_DCL8);
            root_0 = (Object)adaptor.becomeRoot(ASPECT_DCL8_tree, root_0);

            pushFollow(FOLLOW_aspectdcla_in_aspectdcl750);
            aspectdcla9=aspectdcla();

            state._fsp--;

            adaptor.addChild(root_0, aspectdcla9.getTree());
            char_literal10=(Token)match(input,24,FOLLOW_24_in_aspectdcl752); 

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
    // C:\\overture\\astV2\\astCreator\\Astc.g:118:1: aspectdcla : ID ASSIGN ( ( definitions )* ) ;
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
            // C:\\overture\\astV2\\astCreator\\Astc.g:119:3: ( ID ASSIGN ( ( definitions )* ) )
            // C:\\overture\\astV2\\astCreator\\Astc.g:119:6: ID ASSIGN ( ( definitions )* )
            {
            root_0 = (Object)adaptor.nil();

            ID11=(Token)match(input,ID,FOLLOW_ID_in_aspectdcla769); 
            ID11_tree = (Object)adaptor.create(ID11);
            root_0 = (Object)adaptor.becomeRoot(ID11_tree, root_0);

            ASSIGN12=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_aspectdcla772); 
            // C:\\overture\\astV2\\astCreator\\Astc.g:119:18: ( ( definitions )* )
            // C:\\overture\\astV2\\astCreator\\Astc.g:119:19: ( definitions )*
            {
            // C:\\overture\\astV2\\astCreator\\Astc.g:119:19: ( definitions )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ID||LA3_0==28) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:119:20: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_aspectdcla777);
            	    definitions13=definitions();

            	    state._fsp--;

            	    adaptor.addChild(root_0, definitions13.getTree());

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
    // $ANTLR end "aspectdcla"

    public static class production_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "production"
    // C:\\overture\\astV2\\astCreator\\Astc.g:124:1: production : ID ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID ( productionfields )? ( alternative )* ) ;
    public final AstcParser.production_return production() throws RecognitionException {
        AstcParser.production_return retval = new AstcParser.production_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID14=null;
        Token ASSIGN16=null;
        Token char_literal18=null;
        Token char_literal20=null;
        AstcParser.productionfields_return productionfields15 = null;

        AstcParser.alternative_return alternative17 = null;

        AstcParser.alternative_return alternative19 = null;


        Object ID14_tree=null;
        Object ASSIGN16_tree=null;
        Object char_literal18_tree=null;
        Object char_literal20_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_productionfields=new RewriteRuleSubtreeStream(adaptor,"rule productionfields");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:125:3: ( ID ( productionfields )? ASSIGN alternative ( '|' alternative )* ';' -> ^( ID ( productionfields )? ( alternative )* ) )
            // C:\\overture\\astV2\\astCreator\\Astc.g:125:5: ID ( productionfields )? ASSIGN alternative ( '|' alternative )* ';'
            {
            ID14=(Token)match(input,ID,FOLLOW_ID_in_production797);  
            stream_ID.add(ID14);

            // C:\\overture\\astV2\\astCreator\\Astc.g:125:8: ( productionfields )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==26) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // C:\\overture\\astV2\\astCreator\\Astc.g:125:8: productionfields
                    {
                    pushFollow(FOLLOW_productionfields_in_production799);
                    productionfields15=productionfields();

                    state._fsp--;

                    stream_productionfields.add(productionfields15.getTree());

                    }
                    break;

            }

            ASSIGN16=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_production802);  
            stream_ASSIGN.add(ASSIGN16);

            pushFollow(FOLLOW_alternative_in_production804);
            alternative17=alternative();

            state._fsp--;

            stream_alternative.add(alternative17.getTree());
            // C:\\overture\\astV2\\astCreator\\Astc.g:125:45: ( '|' alternative )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==25) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:125:46: '|' alternative
            	    {
            	    char_literal18=(Token)match(input,25,FOLLOW_25_in_production807);  
            	    stream_25.add(char_literal18);

            	    pushFollow(FOLLOW_alternative_in_production809);
            	    alternative19=alternative();

            	    state._fsp--;

            	    stream_alternative.add(alternative19.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            char_literal20=(Token)match(input,24,FOLLOW_24_in_production813);  
            stream_24.add(char_literal20);



            // AST REWRITE
            // elements: productionfields, alternative, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 125:68: -> ^( ID ( productionfields )? ( alternative )* )
            {
                // C:\\overture\\astV2\\astCreator\\Astc.g:125:71: ^( ID ( productionfields )? ( alternative )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                // C:\\overture\\astV2\\astCreator\\Astc.g:125:76: ( productionfields )?
                if ( stream_productionfields.hasNext() ) {
                    adaptor.addChild(root_1, stream_productionfields.nextTree());

                }
                stream_productionfields.reset();
                // C:\\overture\\astV2\\astCreator\\Astc.g:125:94: ( alternative )*
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

    public static class productionfields_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "productionfields"
    // C:\\overture\\astV2\\astCreator\\Astc.g:129:1: productionfields : '{' FIELD_DCL ( productionfield )* '}' ;
    public final AstcParser.productionfields_return productionfields() throws RecognitionException {
        AstcParser.productionfields_return retval = new AstcParser.productionfields_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal21=null;
        Token FIELD_DCL22=null;
        Token char_literal24=null;
        AstcParser.productionfield_return productionfield23 = null;


        Object char_literal21_tree=null;
        Object FIELD_DCL22_tree=null;
        Object char_literal24_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:130:3: ( '{' FIELD_DCL ( productionfield )* '}' )
            // C:\\overture\\astV2\\astCreator\\Astc.g:130:5: '{' FIELD_DCL ( productionfield )* '}'
            {
            root_0 = (Object)adaptor.nil();

            char_literal21=(Token)match(input,26,FOLLOW_26_in_productionfields846); 
            FIELD_DCL22=(Token)match(input,FIELD_DCL,FOLLOW_FIELD_DCL_in_productionfields849); 
            FIELD_DCL22_tree = (Object)adaptor.create(FIELD_DCL22);
            root_0 = (Object)adaptor.becomeRoot(FIELD_DCL22_tree, root_0);

            // C:\\overture\\astV2\\astCreator\\Astc.g:130:21: ( productionfield )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ID) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:130:21: productionfield
            	    {
            	    pushFollow(FOLLOW_productionfield_in_productionfields852);
            	    productionfield23=productionfield();

            	    state._fsp--;

            	    adaptor.addChild(root_0, productionfield23.getTree());

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            char_literal24=(Token)match(input,27,FOLLOW_27_in_productionfields855); 

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
    // C:\\overture\\astV2\\astCreator\\Astc.g:133:1: productionfield : ID ASSIGN QUOTE stringLiteral QUOTE ;
    public final AstcParser.productionfield_return productionfield() throws RecognitionException {
        AstcParser.productionfield_return retval = new AstcParser.productionfield_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID25=null;
        Token ASSIGN26=null;
        Token QUOTE27=null;
        Token QUOTE29=null;
        AstcParser.stringLiteral_return stringLiteral28 = null;


        Object ID25_tree=null;
        Object ASSIGN26_tree=null;
        Object QUOTE27_tree=null;
        Object QUOTE29_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:134:3: ( ID ASSIGN QUOTE stringLiteral QUOTE )
            // C:\\overture\\astV2\\astCreator\\Astc.g:134:5: ID ASSIGN QUOTE stringLiteral QUOTE
            {
            root_0 = (Object)adaptor.nil();

            ID25=(Token)match(input,ID,FOLLOW_ID_in_productionfield871); 
            ID25_tree = (Object)adaptor.create(ID25);
            root_0 = (Object)adaptor.becomeRoot(ID25_tree, root_0);

            ASSIGN26=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_productionfield874); 
            QUOTE27=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield877); 
            pushFollow(FOLLOW_stringLiteral_in_productionfield880);
            stringLiteral28=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral28.getTree());
            QUOTE29=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_productionfield882); 

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
    // C:\\overture\\astV2\\astCreator\\Astc.g:137:1: alternative : ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) ;
    public final AstcParser.alternative_return alternative() throws RecognitionException {
        AstcParser.alternative_return retval = new AstcParser.alternative_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal30=null;
        Token ID31=null;
        Token char_literal32=null;
        AstcParser.definitions_return definitions33 = null;


        Object char_literal30_tree=null;
        Object ID31_tree=null;
        Object char_literal32_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleSubtreeStream stream_definitions=new RewriteRuleSubtreeStream(adaptor,"rule definitions");
        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:138:3: ( ( '{' ID '}' )? ( definitions )* -> ^( ID ( definitions )* ) )
            // C:\\overture\\astV2\\astCreator\\Astc.g:138:5: ( '{' ID '}' )? ( definitions )*
            {
            // C:\\overture\\astV2\\astCreator\\Astc.g:138:5: ( '{' ID '}' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==26) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\overture\\astV2\\astCreator\\Astc.g:138:6: '{' ID '}'
                    {
                    char_literal30=(Token)match(input,26,FOLLOW_26_in_alternative898);  
                    stream_26.add(char_literal30);

                    ID31=(Token)match(input,ID,FOLLOW_ID_in_alternative900);  
                    stream_ID.add(ID31);

                    char_literal32=(Token)match(input,27,FOLLOW_27_in_alternative902);  
                    stream_27.add(char_literal32);


                    }
                    break;

            }

            // C:\\overture\\astV2\\astCreator\\Astc.g:138:19: ( definitions )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ID||LA8_0==28) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:138:20: definitions
            	    {
            	    pushFollow(FOLLOW_definitions_in_alternative907);
            	    definitions33=definitions();

            	    state._fsp--;

            	    stream_definitions.add(definitions33.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
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
            // 138:34: -> ^( ID ( definitions )* )
            {
                // C:\\overture\\astV2\\astCreator\\Astc.g:138:37: ^( ID ( definitions )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(stream_ID.nextNode(), root_1);

                // C:\\overture\\astV2\\astCreator\\Astc.g:138:42: ( definitions )*
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
    // $ANTLR end "alternative"

    public static class definitions_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "definitions"
    // C:\\overture\\astV2\\astCreator\\Astc.g:141:1: definitions : ( '[' ID ']' ':' )? ID ( repeat )? ;
    public final AstcParser.definitions_return definitions() throws RecognitionException {
        AstcParser.definitions_return retval = new AstcParser.definitions_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal34=null;
        Token ID35=null;
        Token char_literal36=null;
        Token char_literal37=null;
        Token ID38=null;
        AstcParser.repeat_return repeat39 = null;


        Object char_literal34_tree=null;
        Object ID35_tree=null;
        Object char_literal36_tree=null;
        Object char_literal37_tree=null;
        Object ID38_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:142:3: ( ( '[' ID ']' ':' )? ID ( repeat )? )
            // C:\\overture\\astV2\\astCreator\\Astc.g:142:5: ( '[' ID ']' ':' )? ID ( repeat )?
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\astCreator\\Astc.g:142:5: ( '[' ID ']' ':' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==28) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\overture\\astV2\\astCreator\\Astc.g:142:6: '[' ID ']' ':'
                    {
                    char_literal34=(Token)match(input,28,FOLLOW_28_in_definitions936); 
                    ID35=(Token)match(input,ID,FOLLOW_ID_in_definitions939); 
                    ID35_tree = (Object)adaptor.create(ID35);
                    adaptor.addChild(root_0, ID35_tree);

                    char_literal36=(Token)match(input,29,FOLLOW_29_in_definitions941); 
                    char_literal37=(Token)match(input,COLON,FOLLOW_COLON_in_definitions944); 

                    }
                    break;

            }

            ID38=(Token)match(input,ID,FOLLOW_ID_in_definitions949); 
            ID38_tree = (Object)adaptor.create(ID38);
            root_0 = (Object)adaptor.becomeRoot(ID38_tree, root_0);

            // C:\\overture\\astV2\\astCreator\\Astc.g:142:30: ( repeat )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=30 && LA10_0<=32)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // C:\\overture\\astV2\\astCreator\\Astc.g:142:31: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_definitions953);
                    repeat39=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat39.getTree());

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
    // C:\\overture\\astV2\\astCreator\\Astc.g:145:1: repeat : ( '?' | '*' | '+' );
    public final AstcParser.repeat_return repeat() throws RecognitionException {
        AstcParser.repeat_return retval = new AstcParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set40=null;

        Object set40_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:146:3: ( '?' | '*' | '+' )
            // C:\\overture\\astV2\\astCreator\\Astc.g:
            {
            root_0 = (Object)adaptor.nil();

            set40=(Token)input.LT(1);
            if ( (input.LA(1)>=30 && input.LA(1)<=32) ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set40));
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
    // C:\\overture\\astV2\\astCreator\\Astc.g:152:1: token : ID ASSIGN QUOTE stringLiteral QUOTE ';' ;
    public final AstcParser.token_return token() throws RecognitionException {
        AstcParser.token_return retval = new AstcParser.token_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID41=null;
        Token ASSIGN42=null;
        Token QUOTE43=null;
        Token QUOTE45=null;
        Token char_literal46=null;
        AstcParser.stringLiteral_return stringLiteral44 = null;


        Object ID41_tree=null;
        Object ASSIGN42_tree=null;
        Object QUOTE43_tree=null;
        Object QUOTE45_tree=null;
        Object char_literal46_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:153:3: ( ID ASSIGN QUOTE stringLiteral QUOTE ';' )
            // C:\\overture\\astV2\\astCreator\\Astc.g:153:5: ID ASSIGN QUOTE stringLiteral QUOTE ';'
            {
            root_0 = (Object)adaptor.nil();

            ID41=(Token)match(input,ID,FOLLOW_ID_in_token996); 
            ID41_tree = (Object)adaptor.create(ID41);
            root_0 = (Object)adaptor.becomeRoot(ID41_tree, root_0);

            ASSIGN42=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_token999); 
            QUOTE43=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token1002); 
            pushFollow(FOLLOW_stringLiteral_in_token1005);
            stringLiteral44=stringLiteral();

            state._fsp--;

            adaptor.addChild(root_0, stringLiteral44.getTree());
            QUOTE45=(Token)match(input,QUOTE,FOLLOW_QUOTE_in_token1007); 
            char_literal46=(Token)match(input,24,FOLLOW_24_in_token1010); 

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
    // C:\\overture\\astV2\\astCreator\\Astc.g:156:1: stringLiteral : ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* ;
    public final AstcParser.stringLiteral_return stringLiteral() throws RecognitionException {
        AstcParser.stringLiteral_return retval = new AstcParser.stringLiteral_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ID47=null;
        Token NormalChar48=null;
        Token char_literal49=null;
        Token string_literal50=null;
        Token string_literal51=null;
        Token char_literal52=null;
        Token JAVANAME53=null;

        Object ID47_tree=null;
        Object NormalChar48_tree=null;
        Object char_literal49_tree=null;
        Object string_literal50_tree=null;
        Object string_literal51_tree=null;
        Object char_literal52_tree=null;
        Object JAVANAME53_tree=null;

        try {
            // C:\\overture\\astV2\\astCreator\\Astc.g:157:5: ( ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )* )
            // C:\\overture\\astV2\\astCreator\\Astc.g:157:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            {
            root_0 = (Object)adaptor.nil();

            // C:\\overture\\astV2\\astCreator\\Astc.g:157:11: ( ID | NormalChar | '+' | '||' | '&&' | ( ':' ) | JAVANAME )*
            loop11:
            do {
                int alt11=8;
                switch ( input.LA(1) ) {
                case ID:
                    {
                    alt11=1;
                    }
                    break;
                case NormalChar:
                    {
                    alt11=2;
                    }
                    break;
                case 32:
                    {
                    alt11=3;
                    }
                    break;
                case 33:
                    {
                    alt11=4;
                    }
                    break;
                case 34:
                    {
                    alt11=5;
                    }
                    break;
                case COLON:
                    {
                    alt11=6;
                    }
                    break;
                case JAVANAME:
                    {
                    alt11=7;
                    }
                    break;

                }

                switch (alt11) {
            	case 1 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:12: ID
            	    {
            	    ID47=(Token)match(input,ID,FOLLOW_ID_in_stringLiteral1033); 
            	    ID47_tree = (Object)adaptor.create(ID47);
            	    adaptor.addChild(root_0, ID47_tree);


            	    }
            	    break;
            	case 2 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:17: NormalChar
            	    {
            	    NormalChar48=(Token)match(input,NormalChar,FOLLOW_NormalChar_in_stringLiteral1037); 
            	    NormalChar48_tree = (Object)adaptor.create(NormalChar48);
            	    adaptor.addChild(root_0, NormalChar48_tree);


            	    }
            	    break;
            	case 3 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:30: '+'
            	    {
            	    char_literal49=(Token)match(input,32,FOLLOW_32_in_stringLiteral1041); 
            	    char_literal49_tree = (Object)adaptor.create(char_literal49);
            	    adaptor.addChild(root_0, char_literal49_tree);


            	    }
            	    break;
            	case 4 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:34: '||'
            	    {
            	    string_literal50=(Token)match(input,33,FOLLOW_33_in_stringLiteral1043); 
            	    string_literal50_tree = (Object)adaptor.create(string_literal50);
            	    adaptor.addChild(root_0, string_literal50_tree);


            	    }
            	    break;
            	case 5 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:39: '&&'
            	    {
            	    string_literal51=(Token)match(input,34,FOLLOW_34_in_stringLiteral1045); 
            	    string_literal51_tree = (Object)adaptor.create(string_literal51);
            	    adaptor.addChild(root_0, string_literal51_tree);


            	    }
            	    break;
            	case 6 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:44: ( ':' )
            	    {
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:44: ( ':' )
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:45: ':'
            	    {
            	    char_literal52=(Token)match(input,COLON,FOLLOW_COLON_in_stringLiteral1048); 
            	    char_literal52_tree = (Object)adaptor.create(char_literal52);
            	    adaptor.addChild(root_0, char_literal52_tree);


            	    }


            	    }
            	    break;
            	case 7 :
            	    // C:\\overture\\astV2\\astCreator\\Astc.g:157:52: JAVANAME
            	    {
            	    JAVANAME53=(Token)match(input,JAVANAME,FOLLOW_JAVANAME_in_stringLiteral1053); 
            	    JAVANAME53_tree = (Object)adaptor.create(JAVANAME53);
            	    adaptor.addChild(root_0, JAVANAME53_tree);


            	    }
            	    break;

            	default :
            	    break loop11;
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
    public static final BitSet FOLLOW_AST_in_ast701 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_production_in_ast706 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_TOKENS_in_toks724 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_token_in_toks729 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ASPECT_DCL_in_aspectdcl747 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_aspectdcla_in_aspectdcl750 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_aspectdcl752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_aspectdcla769 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_aspectdcla772 = new BitSet(new long[]{0x0000000010001002L});
    public static final BitSet FOLLOW_definitions_in_aspectdcla777 = new BitSet(new long[]{0x0000000010001002L});
    public static final BitSet FOLLOW_ID_in_production797 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_productionfields_in_production799 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_production802 = new BitSet(new long[]{0x0000000017001000L});
    public static final BitSet FOLLOW_alternative_in_production804 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_25_in_production807 = new BitSet(new long[]{0x0000000017001000L});
    public static final BitSet FOLLOW_alternative_in_production809 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_24_in_production813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_productionfields846 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_FIELD_DCL_in_productionfields849 = new BitSet(new long[]{0x0000000008001000L});
    public static final BitSet FOLLOW_productionfield_in_productionfields852 = new BitSet(new long[]{0x0000000008001000L});
    public static final BitSet FOLLOW_27_in_productionfields855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_productionfield871 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_productionfield874 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield877 = new BitSet(new long[]{0x0000000700403420L});
    public static final BitSet FOLLOW_stringLiteral_in_productionfield880 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_productionfield882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_alternative898 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_alternative900 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_alternative902 = new BitSet(new long[]{0x0000000010001002L});
    public static final BitSet FOLLOW_definitions_in_alternative907 = new BitSet(new long[]{0x0000000010001002L});
    public static final BitSet FOLLOW_28_in_definitions936 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions939 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_definitions941 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_COLON_in_definitions944 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_ID_in_definitions949 = new BitSet(new long[]{0x00000001C0000002L});
    public static final BitSet FOLLOW_repeat_in_definitions953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_repeat0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_token996 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ASSIGN_in_token999 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token1002 = new BitSet(new long[]{0x0000000700403420L});
    public static final BitSet FOLLOW_stringLiteral_in_token1005 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_QUOTE_in_token1007 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_token1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stringLiteral1033 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_NormalChar_in_stringLiteral1037 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_32_in_stringLiteral1041 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_33_in_stringLiteral1043 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_34_in_stringLiteral1045 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_COLON_in_stringLiteral1048 = new BitSet(new long[]{0x0000000700403022L});
    public static final BitSet FOLLOW_JAVANAME_in_stringLiteral1053 = new BitSet(new long[]{0x0000000700403022L});

}