package org.overture.codegen.traces;

import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ANamedTraceDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ATypeArgExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.config.Settings;

public class TracesTrans extends DepthFirstAnalysisAdaptor
{
	protected TransAssistantIR transAssistant;
	protected IterationVarPrefixes iteVarPrefixes;
	protected ILanguageIterator langIterator;
	protected ICallStmToStringMethodBuilder toStringBuilder;
	protected TraceNames tracePrefixes;
	private List<INode> cloneFreeNodes;

	public TracesTrans(TransAssistantIR transAssistant,
			IterationVarPrefixes iteVarPrefixes, TraceNames tracePrefixes,
			ILanguageIterator langIterator,
			ICallStmToStringMethodBuilder toStringBuilder, List<INode> cloneFreeNodes)
	{
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;
		this.langIterator = langIterator;
		this.toStringBuilder = toStringBuilder;

		this.tracePrefixes = tracePrefixes;
		this.cloneFreeNodes = cloneFreeNodes;
	}

	@Override
	public void caseANamedTraceDeclIR(ANamedTraceDeclIR node)
			throws AnalysisException
	{
		if(!transAssistant.getInfo().getSettings().generateTraces())
		{
			return;
		}
		
		TraceSupportedAnalysis supportedAnalysis = new TraceSupportedAnalysis(node);
		if (!traceIsSupported(supportedAnalysis))
		{
			transAssistant.getInfo().addTransformationWarning(node, supportedAnalysis.getReason());
			return;
		}

		ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);

		if (enclosingClass != null)
		{
			enclosingClass.getMethods().add(consTraceMethod(node));
		} else
		{
			Logger.getLog().printErrorln("Class enclosing trace could not be found so the "
					+ "generated trace could not be added as a method to the corresponding class");
		}
	}

	private boolean traceIsSupported(TraceSupportedAnalysis supportedAnalysis)
	{
		try
		{
			supportedAnalysis.run();
		} catch (AnalysisException e)
		{
			Logger.getLog().printErrorln("Could not determine if a trace could be code generated");
			e.printStackTrace();
			return false;
		}

		return !supportedAnalysis.isUnsupported();
	}

	private AMethodDeclIR consTraceMethod(ANamedTraceDeclIR node)
			throws AnalysisException
	{
		AClassTypeIR testAccType = transAssistant.consClassType(tracePrefixes.testAccumulatorClassName());
		
		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(new AVoidTypeIR());
		methodType.getParams().add(testAccType);
		
		AFormalParamLocalParamIR instanceParam = new AFormalParamLocalParamIR();
		instanceParam.setType(testAccType.clone());
		instanceParam.setPattern(transAssistant.getInfo().getPatternAssistant().consIdPattern(tracePrefixes.traceMethodParamName()));

		AMethodDeclIR traceMethod = new AMethodDeclIR();
		traceMethod.setTag(new TraceMethodTag());
		
		traceMethod.getFormalParams().add(instanceParam);
		
		traceMethod.setImplicit(false);
		traceMethod.setAbstract(false);
		traceMethod.setAccess(IRConstants.PUBLIC);
		traceMethod.setBody(consTraceMethodBody(node));
		traceMethod.setIsConstructor(false);
		traceMethod.setStatic(Settings.dialect == Dialect.VDM_SL);
		traceMethod.setMethodType(methodType);
		traceMethod.setName(getTraceName(node) + "_"
				+ tracePrefixes.runTraceMethodName());

		return traceMethod;
	}

	private SStmIR buildTestExecutionStms(AIdentifierVarExpIR nodeVar,
			String traceEnclosingClassName)
	{
		AExternalTypeIR utilsType = new AExternalTypeIR();
		utilsType.setName(tracePrefixes.traceNodeNodeClassName());

		APlainCallStmIR executeTestsCall = new APlainCallStmIR();
		executeTestsCall.setClassType(utilsType);
		executeTestsCall.setName(tracePrefixes.executeTestsMethodName());
		executeTestsCall.setType(new AVoidTypeIR());

		ATypeArgExpIR typeArg = new ATypeArgExpIR();
		typeArg.setType(transAssistant.consClassType(traceEnclosingClassName));

		executeTestsCall.getArgs().add(nodeVar.clone());
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			executeTestsCall.getArgs().add(typeArg);
		}
		
		executeTestsCall.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.traceMethodParamName(),
				transAssistant.consClassType(tracePrefixes.testAccumulatorClassName())));
		executeTestsCall.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.storeVarName(),
				transAssistant.consClassType(tracePrefixes.storeClassName())));
		

		return executeTestsCall;
	}
	
	private SStmIR consTraceMethodBody(ANamedTraceDeclIR node)
			throws AnalysisException
	{
		StoreAssistant storeAssist = new StoreAssistant(tracePrefixes, transAssistant);
		
		String traceEnclosingClass = getClassName(node);
		
		TraceStmBuilder stmBuilder = consStmBuilder(storeAssist, traceEnclosingClass);

		SStmIR regModules = registerOtherModules(traceEnclosingClass, storeAssist);
		TraceNodeData nodeData = stmBuilder.buildFromDeclTerms(node.getTerms());

		ABlockStmIR stms = new ABlockStmIR();
		stms.getLocalDefs().add(transAssistant.consClassVarDeclDefaultCtor(tracePrefixes.storeClassName(), tracePrefixes.storeVarName()));
		stms.getLocalDefs().add(transAssistant.consClassVarDeclDefaultCtor(tracePrefixes.idGeneratorClassName(), tracePrefixes.idGeneratorVarName()));
		
		if(regModules != null)
		{
			stms.getStatements().add(regModules);
		}
		
		stms.getStatements().add(nodeData.getStms());
		stms.getStatements().add(buildTestExecutionStms(nodeData.getNodeVar(), getClassName(node)));

		return stms;
	}

	public TraceStmBuilder consStmBuilder(StoreAssistant storeAssist, String traceEnclosingClass)
	{
		return new TraceStmBuilder(this, traceEnclosingClass, storeAssist);
	}
	
	private SStmIR registerOtherModules(String encClass, StoreAssistant storeAssist)
	{
		// Static registration of other modules
		if(Settings.dialect == Dialect.VDM_PP && transAssistant.getInfo().getClasses().size() == 1)
		{
			return null;
		}
		
		ABlockStmIR regBlock = new ABlockStmIR();
		regBlock.setScoped(true);
		
		for(SClassDeclIR c : transAssistant.getInfo().getClasses())
		{
			if(Settings.dialect == Dialect.VDM_PP && c.getName().equals(encClass))
			{
				/**
				 * There is no need to register the instance of the enclosing class. This is handled by the
				 * TraceNode.executeTests method
				 */
				continue;
			}
			
			String idConstName = transAssistant.getInfo().getTempVarNameGen().nextVarName(tracePrefixes.idConstNamePrefix());
			regBlock.getLocalDefs().add(storeAssist.consIdConstDecl(idConstName));
			
			AClassTypeIR classType = transAssistant.consClassType(c.getName());
			
			ATypeArgExpIR classArg = new ATypeArgExpIR();
			classArg.setType(classType.clone());
			
			regBlock.getStatements().add(storeAssist.consStoreRegistration(idConstName, classArg, true));
		}
		
		return regBlock;
	}

	private String getTraceName(ANamedTraceDeclIR node)
	{
		String methodName = getClassName(node);

		for (int i = 0; i < node.getPathname().size(); i++)
		{
			methodName += "_" + node.getPathname().get(i).getName();
		}

		return methodName;
	}
	
	public String getClassName(ANamedTraceDeclIR trace)
	{
		if (trace != null)
		{
			ADefaultClassDeclIR enclosingClass = trace.getAncestor(ADefaultClassDeclIR.class);
			if (enclosingClass != null)
			{
				return enclosingClass.getName();
			}
		}

		Logger.getLog().printErrorln("Could not find class declaration enclosing the trace node "
				+ trace + " in 'TraceStmsBuilder'");

		return null;
	}

	public TransAssistantIR getTransAssist()
	{
		return transAssistant;
	}
	
	public IterationVarPrefixes getIteVarPrefixes()
	{
		return iteVarPrefixes;
	}
	
	public TraceNames getTracePrefixes()
	{
		return tracePrefixes;
	}
	
	public ILanguageIterator getLangIterator()
	{
		return langIterator;
	}
	
	public ICallStmToStringMethodBuilder getToStringBuilder()
	{
		return toStringBuilder;
	}
	
	public List<INode> getCloneFreeNodes()
	{
		return cloneFreeNodes;
	}
}
