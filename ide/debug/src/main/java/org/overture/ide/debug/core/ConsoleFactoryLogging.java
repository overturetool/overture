package org.overture.ide.debug.core;

import org.eclipse.ui.console.IConsoleFactory;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class ConsoleFactoryLogging implements IConsoleFactory
{

	public void openConsole()
	{
		new ConsoleWriter(IDebugConstants.CONSOLE_LOGGING_NAME).Show();
	}

}
