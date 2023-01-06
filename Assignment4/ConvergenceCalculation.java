import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Code modified from: http://www.hameister.org/JavaFX_MandelbrotSet.html
 */
public class ConvergenceCalculation implements Callable {

    private int startCol,endCol;
    private double startC,precision;

    /**
     * Method to calculate the color of each pixel on the screen for the Mandelbrot
     * @param ctx GraphicsContext variable to draw to the canvas
     * @param reMin real number minimum value
     * @param reMax real number maximum value
     * @param imMin imaginary number minimum value
     * @param imMax imaginary number maximum value
     */
    public ArrayList<Pixel> paintSet() {
        int convergenceSteps = 50;
        ArrayList<Pixel> pixels=new ArrayList<Pixel>();

        // Outer for loop is controlling the x position (xR) and the convergence for the real number
        for (double c = startC+(startCol*precision), xR = startCol; xR < endCol; c = c + precision, xR++) {

            // Inner for loop is controlling the y position (yR) and the convergence for the imaginary number
            for (double ci = -1.2, yR = 0; yR < Assignment4Starter.CANVAS_HEIGHT; ci = ci + precision, yR++) {
                double convergenceValue = checkConvergence(ci, c, convergenceSteps);  // check how many steps have occured towards convergence
                double t1 = (double) convergenceValue / convergenceSteps;  // calculate the ratio of the current convergent step compared to the complete step (50)
                double c1 = Math.min(255 * 2 * t1, 255);  // calculate the ratio red and blue components of the color
                double c2 = Math.max(255 * (2 * t1 - 1), 0);  // calculate the ratio for the green component of the color
                Pixel pixel;
                if (convergenceValue != convergenceSteps) {
                    pixel=new Pixel(xR,yR,Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0));

                } else {
                    pixel=new Pixel(xR,yR,Color.PURPLE);
                }
                pixels.add(pixel);
            }
        }
        return pixels;
    }

    /**
     * Checks the convergence of a coordinate (c, ci) The convergence factor
     * determines the color of the point.
     * @param c real number current value
     * @param ci imaginary number current value
     * @param convergenceSteps number of steps to converge on
     * @return Which ever is greater of the number of steps it takes to converge or the total convergence steps
     */
    private int checkConvergence(double ci, double c, int convergenceSteps) {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }
    public ConvergenceCalculation(int startCol,int endCol,double startC,double precision){
        this.startCol=startCol;
        this.endCol=endCol;
        this.startC=startC;
        this.precision=precision;
    }
    @Override
    public ArrayList<Pixel> call() throws Exception {
        return paintSet();
    }
}
