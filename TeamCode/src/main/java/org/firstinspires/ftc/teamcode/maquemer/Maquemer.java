package org.firstinspires.ftc.teamcode.maquemer;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Maquemer {
    HardwareMap hardwareMap;
    Telemetry telemetry;

    Maquemer(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }
}