package org.overture.codegen.runtime;

public class Sentinel
{
	public  volatile long[] act;
	public  volatile long[] fin;
	public  volatile long[] req;
	public  volatile long[] active;
	public  volatile long[] waiting;
	
	EvaluatePP instance;
	
	public void init(EvaluatePP inst, long nrf )
	{
		instance = inst;
		act = new long[(int)nrf];
		fin = new long[(int)nrf];
		req = new long[(int)nrf];
		active = new long[(int)nrf];
		waiting = new long[(int)nrf];
	}
	
	public synchronized void entering(long fnr2) {
		int fnr = (int) fnr2;
		requesting(fnr);
		try{
			if(! instance.evaluatePP(fnr).booleanValue())
			{
				waiting(fnr, +1);
				while (!instance.evaluatePP(fnr).booleanValue())
				{
					this.wait();
				}
			waiting(fnr, -1);
			}
		}catch(InterruptedException e){}
		activating(fnr);
	}
	
	public synchronized void leaving(long fnr2){
		int fn = (int) fnr2;
		fin[fn]++;
		active[fn]--;
		stateChanged();
	}
	
	public synchronized void stateChanged(){
		notifyAll();
	}
	
	private synchronized void requesting(int fn){
		req[fn]++;
		stateChanged();
	}
	
	private synchronized void activating(int fn){
		act[fn]++;
		active[fn]++;
		stateChanged();
	}
	
	private synchronized void waiting(int fn, int offset){
		waiting[fn] += offset;
		stateChanged();
	}
}
