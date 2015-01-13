import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Synth extends JFrame{
    Synthesizer syn; //MidiSystem.getSynthesizer();
    Sequence seq;
    Piano piano;
    Chanel[] channels;
    Chanel cc;
    Instrument[] instruments;
    private Container pane;
    private JPanel canvas;
    private JButton record,play,stop,save;
    private JLabel label;

    public Synth() {
	setTitle("Synth");
	setSize(700,700);
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
	JPanel rect = new JPanel (new BorderLayout());
	rect.add(piano = new Piano());
	pane.add(rect);
	//	rect.add(piano = new Piano());

    }

    public void open() {
	try {
	    if (syn!=null) {
		System.out.println("can't open synth");
		return;
	    } else {
		syn.open();
	    }
	} catch (Exception e) {e.printStackTrace();return;}
	Soundbank s=syn.getDefaultSoundbank();
	if (s!=null) {
	    instruments=syn.getDefaultSoundbank().getInstruments();
	    syn.loadInstrument(instruments[0]);
	}
	MidiChannel mc[]=syn.getChannels();
	channels=new Chanel[mc.length];
	for (int i=0;i<5;i++) {
	    channels[i]=new Chanel(mc[i],i);
	}
	cc=channels[0];
    }

    public void close() {
	if (syn!=null) {
	    syn.close();
	}
	syn=null;
	instruments=null;
	channels=null;
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

    class Chanel {
	//actual channel stuff
	MidiChannel channel;
	int channelnum;
	public Chanel(MidiChannel c, int cnum) {
	    channel=c;
	    channelnum=cnum;
	}
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
	public void turnOn(int pitch) {
	    on=true;
	    cc.channel.noteOn(pitch, 60);
	}
	public void turnOff(int pitch) {
	    on=false;
	    cc.channel.noteOff(pitch);
	}
    }

    class Piano extends JPanel implements MouseListener {
	boolean pressed=false;
	ArrayList<Key> whitekeys=new ArrayList<Key>();
	ArrayList<Key> blackkeys=new ArrayList<Key>(); 
	ArrayList<Key> keys=new ArrayList<Key>();
	Key pkey;
      
	public Piano() {
	setPreferredSize(new Dimension(600,700));
	setBorder(BorderFactory.createLineBorder(Color.black));
	    int keystart=60;
	    for (int i=0, x = 0, y= 0;i<16;i++, x+=30, y+=40) {
		//makes key, starting keynum at 60 and incrementing by one
		//adds to keys and white/black array, depending on pitch
		if (keystart!=61 && keystart!=63 && keystart!=66 && keystart!=68 && keystart!=70 && keystart!=73) { 
		    whitekeys.add(new Key(y,0,160,230,keystart));
		    //x-=12;
		} else {
		    if (keystart==61) {
			blackkeys.add(new Key(12,0,20,150,keystart));
		    } else {
			blackkeys.add(new Key(x,0,20,150,keystart));
		    }
		    y-=40;
		}
		keystart++;
	    }
	    keys.addAll(whitekeys);
	    keys.addAll(blackkeys);
	}


	public void paint (Graphics thing)
	{
	    Graphics2D g = (Graphics2D) thing;
            Dimension d = getSize();
            g.setBackground(getBackground());
            g.clearRect(0, 0, 5000, 700);
            g.setColor(Color.white);
            g.fillRect(0, 0, 520,230);
            for (int i = 0; i < whitekeys.size(); i++) {
                Key key = (Key) whitekeys.get(i);
                g.setColor(Color.black);
                g.draw(key);
            }
            for (int i = 0; i < blackkeys.size(); i++) {
                Key key = (Key) blackkeys.get(i);
		g.setColor(Color.black);
		g.fill(key);
	    }
	    addMouseListener(this);

	}

	public void mouseClicked(MouseEvent e) {
	    System.out.print("Mouse clicked");
	}
	public void mousePressed(MouseEvent e) {
	    pkey=getKey(e.getPoint());
	    //System.out.println(pkey);
	    //System.out.println(pkey.keynum);
	    if (pkey!=null) {
		keySound(pkey);
		System.out.println("mouse pressed");
	    }
	}
	public void mouseReleased(MouseEvent e) {
	    if (pkey!=null) {
		System.out.println("mouse released");
		keyUnpress(pkey);
		pkey=null;
	    }
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void keyPress(Key k) {
	    //change color
	    keySound(k);
	    pressed=true;
	}
	
	public void keyUnpress(Key k) {
	    //change color back
	    k.turnOff(k.keynum);
	    pressed=false;
	}

	public void keySound(Key k) {
	    //makes sound
	    System.out.println(k.keynum);
	    k.turnOn(k.keynum);
	}

	public void keyRecord(Key k) {
	    keySound(k);
	    //records a sound
	}

	public Key getKey(Point p) {
	    for (int i=0; i<keys.size();i++) {
		System.out.println("enteredloop");
		if (((Key) keys.get(i)).contains(p)) {
		    System.out.println("gotkey");
		    return keys.get(i);
		}
	    }
	    return null;
	}
    }
    
}

	
	
	    
