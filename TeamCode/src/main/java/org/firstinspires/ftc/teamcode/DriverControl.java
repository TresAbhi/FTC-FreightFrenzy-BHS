package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "DriverControl", group = "Linear Opmode")
// @Disabled
public class DriverControl extends LinearOpMode {

  DriverControlAPI driverControlAPI = new DriverControlAPI();

  private ElapsedTime runtime = new ElapsedTime();

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  public float MOVEMENT_PRECISION = 2f;

  public int EXTENDER_MIN_POS = 40;
  public int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1490;

  public int EXTENDER_INPUT_SPEED = 24;
  public float WRIST_INPUT_SPEED = 0.005f;

  public float SPEED_LOW_POWER = 0.4f;
  public float SPEED_HIGH_POWER = 0.8f;

  // mutables
  String driveMode = "normal";
  boolean isModeSwitched = false;

  // @Override
  public void runOpMode() {
    driverControlAPI.init(hardwareMap);

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
      driverControlAPI.movementPower =
        player1.left_bumper ? SPEED_LOW_POWER : SPEED_HIGH_POWER;

      // God mode toggler
      if ((player1.back || player2.back) && !isModeSwitched) {
        driveMode = driveMode == "normal" ? "god" : "normal";
        isModeSwitched = true;
      }
      if (!(player1.back || player2.back)) isModeSwitched = false;

      // Tweak spinner joint speed
      driverControlAPI.spinnerJointSpeed = player1.right_trigger;

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
      double dampedRightJoystickY = // unused for now
        Math.signum(player1.right_stick_y) *
        Math.pow(player1.right_stick_y, MOVEMENT_PRECISION);

      driverControlAPI.moveX = dampedLeftJoystickX;
      driverControlAPI.moveY = dampedLeftJoystickY;
      driverControlAPI.rotX = dampedRightJoystickX;

      // Tweak extender joint target
      driverControlAPI.extenderTargetPos =
        Math.min(
          driverControlAPI.extenderTargetPos +
          Math.round(EXTENDER_INPUT_SPEED * player2.right_trigger),
          EXTENDER_MAX_POS
        );
      driverControlAPI.extenderTargetPos =
        Math.max(
          driverControlAPI.extenderTargetPos -
          Math.round(EXTENDER_INPUT_SPEED * player2.left_trigger),
          EXTENDER_MIN_POS
        );

      // Tweak wrist joint target
      driverControlAPI.wristTargetAngle =
        Math.min(driverControlAPI.wristTargetAngle + WRIST_INPUT_SPEED, 1);
      if (player2.dpad_down) driverControlAPI.wristTargetAngle =
        Math.max(driverControlAPI.wristTargetAngle - WRIST_INPUT_SPEED, 0);

      // Apply states
      if (player2.b) {
        driverControlAPI.setState(DriverControlAPI.STATE.LOW);
      } else if (player2.x) {
        driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
      } else if (player2.y) {
        driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
      } else if (player2.a) {
        driverControlAPI.setState(DriverControlAPI.STATE.GROUND);
      }

      driverControlAPI.clawTargetState = player2.right_bumper ? 0 : 1;
      driverControlAPI.spinnerSpeed = player1.right_bumper ? 1f : 0.49f;

      driverControlAPI.apply();

      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Drive mode", driveMode);

      telemetry.update();
    }
  }
}
