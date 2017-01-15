package loopperf

import org.scalameter.api._

object NBodyArrayPerf extends Bench.LocalTime {
  val sizes = Gen.range("size")(100, 1000, 100)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  NBodyArray.initArrays

  performance of "NBodyArray" in {
    measure method "for" in {
      using(ranges) in {
        r => r.foreach(i => NBodyArray.forSim(100))
      }
    }

    measure method "while" in {
      using(ranges) in {
        r => r.foreach(i => NBodyArray.whileSim(100))
      }
    }

  }
}

/**
 * Results
 *
 * Scala 2.11.7
 *
 * [info] ::Benchmark NBodyArray.for::
 * [info] cores: 8
 * [info] hostname: kalman-Aspire-VN7-791G
 * [info] name: Java HotSpot(TM) 64-Bit Server VM
 * [info] osArch: amd64
 * [info] osName: Linux
 * [info] vendor: Oracle Corporation
 * [info] version: 25.91-b14
 * [info] Parameters(size -> 100): 0.131104
 * [info] Parameters(size -> 200): 0.263584
 * [info] Parameters(size -> 300): 0.39575
 * [info] Parameters(size -> 400): 0.526925
 * [info] Parameters(size -> 500): 0.666313
 * [info] Parameters(size -> 600): 0.791579
 * [info] Parameters(size -> 700): 0.895099
 * [info] Parameters(size -> 800): 1.022956
 * [info] Parameters(size -> 900): 1.153401
 * [info] Parameters(size -> 1000): 1.277743
 *
 * [info] ::Benchmark NBodyArray.while::
 * [info] cores: 8
 * [info] hostname: kalman-Aspire-VN7-791G
 * [info] name: Java HotSpot(TM) 64-Bit Server VM
 * [info] osArch: amd64
 * [info] osName: Linux
 * [info] vendor: Oracle Corporation
 * [info] version: 25.91-b14
 * [info] Parameters(size -> 100): 8.36E-4
 * [info] Parameters(size -> 200): 0.001564
 * [info] Parameters(size -> 300): 0.002172
 * [info] Parameters(size -> 400): 0.002765
 * [info] Parameters(size -> 500): 0.002888
 * [info] Parameters(size -> 600): 0.003314
 * [info] Parameters(size -> 700): 0.003925
 * [info] Parameters(size -> 800): 0.004432
 * [info] Parameters(size -> 900): 0.004961
 * [info] Parameters(size -> 1000): 0.005477
 *
 * Scala 2.11.8
 * [info] ::Benchmark NBodyArray.for::
 * [info] cores: 8
 * [info] hostname: kalman-Aspire-VN7-791G
 * [info] name: Java HotSpot(TM) 64-Bit Server VM
 * [info] osArch: amd64
 * [info] osName: Linux
 * [info] vendor: Oracle Corporation
 * [info] version: 25.91-b14
 * [info] Parameters(size -> 100): 0.261338
 * [info] Parameters(size -> 200): 0.52403
 * [info] Parameters(size -> 300): 0.784779
 * [info] Parameters(size -> 400): 1.040066
 * [info] Parameters(size -> 500): 1.321773
 * [info] Parameters(size -> 600): 1.524742
 * [info] Parameters(size -> 700): 1.766986
 * [info] Parameters(size -> 800): 2.014274
 * [info] Parameters(size -> 900): 2.284899
 * [info] Parameters(size -> 1000): 2.553726
 *
 * [info] ::Benchmark NBodyArray.while::
 * [info] cores: 8
 * [info] hostname: kalman-Aspire-VN7-791G
 * [info] name: Java HotSpot(TM) 64-Bit Server VM
 * [info] osArch: amd64
 * [info] osName: Linux
 * [info] vendor: Oracle Corporation
 * [info] version: 25.91-b14
 * [info] Parameters(size -> 100): 8.59E-4
 * [info] Parameters(size -> 200): 0.001486
 * [info] Parameters(size -> 300): 0.002719
 * [info] Parameters(size -> 400): 0.003524
 * [info] Parameters(size -> 500): 0.003324
 * [info] Parameters(size -> 600): 0.003898
 * [info] Parameters(size -> 700): 0.004324
 * [info] Parameters(size -> 800): 0.004915
 * [info] Parameters(size -> 900): 0.005348
 * [info] Parameters(size -> 1000): 0.005938
 */
