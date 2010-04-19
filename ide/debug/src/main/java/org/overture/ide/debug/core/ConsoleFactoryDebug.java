package org.overture.ide.debug.core;

import org.eclipse.ui.console.IConsoleFactory;
import org.overture.ide.ui.internal.util.ConsoleWriter;

public class ConsoleFactoryDebug implements IConsoleFactory
{

	public void openConsole()
	{
		new ConsoleWriter(IDebugConstants.CONSOLE_DEBUG_NAME).Show();
	}

}
