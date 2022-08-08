package sim.app.foodwaste;
import sim.portrayal.simple.RectanglePortrayal2D;
import java.awt.*;

public class Receiver extends RectanglePortrayal2D {
    // For Checkpointing
    private static final long serialVersionUID = 1;
    // xpos and ypos of the receiver
    private final int xpos;
    private final int ypos;
    // Local variable to know how many resources the Receiver still need to receive
    private int remainingRequests;
    // Total resource requests
    private int totalRequests;

    // Receiver color = white
    public static Paint receiverColor = new Color(255,255,255);

    public Receiver(double scale, boolean filled, int xpos, int ypos){
        super(scale, filled);
        this.xpos = xpos;
        this.ypos = ypos;
    }

    public int getXPos() { return xpos; }
    public int getYPos() { return ypos; }
    public int getRemainingRequests() { return remainingRequests; }
    public void setRemainingRequests(int val) { if(val>=0) remainingRequests = val; }
    public int getTotalRequests() { return totalRequests; }
    public void setTotalRequests(int val) {
        if(val >= 0){
            totalRequests = val;
            remainingRequests = val;
        }
    }

    // Used to update the GUI color of the receiver during the simulation
    public void updateColor(Color c){
        this.paint = c;
    }

}
