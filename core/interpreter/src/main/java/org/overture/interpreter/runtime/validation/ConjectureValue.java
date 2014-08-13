/***************************************************************************
 *
 *	Copyright (c) 2009 IHA
 *
 *	Author: Kenneth Lausdahl and Augusto Ribeiro
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
 **************************************************************************/

package org.overture.interpreter.runtime.validation;

import org.overture.interpreter.messages.Console;

public class ConjectureValue
{

	private ConjectureDefinition def;
	public long triggerTime;
	public boolean validated;

	// Responsible threads/objects
	public long triggerThreadId;
	public int triggerObjectId;

	public long endThreadId;
	public int endObjectId;

	public long endTime;
	private boolean isEnded = false;

	public ConjectureValue(ConjectureDefinition def, long triggerTime,
			long triggerThreadId, int triggerObjectId)
	{
		this.def = def;
		this.triggerTime = triggerTime;
		this.validated = def.startupValue;
		this.triggerThreadId = triggerThreadId;
		this.triggerObjectId = triggerObjectId;
		this.endTime = 0;
	}

	public void setEnd(long endTime, long threadId, int objectReference)
	{
		this.isEnded = true;
		this.endTime = endTime;
		this.endThreadId = threadId;
		this.endObjectId = objectReference;
		this.validated = this.def.validate(triggerTime, endTime);
		printValidation();
	}

	private void printValidation()
	{
		Console.out.println("----------------------------------------------------------------------------------");
		Console.out.print("Conjecture: ");
		Console.out.println(def.toString());
		Console.out.println("Validated: " + this.validated);
		Console.out.println("Trigger  - time: " + triggerTime + " thread: "
				+ triggerThreadId);
		Console.out.println("Ending   - time: " + endTime + " thread: "
				+ endThreadId);
		Console.out.println("----------------------------------------------------------------------------------");

	}

	public boolean isValidated()
	{
		return this.validated;
	}

	public boolean isEnded()
	{
		return this.isEnded;
	}

	public long getEndTime()
	{
		return endTime;
	}

}
