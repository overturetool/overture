package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.ast.NotAllowedException;
import org.overture.ide.ast.RootNode;
import org.overture.ide.utility.IVdmProject;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

/***
 * VDM PP builder
 * 
 * @author kela <extension<br>
 *         point="org.overture.ide.builder"><br>
 *         <builder<br>
 *         class="org.overture.ide.builders.vdmj.BuilderPp"><br>
 *         </builder><br>
 *         </extension><br>
 */
public class BuilderPp extends VdmjBuilder {
	protected ClassList classes = new ClassList();

	public BuilderPp() {
		Settings.dialect = Dialect.VDM_PP;
	}

	@Override
	public IStatus buileModelElements(IVdmProject project, RootNode rootNode) {
		try
		{
			Settings.release = project.getLanguageVersion();
			Settings.dynamictypechecks = project.hasDynamictypechecks();
			Settings.invchecks = project.hasInvchecks();
			Settings.postchecks = project.hasPostchecks();
			Settings.prechecks = project.hasPrechecks();
		} catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		try {
			classes = rootNode.getClassList();
		} catch (NotAllowedException e) {
			
			e.printStackTrace();
		}

		return buileModelElements(project.getProject());

	}

	@Override
	public String getNatureId() {
		return VdmPpProjectNature.VDM_PP_NATURE;
	}

	@Override
	public String getContentTypeId() {
		return VdmPpCorePluginConstants.CONTENT_TYPE;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#typeCheck()
	 */

	@Override
	public ExitStatus typeCheck() {
		int terrs = 0;

		try {
			TypeChecker typeChecker = new ClassTypeChecker(classes);
			typeChecker.typeCheck();
		} catch (InternalException e) {
			processInternalError(e);
		} catch (Throwable e) {
			processInternalError(e);

			if (e instanceof StackOverflowError) {
				e.printStackTrace();
			}

			terrs++;
		}

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
