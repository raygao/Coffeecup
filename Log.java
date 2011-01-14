
/*
 * Log.java 1.1 April 07, 2003
 * see copyright.txt for copyright issues.
 *
 */
 
//iteration #1
//Log class called by both Drink and Brew class

package coffeecup4j;

import java.io.*;
import java.util.Date;

//Cannot directly extend PrintWriter class, Printwriter has no default constructor.

public class Log {
	protected PrintWriter p;
	public Log (String s) {
		try {
			p = new PrintWriter ((OutputStream) new FileOutputStream(s, true), true);
			record("*******************************************");
			record(new Date(System.currentTimeMillis() ).toString() );
			record("*******************************************");
		}
		catch (java.io.FileNotFoundException fnfe) {
			System.out.println("Unable to create Log file " + s);
			fnfe.printStackTrace();
		}
	}
	public void record (String message) {
		p.println(message);

	}
	public void close() {
		p.close();
	}

}