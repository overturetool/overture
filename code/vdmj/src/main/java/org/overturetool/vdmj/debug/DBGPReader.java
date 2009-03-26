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
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.VDMPP;
import org.overturetool.vdmj.VDMRT;
import org.overturetool.vdmj.VDMSL;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.DebuggerException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.util.Base64;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.Value;

public class DBGPReader
{
	private final String host;
	private final int port;
	private final String sessionId;
	private final String expression;
	private final Socket socket;
	private final InputStream input;
	private final OutputStream output;
	private final Interpreter interpreter;

	private DBGPStatus status = null;
	private DBGPReason statusReason = null;
	private DBGPCommandType command = null;
	private String transaction = "";
	private DBGPFeatures features;
	private byte separator = '\0';

	private Context breakContext = null;
	private Breakpoint breakpoint = null;
	private Value theAnswer = null;

	// Usage: <host> <port> <sessionId> <dialect> <expression> {<filename>}

	public static void main(String[] args)
	{
		Settings.usingDBGP = true;

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String session = args[2];
		Settings.dialect = Dialect.valueOf(args[3]);
		String expression = args[4];

		List<File> files = new Vector<File>();

		for (int i=5; i<args.length; i++)
		{
			try
			{
				files.add(new File(new URI(args[i])));
			}
			catch (URISyntaxException e)
			{
				System.exit(4);
			}
		}

		VDMJ controller = null;

		switch (Settings.dialect)
		{
			case VDM_SL:
				controller = new VDMSL();
				break;

			case VDM_PP:
				controller = new VDMPP();
				break;

			case VDM_RT:
				controller = new VDMRT();
				break;
		}

		if (controller.parse(files) == ExitStatus.EXIT_OK)
		{
    		if (controller.typeCheck() == ExitStatus.EXIT_OK)
    		{
				try
				{
					Interpreter i = controller.getInterpreter();
					new DBGPReader(host, port, session, i, expression).run();
	    			System.exit(0);
				}
				catch (Exception e)
				{
					System.exit(3);
				}
    		}
    		else
    		{
    			System.exit(2);
    		}
		}
		else
		{
			System.exit(1);
		}
	}

	public DBGPReader(
		String host, int port, String session,
		Interpreter interpreter, String expression)
		throws Exception
	{
		this.host = host;
		this.port = port;
		this.sessionId = session;
		this.expression = expression;
		this.interpreter = interpreter;

		if (port > 0)
		{
			InetAddress server = InetAddress.getByName(host);
			socket = new Socket(server, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
		}
		else
		{
			socket = null;
			input = System.in;
			output = System.out;
			separator = ' ';
		}

		init();
	}

	public DBGPReader newThread() throws Exception
	{
		DBGPReader r = new DBGPReader(host, port, sessionId, interpreter, null);
		r.command = DBGPCommandType.UNKNOWN;
		r.transaction = "?";
		return r;
	}

	private void init() throws IOException
	{
		status = DBGPStatus.STARTING;
		statusReason = DBGPReason.OK;
		features = new DBGPFeatures();

		StringBuilder sb = new StringBuilder();

		sb.append("<init ");
		sb.append("appid=\"");
		sb.append(features.getProperty("language_name"));
		sb.append("\" ");
		sb.append("idekey=\"" + sessionId + "\" ");
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

		Set<File> files = interpreter.getSourceFiles();
		sb.append(" fileuri=\"");
		sb.append(files.iterator().next().toURI());		// Any old one...
		sb.append("\"");

		sb.append("/>\n");

		write(sb);
	}

	private String readLine() throws IOException
	{
		StringBuilder line = new StringBuilder();
		int c = input.read();

		while (c != '\n' && c > 0)
		{
			if (c != '\r') line.append((char)c);		// Ignore CRs
			c = input.read();
		}

		return (line.length() == 0 && c == -1) ? null : line.toString();
	}

	private void write(StringBuilder data) throws IOException
	{
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
		sb.append("\"");

		if (body != null)
		{
			sb.append(">");
			sb.append(body);
			sb.append("</response>\n");
		}
		else
		{
			sb.append("/>\n");
		}

		write(sb);
	}

	private void errorResponse(DBGPErrorCode errorCode, String reason)
	{
		try
		{
			StringBuilder sb = new StringBuilder();

			sb.append("<error code=\"");
			sb.append(errorCode.value);
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

	private void statusResponse(DBGPStatus s, DBGPReason reason, StringBuilder body)
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
		sb.append(" filename=\"" + bp.location.file.toURI() + "\"");
		sb.append(" lineno=\"" + bp.location.startLine + "\"");
		sb.append(">");

		if (bp.trace != null)
		{
			sb.append("<expression>" + bp.trace + "</expression>");
		}

		sb.append("</breakpoint>");

		return sb;
	}

	private StringBuilder stackResponse(LexLocation location, int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<stack level=\"" + level + "\"");
		sb.append(" type=\"file\"");
		sb.append(" filename=\"" + location.file.toURI() + "\"");
		sb.append(" lineno=\"" + location.startLine + "\"");
		sb.append(" cmdbegin=\"" + location.startLine + ":" + location.startPos + "\"");
		sb.append("/>");

		return sb;
	}

	private StringBuilder propertyResponse(NameValuePairMap vars)
		throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();

		for (Entry<LexNameToken, Value> e: vars.entrySet())
		{
			sb.append(propertyResponse(e.getKey(), e.getValue()));
		}

		return sb;
	}

	private StringBuilder propertyResponse(LexNameToken name, Value value)
		throws UnsupportedEncodingException
	{
		return propertyResponse(
			name.name, name.getExplicit(true).toString(),
			name.module, value.toString());
	}

	private StringBuilder propertyResponse(
		String name, String fullname, String clazz, String value)
		throws UnsupportedEncodingException
    {
    	StringBuilder sb = new StringBuilder();

    	sb.append("<property");
    	sb.append(" name=\"" + quote(name) + "\"");
    	sb.append(" fullname=\"" + quote(fullname) + "\"");
    	sb.append(" type=\"string\"");
    	sb.append(" classname=\"" + clazz + "\"");
    	sb.append(" constant=\"0\"");
    	sb.append(" children=\"0\"");
    	sb.append(" size=\"" + value.length() + "\"");
    	sb.append(" encoding=\"base64\"");
    	sb.append("><![CDATA[");
    	sb.append(Base64.encode(value.getBytes("UTF-8")));
    	sb.append("]]></property>");

    	return sb;
    }

	private static String quote(String in)
	{
		return in
    		.replaceAll("&", "&amp;")
    		.replaceAll("<", "&lt;")
    		.replaceAll(">", "&gt;")
    		.replaceAll("\\\"", "&quot;");
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

	public void stopped(Context ctxt, Breakpoint bp)
	{
		try
		{
			breakContext = ctxt;
			breakpoint = bp;
			statusResponse(DBGPStatus.BREAK, DBGPReason.OK, null);

			run();

			breakContext = null;
			breakpoint = null;
			status = DBGPStatus.RUNNING;
			statusReason = DBGPReason.OK;
		}
		catch (Exception e)
		{
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
		}
	}

	public void complete(DBGPReason reason)
	{
		try
		{
			statusResponse(DBGPStatus.STOPPED, reason, null);
		}
		catch (IOException e)
		{
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
		}
		finally
		{
			try
			{
				if (socket != null)
				{
					socket.close();
				}
			}
			catch (IOException e)
			{
				// ?
			}
		}
	}

	private boolean process(String line)
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

    			case STDOUT:
       			case STDERR:
       				processRedirect(c);
    				break;

    			case DETACH:
    				carryOn = false;
    				break;

    			default:
    				errorResponse(DBGPErrorCode.NOT_AVAILABLE, c.type.value);
    		}
		}
		catch (DBGPException e)
		{
			errorResponse(e.code, e.reason);
		}
		catch (Throwable e)
		{
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.toString());
		}

		return carryOn;
	}

	private DBGPCommand parse(String[] parts) throws Exception
	{
		// "<type> [<options>] [-- <base64 args>]"

		List<DBGPOption> options = new Vector<DBGPOption>();
		String args = null;
		boolean doneOpts = false;
		boolean gotXID = false;

		try
		{
			command = DBGPCommandType.lookup(parts[0]);

			for (int i=1; i<parts.length; i++)
			{
				if (doneOpts)
				{
					if (args != null)
					{
						throw new Exception("Expecting one base64 arg after '--'");
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

		return new DBGPCommand(command, options, args);
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
		StringBuilder hdr = new StringBuilder();
   		StringBuilder body = new StringBuilder();

		if (feature == null)
		{
			// Unknown feature - unsupported in header; nothing in body
    		hdr.append("feature_name=\"");
    		hdr.append(option.value);
    		hdr.append("\" supported=\"0\"");
		}
		else
		{
			// Known feature - supported in header; body reflects actual support
    		hdr.append("feature_name=\"");
    		hdr.append(option.value);
    		hdr.append("\" supported=\"1\"");
    		body.append(feature);
		}

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

	private boolean processRun(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, false);

		if (status == DBGPStatus.BREAK)
		{
			breakContext.threadState.set(0, null, null);
			return false;	// run means continue
		}

		if (expression == null || status != DBGPStatus.STARTING)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		if (c.data != null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		try
		{
			interpreter.init(this);
			theAnswer = interpreter.execute(expression, this);
			statusResponse(DBGPStatus.STOPPED, DBGPReason.OK, null);
		}
		catch (Exception e)
		{
			status = DBGPStatus.STOPPED;
			statusReason = DBGPReason.ERROR;
			errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
		}

		return true;
	}

	private boolean processEval(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, true);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		try
		{
			String exp = c.data;	// Already base64 decoded by the parser
			theAnswer = interpreter.evaluate(exp, breakContext);
			StringBuilder property = propertyResponse(
				exp, exp, interpreter.getDefaultName(), theAnswer.toString());
			StringBuilder hdr = new StringBuilder("success=\"1\"");
			response(hdr, property);
		}
		catch (Exception e)
		{
			errorResponse(DBGPErrorCode.EVALUATION_ERROR, e.getMessage());
		}

		return true;
	}

	private void processStepInto(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK || breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

   		breakContext.threadState.set(breakpoint.location.startLine, null, null);
	}

	private void processStepOver(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK || breakpoint == null)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		breakContext.threadState.set(breakpoint.location.startLine,	breakContext.getRoot(), null);
	}

	private void processStepOut(DBGPCommand c) throws DBGPException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		breakContext.threadState.set(breakpoint.location.startLine, null, breakContext.getRoot().outer);
	}

	private void processStop(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 1, false);

		if (status != DBGPStatus.BREAK)
		{
			throw new DBGPException(DBGPErrorCode.NOT_AVAILABLE, c.toString());
		}

		statusResponse(DBGPStatus.STOPPING, DBGPReason.OK, null);
		DebuggerException e = new DebuggerException("terminated");
		Interpreter.stop(e, breakContext);
	}

	private void breakpointGet(DBGPCommand c) throws DBGPException, IOException
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

		response(null, breakpointResponse(bp));
	}

	private void breakpointSet(DBGPCommand c)
		throws DBGPException, IOException, URISyntaxException
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

		option = c.getOption(DBGPOptionType.F);
		File filename = null;

		if (option != null)
		{
			filename = new File(new URI(option.value));
		}
		else
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		option = c.getOption(DBGPOptionType.S);

		if (option != null)
		{
    		if (!option.value.equalsIgnoreCase("enabled"))
    		{
    			throw new DBGPException(DBGPErrorCode.INVALID_BREAKPOINT, option.value);
    		}
		}

		option = c.getOption(DBGPOptionType.N);
		int lineno = 0;

		if (option != null)
		{
			lineno = Integer.parseInt(option.value);
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

		StringBuilder hdr = new StringBuilder(
			"state=\"enabled\" id=\"" + bp.number + "\"");
		response(hdr, null);
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

		// We omit the last two frames, as these are unhelpful (globals),
		// but include the "current" non-root frame as this helps.

		int actualDepth = breakContext.getDepth() - 1;

		if (depth >= actualDepth)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_STACK_DEPTH, c.toString());
		}

		if (depth == 0)
		{
			response(null, stackResponse(breakpoint.location, 0));
		}
		else if (depth > 0)
		{
			RootContext ctxt = breakContext.getFrame(depth);
			response(null, stackResponse(ctxt.location, depth));
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			Context ctxt = breakContext;

			int d = 0;
			sb.append(stackResponse(breakpoint.location, d++));		// Top frame

			while (ctxt != null && d < actualDepth)
			{
				if (ctxt instanceof RootContext)
				{
					sb.append(stackResponse(ctxt.location, d++));
				}

				ctxt = ctxt.outer;
			}

			response(null, sb);
		}
	}

	private void contextNames(DBGPCommand c) throws DBGPException, IOException
	{
		if (c.data != null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		DBGPOption option = c.getOption(DBGPOptionType.D);

		if (c.options.size() > ((option == null) ? 1 : 2))
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		StringBuilder names = new StringBuilder();

		names.append("<context name=\"Local\" id=\"0\"/>");
		names.append("<context name=\"Class\" id=\"1\"/>");
		names.append("<context name=\"Global\" id=\"2\"/>");

		response(null, names);
	}

	private NameValuePairMap getContextValues(DBGPContextType context, int depth)
	{
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

		return vars;
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

		NameValuePairMap vars = getContextValues(context, depth);

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

		NameValuePairMap vars = getContextValues(context, depth);
		LexNameToken longname = (LexNameToken)token;
		Value value = vars.get(longname);

		if (value == null)
		{
			throw new DBGPException(
				DBGPErrorCode.CANT_GET_PROPERTY, longname.toString());
		}

		response(null, propertyResponse(longname, value));
	}

	private void processRedirect(DBGPCommand c) throws DBGPException, IOException
	{
		checkArgs(c, 2, false);
		DBGPOption option = c.getOption(DBGPOptionType.C);

		if (option == null)
		{
			throw new DBGPException(DBGPErrorCode.INVALID_OPTIONS, c.toString());
		}

		StringBuilder hdr = new StringBuilder();
		hdr.append("success=\"1\"");

		response(hdr, null);
	}
}
