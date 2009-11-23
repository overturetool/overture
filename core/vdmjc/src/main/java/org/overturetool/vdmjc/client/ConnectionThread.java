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

package org.overturetool.vdmjc.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;

import org.overturetool.vdmjc.common.Base64;
import org.overturetool.vdmjc.common.Utils;
import org.overturetool.vdmjc.dbgp.DBGPErrorCode;
import org.overturetool.vdmjc.dbgp.DBGPRedirect;
import org.overturetool.vdmjc.dbgp.DBGPStatus;
import org.overturetool.vdmjc.xml.XMLDataNode;
import org.overturetool.vdmjc.xml.XMLNode;
import org.overturetool.vdmjc.xml.XMLOpenTagNode;
import org.overturetool.vdmjc.xml.XMLParser;
import org.overturetool.vdmjc.xml.XMLTagNode;



public class ConnectionThread extends Thread
{
	private final boolean principal;
	private final Socket socket;
	private final BufferedInputStream input;
	private final BufferedOutputStream output;

	private long id = 0;
	private long xid = 0;
	private DBGPStatus status;
	private boolean connected;
	private static boolean trace = false;
	private static boolean quiet = false;
	private static ConnectionThread focus = null;

	public ConnectionThread(ThreadGroup group, Socket conn, boolean principal)
		throws IOException
	{
		super(group, null, "DBGp Connection");

		this.socket = conn;
		this.input = new BufferedInputStream(conn.getInputStream());
		this.output = new BufferedOutputStream(conn.getOutputStream());
		this.principal = principal;
		this.status = DBGPStatus.STARTING;

		setDaemon(true);
	}

	@Override
	public String toString()
	{
		return "Id " + (id == 0 ? "?" : id) + ": " + getStatus();
	}

	@Override
	public long getId()
	{
		return id;
	}

	public DBGPStatus getStatus()
	{
		return status;
	}

	public synchronized void setStatus(DBGPStatus s)
	{
		status = s;
	}

	public static synchronized boolean setTrace()
	{
		trace = !trace;
		return trace;
	}

	public static synchronized boolean setQuiet()
	{
		quiet = !quiet;
		return quiet;
	}

	public static synchronized void setFocus(ConnectionThread f)
	{
		focus = f;
	}

	@Override
	public void run()
	{
		connected = true;
		try
        {
			if (!principal)
			{
				runme();		// Send run command to start new thread
			}

			while (connected)
			{
				receive();		// Blocking
			}
        }
        catch (SocketException e)
        {
    		// Caused by die(), and VDMJ death
        }
        catch (IOException e)
        {
        	CommandLine.message("Connection exception: " + e.getMessage());
        	die();
        }

		setStatus(DBGPStatus.STOPPED);

		if (!principal && !quiet)
		{
			CommandLine.message("Thread stopped: " + this);
		}
	}

	public synchronized void die()
	{
		try
		{
			connected = false;
			socket.close();
		}
		catch (IOException e)
		{
			// ?
		}
	}

	private synchronized void write(String cmd) throws IOException
	{
		if (trace) System.err.println("[" + id + "] " + cmd);	// diags!

		output.write(cmd.getBytes("UTF-8"));
		output.write('\n');
		output.flush();
	}

	private void receive() throws IOException
	{
		// <ascii length> \0 <XML data> \0

		int c = input.read();
		int length = 0;

		while (c >= '0' && c <= '9')
		{
			length = length * 10 + (c - '0');
			c = input.read();
		}

		if (c == -1)
		{
			connected = false;		// End of thread
			return;
		}

		if (c != 0)
		{
			throw new IOException("Malformed DBGp count on " + this);
		}

		byte[] data = new byte[length];
		int offset = 0;
		int remaining = length;
		int retries = 10;
		int done = input.read(data, offset, remaining);

		while (done < remaining && --retries > 0)
		{
			Utils.milliPause(100);
			remaining -= done;
			offset += done;
			done = input.read(data, offset, remaining);
		}

		if (retries == 0)
		{
			throw new IOException("Timeout DBGp reply on thread " + this.id +
				", got [" + new String(data) + "]");
		}

		if (done != remaining)
		{
			throw new IOException("Short DBGp reply on thread " + this.id +
				", got [" + new String(data) + "]");
		}

		if (input.read() != 0)
		{
			throw new IOException("Malformed DBGp terminator on thread " + this.id +
				", got [" + new String(data) + "]");
		}

		process(data);
	}

	private void process(byte[] data) throws IOException
	{
		XMLParser parser = new XMLParser(data);
		XMLNode node = parser.readNode();

		if (trace) System.err.println("[" + id + "] " + node);	// diags!

		try
		{
			XMLTagNode tagnode = (XMLTagNode)node;

			if (tagnode.tag.equals("init"))
			{
				processInit(tagnode);
			}
			else
			{
				processResponse(tagnode);
			}
		}
		catch (Exception e)
		{
			throw new IOException("Unexpected XML response: " + node);
		}
	}

	private void processInit(XMLTagNode tagnode) throws IOException
	{
		String sid = tagnode.getAttr("thread");

		// Either "123" or "123 on <CPU name>" for VDM-RT
		int space = sid.indexOf(' ');

		if (space == -1)
		{
			id = Integer.parseInt(sid);
		}
		else
		{
			id = Integer.parseInt(sid.substring(0, space));
		}

		if (principal)
		{
			redirect("stdout", DBGPRedirect.REDIRECT);
			redirect("stderr", DBGPRedirect.REDIRECT);
		}
		else
		{
			if (!quiet)
			{
				CommandLine.message("New thread: " + this);
			}
		}
	}

	private void processResponse(XMLTagNode msg)
	{
		try
    	{
			String command = msg.getAttr("command");
			String newstatus = msg.getAttr("status");

			if (newstatus != null)
			{
				DBGPStatus news = DBGPStatus.valueOf(newstatus.toUpperCase());

				if (news != null)
				{
					if (status != news)
					{
						setStatus(news);

						if (status == DBGPStatus.BREAK || status == DBGPStatus.STOPPING)
						{
							xcmd_overture_currentline();
						}

						if (this == focus)
						{
							CommandLine.message("");	// Just update prompt
						}
					}
				}
			}
			else if (command != null && command.equals("xcmd_overture_cmd"))
    		{
    			XMLOpenTagNode otn = (XMLOpenTagNode)msg;
				XMLNode child = otn.getChild("error");

				if (child != null)
				{
					XMLOpenTagNode err = (XMLOpenTagNode)child;
					int code = Utils.parseInt(err.getAttr("code"));
					XMLOpenTagNode m = (XMLOpenTagNode)err.getChild("message");
					DBGPErrorCode dbgp = DBGPErrorCode.lookup(code);
					CommandLine.message("[" + dbgp.value + "] " + dbgp + ": " + m.text);
				}
				else
				{
					// All successful xcmds are CDATA:
					XMLDataNode data = (XMLDataNode)otn.children.get(0);
					CommandLine.message(data.cdata);
				}
    		}
			else if (msg.tag.equals("stream"))
    		{
    			XMLOpenTagNode otn = (XMLOpenTagNode)msg;
    			String stream = otn.getAttr("type");
    			XMLDataNode data = (XMLDataNode)otn.children.get(0);
    			String text = new String(Base64.decode(data.cdata));
    			CommandLine.message(stream + ": " + text);
    		}
    		else if (msg.tag.equals("response"))
    		{
    			if (msg instanceof XMLOpenTagNode)
    			{
    				XMLOpenTagNode otn = (XMLOpenTagNode)msg;

    				if (otn.children.isEmpty())		// No content
    				{
    					return;
    				}

    				if (otn.children.size() == 1 &&
    					otn.children.get(0) instanceof XMLDataNode)
    				{
    					// Just one CDATA node, so print the message:
    	    			XMLDataNode data = (XMLDataNode)otn.children.get(0);
    	    			CommandLine.message("[" + this + "] " + data.cdata);
    	    			return;
    				}

    				XMLNode child = otn.getChild("error");

    				if (child != null)
    				{
    					XMLOpenTagNode err = (XMLOpenTagNode)child;
    					int code = Utils.parseInt(err.getAttr("code"));
    					XMLOpenTagNode m = (XMLOpenTagNode)err.getChild("message");
    					DBGPErrorCode dbgp = DBGPErrorCode.lookup(code);
    					setStatus(DBGPStatus.STOPPING);
    					CommandLine.message("[" + this + "] " + dbgp + ": " + m.text);
    					return;
    				}

    				if (command.equals("breakpoint_list"))
    				{
        				child = otn.getChild("breakpoint");
    					XMLOpenTagNode bp = (XMLOpenTagNode)child;
    					String bid = bp.getAttr("id");
       					String uri = bp.getAttr("filename");
       					File file = new File(new URI(uri));
       					String location = bp.getAttr("lineno");
       					String exp = "";

       					if (!bp.children.isEmpty())
       					{
       						bp = (XMLOpenTagNode)bp.getChild("expression");
       						exp = "when " + bp.text;
       					}

       					CommandLine.message("Breakpoint [" + bid + "] set at " + file.getName() + ":" + location + " " + exp);
       					return;
    				}

       				child = otn.getChild("stack");

    				if (child != null)
    				{
    					StringBuilder sb = new StringBuilder();
    					String sep = "";

     					for (XMLNode frame: otn.children)
    					{
           					XMLTagNode stack = (XMLTagNode)frame;
           					int level = Utils.parseInt(stack.getAttr("level"));
           					String uri = stack.getAttr("filename");
           					File file = new File(new URI(uri));
           					String location = stack.getAttr("cmdbegin");

           					sb.append(sep);
           					sb.append("Frame [");
           					sb.append(level);
           					sb.append("] ");
           					sb.append(file.getName());
           					sb.append(" at ");
           					sb.append(location);
           					sep = "\n";
    					}

     					CommandLine.message(sb.toString());
     					return;
    				}

      				child = otn.getChild("property");

    				if (child != null)
    				{
       					StringBuilder sb = new StringBuilder();
       					String sep = "";

     					for (XMLNode prop: otn.children)
    					{
     						sb.append(sep);
     						XMLOpenTagNode p = (XMLOpenTagNode)prop;
     						sb.append(p.getAttr("name"));
     						sb.append(" = ");
     						XMLDataNode data = (XMLDataNode)p.getChild(0);
     						sb.append(new String(Base64.decode(data.cdata), "UTF-8"));
            				sep = "\n";
    					}

     					CommandLine.message(sb.toString());
     					return;
    				}

    				CommandLine.message("Cannot display: " + msg);
    			}
    			else
    			{
    				if (command.equals("breakpoint_set"))
    				{
    					CommandLine.message("Breakpoint [" + msg.getAttr("id") + "] set");
     				}
    				else if (command.equals("breakpoint_remove"))
    				{
    					CommandLine.message("Breakpoint removed");
    				}
    				else if (command.equals("stdout"))
    				{
    					CommandLine.message("Standard output redirected to client");
    				}
    				else if (command.equals("stderr"))
    				{
    					CommandLine.message("Standard error redirected to client");
    				}
    				else
    				{
    					CommandLine.message("Cannot display: " + msg);
    				}
    			}
    		}
    		else
    		{
    			CommandLine.message("Cannot display: " + msg);
    		}
    	}
    	catch (Exception e)
    	{
    		CommandLine.message("Cannot display: " + msg);
    	}
	}

	public void status() throws IOException
	{
		write("status -i " + (++xid));
	}

	public void detach() throws IOException
	{
		write("detach -i " + (++xid));
	}

	public void allstop() throws IOException
	{
		write("stop -i " + (++xid));
	}

	public void redirect(String command, DBGPRedirect option) throws IOException
	{
		write(command + " -i " + (++xid) + " -c " + option.value);
	}

	public void runme() throws IOException
	{
		write("run -i " + (++xid));
	}

	public void step_into() throws IOException
	{
		write("step_into -i " + (++xid));
	}

	public void step_over() throws IOException
	{
		write("step_into -i " + (++xid));
	}

	public void step_out() throws IOException
	{
		write("step_into -i " + (++xid));
	}

	public void expr(String expression) throws IOException
	{
		write("expr -i " + (++xid) + " -- " + Base64.encode(expression));
	}

	public void eval(String expression) throws IOException
	{
		write("eval -i " + (++xid) + " -- " + Base64.encode(expression));
	}

	public void breakpoint_set(File file, int line, String condition)
		throws IOException
	{
		write("breakpoint_set -i " + (++xid) +
			" -t line" +
			" -f " + file.toURI() +
			" -n " + line +
			(condition == null ? "" : " -- " + Base64.encode(condition)));
	}

	public void breakpoint_list() throws IOException
	{
		write("breakpoint_list -i " + (++xid));
	}

	public void breakpoint_remove(int n) throws IOException
    {
		write("breakpoint_remove -i " + (++xid) + " -d " + n);
    }

	public void stack_get() throws IOException
    {
		write("stack_get -i " + (++xid));
    }

	public void context_get(int type, int depth) throws IOException
	{
		write("context_get -i " + (++xid) + " -c " + type + " -d " + depth);
	}

	private void xcmd_overture_cmd(String cmd, String arg) throws IOException
	{
		if (arg == null)
		{
			write("xcmd_overture_cmd -i " + (++xid) + " -c " + cmd);
		}
		else
		{
			write("xcmd_overture_cmd -i " + (++xid) + " -c " + cmd +
				" -- " + Base64.encode(arg));
		}
	}

	public void xcmd_overture_init() throws IOException
	{
		xcmd_overture_cmd("init", null);
	}

	public void xcmd_overture_currentline() throws IOException
	{
		xcmd_overture_cmd("currentline", null);
	}

	public void xcmd_overture_source() throws IOException
	{
		xcmd_overture_cmd("source", null);
	}

	public void xcmd_overture_coverage(File file) throws IOException
	{
		xcmd_overture_cmd("coverage", file.toURI().toString());
	}

	public void xcmd_overture_latex(File file) throws IOException
	{
		xcmd_overture_cmd("latex", file.toURI().toString());
	}

	public void xcmd_overture_latexdoc(File file) throws IOException
	{
		xcmd_overture_cmd("latexdoc", file.toURI().toString());
	}

	public void xcmd_overture_pog(String name) throws IOException
	{
		xcmd_overture_cmd("pog", name);
	}

	public void xcmd_overture_stack() throws IOException
	{
		xcmd_overture_cmd("stack", null);
	}

	public void xcmd_overture_list() throws IOException
	{
		xcmd_overture_cmd("list", null);
	}

	public void xcmd_overture_files() throws IOException
	{
		xcmd_overture_cmd("files", null);
	}

	public void xcmd_overture_classes() throws IOException
	{
		xcmd_overture_cmd("classes", null);
	}

	public void xcmd_overture_modules() throws IOException
	{
		xcmd_overture_cmd("modules", null);
	}

	public void xcmd_overture_default(String name) throws IOException
	{
		xcmd_overture_cmd("default", name);
	}

	public void xcmd_overture_create(String var, String exp) throws IOException
	{
		xcmd_overture_cmd("create", var + " " + exp);
	}

	public void xcmd_overture_trace(File file, int lnum, String display) throws IOException
	{
		xcmd_overture_cmd("trace", file.toURI() + " " + lnum + " " + display);
	}

	public void xcmd_overture_log(String file) throws IOException
	{
		xcmd_overture_cmd("log", file);
	}
}
