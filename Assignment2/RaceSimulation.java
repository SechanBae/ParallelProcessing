/**
 * Main class that simulates the race
 * I, Sechan Bae, student number 000803348 ,
 * certify that all code submitted is my own work; that I have not copied it from any other source.
 * I also certify that I have not allowed my work to be copied by others.
 */
public class RaceSimulation {

    /**
     * the three ropeCrossings for the race
     */
    static RopeCrossing[] crossings=new RopeCrossing[3];

    /**
     * main method that sets up the race
     * @param args
     */
    public static void main(String[] args) {
        Thread[] threads=new Thread[5];
        AdventureTeam[] teams=new AdventureTeam[5];
        //create ropeCrossings
        for(int i=0;i<3;i++){
            crossings[i]=new RopeCrossing(i+1);
        }
        //create AdventureTeam threads and start
        for(int i=0;i<5;i++){
            teams[i]=new AdventureTeam(i+1);
            threads[i]=new Thread(teams[i]);
            threads[i].start();
        }
        //Join AdventureTeam threads
        for(int i=0;i<5;i++){
            try{
                threads[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        //Print out ropeCrossing
        for(int i=0;i<3;i++){
            System.out.println(crossings[i].toString());
        }
    }
}