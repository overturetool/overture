package org.overture.codegen.vdm2java.rt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.declarations.AClientInstanceDeclCG;
import org.overture.codegen.cgast.declarations.ACpuDeploymentDeclCG;
import org.overture.codegen.cgast.declarations.ARMIregistryDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteInstanceDeclCG;
import org.overture.codegen.ir.IRInfo;

public class CPUdeploymentGenerator {

	
	private Map<String, Set<AVariableExp>> cpuToDeployedObject;
	private IRInfo info;
	private Map<String, Set<String>> cpuToConnectedCPUs;

	public CPUdeploymentGenerator(
			Map<String, Set<AVariableExp>> cpuToDeployedObject, Map<String, Set<String>> cpuToConnectedCPUs,IRInfo info) {
		super();
		this.cpuToDeployedObject = cpuToDeployedObject;
		this.info = info;
		this.cpuToConnectedCPUs = cpuToConnectedCPUs;
	}

	public Set<ACpuDeploymentDeclCG> run() throws AnalysisException {

		Set<ACpuDeploymentDeclCG> cpuDeployments = new HashSet<ACpuDeploymentDeclCG>();
		

		
		for(String cpuDep : cpuToDeployedObject.keySet()){
			
			ACpuDeploymentDeclCG cpuDeployment = new ACpuDeploymentDeclCG();
			cpuDeployment.setCpuName(cpuDep);
			
			ARMIregistryDeclCG rmiReg = new ARMIregistryDeclCG();
			
			String URL = "localhost";
			int PortNumber = 1099;
			
			rmiReg.setFuncName("LocateRegistry.getRegistry");
			rmiReg.setPortNumber(PortNumber);
			rmiReg.setURL("\""+ URL +"\"");
			
			cpuDeployment.setRMIreg(rmiReg);
			
			Set<String> cpuSet = cpuToConnectedCPUs.get(cpuDep);
			
			for(String cpuCon : cpuSet){
				
				Set<AVariableExp> depObjSet = cpuToDeployedObject.get(cpuCon);
				
				for(AVariableExp depObj : depObjSet){
					AClientInstanceDeclCG clientObj = new AClientInstanceDeclCG();
					
					clientObj.setName(depObj.getName().getName().toString());
					clientObj.setClassName(depObj.getType().toString() + "_i");
					
					clientObj.setNameString("\""+depObj.getName().getName().toString()+"\"");
					
					cpuDeployment.getClientInst().add(clientObj);
				}
				
				
			}
			
			

			
			for(AVariableExp inst_var : cpuToDeployedObject.get(cpuDep)){
				//ARemoteInstanceDeclCG inst = (ARemoteInstanceDeclCG) inst_var.apply(info.getExpVisitor(), info);
				//inst.setName(inst_var.getName().getName().toString());
				//inst.setClassName(inst_var.getType().toString());
				
				ARemoteInstanceDeclCG inst = new ARemoteInstanceDeclCG();
				
				inst.setName(inst_var.getName().getName().toString());
				inst.setClassName(inst_var.getType().toString());
				
				AInstanceVariableDefinition varExp = (AInstanceVariableDefinition) inst_var.getVardef();
				inst.setVarExp(varExp.getExpression().toString());
				inst.setNameString("\""+inst_var.getName().getName().toString()+"\"");
				
				cpuDeployment.getRemoteInst().add(inst);
				
				//cpuDeployment.get
			}
			cpuDeployments.add(cpuDeployment);
			
		}	
		return cpuDeployments;
	}
}
