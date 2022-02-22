package org.firstinspires.ftc.teamcode.core;

import android.os.SystemClock;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drive {

  // Components
  public DcMotor leftFront, leftRear, rightFront, rightRear;

  public DcMotor extender;
  public Servo wrist, claw, capper;

  public Servo spinner, spinnerJoint;

  // Constants
  public final float EXTENDER_POWER = 0.8f;
  public final int EXTENDER_MIN_POS = 0;

  public final float NORMAL_VOLTAGE = 13;

  // Preset states
  public enum ARM_STATE {
    BOTTOM,
    MIDDLE,
    TOP,
    GROUND,
    DEFAULT,
  }

  // a: lower tower layer
  public final int EXTENDER_LOW_POS = EXTENDER_MIN_POS + 527;
  public final float WRIST_LOW_ANGLE = 0.33f;

  // x: middle tower layer
  public final int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 1253;
  public final float WRIST_MIDDLE_ANGLE = 0.33f;

  // y: top tower layer
  public final int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 2080;
  public final float WRIST_HIGH_ANGLE = 0.33f;

  // b: ground
  public final int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public final float WRIST_GROUND_ANGLE = 0.33f;

  // NONE: back
  public final int EXTENDER_BACK_POS = EXTENDER_MIN_POS;
  public final float WRIST_BACK_ANGLE = 1f;

  // Mutables
  public int extenderTargetPos = EXTENDER_MIN_POS;

  public float wristTargetAngle = 0.62f;
  public float clawTargetAngle = 0.62f;

  public boolean capperTargetState = false;

  public float spinnerSpeed = 0.49f;
  public float spinnerJointPos = 0f;

  public float moveX = 0;
  public float moveY = 0;
  public float rot = 0;

  public float movementPower = 1f;
  public float voltageCompensatedPower = 1f;

  public HardwareMap hardwareMap;
  public Telemetry telemetry;

  public void init(HardwareMap hm) {
    hardwareMap = hm;

    // Components
    leftFront = hardwareMap.get(DcMotor.class, "left_front"); // slot 0
    leftRear = hardwareMap.get(DcMotor.class, "left_rear"); // slot 1
    rightRear = hardwareMap.get(DcMotor.class, "right_rear"); // slot 2
    rightFront = hardwareMap.get(DcMotor.class, "right_front"); // slot 3

    extender = hardwareMap.get(DcMotor.class, "extender");
    claw = hardwareMap.get(Servo.class, "claw");
    wrist = hardwareMap.get(Servo.class, "wrist");
    capper = hardwareMap.get(Servo.class, "capper");

    spinner = hardwareMap.get(Servo.class, "spinner");
    spinnerJoint = hardwareMap.get(Servo.class, "spinner_joint");

    // One time executions
    leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    rightRear.setDirection(DcMotorSimple.Direction.REVERSE);

    extender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    extender.setTargetPosition(-EXTENDER_MIN_POS);
    extender.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    extender.setPower(EXTENDER_POWER);

    wrist.setPosition(wristTargetAngle);
    claw.setPosition(clawTargetAngle);
    capper.setPosition(capperTargetState ? 1 : 0);
    spinnerJoint.setPosition(0);
  }

  public void init(HardwareMap hm, Telemetry tl) {
    telemetry = tl;
    init(hm);
  }

  public void setState(ARM_STATE state) {
    if (state == ARM_STATE.BOTTOM) {
      wristTargetAngle = WRIST_LOW_ANGLE;
      extenderTargetPos = EXTENDER_LOW_POS;
      apply();
    } else if (state == ARM_STATE.MIDDLE) {
      wristTargetAngle = WRIST_MIDDLE_ANGLE;
      extenderTargetPos = EXTENDER_MIDDLE_POS;
      apply();
    } else if (state == ARM_STATE.TOP) {
      wristTargetAngle = WRIST_HIGH_ANGLE;
      extenderTargetPos = EXTENDER_HIGH_POS;
      apply();
    } else if (state == ARM_STATE.GROUND) {
      wristTargetAngle = WRIST_GROUND_ANGLE;
      extenderTargetPos = EXTENDER_GROUND_POS;
      apply();
    } else if (state == ARM_STATE.DEFAULT) {
      wristTargetAngle = WRIST_BACK_ANGLE;
      extenderTargetPos = EXTENDER_BACK_POS;
      apply();
    }
  }

  public void apply() {
    // Trig to find out partial offsets in axes (plural of axis)
    // Don't mess with this unless you know what you're doing!!!
    double vectorNormal = Math.hypot(moveX, -moveY);
    double robotAngle = Math.atan2(-moveY, -moveX) - Math.PI / 4;
    double vector1 = vectorNormal * Math.cos(robotAngle);
    double vector2 = vectorNormal * Math.sin(robotAngle);
    double vector3 = vectorNormal * Math.sin(robotAngle);
    double vector4 = vectorNormal * Math.cos(robotAngle);

    // Apply wheel motor powers
    leftFront.setPower(
      (-vector1 + rot) * movementPower * voltageCompensatedPower
    );
    leftRear.setPower(
      (-vector2 + rot) * movementPower * voltageCompensatedPower
    );
    rightFront.setPower(
      (-vector3 - rot) * movementPower * voltageCompensatedPower
    );
    rightRear.setPower(
      (-vector4 - rot) * movementPower * voltageCompensatedPower
    );

    // Apply all targets
    extender.setTargetPosition(-extenderTargetPos);

    wrist.setPosition(wristTargetAngle);

    claw.setPosition(clawTargetAngle);

    capper.setPosition(capperTargetState ? 1 : 0);

    spinner.setPosition(spinnerSpeed);
    spinnerJoint.setPosition(spinnerJointPos);
  }

  public float getBatteryVoltage() {
    double result = Double.POSITIVE_INFINITY;

    for (VoltageSensor sensor : hardwareMap.voltageSensor) {
      double voltage = sensor.getVoltage();

      if (voltage > 0) {
        result = Math.min(result, voltage);
      }
    }

    return (float) result;
  }

  public float compensateForVoltage(int sampleCount) {
    float averageBatteryVoltage = getBatteryVoltage();

    for (int i = 0; i < sampleCount - 1; i++) {
      averageBatteryVoltage += getBatteryVoltage();
      SystemClock.sleep(100);
    }

    averageBatteryVoltage = averageBatteryVoltage / sampleCount;
    voltageCompensatedPower = NORMAL_VOLTAGE / averageBatteryVoltage;

    return averageBatteryVoltage;
  }

  public void compensateForVoltage() {
    compensateForVoltage(1);
  }

  public void logTargetsAndCurrents() {
    telemetry.addData("extender target", extenderTargetPos);
    telemetry.addData("extender pos", extender.getCurrentPosition());

    telemetry.addData("wrist target", wristTargetAngle);
    telemetry.addData("wrist pos", wrist.getPosition());

    telemetry.addData("claw target", clawTargetAngle);
    telemetry.addData("claw pos", claw.getPosition());

    telemetry.addData("capper target", capperTargetState);
    telemetry.addData("capper pos", capper.getPosition());

    telemetry.addData("spinner target", spinnerSpeed);
    telemetry.addData("spinner speed", spinner.getPosition());

    telemetry.addData("spinner joint target", spinnerJointPos);
    telemetry.addData("spinner joint pos", spinnerJoint.getPosition());
  }
}
