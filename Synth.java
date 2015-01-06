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
	boolean on=false;
	int keynum;
	public Key(int x, int y, int w, int h, int n) {
	    super(x,y,w,h);
	    keynum=n;
	}
	public boolean isOn() {
	    return on;
	}
	public boolean turnOn(MidiChannel channel, int pitch) {
	    on=true;
	    channel.noteOn(pitch, 60);
	}
	public boolean turnOff(MidiChannel channel, int pitch) {
	    on=false;
	    channel.noteOff(pitch);
	}
    }

    class Piano extends JPanel implements MouseListener {
	boolean pressed=false;
	List whitekeys=new ArrayList<Key>();
	List blackkeys=new ArrayList<Key>();
	List keys={Key As, Key Cs, Key Ds, Key Fs, Key Gs, Key As2, Key A, Key B, Key C, Key D, Key E, Key F, Key G, Key A2, Key B2, Key C2};

	public Piano() {
	    setLayout(new BorderLayout());

	    int keystart=57;
	    for (i=0;i<15;i++) {
		//makes key, starting keynum at 57 and incrementing by one
		//adds to white/black array, depending on pitch
		if (keys.get(i).keynum<6) {
		    blackkeys.add(keys.get(i));
		} else {
		    whitekeys.add(keys.get(i));
		}
		keystart++;
	    }
	}
	public void paint () //when mouse is clicked
           {




	}
	public void keyPress(key k) {
	    //change color
	    keySound(k);
	    pressed=true;
	}
	
	public void keyUnpress(MidiChannel c, key k) {
	    //change color back
	    k.turnOff(key.keynum, 50);
	}

	public void keySound(MidiChannel c, key k) {
	    //makes sound
	    k.turnOn(key,keynum, 50);
	}

	public void keyRecord(key k) {
	    keySound(k);
	    //records a sound
	}
    }
    
    
}
	
	
	    
