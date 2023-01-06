import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class Representing pixel being drawn
 */
public class Pixel {
    /**
     * X and Y coordinates of pixel
     */
    private double x,y;
    /**
     * Colour of pixel
     */
    private Color colour;

    /**
     * Constructor
     * @param x
     * @param y
     * @param colour
     */
    public Pixel(double x,double y, Color colour){
        this.x=x;
        this.y=y;
        this.colour=colour;
    }


    /**
     * Draw the pixel
     * @param gc
     */
    public void draw(GraphicsContext gc){
        gc.setFill(colour);
        gc.fillRect(x,y,1,1);
    }
}
