package com.eb03.dimmer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BTManager extends Transceiver {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter(); ;


    private BluetoothSocket mSocket = null;

    private ConnectThread mConnectThread = null;
    private WritingThread mWritingThread = null;


    @Override
    public void connect(String id) {
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(id);
        disconnect();
        mConnectThread = new ConnectThread(device);
        setState(STATE_CONNECTING);
        mConnectThread.start();
        setState(STATE_CONNECTED);
    }


    @Override
    public void disconnect() {
        mConnectThread = null ;
        setState(STATE_NOT_CONNECTED);

    }


    @Override
    public void send(byte[] b) {
    // Envoye trame encodé
    }

    /**
     * Classe du Thread de connexion au device
     */
    private class ConnectThread extends Thread{

        public ConnectThread(BluetoothDevice device) {
            //BluetoothSocket socket = null;
            try {
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            mAdapter.cancelDiscovery();
            try {
                mSocket.connect();
            } catch (IOException e) {
                disconnect();
            }
            mConnectThread = null;
            startReadWriteThreads();
        }
    }

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }


    private void startReadWriteThreads(){
        // instanciation d'un thread de lecture

        mWritingThread = new WritingThread(mSocket);
        Log.i("ConnectThread","Thread WritingThread lancé");
        mWritingThread.start();
        setState(STATE_CONNECTED);
    }



    /**
     * Classe du Thread d'écriture vers le device
     */
    private class WritingThread extends Thread {
        private OutputStream mOutStream;
        private ByteRingBuffer mRingBuffer ;
        private static final String TAG = "MY_APP_DEBUG_TAG";
        private Handler handler; // handler that gets info from Bluetooth service


        public WritingThread(BluetoothSocket mSocket) {
            mRingBuffer = new ByteRingBuffer(1024) ;
            try {
                mOutStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(mSocket != null){
                try {
                    mOutStream.write(1); // Envoi des trames pas encore effectuée réalisé par manque de temps
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mOutStream.write(bytes);
                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }
    }

}

/**
 * ByteBufferRing est une classe permettant de modéliser un buffer circulaire nécessaire à notre projet .
 * Un buffer circulaire est une structure de données utilisant
 * un buffer de taille fixe et dont le début et la fin sont considérés comme connectés utile afin de gérer des flux de données.
 */
class ByteRingBuffer {
    private final int taille;
    private final byte[] buffer;
    private volatile int write, read;

    /**
     * Constructeur
     * @param taille
     */
    public ByteRingBuffer(int taille) {
        this.taille = taille;
        this.buffer = new byte[taille];
        this.read = 0;
        this.write = -1;
    }

    /**
     * Ajoute un élément dans le buffer circulaire
     * @param element
     * @return
     */
    public boolean put(byte element) {
        if (isNotEmpty()) {
            int nextW = write + 1;
            buffer[nextW % taille] = element;
            write++;
            return true;
        }
        return false;
    }

    /**
     * Ajoute plusieurs éléments dans le buffer circulaire
     * @param elements tableau d'element à ajouter
     * @return
     */
    public void put(byte[] elements) {
        for (byte b : elements)
            put(b);
    }

    /**
     * Récupère la prochaine valeur à lire
     * @return
     */
    public Byte bytesToRead() {
        if (isNotEmpty()) {
            byte nextValue = buffer[read % taille];
            read++;
            return nextValue;
        }
        return null;
    }

    /**
     * Récupère un élément dans le buffer circulaire et incrémente.
     * @return element recuperé
     */
    public byte get(){
        if(isNotEmpty()) {
            byte element = buffer[read];
            read = (read + 1) % taille;
            System.out.println("\nELEMENT : " + element);
            System.out.println("READ INDEX : " + read);
            return element;
        }else{
            return -1;
        }
    }

    /**
     * Récupère tous les éléments dans le buffer circulaire et incrémente.
     * @return Buf le nouveau tableau incrementé.
     */
    public byte[] getAll(){
        int taille = bytesToRead();
        byte[] buf = new byte[taille-1];
        for (int i = 0; i < taille -1; i++) {
            buf[i]=get();
        }
        return buf;
    }


    /**
     * Affiche le contenu d'un tableau de byte
     */
    public void display() {
        System.out.print("[ ");
        for(int i = 0; i < this.getBuffer().length ; i++)
            System.out.print(this.getBuffer()[i] + "; ");
        System.out.print(" ]");
    }

    /**
     * Affiche le contenu d'un tableau de byte
     * @param b tableau de byte
     */
    public void display(byte [] b) {
        System.out.print("[ ");
        for(int i = 0; i < b.length ; i++)
            System.out.print(b[i] + "; ");

        System.out.print(" ]");
    }


    public int getTaille() {
        return taille;
    }

    /**
     * Verification si la taille oui ou non inf à 0
     * @return boolean
     */
    private boolean isNotEmpty() {
        if(this.taille > 0) {
            return true ;
        }
        return false ;
    }



    public byte[] getBuffer() {
        return buffer;
    }
}


