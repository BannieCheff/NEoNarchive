package com.gu.mediaservice.lib.collections

import java.awt.Color

import com.gu.mediaservice.lib.net.URI.{encode, decode}
import com.gu.mediaservice.model.Collection

import scala.util.Try

object CollectionsManager {
  val delimiter = "/"

  def stringToPath(s: String) = s.split(delimiter).toList
  def pathToString(path: List[String]) = path.mkString(delimiter).toLowerCase
  def pathToUri(path: List[String]) = pathToString(path.map(encode))
  def uriToPath(uri: String) = stringToPath(decode(uri))

  def sortBy(c: Collection) = c.pathId

  def add(collection: Collection, collections: List[Collection]): List[Collection] =
    (collection :: collections.filter(col => col.path != collection.path)).sortBy(sortBy)

  def remove(path: List[String], collections: List[Collection]): List[Collection] =
    collections.filter(col => col.path != path)

  def find(path: List[String], collections: List[Collection]): Option[Collection] =
    collections.find(col => col.path == path)

  def findIndex(path: List[String], collections: List[Collection]): Option[Int] =
    collections.indexWhere(_.path == path) match {
      case -1    => None
      case index => Some(index)
    }

  def onlyLatest(collections: List[Collection]): List[Collection] =
    collections filter { collection =>
      // if there isn't a collection with the same path created after itself.
      !collections.exists { col => {
        col.path == collection.path && col.actionData.date.isAfter(collection.actionData.date)
      }}
    }

  // We could use `ValidationNel`s here, but that's overkill
  def isValidPathBit(s: String) = if (s.contains(delimiter)) false else true

  // TODO: Find something that works off a palette, this is terrible.
  def stringToCssRgb(s: String): String = {
    val hash = s.hashCode
    val r = (hash & 0xFF0000) >> 16
    val g = (hash & 0x00FF00) >> 8
    val b = hash & 0x0000FF
    s"rgb($r, $g, $b)"
  }

  def getCssColour(path: List[String]): String = path.headOption.map(stringToCssRgb).getOrElse("#cccccc")
}
