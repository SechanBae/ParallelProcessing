import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Main class that starts program
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */
public class FXClient extends Application {

    /**
     * Button for submit message
     */
    private Button submit;
    /**
     * textfield for user input
     */
    private TextField input;
    /**
     * Label for output
     */
    private Label output;
    /**
     * Scrollpane that holds label
     */
    private ScrollPane messages;
    /**
     * Name of the user
     */
    private static String name;
    /**
     * The background service to listen to messages
     */
    private BackgroundService service;
    /**
     * For writing out messages
     */
    static DataOutputStream out;
    /**
     * For reading in messages
     */
    static DataInputStream in;
    /**
     * Socket for client
     */
    static Socket client;



    /**
     * Start method (use this instead of main).
     *
     * @param stage The FX stage to draw on
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root);
        Canvas canvas = new Canvas(400, 600); // Set canvas Size in Pixels
        stage.setTitle("FXClient"); // Set window title
        root.getChildren().add(canvas);
        stage.setScene(scene);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        // Initialize all GUI components and add them to the Root
        submit = new Button("Submit");
        input = new TextField();
        output = new Label();
        VBox box = new VBox();
        messages = new ScrollPane();
        root.getChildren().addAll(submit, input, messages);

        // Relocate and set sizes for all GUI components
        input.relocate(10, 500);
        input.setPrefWidth(300);
        submit.relocate(325, 500);
        messages.relocate(10, 10);
        messages.setPrefWidth(390);
        messages.setPrefHeight(440);
        output.setPrefWidth(375);
        output.setPrefHeight(5000);
        output.setAlignment(Pos.TOP_LEFT);
        output.setWrapText(true);
        box.getChildren().add(output);
        messages.setContent(box);
        messages.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);


        //request port number from user
        Scanner keyboard = new Scanner(System.in);
        System.out.println("What is the port number to connect to: ");
        int port = keyboard.nextInt();
        //ask name
        System.out.println("What is your name: ");
        name=keyboard.next();
        // Create a new socket to communicate over
        client = new Socket("localhost", port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());

        // Create input and output streams that connect with server
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());

        //send join message
        out.writeUTF(name+" has joined");
        //start background service
        service=new BackgroundService(in);
        listenToMessages();
        //set event handler for button
        submit.setOnAction(this::btnSubmitHandler);
        // show the GUI
        stage.show();

    }

    /**
     * Starts background service to listen to messages from server and bind label to it
     */
    private void listenToMessages(){
        output.textProperty().bind(service.valueProperty());
        service.start();
    }

    /**
     * When submit is pressed, send content to server
     * @param e
     */
    private void btnSubmitHandler(ActionEvent e){
        String text=name+": "+input.getText();
        try {
            out.writeUTF(text);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        input.setText("");
    }
    /**
     * The actual main method that launches the app.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);

        // close all connections
        try {
            FXClient.out.writeUTF(name+" has exited");
            FXClient.out.writeUTF("exit");
            FXClient.out.close();
            FXClient.in.close();
            FXClient.client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
