/*
 * #%~
 * VDM Tools CORBA wrapper
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
