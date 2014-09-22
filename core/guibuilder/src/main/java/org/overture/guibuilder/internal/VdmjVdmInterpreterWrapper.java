/*
 * #%~
 * Overture GUI Builder
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
package org.overture.guibuilder.internal;

import java.io.File;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.interpreter.VDMJ;
import org.overture.interpreter.VDMPP;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.VDMSL;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.util.ExitStatus;

/**
 * This class serves as a wrapper to the VDMJ controller classes in order to execute and evaluate vdm commands and
 * expressions in the interpreter
 * 
 * @author carlos
 */
public class VdmjVdmInterpreterWrapper
{

	private VDMJ controller = null;
	private Interpreter interpreter = null;

	/**
	 * Constructor
	 * 
	 * @param dialect
	 *            The target vdm dialect for the interpreter
	 * @throws Exception
	 */
	public VdmjVdmInterpreterWrapper(Dialect dialect) throws Exception
	{

		// global VDMJ settings
		Settings.usingCmdLine = true; // FIXME: not really sure what this is for...
		Settings.usingDBGP = false;
		Settings.dialect = dialect;

		// warning only the VDMPP dialect has been given atention,
		// other dialect have not been tested, and usage will certainly
		// lead to unexpected behaviour
		if (dialect == Dialect.VDM_PP)
		{
			controller = new VDMPP();
			return;
		} else if (dialect == Dialect.VDM_RT)
		{
			controller = new VDMRT();
			return;
		} else if (dialect == Dialect.VDM_SL)
		{
			controller = new VDMSL();
			return;
		}

		throw new Exception("Unknown VDM Dialect");

	}

	/**
	 * Parses and type checks the vdm specification files
	 * 
	 * @param filenames
	 *            List of files to parse and type check
	 */
	public void parseFilesAndTypeCheck(List<File> filenames)
	{

		ExitStatus status;

		// nothing to parse
		if (filenames.isEmpty())
		{
			System.out.println("No files specified.");
		} else
		{
			status = controller.parse(filenames);

			if (status == ExitStatus.EXIT_OK)
			{
				status = controller.typeCheck();
				if (status != ExitStatus.EXIT_OK)
				{
					System.out.println("Type check failed");
				}
			}
		} // end if(filenames.isEmpty())

	}

	/**
	 * Inits the interpreter
	 * 
	 * @throws Exception
	 */
	public void initInterpreter() throws Exception
	{

		interpreter = controller.getInterpreter();
		interpreter.init(null);
	}

	/**
	 * Returns the value of global instance variable
	 * 
	 * @param line
	 *            The name of the variable
	 * @return The current value of the variable
	 * @throws Exception
	 */
	public String getValueOf(String line) throws Exception
	{
		return execute(line);
	}

	/**
	 * Executes a vdm expression
	 * 
	 * @param cmd
	 *            The vdm expression in string form
	 * @return The result of the expression in string form
	 * @throws Exception
	 */
	public String execute(String cmd) throws Exception
	{
		// lazy instantion of the interpreter
		if (interpreter == null)
		{
			try
			{
				initInterpreter();
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return interpreter.execute(cmd, null).toString();
	}

	/**
	 * Creates a global instance of a vdm object/type
	 * 
	 * @param var
	 *            - name of the instance
	 * @param expr
	 *            - constructor expression
	 * @throws Exception
	 */
	public void createInstance(String var, String expr) throws Exception
	{
		// lazy instantion of the interpreter
		if (interpreter == null)
		{
			try
			{
				initInterpreter();
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}
		((ClassInterpreter) interpreter).create(var, expr);
	}

}
