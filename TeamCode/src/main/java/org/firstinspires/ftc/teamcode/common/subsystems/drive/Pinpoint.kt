package org.firstinspires.ftc.teamcode.common.subsystems.drive

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.common.Robot
import org.firstinspires.ftc.teamcode.common.utils.Pose2d
import org.firstinspires.ftc.teamcode.common.utils.Subsystem
import org.firstinspires.ftc.teamcode.gobilda.GoBildaPinpointDriver

class Pinpoint(val robot: Robot) : Subsystem() {
    val pinpoint: GoBildaPinpointDriver = (robot.hardware["odometry"] as GoBildaPinpointDriver).apply {
        setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)
        val xDirection = if (xReversed) GoBildaPinpointDriver.EncoderDirection.REVERSED else GoBildaPinpointDriver.EncoderDirection.FORWARD
        val yDirection = if (yReversed) GoBildaPinpointDriver.EncoderDirection.REVERSED else GoBildaPinpointDriver.EncoderDirection.FORWARD
        setEncoderDirections(xDirection, yDirection)
        setOffsets(Pinpoint.xOffset, Pinpoint.yOffset)
        resetPosAndIMU()
    }

    val pose: Pose2d
        get() {
            val pose = pinpoint.position
            val x = pose.getX(DistanceUnit.INCH)
            val y = pose.getY(DistanceUnit.INCH)
            val heading = pose.getHeading(AngleUnit.DEGREES)
            return Pose2d(x, y, heading)
        }

    val velocity: Pose2d
        get() {
            val velocity = pinpoint.velocity
            val x = velocity.getX(DistanceUnit.INCH)
            val y = velocity.getY(DistanceUnit.INCH)
            val heading = velocity.getHeading(AngleUnit.DEGREES)
            return Pose2d(x, y, heading)
        }

    companion object {
        // TODO: MAKE SURE TO TUNE THESE
        @JvmField
        var xReversed: Boolean = false
        @JvmField
        var yReversed: Boolean = false

        @JvmField
        var xOffset: Double = 0.0
        @JvmField
        var yOffset: Double = 0.0
    }
}