/*
    author: delia jasper
    purpose: another way to drive using mecanum wheels
 */

// i want to try this out soon and see if a it works and if b if the drivers like robot or field centric better

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;

@TeleOp(name="Drive and Lift", group="TeleOp")
public class DriveAndLift extends LinearOpMode {

    // initialize narrators
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    Servo claw;
    Servo servo;
    Servo servo2;

    // final variables
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_MM = 37.0;
    static final double COUNTS_PER_MOTOR_REV = 1440;
    static final double middleServo = 55;
    static final double middleServo2= 30;
    static final double topServo = 67;
    static final double topServo2 = 57;
    static final double servoMax = 69;
    static final double servo2Max = 60;
    static final double servoMin = 31;
    static final double servo2Min = 3;

    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        leftFrontDrive = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        servo = hardwareMap.servo.get("servo");
        servo2 = hardwareMap.servo.get("servo2");
        claw = hardwareMap.servo.get("servo3");

        // setting direction for motors
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

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

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double deltaY = -gamepad2.left_stick_y;
            double deltaY2 = -gamepad2.right_stick_y;

            if (gamepad2.b)
            {
                curArmPosition = middleServo;
                curArmPosition2 = middleServo2;
            }

            if (gamepad2.y)
            {
                curArmPosition = topServo;
                curArmPosition2 = topServo2;
            }

            if (gamepad2.a)
            {
                curArmPosition = servoMin;
                curArmPosition2 = servo2Min;
                claw.setPosition(1);
            }

            if (gamepad2.x)
            {
                curArmPosition -= 7.5;
                curArmPosition2 -= 7.5;
                sleep(1000);
                claw.setPosition(1);
            }

            curArmPosition2 += deltaY2*0.1;
            curArmPosition += deltaY*0.1;

            if (curArmPosition > servoMax) {
                curArmPosition = servoMax;
            }
            else if (curArmPosition < servoMin ) {
                curArmPosition = servoMin;
            }

            if (curArmPosition2 > servo2Max) {
                curArmPosition2 = servo2Max;
            }
            else if (curArmPosition2 < servo2Min) {
                curArmPosition2 = servo2Min;
            }

            servo.setPosition(curArmPosition/100);
            servo2.setPosition(curArmPosition2/100);

            if (gamepad2.left_bumper) {
                claw.setPosition(1);
            }

            if (gamepad2.right_bumper) {
                claw.setPosition(0);
            }

            // Wait for the game to start (driver presses PLAY)
            telemetry.addData("Current Position: ", curArmPosition);
            telemetry.addData("Current Position: ", curArmPosition2);
            telemetry.update();

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            leftFrontDrive.setPower(frontLeftPower);
            leftBackDrive.setPower(backLeftPower);
            rightFrontDrive.setPower(frontRightPower);
            rightBackDrive.setPower(backRightPower);


        }

    }
}
