package org.overture.ide.builders.vdmj;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.vdmpp.core.VdmPpCorePluginConstants;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
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

	@SuppressWarnings("unchecked")
	@Override
	public IStatus buileModelElements(IProject project, List modelElements) {

		classes.clear();
		for (Object classDefinition : modelElements) {
			if (classDefinition instanceof ClassDefinition)
				classes.add((ClassDefinition) classDefinition);
		}

		return buileModelElements(project);

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
