Do-Re-Midi 
========

Mariya Gedrich and Rabia Akhtar Period 2

This project includes a Synthesizer. There are 16 types of instruments you can play using the keyboard. You can record, play, and save what you do in .mid files. 

Installation
==========
1. Pull folder
2. Compile Driver.java 
3. Run Driver 

Use
=============
Play with the synthesizer. You can either play it by using your mouse or by using your keyboard. 
Use the QWERTY row to play the white keys (middle C starting at Q) and use the row above to play the black keys. 
Use each channel to record a different recording to use different instruments in one file. When you are done, click save to save your recording as a midi file. When you want to start over, press clear. 

Changes
=====
12/19 - created repo <br/>
12/30 - started creating GUI - RA <br/>
12/30 - created Synth, Key (basic constructor and turn on/off), Piano class - MG <br/>
01/05 - wrote empty container and main function for GUI, outlined basic classes, wrote turnOn/Off in Key class, Piano constructor now adds keys to black/white keylists, wrote Piano functions keyPress, keyUnpress, keySound. - MG <br/>
01/07 - wrote open, close methods, made Chanel class, edited methods to use currentchannel - MG <br/>
01/07 - made Piano box and class, edited layout and made buttons. Went home and got some haphazard looking keys to show up on a piano looking rectangle. - RA <br/>
01/08 - Made a keyboard looking thing. Some of the keys aren't positioned correctly. - RA <br/>
01/11 - Fixed key positioning. - MG <br/>
01/12 - Made piano bigger and Mariya worked on the mouselistener - RA
 <br/>
01/13 - Fixed NullPointerException Error, adjusted piano keys, fixed keypress things - MG <br/>
01/13 - Added repaint function and had the piano actually respon to clicks and play keys! - RA <br/>
01/14 - Error fix: individual white keys respond to clicks - MG <br/>
01/15 - wrote addEvent (to be continued...) and created Recorder class with startRecording and stopRecording functions - MG <br/>
01/18 - spent 5 hours uselessly trying to figure out layouts - RA <br/>
01/19 - fixed layout formatting, recording and playback now works. however, multi-voice recording does not. added vague instrument table - MG <br/>
01/19 - save button saves, and instrument table vaguely exists- MG <br/>
01/20 - instrument table and program change work, Rabia adjusted piano size - MG <br/>
01/20 - added some comments - RA <br/>
01/20 - button bug and instrument loading bug fixed. persisting issues: random images of piano and table appear on click, playback of recorded stuff does not acknowledge program change, and much to my anguish, the files created by the program do not play, which actually makes no sense because a saved file that played yesterday on my computer does not play anymore?? - MG <br/>
01/21 - fixed GUI, changed layout, added border, moved things around, changed colors, etc. etc. - RA <br/>
01/21 - added table for tracks (soon to be channels because I discovered a **major** bug with that), saving works, program change acknowledged when recording. also added keylistener. remaining things: ANNOTATE IT all nice like in javadocs format, fix bug - MG <br/> 
01/22 - fixed layout, cleaned up code. Goals: do java docs stuff tonight. - RA <br/>
01/22 - fixed bugs (channel vs. track, program change vs. channel table), changed layout and background color. to do: documentation and cleanup, possibly center the instrument/channeltable part. - MG <br/>
