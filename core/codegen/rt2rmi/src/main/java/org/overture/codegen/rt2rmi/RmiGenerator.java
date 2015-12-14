package org.overture.codegen.rt2rmi;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.ir.IREventObserver;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.rt2rmi.trans.RemoteTypeTrans;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;

public class RmiGenerator implements IREventObserver
{
	private JavaCodeGen javaGen;

	public RmiGenerator()
	{
		this.javaGen = new JavaCodeGen();
		this.javaGen.registerIrObs(this);
		addTransformations();
	}

	private void addTransformations()
	{
		// Add additional transformations
		this.javaGen.getTransSeries().getSeries().add(new RemoteTypeTrans());
	}

	public GeneratedData generate(List<SClassDefinition> rtClasses) throws AnalysisException
	{
		return javaGen.generateJavaFromVdm(rtClasses);
	}
	
	public JavaCodeGen getJavaGen()
	{
		return this.javaGen;
	}

	public IRSettings getIrSettings()
	{
		return this.javaGen.getSettings();
	}

	public JavaSettings getJavaSettings()
	{
		return this.javaGen.getJavaSettings();
	}
	
	@Override
	public List<IRStatus<INode>> initialIRConstructed(List<IRStatus<INode>> ast, IRInfo info)
	{
		// This method received the initial version of the IR before it is transformed
		Logger.getLog().println("Initial IR has " + ast.size() + " node(s)");
		
		// For an example of how to process/modify the IR see org.overture.codegen.vdm2jml.JmlGenerator
		
		// Return the (possibly modified) AST that the Java code generator should use subsequently
		return ast;
	}

	@Override
	public List<IRStatus<INode>> finalIRConstructed(List<IRStatus<INode>> ast, IRInfo info)
	{
		// The final version of the IR
		
		Logger.getLog().println("Final version of the IR has " + ast.size() + " node(s)");
		
		return ast;
	}
}
