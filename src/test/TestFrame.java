/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
public class TestFrame extends JFrame{
    
    public TestFrame(){
        JPanel wrap=new JPanel();
        wrap.setLayout(new BorderLayout());
        wrap.add(new SBDemo2());
        this.add(wrap);
//        this.add(new TestPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);//in the center of screen
//        displayAtCenter(this);
        this.setVisible(true);
    }
    
    public static void main(String[] args){
        TestFrame tf=new TestFrame();
        int n=8;
        int a=0;
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(j==i) continue;
                for(int k=j+1;k<n;k++){
                    if(k==i) continue;
                    a+=1;
                    System.out.println(a);
                }
            }
        }
                   
    }
    
}
