// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   VDMThread.java

package jp.co.csk.vdm.toolbox.VDM;


public class VDMThread extends Thread
{

    public VDMThread(Runnable runnable)
    {
        super(runnable);
        id = count++;
    }

    public VDMThread()
    {
        id = count++;
    }

    public long getId()
    {
        return id;
    }

    public static Integer getThreadId()
    {
        return new Integer((int)((VDMThread)(VDMThread)Thread.currentThread()).getId());
    }

    protected long id;
    protected static int count = 0;

}