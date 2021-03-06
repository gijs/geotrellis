package geotrellis.operation

import geotrellis.process._
import geotrellis.operation._
import geotrellis.raster._
import geotrellis._

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import org.scalatest.matchers.ShouldMatchers

@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class UnaryLocalSpec extends Spec with MustMatchers with ShouldMatchers {
  def f(op:Op[IntRaster]) = AddConstant(op, 1)

  describe("The UnaryLocal operation (AddConstant)") {
    val cols = 1000
    val rows = 1000

    val server = TestServer()
    val e = Extent(0.0, 0.0, 100.0, 100.0)
    val re = RasterExtent(e, e.width / cols, e.height / rows, cols, rows)
    val data = Array.fill(re.cols * re.rows)(100)
    val raster = IntRaster(data, re)

    it("should produce correct results") {
      val op = AddConstant(raster, 33)
      val raster2 = server.run(op)
      raster2.data(0) must be === raster.data(0) + 33
    }

    it("should compose 2 local operations") {
      server.run(f(f(raster))).data(0) must be === raster.data(0) + 2
    }

    it("should compose 3 local operations") {
      server.run(f(f(f(raster)))).data(0) must be === raster.data(0) + 3
    }

    it("should compose 4 local operations") {
      server.run(f(f(f(f(raster))))).data(0) must be === raster.data(0) + 4
    }

    it("should compose multiple operations") {
      val Complete(raster2, history) = server.getResult(f(f(f(f(f(raster))))))
      println(history.toPretty)
      raster2.data(0) must be === raster.data(0) + 5
    }
  }
}
