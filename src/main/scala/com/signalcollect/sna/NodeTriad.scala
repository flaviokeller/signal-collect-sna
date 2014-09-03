package com.signalcollect.sna

class NodeTriad(val headId: Int, val centerId: Int, val tailId: Int) {
  override def toString(): String = {
    "HeadId: " + headId + "\tcenterId: " + centerId + "\ttailId: " + tailId + "\ttriad type: " + triadType
  }
  var triadType = TriadType.typeone
}
object TriadType extends Enumeration {
  type TriadType = Value
  val none = Value("No Triad Type!");
  val typeone = Value("003");
  val typetwo = Value("012");
  val typethree = Value("102");
  val typefour = Value("021D");
  val typefive = Value("021U");
  val typesix = Value("021C");
  val typeseven = Value("111D");
  val typeeight = Value("111U");
  val typenine = Value("030T");
  val typeten = Value("030C");
  val typeeleven = Value("201");
  val typetwelve = Value("120D");
  val typethirteen = Value("120U");
  val typefourteen = Value("120C");
  val typefifteen = Value("210");
  val typesixteen = Value("300");
}