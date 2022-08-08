package sim.app.foodwaste;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.engine.*;
import sim.util.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;


public class Agent extends RectanglePortrayal2D implements Steppable {
    // For Checkpointing
    private static final long serialVersionUID = 1;
    // agent's ID
    private final int ID;
    // agent's xpos and ypos
    private final int xpos;
    private final int ypos;
    // Referencing Model to access its attributes in the step() method
    private final Model model;
    private boolean releasingFood = false;
    // Total quantity of resources that the agent has to release
    private int totalResourcesToRelease;
    // Quantity of resources that agent still has to release
    private int remainingResourcesToRelease;
    // Local variable used to know if the agent has resources to release or not in a specific simulation
    private int resourcesToRelease;
    // Local Receivers of the Agent
    private HashSet<Integer> localReceivers = new HashSet<>();

    // Agent color = cream
    public static Paint agentColor = new Color(192,255,192);

    public Agent(double scale, int ID, int xpos, int ypos, Model model) {
        super(agentColor,scale);
        this.ID = ID;
        this.xpos = xpos;
        this.ypos = ypos;
        this.model = model;
    }

    public int getID() {return ID; }
    public int getXPos() { return xpos; }
    public int getYPos() { return ypos; }
    public boolean getReleasingFood() { return releasingFood; }
    public int getTotalResourcesToRelease() { return totalResourcesToRelease; }
    public int getRemainingResourcesToRelease() { return remainingResourcesToRelease; }
    //public void setRemainingResourcesToRelease(int val) { if(val>= 0) remainingResourcesToRelease = val; }
    //public int getResourcesToRelease() { return resourcesToRelease; }
    public void setResourcesToRelease(int val) {
        if (val >= 0){
            totalResourcesToRelease = val;
            resourcesToRelease = val;
            remainingResourcesToRelease = val;
        }

    }

    // Used to set localReceivers set for an Agent
    public void setLocalReceivers(){
        if(this.getID() == 0 || this.getID() == 1){
            this.localReceivers.add(0);
            this.localReceivers.add(1);
            this.localReceivers.add(2);
        }
        else if(this.getID() == 2){
            this.localReceivers.add(0);
            this.localReceivers.add(2);
            this.localReceivers.add(4);
        }
        else if(this.getID() == 3 || this.getID() == 4){
            this.localReceivers.add(2);
            this.localReceivers.add(3);
            this.localReceivers.add(4);
        }
        else if(this.getID() == 5 || this.getID() == 6){
            this.localReceivers.add(5);
            this.localReceivers.add(6);
            this.localReceivers.add(7);
        }

    }

    // Used to update the GUI color of the Agent during the simulation
    public void updateColor(Color c){
        this.paint = c;
    }

    @Override
    public void step(SimState state) {
        // Matchmaker of the simulation
        Matchmaker matchmaker = model.getMatchmaker();

        System.out.println("ACTIVE RECEIVERS: " + matchmaker.getActiveReceivers());

        System.out.println("LOCAL RECEIVERS FOR AGENT: " + this.getID() + " ARE: " + localReceivers);

        if(resourcesToRelease > 0) {

            int randomReceiverPosition = 0;

            if(localReceivers.size() > 0){
                // Choosing a random Receiver from localReceivers
                int randomR = model.random.nextInt(localReceivers.size());
                int counter = 0;
                for(Integer i: localReceivers){
                    if( counter == randomR)
                        randomReceiverPosition = i;
                    counter++;
                }
                //randomReceiverPosition = localReceivers.iterator().next();
                System.out.println("RANDOM RECEIVER POSITION AFTER FOR: " + randomReceiverPosition);
                Bag br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[randomReceiverPosition][0], Model.receiversInfo[randomReceiverPosition][1]);
                Receiver r = (Receiver) br.objs[0];
                System.out.println("RECEIVER: " + r.getXPos() + " " + r.getYPos());
                Food f;

                if(r.getRemainingRequests() > 0){

                    f = new Food(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this, r);
                    model.foodGrid.setObjectLocation(f, this.getXPos(), this.getYPos());
                    releasingFood = true;
                    model.schedule.scheduleRepeating(f);
                    System.out.println("FOOD SCHEDULED " + model.schedule.getSteps());
                    // Updating the number of resources that Receiver r can get
                    r.setRemainingRequests(r.getRemainingRequests()-1);
                    // Removing Receiver r from sets activeReceivers and localReceivers if it has not other requests
                    if(r.getRemainingRequests() == 0){
                        matchmaker.getActiveReceivers().remove(randomReceiverPosition);
                        localReceivers.remove(randomReceiverPosition);
                    }
                    // Decrementing both variables related to the number of resources that the agent has
                    remainingResourcesToRelease--;
                    resourcesToRelease--;
                    // Setting GUI agent color to yellow to represent the fact that it is releasing resources
                    updateColor(new Color(255,255,0));
                    System.out.println("I HAVE: " + remainingResourcesToRelease + " RESOURCES TO RELEASE");


                }
                else {
                    System.out.println("ELSE CASE: CHANGING RECEIVER");
                    // Removing actual Receiver from activeReceivers and localReceivers sets
                    matchmaker.getActiveReceivers().remove(randomReceiverPosition);
                    localReceivers.remove(randomReceiverPosition);
                    System.out.println("ACTIVE RECEIVERS AFTER REMOVE: " + matchmaker.getActiveReceivers());
                    System.out.println("LOCAL RECEIVERS AFTER REMOVE: " + localReceivers);

                /*Iterator iterator = model.getActiveReceivers().iterator();
                if(iterator.hasNext()){
                    // Getting the first Receiver with a request from the set activeReceivers
                    randomReceiverPosition = model.getActiveReceivers().iterator().next();
                    br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[randomReceiverPosition][0], Model.receiversInfo[randomReceiverPosition][1]);
                    r = (Receiver) br.objs[0];
                    f = new Food(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this, r);
                    model.food.setObjectLocation(f, this.getXPos(), this.getYPos());
                    releasingFood = true;
                    model.schedule.scheduleRepeating(f);
                    System.out.println("FOOD SCHEDULED " + model.schedule.getSteps());
                    // Updating the number of resources that Receiver r can get
                    r.setRemainingRequests(r.getRemainingRequests()-1);
                    // Removing Receiver r from the set activeReceivers if it has not other requests
                    if(r.getRemainingRequests() == 0){
                        model.getActiveReceivers().remove(randomReceiverPosition);
                    }
                    // Decrementing both variables related to the number of resources that the agent has
                    remainingResourcesToRelease--;
                    resourcesToRelease--;
                    // Setting GUI agent color to yellow to represent the fact that it is releasing resources
                    updateColor(new Color(255,255,0));
                    System.out.println("I HAVE: " + remainingResourcesToRelease + " RESOURCES TO RELEASE");
                }*/
                    if(localReceivers.iterator().hasNext()){
                        // Getting the first Receiver with a request from the set localReceivers
                        randomReceiverPosition = localReceivers.iterator().next();
                        System.out.println("RECEIVER AFTER CHANGE: " + randomReceiverPosition);
                        br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[randomReceiverPosition][0], Model.receiversInfo[randomReceiverPosition][1]);
                        r = (Receiver) br.objs[0];
                        // Checking if Receiver still has requests because different Agent may have a specific receiver in common in their localReceivers set;
                        // so before releasing a resource the Agent has to check if the specific Receiver still needs resources, or it has only to be removed from the localReceivers set
                        if(r.getRemainingRequests() > 0){
                            // The Receiver in the localReceivers list needs a resource, so here we scheduling it
                            f = new Food(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this, r);
                            model.foodGrid.setObjectLocation(f, this.getXPos(), this.getYPos());
                            releasingFood = true;
                            model.schedule.scheduleRepeating(f);
                            System.out.println("FOOD SCHEDULED " + model.schedule.getSteps());
                            // Updating the number of resources that Receiver r can get
                            r.setRemainingRequests(r.getRemainingRequests()-1);
                            // Removing Receiver r from sets activeReceivers and localReceivers if it has not other requests
                            if(r.getRemainingRequests() == 0){
                                matchmaker.getActiveReceivers().remove(randomReceiverPosition);
                                localReceivers.remove(randomReceiverPosition);
                            }
                            // Decrementing both variables related to the number of resources that the agent has
                            remainingResourcesToRelease--;
                            resourcesToRelease--;
                            // Setting GUI agent color to yellow to represent the fact that it is releasing resources
                            updateColor(new Color(255,255,0));
                            System.out.println("I HAVE: " + remainingResourcesToRelease + " RESOURCES TO RELEASE");
                        }
                        else {
                            // Removing the Receiver with no other requests from localReceivers set
                            localReceivers.remove(randomReceiverPosition);
                        }

                    }
                    /*else {
                        // No more Receivers in localReceivers set waiting for resources
                        // Here the Agent will know the Receiver from the Matchmaker.
                        // At the moment I choose a random Receiver in the map in order to create an instance Food
                        int receiverPosition = model.random.nextInt(model.numReceivers);
                        br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[receiverPosition][0], Model.receiversInfo[receiverPosition][1]);
                        Receiver newR = (Receiver) br.objs[0];
                        f = new Food(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this,newR);
                        model.food.setObjectLocation(f, this.getXPos(), this.getYPos());
                        // I do not decrement remainingResourcesToRelease because in this step the agent will not release a resource
                        // Decrement resourcesToRelease in order to avoid an infinite loop
                        resourcesToRelease--;
                    }*/

                }
            }
            else {

                // No more local receivers waiting for resources
                // Here the Agent will know the Receiver from the activeReceivers set of the Matchmaker if it is not null.
                // Scheduling a question only for GUI purposes
                Question q = new Question(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this, matchmaker);
                model.foodGrid.setObjectLocation(q, this.getXPos(), this.getYPos());
                model.schedule.scheduleRepeating(q);
                System.out.println("AGENT: " + this.getID() + " SENT QUESTION TO MATCHMAKER");
                /*q = new Question(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, matchmaker, this);
                //model.foodGrid.setObjectLocation(q, this.getXPos(), this.getYPos());
                model.schedule.scheduleRepeating(model.schedule.getTime()+40,q);
                System.out.println("MATCHMAKER SENT ANSWER TO AGENT: " + this.getID());*/

                Iterator iterator = matchmaker.getActiveReceivers().iterator();
                if(iterator.hasNext()) {
                    // activeReceivers set is not empty, so exists a Receiver with active requests.
                    // Getting the first Receiver with a request from the set activeReceivers
                    randomReceiverPosition = matchmaker.getActiveReceivers().iterator().next();
                    Bag br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[randomReceiverPosition][0], Model.receiversInfo[randomReceiverPosition][1]);
                    Receiver newR = (Receiver) br.objs[0];
                    System.out.println("AGENT: " + this.getID() + " RECEIVER TO SATISFY: " + newR.getXPos() + " " + newR.getYPos());
                    Food f = new Food(model.random.nextInt(3) - 1, model.random.nextInt(3) - 1, this, newR);
                    model.foodGrid.setObjectLocation(f, this.getXPos(), this.getYPos());
                    // model.schedule.getTime()+40 in order to schedule the resource after arrival of the question to the matchmaker
                    model.schedule.scheduleRepeating(model.schedule.getTime()+40,f);
                    System.out.println("FOOD SCHEDULED " + model.schedule.getSteps());
                    // Updating the number of resources that Receiver r can get
                    newR.setRemainingRequests(newR.getRemainingRequests()-1);
                    // Removing Receiver r from the set activeReceivers if it has not other requests
                    if(newR.getRemainingRequests() == 0){
                        matchmaker.getActiveReceivers().remove(randomReceiverPosition);
                    }
                    // Decrementing both variables related to the number of resources that the agent has
                    remainingResourcesToRelease--;
                    resourcesToRelease--;
                    // Setting GUI agent color to yellow to represent the fact that it is releasing resources
                    //updateColor(new Color(255,255,0));
                }
                // There are no more activeReceivers, this means that all requests have been satisfied
                else {
                    Food f = new Food(0, 0, this,new Receiver(Model.SCALE,false,this.getXPos(),this.getYPos()));
                    model.foodGrid.setObjectLocation(f, this.getXPos(), this.getYPos());
                    // I do not decrement remainingResourcesToRelease because in this step the agent will not release a resource
                    // Decrement resourcesToRelease in order to avoid an infinite loop
                    resourcesToRelease--;
                }
            }



       }


        // Agent has released all its resources so setting GUI agent color to green
        if(!(remainingResourcesToRelease>0))
            updateColor(new Color(50,205,50));


        // This Steppable will updating GUI color of specific agents and receivers at the end of the simulation
        Steppable graphicEndSimulation = new Steppable() {
            public static final long serialVersionUID = 1;

            @Override
            public void step(SimState state) {

                // Setting GUI agent color to orange if it still has resources to release
                if(remainingResourcesToRelease > 0)
                    updateColor(new Color(255,127,0));

                // Setting GUI receiver color to orange for all the receivers that hasn't received all the resources requested
                if(!matchmaker.getActiveReceivers().isEmpty()) {
                    Iterator iterator = matchmaker.getActiveReceivers().iterator();
                    while (iterator.hasNext()) {
                        int receiverPosition = (Integer)iterator.next();
                        Bag br = model.agentsGrid.getObjectsAtLocation(Model.receiversInfo[receiverPosition][0], Model.receiversInfo[receiverPosition][1]);
                        Receiver r = (Receiver) br.objs[0];
                        r.updateColor(new Color(255, 127, 0));
                    }
                }
            }
        };

        // Schedule graphicEndSimulation at the end of the simulation
        model.schedule.scheduleOnce(model.getEndSimulation(), graphicEndSimulation);


    }
}
