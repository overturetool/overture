package org.overturetool.vdmtools.interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;

import org.overturetool.vdmtools.VDMToolsError;
import org.overturetool.vdmtools.VDMToolsProject;

import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolType;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMInterpreter;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGeneric;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMGenericHolder;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumeric;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMNumericHelper;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequence;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMSequenceHelper;
import jp.co.csk.vdm.toolbox.api.ToolboxClient;

public class Interpreter {
	private VDMInterpreter interpreter;
	private short client;
	private Boolean isInilized = false;
	private VDMFactory fact;
	private VDMErrors vdmerrors;

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
		int numberOfWarnings = vdmerrors.NumWarn();
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

	public VDMGeneric EvalExpression(String expression) throws Exception {
		if (!isInilized)
			throw new Exception("Initerpeter NOT inizialized");

		return interpreter.EvalExpression(client, expression);

	}

	// used for eg. create o := new SortMachine()
	public void EvalCommand(String command) throws Exception {
		if (!isInilized)
			throw new Exception("Initerpeter NOT inizialized");
		interpreter.EvalCmd(ToolboxClient.toISO(command));
	}

	// used as "o.GoSorting", arg_l
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
			int type = is.read();
			int c = -1;
			int last = -1;
			String str = "";
			while (true) {
				c = is.read();
				if ((c == -1) || (c == ',')) {
					last = c;
					break;
				}
				str += Character.toString((char) c);
			}
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}
}
