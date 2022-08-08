package sim.app.foodwaste;
import sim.engine.*;
import sim.field.grid.*;
import sim.util.*;

import java.util.HashSet;


public class Model extends SimState {
    private static final long serialVersionUID = 1;
    // FoodTrails, Food and Agents environments
    public DoubleGrid2D foodTrailsGrid;
    public SparseGrid2D foodGrid;
    public SparseGrid2D agentsGrid;
    // Number of Agents and Receivers
    public int numAgents;
    public int numReceivers;
    public int gridWidth = 100;
    public int gridHeight = 100;
    // Number of Active Agents that will release expiring food
    public int numActiveAgents;
    // Set containing Agents releasing food: used to randomly choose an Agent
    // It enumerates the Agents from 0 to numAgents-1
    private HashSet<Integer> activeAgents = new HashSet<>();
    // Agent and Receiver Scale
    public final static int SCALE = 5;
    // Upper bound of resources that an agent can release (Max = 7)
    public final static int MAX_RESOURCES_TO_RELEASE = 7;
    // Upper bound of resources that a receiver can request (used in a random generator so the Maximum value will be 4)
    public final static int MAX_REQUESTED_RESOURCES = 5;
    // Time after which the simulation will end
    public final static int END_SIMULATION = 200;

    // Where the agents are located (xpos, ypos)
    public static final int[][] agentsInfo = { {30, 40}, {40, 30},{50, 50}, {60,30}, {70,40}, {40, 75},  {60,75}};

    // Where the receivers are located (xpos, ypos)
    public static final int[][] receiversInfo = { {10,40}, {20, 10}, {50,20}, {80, 10}, {90,60}, {20,90}, {50,90}, {80,90}};

    // Where the matchmakers are located (xpos, ypos).
    // In this simulation we have only one matchmaker.
    public static final int[] matchmakerInfo = {25,57};

    // for Global and Volatile Inspectors used by Console
    public int getNumActiveAgents(){ return numActiveAgents; }
    public void setNumActiveAgents(int val) { if (val >= 0) numActiveAgents = val; }
    public int getMAXResourcesToRelease() { return MAX_RESOURCES_TO_RELEASE; }
    public int getMaxRequestedResources() { return MAX_REQUESTED_RESOURCES; }
    public int getEndSimulation() { return END_SIMULATION; }

    // Used to reference the matchmaker in the simulation
    public Matchmaker getMatchmaker(){
        Matchmaker m = null;
        if(agentsGrid != null){
            Bag br = agentsGrid.getObjectsAtLocation(matchmakerInfo[0], matchmakerInfo[1]);
            m = (Matchmaker) br.objs[0];
        }

        return m;
    }

    public Model(long seed){
        super(seed);
    }


    public void start(){

        // clear out the Set of Active Agents
        activeAgents.clear();

        // clear out the schedule
        super.start();

        foodTrailsGrid = new DoubleGrid2D(gridWidth,gridHeight);
        foodGrid = new SparseGrid2D(gridWidth,gridHeight);
        agentsGrid = new SparseGrid2D(gridWidth,gridHeight);
        numAgents = agentsInfo.length;
        numReceivers = receiversInfo.length;
        numActiveAgents = /*random.nextInt(numAgents)+1;*/ 3; // +1 because range needs to be from 1 to 7

        // Positioning agents
        for(int i = 0; i< numAgents ; i++){
            Agent a = new Agent(SCALE, i, agentsInfo[i][0], agentsInfo[i][1], this);
            // Setting number of resources that the agent has to release (from 1 to 7)
            a.setResourcesToRelease(/*random.nextInt(MAX_RESOURCES_TO_RELEASE)+1*/4);
            // Setting local receivers of the agent
            a.setLocalReceivers();
            agentsGrid.setObjectLocation(a,new Int2D(a.getXPos(), a.getYPos()));
        }

        // Positioning receivers
        for(int i = 0; i< numReceivers; i++){
            Receiver r = new Receiver(SCALE,false, receiversInfo[i][0],receiversInfo[i][1]);
            // Setting number of resources that the receiver wants to receive
            r.setTotalRequests(/*random.nextInt(MAX_REQUESTED_RESOURCES)*/2);
            agentsGrid.setObjectLocation(r,new Int2D(r.getXPos(), r.getYPos()));
        }

        // Positioning matchmaker
        Matchmaker m = new Matchmaker(SCALE, matchmakerInfo[0],matchmakerInfo[1]);
        agentsGrid.setObjectLocation(m,new Int2D(m.getXPos(), m.getYPos()));
        // adding receivers to the activeReceivers set of the matchmaker
        for(int i=0; i<numReceivers; i++){
            getMatchmaker().getActiveReceivers().add(i);
        }

        System.out.println("ACTIVE RECEIVERS IN MATCHMAKER: " + m.getActiveReceivers());


        // Scheduling Agents: choosing randomly @numActiveAgents and then schedule them
        int k=0;
        while(k < numActiveAgents){
            Agent a;
            // position of a random Agent
            int randomAgentPosition = random.nextInt(numAgents);
            // Checking if this specific Agent is not already releasing food
            if(!activeAgents.contains(randomAgentPosition)){
                Bag ba = agentsGrid.getObjectsAtLocation(agentsInfo[randomAgentPosition][0],agentsInfo[randomAgentPosition][1]);
                a = (Agent) ba.objs[0];
                activeAgents.add(randomAgentPosition);
            }
            else {
                // Agent is already releasing food, so leave this iteration;
                continue;
            }
            k++;
            // Schedule the agent only if it has resources to release
            if (a.getTotalResourcesToRelease() > 0)
                schedule.scheduleRepeating(Schedule.EPOCH, 1, a, 10);




        }

        System.out.println("ACTIVE AGENTS: "+ activeAgents);

        // foodTrail Object
        Steppable foodTrail = new Steppable() {
            public static final long serialVersionUID = 1;

            @Override
            public void step(SimState state) {
                foodTrailsGrid.multiply(0.9); // multiply all values in DoubleGrid2D by parameter

            }
        };

        // Schedule foodTrail with order 2: Food has maximum priority, then Agent(s) and foodTrail at the end
        schedule.scheduleRepeating(Schedule.EPOCH, 2, foodTrail, 1);

        // Ending the simulation after END_SIMULATION time
        Steppable endSimulation = new Steppable() {
            public static final long serialVersionUID = 1;

            @Override
            public void step(SimState state) {
                finish();
            }
        };

        // Schedule endSimulation to end the simulation
        schedule.scheduleOnce(getEndSimulation(), endSimulation);




    }


    public static void main(String[] args) {
        doLoop(Model.class, args);
        System.exit(0);

    }


}
