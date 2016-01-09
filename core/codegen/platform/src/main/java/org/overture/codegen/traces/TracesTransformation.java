package org.overture.codegen.traces;

import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ATypeArgExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.config.Settings;

public class TracesTransformation extends DepthFirstAnalysisAdaptor
{
	protected TransAssistantCG transAssistant;
	protected IterationVarPrefixes iteVarPrefixes;
	protected ILanguageIterator langIterator;
	protected ICallStmToStringMethodBuilder toStringBuilder;
	protected TraceNames tracePrefixes;
	private List<INode> cloneFreeNodes;

	public TracesTrans(TransAssistantCG transAssistant,
			IterationVarPrefixes iteVarPrefixes, TraceNames tracePrefixes,
			ILanguageIterator langIterator,
			ICallStmToStringMethodBuilder toStringBuilder, List<INode> cloneFreeNodes)
	{
		this.irInfo = irInfo;
		this.classes = classes;
		this.transAssistant = transAssistant;
		this.tempVarPrefixes = tempVarPrefixes;
		this.langIterator = langIterator;
		this.toStringBuilder = toStringBuilder;

		this.tracePrefixes = tracePrefixes;
		this.cloneFreeNodes = cloneFreeNodes;
	}

	@Override
	public void caseANamedTraceDeclCG(ANamedTraceDeclCG node)
			throws AnalysisException
	{
		if(!irInfo.getSettings().generateTraces())
		{
			return;
		}
		
		TraceSupportedAnalysis supportedAnalysis = new TraceSupportedAnalysis(node);
		if (!traceIsSupported(supportedAnalysis))
		{
			irInfo.addTransformationWarning(node, supportedAnalysis.getReason());
			return;
		}

		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

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

	private AMethodDeclCG consTraceMethod(ANamedTraceDeclCG node)
			throws AnalysisException
	{
		AClassTypeCG testAccType = transAssistant.consClassType(tracePrefixes.testAccumulatorClassName());
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(new AVoidTypeCG());
		methodType.getParams().add(testAccType);
		
		AFormalParamLocalParamCG instanceParam = new AFormalParamLocalParamCG();
		instanceParam.setType(testAccType.clone());
		instanceParam.setPattern(transAssistant.consIdPattern(tracePrefixes.traceMethodParamName()));

		AMethodDeclCG traceMethod = new AMethodDeclCG();
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

	private SStmCG buildTestExecutionStms(AIdentifierVarExpCG nodeVar,
			String traceEnclosingClassName)
	{
		AExternalTypeCG utilsType = new AExternalTypeCG();
		utilsType.setName(tracePrefixes.traceNodeNodeClassName());

		APlainCallStmCG executeTestsCall = new APlainCallStmCG();
		executeTestsCall.setClassType(utilsType);
		executeTestsCall.setName(tracePrefixes.executeTestsMethodName());
		executeTestsCall.setType(new AVoidTypeCG());

		ATypeArgExpCG typeArg = new ATypeArgExpCG();
		typeArg.setType(transAssistant.consClassType(traceEnclosingClassName));

		executeTestsCall.getArgs().add(nodeVar.clone());
		
		if(Settings.dialect != Dialect.VDM_SL)
		{
			executeTestsCall.getArgs().add(typeArg);
		}
		
		executeTestsCall.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.traceMethodParamName(),
				transAssistant.consClassType(tracePrefixes.testAccumulatorClassName())));
		executeTestsCall.getArgs().add(transAssistant.consIdentifierVar(tracePrefixes.storeVarName(),
				transAssistant.consClassType(tracePrefixes.storeClassName())));
		

		return executeTestsCall;
	}
	
	private SStmCG consTraceMethodBody(ANamedTraceDeclCG node)
			throws AnalysisException
	{
		StoreAssistant storeAssist = new StoreAssistant(tracePrefixes, transAssistant);
		
		String traceEnclosingClass = getClassName(node);
		
		TraceStmBuilder stmBuilder = consStmBuilder(storeAssist, traceEnclosingClass);

		SStmCG regModules = registerOtherModules(traceEnclosingClass, storeAssist);
		TraceNodeData nodeData = stmBuilder.buildFromDeclTerms(node.getTerms());

		ABlockStmCG stms = new ABlockStmCG();
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
	
	private SStmCG registerOtherModules(String encClass, StoreAssistant storeAssist)
	{
		// Static registration of other modules
		if(Settings.dialect == Dialect.VDM_PP && transAssistant.getInfo().getClasses().size() == 1)
		{
			return null;
		}
		
		ABlockStmCG regBlock = new ABlockStmCG();
		regBlock.setScoped(true);
		
		for(SClassDeclCG c : transAssistant.getInfo().getClasses())
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
			
			AClassTypeCG classType = transAssistant.consClassType(c.getName());
			
			ATypeArgExpCG classArg = new ATypeArgExpCG();
			classArg.setType(classType.clone());
			
			regBlock.getStatements().add(storeAssist.consStoreRegistration(idConstName, classArg, true));
		}
		
		return regBlock;
	}

	private String getTraceName(ANamedTraceDeclCG node)
	{
		String methodName = getClassName(node);

		for (int i = 0; i < node.getPathname().size(); i++)
		{
			methodName += "_" + node.getPathname().get(i).getName();
		}

		return methodName;
	}
	
	public String getClassName(ANamedTraceDeclCG trace)
	{
		if (trace != null)
		{
			ADefaultClassDeclCG enclosingClass = trace.getAncestor(ADefaultClassDeclCG.class);
			if (enclosingClass != null)
			{
				return enclosingClass.getName();
			}
		}

		Logger.getLog().printErrorln("Could not find class declaration enclosing the trace node "
				+ trace + " in 'TraceStmsBuilder'");

		return null;
	}

	public TransAssistantCG getTransAssist()
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
