package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.core.DriverControlAPI;

@TeleOp(name = "DriverControl", group = "Linear Opmode")
// @Disabled
public class DriverControl extends LinearOpMode {

  final DriverControlAPI drive = new DriverControlAPI();

  private final ElapsedTime runtime = new ElapsedTime();

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  public final float MOVEMENT_PRECISION = 2f;

  public final int EXTENDER_MIN_POS = 0;
  public final int EXTENDER_MAX_POS = EXTENDER_MIN_POS + 1550;

  public final int EXTENDER_INPUT_SPEED = 10;
  public final float WRIST_INPUT_SPEED = 0.005f;
  public final float WRIST_MIN_ANGLE = 0.425f;

  // mutables
  String driveMode = "normal";
  boolean isModeSwitched = false;

  // @Override
  public void runOpMode() {
    drive.init(hardwareMap);

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
      drive.movementPower = 1 - (player1.left_trigger * 0.5f);

      // God mode toggler
      if ((player1.back || player2.back) && !isModeSwitched) {
        driveMode = driveMode == "normal" ? "god" : "normal";
        isModeSwitched = true;
      }
      if (!(player1.back || player2.back)) isModeSwitched = false;

      // Tweak spinner joint speed
      drive.spinnerJointSpeed = player1.right_trigger;

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

      drive.moveX = (float) dampedLeftJoystickX;
      drive.moveY = (float) dampedLeftJoystickY;
      drive.rot = (float) dampedRightJoystickX;

      // Tweak extender joint target
      drive.extenderTargetPos =
        Math.min(
          drive.extenderTargetPos +
          Math.round(EXTENDER_INPUT_SPEED * player2.right_trigger),
          EXTENDER_MAX_POS
        );
      drive.extenderTargetPos =
        Math.max(
          drive.extenderTargetPos -
          Math.round(EXTENDER_INPUT_SPEED * player2.left_trigger),
          EXTENDER_MIN_POS
        );

      // Tweak wrist joint target
      if (player2.dpad_up) drive.wristTargetAngle =
        Math.min(drive.wristTargetAngle + WRIST_INPUT_SPEED, 1);
      if (player2.dpad_down) drive.wristTargetAngle =
        Math.max(drive.wristTargetAngle - WRIST_INPUT_SPEED, WRIST_MIN_ANGLE);

      // Apply states
      if (!player2.start) {
        if (player2.a) {
          drive.setState(DriverControlAPI.STATE.LOW);
        } else if (player2.x) {
          drive.setState(DriverControlAPI.STATE.MIDDLE);
        } else if (player2.y) {
          drive.setState(DriverControlAPI.STATE.HIGH);
        } else if (player2.b) {
          drive.setState(DriverControlAPI.STATE.GROUND);
        }
      }

      drive.clawTargetState = player2.right_bumper ? 0 : 1;
      if (player1.right_bumper) drive.spinnerSpeed = 1;
      if (player1.left_bumper) drive.spinnerSpeed = 0;
      if (!player1.right_bumper && !player1.left_bumper) drive.spinnerSpeed =
        0.49f;

      drive.apply();
      drive.compensateForVoltage();

      // Update telemetry
      telemetry.addData("Status", "Run Time: " + runtime.toString());
      telemetry.addData("Drive mode", driveMode);

      telemetry.addData("Extender", drive.extenderTargetPos);
      telemetry.addData("wrist angle", drive.wristTargetAngle);

      telemetry.addData("Battery Voltage", drive.getBatteryVoltage());

      telemetry.update();
    }
  }
}
