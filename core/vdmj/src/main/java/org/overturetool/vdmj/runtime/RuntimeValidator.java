package org.overturetool.vdmj.runtime;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;
import org.overturetool.vdmj.values.OperationValue;

public class RuntimeValidator
{

	public static void init(ClassInterpreter classInterpreter)
	{
		if(Settings.timingInvChecks)
		{
			//TODO: Add init call to validator
		}
	}
	
	public static void bindSystemVariables(SystemDefinition systemDefinition)
	{
		if(Settings.timingInvChecks)
		{
			//TODO: add binding to system variables so runtime changes will be detected.
		}
	}

	public static void validate(OperationValue operationValue, MessageType type)
	{
		if(Settings.timingInvChecks)
		{
			//TODO: Add call to validator
		}
	}
}
