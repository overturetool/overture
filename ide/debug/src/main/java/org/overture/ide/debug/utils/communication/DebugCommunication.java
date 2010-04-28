package org.overture.ide.debug.utils.communication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.ui.services.IDisposable;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugTarget;
import org.overture.ide.debug.core.model.VdmThread;
import org.overture.ide.debug.core.model.VdmDebugState.DebugState;
import org.overture.ide.debug.utils.xml.XMLNode;
import org.overture.ide.debug.utils.xml.XMLParser;
import org.overture.ide.debug.utils.xml.XMLTagNode;

public class DebugCommunication implements IDisposable
{

	static private DebugCommunication instance = null;
	private ServerSocket server;
	private boolean keepAlive = true;
	// private List<Socket> sockets;
	private Map<String, VdmDebugTarget> targets = new HashMap<String, VdmDebugTarget>();
	private int sessionId = 0;

	private DebugCommunication() throws IOException
	{
		int portNumber = 9000;// findFreePort();
		// if(portNumber == -1)
		// throw new IOException("Debug communication: no ports available");//very unlikely
		try{
		server = new ServerSocket(portNumber);
		}catch(Exception e)
		{
			server = new ServerSocket(findFreePort());
		}
		// System.out.println("listning on port: " + portNumber);
		// server.setSoTimeout(50000);

		Thread t = new Thread(new ThreadAccept());
		t.setName("DebugCommunication Socket Listener");
		t.start();
	}

	public String getSessionId()
	{
		return Integer.toString(++sessionId);
	}

	static public DebugCommunication getInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new DebugCommunication();
			return instance;
		} else
		{
			return instance;
		}
	}

	public int getPort()
	{
		return this.server.getLocalPort();
	}

	public void registerDebugTarger(String sessionId, VdmDebugTarget target)
			throws DebugException
	{

		if (!targets.containsKey(sessionId))
		{
			targets.put(sessionId, target);
		} else
		{
			throw new DebugException(new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, "Failed to register target: session Id already exists"));
		}
	}

	public void removeSession(String sessionId)
	{
		if (targets.containsKey(sessionId))
			targets.remove(sessionId);
	}

	class ThreadAccept implements Runnable
	{

		public void run()
		{
			while (keepAlive)
			{
				try
				{
					Socket s = server.accept();
					receive(s);

				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private void receive(Socket s) throws IOException
	{
		BufferedInputStream input = new BufferedInputStream(s.getInputStream());
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
			// connected = false; // End of thread
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
			throw new IOException("Timeout DBGp reply on initialization, got ["
					+ new String(data) + "]");
		}

		if (done != remaining)
		{
			throw new IOException("Short DBGp reply on initialization got ["
					+ new String(data) + "]");
		}

		if (input.read() != 0)
		{
			throw new IOException("Malformed DBGp terminator on initialization, got ["
					+ new String(data) + "]");
		}

		XMLParser parser = new XMLParser(data);
		XMLNode node = parser.readNode();

		// if (trace) System.err.println("[" + id + "] " + node); // diags!

		XMLTagNode tagnode = (XMLTagNode) node;

		if (tagnode.tag.equals("init"))
		{

			// System.out.println("Process Init: " + tagnode);
			processInit(tagnode, s);
		} else
		{
			System.err.println("Unexpected tag: " + tagnode.tag);
		}

	}

	private void processInit(XMLTagNode tagnode, Socket s) throws IOException
	{

		String sessionId = tagnode.getAttr("idekey");

		if (targets.containsKey(sessionId))
		{
			try
			{
				String sid = tagnode.getAttr("thread");
				int id = -1;
				String name = sid;
				// Either "123" or "123 on <CPU name>" for VDM-RT
				int space = sid.indexOf(' ');

				if (space == -1)
				{
					id = Integer.parseInt(sid);
				} else
				{
					id = Integer.parseInt(sid.substring(0, space));
				}
				if(id == 1)
				{
					name = "Thread [Main]";
				}
				
				VdmDebugTarget target = targets.get(sessionId);
				
				DebugState initialState =DebugState.Resumed;
				if(target.isTerminated() )
				{
					initialState = DebugState.Terminated;
				}else if(target.isSuspended())
				{
					initialState = DebugState.Suspended;
				}
				
				VdmThread t = new VdmThread(targets.get(sessionId), id, name,sessionId, s,initialState);
				
				target.newThread(t);
				
				try
				{
					t.getProxy().processInit(tagnode);
				} catch (IOException e)
				{
					if (Activator.DEBUG)
					{
						e.printStackTrace();
					}
					throw new DebugException(new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, "Cannot init thread", e));

				}
				
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			System.err.println("Unexpected node: " + tagnode.toString());
		}

	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e)
		{
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
		return -1;
	}

	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	public void disposeTarget(String sessionId)
	{
		if (targets.containsKey(sessionId))
		{
			targets.remove(sessionId);
		}

	}

}
