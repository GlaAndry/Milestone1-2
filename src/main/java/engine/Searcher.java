package engine;

import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Searcher {

    private static final Logger LOGGER = Logger.getLogger(Searcher.class.getName());

    static String resourcePath = "";
    static String proj = "";
    static String finalRes = "";

    private SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");


    private static void importResources() {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            resourcePath = prop.getProperty("resourcePath");
            proj = prop.getProperty("projectName");
            finalRes = resourcePath + "\\RisultatiFinali.csv";

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    private void lastIssue() throws IOException {
        /**
         * Restituisce il file NewCommits.txt che rappresenta una nuova verione del file
         * Commits.txt eliminando le parti superflue. (Cerchiamo quindi di avere solamente
         * La data ed il ticket associato all'interno di questo file.
         * NB: Non rispettando lo standard del commit, alcuni dati risultano essere
         * inutilizzabili allo scopo.*/

        File file = new File(resourcePath, "commits.txt");

        try (FileWriter fw = new FileWriter(resourcePath + "\\NEWCommits.txt"); Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("TIME")) {

                    String tempo = line.substring(5, 15); //Delimito la stringa per la data
                    fw.append(tempo);
                    fw.append("\n");

                } else if (line.contains("COMMIT:")) {
                    if (line.contains(proj) && line.length() > 20) {
                        /**
                         * Il controllo sulla lunghezza è necessario altrimenti potrei avere un StringIndexOutOfBound
                         * o NoSuchElementException. Questo problema è causato dalla variabilità dei commit e dal
                         * mancato utilizzo dello standard. (Ad esempio quando non si specifica il ticket o si fa un
                         * commit senza commento)
                         */
                        String commmit = line.substring(7, 18); //Delimito la stringa per il Ticket
                        fw.append(commmit);
                        fw.append("\n");

                    }
                    LOGGER.log(Level.INFO, "Nessun Parametro corrispondente");

                } else {
                    LOGGER.log(Level.INFO, "Nessun Parametro corrispondente");
                }


            }

        } catch (StringIndexOutOfBoundsException | NoSuchElementException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void removeElements() {

        /**
         * Va a creare un nuovo file chiamato FinalCommits.txt
         * All'interno di questo file troveremo in ordine prima la data del ticket
         * e successivamente il ticket stesso.
         */

        File file = new File(resourcePath, "NEWCommits.txt");

        try (FileWriter fw = new FileWriter(resourcePath + "\\FinalCommits.txt"); Scanner scanner = new Scanner(file);) {
            while (scanner.hasNextLine()) {

                //Compongo un file che al suo interno ha solo coppie DATA - Ticket
                String line = scanner.nextLine();
                String line2 = scanner.nextLine();
                /**Necessario in quanto se eseguo scanner.nextline() automaticamente salta alla linea successiva anche
                 * la stringa line, quindi vado avanti a controllare a due a due.
                 * */

                if ((Character.isDigit(line.charAt(0)) && line2.contains(proj))) {
                    fw.append(line);
                    fw.append("\n");
                }
                if (line2.contains(proj)) {
                    fw.append(line2);
                    fw.append("\n");
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }

    }

    private void createTicketCSV() {

        /**
         * Crea una versione CSV del file FinalCommits.txt, in modo tale da mettere in una singola
         * riga la data e il ticket associato a quella data. Necessario per la comparazione con il
         * file results.csv, che abbiamo ricavato attraverso la query effettuata da Jira.
         */

        File file = new File(resourcePath, "FinalCommits.txt");
        File file2 = new File(resourcePath, "finRes.csv");

        try (Scanner scanner = new Scanner(file);
             FileWriter outputfile = new FileWriter(file2);
             CSVWriter writer = new CSVWriter(outputfile)) {

            List<String[]> data = new ArrayList<>();
            String appoggio1 = "";
            String appoggio2 = "";

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (appoggio1.equals("")) {
                    appoggio1 = line; //fisso la prima riga
                } else {
                    appoggio2 = line; //fisso la seconda riga per poi aggiungere la coppia all'interno della lista.
                    data.add(new String[]{appoggio1, appoggio2});
                    appoggio1 = "";
                }

            }
            // create FileWriter object with file as parameter
            writer.writeAll(data);
            writer.flush(); //NECESSARIO, altrimenti non vengono scritti tutti i record!

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    private void removeSpaceseCSV() throws IOException {

        /**
         * Poiché molti commit non seguono lo standard, questa funzione prende in input
         * finRes.csv e va ad eliminare "gli spazi", in modo tale da avere elementi comparabili
         * nella funzione removeDuplicatesFromCSV.
         */

        try (FileReader fileReader = new FileReader(resourcePath + "\\finRes.csv");
             CSVReader csvReader = new CSVReader(fileReader);
             FileWriter file = new FileWriter(resourcePath + "\\finRes2.csv");
             CSVWriter writer = new CSVWriter(file)) {

            ArrayList<String[]> lista = new ArrayList<>();
            List<String[]> uno = csvReader.readAll();


            for (String[] lis : uno) {
                String lineaAppoggio = ""; //Necessito della stringa di appoggio in quanto così continuando a scorrere posso ricavare un match.
                if ((lis[1].substring(10, 11).equals(":")) || (lis[1].substring(10, 11).equals(" "))) {
                    lineaAppoggio = lis[1].substring(0, 10);
                } else {
                    lineaAppoggio = lis[1];
                }
                lista.add(new String[]{lis[0], lineaAppoggio});
            }

            writer.writeAll(lista);


        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }


    private ArrayList<String[]> removeDuplicatesFromCSV() {

        /**
         * Restituisce l'intersezione tra i ticket ricavati da Jira e quelli ricavati
         * da GitHub. Questo output è situato all'intero del file RisultatiFinali.csv
         * in modo da poterlo utilizzare per andare a creare un grafico.
         */

        ArrayList<String[]> lista = new ArrayList<>();

        try (FileReader filereader1 = new FileReader(resourcePath + "\\finRes2.csv");
             FileReader fileReader2 = new FileReader(resourcePath + "\\results.csv");
             CSVReader csvReader1 = new CSVReader(filereader1);
             CSVReader csvReader2 = new CSVReader(fileReader2);
        ) {
            // Create an object of file reader
            // class with CSV file as a parameter.
            // create csvReader object and skip first Line

            List<String[]> uno = csvReader1.readAll();
            List<String[]> due = csvReader2.readAll();

            /**
             Verifico l'uguaglianza del nome del ticket, senza considerarne il numero
             Se sono uguali aggiungo alla lista, altrimenti vado avanti.
             Ogni volta che finisco il giro ripristino il valore di cont, così da poter
             aggiunger nuovi elementi alla lista. (Una volta che cont sarà maggiore di
             0 significa che quell'elemento è già stato trovato precedentemente)
             */
            int cont = 0;

            for (String[] record1 : uno) { // scorro finRes2.csv
                for (String[] record2 : due) { //scorro results.csv
                    if (record1[1].equals(record2[0])) {
                        if (cont == 0) {
                            lista.add(new String[]{record1[0], record1[1]});
                        }
                        cont++;
                    }
                }
                cont = 0;
            }
            return lista;
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        return lista;
    }

    private void finalResults(ArrayList<String[]> list) {

        /**
         * Questa parte è necessaria in quanto adesso nella lista ci sono solamente determinati ticket
         * (In particolari quelli ricavati tra l'intersezione tra finRes2 e results. A questo punto andiamo a
         * prendere solamente il ticket più aggiornato, non considerando gli altri.
         */

        int cont = 0;
        ArrayList<String[]> finlis = new ArrayList<>();
        try (FileWriter fileWriter = new FileWriter(finalRes);
             CSVWriter writer = new CSVWriter(fileWriter)) {

            String appoggio = ""; // Stringa di appoggio per determinare un valore in comune.
            for (String[] lis : list) {
                for (String[] lis2 : list) {
                    if (lis[1].equals(lis2[1])) {
                        if (cont == 0) {
                            appoggio = lis2[1];
                            finlis.add(new String[]{lis[0], lis[1]});
                        }
                        cont++;
                    }
                }
                if (!(lis[1].equals(appoggio))) {
                    cont = 0;
                }
            }

            writer.writeAll(finlis);
            writer.flush();
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    public Integer[] filterCSVByDate() {

        List<String[]> list;
        List<Integer> ret = new ArrayList<>();
        String newDate;

        int initDate;
        int endDate;

        Integer[] returnInt = new Integer[2];

        try (FileReader fileReader = new FileReader(finalRes);
             CSVReader csvReader = new CSVReader(fileReader)) {

            list = csvReader.readAll();

            for (String[] str : list) {
                newDate = str[0].substring(6);
                ret.add(Integer.parseInt(newDate));
            }
            Collections.sort(ret);

            initDate = ret.get(0);
            endDate = ret.get(ret.size() - 1);

            returnInt[0] = initDate;
            returnInt[1] = endDate;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnInt;
    }

    private List<String[]> sortByDate(List<String[]> list) {
        /**
         * Questo metodo esegue il sorting della lista
         * andando a considerare la data.
         * Return list sorted.
         */

        list.sort((strings, t1) -> {
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = format.parse(strings[0]);
                date2 = format.parse(t1[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date1.compareTo(date2);
        });

        return list;
    }

    public void countOccurrences(Integer[] dates) {

        Map<String, Integer> map = new HashMap<>();
        int min = dates[0];
        int max = dates[1];

        for (int i = min; i < max + 1; i++) {
            for (int j = 1; j < 13; j++) {
                if (j > 9) {
                    map.put(j + "/" + i, 0);
                } else {
                    map.put("0" + j + "/" + i, 0);
                }
            }
        }

        List<String[]> anotherList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(finalRes);
             CSVReader csvReader = new CSVReader(fileReader);
             FileWriter fileWriter = new FileWriter(resourcePath + "\\counterTicket.csv");
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            List<String[]> list = csvReader.readAll();

            for (String[] strings : list) {

                String date = strings[0].substring(0,3) + strings[0].substring(6);
                if (map.containsKey(date)) {
                    int integer = map.get(date);
                    map.replace(date, integer + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                anotherList.add(new String[]{entry.getKey(), entry.getValue().toString()});
            }

            sortByDate(anotherList);
            csvWriter.writeAll(anotherList);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }


    public void compareCSV() throws IOException {

        importResources();
        new Searcher().lastIssue();
        new Searcher().removeElements();
        new Searcher().createTicketCSV();
        new Searcher().removeSpaceseCSV();
        new Searcher().finalResults(new Searcher().removeDuplicatesFromCSV());
        new Searcher().countOccurrences(filterCSVByDate());

    }

    public static void main(String[] args) throws IOException {
        new Searcher().compareCSV();
    }
}
