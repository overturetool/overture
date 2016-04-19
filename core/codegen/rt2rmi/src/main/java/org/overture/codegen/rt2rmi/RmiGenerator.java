package org.overture.codegen.rt2rmi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.cgrmi.extast.declarations.AClientInstanceDeclIR;
import org.overture.cgrmi.extast.declarations.ACpuDeploymentDeclIR;
import org.overture.cgrmi.extast.declarations.ARMIServerDeclIR;
import org.overture.cgrmi.extast.declarations.ARMIregistryDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteContractDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteContractImplDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteInstanceDeclIR;
import org.overture.cgrmi.extast.declarations.ASynchTokenDeclIR;
import org.overture.cgrmi.extast.declarations.ASynchTokenInterfaceDeclIR;
//import org.overture.cgrmi.extast.node.PCG;
//import org.overture.codegen.cgast.PCG;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.rt2rmi.systemanalysis.DistributionMapping;
import org.overture.codegen.rt2rmi.trans.FunctionToRemoteTrans;
import org.overture.codegen.rt2rmi.trans.InterfaceFuncToRemoteTrans;
import org.overture.codegen.rt2rmi.trans.RemoteTypeTrans;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaQuoteValueCreator;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.ir.CodeGenBase;

public class RmiGenerator implements IREventObserver
{
	private JavaCodeGen javaGen;
	private String systemClassName;
	LinkedList<AFieldDeclIR> system_fields;

	public static String mvn_path = "src/main/java/";
	
	public RmiGenerator()
	{
		this.javaGen = new JavaCodeGen();
		this.javaGen.registerIrObs(this);
		this.javaGen.getSettings().setCharSeqAsString(true);
//		this.javaGen.emitCode(, fileName, code);
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ACpuDeploymentDeclIR.class, "RmiTemplates/ACpuDeploymentDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, AClientInstanceDeclIR.class, "RmiTemplates/AClientInstanceDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ARemoteContractDeclIR.class, "RmiTemplates/ARemoteContractDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ARemoteContractImplDeclIR.class, "RmiTemplates/ARemoteContractImplDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ARemoteInstanceDeclIR.class, "RmiTemplates/ARemoteInstanceDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ARMIregistryDeclIR.class, "RmiTemplates/ARMIregistryDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ARMIServerDeclIR.class, "RmiTemplates/ARMIServerDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ASynchTokenDeclIR.class, "RmiTemplates/ASynchTokenDeclCG.vm");
		
		this.javaGen.getJavaFormat().getMergeVisitor().getTemplateManager().setUserTemplatePath(
				RmiTemplateManager.class, ASynchTokenInterfaceDeclIR.class, "RmiTemplates/ASynchTokenInterfaceDeclCG.vm");
		
		addTransformations();
	}

	private void addTransformations()
	{
		// Add additional transformations
		this.javaGen.getTransSeries().getSeries().add(new RemoteTypeTrans(systemClassName, this.javaGen.getInfo()));
		this.javaGen.getTransSeries().getSeries().add(new FunctionToRemoteTrans(systemClassName, this.javaGen.getInfo()));
		//this.javaGen.getTransSeries().getSeries().add(new InterfaceFuncToRemoteTrans(systemClassName, this.javaGen.getInfo()));
	}
	
	public void generate(List<SClassDefinition> rtClasses, String output_dir)
			throws AnalysisException, org.overture.codegen.ir.analysis.AnalysisException, IOException
	{

		XCodeGen xgen = new XCodeGen();

		system_fields = xgen.generateXFromVdm(rtClasses);

		/********** Analyse System class **********/
		// Now the architecture of the VDM-RT model is analysed
		// in order to extract the Connection map, Distribution map,
		// number of deployed objects and number of CPUs

		DistributionMapping mapping = new DistributionMapping(rtClasses);
		mapping.run();

		try
		{
			// Settings.dialect=Dialect.VDM_RT;
			systemClassName = mapping.getSystemName();

			GeneratedData data = javaGen.generate(CodeGenBase.getNodes(rtClasses));
			
			// JavaCGMain line: 
			
			List<ADefaultClassDeclIR> irClasses = Util.getClasses(data.getClasses());

			RemoteContractGenerator contractGenerator = new RemoteContractGenerator(irClasses, this.getJavaGen().getInfo());
			Set<ARemoteContractDeclIR> remoteContracts = contractGenerator.run();

			// printRemoteContracts(remoteContracts);

			RemoteImplGenerator implsGen = new RemoteImplGenerator(irClasses, this.getJavaGen().getInfo());
			List<ARemoteContractImplDeclIR> remoteImpls = implsGen.run();

			// printRemoteContractsImpl(remoteImpls);

			// System.out.println("**********************CPU deployment**********************");

			int deployedObjCounter = mapping.getDeployedObjCounter();
			Map<String, Set<AVariableExp>> cpuToDeployedObject = mapping.getCpuToDeployedObject();
			Map<String, Set<String>> cpuToConnectedCPUs = mapping.cpuToConnectedCPUs();
			Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses = mapping.cpuToDeployedClasses();

			// Generate the RMI server
			generateRMIserver(output_dir, 1099, remoteContracts);

			// Distributed the generate remote contracts and their implementation
			generateRemConAndRemConImp(output_dir, remoteContracts, cpuToDeployedClasses, cpuToConnectedCPUs, remoteImpls, data);

			// Generate entry method for each CPU and the local system class
			generateEntryAndSystemClass(output_dir, cpuToDeployedObject, cpuToConnectedCPUs, deployedObjCounter);

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: " + e.getMessage());
		}

	}

	public JavaCodeGen getJavaGen()
	{
		return this.javaGen;
	}

	public JavaFormat getJavaFormat()
	{
		return this.javaGen.getJavaFormat();
	}

	public IRSettings getIrSettings()
	{
		return this.javaGen.getSettings();
	}

	public JavaSettings getJavaSettings()
	{
		return this.javaGen.getJavaSettings();
	}

	/*
	 * In this the ARMIServerDeclCG node is set up in order to code generate the global registration service. In
	 * addition, the generate remote contracts and remote contract implementation are printed to files inside the
	 * relevant CPUs in order to test the code. For this reason, the path is currently fix to a local path.
	 */

	public void generateRMIserver(String output_dir, int portNumber, Set<ARemoteContractDeclIR> remoteContracts)
			throws org.overture.codegen.ir.analysis.AnalysisException, IOException
	{
		// Create the RMI server

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		ARMIServerDeclIR rmiServer = new ARMIServerDeclIR();

		String RMI_ServerName = "RMI_Server";

		rmiServer.setPortNumber(portNumber);
		rmiServer.setName(RMI_ServerName);

		StringWriter writer_s = new StringWriter();
		rmiServer.apply(printer, writer_s);

		File theDir_s = new File(output_dir + RMI_ServerName);

		theDir_s.mkdir();

		File file_s = new File(output_dir + RMI_ServerName + "/" + mvn_path  + RMI_ServerName + ".java");
		BufferedWriter output_s = new BufferedWriter(new FileWriter(file_s));
		output_s.write(JavaCodeGenUtil.formatJavaCode(writer_s.toString()));
		output_s.flush();
		output_s.close();

		// Print remote contracts to the RMI server
		for (ARemoteContractDeclIR contract : remoteContracts)
		{
			StringWriter writer_i = new StringWriter();
			contract.apply(printer, writer_i);

			File file_i = new File(output_dir + RMI_ServerName + "/" + mvn_path  + contract.getName() + ".java");
			BufferedWriter output_i = new BufferedWriter(new FileWriter(file_i));
			output_i.write(JavaCodeGenUtil.formatJavaCode(writer_i.toString()));
			output_i.flush();
			output_i.close();
		}

		// Add SyncToken interface to the RMI server directory
		ASynchTokenInterfaceDeclIR synchToken_interface_RMI = new ASynchTokenInterfaceDeclIR();

		StringWriter writer_synch_i_RMI = new StringWriter();
		synchToken_interface_RMI.apply(printer, writer_synch_i_RMI);

		File file_synch_i_RMI = new File(output_dir + RMI_ServerName + "/" + mvn_path  + "SynchToken_interface.java");
		BufferedWriter output_synch_i_RMI = new BufferedWriter(new FileWriter(file_synch_i_RMI));
		output_synch_i_RMI.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i_RMI.toString()));
		output_synch_i_RMI.flush();
		output_synch_i_RMI.close();
	}

	// Create a directory for every CPU, and place relevant remote contracts
	// and remote contract implementations inside

	public void generateRemConAndRemConImp(String output_dir, Set<ARemoteContractDeclIR> remoteContracts,
			Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses, Map<String, Set<String>> cpuToConnectedCPUs,
			List<ARemoteContractImplDeclIR> remoteImpls, GeneratedData data)
					throws org.overture.codegen.ir.analysis.AnalysisException, IOException
	{

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		for (String cpu : cpuToDeployedClasses.keySet())
		{
			File theDir = new File(output_dir + cpu);
			theDir.mkdir();
		}

		for (String cpu : cpuToDeployedClasses.keySet())
		{

			int size = cpuToDeployedClasses.get(cpu).size();
			for (int i = 0; i < size; i++)
			{
				// Generate a SynchToken and its interface for each CPU
				ASynchTokenDeclIR synchToken = new ASynchTokenDeclIR();

				StringWriter writer_synch = new StringWriter();
				synchToken.apply(printer, writer_synch);

				File file_synch = new File(output_dir + cpu + "/" + mvn_path  + "SynchToken.java");
				BufferedWriter output_synch = new BufferedWriter(new FileWriter(file_synch));
				output_synch.write(JavaCodeGenUtil.formatJavaCode(writer_synch.toString()));
				output_synch.flush();
				output_synch.close();

				ASynchTokenInterfaceDeclIR synchToken_interface = new ASynchTokenInterfaceDeclIR();

				StringWriter writer_synch_i = new StringWriter();
				synchToken_interface.apply(printer, writer_synch_i);

				File file_synch_i = new File(output_dir + cpu + "/" + mvn_path  + "SynchToken_interface.java");
				BufferedWriter output_synch_i = new BufferedWriter(new FileWriter(file_synch_i));
				output_synch_i.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i.toString()));
				output_synch_i.flush();
				output_synch_i.close();

				List<GeneratedModule> qutes = data.getQuoteValues();
				
				for(GeneratedModule q : qutes){
					String javaFileName = q.getName();
					javaFileName += JavaQuoteValueCreator.JAVA_QUOTE_NAME_SUFFIX;
					javaFileName += IJavaConstants.JAVA_FILE_EXTENSION;
					CodeGenBase.emitCode(new File(output_dir + cpu + "/" + mvn_path + "quotes"), javaFileName, q.getContent());
				}
				
				for(GeneratedModule li : data.getClasses()){
					if(li.getIrNode() instanceof AInterfaceDeclIR)
						{
							AInterfaceDeclIR hi = (AInterfaceDeclIR) li.getIrNode();
							
							LinkedList<AMethodDeclIR> met = hi.getMethodSignatures();
							
							for(AMethodDeclIR m:met){
								AExternalTypeIR runtimeExpType = new AExternalTypeIR();
								runtimeExpType.setName("java.rmi.RemoteException");
								m.getRaises().add(runtimeExpType);
							}
							
							String tr = li.getContent();
							
							
							
							String javaFileName = li.getName();
							javaFileName += IJavaConstants.JAVA_FILE_EXTENSION;
							CodeGenBase.emitCode(new File(output_dir + cpu + "/" + mvn_path), javaFileName, li.getContent());
						}
				}
				
				for (ARemoteContractDeclIR contract : remoteContracts)
				{
					StringWriter writer = new StringWriter();
					contract.apply(printer, writer);

					File file = new File(output_dir + cpu + "/" + mvn_path  +  contract.getName() + ".java");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
					output.flush();
					output.close();
				}

				for (ARemoteContractImplDeclIR impl : remoteImpls)
				{
					StringWriter writer = new StringWriter();
					impl.apply(printer, writer);
					
					// Filter these methods currently
					//if(impl.getName().equals("bridge_FieldGraph")) continue;
					if(impl.getName().equals("MyUtils")) continue;
					
					File file = new File(output_dir + cpu + "/" + mvn_path + impl.getName() + ".java");
					BufferedWriter output = new BufferedWriter(new FileWriter(file));
					output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
					output.flush();
					output.close();
				}
			}
		}
	}

	public void generateEntryAndSystemClass(String output_dir, Map<String, Set<AVariableExp>> cpuToDeployedObject,
			Map<String, Set<String>> cpuToConnectedCPUs, int DeployedObjCounter)
					throws AnalysisException, org.overture.codegen.ir.analysis.AnalysisException, IOException
	{

		JavaFormat javaFormat = getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		CPUdeploymentGenerator cpuDepGenerator = new CPUdeploymentGenerator(cpuToDeployedObject, cpuToConnectedCPUs, DeployedObjCounter, system_fields);
		Set<ACpuDeploymentDeclIR> cpuDeps = cpuDepGenerator.run();
		Map<String, ADefaultClassDeclIR> cpuToSystemDecl = cpuDepGenerator.getcpuToSystemDecl();

		// Distribute the CPU deployment for each CPU
		for (ACpuDeploymentDeclIR impl : cpuDeps)
		{
			StringWriter writer = new StringWriter();
			impl.apply(printer, writer);

			// System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));

			// FIXME: Do not print entry method currently
			
			// The CPU entry method
			//File file = new File(output_dir + impl.getCpuName() + "/" + impl.getCpuName() + ".java");
			//BufferedWriter output = new BufferedWriter(new FileWriter(file));
			//System.out.println("The entry method for: " + impl.getCpuName());
			// System.out.println(writer.toString());
			//output.write(JavaCodeGenUtil.formatJavaCode(writer.toString()));
			//output.close();

			
			
			// Create the unique system class for each CPU
			ADefaultClassDeclIR systemClass = cpuToSystemDecl.get(impl.getCpuName());
			StringWriter writer2 = new StringWriter();
			systemClass.apply(printer, writer2);
			// System.out.println(JavaCodeGenUtil.formatJavaCode(writer2.toString()));
			
			File file2 = new File(output_dir + impl.getCpuName() + "/" + mvn_path  + systemClass.getName() + ".java");

			System.out.println("The system class for: " + impl.getCpuName());
			// System.out.println(writer2.toString());

			BufferedWriter output2 = new BufferedWriter(new FileWriter(file2));
			output2.write(JavaCodeGenUtil.formatJavaCode(writer2.toString()));
			output2.close();

		}

	}

	public void printRemoteContracts(Set<ARemoteContractDeclIR> remoteContracts)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		System.out.println("**********************Remote contracts**********************");
		JavaFormat javaFormat = getJavaGen().getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();

		for (ARemoteContractDeclIR contract : remoteContracts)
		{
			StringWriter writer = new StringWriter();
			contract.apply(printer, writer);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));
		}
	}

	public void printRemoteContractsImpl(List<ARemoteContractImplDeclIR> remoteImpls)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		System.out.println("**********************Remote contracts implementation**********************");
		JavaFormat javaFormat = getJavaGen().getJavaFormat();
		MergeVisitor printer = javaFormat.getMergeVisitor();
		for (ARemoteContractImplDeclIR impl : remoteImpls)
		{
			StringWriter writer = new StringWriter();
			impl.apply(printer, writer);

			System.out.println(JavaCodeGenUtil.formatJavaCode(writer.toString()));
		}
	}

	@Override
	public List<IRStatus<PIR>> initialIRConstructed(List<IRStatus<PIR>> ast, IRInfo info) {
		// This method received the initial version of the IR before it is
		// transformed
		
		Logger.getLog().println("Initial IR has " + ast.size() + " node(s)");
		
		return ast;
	}

	@Override
	public List<IRStatus<PIR>> finalIRConstructed(List<IRStatus<PIR>> ast, IRInfo info) {
//		// The final version of the IR
//
		
		ACpuDeploymentDeclIR dep = new ACpuDeploymentDeclIR();
		
		IRStatus<PIR> stat = new IRStatus<PIR>(null, "Demo", dep, new HashSet<VdmNodeInfo>(), new HashSet<IrNodeInfo>());
		LinkedList<IRStatus> li = new LinkedList<IRStatus>();
		
		li.add(stat);
		
//		ast.add(cpuD);
//		ast.a
		ast.add(stat);
		
		Logger.getLog().println("Final version of the IR has " + ast.size() + " node(s)");
		
		return ast;
	}

//	@Override
//	protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses) throws AnalysisException {
//		// TODO Auto-generated method stub
////		return this.javaGen.generate(CodeGenBase.getNodes(statuses));
//		return null;
//	}
	
	
//	@Override
//	public List<IRStatus<PCG>> initialIRConstructed(List<IRStatus<PCG>> ast, IRInfo info)
//	{
//		// This method received the initial version of the IR before it is
//		// transformed
//		Logger.getLog().println("Initial IR has " + ast.size() + " node(s)");
//
//		// For an example of how to process/modify the IR see
//		// org.overture.codegen.vdm2jml.JmlGenerator
//
//		// Return the (possibly modified) AST that the Java code generator
//		// should use subsequently
//		return ast;
//	}

//	@Override
//	public List<IRStatus<PCG>> finalIRConstructed(List<IRStatus<PCG>> ast, IRInfo info)
//	{
//		// The final version of the IR
//
//		Logger.getLog().println("Final version of the IR has " + ast.size() + " node(s)");
//
//		return ast;
//	}
}
