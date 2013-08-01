/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wb
 */
public class StringMatcher {
    private static String regSL="(\\.*\\([\\.\\(]*\\()(\\.+)(\\)[\\.\\)]*\\)\\.*)";
    private static Pattern pattern=Pattern.compile(regSL);

    
    public static void main(String[] args){
//        String str="......................................................(....).";
        String st2="AAACCAACAAA   UCUAA ACCUUAACAU  UUCAAUAUAAUAUCA   ACAAAA   AGUAAUGCGGAAGA";

        String st1="AAACCAA CAAA   UCUAA ACCUUA ACAUUUCA AUAUAAUAU CAACAAA AAGUAA   UGCGG  AAGA";
//    
//        Matcher m=pattern.matcher(str);
//
//        if(m.find()){
//            System.out.println("matched!");
//        }
//        
//        if(pattern.matcher(str).matches() ==false) System.out.println("not match!");
//        while(m.find()){
//            
//        }
        
        StringMatcher sm=new StringMatcher();
        
        long start,end,time;
        
        start = System.currentTimeMillis();
        System.out.println(sm.internalLoopSize(st1, st2));
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println(time/1000.0);
        
        start = System.currentTimeMillis();
        System.out.println(sm.internalLoopSize2(st1, st2));
        end = System.currentTimeMillis();
        time = end - start;
        System.out.println(time/1000.0);

        
    }
    
    public int internalLoopSize2(String line1, String line4) {
        String l1=line1.replaceAll("^\\w+\\s+","").replaceAll("\\s+\\w+$", " ");
        String l4=line4.replaceAll("^\\w+\\s+"," ").replaceAll("\\s+\\w+$", "");
        String[] s = (l1 + l4).replaceAll("\\s+", " ").split(" ");
        int loop = 0;
        for (int i = 0; i < s.length; i++) {
            loop = Math.max(loop, s[i].length());
        }
        return loop;
    }

    public int internalLoopSize(String line1, String line4){
        return Math.max(maxGapFreeInternalSubString(line1), 
                maxGapFreeInternalSubString(line4));
    }
    private int maxGapFreeInternalSubString(String str){
        char[] c1=str.toCharArray();
        int n=c1.length;
        int b=0,e=0;
        for(int i=0;i<n;i++){
            if(c1[i]==' '){
                b=i;
                break;
            }
        }
        for(int i=n-1;i>0;i--){
            if(c1[i]==' '){
                e=i;
                break;
            }
        }
        int max=0;
        int s=0;
        for(int i=b;i<e;i++){
            if(c1[i]==' ' && c1[i+1]!=' '){
                s=1;
            }
            else if(c1[i]!=' ' && c1[i+1]!=' '){
                s+=1;
            }
            else if(c1[i]!=' ' && c1[i+1]==' '){
                if(s>max){
                    max=s;
                }
            }
        }
        return max;
    }
    
}
