package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
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

  private DcMotor ARM_JOINT_LEFT = null;
  private DcMotor ARM_JOINT_RIGHT = null;
  private DcMotor EXTENDER = null;

  private Servo CLAW = null;
  private Servo WRIST = null;

  private Servo SPINNER = null;

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  float MOVEMENT_PRECISION = 2f;

  int ARM_JOINT_MIN_ANGLE = 40;
  int ARM_JOINT_MAX_ANGLE = ARM_JOINT_MIN_ANGLE + 415;
  float ARM_JOINT_POWER = 0.2f;
  int ARM_JOINT_INPUT_SPEED = 4;

  int EXTENDER_MIN_POS = 40;
  int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1490;
  float EXTENDER_POWER = 0.2f;
  int EXTENDER_INPUT_SPEED = 24;

  float SPEED_LOW_POWER = 0.4f;
  float SPEED_HIGH_POWER = 0.8f;

  // Preset states
  int ARM_LOW_JOINT_ANGLE;
  int ARM_LOW_EXTENDER_POS;
  int ARM_LOW_WRIST_ANGLE;

  int ARM_MIDDLE_JOINT_ANGLE;
  int ARM_MIDDLE_EXTENDER_POS;
  int ARM_MIDDLE_WRIST_ANGLE;

  int ARM_HIGH_JOINT_ANGLE;
  int ARM_HIGH_EXTENDER_POS;
  int ARM_HIGH_WRIST_ANGLE;

  int ARM_GROUND_JOINT_ANGLE;
  int ARM_GROUND_EXTENDER_POS;
  int ARM_GROUND_WRIST_ANGLE;

  // Mutables
  int armJointTargetPosition = ARM_JOINT_MIN_ANGLE;
  int extenderTargetPosition = EXTENDER_MIN_POS;

  String driveMode = "normal";
  boolean isModeSwitched = false;

  @Override
  public void runOpMode() {
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

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();

    runtime.reset();

    while (opModeIsActive()) {
      if (driveMode == "normal") {
        player1 = gamepad1;
        player2 = gamepad2;
      } else if (driveMode == "god") {
        player1 = gamepad1;
        player2 = gamepad1;
      }

      float powerMode = player1.left_bumper
        ? SPEED_LOW_POWER
        : SPEED_HIGH_POWER;

      if ((player1.back || player1.back) && !isModeSwitched) {
        driveMode = driveMode == "normal" ? "god" : "normal";
        isModeSwitched = true;
      }
      if (!(player1.back || player1.back)) isModeSwitched = false;

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

      /**
       * Trig to find out partial offsets in axes (plural of axis)
       *
       * Don't mess with this unless you know what you're doing!!!
       */
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

      LEFT_FRONT.setPower((-vector1 + dampedRightJoystickX) * powerMode);
      LEFT_REAR.setPower((-vector2 + dampedRightJoystickX) * powerMode);
      RIGHT_FRONT.setPower((-vector3 - dampedRightJoystickX) * powerMode);
      RIGHT_REAR.setPower((-vector4 - dampedRightJoystickX) * powerMode);

      if (player2.right_bumper) {
        armJointTargetPosition =
          Math.min(
            armJointTargetPosition + ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MAX_ANGLE
          );
      }
      if (player2.left_bumper) {
        armJointTargetPosition =
          Math.max(
            armJointTargetPosition - ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MIN_ANGLE
          );
      }

      ARM_JOINT_LEFT.setTargetPosition(armJointTargetPosition);
      ARM_JOINT_RIGHT.setTargetPosition(armJointTargetPosition);

      extenderTargetPosition =
        Math.min(
          extenderTargetPosition +
          Math.round((EXTENDER_INPUT_SPEED * player2.right_trigger)),
          EXTENDER_MAX_POS
        );
      extenderTargetPosition =
        Math.max(
          extenderTargetPosition -
          Math.round((EXTENDER_INPUT_SPEED * player2.left_trigger)),
          EXTENDER_MIN_POS
        );

      EXTENDER.setTargetPosition(extenderTargetPosition);

      SPINNER.setPosition(player1.right_bumper ? 1 : 0.49);
      CLAW.setPosition(player2.dpad_right ? 1 : 0);
      WRIST.setPosition(player2.dpad_up ? 1 : 0);

      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Drive mode", driveMode);

      telemetry.addData("Extender position", EXTENDER.getCurrentPosition());
      telemetry.addData("Extender target position", extenderTargetPosition);

      telemetry.addData("Arm position", ARM_JOINT_LEFT.getCurrentPosition());
      telemetry.addData("Arm target position", armJointTargetPosition);

      telemetry.addData("left joint", ARM_JOINT_LEFT.getCurrentPosition());
      telemetry.addData("right joint", ARM_JOINT_RIGHT.getCurrentPosition());

      telemetry.update();
    }
  }
}
