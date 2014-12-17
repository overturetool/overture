package org.overture.codegen.vdm2java.rt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;

/*
 * This set up the remote contracts for the generated Java code
 * according to the description in the main report
 * Here only signatures of methods are set up, because it is a
 * remote contract
 * 
 * Sets up the ARemoteContractDeclCG node
 */

public class RemoteContractGenerator {

	private List<AClassDeclCG> irClasses;

	public RemoteContractGenerator(List<AClassDeclCG> irClasses) {
		super();
		this.irClasses = irClasses;
	}

	public Set<ARemoteContractDeclCG> run() throws AnalysisException {

		Set<ARemoteContractDeclCG> remoteContracts = new HashSet<ARemoteContractDeclCG>();

		for(AClassDeclCG classCg : irClasses){

			String currentName = classCg.getName().toString();

			ARemoteContractDeclCG remoteContract = new ARemoteContractDeclCG();

			remoteContract.setName(currentName + "_i"); // transform name

			for(AMethodDeclCG method : classCg.getMethods()){

				AMethodDeclCG methodSignature = method.clone();

				// Skip the auto generated toString() method
				if(methodSignature.getName().equals("toString")){
				}
				else if(methodSignature.getAccess().equals("public")){ // if public add to remote contract

					if(methodSignature.getIsConstructor()) continue;
					methodSignature.setIsRemote(true);
					methodSignature.setAbstract(false);
					methodSignature.setBody(null);
					methodSignature.setStatic(false);
					remoteContract.getMethodSignatures().add(methodSignature);
				}
			}
			remoteContracts.add(remoteContract);
		}
		return remoteContracts;
	}
}