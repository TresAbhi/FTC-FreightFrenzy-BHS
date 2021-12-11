package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TeamScoreDetermination extends OpenCvPipeline {

  static final int ROI_WIDTH = 60;
  static final int ROI_HEIGHT = 80;

  Scalar LOW_ACCEPTANCE_COLOR = new Scalar(120, 100, 100); // in HSV
  Scalar HIGH_ACCEPTANCE_COLOR = new Scalar(120, 100, 50); // in HSV

  Scalar TEAM_SCORE_VISUAL_COLOR = new Scalar(0, 255, 0); // in RGB
  Scalar NO_TEAM_SCORE_VISUAL_COLOR = new Scalar(255, 0, 0); // in RGB

  public enum LOCATION {
      LEFT,
      MIDDLE,
      RIGHT,
      NONE
  }
  private LOCATION location;

  static final Rect LEFT_ROI = new Rect(
    new Point(10, 50),
    new Point(10 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );
  static final Rect MIDDLE_ROI = new Rect(
    new Point(110, 50),
    new Point(110 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );
  static final Rect RIGHT_ROI = new Rect(
    new Point(220, 50),
    new Point(220 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );

  static double PERCENT_COLOR_COVERAGE_THRESHOLD = 0.4;

  Telemetry telemetry;
  Mat mat = new Mat();

  public void TeamScoreDetector(Telemetry tele) {
    telemetry = tele;
  }

  @Override
  public Mat processFrame(Mat input) {
    Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

    Core.inRange(mat, LOW_ACCEPTANCE_COLOR, HIGH_ACCEPTANCE_COLOR, mat);

    Mat left = mat.submat(LEFT_ROI);
    Mat middle = mat.submat(MIDDLE_ROI);
    Mat right = mat.submat(RIGHT_ROI);

    double leftCoverage = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
    double middleCoverage =
      Core.sumElems(middle).val[0] / MIDDLE_ROI.area() / 255;
    double rightCoverage = Core.sumElems(right).val[0] / RIGHT_ROI.area() / 255;

    left.release();
    middle.release();
    right.release();

    telemetry.addData("Left raw value", (int) Core.sumElems(left).val[0]);
    telemetry.addData("Middle raw value", (int) Core.sumElems(middle).val[0]);
    telemetry.addData("Right raw value", (int) Core.sumElems(right).val[0]);

    telemetry.addData("Left percentage", Math.round(leftCoverage * 100) + "%");
    telemetry.addData(
      "Middle percentage",
      Math.round(middleCoverage * 100) + "%"
    );
    telemetry.addData(
      "Right percentage",
      Math.round(rightCoverage * 100) + "%"
    );

    boolean leftCovered = leftCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean middleCovered = middleCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean rightCovered = rightCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;

    if (leftCovered) {
      location = LOCATION.LEFT;
      telemetry.addData("Skystone Location", "left");
    } else if (middleCovered) {
      location = LOCATION.MIDDLE;
      telemetry.addData("Skystone Location", "middle");
    } else if (rightCovered) {
      location = LOCATION.RIGHT;
      telemetry.addData("Skystone Location", "right");
    } else {
      location = LOCATION.NONE;
      telemetry.addData("Skystone Location", "none");
    }
    telemetry.update();

    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

    return input;
  }
}
