package com.millburnx.utils

class Circle(val center: Vec2d, val radius: Double) {
    /**
     * Checks if a point is inside the circle
     */
    fun contains(p: Vec2d): Boolean {
        return center.distanceTo(p) <= radius
    }

    fun bounding(): Pair<Vec2d, Vec2d> {
        return Pair(center - Vec2d(radius, radius), center + Vec2d(radius, radius))
    }

    /**
     * Checks if a point is inside the bounding box of the circle
     */
    fun boundingContains(p: Vec2d): Boolean {
        val (min, max) = bounding()

        return p.x in min.x..max.x && p.y in min.y..max.y
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Circle) return false
        return center == other.center && radius == other.radius
    }

    override fun toString(): String {
        return "($center, r=$radius)"
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }
}