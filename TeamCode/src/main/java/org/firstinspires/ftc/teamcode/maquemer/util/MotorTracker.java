package org.firstinspires.ftc.teamcode.maquemer.util;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.maquemer.Constants;
import org.opencv.core.Mat;

public class MotorTracker {

    DcMotor motor;
    Constants constants = new Constants();

    public double target;
    public double threshold;
    public double start;
    public double direction;
    public double current;
    public double progress = 0;
    public boolean done = false;

    public MotorTracker (DcMotor motor, double target, double threshold) {
        this.motor = motor;

        this.start = motor.getTargetPosition();
        this.target = start + target;
        this.threshold = threshold;
        this.current = start;
        this.direction = Math.signum(this.target - current);
    }

    public void iterate () {
        current = motor.getCurrentPosition();
        progress = Math.abs(current - start) / Math.abs(target - start);
        done = Math.abs(target - current) <= threshold;

        if (done) {
            motor.setPower(0);
        } else {
            motor.setPower(constants.progressToPower(progress));
        }
    }
}
