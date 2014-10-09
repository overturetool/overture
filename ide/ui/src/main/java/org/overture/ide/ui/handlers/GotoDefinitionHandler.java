/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.PType;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2.TextReference;

public class GotoDefinitionHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null & selection instanceof TextSelection)
		{
			TextSelection tselection = (TextSelection) selection;

			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			// IEditorInput editorInput = activeEditor.getEditorInput();
			if (activeEditor instanceof VdmEditor)
			{
				VdmEditor editor = (VdmEditor) activeEditor;
				IVdmElement input = editor.getInputVdmElement();
				if (input instanceof IVdmSourceUnit)
				{
					IVdmSourceUnit source = (IVdmSourceUnit) input;
					IProject iproject = source.getFile().getProject();
					IVdmProject p = (IVdmProject) iproject.getAdapter(IVdmProject.class);
					if (p != null)
					{
						if (!p.getModel().isTypeChecked())
						{
							// We need to get rid of the untyped stuff
							VdmTypeCheckerUi.typeCheck(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), p);
						}
					}
					INode node = new AstLocationSearcher2().getNode(new TextReference(source.getSystemFile(), tselection.getOffset()),source.getParseList() );

					PType gotoType = null;//all code related to this var is actually togo type defeinition
					ILexLocation gotoLocation = null;
					//behaviour toto definition - connecnt this block to get goto type
					if(node instanceof AVariableExp)
					{
						gotoLocation = ((AVariableExp) node).getVardef().getLocation();
					}
					else
						//end goto definition
					if (node instanceof PDefinition)
					{
						gotoType = ((PDefinition) node).getType();
					} else if (node instanceof PStm)
					{
						gotoType = ((PStm) node).getType();
					} else if (node instanceof PExp)
					{
						gotoType = ((PExp) node).getType();
					} else if (node instanceof PType)
					{
						if (node instanceof ANamedInvariantType)
						{
							gotoLocation = ((ANamedInvariantType) node).getName().getLocation();
						} else
						{
							gotoLocation = ((PType) node).getLocation();// we have no where to goto begining of the type
						}
					} else if (node instanceof AModuleModules)
					{
						// //do something special
					}

					if (gotoType != null)
					{
						gotoLocation = gotoType.getLocation();
					}

					if (gotoLocation != null)
					{
						if (!source.getFile().getName().startsWith(gotoLocation.getFile().getName()))
						{
							try
							{
								for (IVdmSourceUnit su : p.getSpecFiles())
								{
									if(su.getFile().getName().startsWith(gotoLocation.getFile().getName()))
									{
										source = su;
										break;
									}
								}
								IEditorPart newEditor = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), source.getFile(), true);
								if(newEditor instanceof VdmEditor)
								{
									editor = (VdmEditor) newEditor;
								}
							} catch (PartInitException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CoreException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						if(editor!=null)
						{
							int start = gotoLocation.getStartOffset();
							int length = gotoLocation.getEndOffset()
									- gotoLocation.getEndOffset();
							editor.setFocus();
							editor.selectAndReveal(start, length);
						}
					}
					System.out.println("Selected:" + tselection.getText()+" Node: "+node);
				}
			}
		}
		return null;
	}
}
