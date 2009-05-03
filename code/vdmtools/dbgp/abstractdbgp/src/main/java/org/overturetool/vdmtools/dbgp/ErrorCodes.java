package org.overturetool.vdmtools.dbgp;

public class ErrorCodes {
	
	//000 COMMAND PARSING ERRORS
	/***
	 * no error
	 */
	public final static ErrorCode NO_ERROR =  new ErrorCode(0,"no error"); 	
	
	/***
	 * parse error in command
	 */ 
	public final static ErrorCode PARSE_ERROR = new ErrorCode(1,"parse error in command");
	
	/**
	 * duplicate arguments in command
	 */
	public final static ErrorCode DUPLICATE_ARGS = new ErrorCode(2,"duplicate arguments in command");
	
	/**
	 * invalid options (ie, missing a required option, invalid value for a passed option)
	 */
	public final static ErrorCode INVALID_OPTIONS = new ErrorCode(3,"invalid options (ie, missing a required option, invalid value for a passed option)");;

	/***
	 * Unimplemented command
	 */
	public final static ErrorCode UNIMPLEMENTED_COMMAND = new ErrorCode(4,"Unimplemented command");
	/**
	 *  Command not available (Is used for async commands. For instance
	 *  if the engine is in state "run" then only "break" and "status"
	 *  are available)
	 */
	public final static ErrorCode COMMAND_NOT_AVAILABLE = new  ErrorCode(5,"Command not available (Is used for async commands. For instance if the engine is in state" 
																		+" \"run\" then only \"break\" and \"status\" are available)");
	
	
	
	//100 FILE RELATED ERRRORS
	/**
	 * 100 - can not open file (as a reply to a "source" command if the
	 * requested source file can't be opened)
	 */
	 public final static ErrorCode UNABLE_TO_OPEN_FILE  = new  ErrorCode(100, "can not open file (as a reply to a \"source\" command if the requested source file can't be opened)"); 
	
	/**
	 * 101 - stream redirect failed
	 */
	 public final static ErrorCode STREAM_REDIRECT_FAILED =  new  ErrorCode(101,"stream redirect failed"); 
	 
	  //BREAKPOINT OR FLOW ERRORS
	 
	 /**
	  *  200 - breakpoint could not be set (for some reason the breakpoint
	  * could not be set due to problems registering it)
	  */
	  public final static  ErrorCode UNABLE_TO_SET_BREAKPOINT =  new ErrorCode(200,"breakpoint could not be set (for some reason the breakpoint\r\n"
			  																	 +" could not be set due to problems registering it)");
	 
	 
	 /**
	  * 201 - breakpoint type not supported (for example I don't support
	  * 'watch' yet and thus return this error)
	  */
	  public final static ErrorCode UNSUPPORTED_BREAKPOINT_TYPE = new ErrorCode(201,"breakpoint type not supported (for example I don't support 'watch' yet and thus return this error)");
	
	 /**
	  *  202 - invalid breakpoint (the IDE tried to set a breakpoint on a
	  * line that does not exist in the file (ie "line 0" or lines
	  * past the end of the file)
	  */
	  public final static ErrorCode INVALID_BREAKPOINT_TYPE = new ErrorCode(202,"invalid breakpoint (the IDE tried to set a breakpoint on a line that does not exist " 
			  																+"in the file (ie \"line 0\" or lines past the end of the file)");
	
	  
	 /**
	  * 203 - no code on breakpoint line (the IDE tried to set a breakpoint
	  * on a line which does not have any executable code. The
	  * debugger engine is NOT required to return this type if it
 	  * is impossible to determine if there is code on a given
	  * location. (For example, in the PHP debugger backend this
 	  * will only be returned in some special cases where the current
 	  * scope falls into the scope of the breakpoint to be set)).
	  */
	  public final static ErrorCode NO_CODE_ON_BREAKPOINT_LINE = new ErrorCode(203,"no code on breakpoint line (the IDE tried to set a breakpoint"
			  																		+"on a line which does not have any executable code. The "
			  																		+"debugger engine is NOT required to return this type if it "
			  																		+"is impossible to determine if there is code on a given "
			  																		+"location. (For example, in the PHP debugger backend this "
			  																		+"will only be returned in some special cases where the current "
			  																		+"scope falls into the scope of the breakpoint to be set)).");
	 
	  /**
	   * 204 - Invalid breakpoint state (using an unsupported breakpoint state
	   * was attempted)
	   */
	  public final static ErrorCode INVALID_BREAKPINT_STATE = new ErrorCode(204,"Invalid breakpoint state (using an unsupported breakpoint state was attempted)");
	  
	  /**
	   * 205 - No such breakpoint (used in breakpoint_get etc. to show that
	   * there is no breakpoint with the given ID)
	   */
	  public final static ErrorCode NO_SUCH_BREAKPOINT = new ErrorCode(205,"No such breakpoint (used in breakpoint_get etc. to show that there is no breakpoint with the given ID)");
	  
	  
	  /**
	   * 206 - Error evaluating code (use from eval() (or perhaps
	   * property_get for a full name get))
	   */
	  public final static ErrorCode CODE_EVALUATION_ERROR = new ErrorCode(206,"Error evaluating code (use from eval() (or perhaps "
			  																+"property_get for a full name get))");		
	 
	 /**
	  * 207 - Invalid expression (the expression used for a non-eval()
	  * was invalid)
	  */
	  public final static ErrorCode INVALID_EXPRESSION = new ErrorCode(207,"Invalid expression (the expression used for a non-eval() was invalid)");
	  
	  
	  //300 DATA ERRORS
	  /**
	   * Can not get property (when the requested property to get did
	   * not exist, this is NOT used for an existing but uninitialized
	   * property, which just gets the type "uninitialised" (See:
	   * PreferredTypeNames)).
	   */
	  public final static ErrorCode UNABLE_TO_GET_PROPERTY = new ErrorCode(300,"Can not get property (when the requested property to get did "
																			   +"not exist, this is NOT used for an existing but uninitialized "
																			   +"property, which just gets the type \"uninitialised\" (See: "
																			   +"PreferredTypeNames)).");
	  
	  /**
	   * Stack depth invalid (the -d stack depth parameter did not
	   * exist (ie, there were less stack elements than the number
	   * requested) or the parameter was < 0)
	   */
	  public final static ErrorCode INVALID_STACK_DEPTH = new ErrorCode(301,"Stack depth invalid (the -d stack depth parameter did not "
																		    +"exist (ie, there were less stack elements than the number "
																		    +"requested) or the parameter was < 0)");
	  
	  /**
	   * Context invalid (an non existing context was requested)
	   */
	  public final static ErrorCode INVALID_CONTEXT = new ErrorCode(302,"ontext invalid (an non existing context was requested)");
	  
	  
	  //900 PROTOCOL ERRORS
	  /**
	   * Encoding not supported
	   */
	  public final static ErrorCode UNSUPPORTED_ENCODING = new ErrorCode(900,"Encoding not supported");
	  
	  /**
	   * An internal exception in the debugger occurred
	   */
	  public final static ErrorCode INTERNAL_DEBUGGER_EXCPETION = new ErrorCode(998,"An internal exception in the debugger occurred");
	  
	  /**
	   * 999 - Unknown error
	   */
	  public final static ErrorCode UNKNOWN_ERROR = new ErrorCode(999,"Unknown error"); 
}
