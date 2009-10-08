// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstParser.java

package org.overturetool.tools.astgen.vdm;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;
import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstParserTokens, AstParserVal, AstScanner, AstDefinitions, 
//            AstComposite, AstShorthand, AstType, AstField, 
//            AstTypeName, AstQuotedType, AstUnionType, AstOptionalType, 
//            AstSeqOfType, AstSetOfType, AstMapType

public class AstParser
    implements AstParserTokens
{

    void debug(String msg)
    {
        if(yydebug)
            System.out.println(msg);
    }

    final void state_push(int state)
    {
        try
        {
            stateptr++;
            statestk[stateptr] = state;
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            int oldsize = statestk.length;
            int newsize = oldsize * 2;
            int newstack[] = new int[newsize];
            System.arraycopy(statestk, 0, newstack, 0, oldsize);
            statestk = newstack;
            statestk[stateptr] = state;
        }
    }

    final int state_pop()
    {
        return statestk[stateptr--];
    }

    final void state_drop(int cnt)
    {
        stateptr -= cnt;
    }

    final int state_peek(int relative)
    {
        return statestk[stateptr - relative];
    }

    final boolean init_stacks()
    {
        stateptr = -1;
        val_init();
        return true;
    }

    void dump_stacks(int count)
    {
        System.out.println((new StringBuilder("=index==state====value=     s:")).append(stateptr).append("  v:").append(valptr).toString());
        for(int i = 0; i < count; i++)
            System.out.println((new StringBuilder(" ")).append(i).append("    ").append(statestk[i]).append("      ").append(valstk[i]).toString());

        System.out.println("======================");
    }

    void val_init()
    {
        valstk = new AstParserVal[500];
        yyval = new AstParserVal();
        yylval = new AstParserVal();
        valptr = -1;
    }

    void val_push(AstParserVal val)
    {
        if(valptr >= 500)
        {
            return;
        } else
        {
            valstk[++valptr] = val;
            return;
        }
    }

    AstParserVal val_pop()
    {
        if(valptr < 0)
            return new AstParserVal();
        else
            return valstk[valptr--];
    }

    void val_drop(int cnt)
    {
        int ptr = valptr - cnt;
        if(ptr < 0)
        {
            return;
        } else
        {
            valptr = ptr;
            return;
        }
    }

    AstParserVal val_peek(int relative)
    {
        int ptr = valptr - relative;
        if(ptr < 0)
            return new AstParserVal();
        else
            return valstk[ptr];
    }

    static void yytable()
    {
        yytable = (new short[] {
            17, 34, 27, 23, 17, 19, 48, 19, 20, 24, 
            22, 44, 45, 52, 46, 1, 47, 15, 27, 28, 
            29, 2, 30, 16, 31, 21, 50, 3, 4, 24, 
            5, 32, 53, 2, 6, 51, 54, 48, 23, 3, 
            4, 35, 5, 34, 42, 48, 6, 1, 49, 3, 
            36, 37, 38, 39, 40, 41, 0, 0, 43, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
            0, 0, 0, 0, 0, 0, 0, 0, 0, 34, 
            27, 23, 17, 34, 27, 19, 34, 24, 22, 0, 
            0, 0, 0, 0, 0, 34
        });
    }

    static void yycheck()
    {
        yycheck = (new short[] {
            262, 0, 0, 0, 0, 262, 267, 0, 272, 0, 
            0, 27, 28, 274, 30, 256, 32, 257, 260, 261, 
            262, 262, 264, 263, 266, 262, 42, 268, 269, 262, 
            271, 273, 48, 262, 275, 265, 52, 267, 258, 268, 
            269, 262, 271, 270, 259, 267, 275, 0, 262, 0, 
            23, 23, 23, 23, 23, 23, -1, -1, 25, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
            -1, -1, -1, -1, -1, -1, -1, -1, -1, 258, 
            258, 258, 258, 262, 262, 258, 265, 258, 258, -1, 
            -1, -1, -1, -1, -1, 274
        });
    }

    public AstParser(String fname)
        throws CGException
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
        theScanner = new AstScanner(this, fname);
        theAst = new AstDefinitions();
    }

    public AstParser(String fname, boolean debug)
        throws CGException
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
        theScanner = new AstScanner(this, fname);
        theAst = new AstDefinitions();
        yydebug = debug;
    }

    private int yylex()
        throws IOException
    {
        return theScanner.yylex();
    }

    private void yyerror(String msg)
    {
        System.out.println((new StringBuilder(String.valueOf(msg))).append(theScanner.atPosition()).toString());
        errors++;
    }

    void yylexdebug(int state, int ch)
    {
        String s = null;
        if(ch < 0)
            ch = 0;
        if(ch <= 275)
            s = yyname[ch];
        if(s == null)
            s = "illegal-symbol";
        debug((new StringBuilder("state ")).append(state).append(", reading ").append(ch).append(" (").append(s).append(")").toString());
    }

  public  int yyparse()
        throws IOException, CGException
    {
        init_stacks();
        yynerrs = 0;
        yyerrflag = 0;
        yychar = -1;
        yystate = 0;
        state_push(yystate);
        do
        {
            boolean doaction;
            do
            {
                doaction = true;
                if(yydebug)
                    debug("loop");
                for(yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate])
                {
                    if(yydebug)
                        debug((new StringBuilder("yyn:")).append(yyn).append("  state:").append(yystate).append("  yychar:").append(yychar).toString());
                    if(yychar < 0)
                    {
                        yychar = yylex();
                        if(yydebug)
                            debug((new StringBuilder(" next yychar:")).append(yychar).toString());
                        if(yychar < 0)
                        {
                            yychar = 0;
                            if(yydebug)
                                yylexdebug(yystate, yychar);
                        }
                    }
                    yyn = yysindex[yystate];
                    if(yyn != 0 && (yyn += yychar) >= 0 && yyn <= 275 && yycheck[yyn] == yychar)
                    {
                        if(yydebug)
                            debug((new StringBuilder("state ")).append(yystate).append(", shifting to state ").append(yytable[yyn]).toString());
                        yystate = yytable[yyn];
                        state_push(yystate);
                        val_push(yylval);
                        yychar = -1;
                        if(yyerrflag > 0)
                            yyerrflag--;
                        doaction = false;
                        break;
                    }
                    yyn = yyrindex[yystate];
                    if(yyn != 0 && (yyn += yychar) >= 0 && yyn <= 275 && yycheck[yyn] == yychar)
                    {
                        if(yydebug)
                            debug("reduce");
                        yyn = yytable[yyn];
                        doaction = true;
                        break;
                    }
                    if(yyerrflag == 0)
                    {
                        yyerror("syntax error");
                        yynerrs++;
                    }
                    if(yyerrflag < 3)
                    {
                        yyerrflag = 3;
                        do
                        {
                            yyn = yysindex[state_peek(0)];
                            if(yyn != 0 && (yyn += 256) >= 0 && yyn <= 275 && yycheck[yyn] == 256)
                            {
                                if(yydebug)
                                    debug((new StringBuilder("state ")).append(state_peek(0)).append(", error recovery shifting to state ").append(yytable[yyn]).append(" ").toString());
                                yystate = yytable[yyn];
                                state_push(yystate);
                                val_push(yylval);
                                doaction = false;
                                break;
                            }
                            if(yydebug)
                                debug((new StringBuilder("error recovery discarding state ")).append(state_peek(0)).append(" ").toString());
                            state_pop();
                            val_pop();
                        } while(true);
                    } else
                    {
                        if(yychar == 0)
                            return 1;
                        if(yydebug)
                        {
                            yys = null;
                            if(yychar <= 275)
                                yys = yyname[yychar];
                            if(yys == null)
                                yys = "illegal-symbol";
                            debug((new StringBuilder("state ")).append(yystate).append(", error recovery discards token ").append(yychar).append(" (").append(yys).append(")").toString());
                        }
                        yychar = -1;
                    }
                }

            } while(!doaction);
            yym = yylen[yyn];
            if(yydebug)
                debug((new StringBuilder("state ")).append(yystate).append(", reducing ").append(yym).append(" by rule ").append(yyn).append(" (").append(yyrule[yyn]).append(")").toString());
            if(yym > 0)
                yyval = val_peek(yym - 1);
            switch(yyn)
            {
            case 4: // '\004'
            {
                AstComposite theComposite = (AstComposite)val_peek(0).obj;
                theAst.addComposite(theComposite);
                break;
            }

            case 5: // '\005'
            {
                AstShorthand theShorthand = (AstShorthand)val_peek(0).obj;
                theAst.addShorthand(theShorthand);
                break;
            }

            case 10: // '\n'
            {
                AstComposite theComposite = (AstComposite)val_peek(0).obj;
                theAst.addComposite(theComposite);
                break;
            }

            case 11: // '\013'
            {
                AstShorthand theShorthand = (AstShorthand)val_peek(0).obj;
                theAst.addShorthand(theShorthand);
                break;
            }

            case 16: // '\020'
            {
                theAst.setPrefix(val_peek(0).sval);
                break;
            }

            case 17: // '\021'
            {
                Vector thePackageList = (Vector)val_peek(0).obj;
                theAst.setPackage(thePackageList);
                break;
            }

            case 18: // '\022'
            {
                String theArg = val_peek(0).sval;
                theAst.setDirectory(theArg);
                break;
            }

            case 19: // '\023'
            {
                Vector theTopList = (Vector)val_peek(0).obj;
                theAst.setTop(theTopList);
                break;
            }

            case 20: // '\024'
            {
                Vector theRes = new Vector();
                theRes.add(val_peek(0).sval);
                yyval.obj = theRes;
                break;
            }

            case 21: // '\025'
            {
                Vector theRes = (Vector)val_peek(2).obj;
                theRes.add(val_peek(0).sval);
                yyval.obj = theRes;
                break;
            }

            case 22: // '\026'
            {
                AstType theArg = (AstType)val_peek(0).obj;
                AstShorthand theShorthand = new AstShorthand(val_peek(2).sval, theArg);
                yyval.obj = theShorthand;
                break;
            }

            case 23: // '\027'
            {
                Vector theArg = new Vector();
                AstComposite theComposite = new AstComposite(val_peek(1).sval, theArg);
                yyval.obj = theComposite;
                break;
            }

            case 24: // '\030'
            {
                Vector theArg = (Vector)val_peek(0).obj;
                AstComposite theComposite = new AstComposite(val_peek(2).sval, theArg);
                yyval.obj = theComposite;
                break;
            }

            case 25: // '\031'
            {
                AstField theArg = (AstField)val_peek(0).obj;
                Vector theRes = new Vector();
                theRes.add(theArg);
                yyval.obj = theRes;
                break;
            }

            case 26: // '\032'
            {
                AstField theArg = (AstField)val_peek(0).obj;
                Vector theRes = (Vector)val_peek(1).obj;
                theRes.add(theArg);
                yyval.obj = theRes;
                break;
            }

            case 27: // '\033'
            {
                AstType theArg = (AstType)val_peek(0).obj;
                AstField theField = new AstField(val_peek(2).sval, theArg);
                yyval.obj = theField;
                break;
            }

            case 28: // '\034'
            {
                AstTypeName theTypeName = new AstTypeName(val_peek(0).sval);
                yyval.obj = theTypeName;
                break;
            }

            case 29: // '\035'
            {
                AstQuotedType theQuotedType = new AstQuotedType(val_peek(0).sval);
                yyval.obj = theQuotedType;
                break;
            }

            case 30: // '\036'
            {
                AstType theLhs = (AstType)val_peek(2).obj;
                AstType theRhs = (AstType)val_peek(0).obj;
                AstUnionType theUnionType = new AstUnionType(theLhs, theRhs);
                yyval.obj = theUnionType;
                break;
            }

            case 31: // '\037'
            {
                AstType theArg = (AstType)val_peek(1).obj;
                AstOptionalType theOptionalType = new AstOptionalType(theArg);
                yyval.obj = theOptionalType;
                break;
            }

            case 32: // ' '
            {
                AstType theArg = (AstType)val_peek(0).obj;
                AstSeqOfType theSeqOfType = new AstSeqOfType(theArg);
                yyval.obj = theSeqOfType;
                break;
            }

            case 33: // '!'
            {
                AstType theArg = (AstType)val_peek(0).obj;
                AstSetOfType theSetOfType = new AstSetOfType(theArg);
                yyval.obj = theSetOfType;
                break;
            }

            case 34: // '"'
            {
                AstType theArg1 = (AstType)val_peek(2).obj;
                AstType theArg2 = (AstType)val_peek(0).obj;
                AstMapType theMapType = new AstMapType(theArg1, theArg2);
                yyval.obj = theMapType;
                break;
            }

            case 35: // '#'
            {
                Vector res = new Vector();
                res.add(val_peek(0).sval);
                yyval.obj = res;
                break;
            }

            case 36: // '$'
            {
                Vector res = (Vector)val_peek(1).obj;
                res.add(val_peek(0).sval);
                yyval.obj = res;
                break;
            }
            }
            if(yydebug)
                debug("reduce");
            state_drop(yym);
            yystate = state_peek(0);
            val_drop(yym);
            yym = yylhs[yyn];
            if(yystate == 0 && yym == 0)
            {
                if(yydebug)
                    debug("After reduction, shifting from state 0 to state 7");
                yystate = 7;
                state_push(7);
                val_push(yyval);
                if(yychar < 0)
                {
                    yychar = yylex();
                    if(yychar < 0)
                        yychar = 0;
                    if(yydebug)
                        yylexdebug(yystate, yychar);
                }
                if(yychar == 0)
                    break;
            } else
            {
                yyn = yygindex[yym];
                if(yyn != 0 && (yyn += yystate) >= 0 && yyn <= 275 && yycheck[yyn] == yystate)
                    yystate = yytable[yyn];
                else
                    yystate = yydgoto[yym];
                if(yydebug)
                    debug((new StringBuilder("after reduction, shifting from state ")).append(state_peek(0)).append(" to state ").append(yystate).toString());
                state_push(yystate);
                val_push(yyval);
            }
        } while(true);
        return 0;
    }

    public AstParser()
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
    }

    public AstParser(boolean debugMe)
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
        yydebug = debugMe;
    }

    boolean yydebug;
    int yynerrs;
    int yyerrflag;
    int yychar;
    static final int YYSTACKSIZE = 500;
    int statestk[];
    int stateptr;
    int stateptrmax;
    int statemax;
    String yytext;
    AstParserVal yyval;
    AstParserVal yylval;
    AstParserVal valstk[];
    int valptr;
    public static final short YYERRCODE = 256;
    static final short yylhs[] = {
        -1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 4, 5, 6, 7, 
        8, 8, 3, 2, 2, 11, 11, 12, 10, 10, 
        10, 10, 10, 10, 10, 9, 9
    };
    static final short yylen[] = {
        2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 
        3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 
        1, 3, 3, 2, 3, 1, 2, 3, 1, 1, 
        3, 3, 2, 2, 4, 1, 2
    };
    static final short yydefred[] = {
        0, 2, 0, 0, 0, 0, 0, 0, 0, 4, 
        5, 6, 7, 8, 9, 0, 0, 20, 0, 16, 
        18, 35, 0, 0, 0, 0, 25, 0, 0, 28, 
        0, 29, 0, 0, 0, 36, 10, 11, 12, 13, 
        14, 15, 0, 26, 32, 33, 0, 0, 0, 21, 
        0, 31, 0, 30, 0
    };
    static final short yydgoto[] = {
        7, 8, 9, 10, 11, 12, 13, 14, 18, 22, 
        33, 25, 26
    };
    static final short yysindex[] = {
        -241, 0, -240, -262, -257, -264, -237, 0, -220, 0, 
        0, 0, 0, 0, 0, -233, -242, 0, -227, 0, 
        0, 0, -221, -229, -215, -233, 0, -242, -242, 0, 
        -242, 0, -242, -222, -214, 0, 0, 0, 0, 0, 
        0, 0, -242, 0, 0, 0, -230, -261, -242, 0, 
        -222, 0, -242, 0, -222
    };
    static final short yyrindex[] = {
        47, 0, 0, 0, 0, 0, 0, 0, 49, 0, 
        0, 0, 0, 0, 0, 3, 0, 0, 4, 0, 
        0, 0, 7, 0, 0, 9, 0, 0, 0, 0, 
        0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        2, 0, 0, 0, 1
    };
    static final short yygindex[] = {
        0, 0, 27, 28, 29, 30, 31, 32, 0, 0, 
        -16, 0, 33
    };
    static final int YYTABLESIZE = 275;
    static short yytable[];
    static short yycheck[];
    static final short YYFINAL = 7;
    static final short YYMAXTOKEN = 275;
    static final String yyname[] = {
        "end-of-file", null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, null, null, null, 
        null, null, null, null, null, null, null, "DCOLON", "SCOLON", "COLON", 
        "SEQ_OF", "SET_OF", "IDENT", "EQ", "LB", "RB", "QUOTE", "BAR", "PACKAGE", "PREFIX", 
        "DOT", "DIRECTORY", "STRING", "MAP", "TO", "TOP"
    };
    static final String yyrule[] = {
        "$accept : Document", "Document :", "Document : error", "Document : DefinitionList", "DefinitionList : Definition", "DefinitionList : ShortHand", "DefinitionList : PrefixSpecification", "DefinitionList : PackageSpecification", "DefinitionList : DirectorySpecification", "DefinitionList : TopSpecification", 
        "DefinitionList : DefinitionList SCOLON Definition", "DefinitionList : DefinitionList SCOLON ShortHand", "DefinitionList : DefinitionList SCOLON PrefixSpecification", "DefinitionList : DefinitionList SCOLON PackageSpecification", "DefinitionList : DefinitionList SCOLON DirectorySpecification", "DefinitionList : DefinitionList SCOLON TopSpecification", "PrefixSpecification : PREFIX IDENT", "PackageSpecification : PACKAGE PackageList", "DirectorySpecification : DIRECTORY STRING", "TopSpecification : TOP IdentifierList", 
        "PackageList : IDENT", "PackageList : PackageList DOT IDENT", "ShortHand : IDENT EQ Type", "Definition : IDENT DCOLON", "Definition : IDENT DCOLON FieldList", "FieldList : Field", "FieldList : FieldList Field", "Field : IDENT COLON Type", "Type : IDENT", "Type : QUOTE", 
        "Type : Type BAR Type", "Type : LB Type RB", "Type : SEQ_OF Type", "Type : SET_OF Type", "Type : MAP Type TO Type", "IdentifierList : IDENT", "IdentifierList : IdentifierList IDENT"
    };
    public AstScanner theScanner;
    public AstDefinitions theAst;
    public int errors;
    int yyn;
    int yym;
    int yystate;
    String yys;

    static 
    {
        yytable();
        yycheck();
    }
}