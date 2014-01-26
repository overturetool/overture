package org.overture.codegen.ooast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.codegen.visitor.ClassVisitorCG;
import org.overture.codegen.visitor.DeclVisitorCG;
import org.overture.codegen.visitor.ExpVisitorCG;
import org.overture.codegen.visitor.ObjectDesignatorVisitorCG;
import org.overture.codegen.visitor.StateDesignatorVisitorCG;
import org.overture.codegen.visitor.StmVisitorCG;
import org.overture.codegen.visitor.TypeVisitorCG;

public class OoAstInfo
{
	
	//Visitors:
	private OoAstGenerator rootVisitor;
	private ClassVisitorCG classVisitor;
	private DeclVisitorCG declVisitor;
	private ExpVisitorCG expVisitor;
	private TypeVisitorCG typeVisitor;
	private StmVisitorCG stmVisitor;
	private StateDesignatorVisitorCG stateDesignatorVisitor;
	private ObjectDesignatorVisitorCG objectDesignatorVisitor;
	
	//Quotes:
	private Set<String> quoteVaues;

	//Unsupported VDM nodes
	private Set<INode> unsupportedNodes;
	
	public OoAstInfo(OoAstGenerator rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.classVisitor = new ClassVisitorCG();
		this.declVisitor = new DeclVisitorCG();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
		this.stateDesignatorVisitor = new StateDesignatorVisitorCG();
		this.objectDesignatorVisitor = new ObjectDesignatorVisitorCG();
		
		this.quoteVaues = new HashSet<String>();
		
		this.unsupportedNodes = new HashSet<INode>();
	}

	public OoAstGenerator getRootVisitor()
	{
		return rootVisitor;
	}
	
	public ClassVisitorCG getClassVisitor()
	{
		return classVisitor;
	}
	
	public DeclVisitorCG getDeclVisitor()
	{
		return declVisitor;
	}
	
	public ExpVisitorCG getExpVisitor()
	{
		return expVisitor;
	}
	
	public TypeVisitorCG getTypeVisitor()
	{
		return typeVisitor;
	}
	
	public StmVisitorCG getStatementVisitor()
	{
		return stmVisitor;
	}
	
	public StateDesignatorVisitorCG getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}
	
	public ObjectDesignatorVisitorCG getObjectDesignatorVisitor()
	{
		return objectDesignatorVisitor;
	}
	
	public void registerQuoteValue(String value) throws AnalysisException
	{
		if(value == null || value.isEmpty())
			throw new AnalysisExceptionCG("Tried to register invalid qoute value");
		
		quoteVaues.add(value);
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
		
		quotes.setName(IOoAstConstants.QUOTES_INTERFACE_NAME);
		
		LinkedList<AFieldDeclCG> fields = quotes.getFields();
		
		List<String> quoteValuesList = getQuoteValues();
		
		for(int i = 0; i < quoteValuesList.size(); i++)
		{
			AFieldDeclCG fieldDecl = new AFieldDeclCG();
			fieldDecl.setName(quoteValuesList.get(i));
			fieldDecl.setAccess(IOoAstConstants.PUBLIC);
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
		unsupportedNodes.add(node);
	}
	
	public Set<INode> getUnsupportedNodes()
	{
		return unsupportedNodes;
	}
}
