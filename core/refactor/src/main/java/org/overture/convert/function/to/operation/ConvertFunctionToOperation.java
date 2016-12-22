package org.overture.convert.function.to.operation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AOperationType;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.refactoring.RefactoringLogger;

public class ConvertFunctionToOperation  extends DepthFirstAnalysisAdaptor{
	
	private PDefinition enclosingDef;

	private AModuleModules currentModule;
	private boolean foundFunctionToConvert;
	private int line;
	private RefactoringLogger<ConversionFromFuncToOp> refactoringLogger;
	private AExplicitOperationDefinition lastOperation = null;
	
	public ConvertFunctionToOperation(int line)
	{
		this.enclosingDef = null;
		this.currentModule = null;
		this.foundFunctionToConvert = false;
		this.line = line;
		this.refactoringLogger = new RefactoringLogger<ConversionFromFuncToOp>();
	}
	
	@Override
	public void caseAModuleModules(AModuleModules node) throws AnalysisException
	{
		if (enclosingDef != null)
		{
			return;
		}
		
		if(node instanceof CombinedDefaultModule)
		{
			for(AModuleModules m : ((CombinedDefaultModule) node).getModules())
			{
				m.apply(THIS);
			}
		}else{
			currentModule = node;
			visitModuleDefs(node.getDefs(), node);
		}
	}
	
	private void visitModuleDefs(List<PDefinition> defs, INode module)
			throws AnalysisException
	{
		handleExecutables(defs);
	}

	private void handleExecutables(List<PDefinition> defs)
			throws AnalysisException
	{
		for (PDefinition def : new LinkedList<>(defs))
		{
			if (def instanceof SOperationDefinition
					|| def instanceof SFunctionDefinition
					|| def instanceof ANamedTraceDefinition)
			{
				enclosingDef = def;
				def.apply(this);
			}
		}
	}
	
	@Override
	public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node) throws AnalysisException {

		if(isInRange(node.getLocation(), line) && !foundFunctionToConvert){
			foundFunctionToConvert = true;
			if(checkIfUsedInFunction(node)){
				refactoringLogger.addWarning("Function is used in another function!");
			}
			PStm convertedPStm = convertPExpToPStm(node.getBody());
			AExplicitFunctionDefinition nodeClone = node.clone();
			LexNameToken token = new LexNameToken(nodeClone.getName().getModule(), (nodeClone.getName().getName()), new LexLocation());
			
			List<PPattern> parameterTypes = new ArrayList<>();
			for(PPattern item : nodeClone.getParamPatternList().getFirst()){
				PPattern itemClone = item.clone();
				parameterTypes.add(itemClone);
			}
			
			AOperationType operationType = AstFactory.newAOperationType(new LexLocation(), nodeClone.getType().getParameters(), nodeClone.getExpectedResult());
			AExplicitOperationDefinition convertedOperation = AstFactory.newAExplicitOperationDefinition(token, operationType, parameterTypes,nodeClone.getPrecondition(), nodeClone.getPostcondition(),convertedPStm);
			AAccessSpecifierAccessSpecifier accessSpec = convertedOperation.getAccess();
			accessSpec.setPure(true);
			addToNodeCurrentModuleAndRemoveOld(convertedOperation, node);
			applyOccurrenceSwitcher(convertedOperation, node);
		}
	}

	private boolean checkIfUsedInFunction(AExplicitFunctionDefinition node) throws AnalysisException {
		CheckIfFunctionUsedInFunction checker = new CheckIfFunctionUsedInFunction(node);
		currentModule.apply(checker);
		return checker.isFunctionUsedInFunction();
	}
	@Override
	public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node) throws AnalysisException {
		lastOperation = node;
		super.caseAExplicitOperationDefinition(node);
	}
	
	public PStm convertPExpToPStm(PExp pExp){
		if(pExp instanceof PExp){
			return AstFactory.newAReturnStm(pExp.getLocation(), pExp);
		}
		return null;
	}
	
	private void applyOccurrenceSwitcher(AExplicitOperationDefinition toOperation, AExplicitFunctionDefinition function) throws AnalysisException
	{
		FunctionOccurrenceSwitcher collector = new FunctionOccurrenceSwitcher(toOperation, function, refactoringLogger);
		currentModule.apply(collector);
	}
	
	public void addToNodeCurrentModuleAndRemoveOld(PDefinition newNode, PDefinition oldNode){
		refactoringLogger.add(new ConversionFromFuncToOp(oldNode.getLocation(), oldNode.getName().getName()));
		if(lastOperation != null){
			int index = currentModule.getDefs().indexOf(lastOperation) + 1;
			currentModule.getDefs().remove(oldNode);
			currentModule.getDefs().add(index,newNode);
		}
	}
	
	public static boolean isInRange(ILexLocation loc, int from){	
		return loc.getStartLine() == from;
	}
	
	public List<ConversionFromFuncToOp> getAllConversionFromFuncToOp(){
		return new ArrayList<ConversionFromFuncToOp>(refactoringLogger.get());
	}
	
	public List<String> getWarnings(){
		return new ArrayList<String>(refactoringLogger.getWarnings());
	}
}
