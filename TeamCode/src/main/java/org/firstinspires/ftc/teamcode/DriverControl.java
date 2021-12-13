package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DriverControl", group = "Linear Opmode")
// @Disabled
public class DriverControl extends LinearOpMode {

  DriverControlAPI driverControlAPI = new DriverControlAPI(hardwareMap);

  private ElapsedTime runtime = new ElapsedTime();

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  public float MOVEMENT_PRECISION = 2f;

  public int ARM_JOINT_MIN_ANGLE = 55;
  public int ARM_JOINT_MAX_ANGLE = ARM_JOINT_MIN_ANGLE + 430;

  public int EXTENDER_MIN_POS = 40;
  public int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1490;

  public int ARM_JOINT_INPUT_SPEED = 4;
  public int EXTENDER_INPUT_SPEED = 24;
  public float WRIST_INPUT_SPEED = 0.03f;

  public float SPEED_LOW_POWER = 0.4f;
  public float SPEED_HIGH_POWER = 0.8f;

  // Preset states
  // b: lower tower layer
  public int ARM_JOINT_LOW_ANGLE = ARM_JOINT_MIN_ANGLE + 310;
  public int EXTENDER_LOW_POS = EXTENDER_MIN_POS;
  public float WRIST_LOW_ANGLE = 0.8f;

  // x: middle tower layer
  public int ARM_JOINT_MIDDLE_ANGLE = ARM_JOINT_MIN_ANGLE + 245;
  public int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 110;
  public float WRIST_MIDDLE_ANGLE = 0.75f;

  // y: top tower layer
  public int ARM_JOINT_HIGH_ANGLE = ARM_JOINT_MIN_ANGLE + 190;
  public int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 630;
  public float WRIST_HIGH_ANGLE = 0.65f;

  // x: ground
  public int ARM_JOINT_GROUND_ANGLE = ARM_JOINT_MIN_ANGLE + 430;
  public int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public float WRIST_GROUND_ANGLE = 1f;

  // mutables
  String driveMode = "normal";
  boolean isModeSwitched = false;

  // @Override
  public void runOpMode() {
    telemetry.addData("Status", "Initialized");
    telemetry.update();

    driverControlAPI.init(hardwareMap);
    driverControlAPI.ARM_JOINT_MIN_ANGLE = ARM_JOINT_MIN_ANGLE;
    driverControlAPI.EXTENDER_MIN_POS = EXTENDER_MIN_POS;

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
      driverControlAPI.movementPower =
        player1.left_bumper ? SPEED_LOW_POWER : SPEED_HIGH_POWER;

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

      driverControlAPI.moveX = dampedLeftJoystickX;
      driverControlAPI.moveY = dampedLeftJoystickY;
      driverControlAPI.rotX = dampedRightJoystickX;
      driverControlAPI.rotY = dampedRightJoystickY;

      // Tweak arm joint target
      if (player2.right_bumper) {
        driverControlAPI.armJointTargetAngle =
          Math.min(
            driverControlAPI.armJointTargetAngle + ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MAX_ANGLE
          );
      }
      if (player2.left_bumper) {
        driverControlAPI.armJointTargetAngle =
          Math.max(
            driverControlAPI.armJointTargetAngle - ARM_JOINT_INPUT_SPEED,
            ARM_JOINT_MIN_ANGLE
          );
      }

      // Tweak extender joint target
      driverControlAPI.extenderTargetPos =
        Math.min(
          driverControlAPI.extenderTargetPos +
          Math.round((EXTENDER_INPUT_SPEED * player2.right_trigger)),
          EXTENDER_MAX_POS
        );
      driverControlAPI.extenderTargetPos =
        Math.max(
          driverControlAPI.extenderTargetPos -
          Math.round((EXTENDER_INPUT_SPEED * player2.left_trigger)),
          EXTENDER_MIN_POS
        );

      // Tweak wrist joint target
      if (player2.dpad_up) driverControlAPI.wristTargetAngle =
        Math.min(driverControlAPI.wristTargetAngle + WRIST_INPUT_SPEED, 1);
      if (player2.dpad_down) driverControlAPI.wristTargetAngle =
        Math.max(driverControlAPI.wristTargetAngle - WRIST_INPUT_SPEED, 0);

      // Apply states
      if (player2.b) {
        driverControlAPI.armJointTargetAngle = ARM_JOINT_LOW_ANGLE;
        driverControlAPI.extenderTargetPos = EXTENDER_LOW_POS;
        driverControlAPI.wristTargetAngle = WRIST_LOW_ANGLE;
      } else if (player2.x) {
        driverControlAPI.armJointTargetAngle = ARM_JOINT_MIDDLE_ANGLE;
        driverControlAPI.extenderTargetPos = EXTENDER_MIDDLE_POS;
        driverControlAPI.wristTargetAngle = WRIST_MIDDLE_ANGLE;
      } else if (player2.y) {
        driverControlAPI.armJointTargetAngle = ARM_JOINT_HIGH_ANGLE;
        driverControlAPI.extenderTargetPos = EXTENDER_HIGH_POS;
        driverControlAPI.wristTargetAngle = WRIST_HIGH_ANGLE;
      } else if (player2.a) {
        driverControlAPI.armJointTargetAngle = ARM_JOINT_GROUND_ANGLE;
        driverControlAPI.extenderTargetPos = EXTENDER_GROUND_POS;
        driverControlAPI.wristTargetAngle = WRIST_GROUND_ANGLE;
      }

      driverControlAPI.clawTargetState = player2.dpad_left ? 0 : 1;
      driverControlAPI.spinnerSpeed = player1.right_bumper ? 1f : 0.5f;

      driverControlAPI.apply();

      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Drive mode", driveMode);

      telemetry.update();
    }
  }
}
