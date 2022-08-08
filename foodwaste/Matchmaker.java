package sim.app.foodwaste;
import sim.portrayal.simple.RectanglePortrayal2D;
import java.awt.*;
import java.util.HashSet;

public class Matchmaker extends RectanglePortrayal2D {
    // For Checkpointing
    private static final long serialVersionUID = 1;
    // xpos and ypos of the matchmaker
    private final int xpos;
    private final int ypos;
    // Set that enumerates active Receivers from 0 to numReceivers-1.
    // Once a receiver has obtained all the resources it needs, it is removed from the set.
    private HashSet<Integer> activeReceivers = new HashSet<>();

    // Matchmaker color = blue
    public static Paint matchmakerColor = new Color(0,191,255);

    public Matchmaker(double scale, int xpos, int ypos){
        super(matchmakerColor,scale);
        this.xpos = xpos;
        this.ypos = ypos;
    }
    public int getXPos() { return xpos; }
    public int getYPos() { return ypos; }
    public HashSet<Integer> getActiveReceivers() { return activeReceivers; }
}
