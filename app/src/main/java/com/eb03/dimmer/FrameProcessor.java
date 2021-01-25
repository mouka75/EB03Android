package com.eb03.dimmer;


public class FrameProcessor {
    private final static byte HEAD = 0x05;
    private final static byte TAIL = 0x04;
    private static final byte setCalibrationDutyCyle = 0x0A ;


    /**
     * Cette methode encore le Payload associé à une
     * commande en une trame lisible qui peut ensuite être envoyée au transceiver.
     * @param b est le payload
     */
    public void toFrame(byte[] b) {
        System.out.print("PAYLOAD : ") ;
        afficher(b);
        byte[] trame = new byte[b.length+5];
        //System.out.println("Nombre : " +  (b.length+5));

        trame[0] = HEAD ;
        trame[trame.length-1] = TAIL ;
        trame[1] = (byte) 0 ;
        int valueLen = b.length-1 ;
        trame[2] = (byte) valueLen ;


        for (int i = 0 ; i < b.length ; i++){
            if(b[i] == 0x04 || b[i]== 0x05 || b[i] == 0x06){
                trame[i+3]= 0x06;
                trame[i+4] = (byte)(b[i]+0x06);
            }
            trame[i+3] = b[i] ;
        }

        byte ctrl = (byte) (b[b.length-1] + valueLen+1) ;
        //System.out.println("CTRL : " +  ctrl);
        trame[trame.length-2] = (byte) -ctrl;

        System.out.print("TRAME ENCODE : ") ;
        afficher(trame);
    }

    public void afficher(byte[] b) {
        System.out.print("[ ") ;
        for (int i = 0; i < b.length; i++) {
            System.out.print(b[i] + ", ");
        }
        System.out.print(" ]\n") ;
    }

    public void attachFrameProcessor(byte[] b) {

    }

    public void detachFrameProcessor(byte[] b) {

    }


}


