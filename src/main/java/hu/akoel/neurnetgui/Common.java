package hu.akoel.neurnetgui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Common {
	private static String DEFAULT_LANGUAGE = "en";
	private static String DEFAULT_COUNTRY = "us";
	private static String LANGUAGE_BASENAME = "language";
	private static String CONFIG_FILENAME = "settings/config.properties";
	
	private static Locale currentLocale = new Locale(DEFAULT_LANGUAGE, DEFAULT_COUNTRY);
	private static ResourceBundle messages = ResourceBundle.getBundle( LANGUAGE_BASENAME, currentLocale );
	
/*	public static String getDecimalFormat( double value, int extraZeros ){		
		return 
				"#" + 
				(new BigDecimal(value)).toPlainString().replaceFirst("[1-9][0-9]+$", "") + 
				new String(new char[extraZeros + 1]).replace('\0', '0');
	}
*/	
	public static String getDecimalFormat( String pattern, int extraZeros ){
		//"#0.00" + new String(new char[pattern.length()]).replace('\0', '0');
		return "#" + pattern.replaceAll("\\d", "0") + new String(new char[extraZeros]).replace('\0', '0');		
	}
	
	public static String getFormattedDecimal( double value, String format ){
		NumberFormat formatter = new DecimalFormat( format );     
		return formatter.format( value );
	}
	
	public static void loadSettings(){
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(CONFIG_FILENAME);
			prop.load(input);

			// get the property value
			changeLocale( prop.getProperty("language"),	prop.getProperty("country") );

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
	
/*
Properties prop = new Properties();
	OutputStream output = null;

	try {

		output = new FileOutputStream("config.properties");

		// set the properties value
		prop.setProperty("database", "localhost");
		prop.setProperty("dbuser", "mkyong");
		prop.setProperty("dbpassword", "password");

		// save properties to project root folder
		prop.store(output, null);

	} catch (IOException io) {
		io.printStackTrace();
	} finally {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
 */
	
	public static void changeLocale( String language, String country ){
		currentLocale = new Locale( language, country );
		messages = ResourceBundle.getBundle( LANGUAGE_BASENAME, currentLocale );
	}
	
	public static String getTranslated( String string ){		
        return messages.getString(string);
	}
}
