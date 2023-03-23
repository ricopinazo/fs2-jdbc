package com.ricopinazo

import cats.effect.Sync
import fs2.Stream.fromAutoCloseable

import java.sql.{Connection, ResultSet}

object JdbcStream {
  def apply[F[_], T](connection: Connection, sql: String, f: ResultSet => T)(implicit F: Sync[F]): fs2.Stream[F, T] = {
    for {
      statement <- fromAutoCloseable(F.blocking(connection.createStatement()))
      resultSet <- fromAutoCloseable(F.blocking(statement.executeQuery(sql)))
      value     <- fs2.Stream
                     .repeatEval(F.blocking(if(resultSet.next()) Some(f.apply(resultSet)) else None))
                     .collectWhile { case Some(value) => value }
    } yield value
  }
}
