package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class Controller extends JFrame  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel buttonPanel = new JPanel();
    JButton decreaseInflowBtn;
    JButton increaseInflowBtn;

    Model model;
    View view;
    public static BuslinesControl buslinesControl; 

    public Controller() {
    	
    	this.model = new Model();
    	
    	increaseInflowBtn = new JButton("Increase inflow");
    	decreaseInflowBtn = new JButton("Decrease inflow");
    	
    	setSize(1280, 720);  
        init();
        setVisible(true);  
    }

    public void init() {
        try {
            view = new View(model);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      attachListenersToComponents();
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      layOutComponents();
      // Connect model and view
      model.addObserver(view);
   }

    private void attachListenersToComponents() {
    		
    	increaseInflowBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	System.out.println("FOOOOOO");
            	//buslinesControl.IncreaseInflow();
            }
        });	
    	
    	decreaseInflowBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	//buslinesControl.DecreaseInflow();
            }
        });	
    	
    	//resize event
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				getModel().setLimits(view.getWidth(), view.getHeight());
			}
		});
          
		// resizing event
		getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				getModel().setLimits(view.getWidth(), view.getHeight());
			}
		});
    }
    
    private void layOutComponents() {
        setLayout(new BorderLayout());
        this.add(BorderLayout.SOUTH, buttonPanel);
        buttonPanel.add(increaseInflowBtn);
        buttonPanel.add(decreaseInflowBtn);
    	this.add(BorderLayout.CENTER, view);
    	//DisableButtons();
    	
		this.setVisible(true);
    }

    
    public void DisableButtons()
    { 	
    	increaseInflowBtn.setEnabled(false);
    	decreaseInflowBtn.setEnabled(false);
    }
    
    public void EnableButtons()
    {
    	increaseInflowBtn.setEnabled(true);
    	decreaseInflowBtn.setEnabled(true);
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }
}
