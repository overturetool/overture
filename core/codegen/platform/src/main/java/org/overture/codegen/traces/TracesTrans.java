package org.overture.codegen.traces;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ATypeArgExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class TracesTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private IterationVarPrefixes iteVarPrefixes;
	private ILanguageIterator langIterator;
	private ICallStmToStringMethodBuilder toStringBuilder;
	private TraceNames tracePrefixes;

	public TracesTrans(TransAssistantCG transAssistant,
			IterationVarPrefixes iteVarPrefixes, TraceNames tracePrefixes,
			ILanguageIterator langIterator,
			ICallStmToStringMethodBuilder toStringBuilder)
	{
		this.transAssistant = transAssistant;
		this.iteVarPrefixes = iteVarPrefixes;
		this.langIterator = langIterator;
		this.toStringBuilder = toStringBuilder;

		this.tracePrefixes = tracePrefixes;
	}

	@Override
	public void caseANamedTraceDeclCG(ANamedTraceDeclCG node)
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
		instanceParam.setPattern(transAssistant.getInfo().getPatternAssistant().consIdPattern(tracePrefixes.traceMethodParamName()));

		AMethodDeclCG traceMethod = new AMethodDeclCG();
		
		traceMethod.getFormalParams().add(instanceParam);
		
		traceMethod.setImplicit(false);
		traceMethod.setAbstract(false);
		traceMethod.setAccess(IRConstants.PUBLIC);
		traceMethod.setBody(consTraceMethodBody(node));
		traceMethod.setIsConstructor(false);
		traceMethod.setStatic(false);
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
		executeTestsCall.getArgs().add(typeArg);
		executeTestsCall.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.traceMethodParamName(),
				transAssistant.consClassType(tracePrefixes.testAccumulatorClassName())));
		executeTestsCall.getArgs().add(transAssistant.getInfo().getExpAssistant().consIdVar(tracePrefixes.storeVarName(),
				transAssistant.consClassType(tracePrefixes.storeClassName())));
		

		return executeTestsCall;
	}
	
	private SStmCG consTraceMethodBody(ANamedTraceDeclCG node)
			throws AnalysisException
	{
		String traceEnclosingClass = getTraceEnclosingClass(node);
		TraceStmsBuilder stmBuilder = new TraceStmsBuilder(transAssistant.getInfo(), transAssistant.getInfo().getClasses(), transAssistant, 
				iteVarPrefixes, tracePrefixes, langIterator, toStringBuilder, traceEnclosingClass);

		TraceNodeData nodeData = stmBuilder.buildFromDeclTerms(node.getTerms());

		ABlockStmCG stms = new ABlockStmCG();
		stms.getLocalDefs().add(transAssistant.consClassVarDeclDefaultCtor(tracePrefixes.storeClassName(), tracePrefixes.storeVarName()));
		stms.getLocalDefs().add(transAssistant.consClassVarDeclDefaultCtor(tracePrefixes.idGeneratorClassName(), tracePrefixes.idGeneratorVarName()));
		stms.getStatements().add(nodeData.getStms());
		stms.getStatements().add(buildTestExecutionStms(nodeData.getNodeVar(), getClassName(node)));

		return stms;
	}
	
	private String getTraceEnclosingClass(ANamedTraceDeclCG trace)
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
				+ trace + " in TraceStmsBuilder");

		return null;
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

	private String getClassName(ANamedTraceDeclCG node)
	{
		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

		String traceClassName = "";
		if (enclosingClass != null)
		{
			traceClassName = enclosingClass.getName();
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing class for "
					+ node);
			traceClassName = "Unknown";
		}

		return traceClassName;
	}
}
