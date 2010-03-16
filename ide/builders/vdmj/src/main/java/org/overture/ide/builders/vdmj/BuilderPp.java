package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.ast.NotAllowedException;
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

	
	@SuppressWarnings("unchecked")
	@Override
	public IStatus buileModelElements(IVdmModel rooList)
	{
	
		try
		{
			Settings.release = getProject().getLanguageVersion();
			Settings.dynamictypechecks = getProject().hasDynamictypechecks();
			Settings.invchecks = getProject().hasInvchecks();
			Settings.postchecks = getProject().hasPostchecks();
			Settings.prechecks = getProject().hasPrechecks();
		} catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		try {
			classes = rooList.getClassList();
		} catch (NotAllowedException e) {
			
			e.printStackTrace();
		}

		return buileModelElements(getProject());

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
