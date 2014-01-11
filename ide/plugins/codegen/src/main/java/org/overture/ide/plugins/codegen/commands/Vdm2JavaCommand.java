package org.overture.ide.plugins.codegen.commands;

import java.io.File;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.constants.OoAstConstants;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.utils.InvalidNamesException;
import org.overture.codegen.utils.UnsupportedModelingException;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.ICodeGenConstants;
import org.overture.ide.plugins.codegen.util.PluginVdm2JavaUtil;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class Vdm2JavaCommand extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof IProject)
			{
				IProject project = ((IProject) firstElement);
				IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

				if (vdmProject == null)
					return null;

				final IVdmModel model = vdmProject.getModel();

				if (model == null || !model.isParseCorrect())
					return null;

				if (!model.isTypeChecked())
					VdmTypeCheckerUi.typeCheck(HandlerUtil.getActiveShell(event), vdmProject);

				if (!model.isTypeCorrect()
						|| !PluginVdm2JavaUtil.isSupportedVdmDialect(vdmProject))
					return null;

				final JavaCodeGen vdm2java = new JavaCodeGen();

				CodeGenConsole.GetInstance().show();			
				
				try
				{
					CodeGenConsole.GetInstance().clearConsole();
					CodeGenConsole.GetInstance().println("Starting Java to VDM code generation...\n");
					
					List<IVdmSourceUnit> sources = model.getSourceUnits();
					List<SClassDefinition> mergedParseLists = PluginVdm2JavaUtil.mergeParseLists(sources);

					List<GeneratedModule> userspecifiedClasses = vdm2java.generateJavaFromVdm(mergedParseLists);
					File outputFolder = PluginVdm2JavaUtil.getOutputFolder(vdmProject);
					vdm2java.generateJavaSourceFiles(outputFolder, userspecifiedClasses);
					
					GeneratedModule quotes = vdm2java.generateJavaFromVdmQuotes();
					if(quotes != null)
					{
						File quotesFolder = PluginVdm2JavaUtil.getQuotesFolder(vdmProject);
						vdm2java.generateJavaSourceFile(quotesFolder, quotes);
						
						CodeGenConsole.GetInstance().println("Quotes interface generated.");
						File quotesFile = new File(outputFolder, OoAstConstants.QUOTES_INTERFACE_NAME + IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
						CodeGenConsole.GetInstance().println("Java source file: " + quotesFile.getAbsolutePath());
						CodeGenConsole.GetInstance().println("");
					}
					
					List<GeneratedModule> utils = vdm2java.generateJavaCodeGenUtils();

					File utilsFolder = PluginVdm2JavaUtil.getUtilsFolder(vdmProject);
					vdm2java.generateJavaSourceFiles(utilsFolder, utils);
					
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					
					for (GeneratedModule generatedModule : userspecifiedClasses)
					{
						if(generatedModule.canBeGenerated())
						{
							File javaFile = new File(outputFolder, generatedModule.getName() + IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
							CodeGenConsole.GetInstance().println("Generated module: " + generatedModule.getName());
							CodeGenConsole.GetInstance().println("Java source file: " + javaFile.getAbsolutePath());
						}
						else
						{
							List<INode> nodes = LocationAssistantCG.getNodeLocationsSorted(generatedModule.getUnsupportedNodes());
							CodeGenConsole.GetInstance().println("Could not code generate module: " + generatedModule.getName() + ".");
							CodeGenConsole.GetInstance().println("Following constructs are not supported:");
							
							for(INode node : nodes)
							{
								ILexLocation nodeLoc = LocationAssistantCG.findLocation(node);
								IFile ifile = PluginVdm2JavaUtil.convert(nodeLoc.getFile());
								String message = "\t" + node.toString() + " (" + node.getClass().getSimpleName() + ") " + nodeLoc.toShortString();
								
								CodeGenConsole.GetInstance().println(message);
								FileUtility.addMarker(ifile, message, nodeLoc, IMarker.PRIORITY_NORMAL, ICodeGenConstants.PLUGIN_ID, -1);
							}
						}
						CodeGenConsole.GetInstance().println("");
					}

				}catch(InvalidNamesException ex)
				{
					CodeGenConsole.GetInstance().println("Could not code generate VDM model: " + ex.getMessage());

					String violationStr = JavaCodeGenUtil.constructNameViolationsString(ex);
					CodeGenConsole.GetInstance().println(violationStr);
					
				}catch(UnsupportedModelingException ex)
				{
					CodeGenConsole.GetInstance().println("Could not code generate VDM model: " + ex.getMessage());

					String violationStr = JavaCodeGenUtil.constructUnsupportedModelingString(ex);
					CodeGenConsole.GetInstance().println(violationStr);
					
				}catch (AnalysisExceptionCG ex)
				{
					CodeGenConsole.GetInstance().println("Could not code generate VDM model: " + ex.getMessage());
					return null;
				}
				catch (Exception ex)
				{
					String errorMessage = "Unexpected exception caught when attempting to code generate VDM model.";
					
					Activator.log(errorMessage, ex);
					
					CodeGenConsole.GetInstance().println(errorMessage);
					CodeGenConsole.GetInstance().println(ex.getMessage());
					ex.printStackTrace();
					
					return null;
				}
			}
		}
		
		return null;
	}
}