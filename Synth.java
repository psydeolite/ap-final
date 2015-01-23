/* --------
   Do-Re-Midi 
   Rabia Akhtar and Mariya Gedrich 
   Period 2 
   January 2015 

Welcome to Synth.java!
All classes are placed into this file for easy viewing and cross usage purposes. 

*/ 


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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
    int numOfTracks;
    //ArrayList<Track> tracks;
    Instrument[] instruments;
    int[] instrumentnums;
    int currentInstrument;
    int trackindex;
    boolean recording;
    boolean playing;
    long stime;
    Track ctrack;
    Recorder recorder;
    InstrumentTable instrumentable;
    TrackTable tracktable;
    private Container content;
    private JPanel x,y,topbox;
    private JLabel label;
    private JFrame frame;
    private Box whole;

    /*sets up the GUI  */ 
    public Synth() {
	open();
	setSize(300,300);
        frame = new JFrame("Do-Re-Midi");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialize 
	recorder=new Recorder();
        topbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topbox.setBorder(new EmptyBorder(20, 60, 0, 0) );
        piano = new Piano();
	piano.setFocusable(true);
	instrumentable=new InstrumentTable();
	tracktable=new TrackTable();
	x = new JPanel();
	y = new JPanel();
	y.add(instrumentable.getBox());
        y.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEmptyBorder (),"Instruments",TitledBorder.CENTER, TitledBorder.TOP));
	x.add(tracktable.getBox());
        x.setBorder(new EmptyBorder(0, 10, 10, 10) );

	//set up GUI
	whole = Box.createVerticalBox();
	whole.add(recorder.getBox());
     	topbox.add(piano);
	whole.add(topbox);
	whole.add(x);
	whole.add(y);
        whole.setBorder(new EmptyBorder(10, 50, 50, 0) );

	//add to container
	content = frame.getContentPane();
	content.setLayout(new BorderLayout());
	content.add(whole, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
	
	piano.requestFocus();
    }

    /* gets sound */ 
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
		Soundbank s=syn.getDefaultSoundbank();
		if (s!=null) {
		    instruments=syn.getDefaultSoundbank().getInstruments();
		    instrumentnums=new int[16];
		    instrumentnums[0]=0;
		    instrumentnums[1]=11;
		    instrumentnums[2]=19;
		    instrumentnums[3]=29;
		    instrumentnums[4]=34;
		    instrumentnums[5]=40;
		    instrumentnums[6]=52;
		    instrumentnums[7]=56;
		    instrumentnums[8]=65;
		    instrumentnums[9]=73;
		    instrumentnums[10]=81;
		    instrumentnums[11]=88;
		    instrumentnums[12]=97;
		    instrumentnums[13]=106;
		    instrumentnums[14]=116;
		    instrumentnums[15]=126;
		    syn.loadInstrument(instruments[instrumentnums[0]]);
		    currentInstrument=0;
		}
		MidiChannel mc[]=syn.getChannels();
		cc=new Chanel(mc[0],1);
		channels=new Chanel[4];
		for (int i=0;i<4;i++) {
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
    }
    
    public void addEvent(int command, int n) {
	ShortMessage m=new ShortMessage();
	long dur=System.currentTimeMillis()-stime;
	long tic=dur*seq.getResolution()/500;
	try {
	    if (command==192) {
		
		m.setMessage(command,cc.channelnum,n,0);
		if (recording) {
		    //System.out.println("program change WHILE RECORDING");
		} 
	    } else {
		m.setMessage(command,cc.channelnum,n,100);
	    }
	    MidiEvent me = new MidiEvent(m, tic);
	    
	    cc.track.add(me);
	    System.out.println("Channel " +cc.channelnum+" track size: "+cc.track.size());
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
	
	public Recorder() {
	    numOfTracks=0;
	    //tracks=new ArrayList<Track>();
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
		    if (cc.track!=null) {
			play.setText("Stop");
			startPlay();
			record.setEnabled(false);
			clear.setEnabled(false);
			save.setEnabled(false);
			piano.requestFocus();
		    } else {
			System.out.println("can't play track, null");
		    }
		} else {
		    play.setText("Play");
		    stopPlay();
		    record.setEnabled(true);
		    clear.setEnabled(true);
		    save.setEnabled(true);
		    piano.requestFocus();
		}
	   
	    } else if (button.equals(record)) {
		if (recording) {
		    record.setText("Record");
		    stopRecord();
		    play.setEnabled(true);
		    clear.setEnabled(true);
		    save.setEnabled(true);
		    piano.requestFocus();
		} else {
		    record.setText("Stop");
		    startRecord();
		    play.setEnabled(false);
		    clear.setEnabled(false);
		    save.setEnabled(false);
		    piano.requestFocus();
		}
	    } else if (button.equals(clear)) {
		if (cc.track!=null && cc.track.size()!=0) {
		    clearAll();
		} 
		piano.requestFocus();
	    } else if (button.equals(save)) {
		try {
		    save();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		piano.requestFocus();
	    }
	}
	
	public void save() {
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
		File out=fc.getSelectedFile();
		try {
		    int[] types=MidiSystem.getMidiFileTypes(seq);
		    try {
			MidiSystem.write(seq,types[0],out);
		    } catch (Exception e) {
			System.out.println("Problems writing file");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	public void startRecord() {
	    System.out.println("start recording");
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    recording=true;
	    if (cc.track!=null) {
		clearTrack(cc.track);
	    }
	    cc.track=seq.createTrack();
	    numOfTracks++;
	    //tracks.add(cc.track);
	    seqr.recordEnable(cc.track,cc.channelnum);
	    stime=System.currentTimeMillis();
	    syn.loadInstrument(instruments[currentInstrument]);
	    addEvent(PROGRAM,currentInstrument);
		seqr.start();
		playing=true;
	}

	public void stopRecord() {
	    recording=false;
	    if (playing==true) {
		seqr.stop();
		seqr.close();
		playing=false;
	    }
	}

	public void startPlay() {
	    playing=true;
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }	   
	    seqr.start();
	}

	public void stopPlay() {
	    playing=false;
	    seqr.stop();
	    seqr.close();
	}

	public void clearAll() {
	    Track current;
	    /*while (tracks.size()>0) {
		current=tracks.get(0);
		seq.deleteTrack(current);
		tracks.remove(0);*/
	    for (int i=0;i<channels.length;i++) {
		current=channels[i].track;
		seq.deleteTrack(current);
		channels[i].track=null;
		//if (tracks.size()>0) {
		//if (numOfTracks>0) {
		//   numOfTracks--;
		    //tracks.remove(0);
	    }
	    numOfTracks=0;
	}

	//clears individual track
	public void clearTrack(Track t) {
	    numOfTracks--;
	    seq.deleteTrack(t);
	    cc.track=null;
	    System.out.println("deleted track");
	}
    }
    /* creates instrument table */
    class InstrumentTable {
	private int rownum=4;
	private int colnum=4;
	Box box = Box.createVerticalBox();
	JTable table;
	String[] columnNames={"1","2","3","4"};
	int[][] instrumentarray=new int[4][4];
	int ccol=0;
	int crow=0;
	public InstrumentTable() {    
	    int c=0;
	    for (int i=0;i<4;i++) {
		for (int j=0;j<4;j++) {
		    instrumentarray[j][i]=instrumentnums[c];
		    c++;
		}
	    }
	    TableModel model = new AbstractTableModel() {
		    public int getRowCount() {return rownum;}
		    public int getColumnCount() {return colnum;}
		    public Object getValueAt(int row, int col) {
			if (instruments!=null) {
			    //return instruments[instrumentnums[row]].getName();
			    return instruments[instrumentarray[row][col]].getName();
			} else {
			    return Integer.toString(2);
			}
		    }
		    public String getColumnName(int col) {return columnNames[col];}
		    public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
		    public boolean isCellEditable(int row, int col) { return false;}
		    public void setValueAt(Object obj,int row, int col) {}
		};
	
	
	    table=new JTable(model);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	       
	    ListSelectionModel lsm=table.getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
			    ListSelectionModel sm=(ListSelectionModel) e.getSource();
			    if (!sm.isSelectionEmpty()) {
				crow=table.getSelectedRow();
			    } 
			    changeProgram(instrumentarray[crow][ccol]);
			}
		    }
		    });
	    lsm=table.getColumnModel().getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel sm=(ListSelectionModel) e.getSource();
			if (!sm.isSelectionEmpty()) {
			    //System.out.println("colnum: "+table.getSelectedColumn());
			    //changeProgram(instrumentarray[table.getSelectedRow()][table.getSelectedColumn()]);
			    ccol=table.getSelectedColumn();
			}
			changeProgram(instrumentarray[crow][ccol]);
		    }
		});
	    //public void 
	    table.setRowSelectionInterval(0,0);
	    table.setColumnSelectionInterval(0,0);
	    table.setCellSelectionEnabled(true);
	    table.setColumnSelectionAllowed(true);
	    table.setRowMargin(5);
	    JTableHeader header = table.getTableHeader(); 
	    header.setBackground(Color.pink);
	    table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
	    for (int i=0;i<4;i++) {
		TableColumn column=table.getColumn(table.getColumnName(i));
		
		column.setMinWidth(150);
		column.setMaxWidth(150);
	    }
	    box.add(header);
	    box.add(table);

	}
	
	public Box getBox() {
	    return box;
	}
	
	public void changeProgram(int i) {
	    //ci=select;
	    //System.out.println(ci);
	    currentInstrument=i;
	    if (instruments!=null) {
		syn.loadInstrument(instruments[currentInstrument]);
	    } else {
		System.out.println("no instruments to speak of");
	    }
	    //cc.channel.programChange(instrumentnums[ciindex]);
	    cc.channel.programChange(currentInstrument);
	    if (recording) {
		addEvent(PROGRAM,currentInstrument);
	    }
	    piano.requestFocus();
	    
	}
	
	//instrument selection
    }
    
    class TrackTable {
	private JTable table;
	private Box box=Box.createVerticalBox();
	private int colnum=1;
	private int rownum=4;
	private String[] columnName={"Channels"};
	private String s=new String("Empty");
	    

	public TrackTable() {
		
	    TableModel model = new AbstractTableModel() {
		    public int getRowCount() {return rownum;}
		    public int getColumnCount() {return colnum;}
		    public Object getValueAt(int row, int col) {
			if (numOfTracks!=0 && channels[row].track!=null) {
			    return "Channel "+(row+1)+": "+instruments[channels[row].channel.getProgram()].getName();
			} else {
			    return s;
			}
		    }
		    public String getColumnName(int col) {return columnName[col];}
		    public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
		    public boolean isCellEditable(int row, int col) { return false;}
		    public void setValueAt(Object obj,int row, int col) {}
		};
	    table=new JTable(model);
	    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    ListSelectionModel lsm=table.getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
			    ListSelectionModel sm=(ListSelectionModel) e.getSource();
			    if (!sm.isSelectionEmpty()) {
				cc=channels[table.getSelectedRow()];
				piano.requestFocus();
				instrumentable.changeProgram(currentInstrument);
			    }
			}
		    }
		});
	    table.setRowSelectionInterval(0,0);
	    table.setCellSelectionEnabled(true);
	    table.setColumnSelectionAllowed(false);
	    table.setRowMargin(5);
	    JTableHeader header = table.getTableHeader(); 
	    header.setBackground(Color.cyan);
	    table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
	    TableColumn columnA = table.getColumn(table.getColumnName(0));
	    columnA.setMinWidth(200);
	    columnA.setMaxWidth(200);
	    box.add(header);
	    box.add(table);
		
	}
	
	public Box getBox() {
	    return box;
	}
	    

	//channel stuff
    }
    
    class Chanel {
	//actual channel stuff
	MidiChannel channel;
	int channelnum;
	Track track;
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
	    System.out.println("turnOn");
	    on=true;
	    cc.channel.noteOn(k.keynum, 100);
	    if (recording) {
		addEvent(NOTEON, k.keynum);
	    }
	}
	public void turnOff(Key k) {
	    System.out.println("turnOff");
	    on=false;
	    cc.channel.noteOff(k.keynum);
	    if (recording) {
		addEvent(NOTEOFF, k.keynum);
	    }
	}
    }
    
    class Piano extends JPanel implements MouseListener, KeyListener {
	boolean pressed=false;
	ArrayList<Key> whitekeys=new ArrayList<Key>();
	ArrayList<Key> blackkeys=new ArrayList<Key>(); 
	ArrayList<Key> keys=new ArrayList<Key>();
	HashMap<Character,Key> charKeys=new HashMap<Character,Key>();
	char[] blackchars={'2','3','5','6','7','9','0','=','\u0008'};
	char[] whitechars={'q','w','e','r','t','y','u','i','o','p','[',']',(char) 92};
	Key pkey;
	
	/* Piano sets note values for each each key and determines thier positioning */
	
	public Piano() {
	    setPreferredSize(new Dimension(600,240));
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
	    for (int i=0;i<blackchars.length;i++) {
		charKeys.put(blackchars[i],blackkeys.get(i));
	    }
	    for (int i=0;i<whitechars.length;i++) {
		charKeys.put(whitechars[i],whitekeys.get(i));
	    }
	    
	    addKeyListener(this);
	    addMouseListener(this);
	    this.requestFocus();
	    
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
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
	    System.out.println("key is pressed");
	    char key=e.getKeyChar();
	    try { 
		if (!charKeys.get(key).isOn()) {
		    charKeys.get(key).turnOn(charKeys.get(key));
		    repaint();
		    pressed=true;
		    repaint();
		}
	    } catch (Error x) {}
	    /*Key ckey;
	    if (charKeys.containsKey(key)) {
		ckey=charKeys.get(key);
		if (!ckey.isOn()) {
		    System.out.println("pressed");
		    //keyPress(charKeys.get(key));
		    ckey.turnOn(ckey);
		    repaint();
		    pressed=true;
		    repaint();
		}
		}*/
	}
	public void keyReleased(KeyEvent e) {
	    char key=e.getKeyChar();
	    Key ckey;
	    if (charKeys.containsKey(key)){
		ckey=charKeys.get(key);
		ckey.turnOff(ckey);
		pressed=false;
		repaint();
		//keyUnpress(charKeys.get(key));
	    }
	}
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
	    pkey=getKey(e.getPoint());
	    /*try {
		 pkey.turnOn(pkey);
		 repaint();
		 pressed=true;
		 repaint();
		 } catch (Error x) {}*/
	    if (pkey!=null) {
		//keyPress(pkey);
		 pkey.turnOn(pkey);
		 repaint();
		 pressed=true;
		 repaint();
		 }
	}
	public void mouseReleased(MouseEvent e) {
	    if (pkey!=null) {
		//System.out.println("mouse released");
		//keyUnpress(pkey);
		pkey.turnOff(pkey);
		pressed=false;
		repaint();
		pkey=null;
	    }
	    this.requestFocus();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	/*public void keyPress(Key k) {
	    //change color
	    keySound(k);
	    repaint();
	    pressed=true;
	    repaint();
	    }*/
	
	/*public void keyUnpress(Key k) {
	    //change color back
	    k.turnOff(k);
	    pressed=false;
	    repaint();
	    }*/
	
	/*public void keySound(Key k) {
	    //makes sound
	    k.turnOn(k);
	    }*/
	
	/*public void keyRecord(Key k) {
	    keySound(k);
	    //records a sound
	    }*/
	
	public Key getKey(Point p) {
	    Key r;
	    for (int j=0; j<blackkeys.size();j++) {
		if (((Key) blackkeys.get(j)).contains(p)) {
		    return blackkeys.get(j);
		}
	    }
	    for (int i=0; i<keys.size();i++) {
		if (((Key) keys.get(i)).contains(p)) {
		    return keys.get(i);
		}
	    }
	    return null;
	}
    }
    
}

	
	
	    
