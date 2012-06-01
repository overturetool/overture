package org.overture.pog.tests;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.modules.AModuleModules;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typecheck.ModuleTypeChecker;
import org.overture.typecheck.TypeChecker;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Message;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public class OvertureTestHelperPOG 
{
	
	
	
	protected Result<ProofObligationList> collectTypeResults(TypeChecker mtc)
	{
		List<IMessage> errors = new Vector<IMessage>();
		List<IMessage> warnings = new Vector<IMessage>();
		if (mtc != null && TypeChecker.getErrorCount() > 0)
		{

			for (VDMError msg : TypeChecker.getErrors())
			{
				errors.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}

		if (mtc != null && TypeChecker.getWarningCount() > 0)
		{
			for (VDMWarning msg : TypeChecker.getWarnings())
			{
				warnings.add(new Message(msg.location.file.getName(), msg.number, msg.location.startLine, msg.location.startPos, msg.message));
			}
		}
		return new Result<ProofObligationList>(null, warnings, errors);
	}

	
	public Result<ProofObligationList> getProofObligations(File file) throws Exception {
		Result<List<AModuleModules>> parserResult = new OvertureTestHelper().parseCheckSl(file);
		if (parserResult.errors.isEmpty())
		{
			ModuleTypeChecker mtc = new ModuleTypeChecker(parserResult.result);
			mtc.typeCheck();
			
			
			
			if(ModuleTypeChecker.getErrorCount() == 0)
			{
				ProofObligationList proofObligations = new ProofObligationList();
				for (AModuleModules aModule : parserResult.result) {
					proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
				}
				//return collectPOsToResult(proofObligations,file);
				return new Result<ProofObligationList>(proofObligations, new Vector<IMessage>(),new Vector<IMessage>());
				
			}
			else
			{
				throw new Exception("failed typecheck");
			}
		}
		throw new Exception("failed typecheck");
	}
//	private Result<ProofObligationList> collectPOsToResult(ProofObligationList proofObligations, File file) 
//	{
//		List<IMessage> messageList = new Vector<IMessage>();
//		
//		for (ProofObligation proofObligation : proofObligations) {
//			Message m = new Message(file.getName(), proofObligation.number, proofObligation.location.startLine, proofObligation.location.startPos, proofObligation.toString());
//			messageList.add(m);
//		}
//		
//		return new Result<List<IMessage>>(messageList, new Vector<IMessage>(), new Vector<IMessage>());
//		
//	}
}
