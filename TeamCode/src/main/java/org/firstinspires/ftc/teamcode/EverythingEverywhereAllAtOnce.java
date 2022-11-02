// author: delia jasper
// purpose: combining the programs into one



package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Everything Everywhere All At Once", group = "Concept")
@Disabled
public class EverythingEverywhereAllAtOnce {

    NewCameraPleaseWork cam = new NewCameraPleaseWork();

    String imageKey = cam.cameraResult();

    public void runOpMode() {

        switch (imageKey) {
            case "1 Bolt":
                ParkLeft pl = new ParkLeft();
                pl.runOpMode();
                break;

            case "2 Bulb":
                Park1 pm = new Park1();
                pm.runOpMode();
                break;

            case "3 Panel":
                ParkRight pr = new ParkRight();
                pr.runOpMode();
                break;
        }
    }

    public String getImageKey() {
        return imageKey;
    }
}
