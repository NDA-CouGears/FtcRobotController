// author: delia jasper
// purpose: combining the programs into one



package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "Try Camera")
public class TryCamera extends LinearOpMode {

    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.5;
    static final double DRIVE_GEAR_REDUCTION = 1.0 ;
    static final double WHEEL_DIAMETER_MM = 97;
    static final double COUNTS_PER_MOTOR_REV = 1440;
    static final double COUNTS_PER_MM = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_MM * 3.1415);
    private ElapsedTime runtime = new ElapsedTime();

    public void encoderDrive(double speed, int leftFrontInches, int rightFrontInches, int leftBackInches, int rightBackInches, double timeoutS, DcMotor lfd, DcMotor lbd, DcMotor rfd, DcMotor rbd) {
        telemetry.update();
        sleep(100);
        DcMotor leftFrontDrive  = lfd;
        DcMotor leftBackDrive = lbd;
        DcMotor rightFrontDrive  = rfd;
        DcMotor rightBackDrive = rbd;

        int newLeftFrontTarget;
        int newLeftBackTarget;
        int newRightFrontTarget;
        int newRightBackTarget;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftFrontTarget = leftFrontDrive.getCurrentPosition() + (int) (25.4 * leftFrontInches * COUNTS_PER_MM);
            newLeftBackTarget = leftBackDrive.getCurrentPosition() + (int) (25.4 * leftBackInches * COUNTS_PER_MM);
            newRightFrontTarget = rightFrontDrive.getCurrentPosition() + (int) (25.4 * rightFrontInches * COUNTS_PER_MM);
            newRightBackTarget = rightBackDrive.getCurrentPosition() + (int) (25.4 * rightBackInches * COUNTS_PER_MM);
            leftFrontDrive.setTargetPosition(newLeftFrontTarget);
            leftBackDrive.setTargetPosition(newLeftBackTarget);
            rightFrontDrive.setTargetPosition(newRightFrontTarget);
            rightBackDrive.setTargetPosition(newRightBackTarget);

            // Turn On RUN_TO_POSITION
            leftFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            telemetry.addData("Currently at", " at %7d :%7d",
                    leftFrontDrive.getCurrentPosition(), leftBackDrive.getCurrentPosition(),
                    rightFrontDrive.getCurrentPosition(), rightBackDrive.getCurrentPosition());
            telemetry.update();
            sleep(500);

            // reset the timeout time and start motion.
            runtime.reset();
            leftFrontDrive.setPower(Math.abs(speed));
            leftBackDrive.setPower(Math.abs(speed));
            rightFrontDrive.setPower(Math.abs(speed));
            rightBackDrive.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftFrontDrive.isBusy() || leftBackDrive.isBusy() || rightFrontDrive.isBusy() || rightBackDrive.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Running to", " %7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
                telemetry.addData("Currently at", " at %7d :%7d",
                        leftFrontDrive.getCurrentPosition(), leftBackDrive.getCurrentPosition(),
                        rightFrontDrive.getCurrentPosition(), rightBackDrive.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftFrontDrive.setPower(0);
            leftBackDrive.setPower(0);
            rightFrontDrive.setPower(0);
            rightBackDrive.setPower(0);



            telemetry.addData("Running to", " %7d :%7d", newLeftFrontTarget, newLeftBackTarget, newRightFrontTarget, newRightBackTarget);
            telemetry.update();
            sleep(500);


            // Turn off RUN_TO_POSITION
            leftFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightFrontDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(250);   // optional pause after each move.
        }
    }



    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";

    private static final String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };


    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AVizTQr/////AAABmdusHyCnP0/ekzJ/reBbvQcKPNZq8wLHaU+onHorR5RGzgh8Ts6FBvui7+3xv5zDjniFMr71j8hB5DCjv77Gq6TVlPcww8aCN9xFp+TKUrDp6mkyR80yMYiw35ODmfmZRYsbnkbDyXDwSSR7YzX/Zb7UlSFhtwUMCtHZuEFJmSrdfOgdKLPfF8N+k/sdAPtX3XXKTaGoPOfJ2I/cr022zFxyAnhD8LAdoQ8WtawU3j9UANmuRFaSZcbppMohtC7ATyfXKMU1mZZb3di5p/c8HOmuqgofqQEXsm1VTpWn8rAzD8O0mHfIvq7zfDCa0dCr8rKRGQqYcc/iJ9k60GNgbLrdwsfvMTWevCqYiIBAfmIF";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    public String cameraResult() {

        String image = "";

        if (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();

                    if (updatedRecognitions != null) {
                        telemetry.addData("# Objects Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display image position/size information for each one
                        // Note: "Image number" refers to the randomized image orientation/number
                        for (Recognition recognition : updatedRecognitions) {
                            double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                            double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                            double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                            double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                            telemetry.addData(""," ");
                            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                            telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                            telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);
                            image = recognition.getLabel();
                        }
                        telemetry.update();
                    }
                }

        }
        return image;
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }


    @Override
    public void runOpMode() {

        initVuforia();
        initTfod();

        telemetry.addData("hello", "");
        telemetry.update();

        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }


        telemetry.update();

        DcMotor leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        DcMotor leftBackDrive = hardwareMap.get(DcMotor.class, "left_back_drive");
        DcMotor rightFrontDrive  = hardwareMap.get(DcMotor.class, "right_front_drive");
        DcMotor rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");
        Servo servo = hardwareMap.servo.get("servo");
        telemetry.update();

        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        servo.setPosition(0.45);

        String imageKey = "";
        while(imageKey == "" && opModeIsActive()) {
            imageKey = cameraResult();
        }
        servo.setPosition(0.31);
        /*while(true)
        {*/
            switch (imageKey) {
                case "1 Bolt":
                    telemetry.addLine("Image 1");
                    encoderDrive(DRIVE_SPEED,  27,  27, 27,27,5.0, leftFrontDrive,leftBackDrive,rightFrontDrive, rightBackDrive);  // S1: Forward 47 Inches with 5 Sec timeout
                    encoderDrive(DRIVE_SPEED,   -26, 26, 26, -26,4.0,leftFrontDrive,leftBackDrive,rightFrontDrive, rightBackDrive);  // S2: Turn Right 12 Inches with 4 Sec timeout
                    telemetry.update();
                    sleep(2000);
                    break;
                case "2 Bulb":
                    telemetry.addLine("Image 2");
                    encoderDrive(DRIVE_SPEED,  27,  27, 27,27, 5.0, leftFrontDrive,leftBackDrive,rightFrontDrive, rightBackDrive);
                    telemetry.update();
                    sleep(2000);
                    break;
                case "3 Panel":
                    telemetry.addLine("Image 3");
                    encoderDrive(DRIVE_SPEED,  27,  27, 27,27,5.0,leftFrontDrive,leftBackDrive,rightFrontDrive, rightBackDrive);  // S1: Forward 47 Inches with 5 Sec timeout
                    encoderDrive(DRIVE_SPEED,   26, -26, -26, 26,4.0,leftFrontDrive,leftBackDrive,rightFrontDrive, rightBackDrive);  // S2: Turn Right 12 Inches with 4 Sec timeout
                    telemetry.update();
                    sleep(2000);
                    break;
            }
       //   }

    }


}
