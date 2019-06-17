package vmc.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import vmc.core.log;

/**
 * <b>Create Date:</b> 9/22/16<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public class SerialPortController implements ISerialPortController {
    private static final String TAG = "SerialPortController";
    private String mDevice;
    private int mBaudrate;
    private int mFlag;
    private IVMCProtocol mProtocol;

    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private boolean isStop;
    private LinkedBlockingQueue<String> mSendDataQueue;

    public SerialPortController(String device, int baudrate, IVMCProtocol protocol) {
        this(device, baudrate, 0, protocol);
    }

    public SerialPortController(String device, int baudrate, int flag, IVMCProtocol protocol) {
        this.mDevice = device;
        this.mBaudrate = baudrate;
        this.mFlag = flag;
        this.mProtocol = protocol;

        this.mSendDataQueue = new LinkedBlockingQueue<String>();
        mProtocol.setSerialPortController(this);
    }

    public void start() throws IOException {
        if (null != mSerialPort) {
            throw new RuntimeException("Controller has already started.");
        }
        log.d(TAG, "start.");

        mSerialPort = new SerialPort(new File(mDevice), mBaudrate, mFlag);
        isStop = false;
        mOutputStream = mSerialPort.getOutputStream();


        Executors.newSingleThreadExecutor().execute(new ReadRunnable());
//        Executors.newSingleThreadExecutor().execute(new WriteRunnable());
    }

    public void stop() {
        log.d(TAG, "stop.");
        isStop = true;
        mSerialPort.close();
        mSerialPort = null;
    }

    @Override
    public void sendData(byte[] data) {
        try {
            mOutputStream.write(data);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        mSendDataQueue.offer(data);
    }

    private class ReadRunnable implements Runnable {

        @Override
        public void run() {
            byte[] buffer = new byte[64];
            int length;
            final InputStream ins = mSerialPort.getInputStream();
            while (!isStop) {
                try {
                    length = ins.read(buffer);
                    if (length > 0) {
                        mProtocol.onDataReceived(buffer, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class WriteRunnable implements Runnable {
        @Override
        public void run() {
            final OutputStream ops = mSerialPort.getOutputStream();
            String data;
            while (!isStop) {
                data = mSendDataQueue.poll();
                if (null != data) {
                    try {
                        ops.write(data.getBytes());
                        ops.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
