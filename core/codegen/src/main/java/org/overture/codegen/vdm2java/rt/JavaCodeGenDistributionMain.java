package org.overture.codegen.vdm2java.rt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.ACpuDeploymentDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class JavaCodeGenDistributionMain {

	public static void main(String[] args) throws AnalysisException,
	org.overture.codegen.cgast.analysis.AnalysisException, IOException {

		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_RT;

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		List<File> files = Util.getFilesFromPaths(args);

		List<File> libFiles = GeneralUtils.getFiles(new File(
				"src\\test\\resources\\lib"));
		files.addAll(libFiles);

		GeneratedData data;
		try {
			data = JavaCodeGenUtil.generateJavaFromFiles(files, irSettings,
					javaSettings, dialect);
			List<GeneratedModule> generatedClasses = data.getClasses();

			for (GeneratedModule generatedClass : generatedClasses) {
				Logger.getLog().println("**********");

				if (generatedClass.hasMergeErrors()) {
					Logger.getLog()
					.println(
							String.format(
									"Class %s could not be merged. Following merge errors were found:",
									generatedClass.getName()));

					JavaCodeGenUtil.printMergeErrors(generatedClass
							.getMergeErrors());
				} else if (!generatedClass.canBeGenerated()) {
					Logger.getLog().println(
							"Could not generate class: "
									+ generatedClass.getName() + "\n");
					JavaCodeGenUtil.printUnsupportedNodes(generatedClass
							.getUnsupportedNodes());
				} else {
					Logger.getLog().println(generatedClass.getContent());
				}

				Logger.getLog().println("\n");
			}

			GeneratedModule quotes = data.getQuoteValues();

			if (quotes != null) {
				Logger.getLog().println("**********");
				Logger.getLog().println(quotes.getContent());
			}

			InvalidNamesResult invalidName = data.getInvalidNamesResult();

			if (!invalidName.isEmpty()) {
				Logger.getLog().println(
						JavaCodeGenUtil
						.constructNameViolationsString(invalidName));
			}

			TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil
					.typeCheckRt(files);

			//**********************************************************************//
			DistributionMapping mapping = new DistributionMapping(result.result);
			mapping.run();

			int DeployedObjCounter = mapping.getDeployedObjCounter();
			
			Set<AClassClassDefinition> deployedClasses = mapping
					.getDeployedClasses();


			Set<AVariableExp> deployedObjects = mapping.getDeployedObjects();

			//SystemClassDeclaration systemClassDecl = new SystemClassDeclaration(deployedObjects);

			//AClassDeclCG systemClass = systemClassDecl.Run();

			// TODO: Do nicely
			IRInfo info = new IRInfo("cg_init");
			JavaFormat javaFormat = new JavaFormat(new TempVarPrefixes(), info);

			List<AClassDeclCG> irClasses = Util.getClasses(data.getClasses());
			
			//******Transform the ir_classes*********/////
			
			//FIXME: Look the name up the right place
			String systemClassName = "Dist";
			RemoteTypeTransformation remoteTypeTrans = new RemoteTypeTransformation(systemClassName, info);
			for ( AClassDeclCG irClass : irClasses) {
				irClass.apply(remoteTypeTrans);
			}
			
			//**********************************************************************//
			RemoteContractGenerator contractGenerator = new RemoteContractGenerator(
					irClasses);
			Set<ARemoteContractDeclCG> remoteContracts = contractGenerator
					.run();

			MergeVisitor printer = javaFormat.getMergeVisitor();


			System.out.println("**********************Remote contracts**********************");
			for (ARemoteContractDeclCG conract : remoteContracts) {
				StringWriter writer = new StringWriter();
				conract.apply(printer, writer);

				System.out.println(JavaCodeGenUtil.formatJavaCode(writer
						.toString()));
			}


			RemoteImplGenerator implsGen = new RemoteImplGenerator(irClasses);
			List<ARemoteContractImplDeclCG> remoteImpls = implsGen.run();

			System.out.println("**********************Remote contracts implementation**********************");
			for (ARemoteContractImplDeclCG impl : remoteImpls) {
				StringWriter writer = new StringWriter();
				impl.apply(printer, writer);

				System.out.println(JavaCodeGenUtil.formatJavaCode(writer
						.toString()));
			}

			System.out.println("**********************CPU deployment**********************");

			Map<String, Set<AVariableExp>> CpuToDeployedObject = mapping.getCpuToDeployedObject();

			Map<String, Set<String>> cpuToConnectedCPUs = mapping.cpuToConnectedCPUs();
			//CPUdeploymentGenerator cpuDep = new CPUdeploymentGenerator(CpuToDeployedObject);

			Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses = mapping.cpuToDeployedClasses();

			RemoteContractDistribution RemConDist = new RemoteContractDistribution(remoteContracts, cpuToDeployedClasses, cpuToConnectedCPUs, remoteImpls);

			RemConDist.run();



			//**********************************************************************//
			CPUdeploymentGenerator cpuDepGenerator = new CPUdeploymentGenerator(
					CpuToDeployedObject, cpuToConnectedCPUs , info, DeployedObjCounter);
			Set<ACpuDeploymentDeclCG> cpuDeps = cpuDepGenerator
					.run();


			Map<String, AClassDeclCG> cpuToSystemDecl = cpuDepGenerator.getcpuToSystemDecl();
			
			for (ACpuDeploymentDeclCG impl : cpuDeps) {
				StringWriter writer = new StringWriter();
				impl.apply(printer, writer);

				System.out.println(JavaCodeGenUtil.formatJavaCode(writer
						.toString()));

				File file = new File("/Users/Miran/Documents/files/" + impl.getCpuName() + "/" + impl.getCpuName()  + ".java");
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(JavaCodeGenUtil.formatJavaCode(writer
						.toString()));
				output.close();
					
				// Create the unique system class for each CPU
				AClassDeclCG systemClass = cpuToSystemDecl.get(impl.getCpuName());
				
				StringWriter writer2 = new StringWriter();
				systemClass.apply(printer, writer2);

				System.out.println(JavaCodeGenUtil.formatJavaCode(writer2
						.toString()));

				File file2 = new File("/Users/Miran/Documents/files/" + impl.getCpuName() + "/" + systemClass.getName()  + ".java");
				BufferedWriter output2 = new BufferedWriter(new FileWriter(file2));
				output2.write(JavaCodeGenUtil.formatJavaCode(writer2
						.toString()));
				output2.close();
				
			}

//			for (ACpuDeploymentDeclCG impl : cpuDeps) {
//				StringWriter writer = new StringWriter();
//				systemClass.apply(printer, writer);
//
//				System.out.println(JavaCodeGenUtil.formatJavaCode(writer
//						.toString()));
//
//				File file = new File("/Users/Miran/Documents/files/" + impl.getCpuName() + "/" + systemClass.getName()  + ".java");
//				BufferedWriter output = new BufferedWriter(new FileWriter(file));
//				output.write(JavaCodeGenUtil.formatJavaCode(writer
//						.toString()));
//				output.close();
//			}
			
			
			
			
			//			for(String key : CpuToDeployedObject.keySet()){
			//				Set<AVariableExp> deployedObj = CpuToDeployedObject.get(key);
			//				for(AVariableExp dep: deployedObj){
			//					AVariableExp cu = dep;
			//
			//				}
			//				
			//				//CPUdeploymentGenerator 
			//			}

			//System.out.println();
		} catch (UnsupportedModelingException e) {
			Logger.getLog().println(
					"Could not generate model: " + e.getMessage());
			Logger.getLog().println(
					JavaCodeGenUtil.constructUnsupportedModelingString(e));
		}

	}
}
