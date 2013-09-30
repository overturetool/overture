package gui;


import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

public class Controller extends JFrame  {
 
	private static final long serialVersionUID = 1L;
    private Model model;
    View view;

    public Controller(int sideCount) {
    	initialise(sideCount);
        
        setSize(700,700);  
        setVisible(true);  
    }

    public void initialise(int sideCount) {
      model = new Model();
        try {
            view = new View(getModel(),sideCount);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      setLayout(new BorderLayout());
      this.add(BorderLayout.CENTER, view);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
      //Connect model and view
      model.addObserver(view);
   }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }
}
