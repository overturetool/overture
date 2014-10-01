package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenCpu implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2520013697502583700L;

	public Integer id;
	public String name;
	public String systemClassName;
	public boolean expl;

	public NextGenCpu(int id, boolean expl, String name, String systemClassName)
	{
		this.id = id;
		this.name = name;
		this.systemClassName = systemClassName;
		this.expl = expl;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("CPUdecl -> ");
		s.append("id: ");
		s.append(this.id);
		s.append(" expl: ");
		s.append(this.expl);
		s.append(" sys: \"");
		s.append(this.systemClassName);
		s.append("\"");
		s.append(" name: \"");
		s.append(this.name);
		s.append("\"");
		s.append(" time: 0");

		return s.toString();
	}

}
