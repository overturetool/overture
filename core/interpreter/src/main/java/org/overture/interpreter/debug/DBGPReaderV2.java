/*******************************************************************************
 *
 *	Copyright (c) 2010 Overture.
 *
 *	Author: Kenneth Lausdahl
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AHistoryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.VDMJ;
import org.overture.interpreter.VDMPP;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.VDMSL;
import org.overture.interpreter.debug.DBGPExecProcesser.DBGPExecResult;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTTextLogger;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overture.interpreter.runtime.ClassContext;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.util.ExitStatus;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.FieldValue;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.ReferenceValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TokenValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.util.Base64;

/**
 * Extended DBGPReader adding support for:
 * <ul>
 * <li>In-depth variable inspection.
 * <li>Overture command for writing covtbl files after execution.
 * <li>Overture response. Used to check status after issuing an xcmd Overture
 * </ul>
 * 
 * @author kela
 */
public class DBGPReaderV2 extends DBGPReader implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final int SHORT_STRING_MAX = 200;

	/**
	 * Map containing Values accessible from the client by a key. Should be emptied at resume
	 */
	private Map<Integer, Value> debugValueMap = new Hashtable<Integer, Value>();
	/**
	 * Debug key counter for the client
	 */
	private Integer debugValueKeyCounter = 0;
	/**
	 * Indicating if the debugger is running in a trace mode
	 */
	private static Boolean traceExpression = false;

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		Settings.usingDBGP = true;
		Settings.baseDir = new File(".").getParentFile();

		String host = null;
		int port = -1;
		String ideKey = null;
		Settings.dialect = null;
		String expression = null;
		List<File> files = new Vector<File>();
		List<String> largs = Arrays.asList(args);
		VDMJ controller = null;
		boolean warnings = true;
		boolean quiet = false;
		String logfile = null;
		String logTimeInvfile = null;
		boolean expBase64 = false;
		File coverage = null;
		String defaultName = null;
		String remoteName = null;
		Class<RemoteControl> remoteClass = null;

		Properties.init(); // Read properties file, if any

		Properties.parser_tabstop = 1;// required to match locations with the editor representation

		for (Iterator<String> i = largs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if (arg.equals("-vdmsl"))
			{
				controller = new VDMSL();
			} else if (arg.equals("-vdmpp"))
			{
				controller = new VDMPP();
			} else if (arg.equals("-vdmrt"))
			{
				controller = new VDMRT();
			} else if (arg.equals("-h"))
			{
				if (i.hasNext())
				{
					host = i.next();
				} else
				{
					usage("-h option requires a hostname");
				}
			} else if (arg.equals("-p"))
			{
				try
				{
					port = Integer.parseInt(i.next());
				} catch (Exception e)
				{
					usage("-p option requires a port");
				}
			} else if (arg.equals("-k"))
			{
				if (i.hasNext())
				{
					ideKey = i.next();
				} else
				{
					usage("-k option requires a key");
				}
			} else if (arg.equals("-e"))
			{
				if (i.hasNext())
				{
					expression = i.next();
				} else
				{
					usage("-e option requires an expression");
				}
			} else if (arg.equals("-e64"))
			{
				if (i.hasNext())
				{
					expression = i.next();
					expBase64 = true;
				} else
				{
					usage("-e64 option requires an expression");
				}
			} else if (arg.equals("-c"))
			{
				if (i.hasNext())
				{
					if (controller == null)
					{
						usage("-c must come after <-vdmpp|-vdmsl|-vdmrt>");
					}

					controller.setCharset(validateCharset(i.next()));
				} else
				{
					usage("-c option requires a charset name");
				}
			} else if (arg.equals("-r"))
			{
				if (i.hasNext())
				{
					Settings.release = Release.lookup(i.next());

					if (Settings.release == null)
					{
						usage("-r option must be " + Release.list());
					}
				} else
				{
					usage("-r option requires a VDM release");
				}
			} else if (arg.equals("-pre"))
			{
				Settings.prechecks = false;
			} else if (arg.equals("-post"))
			{
				Settings.postchecks = false;
			} else if (arg.equals("-inv"))
			{
				Settings.invchecks = false;
			} else if (arg.equals("-dtc"))
			{
				// NB. Turn off both when no DTC
				Settings.invchecks = false;
				Settings.dynamictypechecks = false;
			} else if (arg.equals("-measures"))
			{
				Settings.measureChecks = false;
			} else if (arg.equals("-log"))
			{
				if (i.hasNext())
				{
					try
					{
						logfile = new URI(i.next()).getPath();
					} catch (URISyntaxException e)
					{
						usage(e.getMessage() + ": " + arg);
					} catch (IllegalArgumentException e)
					{
						usage(e.getMessage() + ": " + arg);
					}
				} else
				{
					usage("-log option requires a filename");
				}
			} else if (arg.equals("-timeinv"))
			{
				if (i.hasNext())
				{
					try
					{
						logTimeInvfile = new URI(i.next()).getPath();
					} catch (URISyntaxException e)
					{
						usage(e.getMessage() + ": " + arg);
					} catch (IllegalArgumentException e)
					{
						usage(e.getMessage() + ": " + arg);
					}
				} else
				{
					usage("-timeinv option requires a filename");
				}
			} else if (arg.equals("-w"))
			{
				warnings = false;
			} else if (arg.equals("-q"))
			{
				quiet = true;
			} else if (arg.equals("-coverage"))
			{
				if (i.hasNext())
				{
					try
					{
						coverage = new File(new URI(i.next()));

						if (!coverage.isDirectory())
						{
							usage("Coverage location is not a directory");
						}
					} catch (URISyntaxException e)
					{
						usage(e.getMessage() + ": " + arg);
					} catch (IllegalArgumentException e)
					{
						usage(e.getMessage() + ": " + arg);
					}
				} else
				{
					usage("-coverage option requires a directory name");
				}
			} else if (arg.equals("-default64"))
			{
				if (i.hasNext())
				{
					defaultName = i.next();
				} else
				{
					usage("-default64 option requires a name");
				}
			} else if (arg.equals("-remote"))
			{
				if (i.hasNext())
				{
					remoteName = i.next();
				} else
				{
					usage("-remote option requires a Java classname");
				}
			} else if (arg.equals("-t"))
			{
				traceExpression = true;
			} else if (arg.equals("-consoleName"))
			{
				if (i.hasNext())
				{
					LexTokenReader.consoleFileName = i.next();
				} else
				{
					usage("-consoleName option requires a console name");
				}
			} else if (arg.equals("-baseDir"))
			{
				if (i.hasNext())
				{
					try
					{
						Settings.baseDir = new File(new URI(i.next()));
					} catch (URISyntaxException e)
					{
						usage(e.getMessage() + ": " + arg);
					} catch (IllegalArgumentException e)
					{
						usage(e.getMessage() + ": " + arg);
					}
				} else
				{
					usage("-baseDir option requires a folder name");
				}
			} else if (arg.startsWith("-"))
			{
				usage("Unknown option " + arg);
			} else
			{
				try
				{
					File dir = new File(new URI(arg));

					if (dir.isDirectory())
					{
						for (File file : dir.listFiles(Settings.dialect.getFilter()))
						{
							if (file.isFile())
							{
								files.add(file);
							}
						}
					} else
					{
						files.add(dir);
					}
				} catch (URISyntaxException e)
				{
					usage(e.getMessage() + ": " + arg);
				} catch (IllegalArgumentException e)
				{
					usage(e.getMessage() + ": " + arg);
				}
			}
		}

		if (host == null)
		{
			usage("Missing mandatory host URL");
		}

		if (port == -1)
		{
			usage("Missing mandatory port number");
		}

		if (ideKey == null)
		{
			usage("Missing mandatory IDE key");
		}

		if (expression == null)
		{
			usage("Missing mandatory expression");
		}

		if (Settings.dialect == null)
		{
			usage("Missing mandatory dialect");
		}

		if (files.isEmpty())
		{
			usage("Missing mandatory specification files");
		}

		if (host == null || port == -1 || controller == null || ideKey == null
				|| expression == null || Settings.dialect == null
				|| files.isEmpty())
		{
			usage("Missing mandatory arguments");
		}

		if (Settings.dialect != Dialect.VDM_RT && logfile != null)
		{
			usage("-log can only be used with -vdmrt");
		}
		if (Settings.dialect != Dialect.VDM_RT && logTimeInvfile != null)
		{
			usage("-timeinv can only be used with -vdmrt");
		}

		if (expBase64)
		{
			try
			{
				byte[] bytes = Base64.decode(expression);
				expression = new String(bytes, VDMJ.filecharset);
			} catch (Exception e)
			{
				usage("Malformed -e64 base64 expression");
			}
		}

		if (defaultName != null)
		{
			try
			{
				byte[] bytes = Base64.decode(defaultName);
				defaultName = new String(bytes, VDMJ.filecharset);
			} catch (Exception e)
			{
				usage("Malformed -default64 base64 name");
			}
		}

		if (remoteName != null)
		{
			try
			{
				Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(remoteName);
				remoteClass = (Class<RemoteControl>) cls;
			} catch (ClassNotFoundException e)
			{
				usage("Cannot locate " + remoteName + " on the CLASSPATH");
			}
		}

		controller.setWarnings(warnings);
		controller.setQuiet(quiet);

		if (controller.parse(files) == ExitStatus.EXIT_OK)
		{
			if (controller.typeCheck() == ExitStatus.EXIT_OK)
			{
				try
				{
					if (logfile != null)
					{
						RTLogger.setLogfile(RTTextLogger.class, new File(logfile));
						RTLogger.setLogfile(NextGenRTLogger.class, new File(logfile));
					}

					if (logTimeInvfile != null)
					{
						Settings.timingInvChecks = true;
						PrintWriter p = new PrintWriter(new FileOutputStream(logTimeInvfile, false));
						RuntimeValidator.setLogFile(p);
					}

					Interpreter i = controller.getInterpreter();

					if (defaultName != null)
					{
						i.setDefaultName(defaultName);
					}

					RemoteControl remote = remoteClass == null ? null
							: remoteClass.newInstance();

					new DBGPReaderV2(host, port, ideKey, i, expression, null).startup(remote);

					if (coverage != null)
					{
						writeCoverage(i, coverage);
					}

					RTLogger.dump(true);
					System.exit(0);
				} catch (ContextException e)
				{
					System.err.println("Initialization: " + e);
					
					if (e.isStackOverflow())
					{
						e.ctxt.printStackFrames(Console.out);
					}
					else
					{
						e.ctxt.printStackTrace(Console.out, true);
					}
					
					RTLogger.dump(true);
					System.exit(3);
				} catch (Exception e)
				{
					System.err.println("Initialization: " + e);
					e.printStackTrace();
					RTLogger.dump(true);
					System.exit(3);
				}
			} else
			{
				System.exit(2);
			}
		} else
		{
			System.exit(1);
		}
	}

	public DBGPReaderV2(String host, int port, String ideKey,
			Interpreter interpreter, String expression, CPUValue cpu)
	{
		super(host, port, ideKey, interpreter, expression, cpu);
	}

	/**
	 * Overrides to use DBGPReaderV2 debug reader
	 */
	@Override
	public DBGPReaderV2 newThread(CPUValue _cpu)
	{
		DBGPReaderV2 r = new DBGPReaderV2(host, port, ideKey, interpreter, null, _cpu);
		r.command = DBGPCommandType.UNKNOWN;
		r.transaction = "?";
		return r;
	}

	@Override
	protected boolean process(String line)
	{
		boolean carryOn = true;

		try
		{
			command = DBGPCommandType.UNKNOWN;
			transaction = "?";

			String[] parts = line.split("\\s+");
			DBGPCommand c = parse(parts);

			switch (c.type)
			{
				case STATUS:
					processStatus(c);
					break;

				case FEATURE_GET:
					processFeatureGet(c);
					break;

				case FEATURE_SET:
					processFeatureSet(c);
					break;

				case RUN:
					carryOn = processRun(c);
					break;

				case EVAL:
					carryOn = processEval(c);
					break;

				case EXPR:
					carryOn = processExpr(c);
					break;

				case EXEC:
					carryOn = processExec(c);
					break;

				case STEP_INTO:
					processStepInto(c);
					carryOn = false;
					break;

				case STEP_OVER:
					processStepOver(c);
					carryOn = false;
					break;

				case STEP_OUT:
					processStepOut(c);
					carryOn = false;
					break;

				case STOP:
					processStop(c);
					carryOn = false;
					break;

				case BREAKPOINT_GET:
					breakpointGet(c);
					break;

				case BREAKPOINT_SET:
					breakpointSet(c);
					break;

				case BREAKPOINT_UPDATE:
					breakpointUpdate(c);
					break;

				case BREAKPOINT_REMOVE:
					breakpointRemove(c);
					break;

				case BREAKPOINT_LIST:
					breakpointList(c);
					break;

				case STACK_DEPTH:
					stackDepth(c);
					break;

				case STACK_GET:
					stackGet(c);
					break;

				case CONTEXT_NAMES:
					contextNames(c);
					break;

				case CONTEXT_GET:
					contextGet(c);
					break;

				case PROPERTY_GET:
					propertyGet(c);
					break;

				case SOURCE:
					processSource(c);
					break;

				case STDOUT:
					processStdout(c);
					break;

				case STDERR:
					processStderr(c);
					break;

				case DETACH:
					carryOn = false;
					break;

				case XCMD_OVERTURE_CMD:
					processOvertureCmd(c);
					break;

				case PROPERTY_SET:
					propertySet(c);
					break;
				default:
					errorResponse(DBGPErrorCode.NOT_AVAILABLE, c.type.value);
			}
		} catch (DBGPException e)
		{
			errorResponse(e.code, e.reason);
		} catch (StackOverflowError e)
		{
			invocationError(e);
		} catch (Throwable e)
		{
			e.printStackTrace();
			String msg = e.getMessage();
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, msg != null ? msg : e.toString());
		}

		return carryOn;
	}

	private void propertySet(DBGPCommand c) throws DBGPException, IOException
	{

		checkArgs(c, 4, false);
		DBGPOption option = c.getOption(DBGPOptionType.K);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		boolean success = false;
		Integer key = Integer.parseInt(option.value);

		Value vOriginal = null;
		UpdatableValue uv = null;
		try
		{
			if (!debugValueMap.containsKey(key))
			{
				throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, "Key = "
						+ key);
			}
			Value v = debugValueMap.get(key);
			vOriginal = v.deepCopy();// inexpensive only support of simple types

			if (v instanceof UpdatableValue)
			{
				uv = (UpdatableValue) v;

				// TODO BUG: Here is a problem if the evaluate is suspended in a
				// property_set by the scheduler
				Value newval = interpreter.evaluate(c.data, interpreter.initialContext);

				if (newval != null && canAssignValue(newval.kind(), uv.kind()))
				{
					uv.set(breakpoint.location, newval, breakContext);
					success = true;
				}
			}
		} catch (ContextException e)
		{
			success = false;
			// aboard value update and put back the original value
			try
			{
				if (uv != null)
				{
					uv.set(breakpoint.location, vOriginal, breakContext);
				}
			} catch (Exception ex)
			{
				// this is fatal we cannot continue interpretation
				throw new DBGPException(DBGPErrorCode.INTERNAL_ERROR, ex.toString());
			}
		}

		catch (Exception e)
		{
			throw new DBGPException(DBGPErrorCode.INTERNAL_ERROR, e.toString());
		}

		StringBuilder hdr = new StringBuilder();

		hdr.append("success=\"" + (success ? "1" : "0") + "\"");

		response(hdr, null);

	}

	private boolean canAssignValue(String newKind, String source)
	{

		if (newKind.equals(source))
		{
			return true;
		}

		final String TYPE_REAL = "real";
		final String TYPE_NAT = "nat";
		final String TYPE_INT = "int";

		if (newKind.contains(TYPE_NAT) && source.equals(TYPE_NAT))
		{
			return true;
		}
		if (newKind.contains(TYPE_NAT) && source.equals(TYPE_REAL))
		{
			return true;
		}

		if (newKind.contains(TYPE_NAT) && source.equals(TYPE_INT))
		{
			return true;
		}
		if (newKind.contains(TYPE_INT) && source.equals(TYPE_REAL))
		{
			return true;
		}
		return false;
	}

	/**
	 * Send a xcmd Overture Response
	 * 
	 * @param overtureCmd
	 *            The overture command which this is a response to
	 * @param hdr
	 *            The header
	 * @param body
	 *            The body
	 * @throws IOException
	 */
	private void xcmdOvertureResponse(DBGPXCmdOvertureCommandType overtureCmd,
			StringBuilder hdr, StringBuilder body) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<xcmd_overture_response command=\"");
		sb.append(command);
		sb.append("\"");

		sb.append(" overtureCmd=\"");
		sb.append(overtureCmd);
		sb.append("\"");

		if (hdr != null)
		{
			sb.append(" ");
			sb.append(hdr);
		}

		sb.append(" transaction_id=\"");
		sb.append(transaction);
		sb.append("\"");

		if (body != null)
		{
			sb.append(">");
			sb.append(body);
			sb.append("</xcmd_overture_response>\n");
		} else
		{
			sb.append("/>\n");
		}

		write(sb);
	}

	@Override
	protected void statusResponse(DBGPStatus s, DBGPReason reason)
			throws IOException
	{
		if (s == DBGPStatus.STOPPED)
		{
			stopped = true;
		}

		StringBuilder sb = new StringBuilder();

		status = s;
		statusReason = reason;

		sb.append("status=\"");
		sb.append(status);
		sb.append("\"");
		sb.append(" reason=\"");
		sb.append(statusReason);
		sb.append("\"");

		StringBuilder body = new StringBuilder();
		body.append("<internal ");

		ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());

		if (th != null)
		{
			body.append("threadId=\"");
			body.append(th.getId());
			body.append("\" ");

			body.append("threadName=\"");
			body.append(th.getName());
			body.append("\" ");

			body.append("threadState=\"");
			body.append(th.getRunState().toString());
			body.append("\" ");

		}

		body.append("/>");

		response(sb, body);
	}

	/**
	 * Overrides super class by filtering all entries against isDebugVisible
	 */
	@Override
	protected StringBuilder propertyResponse(NameValuePairMap vars,
			DBGPContextType context) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();

		for (Entry<ILexNameToken, Value> e : vars.entrySet())
		{
			if (!e.getKey().getName().equals("self"))
			{ // This test makes the self not appear

				if (isDebugVisible(e.getValue()))
				{
					sb.append(propertyResponse(e.getKey(), e.getValue(), context));
				}
			}
		}

		return sb;
	}

	@Override
	protected StringBuilder propertyResponse(ILexNameToken name, Value value,
			DBGPContextType context) throws UnsupportedEncodingException
	{
		String nameString = context == DBGPContextType.GLOBAL ? name.getModule()
				+ "`" + name.getName()
				: name.getOld() ? name.getName() + "~" : name.getName();
		return propertyResponse(nameString, name.getExplicit(true).toString(), name.getModule(), value);
	}

	private StringBuilder propertyResponse(String name, String fullname,
			String clazz, Value value) throws UnsupportedEncodingException
	{
		return propertyResponse(name, fullname, clazz, value, 3, 0);
	}

	private StringBuilder propertyResponse(String name, String fullname,
			String clazz, Value value, Integer depth, Integer currentDepth)
			throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		currentDepth++;

		Integer numChildren = getChildCount(value);

		Integer page = 0;
		Integer pageSize = Integer.parseInt(features.getProperty(DBGPFeatures.MAX_CHILDREN));

		Integer key = null;
		String data = null;
		StringBuilder nestedChildren = null;

		// store property for retrieval of additional pages or value editing
		if (numChildren > pageSize || depth == currentDepth
				|| value instanceof UpdatableValue
				|| value instanceof ObjectValue)
		{
			debugValueKeyCounter++;
			debugValueMap.put(debugValueKeyCounter, value);
			key = debugValueKeyCounter;
		}

		if (numChildren > pageSize || depth == currentDepth)
		{
			name += " (ref=" + debugValueKeyCounter + ")";
		}

		if (numChildren > 0)
		{
			data = value.toShortString(SHORT_STRING_MAX);
		} else
		{
			data = value.toString();
		}

		Value valDeref = value.deref();
		if (valDeref instanceof FunctionValue)
		{
			data = formatFunctionValue((FunctionValue) valDeref);
		}

		if (currentDepth < depth && numChildren > 0)
		{
			// max depth not reached. Fetch children of page size
			sb.append(propertyResponseChild(value, depth, currentDepth, pageSize, 0));
			nestedChildren = propertyResponseChild(value, depth, currentDepth, pageSize, 0);
		}

		boolean constant = numChildren > 0
				|| !(value instanceof UpdatableValue);

		return makeProperty(name, fullname, value.kind(), clazz, page, pageSize, constant, data.length(), key, numChildren, data, nestedChildren);
	}

	private String formatFunctionValue(FunctionValue value)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(value.name + ": " + value.toString() + " & " + value.body);
		return sb.toString();
	}

	/**
	 * @param size
	 *            Unused.
	 */
	private StringBuilder makeProperty(String name, String fullName,
			String type, String clazz, Integer page, Integer pageSize,
			boolean constant, Integer size, Integer key, Integer numChildren,
			String data, StringBuilder nestedProperties)
			throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		Integer children = 0;

		if (numChildren > 0)
		{
			children = 1;
		}
		sb.append("<property");
		sb.append(" name=\"" + quote(name) + "\"");
		sb.append(" fullname=\"" + quote(fullName) + "\"");
		sb.append(" type=\"" + quote(type) + "\"");
		sb.append(" classname=\"" + clazz + "\"");
		if (numChildren > 0)
		{
			sb.append(" page=\"" + page + "\"");
			sb.append(" pagesize=\"" + pageSize + "\"");
		}

		sb.append(" constant=\"" + (constant ? "1" : "0") + "\"");
		sb.append(" children=\"" + children + "\"");

		StringBuffer encodedData = Base64.encode(data.getBytes("UTF-8"));

		sb.append(" size=\"" + encodedData.length() + "\"");
		if (key != null)
		{
			sb.append(" key=\"" + key + "\"");
		}
		sb.append(" encoding=\"base64\"");
		if (numChildren > 0)
		{
			sb.append(" numchildren=\"" + numChildren + "\"");
		}
		sb.append("><![CDATA[");
		sb.append(encodedData);
		sb.append("]]>");

		if (nestedProperties != null && nestedProperties.length() > 0)
		{
			sb.append(nestedProperties.toString());
		}

		sb.append("</property>");

		return sb;
	}

	/**
	 * Calculates if a value has children and returns the child count
	 * 
	 * @param value
	 *            The value to determine child count for
	 * @return number of children
	 */
	private Integer getChildCount(Value value)
	{
		// all types listed here are directly toString() in the debugger
		if (value instanceof NumericValue || value instanceof CharacterValue
				|| value instanceof NilValue || value instanceof TokenValue)
		{
			return 0;
		} else if (value instanceof SetValue)
		{
			return ((SetValue) value).values.size();
		} else if (value instanceof SeqValue)
		{
			boolean isString = true;
			for (Value v : ((SeqValue) value).values)
			{
				if (!(deref(v) instanceof CharacterValue))
				{
					isString = false;
				}
			}

			if (isString)
			{
				return 0; // tread as simple value
			} else
			{
				return ((SeqValue) value).values.size();
			}

		} else if (value instanceof MapValue)
		{
			return ((MapValue) value).values.size();
		} else if (value instanceof ObjectValue)
		{
			int count = 0;
			for (NameValuePair v : ((ObjectValue) value).members.asList())
			{
				if (isDebugVisible(v.value))
				{
					count++;
				}
			}
			return count;

		} else if (value instanceof UpdatableValue
				|| value instanceof TransactionValue
				|| value instanceof ReferenceValue)
		{
			return getChildCount(deref(value));
		} else if (value instanceof RecordValue)
		{
			RecordValue rVal = (RecordValue) value;
			return rVal.fieldmap.size();
		} else if (value instanceof TupleValue)
		{
			TupleValue tVal = (TupleValue) value;
			return tVal.values.size();
		}

		return 0;
	}

	/**
	 * Creates a string with property responses for all requested children of the parsed value, intended to be used as
	 * the body of the value parsed
	 * 
	 * @param value
	 *            The value which children should be fetched
	 * @param depth
	 *            The max depth
	 * @param currentDepth
	 *            The current depth (The method recurses over children if needed)
	 * @param pageSize
	 *            The page size used when returning children
	 * @param page
	 *            The current page
	 * @return A string with property responses for all requested children
	 * @throws UnsupportedEncodingException
	 */
	private StringBuilder propertyResponseChild(Value value, Integer depth,
			Integer currentDepth, Integer pageSize, Integer page)
			throws UnsupportedEncodingException
	{
		StringBuilder s = new StringBuilder();
		if (value instanceof SeqValue)
		{
			SeqValue sVal = (SeqValue) value;
			for (Integer i = page * pageSize; i < sVal.values.size()
					&& i < page + 1 * pageSize; i++)
			{
				Value element = sVal.values.get(i);
				Integer vdmIndex = i + 1;
				s.append(propertyResponse("Element["
						+ makeDisplayId(sVal.values.size(), vdmIndex) + "]", vdmIndex.toString(), "-", element, depth, currentDepth));

			}
		} else if (value instanceof SetValue)
		{
			SetValue sVal = (SetValue) value;
			for (Integer i = page * pageSize; i < sVal.values.size()
					&& i < (page + 1) * pageSize + 1; i++)
			{
				Value element = sVal.values.get(i);
				Integer vdmIndex = i + 1;
				s.append(propertyResponse("Element "
						+ makeDisplayId(sVal.values.size(), vdmIndex), vdmIndex.toString(), "-", element, depth, currentDepth));

			}
		} else if (value instanceof ObjectValue)
		{
			ObjectValue oVal = (ObjectValue) value;
			currentDepth++;
			for (ILexNameToken key : oVal.members.keySet())
			{
				Value val = oVal.members.get(key);
				if (isDebugVisible(val))
				{
					s.append(propertyResponse(key.getName(), key.getExplicit(true).toString(), key.getModule(), val, depth, currentDepth));
				}
			}
		} else if (value instanceof UpdatableValue)
		{
			return propertyResponseChild(((UpdatableValue) value).deref(), depth, currentDepth, pageSize, page);

		} else if (value instanceof TransactionValue)
		{
			return propertyResponseChild(((TransactionValue) value).deref(), depth, currentDepth, pageSize, page);

		} else if (value instanceof ReferenceValue)
		{
			return propertyResponseChild(((ReferenceValue) value).deref(), depth, currentDepth, pageSize, page);

		} else if (value instanceof MapValue)
		{
			MapValue mVal = (MapValue) value;
			Value[] keys = mVal.values.keySet().toArray(new Value[mVal.values.keySet().size()]);
			for (Integer i = page * pageSize; i < keys.length
					&& i < (page + 1) * pageSize + 1; i++)
			{
				Value dom = keys[i];
				Value rng = mVal.values.get(dom);
				Integer vdmIndex = i + 1;

				StringBuilder entries = new StringBuilder();
				entries.append(propertyResponse("dom", vdmIndex.toString(), "-", dom, depth, currentDepth));
				entries.append(propertyResponse("rng", vdmIndex.toString(), "-", rng, depth, currentDepth));
				s.append(makeProperty("Maplet "
						+ makeDisplayId(mVal.values.keySet().size(), vdmIndex), vdmIndex.toString(), value.kind(), "", page, pageSize, true, 2, null, 2, "{"
						+ dom + " |-> " + rng + "}", entries));
			}
		} else if (value instanceof RecordValue)
		{
			RecordValue rVal = (RecordValue) value;
			for (Integer i = page * pageSize; i < rVal.fieldmap.size()
					&& i < (page + 1) * pageSize + 1; i++)
			{
				FieldValue field = rVal.fieldmap.get(i);
				Integer vdmIndex = i + 1;
				s.append(propertyResponse(field.name, vdmIndex.toString(), "-", field.value, depth, currentDepth));

			}
		} else if (value instanceof TupleValue)
		{
			TupleValue tVal = (TupleValue) value;
			for (Integer i = page * pageSize; i < tVal.values.size()
					&& i < (page + 1) * pageSize + 1; i++)
			{
				Value v = tVal.values.get(i);
				Integer vdmIndex = i + 1;
				s.append(propertyResponse("#"
						+ makeDisplayId(tVal.values.size(), vdmIndex), vdmIndex.toString(), "-", v, depth, currentDepth));

			}
		}

		return s;
	}

	private String makeDisplayId(Integer size, Integer vdmIndex)
	{
		StringBuffer id = new StringBuffer(vdmIndex.toString());
		while (size.toString().length() > id.length())
		{
			id.insert(0, "0");
		}
		return id.toString();
	}

	/**
	 * Deref Value of Reference and Updatable Value types
	 * 
	 * @param value
	 *            The value to deref
	 * @return The internal value of the parameter
	 */
	private Value deref(Value value)
	{
		if (value instanceof ReferenceValue || value instanceof UpdatableValue
				|| value instanceof TransactionValue)
		{
			return value.deref();
		} else
		{
			return value;
		}
	}

	/**
	 * Determines if a value should be shown in the debug client
	 * 
	 * @param v
	 *            The value to check
	 * @return True if the value is allowed to be displayed in the client
	 */
	private boolean isDebugVisible(Value v)
	{
		return v instanceof ReferenceValue || v instanceof NumericValue
				|| v instanceof CharacterValue || v instanceof BooleanValue
				|| v instanceof SetValue || v instanceof SeqValue
				|| v instanceof MapValue || v instanceof TokenValue
				|| v instanceof RecordValue || v instanceof ObjectValue
				|| v instanceof NilValue || v instanceof FunctionValue;
	}

	/**
	 * Overridden to enable trace handling
	 */
	@Override
	protected boolean processRun(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, false);

		if (status == DBGPStatus.BREAK || status == DBGPStatus.STOPPING)
		{
			if (breakContext != null)
			{
				breakContext.threadState.setBreaks(null, null, null);
				status = DBGPStatus.RUNNING;
				statusReason = DBGPReason.OK;
				return false; // run means continue
			} else
			{
				throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
			}
		}

		if (status == DBGPStatus.STARTING && expression == null)
		{
			status = DBGPStatus.RUNNING;
			statusReason = DBGPReason.OK;
			return false; // a run for a new thread, means continue
		}

		if (status != DBGPStatus.STARTING)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		if (c.data != null) // data is in "expression"
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		if (remoteControl != null)
		{
			try
			{
				status = DBGPStatus.RUNNING;
				statusReason = DBGPReason.OK;
				final RemoteInterpreter remoteInterpreter = new RemoteInterpreter(interpreter, this);
				Thread remoteThread = new Thread(new Runnable()
				{

					public void run()
					{
						try
						{
							remoteControl.run(remoteInterpreter);
						} catch (Exception e)
						{
							status = DBGPStatus.STOPPED;
							statusReason = DBGPReason.ERROR;
							errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
						}
					}
				});
				remoteThread.setName("RemoteControl runner");
				remoteThread.setDaemon(true);
				remoteThread.start();
				remoteInterpreter.processRemoteCalls();
				stdout("Remote control completed");
				statusResponse(DBGPStatus.STOPPED, DBGPReason.OK);
				run();

			} catch (Exception e)
			{
				status = DBGPStatus.STOPPED;
				statusReason = DBGPReason.ERROR;
				errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
			}

			return false; // Do not continue after remote session
		} else
		{
			try
			{
				status = DBGPStatus.RUNNING;
				statusReason = DBGPReason.OK;
				if (!traceExpression)
				{
	    			long before = System.currentTimeMillis();
					if (expression.equals("###CONSOLE###"))
					{
						run();
					} else
					{
						theAnswer = interpreter.execute(expression, this);
					}
					stdout("\n" + expression + " = " + theAnswer.toString() + "\n");
	    			long after = System.currentTimeMillis();
	    			stdout("Executed in " + (double)(after-before)/1000 + " secs. ");
				} else
				{
					String[] parts = expression.split("\\s+");
					int testNo = 0;
					float reduction = 1.0F;
					TraceReductionType reductionType = TraceReductionType.NONE;
					long seed = 999;
					// Test`T1 4 {subset,reduction,seed}
					if (parts.length >= 2 && !parts[1].startsWith("{"))
					{
						try
						{
							testNo = Integer.parseInt(parts[1]);
						} catch (NumberFormatException e)
						{
							errorResponse(DBGPErrorCode.INTERNAL_ERROR, parts[0]
									+ " <name> [test number]");
							return true;
						}
					}
					if (parts.length >= 2
							&& parts[parts.length - 1].length() > 7
							&& parts[parts.length - 1].startsWith("{"))
					{
						try
						{
							String settings = parts[parts.length - 1];
							String[] tmp = settings.substring(1, settings.length() - 1).split(",");
							if (tmp.length == 3)
							{
								reduction = Float.parseFloat(tmp[0]);
								reductionType = TraceReductionType.valueOf(tmp[1]);
								seed = Long.parseLong(tmp[2]);
							}
						} catch (NumberFormatException e)
						{
							errorResponse(DBGPErrorCode.INTERNAL_ERROR, parts[0]
									+ " <name> [test number]");
							return true;
						}
					}

					String traceExpression1 = parts[0];

					interpreter.runtrace(traceExpression1, testNo, true, reduction, reductionType, seed);
					stdout("\n" + expression + " = " + "Trace completed\n");
				}

				statusResponse(DBGPStatus.STOPPED, DBGPReason.OK);

			} catch (ContextException e)
			{
				dyingThread(e);
			} catch (Exception e)
			{
				status = DBGPStatus.STOPPED;
				statusReason = DBGPReason.ERROR;
				errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
			}

			return true;
		}
	}

	@Override
	protected boolean processEval(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, true);

		if (status != DBGPStatus.BREAK && status != DBGPStatus.STOPPING
				|| breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		breaksSuspended = true;

		try
		{
			String exp = c.data; // Already base64 decoded by the parser
			interpreter.setDefaultName(breakpoint.location.getModule());
			theAnswer = interpreter.evaluate(exp, breakContext);
			StringBuilder property = propertyResponse(exp, exp, interpreter.getDefaultName(), theAnswer);
			StringBuilder hdr = new StringBuilder("success=\"1\"");
			response(hdr, property);
		} catch (Exception e)
		{
			errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
		} finally
		{
			breaksSuspended = false;
		}

		return true;
	}

	@Override
	protected boolean processExpr(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, true);

		if (status == DBGPStatus.BREAK || status == DBGPStatus.STOPPING)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		try
		{
			status = DBGPStatus.RUNNING;
			statusReason = DBGPReason.OK;
			String exp = c.data; // Already base64 decoded by the parser
			theAnswer = interpreter.execute(exp, this);
			StringBuilder property = propertyResponse(exp, exp, interpreter.getDefaultName(), theAnswer);
			StringBuilder hdr = new StringBuilder("success=\"1\"");
			status = DBGPStatus.STOPPED;
			statusReason = DBGPReason.OK;
			response(hdr, property);
		} catch (ContextException e)
		{
			dyingThread(e);
		} catch (Exception e)
		{
			status = DBGPStatus.STOPPED;
			statusReason = DBGPReason.ERROR;
			errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
		}

		return true;
	}

	protected boolean processExec(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, true);
		// TODO
		// if (status == DBGPStatus.BREAK || status == DBGPStatus.STOPPING) {
		// throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		// }

		try
		{
			status = DBGPStatus.RUNNING;
			statusReason = DBGPReason.OK;
			String exp = c.data; // Already base64 decoded by the parser

			DBGPReader dbgpReaderThread = newThread(null);
			DBGPExecResult result = DBGPExecProcesser.process(dbgpReaderThread, interpreter, exp);
			dbgpReaderThread.complete(DBGPReason.OK, null);
			StringBuilder property = makeProperty("", "", "", "", 0, 0, true, result.result.length(), -1, 0, result.result, new StringBuilder());
			theAnswer = new CharacterValue('l');
			StringBuilder hdr = new StringBuilder("success=\"1\"");
			status = DBGPStatus.STOPPED;
			statusReason = DBGPReason.OK;
			response(hdr, property);
			if (result.quit)
			{
				this.complete(DBGPReason.OK, null);
			}
		} catch (ContextException e)
		{
			dyingThread(e);
		} catch (Exception e)
		{
			status = DBGPStatus.STOPPED;
			statusReason = DBGPReason.ERROR;
			errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
		}

		return true;
	}

	@Override
	protected NameValuePairMap getContextValues(DBGPContextType context,
			int depth)
	{
		NameValuePairMap vars = new NameValuePairMap();

		switch (context)
		{
			case LOCAL:
				if (depth == 0)
				{
					vars.putAll(breakContext.getVisibleVariables());
				} else
				{
					Context frame = breakContext.getFrame(depth - 1).outer;

					if (frame != null)
					{
						vars.putAll(frame.getVisibleVariables());
					}
				}

				if (breakContext instanceof ObjectContext)
				{
					ObjectContext octxt = (ObjectContext) breakContext;
					int line = breakpoint.location.getStartLine();
					String opname = breakContext.guardOp == null ? ""
							: breakContext.guardOp.name.getName();

					for (PDefinition d : octxt.self.type.getClassdef().getDefinitions())
					{
						if (d instanceof APerSyncDefinition)
						{
							APerSyncDefinition pdef = (APerSyncDefinition) d;

							if (pdef.getOpname().getName().equals(opname)
									|| pdef.getLocation().getStartLine() == line
									|| octxt.assistantFactory.createPExpAssistant().findExpression(pdef.getGuard(), line) != null)
							{
								for (PExp sub : octxt.assistantFactory.createPExpAssistant().getSubExpressions(pdef.getGuard()))
								{
									if (sub instanceof AHistoryExp)
									{
										AHistoryExp hexp = (AHistoryExp) sub;

										try
										{
											Value v = hexp.apply(VdmRuntime.getExpressionEvaluator(), octxt);
											LexNameToken name = new LexNameToken(octxt.self.type.getName().getModule(), hexp.toString(), hexp.getLocation());
											vars.put(name, v);
										} catch (Throwable e)
										{
											// Ignore
										}

									}
								}

								break;
							}
						} else if (d instanceof AMutexSyncDefinition)
						{
							AMutexSyncDefinition mdef = (AMutexSyncDefinition) d;

							for (ILexNameToken mop : mdef.getOperations())
							{
								if (mop.getName().equals(opname))
								{
									for (ILexNameToken op : mdef.getOperations())
									{
										LexNameList ops = new LexNameList(op);
										PExp hexp = AstFactory.newAHistoryExp(mdef.getLocation(), new LexToken(null, VDMToken.ACTIVE), ops);

										try
										{
											Value v = hexp.apply(VdmRuntime.getExpressionEvaluator(), octxt);
											LexNameToken name = new LexNameToken(octxt.self.type.getName().getModule(), hexp.toString(), mdef.getLocation());
											vars.put(name, v);
										} catch (Throwable e)
										{
											// Ignore
										}

									}

									break;
								}
							}
						}
					}
				}
				break;

			case CLASS: // Includes modules
				Context root = breakContext.getFrame(depth);

				if (root instanceof ObjectContext)
				{
					// Filter Values based in isDebugVisible instead of
					// vars.putAll(octxt.self.members)
					ObjectContext octxt = (ObjectContext) root;
					for (ILexNameToken key : octxt.self.members.keySet())
					{
						Value v = octxt.self.members.get(key);
						if (isDebugVisible(v))
						{
							vars.put(key, v);
						}
					}
				} else if (root instanceof ClassContext)
				{
					ClassContext cctxt = (ClassContext) root;
					vars.putAll(cctxt.assistantFactory.createSClassDefinitionAssistant().getStatics(cctxt.classdef));
				} else if (root instanceof StateContext)
				{
					StateContext sctxt = (StateContext) root;

					if (sctxt.stateCtxt != null)
					{
						vars.putAll(sctxt.stateCtxt);
					}
				}
				break;

			case GLOBAL:
				vars.putAll(interpreter.initialContext);
				break;
		}

		return vars;
	}

	@Override
	protected void propertyGet(DBGPCommand c) throws DBGPException, IOException
	{
		if (c.data != null || c.options.size() > 5)// new parameter
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		if (status != DBGPStatus.BREAK && status != DBGPStatus.STOPPING)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		DBGPOption option = c.getOption(DBGPOptionType.C);
		int type = 0;

		if (option != null)
		{
			type = Integer.parseInt(option.value);
		}

		DBGPContextType context = DBGPContextType.lookup(type);

		option = c.getOption(DBGPOptionType.D);
		int depth = -1;

		if (option != null)
		{
			depth = Integer.parseInt(option.value);
		}
		//

		option = c.getOption(DBGPOptionType.P);
		int page = 0;
		if (option != null)
		{
			page = Integer.parseInt(option.value);
		}

		option = c.getOption(DBGPOptionType.K);
		Integer key;
		if (option != null)
		{
			key = Integer.parseInt(option.value);
			if (debugValueMap.containsKey(key))
			{
				response(null, propertyResponse(key, page));
				return;
			}
		}
		//

		option = c.getOption(DBGPOptionType.N);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, c.toString());
		}

		LexTokenReader ltr = new LexTokenReader(option.value, Dialect.VDM_PP);
		LexToken token = null;

		try
		{
			token = ltr.nextToken();
		} catch (LexException e)
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, option.value);
		} finally
		{
			ltr.close();
		}

		if (token.isNot(VDMToken.NAME))
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, token.toString());
		}

		NameValuePairMap vars = getContextValues(context, depth);
		LexNameToken longname = (LexNameToken) token;
		Value value = vars.get(longname);

		if (value == null)
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, longname.toString());
		}

		response(null, propertyResponse(longname, value, context));
	}

	private StringBuilder propertyResponse(Integer key, Integer page)
			throws UnsupportedEncodingException
	{
		Value value = debugValueMap.get(key);
		StringBuilder sb = new StringBuilder();
		Integer numChildren = getChildCount(value);

		Integer pageSize = Integer.parseInt(features.getProperty(DBGPFeatures.MAX_CHILDREN));
		String data = null;
		StringBuilder nestedChildren = null;
		String name = "(ref=" + key + ")";

		if (numChildren > 0)
		{
			data = value.kind().toString();
		} else
		{
			data = value.toString();
		}
		Integer defaultPageSize = Integer.parseInt(features.getProperty(DBGPFeatures.MAX_CHILDREN));
		sb.append(propertyResponseChild(value, 1, 0, defaultPageSize, page));
		nestedChildren = propertyResponseChild(value, 1, 0, defaultPageSize, page);

		boolean constant = numChildren > 0
				|| !(value instanceof UpdatableValue);

		return makeProperty(name, name, value.kind(), "", page, pageSize, constant, data.length(), key, numChildren, data, nestedChildren);
	}

	@Override
	protected void processOvertureCmd(DBGPCommand c) throws DBGPException,
			IOException, URISyntaxException, AnalysisException
	{
		checkArgs(c, 2, false);
		DBGPOption option = c.getOption(DBGPOptionType.C);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}
		if (option.value.startsWith(DBGPXCmdOvertureCommandType.LATEX.toString()))
		{
			processLatex(c);
		} else
		{
			DBGPXCmdOvertureCommandType xcmd = DBGPXCmdOvertureCommandType.lookup(option.value);

			switch (xcmd)
			{
				case INIT:
					processInit(c);
					break;
				case CREATE:
					processCreate(c);
					break;
				case CURRENT_LINE:
					processCurrentLine(c);
					break;
				case SOURCE:
					processCurrentSource(c);
					break;
				case COVERAGE:
					processCoverage(c);
					break;
				case WRITE_COMPLETE_COVERAGE:
					processWriteCoverage(c);
					break;
				case POG:
					processPOG(c);
					break;
				case STACK:
					processStack(c);
					break;
				case TRACE:
					processTrace(c);
					break;
				case LIST:
					processList();
					break;
				case FILES:
					processFiles();
					break;
				case CLASSES:
					processClasses(c);
					break;
				case MODULES:
					processModules(c);
					break;
				case DEFAULT:
					processDefault(c);
					break;
				case LOG:
					processLog(c);
					break;
				default:
					throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());

			}
		}
	}

	private void processClasses(DBGPCommand c) throws IOException,
			DBGPException
	{
		if (!(interpreter instanceof ClassInterpreter))
		{
			throw new DBGPException(DBGPErrorCode.INTERNAL_ERROR, c.toString());
		}

		ClassInterpreter cinterpreter = (ClassInterpreter) interpreter;
		String def = cinterpreter.getDefaultName();
		ClassList classes = cinterpreter.getClasses();
		OutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);

		for (SClassDefinition cls : classes)
		{
			if (cls.getName().getName().equals(def))
			{
				pw.println(cls.getName().getName() + " (default)");
			} else
			{
				pw.println(cls.getName().getName());
			}
		}

		pw.close();
		cdataResponse(out.toString());
	}

	private void processModules(DBGPCommand c) throws DBGPException,
			IOException
	{
		if (!(interpreter instanceof ModuleInterpreter))
		{
			throw new DBGPException(DBGPErrorCode.INTERNAL_ERROR, c.toString());
		}

		ModuleInterpreter minterpreter = (ModuleInterpreter) interpreter;
		String def = minterpreter.getDefaultName();
		List<AModuleModules> modules = minterpreter.getModules();
		OutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);

		for (AModuleModules m : modules)
		{
			if (m.getName().getName().equals(def))
			{
				pw.println(m.getName().getName() + " (default)");
			} else
			{
				pw.println(m.getName().getName());
			}
		}

		pw.close();
		cdataResponse(out.toString());
	}

	/**
	 * Overrides processLog to support URI file format and xcmdOvertureResponse as reply
	 */
	@Override
	protected void processLog(DBGPCommand c) throws IOException
	{
		StringBuilder out = new StringBuilder();

		try
		{
			if (c.data == null)
			{
				if (RTLogger.getLogSize() > 0)
				{
					out.append("Flushing " + RTLogger.getLogSize()
							+ " RT events\n");
				}

				RTLogger.setLogfile(RTTextLogger.class, null);
				RTLogger.setLogfile(NextGenRTLogger.class, (File) null);
				out.append("RT events now logged to the console");
			} else if (c.data.equals("off"))
			{
				RTLogger.enable(false);
				out.append("RT event logging disabled");
			} else
			{
				File file = new File(new URI(c.data));
				RTLogger.setLogfile(RTTextLogger.class, file);
				out.append("RT events now logged to " + c.data);
			}
		} catch (FileNotFoundException e)
		{
			out.append("Cannot create RT event log: " + e.getMessage());
		} catch (URISyntaxException e)
		{
			out.append("Cannot decode log file from URI: " + e.getMessage());
		}

		xcmdOvertureResponse(DBGPXCmdOvertureCommandType.LOG, null, out);
	}

	@Override
	protected void processStack(DBGPCommand c) throws IOException,
			DBGPException
	{
		if (status != DBGPStatus.BREAK && status != DBGPStatus.STOPPING
				|| breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		OutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);
		pw.println("Stopped at " + breakpoint);
		breakContext.printStackTrace(pw, true);
		pw.close();
		cdataResponse(out.toString());
	}

	private void processWriteCoverage(DBGPCommand c) throws DBGPException,
			IOException, URISyntaxException
	{
		// if (status == DBGPStatus.BREAK) {
		// throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		// }

		File file = new File(new URI(c.data));

		if (file == null || file.getName().length() == 0)
		{
			cdataResponse(file + ": folder not found");
		} else
		{
			file.mkdirs();
			writeCoverage(interpreter, file);
			StringBuilder sb = new StringBuilder();
			sb.append("Coverage written to: " + file.toURI().toASCIIString());
			xcmdOvertureResponse(DBGPXCmdOvertureCommandType.WRITE_COMPLETE_COVERAGE, null, sb);
		}
	}

	public static void writeCoverage(Interpreter interpreter, File coverage)
			throws IOException
	{

		Properties.init(); // Read properties file, if any

		for (File f : interpreter.getSourceFiles())
		{

			SourceFile source = interpreter.getSourceFile(f);

			File data = new File(coverage.getPath() + File.separator
					+ f.getName() + ".covtbl");
			PrintWriter pw = new PrintWriter(data);
			source.writeCoverage(pw);
			pw.close();

		}

		Properties.parser_tabstop = 1;// required to match locations with the editor representation
	}

	public static String getStackTrace(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

}
