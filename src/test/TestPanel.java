/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author wb
 */
public class TestPanel extends JPanel{
    
    public TestPanel(){
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(600,230));
        System.out.println(System.getProperty("os.name"));
        GraphicsEnvironment env=GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] font=env.getAvailableFontFamilyNames();
        for(String f:font)
            System.out.println(f);
    }
    
    @Override
         public void paintComponent(Graphics g) {
             super.paintComponent(g);
             Graphics2D g2 = (Graphics2D) g;
//             paintRectangle(g);
//             paintLine(g);
//             g.setColor(Color.red);
//             paintOval(g);
//             paintRoundRectangle(g);
//             paintCircle(g);
//             paintArc(g);
             
             int w=this.getWidth();
             int h=this.getHeight();
             textBox(g2,w,h);
         }
    
    
         private void textBox(Graphics2D g, int width, int height){
             float p=25;
             
             Font font=new Font("Courier", Font.PLAIN, 14);
             g.setFont(font);
             float lh=g.getFontMetrics().getHeight();
             float x=p;
             float y=p+lh;          
             g.setPaint(Color.BLUE);
             g.drawString("rec: ", x, y);
             y+=lh*1.5f;
             g.setPaint(Color.RED);
             g.drawString("major: ", x, y);
             y+=lh*1.5f;
             g.setPaint(Color.GREEN);
             g.drawString("minor:", x, y);
             y+=lh*2.2f;
             g.setPaint(Color.gray);
             g.drawString("recombination fragments:", x, y);
             
         }
    
         private void paintBox(Graphics2D g, int width, int height){
             
             int p=50; //padding                       
             int sw=100; //string width
             int h=30; //height
             int sh=20; //string height
             int hs=10; //horizontal space
             int vs=20; //vertical space
             int w=width-sw-hs-2*p; //width
             
             
             g.drawString("Major", p,p+sh);
             g.drawString("Rec", p, p+h+vs+sh);
             g.drawString("Minor", p,p+2*(h+vs)+sh);
             
             g.setPaint(Color.RED);
             g.fillRect(p+sw+hs, p, w, h);            
             g.fillRect(p+sw+hs, p+h+vs, w, h);
             g.setPaint(Color.BLACK);           
             g.fillRect(p+sw+hs, p+2*(h+vs), w, h);
             
             int length=1000;
             ArrayList<int[]> blocks=new ArrayList();
             blocks.add(new int[]{230,760});
             blocks.add(new int[]{800,860});
             
             double unit=w*1.0/length;
             for(int[] block:blocks){
                 int bx=p+sw+hs+(int)(block[0]*unit);
                 int by=p+h+vs;
                 int bw=(int)((block[1]-block[0]+1)*unit);
                 int bh=h;
                 g.fillRect(bx,by,bw,bh);
             }
             
         }
    
    
    
         //绘制一条直线
         private void paintLine(Graphics g) {
             Graphics2D g2 = (Graphics2D) g;
             g2.setPaint(Color.gray);
             int x = 100;
             int y = 75;
             g2.draw(new Line2D.Double(x, y, 250, 195));
         }
         //绘制一个矩形边框，填充一个矩形
         private void paintRectangle(Graphics g) {
             Graphics2D g2d = (Graphics2D) g;
             g2d.setColor(new Color(212, 212, 212));
             g2d.draw(new Rectangle2D.Float(10, 15, 90, 60));
             g2d.setColor(new Color(31, 21, 1));
             g2d.fill(new Rectangle2D.Float(250, 195, 90, 60));
         }
         
         private void paintOval(Graphics g) {
             Graphics2D g2 = (Graphics2D)g;
             g2.draw(new Ellipse2D.Float(5, 15, 50, 75));   
         }
         //绘制一个圆形边角的矩形
         private void paintRoundRectangle(Graphics g) {
             Graphics2D g2 = (Graphics2D)g;
             RoundRectangle2D rr = new RoundRectangle2D.Float(100, 10, 80, 30, 15, 15);
             //获取当前的Color属性并保存到临时变量中，更新该属性，在使用完之后，
             //用该临时变量恢复到原有的Color属性，这种方式是比较通用的一种技巧。
             //在很多其他的类库中也会经常被用到。
             Color oldColor = g2.getColor();
             g2.setColor(Color.blue);
             g2.fill(rr);
             g2.setColor(oldColor);
         }
          //画圆
         private void paintCircle(Graphics g) {
             Graphics2D g2 = (Graphics2D)g;
             //没有提供drawCircle方法，通过将椭圆的width和heigh参数设置为等长，
             //即可获得一个圆形。
             g2.drawOval(5, 15, 75, 75);   
         }
         private void paintArc(Graphics g) {
             Graphics2D g2 = (Graphics2D) g;
             int x = 50;
             int y = 70;
             g2.setStroke(new BasicStroke(8.0f));
             //缺省填充为饼图
             g2.fillArc(x, y, 200, 200, 90, 135);
             //还可以通过Arc2D构造函数中的最后一个参数来定义不同的填充类型
             Color oldColor = g.getColor();
             g.setColor(Color.blue);
             g2.fill(new Arc2D.Double(200, 30, 200, 200, 90, 135,Arc2D.CHORD));
             g.setColor(oldColor);
         }
}
