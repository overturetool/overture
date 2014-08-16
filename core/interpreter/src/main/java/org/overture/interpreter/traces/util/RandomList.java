package org.overture.interpreter.traces.util;

import java.util.Random;

public class RandomList
{
    private final int N;
    private int R;
    private int last;
    private Random prng;
    
    public RandomList(int N, int R, Random prng)
    {
        this.N = N;
        this.R = R;
        this.last = 0;
        this.prng = prng;
    }

    public int next()
    {
        if (R == 0)
        {
            return -1;
        }
        
        int step = (N - last) / R;
        int move = last + step;
        int jitter = prng.nextInt() % step;
        
        while (move + jitter > N)
        {
            jitter = prng.nextInt() % step;
        }
        
        last = move + jitter;
        R = R - 1;
        return last;
    }
}

