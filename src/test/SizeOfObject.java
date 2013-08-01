/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author wb
 */
public class SizeOfObject extends SizeOf {

	@Override
	protected Object newInstance() {
		return new Object();
	}

	public static void main(String[] args) throws Exception {
		SizeOf sizeOf = new SizeOfObject();
		System.out.println("memory:" + sizeOf.size() + "b");
	}
}