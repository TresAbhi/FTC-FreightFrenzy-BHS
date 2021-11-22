package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DriverControl", group = "Linear Opmode")
// @Disabled
public class DriverControl extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  private DcMotor LEFT_FRONT = null;
  private DcMotor LEFT_REAR = null;
  private DcMotor RIGHT_FRONT = null;
  private DcMotor RIGHT_REAR = null;

  private DcMotor CONVEYOR_1 = null;
  private DcMotor CONVEYOR_2 = null;
  private DcMotor EXTENDER = null;

  private Servo CLAW = null;

  private Servo SPINNER = null;

  @Override
  public void runOpMode() {
    // Constants
    /**
     * why is it all caps? it's a constant
     */
    double PRECISION = 2;

    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

    CONVEYOR_1 = hardwareMap.get(DcMotor.class, "conveyor_1");
    CONVEYOR_2 = hardwareMap.get(DcMotor.class, "conveyor_2");
    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");

    SPINNER = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();
    runtime.reset();

    while (opModeIsActive()) {
      // Constants

      /**
       * dividing by 1.5 because it's too fast at 100% power
       *
       * don't know what "a ? b : c" does? google ternary operators
       * ~ 😘 Abhi
       */
      double speedControl = gamepad1.left_bumper ? 2.5 : 1.5;

      // dampen to not make it 1:1, it's an exponential growth
      double dampedLeftJoystickX =
        Math.signum(gamepad1.left_stick_x) *
        Math.pow(gamepad1.left_stick_x, PRECISION);
      double dampedLeftJoystickY =
        Math.signum(gamepad1.left_stick_y) *
        Math.pow(gamepad1.left_stick_y, PRECISION);
      double dampedRightJoystickX =
        Math.signum(gamepad1.right_stick_x) *
        Math.pow(gamepad1.right_stick_x, PRECISION);
      double dampedRightJoystickY =
        Math.signum(gamepad1.right_stick_y) *
        Math.pow(gamepad1.right_stick_y, PRECISION);

      // resultant vectors (google it if yoy don't know)
      double vectorNormal = Math.hypot(
        dampedLeftJoystickY,
        dampedLeftJoystickX
      );
      double robotAngle =
        Math.atan2(dampedLeftJoystickY, -dampedLeftJoystickX) - Math.PI / 4;
      // trig to find out partial offsets in axes (plural of axis)
      double v1 = vectorNormal * Math.cos(robotAngle);
      double v2 = vectorNormal * Math.sin(robotAngle);
      double v3 = vectorNormal * Math.sin(robotAngle);
      double v4 = vectorNormal * Math.cos(robotAngle);

      LEFT_FRONT.setPower((-v1 + dampedRightJoystickX) / speedControl);
      LEFT_REAR.setPower((-v2 + dampedRightJoystickX) / speedControl);
      RIGHT_FRONT.setPower((-v3 - dampedRightJoystickX) / speedControl);
      RIGHT_REAR.setPower((-v4 - dampedRightJoystickX) / speedControl);

      CONVEYOR_1.setPower(gamepad2.right_bumper ? 0.5 : 0);
      CONVEYOR_2.setPower(gamepad2.right_bumper ? -0.5 : 0);
      CONVEYOR_1.setPower(gamepad2.left_bumper ? -0.5 : 0);
      CONVEYOR_2.setPower(gamepad2.left_bumper ? 0.5 : 0);

      EXTENDER.setPower(gamepad2.right_trigger);
      EXTENDER.setPower(-gamepad2.left_trigger);

      SPINNER.setPosition(gamepad1.right_bumper ? 1 : 0.49);

      CLAW.setPosition(gamepad2.dpad_right ? 1 : 0);

      double drive = -dampedLeftJoystickY;
      double turn = dampedRightJoystickX;

      telemetry.addData("Status", "Run Time: " + runtime.toString());

      telemetry.addData("LF Power", LEFT_FRONT.getPower());
      telemetry.addData("LR Power", LEFT_REAR.getPower());
      telemetry.addData("RF Power", RIGHT_FRONT.getPower());
      telemetry.addData("RR Power", RIGHT_REAR.getPower());

      telemetry.addData("Left Joystick X", gamepad1.left_stick_x);
      telemetry.addData("Left Joystick Y", gamepad1.left_stick_y);
      telemetry.addData("Right Joystick X", gamepad1.right_stick_x);
      telemetry.addData("Right Joystick Y", gamepad1.right_stick_y);

      telemetry.addData("Dampened Left Joystick X", dampedLeftJoystickX);
      telemetry.addData("Dampened Left Joystick Y", dampedLeftJoystickY);
      telemetry.addData("Dampened Right Joystick X", dampedRightJoystickX);
      telemetry.addData("Dampened Right Joystick Y", dampedRightJoystickY);

      telemetry.addData("Right DPad", gamepad2.dpad_right);

      telemetry.addData("Conveyor 1", CONVEYOR_1.getPower());
      telemetry.addData("Conveyor 2", CONVEYOR_2.getPower());

      telemetry.addData("Claw Value", CLAW.getPosition());

      telemetry.update();
    }
  }
}
