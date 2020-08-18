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
package org.overture.ide.debug.core.model.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpProperty;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public class VdmConsoleInputListener
{
	final IVdmStreamProxy proxy;
	final VdmDebugTarget target;
	Thread thread;

	public VdmConsoleInputListener(VdmDebugTarget vdmDebugTarget,
			IVdmStreamProxy proxy)
	{
		this.proxy = proxy;
		this.target = vdmDebugTarget;
	}

	public void start()
	{
		thread = new Thread(new Runnable()
		{

			public void run()
			{
				InputStream is = proxy.getStdin();
				BufferedReader reader = null;
				try
				{
					reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						try
						{
							if (target.getSessions().length > 0)
							{
								IDbgpSession session = target.getSessions()[0];
								
								// Print coverage before send the quit command to the interpreter 
								if( line.equals("q") || line.equals("quit") ) {
									target.handleCustomTerminationCommands(session);
								}
								
								final IDbgpProperty property = session.getExtendedCommands().execute(line);
								if(property != null) {
									final String result = property.getValue();
									proxy.writeStdout(result);
									
									// No need to print prompt
									if( line.equals("q") || line.equals("quit") ) 
										return;
				
									proxy.printPrompt();
								}else { 
									VdmDebugPlugin.logWarning("Interpreter response did not contain a value!");									
								}
							}
						} catch (DbgpException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (UnsupportedEncodingException e)
				{
					//
				} catch (IOException e)
				{
					// dont't care
				}

			}
		});
		thread.setName("DBGP Console reader: " + target.getName());
		thread.setDaemon(true);
		thread.start();
	}

	public void stop()
	{
		if (thread != null)
		{
			thread.interrupt();
		}
	}
}
