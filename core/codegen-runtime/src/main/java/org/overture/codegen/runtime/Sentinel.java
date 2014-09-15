package org.overture.codegen.runtime;

public class Sentinel
{
	public volatile int[] act;
	public volatile int[] fin;
	public volatile int[] req;
	public volatile int[] active;
	public volatile int[] waiting;
	
	public void init(int nrf )
	{
		//instance = inst;
		act = new int[nrf];
		fin = new int[nrf];
		req = new int[nrf];
		active = new int[nrf];
		waiting = new int[nrf];
	}
	
	public synchronized void entering(int fnr) throws InterruptedException {
		requesting(fnr);
		try{
			waiting(fnr, +1);
			this.wait();
			waiting(fnr, -1);
		}
		finally{
			activating(fnr);
		}
		
	}
	
	public synchronized void leaving(int fn){
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
