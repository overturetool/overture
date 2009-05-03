package org.overturetool.vdmtools.interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import jp.co.csk.vdm.toolbox.api.ToolboxClient;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreter;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumeric;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMTuple;

import org.overturetool.vdmtools.VDMToolsError;
import org.overturetool.vdmtools.VDMToolsProject;

public class Interpreter {
	private VDMInterpreter interpreter;
	private short client;
	private Boolean isInilized = false;
	private VDMFactory fact;
	private VDMErrors vdmerrors;
	
	private VDMApplication vdmApplication;
	public static enum DebugState {SUCCES, BREAKPOINT, INTERRUPT, ERROR};

	public void PrintErrors() {
		for (Error err : GetErrors()) {

			System.out.println("Filename: " + ToolboxClient.fromISO(err.fname) + " | " + err.line
					+ "." + err.col + " | " + ToolboxClient.fromISO(err.msg));

		}
	}

	public Error[] GetErrors() {
		ErrorListHolder errList = new ErrorListHolder();
		// errList.value= new Error[100];
		ErrorListHolder warList = new ErrorListHolder();
		// short numberOfErrors = vdmerrors.NumErr();
//		int numberOfWarnings = vdmerrors.NumWarn();
		vdmerrors.GetErrors(errList);

		vdmerrors.GetWarnings(warList);

		return errList.value;
	}

	public static String ResultToString(VDMGeneric result) {
		// if (result.IsNumeric()) {
		// VDMNumeric num = VDMNumericHelper.narrow(result);
		// return num.GetValue();
		// }else if (result.IsSequence()) {

		return (ToolboxClient.fromISO(result.ToAscii()));
		// }
	}

	public String[] EvalTraceCase(String className, String[] expressions)
			throws Exception {
		try{
		String[] results = new String[expressions.length];

		init();

		
		EvalCommand("push " + ToolboxClient.toISO(className));
		for (int i = 0; i < expressions.length; i++) {
			try {

				results[i] = ResultToString(EvalExpression(expressions[i]));

			} catch (APIError e) {
				results[i] = "";
				for (Error err : GetErrors()) {
					results[i] += ("Error, Filename: " + ToolboxClient.fromISO(err.fname) + " | "
							+ err.line + "." + err.col + " | " + ToolboxClient.fromISO(err.msg));
				}
				if(results[i]==null || (results[i]!=null && results[i].length()==0))
					results[i]="Error API, "+ToolboxClient.fromISO(e.getMessage()) + " " +ToolboxClient.fromISO(e.msg);
				break;
			}

		}
		EvalCommand("pop " + ToolboxClient.toISO(className));
		return results;
		}catch(Exception e)
		{
			VDMToolsProject.logger.logp(Level.SEVERE, "Interpreter", "EvalTraceCase", "Problem when executing test case", e);
			throw e;
		}
	}

	public Interpreter(VDMApplication vdmApplication, short client) {
		interpreter = vdmApplication.GetInterpreter();
		vdmerrors = vdmApplication.GetErrorHandler();
		this.client = client;
		this.fact = vdmApplication.GetVDMFactory();
		
		this.vdmApplication = vdmApplication;
		this.vdmApplication.PushTag(client);
	}

	public ArrayList<VDMToolsError> init() throws Exception {
		VDMToolsProject project = VDMToolsProject.getInstance();
		if (!project.isSuccessfulTypeChecked()) {
			ArrayList<VDMToolsError> errs = project.typeCheckProject();

			if (errs.size() > 0) {
				isInilized = false;
				return errs;
			} else {

				return initHelper();
			}
		} else
			return initHelper();

	}

	private ArrayList<VDMToolsError> initHelper() throws Exception {
		VDMToolsProject project = VDMToolsProject.getInstance();
		try{
		interpreter.Verbose(true);
		interpreter.Debug(true);
		interpreter.DynInvCheck(true);
		interpreter.DynPostCheck(true);
		interpreter.DynPreCheck(true);
		interpreter.DynTypeCheck(true);
		interpreter.Initialize();

		ArrayList<VDMToolsError> initErrs = project.GetErrors();
		if (initErrs.size() == 0)
			isInilized = true;

		System.out.println("Initerpeter inizialized.");

		return initErrs;
		}catch(Exception e)
		{
			VDMToolsProject.logger.logp(Level.SEVERE, "Interpreter", "initHelper", "Init faild", e);
			throw e;
		}
	}

	public VDMGeneric GetMkObject(Class t, Object value) {
		if (t == VDMSequence.class)
			return fact.MkSequence(client);
		else if (t == VDMNumeric.class)
			return fact.MkNumeric(client, (Double) value);

		return null;
	}

	
	/**
	 * Evaluates expr Result of evaluation returned as result of method. 
	 * Result will be echoed to screen if Verbose is true. 
	 * Run-time errors cause exceptions to be raised.
	 * @param expression
	 * @return
	 * @throws APIError
	 */
	public VDMGeneric EvalExpression(String expression) throws Exception {
		if (!isInilized)
			throw new Exception("Initerpeter NOT inizialized");

		return interpreter.EvalExpression(client, ToolboxClient.toISO(expression));
	}
	
	/***
	 * Evaluates the command cmd as if it was written directly to the interpreter.
	 * 
	 * // First we create the main sort object:
	 *		interpreter.EvalCmd("create o := new SortMachine()");
	 *  Next, the GoSorting method is called on this object:
	 *		g = interpreter.Apply(client, "o.GoSorting", arg_l);
	 * @param command the command that should be evaluated
	 * @throws APIError 
	 */
	public void EvalCommand(String command) throws Exception {
		if (!isInilized)
			throw new Exception("Initerpeter NOT inizialized");
		interpreter.EvalCmd(ToolboxClient.toISO(command));
	}
	

	/**
	 * 
	 * Applies the function (or operation) functionName on argument(s) vdmSeqence on behalf of
	 * client id. The result of function (or operation) call is returned as result 
	 * of method. Run-time errors cause exceptions to be raised.
	 * 
	 * One could specify (in VDM) a function,
	 * Primes, for extracting all primes from a sequence and invoke it through the
	 * Apply method of the interface:
	 * Apply(client_id, "Primes", s)
	 *	with s being the argument for the function. Apply will also return the result of
	 *	applying the function to the given arguments as a VDM value contained in an
	 *	Generic.
	 * @param functionName the name of the function
	 * @param vdmSequence 
	 * @throws APIError 
	 */
	public VDMGeneric ApplyCommand(String command, VDMSequence arg_l)
			throws Exception {
		if (!isInilized)
			throw new Exception("Initerpeter NOT inizialized");

		return interpreter.Apply(client,ToolboxClient.toISO( command), arg_l);
	}

	
	public int GetNumeric(VDMNumeric num) {
		byte[] b1 = num.GetCPPValue();
		try {
			InputStream is = new ByteArrayInputStream(b1);
//			int type = is.read();
			int c = -1;
//			int last = -1;
			String str = "";
			while (true) {
				c = is.read();
				if ((c == -1) || (c == ',')) {
//					last = c;
					break;
				}
				str += Character.toString((char) c);
			}
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	//
	// *****************************
	// ** Breakpoints
	// *****************************
	//
	/**
	 * 
	 * @param filename the filename
	 * @param line the line number
	 * @param col the column number
	 * @return the number of the breakpoint that has been set. This number can be used to delete the breakpoint again (DeleteBreakPoint).
	 * @throws APIError
	 */
	public int  SetBreakPointByPos(String filename, int line, int col) throws APIError{
		try {
			int bpID = interpreter.SetBreakPointByPos(filename, line, col);
			return bpID;
		} catch (APIError e) {
			System.out.println(e.msg);
			throw e;
		}
	}


	/**
	 * @param module the module name
	 * @param functionName the function name
	 * @return the number of the breakpoint that has been set. This number can be used to delete the breakpoint again (DeleteBreakPoint).
	 * @throws APIError
	 */
	public int SetBreakPointByName (String module, String functionName) throws APIError{
		return interpreter.SetBreakPointByName(module, functionName);
	}
	
	/**
	 * Deletes the breakpoint number
	 * @param number
	 * @throws APIError
	 */
	public void DeleteBreakPoint (int number) throws APIError{
		interpreter.DeleteBreakPoint(number);
	}
	
	
	// **********************************************************
	// ** Debug
	// **********************************************************	
	/**
	 * The method takes an expression (a string) as parameter. StartDebugging returns,
	 * when the evaluation is finished or a breakpoint has been encountered. It returns
	 * a VDMTuple, containing the evaluation state (either <BREAKPOINT>, <INTERRUPT>,
	 * <SUCCESS> or <ERROR>) and, in case of <SUCCESS>, the result of the evaluation 
	 * as a MetaIV value. The methods DebugStep, DebugStepIn, DebugSingleStep 
	 * and DebugContinue can be used to step through the specification.
	 * @param expr the expression to evaluate
	 * @throws APIError 
	 * @throws VDMError 
	 */
	public DebugState StartDebugging(String expr) throws APIError, VDMError {
		VDMTuple tuple = interpreter.StartDebugging(client, expr);
		if (tuple.ToAscii().contains("<BREAKPOINT>")){
			return DebugState.BREAKPOINT;
		}
		else if (tuple.ToAscii().contains("<INTERRUPT>")){
			return DebugState.INTERRUPT;
		}
		else if (tuple.ToAscii().contains("<SUCCESS>")){
			return DebugState.SUCCES;
		}
		else {
			System.out.println(tuple.ToAscii());
			return DebugState.ERROR;
		}
			
	}
	
	/**
	 * This method is the equivalent to the step command in the toolbox. It 
	 * executes the next statement and then breaks. It will not step into
	 * function and operation calls. It returns a VDMTuple containing the
	 * evaluation state (which can be either <BREAKPOINT>, <INTERRUPT>,
	 * <SUCCESS> or <ERROR>) and, in case of <SUCCESS> (what means, that the
	 * expression has been successfully evaluated) the result of the evaluation 
	 * as a MetaIV value.
	 * @throws APIError
	 */
	public DebugState DebugStep() throws APIError{
		VDMTuple vdmTuple = interpreter.DebugStep(client);
		if (vdmTuple.ToAscii().contains("<BREAKPOINT>")){
			return DebugState.BREAKPOINT;
		}
		else if (vdmTuple.ToAscii().contains("<INTERRUPT>")){
			return DebugState.INTERRUPT;
		}
		else if (vdmTuple.ToAscii().contains("<SUCCESS>")){
			return DebugState.SUCCES;
		}
		else {
			System.out.println(vdmTuple.ToAscii());
			return DebugState.ERROR;
		}
	}
	
	/**
	 * This method is the equivalent to the stepin command in the toolbox. It 
	 * executes the next statement and then breaks. It will also step into 
	 * function and operation calls. It returns a VDMTuple containing the 
	 * evaluation state (which can be either <BREAKPOINT>, <INTERRUPT>, 
	 * <SUCCESS> or <ERROR>) and, in case of <SUCCESS> (what means, that the 
	 * expression has been successfully evaluated) the result of the evaluation as a MetaIV value.
	 * @throws APIError
	 */
	public DebugState DebugStepIn() throws APIError{
		VDMTuple vdmTuple = interpreter.DebugStepIn(client);
		if (vdmTuple.ToAscii().contains("<BREAKPOINT>")){
			return DebugState.BREAKPOINT;
		}
		else if (vdmTuple.ToAscii().contains("<INTERRUPT>")){
			return DebugState.INTERRUPT;
		}
		else if (vdmTuple.ToAscii().contains("<SUCCESS>")){
			return DebugState.SUCCES;
		}
		else {
			System.out.println(vdmTuple.ToAscii());
			return DebugState.ERROR;
		}
	}
	
	/**
	 * This method is the equivalent to the
	 * singlestep command in the
	 * toolbox. It executes the next
	 * expression or statement and then
	 * breaks. It returns a VDMTuple
	 * containing the evaluation state
	 * (which can be either <BREAKPOINT>,
	 * <INTERRUPT>, <SUCCESS> or
	 * <ERROR>) and, in case of <SUCCESS>
	 * (what means, that the expression
	 * has been successfully evaluated) the
	 * result of the evaluation as a MetaIV
	 * value
	 * @throws APIError
	 */
	public DebugState DebugSingleStep() throws APIError{
		VDMTuple vdmTuple = interpreter.DebugSingleStep(client);
		if (vdmTuple.ToAscii().contains("<BREAKPOINT>")){
			return DebugState.BREAKPOINT;
		}
		else if (vdmTuple.ToAscii().contains("<INTERRUPT>")){
			return DebugState.INTERRUPT;
		}
		else if (vdmTuple.ToAscii().contains("<SUCCESS>")){
			return DebugState.SUCCES;
		}
		else {
			System.out.println(vdmTuple.ToAscii());
			return DebugState.ERROR;
		}
		
	}
	
	/**
	 * This method is the equivalent to the cont command in the toolbox. It
	 * continues the execution after a breakpoint has been encountered. It
	 * returns a VDMTuple containing the evaluation state (which can be
	 * either <BREAKPOINT>, <INTERRUPT>, <SUCCESS> or <ERROR>) and, in case
	 * of <SUCCESS> (what means, that the expression has been successfully
	 * evaluated) the result of the evaluation as a MetaIV value. 
	 * @throws APIError
	 */
	public DebugState DebugContinue() throws APIError{
		VDMTuple vdmTuple = interpreter.DebugContinue(client);
		if (vdmTuple.ToAscii().contains("<BREAKPOINT>")){
			return DebugState.BREAKPOINT;
		}
		else if (vdmTuple.ToAscii().contains("<INTERRUPT>")){
			return DebugState.INTERRUPT;
		}
		else if (vdmTuple.ToAscii().contains("<SUCCESS>")){
			return DebugState.SUCCES;
		}
		else {
			System.out.println(vdmTuple.ToAscii());
			return DebugState.ERROR;
		}
	}
		
}
