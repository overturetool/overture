package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class Controller extends JFrame  {

	private static final long serialVersionUID = 1L;
	JPanel buttonPanel = new JPanel();
    JButton decreaseInflowBtn;
    JButton increaseInflowBtn;
    JButton prevPlan;
    JButton loadAndsimulatePlan;
    JButton nextPlan;

    Model model;
    View view;
    public static BuslinesControl buslinesControl; 

    public Controller() {
    	
		try {
			this.model = new Model();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "DB Driver Error", 0);
			dispose();
			System.exit(0); 
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "DB Error", 0);
			dispose();
			System.exit(0); 
		}
    	
    	increaseInflowBtn = new JButton("Increase inflow");
    	decreaseInflowBtn = new JButton("Decrease inflow");
    	prevPlan = new JButton("Previous Plan");
    	loadAndsimulatePlan = new JButton("Load");
    	nextPlan = new JButton("Next Plan");
    	
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

    public void planUpdate() {
		buslinesControl.NewCityPlan();
		for (Waypoint wp : model.getWaypoints()) {
			if(wp.IsStop())
				buslinesControl.AddBusstop(wp.toString());
			else
				buslinesControl.AddWaypoint(wp.toString());
		}
		
		for(Road r : model.getRoads()){
			buslinesControl.AddRoad(r.getWp1().toString(), r.getWp2().toString(), r.toString(), r.getLength(), r.isHighspeed());
		}
	}
    
    private void attachListenersToComponents() {
    		
    	increaseInflowBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	buslinesControl.IncreaseInflow();
            }
        });	
    	
    	decreaseInflowBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	buslinesControl.DecreaseInflow();
            }
        });	
    	
    	prevPlan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.prevPlan();
					planUpdate();
				} catch (SQLException exp) {
					exp.printStackTrace();
					JOptionPane.showMessageDialog(null, exp.getMessage(), "DB Error", 0);
					dispose();
					System.exit(0); 
				}
			}
		});
    	
		nextPlan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					model.nextPlan();
					planUpdate();
				} catch (SQLException exp) {
					exp.printStackTrace();
					JOptionPane.showMessageDialog(null, exp.getMessage(), "DB Error", 0);
					dispose();
					System.exit(0); 
				}
			}
		});
		
		loadAndsimulatePlan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(loadAndsimulatePlan.getText().equals("Load")){
					loadAndsimulatePlan.setText("Simulate");
					prevPlan.setVisible(true);
					nextPlan.setVisible(true);
					try {
					model.loadPlan();
					planUpdate();
					} catch (SQLException exp) {
						exp.printStackTrace();
						JOptionPane.showMessageDialog(null, exp.getMessage(), "DB Error", 0);
						dispose();
						System.exit(0); 
					}
				}
				else {
					prevPlan.setVisible(false);
					nextPlan.setVisible(false);
					loadAndsimulatePlan.setEnabled(false);
					loadAndsimulatePlan.setText("Simulating ...");
			        buttonPanel.add(decreaseInflowBtn,0);
			        buttonPanel.add(increaseInflowBtn);
			        
					//add buses
					for(String stm : model.getBusStms()){
						buslinesControl.AddBus(stm);
					}
			        
			        buslinesControl.StartSimulation();
				}
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
        prevPlan.setVisible(false);
		nextPlan.setVisible(false);
        buttonPanel.add(prevPlan);
        buttonPanel.add(loadAndsimulatePlan);
        buttonPanel.add(nextPlan);
    	this.add(BorderLayout.CENTER, view);
    	
		this.setVisible(true);
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }
}
