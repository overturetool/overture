package org.overture.codegen.vdm2java.rt;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.ir.IRInfo;

public class RemoteContractGenerator {

	private Set<AClassClassDefinition> classes;
	private IRInfo info;
	
	public RemoteContractGenerator(Set<AClassClassDefinition> classes, IRInfo info) {
		super();
		this.classes = classes;
		this.info = info;
	}

	public Set<ARemoteContractDeclCG> run() throws AnalysisException {
		// TODO Auto-generated method stub

		Set<ARemoteContractDeclCG> remoteContracts = new HashSet<ARemoteContractDeclCG>();
		
		for(AClassClassDefinition currentClass : classes)
		{
			String currentName = currentClass.getName().getName();
			
			ARemoteContractDeclCG remoteContract = new ARemoteContractDeclCG();

			remoteContract.setName(currentName + "_i");
			
			List<AExplicitOperationDefinition> vdmOperations = Util.getPublicOperations(currentClass.getDefinitions());
			
			for(AExplicitOperationDefinition vdmOp : vdmOperations)
			{
				if(vdmOp.getIsConstructor()) continue;
				AMethodDeclCG methodSignature = (AMethodDeclCG) vdmOp.apply(info.getDeclVisitor(), info);
				methodSignature.setIsRemote(true);
				methodSignature.setBody(null);
				methodSignature.setAbstract(true);
//				methodSignature.setAbstract(false);
				remoteContract.getMethodSignatures().add(methodSignature);
			}
			
			remoteContracts.add(remoteContract);
		}
		
		return remoteContracts;
	}
	
}
