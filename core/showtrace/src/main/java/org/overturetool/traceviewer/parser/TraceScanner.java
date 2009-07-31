// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TraceScanner.java

package org.overturetool.traceviewer.parser;

import java.io.*;

// Referenced classes of package org.overturetool.tracefile.parser:
//            TraceParserTokens, TraceParser, TraceParserVal

class TraceScanner
    implements TraceParserTokens
{

    private static int[] zzUnpackAction()
    {
        int result[] = new int[21];
        int offset = 0;
        offset = zzUnpackAction("\001\000\001\001\001\002\001\003\002\001\001\004\001\001\001\005\001\006\001\007\001\b\001\t\001\n\001\013\002\000\001\f\001\r\002\0", offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int result[])
    {
        int i = 0;
        int j = offset;
        for(int l = packed.length(); i < l;)
        {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do
                result[j++] = value;
            while(--count > 0);
        }

        return j;
    }

    private static int[] zzUnpackRowMap()
    {
        int result[] = new int[21];
        int offset = 0;
        offset = zzUnpackRowMap("\000\000\000\017\000\036\000-\000<\000K\000\017\000Z\000\017\000\017\000\017\000\017\000\017\000\017\000\017\000i\000x\000\017\000\017\000\207\000\226", offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int result[])
    {
        int i = 0;
        int j = offset;
        for(int l = packed.length(); i < l;)
        {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i++);
        }

        return j;
    }

    private static int[] zzUnpackTrans()
    {
        int result[] = new int[165];
        int offset = 0;
        offset = zzUnpackTrans("\001\002\001\003\001\004\001\005\001\006\001\007\001\b\001\002\001\t\001\n\001\013\001\f\001\r\001\016\001\017\020\000\001\003\017\000\001\004\f\000\003\020\001\000\013\020\003\000\001\021\022\000\001\022\007\000\003\020\001\023\013\020\003\024\001\000\016\024\001\000\001\025\r\024\001\023\001\025\n\024", offset, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int result[])
    {
        int i = 0;
        int j = offset;
        for(int l = packed.length(); i < l;)
        {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            value--;
            do
                result[j++] = value;
            while(--count > 0);
        }

        return j;
    }

    private static int[] zzUnpackAttribute()
    {
        int result[] = new int[21];
        int offset = 0;
        offset = zzUnpackAttribute("\001\000\001\t\004\001\001\t\001\001\007\t\002\000\002\t\002\0", offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int result[])
    {
        int i = 0;
        int j = offset;
        for(int l = packed.length(); i < l;)
        {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do
                result[j++] = value;
            while(--count > 0);
        }

        return j;
    }

    public TraceScanner(TraceParser parser, String fname)
    {
        zzLexicalState = 0;
        zzBuffer = new char[16384];
        zzAtBOL = true;
        theParser = null;
        theParser = parser;
        try
        {
            zzReader = new FileReader(fname);
        }
        catch(IOException ioe)
        {
            theParser.yyerror(ioe.getMessage());
        }
    }

    public TraceScanner(TraceParser parser, String fname, String encoding)
    {
        zzLexicalState = 0;
        zzBuffer = new char[16384];
        zzAtBOL = true;
        theParser = null;
        theParser = parser;
        try
        {
            FileInputStream theFile = new FileInputStream(fname);
            zzReader = new InputStreamReader(theFile, encoding);
        }
        catch(IOException ioe)
        {
            theParser.yyerror(ioe.getMessage());
        }
    }

    private String stripString(String in)
    {
        String out;
        if(in.charAt(0) == '\\')
            out = in.substring(2, in.length() - 2);
        else
        if(in.charAt(0) == '"')
            out = in.substring(1, in.length() - 1);
        else
            out = new String(in);
        return out;
    }

    public int getLine()
    {
        return yyline + 1;
    }

    public int getColumn()
    {
        return yycolumn;
    }

    TraceScanner(Reader in)
    {
        zzLexicalState = 0;
        zzBuffer = new char[16384];
        zzAtBOL = true;
        theParser = null;
        zzReader = in;
    }

    TraceScanner(InputStream in)
    {
        this(((Reader) (new InputStreamReader(in))));
    }

    private static char[] zzUnpackCMap(String packed)
    {
        char map[] = new char[0x10000];
        int i = 0;
        int j = 0;
        while(i < 56) 
        {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do
                map[j++] = value;
            while(--count > 0);
        }
        return map;
    }

    private boolean zzRefill()
        throws IOException
    {
        if(zzStartRead > 0)
        {
            System.arraycopy(zzBuffer, zzStartRead, zzBuffer, 0, zzEndRead - zzStartRead);
            zzEndRead -= zzStartRead;
            zzCurrentPos -= zzStartRead;
            zzMarkedPos -= zzStartRead;
            zzPushbackPos -= zzStartRead;
            zzStartRead = 0;
        }
        if(zzCurrentPos >= zzBuffer.length)
        {
            char newBuffer[] = new char[zzCurrentPos * 2];
            System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
            zzBuffer = newBuffer;
        }
        int numRead = zzReader.read(zzBuffer, zzEndRead, zzBuffer.length - zzEndRead);
        if(numRead < 0)
        {
            return true;
        } else
        {
            zzEndRead += numRead;
            return false;
        }
    }

    public final void yyclose()
        throws IOException
    {
        zzAtEOF = true;
        zzEndRead = zzStartRead;
        if(zzReader != null)
            zzReader.close();
    }

    public final void yyreset(Reader reader)
    {
        zzReader = reader;
        zzAtBOL = true;
        zzAtEOF = false;
        zzEndRead = zzStartRead = 0;
        zzCurrentPos = zzMarkedPos = zzPushbackPos = 0;
        yyline = yychar = yycolumn = 0;
        zzLexicalState = 0;
    }

    public final int yystate()
    {
        return zzLexicalState;
    }

    public final void yybegin(int newState)
    {
        zzLexicalState = newState;
    }

    public final String yytext()
    {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    public final char yycharat(int pos)
    {
        return zzBuffer[zzStartRead + pos];
    }

    public final int yylength()
    {
        return zzMarkedPos - zzStartRead;
    }

    private void zzScanError(int errorCode)
    {
        String message;
        try
        {
            message = ZZ_ERROR_MSG[errorCode];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            message = ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }

    public void yypushback(int number)
    {
        if(number > yylength())
            zzScanError(2);
        zzMarkedPos -= number;
    }

    private void zzDoEOF()
        throws IOException
    {
        if(!zzEOFDone)
        {
            zzEOFDone = true;
            yyclose();
        }
    }

    public int yylex()
        throws IOException
    {
        int zzEndReadL = zzEndRead;
        char zzBufferL[] = zzBuffer;
        char zzCMapL[] = ZZ_CMAP;
        int zzTransL[] = ZZ_TRANS;
        int zzRowMapL[] = ZZ_ROWMAP;
        int zzAttrL[] = ZZ_ATTRIBUTE;
        do
        {
            int zzMarkedPosL = zzMarkedPos;
            boolean zzR = false;
            int zzCurrentPosL;
            for(zzCurrentPosL = zzStartRead; zzCurrentPosL < zzMarkedPosL; zzCurrentPosL++)
                switch(zzBufferL[zzCurrentPosL])
                {
                case 11: // '\013'
                case 12: // '\f'
                case 133: 
                case 8232: 
                case 8233: 
                    yyline++;
                    yycolumn = 0;
                    zzR = false;
                    break;

                case 13: // '\r'
                    yyline++;
                    yycolumn = 0;
                    zzR = true;
                    break;

                case 10: // '\n'
                    if(zzR)
                    {
                        zzR = false;
                    } else
                    {
                        yyline++;
                        yycolumn = 0;
                    }
                    break;

                default:
                    zzR = false;
                    yycolumn++;
                    break;
                }

            if(zzR)
            {
                boolean zzPeek;
                if(zzMarkedPosL < zzEndReadL)
                    zzPeek = zzBufferL[zzMarkedPosL] == '\n';
                else
                if(zzAtEOF)
                {
                    zzPeek = false;
                } else
                {
                    boolean eof = zzRefill();
                    zzEndReadL = zzEndRead;
                    zzMarkedPosL = zzMarkedPos;
                    zzBufferL = zzBuffer;
                    if(eof)
                        zzPeek = false;
                    else
                        zzPeek = zzBufferL[zzMarkedPosL] == '\n';
                }
                if(zzPeek)
                    yyline--;
            }
            int zzAction = -1;
            zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
            zzState = zzLexicalState;
            int zzInput;
            int zzAttributes;
label0:
            do
            {
                do
                {
                    if(zzCurrentPosL < zzEndReadL)
                    {
                        zzInput = zzBufferL[zzCurrentPosL++];
                    } else
                    {
                        if(zzAtEOF)
                        {
                            zzInput = -1;
                            break label0;
                        }
                        zzCurrentPos = zzCurrentPosL;
                        zzMarkedPos = zzMarkedPosL;
                        boolean eof = zzRefill();
                        zzCurrentPosL = zzCurrentPos;
                        zzMarkedPosL = zzMarkedPos;
                        zzBufferL = zzBuffer;
                        zzEndReadL = zzEndRead;
                        if(eof)
                        {
                            zzInput = -1;
                            break label0;
                        }
                        zzInput = zzBufferL[zzCurrentPosL++];
                    }
                    int zzNext = zzTransL[zzRowMapL[zzState] + zzCMapL[zzInput]];
                    if(zzNext == -1)
                        break label0;
                    zzState = zzNext;
                    zzAttributes = zzAttrL[zzState];
                } while((zzAttributes & 1) != 1);
                zzAction = zzState;
                zzMarkedPosL = zzCurrentPosL;
            } while((zzAttributes & 8) != 8);
            zzMarkedPos = zzMarkedPosL;
            switch(zzAction >= 0 ? ZZ_ACTION[zzAction] : zzAction)
            {
            case 6: // '\006'
                return 264;

            case 8: // '\b'
                return 266;

            case 11: // '\013'
                return 269;

            case 12: // '\f'
                return 261;

            case 1: // '\001'
                return yytext().charAt(0);

            case 2: // '\002'
                if(yytext().compareTo("nil") == 0)
                    return 263;
                if(yytext().compareTo("true") == 0)
                {
                    theParser.yylval = new TraceParserVal(1);
                    return 259;
                }
                if(yytext().compareTo("false") == 0)
                {
                    theParser.yylval = new TraceParserVal(0);
                    return 259;
                } else
                {
                    theParser.yylval = new TraceParserVal(yytext());
                    return 257;
                }

            case 13: // '\r'
                theParser.yylval = new TraceParserVal(stripString(yytext()));
                return 258;

            case 9: // '\t'
                return 267;

            case 7: // '\007'
                return 265;

            case 10: // '\n'
                return 268;

            case 3: // '\003'
                theParser.yylval = new TraceParserVal(Integer.parseInt(yytext()));
                return 260;

            case 5: // '\005'
                return 262;

            default:
                if(zzInput == -1 && zzStartRead == zzCurrentPos)
                {
                    zzAtEOF = true;
                    zzDoEOF();
                    return 0;
                }
                zzScanError(1);
                break;

            case 4: // '\004'
            case 14: // '\016'
            case 15: // '\017'
            case 16: // '\020'
            case 17: // '\021'
            case 18: // '\022'
            case 19: // '\023'
            case 20: // '\024'
            case 21: // '\025'
            case 22: // '\026'
            case 23: // '\027'
            case 24: // '\030'
            case 25: // '\031'
            case 26: // '\032'
                break;
            }
        } while(true);
    }

    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 16384;
    public static final int YYINITIAL = 0;
    private static final String ZZ_CMAP_PACKED = "\t\000\001\005\001\016\002\000\001\005\022\000\001\005\001\000\001\003\t\000\001\r\001\006\002\000\n\002\001\b\003\000\001\007\002\000\032\001\001\t\001\004\001\n\003\000\032\001\001\013\001\000\001\f\uFF82\0";
    private static final char ZZ_CMAP[] = zzUnpackCMap("\t\000\001\005\001\016\002\000\001\005\022\000\001\005\001\000\001\003\t\000\001\r\001\006\002\000\n\002\001\b\003\000\001\007\002\000\032\001\001\t\001\004\001\n\003\000\032\001\001\013\001\000\001\f\uFF82\0");
    private static final int ZZ_ACTION[] = zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\001\000\001\001\001\002\001\003\002\001\001\004\001\001\001\005\001\006\001\007\001\b\001\t\001\n\001\013\002\000\001\f\001\r\002\0";
    private static final int ZZ_ROWMAP[] = zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000\017\000\036\000-\000<\000K\000\017\000Z\000\017\000\017\000\017\000\017\000\017\000\017\000\017\000i\000x\000\017\000\017\000\207\000\226";
    private static final int ZZ_TRANS[] = zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0 = "\001\002\001\003\001\004\001\005\001\006\001\007\001\b\001\002\001\t\001\n\001\013\001\f\001\r\001\016\001\017\020\000\001\003\017\000\001\004\f\000\003\020\001\000\013\020\003\000\001\021\022\000\001\022\007\000\003\020\001\023\013\020\003\024\001\000\016\024\001\000\001\025\r\024\001\023\001\025\n\024";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String ZZ_ERROR_MSG[] = {
        "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"
    };
    private static final int ZZ_ATTRIBUTE[] = zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\001\000\001\t\004\001\001\t\001\001\007\t\002\000\002\t\002\0";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState;
    private char zzBuffer[];
    private int zzMarkedPos;
    private int zzPushbackPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL;
    private boolean zzAtEOF;
    private boolean zzEOFDone;
    private TraceParser theParser;

}