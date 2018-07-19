package ha

import java.io.IOException


import scala.util.control.NonFatal

/**
  * Created by shuijun on 2018/7/19.
  */
object Utils {

  def tryOrIOException[T](block: => T): T = {
    try {
      block
    } catch {
      case e: IOException =>
        print("Exception encountered", e)
        throw e
      case NonFatal(e) =>
        print("Exception encountered", e)
        throw new IOException(e)
    }
  }

}
