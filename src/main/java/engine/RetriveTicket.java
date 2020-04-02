package engine;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.opencsv.CSVWriter;
import org.json.JSONPointerException;

public class RetriveTicket {

    public static final Logger LOGGER = Logger.getLogger(RetriveTicket.class.getName());

    private static String path = "";
    public static  String proj ="";


    private void importResources(){
        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            path = prop.getProperty("result.csv");
            proj = prop.getProperty("projectName");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////
    }


    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        JSONArray json;
        try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String jsonText = readAll(rd);
            json = new JSONArray(jsonText);
            return json;
        } catch (IOException e){
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        return null;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        JSONObject json;
        try(BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            return json;
        } catch (IOException e){
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        return null;
    }

    public void retreive(){

        importResources();
        LOGGER.info("Scrivo i file su CSV!");
        File file = new File(path);
        List<String[]> data = new ArrayList<>();

        Integer j = 0;
        Integer i = 0;
        Integer total = 1;
        //Get JSON API for closed bugs w/ AV in the project
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;

            String urlFixedNewFeature = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + proj + "%22AND%22issueType%22=%22New%20Feature%22AND(%22status%22=%22closed%22OR" //aggiornato a NEWFEATURE al posto di BUG
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    + i.toString() + "&maxResults=" + j.toString();


            try {
                JSONObject json = readJsonFromUrl(urlFixedNewFeature);
                assert json != null;
                JSONArray issues = json.getJSONArray("issues");
                total = json.getInt("total");
                for (; i < total && i < j; i++) {
                    //Iterate through each bug
                    String key = issues.getJSONObject(i%1000).get("key").toString();
                    //Aggiungo i dati alla lista.
                    data.add(new String[] {key});
                }
            } catch (NullPointerException | IOException | JSONPointerException e){
                LOGGER.log(Level.WARNING, String.valueOf(e));
            }

            try(FileWriter outputfile = new FileWriter(file); CSVWriter writer = new CSVWriter(outputfile)) {
                //Scrivo i Dati ricavati sul file csv
                writer.writeAll(data);
                writer.flush();
            }
            catch (IOException | JSONException e) {
                LOGGER.log(Level.WARNING, String.valueOf(e));
            }

            LOGGER.info("Fatto!");

        } while (i < total);
    }


    public static void main(String[] args) {

        new RetriveTicket().retreive();

    }



}
