package org.overture.codegen.vdm2java.rt;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;

public class RemoteContractGenerator {

	private List<AClassDeclCG> irClasses;

	public RemoteContractGenerator(List<AClassDeclCG> irClasses) {
		super();
		this.irClasses = irClasses;
	}

	public Set<ARemoteContractDeclCG> run() throws AnalysisException {
		// TODO Auto-generated method stub

		Set<ARemoteContractDeclCG> remoteContracts = new HashSet<ARemoteContractDeclCG>();

		for(AClassDeclCG classCg : irClasses){

			String currentName = classCg.getName().toString();

			ARemoteContractDeclCG remoteContract = new ARemoteContractDeclCG();

			remoteContract.setName(currentName + "_i");

			for(AMethodDeclCG method : classCg.getMethods()){

				AMethodDeclCG methodSignature = method.clone();

				if(methodSignature.getName().equals("toString")){
					//					method.setIsRemote(true);
					//					publicMethods.add(method);
				}
				else if(methodSignature.getAccess().equals("public")){

					if(methodSignature.getIsConstructor()) continue;
					
					LinkedList<AFormalParamLocalParamCG> formalParams = (LinkedList<AFormalParamLocalParamCG>) methodSignature.getFormalParams().clone();

					LinkedList<AFormalParamLocalParamCG> formalParams2 = new LinkedList<AFormalParamLocalParamCG>();

					methodSignature.setFormalParams(null);

					for(AFormalParamLocalParamCG formPar : formalParams){
						STypeCG ty = formPar.getType().clone();
						if(ty instanceof AClassTypeCG){
							AClassTypeCG classTy = (AClassTypeCG) ty;
							String className = classTy.getName().toString();
							classTy.setName(className + "_i");
							formPar.setType(classTy);
						}
						formalParams2.add(formPar);
					}
					methodSignature.setFormalParams(formalParams2);
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
