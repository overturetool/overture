package org.overture.codegen.ir;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

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
import org.overture.codegen.trans.assistants.TransAssistantCG;


abstract public class CodeGenBase implements IREventCoordinator
{
	protected IRGenerator generator;
	protected TransAssistantCG transAssistant;
	private IREventObserver irObserver;
	
	protected CodeGenBase()
	{
		super();
		this.irObserver = null;
		this.generator = new IRGenerator();
	}
	
	public String formatCode(StringWriter writer)
	{
		// Do nothing by default
		return writer.toString();
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
	
	public List<String> preProcessAst(List<? extends INode> ast)
	{
		List<String> userTestCases = new LinkedList<>();
		
		for (INode node : ast)
		{
			if (getInfo().getAssistantManager().getDeclAssistant().isLibrary(node))
			{
				simplifyLibrary(node);
			}
			else
			{
				if(getInfo().getDeclAssistant().isTestCase(node))
				{
					userTestCases.add(getInfo().getDeclAssistant().getNodeName(node));
				}
				
				preProcessUserClass(node);
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