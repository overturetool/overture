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
package org.overture.vdmtools.parser;

import java.util.ArrayList;

import org.overture.vdmtools.VDMToolsError;

import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.APIError;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.ErrorListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.FileListHolder;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMApplication;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMErrors;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMParser;
import jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.VDMProject;


public class Parser {
	

	public Parser() {
	}
	
	
	public ArrayList<VDMToolsError> parseProject(VDMApplication vdmApplication, VDMProject vdmProject)
	{
		try
		{
			ArrayList<VDMToolsError> errorList = new ArrayList<VDMToolsError>();
			VDMParser parser = vdmApplication.GetParser();
			FileListHolder fl = new FileListHolder();
			int count = vdmProject.GetFiles(fl);
			System.out.println("count" + count);
			String flist[] = fl.value;
			// Parse the files in two different ways. First we traverse
			// the list of files and parses each file individually.
			// (OK, I know that for the SL_TOOLBOX there is only one
			// file configured, but it is fine for an illustration)
			System.out.println("Parsing files individually");
			for (int i = 0; i < flist.length; i++) {
				System.out.println(flist[i]);
				System.out.println("...Parsing...");
				if (parser.Parse(flist[i]))
					System.out.println("done.");
				else
					System.out.println("error.");
			}
			// And then we parse all files in one go:
			System.out.println("Parsing entire list...");
			parser.ParseList(flist);
			System.out.println("done.");
			// If errors were encountered during the parse they can now
			// be inspected:
			// The error handler
			VDMErrors errhandler = vdmApplication.GetErrorHandler();
			ErrorListHolder errs = new ErrorListHolder();
			// retrieve the sequence of errors
			int nerr = errhandler.GetErrors(errs);
			jp.co.csk.vdm.toolbox.api.corba.ToolboxAPI.Error errlist[] = errs.value;
			if (nerr > 0) {
				for (int i = 0; i < errlist.length; i++) {
					errorList.add(new VDMToolsError(errlist[i].msg,errlist[i].fname,errlist[i].line, errlist[i].col));
				}
			}
//			else if(nerr == 0)
//			{
//				//type check
//				errorList =  typeCheckProject();
//			}
			// Warnings can be queried similarly.
			// List the names and status of all modules:
//			ListModules(app);
			return errorList;
		}
		catch(APIError e)
		{
			System.out.println("Error parsing project " + e.getMessage());
			return null;
		}
	}
	

}
