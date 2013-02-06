package org.overture.ide.plugins.showtraceNextGen.data;

public class Conjecture
{
	public enum ConjectureType {SOURCE, DESTINATION}
	
	private ConjectureType type;
	private Long time;
    private Long threadID;
    private String name;

    public Conjecture(Long time, Long threadID, String name, ConjectureType type)
    {
    	this.type = type;
    	this.time = time;
    	this.threadID = threadID;
    	this.name = name;
    }

	public Long getTime()
	{
		return time;
	}

	public Long getThreadID()
	{
		return threadID;
	}

	public String getName()
	{
		return name;
	}
	
    public ConjectureType getType()
    {
		return type;
	}
}
