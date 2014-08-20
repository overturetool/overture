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
