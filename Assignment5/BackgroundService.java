import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.DataInputStream;
/**
 * class for background service
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */
public class BackgroundService extends Service {

    /**
     * For reading in data
     */
    private DataInputStream in;

    /**
     * constructor
     * @param in
     */
    public BackgroundService(DataInputStream in){
        this.in=in;
    }

    /**
     * Create background task
     * @return
     */
    @Override
    protected Task createTask() {
        return new BackgroundTask(in);
    }
}
