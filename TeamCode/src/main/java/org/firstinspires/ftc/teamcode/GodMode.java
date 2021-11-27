package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "GodMode", group = "Linear Opmode")
// @Disabled
public class GodMode extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  private DcMotor LEFT_FRONT = null;
  private DcMotor LEFT_REAR = null;
  private DcMotor RIGHT_FRONT = null;
  private DcMotor RIGHT_REAR = null;

  private DcMotor ARM_JOINT_LEFT = null;
  private DcMotor ARM_JOINT_RIGHT = null;
  private DcMotor EXTENDER = null;

  private Servo CLAW = null;
  private Servo WRIST = null;

  private Servo SPINNER = null;

  @Override
  public void runOpMode() {
    // Constants
    double MOVEMENT_PRECISION = 2;

    double ARM_CATCH_UP_MAX_POWER = 0.5; // when the arm is at ARM_CATCHUP_ACCPECTANCE_RANGE diffrence, it will approach this power value [-1.0, +1]
    double ARM_CATCHUP_ACCPECTANCE_RANGE = 25;
    int ARM_CATCH_UP_INPUT_SPEED = 3;
    int ARM_POS_MIN = 50;
    int ARM_POS_MAX = 470;

    int armTargetPosition = ARM_POS_MIN;

    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

    ARM_JOINT_LEFT = hardwareMap.get(DcMotor.class, "conveyor_left");
    ARM_JOINT_RIGHT = hardwareMap.get(DcMotor.class, "conveyor_right");
    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");
    WRIST = hardwareMap.get(Servo.class, "wrist");

    SPINNER = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    ARM_JOINT_LEFT.setDirection(DcMotor.Direction.FORWARD);
    ARM_JOINT_RIGHT.setDirection(DcMotor.Direction.REVERSE);

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
      double currentPosition = ARM_JOINT_LEFT.getCurrentPosition();
      double armPosDiffRaw = armTargetPosition - currentPosition;
      double armPosDiffPartial = armPosDiffRaw / ARM_CATCHUP_ACCPECTANCE_RANGE;
      double armPosDiffCoefficient =
        Math.signum(armPosDiffPartial) *
        Math.min(Math.abs(armPosDiffPartial), 1);

      // dampen to not make it 1:1, it's an exponential growth
      double dampedLeftJoystickX =
        Math.signum(gamepad1.left_stick_x) *
        Math.pow(gamepad1.left_stick_x, MOVEMENT_PRECISION);
      double dampedLeftJoystickY =
        Math.signum(gamepad1.left_stick_y) *
        Math.pow(gamepad1.left_stick_y, MOVEMENT_PRECISION);
      double dampedRightJoystickX =
        Math.signum(gamepad1.right_stick_x) *
        Math.pow(gamepad1.right_stick_x, MOVEMENT_PRECISION);
      double dampedRightJoystickY =
        Math.signum(gamepad1.right_stick_y) *
        Math.pow(gamepad1.right_stick_y, MOVEMENT_PRECISION);

      // resultant vectors
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

      if (gamepad1.right_bumper) armTargetPosition =
        Math.min(armTargetPosition + ARM_CATCH_UP_INPUT_SPEED, ARM_POS_MAX);
      if (gamepad1.left_bumper) armTargetPosition =
        Math.max(armTargetPosition - ARM_CATCH_UP_INPUT_SPEED, ARM_POS_MIN);

      ARM_JOINT_LEFT.setPower(armPosDiffCoefficient * ARM_CATCH_UP_MAX_POWER);
      ARM_JOINT_RIGHT.setPower(armPosDiffCoefficient * ARM_CATCH_UP_MAX_POWER);

      EXTENDER.setPower(gamepad1.right_trigger);
      EXTENDER.setPower(-gamepad1.left_trigger);

      SPINNER.setPosition(gamepad2.right_bumper ? 1 : 0.49);

      CLAW.setPosition(gamepad2.dpad_right ? 1 : 0);
      WRIST.setPosition(gamepad2.dpad_up ? 1 : 0);
      /** If we have time, make it parallel to the ground auto*/

      double drive = -dampedLeftJoystickY;
      double turn = dampedRightJoystickX;

      telemetry.addData("Status", "Run Time: " + runtime.toString());

      telemetry.addData("Claw Value", CLAW.getPosition());

      telemetry.addData("armTargetPosition", armTargetPosition);
      telemetry.addData("currentPosition", currentPosition);
      telemetry.addData("armPosDiffRaw", armPosDiffRaw);
      telemetry.addData("armPosDiffPartial", armPosDiffPartial);
      telemetry.addData("armPosDiffCoefficient", armPosDiffCoefficient);
      telemetry.addData(
        "applied power",
        armPosDiffCoefficient * ARM_CATCH_UP_MAX_POWER
      );

      telemetry.update();
    }
  }
}
