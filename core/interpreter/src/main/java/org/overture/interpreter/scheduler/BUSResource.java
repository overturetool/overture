/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
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

package org.overture.interpreter.scheduler;

import java.util.LinkedList;
import java.util.List;

import org.overture.interpreter.messages.rtlog.RTBusActivateMessage;
import org.overture.interpreter.messages.rtlog.RTBusCompletedMessage;
import org.overture.interpreter.messages.rtlog.RTBusReplyRequestMessage;
import org.overture.interpreter.messages.rtlog.RTBusRequestMessage;
import org.overture.interpreter.messages.rtlog.RTDeclareBUSMessage;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.scheduler.SystemClock.TimeUnit;

public class BUSResource extends Resource
{
	private static final long serialVersionUID = 1L;
	private static int nextBUS = 1;
	private static BUSResource vBUS = null;

	private final int busNumber;
	private final ControlQueue cq;
	private final double speed;
	private final List<CPUResource> cpus;
	private final List<MessagePacket> messages;

	private ISchedulableThread busThread = null;

	public BUSResource(boolean isVirtual, SchedulingPolicy policy,
			double speed, List<CPUResource> cpus)
	{
		super(policy);

		this.busNumber = isVirtual ? 0 : nextBUS++;
		this.cq = new ControlQueue();
		this.speed = speed;
		this.cpus = cpus;
		this.messages = new LinkedList<>();

		busThread = null;

		if (isVirtual)
		{
			vBUS = this;
		}
	}

	public static void init()
	{
		MessagePacket.init();
		nextBUS = 1;
		vBUS = null;
	}

	@Override
	public void reset()
	{
		messages.clear();
		cq.reset();
		policy.reset();
	}

	@Override
	public void setName(String name)
	{
		super.setName(name);

		if (busNumber != 0)
		{
			RTLogger.log(new RTDeclareBUSMessage(busNumber, cpusToSet(), name));
		}
	}

	@Override
	public boolean reschedule()
	{
		// This is scheduling threads, as though for a CPU, but really we
		// want to schedule (ie. order) the messages on the queue for the BUS.
		// There is only one BUS thread.

		if (policy.reschedule())
		{
			busThread = policy.getThread();
			busThread.runslice(policy.getTimeslice());
			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public long getMinimumTimestep()
	{
		if (busThread == null)
		{
			return Long.MAX_VALUE; // We're not in timestep
		} else
		{
			switch (busThread.getRunState())
			{
				case TIMESTEP:
					return busThread.getTimestep();

				case RUNNING:
					return -1; // Can't timestep

				default:
					return Long.MAX_VALUE;
			}
		}
	}

	public boolean links(CPUResource from, CPUResource to)
	{
		if (from.equals(to))
		{
			return false;
		} else
		{
			return cpus.contains(from) && cpus.contains(to);
		}
	}

	public void transmit(MessageRequest request)
	{
		RTLogger.log(new RTBusRequestMessage(request));

		messages.add(request);
		cq.stim();
	}

	public void reply(MessageResponse response)
	{
		RTLogger.log(new RTBusReplyRequestMessage(response));

		messages.add(response);
		cq.stim();
	}

	public void process(ISchedulableThread th)
	{
		cq.join(null, null); // Never leaves

		while (true)
		{
			while (messages.isEmpty())
			{
				cq.block(null, null);
			}

			MessagePacket m = messages.remove(0);

			RTLogger.log(new RTBusActivateMessage(m));

			if (m instanceof MessageRequest)
			{
				MessageRequest mr = (MessageRequest) m;

				if (!mr.bus.isVirtual())
				{
					long pause = getDataDuration(mr.getSize());
					th.duration(pause, null, null);
				}

				AsyncThread thread = new AsyncThread(mr);
				thread.start();
			} else
			{
				MessageResponse mr = (MessageResponse) m;

				if (!mr.bus.isVirtual())
				{
					long pause = getDataDuration(mr.getSize());
					th.duration(pause, null, null);
				}

				mr.replyTo.set(mr);
			}

			RTLogger.log(new RTBusCompletedMessage(m));
		}
	}

	@Override
	public boolean isVirtual()
	{
		return this == vBUS;
	}

	private long getDataDuration(long bytes)
	{
		if (speed == 0)
		{
			return 0; // Infinitely fast virtual bus
		} else
		{
			// TODO optimize by converting the speed into the correct units only once
			return SystemClock.timeToInternal(TimeUnit.seconds, new Double(bytes)
					/ speed); // bytes/s
		}
	}

	private String cpusToSet()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String prefix = "";

		for (CPUResource cpu : cpus)
		{
			sb.append(prefix);
			sb.append(cpu.getNumber());
			prefix = ",";
		}

		sb.append("}");
		return sb.toString();
	}

	@Override
	public String getStatus()
	{
		return name + " queue = " + messages.size();
	}

	public int getNumber()
	{
		return busNumber;
	}
}
