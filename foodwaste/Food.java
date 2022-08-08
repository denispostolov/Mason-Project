package sim.app.foodwaste;
import sim.engine.*;
import sim.util.*;
import java.awt.*;

public class Food implements Steppable {
    // For Checkpointing
    private static final long serialVersionUID = 1;
    private int xdir; //-1,0,1
    private int ydir; // -1,0,1
    private int newx; // -1,0,1
    private int newy; // -1,0,1
    private Agent agent; // Agent from which the food will depart
    private Receiver receiver; // Receiver that will receive food

    public Food(int xdir, int ydir, Agent a, Receiver r) {
        this.xdir = xdir;
        this.ydir = ydir;
        this.agent = a;
        this.receiver = r;
    }

    public void step(SimState state){
        Model m = (Model) state;
        Int2D location = m.foodGrid.getObjectLocation(this);

        // Leave a trail
        m.foodTrailsGrid.field[location.x][location.y] = 1.0;

        // Edge Cases
        if(location.x == receiver.getXPos() && location.y == receiver.getYPos()){
            xdir = 0;
            ydir = 0;
            // Updating GUI receiver color
            if(receiver.getRemainingRequests()>0)
                receiver.updateColor(new Color(255,255,0));
            else
                receiver.updateColor(new Color(34,139,34));
            //this = null;
            // Updating the number of resources that the specific receiver can receive
            //receiver.setRequestedResources(receiver.getRequestedResources()-1);

        }
        else if (location.x == receiver.getXPos() && location.y < receiver.getYPos()) {
            xdir = 0;
            ydir = 1;
        }
        else if (location.x == receiver.getXPos() && location.y > receiver.getYPos()) {
            xdir = 0;
            //ydir = -ydir;
            ydir = -1;
        }
        else if (location.y == receiver.getYPos() && location.x < receiver.getXPos()) {
            xdir = 1;
            ydir = 0;
        }
        else if (location.y == receiver.getYPos() && location.x > receiver.getXPos()) {
            //xdir = -xdir;
            xdir = -1;
            ydir = 0;
        }
        // Cases in which resource loop without changing position, so changing xdir and ydir in new random directions
        else if (location.x < receiver.getXPos() && location.y < receiver.getYPos() && xdir == -1 && ydir == -1 ||
                location.x < receiver.getXPos() && location.y > receiver.getYPos() && xdir == -1 && ydir == 1 ||
                // opposite cases
                location.x > receiver.getXPos() && location.y > receiver.getYPos() && xdir == 1 && ydir == 1 ||
                location.x > receiver.getXPos() && location.y < receiver.getYPos() && xdir == 1 && ydir == -1) {

            xdir = m.random.nextInt(3)-1;
            ydir = m.random.nextInt(3)-1;
        }

        // move the food
        newx = location.x + xdir;
        newy = location.y + ydir;


        if (newx < receiver.getXPos()) {
            newx++;
        }

        else if (newx > receiver.getXPos()) {
            newx--;
        }

        if (newy < receiver.getYPos()) {
            newy++;
        }

        else if (newy > receiver.getYPos()) {
            newy--;
        }

        // set new location
        Int2D newlocation = new Int2D(newx,newy);
        m.foodGrid.setObjectLocation(this,newlocation);

    }


    public int getXDir() { return xdir; }
    public int getYDir() { return ydir; }
    public Agent getAgent() { return agent; }
    public Receiver getReceiver() { return receiver; }

}

