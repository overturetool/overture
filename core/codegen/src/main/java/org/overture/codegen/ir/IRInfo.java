package org.overture.codegen.ir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.BindAssistantCG;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.DesignatorAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.assistant.TypeAssistantCG;
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
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.codegen.visitor.CGVisitor;
import org.overture.codegen.visitor.VisitorManager;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class IRInfo
{
	//Visitors
	private VisitorManager visitorManager;
	
	//Assistants
	private AssistantManager assistantManager;
	
	//VDM assistant factory
	private TypeCheckerAssistantFactory tcFactory;
	
	//Quotes:
	private Set<String> quoteVaues;

	//Unsupported VDM nodes
	private Set<NodeInfo> unsupportedNodes;

	//For generating variable names
	private ITempVarGen tempVarNameGen;
	
	//For configuring code generation
	private IRSettings settings;
	
	public IRInfo()
	{
		super();
		
		this.visitorManager = new VisitorManager();
		this.assistantManager = new AssistantManager();
		this.tcFactory = new TypeCheckerAssistantFactory();
		this.quoteVaues = new HashSet<String>();
		this.unsupportedNodes = new HashSet<NodeInfo>();
		this.tempVarNameGen = new TempVarNameGen();
		
		this.settings = new IRSettings();
	}
	
	public AssistantManager getAssistantManager()
	{
		return assistantManager;
	}

	public CGVisitor<AClassDeclCG> getClassVisitor()
	{
		return visitorManager.getClassVisitor();
	}
	
	public CGVisitor<SDeclCG> getDeclVisitor()
	{
		return visitorManager.getDeclVisitor();
	}
	
	public CGVisitor<SExpCG> getExpVisitor()
	{
		return visitorManager.getExpVisitor();
	}
	
	public CGVisitor<STypeCG> getTypeVisitor()
	{
		return visitorManager.getTypeVisitor();
	}
	
	public CGVisitor<SStmCG> getStmVisitor()
	{
		return visitorManager.getStmVisitor();
	}
	
	public CGVisitor<SStateDesignatorCG> getStateDesignatorVisitor()
	{
		return visitorManager.getStateDesignatorVisitor();
	}
	
	public CGVisitor<SObjectDesignatorCG> getObjectDesignatorVisitor()
	{
		return visitorManager.getObjectDesignatorVisitor();
	}
	
	public CGVisitor<SMultipleBindCG> getMultipleBindVisitor()
	{
		return visitorManager.getMultipleBindVisitor();
	}
	
	public CGVisitor<SBindCG> getBindVisitor()
	{
		return visitorManager.getBindVisitor();
	}
	
	public CGVisitor<SPatternCG> getPatternVisitor()
	{
		return visitorManager.getPatternVisitor();
	}
	
	public ExpAssistantCG getExpAssistant()
	{
		return assistantManager.getExpAssistant();
	}

	public DeclAssistantCG getDeclAssistant()
	{
		return assistantManager.getDeclAssistant();
	}

	public DesignatorAssistantCG getDesignatorAssistant()
	{
		return assistantManager.getDesignatorAssistant();
	}
	
	public StmAssistantCG getStmAssistant()
	{
		return assistantManager.getStmAssistant();
	}
	
	public TypeAssistantCG getTypeAssistant()
	{
		return assistantManager.getTypeAssistant();
	}
	
	public LocationAssistantCG getLocationAssistant()
	{
		return assistantManager.getLocationAssistant();
	}
	
	public BindAssistantCG getBindAssistant()
	{
		return assistantManager.getBindAssistant();
	}
	
	public void registerQuoteValue(String value) throws AnalysisException
	{
		if(value == null || value.isEmpty())
			throw new AnalysisExceptionCG("Tried to register invalid qoute value");
		
		quoteVaues.add(value);
	}
	
	public TypeCheckerAssistantFactory getTcFactory()
	{
		return tcFactory;
	}
	
	private List<String> getQuoteValues()
	{
		List<String> quoteValuesSorted = new ArrayList<String>(quoteVaues);
		Collections.sort(quoteValuesSorted);
		
		return quoteValuesSorted;
	}
	
	public AInterfaceDeclCG getQuotes()
	{
		AInterfaceDeclCG quotes = new AInterfaceDeclCG();
		
		quotes.setName(IRConstants.QUOTES_INTERFACE_NAME);
		
		LinkedList<AFieldDeclCG> fields = quotes.getFields();
		
		List<String> quoteValuesList = getQuoteValues();
		
		for(int i = 0; i < quoteValuesList.size(); i++)
		{
			AFieldDeclCG fieldDecl = new AFieldDeclCG();
			fieldDecl.setName(quoteValuesList.get(i));
			fieldDecl.setAccess(IRConstants.PUBLIC);
			fieldDecl.setFinal(false);
			fieldDecl.setStatic(true);
			fieldDecl.setType(new AIntNumericBasicTypeCG());
			
			AIntLiteralExpCG initial = new AIntLiteralExpCG();
			initial.setType(new AIntNumericBasicTypeCG());
			initial.setValue(1L + i);
			
			fieldDecl.setInitial(initial);
			
			fields.add(fieldDecl);
		}

		return quotes;
	}
	
	public void clearNodes()
	{
		unsupportedNodes.clear();
	}
	
	public void addUnsupportedNode(INode node)
	{
		NodeInfo info = new NodeInfo(node);
		unsupportedNodes.add(info);
	}
	
	public void addUnsupportedNode(INode node, String reason)
	{
		NodeInfo info = new NodeInfo(node, reason);
		unsupportedNodes.add(info);
	}
	
	public Set<NodeInfo> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
	
	public ITempVarGen getTempVarNameGen()
	{
		return tempVarNameGen;
	}
	
	public IRSettings getSettings()
	{
		return settings;
	}
	
	public void setSettings(IRSettings settings)
	{
		this.settings = settings;
	}
}
