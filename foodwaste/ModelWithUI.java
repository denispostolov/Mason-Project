package sim.app.foodwaste;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import javax.swing.*;
import java.awt.*;

public class ModelWithUI extends GUIState {
    public Display2D display;
    public JFrame displayFrame;

    FastValueGridPortrayal2D foodTrailsPortrayal = new FastValueGridPortrayal2D("FoodTrail");
    SparseGridPortrayal2D foodPortrayal = new SparseGridPortrayal2D();
    SparseGridPortrayal2D agentsPortrayal = new SparseGridPortrayal2D();

    public static String getName(){ return "Avoid Food Waste"; }

    public static Object getInfo(){
        return "<h2> Avoid Food Waste </h2>" + "<p> A simulation showing agents like restaurants, supermarkets and others avoiding food waste.";
    }

    public ModelWithUI(){
        super(new Model(System.currentTimeMillis()));
    }

    public ModelWithUI(SimState state){
        super(state);
    }

    public void start(){
        super.start();
        setupPortrayals();
    }

    public void load(SimState state){
        super.load(state);
        setupPortrayals();
    }

    public void quit(){
        super.quit();
        if(displayFrame != null)
            displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

    public void setupPortrayals(){
        foodTrailsPortrayal.setField(((Model)state).foodTrailsGrid);
        foodTrailsPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0,1.0,Color.black,Color.white));
        foodPortrayal.setField(((Model)state).foodGrid);
        foodPortrayal.setPortrayalForClass(Food.class,new sim.portrayal.simple.OvalPortrayal2D(Color.green,1.5));
        foodPortrayal.setPortrayalForClass(Question.class, new sim.portrayal.simple.RectanglePortrayal2D(Color.blue,1.5));
        agentsPortrayal.setField(((Model)state).agentsGrid);
        // reschedule the display
        display.reset();
        // redraw the display
        display.repaint();
    }

    @Override
    public void init(Controller c) {
        super.init(c);
        display = new Display2D(400,400,this);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Avoid Food Waste Demonstration Display");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.setBackdrop(Color.black);
        display.attach(foodTrailsPortrayal, "Food Trail");
        display.attach(foodPortrayal, "Food");
        display.attach(agentsPortrayal, "Agents");
        // Placing displayFrame at the center of the screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - displayFrame.getWidth())/3);
        int y = (int) ((dimension.getHeight() - displayFrame.getHeight())/2);
        displayFrame.setLocation(x,y);


    }

    // for Global and Volatile Inspector
    @Override
    public Object getSimulationInspectedObject() {
        return state;
    }

    // to place Console near displayFrame
    @Override
    public Controller createController() {
        Console console = new Console(this);
        console.setVisible(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - displayFrame.getWidth())/3);
        int y = (int) ((dimension.getHeight() - displayFrame.getHeight())/2);
        // Placing Console next to displayFrame
        console.setLocation(x+440,y);
        return console;

    }

    public static void main(String[] args) {

        new ModelWithUI().createController();

    }


}
