/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.text.DefaultStyledDocument;

/**
 *
 * @author wb
 */
class TextPaneListRenderer extends JTextPane implements ListCellRenderer {

      //implements the list cell renderer
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            //set model of the JTextPane
            this.setDocument((DefaultStyledDocument)value);
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }   
//            this.setEnabled(list.isEnabled());
//            this.setEditable(true);
            this.setOpaque(true);
            
            return this;
        }
    
}
