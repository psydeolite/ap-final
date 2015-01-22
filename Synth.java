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
import javax.swing.border.EmptyBorder;
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
    //Chanel[] channels;
    Chanel cc;
    TrackTable tracktable;
    Track[] tracks;
    Instrument[] instruments;
    int[] instrumentnums;
    int ci;
    int trackindex;
    boolean recording;
    boolean playing;
    long stime;
    Track ctrack;
    Recorder recorder;
    InstrumentTable instrumentable;
    private Container pane;
    private JPanel x;
    private JLabel label;
    private JFrame frame;
    private Box whole;

    /*sets up the GUI 

     */ 
    public Synth() {
	open();
	setSize(300,300);
        frame = new JFrame("Do-Re-Midi");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//frame.requestFocusInWindow();
        JPanel topbox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topbox.setBorder(new EmptyBorder(20, 60, 0, 0) );
	topbox.add(piano = new Piano());
	piano.setFocusable(true);
	//piano.requestFocusInWindow();
	instrumentable=new InstrumentTable();
	recorder=new Recorder();
	tracktable=new TrackTable();
	whole = Box.createVerticalBox();
	whole.add(recorder.getBox());
	whole.add(topbox);
	JPanel x = new JPanel();
	x.add(tracktable.getBox());
        x.setBorder(new EmptyBorder(0, 10, 10, 10) );
	whole.add(x);
	whole.add(instrumentable.getBox());
	Container content = frame.getContentPane();
        whole.setBorder(new EmptyBorder(10, 50, 50, 0) );
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
		//System.out.println("opening synth");
		syn.open();
		//System.out.println("done opening synth");
		seq=new Sequence(Sequence.PPQ, 10);
		seqr=MidiSystem.getSequencer();
		//seqr.open();
		//System.out.println("getting soundbank");
		Soundbank s=syn.getDefaultSoundbank();
		if (s!=null) {
		    //System.out.println("instrumenting");
		    instruments=syn.getDefaultSoundbank().getInstruments();
		    instrumentnums=new int[5];
		    instrumentnums[0]=5;
		    instrumentnums[1]=99;
		    instrumentnums[2]=45;
		    instrumentnums[3]=61;
		    instrumentnums[4]=81;
		    //syn.loadInstrument(instruments[instrumentnums[0]]);
		    syn.loadInstrument(instruments[instrumentnums[0]]);
		    ci=0;
		}
		MidiChannel mc[]=syn.getChannels();
		cc=new Chanel(mc[0],1);
		/*for (int i=0;i<5;i++) {
		    channels[i]=new Chanel(mc[i],i);
		}
		cc=channels[0];
		*/
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
	//channels=null;
    }
    
    public void addEvent(int command, int n) {
	ShortMessage m=new ShortMessage();
	long dur=System.currentTimeMillis()-stime;
	long tic=dur*seq.getResolution()/500;
	try {
	    if (command==192) {
		
		m.setMessage(command,cc.channelnum,n,0);
		//m.setMessage(192,0,5,100);
		//System.out.println("wtf");
		if (recording) {
		    System.out.println("program change WHILE RECORDING");
		} 
	    } else {
		m.setMessage(command,cc.channelnum,n,100);
	    }
	    MidiEvent me = new MidiEvent(m, tic);
	    
	    ctrack.add(me);
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
	JButton thing=new JButton("do the thing");
	
	public Recorder() {
	    tracks=new Track[4];
	    record.addActionListener(this);
	    one.add(record);
	    clear.addActionListener(this);
	    one.add(clear);
	    play.addActionListener(this);
	    one.add(play);
	    save.addActionListener(this);
	    one.add(save);
//------------debugging code
	    thing.addActionListener(this);
	    one.add(thing);
//------------end debugging code
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
		    if (ctrack!=null) {
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
	   
//----------------debugging code start
		} else if (button.equals(thing)) {
		try {
		    seqr.open();
		    seqr.setSequence(seq);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		    ctrack=seq.createTrack();
		    addEvent(192, 5);
		    addEvent(NOTEON, 100);
		    addEvent(NOTEOFF, 100);
		    seqr.start();
//---------------debugging code end
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
		if (ctrack!=null && ctrack.size()!=0) {
		    clearAll();
		} 
	    } else if (button.equals(save)) {
		try {
		    save();
		} catch (Exception e) {}
	    }
	}
	
	public void save() {
	    //System.out.println(""+track.size());
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
		    /*int[] types=MidiSystem.getMidiFileTypes(seq);
		    if (types.length!=0) {
			if (MidiSystem.write(seq,types[0],rf)==-1) {
			    throw new IOException("problems writing file");
			}
		    } else {
			System.out.println("can't save this");
			}*/
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
	    //System.out.println("trackindex: "+trackindex);
	    if (tracks[trackindex]!=null) {
		//System.out.println("if trackindex isn't null");
		clearTrack(trackindex);
	    }
	    ctrack=seq.createTrack();
	    
	    //cc.channel.programChange(instrumentnums[ci]);
	    System.out.println("ci in startRecord: "+ci);
	    
	    System.out.println("Instrument denoted by ci in startRecord: "+instruments[instrumentnums[ci]]);
	    tracks[trackindex]=ctrack;
	    seqr.recordEnable(ctrack,cc.channelnum);
	    stime=System.currentTimeMillis();
	    addEvent(PROGRAM,instrumentnums[ci]);
	    if (tracks.length!=0) {
		//System.out.println(seq.getTracks().length);
		seqr.start();
		playing=true;
	    }
	    //stime=System.currentTimeMillis();
	  
	}

	public void stopRecord() {
	    recording=false;
	    if (playing==true) {
		seqr.stop();
		seqr.close();
		playing=false;
	    }
	    //System.out.println(tracks.size());
	}

	public void startPlay() {
	    playing=true;
	    try {
		seqr.open();
		seqr.setSequence(seq);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    //System.out.println("program:"+cc.channel.getProgram());
	    seqr.start();
	}

	public void stopPlay() {
	    playing=false;
	    seqr.stop();
	    seqr.close();
	}

	public void clearAll() {
	    Track current;
	    for (int i=0;i<4;i++) {
		current=tracks[i];
		seq.deleteTrack(current);
		tracks[i]=null;
	    }
	    /*while (tracks.length>0) {
		current=tracks[0];
		seq.deleteTrack(current);
	      
		current=tracks[0];

		System.out.println("currentsize: "+current.size());
		while (current.size()>1) {
		    System.out.println(""+current.size());
		    current.remove(current.get(0));
		    
		}
		current.remove(current.get(0));
		//System.out.println("postclear:"+track.size());
		tracks[0]=null;
		}*/
	    trackindex=0;
	    ctrack=null;
	}

	//clears individual track
	public void clearTrack(int tindex) {
	    
	    seq.deleteTrack(tracks[tindex]);
	    System.out.println("deleted track");
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
				    //System.out.println("row: "+table.getSelectedRow());
				    ci=table.getSelectedRow();
				    System.out.println("ci after being selected: "+ci);
				    //changeProgram(table.getSelectedRow());
				    changeProgram();
				} 
			    }
			}
		    });
		table.setRowSelectionInterval(0,0);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(false);
		table.setRowMargin(5);
		JTableHeader header = table.getTableHeader(); 
		header.setBackground(Color.pink);
		table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
		TableColumn columnA = table.getColumn(table.getColumnName(0));
		columnA.setMinWidth(350);
		columnA.setMaxWidth(350);
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		box.add(header);
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
	
	    public void changeProgram() {
		//ci=select;
		//System.out.println(ci);
		if (instruments!=null) {
		    syn.loadInstrument(instruments[instrumentnums[ci]]);
		} else {
		    System.out.println("no instruments to speak of");
		}
		cc.channel.programChange(instrumentnums[ci]);
		if (recording) {
		    addEvent(PROGRAM,instrumentnums[ci]);
		}
	    
	    }
	
	    //instrument selection
	}
    
	class TrackTable {
	    private JTable table;
	    private Box box=Box.createVerticalBox();
	    private int colnum=1;
	    private int rownum=4;
	    private String[] columnName={"Tracks"};
	    private String s=new String("Empty");
	    

	    public TrackTable() {
		
		TableModel model = new AbstractTableModel() {
			public int getRowCount() {return rownum;}
			public int getColumnCount() {return colnum;}
			public Object getValueAt(int row, int col) {
			    if (tracks!=null && tracks[row]!=null) {
				return "Track "+(row+1)+": "+instruments[ci].getName();
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
				    trackindex=table.getSelectedRow();
				    //ctrack=tracks[trackindex];
				}
			    }
			}
		    });
		table.setRowSelectionInterval(0,0);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(false);
		table.setRowMargin(5);
		JTableHeader header = table.getTableHeader(); 
		header.setBackground(Color.pink);
		table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
		TableColumn columnA = table.getColumn(table.getColumnName(0));
		columnA.setMinWidth(200);
		columnA.setMaxWidth(200);
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
	    System.out.println("key is pressed");
	    char key=e.getKeyChar();
	    if (charKeys.containsKey(key)) {
		keyPress(charKeys.get(key));
	    }
	}
	public void keyReleased(KeyEvent e) {
	    char key=e.getKeyChar();
	    if (charKeys.containsKey(key)){
		keyUnpress(charKeys.get(key));
	    }
	}
	public void mouseClicked(MouseEvent e) {
	    
	}
	public void mousePressed(MouseEvent e) {
	    pkey=getKey(e.getPoint());
	    if (pkey!=null) {
		keyPress(pkey);
	    }
	}
	public void mouseReleased(MouseEvent e) {
	    if (pkey!=null) {
		//System.out.println("mouse released");
		keyUnpress(pkey);
		pkey=null;
	    }
	    this.requestFocus();
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
		    
		    //System.out.println("gotPitch: "+keys.get(i).keynum);
		    return keys.get(i);
		}
	    }
	    return null;
	}
    }
    
}

	
	
	    
