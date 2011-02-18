package org.overture.ide.core.resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ModelBuildPath
{
	final IVdmProject vdmProject;
	final IProject project;
	final File modelPathFile;

	List<IContainer> srcPaths = new Vector<IContainer>();
	IContainer output;

	public ModelBuildPath(IVdmProject project)
	{
		this.vdmProject = project;

		this.project = (IProject) this.vdmProject.getAdapter(IProject.class);
		IPath base = this.project.getLocation();
		base = base.append(".modelpath");
		this.modelPathFile = base.toFile();
		this.output = this.project.getFolder("generated");
		parse();
	}

	private boolean hasModelPath()
	{
		return this.modelPathFile.exists();
	}

	private IContainer getDefaultModelSrcPath()
	{
		return this.project;
	}

	public List<IContainer> getModelSrcPaths()
	{
		List<IContainer> tmp = new Vector<IContainer>(srcPaths.size());
		tmp.addAll(srcPaths);
		return tmp;
	}

	public IContainer getOutput()
	{
		return this.output;
	}

	private synchronized void parse()
	{
		if (!hasModelPath())
		{
			srcPaths.add(getDefaultModelSrcPath());
			return;
		}
		try
		{
			File file = this.modelPathFile;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("modelpathentry");
			for (int s = 0; s < nodeLst.getLength(); s++)
			{
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Node kindAttribute = fstNode.getAttributes().getNamedItem("kind");
					String kindValue = kindAttribute.getNodeValue();
					if (kindValue != null)
					{
						if (kindValue.equals("src"))
						{
							Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
							String pathValue = pathAttribute.getNodeValue();
							srcPaths.add(this.project.getFolder(pathValue));
						} else if (kindValue.equals("output"))
						{
							Node pathAttribute = fstNode.getAttributes().getNamedItem("path");
							String pathValue = pathAttribute.getNodeValue();
							output = this.project.getFolder(pathValue);
						}
					}
				}

			}
		} catch (Exception e)
		{
			VdmCore.log("Faild to parse .modelpath file", e);
		}
	}
	
	public synchronized void setOutput(IContainer container)
	{
		this.output = container;
	}

	public synchronized void add(IContainer container)
	{
		if (!srcPaths.contains(container))
		{
			srcPaths.add(container);
		}
	}

	public synchronized void remove(IContainer container)
	{
		if (srcPaths.contains(container))
		{
			srcPaths.remove(container);
		}
	}

	public synchronized boolean contains(IContainer container)
	{
		return srcPaths.contains(container);
	}

	public void save() throws CoreException
	{
		save(srcPaths, output);
	}

	/**
	 * Reload the build path and discard any un-saved changes
	 */
	public void reload()
	{
		parse();
	}

	private synchronized void save(List<IContainer> srcPaths, IContainer output)
			throws CoreException
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<modelpath>\n");

		for (IContainer src : srcPaths)
		{
			if (src.getProjectRelativePath().toString().length() > 0)
			{
				sb.append("\t<modelpathentry kind=\"src\" path=\""
						+ src.getProjectRelativePath() + "\"/>\n");
			}

		}

		if (output != null
				&& output.getProjectRelativePath().toString().length() > 0)
		{
			sb.append("\t<modelpathentry kind=\"output\" path=\""
					+ output.getProjectRelativePath() + "\"/>\n");
		}
		sb.append("</modelpath>");

		PrintWriter out = null;
		try
		{
			FileWriter outFile = new FileWriter(this.modelPathFile);
			out = new PrintWriter(outFile);
			out.println(sb.toString());

		} catch (IOException e)
		{
			VdmCore.log("Faild to save .modelpath file", e);
		} finally
		{
			out.close();
		}
		ResourceManager.getInstance().syncBuildPath(vdmProject);

	}
}
