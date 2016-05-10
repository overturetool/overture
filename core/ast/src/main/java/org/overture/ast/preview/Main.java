/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.preview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.INode;
import org.overture.ast.preview.GraphViz.GraphVizException;

public class Main
{

	public static File dot = null;

	public static Set<String> filterClassNames = new HashSet<>();

	// digraph ast
	// {
	// node [shape=record];
	// n0 [label="{<f0>ABinaryExp}"];
	// n1 [label="{<f0>ABoolSingleExp}"];
	// n0 -> n1
	// n2 [label="{<f0>AOrBinop}"];
	// n0 -> n2
	// n3 [label="{<f0>AUnaryExp}"];
	// n0 -> n3
	// n4 [label="{<f0>ACeilUnop}"];
	// n3 -> n4
	// n5 [label="{<f0>AIdentifierSingleExp}"];
	// n3 -> n5
	// n6 [label="{<f0>ACtDomain}"];
	// n5 -> n6
	// n7 [label="{<f0>ARealType}"];
	// n5 -> n7
	//
	// }

	/**
	 * @param args
	 * @throws GraphVizException
	 */
	public static void main(String[] args) throws GraphVizException
	{
		// AUnaryExp un = new AUnaryExp(new ACeilUnop(), new AIdentifierSingleExp(
		// new ACtDomain(), new ARealType(), "somename"));
		// PExp exp = new ABinaryExp(new ABoolSingleExp(true), new AOrBinop(), un);

		LexLocation location = new LexLocation(new File("fileA"), "A", 1, 1, 2, 2, 1, 2);
		PExp exp = AstFactory.newAAndBooleanBinaryExp(AstFactory.newAVariableExp(new LexNameToken("A", "kk", location)), new LexToken(location, VDMToken.AND), AstFactory.newABooleanConstExp(new LexBooleanToken(true, location)));

		show(exp, true);
	}

	public static void makeImage(INode node, String type, File output)
			throws GraphVizException
	{
		DotGraphVisitor visitor = new DotGraphVisitor(filterClassNames);
		try
		{
			node.apply(visitor, null);
		} catch (Throwable e)
		{
			// Ignore
		}
		GraphViz gv = dot == null ? new GraphViz() : new GraphViz(dot);
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), output);
	}

	public static void makeImage(List<? extends INode> nodes, String type,
			File output) throws GraphVizException
	{
		DotGraphVisitor visitor = new DotGraphVisitor(filterClassNames);
		try
		{
			for (INode node : nodes)
			{
				node.apply(visitor, null);
			}

		} catch (Throwable e)
		{
			// Ignore
		}
		GraphViz gv = dot == null ? new GraphViz() : new GraphViz(dot);
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), output);
	}

	public static void show(INode node, final boolean exitOnClose)
			throws GraphVizException
	{
		DotGraphVisitor visitor = new DotGraphVisitor(filterClassNames);
		try
		{
			node.apply(visitor, null);
		} catch (Throwable e)
		{
			// Ignore
		}
		GraphViz gv = dot == null ? new GraphViz() : new GraphViz(dot);
		String type = "png";
		final File out = new File("out." + type); // out.gif in this example
		final File out1 = new File("out1." + type);
		System.out.println(visitor.getResultString());
		System.out.println(out.getAbsolutePath());
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), out);
		type = "svg";
		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), out1);

		JFrame frame = new JFrame("Display image");
		Panel panel = new ShowImage(out);
		JScrollPane scroller = new JScrollPane(panel);
		scroller.setMaximumSize(panel.getPreferredSize());
		scroller.setAutoscrolls(true);
		Dimension dim = panel.getPreferredSize();
		dim.setSize(dim.width + 20, dim.height + 20);
		scroller.setPreferredSize(dim);
		frame.getContentPane().add(scroller, BorderLayout.CENTER);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowListener()
		{

			public void windowOpened(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowDeactivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
				out.deleteOnExit();
				out1.deleteOnExit();
				if (exitOnClose)
				{
					System.exit(0);
				}
			}

			public void windowClosed(WindowEvent arg0)
			{
			}

			public void windowActivated(WindowEvent arg0)
			{
			}
		});
		frame.setVisible(true);

	}

	public static class ShowImage extends Panel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7334523949913510202L;
		BufferedImage image;

		public ShowImage(File input)
		{
			try
			{
				image = ImageIO.read(input);
			} catch (IOException ie)
			{
				System.out.println("Error:" + ie.getMessage());
			}
		}

		public void paint(Graphics g)
		{
			g.drawImage(image, 0, 0, null);// , getWidth(), getHeight(), null);
		}

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(image.getWidth(), image.getHeight());
		}
	}

}
