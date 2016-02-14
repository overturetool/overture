package org.overture.codegen.rt2rmi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.cgrmi.extast.declarations.AClientInstanceDeclIR;
import org.overture.cgrmi.extast.declarations.ACpuDeploymentDeclIR;
import org.overture.cgrmi.extast.declarations.ARMIregistryDeclIR;
import org.overture.cgrmi.extast.declarations.ARemoteInstanceDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.ir.types.AClassTypeIR;

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
	Map<String, ADefaultClassDeclIR> cpuToSystemDecl = new HashMap<String, ADefaultClassDeclIR>();
	private int DeployedObjCounter;
	LinkedList<AFieldDeclIR> system_fields;

	public CPUdeploymentGenerator(
			Map<String, Set<AVariableExp>> cpuToDeployedObject, 
			Map<String, Set<String>> cpuToConnectedCPUs, 
			int DeployedObjCounter,
			LinkedList<AFieldDeclIR> system_fields) {
		super();
		this.cpuToDeployedObject = cpuToDeployedObject;
		this.cpuToConnectedCPUs = cpuToConnectedCPUs;
		this.DeployedObjCounter = DeployedObjCounter;
		this.system_fields=system_fields;
	}

	public Set<ACpuDeploymentDeclIR> run() throws AnalysisException {

		Set<ACpuDeploymentDeclIR> cpuDeployments = new HashSet<ACpuDeploymentDeclIR>();

		// Number of CPU
		int numberofCPUs = cpuToDeployedObject.keySet().size();
		
		for(String cpuDep : cpuToDeployedObject.keySet()){
			
			ACpuDeploymentDeclIR cpuDeployment = new ACpuDeploymentDeclIR();
			cpuDeployment.setCpuName(cpuDep);
			// Set the number of deployed objects
			cpuDeployment.setDeployedObjCounter(DeployedObjCounter);
			// Set number of total CPUs
			cpuDeployment.setNumberofCPUs(numberofCPUs);
			
			ARMIregistryDeclIR rmiReg = new ARMIregistryDeclIR();

			String URL = "localhost";
			int PortNumber = 1099;

			rmiReg.setFuncName("LocateRegistry.getRegistry");
			rmiReg.setPortNumber(PortNumber);
			rmiReg.setURL("\""+ URL +"\"");

			cpuDeployment.setRMIreg(rmiReg);

			Set<String> cpuSet = cpuToConnectedCPUs.get(cpuDep);

			ADefaultClassDeclIR systemClass = new ADefaultClassDeclIR();
			systemClass.setAccess("public");
			ASystemClassDefinition sysClass = null;
			for(String cpuCon : cpuSet){

				Set<AVariableExp> depObjSet = cpuToDeployedObject.get(cpuCon);

				// The objects which are "lookup"
				for(AVariableExp depObj : depObjSet){
					
					// For the system class

					AClassTypeIR classType = new AClassTypeIR();
					classType.setName(depObj.getType().toString() + "_i");
					sysClass = depObj.getAncestor(ASystemClassDefinition.class);
					AFieldDeclIR deploydObj = new AFieldDeclIR();
					deploydObj.setStatic(true);
					deploydObj.setFinal(false);
					deploydObj.setAccess("public");
					deploydObj.setType(classType);
					deploydObj.setName(depObj.getName().getName().toString());
					deploydObj.setInitial(new ANullExpIR());

					systemClass.getFields().add(deploydObj);
					
					// For each deployed object
					
					AClientInstanceDeclIR clientObj = new AClientInstanceDeclIR();

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

				ARemoteInstanceDeclIR inst = new ARemoteInstanceDeclIR();

				inst.setName(sysClass.getName().toString() + "." + inst_var.getName().getName().toString());
				inst.setClassName(inst_var.getType().toString());

				AInstanceVariableDefinition varExp = (AInstanceVariableDefinition) inst_var.getVardef();
				
				
				for(AFieldDeclIR field : system_fields){
					if(inst_var.getName().getName().toString().equals(field.getName())){
						inst.setInitial(field.getInitial());
					}
				}
				
				inst.setVarExp(varExp.getExpression().toString());
				
				inst.setNameString("\""+inst_var.getName().getName().toString()+"\"");

				cpuDeployment.getRemoteInst().add(inst);

				cpuDeployment.setCpuNameString("\"" + cpuDep + "\"");

				AClassTypeIR classType = new AClassTypeIR();
				classType.setName(inst_var.getType().toString());
				sysClass = inst_var.getAncestor(ASystemClassDefinition.class);
				AFieldDeclIR deploydObj = new AFieldDeclIR();
				deploydObj.setStatic(true);
				deploydObj.setFinal(false);
				deploydObj.setAccess("public");
				deploydObj.setType(classType);
				deploydObj.setName(inst_var.getName().getName().toString());
				deploydObj.setInitial(new ANullExpIR());

				systemClass.getFields().add(deploydObj);
			}
			cpuDeployments.add(cpuDeployment);
			cpuToSystemDecl.put(cpuDep, systemClass);
		}	
		return cpuDeployments;
	}

	public Map<String, ADefaultClassDeclIR> getcpuToSystemDecl(){
		return cpuToSystemDecl;
	}

}
