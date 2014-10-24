package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.codegen.cgast.SNameCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHistoryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;

public class MutexDeclTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private List<AClassDeclCG> classes;
	
	public MutexDeclTransformation(IRInfo info, List<AClassDeclCG> classes)
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
		
		for(AMutexSyncDeclCG mutex : node.getMutexSyncs())
		{
			//System.out.println(node.getMutexSyncs());
			if(mutex.getOpnames().size() == 1)
			{
				//System.out.println(mutex);
				APersyncDeclCG perpred = new APersyncDeclCG();
				perpred.setOpname(mutex.getOpnames().getFirst().toString());
				
				//System.out.println(perpred.getOpname());
				AEqualsBinaryExpCG guard = new AEqualsBinaryExpCG();
				
				AHistoryExpCG histcount = new AHistoryExpCG();
				histcount.setHistype("active");
				histcount.setOpsname(mutex.getOpnames().getFirst().toString());
				
				AClassTypeCG innerclass = new AClassTypeCG();
				innerclass.setName(node.getName()+"_sentinel");
				
				histcount.setSentinelType(innerclass);
				
				AIntLiteralExpCG zero = new AIntLiteralExpCG();
				zero.setValue(0L);
				
				guard.setLeft(histcount);
				guard.setRight(zero);
				
				Boolean foundsame = false;
				int foundplace = 0;
				for(int i=0; i<node.getPerSyncs().size(); i++)
				{
					System.out.println(perpred.getOpname());
					System.out.println(node.getPerSyncs().get(i));
					if(node.getPerSyncs().get(i).getOpname().equals(perpred.getOpname()))
					{
						
						foundsame = true;
						foundplace = i;
					}
				}
				//for(APersyncDeclCG pred : node.getPerSyncs())
				//{
					
					System.out.println(foundsame);
					if(!foundsame)
					{
						
						perpred.setPred(guard);
						node.getPerSyncs().add(perpred);
						System.out.println(node.getPerSyncs().getLast());																
					}
					else
					{	
						AAndBoolBinaryExpCG newpred = new AAndBoolBinaryExpCG();
						newpred.setLeft(node.getPerSyncs().get(foundplace).getPred().clone());
						newpred.setRight(guard);
						node.getPerSyncs().get(foundplace).setPred(newpred);
					}
				//}
				
				
			}
			else{
				for(SNameCG operation : mutex.getOpnames())
				{
					if(operation instanceof ATokenNameCG)
					{
						APersyncDeclCG perpred = new APersyncDeclCG();
						perpred.setOpname(((ATokenNameCG) operation).getName());
						System.out.println(mutex.getOpnames().getFirst());
						
						APlusNumericBinaryExpCG addedhistcounter = new APlusNumericBinaryExpCG();
						AHistoryExpCG firsthistcount = new AHistoryExpCG();
						firsthistcount.setHistype("active");
						firsthistcount.setOpsname(mutex.getOpnames().getFirst().toString());
						addedhistcounter.setLeft(firsthistcount);
						
						for(int i=1; i< mutex.getOpnames().size() - 1; i++)
						{
							AHistoryExpCG histcount = new AHistoryExpCG();
							histcount.setHistype("active");
							histcount.setOpsname(mutex.getOpnames().get(i).toString());
							APlusNumericBinaryExpCG addition = new APlusNumericBinaryExpCG();
							addition.setLeft(histcount);
							addition.setRight(addedhistcounter);
							
						}
						//perpred.setPred();
					}
					
				}
			}
		}
	}
	
	
	

}
