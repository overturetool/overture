package org.overture.extract;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;

public class BodyOccurrenceCollector extends DepthFirstAnalysisAdaptor{
	
	private AExplicitOperationDefinition fromOperation;
	private AExplicitOperationDefinition toOperation;
	private AModuleModules currentModule;
	private int from;
	private int to;
	private String extractedOperationName;
	
	public BodyOccurrenceCollector(AExplicitOperationDefinition fromOp, AModuleModules currentModule, int from, int to, String extractedOperationName)
	{
		this.fromOperation = fromOp;
		this.toOperation = null;
		this.currentModule = currentModule;
		this.from = from;
		this.to = to;
		this.extractedOperationName = extractedOperationName;
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException {
		
		if(!ExtractUtil.isInRange(node.getLocation(), from, to)){
			fromOperation.getBody();
		}
		super.caseACallStm(node);
	}
	
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node) throws AnalysisException {
		ExtractUtil.init();
		LinkedList<PStm> fromStatements = new LinkedList<PStm>(node.getStatements());
		ABlockSimpleBlockStm toNodeOperation = null;
		int counter = 0;
		for(PStm stm : fromStatements){
			
			if(ExtractUtil.isInRange(stm.getLocation(), from, to)){
				
				if(toOperation == null){
					addToNodeCurrentModule(fromOperation);
				}
				
				if(ExtractUtil.addToOperationToFromOperation( stm, node, fromStatements, toOperation, counter)){
					ExtractUtil.removeFromStatements(stm, node.getStatements());
				}
				
			} else if(!ExtractUtil.isInRange(stm.getLocation(), from, to)){
				
				if(toOperation == null){
					addToNodeCurrentModule(fromOperation);
				}
				//TODO check if this is correct
				//ExtractUtil.addToOperationToFromOperation( stm, node, fromStatements, toOperation, counter);
				
				if(toNodeOperation == null){
					if(toOperation.getBody() instanceof ABlockSimpleBlockStm){
						toNodeOperation = (ABlockSimpleBlockStm) toOperation.getBody(); 
					}
				}
				if(toNodeOperation != null){
					ExtractUtil.removeFromStatements(stm, toNodeOperation.getStatements());					
				}
			}
			counter++;
		}
	}
		
	private void addToNodeCurrentModule(AExplicitOperationDefinition node){
		toOperation = node.clone();
		LexNameToken token = new LexNameToken(toOperation.getName().getModule(), extractedOperationName, toOperation.getName().getLocation());
		toOperation.setName(token);
		toOperation.setLocation(new LexLocation());
		currentModule.getDefs().add(toOperation);	
	}
	
	public AExplicitOperationDefinition getToOperation(){
		return toOperation;
	}
	
}
