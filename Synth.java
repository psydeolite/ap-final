import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.*;
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
	boolean on=false;
	int keynum;
	public Key(int x, int y, int w, int h, int n) {
	    super(x,y,w,h);
	    keynum=n;
	}
	public boolean isOn() {
	    return on;
	}
	public void turnOn(MidiChannel channel, int pitch) {
	    on=true;
	    channel.noteOn(pitch, 60);
	}
	public void turnOff(MidiChannel channel, int pitch) {
	    on=false;
	    channel.noteOff(pitch);
	}
    }

    abstract class Piano extends JPanel implements MouseListener {
	boolean pressed=false;
	ArrayList<Key> whitekeys=new ArrayList<Key>();
	ArrayList<Key> blackkeys=new ArrayList<Key>(); 
	ArrayList<Key> keys= new ArrayList<Key>();
	public Piano() {
	    int keystart=57;
	    for (int i=0;i<15;i++) {
		//makes key, starting keynum at 57 and incrementing by one
		//adds to keys and white/black array, depending on pitch
		if (keystart!=58 && keystart!=61 && keystart!=63 && keystart!=66 && keystart!=68 && keystart!=70) {
		    whitekeys.add(keys.get(i));
		} else {
		    blackkeys.add(keys.get(i));
		}
		keystart++;
	    }
	}
	public void keyPress(MidiChannel c, Key k) {
	    //change color
	    keySound(c,k);
	    pressed=true;
	}
	
	public void keyUnpress(MidiChannel c, Key k) {
	    //change color back
	    k.turnOff(c, k.keynum);
	}

	public void keySound(MidiChannel c, Key k) {
	    //makes sound
	    k.turnOn(c, k.keynum);
	}

	public void keyRecord(MidiChannel c, Key k) {
	    keySound(c, k);
	    //records a sound
	}
    }
    
    
}
	
	
	    
