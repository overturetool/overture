package org.overture.codegen.vdm2java.rt;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;
import org.overture.codegen.cgast.types.AClassTypeCG;

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
			contractImpl.setFields(classCg.getFields());
			contractImpl.setSuperName(classCg.getSuperName());
			
			if(classCg.getSuperName()==null) contractImpl.setIsUniCast(true);
			else contractImpl.setIsUniCast(false);
			for(AMethodDeclCG method : classCg.getMethods()){

				if(method.getName().equals("toString")){
					//					method.setIsRemote(true);
					//					publicMethods.add(method);
				}
				else if(method.getAccess().equals("public")){ //&& !method.getIsConstructor()){
					//TODO : Make afunction which transforms the parameters
					LinkedList<AFormalParamLocalParamCG> formalParams = (LinkedList<AFormalParamLocalParamCG>) method.getFormalParams().clone();

					LinkedList<AFormalParamLocalParamCG> formalParams2 = new LinkedList<AFormalParamLocalParamCG>();

					method.setFormalParams(null);

					for(AFormalParamLocalParamCG formPar : formalParams){
						STypeCG ty = formPar.getType();
						if(ty instanceof AClassTypeCG){
							AClassTypeCG classTy = (AClassTypeCG) ty;
							String className = classTy.getName().toString();
							classTy.setName(className + "_i");
							formPar.setType(classTy);
						}
						formalParams2.add(formPar);
					}
					method.setFormalParams(formalParams2);
					method.setStatic(false);
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
