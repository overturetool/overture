package org.overture.vdmtools;

import java.util.ArrayList;

import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ModuleListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProject;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMTypeChecker;

public class TypeChecker {
	
	private ArrayList<VDMToolsError> errorList = new ArrayList<VDMToolsError>();
	private ArrayList<VDMToolsWarning> warningList = new ArrayList<VDMToolsWarning>();
	
	public ArrayList<VDMToolsWarning> getWarningList() {
		return warningList;
	}

	public ArrayList<VDMToolsError> getErrorList() {
		return errorList;
	}

	/***
	 * type check project and saves the warning and errors. It is possible to retrieve 
	 * the errors and warnings afterwards 
	 * @see getErrorList() and getWarningList
	 * @param vdmApplication
	 * @param vdmProject
	 */
	public void typeCheckProject2(VDMApplication vdmApplication, VDMProject vdmProject)
	{
		try
		{
			errorList.clear();
			warningList.clear();
			
			// The error handler
			VDMErrors errhandler = vdmApplication.GetErrorHandler();
			ErrorListHolder errorListHolder = new ErrorListHolder();
			
			// Type check all modules:
			VDMTypeChecker typeChecker = vdmApplication.GetTypeChecker();
			ModuleListHolder moduleholder = new ModuleListHolder();
			vdmProject.GetModules(moduleholder);
			String modules[] = moduleholder.value;
//			System.out.println("Type checking all modules...");
			if (typeChecker.TypeCheckList(modules))
			{
//				System.out.println("done.");
			}
			else
			{
//				System.out.println("Type check errors.");
				// errors
				int nerr = errhandler.GetErrors(errorListHolder);
				jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error errlist[] = errorListHolder.value;
				if (errlist != null && nerr > 0)
				{
					for (int i = 0; i < errlist.length; i++) {
						errorList.add(new VDMToolsError(
									errlist[i].msg,
									errlist[i].fname,
									errlist[i].line, 
									errlist[i].col));
//						System.out.println(errlist[i].msg);
					}
				}
			}
			int nWarnings = errhandler.GetWarnings(errorListHolder);
			jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error warnings[] = errorListHolder.value;
			if (warnings != null && nWarnings > 0)
			{
				for (int i = 0; i < warnings.length; i++) {
					warningList.add(new VDMToolsWarning(
							warnings[i].msg,
							warnings[i].fname,
							warnings[i].line, 
							warnings[i].col));
//					System.out.println(warnings[i].msg);
				}
			}
			// List the new status of all modules:
//			ListModules(vdmApplication);
			
		}catch (APIError e) {
			System.out.println("Error when trying to type check document: " + e.getMessage());
		}
	}
	
	public ArrayList<VDMToolsError> typeCheckProject(VDMApplication vdmApplication, VDMProject vdmProject)
	{
		try
		{
			ArrayList<VDMToolsError> errorList = new ArrayList<VDMToolsError>();
			// The error handler
			VDMErrors errhandler = vdmApplication.GetErrorHandler();
			ErrorListHolder errorListHolder = new ErrorListHolder();
			
			// Type check all modules:
			VDMTypeChecker typeChecker = vdmApplication.GetTypeChecker();
			ModuleListHolder moduleholder = new ModuleListHolder();
			vdmProject.GetModules(moduleholder);
			String modules[] = moduleholder.value;
			System.out.println("Type checking all modules...");
			if (typeChecker.TypeCheckList(modules))
			{
				System.out.println("done.");
			}
			else
			{
				System.out.println("Type check errors.");
				int nerr = errhandler.GetErrors(errorListHolder);
				jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error errlist[] = errorListHolder.value;
				if (errlist != null && nerr > 0)
				{
					for (int i = 0; i < errlist.length; i++) {
						errorList.add(new VDMToolsError(
									errlist[i].msg,
									errlist[i].fname,
									errlist[i].line, 
									errlist[i].col));
					}
				}
			}
			// List the new status of all modules:
//			ListModules(vdmApplication);
			
			return errorList;
		}catch (APIError e) {
			System.out.println("Error when trying to type check document: " + e.getMessage());
			return null;
		}
	}
}
