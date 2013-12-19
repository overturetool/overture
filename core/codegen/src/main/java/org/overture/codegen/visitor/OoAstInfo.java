package org.overture.codegen.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.constants.OoAstConstants;

public class OoAstInfo
{
	
	//Visitors:
	private OoAstGenerator rootVisitor;
	private ClassVisitorCG classVisitor;
	private DeclVisitorCG declVisitor;
	private ExpVisitorCG expVisitor;
	private TypeVisitorCG typeVisitor;
	private StmVisitorCG stmVisitor;
	private StateDesignatorVisitor stateDesignatorVisitor;

	//Quotes:
	private HashSet<String> quoteVaues;
	
	public OoAstInfo(OoAstGenerator rootVisitor)
	{
		super();
		this.rootVisitor = rootVisitor;
		this.classVisitor = new ClassVisitorCG();
		this.declVisitor = new DeclVisitorCG();
		this.expVisitor = new ExpVisitorCG();
		this.typeVisitor = new TypeVisitorCG();
		this.stmVisitor = new StmVisitorCG();
		this.stateDesignatorVisitor = new StateDesignatorVisitor();
		
		this.quoteVaues = new HashSet<String>();
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
	
	public StateDesignatorVisitor getStateDesignatorVisitor()
	{
		return stateDesignatorVisitor;
	}
	
	public void registerQuoteValue(String value) throws AnalysisException
	{
		if(value == null || value.isEmpty())
			throw new AnalysisException("Tried to register invalid qoute value");
		
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
		
		quotes.setName(OoAstConstants.QUOTES_INTERFACE_NAME);
		
		LinkedList<AFieldDeclCG> fields = quotes.getFields();
		
		List<String> quoteValuesList = getQuoteValues();
		
		for(int i = 0; i < quoteValuesList.size(); i++)
		{
			AFieldDeclCG fieldDecl = new AFieldDeclCG();
			fieldDecl.setName(quoteValuesList.get(i));
			fieldDecl.setAccess(OoAstConstants.PUBLIC);
			fieldDecl.setFinal(false);
			fieldDecl.setStatic(true);
			fieldDecl.setType(new AIntNumericBasicTypeCG());
			
			AIntLiteralExpCG initial = new AIntLiteralExpCG();
			initial.setType(new AIntNumericBasicTypeCG());
			initial.setValue((1 + i) + "");
			
			fieldDecl.setInitial(initial);
			
			fields.add(fieldDecl);
		}
		
		
		
		return quotes;
	}
	
}
