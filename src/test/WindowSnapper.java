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
public class WindowSnapper extends ComponentAdapter {

	private boolean locked = false;
	private int snap_distance = 50;

	public void componentMoved(ComponentEvent evt) {
		if(locked) return;
		Dimension size = Toolkit.getDefaultToolkit( ).getScreenSize( );
		int nx = evt.getComponent( ).getX( );
		int ny = evt.getComponent( ).getY( );
		// top
		if(ny < 0+snap_distance) {
			ny = 0;
		}
		// left
		if(nx < 0+snap_distance) {
			nx = 0;
		}
		// right
		if(nx > size.getWidth( ) - evt.getComponent( ).getWidth( ) -
				snap_distance) {
			nx = (int)size.getWidth( )-evt.getComponent( ).getWidth( );
		}
		// bottom
		if(ny > size.getHeight( ) - evt.getComponent( ).getHeight( ) -
				snap_distance) {
			ny = (int)size.getHeight( )-evt.getComponent( ).getHeight( );
		}
		
		// make sure we don't get into a recursive loop when the
		// set location generates more events
locked = true;
	    evt.getComponent( ).setLocation(nx,ny);
		locked = false;
	}


       public static void main(String[] args) {
           JFrame frame = new JFrame("Hack #33: Window Snapping");
           JLabel label = new JLabel(
              "Move this window's titlebar to demonstrate screen edge snapping.");

             frame.getContentPane( ).add(label);  
             frame.pack( );
 
             frame.addComponentListener(new WindowSnapper( ));
             frame.setVisible(true);
       }
}

