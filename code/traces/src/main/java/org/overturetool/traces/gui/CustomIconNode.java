package org.overturetool.traces.gui;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
* Custom Node
* @author usman
*/
public class CustomIconNode extends TraceNode{
private Icon img=null;
 public CustomIconNode(String text,Icon img) {
  super(text);
  this.img = img;
 }

 public javax.swing.Icon getIcon() {
  return img;//new javax.swing.ImageIcon("resources/ok.png");//getClass().getResource("/icons/myicon16.png"));
 }
 
 public void SetIcon(Icon img)
 {
	 this.img=img;
 }
}