package org.overture.ide.debug.utils.communication;

import org.overturetool.vdmj.debug.DBGPErrorCode;
import org.overturetool.vdmj.debug.DBGPException;

public enum DBGPErrorType
{
	//000 Command parsing errors
	NO_ERROR(0,"no error"),
	PARSE_ERROR_IN_COMMAND(1,"parse error in command"),
	DUBLICATE_ARGUMENT_IN_COMMAND(2,"duplicate arguments in command"),
	INVALUD_OPTIONS(3,"invalid options (ie, missing a required option, invalid value for a passed option)"),
	UNIMPLEMENTD_COMMAND(4 ,"Unimplemented command"),
	COMMAND_NOT_AVALIABLE(5,"Command not available (Is used for async commands. For instance if the engine is in state \"run\" then only \"break\" and \"status\" are available)."),
	//100 File related errors
	CANNOT_OPEN_FILE(100,"can not open file (as a reply to a \"source\" command if the requested source file can't be opened)"),
	STREAM_REDIRECT_FAILD(101,"stream redirect failed"),
	
	//200 Breakpoint, or code flow errors
	BREAKPOINT_COULD_NOT_BE_SET(200,"breakpoint could not be set (for some reason the breakpoint could not be set due to problems registering it)"),
	BREAKPOINT_TYPE_NOT_SUPPORTED(201,"breakpoint type not supported (for example I don't support 'watch' yet and thus return this error)"),
	INVALID_BREAKPOINT(202,"invalid breakpoint (the IDE tried to set a breakpoint on a line that does not exist in the file (ie \"line 0\" or lines past the end of the file)"),
	NO_CODE_ON_BREAKPOINT_LINE(203,"no code on breakpoint line (the IDE tried to set a breakpoint on a line which does not have any executable code. The debugger engine is NOT required to return this type if it is impossible to determine if there is code on a given location. (For example, in the PHP debugger backend this will only be returned in some special cases where the current scope falls into the scope of the breakpoint to be set))."),
	INVALID_BREAKPOINT_STATE(204, "Invalid breakpoint state (using an unsupported breakpoint state was attempted)"),
	NO_SUCH_BREAKPOINT(205,"No such breakpoint (used in breakpoint_get etc. to show that there is no breakpoint with the given ID)"),
	ERROR_EVALUATING_CODE(206,"Error evaluating code"),
	INVALID_EXPRESSION(207,"Invalid expression (the expression used for a non-eval() was invalid)"),
	//300 Data errors
	CANNOT_GET_PROPERTY(300 ,"Can not get property (when the requested property to get did not exist, this is NOT used for an existing but uninitialized property, which just gets the type \"uninitialised\" (See: PreferredTypeNames))."),
	STACK_DEPTH_INVALID(301 ,"Stack depth invalid (the -d stack depth parameter did not exist (ie, there were less stack elements than the number requested) or the parameter was < 0)"),
	CONTEXT_INVALID(302,"Context invalid (an non existing context was requested)"),
	//900 Protocol errors
	ENCODING_NOT_SUPPORTED(900, "Encoding not supported"),
	INTERNAL_EXCEPTION(998,"An internal exception in the debugger occurred"),
	UNKNOWN_ERROR(999 ,"Unknown error");
	
	
	public String description;
	public Integer value;

	DBGPErrorType(Integer id,String description)
	{
		this.value = id;
		this.description = description;
	}

	public static DBGPErrorType lookup(Integer id) throws DBGPException
	{
		for (DBGPErrorType cmd: values())
		{
			if (cmd.value.equals(id))
			{
				return cmd;
			}
		}

		throw new DBGPException(DBGPErrorCode.PARSE, id.toString());
	}

	@Override
	public String toString()
	{
		return value.toString() + ": "+ description;
	}
}
