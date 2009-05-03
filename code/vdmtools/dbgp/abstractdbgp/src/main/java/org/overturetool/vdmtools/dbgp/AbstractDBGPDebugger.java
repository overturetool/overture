package org.overturetool.vdmtools.dbgp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Vector;

public abstract class AbstractDBGPDebugger extends Thread{
//	public boolean isInited;
//	private String className;
//	private String operationName;
//	private boolean debugFromConsole;
//	protected String sessionId;

	String runTransctionId;
	private String transaction = "";
	private final InputStream input;
	private final Socket socket;
	private OutputStream out;
	private ErrorType errorType;
	private DBGPCommandType command = null;
	private byte separator = '\0';
	private DBGPStatus status = null;
	private DBGPReason statusReason = null;
	
	
	private Hashtable<DBGPCommandType, CommandResponse> strategies = new Hashtable<DBGPCommandType, CommandResponse>(); 
	protected String host;
	protected int port;
	protected String ideKey;
	protected VdmDialect dialect;
	protected String expression;
	//protected ArrayList<String> fileNames;
	protected int sessionId;
	private ArrayList<File> files = new ArrayList<File>();
	
	public ArrayList<File> getFiles() {
		return files;
	}

	public AbstractDBGPDebugger(
			String host,
			int port,
			String ideKey,
			String dialect,
			String expression,
			ArrayList<String> files 
			) throws IOException {
		// start constructor
		
		
		//super();
		// set local variables
		this.host = host;
		this.port = port;
		this.ideKey = ideKey;
		this.dialect = VdmDialect.valueOf(dialect);
		this.expression = expression;
		for (String filename : files) {
			try
			{
				this.files.add(new File(new URI(filename)));
			}
			catch (URISyntaxException e)
			{
				throw new IOException("Could not add the file");
				//System.exit(4);
			}
		}
		sessionId = Math.abs(new Random().nextInt());
		socket = new Socket(host, port);
	
		input = socket.getInputStream();
		setupCommands();
		// init
		try
		{	
			out = new BufferedOutputStream(socket.getOutputStream(), 2048);
			printResponse(init());
		}catch (Exception e) {
			System.out.println("Exception BufferedOutputStream " + e.getMessage());
		}
		
	}
	
	/**
	 * addStrategy<command name> must be called for all commands supported by the debugger!
	 * @see Command
	 * TODO: JAVA DOC	 
	 */
	protected abstract void setupCommands();
	
	
	/***
	 * The debugger engine initiates a debugging session. The debugger
	 * engine will make a connection to a listening IDE, then wait for the
	 * IDE to initiate commands. When a debugger engine connects to either a
	 * IDE or proxy, it must send an init packet:
	 * 
	 * <init appid="APPID" idekey="IDE_KEY" session="DBGP_COOKIE"
	 * thread="THREAD_ID" parent="PARENT_APPID" language="LANGUAGE_NAME"
	 * protocol_version="1.0" fileuri="file://path/to/file">
	 */
	protected abstract String init();
	

	
	/**
	 * 
	 * @param cmd
	 */
	protected void addStrategy(DBGPCommandType type, CommandResponse cmd){
		strategies.put(type, cmd);
	}
		
	
	
	public void SendErrorCode(String cmd, String transId, int errCode, String appErr, String msg) {
		this.printResponse("<response command=\""+ cmd +"\"\r\n"					 
				 + " transaction_id=\"" + transId + "\">\r\n"
				 + "<error code=\"" + errCode + "\" apperr=\"" + appErr +"\">\r\n"
				 + "<message>"+ msg +"</message>"
				 +	"</error>"
				 + "</response>\r\n" + "");
		
	}
	
	protected /*Breakpoint*/ int setBreakpoint(String filename, int lineNo){
//		return interpreter.setBreakpoint(filename, lineNo);
		return 0; // TODO return breakpoint... .. . . . .
	}
	
	public void update(Observable o, Object arg) {
		if (runTransctionId != null)
			printResponse("<response command=\"run\"\r\n" + "status=\"break\""
					+ " reason=\"ok\"" + " transaction_id=\"" + runTransctionId
					+ "\">\r\n" + "</response>\r\n" + "");
		
	}
	
	public void notifyEnd() {
		if(errorType == null){
			printResponse("<response command=\"run\"\r\n" + "status=\"stopped\""
					+ " reason=\"ok\"" + " transaction_id=\"" + runTransctionId 
					+ "\">\r\n" + "</response>\r\n" + "");
			System.exit(0);
		}
	}
	
	private static void writeResponseLength(OutputStream out, int value) throws IOException {
		out.write(String.valueOf(value).getBytes());
	}
	
	public void printResponse(String response) {
		try {
			System.out.println("printResponse():\n " + response);
			byte[] bytes = response.getBytes("UTF-8"); //$NON-NLS-1$
			writeResponseLength(out, bytes.length);
			out.write(0);
			out.write(bytes, 0, bytes.length);
			out.write(0);
			out.flush();
		} catch (IOException e) {
			try {
				System.out.println("print response exception: " + e.getMessage());
				socket.close();
			} catch (IOException e2) {
				// ignore
				System.out.println("ioexception... " + e2.getMessage());
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
    		DBGPCommand command = parse(parts);
    		
    		if (command.type == DBGPCommandType.UNKNOWN)
    		{
    			errorResponse(DBGPErrorCode.NOT_AVAILABLE, command.type.value);    			
    		}
    		// runs the command
    		if (strategies.containsKey(command.type))
    		{
    			CommandResponse commandResponse = strategies.get(command.type);
    			String response = commandResponse.parseAndExecute(command);
    			printResponse(response);
    		}
    		else
    		{
    			errorResponse(DBGPErrorCode.NOT_AVAILABLE, command.type.value);
    		}
			return carryOn;
		}
		catch (DBGPException dbgpException) {
			errorResponse(DBGPErrorCode.UNKNOWN_ERROR, dbgpException.getMessage());
			return true;
		}
		catch (Exception e) {
			errorResponse(DBGPErrorCode.INTERNAL_ERROR, e.getMessage());
			return false;
		}
	}
	
	public void run()
	{
		try{
			String line = null;
			
			do
			{
				line = readLine();
			}
			while (line != null && process(line));
		}catch (Exception e) {
		}
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
	
	
	public void errorResponse(DBGPErrorCode errorCode, String reason)
	{
		String error = 
			"<error code=\""  + errorCode.value + "\" apperr=\"\">" +
				"<message>" + reason + "</message>" +
			"</error>";
		printResponse(error);
	}
	
	public void statusResponse(DBGPStatus s, DBGPReason reason, StringBuilder body) throws IOException
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
	
	private void write(StringBuilder data) throws IOException
	{
		byte[] header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8");
		byte[] body = data.toString().getBytes("UTF-8");
		byte[] size = Integer.toString(header.length + body.length).getBytes("UTF-8");

		out.write(size);
		out.write(separator);
		out.write(header);
		out.write(body);
		out.write(separator);

		out.flush();
	}

	
//	private static String escapeHTML(String content) {
//		content = replace(content, '&', "&amp;"); //$NON-NLS-1$
//		content = replace(content, '"', "&quot;"); //$NON-NLS-1$
//		content = replace(content, '<', "&lt;"); //$NON-NLS-1$
//		return replace(content, '>', "&gt;"); //$NON-NLS-1$
//	}
//
//	private static String replace(String text, char c, String s) {
//
//		int previous = 0;
//		int current = text.indexOf(c, previous);
//
//		if (current == -1)
//			return text;
//
//		StringBuffer buffer = new StringBuffer();
//		while (current > -1) {
//			buffer.append(text.substring(previous, current));
//			buffer.append(s);
//			previous = current + 1;
//			current = text.indexOf(c, previous);
//		}
//		buffer.append(text.substring(previous));
//
//		return buffer.toString();
//	}
		
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
}

