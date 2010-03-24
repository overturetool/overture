package org.overture.ide.debug.core.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.utils.communication.DebugCommunication;
import org.overture.ide.debug.utils.xml.XMLDataNode;
import org.overture.ide.debug.utils.xml.XMLNode;
import org.overture.ide.debug.utils.xml.XMLOpenTagNode;
import org.overture.ide.debug.utils.xml.XMLParser;
import org.overture.ide.debug.utils.xml.XMLTagNode;
import org.overturetool.vdmj.debug.DBGPRedirect;
import org.overturetool.vdmj.util.Base64;

public class VdmDebugTarget extends VdmDebugElement implements IDebugTarget
{

	private ILaunch fLaunch;
	private IProcess fProcess;
	private List<IThread> fThreads;
	private VdmThread fThread;
	private boolean fTerminated = false;
	private boolean fSuspended = false;
	private boolean fDisconected = false;
	private int xid = 0;
	private Socket fSocket;

	// read runner
	private ReaderRunnable readerRunner = null;
	private BufferedInputStream input = null;
	private boolean connected = true;
	private boolean carryOn = true;
	private String readLn = null;
	private BufferedOutputStream output;
	private PrintWriter fRequestWriter;
	private BufferedReader fRequestReader;
	private String sessionId;

	public VdmDebugTarget(ILaunch launch)
			throws DebugException {
		super(null);
		fTarget = this;
		fLaunch = launch;
		

		fThread = new VdmThread(this, 1); //TODO: assuming main thread is number 1;
		fThread.setName("Thread [Main]");
		fThreads = new ArrayList<IThread>();
		fThreads.add(fThread);

		connected = true;

		

		DebugPlugin.getDefault()
				.getBreakpointManager()
				.addBreakpointListener(this);
		

	}
	
	public void setProcess(IProcess process){
		fProcess = process;
	}

	public String getName() throws DebugException
	{
		return "VdmVM";
	}

	public IProcess getProcess()
	{
		return fProcess;
	}

	public ILaunch getLaunch()
	{
		return this.fLaunch;
	}

	public IThread[] getThreads() throws DebugException
	{
		IThread[] result = new IThread[fThreads.size()];
		System.arraycopy(fThreads.toArray(), 0, result, 0, fThreads.size());
		return result;
	}

	public boolean hasThreads() throws DebugException
	{
		return fThreads.size() > 0;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canTerminate()
	{
		return !fTerminated;
	}

	public boolean isTerminated()
	{
		return fTerminated;
	}

	public void terminate() throws DebugException
	{
		
		try
		{
			DebugCommunication.getInstance().disposeTarget(sessionId);
			if(!fSocket.isClosed())
				this.fSocket.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Do dispose of objects here
		fTerminated = true;
	}

	public boolean canResume()
	{
		return (fSuspended && !fTerminated);
	}

	public boolean canSuspend()
	{
		return !fSuspended && !fTerminated;
	}

	public boolean isSuspended()
	{
		return fSuspended;
	}

	public void resume() throws DebugException
	{

		write("run -i " + (++xid));

		fireResumeEvent(DebugEvent.RESUME);
		fSuspended = false;
	}

	/**
	 * 
	 * @param detail
	 *            - see DebugEvent detail constants;
	 * @throws DebugException
	 */
	public void suspend() throws DebugException
	{
		fSuspended = true;
		fireSuspendEvent(DebugEvent.SUSPEND);

	}

	public void breakpointAdded(IBreakpoint breakpoint)
	{
		// TODO Auto-generated method stub

	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		// TODO Auto-generated method stub

	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		// TODO Auto-generated method stub

	}

	public boolean canDisconnect()
	{
		return false;
	}

	public void disconnect() throws DebugException
	{
		// TODO Disconnect here
		fDisconected = true;
	}

	public boolean isDisconnected()
	{
		return fDisconected;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException
	{
		return null;
	}

	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint manager.
	 */
	private void installDeferredBreakpoints()
	{
		IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager()
				.getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++)
		{
			breakpointAdded(breakpoints[i]);
		}
	}

	private void started()
	{
		fireCreationEvent();
		installDeferredBreakpoints();
		try
		{
			resume();
		} catch (DebugException e)
		{
		}

	}

	private void suspended(int breakpoint)
	{
		// TODO Auto-generated method stub

	}

	private void terminated() 
	{
		
		try {
			terminate();
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

	class ReaderRunnable implements Runnable
	{

		public void run()
		{

			while (isConnected())
			{
				try
				{
					receive();
				} catch (IOException e)
				{
					
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

		public BufferedInputStream getInput()
		{
			return input;
		}

		public boolean isConnected()
		{
			return connected;
		}

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
			throw new IOException("Timeout DBGp reply on thread " + ((VdmThread)fThread).getId()
					+ ", got [" + new String(data) + "]");
		}

		if (done != remaining)
		{
			throw new IOException("Short DBGp reply on thread " + ((VdmThread)fThread).getId()
					+ ", got [" + new String(data) + "]");
		}

		if (input.read() != 0)
		{
			throw new IOException("Malformed DBGp terminator on thread "
					+ ((VdmThread)fThread).getId() + ", got [" + new String(data) + "]");
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
				System.out.println("Process Init: " + tagnode);
				processInit(tagnode);
			} else if (tagnode.tag.equals("response"))
			{
				System.out.println("Process Response: " + tagnode);
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
			System.out.println(stream + ": " + text);
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
				breakpointHit("event");
			} else if (newstatus.equals("stopped"))
			{
				terminated();
				
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
		
		int id =-1;
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

		started();

	}

	public void redirect(String command, DBGPRedirect option)
			throws IOException
	{
		write(command + " -i " + (++xid) + " -c " + option.value);
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
		System.out.println("Writing request: " + request);

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

	/**
	 * Notification a breakpoint was encountered. Determine which breakpoint was hit and fire a suspend event.
	 * 
	 * @param event
	 *            debug event
	 */
	private void breakpointHit(String event)
	{
		// determine which breakpoint was hit, and set the thread's breakpoint
		int lastSpace = event.lastIndexOf(' ');
		if (lastSpace > 0)
		{
			String line = event.substring(lastSpace + 1);
			int lineNumber = Integer.parseInt(line);
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager()
					.getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++)
			{
				IBreakpoint breakpoint = breakpoints[i];
				if (supportsBreakpoint(breakpoint))
				{
					if (breakpoint instanceof ILineBreakpoint)
					{
						ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoint;
						try
						{
							if (lineBreakpoint.getLineNumber() == lineNumber)
							{
								// TODO: is this needed ?
								// fThread.setBreakpoints(new IBreakpoint[]{breakpoint});
								break;
							}
						} catch (CoreException e)
						{
						}
					}
				}
			}
		}
		suspended(DebugEvent.BREAKPOINT);
	}

	public void newThread(XMLTagNode tagnode, Socket s) throws DebugException {
		String sid = tagnode.getAttr("thread");
		int id =-1;
		// Either "123" or "123 on <CPU name>" for VDM-RT
		int space = sid.indexOf(' ');

		if (space == -1)
		{
			id = Integer.parseInt(sid);
		} else
		{
			id = Integer.parseInt(sid.substring(0, space));
		}

		
		if(id == 1){ //Assuming 1 is main thread;
			try
			{
				fSocket = s;
				fRequestWriter = new PrintWriter(fSocket.getOutputStream());
				// fRequestReader = new BufferedReader(new InputStreamReader(fRequestSocket.getInputStream()));
				input = new BufferedInputStream(fSocket.getInputStream());
				output = new BufferedOutputStream(fSocket.getOutputStream());
				readerRunner = new ReaderRunnable();
				new Thread(readerRunner).start();
				
			} catch (UnknownHostException e)
			{
				abort("Unable to connect to Vdm VM", e);
			} catch (IOException e)
			{
				abort("Unable to connect to Vdm VM", e);
			}
		}
		try {
			redirect("stdout", DBGPRedirect.REDIRECT);
			redirect("stderr", DBGPRedirect.REDIRECT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		started();
		
	}

}
