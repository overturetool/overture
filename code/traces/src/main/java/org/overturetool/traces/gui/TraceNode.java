package org.overturetool.traces.gui;
import javax.swing.tree.DefaultMutableTreeNode;

/**
* My Node
* @author kela
*/
public abstract class TraceNode extends DefaultMutableTreeNode{

 public TraceNode (String text) {
  super(text);
 }

 public abstract javax.swing.Icon getIcon();
}