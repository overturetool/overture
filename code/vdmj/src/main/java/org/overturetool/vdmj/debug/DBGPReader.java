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

package org.overturetool.vdmj.debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.DebuggerException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;

public class DBGPReader
{
	private ClassList classes;
	private InputStream input;
	private OutputStream output;
	private Interpreter interpreter = null;

	private int sessionId;
	private DBGPStatus status = null;
	private String statusReason = "";
	private String command = "";
	private String transaction = "";
	private DBGPFeatures features;

	private Context breakContext = null;
	private Breakpoint breakpoint = null;

	public static void main(String[] args) throws Exception
	{
		Settings.usingDBGP = true;
		Settings.dialect = Dialect.VDM_PP;

		LexTokenReader ltr = new LexTokenReader(new File(args[0]), Dialect.VDM_PP);
		ClassReader mr = new ClassReader(ltr);
		ClassList classes = mr.readClasses();

		if (mr.getErrorCount() == 0)
		{
    		TypeChecker tc = new ClassTypeChecker(classes);
    		tc.typeCheck();

    		if (TypeChecker.getErrorCount() == 0)
    		{
    			DBGPReader r = new DBGPReader(classes, 0);
    			r.run();
    		}
		}
	}

	public DBGPReader(ClassList classes, int port) throws Exception
	{
		this.classes = classes;
		this.interpreter = new ClassInterpreter(classes);

		if (port > 0)
		{
			InetAddress server = InetAddress.getLocalHost();
			Socket socket = new Socket(server, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
		}
		else
		{
			input = System.in;
			output = System.out;
		}

		init();
	}

	private void init() throws IOException
	{
		sessionId = Math.abs(new Random().nextInt());
		status = DBGPStatus.STARTING;
		features = new DBGPFeatures();

		StringBuilder sb = new StringBuilder();

		sb.append("<init ");
		sb.append("appid=\"");
		sb.append(features.getProperty("language_name"));
		sb.append("\" ");
		sb.append("idekey=\"\" ");
		sb.append("session=\"" + sessionId + "\" ");
		sb.append("thread=\"");
		sb.append(Thread.currentThread().getId());
		sb.append("\" ");
		sb.append("parent=\"");
		sb.append(features.getProperty("language_name"));
		sb.append("\" ");
		sb.append("language=\"");
		sb.append(features.getProperty("language_name"));
		sb.append("\" ");
		sb.append("protocol_version=\"");
		sb.append(features.getProperty("protocol_version"));
		sb.append("\"");

		for (File f: classes.getSourceFiles())
		{
			sb.append(" fileuri=\"");
			sb.append(f.toURI());
			sb.append("\"");
		}

		sb.append("/>\n");

		write(sb);
	}

	private String readLine() throws IOException
	{
		StringBuilder line = new StringBuilder();
		char c = (char)input.read();

		while (c != '\n' && c >= 0)
		{
			if (c != '\r') line.append(c);		// Ignore CRs
			c = (char)input.read();
		}

		return (line.length() == 0 && c == -1) ? null : line.toString();
	}

	private void write(StringBuilder data) throws IOException
	{
		byte separator = ' ';
		byte[] header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8");
		byte[] body = data.toString().getBytes("UTF-8");
		byte[] size = Integer.toString(header.length + body.length).getBytes("UTF-8");

		output.write(size);
		output.write(separator);
		output.write(header);
		output.write(body);
		output.write(separator);

		output.flush();
	}

	private void response(StringBuilder hdr, StringBuilder body) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<response command=\"");
		sb.append(command);
		sb.append("\"");

		if (hdr != null)
		{
			sb.append(" ");
			sb.append(hdr);
		}

		sb.append(" transaction_id=\"");
		sb.append(transaction);
		sb.append("\">");

		if (body != null)
		{
			sb.append(body);
		}

		sb.append("</response>\n");

		write(sb);
	}

	private void errorResponse(DBGPErrorCode errorCode, String reason)
	{
		try
		{
			StringBuilder sb = new StringBuilder();

			sb.append("<error code=\"");
			sb.append(errorCode);
			sb.append("\" apperr=\"\"><message>");
			sb.append(reason);
			sb.append("</message></error>");

			response(null, sb);
		}
		catch (IOException e)
		{
			throw new MessageException("Internal 0029: DBGP: " + reason);
		}
	}

	private void statusResponse() throws IOException
	{
		statusResponse(status, statusReason, null);
	}

	private void statusResponse(DBGPStatus s, String reason, StringBuilder body)
		throws IOException
	{
		StringBuilder sb = new StringBuilder();

		status = s;
		statusReason = reason;

		sb.append("status=\"");
		sb.append(status);
		sb.append("\"");
		sb.append(" reason=\"");
		sb.append(statusReason);
		sb.append("\"");

		response(sb, body);
	}

	private StringBuilder breakpointResponse(Breakpoint bp)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<breakpoint id=\"" + bp.number + "\"");
		sb.append(" type=\"line\"");
		sb.append(" state=\"enabled\"");
		sb.append(" filename=\"" + bp.location.file + "\"");
		sb.append(" lineno=\"" + bp.location.startLine + "\"");

		/*
		sb.append(" function=\"?\"");
		sb.append(" exception=\"?\"");
		sb.append(" hit_value=\"?\"");
		sb.append(" hit_condition=\"?\"");
		sb.append(" hit_count=\"?\"");
		*/

		sb.append(">");

		if (bp.trace != null)
		{
			sb.append("<expression>" + bp.trace + "</expression>");
		}

		sb.append("</breakpoint>");

		return sb;
	}

	private StringBuilder stackResponse(Context ctxt, int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<stack level=\"" + level + "\"");
		sb.append(" type=\"file\"");
		sb.append(" filename=\"" + ctxt.location.file + "\"");
		sb.append(" lineno=\"" + ctxt.location.startLine);
		sb.append(" cmdbegin=\"" + ctxt.location.file + ":" + ctxt.location.startPos + "\"");
		sb.append("</stack>");

		return sb;
	}

	private StringBuilder propertyResponse(NameValuePairMap vars)
	{
		StringBuilder sb = new StringBuilder();

		for (LexNameToken name: vars.keySet())
		{
			sb.append(propertyResponse(name, vars.get(name)));
		}

		return sb;
	}

	private StringBuilder propertyResponse(LexNameToken name, Value value)
	{
		StringBuilder sb = new StringBuilder();
		String sval = value.toString();

		sb.append("<property");
		sb.append(" name=\"" + name.name + "\"");
		sb.append(" fullname=\"" + name.getExplicit(true) + "\"");
		sb.append(" type=\"resource\"");
		sb.append(" classname=\"" + name.module + "\"");
		sb.append(" constant=\"0\"");
		sb.append(" children=\"0\"");
		sb.append(" size=\"" + sval.length() + "\"");
		sb.append(" encoding=\"none\"");
		sb.append(">");
		sb.append(sval);
		sb.append("</property>");

		return sb;
	}

	private void run() throws IOException
	{
		String line = null;

		do
		{
			line = readLine();
		}
		while (line != null && process(line));
	}

	private boolean process(String line)
	{
		boolean carryOn = true;

		try
		{
			command = "?";
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

    			case STEP_INTO:
    				processStepInto(c);
    				carryOn = true;
    				break;

    			case STEP_OVER:
    				processStepOver(c);
    				carryOn = true;
    				break;

    			case STEP_OUT:
    				processStepOut(c);
    				carryOn = true;
    				break;

    			case STOP:
    				processStop(c);
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

    			case PROPERTY_SET:
    				break;

    			case DETACH:
    			default:
    				errorResponse(DBGPErrorCode.NOT_AVAILABLE, c.type.value);
    		}
		}
		catch (DBGPException e)
		{
			errorResponse(e.code, e.reason);
		}
		catch (Exception e)
		{
			errorResponse(DBGPErrorCode.PARSE, e.getMessage());
		}

		return carryOn;
	}

	private DBGPCommand parse(String[] parts) throws DBGPException
	{
		// "<type> [<options>] [-- <args>]"

		DBGPCommandType type = null;
		List<DBGPOption> options = new Vector<DBGPOption>();
		String args = null;
		boolean doneOpts = false;
		boolean gotXID = false;

		try
		{
			type = DBGPCommandType.lookup(parts[0]);
			command = type.value;

			for (int i=1; i<parts.length; i++)
			{
				if (doneOpts)
				{
					if (args != null)
					{
						// throw new Exception("Expecting one arg after '--'");
						args = args + " " + parts[i];
					}
					else
					{
						args = parts[i];
					}
				}
				else
				{
	    			if (parts[i].equals("--"))
	    			{
	    				doneOpts = true;
	    			}
	     			else
	    			{
	    				DBGPOptionType opt = DBGPOptionType.lookup(parts[i++]);

	    				if (opt == DBGPOptionType.TRANSACTION_ID)
	    				{
	    					gotXID = true;
	    					transaction = parts[i];
	    				}

						options.add(new DBGPOption(opt, parts[i]));
	     			}
				}
			}

			if (!gotXID)
			{
				throw new Exception("No transaction_id");
			}
		}
		catch (DBGPException e)
		{
			throw e;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new DBGPException(
				DBGPErrorCode.INVALID_OPTIONS, "Option arg missing");
		}
		catch (Exception e)
		{
			if (doneOpts)
			{
				throw new DBGPException(DBGPErrorCode.PARSE, e.getMessage());
			}
			else
			{
				throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, e.getMessage());
			}
		}

		return new DBGPCommand(type, options, args);
	}

	private void checkArgs(DBGPCommand c, int n, boolean data) throws DBGPException
	{
		if (data && c.data == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		if (c.options.size() != n)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}
	}

	private void processStatus(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);
		statusResponse();
	}

	private void processFeatureGet(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 2, false);
		DBGPOption option = c.getOption(DBGPOptionType.N);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		String feature = features.getProperty(option.value);

		if (feature == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		StringBuilder hdr = new StringBuilder();

		hdr.append("feature_name=\"");
		hdr.append(option.value);
		hdr.append("\" supported=\"1\"");

		StringBuilder body = new StringBuilder();
		body.append(feature);

		response(hdr, body);
	}

	private void processFeatureSet(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 3, false);
		DBGPOption option = c.getOption(DBGPOptionType.N);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		String feature = features.getProperty(option.value);

		if (feature == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		DBGPOption newval = c.getOption(DBGPOptionType.V);

		if (newval == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		features.setProperty(option.value, newval.value);

		StringBuilder hdr = new StringBuilder();

		hdr.append("feature_name=\"");
		hdr.append(option.value);
		hdr.append("\" success=\"1\"");

		response(hdr, null);
	}

	private boolean processRun(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, true);

		if (status == DBGPStatus.BREAK)
		{
			return false;	// run means continue
		}

		if (status != DBGPStatus.STARTING)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		try
		{
			interpreter.init(this);
			Value v = interpreter.execute(c.data, this);
			StringBuilder sb = new StringBuilder();

			sb.append("<message>");
			sb.append("=");
			sb.append(v.toString());
			sb.append("</message>");

			statusResponse(DBGPStatus.STOPPING, "OK", sb);
		}
		catch (Exception e)
		{
			StringBuilder sb = new StringBuilder();

			sb.append("<message>");
			sb.append(e.getMessage());
			sb.append("</message>");

			statusResponse(DBGPStatus.STOPPING, "ERROR", sb);
		}

		return true;
	}

	private void processStepInto(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK || breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		statusResponse(DBGPStatus.BREAK, "OK", null);
   		breakContext.threadState.set(breakpoint.location.startLine, null, null);
	}

	private void processStepOver(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK || breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		statusResponse(DBGPStatus.BREAK, "OK", null);
		breakContext.threadState.set(breakpoint.location.startLine,	breakContext.getRoot(), null);
	}

	private void processStepOut(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		statusResponse(DBGPStatus.BREAK, "OK", null);
		breakContext.threadState.set(breakpoint.location.startLine, null, breakContext.getRoot().outer);
	}

	private void processStop(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		statusResponse(DBGPStatus.STOPPING, "OK", null);
		DebuggerException e = new DebuggerException("terminated");
		Interpreter.stop(e, breakContext);
	}

	private void breakpointGet(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 2, false);

		DBGPOption option = c.getOption(DBGPOptionType.D);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		Breakpoint bp = interpreter.breakpoints.get(Integer.parseInt(option.value));

		if (bp == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, c.toString());
		}

		breakpointResponse(bp);
	}

	private void breakpointSet(DBGPCommand c) throws DBGPException, IOException
	{
		DBGPOption option = c.getOption(DBGPOptionType.T);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		DBGPBreakpointType type = DBGPBreakpointType.lookup(option.value);

		if (type == null)
		{
			throw new DBGPException(DBGPErrorCode.BREAKPOINT_TYPE_UNSUPPORTED, option.value);
		}

		option = c.getOption(DBGPOptionType.S);

		if (option != null)
		{
    		if (!option.value.equalsIgnoreCase("enabled"))
    		{
    			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
    		}
		}

		option = c.getOption(DBGPOptionType.F);
		String filename = null;

		if (option != null)
		{
			filename = new File(option.value).getPath();
		}

		option = c.getOption(DBGPOptionType.N);
		int lineno = 0;

		if (option != null)
		{
			lineno = Integer.parseInt(option.value);
		}

		option = c.getOption(DBGPOptionType.M);

		if (option != null)
		{
   			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
		}

		option = c.getOption(DBGPOptionType.X);

		if (option != null)
		{
   			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
		}

		option = c.getOption(DBGPOptionType.H);

		if (option != null)
		{
   			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
		}

		option = c.getOption(DBGPOptionType.O);

		if (option != null)
		{
   			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
		}

		option = c.getOption(DBGPOptionType.R);

		if (option != null)
		{
   			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
		}

		String condition = null;

		if (c.data != null)
		{
			condition = c.data;
		}

		Breakpoint bp = null;
		Statement stmt = interpreter.findStatement(filename, lineno);

		if (stmt == null)
		{
			Expression exp = interpreter.findExpression(filename, lineno);

			if (exp == null)
			{
				throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT, filename + ":" + lineno);
			}
			else
			{
				try
				{
					interpreter.clearBreakpoint(exp.breakpoint.number);
					bp = interpreter.setBreakpoint(exp, condition);
				}
				catch (ParserException e)
				{
					throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT,
						filename + ":" + lineno + ", " + e.getMessage());
				}
				catch (LexException e)
				{
					throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT,
						filename + ":" + lineno + ", " + e.getMessage());
				}
			}
		}
		else
		{
			try
			{
				interpreter.clearBreakpoint(stmt.breakpoint.number);
				bp = interpreter.setBreakpoint(stmt, condition);
			}
			catch (ParserException e)
			{
				throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT,
					filename + ":" + lineno + ", " + e.getMessage());
			}
			catch (LexException e)
			{
				throw new DBGPException(DBGPErrorCode.CANT_SET_BREAKPOINT,
					filename + ":" + lineno + ", " + e.getMessage());
			}
		}

		response(null, breakpointResponse(bp));
	}

	private void breakpointUpdate(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 2, false);

		DBGPOption option = c.getOption(DBGPOptionType.D);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		Breakpoint bp = interpreter.breakpoints.get(Integer.parseInt(option.value));

		if (bp == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, c.toString());
		}

		// ???
	}

	private void breakpointRemove(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 2, false);

		DBGPOption option = c.getOption(DBGPOptionType.D);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		Breakpoint old = interpreter.clearBreakpoint(Integer.parseInt(option.value));

		if (old == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, c.toString());
		}

		response(null, null);
	}

	private void breakpointList(DBGPCommand c) throws IOException, DBGPException
	{
		checkArgs(c, 1, false);
		StringBuilder bps = new StringBuilder();

		for (Integer key: interpreter.breakpoints.keySet())
		{
			Breakpoint bp = interpreter.breakpoints.get(key);
			bps.append(breakpointResponse(bp));
		}

		response(null, bps);
	}

	private void stackDepth(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		StringBuilder sb = new StringBuilder();
		sb.append(breakContext.getDepth());

		response(null, sb);
	}

	private void stackGet(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		DBGPOption option = c.getOption(DBGPOptionType.D);
		int depth = -1;

		if (option != null)
		{
			depth = Integer.parseInt(option.value);	// 0 to n-1
		}

		if (depth >= breakContext.getDepth())
		{
			throw new DBGPException(DBGPErrorCode.INVALID_STACK_DEPTH, c.toString());
		}

		if (depth >= 0)
		{
			Context ctxt = breakContext.getFrame(depth);
			response(null, stackResponse(ctxt, depth));
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			Context ctxt = breakContext;
			int d = 0;

			while (ctxt != null)
			{
				if (ctxt instanceof RootContext)
				{
					sb.append(stackResponse(ctxt, d++));
				}

				ctxt = ctxt.outer;
			}

			response(null, sb);
		}
	}

	private void contextNames(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		StringBuilder names = new StringBuilder();

		names.append("<context name=\"Local\" id=\"0\"/>");
		names.append("<context name=\"Class\" id=\"1\"/>");
		names.append("<context name=\"Global\" id=\"2\"/>");

		response(null, names);
	}

	private void contextGet(DBGPCommand c) throws DBGPException, IOException
	{
		if (c.data != null || c.options.size() > 3)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		if (status != DBGPStatus.BREAK)
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

		NameValuePairMap vars = new NameValuePairMap();
		Context frame = (depth < 0) ? breakContext : breakContext.getFrame(depth);

		switch (context)
		{
			case LOCAL:
				vars.putAll(frame.getFreeVariables());
				break;

			case CLASS:
				RootContext root = frame.getRoot();

				if (root instanceof ObjectContext)
				{
					ObjectContext octxt = (ObjectContext)root;
					vars.putAll(octxt.self.members);
				}
				else
				{
					StateContext sctxt = (StateContext)root;
					vars.putAll(sctxt.stateCtxt);
				}
				break;

			case GLOBAL:
				vars.putAll(frame.getGlobal());
				break;
		}

		response(null, propertyResponse(vars));
	}

	private void propertyGet(DBGPCommand c) throws DBGPException, IOException
	{
		if (c.data != null || c.options.size() > 4)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		if (status != DBGPStatus.BREAK)
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
		}
		catch (LexException e)
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, option.value);
		}

		if (token.isNot(Token.NAME))
		{
			throw new DBGPException(DBGPErrorCode.CANT_GET_PROPERTY, token.toString());
		}

		LexNameToken longname = (LexNameToken)token;
		Context frame = (depth < 0) ? breakContext : breakContext.getFrame(depth);
		Value value = null;

		switch (context)
		{
			case LOCAL:
				value = frame.getFreeVariables().get(longname);
				break;

			case CLASS:
				RootContext root = frame.getRoot();

				if (root instanceof ObjectContext)
				{
					ObjectContext octxt = (ObjectContext)root;
					value = octxt.self.members.get(longname);
				}
				else
				{
					StateContext sctxt = (StateContext)root;
					value = sctxt.stateCtxt.get(longname);
				}
				break;

			case GLOBAL:
				value = frame.getGlobal().get(longname);
				break;
		}

		if (value == null)
		{
			throw new DBGPException(
				DBGPErrorCode.CANT_GET_PROPERTY, longname.toString());
		}

		response(null, propertyResponse(longname, value));
	}

	public void stopped(Context ctxt, Breakpoint bp)
	{
		try
		{
			breakContext = ctxt;
			breakpoint = bp;
			statusResponse(DBGPStatus.BREAK, "BREAKPOINT", null);

			run();

			breakContext = null;
			breakpoint = null;
			status = DBGPStatus.RUNNING;
			statusReason = "OK";
		}
		catch (Exception e)
		{
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
		}
	}
}
