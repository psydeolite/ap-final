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
	    syn=MidiSystem.getSynthesizer();
	    if (syn==null) {
		System.out.println("can't open synth");
		return;
	    } else {
		syn.open();
		Soundbank s=syn.getDefaultSoundbank();
		if (s!=null) {
		    instruments=syn.getDefaultSoundbank().getInstruments();
		    syn.loadInstrument(instruments[0]);
		}
		MidiChannel mc[]=syn.getChannels();
		channels=new Chanel[mc.length];
		for (int i=0;i<5;i++) {
		    System.out.println("added channel "+i);
		    channels[i]=new Chanel(mc[i],i);
		}
		cc=channels[0];
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
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
	s.open();
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
	public void turnOn(Key k) {
	    System.out.println("pitchOn: "+k.keynum);
	    on=true;
	    cc.channel.noteOn(k.keynum, 60);
	}
	public void turnOff(Key k) {
	    System.out.println("pitchOff: "+k.keynum);
	    on=false;
	    cc.channel.noteOff(k.keynum);
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
	    //int keystart=60;
	    for (int i=0, x = 0, y= 0, keystart=60;i<22;i++, x+=23, y+=40,keystart++) {
		//makes key, starting keynum at 60 and incrementing by one
		//adds to keys and white/black array, depending on pitch
		//System.out.println(""+keystart);
		if (keystart!=61 && keystart!=63 && keystart!=66 && keystart!=68 && keystart!=70 && keystart!=73 && keystart!=75 && keystart!=78 && keystart!=80) { 
		    whitekeys.add(new Key(y,0,40,230,keystart));
		    System.out.println("Key: "+y);
		    //System.out.println("w: "+keystart);
		    //x-=12;
		} else {
		    if (keystart==61) {
			blackkeys.add(new Key(26,0,25,150,keystart));
			//System.out.println("b1: "+keystart);
		    } else {
			blackkeys.add(new Key(x,0,25,150,keystart));
			System.out.println("Key: "+x);
			//System.out.println("b: "+keystart);
		    }
		    y-=40;
		}
		//	keystart++;
	    }
	    keys.addAll(whitekeys);
	    keys.addAll(blackkeys);
	    for (int i=0;i<keys.size();i++) {
		System.out.println("key#" + i+": "+keys.get(i).keynum);
	    }
	    addMouseListener(this);
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
		if (key.isOn()) {
		    //System.out.println(""+key.keynum);
		    g.setColor(Color.blue);
		    g.fill(key);
		}
		g.setColor(Color.black);
		g.draw(key);
	    }
	    for (int i = 0; i < blackkeys.size(); i++) {
		Key key = (Key) blackkeys.get(i);
		if (key.isOn()){
		    g.setColor(Color.pink);
		    g.fill(key);
		    g.draw(key);
		}
		else{
		    g.setColor(Color.black);
		    g.fill(key);
		}
	    }
	    
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
	    pkey=getKey(e.getPoint());
	    //System.out.println(pkey);
	    //System.out.println(pkey.keynum);
	    if (pkey!=null) {
		keyPress(pkey);
		System.out.println("mouse pressed: "+pkey.keynum);
	    }
	}
	public void mouseReleased(MouseEvent e) {
	    if (pkey!=null) {
		//System.out.println("mouse released");
		keyUnpress(pkey);
		pkey=null;
	    }
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void keyPress(Key k) {
	    //change color
	    keySound(k);
	    repaint();
	    pressed=true;
	    repaint();
	}
	
	public void keyUnpress(Key k) {
	    //change color back
	    k.turnOff(k);
	    pressed=false;
	    repaint();
	}
	
	public void keySound(Key k) {
	    //makes sound
	    //System.out.println(k.keynum);
	    k.turnOn(k);
	}
	
	public void keyRecord(Key k) {
	    keySound(k);
	    //records a sound
	}
	
	public Key getKey(Point p) {
	    Key r;
	    for (int j=0; j<blackkeys.size();j++) {
		if (((Key) blackkeys.get(j)).contains(p)) {
		    return blackkeys.get(j);
		}
	    }
	    for (int i=0; i<keys.size();i++) {
		//System.out.println("enteredloop");
		if (((Key) keys.get(i)).contains(p)) {
		    //System.out.println("gotkey");
		    
		    System.out.println("gotPitch: "+keys.get(i).keynum);
		    return keys.get(i);
		}
	    }
	    return null;
	}
    }
    
}

	
	
	    
