package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.maquemer.Maquemer;

//@Disabled
@Autonomous(name = "AA-PROTO-MaquemerTest", group = "A")
public class MaquemerTest extends LinearOpMode {

  public Maquemer maquemer = new Maquemer(telemetry);

  // @Override
  public void runOpMode() {
    maquemer.init(hardwareMap, telemetry);

    waitForStart();

    maquemer.moveY(537.6);
  }
}
