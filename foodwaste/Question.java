package sim.app.foodwaste;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Int2D;

import java.awt.*;


public class Question implements Steppable {
    // For Checkpointing
    private static final long serialVersionUID = 1;
    private int xdir; //-1,0,1
    private int ydir; // -1,0,1
    private int newx; // -1,0,1
    private int newy; // -1,0,1
    private Agent agent; // Agent from which the question will depart
    private Matchmaker matchmaker; // Matchmaker that will receive the question

    public Question(int xdir, int ydir, Agent a, Matchmaker m) {
        this.xdir = xdir;
        this.ydir = ydir;
        this.agent = a;
        this.matchmaker = m;
    }

    public void step(SimState state){
        Model m = (Model) state;
        Int2D location = m.foodGrid.getObjectLocation(this);

        // Leave a trail
        m.foodTrailsGrid.field[location.x][location.y] = 1.0;

        // Edge Cases
        if(location.x == matchmaker.getXPos() && location.y == matchmaker.getYPos()){
            xdir = 0;
            ydir = 0;

        }
        else if (location.x == matchmaker.getXPos() && location.y < matchmaker.getYPos()) {
            xdir = 0;
            ydir = 1;
        }
        else if (location.x == matchmaker.getXPos() && location.y > matchmaker.getYPos()) {
            xdir = 0;
            //ydir = -ydir;
            ydir = -1;
        }
        else if (location.y == matchmaker.getYPos() && location.x < matchmaker.getXPos()) {
            xdir = 1;
            ydir = 0;
        }
        else if (location.y == matchmaker.getYPos() && location.x > matchmaker.getXPos()) {
            //xdir = -xdir;
            xdir = -1;
            ydir = 0;
        }
        // Cases in which resource loop without changing position, so changing xdir and ydir in new random directions
        else if (location.x < matchmaker.getXPos() && location.y < matchmaker.getYPos() && xdir == -1 && ydir == -1 ||
                location.x < matchmaker.getXPos() && location.y > matchmaker.getYPos() && xdir == -1 && ydir == 1 ||
                // opposite cases
                location.x > matchmaker.getXPos() && location.y > matchmaker.getYPos() && xdir == 1 && ydir == 1 ||
                location.x > matchmaker.getXPos() && location.y < matchmaker.getYPos() && xdir == 1 && ydir == -1) {

            xdir = m.random.nextInt(3)-1;
            ydir = m.random.nextInt(3)-1;
        }

        // move the food
        newx = location.x + xdir;
        newy = location.y + ydir;


        if (newx < matchmaker.getXPos()) {
            newx++;
        }

        else if (newx > matchmaker.getXPos()) {
            newx--;
        }

        if (newy < matchmaker.getYPos()) {
            newy++;
        }

        else if (newy > matchmaker.getYPos()) {
            newy--;
        }

        // set new location
        Int2D newlocation = new Int2D(newx,newy);
        m.foodGrid.setObjectLocation(this,newlocation);



    }



    public int getXDir() { return xdir; }
    public int getYDir() { return ydir; }
    public Agent  getAgent() { return agent; }
    public Matchmaker getMatchmaker() { return matchmaker; }
}
