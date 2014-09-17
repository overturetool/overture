/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.resources.OptionGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Options implements Serializable
{
	private static final String BOOLEAN_ATTRIBUTE = "booleanAttribute";

	private static final String STRING_ATTRIBUTE = "stringAttribute";

	/**
	 * 
	 */
	private static final long serialVersionUID = -7776063704790917820L;

	transient private IProject project;
	transient private File optionPathFile;
	transient private IVdmProject vdmProject;
	final List<OptionGroup> groups = new Vector<OptionGroup>();

	public Options()
	{

	}

	public static Options load(IVdmProject vdmProject)
	{
		IProject project = (IProject) vdmProject.getAdapter(IProject.class);
		IPath base = project.getLocation();
		base = base.append(".overture");
		File optionPathFile = base.toFile();

		Options opt = null;

		if (!optionPathFile.exists())
		{
			opt = new Options();
		} else
		{
			// FileInputStream fis = null;
			// ObjectInputStream in = null;
			// try
			// {
			// fis = new FileInputStream(optionPathFile);
			// in = new ObjectInputStream(fis);
			// opt = (Options) in.readObject();
			// in.close();
			// } catch (IOException ex)
			// {
			// ex.printStackTrace();
			// } catch (ClassNotFoundException ex)
			// {
			// ex.printStackTrace();
			// }
			// XMLDecoder decoder = null;
			// try
			// {
			// decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(optionPathFile)));
			// opt = (Options) decoder.readObject();
			//
			// } catch (FileNotFoundException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } finally
			// {
			// decoder.close();
			// }
			opt = readXml(optionPathFile);

		}

		opt.init(vdmProject);

		return opt;
	}

	private synchronized void init(IVdmProject project)
	{
		this.vdmProject = project;
		this.project = (IProject) this.vdmProject.getAdapter(IProject.class);
		IPath base = this.project.getLocation();
		base = base.append(".overture");
		this.optionPathFile = base.toFile();
		for (OptionGroup g : this.groups)
		{
			g.setParent(this);
		}
	}

	public synchronized void save()
	{
		Assert.isNotNull(this.project, "Options not initlialized, project null");
		// FileOutputStream fos = null;
		// ObjectOutputStream out = null;
		// try
		// {
		// fos = new FileOutputStream(this.optionPathFile);
		// out = new ObjectOutputStream(fos);
		// out.writeObject(this);
		// out.close();
		// } catch (IOException ex)
		// {
		// ex.printStackTrace();
		// }
		// XMLEncoder encoder = null;
		// try
		// {
		// encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(optionPathFile)));
		// encoder.setExceptionListener(new ExceptionListener() {
		// public void exceptionThrown(Exception exception) {
		// exception.printStackTrace();
		// }
		// });
		//			
		// encoder.writeObject(new A());
		// } catch (FileNotFoundException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally
		// {
		// encoder.close();
		// }
		writeXml();
	}

	private void writeXml()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<overture>\n");

		for (OptionGroup g : groups)
		{
			sb.append("\t<group key=\"" + g.id + "\">\n");

			Map<String, Object> attributes = g.getAttributes();
			for (Entry<String, Object> entry : attributes.entrySet())
			{
				String type = null;

				Object value = entry.getValue();

				if (value instanceof String)
				{
					type = STRING_ATTRIBUTE;
				} else if (value instanceof Boolean)
				{
					type = BOOLEAN_ATTRIBUTE;
				}

				if (type != null)
				{
					sb.append("\t\t<" + type + " key=\"" + entry.getKey()
							+ "\" value=\"" + entry.getValue().toString()
							+ "\"/>\n");
				}
			}

			sb.append("\t</group>\n");
		}

		sb.append("</overture>");

		PrintWriter out = null;
		try
		{
			FileWriter outFile = new FileWriter(this.optionPathFile);
			out = new PrintWriter(outFile);
			out.println(sb.toString());

		} catch (IOException e)
		{
			VdmCore.log("Faild to save .modelpath file", e);
		} finally
		{
			if(out != null)
			{
				out.close();
			}
		}
	}

	private static Options readXml(File file)
	{
		try
		{
			Options opt = new Options();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("group");
			for (int s = 0; s < nodeLst.getLength(); s++)
			{
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Node keyAttribute = fstNode.getAttributes().getNamedItem("key");
					String keyValue = keyAttribute.getNodeValue();
					if (keyValue != null)
					{
						OptionGroup g = opt.internalCreateGroup(keyValue);

						Element groupElement = (Element) fstNode;
						{
							NodeList nodeLstStringAtt = groupElement.getElementsByTagName(STRING_ATTRIBUTE);
							for (int j = 0; j < nodeLstStringAtt.getLength(); j++)
							{
								Node fstNodeStrAtt = nodeLstStringAtt.item(j);
								if (fstNodeStrAtt.getNodeType() == Node.ELEMENT_NODE)
								{
									Element eStrAtt = (Element) fstNodeStrAtt;

									g.setAttribute(eStrAtt.getAttribute("key"), eStrAtt.getAttribute("value"));

								}

							}
						}
						{
							NodeList nodeLstStringAtt = groupElement.getElementsByTagName(BOOLEAN_ATTRIBUTE);
							for (int j = 0; j < nodeLstStringAtt.getLength(); j++)
							{
								Node fstNodeStrAtt = nodeLstStringAtt.item(j);
								if (fstNodeStrAtt.getNodeType() == Node.ELEMENT_NODE)
								{
									Element eStrAtt = (Element) fstNodeStrAtt;

									g.setAttribute(eStrAtt.getAttribute("key"), Boolean.valueOf(eStrAtt.getAttribute("value")));

								}

							}
						}

					}
					// if (keyValue.equals("src"))
					// {
					// Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
					// String pathValue = pathAttribute.getNodeValue();
					// srcPaths.add(this.project.getFolder(pathValue));
					// } else if (keyValue.equals("output"))
					// {
					// Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
					// String pathValue = pathAttribute.getNodeValue();
					// output = this.project.getFolder(pathValue);
					// }
				}
			}
			return opt;
		} catch (Exception e)
		{
			VdmCore.log("Faild to parse .modelpath file", e);
		}
		return null;
	}

	public synchronized IOptionGroup getGroup(String id)
	{
		Assert.isNotNull(this.project, "Options not initlialized, project null");

		for (OptionGroup g : groups)
		{
			if (g.id.equals(id))
			{
				return g;
			}
		}

		return null;
	}

	public synchronized IOptionGroup getGroup(String id,
			boolean createIfNotFound)
	{
		if (createIfNotFound)
		{
			return createGroup(id);
		} else
		{
			return getGroup(id);
		}
	}

	public IOptionGroup createGroup(String id)
	{
		Assert.isNotNull(this.project, "Options not initlialized, project null");
		IOptionGroup group = getGroup(id);
		if (group != null)
		{
			return group;
		} else
		{
			return internalCreateGroup(id);
		}
	}

	private synchronized OptionGroup internalCreateGroup(String id)
	{
		OptionGroup g = new OptionGroup(id, this);
		groups.add(g);
		return g;
	}
}
