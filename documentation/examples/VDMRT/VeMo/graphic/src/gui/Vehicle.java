package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Vehicle extends JLabel {
	private static final long serialVersionUID = -3979905387344521236L;
	
	AffineTransform at = new AffineTransform();
    Point gridCenter = new Point();
    Point position =  new Point();
    int circleSize;
    Color carColor;
    int resolutionFactor;
    Long vecID;
    transient Image messagein;
    transient Image messageout;
    
    boolean receivedData;
    int imageShownCounter;
    
    public enum CarColor {
        Blue, Red, Black, Grey
    }

    public Vehicle(long vehicleID,CarColor color) {

        vecID = vehicleID;

        Icon car;
        switch (color) {
            case Blue:
                carColor = Color.BLUE;
                car = new ImageIcon(getClass().getResource("/gui/resources/bluecar.png"));
                break;
            case Red:
                carColor = Color.RED;
                car = new ImageIcon(getClass().getResource("/gui/resources/redcar.png"));
                break;
            case Black:
                carColor = Color.BLACK;
                car = new ImageIcon(getClass().getResource("/gui/resources/blackcar.png"));
                break;
            case Grey:
                carColor = Color.GRAY;
                car = new ImageIcon(getClass().getResource("/gui/resources/greycar.png"));
                break;
            default:
                carColor = Color.RED;
                car = new ImageIcon(getClass().getResource("/gui/resources/redcar.png"));
                break;
        }
        
        this.setIcon(car);
        messagein = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/gui/resources/messagein.png"));
    }

    /**
     * @return the vechicleID
     */
    public Long getVechicleID() {
        return vecID;
    }

        /**
     * @return the carColor
     */
    public Color getCarColor() {
        return carColor;
    }

    public void SetDirection(int direction) {
        switch (direction) {
            case EAST:
                at.setToRotation(Math.toRadians(0));
                break;
            case WEST:
                at.setToRotation(Math.toRadians(180), 40, 40);
                break;
            case NORTH:
                at.setToRotation(Math.toRadians(270), 40, 40);
                break;
            case SOUTH:
                at.setToRotation(Math.toRadians(90), 40, 40);
                break;
        }
    }

    public void SetPosition(int x, int y) {
    	
        position.x = gridCenter.x + x * resolutionFactor;
        position.y =  gridCenter.y - y * resolutionFactor;
        this.setLocation(position.x  , position.y);
        repaint();
    }

     /**
     * @return the position converted to screen coordinates
     */
    public Point GetPosition() {
        return position;
    }

      /**
     * @return the range converted to screen coordinates
     */
    public int GetRange()
    {
        return circleSize;
    }
    
    public void SetReceivedData()
    {
    	receivedData = true;
    	imageShownCounter = 30;
    }

    public void GridSizeChanged() {
    	
        Dimension dim = this.getParent().getSize();
        //set coordinate origin as center screen. -30 is to center car image
        gridCenter.setLocation(dim.getWidth() / 2 - 30, dim.getHeight() / 2 - 30);

        //calc step in model to move on screen. Grid_size indicate one positive axis,
        //but screen has both positive and negative, thus times 2
        resolutionFactor = (int) (dim.getHeight() / (Model.GRID_SIZE * 2));

        //static step size, meaning resize gives more space
        //resolutionFactor = 10;

        circleSize = resolutionFactor * 2 * Model.VEHICLE_RANGE;
        
        this.setLocation(position.x  , position.y);
    }

    transient Graphics2D g2;
    @Override
    protected void paintComponent(Graphics g) {

        g2 = (Graphics2D) g;
        g2.transform(at);
        super.paintComponent(g);
        
        if(receivedData)
        {
        	g2.drawImage(messagein, 0, 0, null);
        	imageShownCounter--;
        	if(imageShownCounter <= 0)
        	{
        		receivedData = false;	
        	}
        }
    }
}
