package org.firstinspires.ftc.teamcode.maquemer.util;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.maquemer.Constants;

public class MotorTracker {

    DcMotor motor;
    Constants constants = new Constants();

    public boolean done = false;
    public double progress = 0;

    public MotorTracker (DcMotor motor, double target, double threshold) {
        this.motor = motor;
    }

    public void iterate () {
        if (done) {
            motor.setPower(0);
        } else {
            motor.setPower(0.1);
            this.progress = motor.getCurrentPosition();
        }
    }
}
