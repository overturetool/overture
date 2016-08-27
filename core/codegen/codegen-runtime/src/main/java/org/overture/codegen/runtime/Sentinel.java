package org.overture.codegen.runtime;

/**
 * This is the Sentinel class which the inner classes are extending. It contains methods and variables for holding and
 * manipulating the history counters existing in VDM++
 * 
 * @author gkanos
 */
public class Sentinel
{
	public volatile long[] act; // holds the #act history counter.
	public volatile long[] fin; // holds the #fin history counter.
	public volatile long[] req; // holds the #req history counter.
	public volatile long[] active; // holds the #active history counter.
	public volatile long[] waiting; // holds the #waiting history counter.

	public EvaluatePP instance; // hold the instance of the class that the evaluatePP() is going to be used.

	// the initialization of the instance of Sentinel class.
	// Takes as parameters the instance of the class implementing the EvaluatePP
	// and the number of methods to define the size of the arrays.
	public void init(EvaluatePP inst, long nrf)
	{
		instance = inst;
		act = new long[(int) nrf];
		fin = new long[(int) nrf];
		req = new long[(int) nrf];
		active = new long[(int) nrf];
		waiting = new long[(int) nrf];
	}

	// This methods is used to enable the activation of a method.
	public synchronized void entering(long fnr2)
	{
		int fnr = (int) fnr2; // Here the long value is casted into in to be used as index to the arrays.
		requesting(fnr);// the #req counter is changed to add one to it.
		try
		{
			if (!instance.evaluatePP(fnr).booleanValue()) // the first evaluation of the permition predicate.
			{
				waiting(fnr, +1);// if the permission predicate is false. It add one to the #waiting counter.
				while (!instance.evaluatePP(fnr).booleanValue())// reevaluation of the permission predicate.
				{
					this.wait(); // actual thread wait method. This freeze the thread waiting for the execution of its
									// method.
				}
				waiting(fnr, -1); // if predicate changes to true, #waiting is changed to remove one.
			}
		} catch (InterruptedException e)
		{
		}
		activating(fnr);// the #act and the #active counters are change to add one to them
	}

	// this method is registering the termination of a method.
	public synchronized void leaving(long fnr2)
	{
		int fn = (int) fnr2;
		fin[fn]++; // changes the #fin counter adding one to it.
		active[fn]--; // changes the #active counter removing one to it.
		stateChanged();
	}

	// this method notifies the threads that a counter has be changed to reevaluate their permission predicates.
	public synchronized void stateChanged()
	{
		notifyAll();
	}

	// The method that actually changes the #req history counter.
	private synchronized void requesting(int fn)
	{
		req[fn]++;
		stateChanged();
	}

	// The method that actually changing the #act and #active history counters.
	private synchronized void activating(int fn)
	{
		act[fn]++;
		active[fn]++;
		stateChanged();
	}

	// The method that actually changing the #waiting history counter.
	// The offset defines how many methods of the same name are waiting.
	private synchronized void waiting(int fn, int offset)
	{
		waiting[fn] += offset;
		stateChanged();
	}
}
