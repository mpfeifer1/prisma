package com.puzzletimer.timer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.TimerTask;

import javax.swing.JFrame;

import com.puzzletimer.models.Timing;
import com.puzzletimer.state.TimerListener;
import com.puzzletimer.state.TimerManager;

public class SpaceKeyTimer implements Timer {
    private enum State {
        READY_FOR_INSPECTION,
        READY,
        RUNNING,
        FINISHED,
    };

    private JFrame frame;
    private TimerManager timerManager;
    private boolean inspectionEnabled;
    private KeyListener keyListener;
    private TimerListener timerListener;
    private java.util.Timer repeater;
    private Date start;
    private State state;

    public SpaceKeyTimer(JFrame frame, TimerManager timerManager) {
        this.frame = frame;
        this.timerManager = timerManager;
        this.inspectionEnabled = false;
        this.repeater = null;
        this.start = null;
        this.state = this.inspectionEnabled ?
            State.READY_FOR_INSPECTION : State.READY;
    }

    @Override
    public String getTimerId() {
        return "KEYBOARD-TIMER-SPACE";
    }

    @Override
    public void setInspectionEnabled(boolean inspectionEnabled) {
        this.inspectionEnabled = inspectionEnabled;

        switch (this.state) {
            case READY_FOR_INSPECTION:
                if (!inspectionEnabled) {
                    this.state = State.READY;
                }
                break;

            case READY:
                if (inspectionEnabled) {
                    this.state = State.READY_FOR_INSPECTION;
                }
                break;
        }
    }

    @Override
    public void start() {
        this.keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != KeyEvent.VK_SPACE) {
                    return;
                }

                switch (SpaceKeyTimer.this.state) {
                    case RUNNING:
                        Date now = new Date();
                        if (now.getTime() - SpaceKeyTimer.this.start.getTime() > 250) {
                            SpaceKeyTimer.this.repeater.cancel();

                            SpaceKeyTimer.this.timerManager.finishSolution(
                                new Timing(SpaceKeyTimer.this.start, new Date()));

                            SpaceKeyTimer.this.state = State.FINISHED;
                        }
                        break;
                }

                SpaceKeyTimer.this.timerManager.pressLeftHand();
                SpaceKeyTimer.this.timerManager.pressRightHand();
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != KeyEvent.VK_SPACE) {
                    return;
                }

                switch (SpaceKeyTimer.this.state) {
                    case READY_FOR_INSPECTION:
                        SpaceKeyTimer.this.timerManager.startInspection();

                        SpaceKeyTimer.this.state = State.READY;
                        break;

                    case READY:
                        SpaceKeyTimer.this.timerManager.startSolution();

                        SpaceKeyTimer.this.start = new Date();
                        SpaceKeyTimer.this.repeater = new java.util.Timer();
                        SpaceKeyTimer.this.repeater.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SpaceKeyTimer.this.timerManager.updateSolutionTiming(
                                    new Timing(SpaceKeyTimer.this.start, new Date()));
                            }
                        }, 0, 5);

                        SpaceKeyTimer.this.state = State.RUNNING;
                        break;

                    case FINISHED:
                        SpaceKeyTimer.this.state = SpaceKeyTimer.this.inspectionEnabled ?
                            State.READY_FOR_INSPECTION : State.READY;
                        break;
                }

                SpaceKeyTimer.this.timerManager.releaseLeftHand();
                SpaceKeyTimer.this.timerManager.releaseRightHand();
            }
        };
        this.frame.addKeyListener(this.keyListener);

        this.timerListener = new TimerListener() {
            @Override
            public void inspectionFinished() {
                SpaceKeyTimer.this.state = SpaceKeyTimer.this.inspectionEnabled ?
                        State.READY_FOR_INSPECTION : State.READY;
            }
        };
        this.timerManager.addTimerListener(this.timerListener);
    }

    @Override
    public void stop() {
        if (this.repeater != null) {
            this.repeater.cancel();
        }

        this.frame.removeKeyListener(this.keyListener);
        this.timerManager.removeTimerListener(this.timerListener);
    }
}
