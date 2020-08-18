/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.model.internal.operations;

import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.internal.VdmThread;

public class DbgpTerminateOperation extends DbgpOperation
{
	private static final String JOB_NAME = "Terminate Operation";
	private IVdmThread thread;

	public DbgpTerminateOperation(IVdmThread thread, IResultHandler finish)
	{
		super(thread, JOB_NAME, finish);
		this.thread = thread;	
	}

	protected void process() throws DbgpException
	{
		// Call coverage before send the quit command to the interpreter
		final Boolean success = thread.handleCoverage();
		if(success == false) 
			VdmDebugPlugin.logError("Coverage writing did not succeeed!");
		
		callFinish(getCore().stop());
	}
}
