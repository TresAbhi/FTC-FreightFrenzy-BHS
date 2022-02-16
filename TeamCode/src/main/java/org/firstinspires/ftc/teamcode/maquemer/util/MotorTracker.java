package org.firstinspires.ftc.teamcode.maquemer.util;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.maquemer.Constants;
import org.opencv.core.Mat;

public class MotorTracker {

    Constants constants = new Constants();

    DcMotor motor;

    double start;
    double direction;
    double current;
    double target;

    public boolean done = false;

    public MotorTracker (DcMotor motor, double target, double threshold) {
        this.motor = motor;
        this.start = motor.getCurrentPosition();
        this.target = target + start;

        this.direction = Math.signum(this.target - start);
        this.current = start;
    }

    public void iterate () {
        done = Math.abs(target - start) < target;

        current = motor.getCurrentPosition();
        double progress = (current - start) / (target - start);

        if (done) {
            motor.setPower(0);
        } else {
            motor.setPower(direction * constants.progressToPower(progress));
        }
    }
}
