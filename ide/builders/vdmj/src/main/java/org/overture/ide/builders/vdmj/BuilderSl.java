package org.overture.ide.builders.vdmj;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.vdmsl.core.VdmSlCorePluginConstants;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

/***
 * VDM SL builder
 * 
 * @author kela <extension<br>
 *         point="org.overture.ide.builder"><br>
 *         <builder<br>
 *         class="org.overture.ide.builders.vdmj.BuilderSl"><br>
 *         </builder><br>
 *         </extension><br>
 */
public class BuilderSl extends VdmjBuilder {
	protected ModuleList modules = new ModuleList();

	public BuilderSl() {
		Settings.dialect = Dialect.VDM_PP;
	}

	@Override
	public IStatus buileModelElements(IProject project, RootNode rootNode) {
		try {
			modules=rootNode.getModuleList();
		} catch (NotAllowedException e) {
			e.printStackTrace();
		}

		return buileModelElements(project);
	}

	@Override
	public String getNatureId() {
		return VdmSlProjectNature.VDM_SL_NATURE;
	}

	@Override
	public String getContentTypeId() {
		return VdmSlCorePluginConstants.CONTENT_TYPE;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#typeCheck()
	 */

	@Override
	public ExitStatus typeCheck() {
		int terrs = 0;
		
		modules.combineDefaults();

		terrs += TypeChecker.getErrorCount();

		if (terrs > 0) {
			processErrors(TypeChecker.getErrors());
		}

		int twarn = TypeChecker.getWarningCount();

		if (twarn > 0) {
			processWarnings(TypeChecker.getWarnings());
		}

		return terrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

}
