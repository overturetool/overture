package org.overture.codegen.rt2rmi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.cgrmi.extast.declarations.ARemoteContractDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.IRInfo;

/*
 * This set up the remote contracts for the generated Java code
 * according to the description in the main report
 * Here only signatures of methods are set up, because it is a
 * remote contract
 * 
 * Sets up the ARemoteContractDeclCG node
 */

public class RemoteContractGenerator {

	private List<ADefaultClassDeclIR> irClasses;

	private IRInfo info;
	
	public static final String MULTIPLE_INHERITANCE_WARNING = "Code generation of RT models is not supported for multiple inheritance models";
	
	public RemoteContractGenerator(List<ADefaultClassDeclIR> irClasses, IRInfo info) {
		super();
		this.irClasses = irClasses;
		this.info = info;
	}

	public Set<ARemoteContractDeclIR> run() throws AnalysisException {

		for(ADefaultClassDeclIR classCg : irClasses)
		{
			if (classCg.getSuperNames().size() > 1)
			{
				info.addTransformationWarning(classCg, MULTIPLE_INHERITANCE_WARNING);
				return new HashSet<>();
			}
			
		}
		
		Set<ARemoteContractDeclIR> remoteContracts = new HashSet<ARemoteContractDeclIR>();

		for(ADefaultClassDeclIR classCg : irClasses){

			String currentName = classCg.getName().toString();

			ARemoteContractDeclIR remoteContract = new ARemoteContractDeclIR();

			remoteContract.setName(currentName + "_i"); // transform name

			if(classCg.getSuperNames().isEmpty()) {
				remoteContract.setIsSuperClass(true);
				remoteContract.setSuperName(currentName);
			}
			else{
				remoteContract.setIsSuperClass(false);
				remoteContract.setSuperName(classCg.getSuperNames().getFirst().getName()+"_i");
			}
			
			for(AMethodDeclIR method : classCg.getMethods()){

				AMethodDeclIR methodSignature = method.clone();
				
				// Skip the auto generated toString() method
				if(methodSignature.getName().equals("toString")){
				}
				else if(methodSignature.getAccess().equals("public")){// && methodSignature.getStatic()==null){ // if public add to remote contract

					if(methodSignature.getIsConstructor()) continue;
					methodSignature.setAbstract(false);
					methodSignature.setBody(null);
					//methodSignature.setStatic(false);
					
					remoteContract.getMethodSignatures().add(methodSignature);
				}
			}
			remoteContracts.add(remoteContract);
		}
		return remoteContracts;
	}
}