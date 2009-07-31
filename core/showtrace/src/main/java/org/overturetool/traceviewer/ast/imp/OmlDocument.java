// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlDocument.java

package org.overturetool.traceviewer.ast.imp;

import java.util.Vector;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.*;
import org.overturetool.traceviewer.visitor.VdmPpVisitor;
import org.overturetool.traceviewer.visitor.VdmSlVisitor;


// Referenced classes of package org.overturetool.tracefile.ast.imp:
//            OmlLexem

public class OmlDocument
    implements IOmlDocument
{

    public OmlDocument()
        throws CGException
    {
        ivFilename = null;
        ivTopNode = null;
        ivLexems = null;
        try
        {
            ivFilename = UTIL.ConvertToString(new String());
            ivTopNode = null;
            ivLexems = new Vector();
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public String getFilename()
        throws CGException
    {
        return ivFilename;
    }

    public void setFilename(String pfilename)
        throws CGException
    {
        ivFilename = UTIL.ConvertToString(UTIL.clone(pfilename));
    }

    public Boolean hasTraceFile()
        throws CGException
    {
        return new Boolean(ivTopNode instanceof IOmlTraceFile);
    }

    public IOmlTraceFile getTraceFile()
        throws CGException
    {
        if(!pre_getTraceFile().booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getTraceFile");
        return (IOmlTraceFile)ivTopNode;
    }

    public Boolean pre_getTraceFile()
        throws CGException
    {
        return hasTraceFile();
    }

    public void setTraceFile(IOmlTraceFile pNode)
        throws CGException
    {
        if(!pre_setTraceFile(pNode).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in setTraceFile");
        ivTopNode = (IOmlNode)UTIL.clone(pNode);
    }

    public Boolean pre_setTraceFile(IOmlTraceFile pNode)
        throws CGException
    {
        return new Boolean(UTIL.equals(ivTopNode, null));
    }

    public Vector getLexems()
        throws CGException
    {
        return ivLexems;
    }

    public void setLexems(Vector plexems)
        throws CGException
    {
        ivLexems = (Vector)UTIL.ConvertToList(UTIL.clone(plexems));
    }

    public IOmlLexem createLexem(Long pline, Long pcolumn, Long plexval, String ptext, Long ptype)
        throws CGException
    {
        IOmlLexem res = new OmlLexem(pline, pcolumn, plexval, ptext, new Long((new Long(ivLexems.size())).intValue() + (new Long(1L)).intValue()), ptype);
        ivLexems.add(res);
        return res;
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitDocument(this);
    }

    public String toVdmSlValue()
        throws CGException
    {
        VdmSlVisitor visitor = new VdmSlVisitor();
        accept(visitor);
        String rexpr_3 = null;
        rexpr_3 = visitor.result;
        return rexpr_3;
    }

    public String toVdmPpValue()
        throws CGException
    {
        VdmPpVisitor visitor = new VdmPpVisitor();
        accept(visitor);
        String rexpr_3 = null;
        rexpr_3 = visitor.result;
        return rexpr_3;
    }

    public OmlDocument(String pfilename, IOmlNode pnode, Vector plexems)
        throws CGException
    {
        ivFilename = null;
        ivTopNode = null;
        ivLexems = null;
        if(!pre_OmlDocument(pfilename, pnode, plexems).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in OmlDocument");
        setFilename(pfilename);
        ivTopNode = (IOmlNode)UTIL.clone(pnode);
        setLexems(plexems);
    }

    public Boolean pre_OmlDocument(String pfilename, IOmlNode pnode, Vector plexems)
        throws CGException
    {
        return new Boolean(pnode instanceof IOmlTraceFile);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private String ivFilename;
    private IOmlNode ivTopNode;
    private Vector ivLexems;

}