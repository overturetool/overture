package org.overture.ide.vdmsl.debug.ui.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.ui.launching.AbstractVdmMainLaunchConfigurationTab;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.ide.vdmsl.debug.Activator;
import org.overture.ide.vdmsl.core.IVdmSlCoreConstants;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.runtime.ModuleInterpreter;

public class VdmSlMainLaunchConfigurationTab extends
		AbstractVdmMainLaunchConfigurationTab
{

	@Override
	protected String getExpression(String module, String operation,
			boolean isStatic)
	{
		return module + STATIC_CALL_SEPERATOR + operation;
	}

	@Override
	protected boolean isSupported(IProject project) throws CoreException
	{
		return project.hasNature(IVdmSlCoreConstants.NATURE);
	}

	@Override
	protected boolean validateTypes(IVdmProject project, String expression)
	{
		try
		{
			Settings.dialect = Dialect.VDM_SL;
			Settings.release = project.getLanguageVersion();
			Console.charset = getProject().getDefaultCharset();
			IVdmModel model = project.getModel();
			if (!model.isTypeCorrect())
			{
				if (!VdmTypeCheckerUi.typeCheck(getShell(), project))
				{
					setErrorMessage("Type errors in Model");
					return false;
				}
			}
			ModuleList modules = model.getModuleList();
			modules.combineDefaults();
			ModuleInterpreter ci = new ModuleInterpreter(modules);
			if (expression.contains(STATIC_CALL_SEPERATOR))
			{
				ci.setDefaultName(expression.substring(0, expression.indexOf(STATIC_CALL_SEPERATOR))); // needed for static fn/op check
			}

			ci.typeCheck(expression);
			return true;
		} catch (NotAllowedException e)
		{
			setErrorMessage(e.toString());
			e.printStackTrace();
		} catch (VDMErrorsException e)
		{
			setErrorMessage(e.toString());
		} catch (Exception e)
		{
			if (e.getMessage() != null && e.getMessage().contains("not loaded"))
			{
				setErrorMessage(e.getMessage());
			}
			else
			{
				Activator.logError("Internal type check error", e);
				setErrorMessage("Internal type check error: " + e.toString());
			}
		}

		return false;
	}

}
