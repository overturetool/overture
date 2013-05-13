package org.overture.ide.plugins.codegen.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.vdm2cpp.Vdm2Cpp;
import org.overture.ide.plugins.codegen.vdm2cpp.Vdm2CppUtil;
import org.overture.ide.plugins.codegen.visitor.CodeGenContextMap;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.interpreter.messages.Console;

public class Vdm2CppCommand extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IVdmProject vdmProject = Vdm2CppUtil.getVdmProject(event);

		if (vdmProject == null)
			return null;

		final IVdmModel model = vdmProject.getModel();

		if (model == null || !model.isParseCorrect())
			return null;

		if (!model.isTypeChecked())
			VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);

		if (!model.isTypeCorrect()
				|| !Vdm2CppUtil.isSupportedVdmDialect(vdmProject))
			return null;

		final Vdm2Cpp vdm2cpp = new Vdm2Cpp();

		CodeGenContextMap codeGenContext = null;
		
		try
		{
			codeGenContext = vdm2cpp.generateCode(model);

		} catch (AnalysisException ex)
		{
			Activator.log("Failed generating code", ex);
			return null;
		} catch (Exception ex)
		{
			Activator.log("Failed generating code", ex);
			return null;
		}

		if(codeGenContext == null)
		{
			Console.out.println("There is no source to generate from.");
			return null;
		}
		
		vdm2cpp.save(vdmProject, codeGenContext);

		return null;
	}

}
