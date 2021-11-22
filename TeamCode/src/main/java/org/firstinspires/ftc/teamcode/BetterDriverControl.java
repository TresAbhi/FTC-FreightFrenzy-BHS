package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "BetterDriverControl", group = "Linear Opmode")
// @Disabled
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
    // Constants
    double PRECISION = 2;

    // Components
    leftFront = hardwareMap.get(DcMotor.class, "left_front");
    leftRear = hardwareMap.get(DcMotor.class, "left_rear");
    rightFront = hardwareMap.get(DcMotor.class, "right_front");
    rightRear = hardwareMap.get(DcMotor.class, "right_rear");

    conveyor1 = hardwareMap.get(DcMotor.class, "conveyor_1");
    conveyor2 = hardwareMap.get(DcMotor.class, "conveyor_2");
    extender = hardwareMap.get(DcMotor.class, "extender");
    claw = hardwareMap.get(Servo.class, "claw");

    spinner = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    leftFront.setDirection(DcMotor.Direction.REVERSE);
    rightRear.setDirection(DcMotor.Direction.REVERSE);

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();
    runtime.reset();

    while (opModeIsActive()) {
      // Constants

      /**
       * dividing by 1.5 because it's too fast at 100% power
       * don't know what "a ? b : c" does? google ternary operators
       * 😘 Abhi
       */
      double SPEED_CONTROL = gamepad1.left_bumper ? 2.5 : 1.5;

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

      leftFront.setPower((-v1 + dampedRightJoystickX) / SPEED_CONTROL);
      leftRear.setPower((-v2 + dampedRightJoystickX) / SPEED_CONTROL);
      rightFront.setPower((-v3 - dampedRightJoystickX) / SPEED_CONTROL);
      rightRear.setPower((-v4 - dampedRightJoystickX) / SPEED_CONTROL);

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

      double drive = -dampedLeftJoystickY;
      double turn = dampedRightJoystickX;

      telemetry.addData("Status", "Run Time: " + runtime.toString());

      telemetry.addData("LF Power", leftFront.getPower());
      telemetry.addData("LR Power", leftRear.getPower());
      telemetry.addData("RF Power", rightFront.getPower());
      telemetry.addData("RR Power", rightRear.getPower());

      telemetry.addData("Left Joystick X", gamepad1.left_stick_x);
      telemetry.addData("Left Joystick Y", gamepad1.left_stick_y);
      telemetry.addData("Right Joystick X", gamepad1.right_stick_x);
      telemetry.addData("Right Joystick Y", gamepad1.right_stick_y);

      telemetry.addData("Dampened Left Joystick X", dampedLeftJoystickX);
      telemetry.addData("Dampened Left Joystick Y", dampedLeftJoystickY);
      telemetry.addData("Dampened Right Joystick X", dampedRightJoystickX);
      telemetry.addData("Dampened Right Joystick Y", dampedRightJoystickY);

      telemetry.addData("Right DPad", gamepad2.dpad_right);

      telemetry.addData("Conveyor 1", conveyor1.getPower());
      telemetry.addData("Conveyor 2", conveyor2.getPower());

      telemetry.addData("Claw Value", claw.getPosition());

      telemetry.update();
    }
  }
}
