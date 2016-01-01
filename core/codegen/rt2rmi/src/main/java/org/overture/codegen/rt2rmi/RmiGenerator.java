package org.overture.codegen.rt2rmi;

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
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.ACpuDeploymentDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ARMIServerDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;
import org.overture.codegen.cgast.declarations.ASynchTokenDeclCG;
import org.overture.codegen.cgast.declarations.ASynchTokenInterfaceDeclCG;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.rt2rmi.systemanalysis.DistributionMapping;
import org.overture.codegen.rt2rmi.trans.RemoteTypeTrans;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenMain;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaSettings;

public class RmiGenerator implements IREventObserver {
	private JavaCodeGen javaGen;
	private String systemClassName;

	public RmiGenerator() {
		this.javaGen = new JavaCodeGen();
		this.javaGen.registerIrObs(this);
		addTransformations();
	}

	private void addTransformations() {
		// Add additional transformations
		IRInfo info = new IRInfo();
		this.javaGen.getTransSeries().getSeries().add(new RemoteTypeTrans(systemClassName, info));
	}

	public void generate(List<SClassDefinition> rtClasses, String output_dir)
			throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException, IOException {

		/********** Analyse System class **********/
		// Now the architecture of the VDM-RT model is analysed
		// in order to extract the Connection map, Distribution map,
		// number of deployed objects and number of CPUs

		DistributionMapping mapping = new DistributionMapping(rtClasses);
		mapping.run();

		try {
			systemClassName = mapping.getSystemName();

			GeneratedData data = javaGen.generateJavaFromVdm(rtClasses);

			List<ADefaultClassDeclCG> irClasses = Util.getClasses(data.getClasses());

			RemoteContractGenerator contractGenerator = new RemoteContractGenerator(irClasses);
			Set<ARemoteContractDeclCG> remoteContracts = contractGenerator.run();

			// printRemoteContracts(remoteContracts);

			RemoteImplGenerator implsGen = new RemoteImplGenerator(irClasses);
			List<ARemoteContractImplDeclCG> remoteImpls = implsGen.run();

			// printRemoteContractsImpl(remoteImpls);

			//System.out.println("**********************CPU deployment**********************");

			int deployedObjCounter = mapping.getDeployedObjCounter();
			Map<String, Set<AVariableExp>> cpuToDeployedObject = mapping.getCpuToDeployedObject();
			Map<String, Set<String>> cpuToConnectedCPUs = mapping.cpuToConnectedCPUs();
			Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses = mapping.cpuToDeployedClasses();
			
			// Generate the RMI server
			generateRMIserver(output_dir, 1099, remoteContracts);
			
			// Distributed the generate remote contracts and their implementation
			processData(output_dir, remoteContracts, cpuToDeployedClasses, cpuToConnectedCPUs, remoteImpls);

			// Generate entry method for each CPU and the local system class
			processData2(output_dir, cpuToDeployedObject, cpuToConnectedCPUs, deployedObjCounter);

		} catch (AnalysisException e) {
			Logger.getLog().println("Could not code generate model: " + e.getMessage());
		}

	}

	public JavaCodeGen getJavaGen() {
		return this.javaGen;
	}

	public JavaFormat getJavaFormat() {
		return this.javaGen.getJavaFormat();
	}

	public IRSettings getIrSettings() {
		return this.javaGen.getSettings();
	}

	public JavaSettings getJavaSettings() {
		return this.javaGen.getJavaSettings();
	}

	@Override
	public List<IRStatus<INode>> initialIRConstructed(List<IRStatus<INode>> ast, IRInfo info) {
		// This method received the initial version of the IR before it is
		// transformed
		Logger.getLog().println("Initial IR has " + ast.size() + " node(s)");

		// For an example of how to process/modify the IR see
		// org.overture.codegen.vdm2jml.JmlGenerator

		// Return the (possibly modified) AST that the Java code generator
		// should use subsequently
		return ast;
	}

	@Override
	public List<IRStatus<INode>> finalIRConstructed(List<IRStatus<INode>> ast, IRInfo info) {
		// The final version of the IR

		Logger.getLog().println("Final version of the IR has " + ast.size() + " node(s)");

		return ast;
	}

	/*
	 * In this the ARMIServerDeclCG node is set up in order to code generate the
	 * global registration service. In addition, the generate remote contracts
	 * and remote contract implementation are printed to files inside the
	 * relevant CPUs in order to test the code. For this reason, the path is
	 * currently fix to a local path.
	 */

	public void generateRMIserver(String output_dir, int portNumber, Set<ARemoteContractDeclCG> remoteContracts) throws org.overture.codegen.cgast.analysis.AnalysisException, IOException{
		// Create the RMI server

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();
		
		ARMIServerDeclCG rmiServer = new ARMIServerDeclCG();

		String RMI_ServerName = "RMI_Server";

		rmiServer.setPortNumber(portNumber);
		rmiServer.setName(RMI_ServerName);

		StringWriter writer_s = new StringWriter();
		rmiServer.apply(printer, writer_s);

		File theDir_s = new File(output_dir + RMI_ServerName);

		theDir_s.mkdir();

		File file_s = new File(output_dir + RMI_ServerName + "/" + RMI_ServerName + ".java");
		BufferedWriter output_s = new BufferedWriter(new FileWriter(file_s));
		output_s.write(JavaCodeGenUtil.formatJavaCode(writer_s.toString()));
		output_s.close();

		// Print remote contracts to the RMI server
		for (ARemoteContractDeclCG contract : remoteContracts) {
			StringWriter writer_i = new StringWriter();
			contract.apply(printer, writer_i);

			File file_i = new File(
					output_dir + RMI_ServerName + "/" + contract.getName() + ".java");
			BufferedWriter output_i = new BufferedWriter(new FileWriter(file_i));
			output_i.write(JavaCodeGenUtil.formatJavaCode(writer_i.toString()));
			output_i.close();
		}

		// Add SyncToken interface to the RMI server directory
		ASynchTokenInterfaceDeclCG synchToken_interface_RMI = new ASynchTokenInterfaceDeclCG();

		StringWriter writer_synch_i_RMI = new StringWriter();
		synchToken_interface_RMI.apply(printer, writer_synch_i_RMI);

		File file_synch_i_RMI = new File(
				output_dir + RMI_ServerName + "/" + "SynchToken_interface.java");
		BufferedWriter output_synch_i_RMI = new BufferedWriter(new FileWriter(file_synch_i_RMI));
		output_synch_i_RMI.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i_RMI.toString()));
		output_synch_i_RMI.close();
	}
			
	// Create a directory for every CPU, and place relevant remote contracts
	// and remote contract implementations inside
	
	public void processData(String output_dir, Set<ARemoteContractDeclCG> remoteContracts,
			Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses, Map<String, Set<String>> cpuToConnectedCPUs,
			List<ARemoteContractImplDeclCG> remoteImpls)
					throws org.overture.codegen.cgast.analysis.AnalysisException, IOException {

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();



		for (String cpu : cpuToDeployedClasses.keySet()) {
			File theDir = new File(output_dir + cpu);

			theDir.mkdir();
		}

		for (String cpu : cpuToDeployedClasses.keySet()) {

			for (AClassClassDefinition clas : cpuToDeployedClasses.get(cpu)) {

				// Generate a SynchToken and its interface for each CPU
				ASynchTokenDeclCG synchToken = new ASynchTokenDeclCG();

				StringWriter writer_synch = new StringWriter();
				synchToken.apply(printer, writer_synch);

				File file_synch = new File(output_dir + cpu + "/" + "SynchToken.java");
				BufferedWriter output_synch = new BufferedWriter(new FileWriter(file_synch));
				output_synch.write(JavaCodeGenUtil.formatJavaCode(writer_synch.toString()));
				output_synch.close();

				ASynchTokenInterfaceDeclCG synchToken_interface = new ASynchTokenInterfaceDeclCG();

				StringWriter writer_synch_i = new StringWriter();
				synchToken_interface.apply(printer, writer_synch_i);

				File file_synch_i = new File(output_dir + cpu + "/" + "SynchToken_interface.java");
				BufferedWriter output_synch_i = new BufferedWriter(new FileWriter(file_synch_i));
				output_synch_i.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i.toString()));
				output_synch_i.close();

				for (ARemoteContractDeclCG contract : remoteContracts) { 
					StringWriter writer = new StringWriter();
					contract.apply(printer, writer);

					File file = new File(output_dir + cpu + "/" + contract.getName() + ".java");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
					output.close();
				}

				for (ARemoteContractImplDeclCG impl : remoteImpls) { 
					StringWriter writer = new StringWriter();
					impl.apply(printer, writer);

					File file = new File(output_dir + cpu + "/" + impl.getName() + ".java");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
					output.close();
				}
			}
		}
	}

	public void processData2(String output_dir, Map<String, Set<AVariableExp>> cpuToDeployedObject,
			Map<String, Set<String>> cpuToConnectedCPUs, int DeployedObjCounter)
					throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException, IOException {

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		CPUdeploymentGenerator cpuDepGenerator = new CPUdeploymentGenerator(cpuToDeployedObject, cpuToConnectedCPUs,
				DeployedObjCounter);
		Set<ACpuDeploymentDeclCG> cpuDeps = cpuDepGenerator.run();

		Map<String, ADefaultClassDeclCG> cpuToSystemDecl = cpuDepGenerator.getcpuToSystemDecl();
		
		// Distribute the CPU deployment for each CPU
		for (ACpuDeploymentDeclCG impl : cpuDeps) {
			StringWriter writer = new StringWriter();
			impl.apply(printer, writer);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));

			// The CPU entry method
			File file = new File(
					output_dir + impl.getCpuName() + "/" + impl.getCpuName() + ".java");
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
			output.close();

			// Create the unique system class for each CPU
			ADefaultClassDeclCG systemClass = cpuToSystemDecl.get(impl.getCpuName());

			StringWriter writer2 = new StringWriter();
			systemClass.apply(printer, writer2);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer2.toString()));

			// The unique system class for each CPU
			File file2 = new File(
					output_dir + impl.getCpuName() + "/" + systemClass.getName() + ".java");
			BufferedWriter output2 = new BufferedWriter(new FileWriter(file2));
			output2.write(JavaCodeGenUtil.formatJavaCode(writer2.toString()));
			output2.close();

		}

	}

	public void printRemoteContracts(Set<ARemoteContractDeclCG> remoteContracts)
			throws org.overture.codegen.cgast.analysis.AnalysisException {
		System.out.println("**********************Remote contracts**********************");
		JavaFormat javaFormat = getJavaGen().getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		for (ARemoteContractDeclCG contract : remoteContracts) {
			StringWriter writer = new StringWriter();
			contract.apply(printer, writer);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));
		}
	}

	public void printRemoteContractsImpl(List<ARemoteContractImplDeclCG> remoteImpls)
			throws org.overture.codegen.cgast.analysis.AnalysisException {
		System.out.println("**********************Remote contracts implementation**********************");
		JavaFormat javaFormat = getJavaGen().getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();
		for (ARemoteContractImplDeclCG impl : remoteImpls) {
			StringWriter writer = new StringWriter();
			impl.apply(printer, writer);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));
		}
	}
}
