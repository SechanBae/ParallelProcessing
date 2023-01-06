/**
 * Class representing the rope racers can cross
 */
public class RopeCrossing {
    /**
     * number associated with rope
     */
    private int ropeNumber;
    /**
     * times racers have crossed rope
     */
    private int crossed=0;

    /**
     * constructor for rope, includes the ropenumber
     * @param ropeNumber
     */
    public RopeCrossing(int ropeNumber){
        this.ropeNumber=ropeNumber;
    }

    /**
     * increment number of cross as racer calls method
     */
    public void cross(){
        crossed++;
    }

    /**
     * string form of rope crossing
     * @return
     */
    @Override
    public String toString() {
        return "Rope Crossing # "+ropeNumber+" has been crossed "+crossed+" times.";
    }

}
