/*************************************************
 * 
 * This is a visitor that is used to analyze the
 * system definition, get the distributed 
 * architecture from the VDM-RT model 
 *************************************************/

package org.overture.codegen.rt2rmi.systemanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;

/*
 * This is the visitor of applied in order to analyse
 * the VDM AST, which extracts the connection map, distribution map, 
 * number of CPUs and number of deployed objects
 */

public class NodeSystem extends DepthFirstAnalysisAdaptor {
	
	String SystemName = new String();
	
	// Maps are initialized to store information from the AST VDM that is needed for this project
	
	// This map is the Distribution Map
	Map<String, Set<AVariableExp>> cpuToDeployedObjects = new HashMap<String, Set<AVariableExp>>();
	
	// This map is the Connection Map
	Map<String, Set<String>> cpuToConnectedCPUs = new HashMap<String, Set<String>>();
	Map<String, LinkedList<PExp>> cpuToConnectedCPUsList = new HashMap<String, LinkedList<PExp>>();
		
	// Map to identify the public operations and functions for each deployed class
	Map<AClassClassDefinition, Set<AExplicitOperationDefinition>> deployedClassesToPubOp2 = new HashMap<AClassClassDefinition, Set<AExplicitOperationDefinition>>();
	Map<String, Set<AExplicitOperationDefinition>> deployedObjectsToPubOp = new HashMap<String, Set<AExplicitOperationDefinition>>();

	// Map to identify class deployed for each CPU
	Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses = new HashMap<String, Set<AClassClassDefinition>>();
	
	// Tracks the number of total objects deployed in the distributed system
	public int DeployedObjCounter = 0;
	
	// Method to print the architecture of the VDM-RT model analyzed
	public void printArch() {
		for (String key : cpuToDeployedObjects.keySet()) {
			System.out.println("The CPU " + key + " deploys objects "
					+ cpuToDeployedObjects.get(key) + " and is connected to : "
					+ cpuToConnectedCPUs.get(key));
		}

		for (AClassClassDefinition key : deployedClassesToPubOp2.keySet()) {
			System.out.println("The class: " + key.getName()
					+ " has the following public operations " + deployedClassesToPubOp2.get(key));
		}

	}

	public String getSystemName() {
		return SystemName;
	}
	
	// This analyses the System definition, an extracts
	// the need information about the architecture
	@Override
	public void caseACallObjectStm(ACallObjectStm node)
			throws AnalysisException {

		PObjectDesignator des = node.getDesignator();

		if (des instanceof AIdentifierObjectDesignator) {

			AIdentifierObjectDesignator id = (AIdentifierObjectDesignator) des;

			String op_name = node.getFieldname().getName();
			
			PType idTy = id.getExpression().getType();
			
			String mod_name;
			
			if(idTy!=null) 
				mod_name = id.getExpression().getType().toString();
			else mod_name = "no";
			
			

			//System.out.println("Module name is: " + mod_name);
			
			if (mod_name.equals("CPU")) {
				if (op_name.equals("deploy")) {
					// System.out.println("The cpu name is: " + des);
					// System.out.println("It deploys objects:" +
					// node.getArgs());

					PExp exp = node.getArgs().get(0);

					if (exp instanceof AVariableExp) {
						AVariableExp var_exp = (AVariableExp) exp;

						String key_obj = id.getName().toString();
						
						DeployedObjCounter = DeployedObjCounter + 1;
						
						ASystemClassDefinition systemDef = var_exp.getAncestor(ASystemClassDefinition.class);
						
						SystemName = systemDef.getName().toString();
						
						if (cpuToDeployedObjects.containsKey(key_obj))
							cpuToDeployedObjects.get(key_obj).add(var_exp);
						else {
							Set<AVariableExp> xs = new HashSet();
							xs.add(var_exp);
							cpuToDeployedObjects.put(key_obj, xs);
						}
						// System.out.println(map.get(key_obj));

						PType type = var_exp.getType();

						if (type instanceof AClassType) {
							AClassType var_type = (AClassType) type;
							SClassDefinition classDef = var_type.getClassdef();

							if (classDef instanceof AClassClassDefinition) {
								AClassClassDefinition var_classDef = (AClassClassDefinition) classDef;
								LinkedList<PDefinition> var_op_list = var_classDef
										.getDefinitions();

								for (PDefinition var_op : var_op_list) {
									if (var_op instanceof AExplicitOperationDefinition) {
										AExplicitOperationDefinition var_op_cast = (AExplicitOperationDefinition) var_op;
										if (var_op_cast.getAccess().getAccess() instanceof APublicAccess) {
											// System.out.println("It is a public operation"
											// + var_op_cast.getName());

											cpuToDeployedClassesFunc(key_obj, var_classDef);


											deployedClassesToPubOp2Func(var_classDef, var_op_cast);

											String key_obj2 = var_exp.toString();
											deployedObjectsToPubOpFunc(key_obj2, var_op_cast);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void caseASetEnumSetExp(ASetEnumSetExp node)
			throws AnalysisException {
		// System.out.println("Got in set enum: ");

		LinkedList<PExp> set = (LinkedList<PExp>) node.getMembers().clone();

		LinkedList<PExp> set2 = (LinkedList<PExp>) node.getMembers().clone();

		for (PExp exp : node.getMembers()) {

			if (exp instanceof AVariableExp) {
				set.remove(exp);
				AVariableExp var_exp = (AVariableExp) exp;

				String key_obj = var_exp.getName().getName().toString();
			
				cpuToConnectedCPUsFunc(key_obj, set);
				
				// System.out.println(map2.get(key_obj));
			}

			// System.out.println("Member: " + exp);

			set = (LinkedList<PExp>) node.getMembers().clone();
		}
	}

	// This analyses the BUS structur in order to get the
	// connection map
	@Override
	public void caseANewExp(ANewExp node) throws AnalysisException {


		// Identify when a new BUS is created.
		if (node.getClassdef() instanceof ABusClassDefinition) {
			node.getArgs().get(2).apply(this);
		}
	}
	
	
	public Map<String, Set<AVariableExp>> getCpuToDeployedObject() {
		return cpuToDeployedObjects;
	}

	public Map<String, LinkedList<PExp>> getMap2() {
		return cpuToConnectedCPUsList;
	}

	public Map<String, Set<String>> cpuToConnectedCPUs() {
		return cpuToConnectedCPUs;
	}

	public Map<String, Set<AExplicitOperationDefinition>> getMap3() {
		return deployedObjectsToPubOp;
	}

	public Map<AClassClassDefinition, Set<AExplicitOperationDefinition>> getMap5() {
		return deployedClassesToPubOp2;
	}



	public Map<String, Set<AClassClassDefinition>> getcpuToDeployedClasses() {
		return cpuToDeployedClasses;
	}

	private void deployedObjectsToPubOpFunc(String key_obj2,
			AExplicitOperationDefinition var_op_cast) {
		if (deployedObjectsToPubOp.containsKey(key_obj2))
			deployedObjectsToPubOp.get(key_obj2).add(
					var_op_cast);
		else {
			Set<AExplicitOperationDefinition> xs2 = new HashSet();
			xs2.add(var_op_cast);
			deployedObjectsToPubOp.put(key_obj2, xs2);
		}
		
	}

	private void deployedClassesToPubOp2Func(
			AClassClassDefinition var_classDef,
			AExplicitOperationDefinition var_op_cast) {
		if (deployedClassesToPubOp2.containsKey(var_classDef))
			deployedClassesToPubOp2.get(var_classDef).add(
					var_op_cast);
		else {
			Set<AExplicitOperationDefinition> xs2 = new HashSet();
			xs2.add(var_op_cast);
			deployedClassesToPubOp2.put(var_classDef, xs2);
		}

	}

	private void cpuToDeployedClassesFunc(String key_obj,
			AClassClassDefinition var_classDef) {
		if (cpuToDeployedClasses.containsKey(key_obj))
			cpuToDeployedClasses.get(key_obj).add(
					var_classDef);
		else {
			Set<AClassClassDefinition> xs2 = new HashSet();
			xs2.add(var_classDef);
			cpuToDeployedClasses.put(key_obj, xs2);
		}
	}
	
	private void cpuToConnectedCPUsFunc(String key_obj, LinkedList<PExp> set) {
		
		// TODO: Change at some point
		if (cpuToConnectedCPUsList.containsKey(key_obj))
			cpuToConnectedCPUsList.get(key_obj).addAll(set);
		else {
			cpuToConnectedCPUsList.put(key_obj, set);
		}
		
		// Convert linked list to set
		for (String key : cpuToConnectedCPUsList.keySet()) {

			Set<String> foo = new HashSet();
			for (PExp keyy : cpuToConnectedCPUsList.get(key)) {
				foo.add(keyy.toString());
			}

			cpuToConnectedCPUs.put(key, foo);
		}
		
		
	}
}
