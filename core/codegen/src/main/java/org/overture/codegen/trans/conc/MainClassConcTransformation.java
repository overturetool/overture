/**
 * 
 */
package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2java.JavaFormat;

/**
 * @author gkanos
 *
 */
public class MainClassConcTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private List<AClassDeclCG> classes;

	public MainClassConcTransformation(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.classes = classes;
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		AExternalTypeCG sentType = new AExternalTypeCG();
		sentType.setName("Sentinel");
		AFieldDeclCG sentinelfld = new AFieldDeclCG();
		sentinelfld.setName("sentinel");
		sentinelfld.setType(sentType);
		sentinelfld.setAccess(JavaFormat.JAVA_PUBLIC);
		
		node.getFields().add(sentinelfld);
//		for(AFieldDeclCG x : node.getFields())
//		{
//			//x.s
//		}
		
		for(AMethodDeclCG x : node.getMethods())
		{
			if(x.getName() != node.getName()){
				if (!x.getName().equals("toString")){//x.getName() != "toString"){
					ABlockStmCG bodyStm = new ABlockStmCG();
					
					ACallStmCG entering = new ACallStmCG();
					ACallStmCG leaving = new ACallStmCG();
					
				//	ACallObjectStmCG enteringstm = new ACallObjectStmCG();
				
					entering.setName("entering");
					AClassTypeCG sentinel = new AClassTypeCG();
					sentinel.setName("Sentinel");
					
					entering.setClassType(sentinel);
					entering.setType(new AVoidTypeCG());
					
					leaving.setName("leaving");
					leaving.setClassType(sentinel.clone());
					leaving.setType(new AVoidTypeCG());
					
					bodyStm.getStatements().add(entering);
					//this needs merging with try catch finally stm.
					bodyStm.getStatements().add(x.getBody());
					
					bodyStm.getStatements().add(leaving);
					
					x.setBody(bodyStm);
				}
				else
				{
					continue;
				}
			}
			else
			{
				continue;
			}
		}
	}
}
