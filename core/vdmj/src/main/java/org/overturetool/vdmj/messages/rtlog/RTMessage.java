package org.overturetool.vdmj.messages.rtlog;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.scheduler.SystemClock;

public abstract class RTMessage
{
	public enum MessageType
	{
		Request, Activate, Completed
	}

	static Map<String, Long> staticIds = new Hashtable<String, Long>();
	public final static List<RTDeployStaticMessage> cachedStaticDeploys = new Vector<RTDeployStaticMessage>();

	protected Long time = SystemClock.getWallTime();// Timestamp the message

	protected synchronized Long getStaticId(String name)
	{
		if (staticIds.containsKey(name))
		{
			return staticIds.get(name);
		} else
		{
			RTDeployStaticMessage deployMessage = new RTDeployStaticMessage(name);
			cachedStaticDeploys.add(deployMessage);
			staticIds.put(name, deployMessage.getObjectReference());
			return deployMessage.getObjectReference();
		}

	}

	protected String getTimeField()
	{
		return " time: " + time;
	}

	public String getMessage()
	{
		return getInnerMessage() + getTimeField();
	}

	abstract String getInnerMessage();

	/**
	 * Sub classes which intend to generate static deploy messages must override this. After this has been called the
	 * generated message must be avaliable in the static list of static deploy messages
	 */
	public void generateStaticDeploys()
	{

	}

	@Override
	public String toString()
	{
		return getMessage();
	}
	
	public Long getLogTime()
	{
		return this.time;
	}
}
