package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class TeamScoreDetermination extends OpenCvPipeline {
    Telemetry telemetry;
    Mat mat = new Mat();
    public void TeamScoreDetector(Telemetry tele) { telemetry = tele; };

    @Override
    public Mat processFrame(Mat input) {
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        Scalar LOW_ACCEPTANCE_COLOR = new Scalar(120, 100, 100);
        Scalar HIGH_ACCEPTANCE_COLOR = new Scalar(120, 100, 50);

        Core.inRange(mat, LOW_ACCEPTANCE_COLOR, HIGH_ACCEPTANCE_COLOR, mat);

        return input;
    };
}