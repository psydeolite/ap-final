/* code copied from kevinboone.net/javamidi.html
   testing writing midi file
*/ 

import java.io.*;
import java.util.*;

public class MidiGen {
    static final int semiquaver=4;
    static final int quaver=8;
    static final int crotchet=16;
    static final int minim=32;
    static final int semibreve=64;

    static final int header[]=new int[] {
	0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x06,
	0x00, 0x00,
	0x00, 0x01,
	0x00, 0x10,
	0x4d, 0x54, 0x72, 0x68
    };

    static final int footer[]=new int[] {
	0x01,0xFF,0x2F,0x00
    };
    
    static final int tempoEvent[]=new int[] {
	0x00,0xFF,0x51,0x03,
	0x0F, 0x42, 0x40
    };

    static final int keySigEvent[]=new int[] {
	0x00,0xFF,0x59,0x02,
	0x00,
	0x00
    };

    static final int timeSigEvent[]=new int[] {
	0x00,0xFF,0x58,0x04,
	0x04,
	0x02,
	0x30,
	0x08
    };

    protected ArrayList<int[]> playEvents;
    
    public MidiGen() {
	playEvents=new ArrayList<int[]>();
    }

    public void writeToFile(String filename) throws IOException {
	FileOutputStream fos=new FileOutputStream(filename);
	fos.write(intArrayToByteArray(header));

	int size=tempoEvent.length+keySigEvent.length+timeSigEvent.length+footer.length;
	for (int i=0;i<playEvents.size();i++) {
	    size+=playEvents.get(i).length;
	}
	
	int high=size/256;
	int low=size=(high*256);
	fos.write ((byte) 0);
	fos.write((byte) 0);
	fos.write((byte) high);
	fos.write((byte) low);

	fos.write (intArrayToByteArray(tempoEvent));
	fos.write (intArrayToByteArray(keySigEvent));
	fos.write (intArrayToByteArray(timeSigEvent));
	
	for (int i=0;i<playEvents.size(); i++) {
	    fos.write(intArrayToByteArray (playEvents.get(i)));
	}

	fos.write(intArrayToByteArray(footer));
	fos.close();
	System.out.println(playEvents);
    }

    protected static byte[] intArrayToByteArray(int[] ints) {
	int l=ints.length;
	byte[] out=new byte[l];
	for (int i=0;i<l; i++) {
	    out[i]=(byte)ints[i];
	}
	return out;
    }

    public void noteOn(int delta, int note, int velocity) {
	int[] data=new int[4];
	data[0]=delta;
	data[1]=0x90;
	data[2]=note;
	data[3]=velocity;
	playEvents.add(data);
    }

    public void noteOff(int delta, int note) {
	int[] data=new int[4];
	data[0]=delta;
	data[1]=0x80;
	data[2]=note;
	data[3]=0;
	playEvents.add(data);
    }

    public void progChange(int prog) {
	int[] data=new int[3];
	data[0]=0;
	data[1]=0xC0;
	data[2]=prog;
	playEvents.add(data);
    }

    public void noteOnOffNow(int duration,int note, int velocity) {
	noteOn(0,note,velocity);
	noteOff(duration, note);
    }

    public void noteSequenceFixedVelocity (int[] sequence, int velocity) {
	boolean lastWasRest=false;
	int restDelta=0;
	for (int i=0;i<sequence.length;i+=2) {
	    int note=sequence[i];
	    int duration=sequence[i+1];
	    if (note==0) {
		restDelta+=duration;
		lastWasRest=true;
	    } else {
		if (lastWasRest) {
		    noteOn(restDelta,note,velocity);
		    noteOff(duration, note);
		} else {
		    noteOn(0,note,velocity);
		    noteOff(duration, note);
		}
		restDelta=0;
		lastWasRest=false;
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	MidiGen mf = new MidiGen();
	mf.noteOn(0,60,127);
	mf.noteOn(0,64,127);
	mf.noteOn(0,67,127);
	
	mf.noteOff(minim,60);
	mf.noteOff(0,64);
	mf.noteOff(0,67);

	mf.noteOnOffNow(quaver,60,127);
	mf.noteOnOffNow(quaver,62,127);
	mf.noteOnOffNow(quaver,64,127);
	mf.noteOnOffNow(quaver,65,127);
	mf.noteOnOffNow(quaver,67,127);
	mf.noteOnOffNow(quaver,69,127);
	mf.noteOnOffNow(quaver,71,127);
	mf.noteOnOffNow(quaver,72,127);

	mf.writeToFile("test1.mid");
    }
}
