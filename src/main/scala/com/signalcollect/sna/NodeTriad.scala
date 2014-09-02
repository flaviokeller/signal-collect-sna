package com.signalcollect.sna

class NodeTriad(val headId: Int, val centerId: Int, val tailId: Int) {
  override def toString(): String = {
    "HeadId: " + headId + "\tcenterId: " + centerId + "\ttailId: " + tailId + "\ttriad type: " + triadType
  }
  var triadType = TriadType.intransitive
}
object TriadType extends Enumeration {
  type TriadType = Value
  val transitive = Value("Transitive");
  val intransitive = Value("Intransitive");
  val noTriad = Value("No Triad");
}