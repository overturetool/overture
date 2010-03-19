package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.InternalException;
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
public class BuilderSl extends VdmjBuilder
{
	protected ModuleList modules = new ModuleList();

	public BuilderSl() {
		Settings.dialect = Dialect.VDM_SL;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public IStatus buildModel(IVdmModel rooList)
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
		try
		{
			modules = rooList.getModuleList();
		} catch (NotAllowedException e)
		{
			e.printStackTrace();
		}

		return buildModel(getProject());
	}

	

	

	/**
	 * @see org.overturetool.vdmj.VDMJ#typeCheck()
	 */

	@Override
	public ExitStatus typeCheck()
	{
		int terrs = 0;

		modules.combineDefaults();

		try
		{
			TypeChecker typeChecker = new ModuleTypeChecker(modules);
			typeChecker.typeCheck();
		} catch (InternalException e)
		{
			processInternalError(e);
		} catch (Throwable e)
		{
			processInternalError(e);
			terrs++;
		}

		terrs += TypeChecker.getErrorCount();

		if (terrs > 0)
		{
			processErrors(TypeChecker.getErrors());
		}

		int twarn = TypeChecker.getWarningCount();

		if (twarn > 0)
		{
			processWarnings(TypeChecker.getWarnings());
		}

		return terrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

}
