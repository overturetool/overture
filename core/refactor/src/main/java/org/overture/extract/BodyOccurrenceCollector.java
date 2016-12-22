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
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.PStm;
import org.overture.refactoring.RefactoringLogger;

public class BodyOccurrenceCollector extends DepthFirstAnalysisAdaptor{
	
	private AExplicitOperationDefinition fromOperation;
	private AExplicitOperationDefinition toOperation;
	private AModuleModules currentModule;
	private int from;
	private int to;
	private String extractedOperationName;
	private RefactoringLogger<Extraction> refactoringLogger;
	
	public BodyOccurrenceCollector(AExplicitOperationDefinition fromOp, AModuleModules currentModule, int from, int to,
			String extractedOperationName, RefactoringLogger<Extraction> refactoringLogger)
	{
		this.fromOperation = fromOp;
		this.toOperation = null;
		this.currentModule = currentModule;
		this.from = from;
		this.to = to;
		this.extractedOperationName = extractedOperationName;
		this.refactoringLogger = refactoringLogger;
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
		int counter = 0;
		LinkedList<PStm> statementsToExtract = new LinkedList<PStm>();
		for(PStm stm : fromStatements){
			
			if(ExtractUtil.isInRange(stm.getLocation(), from, to)){
				
				if(toOperation == null){
					addToNodeCurrentModule(fromOperation);
				}
				statementsToExtract.add(stm.clone());
				if(ExtractUtil.addToOperationToFromOperation( stm, node, fromStatements, toOperation, counter)){
					
					ExtractUtil.removeFromStatements(stm, node.getStatements());
					refactoringLogger.add(new Extraction(stm.getLocation(), stm.toString(), null));					
				}else{
					refactoringLogger.add(new Extraction(stm.getLocation(), stm.toString(), toOperation.getName().getName()));					
				}
			}
			counter++;
			stm.apply(THIS);
		}
		if(statementsToExtract.size() > 0){
			if(toOperation.getBody() instanceof ABlockSimpleBlockStm){
				ABlockSimpleBlockStm block = (ABlockSimpleBlockStm) toOperation.getBody();
				block.setStatements(statementsToExtract);
			}
		}
	}
		
	@Override
	public void caseAIfStm(AIfStm node) throws AnalysisException {
		
		if(node.getThenStm() != null){
			node.getThenStm().apply(THIS);
		}
		if(node.getElseStm() != null){
			node.getElseStm().apply(THIS);
		}
		super.caseAIfStm(node);
	}
	
	private void addToNodeCurrentModule(AExplicitOperationDefinition node){
		toOperation = node.clone();
		LexNameToken token = new LexNameToken(toOperation.getName().getModule(), extractedOperationName, toOperation.getName().getLocation());
		toOperation.setName(token);
		int  i = currentModule.getDefs().indexOf(node);
		toOperation.setLocation(new LexLocation());
		currentModule.getDefs().add(i,toOperation);	
	}
	
	public AExplicitOperationDefinition getToOperation(){
		return toOperation;
	}
	
}
