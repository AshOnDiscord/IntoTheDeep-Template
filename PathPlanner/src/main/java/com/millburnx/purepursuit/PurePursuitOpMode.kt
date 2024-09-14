package com.millburnx.purepursuit

import com.acmerobotics.dashboard.canvas.Canvas
import com.millburnx.dashboard.TelemetryPacket
import com.millburnx.utils.Bezier
import com.millburnx.utils.BezierIntersection
import com.millburnx.utils.Utils
import com.millburnx.utils.Vec2d
import java.awt.Color
import kotlin.math.abs

class PurePursuitOpMode(ppi: Double, updateHertz: Double = -1.0) : OpMode(ppi, updateHertz) {
    val background = Utils.Colors.bg1
    val colors = listOf(Utils.Colors.red, Utils.Colors.blue, Utils.Colors.green, Utils.Colors.yellow)

    private var path: List<Vec2d> = listOf(
        Vec2d(0.0, 0.0),
        Vec2d(48.0, 0.0),
        Vec2d(48.0, -48.0),
        Vec2d(0.0, -48.0),
    )

    private var beziers: List<Bezier> = Utils.pathToBeziers(path)

    private var lastIntersection: BezierIntersection = BezierIntersection(path[0], beziers[0], 0.0) // start of the path
    private var lastSegment: Int = 0 // prevent backtracking

    private val prevPositions: MutableList<Vec2d> = mutableListOf()

    override fun init() {
        println("Initializing Pure Pursuit")
        ftcDashboard.reset = {
            stop()
            robot.position = Vec2d(0.0, 0.0)
            robot.heading = 0.0
            prevPositions.clear()
            lastSegment = 0
            lastIntersection = BezierIntersection(path[0], beziers[0], 0.0)
            lastFrame = 0L
            val packet = TelemetryPacket()
            val canvas = packet.fieldOverlay()
            renderPath(canvas)
            drawRobot(canvas)
            ftcDashboard.sendTelemetryPacket(packet)
        }
        ftcDashboard.load = {
            stop()
            loadPath()
        }
    }

    private fun updatePath() {
        beziers = Utils.pathToBeziers(path)
        lastSegment = 0
        lastIntersection = BezierIntersection(path[0], beziers[0], 0.0)
        val packet = TelemetryPacket()
        val canvas = packet.fieldOverlay()
        renderPath(canvas)
        drawRobot(canvas)
        ftcDashboard.sendTelemetryPacket(packet)
    }

    private fun loadPath() {
        val file = Utils.fileDialog("paths", "*.tsv") ?: return
        val pathList = Vec2d.loadList(file)
        path = pathList
        updatePath()
    }

    private fun renderPath(canvas: Canvas) {
        canvas.setFill(background).fillRect(-144.0 / 2, -144.0 / 2, 144.0, 144.0).setStroke(Utils.Colors.bg2)
            .drawGrid(0.0, 0.0, 144.0, 144.0, 7, 7).setStrokeWidth(2)

        var color = 0
        for (bezier in beziers) {
            canvas.setStroke(colors[color % colors.size])
            bezier.draw(canvas)
            color++
        }

        color = 0
        val anchorSize = 1.5
        val handleSize = 1.5
        for (i in 0..path.size - 2 step 3) {
            val p0 = path[i]
            val p1 = path[i + 1]
            val p2 = path[i + 2]
            val p3 = path[i + 3]

            canvas.setStroke(colors[color % colors.size]).strokeLine(p0.x, p0.y, p1.x, p1.y)
                .strokeLine(p2.x, p2.y, p3.x, p3.y).setFill(background).fillCircle(p0.x, p0.y, anchorSize)
                .fillCircle(p1.x, p1.y, handleSize).fillCircle(p2.x, p2.y, handleSize)
                .fillCircle(p3.x, p3.y, anchorSize).setStroke("#FFFFFF").strokeCircle(p0.x, p0.y, anchorSize)
                .strokeCircle(p3.x, p3.y, anchorSize).setStroke(colors[color % colors.size])
                .strokeCircle(p1.x, p1.y, handleSize).strokeCircle(p2.x, p2.y, handleSize)

            color++
        }
    }

    override fun loop(): Boolean {
        val packet = TelemetryPacket()
        val canvas = packet.fieldOverlay()

        renderPath(canvas)

        val distanceToFinal = robot.position.distanceTo(path.last())
//        println(distanceToFinal)
        if (distanceToFinal < robot.lookahead) {
            if (distanceToFinal < 1.0) {
                println("Reached the end of the path ${path.last()} with distance $distanceToFinal (ending at ${robot.position})")
                return false
            }
            lastIntersection = BezierIntersection(path.last(), beziers.last(), 1.0)
            canvas.setFill(Color.CYAN.rgb.toString())
                .fillCircle(lastIntersection.point.x, lastIntersection.point.y, 1.0)
            driveTo(path.last())
            drawRobot(canvas)
            ftcDashboard.sendTelemetryPacket(packet)
            return true
        }

        val remainingSegments = beziers.subList(lastSegment, beziers.size).toMutableList()
        remainingSegments[0] =
            remainingSegments[0].split((lastIntersection.t - 0.01).coerceAtLeast(0.0)).second // prune the first segment
        val intersections = remainingSegments.flatMapIndexed { index, bezier ->
            if (index == 0) {
                bezier.intersections(
                    robot.lookaheadCircle,
                    canvas,
                    start = lastIntersection.t - 0.01
                )
            } else {
                bezier.intersections(
                    robot.lookaheadCircle,
                    canvas
                )
            }
        }
        // closest by angle from current heading
        val closestIntersection = intersections.minByOrNull { abs(PurePursuit.getAngleDiff(robot.toPair(), it.point)) }
        for (intersection in intersections) {
            val segmentIndex = beziers.indexOf(intersection.line)
            val fixedIndex = if (segmentIndex == -1) 0 else segmentIndex
            val color = colors[fixedIndex % colors.size]
            canvas.setFill(if (intersection == closestIntersection) "#FFFFFF" else color)
                .fillCircle(intersection.point.x, intersection.point.y, 1.0)
        }
        val targetIntersection = closestIntersection ?: lastIntersection
        val segmentIndex = beziers.indexOf(targetIntersection.line)
        val fixedIndex = if (segmentIndex == -1) lastSegment else segmentIndex
        lastSegment = fixedIndex
        lastIntersection = targetIntersection
        driveTo(targetIntersection.point)

        drawRobot(canvas)

        ftcDashboard.sendTelemetryPacket(packet)
        return true
    }

    private fun drawRobot(canvas: Canvas) {
        val lookaheadVector = Vec2d(robot.lookahead, 0.0).rotate(robot.heading)
        val lookaheadPoint = robot.position + lookaheadVector

        prevPositions.add(robot.position)
        val copyPositions = prevPositions.toList()

        canvas.setFill("#FFFFFF")
            .strokePolyline(copyPositions.map { it.x }.toDoubleArray(), copyPositions.map { it.y }.toDoubleArray()
            ).setFill(Utils.Colors.purple).fillCircle(robot.position.x, robot.position.y, 1.0)
            .strokeCircle(robot.position.x, robot.position.y, robot.lookahead)
            .strokeLine(robot.position.x, robot.position.y, lookaheadPoint.x, lookaheadPoint.y)
    }

    private fun driveTo(point: Vec2d) {
        val angleDiff = PurePursuit.getAngleDiff(robot.toPair(), point)
        val forwardPower = robot.position.distanceTo(point) / robot.lookahead
        drive(
            forwardPower, 0.0, angleDiff
        ) // robot never strafes in pp since pp is a differential drive algorithm
    }
}