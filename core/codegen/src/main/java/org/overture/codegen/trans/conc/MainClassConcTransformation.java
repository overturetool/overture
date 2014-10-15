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
import org.overture.codegen.cgast.statements.ATryStmCG;
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
		
		for(AFieldDeclCG fieldCG : node.getFields())
		{
			fieldCG.setVolatile(true);
		}
		
		AExternalTypeCG sentType = new AExternalTypeCG();
		sentType.setName("Sentinel");
		AFieldDeclCG sentinelfld = new AFieldDeclCG();
		sentinelfld.setName("sentinel");
		sentinelfld.setType(sentType);
		sentinelfld.setAccess(JavaFormat.JAVA_PUBLIC);
		sentinelfld.setVolatile(false);
		
		node.getFields().add(sentinelfld);
		
		
		for(AMethodDeclCG methodCG : node.getMethods())
		{
			if(!methodCG.getIsConstructor()){//(x.getName() != node.getName()){
				if (!methodCG.getName().equals("toString") ){//&& !methodCG.getName().equals("Run")){//x.getName() != "toString"){
					ABlockStmCG bodyStm = new ABlockStmCG();
					
					ACallStmCG entering = new ACallStmCG();
					ACallStmCG leaving = new ACallStmCG();
					
				
					entering.setName("entering");
					AClassTypeCG sentinel = new AClassTypeCG();
					sentinel.setName("Sentinel");
					
					entering.setClassType(sentinel);
					entering.setType(new AVoidTypeCG());
					
					leaving.setName("leaving");
					leaving.setClassType(sentinel.clone());
					leaving.setType(new AVoidTypeCG());
					
					bodyStm.getStatements().add(entering);
					ATryStmCG trystm = new ATryStmCG();
					trystm.setStm(methodCG.getBody());
					trystm.setFinally(leaving);
					bodyStm.getStatements().add(trystm);
										
					methodCG.setBody(bodyStm);
				}
			}
		}
	}
	
//	@Override
//	public void caseAHistoryExpCG(AHistoryExpCG node) throws AnalysisException
//	{
//		if(node.getHistype().equals("#act"))
//		{
//			
//		}
//	}
}
