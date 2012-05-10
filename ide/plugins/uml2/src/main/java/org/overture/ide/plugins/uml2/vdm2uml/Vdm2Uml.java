package org.overture.ide.plugins.uml2.vdm2uml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.overture.ast.definitions.SClassDefinition;

public class Vdm2Uml {

	private Vector<String> filteredClassNames = new Vector<String>();
	private Model modelWorkingCopy = null; 
	private Map<String,Class> classes = new HashMap<String, Class>();
	
	public Vdm2Uml() {
		
	}
	
	
	public Model init(List<SClassDefinition> classes)
	{
		modelWorkingCopy = UMLFactory.eINSTANCE.createModel();		
		
		buildUml(classes);
		
		return modelWorkingCopy;
	}

	public  void save(URI uri)
			throws IOException {

		Resource resource = new ResourceSetImpl().createResource(uri.appendFileExtension(UMLResource.FILE_EXTENSION));
		resource.getContents().add(modelWorkingCopy);

		resource.save(null);
	}

	private void buildUml(List<SClassDefinition> classes)  {
		
		for (SClassDefinition sClass : classes) {
			
			String className = sClass.getName().name;
			if(!filteredClassNames.contains(className))
			{
				Class class_ = buildClass(sClass);
				this.classes.put(className, class_);
			}
			
		}
	
		
	}

	private Class buildClass(SClassDefinition sClass)  {
		
		String name = sClass.getName().name;
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		Class class_ = modelWorkingCopy.createOwnedClass(name, isAbstract);
		
		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		class_.setIsActive(isActive);
		
		class_.setVisibility(VisibilityKind.PUBLIC_LITERAL);
		
		
		return class_;
		//buildDefinitionBlocks(class_,sClass.getDefinitions());
		
	}

	

	

	


	

	
	
}
