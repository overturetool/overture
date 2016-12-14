package org.overture.add.parameter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class RefactoringAddParameterCollector extends DepthFirstAnalysisAdaptor {

	private PDefinition enclosingDef;
	private int enclosingCounter;

	private Set<AddParameterRefactoring> addParameterRefactorings;
	private Set<String> namesToAvoid;
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private String[] parameters;
	private int startLine;
	private String paramType;
	private String paramName;
	private String paramPlaceholder;
	private boolean isParamListEmpty = true;
	
	public RefactoringAddParameterCollector(ITypeCheckerAssistantFactory af,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.addParameterRefactorings = new HashSet<AddParameterRefactoring>();
		this.namesToAvoid = new HashSet<String>();
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
		}
		else 
		{
			visitModuleDefs(node.getDefs(), node);
		}
	}

	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node) throws AnalysisException {
		if (!proceed(node)) {
			return;
		}
		
		if(compareNodeLocation(node.getLocation())){

			LexNameToken parName = new LexNameToken(node.getName().getModule(), paramName, null);
			
			//Add parameter to node patterns
			LinkedList<PPattern> patterns = node.getParameterPatterns();
			AIdentifierPattern pat = new AIdentifierPattern();
			LexLocation newLastLoc = new LexLocation();
			isParamListEmpty = patterns.isEmpty();
			PDefinition newParam;
			
			if(!isParamListEmpty){
				PPattern lastPattern = patterns.getLast();
				ILexLocation lastLoc = lastPattern.getLocation();
				newLastLoc = AddParameterUtil.calculateNewParamLocationWhenNotEmptyList(lastLoc, parName.toString());
			} else{
				newLastLoc = AddParameterUtil.calculateParamLocationWhenEmptyList(node.getType().getLocation(), parName.toString());
			}
			
			//Set patterns
			pat.setLocation(newLastLoc);
			pat.setName(parName);
			patterns.add(pat);
			
			//Set paramName
			newParam = new ALocalDefinition();
			newParam.setName(parName);
			
			//Get expression object
			AddParameterExpObject expObj = getParamExpObj(paramType, paramPlaceholder, newLastLoc);
			newParam.setType(expObj.getType());
			
			//Add parameter to node definitions
			node.getParamDefinitions().add(newParam);
			
			//Add parameter to node operation type
			PType operationType = node.getType();
			if(operationType instanceof AOperationType){
				((AOperationType) operationType).getParameters().add(expObj.getType());
			} else if(operationType instanceof AFunctionType){
				//Add param to function...
			}
			
			addParameterRefactorings.add(new AddParameterRefactoring(newLastLoc, parName.toString(), node.getName().getName(), newParam.getType().toString()));
			
			//Update calls
			signatureChangeCallOccurences(node.getLocation(), node.parent(), this::registerSignatureChange);
			
			//Update applications
			signatureChangeApplicationOccurences(node.getLocation(), node.parent(), this::registerSignatureChange);
		}		
	}
	
	private Set<AApplyExp> signatureChangeApplicationOccurences(ILexLocation defLoc, INode defScope, 
			Consumer<AddParameterObject> function) throws AnalysisException{
		ApplyOccurrenceSignatureChanger collector = new ApplyOccurrenceSignatureChanger(defLoc, function, paramName, paramPlaceholder, paramType, isParamListEmpty);
		defScope.apply(collector);
		return collector.getApplications();		
	}

	private Set<ACallStm> signatureChangeCallOccurences(ILexLocation defLoc, INode defScope,
			Consumer<AddParameterObject> function) throws AnalysisException{
		CallOccurrenceSignatureChanger collector = new CallOccurrenceSignatureChanger(defLoc, function, paramName, paramPlaceholder, paramType, isParamListEmpty);
		defScope.apply(collector);
		return collector.getCalls();
	}

	private void registerSignatureChange(AddParameterObject reObj){
		if (!contains(reObj.location))
		{
			addParameterRefactorings.add(new AddParameterRefactoring(reObj.location, reObj.newParamName.getName(), reObj.parentName, 
					reObj.paramType));	
			reObj.paramList.add(getParamExpObj(paramType, paramPlaceholder, reObj.location).getExpression());
		}
	}
	
	private AddParameterExpObject getParamExpObj(String aParamType, String aParamPlaceholder, ILexLocation loc){
		AddParameterExpObject expObj = new AddParameterExpObject();
		
		switch(ParamType.valueOf(aParamType.toUpperCase())){
		case BOOL:
			ABooleanBasicType boolType = new ABooleanBasicType();
			expObj.setType(boolType);
			ABooleanConstExp boolExp = new ABooleanConstExp();
			LexBooleanToken boolToken = new LexBooleanToken(Boolean.parseBoolean(aParamPlaceholder), loc);
			boolExp.setType(boolType);
			boolExp.setValue(boolToken);
			boolExp.setLocation(loc);
			expObj.setExpression(boolExp);
			return expObj;
		case NAT:
			ANatNumericBasicType natType = new ANatNumericBasicType();
			expObj.setType(natType);
			LexIntegerToken natToken = new LexIntegerToken(Integer.parseInt(aParamPlaceholder), loc);
			AIntLiteralExp natExp = new AIntLiteralExp();
			natExp.setType(natType);
			natExp.setValue(natToken);
			natExp.setLocation(loc);
			expObj.setExpression(natExp);
			return expObj;
		case NAT1:
//			this.paramType = new ANatOneNumericBasicType();
//			this.paramPlaceholder = Integer.parseInt(aParamPlaceholder);
			return null;
		default:
			paramPlaceholder = String.valueOf(aParamPlaceholder);
			return null;
		}		
	}
	
	private boolean contains(ILexLocation loc)
	{
		for (AddParameterRefactoring r : addParameterRefactorings)
		{
			if (r.getLoc().equals(loc))
			{
				return true;
			}
		}

		return false;
	}
		
	@Override
	public void caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
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
				enclosingCounter = 0;

				def.apply(this);
			}
		}
	}

	public void init(boolean clearSignatureChanges)
	{
		this.enclosingDef = null;
		this.enclosingCounter = 0;
		this.namesToAvoid.clear();

		if (addParameterRefactorings != null && clearSignatureChanges)
		{
			addParameterRefactorings.clear();
		}
	}

	public Set<AddParameterRefactoring> getSignatureChanges()
	{
		return addParameterRefactorings;
	}

	private boolean proceed(INode node)
	{
		if (node == enclosingDef)
		{
			enclosingCounter++;
		}

		if (enclosingCounter > 1)
		{
			// To protect against recursion
			return false;
		}

		PDefinition def = node.getAncestor(SOperationDefinition.class);

		if (def == null)
		{
			def = node.getAncestor(SFunctionDefinition.class);

			if (def == null)
			{
				def = node.getAncestor(ANamedTraceDefinition.class);

				if (def == null)
				{
					def = node.getAncestor(AValueDefinition.class);

					if (def == null)
					{
						def = node.getAncestor(AInstanceVariableDefinition.class);

						if (def == null)
						{
							def = node.getAncestor(ATypeDefinition.class);

							if (def == null)
							{
								def = node.getAncestor(AStateDefinition.class);
							}
						}
					}
				}
			}
		}

		if (def == null)
		{
			log.error("Got unexpected definition: " + enclosingDef);
		}

		return enclosingDef == def;
	}
	
	private boolean compareNodeLocation(ILexLocation newNode){
		if(parameters.length >= 3){
			if(newNode.getStartLine() == startLine){
				return true;
			}
		}
		return false;
	}
	
	public void setRefactoringParameters(String[] parameters) {
		if(parameters.length >= 4){
			this.parameters = parameters;
			this.startLine = Integer.parseInt(parameters[0]);
			this.paramName = parameters[1];
			this.paramType = parameters[2];
			this.paramPlaceholder = parameters[3];
		}
	}
}
