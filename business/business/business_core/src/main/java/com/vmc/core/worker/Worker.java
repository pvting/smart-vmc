package com.vmc.core.worker;

import com.want.vmc.core.Constants;

import java.util.Random;

/**
 * <b>Create Date:</b> 04/11/2016<br>
 * <b>Author:</b> Gordon<br>
 * <b>Description:</b> <br>
 */
public abstract class Worker extends Thread implements Constants {
    private static final int RANDOM_INT = 1000 * 60;

    private final Object lock = new Object();
    private boolean isStarted = false;
    private boolean isStop = true;
    private Random mRandom = new Random();
    private int mMachineRandomInt;

    {
        mMachineRandomInt = mRandom.nextInt(RANDOM_INT);
    }

    protected Worker() {
        setDaemon(true);
    }

    public void startWork() {
        isStarted = true;
        this.isStop = false;
        this.start();
    }

    @Override
    public synchronized void start() {
        if (!isStarted) {
            throw new IllegalStateException("Workder must be started by 'startWork()' method.");
        }
        super.start();
    }

    public void stopWork() {
        this.isStop = true;
    }

    protected void onPrepare() {
        safeWait(mMachineRandomInt + mRandom.nextInt(RANDOM_INT));
    }

    protected void onFinish() {

    }

    protected abstract void onWorking();

    @Override
    public final void run() {
        super.run();
        onPrepare();
        while (!isStop) {
            onWorking();
        }
        onFinish();
    }

    protected void safeWait(final long timesInMillions) {
        try {
            synchronized (lock) {
                lock.wait(timesInMillions);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void safeWait() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void safeNotify() {
        synchronized (lock) {
            lock.notify();
        }
    }
}
