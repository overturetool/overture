package org.overture.codegen.runtime;

public class Sentinel
{
	public  volatile int[] act;
	public  volatile int[] fin;
	public  volatile int[] req;
	public  volatile int[] active;
	public  volatile int[] waiting;
	
	EvaluatePP instance;
	
	public void init(EvaluatePP inst, long nrf )
	{
		instance = inst;
		act = new int[(int)nrf];
		fin = new int[(int)nrf];
		req = new int[(int)nrf];
		active = new int[(int)nrf];
		waiting = new int[(int)nrf];
	}
	
	public synchronized void entering(long fnr2) throws InterruptedException {
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
