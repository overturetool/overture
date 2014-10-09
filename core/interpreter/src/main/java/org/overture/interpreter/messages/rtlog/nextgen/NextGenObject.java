package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenObject implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3339217752089264209L;

	public Integer id;
	public NextGenClassDefinition classDef;
	public NextGenCpu cpu;

	public NextGenObject(int id, NextGenClassDefinition classDef, NextGenCpu cpu)
	{

		this.id = id;
		this.classDef = classDef;
		this.cpu = cpu;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();
		s.append("DeployObj -> ");
		s.append("objref: ");
		s.append(this.id);
		s.append(" clnm: \"");
		s.append(classDef.name);
		s.append("\"");
		s.append(" cpunm: ");
		s.append(cpu.id);
		s.append(" time: 0");

		return s.toString();
	}

}
