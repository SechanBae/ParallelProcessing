import java.io.File;

/**
 * Main class that indexes files
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */
public class Main {
    /**
     * main method that starts program
     * @param args
     */
    public static void main(String[] args) {
        File[] root=new File[1];
        root[0]=new File("C:\\test10183");
        ProducerConsumer.startIndexing(root);
    }
}