import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class representing the racer of the race
 */
public class Racer implements Runnable{
    /**
     * racer number
     */
    private int racerNumber;
    /**
     * AdventureTeam the racer is on
     */
    private AdventureTeam team;
    /**
     * if racer has crossed either way
     */
    private boolean crossed=false;
    /**
     * if racer has gear to start race
     */
    public boolean gearReady=false;
    /**
     * if racer has medallion to cross back
     */
    public boolean crossBack=false;
    /**
     * monitor object one, used to wait for gear is ready
     */
    public Object monitor=new Object();
    /**
     * monitor object two, used to wait for medallion
     */
    public Object monitorTwo=new Object();
    /**
     * array of locks for the three cross ropes
     */
    private static ReentrantLock[] locks = new ReentrantLock[3];
    /**
     * low bound for random generator
     */
    private static final int low=100;
    /**
     * high bound for random generator
     */
    private static final int high=500;
    /**
     * random generator
     */
    private static final Random r=new Random();

    /**
     * constructor for racer class, includes racer number and the team they are on
     * @param racerNumber
     * @param team
     */
    public Racer(int racerNumber,AdventureTeam team){
        this.racerNumber=racerNumber;
        this.team=team;
        for(int i=0;i<3;i++){
            locks[i]=new ReentrantLock();
        }
    }

    /**
     * what is run by the Racer
     */
    @Override
    public void run() {
        //wait for gear notify
        synchronized (monitor){
            while(!gearReady){
                try{
                    monitor.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(!gearReady){
                    try{
                        monitor.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            //cross rope to get medallion
            crossRopeMedallion();
            //set crossed back to false
            crossed=false;
            //wait for team to get medallion
            synchronized (monitorTwo){
                while(!crossBack){
                    try{
                        monitorTwo.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    if(!crossBack){
                        try{
                            monitorTwo.wait();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
                //cross back
                crossRopeBack();
            }
        }
    }

    /**
     * Cross the rope to get medallion,
     * until racer has crossed, check each RopeCrossing if it can be locked
     * then cross it and unlock it
     */
    public void crossRopeMedallion(){
        while(!crossed){
            for(int i=0;i<3;i++){
                if(locks[i].tryLock()){
                    try{
                        RaceSimulation.crossings[i].cross();
                        int waitTime = r.nextInt(high-low) + low;
                        System.out.println("Team # "+team.getTeamNumber()+" Member #"+racerNumber+" crossed :"+waitTime);
                        try{
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //notify AdventureTeam, racer arrived at medallion
                        team.medallionCrossNotify(waitTime);
                        crossed=true;
                        break;
                    }finally{
                        locks[i].unlock();
                    }

                }
            }

        }
   }

    /**
     * Cross the rope back to finish race
     * until racer has crossed, check each RopeCrossing if it can be locked
     * then cross it and unlock it
     */
    public void crossRopeBack(){
        while(!crossed){
            for(int i=0;i<3;i++){
                if(locks[i].tryLock()){
                    try{
                        RaceSimulation.crossings[i].cross();
                        int waitTime = r.nextInt(high-low) + low;
                        System.out.println("Team # "+team.getTeamNumber()+" Member #"+racerNumber+" crossed back :"+waitTime);
                        try{
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //notify AdventureTeam, racer completed race
                        team.completeCrossNotify(waitTime);
                        crossed=true;
                        break;
                    }finally{
                        locks[i].unlock();
                    }

                }
            }

        }
    }

    /**
     * Notify racers that gear is ready and racers can cross to get medallion
     */
    public void gearNotify(){
        synchronized (monitor){
            gearReady=true;
            monitor.notify();
        }
    }
    /**
     * Notify racers that medallion has been received and racers can cross back to complete race
     */
    public void medallionNotify(){
        synchronized (monitorTwo){
            crossBack=true;
            monitorTwo.notify();
        }
    }
}
