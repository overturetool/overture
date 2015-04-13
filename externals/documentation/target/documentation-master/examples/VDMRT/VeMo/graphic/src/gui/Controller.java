package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class Controller extends JFrame  {
 
	private static final long serialVersionUID = 1L;
	JPanel buttonPanel = new JPanel();

    JButton toggleRangeButton = new JButton("Range");
    JButton toggleConnectionsButton = new JButton("Connections");

    private Model model;
    View view;

    public Controller() {
        init();
        
        setSize(1000,800);  
        setVisible(true);  
    }

    public void init() {
      model = new Model();
        try {
            view = new View(getModel());
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      layOutComponents();
      attachListenersToComponents();
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // Connect model and view
      model.addObserver(view);
   }

    private void attachListenersToComponents() {

          
        toggleRangeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getModel().toggleRange();
            }
        });

        toggleConnectionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getModel().toggleConnections();
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
 
              buttonPanel.add(toggleRangeButton);
              buttonPanel.add(toggleConnectionsButton);

          this.add(BorderLayout.CENTER, view);
  
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }
}
