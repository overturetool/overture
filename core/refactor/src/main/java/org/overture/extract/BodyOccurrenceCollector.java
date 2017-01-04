package org.overture.extract;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.PStm;
import org.overture.refactoring.RefactoringLogger;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class BodyOccurrenceCollector extends DepthFirstAnalysisAdaptor{
	
	private AExplicitOperationDefinition fromOperation;
	private AExplicitOperationDefinition toOperation;
	private AModuleModules currentModule;
	private int from;
	private int to;
	private String extractedOperationName;
	private RefactoringLogger<Extraction> refactoringLogger;
	private List<PDefinition> neededParametersForNewOp;
	private boolean toOpCreatedFlag = false;
	private ITypeCheckerAssistantFactory af;
	
	public BodyOccurrenceCollector(AExplicitOperationDefinition fromOp, AModuleModules currentModule, int from, int to,
			String extractedOperationName, RefactoringLogger<Extraction> refactoringLogger, ITypeCheckerAssistantFactory af)
	{
		
		this.fromOperation = fromOp;
		this.toOperation = null;
		this.currentModule = currentModule;
		this.from = from;
		this.to = to;
		this.extractedOperationName = extractedOperationName;
		this.refactoringLogger = refactoringLogger;
		this.af = af;
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
		LinkedList<AAssignmentDefinition> assignmentsToExtract = new LinkedList<AAssignmentDefinition>();
		
		for(PStm stm : fromStatements){
			
			if(ExtractUtil.isInRange(stm.getLocation(), from, to)){
				
				if(toOperation == null){
					addToNodeCurrentModule(node.getAncestor(AExplicitOperationDefinition.class)); //TODO
				}
				statementsToExtract.add(stm.clone());
				if(ExtractUtil.addToOperationToFromOperation( stm, node, fromStatements, toOperation, counter, neededParametersForNewOp)){
					
					ExtractUtil.removeFromStatements(stm, node.getStatements());
					refactoringLogger.add(new Extraction(stm.getLocation(), stm.toString(), null));					
				}else{
					refactoringLogger.add(new Extraction(stm.getLocation(), stm.toString(), toOperation.getName().getName()));					
				}
			}
			counter++;
			stm.apply(THIS);
		}
		
		if(toOperation != null && toOperation.getBody() instanceof ABlockSimpleBlockStm){
			ABlockSimpleBlockStm block = (ABlockSimpleBlockStm) toOperation.getBody();
				
			for(AAssignmentDefinition item : block.getAssignmentDefs()){
				
				if(ExtractUtil.isInRange(item.getLocation(), from, to)){
					assignmentsToExtract.add(item.clone());
					ExtractUtil.removeFromAssignmentDefs(item, node.getAssignmentDefs());
					refactoringLogger.add(new Extraction(item.getLocation(), item.toString(), null));	
				}
			}
			
		}
		
		if(toOperation != null && toOperation.getBody() instanceof ABlockSimpleBlockStm && !toOpCreatedFlag){
			ABlockSimpleBlockStm block = (ABlockSimpleBlockStm) toOperation.getBody();
			
			if(statementsToExtract.size() > 0){
				block.setStatements(statementsToExtract);
			}else{
				block.getStatements().clear();
			}
			if(assignmentsToExtract.size() > 0){
				block.setAssignmentDefs(assignmentsToExtract);
			}else{
				block.getAssignmentDefs().clear();
			}
			toOpCreatedFlag = true;
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
	
	private void addToNodeCurrentModule(AExplicitOperationDefinition node) throws AnalysisException{
		toOperation = node.clone();
		LexNameToken token = new LexNameToken(toOperation.getName().getModule(), extractedOperationName, toOperation.getName().getLocation());
		toOperation.setName(token);	
		
		CollectNeededParameters paramsCollector = new CollectNeededParameters(af, from, to);
		node.apply(paramsCollector);
		
		neededParametersForNewOp = paramsCollector.getNeededParameters();
		ExtractUtil.setParametersForOperation(toOperation, neededParametersForNewOp);
		
		int  i = currentModule.getDefs().indexOf(node);
		toOperation.setLocation(new LexLocation());
		currentModule.getDefs().add(i,toOperation);	
	}
	
	public AExplicitOperationDefinition getToOperation(){
		return toOperation;
	}
	
	public List<PDefinition> getNeededParametersForNewOp() {
		return neededParametersForNewOp;
	}
	
}
