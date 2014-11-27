package org.overture.codegen.vdm2java.rt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;

public class RemoteContractDistribution {



	private Map<String, Set<String>> cpuToConnectedCPUs;
	private Set<ARemoteContractDeclCG> remoteContracts;
	private Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses;
	private List<ARemoteContractImplDeclCG> remoteImpls;

	public RemoteContractDistribution(
			Set<ARemoteContractDeclCG> remoteContracts, Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses, Map<String, Set<String>> cpuToConnectedCPUs, List<ARemoteContractImplDeclCG> remoteImpls) {
		this.cpuToDeployedClasses = cpuToDeployedClasses;
		this.remoteContracts = remoteContracts;
		this.cpuToConnectedCPUs = cpuToConnectedCPUs;
		this.remoteImpls = remoteImpls;
	}

	public void run() throws AnalysisException, IOException {

		// TODO: Do nicely
		IRInfo info = new IRInfo("cg_init");
		JavaFormat javaFormat = new JavaFormat(new TempVarPrefixes(), info);
		MergeVisitor printer = javaFormat.getMergeVisitor();

		//System.out.println("**********************Remote contracts**********************");

		// Create a directory for every cpu

		for(String cpu : cpuToDeployedClasses.keySet()){
			File theDir = new File("/Users/Miran/Documents/files/" + cpu);

			theDir.mkdir();
		}


		for(String cpu : cpuToDeployedClasses.keySet()){


			for(AClassClassDefinition clas : cpuToDeployedClasses.get(cpu)){


				for (ARemoteContractDeclCG contract : remoteContracts) {
					//if(contract.getName().equals(clas.getName().toString() + "_i")){
						StringWriter writer = new StringWriter();
						contract.apply(printer, writer);

						File file = new File("/Users/Miran/Documents/files/" + cpu + "/" + contract.getName() + ".java");
						BufferedWriter output = new BufferedWriter(new FileWriter(file));
						output.write(JavaCodeGenUtil.formatJavaCode(writer
								.toString()));
						output.close();
						
//						for(String conectedCpu : cpuToConnectedCPUs.get(cpu)){
//							File file2 = new File("/Users/Miran/Documents/files/" + conectedCpu + "/" + contract.getName() + ".java");
//							BufferedWriter output2 = new BufferedWriter(new FileWriter(file2));
//							output2.write(JavaCodeGenUtil.formatJavaCode(writer
//									.toString()));
//							output2.close();
//						}
					//}
				}

				for (ARemoteContractImplDeclCG impl : remoteImpls) {
					//if(impl.getName().equals(clas.getName().toString())){
						StringWriter writer = new StringWriter();
						impl.apply(printer, writer);

						File file = new File("/Users/Miran/Documents/files/" + cpu + "/" + impl.getName() + ".java");
						BufferedWriter output = new BufferedWriter(new FileWriter(file));
						output.write(JavaCodeGenUtil.formatJavaCode(writer
								.toString()));
						output.close();
					//}
				}
			}
		}
		//        File file = new File("/Users/Miran/Documents/files/cpu/example.java");
		//        BufferedWriter output = new BufferedWriter(new FileWriter(file));
		//        output.write("Hell");
		//        output.close();

	}

}
