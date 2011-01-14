
/*
 * Hello.java 1.1 April 07, 2003
 * see copyright.txt for copyright issues.
 *
 */
 
//iteration #1

package coffeecup4j;

public class Hello {
	public static void main (String [] args ) {
		System.out.println("Hello my friend. Below are your inputs");
		for (int i=0; i < args.length; i++) {
			System.out.println(args[i]);
		}
	}
}