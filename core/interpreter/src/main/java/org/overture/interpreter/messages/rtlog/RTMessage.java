package org.overture.interpreter.messages.rtlog;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.SystemClock;

public abstract class RTMessage
{
	public enum MessageType
	{
		Request, Activate, Completed
	}

	static Map<String, Long> staticIds = new Hashtable<>();
	public final static List<RTDeployStaticMessage> cachedStaticDeploys = new Vector<>();

	protected Long time = SystemClock.getWallTime();// Timestamp the message

	protected synchronized Long getStaticId(String name, CPUResource cpuId)
	{
		String nameFinal = name + cpuId.getNumber();

		if (staticIds.containsKey(nameFinal))
		{
			return staticIds.get(nameFinal);
		} else
		{
			RTDeployStaticMessage deployMessage = new RTDeployStaticMessage(name, cpuId);
			cachedStaticDeploys.add(deployMessage);
			staticIds.put(nameFinal, deployMessage.getObjectReference());
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
