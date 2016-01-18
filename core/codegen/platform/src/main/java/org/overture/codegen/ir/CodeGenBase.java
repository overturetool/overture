package org.overture.codegen.ir;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.codegen.analysis.vdm.UnreachableStmRemover;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.OldNameRenamer;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedModule;


abstract public class CodeGenBase implements IREventCoordinator
{
	protected IRGenerator generator;
	protected TransAssistantCG transAssistant;
	private IREventObserver irObserver;
	
	protected CodeGenBase()
	{
		super();
		initVelocity();
		this.irObserver = null;
		this.generator = new IRGenerator();
	}
	
	public void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}
	
	public String formatCode(StringWriter writer)
	{
		// Do nothing by default
		return writer.toString();
	}
	
	public boolean isTestCase(IRStatus<? extends PCG> status)
	{
		return getInfo().getDeclAssistant().isLibrary(status.getIrNode().getSourceNode().getVdmNode());
	}
	
	protected List<INode> getUserModules(
			List<? extends INode> mergedParseLists)
	{
		List<INode> userModules = new LinkedList<INode>();
		
		if(mergedParseLists.size() == 1 && mergedParseLists.get(0) instanceof CombinedDefaultModule)
		{
			CombinedDefaultModule combined = (CombinedDefaultModule) mergedParseLists.get(0);
			
			for(AModuleModules m : combined.getModules())
			{
				userModules.add(m);
			}
			
			return userModules;
		}
		else
		{
			for (INode node : mergedParseLists)
			{
				if(!getInfo().getDeclAssistant().isLibrary(node))
				{
					userModules.add(node);
				}
			}
			
			return userModules;
		}
	}
	
	public void removeUnreachableStms(List<? extends INode> mergedParseLists)
			throws AnalysisException
	{
		UnreachableStmRemover remover = new UnreachableStmRemover();

		for (INode node : mergedParseLists)
		{
			node.apply(remover);
		}
	}
	
	
	public void simplifyLibrary(INode node)
	{
		List<PDefinition> defs = null;
		
		if(node instanceof SClassDefinition)
		{
			defs = ((SClassDefinition) node).getDefinitions();
		}
		else if(node instanceof AModuleModules)
		{
			defs = ((AModuleModules) node).getDefs();
		}
		else
		{
			// Nothing to do
			return;
		}
		
		for (PDefinition def : defs)
		{
			if (def instanceof SOperationDefinition)
			{
				SOperationDefinition op = (SOperationDefinition) def;

				op.setBody(new ANotYetSpecifiedStm());
				op.setPrecondition(null);
				op.setPostcondition(null);
			} else if (def instanceof SFunctionDefinition)
			{
				SFunctionDefinition func = (SFunctionDefinition) def;

				func.setBody(new ANotYetSpecifiedExp());
				func.setPrecondition(null);
				func.setPostcondition(null);
			}
		}
	}
	
	public void handleOldNames(List<? extends INode> ast) throws AnalysisException
	{
		OldNameRenamer oldNameRenamer = new OldNameRenamer();
		
		for(INode module : ast)
		{
			module.apply(oldNameRenamer);
		}
	}
	
	public void preProcessAst(List<? extends INode> ast) throws AnalysisException
	{
		generator.computeDefTable(getUserModules(ast));
		removeUnreachableStms(ast);
		handleOldNames(ast);
		
		for (INode node : ast)
		{
			if (getInfo().getAssistantManager().getDeclAssistant().isLibrary(node))
			{
				simplifyLibrary(node);
			}
			else
			{
				preProcessUserClass(node);
			}
		}
	}
	
	public List<String> getUserTestCases(List<? extends INode> ast)
	{
		List<String> userTestCases = new LinkedList<>();
		
		for (INode node : ast)
		{
			if(getInfo().getDeclAssistant().isTestCase(node))
			{
				userTestCases.add(getInfo().getDeclAssistant().getNodeName(node));
			}
		}
		
		return userTestCases;
	}
	
	public void preProcessUserClass(INode node)
	{
		// Do nothing by default
	}
	
	public boolean shouldGenerateVdmNode(INode node)
	{
		DeclAssistantCG declAssistant = getInfo().getDeclAssistant();
		
		if (declAssistant.isLibrary(node))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public GeneratedModule genIrModule(MergeVisitor mergeVisitor,
			IRStatus<? extends PCG> status) throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if (status.canBeGenerated())
		{
			StringWriter writer = new StringWriter();
			status.getIrNode().apply(mergeVisitor, writer);

			boolean isTestCase = isTestCase(status);

			GeneratedModule generatedModule;
			if (mergeVisitor.hasMergeErrors())
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), mergeVisitor.getMergeErrors(), isTestCase);
			} else if (mergeVisitor.hasUnsupportedTargLangNodes())
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), new HashSet<VdmNodeInfo>(), mergeVisitor.getUnsupportedInTargLang(), isTestCase);
			} else
			{
				generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), formatCode(writer), isTestCase);
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
			}

			return generatedModule;
		} else
		{
			return new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status));
		}
	}
	
	public Generated genIrExp(IRStatus<SExpCG> expStatus, MergeVisitor mergeVisitor)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		StringWriter writer = new StringWriter();
		SExpCG expCg = expStatus.getIrNode();

		if (expStatus.canBeGenerated())
		{
			mergeVisitor.init();
			
			expCg.apply(mergeVisitor, writer);

			if (mergeVisitor.hasMergeErrors())
			{
				return new Generated(mergeVisitor.getMergeErrors());
			} else if (mergeVisitor.hasUnsupportedTargLangNodes())
			{
				return new Generated(new HashSet<VdmNodeInfo>(), mergeVisitor.getUnsupportedInTargLang());
			} else
			{
				String code = writer.toString();

				return new Generated(code);
			}
		} else
		{

			return new Generated(expStatus.getUnsupportedInIr(), new HashSet<IrNodeInfo>());
		}
	}
	
	@Override
	public void registerIrObs(IREventObserver obs)
	{
		if(obs != null && irObserver == null)
		{
			irObserver = obs;
		}
	}

	@Override
	public void unregisterIrObs(IREventObserver obs)
	{
		if(obs != null && irObserver == obs)
		{
			irObserver = null;
		}
	}
	
	public List<IRStatus<org.overture.codegen.cgast.INode>> initialIrEvent(List<IRStatus<org.overture.codegen.cgast.INode>> ast)
	{
		if(irObserver != null)
		{
			return irObserver.initialIRConstructed(ast, getInfo());
		}
		
		return ast;
	}
	
	public List<IRStatus<org.overture.codegen.cgast.INode>> finalIrEvent(List<IRStatus<org.overture.codegen.cgast.INode>> ast)
	{
		if(irObserver != null)
		{
			return irObserver.finalIRConstructed(ast, getInfo());
		}
		
		return ast;
	}
	
	public void setIRGenerator(IRGenerator generator)
	{
		this.generator = generator;
	}

	public IRGenerator getIRGenerator()
	{
		return generator;
	}

	public void setSettings(IRSettings settings)
	{
		generator.getIRInfo().setSettings(settings);
	}
	
	public IRSettings getSettings()
	{
		return generator.getIRInfo().getSettings();
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}
	
	public void setTransAssistant(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	public TransAssistantCG getTransAssistant()
	{
		return transAssistant;
	}
}