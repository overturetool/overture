/*
 * #%~
 * Combinatorial Testing Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ct.ctruntime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.utils.TraceXmlWrapper;
import org.overture.interpreter.VDMJ;
import org.overture.interpreter.VDMPP;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.VDMSL;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTTextLogger;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.SourceFile;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.traces.TraceReductionType;
import org.overture.interpreter.util.ExitStatus;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.LexTokenReader;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.util.Base64;

public class TraceRunnerMain implements IProgressMonitor
{
	public static boolean USE_SYSTEM_EXIT = true;
	private final static boolean DEBUG = false;
	
	protected final String host;
	protected final int port;
	protected final String ideKey;
	protected final String moduleName;
	protected final String traceName;
	protected final File traceFolder;
	protected Socket socket;
	protected InputStream input;
	protected OutputStream output;
	protected final Interpreter interpreter;

	protected int sessionId = 0;
	protected String transaction = "";
	protected byte separator = '\0';

	protected boolean connected = false;

	protected boolean completed = false;

	float subset = 1.0F;
	TraceReductionType reductionType = TraceReductionType.NONE;
	long seed = 999;

	public TraceRunnerMain(String host, int port, String ideKey,
			Interpreter interpreter, String moduleName, String traceName,
			File traceFolder, float subset,
			TraceReductionType traceReductionType, long seed)
	{
		this.host = host;
		this.port = port;
		this.ideKey = ideKey;
		this.moduleName = moduleName;
		this.interpreter = interpreter;
		this.traceName = traceName;
		this.traceFolder = traceFolder;

		this.seed = seed;
		this.reductionType = traceReductionType;
		this.subset = subset;
	}

	/**
	 * @param args
	 *            the args
	 */
	public static void main(String[] args)
	{
		Settings.usingDBGP = false;

		String host = null;
		int port = -1;
		String ideKey = null;
		Settings.dialect = null;
		String moduleName = null;
		List<File> files = new ArrayList<File>();
		List<String> largs = Arrays.asList(args);
		VDMJ controller = null;
		boolean warnings = true;
		boolean quiet = false;
		String logfile = null;
		boolean expBase64 = false;
		boolean traceNameBase64 = false;
		File coverage = null;
		String defaultName = null;
		String traceName = null;
		String traceReductionPattern = null;
		// String remoteName = null;
		// Class<RemoteControl> remoteClass = null;
		File traceFolder = null;

		Properties.init(); // Read properties file, if any

		Properties.parser_tabstop = 1;

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
					moduleName = i.next();
				} else
				{
					usage("-e option requires an expression");
				}
			} else if (arg.equals("-e64"))
			{
				if (i.hasNext())
				{
					moduleName = i.next();
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
			}
			// else if (arg.equals("-remote"))
			// {
			// if (i.hasNext())
			// {
			// remoteName = i.next();
			// }
			// else
			// {
			// usage("-remote option requires a Java classname");
			// }
			// }
			else if (arg.equals("-t"))
			{
				if (i.hasNext())
				{
					traceName = i.next();
				} else
				{
					usage("-t option requires a Trace Name");
				}
			} else if (arg.equals("-t64"))
			{
				if (i.hasNext())
				{
					traceName = i.next();
					traceNameBase64 = true;
				} else
				{
					usage("-t option requires a Trace Name");
				}
			} else if (arg.equals("-tracefolder"))
			{
				if (i.hasNext())
				{
					try
					{
						traceFolder = new File(new URI(i.next()));

						if (!traceFolder.isDirectory())
						{
							usage("Tracefolder location is not a directory");
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
					usage("-tracefolder option requires a directory name");
				}
			} else if (arg.equals("-traceReduction"))
			{
				if (i.hasNext())
				{
					try
					{
						traceReductionPattern = i.next();
					} catch (IllegalArgumentException e)
					{
						usage(e.getMessage() + ": " + arg);
					}
				} else
				{
					usage("-traceReduction option requires a pattern");
				}
			} else if (arg.equals("-consoleName"))
			{
				if (i.hasNext())
				{
					LexTokenReader.consoleFileName = i.next();
				} else
				{
					usage("-consoleName option requires a console name");
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

		if (host == null || port == -1 || controller == null || ideKey == null
				|| moduleName == null || Settings.dialect == null
				|| traceFolder == null || files.isEmpty())
		{
			usage("Missing mandatory arguments");
		}

		if (Settings.dialect != Dialect.VDM_RT && logfile != null)
		{
			usage("-log can only be used with -vdmrt");
		}

		if (expBase64)
		{
			try
			{
				byte[] bytes = Base64.decode(moduleName);
				moduleName = new String(bytes, VDMJ.filecharset);
			} catch (Exception e)
			{
				usage("Malformed -e64 base64 expression");
			}
		}
		if (traceNameBase64)
		{
			try
			{
				byte[] bytes = Base64.decode(traceName);
				traceName = new String(bytes, VDMJ.filecharset);
			} catch (Exception e)
			{
				usage("Malformed -t64 base64 trace name");
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

		// if (remoteName != null)
		// {
		// try
		// {
		// Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(remoteName);
		// remoteClass = (Class<RemoteControl>)cls;
		// }
		// catch (ClassNotFoundException e)
		// {
		// usage("Cannot locate " + remoteName + " on the CLASSPATH");
		// }
		// }

		controller.setWarnings(warnings);
		controller.setQuiet(quiet);
		Console.disableStdout();

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

					Interpreter i = controller.getInterpreter();

					if (defaultName != null)
					{
						i.setDefaultName(defaultName);
					}

					// RemoteControl r emote =
					// (remoteClass == null) ? null : remoteClass.newInstance();

					// new ConnectionListener(port).start();

					// String[] parts = traceReductionPattern.split("\\s+");
					// int testNo = 0;
					float subset = 1.0F;
					TraceReductionType reductionType = TraceReductionType.NONE;
					long seed = 999;
					// {subset,reduction,seed}

					if (traceReductionPattern != null
							&& traceReductionPattern.startsWith("{"))
					{
						try
						{
							String settings = traceReductionPattern;
							String[] tmp = settings.substring(1, settings.length() - 1).split(",");
							if (tmp.length == 3)
							{
								subset = Float.parseFloat(tmp[0]);
								reductionType = TraceReductionType.valueOf(tmp[1]);
								seed = Long.parseLong(tmp[2]);
							}
						} catch (NumberFormatException e)
						{
							usage(traceReductionPattern
									+ " <name> [test number]");
							return;
						}
					}

					TraceRunnerMain runner = new TraceRunnerMain(host, port, ideKey, i, moduleName, traceName, traceFolder, subset, reductionType, seed);
					runner.startup();

					if (coverage != null)
					{
						writeCoverage(i, coverage);
					}

					RTLogger.dump(true);

					// runner.progressTerminating();
					exit(0);
				} catch (ContextException e)
				{
					System.err.println("Initialization: " + e);
					e.ctxt.printStackTrace(Console.out, true);
					RTLogger.dump(true);
					exit(3);
				} catch (ValueException e)
				{
					System.err.println("Initialization: " + e);
					e.ctxt.printStackTrace(Console.out, true);
					RTLogger.dump(true);
					exit(3);
				} catch (Exception e)

				{
					System.err.println("Initialization: " + e);
					e.printStackTrace();
					RTLogger.dump(true);
					exit(3);
				}
			} else
			{
				final PrintWriter out = new PrintWriter(System.err);
				TypeChecker.printErrors(out);
				out.flush();
				exit(2);
			}
		} else
		{
			exit(1);
		}

	}

	private static void exit(int code)
	{
		if (USE_SYSTEM_EXIT)
		{
			System.exit(code);
		}
	}

	private void startup() throws Exception
	{
		connect();
	}

	protected static void usage(String string)
	{
		System.err.println(string);
		System.err.println("Usage: -h <host> -p <port> -k <ide key> <-vdmpp|-vdmsl|-vdmrt>"
				+ " -e <expression> | -e64 <base64 expression>"
				+ " [-w] [-q] [-log <logfile URL>] [-c <charset>] [-r <release>]"
				+ " [-coverage <dir URL>] [-default64 <base64 name>]"
				+ " [-remote <class>] [-consoleName <console>] {<filename URLs>}");

		System.exit(1);
	}

	protected static String validateCharset(String cs)
	{
		if (!Charset.isSupported(cs))
		{
			System.err.println("Charset " + cs + " is not supported\n");
			System.err.println("Available charsets:");
			System.err.println("Default = " + Charset.defaultCharset());
			Map<String, Charset> available = Charset.availableCharsets();

			for (String name : available.keySet())
			{
				System.err.println(name + " " + available.get(name).aliases());
			}

			System.err.println("");
			usage("Charset " + cs + " is not supported");
		}

		return cs;
	}

	protected static void writeCoverage(Interpreter interpreter, File coverage)
			throws IOException
	{
		for (File f : interpreter.getSourceFiles())
		{
			SourceFile source = interpreter.getSourceFile(f);

			File data = new File(coverage.getPath() + File.separator
					+ f.getName() + ".covtbl");
			PrintWriter pw = new PrintWriter(data);
			source.writeCoverage(pw);
			pw.close();
		}
	}

	protected void connect() throws Exception
	{
		if (!connected)
		{
			if (port > 0)
			{
				if (DEBUG)
				{
					System.out.println("Trying to connect to CT IDE");
				}
				
				try
				{
					InetAddress server = InetAddress.getByName(host);
					socket = new Socket(server, port);
					input = socket.getInputStream();
					output = socket.getOutputStream();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			} else
			{
				System.err.println("Something wrong no port");
				socket = null;
				input = System.in;
				output = System.out;
				separator = ' ';
			}

			connected = true;
			init();
			run(); // New threads wait for a "run -i"
		}
	}

	private void run() throws Exception
	{
		Thread t = new Thread(new Runnable()
		{

			public void run()
			{
				String tmp = "";
				while (input != null && !socket.isClosed())
				{
					int b;
					try
					{
						b = input.read();
						if (b == -1)
						{

						} else
						{
							tmp += new String(new byte[] { (byte) b });
						}

						if (tmp.equals("exit"))
						{
							completed = true;
							return;
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
		t.setDaemon(true);
		t.start();
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
		{

			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				e.printStackTrace();

			}
		});

		TraceXmlWrapper storage = new TraceXmlWrapper(new File(traceFolder, moduleName
				+ "-" + traceName + ".xml"));

		new TraceInterpreter(this, subset, reductionType, seed, new TypeCheckerAssistantFactory()).run(moduleName, traceName, interpreter, storage);

		while (!completed)
		{
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
			}
		}

		try
		{
			if (DEBUG)
			{
				System.out.println("Closing socket");
			}
			socket.close();
		} catch (IOException e)
		{

		}

	}

	private void init() throws IOException
	{
		if (DEBUG)
		{
			System.out.println("Connected");
		}
		
		StringBuilder sb = new StringBuilder();
		// interpreter.init(null);
		sb.append("<init ");
		sb.append("module=\"" + moduleName + "\" ");
		sb.append("/>\n");

		write(sb);
		
		if (DEBUG)
		{
			System.out.println("Wrote init");
		}
	}

	private String currentTraceName = "";

	/*
	 * (non-Javadoc)
	 * @see org.overture.traces.vdmj.IProgressMonitor#progress(java.lang.Integer)
	 */
	public void progress(Integer procentage) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		// interpreter.init(null);
		sb.append("<response ");
		sb.append("status=\"progress\" ");
		sb.append("progress=\"" + procentage + "\" ");
		sb.append("tracename=\"" + currentTraceName + "\" ");
		sb.append("/>\n");

		write(sb);
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.traces.vdmj.IProgressMonitor#progressStartTrace(java.lang.String)
	 */
	public void progressStartTrace(String traceName) throws IOException
	{
		this.currentTraceName = traceName;
		StringBuilder sb = new StringBuilder();
		// interpreter.init(null);
		sb.append("<response ");
		sb.append("status=\"tracestart\" ");
		sb.append("tracename=\"" + traceName + "\" ");
		sb.append("progress=\"" + 0 + "\" ");
		sb.append("/>\n");

		write(sb);
	}

	public void progressCompleted() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<response ");
		sb.append("status=\"completed\" ");
		sb.append("progress=\"" + 100 + "\" ");
		sb.append("/>\n");

		write(sb);

	}

	public void progressTerminating() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<response ");
		sb.append("status=\"terminating\" ");
		sb.append("/>\n");

		write(sb);

	}

	public void progressError(String message) throws IOException
	{

		StringBuilder sb = new StringBuilder();
		// interpreter.init(null);
		sb.append("<response ");
		sb.append("status=\"error\" ");
		sb.append("message=\"" + message + "\" ");
		// sb.append("progress=\"" + 100 + "\" ");
		sb.append("/>\n");

		write(sb);

	}

	protected void write(StringBuilder data) throws IOException
	{
		if (output == null)
		{
			// TODO: Handle the error in VDMJ, terminate?
			System.err.println("Socket to IDE not valid.");
			return;
		}
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

}
