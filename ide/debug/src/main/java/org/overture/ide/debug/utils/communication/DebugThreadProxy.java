package org.overture.ide.debug.utils.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.model.VdmGroupValue;
import org.overture.ide.debug.core.model.VdmLineBreakpoint;
import org.overture.ide.debug.core.model.VdmMultiValue;
import org.overture.ide.debug.core.model.VdmSimpleValue;
import org.overture.ide.debug.core.model.VdmStackFrame;
import org.overture.ide.debug.core.model.VdmValue;
import org.overture.ide.debug.core.model.VdmVariable;
import org.overture.ide.debug.utils.xml.XMLDataNode;
import org.overture.ide.debug.utils.xml.XMLNode;
import org.overture.ide.debug.utils.xml.XMLOpenTagNode;
import org.overture.ide.debug.utils.xml.XMLParser;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overturetool.vdmj.debug.DBGPRedirect;
import org.overturetool.vdmj.util.Base64;

public class DebugThreadProxy extends AsyncCaller
{
	private class DBGPReader extends Thread
	{
		public DBGPReader()
		{
			setDaemon(true);
		}

		@Override
		public void run()
		{
			while (isConnected())
			{
				try
				{
					receive();
				} catch (IOException e)
				{
					// e.printStackTrace();
					connected = false;
				}
			}
			try
			{
				input.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Reader runner closing socket");
			}
		}
	}

	private Socket fSocket;
	private String sessionId;
	private BufferedOutputStream output;
	private BufferedInputStream input = null;
	private boolean connected;
	private IDebugThreadProxyCallback callback = null;
	private Integer threadId;
	private Thread dbgpReaderThread;

	public DebugThreadProxy(Socket socket, String sessionId, Integer threadId,
			IDebugThreadProxyCallback callback)
	{
		this.fSocket = socket;
		this.sessionId = sessionId;
		this.callback = callback;
		this.threadId = threadId;
		this.dbgpReaderThread = new DBGPReader();
		this.connected = true;
	}

	public void terminate()
	{
		try
		{
			DebugCommunication.getInstance().disposeTarget(sessionId);
			if (!fSocket.isClosed())
			{
				this.fSocket.close();
			}
		} catch (IOException e)
		{
			// Ok, socket is closed
		}
	}

	public void resume()
	{
		Integer ticket = getNextTicket();
		write("run -i " + ticket);

	}

	/**
	 * Sends a request to the PDA VM and waits for an OK.
	 * 
	 * @param request
	 *            debug command
	 * @throws DebugException
	 *             if the request fails
	 */
	@Override
	protected void write(String request)
	{
		callback.firePrintMessage(true, "Request:  " + request);

		try
		{
			output.write(request.getBytes("UTF-8"));
			output.write('\n');
			if (!fSocket.isClosed())
				output.flush();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void redirect(String command, DBGPRedirect option)
	{
		Integer ticket = getNextTicket();
		write(command + " -i " + ticket + " -c " + option.value);
	}

	public void start()
	{
		try
		{
			input = new BufferedInputStream(fSocket.getInputStream());

			output = new BufferedOutputStream(fSocket.getOutputStream());
			// readerRunner = new ReaderRunnable();
			// new Thread(readerRunner).start();

			// redirect("stdout", DBGPRedirect.REDIRECT);
			// redirect("stderr", DBGPRedirect.REDIRECT);

			// callback.fireStarted();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbgpReaderThread.start();
	}

	/*********************************************************************************
	 * ' *
	 *******************************************************************************/

	public BufferedInputStream getInput()
	{
		return input;
	}

	public boolean isConnected()
	{
		return connected;
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
			connected = false; // End of thread
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
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			remaining -= done;
			offset += done;
			done = input.read(data, offset, remaining);
		}

		if (retries == 0)
		{
			throw new IOException("Timeout DBGp reply on thread " + threadId
					+ ", got [" + new String(data) + "]");
		}

		if (done != remaining)
		{
			throw new IOException("Short DBGp reply on thread " + threadId
					+ ", got [" + new String(data) + "]");
		}

		if (input.read() != 0)
		{
			throw new IOException("Malformed DBGp terminator on thread "
					+ threadId + ", got [" + new String(data) + "]");
		}

		process(data);
	}

	private void process(byte[] data) throws IOException
	{
		XMLParser parser = new XMLParser(data);
		XMLNode node = parser.readNode();

		// if (trace) System.err.println("[" + id + "] " + node); // diags!

		try
		{
			XMLTagNode tagnode = (XMLTagNode) node;

			if (tagnode.tag.equals("init"))
			{
				callback.firePrintMessage(false, "Res Init: " + tagnode);
				processInit(tagnode);
			} else if (tagnode.tag.equals("response"))
			{
				callback.firePrintMessage(false, "Response: " + tagnode);
				processResponse(tagnode);
			} else if (tagnode.tag.equals("stream"))
			{

				processStream(tagnode);
			}
		} catch (Exception e)
		{
			throw new IOException("Unexpected XML response: " + node);
		}
	}

	private void processStream(XMLTagNode msg)
	{

		XMLOpenTagNode otn = (XMLOpenTagNode) msg;
		String stream = otn.getAttr("type");
		XMLDataNode data = (XMLDataNode) otn.children.get(0);
		String text;
		try
		{
			text = new String(Base64.decode(data.cdata));
			callback.firePrintMessage(false, stream + ": " + text);
			if (stream.equals("stdout"))
			{
				callback.firePrintOut(text);
			} else if (stream.equals("stderr"))
			{
				callback.firePrintErr(text);
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void processResponse(XMLTagNode msg)
	{
		String transaction_id = msg.getAttr("transaction_id");
		Integer transactionId = Integer.parseInt(transaction_id);
		String command = msg.getAttr("command");

		if (command.equals("breakpoint_set"))
		{

			Integer tid = Integer.parseInt(transaction_id);
			Integer id = Integer.parseInt(msg.getAttr("id"));

			callback.fireBreakpointSet(tid, id);
			// synchronized (breakpointMap) {
			// if(breakpointMap.containsKey(tid))
			// {
			//					
			// VdmLineBreakpoint bp = breakpointMap.get(tid);
			// bp.setId(id);
			// breakpointMap.remove(tid);
			// }
			// }
		} else if (command.equals("run"))
		{
			String newstatus = msg.getAttr("status");
			if (newstatus.equals("break"))
			{
				callback.fireBreakpointHit();
				// breakpointHit("event");
			} else if (newstatus.equals("stopped"))
			{
				callback.fireStopped();// terminated();

			}
		} else if (command.equals("step_over"))
		{
			String newstatus = msg.getAttr("status");
			if (newstatus.equals("break"))
			{
				callback.suspended(IDebugThreadProxyCallback.STEP_OVER);
			}else if (newstatus.equals("stopped"))
			{
				callback.fireStopped();// terminated();

			}
		}else if (command.equals("step_into"))
		{
			String newstatus = msg.getAttr("status");
			if (newstatus.equals("break"))
			{
				callback.suspended(IDebugThreadProxyCallback.STEP_INTO);
			}else if (newstatus.equals("stopped"))
			{
				callback.fireStopped();// terminated();

			}
		}else if (command.equals("step_out"))
		{
			String newstatus = msg.getAttr("status");
			if (newstatus.equals("break"))
			{
				callback.suspended(IDebugThreadProxyCallback.STEP_RETURN);
			}else if (newstatus.equals("stopped"))
			{
				callback.fireStopped();// terminated();

			}
		}
		else if (command.equals("context_get")
				|| command.equals("property_get"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			setResult(transactionId, processContext(node));
		} else if (command.equals("context_names"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			setResult(transactionId, processContextNames(node));
		} else if (command.equals("stack_get"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			setResult(transactionId, processStackFrame(node));
			// fStack = new VdmStackFrame(fThread, node.children, (int)id);
		} else if (command.equals("stack_depth"))
		{
			setResult(transactionId, Integer.parseInt(((XMLOpenTagNode) msg).text));
			// System.out.println("STACK DEPTH = " + msg.toString());
		}

	}

	public void processInit(XMLTagNode tagnode) throws IOException
	{
		callback.firePrintMessage(false, "Process init: " + tagnode.toString());
		// String sid = tagnode.getAttr("thread");
		sessionId = tagnode.getAttr("idekey");

		// int id = -1;
		// // Either "123" or "123 on <CPU name>" for VDM-RT
		// int space = sid.indexOf(' ');
		//
		// if (space == -1)
		// {
		// id = Integer.parseInt(sid);
		// } else
		// {
		// id = Integer.parseInt(sid.substring(0, space));
		// }

		redirect("stdout", DBGPRedirect.REDIRECT);
		redirect("stderr", DBGPRedirect.REDIRECT);

		callback.fireStarted();

	}

	private VdmStackFrame[] processStackFrame(XMLOpenTagNode node)
	{
		List<VdmStackFrame> frames = new Vector<VdmStackFrame>();

		for (XMLNode n : node.children)
		{
			XMLTagNode stackNode = (XMLTagNode) n;
			Integer lineNumber = 0;
			Integer charStart = -1;
			Integer charEnd = -1;
			String where = "";
			boolean nameIsFileUri = false;

			if (stackNode.getAttr("cmdbegin") != null)
			{
				String[] cmdBegin = stackNode.getAttr("cmdbegin").split(":");
				if (cmdBegin.length > 1)
				{
					charStart = Integer.parseInt(cmdBegin[1]);
				}
			}

			if (stackNode.getAttr("cmdend") != null)
			{
				String[] cmdEnd = stackNode.getAttr("cmdend").split(":");
				if (cmdEnd.length > 1)
				{
					charEnd = Integer.parseInt(cmdEnd[1]);
				}
			}

			if (stackNode.getAttr("where") != null)
			{
				where = stackNode.getAttr("´where");
			}

			String filename = stackNode.getAttr("filename");
			nameIsFileUri = stackNode.getAttr("type").equalsIgnoreCase("file");

			lineNumber = Integer.parseInt(stackNode.getAttr("lineno"));
			String level = stackNode.getAttr("level");
			VdmStackFrame frame = new VdmStackFrame(null, filename, nameIsFileUri, charStart, charEnd, lineNumber, Integer.parseInt(level), where);
			frames.add(frame);
		}

		return frames.toArray(new VdmStackFrame[frames.size()]);
	}

	private VdmVariable[] processContext(XMLOpenTagNode node)
	{
		List<VdmVariable> variables = new Vector<VdmVariable>();
		for (XMLNode prop : node.children)
		{
			variables.add(processContextNode((XMLOpenTagNode) prop));
		}
		return variables.toArray(new VdmVariable[variables.size()]);
	}

	private VdmVariable processContextNode(XMLOpenTagNode node)
	{
		XMLOpenTagNode p = node;
		String name = (p.getAttr("name"));
		// String fullname = p.getAttr("fullname");
		// String classname = p.getAttr("classname");
		String type = p.getAttr("type");
		String key = p.getAttr("key");
		boolean childern = p.getAttr("children") != null
				&& p.getAttr("children").equals("1");
		String data = "";
		Integer page = 0;
		if (p.getAttr("page") != null)
		{
			page = Integer.parseInt(p.getAttr("page"));
		}
		Integer numChildren = 0;
		if (p.getAttr("numchildren") != null)
		{
			numChildren = Integer.parseInt(p.getAttr("numchildren"));
		}
		Integer pageSize = 0;
		if (p.getAttr("pagesize") != null)
		{
			pageSize = Integer.parseInt(p.getAttr("pagesize"));
		}
		List<VdmVariable> childVariables = new Vector<VdmVariable>();

		for (XMLNode child : p.children)
		{
			if (child instanceof XMLDataNode)
			{
				XMLDataNode dataNode = (XMLDataNode) child;

				try
				{
					data = (new String(Base64.decode(dataNode.cdata), "UTF-8"));

				} catch (UnsupportedEncodingException e)
				{
					data = "DECODING FAILD";
					e.printStackTrace();
				} catch (Exception e)
				{
					data = "DECODING FAILD";
					e.printStackTrace();
				}
			} else if (child instanceof XMLOpenTagNode)
			{
				childVariables.add(processContextNode((XMLOpenTagNode) child));
			}
		}

		VdmValue vdmValue = null;

		if (!childern)
		{
			vdmValue = new VdmSimpleValue(type, data);
		} else
		{
			VdmVariable[] childs = null;
			if (childVariables.size() > 0)
			{
				childs = childVariables.toArray(new VdmVariable[childVariables.size()]);
			}

			if (numChildren > pageSize)
			{
				vdmValue = new VdmGroupValue(type, type, key, page, pageSize, numChildren, childs);

			} else
			{
				vdmValue = new VdmMultiValue(type, data, key, page, childs);
			}

		}

		return (new VdmVariable(null, name, type, vdmValue));

	}

	private Map<String, Integer> processContextNames(XMLOpenTagNode node)
	{
		Map<String, Integer> names = new Hashtable<String, Integer>();
		for (XMLNode prop : node.children)
		{
			XMLTagNode p = (XMLTagNode) prop;
			names.put(p.getAttr("name"), Integer.parseInt(p.getAttr("id")));
		}
		return names;
	}

	public int breakpointAdd(int line, String path)
	{

		Integer ticket = getNextTicket();
		String breakpoint_set = "breakpoint_set " + " -r 0" + " -t line"
				+ " -s enabled" + " -n " + line + " -i " + ticket + " -f "
				+ path;
		write(breakpoint_set);

		return ticket;
	}

	public VdmStackFrame[] getStack() throws SocketTimeoutException
	{
		Integer ticket = getNextTicket();
		String command = "stack_get -i " + ticket;
		return (VdmStackFrame[]) request(ticket, command);
	}

	public Integer getStackDepth() throws SocketTimeoutException
	{
		Integer ticket = getNextTicket();
		String command = "stack_depth -i " + (ticket);
		return (Integer) request(ticket, command);

	}

	public VdmVariable[] getVariables(int depth, int contextId)
			throws SocketTimeoutException
	{
		// int type,
		// int depth
		// write("context_get -i " + (++xid) + " -c " + type + " -d " + depth);

		Integer ticket = getNextTicket();
		String command = "context_get -i " + ticket + " -d " + depth + " -c "
				+ contextId;
		return (VdmVariable[]) request(ticket, command);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getContextNames() throws SocketTimeoutException
	{
		Integer ticket = getNextTicket();
		String command = "context_names -i " + ticket;// + " -d " + depth;
		return (Map<String, Integer>) request(ticket, command);

	}

	public VdmVariable[] getVariables(int stackDepth, String propertyLongName,
			String key, Integer page) throws SocketTimeoutException
	{
		Integer ticket = getNextTicket();
		String command = "property_get -i " + ticket + " -d " + stackDepth
				+ " -n " + propertyLongName + " -p " + page;
		if (key != null && key.length() > 0)
		{
			command += " -k " + key;
		}
		return (VdmVariable[]) request(ticket, command);

	}

	public void detach() throws IOException
	{
		Integer ticket = getNextTicket();
		write("detach -i " + ticket);
	}

	public void allstop() throws IOException
	{
		Integer ticket = getNextTicket();
		write("stop -i " + ticket);
	}

	public void runme() throws IOException
	{
		write("run -i " + (getNextTicket()));
	}

	public void step_into() throws IOException
	{
		write("step_into -i " + (getNextTicket()));
	}

	public void step_over() throws IOException
	{
		write("step_over -i " + (getNextTicket()));
	}

	public void step_out() throws IOException
	{
		write("step_out -i " + (getNextTicket()));
	}

	public void breakpointRemove(VdmLineBreakpoint breakpoint)
	{
		Integer ticket = getNextTicket();
		write("breakpoint_remove -i " + ticket + " -d "
				+ ((VdmLineBreakpoint) breakpoint).getId());
	}

	public void shutdown() throws IOException
	{
		if (!fSocket.isClosed())
		{
			fSocket.close();
		}

	}

	public int breakpointAdd(IBreakpoint breakpoint)
	{
		StringBuffer buf = new StringBuffer();
		Integer ticket = getNextTicket();
		buf.append("breakpoint_set ");
		buf.append("-i " + ticket);
		// Boolean value indicating if this breakpoint is temporary. [optional, defaults to false]
		buf.append(" -r 0 ");
		// STATE breakpoint state [optional, defaults to "enabled"]
		buf.append("-s enabled ");

		if (breakpoint instanceof VdmLineBreakpoint)
		{
			VdmLineBreakpoint lineBreakpoint = (VdmLineBreakpoint) breakpoint;
			int line;
			try
			{
				line = lineBreakpoint.getLineNumber();
				String path = lineBreakpoint.getFile().toURI().toASCIIString();

				// the line number (lineno) of the breakpoint [optional]
				buf.append("-n " + line + " ");
				// he filename to which the breakpoint belongs [optional]
				buf.append("-f " + path + " ");

				if (lineBreakpoint.getHitCount() > 0)
				{
					buf.append("-h " + lineBreakpoint.getHitCount() + " ");
				}

				if (lineBreakpoint.isConditionEnabled())
				{
					// breakpoint type
					buf.append("-t conditional ");
					String condition = lineBreakpoint.getCondition();
					// buf.append("-- base64(" + Base64.encode(condition.getBytes()) + ") ");
					buf.append("-- " + Base64.encode(condition.getBytes()));
				} else
				{
					// breakpoint type
					buf.append("-t line ");
				}

			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// String breakpoint_set = "breakpoint_set " + " -r 0" + " -t line"
		// + " -s enabled" + " -n " + line + " -i " + (++xid) + " -f "
		// + path;
		// if(Activator.DEBUG){
		// System.out.println(buf.toString());
		// }
		write(buf.toString());

		return ticket;
	}

	// public void expr(String expression) throws IOException
	// {
	// write("expr -i " + (++xid) + " -- " + Base64.encode(expression));
	// }
	//
	// public void eval(String expression) throws IOException
	// {
	// write("eval -i " + (++xid) + " -- " + Base64.encode(expression));
	// }

}
