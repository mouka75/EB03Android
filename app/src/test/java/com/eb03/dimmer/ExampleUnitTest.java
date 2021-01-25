package com.eb03.dimmer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    ByteRingBuffer t = new ByteRingBuffer(4) ;
    FrameProcessor f = new FrameProcessor();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testByteBufferRing() {
        byte[] buff = new byte[] {1,2,3} ;// 1 2 3

        t.display(); // 0 0 0 0
        t.put(buff);
        assertEquals(1 , (int) t.bytesToRead());
        t.display();// 1 2 3 0
        t.put((byte)4);
        assertEquals(2 , (int) t.bytesToRead());
        t.display();// 1 2 3 4
        t.put((byte)5); // Overflow un tour complet
        assertEquals(3 , (int) t.bytesToRead());
        t.display();// 5 2 3 4
        assertArrayEquals(new byte[] {5, 2, 3, 4} ,t.getBuffer()); // verification de la forme desirée


        t.get() ; // Element : 4 et Read index : 0
        t.get() ; // Element : 5 et Read index : 1
        t.get() ; // Element : 2 et Read index : 2
    }



    @Test
    public void testFrameProcessor() {
        byte[] frame = new byte[]{0x07,0x06,0x0C} ; // PAYLOAD
        f.toFrame(frame); // 5 0 2 7 6 12 -15 4
    }



}