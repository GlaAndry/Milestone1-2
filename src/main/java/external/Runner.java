package external;

import engine.DowloadCommit;
import engine.RetriveTicket;
import engine.Searcher;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws GitAPIException, IOException {

        //Classe runner per eseguire l'intero programma.
        RetriveTicket retriveTicket = new RetriveTicket();
        DowloadCommit dowloadCommit = new DowloadCommit();
        Searcher searcher = new Searcher();

        retriveTicket.retreive();
        dowloadCommit.getAllCommits();
        searcher.compareCSV();
        /////////////////////////////////////

        //Eliminazione dei file non necessari.
        new File("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\NEWCommits.txt").deleteOnExit();
        new File("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\FinalCommits.txt").deleteOnExit();
        new File("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Milestone1Maven\\src\\main\\resources\\results.csv").deleteOnExit();
        ///////////////////////////////////////

    }
}
