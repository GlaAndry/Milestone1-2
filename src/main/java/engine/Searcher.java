package engine;

import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Searcher {

    private static final Logger LOGGER = Logger.getLogger(Searcher.class.getName());


    String resourcePath = "";


    String proj = "QPID";
    Integer linea  = 0;

    private void lastIssue() throws IOException{

        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            resourcePath = prop.getProperty("resourcePath");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////

        File file = new File(resourcePath, "commits.txt");

        try(FileWriter fw = new FileWriter(resourcePath + "\\NEWCommits.txt") ;  Scanner scanner = new Scanner(file)){
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                linea++;
                if (line.contains("TIME")){
                    try{
                        String tempo = line.substring(5,15);
                        LOGGER.info(linea.toString());
                        fw.append(tempo);
                        fw.append("\n");

                    }catch (StringIndexOutOfBoundsException e){
                        LOGGER.log(Level.WARNING, String.valueOf(e));
                    }


                } else if (line.contains("COMMIT:")) {
                    if(line.contains(proj)){
                        try{
                            LOGGER.info(String.valueOf(linea));
                            String commmit = line.substring(7,16);
                            fw.append(commmit);
                            fw.append("\n");

                        } catch (StringIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                    LOGGER.info("Nessun Parametro corrispondente nella linea: " + linea);

                } else {
                    LOGGER.info("Nessun Parametro corrispondente nella linea: " + linea);
                }

            }

        } catch (StringIndexOutOfBoundsException e){
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void removeElements() {

        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            resourcePath = prop.getProperty("resourcePath");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////

        File file = new File(resourcePath, "NEWCommits.txt");

        try(FileWriter fw = new FileWriter(resourcePath + "\\FinalCommits.txt"); Scanner scanner = new Scanner(file);){
            while(scanner.hasNextLine()){

                //Compongo un file che al suo interno ha solo coppie DATA - Ticket
                String line = scanner.nextLine();
                String line2 = scanner.nextLine();
                /*Necessario in quanto se eseguo scanner.nextline() automaticamente salta alla linea successiva anche line,
                 * Quindi vado avanti a controllare a due a due.
                 * */

                if((Character.isDigit(line.charAt(0)) && line2.contains("QPID"))){
                    fw.append(line);
                    fw.append("\n");
                }
                if(line2.contains("QPID")){
                    fw.append(line2);
                    fw.append("\n");
                }
            }

        } catch (IOException e){
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }

    }

    private void createTicketCSV(){

        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            resourcePath = prop.getProperty("resourcePath");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////

        File file = new File(resourcePath, "FinalCommits.txt");
        File file2 = new File(resourcePath, "finRes.csv");

        try(Scanner scanner = new Scanner(file)){

            List<String[]> data = new ArrayList<>();
            while (scanner.hasNextLine()){
                String line1 = scanner.nextLine();
                String line2 = scanner.nextLine();
                // create a List which contains String array

                //Aggiungo lo spacer iniziale per CSv
                data.add(new String[] {line1,line2});
            }
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file2);
            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            writer.writeAll(data);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }




    private void removeDuplicatesFromCSV() {

        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            resourcePath = prop.getProperty("resourcePath");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////



        try(FileReader filereader1 = new FileReader(resourcePath + "\\finRes.csv");
            FileReader fileReader2 = new FileReader(resourcePath + "\\results.csv");
            CSVReader csvReader1 = new CSVReader(filereader1);
            CSVReader csvReader2 = new CSVReader(fileReader2);
            FileWriter fileWriter = new FileWriter(resourcePath + "\\RisultatiFinali.csv");
            CSVWriter writer = new CSVWriter(fileWriter)) {
            // Create an object of file reader
            // class with CSV file as a parameter.
            // create csvReader object and skip first Line

            ArrayList<String[]> lista = new ArrayList<>();
            ArrayList<String[]> finlis = new ArrayList<>();


            List<String[]> uno = csvReader1.readAll();
            List<String[]> due = csvReader2.readAll();

            /*
            Verifico l'uguaglianza del nome del ticket, senza considerarne il numero
            Se sono uguali aggiungo alla lista, altrimenti vado avanti.
            Ogni volta che finisco il giro ripristino il valore di cont, così da poter
            aggiunger nuovi elementi alla lista. (Una volta che cont sarà maggiore di
            0 significa che quell'elemento è già stato trovato precedentemente)
             */

            int cont = 0;
            for (String[] record1 : uno) {
                for(String[] record2: due){
                    if (record1[1].equals(record2[0])) {
                        if (cont == 0){
                            lista.add(new String[] {record1[0], record1[1]});
                        }
                        cont ++;
                    }
                }
                cont = 0;

            }

            String appoggio = ""; // Stringa di appoggio per determinare un valore in comune.

            for(String[] lis : lista){
                for (String[] lis2 : lista){
                    if (lis[1].equals(lis2[1])) {
                        if (cont == 0){
                            appoggio = lis2[1];
                            finlis.add(new String[] {lis[0], lis[1]});
                        }
                        cont ++;
                    }
                }
                if(!(lis[1].equals(appoggio))){
                    cont = 0;
                }

            }

            writer.writeAll(finlis);
        }
        catch (ArrayIndexOutOfBoundsException | IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    public void compareCSV() throws IOException {

        new Searcher().lastIssue();
        new Searcher().removeElements();
        new Searcher().createTicketCSV();
        new Searcher().removeDuplicatesFromCSV();

    }

    public static void main(String[] args) throws IOException {
        new Searcher().compareCSV();
    }
}
