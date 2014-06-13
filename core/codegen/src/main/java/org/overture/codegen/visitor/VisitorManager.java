package org.overture.codegen.visitor;

import org.overture.codegen.cgast.SBindCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;


public class VisitorManager
{
	private CGVisitor<AClassDeclCG> classVisitor;
	private CGVisitor<SDeclCG> declVisitor;
	private CGVisitor<SExpCG> expVisitor;
	private CGVisitor<STypeCG> typeVisitor;
	private CGVisitor<SStmCG> stmVisitor;
	private CGVisitor<SStateDesignatorCG> stateDesignatorVisitor;
	private CGVisitor<SObjectDesignatorCG> objectDesignatorVisitor;
	private CGVisitor<SMultipleBindCG> multipleBindVisitor;
	private CGVisitor<SBindCG> bindVisitor;
	private CGVisitor<SPatternCG> patternVisitor;
	
	public VisitorManager()
	{
		this.classVisitor = new CGVisitor<AClassDeclCG>(new ClassVisitorCG());
		this.declVisitor = new CGVisitor<SDeclCG>(new DeclVisitorCG());
		this.expVisitor = new CGVisitor<SExpCG>(new ExpVisitorCG());
		this.typeVisitor = new CGVisitor<STypeCG>(new TypeVisitorCG());
		this.stmVisitor = new CGVisitor<SStmCG>(new StmVisitorCG());
		this.stateDesignatorVisitor = new CGVisitor<SStateDesignatorCG>(new StateDesignatorVisitorCG());
		this.objectDesignatorVisitor = new CGVisitor<SObjectDesignatorCG>(new ObjectDesignatorVisitorCG());
		this.multipleBindVisitor = new CGVisitor<SMultipleBindCG>(new MultipleBindVisitorCG());
		this.bindVisitor =  new CGVisitor<SBindCG>(new BindVisitorCG());
		this.patternVisitor = new CGVisitor<SPatternCG>(new PatternVisitorCG());
	}
	
	public CGVisitor<AClassDeclCG> getClassVisitor()
	{
		return classVisitor;
	}
	
	public CGVisitor<SDeclCG> getDeclVisitor()
	{
		return declVisitor;
	}
	
	public CGVisitor<SExpCG> getExpVisitor()
	{
		return expVisitor;
	}
	
	public CGVisitor<STypeCG> getTypeVisitor()
	{
		return typeVisitor;
	}
	
	public CGVisitor<SStmCG> getStmVisitor()
	{
		return stmVisitor;
	}
	
	public CGVisitor<SStateDesignatorCG> getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}
	
	public CGVisitor<SObjectDesignatorCG> getObjectDesignatorVisitor()
	{
		return objectDesignatorVisitor;
	}
	
	public CGVisitor<SMultipleBindCG> getMultipleBindVisitor()
	{
		return multipleBindVisitor;
	}
	
	public CGVisitor<SBindCG> getBindVisitor()
	{
		return bindVisitor;
	}
	
	public CGVisitor<SPatternCG> getPatternVisitor()
	{
		return patternVisitor;
	}
}
