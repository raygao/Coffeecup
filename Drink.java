/*
 * Drink.java 1.1 April 12, 2003
 * see copyright.txt for copyright issues.
 *
 */

//iteration #1
//Drink calls Brew. Drink execute the application.

package coffeecup4j; 

import java.util.*;
import java.net.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.io.*;

public class Drink {
	private Brew brew;
	private String classpath;
	private String arguments;
	private String logfile;
	private Properties properties;
	private String target;
	private Class targetClass;
	private Method mainMethod;
	private URLClassLoader loader;
	private URL [] urls;
	
	static String _osName;
	static String _pathSeparator;
	static String _fileSeparator;
	static String _lineSeparator;
	static String _osArch;
	
    private Log log;

	public static void main(String argv[]) {
        if (argv.length < 1) {
            System.err.println("Usage: Drink <filename.xml>");
            System.exit(1);	
		}
        
		Drink drink = new Drink();
		drink.exec(argv);	
		
	}

	public void exec (String []  _argv) {
		initialize();
		
		Brew brew = new Brew();
		brew.exec (_argv);
		
		this.parseLogfile(brew);
		this.parseArguments(brew);
		this.parseClasspath(brew);
		this.parseProperties(brew);
		this.parseTarget(brew);
		this.resolveMainMethod(urls);
		this.launch();

		log.record("***Done executing Drink.java***");
		log.close();
	}
	
	/****************************
	* 		Parse methods		*
	****************************/
	
	private void parseArguments (Brew b) {
		arguments = new String (b.getArguments());
	}
	
	private void parseClasspath (Brew b) {
		classpath = System.getProperty("java.class.path") + _pathSeparator + new String (b.getClasspath());
		try {
			urls = new java.net.URL [] {new URL ("file:/" + classpath)};
			// or use following
			//urls = new java.net.URL[50];
			//urls [0] = new URL ("file:/" + classpath);
		} 
		catch (MalformedURLException mue) {
			log.record("*** URL Malformed Exception ***");
			mue.printStackTrace();
		}
		log.record("Received classpath -> " + classpath + " from brew.java");
	}
	
	private void parseLogfile (Brew b) {
		logfile = new String (b.getLogfile());
		log = new Log(logfile);
		log.record("--- Logfile from executing " + this.getClass().getName() + ".class ---");
	}
	
	private void parseTarget (Brew b) {
		target = new String (b.getTarget());
	}	
		private void parseProperties (Brew b) {
			// Properties extends java.util.Hashtable class, which has keyset() method and can get iterator.
			Set propertyNames = b.getProperties().keySet();
			Iterator itr = propertyNames.iterator();
			while (itr.hasNext()) {
				String str = (String) itr.next();
				System.setProperty (str, b.getProperties().getProperty(str));
			}
	}
	
	private Method resolveMainMethod (URL [] _urls) {
		loader = new java.net.URLClassLoader(_urls); 
		try {
			targetClass = loader.loadClass(this.target);
			log.record("Loaded " + this.target + ".class");
		}
		catch (ClassNotFoundException cnfe) {
			log.record("*** Could not find " + target + ".class ***");
		}
		try {
			Class [] parameterType = new Class [] {String [].class};
			mainMethod = targetClass.getMethod("main", parameterType);
			//or alternatively, use mainMethod = targetClass.getMethod("main", new Class[] { String.class});
			int mask = Modifier.PUBLIC ^ Modifier.STATIC;
			if ((mainMethod.getModifiers() & mask ) != mask) 
				throw new NoSuchMethodException ("Could not find public static void main(String [] args) method in " + target + ".class");
		}
		catch (NoSuchMethodException nsme) {
			log.record("*** Could Not find main method for " + target + ".***");
			nsme.printStackTrace();
		}
		catch (SecurityException se) {
			log.record("*** Security Exception, check Security manager and policy file. ***");
			se.printStackTrace();
		}
		return mainMethod;
	}

	
	private void launch () {
		log.record("Launching " + target + ".class");
		log.record("      with following arguments -> " + arguments);
		log.record("      with classpath -> " + urls[0].toString());
		log.record("      and properties:");
		System.getProperties().list(log.p);
		System.out.println("--- Result from executing " + target + ".class ---");
		try {
			Object _obj = targetClass.newInstance();
			mainMethod.invoke(_obj, new Object[] { new String [] {this.arguments}} );
//Alternatively, use following three lines.
//			String [] _args = {this.arguments};
//			Object []  _argsObject = new Object [] {_args};
//			mainMethod.invoke(_obj, _argsObject);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****************************
	* Obtain general system info*
	****************************/
	
	public void initialize () {
		_pathSeparator = System.getProperty("path.separator");
		_fileSeparator = System.getProperty("file.separator");
		_lineSeparator = System.getProperty("line.separator");
		_osName = System.getProperty ("os.name");
		_osArch = System.getProperty ("os.arch");
	}
}