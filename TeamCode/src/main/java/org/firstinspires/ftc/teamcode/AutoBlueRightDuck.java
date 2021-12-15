package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "AutoBlueRightDuck", group = "A")
// @Disabled
public class AutoBlueRightDuck extends LinearOpMode {

  private ElapsedTime runtime = new ElapsedTime();

  OpenCvWebcam webcam;
  TeamScoreDetector teamScoreDetector = new TeamScoreDetector(telemetry);
  DriverControlAPI driverControlAPI = new DriverControlAPI(hardwareMap);

  // @Override
  public void runOpMode() {
    int cameraMonitorViewId = hardwareMap.appContext
      .getResources()
      .getIdentifier(
        "cameraMonitorViewId",
        "id",
        hardwareMap.appContext.getPackageName()
      );
    webcam =
      OpenCvCameraFactory
        .getInstance()
        .createWebcam(
          hardwareMap.get(WebcamName.class, "webcam"),
          cameraMonitorViewId
        );

    webcam.openCameraDevice();
    webcam.setPipeline(teamScoreDetector);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    driverControlAPI.init(hardwareMap);

    telemetry.update();

    // don't burn CPU cycles busy-looping in this sample
    sleep(200);

    TeamScoreDetector.LOCATION camResult = teamScoreDetector.getAnalysis();

    telemetry.addData("Status", "Initialized");
    telemetry.addData("Location", camResult);
    telemetry.update();

    waitForStart();

    runtime.reset();

    // Move right and a bit up
    driverControlAPI.spinnerSpeed = 1;
    driverControlAPI.moveX = 1;
    driverControlAPI.moveY = -0.2;
    driverControlAPI.iterate();
    sleep(550);

    // stop moving
    driverControlAPI.moveX = 0;
    driverControlAPI.moveY = 0;
    driverControlAPI.spinnerJointSpeed = 0.8f;
    driverControlAPI.iterate();
    sleep(9500);

    // wait for ducks to fall off and go back to original position
    driverControlAPI.spinnerSpeed = 0.49f;
    driverControlAPI.spinnerJointSpeed = 0.4f;
    driverControlAPI.moveX = -1;
    driverControlAPI.moveY = 0;
    driverControlAPI.iterate();
    sleep(950);

    // stop moving to the left and bring it back in
    driverControlAPI.moveX = 0;
    driverControlAPI.spinnerJointSpeed = 0.5f;
    driverControlAPI.iterate();

    /**
     * move the arm to the level
     *
     * left = low
     * middle = middle
     * right = high
     */
    if (camResult == TeamScoreDetector.LOCATION.RIGHT) {
      driverControlAPI.setState(DriverControlAPI.STATE.HIGH);
    } else if (camResult == TeamScoreDetector.LOCATION.MIDDLE) {
      driverControlAPI.setState(DriverControlAPI.STATE.MIDDLE);
    } else {
      driverControlAPI.setState(DriverControlAPI.STATE.LOW);
    }
    for (int i = 0; i < 200; i++) {
      sleep(10);
      driverControlAPI.iterate(); // dampen power as needed
    }

    // move forward
    driverControlAPI.moveY = -0.4;
    driverControlAPI.iterate();
    sleep(1100);

    // stop moving
    driverControlAPI.moveY = 0;
    driverControlAPI.iterate();
    sleep(200);

    // let go of the block
    driverControlAPI.clawTargetState = 0;
    driverControlAPI.iterate();
    sleep(500);

    // move back
    driverControlAPI.moveY = 0.4;
    driverControlAPI.iterate();
    sleep(600);

    // move right again
    driverControlAPI.moveX = 1;
    driverControlAPI.moveY = 0;
    driverControlAPI.setState(DriverControlAPI.STATE.BACK);
    sleep(1300);

    // move forward
    driverControlAPI.moveX = 0;
    driverControlAPI.moveY = -1;
    driverControlAPI.iterate();
    sleep(350);

    driverControlAPI.moveY = 0;
    driverControlAPI.iterate();
    sleep(550);
  }
}
