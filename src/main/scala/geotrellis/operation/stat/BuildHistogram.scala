package geotrellis.operation


import geotrellis._
import geotrellis.process._
import geotrellis.stat._

/**
 * Generic trait used by the various histogram-building operations.
 */
trait BuildHistogram extends Operation[Histogram] {
  val r:Op[IntRaster]

  protected[this] def createHistogram:Histogram

  def _run(context:Context) = runAsync(List(r))

  val nextSteps:Steps = {
    case (raster:IntRaster) :: Nil => {
      val h = createHistogram

      val data   = raster.data

      var i = 0
      val limit = raster.length
      while (i < limit) {
        val z = data(i)
        // TODO: some histogram types can handle negative values. really we
        // should use a protected[this] countValue(h,z) that can be implemented
        // as a final, inlined method in extending subclasses.
        if (z == NODATA) {
        } else if (z < 0) {
          println("bad value: " + z)
        } else {
          h.countItem(z, 1)
        }
        i += 1
      }
      Result(h)
    }
  }
}
