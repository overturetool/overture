package org.overture.codegen.rt2rmi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.declarations.AClientInstanceDeclCG;
import org.overture.codegen.cgast.declarations.ACpuDeploymentDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.ARMIregistryDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteInstanceDeclCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;

/*
 * This sets up the relevant entry method for each CPU
 * In addition, its sets up parameters relevant for the 
 * initialisation for each CPU, with the number of deployed objects
 * and number of CPUs in the system.
 * 
 * Sets up the node called ACpuDeploymentDeclCG
 */

public class CPUdeploymentGenerator {


	private Map<String, Set<AVariableExp>> cpuToDeployedObject;
	private Map<String, Set<String>> cpuToConnectedCPUs;
	Map<String, ADefaultClassDeclCG> cpuToSystemDecl = new HashMap<String, ADefaultClassDeclCG>();
	private int DeployedObjCounter;

	public CPUdeploymentGenerator(
			Map<String, Set<AVariableExp>> cpuToDeployedObject, 
			Map<String, Set<String>> cpuToConnectedCPUs, 
			int DeployedObjCounter) {
		super();
		this.cpuToDeployedObject = cpuToDeployedObject;
		this.cpuToConnectedCPUs = cpuToConnectedCPUs;
		this.DeployedObjCounter = DeployedObjCounter;
	}

	public Set<ACpuDeploymentDeclCG> run() throws AnalysisException {

		Set<ACpuDeploymentDeclCG> cpuDeployments = new HashSet<ACpuDeploymentDeclCG>();

		// Number of CPU
		int numberofCPUs = cpuToDeployedObject.keySet().size();
		
		for(String cpuDep : cpuToDeployedObject.keySet()){
			
			ACpuDeploymentDeclCG cpuDeployment = new ACpuDeploymentDeclCG();
			cpuDeployment.setCpuName(cpuDep);
			// Set the number of deployed objects
			cpuDeployment.setDeployedObjCounter(DeployedObjCounter);
			// Set number of total CPUs
			cpuDeployment.setNumberofCPUs(numberofCPUs);
			
			ARMIregistryDeclCG rmiReg = new ARMIregistryDeclCG();

			String URL = "localhost";
			int PortNumber = 1099;

			rmiReg.setFuncName("LocateRegistry.getRegistry");
			rmiReg.setPortNumber(PortNumber);
			rmiReg.setURL("\""+ URL +"\"");

			cpuDeployment.setRMIreg(rmiReg);

			Set<String> cpuSet = cpuToConnectedCPUs.get(cpuDep);

			ADefaultClassDeclCG systemClass = new ADefaultClassDeclCG();
			systemClass.setAccess("public");
			ASystemClassDefinition sysClass = null;
			for(String cpuCon : cpuSet){

				Set<AVariableExp> depObjSet = cpuToDeployedObject.get(cpuCon);

				// The objects which are "lookup"
				for(AVariableExp depObj : depObjSet){
					
					// For the system class

					AClassTypeCG classType = new AClassTypeCG();
					classType.setName(depObj.getType().toString() + "_i");
					sysClass = depObj.getAncestor(ASystemClassDefinition.class);
					AFieldDeclCG deploydObj = new AFieldDeclCG();
					deploydObj.setStatic(true);
					deploydObj.setFinal(false);
					deploydObj.setAccess("public");
					deploydObj.setType(classType);
					deploydObj.setName(depObj.getName().getName().toString());
					deploydObj.setInitial(new ANullExpCG());

					systemClass.getFields().add(deploydObj);
					
					// For each deployed object
					
					AClientInstanceDeclCG clientObj = new AClientInstanceDeclCG();

					clientObj.setName(sysClass.getName().toString() + "." + depObj.getName().getName().toString());
					clientObj.setClassName(depObj.getType().toString() + "_i");

					clientObj.setNameString("\""+depObj.getName().getName().toString()+"\"");

					cpuDeployment.getClientInst().add(clientObj);
				}

				systemClass.setName(sysClass.getName().toString());
			}

			for(AVariableExp inst_var : cpuToDeployedObject.get(cpuDep)){
				//ARemoteInstanceDeclCG inst = (ARemoteInstanceDeclCG) inst_var.apply(info.getExpVisitor(), info);
				//inst.setName(inst_var.getName().getName().toString());
				//inst.setClassName(inst_var.getType().toString());

				ARemoteInstanceDeclCG inst = new ARemoteInstanceDeclCG();

				inst.setName(sysClass.getName().toString() + "." + inst_var.getName().getName().toString());
				inst.setClassName(inst_var.getType().toString());

				AInstanceVariableDefinition varExp = (AInstanceVariableDefinition) inst_var.getVardef();
				inst.setVarExp(varExp.getExpression().toString());
				inst.setNameString("\""+inst_var.getName().getName().toString()+"\"");

				cpuDeployment.getRemoteInst().add(inst);

				cpuDeployment.setCpuNameString("\"" + cpuDep + "\"");

				AClassTypeCG classType = new AClassTypeCG();
				classType.setName(inst_var.getType().toString());
				sysClass = inst_var.getAncestor(ASystemClassDefinition.class);
				AFieldDeclCG deploydObj = new AFieldDeclCG();
				deploydObj.setStatic(true);
				deploydObj.setFinal(false);
				deploydObj.setAccess("public");
				deploydObj.setType(classType);
				deploydObj.setName(inst_var.getName().getName().toString());
				deploydObj.setInitial(new ANullExpCG());

				systemClass.getFields().add(deploydObj);
			}
			cpuDeployments.add(cpuDeployment);
			cpuToSystemDecl.put(cpuDep, systemClass);
		}	
		return cpuDeployments;
	}

	public Map<String, ADefaultClassDeclCG> getcpuToSystemDecl(){
		return cpuToSystemDecl;
	}

}
