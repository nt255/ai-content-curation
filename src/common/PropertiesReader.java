package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
	
	private static PropertiesReader instance;
	private static Properties properties;
	
	private PropertiesReader() {
		properties = new Properties();
        try {
        	properties.load(new FileInputStream("src/config.properties"));
		} catch (IOException e) {
			System.err.println("failed to open properties file");
			e.printStackTrace();
		}
    }
	
	public static synchronized PropertiesReader getInstance() {
        if (instance == null) {
        	instance = new PropertiesReader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
