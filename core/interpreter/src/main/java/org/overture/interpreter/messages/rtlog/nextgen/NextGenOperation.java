package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenOperation implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7326412415346555552L;

	public String name;
	public NextGenClassDefinition classDef;
	public boolean isAsync;
	public boolean isStatic;

	public NextGenOperation(String name, NextGenClassDefinition classDef,
			boolean isAsync, boolean isStatic)
	{
		this.name = name;
		this.classDef = classDef;
		this.isAsync = isAsync;
		this.isStatic = isStatic;
	}

	@Override
	public String toString()
	{
		StringBuffer s = new StringBuffer();

		s.append("Operation -> ");
		s.append("name: ");
		s.append(this.name);
		s.append(" clnm ");
		s.append(this.classDef.name);
		s.append(" isAsync: ");
		s.append(this.isAsync);
		s.append(" isStatic: ");
		s.append(this.isStatic);

		return s.toString();
	}

}
