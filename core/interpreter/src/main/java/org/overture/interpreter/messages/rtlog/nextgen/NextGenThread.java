package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenThread implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4292041684803164404L;

	public enum ThreadType
	{
		INIT, MAIN, OBJECT
	}

	public Long id;
	public NextGenObject object;
	public NextGenCpu cpu;
	public boolean periodic;
	public ThreadType type;

	public NextGenThread(long id, NextGenCpu cpu, NextGenObject object,
			boolean periodic, ThreadType type)
	{
		this.id = id;
		this.cpu = cpu;
		this.object = object;
		this.periodic = periodic;
		this.type = type;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("Thread -> ");
		s.append("id: ");
		s.append(this.id);
		s.append(" periodic: ");
		s.append(this.periodic);
		s.append(" cpuid: ");
		s.append(this.cpu.id);
		s.append(" type: ");
		s.append(this.type);

		return s.toString();
	}
}
