package org.overture.codegen.trans.conc;

import org.overture.codegen.cgast.SNameCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHistoryExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.ir.IRInfo;

public class MutexDeclTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	
	public MutexDeclTrans(IRInfo info)
	{
		this.info = info;
	}
	
	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		String hop = "active";
		if(!info.getSettings().generateConc())
		{
			return;
		}
				
		for(AMutexSyncDeclCG mutex : node.getMutexSyncs())
		{
			
			if (mutex.getOpnames().size() == 1)
			{
				Boolean foundsame = false;
				int foundplace = 0;
			
				APersyncDeclCG perpred = new APersyncDeclCG();
				perpred.setOpname(mutex.getOpnames().getFirst().toString());

				
				AEqualsBinaryExpCG guard = new AEqualsBinaryExpCG();

				AHistoryExpCG histcount = new AHistoryExpCG();
				histcount.setHistype(hop);
				histcount.setOpsname(mutex.getOpnames().getFirst().toString());
				histcount.setType(new AIntNumericBasicTypeCG());

				AClassTypeCG innerclass = new AClassTypeCG();
				innerclass.setName(node.getName() + "_sentinel");

				histcount.setSentinelType(innerclass);

				
				AIntLiteralExpCG zero = new AIntLiteralExpCG();
				zero.setValue(0L);

				guard.setLeft(histcount);
				guard.setRight(zero);

				
				for (int i = 0; i < node.getPerSyncs().size(); i++)
				{
					
					if (node.getPerSyncs().get(i).getOpname().equals(perpred.getOpname()))
					{

						foundsame = true;
						foundplace = i;
					}
				}

				
				if (!foundsame)
				{

					perpred.setPred(guard);
					node.getPerSyncs().add(perpred);
					
				} else
				{
					AAndBoolBinaryExpCG newpred = new AAndBoolBinaryExpCG();
					newpred.setLeft(node.getPerSyncs().get(foundplace).getPred().clone());
					newpred.setRight(guard);
					node.getPerSyncs().get(foundplace).setPred(newpred);
				}
			} else
			{
				for (SNameCG operation : mutex.getOpnames())
				{
					Boolean foundsame = false;
					int foundplace = 0;
					
					if (operation instanceof ATokenNameCG)
					{
						APersyncDeclCG perpred = new APersyncDeclCG();
						perpred.setOpname(((ATokenNameCG) operation).getName());
						
						AClassTypeCG innerclass = new AClassTypeCG();
						innerclass.setName(node.getName() + "_sentinel");

						APlusNumericBinaryExpCG addedhistcounter = new APlusNumericBinaryExpCG();
						
						AHistoryExpCG firsthistcount = new AHistoryExpCG();
						firsthistcount.setHistype(hop);
						firsthistcount.setSentinelType(innerclass.clone());
						firsthistcount.setOpsname(mutex.getOpnames().getFirst().toString());
						firsthistcount.setType(new AIntNumericBasicTypeCG());
						
						addedhistcounter.setLeft(firsthistcount);
						APlusNumericBinaryExpCG addition1 = new APlusNumericBinaryExpCG();
						addition1 = addedhistcounter;
						for (int i = 1; i < mutex.getOpnames().size() - 1; i++)
						{
							String nextOpName = mutex.getOpnames().get(i).toString();
													
							AHistoryExpCG histcountleft = new AHistoryExpCG();
							histcountleft.setHistype(hop);
							histcountleft.setOpsname(nextOpName);	
							histcountleft.setType(new AIntNumericBasicTypeCG());
							histcountleft.setSentinelType(innerclass.clone());
							
							APlusNumericBinaryExpCG addition = new APlusNumericBinaryExpCG();
							addition.setLeft(histcountleft);
							
							addition1.setRight(addition);
							
							addition1 = addition;
						}
						String lastOpName = mutex.getOpnames().getLast().toString();
						
						
						AHistoryExpCG lastHistoryExpCG = new AHistoryExpCG();
						
						lastHistoryExpCG.setOpsname(lastOpName);
						lastHistoryExpCG.setHistype(hop);
						lastHistoryExpCG.setType(new AIntNumericBasicTypeCG());
						lastHistoryExpCG.setSentinelType(innerclass.clone());
						addition1.setRight(lastHistoryExpCG);
						
						AIntLiteralExpCG zeronum = new AIntLiteralExpCG();
						zeronum.setValue(0L);
						
						AEqualsBinaryExpCG equalzero = new AEqualsBinaryExpCG();			
						equalzero.setLeft(addedhistcounter);
						equalzero.setRight(zeronum);
						
						for (int i = 0; i < node.getPerSyncs().size(); i++)
						{
							if (node.getPerSyncs().get(i).getOpname().equals(perpred.getOpname()))
							{

								foundsame = true;
								foundplace = i;
							}
						}
						if (!foundsame)
						{

							perpred.setPred(equalzero);
							node.getPerSyncs().add(perpred);
							
						} else
						{
							AAndBoolBinaryExpCG newpred = new AAndBoolBinaryExpCG();
							newpred.setLeft(node.getPerSyncs().get(foundplace).getPred().clone());
							newpred.setRight(equalzero);
							node.getPerSyncs().get(foundplace).setPred(newpred);
						}
					}
					
					

				}
			}
		}
	}
}
