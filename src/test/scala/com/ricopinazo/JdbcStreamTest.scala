package com.ricopinazo

import cats.effect.IO
import munit.CatsEffectSuite

import java.sql.DriverManager

class JdbcStreamTest extends CatsEffectSuite {

  test("Reads 100_000 sequential integers from a table correctly") {
    Class.forName("org.h2.Driver")
    val connection = DriverManager.getConnection(s"jdbc:h2:mem:test")

    connection.createStatement().executeUpdate("CREATE TABLE test (index INTEGER PRIMARY KEY)")
    0 until 100_000 foreach { index =>
      connection.createStatement().executeUpdate(s"INSERT INTO test VALUES ($index)")
    }

    val indexes = JdbcStream[IO, Int](connection, "SELECT * FROM test", resultSet => resultSet.getInt(1))

    val expected = fs2.Stream.iterateEval(0)(n => IO(n + 1))

    val allEquals = indexes zip expected forall { case (a, b) => a == b }
    val success = allEquals.compile.last.map(value => value.get)

    assertIOBoolean(success)
  }
}
