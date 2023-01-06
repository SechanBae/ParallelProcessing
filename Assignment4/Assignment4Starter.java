import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Main class that starts program
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */

public class Assignment4Starter  extends Application {
    // Size of the canvas for the Mandelbrot set
    public static final int CANVAS_WIDTH = 700;
    public static final int CANVAS_HEIGHT = 600;

    // Values for the Mandelbrot set
    private static double MANDELBROT_RE_MIN = -2;   // Real Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_RE_MAX = 1;    // Real Number Maximum Value for this Mandelbrot
    private static double MANDELBROT_IM_MIN = -1.2; // Imaginary Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_IM_MAX = 1.2;  // Imaginary Number Maximum Value for this Mandelbrot

    /**
     * Main method that starts the program, calculates pixels multi-threadily, and draws
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        //set up canvas
        Pane fractalRootPane = new Pane();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        //calculate initial precision constant for calculation later
        double precision = Math.max((MANDELBROT_RE_MAX - MANDELBROT_RE_MIN) / Assignment4Starter.CANVAS_WIDTH, (MANDELBROT_IM_MAX - MANDELBROT_IM_MIN) / Assignment4Starter.CANVAS_HEIGHT);

        //ArrayList of pixels that will have all pixels of canvas
        ArrayList<Pixel> pixels=new ArrayList<>();

        //setup number of threads based on availability and width of canvas and how many columns each thread get
        int numThreadsAvailable= Runtime.getRuntime().availableProcessors();
        int[] factors={1,2,4,5,7,10,14,20};
        int numThreads=0;
        for (int i:factors) {
            if(numThreadsAvailable>i){
                numThreads=i;
            }
        }
        int numberOfColsPerThread=(int)Math.ceil((double)CANVAS_WIDTH/numThreads);
        //setup executor service and start threads as well as create future objects
        ExecutorService pool= Executors.newFixedThreadPool(numThreads);
        Future<ArrayList<Pixel>>[] futureValues=new Future[numThreads];
        for(int i=0;i<numThreads;i++){
            int startCol=Math.min(i*numberOfColsPerThread,CANVAS_WIDTH);
            int endCol=Math.min((i+1)*numberOfColsPerThread,CANVAS_WIDTH);
            futureValues[i]=pool.submit(new ConvergenceCalculation(startCol,endCol,MANDELBROT_RE_MIN,precision));
        }



        //retrieve future object arraylist of pixels and put each item into main arraylist
        try{
            for(int w=0;w<numThreads;w++){
                ArrayList<Pixel> partialPixels=futureValues[w].get();
                for (Pixel pixel:partialPixels) {
                    pixels.add(pixel);
                }
            }
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        //shut down threads
        pool.shutdown();
        //draw pixels
        for (Pixel pixel:pixels) {
            pixel.draw(canvas.getGraphicsContext2D());
        }
        //set up javafx
        fractalRootPane.getChildren().add(canvas);
        Scene scene = new Scene(fractalRootPane, CANVAS_WIDTH, CANVAS_HEIGHT);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
