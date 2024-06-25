package com.diaconia.flattojson;

import com.diaconia.flattojson.config.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlattojsonApplication {
    @Value("${access-method}")
    private static String accessMethod;
    @Value("${filePathProperty}")
    private static String filePathProperty;
    /**
     * Pass arguments to spring boot application by:
     * mvn spring-boot:run -Dspring-boot.run.arguments=filePath=C:/temp/200k.txt
     *
     * @param args
     */
    public static void main(String[] args) {
        //when possible, validate inputs here to fail quickly
        boolean foundArg = false;
        if (accessMethod != null && accessMethod.equals("command-line")) {
            for (String arg : args) {
                String[] parts = arg.split("=");
                if (parts.length == 2) {
                    String name = parts[0];
                    System.err.println("name: " + name);
                    if (name.equals(Constants.PROPERTY_FILE_PATH)) {
                        String value = parts[1];
                        System.err.println("value: " + value);
                        System.setProperty(Constants.PROPERTY_FILE_PATH, value);
                        foundArg = true;
                        break;
                    }
                }
            }
            if (!foundArg) {
                throw new IllegalArgumentException("filePath command line argument not set");
            }
        }

        SpringApplication application = new SpringApplication(FlattojsonApplication.class);
        application.run(args);
    }

}
