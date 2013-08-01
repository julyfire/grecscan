/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.core;

/**
 *
 * @author wb
 */
public class Sequence {
    private int id;
    private String name;
    private String description;
    private char[] seq;
//    private double[] mfe;
    private int group;
    private boolean selected;
    private int length;
    
    public Sequence(String name, String seq){
        this.name=name;
        this.seq=seq.toCharArray();
    }
    
    public Sequence(int id, String name, String seq){
        this.id=id;
        this.name=name;
        this.seq=seq.toCharArray();
    }
    
    public char charAt(int index){
        return seq[index];
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the seq
     */
    public char[] getSeq() {
        return seq;
    }

    /**
     * @param seq the seq to set
     */
    public void setSeq(char[] seq) {
        this.seq = seq;
    }

//    /**
//     * @return the mfe
//     */
//    public double[] getMfe() {
//        return mfe;
//    }
//
//    /**
//     * @param mfe the mfe to set
//     */
//    public void setMfe(double[] mfe) {
//        this.mfe = mfe;
//    }

    /**
     * @return the group
     */
    public int getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * @return the index
     */
    public int getId() {
        return id;
    }

    /**
     * @param index the index to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return seq.length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
