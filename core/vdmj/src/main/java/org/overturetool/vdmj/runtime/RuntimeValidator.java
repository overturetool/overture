package org.overturetool.vdmj.runtime;

import java.io.File;
import java.io.PrintWriter;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.messages.rtlog.RTMessage.MessageType;
import org.overturetool.vdmj.runtime.validation.BasicRuntimeValidator;
import org.overturetool.vdmj.runtime.validation.IRuntimeValidatior;
import org.overturetool.vdmj.values.OperationValue;

public class RuntimeValidator
{

	static IRuntimeValidatior validator;
	private static PrintWriter logfile = null;
	
	public static void init(ClassInterpreter classInterpreter)
	{
		if(Settings.timingInvChecks)
		{
			validator = new BasicRuntimeValidator();
			validator.init(classInterpreter);
		}
	}
	
	public static void bindSystemVariables(SystemDefinition systemDefinition)
	{
		if(Settings.timingInvChecks)
		{
			if(validator != null)
			{
				validator.bindSystemVariables(systemDefinition);	
			}
			
		}
	}

	public static void validate(OperationValue operationValue, MessageType type)
	{
		if(Settings.timingInvChecks)
		{
			if(validator != null)
			{
				validator.validate(operationValue, type);
			}
		}
	}

	public static void stop()
	{
		if(Settings.timingInvChecks)
		{
			//TODO
		}
	}

	public static void setLogFile(PrintWriter out)
	{
		logfile = out;
	}
}
