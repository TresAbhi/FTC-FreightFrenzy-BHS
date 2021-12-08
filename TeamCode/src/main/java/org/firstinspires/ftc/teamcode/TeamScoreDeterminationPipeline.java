/*
 * Copyright (c) 2020 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TeamScoreDeterminationPipeline extends OpenCvPipeline {

  /*
   * An enum to define the ring position
   */
  public enum TeamScorePosition {
    FOUR,
    ONE,
    NONE,
  }

  /*
   * Some color constants
   */
  static final Scalar BLUE = new Scalar(0, 0, 255);
  static final Scalar GREEN = new Scalar(0, 255, 0);

  /*
   * The core values which define the location and size of the sample regions
   */
  static final Point MIDDLE_REGION_TOP_LEFT_ANCHOR_POINT = new Point(110, 50);
  static final Point LEFT_REGION_TOP_LEFT_ANCHOR_POINT = new Point(10, 50);
  static final Point RIGHT_REGION_TOP_LEFT_ANCHOR_POINT = new Point(220, 50);

  static final int REGION_WIDTH = 60;
  static final int REGION_HEIGHT = 80;

  final int FOUR_RING_THRESHOLD = 141;
  final int ONE_RING_THRESHOLD = 132;

  Point middle_region_point_a = new Point(
    MIDDLE_REGION_TOP_LEFT_ANCHOR_POINT.x,
    MIDDLE_REGION_TOP_LEFT_ANCHOR_POINT.y
  );
  Point middle_region_point_b = new Point(
    MIDDLE_REGION_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
    MIDDLE_REGION_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT
  );

  Point left_region_point_a = new Point(
    LEFT_REGION_TOP_LEFT_ANCHOR_POINT.x,
    LEFT_REGION_TOP_LEFT_ANCHOR_POINT.y
  );
  Point left_region_point_b = new Point(
    LEFT_REGION_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
    LEFT_REGION_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT
  );

  Point right_region_point_a = new Point(
    RIGHT_REGION_TOP_LEFT_ANCHOR_POINT.x,
    RIGHT_REGION_TOP_LEFT_ANCHOR_POINT.y
  );
  Point right_region_point_b = new Point(
    RIGHT_REGION_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
    RIGHT_REGION_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT
  );

  /*
   * Working variables
   */

  Mat region_middle_Cb;
  Mat region_left_Cb;
  Mat region_right_Cb;
  Mat YCrCb = new Mat();
  Mat Cb = new Mat();

  String targetRegion = "none";

  int region_middle_avg_color;
  int region_left_avg_color;
  int region_right_avg_color;

  // Volatile since accessed by OpMode thread w/o synchronization
  private volatile TeamScorePosition position = TeamScorePosition.FOUR;

  /*
   * This function takes the RGB frame, converts to YCrCb,
   * and extracts the Cb channel to the 'Cb' variable
   */
  void inputToCb(Mat input) {
    Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
    Core.extractChannel(YCrCb, Cb, 1);
  }

  @Override
  public void init(Mat firstFrame) {
    inputToCb(firstFrame);

    region_middle_Cb =
      Cb.submat(new Rect(middle_region_point_a, middle_region_point_b));

    region_left_Cb =
      Cb.submat(new Rect(left_region_point_a, left_region_point_b));

    region_right_Cb =
      Cb.submat(new Rect(right_region_point_a, right_region_point_b));
  }

  @Override
  public Mat processFrame(Mat input) {
    inputToCb(input);

    region_middle_avg_color = (int) Core.mean(region_middle_Cb).val[0];
    region_left_avg_color = (int) Core.mean(region_left_Cb).val[0];
    region_right_avg_color = (int) Core.mean(region_right_Cb).val[0];

    Imgproc.rectangle(
      input, // Buffer to draw on
      middle_region_point_a, // First point which defines the rectangle
      middle_region_point_b, // Second point which defines the rectangle
      BLUE, // The color the rectangle is drawn in
      2
    ); // Thickness of the rectangle lines

    Imgproc.rectangle(input, left_region_point_a, left_region_point_b, BLUE, 2);

    Imgproc.rectangle(
      input,
      right_region_point_a,
      right_region_point_b,
      BLUE,
      2
    );

    int max_color = Math.max(
      Math.max(region_middle_avg_color, region_left_avg_color),
      region_right_avg_color
    );

    if (max_color == region_middle_avg_color) {
      targetRegion = "middle";
    } else if (max_color == region_left_avg_color) {
      targetRegion = "left";
    } else if (max_color == region_right_avg_color) {
      targetRegion = "right";
    }

    Imgproc.rectangle(
      input,
      middle_region_point_a,
      middle_region_point_b,
      GREEN,
      -1
    ); // Negative thickness means solid fill

    Imgproc.rectangle(
      input,
      left_region_point_a,
      left_region_point_b,
      GREEN,
      -1
    ); // Negative thickness means solid fill

    Imgproc.rectangle(
      input,
      right_region_point_a,
      right_region_point_b,
      GREEN,
      -1
    ); // Negative thickness means solid fill

    return input;
  }

  public int[] getAnalysis() {
    return new int[]{region_middle_avg_color, region_left_avg_color, region_right_avg_color};
  }
}
