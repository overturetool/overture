package org.overture.codegen.traces;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.assistant.DeclAssistantIR;
import org.overture.codegen.assistant.ExpAssistantIR;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STraceDeclIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.AnswerAdaptor;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AAnonymousClassExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ATypeArgExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ASkipStmIR;
import org.overture.codegen.ir.statements.SCallStmIR;
import org.overture.codegen.ir.traces.AApplyExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.ABracketedExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.AConcurrentExpTraceCoreDeclIR;
import org.overture.codegen.ir.traces.ALetBeStBindingTraceDeclIR;
import org.overture.codegen.ir.traces.ALetDefBindingTraceDeclIR;
import org.overture.codegen.ir.traces.ARepeatTraceDeclIR;
import org.overture.codegen.ir.traces.ATraceDeclTermIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantIR;
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
	public TraceNodeData caseATraceDeclTermIR(ATraceDeclTermIR node)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().altTraceNodeNamePrefix());
		AClassTypeIR classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().altTraceNodeNodeClassName());

		if (node.getTraceDecls().size() == 1)
		{
			return node.getTraceDecls().getFirst().apply(this);
		}
		{
			AVarDeclIR altTests = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

			ABlockStmIR stms = new ABlockStmIR();
			stms.getLocalDefs().add(altTests);

			List<SStmIR> addStms = new LinkedList<SStmIR>();

			for (STraceDeclIR traceDecl : node.getTraceDecls())
			{
				TraceNodeData nodeData = traceDecl.apply(this);

				stms.getStatements().add(nodeData.getStms());
				addStms.add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), nodeData.getNodeVar()));
			}
			
			stms.getStatements().addAll(addStms);

			return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms, stms);
		}
	}

	@Override
	public TraceNodeData caseAApplyExpTraceCoreDeclIR(
			AApplyExpTraceCoreDeclIR node) throws AnalysisException
	{
		List<AVarDeclIR> argDecls = replaceArgsWithVars(node.getCallStm());

		String classTypeName;
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			classTypeName = traceTrans.getTracePrefixes().callStmClassTypeName();
		}
		else 
		{
			classTypeName = traceTrans.getTracePrefixes().callStmBaseClassTypeName();
		}
		
		AClassTypeIR callStmType = getTransAssist().consClassType(classTypeName);
		String callStmName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmNamePrefix());
		AAnonymousClassExpIR callStmCreation = new AAnonymousClassExpIR();
		callStmCreation.setType(callStmType);
		
		AMethodDeclIR typeCheckMethod = consTypeCheckMethod(node.getCallStm().clone());
		
		if(typeCheckMethod != null)
		{
			callStmCreation.getMethods().add(typeCheckMethod);
		}
		
		AMethodDeclIR preCondMethod = condMeetsPreCondMethod(node.getCallStm().clone());
		
		if(preCondMethod != null)
		{
			callStmCreation.getMethods().add(preCondMethod);
		}

		callStmCreation.getMethods().add(consExecuteMethod(node.getCallStm().clone()));
		callStmCreation.getMethods().add(traceTrans.getToStringBuilder().consToString(getInfo(), node.getCallStm(), storeAssistant.getIdConstNameMap(), storeAssistant, getTransAssist()));
		AVarDeclIR callStmDecl = getTransAssist().consDecl(callStmName, callStmType.clone(), callStmCreation);

		AClassTypeIR stmTraceNodeType = getTransAssist().consClassType(traceTrans.getTracePrefixes().stmTraceNodeClassName());
		ANewExpIR newStmTraceNodeExp = getTransAssist().consDefaultConsCall(stmTraceNodeType);
		newStmTraceNodeExp.getArgs().add(getInfo().getExpAssistant().consIdVar(callStmName, callStmType.clone()));

		String stmNodeName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().stmTraceNodeNamePrefix());
		AVarDeclIR stmNodeDecl = getTransAssist().consDecl(stmNodeName, stmTraceNodeType.clone(), newStmTraceNodeExp);

		ABlockStmIR decls = new ABlockStmIR();
		
		if(!argDecls.isEmpty())
		{
			decls.getLocalDefs().addAll(argDecls);
		}
		
		decls.getLocalDefs().add(callStmDecl);
		decls.getLocalDefs().add(stmNodeDecl);

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(stmNodeName, stmTraceNodeType.clone()), decls, decls);
	}

	@Override
	public TraceNodeData caseABracketedExpTraceCoreDeclIR(
			ABracketedExpTraceCoreDeclIR node) throws AnalysisException
	{
		return buildFromDeclTerms(node.getTerms());
	}

	@Override
	public TraceNodeData caseAConcurrentExpTraceCoreDeclIR(
			AConcurrentExpTraceCoreDeclIR node) throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().concTraceNodeNamePrefix());

		AClassTypeIR classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().concTraceNodeNodeClassName());

		AVarDeclIR concNodeDecl = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		ABlockStmIR stms = new ABlockStmIR();
		stms.getLocalDefs().add(concNodeDecl);

		List<SStmIR> addStms = new LinkedList<SStmIR>();

		// The number of declarations is > 1
		for (STraceDeclIR term : node.getDecls())
		{
			TraceNodeData nodeData = term.apply(this);
			AIdentifierVarExpIR var = nodeData.getNodeVar();
			nodeData.getNodeVarScope().getStatements().add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), var));
			
			stms.getStatements().add(nodeData.getStms());
		}

		stms.getStatements().addAll(addStms);

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms, stms);
	}
	
	@Override
	public TraceNodeData caseALetBeStBindingTraceDeclIR(
			ALetBeStBindingTraceDeclIR node) throws AnalysisException
	{
		ASetMultipleBindIR bind = node.getBind();
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		idCollector.setTopNode(bind);
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			List<AIdentifierPatternIR> patterns = idCollector.findOccurences();

			for (AIdentifierPatternIR p : patterns)
			{
				String idConstName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().idConstNamePrefix());
				storeAssistant.getIdConstNameMap().put(((AIdentifierPatternIR) p).getName(), idConstName);
			}
		}
		
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().altTraceNodeNamePrefix());

		AClassTypeIR classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().altTraceNodeNodeClassName());

		AIdentifierPatternIR id = getInfo().getPatternAssistant().consIdPattern(name);

		AVarDeclIR altTests = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		STraceDeclIR body = node.getBody();
		SExpIR exp = node.getStExp();

		TraceNodeData bodyTraceData = body.apply(this);

		SSetTypeIR setType = getTransAssist().getSetTypeCloned(bind.getSet());
		TraceLetBeStStrategy strategy = new TraceLetBeStStrategy(getTransAssist(), exp, setType, traceTrans.getLangIterator(), 
				getInfo().getTempVarNameGen(), traceTrans.getIteVarPrefixes(), storeAssistant, storeAssistant.getIdConstNameMap(), traceTrans.getTracePrefixes(), id, altTests, bodyTraceData, this);

		if (getTransAssist().hasEmptySet(bind))
		{
			getTransAssist().cleanUpBinding(bind);
			
			ABlockStmIR skip = getTransAssist().wrap(new ASkipStmIR());
			return new TraceNodeData(null, skip, skip);
		}

		ABlockStmIR outerBlock = getTransAssist().consIterationBlock(node.getBind().getPatterns(), bind.getSet(), getInfo().getTempVarNameGen(), strategy, traceTrans.getIteVarPrefixes());

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), outerBlock, outerBlock);
	}
	
	@Override
	public TraceNodeData caseALetDefBindingTraceDeclIR(
			ALetDefBindingTraceDeclIR node) throws AnalysisException
	{
		ABlockStmIR outer = new ABlockStmIR();
		outer.setScoped(true);
		
		IdentifierPatternCollector idCollector = new IdentifierPatternCollector();
		
		ABlockStmIR declBlock = new ABlockStmIR();
		
		List<AIdentifierVarExpIR> traceVars = new LinkedList<>();
		
		for (AVarDeclIR dec : node.getLocalDefs())
		{
			// Find types for all sub patterns
			PatternTypeFinder typeFinder = new PatternTypeFinder(getInfo());
			dec.getPattern().apply(typeFinder, dec.getType());
			
			idCollector.setTopNode(dec);
			List<AIdentifierPatternIR> idOccurences = idCollector.findOccurences();
			
			AVarDeclIR decCopy = dec.clone();
			decCopy.setFinal(true);
			declBlock.getLocalDefs().add(decCopy);
			
			for(AIdentifierPatternIR occ : idOccurences)
			{
				if(Settings.dialect != Dialect.VDM_SL)
				{
					String idConstName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().idConstNamePrefix());
					storeAssistant.getIdConstNameMap().put(occ.getName(), idConstName);
					outer.getLocalDefs().add(storeAssistant.consIdConstDecl(idConstName));
					storeAssistant.appendStoreRegStms(declBlock, occ.getName(), idConstName, false);
				}
				
				traceVars.add(getInfo().getExpAssistant().consIdVar(occ.getName(), PatternTypeFinder.getType(typeFinder, occ)));
			}
		}
		
		TraceNodeData bodyNodeData = node.getBody().apply(this);

		for(int i = traceVars.size() - 1; i >= 0; i--)
		{
			AIdentifierVarExpIR a = traceVars.get(i);
			
			ACallObjectExpStmIR addVar = consAddTraceVarCall(bodyNodeData.getNodeVar(), a);
			ensureStoreLookups(addVar);
			bodyNodeData.getNodeVarScope().getStatements().add(addVar);
		}
		
		outer.getStatements().add(declBlock);
		outer.getStatements().add(bodyNodeData.getStms());
		
		return new TraceNodeData(bodyNodeData.getNodeVar(), outer, bodyNodeData.getNodeVarScope());
	}

	@Override
	public TraceNodeData caseARepeatTraceDeclIR(ARepeatTraceDeclIR node)
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

			AIdentifierVarExpIR varArg = traceData.getNodeVar();
			AIntLiteralExpIR fromArg = getInfo().getExpAssistant().consIntLiteral(from);
			AIntLiteralExpIR toArg = getInfo().getExpAssistant().consIntLiteral(to);

			AClassTypeIR repeat = getTransAssist().consClassType(traceTrans.getTracePrefixes().repeatTraceNodeNamePrefix());

			ABlockStmIR block = new ABlockStmIR();
			block.getStatements().add(traceData.getStms());
			block.getStatements().add(consDecl(traceTrans.getTracePrefixes().repeatTraceNodeNodeClassName(), name, varArg, fromArg, toArg));

			return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, repeat), block, block);
		}
	}

	/**
	 * Assumes dialect is VDM-SL. This method does not work with store lookups for local variables and since code
	 * generated VDM-SL traces do not rely on this then it is safe to use this method for this dialect.
	 * 
	 * @param callStm
	 *            the call statement for which we want to replace the arguments with variables
	 * @return the variable declarations corresponding to the variables that replace the arguments
	 */
	protected List<AVarDeclIR> replaceArgsWithVars(SStmIR callStm)
	{
		List<AVarDeclIR> decls = new LinkedList<AVarDeclIR>();
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			return decls;
		}
		
		List<SExpIR> args;
		
		if (callStm instanceof SCallStmIR)
		{
			args = ((SCallStmIR) callStm).getArgs();
		} else if (callStm instanceof ACallObjectExpStmIR)
		{
			args = ((ACallObjectExpStmIR) callStm).getArgs();
		} else
		{
			Logger.getLog().printErrorln("Expected a call statement or call object statement in '"
					+ this.getClass().getSimpleName() + "'. Got: " + callStm);
			return decls;
		}

		for (SExpIR arg : args)
		{
			String argName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmArgNamePrefix());
			STypeIR type = arg.getType();

			AVarDeclIR argDecl = getTransAssist().consDecl(argName, type.clone(), arg.clone());
			argDecl.setFinal(true);
			decls.add(argDecl);

			getTransAssist().replaceNodeWith(arg, getInfo().getExpAssistant().consIdVar(argName, type.clone()));
		}

		return decls;
	}
	
	protected AMethodDeclIR consExecuteMethod(SStmIR stm)
	{
		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new AObjectTypeIR());
		
		AMethodDeclIR execMethod = new AMethodDeclIR();
		execMethod.setImplicit(false);
		execMethod.setAbstract(false);
		execMethod.setAccess(IRConstants.PUBLIC);
		execMethod.setAsync(false);
		execMethod.setIsConstructor(false);
		execMethod.setMethodType(methodType);
		execMethod.setName(traceTrans.getTracePrefixes().callStmExecMethodNamePrefix());
		execMethod.setStatic(false);

		ABlockStmIR body = new ABlockStmIR();
		body.getStatements().add(makeInstanceCall(stm));
		
		ensureStoreLookups(body);
		
		execMethod.setBody(body);
		
		return execMethod;
	}

	protected void ensureStoreLookups(SStmIR body)
	{
		if(Settings.dialect == Dialect.VDM_SL)
		{
			return;
		}
		
		try
		{
			final Set<String> localVarNames = storeAssistant.getIdConstNameMap().keySet();
			
			body.apply(new DepthFirstAnalysisAdaptor()
			{
				// No need to consider explicit variables because they cannot be local
				
				@Override
				public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
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

	public TraceNodeData buildFromDeclTerms(List<ATraceDeclTermIR> terms)
			throws AnalysisException
	{
		String name = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().seqTraceNodeNamePrefix());

		AClassTypeIR classType = getTransAssist().consClassType(traceTrans.getTracePrefixes().seqClassTypeName());

		AVarDeclIR seqNodeDecl = getTransAssist().consDecl(name, classType, getTransAssist().consDefaultConsCall(classType));

		ABlockStmIR stms = new ABlockStmIR();
		stms.getLocalDefs().add(seqNodeDecl);

		for (ATraceDeclTermIR term : terms)
		{
			TraceNodeData nodeData = term.apply(this);
			stms.getStatements().add(nodeData.getStms());

			AIdentifierVarExpIR var = nodeData.getNodeVar();
			nodeData.getNodeVarScope().getStatements().add(getTransAssist().consInstanceCallStm(classType, name, traceTrans.getTracePrefixes().addMethodName(), var));
		}

		return new TraceNodeData(getInfo().getExpAssistant().consIdVar(name, classType.clone()), stms, stms);
	}
	
	public AMethodDeclIR consTypeCheckMethod(SStmIR stm)
	{
		return null;
	}
	
	public AMethodDeclIR condMeetsPreCondMethod(SStmIR stm)
	{
		if (!canBeGenerated())
		{
			return null;
		}

		AMethodDeclIR meetsPredMethod = initPredDecl(traceTrans.getTracePrefixes().callStmMeetsPreCondNamePrefix());

		boolean isOp = false;
		String pre = "pre_";
		List<SExpIR> args;

		if (stm instanceof APlainCallStmIR)
		{
			APlainCallStmIR plainCall = (APlainCallStmIR) stm;
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
			plainCall.setType(new ABoolBasicTypeIR());

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
				DeclAssistantIR dAssist = this.getInfo().getDeclAssistant();
				String invokedModule = getInvokedModule(stm);
				SClassDeclIR clazz = dAssist.findClass(getInfo().getClasses(), invokedModule);

				for (AFieldDeclIR f : clazz.getFields())
				{
					if (!f.getFinal())
					{
						// It's the state component
						if (traceEnclosingClass.equals(invokedModule))
						{
							ExpAssistantIR eAssist = getInfo().getExpAssistant();
							AIdentifierVarExpIR stateArg = eAssist.consIdVar(f.getName(), f.getType().clone());
							traceTrans.getCloneFreeNodes().add(stateArg);
							args.add(stateArg);
						} else
						{
							AExternalTypeIR traceNodeClassType = new AExternalTypeIR();
							traceNodeClassType.setName(traceTrans.getTracePrefixes().traceUtilClassName());

							AExplicitVarExpIR readStateMethod = new AExplicitVarExpIR();
							readStateMethod.setClassType(traceNodeClassType);
							readStateMethod.setIsLambda(false);
							readStateMethod.setIsLocal(false);
							readStateMethod.setName(traceTrans.getTracePrefixes().readStateMethodName());

							AMethodTypeIR readStateMethodType = new AMethodTypeIR();
							readStateMethodType.setResult(f.getType().clone());
							readStateMethodType.getParams().add(getTransAssist().consClassType(invokedModule));
							readStateMethodType.getParams().add(f.getType().clone());

							readStateMethod.setType(readStateMethodType);

							AApplyExpIR readStateCall = new AApplyExpIR();
							readStateCall.setRoot(readStateMethod);
							readStateCall.setType(f.getType().clone());

							ATypeArgExpIR moduleArg = new ATypeArgExpIR();
							moduleArg.setType(getTransAssist().consClassType(invokedModule));

							ATypeArgExpIR stateArg = new ATypeArgExpIR();
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

		ensureStoreLookups(meetsPredMethod.getBody());

		return meetsPredMethod;
	}
	
	protected AMethodDeclIR initPredDecl(String name)
	{
		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new ABoolBasicTypeIR());
		
		AMethodDeclIR meetsPredMethod = new AMethodDeclIR();
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
	
	protected String getInvokedModule(SStmIR stm)
	{
		if(stm instanceof APlainCallStmIR)
		{
			APlainCallStmIR call = (APlainCallStmIR) stm;
			
			STypeIR type = call.getClassType();
			
			if(type instanceof AClassTypeIR)
			{
				return ((AClassTypeIR) type).getName();
			}
		}
		
		return traceEnclosingClass;
	}

	protected SStmIR makeInstanceCall(SStmIR stm)
	{
		if(stm instanceof ACallObjectExpStmIR)
		{
			// Assume the class enclosing the trace to be S
			// self.op(42) becomes ((S)instance).op(42L)
			// a.op(42) remains a.op(42L) if a is local
			// a.op(42) becomes ((S)instance).a.op(42L) if a is an instance variable
			
			ACallObjectExpStmIR call = (ACallObjectExpStmIR) stm;
			
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
			 * We don't narrow the types of the arguments, which we should and which we do for the 'APlainCallStmIR'
			 * case using <code>castArgs(call);</code>. Code generation of traces does not really work for PP..
			 */
			
			if(call.getType() instanceof AVoidTypeIR)
			{
				return handleVoidValueReturn(call);
			}
			
			return stm;
		}
	    else if (stm instanceof APlainCallStmIR)
		{
			// Assume the class enclosing the trace to be S
			// Example: op(42) becomes ((S)instance).op(42L)
			try
			{
				return handlePlainCallStm((APlainCallStmIR) stm);
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

	protected SStmIR handleVoidValueReturn(SStmIR stm)
	{
		AExternalTypeIR traceNodeClassType = new AExternalTypeIR();
		traceNodeClassType.setName(traceTrans.getTracePrefixes().voidValueEnclosingClassName());

		AExplicitVarExpIR voidValue = new AExplicitVarExpIR();
		voidValue.setType(new AObjectTypeIR());
		voidValue.setClassType(traceNodeClassType);
		voidValue.setIsLambda(false);
		voidValue.setIsLocal(true);
		voidValue.setName(traceTrans.getTracePrefixes().voidValueFieldName());

		AReturnStmIR returnVoidVal = new AReturnStmIR();
		returnVoidVal.setExp(voidValue);

		ABlockStmIR block = new ABlockStmIR();

		block.getStatements().add(stm);
		block.getStatements().add(returnVoidVal);

		return block;
	}

	protected SStmIR handlePlainCallStm(APlainCallStmIR callStmIR)
			throws AnalysisException
	{
		STypeIR type = callStmIR.getType();
		
		if (callStmIR.getIsStatic())
		{
			if (type instanceof AVoidTypeIR)
			{
				return handleVoidValueReturn(callStmIR);
			} else
			{
				return callStmIR;
			}
		}

		List<SExpIR> args = callStmIR.getArgs();
		STypeIR classType = callStmIR.getClassType();
		String name = callStmIR.getName();
		
		SourceNode sourceNode = callStmIR.getSourceNode();

		AClassTypeIR consClassType = getTransAssist().consClassType(traceEnclosingClass);

		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setType(consClassType);
		cast.setExp(getInfo().getExpAssistant().consIdVar(traceTrans.getTracePrefixes().callStmMethodParamName(), consClassType.clone()));

		if (type instanceof AVoidTypeIR)
		{
			ACallObjectExpStmIR paramExp = new ACallObjectExpStmIR();
			paramExp.setObj(cast);

			paramExp.setFieldName(name);
			paramExp.setType(type.clone());

			for (SExpIR arg : args)
			{
				paramExp.getArgs().add(arg.clone());
			}

			paramExp.setSourceNode(sourceNode);

			return handleVoidValueReturn(paramExp);
		} else
		{
			AFieldExpIR field = new AFieldExpIR();
			String fieldModule = classType instanceof AClassTypeIR ? ((AClassTypeIR) classType).getName()
					: traceEnclosingClass;
			field.setType(getInfo().getTypeAssistant().getMethodType(getInfo(), fieldModule, name, args));
			field.setMemberName(name);
			field.setObject(cast);

			AApplyExpIR apply = new AApplyExpIR();
			apply.setType(type.clone());
			apply.setRoot(field);
			apply.setSourceNode(callStmIR.getSourceNode());

			for (SExpIR arg : args)
			{
				apply.getArgs().add(arg.clone());
			}

			String resultName = getInfo().getTempVarNameGen().nextVarName(traceTrans.getTracePrefixes().callStmResultNamePrefix());
			AVarDeclIR resultDecl = getTransAssist().consDecl(resultName, type.clone(), apply);

			AReturnStmIR returnStm = new AReturnStmIR();
			returnStm.setExp(getInfo().getExpAssistant().consIdVar(resultName, type.clone()));

			ABlockStmIR stms = new ABlockStmIR();
			stms.getLocalDefs().add(resultDecl);
			stms.getStatements().add(returnStm);

			return stms;

		}
	}

	protected ABlockStmIR consDecl(String classTypeName, String varName,
			SExpIR... args)
	{
		ATypeNameIR typeName = getTransAssist().consTypeNameForClass(classTypeName);

		AClassTypeIR classType = getTransAssist().consClassType(classTypeName);

		ANewExpIR newExp = new ANewExpIR();
		newExp.setName(typeName);
		newExp.setType(classType);

		for (SExpIR arg : args)
		{
			newExp.getArgs().add(arg);
		}

		return getTransAssist().wrap(getTransAssist().consDecl(varName, classType.clone(), newExp));
	}

	public ACallObjectExpStmIR consAddTraceVarCall(AIdentifierVarExpIR subject, AIdentifierVarExpIR t)
	{
		ANewExpIR newVar = new ANewExpIR();
		newVar.setName(getTransAssist().consTypeNameForClass(traceTrans.getTracePrefixes().traceVarClassName()));
		newVar.setType(getTransAssist().consClassType(traceTrans.getTracePrefixes().traceVarClassName()));
		
		newVar.getArgs().add(getInfo().getExpAssistant().consStringLiteral(t.getName(), false));
		newVar.getArgs().add(getInfo().getExpAssistant().consStringLiteral("" + getTransAssist().getInfo().getTypeAssistant().getVdmType(t.getType()), false));
		newVar.getArgs().add(traceTrans.getToStringBuilder().toStringOf(t.clone()));
		
		return getTransAssist().consInstanceCallStm(subject.getType(), subject.getName(), traceTrans.getTracePrefixes().addVarFirstMethodName(), newVar);
	}

	private TransAssistantIR getTransAssist()
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
