package org.overture.ide.debug.utils.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.VdmStackFrame;
import org.overture.ide.debug.core.model.VdmThread;
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
		public DBGPReader() {
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
	private int xid = 0;
	private Thread dbgpReaderThread;
	private VdmThread fThread;

	public DebugThreadProxy(Socket socket, String sessionId, Integer threadId,
			IDebugThreadProxyCallback callback) {
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
		write("run -i " + (++xid));

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
		callback.firePrintMessage(true, "Writing request: " + request);

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
		write(command + " -i " + (++xid) + " -c " + option.value);
	}

	public void start()
	{
		try
		{
			input = new BufferedInputStream(fSocket.getInputStream());

			output = new BufferedOutputStream(fSocket.getOutputStream());
			// readerRunner = new ReaderRunnable();
			// new Thread(readerRunner).start();

			redirect("stdout", DBGPRedirect.REDIRECT);
			redirect("stderr", DBGPRedirect.REDIRECT);

			// callback.fireStarted();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dbgpReaderThread.start();
	}

	/*********************************************************************************
	 * '
	 * 
	 * *
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
				callback.firePrintMessage(false, "Process Init: " + tagnode);
				processInit(tagnode);
			} else if (tagnode.tag.equals("response"))
			{
				callback.firePrintMessage(false, "Process Response: " + tagnode);
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
		} else if (command.equals("context_get"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			setResult(transactionId, processContext(node));
		} else if (command.equals("stack_get"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			setResult(transactionId, processStackFrame(node));
			// fStack = new VdmStackFrame(fThread, node.children, (int)id);
		} else if (command.equals("stack_depth"))
		{
			setResult(transactionId,
					Integer.parseInt(((XMLOpenTagNode) msg).text));
			System.out.println("STACK DEPTH = " + msg.toString());
		}

	}

	private void processInit(XMLTagNode tagnode) throws IOException
	{
		String sid = tagnode.getAttr("thread");
		sessionId = tagnode.getAttr("idekey");

		int id = -1;
		// Either "123" or "123 on <CPU name>" for VDM-RT
		int space = sid.indexOf(' ');

		if (space == -1)
		{
			id = Integer.parseInt(sid);
		} else
		{
			id = Integer.parseInt(sid.substring(0, space));
		}

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
			String[] cmdbegin = stackNode.getAttr("cmdbegin").split(":");

			String filename = stackNode.getAttr("filename");
			String type = stackNode.getAttr("type");
			String lineno = stackNode.getAttr("lineno");
			String level = stackNode.getAttr("level");
			VdmStackFrame frame = new VdmStackFrame(null,
					filename,
					Integer.parseInt(cmdbegin[0]),
					Integer.parseInt(cmdbegin[1]),
					Integer.parseInt(lineno),
					Integer.parseInt(level));
			frames.add(frame);
		}

		return frames.toArray(new VdmStackFrame[frames.size()]);
	}

	private VdmVariable[] processContext(XMLOpenTagNode node)
	{
		List<VdmVariable> variables = new Vector<VdmVariable>();
		for (XMLNode prop : node.children)
		{

			XMLOpenTagNode p = (XMLOpenTagNode) prop;
			String name = (p.getAttr("name"));
			String fullname = p.getAttr("fullname");
			String classname = p.getAttr("classname");
			String type = p.getAttr("type");
			XMLDataNode dataNode = (XMLDataNode) p.getChild(0);
			String data = "";
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
			variables.add(new VdmVariable(null, name, type, new VdmValue(null,
					type,
					data)));

		}
		return variables.toArray(new VdmVariable[variables.size()]);
	}

	public int breakpointAdd(int line, String path)
	{
		String breakpoint_set = "breakpoint_set " + " -r 0" + " -t line"
				+ " -s enabled" + " -n " + line + " -i " + (++xid) + " -f "
				+ path;
		write(breakpoint_set);

		return xid;
	}

	public VdmStackFrame[] getStack() throws InterruptedException
	{
		Integer ticket = getNextTicket();
		String command = "stack_get -i " + ticket;
		return (VdmStackFrame[]) request(ticket, command);
	}

	public void setVdmThread(VdmThread t)
	{
		fThread = t;
	}

	public Integer getStackDepth() throws InterruptedException
	{
		Integer ticket = getNextTicket();
		String command = "stack_depth -i " + (ticket);
		return (Integer) request(ticket, command);

	}

	public VdmVariable[] getVariables(int depth) throws InterruptedException
	{
		// int type,
		// int depth
		// write("context_get -i " + (++xid) + " -c " + type + " -d " + depth);

		Integer ticket = getNextTicket();
		String command = "context_get -i " + ticket + " -d " + depth;
		return (VdmVariable[]) request(ticket, command);
	}

}
