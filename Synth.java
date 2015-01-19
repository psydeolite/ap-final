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
    Sequencer seqr;
    Sequence seq;
    Piano piano;
    Chanel[] channels;
    Chanel cc;
    Instrument[] instruments;
    boolean recording;
    boolean playing;
    long stime;
    Track track;
    Recorder recorder;
    InstrumentTable instrumentable;
    private Container pane;
    private JPanel canvas;
    //private JButton record, srecord,play,stop,save;
    //private ButtonGroup instrumentz;
    //private JRadioButton p,guitar,violin,trumpet,flute;
    private JLabel label;

    public Synth() {
        JFrame a = new JFrame("Do-Re-Midi");
	a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//Box one = Box.createHorizontalBox();
        //Box two = Box.createVerticalBox();
        JPanel three = new JPanel(new FlowLayout());
	/*
	instrumentz=new ButtonGroup();
	p  = new JRadioButton ("Piano");
	guitar = new JRadioButton ("Guitar");
	violin = new JRadioButton ("Violin");
	trumpet = new JRadioButton ("Trumpet");
	flute = new JRadioButton("Flute");
	instrumentz.add(p); two.add(p);
	instrumentz.add(guitar); two.add(guitar);
	instrumentz.add(violin); two.add(violin);
	instrumentz.add(trumpet); two.add(trumpet);
	instrumentz.add(flute); two.add(flute);
	*/
	three.add(piano = new Piano());
	instrumentable=new InstrumentTable();
	recorder=new Recorder();
	Box top = Box.createVerticalBox();
	top.add(instrumentable.getBox());
	top.add(recorder.getBox());
	top.add(three);
	Container content = a.getContentPane();
	content.setLayout(new BorderLayout());
	content.add(top, BorderLayout.CENTER);
	a.pack();
	a.setVisible(true);
    }

    public void open() {
	try {
	    syn=MidiSystem.getSynthesizer();
	    if (syn==null) {
		System.out.println("can't open synth");
		return;
	    } else {
		syn.open();
		seq=new Sequence(Sequence.PPQ, 10);
		seqr=MidiSystem.getSequencer();
		//seqr.open();
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
    
    public void addNoteEvent(boolean on, Key k) {
	ShortMessage m=new ShortMessage();
	long dur=System.currentTimeMillis()-stime;
	long tic=dur*seq.getResolution()/500;
	 try {
	    if (on) {
		m.setMessage(ShortMessage.NOTE_ON, 0, k.keynum, 60);
	    } else {
		m.setMessage(ShortMessage.NOTE_OFF, 0, k.keynum, 60);
	    }
	} catch (InvalidMidiDataException e) {
	    e.printStackTrace();
	 }
	MidiEvent me = new MidiEvent(m, tic);
	track.add(me);
    }

    class Recorder implements ActionListener {
	Box one=Box.createHorizontalBox();
	JButton record=new JButton("Record");
	//JButton srecord=new JButton("Stop");
	JButton play =new JButton("Play");
	ArrayList tracks=new ArrayList();
	public Recorder() {
	    record.addActionListener(this);
	    one.add(record);
	    //srecord.addActionListener(this);
	    //one.add(srecord);
	    play.addActionListener(this);
	    one.add(play);
	}
	public Box getBox() {
	    return one;
	}
	public void actionPerformed(ActionEvent a) {
	    JButton button = (JButton) a.getSource();
	    //System.out.println("actionperformed");
	    if (button.equals(play)) {
		if (!playing) {
		    if (track!=null) {
			play.setText("Stop");
			startPlay();
			record.setEnabled(false);
		    } else {
			System.out.println("can't play track, null");
		    }
		} else {
		    play.setText("Play");
		    stopPlay();
		    record.setEnabled(true);
		}
	    }
	    else if (button.equals(record)) {
		if (recording) {
		    record.setText("Record");
		    stopRecord();
		    play.setEnabled(true);
		} else {
		    record.setText("Stop");
		    startRecord();
		    play.setEnabled(false);
		}
	    }
	}
	
		
	public void startRecord() {
	    System.out.println("startrecord");
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    track=seq.createTrack();
	    tracks.add(track);
	    System.out.println(tracks.size());
	    seqr.recordEnable(track,cc.channelnum);
	    stime=System.currentTimeMillis();
	    //seqr.startRecording();
	    recording=true;
	}

	public void stopRecord() {
	    System.out.println("stoprecord");
	    //seqr.stopRecording();
	    recording=false;
	}

	public void startPlay() {
	    playing=true;
	    System.out.println("startplay");
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    seqr.start();
	}

	public void stopPlay() {
	    System.out.println("stopplay");
	    playing=false;
	    seqr.stop();
	}
    }
    
    class InstrumentTable {
	Box two = Box.createVerticalBox();
	JRadioButton piano  = new JRadioButton ("Piano");
	JRadioButton guitar = new JRadioButton ("Guitar");
	JRadioButton violin = new JRadioButton ("Violin");
	JRadioButton trumpet = new JRadioButton ("Trumpet");
	JRadioButton flute = new JRadioButton("Flute");
	ButtonGroup instrumentz= new ButtonGroup();
	public InstrumentTable() {    
	    instrumentz.add(guitar); 
	    two.add(guitar);
	    instrumentz.add(violin); 
	    two.add(violin);
	    instrumentz.add(trumpet); 
	    two.add(trumpet);
	    instrumentz.add(flute); 
	    two.add(flute);
	}

	public Box getBox() {
	    return two;
	}
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
	    if (recording) {
		addNoteEvent(on, k);
	    }
	}
	public void turnOff(Key k) {
	    System.out.println("pitchOff: "+k.keynum);
	    on=false;
	    cc.channel.noteOff(k.keynum);
	    if (recording) {
		addNoteEvent(on, k);
	    }
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
		    g.setColor(Color.cyan);
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

	
	
	    
