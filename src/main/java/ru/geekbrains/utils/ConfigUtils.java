package ru.geekbrains.utils;

import lombok.experimental.UtilityClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class ConfigUtils {

    Properties properties = new Properties();
    private static  InputStream configFile ;

    static {

        try{
            configFile= new FileInputStream(("src/test/resources/app.properties"));
        }
        catch (FileNotFoundException e){
        e.printStackTrace();
        }
    }

    public  String getBaseUrl(){
        try {
            properties.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty("url");
    }
}
