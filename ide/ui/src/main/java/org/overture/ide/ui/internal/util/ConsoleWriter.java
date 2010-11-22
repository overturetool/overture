package org.overture.ide.ui.internal.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.ui.VdmUIPlugin;

public class ConsoleWriter extends PrintWriter {
	private String consoleName = "Overture";

	public ConsoleWriter() {
		super(System.out);
	}

	public ConsoleWriter(String consoleName) {
		super(System.out);
		this.consoleName = consoleName;
	}

	@Override
	public void println(String x) {
		consolePrint(x);
	}

	public void show() {
		MessageConsole myConsole = findConsole(consoleName);
		myConsole.activate();

		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(
				myConsole);

		UIJob uiJob = new UIJob("Console pin") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbench w = PlatformUI.getWorkbench();
				IWorkbenchWindow[] wws = w.getWorkbenchWindows();
				String id = IConsoleConstants.ID_CONSOLE_VIEW;
				for (IWorkbenchWindow iWorkbenchWindow : wws) {
					try {

						IWorkbenchPage page = iWorkbenchWindow.getActivePage();
						IConsoleView view = (IConsoleView) page.showView(id);
//						view.display(myConsole);
						view.setPinned(true);
						
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return new Status(IStatus.OK, "pluginId", "OK");
			}
		};
		uiJob.schedule();

	}

	public void consolePrint(final String message) {
		getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void clear() {
		getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					myConsole.clearConsole();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void consolePrint(final String message, final int color) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.setColor(Display.getCurrent().getSystemColor(color));
					out.println(message);
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public synchronized MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public void consolePrint(final Exception exception) {
		consolePrint(getExceptionStackTraceAsString(exception));
	}

	public static String getExceptionStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static Display getDisplay() {
		return VdmUIPlugin.getDefault().getWorkbench().getDisplay();
	}
}
