/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.debug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.debug.RemoteInterpreter.Call.CallType;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueFactory;

public class RemoteInterpreter
{
	private final Interpreter interpreter;
	private final DBGPReader dbgp;
	private boolean running = false;
	private Thread vdmExecuteThread;

	public RemoteInterpreter(Interpreter interpreter, DBGPReader dbgp)
	{
		this.interpreter = interpreter;
		this.dbgp = dbgp;
	}

	public Interpreter getInterpreter()
	{
		return interpreter;
	}

	public ValueFactory getValueFactory()
	{
		return new ValueFactory();
	}

	public DBGPReader getDebugReader()
	{
		return dbgp;
	}

	public String execute(String line) throws Exception
	{
		if (isFinished)
		{
			throw new Exception("RemoteInterpreter has finished.");
		}

		executionQueueRequest.add(new Call(CallType.Execute, line));
		Object result = executionQueueResult.take();
		if (result instanceof Exception)
		{
			throw (Exception) result;
		} else
		{
			return ((Value) result).toString();
		}
	}

	public Value valueExecute(String line) throws Exception
	{
		if (isFinished)
		{
			throw new Exception("RemoteInterpreter has finished.");
		}

		executionQueueRequest.add(new Call(CallType.Execute, line));
		Object result = executionQueueResult.take();
		if (result instanceof Exception)
		{
			throw (Exception) result;
		} else
		{
			return (Value) result;
		}
	}

	public void init()
	{
		interpreter.init(dbgp);
	}

	public void create(String var, String exp) throws Exception
	{
		if (isFinished)
		{
			throw new Exception("RemoteInterpreter has finished.");
		}

		if (interpreter instanceof ClassInterpreter)
		{
			executionQueueRequest.add(new Call(CallType.Create, var, exp));
			Object result = executionQueueResult.take();
			if (result instanceof Exception)
			{
				throw (Exception) result;
			}
		} else
		{
			throw new Exception("Only available for VDM++ and VDM-RT");
		}
	}

	public String getEnvironment()
	{
		return interpreter.getInitialContext();
	}

	public Set<File> getSourceFiles()
	{
		return interpreter.getSourceFiles();
	}

	public SourceFile getSourceFile(File file) throws IOException
	{
		return interpreter.getSourceFile(file);
	}

	public List<String> getModules() throws Exception
	{
		List<String> names = new ArrayList<String>();

		if (interpreter instanceof ClassInterpreter)
		{
			throw new Exception("Only available for VDM-SL");
		} else
		{
			for (AModuleModules m : ((ModuleInterpreter) interpreter).getModules())
			{
				names.add(m.getName().getName());
			}
		}

		return names;
	}

	public List<String> getClasses() throws Exception
	{
		List<String> names = new ArrayList<String>();

		if (interpreter instanceof ClassInterpreter)
		{
			for (SClassDefinition def : ((ClassInterpreter) interpreter).getClasses())
			{
				names.add(def.getName().getName());
			}
		} else
		{
			throw new Exception("Only available for VDM++ and VDM-RT");
		}

		return names;
	}

	public void finish()
	{
		isFinished = true;
		if (vdmExecuteThread != null)
		{
			vdmExecuteThread.interrupt();
		}
	}

	ArrayBlockingQueue<Call> executionQueueRequest = new ArrayBlockingQueue<Call>(1);
	ArrayBlockingQueue<Object> executionQueueResult = new ArrayBlockingQueue<Object>(1);
	private boolean isFinished;

	public static class Call
	{
		public enum CallType
		{
			Execute, Create
		};

		public final CallType type;
		public final String exp;
		public final String var;

		public Call(CallType type, String var, String exp)
		{
			this.type = type;
			this.var = var;
			this.exp = exp;
		}

		public Call(CallType type, String exp)
		{
			this.type = type;
			this.var = null;
			this.exp = exp;
		}

		@Override
		public String toString()
		{
			return this.type + " " + this.var + " " + this.exp;
		}
	}

	public void processRemoteCalls() throws Exception
	{
		if (running)
		{
			return;
		}

		if (BasicSchedulableThread.getThread(Thread.currentThread()) == null)
		{
			throw new Exception("Process Remote Calls can only be called from a valid VDM thread");
		}

		running = true;
		vdmExecuteThread = Thread.currentThread();
		while (!isFinished)
		{
			try
			{
				Call call = executionQueueRequest.take();
				try
				{
					switch (call.type)
					{
						case Create:
							if (interpreter instanceof ClassInterpreter)
							{
								ClassInterpreter ci = (ClassInterpreter) interpreter;
								ci.create(call.var, call.exp);
								executionQueueResult.add(new Object());
							}
							break;
						case Execute:
							executionQueueResult.add(interpreter.execute(call.exp, dbgp));
							break;

					}
				} catch (Exception e)
				{
					executionQueueResult.add(e);
				}
			} catch (InterruptedException e)
			{
				return;
			}
		}
	}

}
