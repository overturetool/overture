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


public class RANDOM
{

    public RANDOM()
    {
        seed = 0L;
        seed = 0L;
    }

    public RANDOM(long l)
    {
        seed = 0L;
        seed = l;
    }

    public void set_seed(long l)
    {
        seed = l;
    }

    public long vdm_rand()
    {
        int i = (int)(seed / 44488L);
        int j = (int)(seed % 44488L);
        int k = (int)(48271L * (long)j - 3399L * (long)i);
        if(k > 0)
            seed = k;
        else
            seed = (long)k + 0x7fffffffL;
        return seed;
    }

    public int get_random(int i)
    {
        return (int)(((double)vdm_rand() / 2147483647D) * (double)i);
    }

    public long get_seed()
    {
        return seed;
    }

    private static final long m = 0x7fffffffL;
    private static final long q = 44488L;
    private static final long a = 48271L;
    private static final long r = 3399L;
    public static final long successfulltest = 0x17cc5ab9L;
    private long seed;
}
