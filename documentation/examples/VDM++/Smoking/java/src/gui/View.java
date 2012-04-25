package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

class View extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	
    Model model;
    
    transient ImageIcon smoking;
    transient ImageIcon notSmoking;
    
    transient List<JLabel> smokers = new LinkedList<JLabel>();
    transient JLabel match = new JLabel(),  paper = new JLabel(), tobacco = new JLabel(); 
     
    View(Model model) throws IOException {
    	
    	super();
        this.model = model;
        this.setBackground(Color.WHITE);

        smokers.add(new JLabel());
        smokers.add(new JLabel());
        smokers.add(new JLabel());
        
        notSmoking= new ImageIcon(this.getClass().getResource("/gui/resources/nsSmiley.gif"));
    	smoking = new ImageIcon(this.getClass().getResource("/gui/resources/smokingSmiley.gif"));
	
        setLayout(new GridLayout(2, 3));
    	
        smokers.get(0).setText("Smoker 1");
        smokers.get(0).setIcon(notSmoking);
		add(smokers.get(0));
		
		smokers.get(1).setText("Smoker 2");
		smokers.get(1).setIcon(notSmoking);
		add(smokers.get(1));
		
		smokers.get(2).setText("Smoker 3");
		smokers.get(2).setIcon(notSmoking);
		add(smokers.get(2));
		
		tobacco.setIcon(new ImageIcon(this.getClass().getResource("/gui/resources/tobacco.gif")));
		add(tobacco);
		tobacco.setVisible(false);
		
		paper.setIcon(new ImageIcon(this.getClass().getResource("/gui/resources/paper.jpg")));
		add(paper);
		paper.setVisible(false);
		
		match.setIcon(new ImageIcon(this.getClass().getResource("/gui/resources/fire.gif")));
		add(match);
		match.setVisible(false);
    }
    
    public void update(Observable obs, Object arg) {
    
		if(arg == null) return;
		
        if(arg == "paperAdded"){
        	paper.setVisible(true);
        }
        else if(arg == "tobaccoAdded"){
        	tobacco.setVisible(true);
        } 
        else if(arg == "matchAdded"){
        	match.setVisible(true);
        } 
        else if(arg == "tableCleared"){
        	match.setVisible(false);
        	tobacco.setVisible(false);
        	paper.setVisible(false);
        } 
        else if(arg == "smoking"){
        	smokers.get(model.getSmoker() -1).setIcon(smoking);
        }
        else if(arg == "finishedSmoking"){
        	smokers.get(model.getSmoker() -1).setIcon(notSmoking);
		}
    }
}
    