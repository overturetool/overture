package org.overture.ide.debug.ui.preferences;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.viewers.IBaseLabelProvider;

public abstract class AbstractInterpreterBlock extends InterpretersBlock {

	@Override
	protected abstract String getCurrentNature();
	
	protected IScriptInterpreterDialog createInterpreterDialog(IEnvironment environment, org.eclipse.dltk.launching.IInterpreterInstall standin) 
	{
		/**
		 * create a Interpreter dialog
		 */
		return new AddScriptInterpreterDialog(this,
				getShell(),
				ScriptRuntime.getInterpreterInstallTypes(getCurrentNature()),standin) {
			
			@Override
			protected boolean useInterpreterArgs() {
				return false;
			}
			
			@Override
			protected AbstractInterpreterLibraryBlock createLibraryBlock(AddScriptInterpreterDialog dialog) {
				
				/***
				 * create a new Library block
				 */
				return new AbstractInterpreterLibraryBlock(dialog) {
					
					@Override
					protected IBaseLabelProvider getLabelProvider() {
						return new LibraryLabelProvider();
					}
				};
			} // end libraryBlock
		
		}; // end interpreter dialog
	};
}
