package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.core.Drive;

public class DriverControlBase {

  final Drive drive = new Drive(true);

  private final ElapsedTime runtime = new ElapsedTime();

  public Gamepad gamepad1;
  public Gamepad gamepad2;
  public HardwareMap hardwareMap;
  public Telemetry telemetry;

  private Gamepad player1 = gamepad1;
  private Gamepad player2 = gamepad2;

  // Constants
  public final float MOVEMENT_PRECISION = 2f;

  public final int EXTENDER_MIN_POS = 0;
  public final int EXTENDER_MAX_POS = 3409;

  public final int EXTENDER_INPUT_SPEED = 20;
  public final float WRIST_INPUT_SPEED = 0.01f;
  public final float WRIST_MAX_ANGLE = 0.62f;
  public final float WRIST_PULL_ANGLE = 0.32f;
  public final float WRIST_PULL_UP_START = 800;
  public final float WRIST_PULL_UP_END = 300;

  public final float CAPPER_LOW_ANGLE = 1;
  public final float CAPPER_TILT_ANGLE = 0.68f;
  public final float CAPPER_HIGH_ANGLE = 0.2f;

  int capperState = 0;

  public final float TURN_COEFFICIENT = 0.75f;

  public enum DRIVE_MODE {
    NORMAL,
    GOD,
  }

  // mutables
  DRIVE_MODE driveMode = DRIVE_MODE.NORMAL;
  boolean isModeSwitched = false;
  boolean isLeftBumperAlreadyPressed = false;

  public void init (HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2) {
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;
    this.gamepad1 = gamepad1;
    this.gamepad2 = gamepad2;

    this.drive.init(hardwareMap, telemetry);
  }

  public void iterate() {

    // Switch between modes
    if (driveMode == DRIVE_MODE.NORMAL) {
      player1 = gamepad1;
      player2 = gamepad2;
    } else if (driveMode == DRIVE_MODE.GOD) {
      player1 = gamepad1;
      player2 = gamepad1;
    }

    // Power modes for slow and fast robot speeds
    drive.movementPower = 0.75f - (player1.left_trigger * 0.5f);

    // God mode toggler
    if ((player1.back || player2.back) && !isModeSwitched) {
      driveMode =
        driveMode == DRIVE_MODE.NORMAL ? DRIVE_MODE.GOD : DRIVE_MODE.NORMAL;
      isModeSwitched = true;
    }
    if (!(player1.back || player2.back)) isModeSwitched = false;

    if (driveMode == DRIVE_MODE.NORMAL) {
      player1 = gamepad1;
      player2 = gamepad2;
    }

    // Tweak spinner joint speed
    drive.spinnerJointPos = player1.right_trigger;

    // Dampen controls to give more precision at lower power levels
    double dampedLeftJoystickX =
      Math.signum(player1.left_stick_x) *
      Math.pow(player1.left_stick_x, MOVEMENT_PRECISION);
    double dampedLeftJoystickY =
      Math.signum(player1.left_stick_y) *
      Math.pow(player1.left_stick_y, MOVEMENT_PRECISION);
    double dampedRightJoystickX =
      Math.signum(player1.right_stick_x) *
      Math.pow(player1.right_stick_x, MOVEMENT_PRECISION * 2);
    double dampedRightJoystickY = // unused for now
      Math.signum(player1.right_stick_y) *
      Math.pow(player1.right_stick_y, MOVEMENT_PRECISION * 2);

    drive.moveX = (float) dampedLeftJoystickX;
    drive.moveY = (float) -dampedLeftJoystickY;
    drive.rot = (float) dampedRightJoystickX * TURN_COEFFICIENT;

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
    float progressToPullUpStart = Math.max(
      Math.min(
        (drive.extenderTargetPos - WRIST_PULL_UP_END) / WRIST_PULL_UP_START,
        1
      ),
      0
    );
    float compensatedWristMinAngle =
      (1 - progressToPullUpStart) * WRIST_PULL_ANGLE;

    if (player2.dpad_up) drive.wristTargetAngle += WRIST_INPUT_SPEED;
    if (player2.dpad_down) drive.wristTargetAngle -= WRIST_INPUT_SPEED;

    drive.wristTargetAngle =
      Math.min(
        Math.max(drive.wristTargetAngle, compensatedWristMinAngle),
        WRIST_MAX_ANGLE
      );

    // Apply states
    if (!player2.start) {
      if (player2.a) {
        drive.setState(Drive.ARM_STATE.BOTTOM);
      } else if (player2.x) {
        drive.setState(Drive.ARM_STATE.MIDDLE);
      } else if (player2.y) {
        drive.setState(Drive.ARM_STATE.TOP);
      } else if (player2.b) {
        drive.setState(Drive.ARM_STATE.GROUND);
      }
    }

    drive.clawTargetState = player2.right_bumper ? 0 : 1;
    if (player1.right_bumper) drive.spinnerSpeed = 1;
    if (player1.left_bumper) drive.spinnerSpeed = 0;
    if (!player1.right_bumper && !player1.left_bumper) drive.spinnerSpeed =
      0.49f;

    // control the capper
    if (player2.left_bumper) {
      if (!isLeftBumperAlreadyPressed) {
        isLeftBumperAlreadyPressed = true;

        if (capperState == 3) {
          capperState = 0;
        } else {
          capperState++;
        }
      }
    } else {
      isLeftBumperAlreadyPressed = false;
    }

    if (capperState == 0) {
      drive.capperTargetAngle = CAPPER_HIGH_ANGLE;
    } else if (capperState == 1 || capperState == 3) {
      drive.capperTargetAngle = CAPPER_TILT_ANGLE;
    } else {
      drive.capperTargetAngle = CAPPER_LOW_ANGLE;
    }

    drive.apply();
    drive.compensateForVoltage();

    // update telemetry
    telemetry.addData("Status", "Run Time: " + runtime.toString());
    telemetry.addData("Drive mode", driveMode);
    telemetry.addData("Battery Voltage", drive.getBatteryVoltage());
    telemetry.addData("----------", "----------");

    drive.logTargetsAndCurrents();

    telemetry.update();
  }
}
