package org.overture.ide.debug.ui.preferences.vdmpp;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;

public class VDMPPInterpreterBlock extends InterpretersBlock {

	@Override
	protected String getCurrentNature() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock#createInterpreterDialog(org.eclipse.dltk.core.environment.IEnvironment, org.eclipse.dltk.launching.IInterpreterInstall)
	 */
	protected IScriptInterpreterDialog createInterpreterDialog(IEnvironment environment, IInterpreterInstall standin) {
		
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
