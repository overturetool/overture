/*
 * #%~
 * UML2 Translator
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
package org.overture.core.uml2.vdm2uml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Class;
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
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.PType;
import org.overture.core.uml2.UmlConsole;

public class UmlDeploymentCreator
{
	private Model modelWorkingCopy;
	private UmlConsole console;
	private boolean deployArtifactsOutsideNodes = true;
	private UmlTypeCreator utc;

	public UmlDeploymentCreator(Model model, UmlConsole console,
			boolean deployArtifactsOutsideNodes, UmlTypeCreator utc)
	{
		this.modelWorkingCopy = model;
		this.console = console;
		this.utc = utc;
		// IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		// if (preferences != null)
		// {
		// this.deployArtifactsOutsideNodes =
		// preferences.getBoolean(IUml2Constants.DISABLE_NESTED_ARTIFACTS_PREFERENCE);
		// }
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
			console.out.println("Creating deployment package");
			deploymentPackage = (Package) this.modelWorkingCopy.createNestedPackage("Deployment");
		}

		if (!systemInsts.isEmpty())
		{
			for (AInstanceVariableDefinition ind : systemInsts)
			{
				PDefinition def = ((AClassType) ind.getType()).getClassdef();
				if (def instanceof ACpuClassDefinition)
				{
					console.out.println("Adding node: "
							+ ind.getName().getName());
					Node n = (Node) deploymentPackage.createPackagedElement(ind.getName().getName(), UMLPackage.Literals.NODE);
					nodes.put(ind.getName().getName(), n);
				}
			}

			for (AInstanceVariableDefinition ind : systemInsts)
			{
				PDefinition def = ((AClassType) ind.getType()).getClassdef();
				if (def instanceof ABusClassDefinition)
				{
					console.out.print("Adding comminication path between: ");
					CommunicationPath con = (CommunicationPath) deploymentPackage.createPackagedElement(ind.getName().getModule(), UMLPackage.Literals.COMMUNICATION_PATH);

					ANewExp e = (ANewExp) ind.getExpression();

					if (e.getArgs().size() == 3
							&& e.getArgs().getLast() instanceof ASetEnumSetExp)
					{
						ASetEnumSetExp set = (ASetEnumSetExp) e.getArgs().getLast();
						for (PExp m : set.getMembers())
						{
							if (nodes.containsKey(m.toString()))
							{
								console.out.print(" " + m.toString());
								con.createNavigableOwnedEnd("", nodes.get(m.toString()));
							}
						}

					}
					console.out.print("\n");
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
										String nodeName = ((AIdentifierObjectDesignator) call.getDesignator()).getName().getName();
										if (nodes.containsKey(nodeName))
										{
											String deployedName = call.getArgs().get(0).toString();
											if (call.getArgs().size() > 1)
											{
												deployedName = call.getArgs().get(1).toString();
											}
											Artifact artifact = null;
											if (deployArtifactsOutsideNodes)
											{
												artifact = (Artifact) deploymentPackage.createPackagedElement(deployedName, UMLPackage.Literals.ARTIFACT);
												// Usage usage = (Usage) artifact.create("use",
												// UMLPackage.Literals.USAGE);
												// usage.createUsage(nodes.get(nodeName));
												// usage.createDependency(artifact);
												// nodes.get(nodeName).createUsage(artifact);
												// artifact.createUsage(nodes.get(nodeName));
												artifact.createOwnedComment().setBody("Deployed to "
														+ nodeName);
												nodes.get(nodeName).createOwnedComment().setBody("Deploys "
														+ artifact.getName());
												deploymentPackage.createOwnedComment().setBody("Artifact "
														+ artifact.getName()
														+ " is deployed onto Node "
														+ nodeName);

												PType type = call.getArgs().getFirst().getType();
												if (type instanceof AOptionalType)
												{
													PType ot = ((AOptionalType) type).getType();
													utc.create((Class) utc.getUmlType(ot), type);
												}
												// utc.create(utc.getBindingPackage(), type);
												nodes.get(nodeName).createOwnedAttribute(deployedName, utc.getUmlType(type));
											} else
											{
												artifact = (Artifact) nodes.get(nodeName).createNestedClassifier(deployedName, UMLPackage.Literals.ARTIFACT);
											}
											if (call.getArgs().get(0) instanceof AVariableExp
													&& ((AVariableExp) call.getArgs().get(0)).getType() instanceof AClassType)
											{

												AVariableExp var = (AVariableExp) call.getArgs().get(0);
												// Class c = classes.get(((AClassType) var.getType()).getName().name);
												artifact.setFileName(((AClassType) var.getType()).getName().getLocation().getFile().getName());
												console.out.println("Adding artifact: "
														+ artifact.getName());
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
