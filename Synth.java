import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class Synth extends JFrame{
    final int PROGRAM = 192;
    final int NOTEON = 144;
    final int NOTEOFF = 128;
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
    private JFrame frame;

    public Synth() {
        frame = new JFrame("Do-Re-Midi");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//Box one = Box.createHorizontalBox();
        //Box two = Box.createVerticalBox();
        JPanel topbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
	topbox.add(piano = new Piano());
	instrumentable=new InstrumentTable();
	recorder=new Recorder();
	Box whole = Box.createVerticalBox();
	whole.add(instrumentable.getBox());
	whole.add(recorder.getBox());
	whole.add(topbox);
	//whole.add(recorder.getBox());
	//whole.add(instrumentable.getBox());
	Container content = frame.getContentPane();
	content.setLayout(new BorderLayout());
	content.add(whole, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
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
		    Instrument[] instrumentlist=syn.getDefaultSoundbank().getInstruments();
		    instruments=new Instrument[5];
		    instruments[0]=instrumentlist[1];
		    instruments[1]=instrumentlist[20];
		    instruments[2]=instrumentlist[41];
		    instruments[3]=instrumentlist[57];
		    instruments[4]=instrumentlist[81];
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
    
    public void addEvent(int command, int n) {
	ShortMessage m=new ShortMessage();
	long dur=System.currentTimeMillis()-stime;
	long tic=dur*seq.getResolution()/500;
	 try {
	    m.setMessage(command,0,n,60);
	    /*if (on) {
		m.setMessage(ShortMessage.NOTE_ON, 0, k.keynum, 60);
	    } else {
		m.setMessage(ShortMessage.NOTE_OFF, 0, k.keynum, 60);
	    }
	} catch (InvalidMidiDataException e) {
	    e.printStackTrace();
	    }*/
	    MidiEvent me = new MidiEvent(m, tic);
	    track.add(me);
	 } catch (Exception e) {
	     e.printStackTrace();
	 }
    }

    class Recorder implements ActionListener {
	Box one=Box.createHorizontalBox();
	JButton record=new JButton("Record");
	JButton clear=new JButton("Clear");
	JButton play =new JButton("Play");
	JButton save=new JButton("Save");
	ArrayList tracks=new ArrayList();
	public Recorder() {
	    record.addActionListener(this);
	    one.add(record);
	    clear.addActionListener(this);
	    one.add(clear);
	    play.addActionListener(this);
	    one.add(play);
	    save.addActionListener(this);
	    one.add(save);
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
			clear.setEnabled(false);
			save.setEnabled(false);
		    } else {
			System.out.println("can't play track, null");
		    }
		} else {
		    play.setText("Play");
		    stopPlay();
		    record.setEnabled(true);
		    clear.setEnabled(true);
		    save.setEnabled(true);
		}
	    } else if (button.equals(record)) {
		if (recording) {
		    record.setText("Record");
		    stopRecord();
		    play.setEnabled(true);
		    clear.setEnabled(true);
		    save.setEnabled(true);
		} else {
		    record.setText("Stop");
		    startRecord();
		    play.setEnabled(false);
		    clear.setEnabled(false);
		    save.setEnabled(false);
		}
	    } else if (button.equals(clear)) {
		if (track.size()!=0) {
		    clearTrack();
		} 
	    } else if (button.equals(save)) {
		try {
		    save();
		} catch (Exception e) {}
	    }
	}
	
	public void save() {
	    System.out.println("save");
	    File f=new File("file.mid");
	    JFileChooser fc=new JFileChooser(f);
	    fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
		    public boolean accept(File ff) {
			if (ff.isDirectory()) {
			    return true;
			}
			return false;
		    }
		    public String getDescription() {
			return "Save as .mid file";
		    }
		});
	    int returnVal=fc.showSaveDialog(frame);
	    if (returnVal==JFileChooser.APPROVE_OPTION) {
		File rf=fc.getSelectedFile();
		try {
		    int[] types=MidiSystem.getMidiFileTypes(seq);
		    if (types.length!=0) {
			if (MidiSystem.write(seq,types[0],rf)==-1) {
			    throw new IOException("problems writing file");
			}
		    } else {
			System.out.println("can't save this");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
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

	public void clearTrack() {
	    while (track.size()>0) {
		track.remove(track.get(0));
	    }
	}
    }
    
    class InstrumentTable {
	private int rownum=5;
	private int colnum=1;
	Box box = Box.createVerticalBox();
	String[] columnName={"Instruments"};
	Object[][] data = { 
	    {"Piano"}, {"Guitar"}, {"Violin"}, {"Trumpet"}, {"Flute"}
	};
	public InstrumentTable() {    
	    //table.setShowGrid(true);
	    //TableColumn c=table.getColumnModel().getColumn(0);
	    //box.add(table);
	    //c.setPreferredWidth(5);
	    //box.add(table);
	 
	    TableModel model = new AbstractTableModel() {
		    public int getRowCount() {
			return rownum;
		    }
		    public int getColumnCount() {
			return colnum;
		    }
		    public Object getValueAt(int row, int col) {
			if (instruments!=null) {
			    return instruments[row].getName();
			} else {
			    return Integer.toString(0);
			}
		    }
		    public String getColumnName(int col) {return columnName[col];}
		    public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
		    public boolean isCellEditable(int row, int col) { 
			return false;
		    }
		    public void setValueAt(Object obj,int row, int col) {}
		};
	
	//JTable table=new JTable(data,columnName);
	
	    JTable table=new JTable(model);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableColumn c = table.getColumnModel().getColumn(0);
            c.setPreferredWidth(5);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    box.add(table);
	}
	public Box getBox() {
	    return box;
	}
	/*
	public void changeProgram() {
	    if (instruments!=null) {
		syn.loadInstrument(instruments[ci]);
	    } else {
		System.out.println("no instruments to speak of");
	    }
	    cc.channel.programChange(ci);
	    if (record) {
		addEvent(PROGRAM,ci);
	    }
	}
	*/  
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
		addEvent(NOTEON, k.keynum);
	    }
	}
	public void turnOff(Key k) {
	    System.out.println("pitchOff: "+k.keynum);
	    on=false;
	    cc.channel.noteOff(k.keynum);
	    if (recording) {
		addEvent(NOTEOFF, k.keynum);
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
	    setPreferredSize(new Dimension(600,280));
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
	    System.out.println("dimension: "+d);
	    g.setBackground(getBackground());
	    g.clearRect(0, 0, 500, 700);
	    //g.clearRect(0,0,520,700);
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

	
	
	    
