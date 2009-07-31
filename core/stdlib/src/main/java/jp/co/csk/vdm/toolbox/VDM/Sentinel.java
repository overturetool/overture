// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Sentinel.java

package jp.co.csk.vdm.toolbox.VDM;


// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            CGException, EvaluatePP

public abstract class Sentinel
{

    public Sentinel()
    {
    }

    public void init(int i, EvaluatePP evaluatepp)
    {
        instance = evaluatepp;
        act = new int[i];
        fin = new int[i];
        req = new int[i];
        active = new int[i];
        waiting = new int[i];
    }

    public synchronized void entering(int i)
        throws CGException
    {
        requesting(i);
        try
        {
            if(!instance.evaluatePP(i).booleanValue())
            {
                waiting(i, 1);
                for(; !instance.evaluatePP(i).booleanValue(); wait());
                waiting(i, -1);
            }
        }
        catch(InterruptedException interruptedexception) { }
        activating(i);
    }

    public synchronized void leaving(int i)
    {
        fin[i]++;
        active[i]--;
        stateChanged();
    }

    public synchronized void stateChanged()
    {
        notifyAll();
    }

    private synchronized void requesting(int i)
    {
        req[i]++;
        stateChanged();
    }

    private synchronized void activating(int i)
    {
        act[i]++;
        active[i]++;
        stateChanged();
    }

    private synchronized void waiting(int i, int j)
    {
        waiting[i] += j;
        stateChanged();
    }

    public volatile int act[];
    public volatile int fin[];
    public volatile int req[];
    public volatile int active[];
    public volatile int waiting[];
    EvaluatePP instance;
}