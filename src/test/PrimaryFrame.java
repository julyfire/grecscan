/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author wb
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PrimaryFrame extends JFrame {
    private SecondFrame secondFrame;//假设SecondFrame一直跟在PrimaryFrame的下边

    public PrimaryFrame() {
	init();
    }

    private void init() {
	setSize(340, 170);
	setTitle("PrimaryFrame");
	setDefaultCloseOperation(3);
	secondFrame = new SecondFrame(this);	
	setVisible(true);	
	addComponentListener(new ComponentAdapter(){
	    public void componentMoved(ComponentEvent e){
                if(Math.abs(getX()+getWidth()-secondFrame.getX())<50)
	        secondFrame.setLocation(getX()+getWidth(),getY());//假设SecondFrame一直跟在PrimaryFrame的下边
	    }
	    public void componentResized(ComponentEvent e) {
		secondFrame.setSize(getWidth(),secondFrame.getHeight());		
	    }
	});	

    }

    public static void main(String[] args) {	
	new PrimaryFrame();
    }

}

class SecondFrame extends JFrame {//假设它一直跟在PrimaryFrame的下边
    PrimaryFrame primaryFrame ;
    public SecondFrame(PrimaryFrame primaryFrame){
	this.primaryFrame = primaryFrame;
	init();
	
    }
    private void init(){
	setTitle("SecondFrame");
	setSize(340, 170);
	setLocation(0, 170);
	setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	setVisible(true);
	addComponentListener(new ComponentAdapter(){
	    public void componentResized(ComponentEvent e) {
		primaryFrame.setSize(getWidth(),primaryFrame.getHeight());
		
	    }
	    @Override public void componentMoved(ComponentEvent e){
	        primaryFrame.setLocation(getX(),getY()-primaryFrame.getHeight());//假设SecondFrame一直跟在PrimaryFrame的下边
	    }
	});
    }
}


