/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.utils.communication;

import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.Map;

public class AsyncCaller
{
	static Integer nextTicket = 0;

	Map<Integer, Thread> threads = new Hashtable<Integer, Thread>();
	Map<Integer, Object> results = new Hashtable<Integer, Object>();

	private final long timeOut = 1000;

	protected synchronized Integer getNextTicket()
	{
		return ++nextTicket;
	}

	public Object request(Integer ticket, String command)
			throws java.net.SocketTimeoutException
	// throws InterruptedException
	{
		// Integer ticket = getNextTicket();

		// final Lock lock = new ReentrantLock();
		// lock.lock();
		Thread t = Thread.currentThread();
		setLock(ticket, t);
		write(command);
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Object result = null;
		long callTime = System.currentTimeMillis();
		synchronized (t)
		{
			while (result == null
					&& callTime >= System.currentTimeMillis() - timeOut)
			{
				try
				{

					t.wait(100);

				} catch (InterruptedException e)
				{
					// What we expect
				}
				result = getResult(ticket);
			}

		}

		if (result == null)
		{
			throw new SocketTimeoutException("Timeout on ticket: " + ticket
					+ " and command: " + command);
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
			// threads.get(ticket).interrupt();
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
