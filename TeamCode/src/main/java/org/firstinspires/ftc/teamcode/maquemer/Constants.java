package org.firstinspires.ftc.teamcode.maquemer;

public class Constants {
    double start = 0.1;
    double max = 0.5;

    public double progressToPower (double x) {
        double x1 = 0;
        double y1 = start;
        double x2 = 0.5;
        double y2 = max;
        double x3 = 1;
        double y3 = 0;

        double a1 = -Math.pow(x1, 2) + Math.pow(x2, 2);
        double b1 = -x1 + x2;
        double d1 = -y1 + y2;

        double a2 = -Math.pow(x2, 2) + Math.pow(x3, 2);
        double b2 = -x2 + x3;
        double d2 = -y2 + y3;

        double bm = - (b2 / b1);
        double a3 = bm * a1 + a2;
        double d3 = bm * d1 + d2;

        double a = d3 / a3;
        double b = (d1 - a1 * a) / b1;
        double c = y1 - a * Math.pow(x1, 2) - b * x1;

        return a * Math.pow(x, 2) + b * x + c;
    }
}
