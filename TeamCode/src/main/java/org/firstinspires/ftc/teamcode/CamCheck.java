package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.core.CameraPipeline;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@TeleOp(name = "CamCheck", group = "Linear Opmode")
// @Disabled
public class CamCheck extends LinearOpMode {

  OpenCvWebcam webcam;
  CameraPipeline cameraPipeline = new CameraPipeline();

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
    webcam.setPipeline(cameraPipeline);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    while (opModeIsActive()) {
      CameraPipeline.LOCATION camResult = cameraPipeline.getAnalysis();

      telemetry.clearAll();
      telemetry.addData("Location", camResult.toString());
      telemetry.update();
    }
  }
}
