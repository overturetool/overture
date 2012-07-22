package org.overture.interpreter.messages.rtlog.nextgen;

import java.io.Serializable;

public class NextGenThread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4292041684803164404L;

	public Long id;
	public NextGenObject object;
	public boolean periodic;
	
	public NextGenThread(long id, NextGenObject object, boolean periodic) 
	{
		this.id = id;
		this.object = object;
		this.periodic = periodic;
	}
		
	
	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append("Thread -> ");
		s.append("id: "); s.append(this.id);
		s.append(" periodic: "); s.append(this.periodic);
				
		return s.toString();
	}
}
