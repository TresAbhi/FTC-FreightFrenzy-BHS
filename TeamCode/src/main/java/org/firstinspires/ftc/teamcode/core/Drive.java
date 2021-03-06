package org.firstinspires.ftc.teamcode.core;

import android.os.SystemClock;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.DriverControlBase;

import java.util.concurrent.TimeUnit;

public class Drive {

  // Components
  public DcMotor leftFront, leftRear, rightFront, rightRear;

  public DcMotor extender;
  public Servo wrist, claw, capper;

  public Servo spinner, spinnerJoint;

  public BNO055IMU imu;

  // Constants
  public final double EXTENDER_POWER = 0.8;
  public final int EXTENDER_MIN_POS = 0;

  public final double NORMAL_VOLTAGE = 13;

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
  public final double WRIST_LOW_ANGLE = 0.33;

  // x: middle tower layer
  public final int EXTENDER_MIDDLE_POS = EXTENDER_MIN_POS + 1253;
  public final double WRIST_MIDDLE_ANGLE = 0.33;

  // y: top tower layer
  public final int EXTENDER_HIGH_POS = EXTENDER_MIN_POS + 2080;
  public final double WRIST_HIGH_ANGLE = 0.33;

  // b: ground
  public final int EXTENDER_GROUND_POS = EXTENDER_MIN_POS;
  public final double WRIST_GROUND_ANGLE = 0.33;

  // NONE: back
  public final int EXTENDER_BACK_POS = EXTENDER_MIN_POS;
  public final double WRIST_BACK_ANGLE = 0.62;

  // Mutables
  public int extenderTargetPos = EXTENDER_MIN_POS;

  public double wristTargetAngle = 0.62;
  public double clawTargetState = 1;

  public double capperTargetAngle = 0.2;

  public double spinnerSpeed = 0.49;
  public double spinnerJointPos = 0;

  public double moveX = 0;
  public double moveY = 0;
  public double rot = 0;

  public double movementPower = 1;

  public boolean useGyro = false;
  private Orientation angles;
  public double headingOffset = 0;

  BNO055IMU.Parameters imuParameters = new BNO055IMU.Parameters();

  public HardwareMap hardwareMap;
  public Telemetry telemetry;

  public Drive(boolean useGyro) {
    this.useGyro = useGyro;
  }

  public Drive() {}

  public void init(HardwareMap hardwareMap, Telemetry telemetry) {
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;

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

    imu = hardwareMap.get(BNO055IMU.class, "imu");

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
    claw.setPosition(clawTargetState);
    capper.setPosition(capperTargetAngle);
    spinnerJoint.setPosition(0);

    imuParameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
    imu.initialize(imuParameters);
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
    angles =
      imu.getAngularOrientation(
        AxesReference.INTRINSIC,
        AxesOrder.ZYX,
        AngleUnit.RADIANS
      );

    // Don't mess with this unless you know what you're doing!!!
    double vectorNormal = Math.hypot(moveX, -moveY);
    double robotAngle =
      Math.atan2(-moveY, -moveX) -
      Math.PI /
      4 -
      (useGyro ? angles.firstAngle - headingOffset : 0);
    double vector1 = vectorNormal * Math.cos(robotAngle);
    double vector2 = vectorNormal * Math.sin(robotAngle);
    double vector3 = vectorNormal * Math.sin(robotAngle);
    double vector4 = vectorNormal * Math.cos(robotAngle);

    // Apply wheel motor powers
    leftFront.setPower(
      (-vector1 + rot) * movementPower
    );
    leftRear.setPower(
      (-vector2 + rot) * movementPower
    );
    rightFront.setPower(
      (-vector3 - rot) * movementPower
    );
    rightRear.setPower(
      (-vector4 - rot) * movementPower
    );

    // Apply all targets
    extender.setTargetPosition(-extenderTargetPos);

    wrist.setPosition(wristTargetAngle);

    claw.setPosition(clawTargetState);

    capper.setPosition(capperTargetAngle);

    spinner.setPosition(spinnerSpeed);
    spinnerJoint.setPosition(spinnerJointPos);
  }

  public double getBatteryVoltage() {
    double result = Double.POSITIVE_INFINITY;

    for (VoltageSensor sensor : hardwareMap.voltageSensor) {
      double voltage = sensor.getVoltage();

      if (voltage > 0) {
        result = Math.min(result, voltage);
      }
    }

    return (double) result;
  }

  public void logTargetsAndCurrents() {
    telemetry.addData("extender target", extenderTargetPos);
    telemetry.addData("extender pos", extender.getCurrentPosition());

    telemetry.addData("wrist target", wristTargetAngle);
    telemetry.addData("wrist pos", wrist.getPosition());

    telemetry.addData("claw target", clawTargetState);
    telemetry.addData("claw pos", claw.getPosition());

    telemetry.addData("capper target", capperTargetAngle);
    telemetry.addData("capper pos", capper.getPosition());

    telemetry.addData("spinner target", spinnerSpeed);
    telemetry.addData("spinner speed", spinner.getPosition());

    telemetry.addData("spinner joint target", spinnerJointPos);
    telemetry.addData("spinner joint pos", spinnerJoint.getPosition());

    telemetry.addData("Heading", angles.firstAngle);
    telemetry.addData("Heading offset", headingOffset);
  }
}
