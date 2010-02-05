// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PeriodicThread.java

package jp.co.csk.vdm.toolbox.VDM;

// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            VDMThread, CGException

public abstract class PeriodicThread extends VDMThread
{

    public PeriodicThread(Integer integer, PeriodicThread periodicthread)
    {
        next = periodicthread;
        period = integer.intValue();
    }

    public void run()
    {
        long l = System.currentTimeMillis();
        do
        {
            try
            {
                threadDef();
            }
            catch(CGException cgexception)
            {
                System.out.println(cgexception.getMessage());
            }
            l += period;
            int i = (int)(l - System.currentTimeMillis());
            try
            {
                Thread.sleep(i);
            }
            catch(InterruptedException interruptedexception) { }
        } while(true);
    }

    public void invoke()
    {
        start();
        if(next != null)
            next.invoke();
    }

    public abstract void threadDef()
        throws CGException;

    protected final int period;
    PeriodicThread next;
}