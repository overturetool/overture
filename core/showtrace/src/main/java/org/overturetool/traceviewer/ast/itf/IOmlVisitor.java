// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:55
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   IOmlVisitor.java

package org.overturetool.traceviewer.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.CGException;

// Referenced classes of package org.overturetool.tracefile.ast.itf:
//            IOmlContextInfo, IOmlNode, IOmlDocument, IOmlLexem, 
//            IOmlDeployObj, IOmlInstVarChange, IOmlThreadSwapOut, IOmlThreadSwapIn, 
//            IOmlDelayedThreadSwapIn, IOmlReplyRequest, IOmlOpRequest, IOmlMessageActivate, 
//            IOmlCPUdecl, IOmlOpCompleted, IOmlTraceFile, IOmlThreadCreate, 
//            IOmlMessageCompleted, IOmlMessageRequest, IOmlBUSdecl, IOmlThreadKill, 
//            IOmlOpActivate, IOmlTraceEvent

public interface IOmlVisitor
{

    public abstract void visitContextInfo(IOmlContextInfo iomlcontextinfo)
        throws CGException;

    public abstract void visitNode(IOmlNode iomlnode)
        throws CGException;

    public abstract void visitDocument(IOmlDocument iomldocument)
        throws CGException;

    public abstract void visitLexem(IOmlLexem iomllexem)
        throws CGException;

    public abstract void visitDeployObj(IOmlDeployObj iomldeployobj)
        throws CGException;

    public abstract void visitInstVarChange(IOmlInstVarChange iomlinstvarchange)
        throws CGException;

    public abstract void visitThreadSwapOut(IOmlThreadSwapOut iomlthreadswapout)
        throws CGException;

    public abstract void visitThreadSwapIn(IOmlThreadSwapIn iomlthreadswapin)
        throws CGException;

    public abstract void visitDelayedThreadSwapIn(IOmlDelayedThreadSwapIn iomldelayedthreadswapin)
        throws CGException;

    public abstract void visitReplyRequest(IOmlReplyRequest iomlreplyrequest)
        throws CGException;

    public abstract void visitOpRequest(IOmlOpRequest iomloprequest)
        throws CGException;

    public abstract void visitMessageActivate(IOmlMessageActivate iomlmessageactivate)
        throws CGException;

    public abstract void visitCPUdecl(IOmlCPUdecl iomlcpudecl)
        throws CGException;

    public abstract void visitOpCompleted(IOmlOpCompleted iomlopcompleted)
        throws CGException;

    public abstract void visitTraceFile(IOmlTraceFile iomltracefile)
        throws CGException;

    public abstract void visitThreadCreate(IOmlThreadCreate iomlthreadcreate)
        throws CGException;

    public abstract void visitMessageCompleted(IOmlMessageCompleted iomlmessagecompleted)
        throws CGException;

    public abstract void visitMessageRequest(IOmlMessageRequest iomlmessagerequest)
        throws CGException;

    public abstract void visitBUSdecl(IOmlBUSdecl iomlbusdecl)
        throws CGException;

    public abstract void visitThreadKill(IOmlThreadKill iomlthreadkill)
        throws CGException;

    public abstract void visitOpActivate(IOmlOpActivate iomlopactivate)
        throws CGException;

    public abstract void visitTraceEvent(IOmlTraceEvent iomltraceevent)
        throws CGException;
}