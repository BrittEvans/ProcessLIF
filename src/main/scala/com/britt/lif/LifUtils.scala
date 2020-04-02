package com.britt.lif

import java.io.File
import java.nio.{ByteBuffer, ByteOrder}

import com.typesafe.scalalogging.LazyLogging
import edf.EdfComplexWavelets
import edfgui.Parameters
import ij.process.ImageConverter
import ij.{IJ, ImagePlus, ImageStack}
import loci.common.services.ServiceFactory
import loci.formats.in.LIFReader
import loci.formats.services.OMEXMLService

object LifUtils extends LazyLogging {

  // Return a tuple of the reader and valid series within it
  def getReader(filename: String): (LIFReader, Seq[Int]) = {
    val factory = new ServiceFactory
    val service = factory.getInstance(classOf[OMEXMLService])
    val omexml = service.createOMEXMLMetadata
    val reader = new LIFReader()
    reader.setMetadataStore(omexml)
    reader.setId(filename)
    (reader,
     Range(0, reader.getSeriesCount)
       .filter(omexml.getImageName(_).endsWith("_ICC")))
  }

  // Optionally transform
  def extractStack(reader: LIFReader,
                   series: Int,
                   channel: Channel): ImageStack = {
    reader.setSeries(series)
    val nX = reader.getSizeX
    val nY = reader.getSizeY
    logger.debug(s"Image is $nX x $nY")
    val buf = ByteBuffer.allocate(nX * nY * 2) // Assuming 16-bit pixels
    if (reader.isLittleEndian)
      buf.order(ByteOrder.LITTLE_ENDIAN)
    val stack = new ImageStack(nX, nY)

    for (i <- 0 until reader.getImageCount
         if i / reader.getSizeZ == channel.number) {
      logger.debug(s"Reading image $i")
      buf.clear()
      reader.openBytes(i, buf.array())
      buf.rewind()
      channel.scale match {
        case Some((min, max)) =>
          val pixels = Array.fill(nX * nY)(transform(buf.getShort(), min, max))
          stack.addSlice(i.toString, pixels)
        case None =>
          val myShortArray = new Array[Short](nX * nY)
          buf.asShortBuffer().get(myShortArray)
          stack.addSlice(i.toString, myShortArray)
      }
    }
    stack
  }

  def doEDOF(input: ImageStack, outputFileName: String): Boolean = {
    logger.info("Running EDOF")
    val iw = imageware.Builder.create(input)
    val params = new Parameters()
    params.setQualitySettings(Parameters.QUALITY_HIGH)
    val edf = new EdfComplexWavelets(params.daubechielength,
                                     params.nScales,
                                     params.subBandCC,
                                     params.majCC)
    val out = edf.process(iw)(0)
    //out.show("EDOF")
    logger.debug("Done EDOF, printing output info")
    logger.whenDebugEnabled(out.printInfo())
    val ip = new ImagePlus("Output", out.buildImageStack())
    val converter = new ImageConverter(ip)
    converter.convertToGray8()
    IJ.saveAsTiff(ip, outputFileName)
  }

  def transform(original: Short, min: Int, max: Int): Byte = {
    val unsigned = original & 0xFFFF
    if (unsigned <= min)
      0.toByte
    else if (unsigned >= max)
      255.toByte
    else
      (((unsigned - min) / max.toFloat) * 255).floor.toByte
  }

  /*
   * Naming construct needed the ID code which will be a combination of 4 numbers
 and examples LC, MLAD2 (exclude the 2), RI, DRC, PROX RCA (make PROXRCA), etc.
 then _s## (the 0-76) then _zEDOF then _ch##
   * e.g. 3470LC_s05_zEDOF_ch03
   */
  def getFileName(outputDirectory: File,
                  idCode: String,
                  series: Int,
                  seriesMin: Int,
                  channel: Channel): String =
    new File(
      outputDirectory,
      f"${idCode}_s${series-seriesMin+1}%02d_zEDOF_ch${channel.number}%02d.tiff").getPath
}
