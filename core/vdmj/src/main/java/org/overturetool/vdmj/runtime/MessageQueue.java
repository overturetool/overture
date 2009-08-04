/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.runtime;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue<T> extends LinkedBlockingQueue<T>
{
	private static final long serialVersionUID = 1L;

	public MessageQueue()
	{
		// ?
	}

	@Override
	public synchronized boolean add(T item)
	{
		boolean rv = super.add(item);
		notifyAll();
		return rv;
	}

	@Override
	public synchronized T take()
	{
		if (!isEmpty())
		{
			while (true)
			{
	    		try
	    		{
	    			return super.take();
	       		}
	    		catch (InterruptedException e)
	    		{
					throw new RuntimeException("Thread stopped");
	    		}
			}
		}
		else
		{
			while (isEmpty())
			{
				try
				{
					wait();
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException("Thread stopped");
				}
			}

			while (true)
			{
	    		try
	    		{
	    			return super.take();
	       		}
	    		catch (InterruptedException e)
	    		{
					throw new RuntimeException("Thread stopped");
	    		}
			}
 		}
	}
}
