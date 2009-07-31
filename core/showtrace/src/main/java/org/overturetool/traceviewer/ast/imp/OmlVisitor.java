// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:23
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlVisitor.java

package org.overturetool.traceviewer.ast.imp;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;
import org.overturetool.traceviewer.ast.itf.*;

public class OmlVisitor
    implements IOmlVisitor
{

    public OmlVisitor()
        throws CGException
    {
    }

    public void visitDocument(IOmlDocument iomldocument)
        throws CGException
    {
    }

    public void visitLexem(IOmlLexem iomllexem)
        throws CGException
    {
    }

    public void visitDeployObj(IOmlDeployObj iomldeployobj)
        throws CGException
    {
    }

    public void visitInstVarChange(IOmlInstVarChange iomlinstvarchange)
        throws CGException
    {
    }

    public void visitThreadSwapOut(IOmlThreadSwapOut iomlthreadswapout)
        throws CGException
    {
    }

    public void visitThreadSwapIn(IOmlThreadSwapIn iomlthreadswapin)
        throws CGException
    {
    }

    public void visitDelayedThreadSwapIn(IOmlDelayedThreadSwapIn iomldelayedthreadswapin)
        throws CGException
    {
    }

    public void visitReplyRequest(IOmlReplyRequest iomlreplyrequest)
        throws CGException
    {
    }

    public void visitOpRequest(IOmlOpRequest iomloprequest)
        throws CGException
    {
    }

    public void visitMessageActivate(IOmlMessageActivate iomlmessageactivate)
        throws CGException
    {
    }

    public void visitCPUdecl(IOmlCPUdecl iomlcpudecl)
        throws CGException
    {
    }

    public void visitOpCompleted(IOmlOpCompleted iomlopcompleted)
        throws CGException
    {
    }

    public void visitTraceFile(IOmlTraceFile iomltracefile)
        throws CGException
    {
    }

    public void visitThreadCreate(IOmlThreadCreate iomlthreadcreate)
        throws CGException
    {
    }

    public void visitMessageCompleted(IOmlMessageCompleted iomlmessagecompleted)
        throws CGException
    {
    }

    public void visitMessageRequest(IOmlMessageRequest iomlmessagerequest)
        throws CGException
    {
    }

    public void visitBUSdecl(IOmlBUSdecl iomlbusdecl)
        throws CGException
    {
    }

    public void visitThreadKill(IOmlThreadKill iomlthreadkill)
        throws CGException
    {
    }

    public void visitOpActivate(IOmlOpActivate iomlopactivate)
        throws CGException
    {
    }

    public void visitContextInfo(IOmlContextInfo iomlcontextinfo)
        throws CGException
    {
    }

    public void visitNode(IOmlNode pNode)
        throws CGException
    {
        if((new Boolean(pNode instanceof IOmlDeployObj)).booleanValue())
            visitDeployObj((IOmlDeployObj)pNode);
        else
        if((new Boolean(pNode instanceof IOmlInstVarChange)).booleanValue())
            visitInstVarChange((IOmlInstVarChange)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadSwapOut)).booleanValue())
            visitThreadSwapOut((IOmlThreadSwapOut)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadSwapIn)).booleanValue())
            visitThreadSwapIn((IOmlThreadSwapIn)pNode);
        else
        if((new Boolean(pNode instanceof IOmlTraceEvent)).booleanValue())
            visitTraceEvent((IOmlTraceEvent)pNode);
        else
        if((new Boolean(pNode instanceof IOmlDelayedThreadSwapIn)).booleanValue())
            visitDelayedThreadSwapIn((IOmlDelayedThreadSwapIn)pNode);
        else
        if((new Boolean(pNode instanceof IOmlReplyRequest)).booleanValue())
            visitReplyRequest((IOmlReplyRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpRequest)).booleanValue())
            visitOpRequest((IOmlOpRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageActivate)).booleanValue())
            visitMessageActivate((IOmlMessageActivate)pNode);
        else
        if((new Boolean(pNode instanceof IOmlCPUdecl)).booleanValue())
            visitCPUdecl((IOmlCPUdecl)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpCompleted)).booleanValue())
            visitOpCompleted((IOmlOpCompleted)pNode);
        else
        if((new Boolean(pNode instanceof IOmlTraceFile)).booleanValue())
            visitTraceFile((IOmlTraceFile)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageCompleted)).booleanValue())
            visitMessageCompleted((IOmlMessageCompleted)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadCreate)).booleanValue())
            visitThreadCreate((IOmlThreadCreate)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageRequest)).booleanValue())
            visitMessageRequest((IOmlMessageRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlBUSdecl)).booleanValue())
            visitBUSdecl((IOmlBUSdecl)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpActivate)).booleanValue())
            visitOpActivate((IOmlOpActivate)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadKill)).booleanValue())
        {
            visitThreadKill((IOmlThreadKill)pNode);
        } else
        {
            UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
            return;
        }
    }

    public void visitTraceEvent(IOmlTraceEvent pNode)
        throws CGException
    {
        if((new Boolean(pNode instanceof IOmlDeployObj)).booleanValue())
            visitDeployObj((IOmlDeployObj)pNode);
        else
        if((new Boolean(pNode instanceof IOmlInstVarChange)).booleanValue())
            visitInstVarChange((IOmlInstVarChange)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadSwapOut)).booleanValue())
            visitThreadSwapOut((IOmlThreadSwapOut)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadSwapIn)).booleanValue())
            visitThreadSwapIn((IOmlThreadSwapIn)pNode);
        else
        if((new Boolean(pNode instanceof IOmlDelayedThreadSwapIn)).booleanValue())
            visitDelayedThreadSwapIn((IOmlDelayedThreadSwapIn)pNode);
        else
        if((new Boolean(pNode instanceof IOmlReplyRequest)).booleanValue())
            visitReplyRequest((IOmlReplyRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpRequest)).booleanValue())
            visitOpRequest((IOmlOpRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageActivate)).booleanValue())
            visitMessageActivate((IOmlMessageActivate)pNode);
        else
        if((new Boolean(pNode instanceof IOmlCPUdecl)).booleanValue())
            visitCPUdecl((IOmlCPUdecl)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpCompleted)).booleanValue())
            visitOpCompleted((IOmlOpCompleted)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageCompleted)).booleanValue())
            visitMessageCompleted((IOmlMessageCompleted)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadCreate)).booleanValue())
            visitThreadCreate((IOmlThreadCreate)pNode);
        else
        if((new Boolean(pNode instanceof IOmlMessageRequest)).booleanValue())
            visitMessageRequest((IOmlMessageRequest)pNode);
        else
        if((new Boolean(pNode instanceof IOmlBUSdecl)).booleanValue())
            visitBUSdecl((IOmlBUSdecl)pNode);
        else
        if((new Boolean(pNode instanceof IOmlThreadKill)).booleanValue())
            visitThreadKill((IOmlThreadKill)pNode);
        else
        if((new Boolean(pNode instanceof IOmlOpActivate)).booleanValue())
        {
            visitOpActivate((IOmlOpActivate)pNode);
        } else
        {
            UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
            return;
        }
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();

}