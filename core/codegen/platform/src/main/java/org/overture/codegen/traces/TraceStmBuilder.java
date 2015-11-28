package org.overture.codegen.traces;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
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
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.config.Settings;

public class TraceStmBuilder extends AnswerAdaptor<TraceNodeData>
{
	private TransAssistantCG transAssistant;
	private IterationVarPrefixes iteVarPrefixes;
	private ILanguageIterator langIterator;
	private ICallStmToStringMethodBuilder toStringBuilder;

	private TraceNames tracePrefixes;
	private String traceEnclosingClass;

	private StoreAssistant storeAssistant; 
	
	public TraceStmBuilder(TransAssistantCG transAssistant, IterationVarPrefixes iteVarPrefixes,
			TraceNames tracePrefixes, ILanguageIterator langIterator, ICallStmToStringMethodBuilder toStringBuilder,
			String traceEnclosingClass, StoreAssistant storeAssist)
	{
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;
		this.langIterator = langIterator;
		this.toStringBuilder = toStringBuilder;

		this.tracePrefixes = tracePrefixes;
		this.traceEnclosingClass = traceEnclosingClass;

		this.storeAssistant = storeAssist;
	}

	public IRInfo getInfo()
	{
		return transAssistant.getInfo();
	}
	
	@Override
	public TraceNodeData caseATraceDeclTermCG(ATraceDeclTermCG node)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.altTraceNodeNamePrefix());
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

		String classTypeName;
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			classTypeName = tracePrefixes.callStmClassTypeName();
		}
		else 
		{
			classTypeName = tracePrefixes.callStmBaseClassTypeName();
		}
		
		AClassTypeCG callStmType = transAssistant.consClassType(classTypeName);
		String callStmName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.callStmNamePrefix());
		AAnonymousClassExpCG callStmCreation = new AAnonymousClassExpCG();
		callStmCreation.setType(callStmType);
		callStmCreation.getMethods().add(consExecuteMethod(node.getCallStm().clone()));
		callStmCreation.getMethods().add(toStringBuilder.consToString(getInfo(), node.getCallStm(), storeAssistant.getIdConstNameMap(), storeAssistant, transAssistant));
		AVarDeclCG callStmDecl = transAssistant.consDecl(callStmName, callStmType.clone(), callStmCreation);

		AClassTypeCG stmTraceNodeType = transAssistant.consClassType(tracePrefixes.stmTraceNodeClassName());
		ANewExpCG newStmTraceNodeExp = transAssistant.consDefaultConsCall(stmTraceNodeType);
		newStmTraceNodeExp.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(callStmName, callStmType.clone()));

		String stmNodeName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.stmTraceNodeNamePrefix());
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
		String name = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.concTraceNodeNamePrefix());

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
				String idConstName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
				storeAssistant.getIdConstNameMap().put(((AIdentifierPatternCG) p).getName(), idConstName);
			}
		}
		
		String name = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.altTraceNodeNamePrefix());

		AClassTypeCG classType = transAssistant.consClassType(tracePrefixes.altTraceNodeNodeClassName());

		AIdentifierPatternCG id = getInfo().getPatternAssistant().consIdPattern(name);

		AVarDeclCG altTests = transAssistant.consDecl(name, classType, transAssistant.consDefaultConsCall(classType));

		STraceDeclCG body = node.getBody();
		SExpCG exp = node.getStExp();

		TraceNodeData bodyTraceData = body.apply(this);

		SSetTypeCG setType = transAssistant.getSetTypeCloned(bind.getSet());
		TraceLetBeStStrategy strategy = new TraceLetBeStStrategy(transAssistant, exp, setType, langIterator, 
				getInfo().getTempVarNameGen(), iteVarPrefixes, storeAssistant, storeAssistant.getIdConstNameMap(), tracePrefixes, id, altTests, bodyTraceData);

		if (transAssistant.hasEmptySet(bind))
		{
			transAssistant.cleanUpBinding(bind);
			return new TraceNodeData(null, transAssistant.wrap(new ASkipStmCG()));
		}

		ABlockStmCG outerBlock = transAssistant.consIterationBlock(node.getBind().getPatterns(), bind.getSet(), getInfo().getTempVarNameGen(), strategy, iteVarPrefixes);

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
				String idConstName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
				storeAssistant.getIdConstNameMap().put(occ.getName(), idConstName);
				outer.getLocalDefs().add(storeAssistant.consIdConstDecl(idConstName));
				storeAssistant.appendStoreRegStms(declBlock, occ.getName(), idConstName, false);
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
			String name = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.repeatTraceNodeNamePrefix());

			TraceNodeData traceData = node.getCore().apply(this);

			AIdentifierVarExpCG varArg = traceData.getNodeVar();
			AIntLiteralExpCG fromArg = getInfo().getExpAssistant().consIntLiteral(from);
			AIntLiteralExpCG toArg = getInfo().getExpAssistant().consIntLiteral(to);

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
		} else if (callStm instanceof ACallObjectExpStmCG)
		{
			args = ((ACallObjectExpStmCG) callStm).getArgs();
		} else
		{
			Logger.getLog().printErrorln("Expected a call statement or call object statement in '"
					+ this.getClass().getSimpleName() + "'. Got: " + callStm);
			return decls;
		}

		for (SExpCG arg : args)
		{
			if (!(arg instanceof SVarExpCG))
			{
				String argName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.callStmArgNamePrefix());
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
		
		AMethodDeclCG execMethod = new AMethodDeclCG();
		execMethod.setImplicit(false);
		execMethod.setAbstract(false);
		execMethod.setAccess(IRConstants.PUBLIC);
		execMethod.setAsync(false);
		execMethod.setIsConstructor(false);
		execMethod.setMethodType(methodType);
		execMethod.setName(tracePrefixes.callStmMethodNamePrefix());
		execMethod.setStatic(false);

		ABlockStmCG body = new ABlockStmCG();
		
		SStmCG preCallStms = makePreCallStms(stm);
		
		if(preCallStms != null)
		{
			body.getStatements().add(preCallStms);
		}
		
		body.getStatements().add(makeInstanceCall(stm));
		
		try
		{
			final Set<String> localVarNames = storeAssistant.getIdConstNameMap().keySet();
			
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
		
		return execMethod;
	}

	
	public SStmCG makePreCallStms(SStmCG stm)
	{
		return null;
	}

	public TraceNodeData buildFromDeclTerms(List<ATraceDeclTermCG> terms)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.seqTraceNodeNamePrefix());

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
		if(stm instanceof ACallObjectExpStmCG)
		{
			// Assume the class enclosing the trace to be S
			// self.op(42) becomes ((S)instance).op(42L)
			// a.op(42) remains a.op(42L) if a is local
			// a.op(42) becomes ((S)instance).a.op(42L) if a is an instance variable
			
			ACallObjectExpStmCG call = (ACallObjectExpStmCG) stm;
			
			try
			{
				call.getObj().apply(new CallObjTraceLocalizer(transAssistant, tracePrefixes, traceEnclosingClass));
			} catch (AnalysisException e)
			{
				Logger.getLog().printErrorln("Got unexpected problem when trying to apply "
						+ CallObjTraceLocalizer.class.getSimpleName() + " in '" + this.getClass().getSimpleName());
				e.printStackTrace();
			}
			
			if(call.getType() instanceof AVoidTypeCG)
			{
				return handleVoidValueReturn(call);
			}
			
			return stm;
		}
	    else if (stm instanceof APlainCallStmCG)
		{
			// Assume the class enclosing the trace to be S
			// Example: op(42) becomes ((S)instance).op(42L)
			try
			{
				return handlePlainCallStm((APlainCallStmCG) stm);
			} catch (AnalysisException e)
			{
				Logger.getLog().printErrorln("Got unexpected problem when handling plain call statement in '"
						+ this.getClass().getSimpleName() + "'");
				e.printStackTrace();
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

	private SStmCG handlePlainCallStm(APlainCallStmCG callStmCG)
			throws AnalysisException
	{
		STypeCG type = callStmCG.getType();
		
		if (callStmCG.getIsStatic())
		{
			if (type instanceof AVoidTypeCG)
			{
				return handleVoidValueReturn(callStmCG);
			} else
			{
				return callStmCG;
			}
		}

		List<SExpCG> args = callStmCG.getArgs();
		STypeCG classType = callStmCG.getClassType();
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
			field.setType(getInfo().getTypeAssistant().getMethodType(getInfo(), fieldModule, name, args));
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

			String resultName = getInfo().getTempVarNameGen().nextVarName(tracePrefixes.callStmResultNamePrefix());
			AVarDeclCG resultDecl = transAssistant.consDecl(resultName, type.clone(), apply);

			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(transAssistant.getInfo().getExpAssistant().consIdVar(resultName, type.clone()));

			ABlockStmCG stms = new ABlockStmCG();
			stms.getLocalDefs().add(resultDecl);
			stms.getStatements().add(returnStm);

			return stms;

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
