package com.eb03.dimmer;

public class OscilloManager implements  Transceiver.TransceiverListener {

    private static final byte setCalibrationDutyCyle = 0x0A ;


    private Transceiver mTransceiver = null ;
    private OscilloEventsListener mOscilloEventsListener = null ;
    FrameProcessor fm = new FrameProcessor() ;
    @Override
    public void onTransceiverDataReceived() {

    }

    @Override
    public void onTransceiverStateChanged(int state) {

    }

    @Override
    public void onTransceiverConnectionLost() {

    }

    @Override
    public void onTransceiverUnableToConnect() {

    }

    public void attachTransceiver(String address) {
        BTManager bt = new BTManager() ;
        bt.connect(address);
    }

    public void detachTransceiver() {
        mTransceiver.disconnect();
    }


    public void setCalibrationDutyCycle(float alpha) {
        //fm.toFrame(alpha);
    }
    public interface OscilloEventsListener {
        void onOscilloStateChange() ;
        void onOscilloUnableToConnect() ;
        void onOscilloConnectionLost() ;
    }



}
