package com.britt.lif

import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._

object ProcessLif extends App with LazyLogging {

  val config = ConfigFactory.load()
  val inputFile = config.getString("inputFile")
  logger.info(s"Processing file $inputFile")
  val (reader, validSeries) = LifUtils.getReader(inputFile)
  logger.info(s"File contains ${validSeries.length} valid images")

  val outputDir = Paths.get(config.getString("outputDirectory")).toFile
  val idCode = config.getString("idCode")
  logger.info(s"Writing data to $outputDir with fileName $idCode")
  if (!outputDir.exists()) outputDir.mkdirs()

  val channels = config.getConfigList("channels").asScala.map(Channel.apply)
  logger.info(s"Extracting channels...")
  channels.foreach(c => logger.info(c.toString))

  for (series <- validSeries) {
    for (channel <- channels) {
      logger.info(s"Working on $series-${channel.number}")
      val rawStack = LifUtils.extractStack(reader, series, channel)
      //IJ.saveAsTiff(new ImagePlus("Raw", rawStack), "/tmp/raw4.tiff")
      LifUtils.doEDOF(rawStack, LifUtils.getFileName(outputDir, idCode, series, channel))
    }
  }
  reader.close()
}
