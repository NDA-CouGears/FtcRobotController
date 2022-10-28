// author: delia jasper
// purpose: combining the programs into one



package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Everything Everywhere All At Once", group = "Concept")
@Disabled
public class EverythingEverywhereAllAtOnce {

    NewCameraPleaseWork cam = new NewCameraPleaseWork();

    int imageKey = cam.cameraResult();




}
