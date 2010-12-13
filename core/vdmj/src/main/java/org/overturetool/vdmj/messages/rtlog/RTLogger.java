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

package org.overturetool.vdmj.messages.rtlog;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.rtlog.RTThreadSwapMessage.SwapType;
import org.overturetool.vdmj.values.CPUValue;

public class RTLogger
{
	private static boolean enabled = false;
	private static List<RTMessage> events = new LinkedList<RTMessage>();
	private static PrintWriter logfile = null;
	private static RTMessage cached = null;
	private static final List<RTMessage> deployEvents = new LinkedList<RTMessage>();

	public static synchronized void enable(boolean on)
	{
		if (!on)
		{
			dump(true);
			cached = null;
		}

		enabled = on;
	}

	
	public static synchronized void log(RTMessage message)
	{
		if (!enabled)
		{
			return;
		}
		// generate any static deploys required for this message
		message.generateStaticDeploys();
		
		//TODO remove all filtering and clean up when the new log standard have been defined
		if (message.time == 0)
		{
			if (RTMessage.cachedStaticDeploys.size() > 0)
			{
				deployEvents.addAll(RTMessage.cachedStaticDeploys);
				RTMessage.cachedStaticDeploys.clear();
			}
			deployEvents.add(message);
		} else
		{
			if (deployEvents.size() > 0)
			{
				cleanUpDeployment();
				moveMessageToStart(RTDeployObjectMessage.class);
				moveMessageToStart(RTDeclareBUSMessage.class);
				moveMessageToStart(RTDeclareCPUMessage.class);
				updateCpuForSystemCreationOperations();
				removeSystemCreatedOperations();
				for (RTMessage deployMessage : deployEvents)
				{
					doLog(deployMessage);
				}
				deployEvents.clear();
			}
			if (RTMessage.cachedStaticDeploys.size() > 0)
			{
				for (RTDeployStaticMessage deployStatic : RTMessage.cachedStaticDeploys)
				{
					doLog(deployStatic);
				}
				RTMessage.cachedStaticDeploys.clear();
			}
			doLog(message);
		}

	}

	private synchronized static void updateCpuForSystemCreationOperations()
	{
		//TODO this method should be removed when the new log standard have been defined
		for (RTMessage message : deployEvents)
		{
			if (message instanceof RTOperationMessage)
			{
				RTOperationMessage opMsg = (RTOperationMessage) message;
				if (opMsg.madeDuringSystemConstruction && opMsg.getCpu() == CPUValue.vCPU.resource)
				{
					// ok we have to fix the CPU since vCPU is used during object construction in the system
					// constructor, but objects can have calls in their constructor, which may end here
					for (RTMessage message1 : deployEvents)
					{
						if (message1 instanceof RTDeployObjectMessage)
						{
							RTDeployObjectMessage deployMsg = (RTDeployObjectMessage) message1;
							if(deployMsg.getObjRef() == opMsg.getObjRef())
							{
								opMsg.updateCpu(deployMsg.getCpu());
							}
						}
					}
				}
			}

		}

	}
	
	private synchronized static void removeSystemCreatedOperations()
	{
		//TODO this method should be removed when the new log standard have been defined
		List<RTMessage> tmp = new Vector<RTMessage>();
		for (RTMessage message : deployEvents)
		{
			if (message instanceof RTOperationMessage)
			{
				RTOperationMessage opMsg = (RTOperationMessage) message;
				if (opMsg.madeDuringSystemConstruction)
				{
					tmp.add(message);
				}
			}
		}
		deployEvents.removeAll(tmp);
	}

	private synchronized static void cleanUpDeployment()
	{
		//TODO this method should be removed when the new log standard have been defined
		for (int i = deployEvents.size() - 1; i >= 0; i--)
		{
			RTMessage item = deployEvents.get(i);
			if (item instanceof RTDeployObjectMessage)
			{
				for (int j = 0; j < deployEvents.size(); j++)
				{
					RTMessage priviousItem = deployEvents.get(j);
					if (priviousItem instanceof RTDeployObjectMessage)
					{
						RTDeployObjectMessage priviousDeploy = (RTDeployObjectMessage) priviousItem;
						if (item != priviousDeploy
								&& ((RTDeployObjectMessage) item).object.objectReference == priviousDeploy.object.objectReference)
						{
							deployEvents.remove(priviousDeploy);
						}
					}
				}
			}

		}
	}

	// private synchronized static void moveDeployMessageToStart()
	// {
	// List<VDMRTMessage> deploys = new Vector<VDMRTMessage>();
	// for (VDMRTMessage message : deployEvents)
	// {
	// if(message instanceof VDMRTDeployObjectMessage)
	// {
	// deploys.add(message);
	// }
	// }
	// deployEvents.removeAll(deploys);
	//		
	// deployEvents.addAll(0, deploys);
	//		
	// }
	//	
	@SuppressWarnings("unchecked")
	private synchronized static void moveMessageToStart(Class definition)
	{
		//TODO this method should be removed when the new log standard have been defined
		List<RTMessage> tmp = new Vector<RTMessage>();
		for (RTMessage message : deployEvents)
		{
			if (message.getClass().equals(definition))
			{
				tmp.add(message);
			}
		}
		deployEvents.removeAll(tmp);

		deployEvents.addAll(0, tmp);

	}
	
	private static synchronized void doLog(RTMessage message)
	{
		RTMessage event = message;

		if (event instanceof RTThreadSwapMessage && (((RTThreadSwapMessage)event).getType()==SwapType.In 
				|| ((RTThreadSwapMessage)event).getType()==SwapType.DelayedIn))
		{
			if (cached != null)
			{
				doInternalLog(cached);
			}

			cached = event;
			return;
		}

		if (cached != null)
		{
			if (event instanceof RTThreadSwapMessage && ((RTThreadSwapMessage)event).getType()==SwapType.Out)
			{
				RTThreadMessage eventThreadMessage = (RTThreadMessage) event;
				if(cached instanceof RTThreadSwapMessage)
				{
					RTThreadSwapMessage cachedThreadSwap = (RTThreadSwapMessage) cached;
					
					if((cachedThreadSwap.getType() == SwapType.DelayedIn || cachedThreadSwap.getType() == SwapType.In)
							&& cachedThreadSwap.equals(eventThreadMessage.getThread())
							&& cachedThreadSwap.getLogTime().equals(eventThreadMessage.getLogTime()))
					{
						cached = null;
						return;
					}
						
				}
			}

			doInternalLog(cached);
			cached = null;
		}

		doInternalLog(event);
	}

	
	private static void doInternalLog(RTMessage event)
	{
		if (logfile == null)
		{
			Console.out.println(event);
		} else
		{
			events.add(event);

			if (events.size() > 1000)
			{
				dump(false);
			}
		}
	}

	public static void setLogfile(PrintWriter out)
	{
		enabled = true;
		dump(true); // Write out and close previous
		logfile = out;
		cached = null;
	}

	public static int getLogSize()
	{
		return events.size();
	}

	public static synchronized void dump(boolean close)
	{
		if (logfile != null)
		{
			for (RTMessage event : events)
			{
				logfile.println(event.getMessage());
			}

			logfile.flush();
			events.clear();

			if (close)
			{
				logfile.close();
			}
		}
	}
}
