import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import java.awt.*;
import javax.swing.*;

public class Synth {
    Piano piano;
    
    class Key extends Rectangle {
	boolean noteOn=false;
	int keynum;
	public Key(int x, int y, int w, int h, int n) {
	    super(x,y,w,h);
	    keynum=n;
	}
	public boolean isOn {
	    return noteOn;
	}
	public boolean turnOn {
	    noteOn=true;
	}
	public boolean turnOff {
	    noteOn=false;
	}
    }

    class Piano extends JPanel implements MouseListener {
	List whitekeys=new ArrayList<Key>();
	List blackkeys=new ArrayList<Key>();
	public Piano() {
	    //makes the piano
	}
    }
}
	
	
	    
