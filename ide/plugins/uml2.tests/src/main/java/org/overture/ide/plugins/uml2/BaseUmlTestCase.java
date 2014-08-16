/*
 * #%~
 * UML2 Translator Tests
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.uml2;

import java.util.List;




import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.Relationship;
import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;


public abstract class BaseUmlTestCase
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
					Assert.fail("Parse fail");
				} catch (LexException e)
				{
					Assert.fail("Parse fail");
				}
				break;
			case VDM_SL:
			case CML:
				Assert.fail("Not supported");
				break;
			default:
				break;
			
		}
		List<SClassDefinition> input = result.result;

		Assert.assertTrue("Parse errors: " + result.parserResult.errors.toString(), result.parserResult.errors.isEmpty());
		Assert.assertTrue("Type Check errors" + result.errors.toString(), result.errors.isEmpty());

		Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, deployArtifactsOutsideNodes);
		vdm2uml.convert("Test Model", input);

		Model umlmodel = vdm2uml.getModel();
		Assert.assertNotNull("No model", umlmodel);
		return umlmodel;
	}
	
	@Before
	public void setUp() throws Exception
	{
		this.preferAssociations = false;
		this.deployArtifactsOutsideNodes = false;
		Settings.dialect = Dialect.VDM_PP;
	}
	
	
	protected Classifier getClass(Namespace model,String name)
	{
		NamedElement classA = model.getOwnedMember(name);
		Assert.assertNotNull("Class "+name+" does not exist", classA);
		if(classA instanceof Classifier)
		{
			return (Classifier) classA;
		}
		
		Assert.fail(name+" is not a class in the model");
		return null;
	}
	
	public Classifier assertIsSubClassOf(Classifier cl, String superClass)
	{
		Assert.assertFalse("No generalizations", cl.getRelationships().isEmpty());

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
		Assert.assertNotNull(cl.getName() + " does not extend " + superClass, extendsSuper);
//		assertTrue(cl.getName() + " does not extend " + superClass, extendsSuper);
		return extendsSuper;
	}
}
