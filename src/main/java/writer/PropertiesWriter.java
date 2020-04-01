package writer;

import engine.DowloadCommit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesWriter {

    public static void main(String[] args){

        final Logger LOGGER = Logger.getLogger(PropertiesWriter.class.getName());

        try (OutputStream output = new FileOutputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("gitDirPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\GitDir");
            prop.setProperty("commitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\commits.txt");
            prop.setProperty("gitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\GitDir\\.git");
            prop.setProperty("result.csv", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\results.csv");
            prop.setProperty("resourcePath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources");
            // save properties to project root folder
            prop.store(output, null);

            String properties = String.valueOf(prop);

            LOGGER.log(Level.INFO, properties);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }
}
