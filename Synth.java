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
import java.lang.*;
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
    private final int PROGRAM = 192;
    private final int NOTEON = 144;
    private final int NOTEOFF = 128;
    private Synthesizer syn; 
    private Sequencer seqr;
    private Sequence seq;
    private Piano piano;
    private Chanel[] channels;
    private Chanel cc;
    private MidiChannel metronome;
    private int numOfTracks;
    private Instrument[] instruments;
    private int[] instrumentnums;
    private int currentInstrument;
    private int trackindex;
    private boolean recording;
    private boolean playing;
    private long stime;
    private Track ctrack;
    private Recorder recorder;
    private InstrumentTable instrumentable;
    private ChannelTable channeltable;
    private Container content;
    private JPanel topbox, bottombox, instAndChan;
    private JLabel label;
    private JFrame frame;
    private Box whole;

    //Constructor: Sets up the GUI
    public Synth() {
	open();
	setSize(300,300);
        frame = new JFrame("Do-Re-Midi");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setBackground(Color.darkGray);
	frame.setResizable(false);
        
	//Initializes parts of the Synthesizer
	recorder=new Recorder();
	piano=new Piano();
	instrumentable=new InstrumentTable();
	channeltable=new ChannelTable();
       
	//Creates all the visual components
	topbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topbox.setBorder(new EmptyBorder(20, 60, 0, 0) );
	topbox.add(piano);
	topbox.setBackground(Color.darkGray);
	instAndChan=new JPanel(new FlowLayout(FlowLayout.LEADING, 15,0));
	instAndChan.add(channeltable.getBox());
	instAndChan.add(instrumentable.getBox());
	instAndChan.setOpaque(true);
	instAndChan.setBackground(Color.darkGray);
	bottombox=new JPanel(new FlowLayout(FlowLayout.CENTER));
	bottombox.setBorder(new EmptyBorder(20,0,0,0));
	bottombox.add(instAndChan);
	bottombox.setBackground(Color.darkGray);
	whole = Box.createVerticalBox();
	whole.add(topbox);
	whole.add(recorder.getBox());
	whole.add(bottombox);
	whole.setBorder(new EmptyBorder(10, 50, 50, 0) );

	//Adds to container
	content = frame.getContentPane();
	content.setLayout(new BorderLayout());
	content.add(whole, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
	piano.setFocusable(true);
	piano.requestFocus();
    }

    //Method: Initializes all the necessary sound components: Synthesizer, Sequencer, Sequence, Soundbank, and MidiChannels  
    public void open() {
	try {
	    syn=MidiSystem.getSynthesizer();
	    if (syn==null) {
		System.out.println("ERROR: Can't open synthesizer");
		return;
	    } else {     
		syn.open();
		seq=new Sequence(Sequence.PPQ, 10);
		seqr=MidiSystem.getSequencer();
		Soundbank s=syn.getDefaultSoundbank();
		if (s!=null) {
		    instruments=s.getInstruments();
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
		metronome=mc[5];
		metronome.programChange(115);
		channels=new Chanel[4];
		for (int i=0;i<4;i++) {
		  channels[i]=new Chanel(mc[i],i);
		  }
		  cc=channels[0];
		  cc.getChannel().programChange(instrumentnums[0]);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }

    //Method: Closes the sound components 
    public void close(){
	if (syn!=null) {
	    syn.close();
	}
	syn=null;
	instruments=null;
    }
    
    //Method: Adds a MidiEvent to a selected channel's track. Takes command parameter (earlier defined as an int in the Synth class) and an int that is the command's argument. 
    public void addEvent(int command, int n) {
	ShortMessage m=new ShortMessage();
	long dur=System.currentTimeMillis()-stime;
	long tic=dur*seq.getResolution()/500;
	try {
	    if (command==PROGRAM) {
		m.setMessage(command,cc.getChannelNum(),n,0);
	    } else {
		m.setMessage(command,cc.getChannelNum(),n,100);
	    }
	    MidiEvent me = new MidiEvent(m, tic);
	    cc.getTrack().add(me);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }


    /* Class: allows you to record, clear, play, and save tracks */ 
    class Recorder implements ActionListener {
	private Box one=Box.createHorizontalBox();
	private JButton record=new JButton("Record");
	private JButton clear=new JButton("Clear");
	private JButton play =new JButton("Play");
	private JButton save=new JButton("Save");
	
	//Constructor: Sets up buttons in GUI, creates a sequence for recording
	public Recorder() {
	    numOfTracks=0;
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

	//Method: Decides on button action, self explanatory
	public void actionPerformed(ActionEvent a) {
	    JButton button = (JButton) a.getSource();
	    if (button.equals(play)) {
		if (!playing) {
		    if (cc.getTrack()!=null) {
			play.setText("Stop");
			startPlay();
			record.setEnabled(false);
			clear.setEnabled(false);
			save.setEnabled(false);
			piano.requestFocus();
		    } else {
			System.out.println("ERROR: Can't play, null track");
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
		if (cc.getTrack()!=null && cc.getTrack().size()!=0) {
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
	
	//Method: Opens up a Save dialog and allows you to save the recorded sequence as a .mid file
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
			System.out.println("ERROR: Problems writing file");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	
	//Method: Starts recording 
	public void startRecord() {
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    recording=true;
	    //If the selected channel's track is not empty, it is cleared in order to be overwritten
	    if (cc.getTrack()!=null) {
		clearTrack(cc.getTrack());
	    }
	    cc.setTrack(seq.createTrack());
	    numOfTracks++;
	    seqr.recordEnable(cc.getTrack(),cc.getChannelNum());
	    stime=System.currentTimeMillis();
	    syn.loadInstrument(instruments[currentInstrument]);
	    //Adds a programChange event to the track to set the channel's instrument
	    addEvent(PROGRAM,currentInstrument);
	    //Plays existing tracks while you record
	    seqr.start();
	    playing=true;
	}

	//Method: Stops recording
	public void stopRecord() {
	    recording=false;
	    if (playing==true) {
		seqr.stop();
		seqr.close();
		playing=false;
	    }
	}

	//Method: Starts playback of existing tracks
	public void startPlay() {
	    playing=true;
	    try {
		seqr.open();
		seqr.setSequence(seq);
		seqr.start();
	    } catch (Exception e) {
		e.printStackTrace();
	    }	   
	}

	//Method: Stops playback
	public void stopPlay() {
	    playing=false;
	    seqr.stop();
	    seqr.close();
	}

	//Method: Clears all tracks in all channels
	public void clearAll() {
	    Track current;
	    for (int i=0;i<channels.length;i++) {
		current=channels[i].track;
		seq.deleteTrack(current);
		channels[i].track=null;
	    }
	    numOfTracks=0;
	}

	//Method: Clears an individual track
	public void clearTrack(Track t) {
	    numOfTracks--;
	    seq.deleteTrack(t);
	    cc.setTrack(null);
	    
	}
    }
    
    /* Class: Allows you to select an instrument from 16 options */
    class InstrumentTable {
	private int rownum=4;
	private int colnum=4;
	private Box box = Box.createVerticalBox();
	private JTable table;
	private String[] columnNames={"1","2","3","4"};
	private int[][] instrumentarray=new int[4][4];
	private int ccol=0;
	private int crow=0;
	
	//Constructor: Creates the table of 16 instruments
	public InstrumentTable() {    
	    //Creates 2D array for easier cell selection and navigation
	    int c=0;
	    for (int i=0;i<4;i++) {
		for (int j=0;j<4;j++) {
		    instrumentarray[j][i]=instrumentnums[c];
		    c++;
		}
	    }
	    //Constructs the table 
	    TableModel model = new AbstractTableModel() {
		    public int getRowCount() {return rownum;}
		    public int getColumnCount() {return colnum;}
		    public Object getValueAt(int row, int col) {
			if (instruments!=null) {
			    
			    return " "+instruments[instrumentarray[row][col]].getName();
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
	  
	    //Sets action for row selection
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
	    //Sets action for column selection
	    lsm=table.getColumnModel().getSelectionModel();
	    lsm.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel sm=(ListSelectionModel) e.getSource();
			if (!sm.isSelectionEmpty()) {
			    
			    ccol=table.getSelectedColumn();
			}
			changeProgram(instrumentarray[crow][ccol]);
		    }
		});
	    //Sets various table attributes
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
		column.setMinWidth(100);
		column.setMaxWidth(100);
	    }
	    box.add(header);
	    box.add(table);

	}
	
	public Box getBox() {
	    return box;
	}
	
	//Method: Changes the loaded instrument and changes the current channel's instrument. If it is recording, adds a programChange event to the track
	public void changeProgram(int i) {
	    currentInstrument=i;
	    syn.loadInstrument(instruments[currentInstrument]);
	    cc.getChannel().programChange(currentInstrument);
	    if (recording) {
		addEvent(PROGRAM,currentInstrument);
	    }
	    piano.requestFocus();    
	}	
    }
    
    /* Class: Allows you to select a channel to which you wish to record a track */
    class ChannelTable {
	private JTable table;
	private Box box=Box.createVerticalBox();
	private int colnum=1;
	private int rownum=4;
	private String[] columnName={"Channels"};
	private String s=new String(" Empty");
	    
	//Constructor: Sets up the table, as in the previous class. 
	public ChannelTable() {
		TableModel model = new AbstractTableModel() {
		    public int getRowCount() {return rownum;}
		    public int getColumnCount() {return colnum;}
		    public Object getValueAt(int row, int col) {
			if (numOfTracks!=0 && channels[row].track!=null) {
			    return " Channel "+(row+1)+": "+instruments[channels[row].channel.getProgram()].getName();
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
	    columnA.setMinWidth(145);
	    columnA.setMaxWidth(145);
	    box.add(header);
	    box.add(table);
		
	}
	
	public Box getBox() {
	    return box;
	}
    }
    
    /* Class: Stores information about each channel */
    class Chanel {
	private MidiChannel channel;
	private int channelnum;
	private Track track;
	public Chanel(MidiChannel c, int cnum) {
	    channel=c;
	    channelnum=cnum;
	}
	public MidiChannel getChannel() {
	    return channel;
	}
	public int getChannelNum() {
	    return channelnum;
	}
	public Track getTrack() {
	    return track;
	}
	public void setTrack(Track t) {
	    this.track=t;
	}
    }
    
    /*Class: Represents each key in the piano */
    class Key extends Rectangle {
	private boolean on=false;
	private int keynum;
	//Constructor: Creates new rectangle with position (x,y), size (wh), and the MIDI note number n
	public Key(int x, int y, int w, int h, int n) {
	    super(x,y,w,h);
	    keynum=n;
	}

	public boolean isOn() {
	    return on;   
	}

	//Method: Turns on the MIDI note associated with the Key. If it is recording, adds NOTEON event to track
	public void turnOn(Key k) {
	    on=true;
	    cc.getChannel().noteOn(k.keynum, 100);
	    if (recording) {
		addEvent(NOTEON, k.keynum);
	    }
	}

	public void turnOff(Key k) {
	    on=false;
	    cc.getChannel().noteOff(k.keynum);
	    if (recording) {
		addEvent(NOTEOFF, k.keynum);
	    }
	}
    }
    
    /* Class: Creates an interactive piano */
    class Piano extends JPanel implements MouseListener, KeyListener {
	ArrayList<Key> whitekeys=new ArrayList<Key>();
	ArrayList<Key> blackkeys=new ArrayList<Key>(); 
	ArrayList<Key> keys=new ArrayList<Key>();
	//Map linking keyboard characters to Piano Keys for use with the KeyListener
	HashMap<Character,Key> charKeys=new HashMap<Character,Key>();
	char[] blackchars={'2','3','5','6','7','9','0','=','\u0008'};
	char[] whitechars={'q','w','e','r','t','y','u','i','o','p','[',']',(char) 92};
	Key pkey;
	
	//Constructor: Creates arrays of white and black keys
	public Piano() {
	    setPreferredSize(new Dimension(600,240));
	    setBorder(BorderFactory.createLineBorder(Color.black));
	    //Increases by position and pitch number, starting at middle C (60)
	    for (int i=0, x = 0, y= 0, keystart=60;i<22;i++, x+=23, y+=40,keystart++) {
		//Checks if the pitch number corresponds to a black key
		if (keystart!=61 && keystart!=63 && keystart!=66 && keystart!=68 && keystart!=70 && keystart!=73 && keystart!=75 && keystart!=78 && keystart!=80) { 
		    whitekeys.add(new Key(y,0,40,230,keystart));
		} else {
		    if (keystart==61) {
			blackkeys.add(new Key(26,0,25,150,keystart));
		    } else {
			blackkeys.add(new Key(x,0,25,150,keystart));
		    }
		    y-=40;
		}
	    }
	    keys.addAll(whitekeys);
	    keys.addAll(blackkeys);

	    //Maps characters to appropriate keys
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
	
	
	//Method: Takes the array we filled in piano and paints it onto the GUI into a piano shape
	public void paint (Graphics thing)
	{
	    Graphics2D g = (Graphics2D) thing;
	    Dimension d = getSize();
	    
	    g.setBackground(Color.darkGray);
	    g.clearRect(0, 0, 500, 700);
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

	//Methods: These deal with the KeyListener. Turn on when an appropriate character is pressed, off when released. 
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
	    char key=e.getKeyChar();
	    if (charKeys.containsKey(key)) {
		if (!charKeys.get(key).isOn()) {
		    charKeys.get(key).turnOn(charKeys.get(key));
		    repaint();
		}
	    }
	}	
	public void keyReleased(KeyEvent e) {
	    char key=e.getKeyChar();
	    if (charKeys.containsKey(key)){
		charKeys.get(key).turnOff(charKeys.get(key));
		repaint();	
	    }
	}

	//Methods: These deal with the MouseListener. 
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
	    pkey=getKey(e.getPoint());
	    if (pkey!=null) {
		pkey.turnOn(pkey);
		repaint();
	    }
	}
	public void mouseReleased(MouseEvent e) {
	    if (pkey!=null) {
		pkey.turnOff(pkey);
		repaint();
		pkey=null;
	    }
	    this.requestFocus();
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	//Method: Used in MouseListener to get the location of a click, checks if the click falls within the coordinates of a Key.
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

    } //End Piano class
  
} //End Synth class

	
	
	    
