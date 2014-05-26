package org.overture.ide.plugins.codegen.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

public class ConfigCommand extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(HandlerUtil.getActiveShell(event), "org.overture.ide.plugins.codegen.pageMain", null, null);
		dialog.open();

		return Status.OK_STATUS;
	}
}
