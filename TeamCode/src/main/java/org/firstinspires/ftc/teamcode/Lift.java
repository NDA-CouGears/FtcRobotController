/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Lift1", group="Linear Opmode")
@Disabled
public class Lift extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    Servo claw;
    Servo servo;
    Servo servo2;


    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.

        servo = hardwareMap.servo.get("servo");
        servo2 = hardwareMap.servo.get("servo2");
        claw = hardwareMap.servo.get("servo3");


        if (servo.getController() instanceof ServoControllerEx)
        {
            ServoControllerEx theControl = (ServoControllerEx) servo.getController();
            int portNum = servo.getPortNumber();
            PwmControl.PwmRange range = new PwmControl.PwmRange(553,2500);
            theControl.setServoPwmRange(portNum,range);
        }

        if (servo2.getController() instanceof ServoControllerEx)
        {
            ServoControllerEx theControl = (ServoControllerEx) servo2.getController();
            int portNum = servo2.getPortNumber();
            PwmControl.PwmRange range = new PwmControl.PwmRange(553,2500);
            theControl.setServoPwmRange(portNum,range);
        }

        double curArmPosition = 31;
        double curArmPosition2 = 3;
        boolean leftBumper = gamepad2.left_bumper;
        boolean rightBumper = gamepad2.right_bumper;

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double deltaY = -gamepad2.left_stick_y;
            double deltaY2 = -gamepad2.right_stick_y;

            curArmPosition2 += deltaY2*0.1;
            curArmPosition += deltaY*0.1;

            if (curArmPosition > 69) {
                curArmPosition = 69;
            }
            else if (curArmPosition < 31 ) {
                curArmPosition = 31;
            }
            servo.setPosition(curArmPosition/100);

            if (curArmPosition2 > 50) {
                curArmPosition2 = 50;
            }
            else if (curArmPosition2 < 3) {
                curArmPosition2 = 3;
            }
            servo2.setPosition(curArmPosition2/100);

            if (gamepad2.left_bumper) {
                claw.setPosition(0.75);
                sleep(2000);
                claw.setPosition(1);
            }

            if (gamepad2.right_bumper) {
                claw.setPosition(0);
            }

            // Wait for the game to start (driver presses PLAY)
            telemetry.addData("Current Position: ", curArmPosition);
            telemetry.addData("Current Position: ", curArmPosition2);
            telemetry.update();

        }




    }
}
