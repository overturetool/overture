package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;
import java.util.List;

public class NextGenBus implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5885158949672715295L;

	public Integer id;
	public String name;
	public List<NextGenCpu> cpus;

	public NextGenBus(int id, String name, List<NextGenCpu> cpus)
	{
		this.id = id;
		this.name = name;
		this.cpus = cpus;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("BUSdecl -> ");
		s.append("id: ");
		s.append(id);
		s.append(" topo: ");
		s.append("{");
		for (int i = 0; i < this.cpus.size() - 1; i++)
		{
			s.append(cpus.get(i).id);
			s.append(",");
		}
		s.append(cpus.get(cpus.size() - 1).id);
		s.append("} ");
		s.append("name: \"");
		s.append(this.name);
		s.append("\"");
		s.append(" time: 0");

		return s.toString();
	}

}
