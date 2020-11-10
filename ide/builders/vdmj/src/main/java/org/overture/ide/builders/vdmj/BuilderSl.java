/*
 * #%~
 * org.overture.ide.builders.vdmj
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.builders.vdmj;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.ast.util.modules.ModuleList;
import org.overture.config.Settings;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.typechecker.ModuleTypeChecker;
import org.overture.typechecker.TypeChecker;

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

	@Override
	public IStatus buildModel(IVdmModel rooList)
	{
		try
		{
			Settings.release = getProject().getLanguageVersion();
			Settings.strict = getProject().hasUseStrictLetDef();
//			Settings.dynamictypechecks = getProject().hasDynamictypechecks();
//			Settings.invchecks = getProject().hasInvchecks();
//			Settings.postchecks = getProject().hasPostchecks();
//			Settings.prechecks = getProject().hasPrechecks();
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
	 * @see org.overture.vdmj.VDMJ#typeCheck()
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
