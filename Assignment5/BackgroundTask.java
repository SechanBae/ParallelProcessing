import javafx.concurrent.Task;

import java.io.DataInputStream;
/**
 * class that starts background task to listen to messages
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */

public class BackgroundTask extends Task<String> {
    /**
     * For reading in messages
     */
    private DataInputStream in;

    /**
     * Constructor
     * @param in
     */
    public BackgroundTask(DataInputStream in){
        this.in=in;
    }

    /**
     * Listen to messages from server, update value every time a new message is received
     * @return
     * @throws Exception
     */
    @Override
    protected String call() throws Exception {
        String line="";
        while (true) {
            line += in.readUTF()+"\n";
            updateValue(line);
            if(this.isCancelled()){
                return line;
            }
        }
    }
}
