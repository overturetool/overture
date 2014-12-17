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
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/* This is the entry method of the code generator for VDM-RT code generator
 * It takes a list of VDM-RT files, and generates Java code
 * according to the description in the report for each CPU
 */

public class JavaCodeGenDistributionMain {

	public static void main(String[] args) throws AnalysisException,
	org.overture.codegen.cgast.analysis.AnalysisException, IOException {

		// Configuration parameters related to the VDM++-to-Java
		// code generator
		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_RT;

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(false);
		irSettings.setGenerateConc(true);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		// Here the input VDM-RT files are read
		List<File> files = Util.getFilesFromPaths(args);

		List<File> libFiles = GeneralUtils.getFiles(new File(
				"src\\test\\resources\\lib"));
		files.addAll(libFiles);

		// This parts generates the sequential part of the VDM-RT model
		// using the existing VDM++ code generator
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
					JavaCodeGenUtil.printUnsupportedIrNodes(generatedClass
							.getUnsupportedInIr());
				} else {
					Logger.getLog().println(generatedClass.getContent());
				}

				Logger.getLog().println("\n");
			}

			List<GeneratedModule> quotes = data.getQuoteValues();

			if (quotes != null && !quotes.isEmpty()) {
				
				Logger.getLog().println("**********");
				Logger.getLog().println("Generated quotes: ");
				
				for(GeneratedModule q : quotes)
				{
					Logger.getLog().println(q.getName());
				}
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
			
			// Now the architecture of the VDM-RT model is analysed
			// in order to extract the Connection map, Distribution map,
			// number of deployed objects and number of CPUs
			DistributionMapping mapping = new DistributionMapping(result.result);
			mapping.run();

			int DeployedObjCounter = mapping.getDeployedObjCounter();
			
			Set<AClassClassDefinition> deployedClasses = mapping
					.getDeployedClasses();

			Set<AVariableExp> deployedObjects = mapping.getDeployedObjects();

			IRInfo info = new IRInfo("cg_init");
			JavaFormat javaFormat = new JavaFormat(new TempVarPrefixes(),new TemplateStructure(JavaCodeGen.JAVA_TEMPLATES_ROOT_FOLDER), info);

			List<AClassDeclCG> irClasses = Util.getClasses(data.getClasses());
			
			//******Transform the ir_classes*********/////
			
			String systemClassName = mapping.getSystemName();
			RemoteTypeTransformation remoteTypeTrans = new RemoteTypeTransformation(systemClassName, info);
			for ( AClassDeclCG irClass : irClasses) {
				irClass.apply(remoteTypeTrans);
			}
			
			//**********************************************************************//
			// Generate the remote contracts
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

			// Generate the remote contract implementations
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

			// Distributed the generate remote contracts and their implementation
			RemoteContractDistribution RemConDist = new RemoteContractDistribution(remoteContracts, cpuToDeployedClasses, cpuToConnectedCPUs, remoteImpls);

			RemConDist.run();



			//**********************************************************************//
			CPUdeploymentGenerator cpuDepGenerator = new CPUdeploymentGenerator(
					CpuToDeployedObject, cpuToConnectedCPUs , info, DeployedObjCounter);
			Set<ACpuDeploymentDeclCG> cpuDeps = cpuDepGenerator
					.run();


			Map<String, AClassDeclCG> cpuToSystemDecl = cpuDepGenerator.getcpuToSystemDecl();
			// Distribute the CPU deployment for each CPU
			// Here just a fix output path is chosen in order to test the generate Java code
			for (ACpuDeploymentDeclCG impl : cpuDeps) {
				StringWriter writer = new StringWriter();
				impl.apply(printer, writer);

				System.out.println(JavaCodeGenUtil.formatJavaCode(writer
						.toString()));

				// The CPU entry method
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

				// The unique system class for each CPU
				File file2 = new File("/Users/Miran/Documents/files/" + impl.getCpuName() + "/" + systemClass.getName()  + ".java");
				BufferedWriter output2 = new BufferedWriter(new FileWriter(file2));
				output2.write(JavaCodeGenUtil.formatJavaCode(writer2
						.toString()));
				output2.close();
				
			}
		} catch (UnsupportedModelingException e) {
			Logger.getLog().println(
					"Could not generate model: " + e.getMessage());
			Logger.getLog().println(
					JavaCodeGenUtil.constructUnsupportedModelingString(e));
		}

	}
}
