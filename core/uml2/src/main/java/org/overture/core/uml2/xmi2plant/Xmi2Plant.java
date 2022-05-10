package org.overture.core.uml2.xmi2plant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
//import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
/* import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype; */
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
/* import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp; */
import org.overture.ast.factory.AstFactory;
//import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
/* import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.config.Settings; */
import org.overture.core.uml2.UmlConsole;
/* import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult; */
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;


public class Xmi2Plant
{
    
	final static LexLocation location = new LexLocation(new File("generated"), "generating", 0, 0, 0, 0, 0, 0);
	Model model;
	private String extension = "wsd";
	//private VdmTypeCreator tc = null;
	private UmlConsole console;
	//private PExp NEW_A_UNDEFINED_EXP = AstFactory.newAUndefinedExp(location);

	public Xmi2Plant()
	{
		console = new UmlConsole();
		//tc = new VdmTypeCreator(console);
	}

	public boolean initialize(URI uri, String extension)
    {
        if (extension != null)
        {
            this.extension = extension;
        }
        ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		UMLPackage.eINSTANCE.eClass();
		resourceSet.createResource(uri);
		
        Resource resource = resourceSet.getResource(uri, true);

        for (EObject c : resource.getContents())
        {
            if (c instanceof Model)
            {
                model = (Model) c;
            }
        }

        return model != null;
    }


    public void convert(File outputDir)
    {
        if (model != null)
        {

            console.show();

            console.out.println("#\n# Starting translation of model: "
                    + model.getName() + "\n#");
            console.out.println("# Into: " + outputDir + "\n#");
            console.out.println("-------------------------------------------------------------------------");
            Map<String, AClassClassDefinition> classes = new HashMap<String, AClassClassDefinition>();
            for (Element e : model.getOwnedElements())
            {
                if (e instanceof Class)
                {
                    Class class_ = (Class) e;
                    console.out.println("Converting: " + class_.getName());
                    classes.put(class_.getName(), createClass(class_));
                }
            }

            console.out.println("Writing source files");
            for (Entry<String, AClassClassDefinition> c : classes.entrySet())
            {
                writeClassFile(outputDir, c);
            }
            console.out.println("Conversion completed.");
        }
    }

	private void writeClassFile(File outputDir,
            Entry<String, AClassClassDefinition> c)
    {
        try
        {
            outputDir.mkdirs();
            FileWriter outFile = new FileWriter(new File(outputDir, c.getKey()
                    + "." + extension));
            PrintWriter out = new PrintWriter(outFile);

            out.println(c.getValue().apply(new PrettyPrinterVisitor(), new PrettyPrinterEnv()));
            out.close();
        } catch (IOException e)
        {
            System.out.print("Error in writeclassfile" + e.getMessage());
        } catch (AnalysisException e)
        {
            System.out.print("Error in writeclassfile" + e.getMessage());
        }
    }

    private AClassClassDefinition createClass(Class class_)
	{
        AClassClassDefinition c = AstFactory.newAClassClassDefinition();
		c.setName(new LexNameToken(class_.getName(), class_.getName(), null));

        return c;
    }
}
