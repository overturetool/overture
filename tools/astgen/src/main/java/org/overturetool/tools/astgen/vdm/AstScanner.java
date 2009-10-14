// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstScanner.java

package org.overturetool.tools.astgen.vdm;

import java.io.*;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstParserTokens, AstParserVal, AstParser
@SuppressWarnings("all")
class AstScanner
    implements AstParserTokens
{

    private static int[] zzUnpackAction()
    {
        int result[] = new int[61];
        int offset = 0;
        offset = zzUnpackAction("\001\000\001\001\001\002\002\001\002\003\001\001\001\002\001\001\002\002\001\004\001\005\001\006\001\007\001\b\001\t\001\n\005\000\001\013\001\003\002\002\001\f\001\r\001\016\004\000\001\017\002\002\001\000\001\020\f\000\001\021\001\022\001\000\001\023\002\000\001\024\001\000\001\025", offset, result);
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
        int result[] = new int[61];
        int offset = 0;
        offset = zzUnpackRowMap("\000\000\000%\000J\000o\000\224\000%\000\271\000\336\000\u0103\000\u0128\000\u014D\000\u0172\000\u0197\000%\000%\000%\000%\000%\000%\000\u01BC\000\u01E1\000\u0206\000\u022B\000\u0250\000J\000\u0275\000\u029A\000\u02BF\000%\000%\000%\000\u02E4\000\u0309\000\u032E\000\u0353\000J\000\u0378\000\u039D\000\u03C2\000%\000\u03E7\000\u040C\000\u0431\000\u0456\000\u047B\000\u04A0\000\u04C5\000\u04EA\000\u050F\000\u0534\000\u0559\000\u057E\000%\000%\000\u05A3\000%\000\u05C8\000\u05ED\000%\000\u0612\000%", offset, result);
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
        int result[] = new int[1591];
        int offset = 0;
        offset = zzUnpackTrans("\001\002\002\003\001\002\001\004\001\002\001\005\001\006\001\007\002\006\001\b\005\003\001\t\b\003\001\n\001\013\001\f\001\003\001\r\001\016\001\017\001\020\001\021\001\022\001\023&\000\003\003\b\000\016\003\001\000\003\003\t\000\002\024!\000\006\025\001\000\036\025\007\000\001\006)\000\001\026\004\000\001\027\002\000\001\030\021\000\003\003\b\000\006\003\001\031\007\003\001\000\003\003!\000\001\032\013\000\003\003\b\000\t\003\001\033\004\003\001\000\003\003\b\000\003\003\b\000\003\003\001\034\n\003\001\000\003\003%\000\001\035\b\000\002\024\001\000\001\036\037\000\006\025\001\037\036\025\r\000\001 )\000\001! \000\001\"\006\000\001#\017\000\007\032\001\000\035\032\001\000\003\003\b\000\b\003\001$\005\003\001\000\003\003\b\000\003\003\b\000\005\003\001%\b\003\001\000\002\003\001&\025\000\001'*\000\001(\037\000\001)%\000\001*\025\000\003\003\006\000\001+\001\000\016\003\001\000\003\003\b\000\003\003\006\000\001,\001\000\016\003\001\000\003\003\026\000\001--\000\001.\"\000\001/ \000\0010$\000\0011\"\000\0012!\000\0013,\000\0014'\000\0015$\000\0016\035\000\0017,\000\0018\"\000\0019\037\000\001:!\000\001;#\000\001<)\000\001=\021\0", offset, result);
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
        int result[] = new int[61];
        int offset = 0;
        offset = zzUnpackAttribute("\001\000\001\t\003\001\001\t\007\001\006\t\005\000\004\001\003\t\004\000\003\001\001\000\001\t\f\000\002\t\001\000\001\t\002\000\001\t\001\000\001\t", offset, result);
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

    public AstScanner(AstParser pAstParser, String fname)
    {
        zzLexicalState = 0;
        zzBuffer = new char[16384];
        zzAtBOL = true;
        theParser = null;
        theParser = pAstParser;
        try
        {
            zzReader = new FileReader(fname);
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println(fnfe.getMessage());
            System.out.println("Switch reading to standard input");
            zzReader = new InputStreamReader(System.in);
        }
    }

    public String atPosition()
    {
        String retval = (new StringBuilder(" at line ")).append(yyline + 1).append(", column ").append(yycolumn).toString();
        return retval;
    }

    AstScanner(Reader in)
    {
        zzLexicalState = 0;
        zzBuffer = new char[16384];
        zzAtBOL = true;
        theParser = null;
        zzReader = in;
    }

    AstScanner(InputStream in)
    {
        this(((Reader) (new InputStreamReader(in))));
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
            case 13: // '\r'
                theParser.yylval = new AstParserVal(yytext().substring(1, yylength() - 1));
                return 266;

            case 21: // '\025'
                return 271;

            case 12: // '\f'
                return 257;

            case 1: // '\001'
                return yytext().charAt(0);

            case 15: // '\017'
                return 273;

            case 5: // '\005'
                return 258;

            case 9: // '\t'
                return 267;

            case 11: // '\013'
                return 274;

            case 4: // '\004'
                return 259;

            case 7: // '\007'
                return 264;

            case 17: // '\021'
                return 261;

            case 6: // '\006'
                return 263;

            case 18: // '\022'
                return 260;

            case 19: // '\023'
                return 269;

            case 8: // '\b'
                return 265;

            case 2: // '\002'
                theParser.yylval = new AstParserVal(yytext());
                return 262;

            case 10: // '\n'
                return 270;

            case 14: // '\016'
                theParser.yylval = new AstParserVal(yytext().substring(1, yylength() - 1));
                return 272;

            case 16: // '\020'
                return 275;

            case 20: // '\024'
                return 268;

            default:
                if(zzInput == -1 && zzStartRead == zzCurrentPos)
                {
                    zzAtEOF = true;
                    zzDoEOF();
                    return -1;
                }
                zzScanError(1);
                break;

            case 3: // '\003'
            case 22: // '\026'
            case 23: // '\027'
            case 24: // '\030'
            case 25: // '\031'
            case 26: // '\032'
            case 27: // '\033'
            case 28: // '\034'
            case 29: // '\035'
            case 30: // '\036'
            case 31: // '\037'
            case 32: // ' '
            case 33: // '!'
            case 34: // '"'
            case 35: // '#'
            case 36: // '$'
            case 37: // '%'
            case 38: // '&'
            case 39: // '\''
            case 40: // '('
            case 41: // ')'
            case 42: // '*'
                break;
            }
        } while(true);
    }

    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 16384;
    public static final int YYINITIAL = 0;
    private static final char ZZ_CMAP[] = {
        '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\t', 
        '\007', '\0', '\0', '\b', '\0', '\0', '\0', '\0', '\0', '\0', 
        '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', 
        '\0', '\0', '\n', '\0', '\006', '\0', '\0', '\013', '\0', '\0', 
        '\0', '\0', '\0', '\0', '\0', '\032', '$', '\0', '\003', '\003', 
        '\003', '\003', '\003', '\003', '\003', '\003', '\003', '\003', '\036', '\037', 
        '\004', ' ', '\005', '\0', '\0', '\002', '\002', '\002', '\002', '\002', 
        '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', 
        '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', '\002', 
        '\002', '!', '\0', '"', '\0', '\002', '\0', '\025', '\001', '\020', 
        '\f', '\017', '\030', '\027', '\001', '\r', '\001', '\026', '\001', '\033', 
        '\001', '\022', '\024', '\035', '\016', '\034', '\021', '\001', '\001', '\001', 
        '\031', '\023', '\001', '\0', '#', '\0', '\0', '\0'
    };
    private static final int ZZ_ACTION[] = zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\001\000\001\001\001\002\002\001\002\003\001\001\001\002\001\001\002\002\001\004\001\005\001\006\001\007\001\b\001\t\001\n\005\000\001\013\001\003\002\002\001\f\001\r\001\016\004\000\001\017\002\002\001\000\001\020\f\000\001\021\001\022\001\000\001\023\002\000\001\024\001\000\001\025";
    private static final int ZZ_ROWMAP[] = zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\000\000\000%\000J\000o\000\224\000%\000\271\000\336\000\u0103\000\u0128\000\u014D\000\u0172\000\u0197\000%\000%\000%\000%\000%\000%\000\u01BC\000\u01E1\000\u0206\000\u022B\000\u0250\000J\000\u0275\000\u029A\000\u02BF\000%\000%\000%\000\u02E4\000\u0309\000\u032E\000\u0353\000J\000\u0378\000\u039D\000\u03C2\000%\000\u03E7\000\u040C\000\u0431\000\u0456\000\u047B\000\u04A0\000\u04C5\000\u04EA\000\u050F\000\u0534\000\u0559\000\u057E\000%\000%\000\u05A3\000%\000\u05C8\000\u05ED\000%\000\u0612\000%";
    private static final int ZZ_TRANS[] = zzUnpackTrans();
    private static final String ZZ_TRANS_PACKED_0 = "\001\002\002\003\001\002\001\004\001\002\001\005\001\006\001\007\002\006\001\b\005\003\001\t\b\003\001\n\001\013\001\f\001\003\001\r\001\016\001\017\001\020\001\021\001\022\001\023&\000\003\003\b\000\016\003\001\000\003\003\t\000\002\024!\000\006\025\001\000\036\025\007\000\001\006)\000\001\026\004\000\001\027\002\000\001\030\021\000\003\003\b\000\006\003\001\031\007\003\001\000\003\003!\000\001\032\013\000\003\003\b\000\t\003\001\033\004\003\001\000\003\003\b\000\003\003\b\000\003\003\001\034\n\003\001\000\003\003%\000\001\035\b\000\002\024\001\000\001\036\037\000\006\025\001\037\036\025\r\000\001 )\000\001! \000\001\"\006\000\001#\017\000\007\032\001\000\035\032\001\000\003\003\b\000\b\003\001$\005\003\001\000\003\003\b\000\003\003\b\000\005\003\001%\b\003\001\000\002\003\001&\025\000\001'*\000\001(\037\000\001)%\000\001*\025\000\003\003\006\000\001+\001\000\016\003\001\000\003\003\b\000\003\003\006\000\001,\001\000\016\003\001\000\003\003\026\000\001--\000\001.\"\000\001/ \000\0010$\000\0011\"\000\0012!\000\0013,\000\0014'\000\0015$\000\0016\035\000\0017,\000\0018\"\000\0019\037\000\001:!\000\001;#\000\001<)\000\001=\021\0";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String ZZ_ERROR_MSG[] = {
        "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"
    };
    private static final int ZZ_ATTRIBUTE[] = zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\001\000\001\t\003\001\001\t\007\001\006\t\005\000\004\001\003\t\004\000\003\001\001\000\001\t\f\000\002\t\001\000\001\t\002\000\001\t\001\000\001\t";
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
    public AstParser theParser;

}