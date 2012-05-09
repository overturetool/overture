package org.overture.ide.plugins.uml2.vdm2uml;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

public class GettingStarted {

	public static boolean DEBUG = true;

    protected static void out(String output) {

        if (DEBUG) {
            System.out.println(output);
        }
    }

    protected static void err(String error) {
        System.err.println(error);
    }
    
    protected static Model createModel(String name) {
        Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);

        out("Model '" + model.getQualifiedName() + "' created.");

        return model;
    }
	
    protected static org.eclipse.uml2.uml.Class createClass(org.eclipse.uml2.uml.Package package_, String name, boolean isAbstract) {
        org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name, isAbstract);
        
        out("Class '" + class_.getQualifiedName() + "' created.");
        
        return class_;
    }
    
    protected static Model load(URI pathToModel)
    { 
		
		ResourceSet set = new ResourceSetImpl();
		set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		set.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		UMLPackage.eINSTANCE.eClass();
		set.createResource(pathToModel);
		Resource r = null;
		r = set.getResource(pathToModel, true);

		Model m = (Model) EcoreUtil.getObjectByType(r.getContents(),
				UMLPackage.Literals.MODEL);

		return m;
    }
    
    protected static void save(org.eclipse.uml2.uml.Package package_, URI uri) {    	
   
        Resource resource = new ResourceSetImpl().createResource(uri);
        resource.getContents().add(package_);
        try {
            resource.save(null);
        } catch (IOException ioe) {
            err(ioe.getMessage());
        }
    }
}
