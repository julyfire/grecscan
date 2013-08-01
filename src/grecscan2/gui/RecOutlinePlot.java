/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
public class RecOutlinePlot extends JPanel{
    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        paintBox(g2);
    }
    
    private void paintBox(Graphics2D g){
                          
        int x=50;
        int y=50;
        int w=500; //width
        int sw=100; //string width
        int h=30; //height
        int sh=20; //string height
        int hs=10; //horizontal space
        int vs=20; //vertical space
        
        g.drawString("Major", x,y+sh);
        g.drawString("Rec", x, y+h+vs+sh);
        g.drawString("Minor", x,y+2*(h+vs)+sh);
             
        g.setPaint(new Color(229,51,25));
        g.fillRect(x+sw+hs, y, w, h);            
        g.fillRect(x+sw+hs, y+h+vs, w, h);
        g.setPaint(Color.BLACK);           
        g.fillRect(x+sw+hs, y+2*(h+vs), w, h);
             
        int length=1000;
        ArrayList<int[]> blocks=new ArrayList();
        blocks.add(new int[]{230,760});
        blocks.add(new int[]{800,860});
             
        double unit=w*1.0/length;
        for(int[] block:blocks){
            int bx=x+sw+hs+(int)(block[0]*unit);
            int by=y+h+vs;
            int bw=(int)((block[1]-block[0]+1)*unit);
            int bh=h;
            g.fillRect(bx,by,bw,bh);
        }
             
    }
}
