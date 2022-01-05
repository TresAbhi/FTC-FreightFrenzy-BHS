package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class CameraAPI extends OpenCvPipeline {

  // ROI: Region of intrest
  static final int ROI_WIDTH = 80;
  static final int ROI_HEIGHT = 80;

  // The minimum and maximum colors that we will accept
  Scalar LOW_ACCEPTANCE_COLOR = new Scalar(23, 50, 70); // in HSV
  Scalar HIGH_ACCEPTANCE_COLOR = new Scalar(32, 255, 255); // in HSV

  // colors for either the team score is there or not
  Scalar TEAM_SCORE_VISUAL_COLOR = new Scalar(0, 255, 0); // in RGB
  Scalar NO_TEAM_SCORE_VISUAL_COLOR = new Scalar(255, 0, 0); // in RGB

  // enumerations: the only possible results
  public enum LOCATION {
    LEFT,
    MIDDLE,
    RIGHT,
    NONE,
  }

  // the value that stores either LEFT, MIDDLE, RIGHT, or NONE
  public LOCATION location = LOCATION.NONE;

  static final Rect LEFT_ROI = new Rect( // Left region of intrest
    new Point(0, 50),
    new Point(0 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );
  static final Rect MIDDLE_ROI = new Rect( // middle region of intrest
    new Point(100, 50),
    new Point(100 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );
  static final Rect RIGHT_ROI = new Rect( // right region of intrest
    new Point(210, 50),
    new Point(210 + ROI_WIDTH, 50 + ROI_HEIGHT)
  );

  /**
   * this percentage or above of the box will have to be covered with yellow
   * in order to be considered `true`
   */
  static double PERCENT_COLOR_COVERAGE_THRESHOLD = 0.1;

  // telemetry object; will be provided by the constructor
  Telemetry telemetry;

  // the whole screen's region/matrix
  Mat mat = new Mat();

  // gives access to telemetry
  public CameraAPI() {}

  @Override
  public Mat processFrame(Mat input) {
    // converts to HSV colors (google it!)
    Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

    // checks for colors in range
    Core.inRange(mat, LOW_ACCEPTANCE_COLOR, HIGH_ACCEPTANCE_COLOR, mat);

    // "cut-outs" of the regions of intrests
    Mat left = mat.submat(LEFT_ROI);
    Mat middle = mat.submat(MIDDLE_ROI);
    Mat right = mat.submat(RIGHT_ROI);

    // calculate coverage for cutouts (from 0 to 1)
    double leftCoverage = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
    double middleCoverage =
      Core.sumElems(middle).val[0] / MIDDLE_ROI.area() / 255;
    double rightCoverage = Core.sumElems(right).val[0] / RIGHT_ROI.area() / 255;

    // release it back to the parent map
    left.release();
    middle.release();
    right.release();

    // booleans to figure out which one is actually covered
    boolean leftCovered = leftCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean middleCovered = middleCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;
    boolean rightCovered = rightCoverage > PERCENT_COLOR_COVERAGE_THRESHOLD;

    /**
     * do stuff with what is actually covered; if two are properly covered, the
     * left-most is preferred
     */
    if (leftCovered) {
      // set the variable all the way up there
      location = LOCATION.LEFT;
      // talk about it in the tele OP
      // telemetry.addData("Skystone Location", "left");
    } else if (middleCovered) {
      location = LOCATION.MIDDLE;
      // telemetry.addData("Skystone Location", "middle");
    } else if (rightCovered) {
      location = LOCATION.RIGHT;
      // telemetry.addData("Skystone Location", "right");
    } else {
      location = LOCATION.NONE;
      // telemetry.addData("Skystone Location", "none");
    }

    // convert back to RGB
    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);

    // draw three rectangles based on which one has the team score
    Imgproc.rectangle(
      mat,
      LEFT_ROI,
      location == LOCATION.LEFT
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );
    Imgproc.rectangle(
      mat,
      MIDDLE_ROI,
      location == LOCATION.MIDDLE
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );
    Imgproc.rectangle(
      mat,
      RIGHT_ROI,
      location == LOCATION.RIGHT
        ? TEAM_SCORE_VISUAL_COLOR
        : NO_TEAM_SCORE_VISUAL_COLOR
    );

    // return mat to let the bot render it on screen
    return mat;
  }

  public LOCATION getAnalysis() {
    return location;
  }
}
