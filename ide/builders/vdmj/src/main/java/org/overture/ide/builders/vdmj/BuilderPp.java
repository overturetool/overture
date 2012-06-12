package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.ast.util.definitions.ClassList;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeChecker;

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
	public IStatus buildModel(IVdmModel rooList)
	{
	
		try
		{
			Settings.release = getProject().getLanguageVersion();
		} catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		try {
			classes = rooList.getClassList();
		} catch (NotAllowedException e) {
			
			e.printStackTrace();
		}

		return buildModel(getProject());

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
		terrs++;
		}catch(TypeCheckException e){
		//is already in TypeChecker Errors
			terrs++;
		}catch (Throwable e) {
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
