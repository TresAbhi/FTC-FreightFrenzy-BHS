package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.core.InitialTeamScoreChecker;
import org.firstinspires.ftc.teamcode.core.TeamScoreAlignmentChecker;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@TeleOp(name = "TeamScoreAlignmentCamCheck", group = "Linear Opmode")
// @Disabled
public class TeamScoreAlignmentCamCheck extends LinearOpMode {

  OpenCvWebcam webcam;
  final TeamScoreAlignmentChecker initialTeamScoreChecker = new TeamScoreAlignmentChecker();

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
    webcam.setPipeline(initialTeamScoreChecker);
    webcam.startStreaming(432, 240, OpenCvCameraRotation.UPRIGHT);

    waitForStart();

    while (opModeIsActive()) {
      telemetry.clearAll();
      telemetry.addData("Location", initialTeamScoreChecker.getAnalysis());
      telemetry.update();
    }
  }
}
