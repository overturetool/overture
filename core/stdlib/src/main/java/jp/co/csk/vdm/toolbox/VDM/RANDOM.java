// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RANDOM.java

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