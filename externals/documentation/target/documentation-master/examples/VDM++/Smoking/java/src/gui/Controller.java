package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Controller extends JFrame  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel buttonPanel = new JPanel();
    JButton addTobaccoButton;
    JButton addMatchButton;
    JButton addPaperButton;

    Model model;
    View view;
    public static SmokingControl smoke; 

    public Controller() {
    	// Allow Overture to do a controlled shutdown 
    	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	this.model = new Model();
    	
    	//add buttons
    	addTobaccoButton = new JButton("Add Tobacco");
    	addPaperButton = new JButton("Add Paper");
    	addMatchButton = new JButton("Add Match");
    	
    	setSize(350,150);  
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
      layOutComponents();
      // Connect model and view
      model.addObserver(view);
   }

    private void attachListenersToComponents() {

    	addTobaccoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	smoke.AddTobacco();
            }
        });
    		
    	addPaperButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	smoke.AddPaper();
            }
        });	
    	
    	addMatchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	smoke.AddMatch();
            }
        });	
    }
    
    private void layOutComponents() {
        setLayout(new BorderLayout());
        this.add(BorderLayout.SOUTH, buttonPanel);
        buttonPanel.add(addTobaccoButton);
        buttonPanel.add(addPaperButton);
        buttonPanel.add(addMatchButton);
    	this.add(BorderLayout.CENTER, view);
    	
		this.setVisible(true);
    }

    
    public void DisableButtons()
    { 	
    	addTobaccoButton.setEnabled(false);
    	addPaperButton.setEnabled(false);
    	addMatchButton.setEnabled(false);
    }
    
    public void EnableButtons()
    {
    	addTobaccoButton.setEnabled(true);
    	addPaperButton.setEnabled(true);
    	addMatchButton.setEnabled(true);
    }

    /**
     * @return the model
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Notify the smoke controller that the JFrame is going down
     */
    @Override
    public void dispose()
    {
    	smoke.finish();
    }
}
