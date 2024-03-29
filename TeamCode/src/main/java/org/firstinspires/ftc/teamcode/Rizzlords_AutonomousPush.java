/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;

@Autonomous(name = "Rizzlords Autonomous (Push)", group = "Rizzlords")
public class Rizzlords_AutonomousPush extends LinearOpMode {
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    AprilTagProcessor myAprilTagProcessor;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    int ID_TAG_OF_INTEREST = 18; // Tag ID 18 from the 36h11 family

    AprilTagDetection tagOfInterest = null;


    //hardware
    //    private ColorSensor Coloursensor;
    private DcMotor TopRight;
    private DcMotor BottomRight;
    private DcMotor TopLeft;
    private DcMotor BottomLeft;

    private DcMotor Arm;
    private DcMotor ForeArm;
    private Servo Hand;

    private double CurrRotation;

    private ElapsedTime runtime = new ElapsedTime();
    private double encoderPower;
    private int armEncoder;
    private int foreArmEncoder;
    private double handOpen;
    private double handClosed;


    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {

        //april tag stuff
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
//        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);
//
//        camera.setPipeline(aprilTagDetectionPipeline);
//        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
//        {
//            @Override
//            public void onOpened()
//            {
//                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
//            }
//
//            @Override
//            public void onError(int errorCode)
//            {
//
//            }
//        });

        // Create the AprilTag processor and assign it to a variable.
        myAprilTagProcessor = AprilTagProcessor.easyCreateWithDefaults();

        telemetry.setMsTransmissionInterval(50);
        TopRight = hardwareMap.get(DcMotor.class, "Top Right");
        BottomRight = hardwareMap.get(DcMotor.class, "Bottom Right");
        TopLeft = hardwareMap.get(DcMotor.class, "Top Left");
        BottomLeft = hardwareMap.get(DcMotor.class, "Bottom Left");

        Arm = hardwareMap.get(DcMotor.class, "Arm1");
        ForeArm = hardwareMap.get(DcMotor.class, "Forearm");
        Hand = hardwareMap.get(Servo.class, "Hand");
        encoderPower = .3;
        handOpen = 0.3;
        handClosed = 0;
        RunUsingEncoder(Arm);
        RunUsingEncoder(ForeArm);

        CurrRotation = 0;

        // initilization blocks, right motor = front right, left motor = front left, arm = back right, hand = back left
        BottomLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        TopLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        HandControl();

        /* Actually do something useful */
        waitForStart();
        RunSequence();

        telemetry.update();
    }

    private void RunUsingEncoder(DcMotor motor){
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(.1);
    }

    /**
     * the sequence of movements to move to a medium/small pole and place cone
     *
     * trajectory arguments meaning
     * 0 - default trajectory
     * 1 - end left
     * 2 - end middle
     * 3 - end right
     */
    private void RunSequence(){
        double blockTravelTime = 1.5/.3, rightRotation = 2.5/.3;

//        moveXY(0, .3, 0);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < (blockTravelTime * .3))){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//        TerminateMovement();
//
//        moveXY(0, 0, .3);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < (rightRotation * 0.3))){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//        TerminateMovement();
//
//        moveXY(0, .3, 0);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < (blockTravelTime * 0.3 * 3))){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//        TerminateMovement();
//
//        moveXY(0, 0, .3);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < (rightRotation * 0.3))){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//        TerminateMovement();
//
//        moveXY(0, .3, 0);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < (blockTravelTime * .3))){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//        TerminateMovement();

        moveXY(0, -0.3, 0);
        runtime.reset();
        while(opModeIsActive() && (runtime.seconds() < 5)){
            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        TerminateMovement();

//        ArmControlPreset(0);
//        runtime.reset();
//        while(opModeIsActive() && (runtime.seconds() < 1)){
//            telemetry.addData("Path", "Leg 1: %4.1f S Elapsed", runtime.seconds());
//            telemetry.update();
//        }
//
//        HandControl();
    }

    /**
     * terminates movement
     */
    private void TerminateMovement(){
        moveXY(0, 0, 0);
        sleep(100);
    }

    /**
     * arm control specific
     */
//    private void ArmControl(int target) {
//        arm.setPower(1);
//        if (target < 0){
//            target = 0;
//        }
//        else if(target > 11416){
//            target = 11416;
//        }
//        arm.setTargetPosition(target);
//    }

    /**
     *
     * @param position
     * position = 0: default
     * position = 1: ready to pickup
     * position = 2: pickup
     * position = 3: place
     */
    private void ArmControlPreset(int position){
        Arm.setPower(encoderPower);
        switch(position){
            case 0:
                telemetry.addData("target pos", 0);
                telemetry.update();
                Arm.setTargetPosition(0);
                break;
            case 1:
                telemetry.addData("target pos", 884);
                telemetry.update();
                Arm.setTargetPosition(0);
                break;
            case 2:
                telemetry.addData("target pos", 6736);
                telemetry.update();
                Arm.setTargetPosition(161);
                break;
            case 3:
                telemetry.addData("target pos", 10473);
                telemetry.update();
                Arm.setTargetPosition(0);
                break;
            default:
                return;
        }
    }

    private void ForeArmControlPreset(int position){
        ForeArm.setPower(encoderPower);
        switch(position){
            case 0:
                telemetry.addData("target pos", 0);
                telemetry.update();
                ForeArm.setTargetPosition(0);
                break;
            case 1:
                telemetry.addData("target pos", 884);
                telemetry.update();
                ForeArm.setTargetPosition(-580);
                break;
            case 2:
                telemetry.addData("target pos", 6736);
                telemetry.update();
                ForeArm.setTargetPosition(-422);
                break;
            case 3:
                telemetry.addData("target pos", 10473);
                telemetry.update();
                ForeArm.setTargetPosition(0);
                break;
            default:
                return;
        }
    }

    private void HandControl(){
        Hand.setPosition(Hand.getPosition() == handOpen ? handClosed : handOpen);
    }

    /**
     * Describe this function...
     */
    private void moveXY(double xVal, double yVal, double rVal) {
        omnidirectional(xVal, yVal, rVal);
    }

    /**
     * Describe this function...
     * +speed = forward
     * +turn = clockwise
     * -strafe = right
     */
    private void omnidirectional(double strafe, double speed, double turn) {
        double topLeft = speed + turn + strafe;
        double topRight = speed - turn - strafe;
        double bottomLeft = speed + turn - strafe;
        double bottomRight = speed - turn + strafe;

        TopRight.setPower(topRight);
        BottomRight.setPower(bottomRight);
        TopLeft.setPower(topLeft);
        BottomLeft.setPower(bottomLeft);
    }

    void tagToTelemetry(AprilTagDetection detection)
    {
        Orientation rot = Orientation.getOrientation(detection.pose.R, AxesReference.INTRINSIC, AxesOrder.YXZ, AngleUnit.DEGREES);

        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", rot.firstAngle));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", rot.secondAngle));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", rot.thirdAngle));
    }
}
