package org.overture.codegen.traces;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AAnonymousClassExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.SCallStmCG;
import org.overture.codegen.cgast.traces.AApplyExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.ABracketedExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.AConcurrentExpTraceCoreDeclCG;
import org.overture.codegen.cgast.traces.ALetBeStBindingTraceDeclCG;
import org.overture.codegen.cgast.traces.ALetDefBindingTraceDeclCG;
import org.overture.codegen.cgast.traces.ARepeatTraceDeclCG;
import org.overture.codegen.cgast.traces.ATraceDeclTermCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class TraceStmsBuilder extends AnswerAdaptor<TraceNodeData>
{
	private IRInfo info;
	private List<SClassDeclCG> classes;
	private TransAssistantCG transAssistant;
	private IterationVarPrefixes iteVarPrefixes;
	private ILanguageIterator langIterator;
	private ICallStmToStringMethodBuilder toStringBuilder;

	private TraceNames tracePrefixes;
	private String traceEnclosingClass;

	private StoreAssistant storeAssistant; 
	
	private Map<String, String> idConstNameMap;
	
	public TraceStmsBuilder(IRInfo info, List<SClassDeclCG> classes,
			TransAssistantCG transAssistant, IterationVarPrefixes iteVarPrefixes,
			TraceNames tracePrefixes, ILanguageIterator langIterator,
			ICallStmToStringMethodBuilder toStringBuilder,
			String traceEnclosingClass)
	{
		this.info = info;
		this.classes = classes;
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;
		this.langIterator = langIterator;
		this.toStringBuilder = toStringBuilder;

		this.tracePrefixes = tracePrefixes;
		this.traceEnclosingClass = traceEnclosingClass;
		
		this.idConstNameMap = new HashMap<String, String>();

		this.storeAssistant = new StoreAssistant(tracePrefixes, idConstNameMap, transAssistant);
	}

	@Override
	public TraceNodeData caseATraceDeclTermCG(ATraceDeclTermCG node)
			throws AnalysisException
	{
		String name = info.getTempVarNameGen().nextVarName(tracePrefixes.altTraceNodeNamePrefix());
		AClassTypeCG classType = transAssistant.consClassType(tracePrefixes.altTraceNodeNodeClassName());

		if (node.getTraceDecls().size() == 1)
		{
			return node.getTraceDecls().getFirst().apply(this);
		}
		{
			AVarDeclCG altTests = transAssistant.consDecl(name, classType, transAssistant.consDefaultConsCall(classType));

			ABlockStmCG stms = new ABlockStmCG();
			stms.getLocalDefs().add(altTests);

			List<SStmCG> addStms = new LinkedList<SStmCG>();

			for (STraceDeclCG traceDecl : node.getTraceDecls())
			{
				TraceNodeData nodeData = traceDecl.apply(this);

				stms.getStatements().add(nodeData.getStms());
				addStms.add(transAssistant.consInstanceCallStm(classType, name, tracePrefixes.addMethodName(), nodeData.getNodeVar()));
			}
			
			stms.getStatements().addAll(addStms);

			return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
		}
	}

	@Override
	public TraceNodeData caseAApplyExpTraceCoreDeclCG(
			AApplyExpTraceCoreDeclCG node) throws AnalysisException
	{
		List<AVarDeclCG> argDecls = replaceArgsWithVars(node.getCallStm());

		AClassTypeCG callStmType = transAssistant.consClassType(tracePrefixes.callStmClassTypeName());
		String callStmName = info.getTempVarNameGen().nextVarName(tracePrefixes.callStmNamePrefix());
		AAnonymousClassExpCG callStmCreation = new AAnonymousClassExpCG();
		callStmCreation.setType(callStmType);
		callStmCreation.getMethods().add(consExecuteMethod(node.getCallStm().clone()));
		callStmCreation.getMethods().add(toStringBuilder.consToString(info, node.getCallStm(), idConstNameMap, storeAssistant, transAssistant));
		AVarDeclCG callStmDecl = transAssistant.consDecl(callStmName, callStmType.clone(), callStmCreation);

		AClassTypeCG stmTraceNodeType = transAssistant.consClassType(tracePrefixes.stmTraceNodeClassName());
		ANewExpCG newStmTraceNodeExp = transAssistant.consDefaultConsCall(stmTraceNodeType);
		newStmTraceNodeExp.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(callStmName, callStmType.clone()));

		String stmNodeName = info.getTempVarNameGen().nextVarName(tracePrefixes.stmTraceNodeNamePrefix());
		AVarDeclCG stmNodeDecl = transAssistant.consDecl(stmNodeName, stmTraceNodeType.clone(), newStmTraceNodeExp);

		ABlockStmCG decls = new ABlockStmCG();
		decls.getLocalDefs().addAll(argDecls);
		decls.getLocalDefs().add(callStmDecl);
		decls.getLocalDefs().add(stmNodeDecl);

		return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(stmNodeName, stmTraceNodeType.clone()), decls);
	}

	@Override
	public TraceNodeData caseABracketedExpTraceCoreDeclCG(
			ABracketedExpTraceCoreDeclCG node) throws AnalysisException
	{
		return buildFromDeclTerms(node.getTerms());
	}

	@Override
	public TraceNodeData caseAConcurrentExpTraceCoreDeclCG(
			AConcurrentExpTraceCoreDeclCG node) throws AnalysisException
	{
		String name = info.getTempVarNameGen().nextVarName(tracePrefixes.concTraceNodeNamePrefix());

		AClassTypeCG classType = transAssistant.consClassType(tracePrefixes.concTraceNodeNodeClassName());

		AVarDeclCG concNodeDecl = transAssistant.consDecl(name, classType, transAssistant.consDefaultConsCall(classType));

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(concNodeDecl);

		List<SStmCG> addStms = new LinkedList<SStmCG>();

		// The number of declarations is > 1
		for (STraceDeclCG term : node.getDecls())
		{
			TraceNodeData nodeData = term.apply(this);
			stms.getStatements().add(nodeData.getStms());

			AIdentifierVarExpCG var = nodeData.getNodeVar();
			addStms.add(transAssistant.consInstanceCallStm(classType, name, tracePrefixes.addMethodName(), var));
		}

		stms.getStatements().addAll(addStms);

		return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
	}

	@Override
	public TraceNodeData caseALetBeStBindingTraceDeclCG(
			ALetBeStBindingTraceDeclCG node) throws AnalysisException
	{

		ASetMultipleBindCG bind = node.getBind();
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(bind);
		List<AIdentifierPatternCG> patterns = idCollector.findOccurences();
		
		for(SPatternCG p : patterns)
		{
			if(p instanceof AIdentifierPatternCG)
			{
				String idConstName = info.getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
				idConstNameMap.put(((AIdentifierPatternCG) p).getName(), idConstName);
			}
		}
		
		String name = info.getTempVarNameGen().nextVarName(tracePrefixes.altTraceNodeNamePrefix());

		AClassTypeCG classType = transAssistant.consClassType(tracePrefixes.altTraceNodeNodeClassName());

		AIdentifierPatternCG id = info.getPatternAssistant().consIdPattern(name);

		AVarDeclCG altTests = transAssistant.consDecl(name, classType, transAssistant.consDefaultConsCall(classType));

		STraceDeclCG body = node.getBody();
		SExpCG exp = node.getStExp();

		TraceNodeData bodyTraceData = body.apply(this);

		SSetTypeCG setType = transAssistant.getSetTypeCloned(bind.getSet());
		TraceLetBeStStrategy strategy = new TraceLetBeStStrategy(transAssistant, exp, setType, langIterator, 
				info.getTempVarNameGen(), iteVarPrefixes, storeAssistant, idConstNameMap, tracePrefixes, id, altTests, bodyTraceData);

		if (transAssistant.hasEmptySet(bind))
		{
			transAssistant.cleanUpBinding(bind);
			return new TraceNodeData(null, transAssistant.wrap(new ASkipStmCG()));
		}

		ABlockStmCG outerBlock = transAssistant.consIterationBlock(node.getBind().getPatterns(), bind.getSet(), info.getTempVarNameGen(), strategy, iteVarPrefixes);

		return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(name, classType.clone()), transAssistant.wrap(outerBlock));
	}
	
	@Override
	public TraceNodeData caseALetDefBindingTraceDeclCG(
			ALetDefBindingTraceDeclCG node) throws AnalysisException
	{
		ABlockStmCG outer = new ABlockStmCG();
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		
		ABlockStmCG declBlock = new ABlockStmCG();
		declBlock.setScoped(true);

		for (AVarDeclCG dec : node.getLocalDefs())
		{
			idCollector.setTopNode(dec);
			List<AIdentifierPatternCG> idOccurences = idCollector.findOccurences();
			
			AVarDeclCG decCopy = dec.clone();
			decCopy.setFinal(true);
			declBlock.getLocalDefs().add(decCopy);
			
			for(AIdentifierPatternCG occ : idOccurences)
			{
				String idConstName = info.getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
				idConstNameMap.put(occ.getName(), idConstName);
				outer.getLocalDefs().add(storeAssistant.consIdConstDecl(idConstName));
				storeAssistant.appendStoreRegStms(declBlock, occ.getName(), idConstName);
			}
		}
		TraceNodeData bodyNodeData = node.getBody().apply(this);

		outer.getStatements().add(declBlock);
		outer.getStatements().add(bodyNodeData.getStms());
		
		return new TraceNodeData(bodyNodeData.getNodeVar(), outer);
	}

	@Override
	public TraceNodeData caseARepeatTraceDeclCG(ARepeatTraceDeclCG node)
			throws AnalysisException
	{
		Long from = node.getFrom();
		Long to = node.getTo();

		if (from == 1 && to == 1)
		{
			return node.getCore().apply(this);
		} else
		{
			String name = info.getTempVarNameGen().nextVarName(tracePrefixes.repeatTraceNodeNamePrefix());

			TraceNodeData traceData = node.getCore().apply(this);

			AIdentifierVarExpCG varArg = traceData.getNodeVar();
			AIntLiteralExpCG fromArg = info.getExpAssistant().consIntLiteral(from);
			AIntLiteralExpCG toArg = info.getExpAssistant().consIntLiteral(to);

			AClassTypeCG repeat = transAssistant.consClassType(tracePrefixes.repeatTraceNodeNamePrefix());

			ABlockStmCG block = new ABlockStmCG();
			block.getStatements().add(traceData.getStms());
			block.getStatements().add(consDecl(tracePrefixes.repeatTraceNodeNodeClassName(), name, varArg, fromArg, toArg));

			return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(name, repeat), block);
		}
	}

	private List<AVarDeclCG> replaceArgsWithVars(SStmCG callStm)
	{
		List<SExpCG> args = null;
		List<AVarDeclCG> decls = new LinkedList<AVarDeclCG>();
		if (callStm instanceof SCallStmCG)
		{
			args = ((SCallStmCG) callStm).getArgs();
		} else if (callStm instanceof ACallObjectStmCG)
		{
			args = ((ACallObjectStmCG) callStm).getArgs();
		} else
		{
			Logger.getLog().printErrorln("Expected a call statement or call object statement. Got: "
					+ callStm);
			return decls;
		}

		for (SExpCG arg : args)
		{
			if (!(arg instanceof SVarExpCG))
			{
				String argName = info.getTempVarNameGen().nextVarName(tracePrefixes.callStmArgNamePrefix());
				STypeCG type = arg.getType();

				AVarDeclCG argDecl = transAssistant.consDecl(argName, type.clone(), arg.clone());
				argDecl.setFinal(true);
				decls.add(argDecl);

				transAssistant.replaceNodeWith(arg, transAssistant.getInfo().getExpAssistant().consIdVar(argName, type.clone()));
			}

		}

		return decls;
	}
	
	private AMethodDeclCG consExecuteMethod(SStmCG stm)
	{
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new AObjectTypeCG());
		methodType.getParams().add(new AObjectTypeCG());
		
		AMethodDeclCG execMethod = new AMethodDeclCG();
		execMethod.setImplicit(false);
		execMethod.setAbstract(false);
		execMethod.setAccess(IRConstants.PUBLIC);
		execMethod.setAsync(false);
		execMethod.setIsConstructor(false);
		execMethod.setMethodType(methodType);
		execMethod.setName(tracePrefixes.callStmMethodNamePrefix());
		execMethod.setStatic(false);

		SStmCG body = makeInstanceCall(stm);
		try
		{
			final Set<String> localVarNames = this.idConstNameMap.keySet();
			
			body.apply(new DepthFirstAnalysisAdaptor()
			{
				// No need to consider explicit variables because they cannot be local
				
				@Override
				public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
						throws AnalysisException
				{
					if(localVarNames.contains(node.getName()))
					{
						transAssistant.replaceNodeWith(node, storeAssistant.consStoreLookup(node));
					}
				}
			});
			
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problem replacing variable expressions with storage lookups in TraceStmBuilder");
			return null;
		}
		execMethod.setBody(body);
		
		AFormalParamLocalParamCG instanceParam = new AFormalParamLocalParamCG();
		instanceParam.setType(new AObjectTypeCG());
		instanceParam.setPattern(info.getPatternAssistant().consIdPattern(tracePrefixes.callStmMethodParamName()));

		execMethod.getFormalParams().add(instanceParam);

		return execMethod;
	}

	
	public TraceNodeData buildFromDeclTerms(List<ATraceDeclTermCG> terms)
			throws AnalysisException
	{
		String name = info.getTempVarNameGen().nextVarName(tracePrefixes.seqTraceNodeNamePrefix());

		AClassTypeCG classType = transAssistant.consClassType(tracePrefixes.seqClassTypeName());

		AVarDeclCG seqNodeDecl = transAssistant.consDecl(name, classType, transAssistant.consDefaultConsCall(classType));

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(seqNodeDecl);

		List<SStmCG> addStms = new LinkedList<SStmCG>();

		for (ATraceDeclTermCG term : terms)
		{
			TraceNodeData nodeData = term.apply(this);
			stms.getStatements().add(nodeData.getStms());

			AIdentifierVarExpCG var = nodeData.getNodeVar();
			addStms.add(transAssistant.consInstanceCallStm(classType, name, tracePrefixes.addMethodName(), var));
		}

		stms.getStatements().addAll(addStms);

		return new TraceNodeData(transAssistant.getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
	}

	private SStmCG makeInstanceCall(SStmCG stm)
	{
		if (stm instanceof ACallObjectStmCG)
		{
			// Assume the class enclosing the trace to be S
			// self.op(42) becomes ((S)instance).op(42L)
			// a.op(42) remains a.op(42L) if a is local
			// a.op(42) becomes ((S)instance).a.op(42L) if a is an instance variable

			ACallObjectStmCG callObj = (ACallObjectStmCG) stm;
			ensureLocalObjDesignator(callObj.getDesignator());

			if (callObj.getType() instanceof AVoidTypeCG)
			{
				return handleVoidValueReturn(stm);
			} else
			{
				try
				{
					return handleCallStmResult(callObj);

				} catch (AnalysisException e)
				{
				}
			}
		} else if (stm instanceof APlainCallStmCG)
		{
			// Assume the class enclosing the trace to be S
			// Example: op(42) becomes ((S)instance).op(42L)
			try
			{
				return handlePlainCallStm((APlainCallStmCG) stm);
			} catch (AnalysisException e)
			{
			}
		}
		// Super call statements are not supported and this case should not be reached!

		Logger.getLog().printErrorln("Got unexpected statement type in TraceStmsBuilder: "
				+ stm);

		return stm;
	}

	private SStmCG handleVoidValueReturn(SStmCG stm)
	{
		AExternalTypeCG traceNodeClassType = new AExternalTypeCG();
		traceNodeClassType.setName(tracePrefixes.voidValueEnclosingClassName());

		AExplicitVarExpCG voidValue = new AExplicitVarExpCG();
		voidValue.setType(new AObjectTypeCG());
		voidValue.setClassType(traceNodeClassType);
		voidValue.setIsLambda(false);
		voidValue.setIsLocal(true);
		voidValue.setName(tracePrefixes.voidValueFieldName());

		AReturnStmCG returnVoidVal = new AReturnStmCG();
		returnVoidVal.setExp(voidValue);

		ABlockStmCG block = new ABlockStmCG();

		block.getStatements().add(stm);
		block.getStatements().add(returnVoidVal);

		return block;
	}

	private SStmCG handleCallStmResult(ACallObjectStmCG callObj)
			throws AnalysisException
	{
		AFieldExpCG field = new AFieldExpCG();
		field.setMemberName(callObj.getFieldName());
		field.setObject(callObj.getDesignator().apply(new ObjectDesignatorToExpCG(info)));
		field.setType(info.getTypeAssistant().getFieldType(classes, traceEnclosingClass, callObj.getFieldName()));

		AApplyExpCG apply = new AApplyExpCG();
		apply.setRoot(field);
		apply.setType(callObj.getType().clone());

		for (SExpCG arg : callObj.getArgs())
		{
			apply.getArgs().add(arg.clone());
		}

		apply.setSourceNode(callObj.getSourceNode());
		apply.setTag(callObj.getSourceNode());

		String resultName = info.getTempVarNameGen().nextVarName(tracePrefixes.callStmResultNamePrefix());

		AVarDeclCG resultDecl = transAssistant.consDecl(resultName, callObj.getType().clone(), apply);
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(transAssistant.getInfo().getExpAssistant().consIdVar(resultName, callObj.getType().clone()));

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(resultDecl);
		stms.getStatements().add(returnStm);

		return stms;
	}

	private SStmCG handlePlainCallStm(APlainCallStmCG callStmCG)
			throws AnalysisException
	{
		if (callStmCG.getIsStatic())
		{
			return callStmCG;
		}

		List<SExpCG> args = callStmCG.getArgs();
		STypeCG classType = callStmCG.getClassType();
		STypeCG type = callStmCG.getType();
		String name = callStmCG.getName();

		SourceNode sourceNode = callStmCG.getSourceNode();

		AClassTypeCG consClassType = transAssistant.consClassType(traceEnclosingClass);

		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(consClassType);
		cast.setExp(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.callStmMethodParamName(), consClassType.clone()));

		if (type instanceof AVoidTypeCG)
		{
			ACallObjectExpStmCG paramExp = new ACallObjectExpStmCG();
			paramExp.setObj(cast);

			paramExp.setFieldName(name);
			paramExp.setType(type.clone());

			for (SExpCG arg : args)
			{
				paramExp.getArgs().add(arg.clone());
			}

			paramExp.setSourceNode(sourceNode);

			return handleVoidValueReturn(paramExp);
		} else
		{
			AFieldExpCG field = new AFieldExpCG();
			String fieldModule = classType instanceof AClassTypeCG ? ((AClassTypeCG) classType).getName()
					: traceEnclosingClass;
			field.setType(info.getTypeAssistant().getMethodType(info, fieldModule, name, args));
			field.setMemberName(name);
			field.setObject(cast);

			AApplyExpCG apply = new AApplyExpCG();
			apply.setType(type.clone());
			apply.setRoot(field);
			apply.setSourceNode(callStmCG.getSourceNode());

			for (SExpCG arg : args)
			{
				apply.getArgs().add(arg.clone());
			}

			String resultName = info.getTempVarNameGen().nextVarName(tracePrefixes.callStmResultNamePrefix());
			AVarDeclCG resultDecl = transAssistant.consDecl(resultName, type.clone(), apply);

			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(transAssistant.getInfo().getExpAssistant().consIdVar(resultName, type.clone()));

			ABlockStmCG stms = new ABlockStmCG();
			stms.getLocalDefs().add(resultDecl);
			stms.getStatements().add(returnStm);

			return stms;

		}
	}

	private void ensureLocalObjDesignator(SObjectDesignatorCG obj)
	{
		try
		{
			AIdentifierObjectDesignatorCG objId = obj.apply(new EnsureLocalObjDesignatorAnalysis(transAssistant, tracePrefixes, traceEnclosingClass));

			if (objId != null)
			{
				objId.apply(new VarExpCasting(transAssistant, traceEnclosingClass));
			}

		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problems encountered when attempting to ensuring a local object designator in TraceStmsBuilder");
			e.printStackTrace();
		}
	}

	private ABlockStmCG consDecl(String classTypeName, String varName,
			SExpCG... args)
	{
		ATypeNameCG typeName = transAssistant.consTypeNameForClass(classTypeName);

		AClassTypeCG classType = transAssistant.consClassType(classTypeName);

		ANewExpCG newExp = new ANewExpCG();
		newExp.setName(typeName);
		newExp.setType(classType);

		for (SExpCG arg : args)
		{
			newExp.getArgs().add(arg);
		}

		return transAssistant.wrap(transAssistant.consDecl(varName, classType.clone(), newExp));
	}

	@Override
	public TraceNodeData createNewReturnValue(INode node)
			throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public TraceNodeData createNewReturnValue(Object node)
			throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
