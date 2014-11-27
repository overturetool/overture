package org.overture.codegen.vdm2java.rt;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AVariableExp;

public class DistributionMapping {

	private List<SClassDefinition> allClasses;
	private NodeSystem nodeSys = new NodeSystem();

	public DistributionMapping(List<SClassDefinition> result) {
		this.allClasses = result;
	}

	public void run() {

		for (SClassDefinition classDef : allClasses) {
			try {
				
				classDef.apply(nodeSys);

//				nodeSys.printArch();
//				nodeSys.printInstancePublicOp();
//				nodeSys.CPUdeployFuncion();

			} catch (AnalysisException e) {
				System.out.println("Something went wrong in the visitor!");
				e.printStackTrace();
			}
		}
	}

	public Set<AClassClassDefinition> getDeployedClasses()
	{
		Set<AClassClassDefinition> deployedClasses = new HashSet<AClassClassDefinition>();
		
		for(String key : nodeSys.getcpuToDeployedClasses().keySet()){
			deployedClasses.addAll(nodeSys.getcpuToDeployedClasses().get(key));
		}
		return deployedClasses;
	}
	
	public Set<AVariableExp> getDeployedObjects()
	{
		Set<AVariableExp> deployedObjects = new HashSet<AVariableExp>();
		
		for(String key : nodeSys.getCpuToDeployedObject().keySet()){
			deployedObjects.addAll(nodeSys.getCpuToDeployedObject().get(key));
		}
		return deployedObjects;
	}
	
	public Map<String, Set<AVariableExp>> getCpuToDeployedObject(){
		return nodeSys.cpuToDeployedObjects;
	}
	
	public Map<String, Set<String>> cpuToConnectedCPUs(){
		return nodeSys.cpuToConnectedCPUs;
	}
	
	public Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses(){
		return nodeSys.cpuToDeployedClasses;
	}
	
	public int getDeployedObjCounter(){
		return nodeSys.DeployedObjCounter;
	}
	
	public String getSystemName(){
		return nodeSys.getSystemName();
	}
}
