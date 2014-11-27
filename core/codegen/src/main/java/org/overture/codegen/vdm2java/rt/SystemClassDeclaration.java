package org.overture.codegen.vdm2java.rt;

import java.io.StringWriter;
import java.util.Set;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;

public class SystemClassDeclaration {

	private Set<AVariableExp> deployedObjects;

	public SystemClassDeclaration(Set<AVariableExp> deployedObjects) {
		this.deployedObjects = deployedObjects;
	}

	public AClassDeclCG Run() throws AnalysisException{
		AClassDeclCG systemClass = new AClassDeclCG();

		systemClass.setAccess("public");
		ASystemClassDefinition sysClass = null;
		for(AVariableExp obj : deployedObjects){
	        AClassTypeCG classType = new AClassTypeCG();
	        classType.setName(obj.getType().toString());
			sysClass = obj.getAncestor(ASystemClassDefinition.class);
			AFieldDeclCG deploydObj = new AFieldDeclCG();
			deploydObj.setStatic(true);
			deploydObj.setFinal(false);
			deploydObj.setAccess("public");
			deploydObj.setType(classType);
			deploydObj.setName(obj.getName().getName().toString());
			deploydObj.setInitial(new ANullExpCG());

			systemClass.getFields().add(deploydObj);
		}

		systemClass.setName(sysClass.getName().toString());
		systemClass.setTag("System");
		
		System.out.println("**********************System**********************");

		IRInfo info2 = new IRInfo("cg_init");
		JavaFormat javaFormat2 = new JavaFormat(new TempVarPrefixes(), info2);
		
		MergeVisitor printer2 = javaFormat2.getMergeVisitor();
		StringWriter writer2 = new StringWriter();
		systemClass.apply(printer2, writer2);

		System.out.println(JavaCodeGenUtil.formatJavaCode(writer2
				.toString()));
		
		
		return systemClass;
	}
}
