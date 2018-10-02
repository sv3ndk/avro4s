package com.sksamuel.avro4s.record.decoder

import com.sksamuel.avro4s.AvroSchema
import com.sksamuel.avro4s.Decoder
import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import org.scalatest.{FunSuite, Matchers}

class SealedTraitDecoderTest extends FunSuite with Matchers {

  test("support sealed traits of case classes") {

    val record = new GenericData.Record(AvroSchema[Wrapper])
    val wobble = new GenericData.Record(AvroSchema[Wobble])
    wobble.put("str", new Utf8("foo"))
    record.put("wibble", wobble)

    val wrapper = Decoder[Wrapper].decode(record)
    wrapper shouldBe Wrapper(Wobble("foo"))
  }

  test("support trait subtypes fields with same name") {

    val record = new GenericData.Record(AvroSchema[Trapper])
    val tobble = new GenericData.Record(AvroSchema[Tobble])
    tobble.put("str", new Utf8("foo"))
    tobble.put("place", new Utf8("bar"))
    record.put("tibble", tobble)

    val trapper = Decoder[Trapper].decode(record)
    trapper shouldBe Trapper(Tobble("foo", "bar"))
  }

  test("support trait subtypes fields with same name and same type") {

    val record = new GenericData.Record(AvroSchema[Napper])
    val nabble = new GenericData.Record(AvroSchema[Nabble])
    nabble.put("str", new Utf8("foo"))
    nabble.put("age", java.lang.Integer.valueOf(44))
    record.put("nibble", nabble)

    val napper = Decoder[Napper].decode(record)
    napper shouldBe Napper(Nabble("foo", 44))
  }

  test("support top level ADTs") {

    val nabble = new GenericData.Record(AvroSchema[Nabble])
    nabble.put("str", new Utf8("foo"))
    nabble.put("age", java.lang.Integer.valueOf(44))

    Decoder[Nibble].decode(nabble) shouldBe Nabble("foo", 44)
  }
}

sealed trait Wibble
case class Wobble(str: String) extends Wibble
case class Wabble(dbl: Double) extends Wibble
case class Wrapper(wibble: Wibble)

sealed trait Tibble
case class Tobble(str: String, place: String) extends Tibble
case class Tabble(str: Double, age: Int) extends Tibble
case class Trapper(tibble: Tibble)

sealed trait Nibble
case class Nobble(str: String, place: String) extends Nibble
case class Nabble(str: String, age: Int) extends Nibble
case class Napper(nibble: Nibble)