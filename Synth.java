import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import java.awt.*;
import javax.swing.*;

public class Synth extends JFrame{
    //Piano piano;
    private Container pane;
    private JPanel canvas;
    private JButton record,play,stop,save;
    private JLabel label;
    public Synth() {
	setTitle("Synth");
	setSize(600,600);
	setLocation(100,100);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	pane=getContentPane();
	pane.setLayout(new FlowLayout());
	record = new JButton ("Record");
	pane.add(record);
	play = new JButton ("Play");
	pane.add(play);
	stop = new JButton ("Stop");
	pane.add(stop);
	save = new JButton ("Save");
	pane.add(save);
    }
    public static void main(String[] args) {
	Synth s=new Synth();
	s.setVisible(true);
    }

    class Controls {
	//buttons here
    }

    class InstrumentTable {
	//instrument selection
    }
    
    class ChannelTable {
	//channel stuff
    }

    class Key extends Rectangle {
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
    
    
}
	
	
	    
