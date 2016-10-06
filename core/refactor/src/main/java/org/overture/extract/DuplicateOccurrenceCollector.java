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
	AModuleModules currentModule;
	public DuplicateOccurrenceCollector(AExplicitOperationDefinition callingOp, AExplicitOperationDefinition extractedOp, int from, int to, String extractedOperationName, AModuleModules currentModule)
	{
		this.callingOperation = callingOp;
		this.extractedOperation = extractedOp;
		this.from = from;
		this.to = to;
		this.currentModule = currentModule;
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
		dublicateRemover(node);
	}
	
	public void dublicateRemover(ABlockSimpleBlockStm node){
		ExtractUtil.init();
		LinkedList<PStm> fromStatements = new LinkedList<PStm>(node.getStatements());
		List<PStm> listOfStm = checkForPattern(node);
		
		if( listOfStm != null && listOfStm.size() > 0){
			
			int listOfStmCounter = 0;
			
			for (int i = 0; i < fromStatements.size(); i ++) {

				int fromAndTo = listOfStm.get(listOfStmCounter).getLocation().getStartLine();
				
				if(ExtractUtil.isInRange(fromStatements.get(i).getLocation(), fromAndTo, fromAndTo)){
					ExtractUtil.addToOperationToFromOperation( fromStatements.get(i), node, node.getStatements(), extractedOperation, i);
					ExtractUtil.removeFromStatements(fromStatements.get(i), node.getStatements());
					listOfStmCounter++;
					
					if(listOfStmCounter > listOfStm.size()-1){
						listOfStm.clear();
						dublicateRemover(node);
						return;
					}
				} 
			}
		}
		return;
	}
	
	public List<PStm> checkForPattern(ABlockSimpleBlockStm currentBlock){
		List<PStm> listOfStm = new ArrayList<PStm>();

		if(extractedOperation.getBody() instanceof ABlockSimpleBlockStm){
			ABlockSimpleBlockStm extractedBlock = (ABlockSimpleBlockStm) extractedOperation.getBody();
				int i = 0;

				PStm extractedStm = extractedBlock.getStatements().get(i);
				
				for(int j = 0; j < currentBlock.getStatements().size(); j++){
					PStm callingStm = currentBlock.getStatements().get(j);
					
					if(callingStm.equals(extractedStm)){
						if(!callingStm.equals(extractedBlock.getStatements().getFirst()) &&
								currentBlock.getStatements().get(j - 1).equals(listOfStm.get(listOfStm.size() - 1))){
							listOfStm.add(callingStm);
							
							if(extractedBlock.getStatements().getLast().equals(extractedStm)){
								return listOfStm;
							}
							
						} else {
							listOfStm.add(callingStm);
						}
						
						i++;
						if(i < extractedBlock.getStatements().size()){
							extractedStm = extractedBlock.getStatements().get(i);
						}else{
//							RETURN fail
							listOfStm.clear();
							return listOfStm;
						}
							
					}else{
						listOfStm.clear();
						if(i > 0){
							i--;
							j--;
						}
						if(i < extractedBlock.getStatements().size()){
							extractedStm = extractedBlock.getStatements().get(i);
						}
						
					}
				
			}
		
		}
		return listOfStm;
	}
	
}
