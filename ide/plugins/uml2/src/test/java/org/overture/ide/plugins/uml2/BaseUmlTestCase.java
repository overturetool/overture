package org.overture.ide.plugins.uml2;

import java.util.List;


import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Relationship;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import junit.framework.TestCase;

public abstract class BaseUmlTestCase extends TestCase
{
	protected boolean preferAssociations = false;
	protected boolean deployArtifactsOutsideNodes = false;
	
	protected Model convert(String spec)
	{
		TypeCheckResult<List<SClassDefinition>> result=null;
		switch(Settings.dialect)
		{
			case VDM_PP:
			result	= TypeCheckerUtil.typeCheckPp(spec);
				break;
			case VDM_RT:
				try
				{
					result = TypeCheckerUtil.typeCheckRt(spec);
				} catch (ParserException e)
				{
					fail("Parse fail");
				} catch (LexException e)
				{
					fail("Parse fail");
				}
				break;
			case VDM_SL:
			case CML:
				fail("Not supported");
				break;
			default:
				break;
			
		}
		List<SClassDefinition> input = result.result;

		assertTrue("Parse errors: " + result.parserResult.errors.toString(), result.parserResult.errors.isEmpty());
		assertTrue("Type Check errors" + result.errors.toString(), result.errors.isEmpty());

		Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, deployArtifactsOutsideNodes);
		vdm2uml.convert("Test Model", input);

		Model umlmodel = vdm2uml.getModel();
		assertNotNull("No model", umlmodel);
		return umlmodel;
	}
	
	@Override
	protected void setUp() throws Exception
	{
		this.preferAssociations = false;
		this.deployArtifactsOutsideNodes = false;
		Settings.dialect = Dialect.VDM_PP;
	}
	
	
	protected Classifier getClass(Namespace model,String name)
	{
		NamedElement classA = model.getOwnedMember(name);
		assertNotNull("Class "+name+" does not exist", classA);
		if(classA instanceof Classifier)
		{
			return (Classifier) classA;
		}
		
		fail(name+" is not a class in the model");
		return null;
	}
	
	public Classifier assertIsSubClassOf(Classifier cl, String superClass)
	{
		assertFalse("No generalizations", cl.getRelationships().isEmpty());

		Classifier extendsSuper = null;
		for (Relationship r : cl.getRelationships())
		{
			if (r instanceof Generalization)
			{
				Generalization g = (Generalization) r;
				if(g.getGeneral().getName().equals(superClass))
				{
					extendsSuper = g.getGeneral();	
				}
				
			}
		}
		assertNotNull(cl.getName() + " does not extend " + superClass, extendsSuper);
//		assertTrue(cl.getName() + " does not extend " + superClass, extendsSuper);
		return extendsSuper;
	}
}
