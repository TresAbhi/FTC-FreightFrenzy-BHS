package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="BetterDriverControl", group="Linear Opmode")
//@Disabled
public class BetterDriverControl extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();

    private DcMotor leftFront = null;
    private DcMotor leftRear = null;
    private DcMotor rightFront = null;
    private DcMotor rightRear = null;

    private DcMotor conveyor1 = null;
    private DcMotor conveyor2 = null;
    private DcMotor extender = null;

    private Servo claw = null;

    private Servo spinner = null;

    @Override
    public void runOpMode() {
        double precision = 10;

        double dampedLeftJoystickX = Math.pow(gamepad1.left_stick_x, precision);
        double dampedLeftJoystickY = Math.pow(gamepad1.left_stick_y, precision);
        double dampedRightJoystickX = Math.pow(gamepad1.right_stick_x, precision);
        double dampedRightJoystickY = Math.pow(gamepad1.right_stick_y, precision);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftFront  = hardwareMap.get(DcMotor.class, "left_front");
        leftRear   = hardwareMap.get(DcMotor.class, "left_rear");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");
        rightRear  = hardwareMap.get(DcMotor.class, "right_rear");

        conveyor1  = hardwareMap.get(DcMotor.class, "conveyor_1");
        conveyor2  = hardwareMap.get(DcMotor.class, "conveyor_2");
        extender   = hardwareMap.get(DcMotor.class, "extender");
        claw       = hardwareMap.get(Servo.class,"claw");

        spinner    = hardwareMap.get(Servo.class, "spinner");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightRear.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double vectorNormal = Math.hypot(gamepad1.left_stick_y, gamepad1.left_stick_x);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, - gamepad1.left_stick_x) - Math.PI / 4;
            double v1 = vectorNormal * Math.cos(robotAngle);
            double v2 = vectorNormal * Math.sin(robotAngle);
            double v3 = vectorNormal * Math.sin(robotAngle);
            double v4 = vectorNormal * Math.cos(robotAngle);

            double speedControl = gamepad1.left_bumper ? 2.5 : 1.5;

            leftFront.setPower((-v1 + gamepad1.right_stick_x) / speedControl);
            leftRear.setPower((-v2 + gamepad1.right_stick_x) / speedControl);
            rightFront.setPower((-v3 - gamepad1.right_stick_x) / speedControl);
            rightRear.setPower((-v4 - gamepad1.right_stick_x) / speedControl);

            conveyor1.setPower(gamepad2.right_bumper ? 0.5 : 0);
            conveyor2.setPower(gamepad2.right_bumper ? -0.5 : 0);
            conveyor1.setPower(gamepad2.left_bumper ? -0.5 : 0);
            conveyor2.setPower(gamepad2.left_bumper ? 0.5 : 0);

            extender.setPower(gamepad2.right_trigger);
            extender.setPower(-gamepad2.left_trigger);

            spinner.setPosition(gamepad1.right_bumper ? 1 : 0.49);

            claw.setPosition(gamepad2.dpad_right ? 1 : 0);

            // conveyor1.setPower(gamepad2.right_stick_y);
            // conveyor2.setPower(-gamepad2.right_stick_y);
            // conveyor1.setPower(-gamepad2.right_stick_y);
            // conveyor2.setPower(gamepad2.right_stick_y);

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;

            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("LF Power", leftFront.getPower());
            telemetry.addData("LR Power", leftRear.getPower());
            telemetry.addData("RF Power", rightFront.getPower());
            telemetry.addData("RR Power", rightRear.getPower());
            
            telemetry.addData("Left Joystick X", gamepad1.left_stick_x);
            telemetry.addData("Left Joystick Y", gamepad1.left_stick_y);
            telemetry.addData("Right Joystick X", gamepad1.right_stick_x);
            telemetry.addData("Right Joystick Y", gamepad1.right_stick_y);

            telemetry.addData("Right DPad", gamepad2.dpad_right);

            telemetry.addData("Conveyor 1", conveyor1.getPower());
            telemetry.addData("Conveyor 2", conveyor2.getPower());

            telemetry.addData("Claw Value",claw.getPosition());

            telemetry.update();
        }
    }
}
