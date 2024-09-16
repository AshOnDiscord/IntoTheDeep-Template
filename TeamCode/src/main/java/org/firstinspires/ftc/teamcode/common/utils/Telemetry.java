package org.firstinspires.ftc.teamcode.common.utils;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.geometry.Pose2d;
import com.arcrobotics.ftclib.geometry.Vector2d;
import com.millburnx.utils.Vec2d;


public class Telemetry {
    Pose2d pos;
    MultipleTelemetry telemetry;

    public Telemetry() {
    }

    public static void drawRobot(Canvas canvas, Pose2d pose) {
        canvas.strokeCircle(pose.getX(), -pose.getY(), 9);
        Vec2d lookVector = new Vec2d(9, 0).rotate(-pose.getHeading());
        Vec2d lookPoint = new Vec2d(pose.getX(), -pose.getY()).plus(lookVector);
        canvas.strokeLine(pose.getX(), -pose.getY(), lookPoint.getX(), lookPoint.getY());
    }

    //draw all the robots on the field and send to the dashboard
    public void drawField(Pose2d pose, FtcDashboard dash) {
        TelemetryPacket packet = new TelemetryPacket();
        Canvas fieldOverlay = packet.fieldOverlay();

        fieldOverlay.setStrokeWidth(1);
        fieldOverlay.setStroke("#3F51B5");
        drawRobot(fieldOverlay, pose);

        packet.put("x", pose.getX());
        packet.put("y", pose.getY());
        packet.put("heading (deg)", Math.toDegrees(pose.getHeading()));

        dash.sendTelemetryPacket(packet);
    }
}
