package dev.marianof.locatevehicles.Controller.ScheduledTask;

import java.util.TimerTask;

import dev.marianof.locatevehicles.Controller.MainController;

public class PositionTask extends TimerTask {
    @Override
    public void run() {
        MainController.getSingleton().makePetition();
    }
}
