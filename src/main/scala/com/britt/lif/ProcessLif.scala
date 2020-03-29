package com.britt.lif

object ProcessLif extends App {

  println("hello world")
  //val x = getClass.getClassLoader.getResource("logback.xml")
  //val logs = scala.io.Source.fromInputStream(x.openStream()).getLines().toArray
  //println(s"I read ${logs.length} logback config lines")

  val inputFile = "/Users/Britt/lifFiles/bigOne.lif"
  val reader = LifUtils.getReader(inputFile)

  for (series <- 0 until reader.getSeriesCount) {
    for (channel <- Seq(0, 1, 3)) {
      println(s"Working on $series-$channel")
      val rawStack = LifUtils.extractStack(reader, series, channel, Some(LifUtils.rangeForChannel(channel)))
      //IJ.saveAsTiff(new ImagePlus("Raw", rawStack), "/tmp/raw4.tiff")
      LifUtils.doEDOF(rawStack, LifUtils.getFileName(series, channel))
    }
  }
  reader.close()

}
