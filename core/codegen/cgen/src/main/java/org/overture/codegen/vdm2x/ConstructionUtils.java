package org.overture.codegen.vdm2x;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;



public class ConstructionUtils {
	
	public static AApplyExpCG consUtilCall(String utils_name,String memberName, STypeCG returnType )
	{
		AExplicitVarExpCG member = new AExplicitVarExpCG();

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType.clone());
		member.setType(methodType);
		member.setIsLambda(false);
		member.setIsLocal(false);
		//member.setIsStatic(true);
		
		AExternalTypeCG classType = new AExternalTypeCG();
		classType.setName(utils_name);
		member.setClassType(classType);
		member.setName(memberName);
		AApplyExpCG call = new AApplyExpCG();
		
		call.setType(returnType.clone());
		call.setRoot(member);
		
		return call;
	}

}
