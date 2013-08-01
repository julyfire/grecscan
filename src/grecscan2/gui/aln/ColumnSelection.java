/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui.aln;

import java.util.LinkedList;

/**
 *
 * @author wb
 */
class ColumnSelection {
    LinkedList selected = new LinkedList();
    
    public void add(int col){
        Integer column = new Integer(col);
        if(selected.contains(column)) return;
        selected.add(col);
    }
    
    public void clear(){
        selected.clear();
    }
    
    public int size(){
        return selected.size();
    }
    
    public LinkedList getSelected(){
        return selected;
    }
    
    public void remove(int col){
        Integer column=new Integer(col);
        if(selected.contains(column))
            selected.remove(column);
    }
    
    public int columnAt(int i){
        return (Integer) selected.get(i);
    }
    
    /**
    * rightmost selected column
    * 
    * @return rightmost column in alignment that is selected
    */
    public int getMax(){
        int max = -1;

        for (int i = 0; i < selected.size(); i++){
            if (columnAt(i) > max){
            max = columnAt(i);
        }
        }

        return max;
    }

    /**
    * Leftmost column in selection
    * 
    * @return column index of leftmost column in selection
    */
    public int getMin(){
        int min = 1000000000;

        for (int i = 0; i < selected.size(); i++){
            if (columnAt(i) < min){
                min = columnAt(i);
            }
        }

        return min;
    }
}
