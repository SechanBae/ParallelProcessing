import java.util.Random;
/**
 * Class representing the team for the race
 */
public class AdventureTeam implements Runnable{
    /**
     * threads for the racers
     */
    private Thread[] threads= new Thread[4];
    /**
     * array for the racers
     */
    private Racer[] racers=new Racer[4];
    /**
     * Time taken by team to cross
     */
    private int time=0;
    /**
     * Team number
     */
    private int teamNumber;
    /**
     * Times team have crossed
     */
    private int crossed=0;
    /**
     * monitor object one,used for racers grabbing medallion
     */
    public Object monitor=new Object();
    /**
     * monitor object two,used for racers finishing race
     */
    public Object monitorTwo=new Object();
    /**
     * if team has medallion
     */
    public boolean medallion=false;
    /**
     * if team has finished the race
     */
    public boolean complete=false;
    /**
     * for random generator
     */
    private static final Random r=new Random();
    /**
     * low bound for random generator
     */
    private static final int low=100;
    /**
     * high bound for random generator
     */
    private static final int high=500;

    /**
     * constructor for adventure team, includes team number
     * @param teamNumber
     */
    public AdventureTeam(int teamNumber){
        this.teamNumber=teamNumber;
    }

    /**
     * What is run by AdventureTeam
     */
    @Override
    public void run() {
        //creates and starts racer threads
        for(int i=0;i<4;i++){
            racers[i]=new Racer(i+1,this);
            threads[i]=new Thread(racers[i]);
            threads[i].start();
        }
        //set up gear for the team
        int waitTime = r.nextInt(high-low) + low;
        System.out.println("Team # "+teamNumber+" set-up gear in :"+waitTime);
        try{
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //notify racers that gear is ready and they may cross
        for(int i=0;i<4;i++){
            racers[i].gearNotify();
        }
        //System.out.println("Team # "+teamNumber+" Waiting for Medallion");
        //wait for medallion
        synchronized (monitor){
            while(!medallion){
                try{
                    monitor.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(!medallion){
                    try{
                        monitor.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Team # "+teamNumber+" has gotten the medallion");
            //notify racers that they can cross back
            for(int i=0;i<4;i++){
                racers[i].medallionNotify();
            }
            //System.out.println("Team # "+teamNumber+" Waiting for Cross Back");
            //wait for the racers to cross back
            synchronized (monitorTwo){
                while(!complete){
                    try{
                        monitorTwo.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    if(!complete){
                        try{
                            monitorTwo.wait();
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
                //once all racers are done print
                System.out.println("Team # "+teamNumber+" has completed in : "+time);
            }
        }
        //join racer threads
        for(int i=0;i<4;i++){
            try{
                threads[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * AdventureTeam is notified that a racer has reached the medallion, waitTime is incremented
     * if all racers are there notifies the team that medallion has been recieved
     * @param waitTime
     */
    public void medallionCrossNotify(int waitTime){
        crossed++;
        time+=waitTime;
        if(crossed==4){
            synchronized (monitor){
                medallion=true;
                monitor.notify();
            }
        }
    }
    /**
     * AdventureTeam is notified that a racer has completed the race, waitTime is incremented
     * if all racers complete it, notifies the team that the team is finished
     * @param waitTime
     */
    public void completeCrossNotify(int waitTime){
        crossed++;
        time+=waitTime;
        if(crossed==8){
            synchronized (monitorTwo){
                complete=true;
                monitorTwo.notify();
            }
        }
    }

    /**
     * get team number
     * @return
     */
    public int getTeamNumber() {
        return teamNumber;
    }
}
