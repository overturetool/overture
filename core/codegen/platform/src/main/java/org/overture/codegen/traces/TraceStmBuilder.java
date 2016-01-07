package org.overture.codegen.traces;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STraceDeclCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
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
import org.overture.codegen.cgast.expressions.ATypeArgExpCG;
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
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
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
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.config.Settings;

public class TraceStmBuilder extends AnswerAdaptor<TraceNodeData>
{
	protected String traceEnclosingClass;
	protected StoreAssistant storeAssistant; 
	protected TracesTrans traceTrans;
	
	public TraceStmBuilder(TracesTrans traceTrans, String traceEnclosingClass, StoreAssistant storeAssist)
	{
		this.traceTrans = traceTrans;
		this.traceEnclosingClass = traceEnclosingClass;
		this.storeAssistant = storeAssist;
	}

	public IRInfo getInfo()
	{
		return getTransAssist().getInfo();
	}
	
	@Override
	public TraceNodeData caseATraceDeclTermCG(ATraceDeclTermCG node)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().altTraceNodeNamePrefix());
		AClassTypeCG classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().altTraceNodeNodeClassName());

		if (node.getTraceDecls().size() == 1)
		{
			return node.getTraceDecls().getFirst().apply(this);
		}
		{
			AVarDeclCG altTests = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

			ABlockStmCG stms = new ABlockStmCG();
			stms.getLocalDefs().add(altTests);

			List<SStmCG> addStms = new LinkedList<SStmCG>();

			for (STraceDeclCG traceDecl : node.getTraceDecls())
			{
				TraceNodeData nodeData = traceDecl.apply(this);

				stms.getStatements().add(nodeData.getStms());
				addStms.add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), nodeData.getNodeVar()));
			}
			
			stms.getStatements().addAll(addStms);

			return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
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
			classTypeName = traceTrans.getTracePrefixes().callStmClassTypeName();
		}
		else 
		{
			classTypeName = traceTrans.getTracePrefixes().callStmBaseClassTypeName();
		}
		
		AClassTypeCG callStmType = getTransAssist().consClassType(classTypeName);
		String callStmName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmNamePrefix());
		AAnonymousClassExpCG callStmCreation = new AAnonymousClassExpCG();
		callStmCreation.setType(callStmType);
		
		AMethodDeclCG typeCheckMethod = consTypeCheckMethod(node.getCallStm().clone());
		
		if(typeCheckMethod != null)
		{
			callStmCreation.getMethods().add(typeCheckMethod);
		}
		
		AMethodDeclCG preCondMethod = condMeetsPreCondMethod(node.getCallStm().clone());
		
		if(preCondMethod != null)
		{
			callStmCreation.getMethods().add(preCondMethod);
		}

		callStmCreation.getMethods().add(consExecuteMethod(node.getCallStm().clone()));
		callStmCreation.getMethods().add(traceTrans.getToStringBuilder().consToString(getInfo(), node.getCallStm(), storeAssistant.getIdConstNameMap(), storeAssistant, getTransAssist()));
		AVarDeclCG callStmDecl = getTransAssist().consDecl(callStmName, callStmType.clone(), callStmCreation);

		AClassTypeCG stmTraceNodeType = getTransAssist().consClassType(traceTrans.getTracePrefixes().stmTraceNodeClassName());
		ANewExpCG newStmTraceNodeExp = getTransAssist().consDefaultConsCall(stmTraceNodeType);
		newStmTraceNodeExp.getArgs().add(getInfo().getExpAssistant().consIdVar(callStmName, callStmType.clone()));

		String stmNodeName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().stmTraceNodeNamePrefix());
		AVarDeclCG stmNodeDecl = getTransAssist().consDecl(stmNodeName, stmTraceNodeType.clone(), newStmTraceNodeExp);

		ABlockStmCG decls = new ABlockStmCG();
		decls.getLocalDefs().addAll(argDecls);
		decls.getLocalDefs().add(callStmDecl);
		decls.getLocalDefs().add(stmNodeDecl);

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(stmNodeName, stmTraceNodeType.clone()), decls);
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
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().concTraceNodeNamePrefix());

		AClassTypeCG classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().concTraceNodeNodeClassName());

		AVarDeclCG concNodeDecl = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(concNodeDecl);

		List<SStmCG> addStms = new LinkedList<SStmCG>();

		// The number of declarations is > 1
		for (STraceDeclCG term : node.getDecls())
		{
			TraceNodeData nodeData = term.apply(this);
			stms.getStatements().add(nodeData.getStms());

			AIdentifierVarExpCG var = nodeData.getNodeVar();
			addStms.add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), var));
		}

		stms.getStatements().addAll(addStms);

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
	}
	
	@Override
	public TraceNodeData caseALetBeStBindingTraceDeclCG(
			ALetBeStBindingTraceDeclCG node) throws AnalysisException
	{
		ASetMultipleBindCG bind = node.getBind();
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(bind);
		List<AIdentifierPatternCG> patterns = idCollector.findOccurences();
		
		for (AIdentifierPatternCG p : patterns)
		{
			String idConstName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().idConstNamePrefix());
			storeAssistant.getIdConstNameMap().put(((AIdentifierPatternCG) p).getName(), idConstName);
		}
		
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().altTraceNodeNamePrefix());

		AClassTypeCG classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().altTraceNodeNodeClassName());

		AIdentifierPatternCG id = getInfo().getPatternAssistant().consIdPattern(name);

		AVarDeclCG altTests = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		STraceDeclCG body = node.getBody();
		SExpCG exp = node.getStExp();

		TraceNodeData bodyTraceData = body.apply(this);

		SSetTypeCG setType = getTransAssist().getSetTypeCloned(bind.getSet());
		TraceLetBeStStrategy strategy = new TraceLetBeStStrategy(getTransAssist(), exp, setType, traceTrans.getLangIterator(), 
				getInfo().getTempVarNameGen(), traceTrans.getIteVarPrefixes(), storeAssistant, storeAssistant.getIdConstNameMap(), traceTrans.getTracePrefixes(), id, altTests, bodyTraceData, this);

		if (getTransAssist().hasEmptySet(bind))
		{
			getTransAssist().cleanUpBinding(bind);
			return new TraceNodeData(null, getTransAssist().wrap(new ASkipStmCG()));
		}

		ABlockStmCG outerBlock = getTransAssist().consIterationBlock(node.getBind().getPatterns(), bind.getSet(), getInfo().getTempVarNameGen(), strategy, traceTrans.getIteVarPrefixes());

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), getTransAssist().wrap(outerBlock));
	}
	
	@Override
	public TraceNodeData caseALetDefBindingTraceDeclCG(
			ALetDefBindingTraceDeclCG node) throws AnalysisException
	{
		ABlockStmCG outer = new ABlockStmCG();
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		
		ABlockStmCG declBlock = new ABlockStmCG();
		declBlock.setScoped(true);

		List<AIdentifierVarExpCG> traceVars = new LinkedList<>();
		
		for (AVarDeclCG dec : node.getLocalDefs())
		{
			// Find types for all sub patterns
			PatternTypeFinder typeFinder = new PatternTypeFinder(getInfo());
			dec.getPattern().apply(typeFinder, dec.getType());
			
			idCollector.setTopNode(dec);
			List<AIdentifierPatternCG> idOccurences = idCollector.findOccurences();
			
			AVarDeclCG decCopy = dec.clone();
			decCopy.setFinal(true);
			declBlock.getLocalDefs().add(decCopy);
			
			for(AIdentifierPatternCG occ : idOccurences)
			{
				String idConstName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().idConstNamePrefix());
				storeAssistant.getIdConstNameMap().put(occ.getName(), idConstName);
				outer.getLocalDefs().add(storeAssistant.consIdConstDecl(idConstName));
				storeAssistant.appendStoreRegStms(declBlock, occ.getName(), idConstName, false);
				
				traceVars.add(getInfo().getExpAssistant().consIdVar(occ.getName(), PatternTypeFinder.getType(typeFinder, occ)));
			}
		}
		
		TraceNodeData bodyNodeData = node.getBody().apply(this);

		for(int i = traceVars.size() - 1; i >= 0; i--)
		{
			AIdentifierVarExpCG a = traceVars.get(i);
			
			ACallObjectExpStmCG addVar = consAddTraceVarCall(bodyNodeData.getNodeVar(), a, true);
			useStoreLookups(addVar);
			bodyNodeData.getStms().getStatements().add(addVar);
		}
		
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
			String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().repeatTraceNodeNamePrefix());

			TraceNodeData traceData = node.getCore().apply(this);

			AIdentifierVarExpCG varArg = traceData.getNodeVar();
			AIntLiteralExpCG fromArg = getInfo().getExpAssistant().consIntLiteral(from);
			AIntLiteralExpCG toArg = getInfo().getExpAssistant().consIntLiteral(to);

			AClassTypeCG repeat = getTransAssist().consClassType(traceTrans.getTracePrefixes().repeatTraceNodeNamePrefix());

			ABlockStmCG block = new ABlockStmCG();
			block.getStatements().add(traceData.getStms());
			block.getStatements().add(consDecl(traceTrans.getTracePrefixes().repeatTraceNodeNodeClassName(), name, varArg, fromArg, toArg));

			return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, repeat), block);
		}
	}

	protected List<AVarDeclCG> replaceArgsWithVars(SStmCG callStm)
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
				String argName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmArgNamePrefix());
				STypeCG type = arg.getType();

				AVarDeclCG argDecl = getTransAssist().consDecl(argName, type.clone(), arg.clone());
				argDecl.setFinal(true);
				decls.add(argDecl);

				getTransAssist().replaceNodeWith(arg, getInfo().getExpAssistant().consIdVar(argName, type.clone()));
			}

		}

		return decls;
	}
	
	protected AMethodDeclCG consExecuteMethod(SStmCG stm)
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
		execMethod.setName(traceTrans.getTracePrefixes().callStmExecMethodNamePrefix());
		execMethod.setStatic(false);

		ABlockStmCG body = new ABlockStmCG();
		body.getStatements().add(makeInstanceCall(stm));
		
		useStoreLookups(body);
		
		execMethod.setBody(body);
		
		return execMethod;
	}

	protected void useStoreLookups(SStmCG body)
	{
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
						getTransAssist().replaceNodeWith(node, storeAssistant.consStoreLookup(node));
					}
				}
			});
			
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Problem replacing variable expressions with storage lookups in TraceStmBuilder");
		}
	}

	public TraceNodeData buildFromDeclTerms(List<ATraceDeclTermCG> terms)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().seqTraceNodeNamePrefix());

		AClassTypeCG classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().seqClassTypeName());

		AVarDeclCG seqNodeDecl = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(seqNodeDecl);

		List<SStmCG> addStms = new LinkedList<SStmCG>();

		for (ATraceDeclTermCG term : terms)
		{
			TraceNodeData nodeData = term.apply(this);
			stms.getStatements().add(nodeData.getStms());

			AIdentifierVarExpCG var = nodeData.getNodeVar();
			addStms.add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), var));
		}

		stms.getStatements().addAll(addStms);

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms);
	}
	
	public AMethodDeclCG consTypeCheckMethod(SStmCG stm)
	{
		return null;
	}
	
	public AMethodDeclCG condMeetsPreCondMethod(SStmCG stm)
	{
		if (!canBeGenerated())
		{
			return null;
		}

		AMethodDeclCG meetsPredMethod = initPredDecl(traceTrans.getTracePrefixes().callStmMeetsPreCondNamePrefix());

		boolean isOp = false;
		String pre = "pre_";
		List<SExpCG> args = null;

		if (stm instanceof APlainCallStmCG)
		{
			APlainCallStmCG plainCall = (APlainCallStmCG) stm;
			args = plainCall.getArgs();
			SourceNode source = plainCall.getSourceNode();
			if (source != null)
			{
				org.overture.ast.node.INode vdmNode = source.getVdmNode();

				if (vdmNode instanceof ACallStm)
				{
					ACallStm callStm = (ACallStm) vdmNode;
					if (callStm.getRootdef() instanceof SOperationDefinition)
					{
						SOperationDefinition op = (SOperationDefinition) callStm.getRootdef();

						if (op.getPredef() == null)
						{
							// The pre condition is "true"
							return null;
						}

						isOp = true;
					}
					else if(callStm.getRootdef() instanceof SFunctionDefinition)
					{
						SFunctionDefinition func = (SFunctionDefinition) callStm.getRootdef();
						
						if(func.getPredef() == null)
						{
							// The pre condition is true
							return null;
						}
					}
				} else
				{
					Logger.getLog().printErrorln("Expected VDM source node to be a call statement at this point in '"
							+ this.getClass().getSimpleName() + "' but got: " + vdmNode);
				}
			} else
			{
				Logger.getLog().printErrorln("Could not find VDM source node for the plain statement call '" + plainCall
						+ "' in '" + this.getClass().getSimpleName() + "'.");
			}

			plainCall.setName(pre + plainCall.getName());
			plainCall.setType(new ABoolBasicTypeCG());

			meetsPredMethod.setBody(plainCall);
		} else
		{
			Logger.getLog().printErrorln("Got unexpected statement type in '" + this.getClass().getSimpleName() + "': "
					+ stm);

			return null;
		}

		if (args != null)
		{
			if (isOp)
			{
				DeclAssistantCG dAssist = this.getInfo().getDeclAssistant();
				String invokedModule = getInvokedModule(stm);
				SClassDeclCG clazz = dAssist.findClass(getInfo().getClasses(), invokedModule);

				for (AFieldDeclCG f : clazz.getFields())
				{
					if (!f.getFinal())
					{
						// It's the state component
						if (traceEnclosingClass.equals(invokedModule))
						{
							ExpAssistantCG eAssist = getInfo().getExpAssistant();
							AIdentifierVarExpCG stateArg = eAssist.consIdVar(f.getName(), f.getType().clone());
							traceTrans.getCloneFreeNodes().add(stateArg);
							args.add(stateArg);
						} else
						{
							AExternalTypeCG traceNodeClassType = new AExternalTypeCG();
							traceNodeClassType.setName(traceTrans.getTracePrefixes().traceUtilClassName());

							AExplicitVarExpCG readStateMethod = new AExplicitVarExpCG();
							readStateMethod.setClassType(traceNodeClassType);
							readStateMethod.setIsLambda(false);
							readStateMethod.setIsLocal(false);
							readStateMethod.setName(traceTrans.getTracePrefixes().readStateMethodName());

							AMethodTypeCG readStateMethodType = new AMethodTypeCG();
							readStateMethodType.setResult(f.getType().clone());
							readStateMethodType.getParams().add(getTransAssist().consClassType(invokedModule));
							readStateMethodType.getParams().add(f.getType().clone());

							readStateMethod.setType(readStateMethodType);

							AApplyExpCG readStateCall = new AApplyExpCG();
							readStateCall.setRoot(readStateMethod);
							readStateCall.setType(f.getType().clone());

							ATypeArgExpCG moduleArg = new ATypeArgExpCG();
							moduleArg.setType(getTransAssist().consClassType(invokedModule));

							ATypeArgExpCG stateArg = new ATypeArgExpCG();
							stateArg.setType(f.getType().clone());

							readStateCall.getArgs().add(moduleArg);
							readStateCall.getArgs().add(stateArg);

							args.add(readStateCall);
						}

						break;
					}
				}
			}
		} else
		{
			Logger.getLog().printErrorln("Could not find args for " + stm + " in '" + this.getClass().getSimpleName()
					+ "'");
		}

		useStoreLookups(meetsPredMethod.getBody());

		return meetsPredMethod;
	}
	
	protected AMethodDeclCG initPredDecl(String name)
	{
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new ABoolBasicTypeCG());
		
		AMethodDeclCG meetsPredMethod = new AMethodDeclCG();
		meetsPredMethod.setImplicit(false);
		meetsPredMethod.setAbstract(false);
		meetsPredMethod.setAccess(IRConstants.PUBLIC);
		meetsPredMethod.setAsync(false);
		meetsPredMethod.setIsConstructor(false);
		meetsPredMethod.setMethodType(methodType);
		meetsPredMethod.setStatic(false);
		meetsPredMethod.setName(name);
		
		return meetsPredMethod;
	}

	protected boolean canBeGenerated()
	{
		// This only works for VDM-SL
		return Settings.dialect == Dialect.VDM_SL && getInfo().getSettings().generatePreConds();
	}
	
	protected String getInvokedModule(SStmCG stm)
	{
		if(stm instanceof APlainCallStmCG)
		{
			APlainCallStmCG call = (APlainCallStmCG) stm;
			
			STypeCG type = call.getClassType();
			
			if(type instanceof AClassTypeCG)
			{
				return ((AClassTypeCG) type).getName();
			}
		}
		
		return traceEnclosingClass;
	}

	protected SStmCG makeInstanceCall(SStmCG stm)
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
				call.getObj().apply(new CallObjTraceLocalizer(getTransAssist(), traceTrans.getTracePrefixes(), traceEnclosingClass));
			} catch (AnalysisException e)
			{
				Logger.getLog().printErrorln("Got unexpected problem when trying to apply "
						+ CallObjTraceLocalizer.class.getSimpleName() + " in '" + this.getClass().getSimpleName() + "'");
				e.printStackTrace();
			}
			
			/**
			 * We don't narrow the types of the arguments, which we should and which we do for the 'APlainCallStmCG'
			 * case using <code>castArgs(call);</code>. Code generation of traces does not really work for PP..
			 */
			
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

	protected SStmCG handleVoidValueReturn(SStmCG stm)
	{
		AExternalTypeCG traceNodeClassType = new AExternalTypeCG();
		traceNodeClassType.setName(traceTrans.getTracePrefixes().voidValueEnclosingClassName());

		AExplicitVarExpCG voidValue = new AExplicitVarExpCG();
		voidValue.setType(new AObjectTypeCG());
		voidValue.setClassType(traceNodeClassType);
		voidValue.setIsLambda(false);
		voidValue.setIsLocal(true);
		voidValue.setName(traceTrans.getTracePrefixes().voidValueFieldName());

		AReturnStmCG returnVoidVal = new AReturnStmCG();
		returnVoidVal.setExp(voidValue);

		ABlockStmCG block = new ABlockStmCG();

		block.getStatements().add(stm);
		block.getStatements().add(returnVoidVal);

		return block;
	}

	protected SStmCG handlePlainCallStm(APlainCallStmCG callStmCG)
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

		AClassTypeCG consClassType = getTransAssist().consClassType(traceEnclosingClass);

		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(consClassType);
		cast.setExp(getInfo().getExpAssistant().consIdVar(traceTrans.getTracePrefixes().callStmMethodParamName(), consClassType.clone()));

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

			String resultName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmResultNamePrefix());
			AVarDeclCG resultDecl = getTransAssist().consDecl(resultName, type.clone(), apply);

			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(getInfo().getExpAssistant().consIdVar(resultName, type.clone()));

			ABlockStmCG stms = new ABlockStmCG();
			stms.getLocalDefs().add(resultDecl);
			stms.getStatements().add(returnStm);

			return stms;

		}
	}

	protected ABlockStmCG consDecl(String classTypeName, String varName,
			SExpCG... args)
	{
		ATypeNameCG typeName = getTransAssist().consTypeNameForClass(classTypeName);

		AClassTypeCG classType = getTransAssist().consClassType(classTypeName);

		ANewExpCG newExp = new ANewExpCG();
		newExp.setName(typeName);
		newExp.setType(classType);

		for (SExpCG arg : args)
		{
			newExp.getArgs().add(arg);
		}

		return getTransAssist().wrap(getTransAssist().consDecl(varName, classType.clone(), newExp));
	}

	public ACallObjectExpStmCG consAddTraceVarCall(AIdentifierVarExpCG subject, AIdentifierVarExpCG t, boolean addFirst)
	{
		ANewExpCG newVar = new ANewExpCG();
		newVar.setName(getTransAssist().consTypeNameForClass(traceTrans.getTracePrefixes().traceVarClassName()));
		newVar.setType(getTransAssist().consClassType(traceTrans.getTracePrefixes().traceVarClassName()));
		
		newVar.getArgs().add(getInfo().getExpAssistant().consStringLiteral(t.getName(), false));
		newVar.getArgs().add(getInfo().getExpAssistant().consStringLiteral("" + getTransAssist().getInfo().getTypeAssistant().getVdmType(t.getType()), false));
		newVar.getArgs().add(traceTrans.getToStringBuilder().toStringOf(t.clone()));
		
		String add = addFirst ? traceTrans.getTracePrefixes().addVarFirstMethodName()
				: traceTrans.getTracePrefixes().addVarMethodName();
		
		return getTransAssist().consInstanceCallStm(subject.getType(), subject.getName(), add, newVar);
	}

	private TransAssistantCG getTransAssist()
	{
		return traceTrans.getTransAssist();
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
