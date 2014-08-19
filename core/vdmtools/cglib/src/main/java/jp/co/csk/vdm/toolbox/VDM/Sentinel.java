/*
 * #%~
 * VDM Tools Code Generator library
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
