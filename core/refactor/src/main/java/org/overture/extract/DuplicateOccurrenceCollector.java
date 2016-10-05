package org.overture.extract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;

public class DuplicateOccurrenceCollector extends DepthFirstAnalysisAdaptor {
	
	private AExplicitOperationDefinition callingOperation;
	private AExplicitOperationDefinition extractedOperation;
	private int from;
	private int to;
	
	public DuplicateOccurrenceCollector(AExplicitOperationDefinition callingOp, AExplicitOperationDefinition extractedOp, int from, int to, String extractedOperationName)
	{
		this.callingOperation = callingOp;
		this.extractedOperation = extractedOp;
		this.from = from;
		this.to = to;
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException {
		
		if(!ExtractUtil.isInRange(node.getLocation(), from, to)){
			callingOperation.getBody();
		}
		super.caseACallStm(node);
	}
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node) throws AnalysisException {
		ExtractUtil.init();
		LinkedList<PStm> fromStatements = new LinkedList<PStm>(node.getStatements());
		List<PStm> listOfStm = checkForPattern(node);
		
		if( listOfStm != null && listOfStm.size() > 0){
			
			int listOfStmCounter = 0;
			
			for(PStm stm : fromStatements){
				int fromAndTo = listOfStm.get(listOfStmCounter).getLocation().getStartLine();
				
				if(ExtractUtil.isInRange(stm.getLocation(), fromAndTo, fromAndTo)){
					
					//TODO Problem core
//					ExtractUtil.addToOperationToFromOperation( stm, node, fromStatements, extractedOperation);
					ExtractUtil.removeFromStatements(stm, node.getStatements());
					listOfStmCounter++;
					
					if(listOfStmCounter > listOfStm.size()-1){
						return;
					}
				} 
			}
		}
	}
	
	public List<PStm> checkForPattern(ABlockSimpleBlockStm currentBlock){
		List<PStm> listOfStm = new ArrayList<PStm>();

		if(extractedOperation.getBody() instanceof ABlockSimpleBlockStm){
			ABlockSimpleBlockStm extractedBlock = (ABlockSimpleBlockStm) extractedOperation.getBody();
			
			for(int i = 0; i < currentBlock.getStatements().size(); i++){
				PStm currentStm = currentBlock.getStatements().get(i);
				
				for(int j = 0; j < extractedBlock.getStatements().size(); j++){
					PStm extractedStm = extractedBlock.getStatements().get(j);
					
					if(extractedStm.equals(currentStm)){
						if(!extractedStm.equals(extractedBlock.getStatements().getFirst()) &&
								extractedBlock.getStatements().get(j - 1).equals(listOfStm.get(listOfStm.size() - 1))){
							listOfStm.add(currentStm);
							
							if(extractedBlock.getStatements().getLast().equals(currentStm)){
								return listOfStm;
							}
							
						} else {
							listOfStm.add(currentStm);
						}
						
						i++;
						if(i < currentBlock.getStatements().size()){
							currentStm = currentBlock.getStatements().get(i);
						}else{
//							RETURN fail
							listOfStm.clear();
							return listOfStm;
						}
							
					}
					
				}
				
			}
		
		}
		return listOfStm;
	}
	
}
