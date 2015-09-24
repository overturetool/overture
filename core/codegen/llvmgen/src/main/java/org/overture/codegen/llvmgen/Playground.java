package org.overture.codegen.llvmgen;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.robovm.llvm.Context;
import org.robovm.llvm.Module;
import org.robovm.llvm.PassManager;
import org.robovm.llvm.PassManagerBuilder;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;

public class Playground
{
	private static final String VDMRT_FILE_EXTENSION = ".vdmrt";
	public static final String INPUT_PATH = "src/test/resources/playground-input/watertank-periodic-de";

	public static void main(String[] args) throws AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;

		File inputFolder = new File(INPUT_PATH);

		List<File> vdmrtFiles = getVdmRtFiles(inputFolder);

		TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckRt(vdmrtFiles);

		if (!tcResult.parserResult.errors.isEmpty())
		{
			System.out.println(tcResult.parserResult.getErrorString());
			return;
		}

		if (!tcResult.errors.isEmpty())
		{
			System.out.println(tcResult.getErrorString());
			return;
		}

		IRGenerator irGen = new IRGenerator();
		irGen.computeDefTable(tcResult.result);

		LlvmBuilder builder = new LlvmBuilder();
		List<LlvmNode> llvmNodes = new LinkedList<>();

		for (SClassDefinition vdmClass : tcResult.result)
		{
			IRStatus<INode> irStatus = irGen.generateFrom(vdmClass);
			INode irNode = irStatus.getIrNode();

			if (irStatus.canBeGenerated())
			{

				if (irNode instanceof ADefaultClassDeclCG
						&& (((ADefaultClassDeclCG) irNode).getName().equals("LevelSensor")|| ((ADefaultClassDeclCG) irNode).getName().equals("ValveActuator")))
				{

					LlvmNode res = irNode.apply(builder, null);
					
				
					
					llvmNodes.add(res);
				}
			} else
			{
				System.out.println("Could not generate: "
						+ irStatus.getIrNodeName());
				GeneralCodeGenUtils.printUnsupportedIrNodes(irStatus.getUnsupportedInIr());
			}
		}

		for (LlvmNode llvmNode : llvmNodes)
		{
			System.out.println(llvmNode);
			check(llvmNode.modref);
		}
	}
	
	
	public static void check(ModuleRef mod)
	{
		Context context = new Context();
		Module m = Module.parseIR(context, LLVM.PrintModuleToString(mod), "foo.bc");
		check(m);
	}
	
	public static void check(Module m)
	{
//		Context context = new Context();
		// PassManager manager = new PassManager();
		// manager.run(m);
		try (PassManager passManager = new PassManager())
		{
			try (PassManagerBuilder builder2 = new PassManagerBuilder())
			{
				builder2.setSetOptLevel(2);
				builder2.setDisableTailCalls(true);
				builder2.useAlwaysInliner(true);
				builder2.populateModulePassManager(passManager);
			}
			passManager.run(m);
			// m.writeBitcode(new File("/tmp/test.bc"));
		}
	}

	private static List<File> getVdmRtFiles(File inputFolder)
	{
		List<File> vdmrtFiles = new LinkedList<File>();

		for (File f : GeneralUtils.getFiles(inputFolder))
		{
			if (f.getName().endsWith(VDMRT_FILE_EXTENSION))
			{
				vdmrtFiles.add(f);
			}
		}
		return vdmrtFiles;
	}
}
