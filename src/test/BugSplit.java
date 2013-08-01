/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.*;	//Container, Dimension, Color, FlowLayout
import java.awt.event.*;
import javax.swing.*;   //JFrame, JPanel, JScrollPane, JSplitPane, BoxLayout

public class BugSplit extends JFrame
{
   public BugSplit()//Constructor
   {
	super("Bug Split Example");
	Container content = getContentPane();
	content.setLayout(new BoxLayout(content, 1));

   	Dimension hd = new Dimension(200, 40);   //The header size
   	Dimension pd = new Dimension(300, 300);  //The panels' size
   	Dimension sd = new Dimension(200, 200); //The split pane's size

	JPanel Header = new JPanel();
	Header.setPreferredSize(hd);
	Header.setBackground(Color.blue); //To differentiate it.
        Header.setAlignmentX(Component.LEFT_ALIGNMENT);

	//Make some panels to scroll
   	JPanel PRed = new JPanel(), PGreen = new JPanel();
	PRed.  setBackground(Color.red);   PRed.  setPreferredSize(pd);
	PGreen.setBackground(Color.green); PGreen.setPreferredSize(pd);

	//create JSplitPane which contains scrolling panels
   	JScrollPane Top = new JScrollPane(PRed), Bottom = new JScrollPane(PGreen);
   	JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Top, Bottom);
	jsp.setPreferredSize(sd);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);

	//Now add the header, and the split pane.  (Order doesn't matter)
	//Splitpane is shown in diminished size.
	content.add(Header);
	content.add(jsp);
                           addWindowListener(new WindowAdapter() {
                             public void windowClosing(WindowEvent e) {
	     System.exit(0);
	  }
	});
	setSize(500,400);
	setVisible(true);
   }

   public static void main(String[] s)
   { new BugSplit();}
}