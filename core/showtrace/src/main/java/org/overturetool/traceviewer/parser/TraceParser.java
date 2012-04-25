// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TraceParser.java

package org.overturetool.traceviewer.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.traceviewer.ast.imp.OmlBUSdecl;
import org.overturetool.traceviewer.ast.imp.OmlCPUdecl;
import org.overturetool.traceviewer.ast.imp.OmlDelayedThreadSwapIn;
import org.overturetool.traceviewer.ast.imp.OmlDeployObj;
import org.overturetool.traceviewer.ast.imp.OmlInstVarChange;
import org.overturetool.traceviewer.ast.imp.OmlMessageActivate;
import org.overturetool.traceviewer.ast.imp.OmlMessageCompleted;
import org.overturetool.traceviewer.ast.imp.OmlMessageRequest;
import org.overturetool.traceviewer.ast.imp.OmlOpActivate;
import org.overturetool.traceviewer.ast.imp.OmlOpCompleted;
import org.overturetool.traceviewer.ast.imp.OmlOpRequest;
import org.overturetool.traceviewer.ast.imp.OmlReplyRequest;
import org.overturetool.traceviewer.ast.imp.OmlThreadCreate;
import org.overturetool.traceviewer.ast.imp.OmlThreadKill;
import org.overturetool.traceviewer.ast.imp.OmlThreadSwapIn;
import org.overturetool.traceviewer.ast.imp.OmlThreadSwapOut;
import org.overturetool.traceviewer.ast.imp.OmlTraceFile;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;

// Referenced classes of package org.overturetool.tracefile.parser:
//            TraceParserTokens, TraceParserVal, TraceScanner

public class TraceParser
    implements TraceParserTokens
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
        valstk = new TraceParserVal[500];
        yyval = new TraceParserVal();
        yylval = new TraceParserVal();
        valptr = -1;
    }

    void val_push(TraceParserVal val)
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

    TraceParserVal val_pop()
    {
        if(valptr < 0)
            return new TraceParserVal();
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

    TraceParserVal val_peek(int relative)
    {
        int ptr = valptr - relative;
        if(ptr < 0)
            return new TraceParserVal();
        else
            return valstk[ptr];
    }

    static void yytable()
    {
        yytable = (new short[] {
            7, 5, 13, 14, 15, 2, 21, 16, 17, 21, 
            18, 7, 11, 24, 22, 26, 1, 2, 27, 28, 
            27, 10, 29, 6, 12, 25
        });
    }

    static void yycheck()
    {
        yycheck = (new short[] {
            257, 261, 258, 259, 260, 257, 260, 263, 264, 260, 
            266, 257, 269, 267, 265, 265, 256, 257, 268, 267, 
            268, 262, 260, 3, 8, 18
        });
    }

    public TraceParser(String fname)
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
        theScanner = new TraceScanner(this, fname);
    }

    public TraceParser(String fname, String encoding)
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
        theScanner = new TraceScanner(this, fname, encoding);
    }

    public int yylex()
    {
        int res = 0;
        try
        {
            res = theScanner.yylex();
        }
        catch(IOException ioe)
        {
            yyerror(ioe.getMessage());
        }
        return res;
    }

    public void yyerror(String error)
    {
        System.out.print(error);
        System.out.print((new StringBuilder(" at line ")).append(theScanner.getLine()).toString());
        System.out.println((new StringBuilder(" column ")).append(theScanner.getColumn()).toString());
        errors++;
    }

    public IOmlTraceFile parse()
        throws CGException
    {
        try
        {
            theAst = new OmlTraceFile();
            yyparse();
        }
        catch(IOException ioe)
        {
            yyerror(ioe.getMessage());
        }
        return theAst;
    }

    public int getLine()
    {
        return theScanner.getLine();
    }

    public int getColumnStart()
    {
        return theScanner.getColumn();
    }

    public int getColumnEnd()
    {
        return theScanner.getColumn() + theScanner.yylength();
    }

    public int errorCount()
    {
        return errors;
    }

    private void addTraceEvent(String name, HashMap fields)
        throws CGException
    {
        if(name.compareTo("DeployObj") == 0)
            addDeployObj(fields);
        else
        if(name.compareTo("BUSdecl") == 0)
            addBUSdecl(fields);
        else
        if(name.compareTo("CPUdecl") == 0)
            addCPUdecl(fields);
        else
        if(name.compareTo("DelayedThreadSwapIn") == 0)
            addDelayedThreadSwapIn(fields);
        else
        if(name.compareTo("MessageActivate") == 0)
            addMessageActivate(fields);
        else
        if(name.compareTo("MessageCompleted") == 0)
            addMessageCompleted(fields);
        else
        if(name.compareTo("MessageRequest") == 0)
            addMessageRequest(fields);
        else
        if(name.compareTo("OpActivate") == 0)
            addOpActivate(fields);
        else
        if(name.compareTo("OpCompleted") == 0)
            addOpCompleted(fields);
        else
        if(name.compareTo("OpRequest") == 0)
            addOpRequest(fields);
        else
        if(name.compareTo("ReplyRequest") == 0)
            addReplyRequest(fields);
        else
        if(name.compareTo("ThreadCreate") == 0)
            addThreadCreate(fields);
        else
        if(name.compareTo("ThreadKill") == 0)
            addThreadKill(fields);
        else
        if(name.compareTo("ThreadSwapIn") == 0)
            addThreadSwapIn(fields);
        else
        if(name.compareTo("ThreadSwapOut") == 0)
            addThreadSwapOut(fields);
        else
        if(name.compareTo("InstVarChange") == 0)
            addInstVarChange(fields);
        else
            yyerror((new StringBuilder("Unknown trace event \"")).append(name).append("\"").toString());
    }

    private void addDeployObj(HashMap fields)
        throws CGException
    {
        OmlDeployObj event = new OmlDeployObj();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addBUSdecl(HashMap fields)
        throws CGException
    {
        OmlBUSdecl event = new OmlBUSdecl();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addCPUdecl(HashMap fields)
        throws CGException
    {
        OmlCPUdecl event = new OmlCPUdecl();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addDelayedThreadSwapIn(HashMap fields)
        throws CGException
    {
        OmlDelayedThreadSwapIn event = new OmlDelayedThreadSwapIn();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addMessageActivate(HashMap fields)
        throws CGException
    {
        OmlMessageActivate event = new OmlMessageActivate();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addMessageCompleted(HashMap fields)
        throws CGException
    {
        OmlMessageCompleted event = new OmlMessageCompleted();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addMessageRequest(HashMap fields)
        throws CGException
    {
        OmlMessageRequest event = new OmlMessageRequest();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addOpActivate(HashMap fields)
        throws CGException
    {
        OmlOpActivate event = new OmlOpActivate();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addOpCompleted(HashMap fields)
        throws CGException
    {
        OmlOpCompleted event = new OmlOpCompleted();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addOpRequest(HashMap fields)
        throws CGException
    {
        OmlOpRequest event = new OmlOpRequest();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addReplyRequest(HashMap fields)
        throws CGException
    {
        OmlReplyRequest event = new OmlReplyRequest();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addThreadCreate(HashMap fields)
        throws CGException
    {
        OmlThreadCreate event = new OmlThreadCreate();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addThreadKill(HashMap fields)
        throws CGException
    {
        OmlThreadKill event = new OmlThreadKill();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addThreadSwapIn(HashMap fields)
        throws CGException
    {
        OmlThreadSwapIn event = new OmlThreadSwapIn();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addThreadSwapOut(HashMap fields)
        throws CGException
    {
        OmlThreadSwapOut event = new OmlThreadSwapOut();
        event.init(fields);
        theAst.addTrace(event);
    }

    private void addInstVarChange(HashMap fields)
        throws CGException
    {
        OmlInstVarChange event = new OmlInstVarChange();
        event.init(fields);
        theAst.addTrace(event);
    }

    void yylexdebug(int state, int ch)
    {
        String s = null;
        if(ch < 0)
            ch = 0;
        if(ch <= 269)
            s = yyname[ch];
        if(s == null)
            s = "illegal-symbol";
        debug((new StringBuilder("state ")).append(state).append(", reading ").append(ch).append(" (").append(s).append(")").toString());
    }

    int yyparse()
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
                    if(yyn != 0 && (yyn += yychar) >= 0 && yyn <= 25 && yycheck[yyn] == yychar)
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
                    if(yyn != 0 && (yyn += yychar) >= 0 && yyn <= 25 && yycheck[yyn] == yychar)
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
                            if(yyn != 0 && (yyn += 256) >= 0 && yyn <= 25 && yycheck[yyn] == 256)
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
                            if(yychar <= 269)
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
                HashMap fields = (HashMap)val_peek(1).obj;
                String name = new String(val_peek(3).sval);
                addTraceEvent(name, fields);
                break;
            }

            case 5: // '\005'
            {
                yyval.obj = val_peek(0).obj;
                break;
            }

            case 6: // '\006'
            {
                HashMap lhs = (HashMap)val_peek(1).obj;
                HashMap rhs = (HashMap)val_peek(0).obj;
                HashMap res = new HashMap();
                res.putAll(lhs);
                res.putAll(rhs);
                yyval.obj = res;
                break;
            }

            case 7: // '\007'
            {
                HashMap res = new HashMap();
                res.put(new String(val_peek(2).sval), new Long(val_peek(0).ival));
                yyval.obj = res;
                break;
            }

            case 8: // '\b'
            {
                HashMap res = new HashMap();
                res.put(new String(val_peek(2).sval), new String(val_peek(0).sval));
                yyval.obj = res;
                break;
            }

            case 9: // '\t'
            {
                HashMap res = new HashMap();
                if(val_peek(0).ival == 1)
                    res.put(new String(val_peek(2).sval), new Boolean(true));
                else
                    res.put(new String(val_peek(2).sval), new Boolean(false));
                yyval.obj = res;
                break;
            }

            case 10: // '\n'
            {
                HashMap res = new HashMap();
                res.put(new String(val_peek(2).sval), null);
                yyval.obj = res;
                break;
            }

            case 11: // '\013'
            {
                HashMap res = new HashMap();
                Vector arg = (Vector)val_peek(0).obj;
                res.put(new String(val_peek(2).sval), arg);
                yyval.obj = res;
                break;
            }

            case 12: // '\f'
            {
                HashMap res = new HashMap();
                HashSet arg = (HashSet)val_peek(0).obj;
                res.put(new String(val_peek(2).sval), arg);
                yyval.obj = res;
                break;
            }

            case 13: // '\r'
            {
                Vector res = new Vector();
                yyval.obj = res;
                break;
            }

            case 14: // '\016'
            {
                Vector res = (Vector)val_peek(1).obj;
                yyval.obj = res;
                break;
            }

            case 15: // '\017'
            {
                HashSet res = new HashSet();
                yyval.obj = res;
                break;
            }

            case 16: // '\020'
            {
                HashSet res = new HashSet();
                Vector elems = (Vector)val_peek(1).obj;
                res.addAll(elems);
                yyval.obj = res;
                break;
            }

            case 17: // '\021'
            {
                Vector res = new Vector();
                res.addElement(new Long(val_peek(0).ival));
                yyval.obj = res;
                break;
            }

            case 18: // '\022'
            {
                Vector res = (Vector)val_peek(2).obj;
                res.addElement(new Long(val_peek(0).ival));
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
                    debug("After reduction, shifting from state 0 to state 3");
                yystate = 3;
                state_push(3);
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
                if(yyn != 0 && (yyn += yystate) >= 0 && yyn <= 25 && yycheck[yyn] == yystate)
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

    public TraceParser()
    {
        statestk = new int[500];
        theScanner = null;
        theAst = null;
        errors = 0;
    }

    public TraceParser(boolean debugMe)
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
    TraceParserVal yyval;
    TraceParserVal yylval;
    TraceParserVal valstk[];
    int valptr;
    public static final short YYERRCODE = 256;
    static final short yylhs[] = {
        -1, 0, 0, 0, 1, 2, 2, 3, 3, 3, 
        3, 3, 3, 4, 4, 5, 5, 6, 6
    };
    static final short yylen[] = {
        2, 1, 1, 2, 4, 1, 2, 3, 3, 3, 
        3, 3, 3, 2, 3, 2, 3, 1, 3
    };
    static final short yydefred[] = {
        0, 1, 0, 0, 2, 0, 3, 0, 0, 5, 
        0, 4, 6, 8, 9, 7, 10, 0, 0, 11, 
        12, 17, 13, 0, 15, 0, 14, 0, 16, 18
    };
    static final short yydgoto[] = {
        3, 4, 8, 9, 19, 20, 23
    };
    static final short yysindex[] = {
        -240, 0, -260, -252, 0, -246, 0, -241, -257, 0, 
        -256, 0, 0, 0, 0, 0, 0, -251, -254, 0, 
        0, 0, 0, -250, 0, -248, 0, -238, 0, 0
    };
    static final short yyrindex[] = new short[30];
    static final short yygindex[] = {
        0, 20, 0, 16, 0, 0, 7
    };
    static final int YYTABLESIZE = 25;
    static short yytable[];
    static short yycheck[];
    static final short YYFINAL = 3;
    static final short YYMAXTOKEN = 269;
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
        null, null, null, null, null, null, null, "IDENT", "STRING", "BOOLEAN", 
        "NUMBER", "ARROW", "COLON", "NIL", "LBR", "RBR", "LAC", "RAC", "COMMA", "NL"
    };
    static final String yyrule[] = {
        "$accept : TraceFile", "TraceFile : error", "TraceFile : TraceEvent", "TraceFile : TraceFile TraceEvent", "TraceEvent : IDENT ARROW FieldList NL", "FieldList : Field", "FieldList : FieldList Field", "Field : IDENT COLON NUMBER", "Field : IDENT COLON STRING", "Field : IDENT COLON BOOLEAN", 
        "Field : IDENT COLON NIL", "Field : IDENT COLON Sequence", "Field : IDENT COLON Set", "Sequence : LBR RBR", "Sequence : LBR NumList RBR", "Set : LAC RAC", "Set : LAC NumList RAC", "NumList : NUMBER", "NumList : NumList COMMA NUMBER"
    };
    private TraceScanner theScanner;
    private OmlTraceFile theAst;
    protected int errors;
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