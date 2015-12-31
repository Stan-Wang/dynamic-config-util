package com.stan.common.dynamic.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.stan.common.DEFUALT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件属性基类
 *
 * Created by StanWang on 2015/12/29.
 */
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private Properties properties = null;

    private static Configuration lastConfiguration;

    public static Configuration getLastConfiguration(){
        return lastConfiguration;
    }

    /**
     * Configuration constructor.
     *
     */
    public Configuration(String fileName) {
        this(fileName, DEFUALT.ENCODING);
    }

    /**
     * Configuration constructor
     * <p>
     * Example:<br>
     * Configuration con = new Configuration("my_config.txt", "UTF-8");<br>
     * String userName = Configuration.get("userName");<br><br>
     *
     * Configuration = new Configuration("config.txt", "UTF-8");<br>
     * String value = Configuration.get("key");
     *
     * @param fileName the properties file's name in classpath or the sub directory of classpath
     * @param encoding the encoding
     */
    public Configuration(String fileName, String encoding) {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);		// properties.load(Prop.class.getResourceAsStream(fileName));
            if (inputStream == null)
                throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        }
        finally {
            if (inputStream != null) try {inputStream.close();} catch (IOException e) {logger.error(e.getMessage(), e);}
        }
        lastConfiguration = this;
    }

    /**
     * Configuration constructor.
     * @see #Configuration(File, String)
     */
    public Configuration(File file) {
        this(file, DEFUALT.ENCODING);
    }

    /**
     * Configuration constructor
     * <p>
     * Example:<br>
     * Configuration prop = new Configuration(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * String userName = Configuration.get("userName");
     *
     * @param file the properties File object
     * @param encoding the encoding
     */
    public Configuration(File file, String encoding) {
        if (file == null)
            throw new IllegalArgumentException("File can not be null.");
        if (file.isFile() == false)
            throw new IllegalArgumentException("Not a file : " + file.getName());

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        }
        finally {
            if (inputStream != null) try {inputStream.close();} catch (IOException e) {logger.error(e.getMessage(), e);}
        }
        lastConfiguration = this;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null) ? value : defaultValue;
    }

    public Integer getInt(String key) {
        String value = get(key);
        return (value != null) ? Integer.parseInt(value) : null;
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = get(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    public Long getLong(String key) {
        String value = get(key);
        return (value != null) ? Long.parseLong(value) : null;
    }

    public Long getLong(String key, Long defaultValue) {
        String value = get(key);
        return (value != null) ? Long.parseLong(value) : defaultValue;
    }

    public Boolean getBoolean(String key) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : null;
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }

}
