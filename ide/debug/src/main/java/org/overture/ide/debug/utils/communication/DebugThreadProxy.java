package org.overture.ide.debug.utils.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.overture.ide.debug.utils.xml.XMLDataNode;
import org.overture.ide.debug.utils.xml.XMLNode;
import org.overture.ide.debug.utils.xml.XMLOpenTagNode;
import org.overture.ide.debug.utils.xml.XMLParser;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overturetool.vdmj.debug.DBGPRedirect;
import org.overturetool.vdmj.util.Base64;

public class DebugThreadProxy
{
	private class DBGPReader extends Thread
	{
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
	IDebugThreadProxyCallback callback = null;
	Integer threadId;
	private int xid = 0;
	Thread dbgpReaderThread;

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
	private void write(String request)
	{
		callback.firePrintMessage(true, "Writing request: " + request);

		try
		{
			output.write(request.getBytes("UTF-8"));
			output.write('\n');
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
			
			callback.fireStarted();

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

		String command = msg.getAttr("command");

		if (command.equals("breakpoint_set"))
		{
			String transaction_id = msg.getAttr("transaction_id");
			Integer tid = Integer.parseInt(transaction_id);
			Integer id = Integer.parseInt(msg.getAttr("id"));

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

		} else if (command.equals("stack_get"))
		{
			XMLOpenTagNode node = (XMLOpenTagNode) msg;
			IStackFrame[] frames = null;
			// fStack = new VdmStackFrame(fThread, node.children, (int)id);
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

}
