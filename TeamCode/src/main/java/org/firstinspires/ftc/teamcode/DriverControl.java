package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
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

  private DcMotorEx ARM_JOINT_LEFT = null;
  private DcMotorEx ARM_JOINT_RIGHT = null;

  private DcMotor EXTENDER = null;

  private Servo CLAW = null;
  private Servo WRIST = null;

  private Servo SPINNER = null;

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  float MOVEMENT_PRECISION = 2f;

  int ARM_JOINT_MIN_ANGLE = 55;
  int ARM_JOINT_MAX_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  float ARM_JOINT_POWER = 0.2f;
  int ARM_JOINT_INPUT_SPEED = 4;

  int EXTENDER_MIN_POS = 40;
  int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1490;
  float EXTENDER_POWER = 0.4f;
  float ARM_JOINT_MAX_VELOCITY = 1;
  int EXTENDER_INPUT_SPEED = 24;

  float WRIST_INPUT_SPEED = 0.03f;

  float SPEED_LOW_POWER = 0.4f;
  float SPEED_HIGH_POWER = 0.8f;

  // Preset states

  // b: lower tower layer
  int ARM_JOINT_LOW_ANGLE = ARM_JOINT_MIN_ANGLE + 310;
  int EXTENDER_LOW_POS = EXTENDER_MIN_POS;
  float WRIST_LOW_ANGLE = 0.8f;

  // x: middle tower layer
  int ARM_JOINT_MIDDLE_ANGLE = ARM_JOINT_MIN_ANGLE + 245;
  int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 110;
  float WRIST_MIDDLE_ANGLE = 0.75f;

  // y: top tower layer
  int ARM_JOINT_HIGH_ANGLE = ARM_JOINT_MIN_ANGLE + 190;
  int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 630;
  float WRIST_HIGH_ANGLE = 0.65f;

  // x: ground
  int ARM_JOINT_GROUND_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  float WRIST_GROUND_ANGLE = 1f;

  // Mutables
  int armJointTargetAngle = ARM_JOINT_MIN_ANGLE;
  int extenderTargetPos = EXTENDER_MIN_POS;
  float wristTargetAngle = 0f;

  String driveMode = "normal";
  boolean isModeSwitched = false;

  @Override
  public void runOpMode() {
    // Components
    LEFT_FRONT = hardwareMap.get(DcMotor.class, "left_front");
    LEFT_REAR = hardwareMap.get(DcMotor.class, "left_rear");
    RIGHT_FRONT = hardwareMap.get(DcMotor.class, "right_front");
    RIGHT_REAR = hardwareMap.get(DcMotor.class, "right_rear");

    ARM_JOINT_LEFT = hardwareMap.get(DcMotorEx.class, "conveyor_left");
    ARM_JOINT_RIGHT = hardwareMap.get(DcMotorEx.class, "conveyor_right");
    EXTENDER = hardwareMap.get(DcMotor.class, "extender");
    CLAW = hardwareMap.get(Servo.class, "claw");
    WRIST = hardwareMap.get(Servo.class, "wrist");

    SPINNER = hardwareMap.get(Servo.class, "spinner");

    // One time executions
    LEFT_FRONT.setDirection(DcMotor.Direction.REVERSE);
    RIGHT_REAR.setDirection(DcMotor.Direction.REVERSE);

    ARM_JOINT_LEFT.setDirection(DcMotor.Direction.FORWARD);
    ARM_JOINT_RIGHT.setDirection(DcMotor.Direction.REVERSE);
    ARM_JOINT_LEFT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_RIGHT.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    ARM_JOINT_LEFT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_RIGHT.setTargetPosition(ARM_JOINT_MIN_ANGLE);
    ARM_JOINT_LEFT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_RIGHT.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    ARM_JOINT_LEFT.setPower(ARM_JOINT_POWER);
    ARM_JOINT_RIGHT.setPower(ARM_JOINT_POWER);

    EXTENDER.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    EXTENDER.setTargetPosition(EXTENDER_MIN_POS);
    EXTENDER.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    EXTENDER.setPower(EXTENDER_POWER);

    WRIST.setPosition(0);

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();

    runtime.reset();

    while (opModeIsActive()) {
      // Switch between modes
      if (driveMode == "normal") {
        player1 = gamepad1;
        player2 = gamepad2;
      } else if (driveMode == "god") {
        player1 = gamepad1;
        player2 = gamepad1;
      }

      // Power modes for slow and fast robot speeds
      float powerMode = player1.left_bumper
        ? SPEED_LOW_POWER
        : SPEED_HIGH_POWER;

      // God mode toggler
      if ((player1.back || player1.back) && !isModeSwitched) {
        driveMode = driveMode == "normal" ? "god" : "normal";
        isModeSwitched = true;
      }
      if (!(player1.back || player1.back)) isModeSwitched = false;

      // Dampen controls to give more precision at lower power levels
      double dampedLeftJoystickX =
        Math.signum(player1.left_stick_x) *
        Math.pow(player1.left_stick_x, MOVEMENT_PRECISION);
      double dampedLeftJoystickY =
        Math.signum(player1.left_stick_y) *
        Math.pow(player1.left_stick_y, MOVEMENT_PRECISION);
      double dampedRightJoystickX =
        Math.signum(player1.right_stick_x) *
        Math.pow(player1.right_stick_x, MOVEMENT_PRECISION);
      double dampedRightJoystickY =
        Math.signum(player1.right_stick_y) *
        Math.pow(player1.right_stick_y, MOVEMENT_PRECISION);

      // Trig to find out partial offsets in axes (plural of axis)
      // Don't mess with this unless you know what you're doing!!!
      double vectorNormal = Math.hypot(
        dampedLeftJoystickY,
        dampedLeftJoystickX
      );
      double robotAngle =
        Math.atan2(dampedLeftJoystickY, -dampedLeftJoystickX) - Math.PI / 4;
      double vector1 = vectorNormal * Math.cos(robotAngle);
      double vector2 = vectorNormal * Math.sin(robotAngle);
      double vector3 = vectorNormal * Math.sin(robotAngle);
      double vector4 = vectorNormal * Math.cos(robotAngle);

      // Apply wheel motor powers
      LEFT_FRONT.setPower((-vector1 + dampedRightJoystickX) * powerMode);
      LEFT_REAR.setPower((-vector2 + dampedRightJoystickX) * powerMode);
      RIGHT_FRONT.setPower((-vector3 - dampedRightJoystickX) * powerMode);
      RIGHT_REAR.setPower((-vector4 - dampedRightJoystickX) * powerMode);

      // Tweak arm joint target
      if (player2.right_bumper) {
        armJointTargetAngle =
          Math.min(
            armJointTargetAngle + ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MAX_ANGLE
          );
      }
      if (player2.left_bumper) {
        armJointTargetAngle =
          Math.max(
            armJointTargetAngle - ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MIN_ANGLE
          );
      }

      // Tweak extender joint target
      extenderTargetPos =
        Math.min(
          extenderTargetPos +
          Math.round((EXTENDER_INPUT_SPEED * player2.right_trigger)),
          EXTENDER_MAX_POS
        );
      extenderTargetPos =
        Math.max(
          extenderTargetPos -
          Math.round((EXTENDER_INPUT_SPEED * player2.left_trigger)),
          EXTENDER_MIN_POS
        );

      // Tweak wrist joint target
      if (player2.dpad_up) wristTargetAngle =
        Math.min(wristTargetAngle + WRIST_INPUT_SPEED, 1);
      if (player2.dpad_down) wristTargetAngle =
        Math.max(wristTargetAngle - WRIST_INPUT_SPEED, 0);

      // Apply states
      if (player2.b) {
        armJointTargetAngle = ARM_JOINT_LOW_ANGLE;
        extenderTargetPos = EXTENDER_LOW_POS;
        wristTargetAngle = WRIST_LOW_ANGLE;
      } else if (player2.x) {
        armJointTargetAngle = ARM_JOINT_MIDDLE_ANGLE;
        extenderTargetPos = EXTENDER_MIDDLE_POS;
        wristTargetAngle = WRIST_MIDDLE_ANGLE;
      } else if (player2.y) {
        armJointTargetAngle = ARM_JOINT_HIGH_ANGLE;
        extenderTargetPos = EXTENDER_HIGH_POS;
        wristTargetAngle = WRIST_HIGH_ANGLE;
      } else if (player2.a) {
        armJointTargetAngle = ARM_JOINT_GROUND_ANGLE;
        extenderTargetPos = EXTENDER_GROUND_POS;
        wristTargetAngle = WRIST_GROUND_ANGLE;
      }

      // Tweak powers based on velocities
      double armJointPowerCoefficient = Math.min(
        Math.abs(ARM_JOINT_LEFT.getVelocity()) / ARM_JOINT_MAX_VELOCITY,
        1
      );
      ARM_JOINT_LEFT.setPower(armJointPowerCoefficient * ARM_JOINT_POWER);
      ARM_JOINT_RIGHT.setPower(armJointPowerCoefficient * ARM_JOINT_POWER);

      // Apply all targets
      ARM_JOINT_LEFT.setTargetPosition(armJointTargetAngle);
      ARM_JOINT_RIGHT.setTargetPosition(armJointTargetAngle);

      EXTENDER.setTargetPosition(extenderTargetPos);

      WRIST.setPosition(wristTargetAngle);

      CLAW.setPosition(player2.dpad_right ? 0 : 1);

      // Misc.
      SPINNER.setPosition(player1.right_bumper ? 1 : 0.49);

      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Drive mode", driveMode);

      telemetry.addData("Arm joint velocity", ARM_JOINT_LEFT.getVelocity());

      telemetry.update();
    }
  }
}
