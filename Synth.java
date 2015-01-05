import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import java.awt.*;
import javax.swing.*;

public class Synth extends JFrame{
    //Piano piano;
    private Container pane;
    private JPanel canvas;
    private JButton b1,b2;
    private JLabel label;
    public Synth() {
	setTitle("Synth");
	setSize(300,300);
	setLocation(100,100);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	
	pane=getContentPane();
	pane.setLayout(new FlowLayout());
    }
    public static void main(String[] args) {
	Synth s=new Synth();
	s.setVisible(true);
    }

	
	
    /* class Key extends Rectangle {
	boolean noteOn=false;
	int keynum;
	public Key(int x, int y, int w, int h, int n) {
	    super(x,y,w,h);
	    keynum=n;
	}
	public boolean isOn() {
	    return noteOn;
	}
	public boolean turnOn() {
	    noteOn=true;
	}
	public boolean turnOff() {
	    noteOn=false;
	}
    }

    class Piano extends JPanel implements MouseListener {
	List whitekeys=new ArrayList<Key>();
	List blackkeys=new ArrayList<Key>();
	public Piano() {
	    //makes the piano
	}
	public void keyPress(key k) {
	    //change color
	}
	public void keySound(key k) {
	    //emits a sound
	}
	public void keyRecord(key k) {
	    //records a sound
	}
    }
    */
    
}
	
	
	    
