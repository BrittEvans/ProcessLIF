package com.britt.lif

import com.typesafe.config.Config

import scala.util.Try

case class Channel(number: Int, scale: Option[(Int, Int)] = None)

object Channel {
  def apply(c: Config): Channel = {
    val scale = Try(c.getIntList("scale")).toOption
    Channel(c.getInt("number"), scale.map(l => (l.get(0), l.get(1))))
  }
}
