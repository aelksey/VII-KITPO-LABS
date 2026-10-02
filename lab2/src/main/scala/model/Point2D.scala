package model

class Point2D(val x: Double = 0.0, val y: Double = 0.0) {
  def getX(): Double = x
  def getY(): Double = y
  def getDistance(): Double = Math.sqrt((x * x) + (y * y))
}
