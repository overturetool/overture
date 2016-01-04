package org.overture.codegen.rt2rmi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRGeneratedTag;

/*
 * This set up the remote contracts for the generated Java code
 * according to the description in the main report
 * Here only signatures of methods are set up, because it is a
 * remote contract
 * 
 * Sets up the ARemoteContractDeclCG node
 */

public class RemoteContractGenerator {

	private List<ADefaultClassDeclCG> irClasses;

	public RemoteContractGenerator(List<ADefaultClassDeclCG> irClasses) {
		super();
		this.irClasses = irClasses;
	}

	public Set<ARemoteContractDeclCG> run() throws AnalysisException {

		Set<ARemoteContractDeclCG> remoteContracts = new HashSet<ARemoteContractDeclCG>();

		for (ADefaultClassDeclCG classCg : irClasses) {

			String currentName = classCg.getName().toString();

			ARemoteContractDeclCG remoteContract = new ARemoteContractDeclCG();

			remoteContract.setName(currentName + "_i"); // transform name

			if(classCg.getThread() != null){
				AMethodDeclCG methodSignature = new AMethodDeclCG();
				methodSignature.setName("start");
				methodSignature.setIsRemote(true);
				methodSignature.setAbstract(false);
				methodSignature.setBody(null);
				methodSignature.setStatic(false);
				methodSignature.setAccess("public");
				methodSignature.setFormalParams(null);
				methodSignature.setIsConstructor(false);
				AMethodTypeCG methodType = new AMethodTypeCG();
				methodType.setParams(null);
				methodType.setResult(new AVoidTypeCG());
				methodSignature.setMethodType(methodType);
				remoteContract.getMethodSignatures().add(methodSignature);
				
			}
			
			for (AMethodDeclCG method : classCg.getMethods()) {

				AMethodDeclCG methodSignature = method.clone();

				// Skip the auto generated toString() method
				if (methodSignature.getName().equals("toString")) {
				} else if (methodSignature.getAccess().equals("public") && !isIRGenerated(methodSignature)) { 

					if (methodSignature.getIsConstructor() != null && methodSignature.getIsConstructor())
						continue;
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

	private boolean isIRGenerated(AMethodDeclCG method) {
		return method.getTag() instanceof IRGeneratedTag;
	}
}