package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.overture.interpreter.messages.rtlog.IRTLogger;
import org.overture.interpreter.messages.rtlog.RTBusActivateMessage;
import org.overture.interpreter.messages.rtlog.RTBusCompletedMessage;
import org.overture.interpreter.messages.rtlog.RTBusMessage;
import org.overture.interpreter.messages.rtlog.RTBusReplyRequestMessage;
import org.overture.interpreter.messages.rtlog.RTBusRequestMessage;
import org.overture.interpreter.messages.rtlog.RTDeclareBUSMessage;
import org.overture.interpreter.messages.rtlog.RTDeclareCPUMessage;
import org.overture.interpreter.messages.rtlog.RTDeployObjectMessage;
import org.overture.interpreter.messages.rtlog.RTMessage;
import org.overture.interpreter.messages.rtlog.RTOperationMessage;
import org.overture.interpreter.messages.rtlog.RTThreadCreateMessage;
import org.overture.interpreter.messages.rtlog.RTThreadKillMessage;
import org.overture.interpreter.messages.rtlog.RTThreadMessage;
import org.overture.interpreter.messages.rtlog.RTThreadSwapMessage;
import org.overture.interpreter.messages.rtlog.RTThreadSwapMessage.SwapType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent.NextGenBusMessageEventType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent.OperationEventType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThread.ThreadType;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadSwapEvent.ThreadEventSwapType;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.scheduler.MainThread;
import org.overture.interpreter.scheduler.MessagePacket;
import org.overture.interpreter.scheduler.MessageRequest;
import org.overture.interpreter.scheduler.MessageResponse;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;

public class NextGenRTLogger implements IRTLogger
{

	private boolean enabled = false;

	public static NextGenRTLogger getInstanceFromFile(String filename)
			throws IOException, ClassNotFoundException
	{
		final NextGenRTLogger logger = new NextGenRTLogger();
		logger.readFromFile(filename);
		return logger;
	}

	private Map<Integer, NextGenCpu> cpuMap = new HashMap<>();
	private Map<Integer, NextGenBus> busMap = new HashMap<>();
	private Map<Integer, NextGenObject> objectMap = new HashMap<>();
	private Map<Integer, NextGenClassDefinition> classDefMap = new HashMap<>();
	private Map<String, NextGenOperation> operationMap = new HashMap<>();
	private Map<Long, NextGenBusMessage> busMessage = new HashMap<>();
	private Map<Long, NextGenThread> threadMap = new HashMap<>();
	// private ArrayList<INextGenEvent> events = new ArrayList<INextGenEvent>();
	private Map<Long, ArrayList<INextGenEvent>> events = new TreeMap<>();
	private NextGenBus vBus;
	private File logFile = null;
	private long currentAbsoluteTime = -1L;
	private int currentRelativeTime = -1;

	public NextGenRTLogger()
	{
		this.addBus(0, new ArrayList<>(), "vBus");
		vBus = this.busMap.get(0);
		this.addCpu(0, false, "vCpu", "system"); // Add the implicit virtual CPU - assuming expl means explicit
	}

	public Map<Long, ArrayList<INextGenEvent>> getEvents()
	{
		return this.events;
	}

	public Map<Integer, NextGenCpu> getCpuMap()
	{
		return cpuMap;
	}

	public Map<Integer, NextGenBus> getBusMap()
	{
		return busMap;
	}

	public Map<Integer, NextGenObject> getObjectMap()
	{
		return objectMap;
	}

	public Map<Integer, NextGenClassDefinition> getClassDefMap()
	{
		return classDefMap;
	}

	public Map<String, NextGenOperation> getOperationMap()
	{
		return operationMap;
	}

	public Map<Long, NextGenBusMessage> getBusMessage()
	{
		return busMessage;
	}

	public Map<Long, NextGenThread> getThreadMap()
	{
		return threadMap;
	}

	public NextGenBus getvBus()
	{
		return vBus;
	}

	public void log(RTMessage message)
	{
		if (!enabled)
		{
			return;
		}
		// Keep track of relative time among events happening at the same wall clock time
		if (currentAbsoluteTime != message.getLogTime())
		{
			currentRelativeTime = 0;
		} else
		{
			currentRelativeTime++;
		}

		currentAbsoluteTime = message.getLogTime();

		NextGenTimeStamp logTime = new NextGenTimeStamp(currentAbsoluteTime, currentRelativeTime);

		/**
		 * Declarations: CPUs and Busses
		 */
		if (message instanceof RTDeclareCPUMessage)
		{
			RTDeclareCPUMessage m = (RTDeclareCPUMessage) message;
			this.addCpu(m.cpuId, m.expl, m.cpuName, m.sysName);
		}

		if (message instanceof RTDeclareBUSMessage)
		{
			RTDeclareBUSMessage m = (RTDeclareBUSMessage) message;
			this.addBus(m.busNumber, parseCpuIds(m.cpus), m.name);
		}

		/**
		 * Object deployments
		 */
		if (message instanceof RTDeployObjectMessage)
		{
			RTDeployObjectMessage m = (RTDeployObjectMessage) message;
			this.deployObject(m.object, m.cpu);
		}

		/**
		 * Thread related: create, swap and kill
		 */
		if (message instanceof RTThreadCreateMessage)
		{
			RTThreadMessage m = (RTThreadCreateMessage) message;
			this.createThread(m.thread, m.cpuNumber, logTime);
		}

		if (message instanceof RTThreadSwapMessage)
		{
			RTThreadSwapMessage m = (RTThreadSwapMessage) message;
			this.addThreadSwap(m.thread, m.cpuNumber, m.getType(), m.overhead, m.delay, logTime);
		}

		if (message instanceof RTThreadKillMessage)
		{
			RTThreadKillMessage m = (RTThreadKillMessage) message;
			this.addThreadKill(m.thread, m.cpuNumber, logTime);
		}

		/**
		 * Operation related: request, activate and completed
		 */
		if (message instanceof RTOperationMessage)
		{
			RTOperationMessage m = (RTOperationMessage) message;
			this.treatOperationMessage(m, logTime);
		}

		/**
		 * Bus related:
		 */
		if (message instanceof RTBusMessage)
		{
			RTBusMessage m = (RTBusMessage) message;
			this.treatBusMessage(m, logTime);
		}
	}

	// public static void dump()
	// {
	// try
	// {
	// getInstance().persistToFile();
	// } catch (IOException e)
	// {
	// //silent ignore
	// }
	// }

	public void persistToFile() throws IOException
	{
		if (logFile != null)
		{

			FileOutputStream fos = new FileOutputStream(logFile + ".rtbin");
			ObjectOutputStream out = new ObjectOutputStream(fos);

			out.writeObject(cpuMap);
			out.writeObject(busMap);
			out.writeObject(objectMap);
			out.writeObject(classDefMap);
			out.writeObject(operationMap);
			out.writeObject(busMessage);
			out.writeObject(threadMap);
			out.writeObject(events);

			out.flush();
			out.close();
		}
	}

	public void printDataStructure(String fileName) throws IOException
	{

		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fstream);

		out.newLine();
		out.append("### CPUs ##########################\n");
		out.newLine();

		for (Integer cpuKey : this.cpuMap.keySet())
		{
			out.append(cpuKey.toString());
			out.append(" -> ");
			out.append(this.cpuMap.get(cpuKey).toString());
			out.newLine();
		}

		out.newLine();
		out.append("### Busses ########################\n");
		out.newLine();

		for (Integer busKey : this.busMap.keySet())
		{
			out.append(busKey.toString());
			out.append(" -> ");
			out.append(this.busMap.get(busKey).toString());
			out.newLine();
		}

		out.newLine();
		out.append("### Objects #######################\n");
		out.newLine();

		for (Integer objectKey : this.objectMap.keySet())
		{
			out.append(objectKey.toString());
			out.append(" -> ");
			out.append(this.objectMap.get(objectKey).toString());
			out.newLine();
		}

		out.newLine();
		out.append("### Operations #####################\n");
		out.newLine();
		for (String opKey : this.operationMap.keySet())
		{
			out.append(opKey.toString());
			out.append(" -> ");
			out.append(this.operationMap.get(opKey).toString());
			out.newLine();
		}

		out.newLine();
		out.append("### Bus Msgs ######################\n");
		out.newLine();
		for (Long busMsgKey : this.busMessage.keySet())
		{
			out.append(busMsgKey.toString());
			out.append(" -> ");
			out.append(this.busMessage.get(busMsgKey).toString());
			out.newLine();
		}

		// System.out.println("Printing events");
		out.newLine();
		out.append("### Events ######################\n");
		out.newLine();
		for (Map.Entry<Long, ArrayList<INextGenEvent>> entry : events.entrySet())
		{
			for (INextGenEvent e : entry.getValue())
			{
				out.append(Long.toString(e.getTime().getAbsoluteTime()));
				out.append(", ");
				out.append(Long.toString(e.getTime().getRelativeTime()));
				out.append(" -> ");
				out.append(e.toString());
				out.newLine();
			}
		}

		out.newLine();
		out.append("### Threads ######################\n");
		out.newLine();
		for (Long threadKey : this.threadMap.keySet())
		{
			out.append(Long.toString(threadKey));
			out.append(" -> ");
			out.append(this.threadMap.get(threadKey).toString());
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public void setLogfile(File logfile)
	{
		this.logFile = logfile;
		this.enabled = logfile != null;
	}

	// Writing to log
	public void toFile() throws IOException
	{
		if (logFile != null)
		{
			FileWriter fstream = new FileWriter(logFile + ".rttxt");
			BufferedWriter out = new BufferedWriter(fstream);

			writeCPUdecls(out);
			writeBUSdecls(out);
			writeDeployObjs(out);
			writeEvents(out);

			out.flush();
			out.close();
		}

	}

	private void treatBusMessage(RTBusMessage message, NextGenTimeStamp time)
	{

		MessagePacket mp = message.message;
		NextGenBusMessage busMessage = null;

		if (message instanceof RTBusRequestMessage)
		{
			NextGenOperation operation = this.operationMap.get(getClassName(mp.operation)
					+ mp.operation.name);

			busMessage = new NextGenBusMessage(mp.msgId, this.busMap.get(mp.bus.getNumber()), this.cpuMap.get(mp.from.getNumber()), this.cpuMap.get(mp.to.getNumber()), this.threadMap.get(mp.thread.getId()), operation, // this.operationMap.get(mp.operation.classdef.getName()
																																																							// +
																																																							// mp.operation.name),
			((MessageRequest) mp).getSize(), this.objectMap.get(mp.target.objectReference));

			this.busMessage.put(mp.msgId, busMessage);
			NextGenBusMessageEvent e = new NextGenBusMessageEvent(busMessage, NextGenBusMessageEventType.REQUEST, time);

			addEvent(e);
		}

		if (message instanceof RTBusActivateMessage)
		{
			busMessage = this.busMessage.get(mp.msgId);
			NextGenBusMessageEvent e = new NextGenBusMessageEvent(busMessage, NextGenBusMessageEventType.ACTIVATE, time);
			addEvent(e);
		}

		if (message instanceof RTBusCompletedMessage)
		{
			busMessage = this.busMessage.get(mp.msgId);
			NextGenBusMessageEvent e = new NextGenBusMessageEvent(busMessage, NextGenBusMessageEventType.COMPLETED, time);
			addEvent(e);
		}

		if (message instanceof RTBusReplyRequestMessage)
		{
			busMessage = this.busMessage.get(((MessageResponse) message.message).originalId);
			NextGenThread replyingThread = this.threadMap.get(((MessageResponse) message.message).thread.getId());

			NextGenBusMessage replyBusMessage = new NextGenBusMessage(mp.msgId, this.busMap.get(mp.bus.getNumber()), this.cpuMap.get(mp.from.getNumber()), this.cpuMap.get(mp.to.getNumber()), replyingThread, busMessage.callerThread, busMessage.operation, ((MessageResponse) mp).getSize(), busMessage.object);

			this.busMessage.put(mp.msgId, replyBusMessage);
			NextGenBusMessageEvent e = new NextGenBusMessageReplyRequestEvent(busMessage, replyBusMessage, time);
			addEvent(e);
		}

	}

	private String getClassName(OperationValue operation)
	{

		if (operation.getSelf() != null)
		{
			return operation.getSelf().type.getName().getName();
		} else
		{
			// return SClassDefinitionAssistantInterpreter.getName(operation.classdef);
			if (operation.classdef.getName() != null)
			{
				return operation.classdef.getName().getName();
			}

			return null;
		}
	}

	private NextGenThread getThread(long id)
	{
		return this.threadMap.get(id);
	}

	private NextGenObject getObject(Long objref)
	{
		return this.objectMap.get(objref.intValue());
	}

	private void treatOperationMessage(RTOperationMessage m,
			NextGenTimeStamp time)
	{

		String opName = m.operationVal.name.toString();
		String className = getClassName(m.operationVal);

		boolean isAsync = m.operationVal.isAsync;
		boolean isStatic = m.operationVal.isStatic;

		NextGenOperation op = getOperation(className, opName);

		if (op == null)
		{
			op = new NextGenOperation(opName, getClassDefinition(className), isAsync, isStatic);
		}

		this.operationMap.put(className + opName, op);

		OperationEventType eventType = null;

		switch (m.messageType)
		{
			case Activate:
				eventType = OperationEventType.ACTIVATE;
				break;
			case Request:
				eventType = OperationEventType.REQUEST;
				break;
			case Completed:
				eventType = OperationEventType.COMPLETE;
			default:
				break;
		}

		NextGenObject obj = null;
		if (m.objref != null)
		{
			obj = getObject(m.objref);
		}

		NextGenOperationEvent opEvent = new NextGenOperationEvent(getThread(m.threadId), time, op, obj, eventType);

		addEvent(opEvent);

	}

	private NextGenOperation getOperation(String className, String opName)
	{
		return this.operationMap.get(className + opName);
	}

	private void addThreadKill(ISchedulableThread thread,
			CPUResource cpuNumber, NextGenTimeStamp time)
	{

		NextGenThread t = this.threadMap.get(thread.getId());
		NextGenThreadEvent threadEvent = new NextGenThreadEvent(t, time, NextGenThreadEvent.ThreadEventType.KILL);
		// t.addEvent(threadEvent);
		addEvent(threadEvent);
	}

	private void addThreadSwap(ISchedulableThread thread,
			CPUResource cpuNumber, SwapType swapType, int overhead, long delay,
			NextGenTimeStamp time)
	{

		NextGenThread t = this.threadMap.get(thread.getId());
		ThreadEventSwapType ngSwapType = null;

		switch (swapType)
		{
			case In:
				ngSwapType = ThreadEventSwapType.SWAP_IN;
				break;
			case DelayedIn:
				ngSwapType = ThreadEventSwapType.DELAYED_IN;
				break;
			case Out:
				ngSwapType = ThreadEventSwapType.SWAP_OUT;
				break;
			default:
				break;
		}

		NextGenThreadSwapEvent swapEvent = new NextGenThreadSwapEvent(t, time, ngSwapType, overhead, delay);
		addEvent(swapEvent);
	}

	private void addCpu(int id, boolean expl, String name,
			String systemClassName)
	{
		NextGenCpu cpu = new NextGenCpu(id, expl, name, systemClassName);
		this.cpuMap.put(cpu.id, cpu);

		vBus.cpus.add(cpu);
	}

	private void addBus(int busNumber, List<Integer> cpus, String name)
	{
		ArrayList<NextGenCpu> newCpus = new ArrayList<>();

		for (Integer cpuId : cpus)
		{
			newCpus.add(this.cpuMap.get(cpuId));
		}
		NextGenBus bus = new NextGenBus(busNumber, name, newCpus);
		this.busMap.put(bus.id, bus);
	}

	private void deployObject(ObjectValue object, CPUResource cpuResource)
	{
		NextGenObject o = new NextGenObject(object.objectReference, getClassDefinition(object.type.getName().getName()), cpuMap.get(cpuResource.getNumber()));
		objectMap.put(object.objectReference, o);
	}

	private NextGenClassDefinition getClassDefinition(String name)
	{
		NextGenClassDefinition classDef = null;
		if (this.classDefMap.containsKey(name))
		{
			classDef = this.classDefMap.get(name);
		} else
		{
			classDef = new NextGenClassDefinition(name);
		}

		return classDef;
	}

	private void createThread(ISchedulableThread thread, CPUResource cpuNumber,
			NextGenTimeStamp time)
	{
		long threadId = thread.getId();

		// Creates thread object
		NextGenObject object = getObjectFromThread(thread);
		NextGenCpu cpu = cpuMap.get(cpuNumber.getNumber());

		ThreadType tType = ThreadType.OBJECT;
		if (object == null && thread instanceof InitThread)
		{
			tType = ThreadType.INIT;
		} else if (thread instanceof MainThread)
		{
			tType = ThreadType.MAIN;
		}

		NextGenThread t = new NextGenThread(threadId, cpu, object, object == null ? false
				: thread.isPeriodic(), tType);

		// Creates thread create event
		NextGenThreadEvent e = new NextGenThreadEvent(t, time, NextGenThreadEvent.ThreadEventType.CREATE);

		// t.addEvent(e);
		addEvent(e);
		this.threadMap.put(thread.getId(), t);

	}

	private NextGenObject getObjectFromThread(ISchedulableThread thread)
	{
		ObjectValue obj = thread.getObject();
		int id;
		NextGenClassDefinition classDef = null;
		NextGenCpu cpu = null;

		if (obj == null)
		{
			return null;
		} else
		{
			classDef = getClassDefinition(obj.type.getName().getName());
			id = obj.objectReference;
			cpu = this.cpuMap.get(obj.getCPU().getNumber());
			return new NextGenObject(id, classDef, cpu);
		}
	}

	private List<Integer> parseCpuIds(String cpus)
	{
		List<Integer> res = new ArrayList<>();

		cpus = cpus.replace("{", "");
		cpus = cpus.replace("}", "");

		String[] ids = cpus.split(",");

		for (String string : ids)
		{
			res.add(Integer.parseInt(string));
		}

		return res;
	}

	private void writeEvents(BufferedWriter out) throws IOException
	{

		for (Map.Entry<Long, ArrayList<INextGenEvent>> entry : this.events.entrySet())
		{
			for (INextGenEvent e : entry.getValue())
			{
				out.append(e.toString());
				out.newLine();
			}
		}

	}

	private void writeDeployObjs(BufferedWriter out) throws IOException
	{

		for (NextGenObject o : this.objectMap.values())
		{
			out.append(o.toString());
			out.newLine();
		}

	}

	private void writeBUSdecls(BufferedWriter out) throws IOException
	{
		for (NextGenBus bus : this.busMap.values())
		{
			if (bus.id != 0)
			{
				out.append(bus.toString());
				out.newLine();
			}
		}

	}

	private void writeCPUdecls(BufferedWriter out) throws IOException
	{
		for (NextGenCpu cpu : this.cpuMap.values())
		{
			if (cpu.id != 0)
			{
				out.append(cpu.toString());
				out.newLine();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void readFromFile(String filename) throws IOException,
			ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(fis);

		this.cpuMap = (Map<Integer, NextGenCpu>) in.readObject();
		this.busMap = (Map<Integer, NextGenBus>) in.readObject();
		this.objectMap = (Map<Integer, NextGenObject>) in.readObject();
		this.classDefMap = (Map<Integer, NextGenClassDefinition>) in.readObject();
		this.operationMap = (Map<String, NextGenOperation>) in.readObject();
		this.busMessage = (Map<Long, NextGenBusMessage>) in.readObject();
		this.threadMap = (Map<Long, NextGenThread>) in.readObject();
		this.events = (Map<Long, ArrayList<INextGenEvent>>) in.readObject();

		in.close();

		// printDataStructure("d:\\readFromFile_structure.txt");
	}

	private void addEvent(INextGenEvent event)
	{
		NextGenTimeStamp eventTime = event.getTime();

		if (!events.containsKey(eventTime.getAbsoluteTime()))
		{
			events.put(eventTime.getAbsoluteTime(), new ArrayList<>());
		}

		ArrayList<INextGenEvent> eventList = events.get(eventTime.getAbsoluteTime());
		eventList.add(event);
	}

	@Override
	public void enable(boolean on)
	{
		this.enabled = on;
	}

	@Override
	public int getLogSize()
	{
		return this.events.size();
	}

	@Override
	public void dump(boolean close)
	{
		try
		{
			persistToFile();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
