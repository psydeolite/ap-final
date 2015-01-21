/* --------
Do-Re-Midi 
Rabia Akhtar and Mariya Gedrich 
Period 2 
January 2015 */ 


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
import javax.swing.event.*;
import javax.swing.event.ListSelectionListener;

public class Synth extends JFrame{
    final int PROGRAM = 192;
    final int NOTEON = 144;
    final int NOTEOFF = 128;
    Synthesizer syn; 
    Sequencer seqr;
    Sequence seq;
    Piano piano;
    Chanel[] channels;
    Chanel cc;
    Instrument[] instruments;
    int[] instrumentnums;
    int ci;
    boolean recording;
    boolean playing;
    long stime;
    Track track;
    Recorder recorder;
    InstrumentTable instrumentable;
    private Container pane;
    private JPanel canvas;
    private JLabel label;
    private JFrame frame;
    private Box whole;

	/*sets up the GUI 

*/ 
    public Synth() {
	open();
        frame = new JFrame("Do-Re-Midi");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//Box one = Box.createHorizontalBox();
        //Box two = Box.createVerticalBox();
        JPanel topbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
	topbox.add(piano = new Piano());
	instrumentable=new InstrumentTable();
	recorder=new Recorder();
	whole = Box.createVerticalBox();
	
	whole.add(recorder.getBox());
	whole.add(topbox);
	whole.add(instrumentable.getBox());
	//whole.add(recorder.getBox());
	//whole.add(instrumentable.getBox());
	Container content = frame.getContentPane();
	content.setLayout(new BorderLayout());
	content.add(whole, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
    }

    	/* gets sound */ 
    public void open() {

	try {
	    syn=MidiSystem.getSynthesizer();
	    if (syn==null) {
		System.out.println("can't open synth");
		return;
	    } else {
		System.out.println("opening synth");
		syn.open();
		System.out.println("done opening synth");
		seq=new Sequence(Sequence.PPQ, 10);
		seqr=MidiSystem.getSequencer();
		//seqr.open();
		System.out.println("getting soundbank");
		Soundbank s=syn.getDefaultSoundbank();
		if (s!=null) {
		    System.out.println("instrumenting");
		    instruments=syn.getDefaultSoundbank().getInstruments();
		    /*Instrument[] instrumentlist=syn.getDefaultSoundbank().getInstruments();
		    instruments=new Instrument[5];
		    instruments[0]=instrumentlist[0];
		    instruments[1]=instrumentlist[20];
		    instruments[2]=instrumentlist[41];
		    instruments[3]=instrumentlist[57];
		    //instrumentable=new InstrumentTable();
		    //whole.add(instrumentable.getBox());
		    instruments=syn.getDefaultSoundbank().getInstruments();
		    /*instruments=new Instrument[5];
		      /*instrumentnums=new int[5];
		    instruments[0]=instrumentlist[99];
		    instrumentnums[0]=99;
		    instruments[1]=instrumentlist[45];
		    instrumentnums[1]=45;
		    instruments[2]=instrumentlist[61];
		    instrumentnums[2]=61;
		    instruments[3]=instrumentlist[10];
		    instrumentnums[3]=10;
		    instruments[4]=instrumentlist[81];
		    
		   
		    syn.loadInstrument(instruments[0]);
		    */
		    instrumentnums=new int[5];
		    instrumentnums[0]=5;
		    instrumentnums[1]=99;
		    instrumentnums[2]=45;
		    instrumentnums[3]=61;
		    instrumentnums[4]=81;
		    //syn.loadInstrument(instruments[instrumentnums[0]]);
		    syn.loadInstrument(instruments[instrumentnums[0]]);
		    ci=0;
		    for (int i=0;i<instruments.length;i++) {
			System.out.println(instruments[i].toString());
		    }
		}
		System.out.println("getting channels");
		MidiChannel mc[]=syn.getChannels();
		channels=new Chanel[mc.length];
		System.out.println("forlooping");
		for (int i=0;i<5;i++) {
		    System.out.println("added channel "+i);
		    channels[i]=new Chanel(mc[i],i);
		}
		cc=channels[0];
		cc.channel.programChange(instrumentnums[0]);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }

    public void close(){
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
	     if (command==PROGRAM) {
		 m.setMessage(command,cc.channelnum,n,0);
		 if (recording) {
		     System.out.println("program change WHILE RECORDING");
		 }
	     } else {
		 m.setMessage(command,cc.channelnum,n,60);
	     }
	     System.out.println("command: "+m.getCommand());
	      MidiEvent me = new MidiEvent(m, tic);
	      track.add(me);
	     
	      System.out.println("track size:"+track.size());   
	 } catch (Exception e) {
	     e.printStackTrace();
	 }
	
    }
	/* class Recorder records what you play
           and allows you to save it*/ 
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
	    try {
		seq=new Sequence(Sequence.PPQ,10);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	public Box getBox() {
	    return one;
	}
	public void actionPerformed(ActionEvent a) {
	    JButton button = (JButton) a.getSource();
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
	    System.out.println(""+track.size());
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
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    recording=true;
	    track=seq.createTrack();
	    System.out.println("in startRecord:" +track.size());
	    tracks.add(track);
	    addEvent(PROGRAM,instrumentnums[ci]);
	    System.out.println("just added program change record event");
	    seqr.recordEnable(track,cc.channelnum);
	    stime=System.currentTimeMillis();
	    //recording=true;
	}

	public void stopRecord() {
	    recording=false;
	    System.out.println(tracks.size());
	}

	public void startPlay() {
	    playing=true;
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    System.out.println("program:"+cc.channel.getProgram());
	    seqr.start();
	}

	public void stopPlay() {
	    playing=false;
	    seqr.stop();
	    seqr.close();
	}

	public void clearTrack() {
	    while (track.size()>0) {
		track.remove(track.get(0));
	    }
	    System.out.println("postclear:"+track.size());
	    tracks.remove(0);
	}
    }
    /* creates instrument table */
    class InstrumentTable {
	private int rownum=5;
	private int colnum=1;
	Box box = Box.createVerticalBox();
	JTable table;
	String[] columnName={"Instruments"};
	public InstrumentTable() {    
	    //table.setShowGrid(true);
	    //TableColumn c=table.getColumnModel().getColumn(0);
	    //box.add(table);
	    //c.setPreferredWidth(5);
	    //box.add(table);
	    TableModel model = new AbstractTableModel() {
		    public int getRowCount() {return rownum;}
		    public int getColumnCount() {return colnum;}
		    public Object getValueAt(int row, int col) {
			if (instruments!=null) {
			    return instruments[instrumentnums[row]].getName();
			} else {
			    return Integer.toString(2);
			}
		    }
		    public String getColumnName(int col) {return columnName[col];}
		    public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
		    public boolean isCellEditable(int row, int col) { return false;}
		    public void setValueAt(Object obj,int row, int col) {}
		};
	
	//JTable table=new JTable(data,columnName);
	
	    table=new JTable(model);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    ListSelectionModel lsm=table.getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			//System.out.println("value changed");
			if (!e.getValueIsAdjusting()) {
			    ListSelectionModel sm=(ListSelectionModel) e.getSource();
			    if (!sm.isSelectionEmpty()) {
				System.out.println("row: "+table.getSelectedRow());
				changeProgram(table.getSelectedRow());
			    } 
			}
		    }
		});
	    table.setCellSelectionEnabled(true);
	    table.setColumnSelectionAllowed(false);
            table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
            TableColumn columnA = table.getColumn(table.getColumnName(0));
            columnA.setMinWidth(350);
            columnA.setMaxWidth(350);
	    //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    box.add(table);

	    /*ListSelectionModel lsm = table.getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel sm=(ListSelectionModel) e.getSource();
			if (!sm.isSelectionEmpty()) {
			    changeProgram(ci);
			} else {
			    changeProgram(sm.getMinSelectionIndex());
			}
		    }
		    });*/
	}
	
	public Box getBox() {
	    return box;
	}
	
	public void changeProgram(int select) {
	    /*
	    if (instruments!=null) {
		syn.loadInstrument(instruments[select]);
	    } else {
		System.out.println("no instruments to speak of");
	    }
	    cc.channel.programChange(select);
	    if (recording) {
		addEvent(PROGRAM,select);
	    }
	}
	 
	    */
	    System.out.println("changing program");
	    ci=select;
	    //System.out.println(ci);
	    if (instruments!=null) {
		syn.loadInstrument(instruments[instrumentnums[select]]);
		System.out.println(instruments[instrumentnums[select]].toString());
	    } else {
		System.out.println("no instruments to speak of");
	    }
	    cc.channel.programChange(instrumentnums[select]);
	    if (recording) {
		addEvent(PROGRAM,instrumentnums[select]);
		}
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
	/* creates each individual key by using the properties of rectangle */
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
	
	/* Piano sets note values for each each key and determines thier positioning */

	public Piano() {
	    setPreferredSize(new Dimension(600,280));
	    setBorder(BorderFactory.createLineBorder(Color.black));
	    //int keystart=60;
	    for (int i=0, x = 0, y= 0, keystart=60;i<22;i++, x+=23, y+=40,keystart++) {
		//makes key, starting keynum at 60 and incrementing by one
		//adds to keys and white/black array, depending on pitch
	
		if (keystart!=61 && keystart!=63 && keystart!=66 && keystart!=68 && keystart!=70 && keystart!=73 && keystart!=75 && keystart!=78 && keystart!=80) { 
		    whitekeys.add(new Key(y,0,40,230,keystart));
		   
		    //System.out.println("w: "+keystart);
		    //x-=12;
		} else {
		    if (keystart==61) {
			blackkeys.add(new Key(26,0,25,150,keystart));
			//System.out.println("b1: "+keystart);
		    } else {
			blackkeys.add(new Key(x,0,25,150,keystart));
		
			//System.out.println("b: "+keystart);
		    }
		    y-=40;
		}
		//	keystart++;
	    }
	    keys.addAll(whitekeys);
	    keys.addAll(blackkeys);
	   
	    addMouseListener(this);
	}
	
	
	/* paint takes the array we filled in piano 
           and paints it onto the GUI into a piano shape*/
	public void paint (Graphics thing)
	{
	    Graphics2D g = (Graphics2D) thing;
	    Dimension d = getSize();
	   
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

	
	
	    
