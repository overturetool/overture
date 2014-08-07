package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenClassDefinition implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8653916852479373723L;
	public String name;

	public NextGenClassDefinition(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("ClassDef -> ");
		s.append(" name: ");
		s.append(this.name);

		return s.toString();
	}

}
