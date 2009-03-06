package org.overturetool.vdmtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.overturetool.vdmtools.interpreter.Interpreter;
import org.overturetool.vdmtools.parser.Parser;

import jp.co.csk.vdm.toolbox.api.ToolboxClient;
import jp.co.csk.vdm.toolbox.api.ToolboxClient.CouldNotResolveObjectException;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ToolType;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProject;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMError;
import jp.co.csk.vdm.toolbox.api.corba.VDM.VDMFactory;

public class VDMToolsProject {
private static boolean VDMToolsProcessStarted=false;
	private Process processToolbox;
	private Parser parser;
	private TypeChecker typeChecker;
	private short client;
	private VDMApplication app;
	private VDMProject prj;
	private boolean isSuccessfulPased;
	private boolean isSuccessfulTypeChecked;
	private VDMErrors vdmErrorListHandle;

	public boolean isSuccessfulPased() {
		return isSuccessfulPased;
	}

	public boolean isSuccessfulTypeChecked() {
		return isSuccessfulTypeChecked;
	}

	public ArrayList<VDMToolsError> GetErrors() {
		ErrorListHolder err = new ErrorListHolder();
		vdmErrorListHandle.GetErrors(err);
		// convert the err to a ArrayList....
		return new ArrayList<VDMToolsError>();// this is wrong and must be
		// corrected
	}

	private static volatile VDMToolsProject INSTANCE;

	public static VDMToolsProject getInstance() {
		if (INSTANCE == null) {
			synchronized (VDMToolsProject.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = new VDMToolsProject();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return INSTANCE;
	}

	private VDMToolsProject() {

	}

	public Interpreter GetInterpreter() {
		return new Interpreter(app, client);
	}

	public void init(String pathToVdmTools, ToolType toolType)
			throws IOException, CouldNotResolveObjectException {
		startVDMTools(pathToVdmTools);
		int retries=10;
		for (int i = 0; i < retries; i++) {
			createOrb(toolType);
			if( app!=null)
				break;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				
			}
		}
		
		parser = new Parser();
		typeChecker = new TypeChecker();
		vdmErrorListHandle = app.GetErrorHandler();
		isSuccessfulPased = false;
		isSuccessfulTypeChecked = false;
	}

	public void addFilesToProject(File[] files) {
		ArrayList<String> fPath = new ArrayList<String>();
		for (File f : files) {
			fPath.add(f.getAbsolutePath());
		}
		addFilesToProject(fPath);
	}

	public void addFilesToProject(ArrayList<String> files) {
		try {
			if (prj == null) {
				prj = app.GetProject();
				prj.New();
			}

			// Configure the project to contain the necessary files.
			// The files must be located in the same directory as where
			// the VDM Toolbox was started. Otherwise the absolute path
			// to the files should be used

			for (String string : files) {
				prj.AddFile(string);
			}
		} catch (APIError e) {
			System.out
					.println("Error setting up project.... " + e.getMessage());
		}
	}

	public void Unload() throws APIError {
		app.DestroyTag(client);
		app.Unregister(client);
	}

	public ArrayList<VDMToolsError> parseProject() {
		ArrayList<VDMToolsError> errorList = parser.parseProject(app, prj);
		if (errorList == null)
			errorList = new ArrayList<VDMToolsError>();
		isSuccessfulPased = errorList.size() == 0 ? true : false;

		VDMErrors errhandler = app.GetErrorHandler();
		ErrorListHolder errs = new ErrorListHolder();
		// retrieve the sequence of errors
		int nerr = errhandler.GetErrors(errs);
		jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error errlist[] = errs.value;
		if (nerr > 0) {
			// Print the errors:
			System.out.println("errors: ");
			for (int i = 0; i < errlist.length; i++) {
				System.out.println(errlist[i].fname);
				System.out.println(errlist[i].line);
				System.out.println(errlist[i].msg);
			}
		}
		return errorList;
	}

	public ArrayList<VDMToolsError> typeCheckProject() throws Exception {
		if (!isSuccessfulPased) {

			ArrayList<VDMToolsError> errorList = parseProject();
			if (errorList == null)
				errorList = new ArrayList<VDMToolsError>();

			if (errorList.size() > 0)
				return errorList;
		}

		ArrayList<VDMToolsError> errorList = typeChecker.typeCheckProject(app,
				prj);
		isSuccessfulTypeChecked = errorList.size() == 0 ? true : false;
		return errorList;
	}

	private void startVDMTools(String pathToVDMTools) throws IOException { // TODO
		if(VDMToolsProcessStarted)
			return;
		
		
		String s = null;

		System.out.println("Launching VDM Tools");
		// run the Unix "ps -ef" command
		processToolbox = Runtime.getRuntime().exec(pathToVDMTools);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				processToolbox.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				processToolbox.getErrorStream()));

		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		// read the output from the command

		if (stdInput.ready()) {
			System.out.println("Standard output of VDMTools command:\n");
			s = stdInput.readLine();
			System.out.println(s);
		}

		// read any errors from the attempted command

		if (stdError.ready()) {
			System.out.println("Error of VDMTools command:\n");
			s = stdError.readLine();
			System.out.println(s);
		}
		System.out.println("Launching VDM Tools - SUCCESSFUL Completed");
		VDMToolsProcessStarted = true;
	}

	/***
	 * Create ORB
	 * 
	 * @param type
	 *            the Type of the toolbox VDM++ VDM-SL
	 * @throws CouldNotResolveObjectException
	 */
	private void createOrb(ToolType type) throws CouldNotResolveObjectException {
		String[] args = {};
		System.out.println("Connect to CORBA server in VDMTools");
		app = (new ToolboxClient()).getVDMApplication(args, type);
		System.out
				.println("Connected to CORBA server in VDMTools server - SUCCESSFUL");

		System.out.println("Registre this CORBA client in VDMTools server");
		// Register the client in the Toolbox:
		client = app.Register();
		System.out.println("CORBA Client SUCCESSFUL registered as client: "
				+ client);
	}

}
