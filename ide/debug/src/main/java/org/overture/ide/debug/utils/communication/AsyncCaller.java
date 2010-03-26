package org.overture.ide.debug.utils.communication;

import java.util.Hashtable;
import java.util.Map;

public class AsyncCaller
{
	Integer nextTicket = 0;

	Map<Integer, Thread> threads = new Hashtable<Integer, Thread>();
	Map<Integer, Object> results = new Hashtable<Integer, Object>();

	private final long timeOut = 3000;

	protected synchronized Integer getNextTicket()
	{
		return ++nextTicket;
	}

	public Object request(Integer ticket, String command)
			//throws InterruptedException
	{
		// Integer ticket = getNextTicket();

		// final Lock lock = new ReentrantLock();
		// lock.lock();
		Thread t = Thread.currentThread();
		setLock(ticket, t);
		write(command);
		Object result = null;
		synchronized (t)
		{
			while (result == null)
			{
				try
				{

					t.wait(timeOut);

				} catch (InterruptedException e)
				{
					// What we expect
				}
				result = getResult(ticket);
			}
			
		}

		return result;

	}

	private synchronized void setLock(Integer ticket, Thread thread)
	{

		threads.put(ticket, thread);

	}

	private synchronized Object getResult(Integer ticket)
	{
		if (results.containsKey(ticket))
		{
			Object result = results.get(ticket);
			results.remove(ticket);
			return result;
		}
		return null;
	}

	protected void write(String command)
	{
		System.out.println("Write: " + command);
	}

	protected synchronized void setResult(Integer ticket, Object result)
	{
		if (threads.containsKey(ticket))
		{
			results.put(ticket, result);
			threads.get(ticket).interrupt();
			threads.remove(ticket);
		}
	}

	// private synchronized Object[] getNext()
	// {
	// Thread t = null;
	// Integer k = null;
	// for (Integer key : threads.keySet())
	// {
	//
	// k = key;
	// t = threads.get(key);
	// break;
	//
	// }
	// if (t != null && k != null)
	// {
	// threads.remove(k);
	// return new Object[] { k, t };
	// }
	// return null;
	// }

	// @Override
	// public void run()
	// {
	// while (true)
	// {
	// try
	// {
	// Thread.sleep(1);
	//
	// Object[] next = getNext();
	// if (next != null)
	// {
	// Integer key = (Integer) next[0];
	// setResult(key, "Result " + key);
	// ((Thread) next[1]).interrupt();
	//
	// }
	//
	// } catch (InterruptedException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
}
