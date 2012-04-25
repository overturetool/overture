// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlLexem.java

package org.overturetool.traceviewer.ast.imp;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlLexem;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

public class OmlLexem
    implements IOmlLexem
{

    public OmlLexem()
        throws CGException
    {
        ivLine = null;
        ivColumn = null;
        ivLexval = null;
        ivText = null;
        ivIndex = null;
        ivType = null;
        try
        {
            ivLine = new Long(0L);
            ivColumn = new Long(0L);
            ivLexval = new Long(0L);
            ivText = UTIL.ConvertToString(new String());
            ivIndex = new Long(0L);
            ivType = ILEXEMUNKNOWN;
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public Long getLine()
        throws CGException
    {
        return ivLine;
    }

    public void setLine(Long pline)
        throws CGException
    {
        ivLine = UTIL.NumberToLong(UTIL.clone(pline));
    }

    public Long getColumn()
        throws CGException
    {
        return ivColumn;
    }

    public void setColumn(Long pcolumn)
        throws CGException
    {
        ivColumn = UTIL.NumberToLong(UTIL.clone(pcolumn));
    }

    public Long getLexval()
        throws CGException
    {
        return ivLexval;
    }

    public void setLexval(Long plexval)
        throws CGException
    {
        ivLexval = UTIL.NumberToLong(UTIL.clone(plexval));
    }

    public String getText()
        throws CGException
    {
        return ivText;
    }

    public void setText(String ptext)
        throws CGException
    {
        ivText = UTIL.ConvertToString(UTIL.clone(ptext));
    }

    public Long getIndex()
        throws CGException
    {
        return ivIndex;
    }

    public void setIndex(Long pindex)
        throws CGException
    {
        ivIndex = UTIL.NumberToLong(UTIL.clone(pindex));
    }

    public Long getType()
        throws CGException
    {
        return ivType;
    }

    public Boolean isReservedWord()
        throws CGException
    {
        return new Boolean(ivType.intValue() == ILEXEMRESERVEDWORD.intValue());
    }

    public Boolean isIdentifier()
        throws CGException
    {
        return new Boolean(ivType.intValue() == ILEXEMIDENTIFIER.intValue());
    }

    public Boolean isLineComment()
        throws CGException
    {
        return new Boolean(ivType.intValue() == ILEXEMLINECOMMENT.intValue());
    }

    public Boolean isBlockComment()
        throws CGException
    {
        return new Boolean(ivType.intValue() == ILEXEMBLOCKCOMMENT.intValue());
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitLexem(this);
    }

    public OmlLexem(Long pline, Long pcolumn, Long plexval, String ptext, Long pindex, Long ptype)
        throws CGException
    {
        this();
        ivLine = UTIL.NumberToLong(UTIL.clone(pline));
        ivColumn = UTIL.NumberToLong(UTIL.clone(pcolumn));
        ivLexval = UTIL.NumberToLong(UTIL.clone(plexval));
        ivText = UTIL.ConvertToString(UTIL.clone(ptext));
        ivIndex = UTIL.NumberToLong(UTIL.clone(pindex));
        ivType = UTIL.NumberToLong(UTIL.clone(ptype));
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private Long ivLine;
    private Long ivColumn;
    private Long ivLexval;
    private String ivText;
    private Long ivIndex;
    private Long ivType;
    public static final Long ILEXEMUNKNOWN = new Long(0L);
    public static final Long ILEXEMRESERVEDWORD = new Long(1L);
    public static final Long ILEXEMIDENTIFIER = new Long(2L);
    public static final Long ILEXEMLINECOMMENT = new Long(3L);
    public static final Long ILEXEMBLOCKCOMMENT = new Long(4L);

}