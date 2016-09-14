package org.overture.codegen.trans.conc;

import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SNameIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMutexSyncDeclIR;
import org.overture.codegen.ir.declarations.APersyncDeclIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AHistoryExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;

public class MutexDeclTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private ConcPrefixes concPrefixes;

	public MutexDeclTrans(IRInfo info, ConcPrefixes concPrefixes)
	{
		this.info = info;
		this.concPrefixes = concPrefixes;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		if (!info.getSettings().generateConc())
		{
			return;
		}
		
		if(info.getDeclAssistant().isTest(node))
		{
			return;
		}

		for (AMutexSyncDeclIR mutex : node.getMutexSyncs())
		{
			if (mutex.getOpnames().size() == 1)
			{
				Boolean foundsame = false;
				int foundplace = 0;

				APersyncDeclIR perpred = new APersyncDeclIR();
				perpred.setOpname(mutex.getOpnames().getFirst().toString());

				AEqualsBinaryExpIR guard = new AEqualsBinaryExpIR();

				AHistoryExpIR histcount = new AHistoryExpIR();
				histcount.setHistype(concPrefixes.activeHistOpTypeName());
				histcount.setOpsname(mutex.getOpnames().getFirst().toString());
				histcount.setType(new AIntNumericBasicTypeIR());

				AClassTypeIR innerclass = new AClassTypeIR();
				innerclass.setName(node.getName()
						+ concPrefixes.sentinelClassPostFix());

				histcount.setSentinelType(innerclass);

				AIntLiteralExpIR zero = new AIntLiteralExpIR();
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
					AAndBoolBinaryExpIR newpred = new AAndBoolBinaryExpIR();
					newpred.setLeft(node.getPerSyncs().get(foundplace).getPred().clone());
					newpred.setRight(guard);
					node.getPerSyncs().get(foundplace).setPred(newpred);
				}
			} else
			{
				for (SNameIR operation : mutex.getOpnames())
				{
					Boolean foundsame = false;
					int foundplace = 0;

					if (operation instanceof ATokenNameIR)
					{
						APersyncDeclIR perpred = new APersyncDeclIR();
						perpred.setOpname(((ATokenNameIR) operation).getName());

						AClassTypeIR innerclass = new AClassTypeIR();
						innerclass.setName(node.getName()
								+ concPrefixes.sentinelClassPostFix());

						APlusNumericBinaryExpIR addedhistcounter = new APlusNumericBinaryExpIR();

						AHistoryExpIR firsthistcount = new AHistoryExpIR();
						firsthistcount.setHistype(concPrefixes.activeHistOpTypeName());
						firsthistcount.setSentinelType(innerclass.clone());
						firsthistcount.setOpsname(mutex.getOpnames().getFirst().toString());
						firsthistcount.setType(new AIntNumericBasicTypeIR());

						addedhistcounter.setLeft(firsthistcount);
						APlusNumericBinaryExpIR addition1 = new APlusNumericBinaryExpIR();
						addition1 = addedhistcounter;

						for (int i = 1; i < mutex.getOpnames().size() - 1; i++)
						{
							String nextOpName = mutex.getOpnames().get(i).toString();

							AHistoryExpIR histcountleft = new AHistoryExpIR();
							histcountleft.setHistype(concPrefixes.activeHistOpTypeName());
							histcountleft.setOpsname(nextOpName);
							histcountleft.setType(new AIntNumericBasicTypeIR());
							histcountleft.setSentinelType(innerclass.clone());

							APlusNumericBinaryExpIR addition = new APlusNumericBinaryExpIR();
							addition.setLeft(histcountleft);

							addition1.setRight(addition);

							addition1 = addition;
						}
						String lastOpName = mutex.getOpnames().getLast().toString();

						AHistoryExpIR lastHistoryExpIR = new AHistoryExpIR();

						lastHistoryExpIR.setOpsname(lastOpName);
						lastHistoryExpIR.setHistype(concPrefixes.activeHistOpTypeName());
						lastHistoryExpIR.setType(new AIntNumericBasicTypeIR());
						lastHistoryExpIR.setSentinelType(innerclass.clone());
						addition1.setRight(lastHistoryExpIR);

						AIntLiteralExpIR zeronum = new AIntLiteralExpIR();
						zeronum.setValue(0L);

						AEqualsBinaryExpIR equalzero = new AEqualsBinaryExpIR();
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
							AAndBoolBinaryExpIR newpred = new AAndBoolBinaryExpIR();
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
