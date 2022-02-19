package org.firstinspires.ftc.teamcode.maquemer;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.maquemer.util.MotorTracker;

public class Maquemer {
    HardwareMap hardwareMap;
    Telemetry telemetry;

    public DcMotor leftFront, leftRear, rightFront, rightRear;

    public Maquemer(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void init (HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        leftFront = hardwareMap.get(DcMotor.class, "left_front");
        leftRear = hardwareMap.get(DcMotor.class, "left_rear");
        rightRear = hardwareMap.get(DcMotor.class, "right_rear");
        rightFront = hardwareMap.get(DcMotor.class, "right_front");

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void moveY (double y) {
        MotorTracker leftFrontTracker = new MotorTracker(leftFront, y, 5);

        while (!leftFrontTracker.done) {
            leftFrontTracker.iterate();
        }
    }
}