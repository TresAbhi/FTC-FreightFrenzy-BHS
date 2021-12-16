package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

@TeleOp(name = "CamCheck", group = "Linear Opmode")
// @Disabled
public class CamCheck extends LinearOpMode {
  OpenCvWebcam webcam;
  TeamScoreDetector teamScoreDetector = new TeamScoreDetector(telemetry);

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

    while(opModeIsActive()) {
      TeamScoreDetector.LOCATION camResult = teamScoreDetector.getAnalysis();

      telemetry.clearAll();
      telemetry.addData("Status", "Initialized");
      telemetry.addData("Location", camResult);
      telemetry.update();
    }
  }
}