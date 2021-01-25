package com.eb03.dimmer;

public abstract class Transceiver {

    public static final int STATE_NOT_CONNECTED = 0; // non connecté
    public static final int STATE_CONNECTING = 1;    // connexion en cours
    public static final int STATE_CONNECTED = 2;     // connecté


   private int mState;
   private String address;
   private TransceiverListener mTransceiverListener;
   private FrameProcessor mFrameProcessor;

   public void attachFrameProcessor(FrameProcessor frameProcessor){
       mFrameProcessor = frameProcessor;
   }

    public void detachFrameProcessor(){
       mFrameProcessor = null;
    }

    /*********************************************************************************************
     *
     *                                         GETTER/SETTER
     *
     ********************************************************************************************/


    public void setTransceiverListener(TransceiverListener transceiverListener){
        mTransceiverListener = transceiverListener;
    }

    public String getAddress() {return address; }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
        if(mTransceiverListener != null){
            mTransceiverListener.onTransceiverStateChanged(state);
        }
    }

    /*********************************************************************************************
     *
     *                                         INTERFACES
     *
     ********************************************************************************************/

    public interface TransceiverListener{
       void onTransceiverDataReceived();
       void onTransceiverStateChanged(int state);
       void onTransceiverConnectionLost();
       void onTransceiverUnableToConnect();
   }


    /*********************************************************************************************
     *
     *                                         METHODES ABSTRAITES
     *
     ********************************************************************************************/

    public abstract void connect(String id);
    public abstract void disconnect();
    public abstract void send(byte[] b);


}
