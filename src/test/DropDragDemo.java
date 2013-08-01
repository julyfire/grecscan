/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author zhang xiaojin
 * 演示Swing文件拖拽
 */
public class DropDragDemo {
    public static void main(String[] args){
        JFrame frame = new JFrame("文件拖拽Demo");
        frame.setSize(500, 400);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("文件拖拽演示"));

        JTextArea textArea = new DropDragSupportTextArea();
        JScrollPane jsp = new JScrollPane();
        jsp.setViewportView(textArea);
        textArea.setColumns(40);
        textArea.setRows(20);
        
        panel.add(jsp);
        frame.add(panel);
        frame.setVisible(true);
    }
}
