package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.CommunicationPath;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AClassType;

public class UmlDeploymentCreator
{
	private Model modelWorkingCopy;
	
	public UmlDeploymentCreator(Model model)
	{
		this.modelWorkingCopy = model;
	}

	public void buildDeployment(List<SClassDefinition> classes2)
	{
		Map<String, Node> nodes = new HashMap<String, Node>();
		List<AInstanceVariableDefinition> systemInsts = new Vector<AInstanceVariableDefinition>();
		ASystemClassDefinition system = null;
		Package deploymentPackage = null;
		for (SClassDefinition c : classes2)
		{
			if (c instanceof ASystemClassDefinition)
			{
				system = (ASystemClassDefinition) c;

				for (PDefinition d : system.getDefinitions())
				{
					if (d instanceof AInstanceVariableDefinition)
					{

						AInstanceVariableDefinition ind = (AInstanceVariableDefinition) d;
						if (ind.getType() instanceof AClassType)
						{
							systemInsts.add((AInstanceVariableDefinition) d);
						}
					}
				}
			}
		}

		if (system != null)
		{
			deploymentPackage = (Package) this.modelWorkingCopy.createNestedPackage("Deployment");
		}

		if (!systemInsts.isEmpty())
		{
			for (AInstanceVariableDefinition ind : systemInsts)
			{
				PDefinition def = ((AClassType) ind.getType()).getClassdef();
				if (def instanceof ACpuClassDefinition)
				{
					Node n = (Node) deploymentPackage.createPackagedElement(ind.getName().name, UMLPackage.Literals.NODE);
					nodes.put(ind.getName().name, n);
				}
			}

			for (AInstanceVariableDefinition ind : systemInsts)
			{
				PDefinition def = ((AClassType) ind.getType()).getClassdef();
				if (def instanceof ABusClassDefinition)
				{
					CommunicationPath con = (CommunicationPath) deploymentPackage.createPackagedElement(ind.getName().module, UMLPackage.Literals.COMMUNICATION_PATH);

					ANewExp e = (ANewExp) ind.getExpression();

					if (e.getArgs().size() == 3
							&& e.getArgs().getLast() instanceof ASetEnumSetExp)
					{
						ASetEnumSetExp set = (ASetEnumSetExp) e.getArgs().getLast();
						for (PExp m : set.getMembers())
						{
							if (nodes.containsKey(m.toString()))
							{
								con.createNavigableOwnedEnd("", nodes.get(m.toString()));
							}
						}

					}
				}
			}
		}

		if (system != null)
		{
			for (PDefinition d : system.getDefinitions())
			{
				if (d instanceof AExplicitOperationDefinition)
				{
					AExplicitOperationDefinition op = (AExplicitOperationDefinition) d;
					if (op.getIsConstructor())
					{

						if (op.getBody() instanceof ABlockSimpleBlockStm)
						{
							ABlockSimpleBlockStm block = (ABlockSimpleBlockStm) op.getBody();
							for (PStm stm : block.getStatements())
							{
								System.out.println(stm);
								if (stm instanceof ACallObjectStm)
								{
									ACallObjectStm call = (ACallObjectStm) stm;
									if (call.getFieldname().toString().equals("deploy")
											&& call.getDesignator() instanceof AIdentifierObjectDesignator)
									{
										String nodeName = ((AIdentifierObjectDesignator) call.getDesignator()).getName().name;
										if (nodes.containsKey(nodeName))
										{
											String deployedName = call.getArgs().get(0).toString();
											if (call.getArgs().size() > 1)
											{
												deployedName = call.getArgs().get(1).toString();
											}
											Artifact artifact = (Artifact) nodes.get(nodeName).createNestedClassifier(deployedName, UMLPackage.Literals.ARTIFACT);
											if (call.getArgs().get(0) instanceof AVariableExp
													&& ((AVariableExp) call.getArgs().get(0)).getType() instanceof AClassType)
											{
												AVariableExp var = (AVariableExp) call.getArgs().get(0);
												// Class c = classes.get(((AClassType) var.getType()).getName().name);
												artifact.setFileName(((AClassType) var.getType()).getName().location.file.getName());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}
}
