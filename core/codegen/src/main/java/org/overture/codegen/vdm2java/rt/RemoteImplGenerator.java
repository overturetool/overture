package org.overture.codegen.vdm2java.rt;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;

public class RemoteImplGenerator {

	private List<AClassDeclCG> irClasses;

	public RemoteImplGenerator(List<AClassDeclCG> irClasses) {
		super();
		this.irClasses = irClasses;
	}
	
	public List<ARemoteContractImplDeclCG> run() {
		List<ARemoteContractImplDeclCG> contractImpls = new LinkedList<ARemoteContractImplDeclCG>();
		
		for(AClassDeclCG classCg : irClasses)
		{
			List<AMethodDeclCG> publicMethods = new LinkedList<AMethodDeclCG>();
			ARemoteContractImplDeclCG contractImpl = new ARemoteContractImplDeclCG();
			LinkedList<AMethodDeclCG> a = classCg.getMethods();
			contractImpl.setName(classCg.getName());
			
			for(AMethodDeclCG method : classCg.getMethods()){
				
				if(method.getName().equals("toString")){
//					method.setIsRemote(true);
//					publicMethods.add(method);
				}
				else if(method.getAccess().equals("public")){ //&& !method.getIsConstructor()){
					method.setIsRemote(true);
					publicMethods.add(method);
				}
//									if(method.getAccess().equals("public") && method.getIsConstructor() != null && !method.getIsConstructor()){
//				if(method.getName().equals(classCg.getName())){
//					continue;
//				}
//				else if(method.getAccess().equals("public")){
//					method.setIsRemote(true);
//					publicMethods.add(method);
//				}	
			}
			
			contractImpl.setMethods(publicMethods);
			
			contractImpls.add(contractImpl);
		}
		
		return contractImpls;
	}
	
}
