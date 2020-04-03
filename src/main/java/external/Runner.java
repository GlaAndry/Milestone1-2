package external;

import engine.DowloadCommit;
import engine.RetriveTicket;
import engine.Searcher;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws GitAPIException, IOException {

        /**
         * Classe Runner per eseguire l'intero programma senza effettuare più run delle diverse classi.
         * Restituisce come output il file RisultatiFinali.csv, che risulta essere l'intersezione tra i
         * Ticket di Jira ed i Ticket ricavati attraverso GitHub. Con questi dati è possibile eseguire il grafico.
         *
         * All'interno del file finRes è possibile inoltre verificare quale è stata la prima data di commit e l'ultima
         * data, così da poter eseguire un plot sull'intera vita dell'applicazione.
         */


        RetriveTicket retriveTicket = new RetriveTicket();
        DowloadCommit dowloadCommit = new DowloadCommit();
        Searcher searcher = new Searcher();

        retriveTicket.retreive();
        dowloadCommit.getAllCommits();
        searcher.compareCSV();



    }
}
