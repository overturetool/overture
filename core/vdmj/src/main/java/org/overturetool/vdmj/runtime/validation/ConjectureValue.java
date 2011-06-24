package org.overturetool.vdmj.runtime.validation;


public class ConjectureValue {

	private ConjectureDefinition def;
	public long triggerTime;
	public boolean validated;
	
	//Responsible threads/objects
	public long triggerThreadId;
	public int triggerObjectId;
	
	public long endThreadId;
	public int endObjectId;
	
	
	public long endTime;
	private boolean isEnded = false;
	
	public ConjectureValue(ConjectureDefinition def, long triggerTime, long triggerThreadId, int triggerObjectId) 
	{
		this.def = def;
		this.triggerTime = triggerTime;
		this.validated = def.startupValue;
		this.triggerThreadId = triggerThreadId;
		this.triggerObjectId = triggerObjectId;
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
		System.out.println("-----------------------------------------");
		System.out.print("Conjecture: ");
		System.out.println(def.toString());
		System.out.println("Validated? " + this.validated);
		System.out.println("Trigger - time: " + triggerTime + " thread: " + triggerThreadId); 
		System.out.println("Ending  - time: " + endTime + " thread: " + endThreadId);
		System.out.println("-----------------------------------------");
				
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
