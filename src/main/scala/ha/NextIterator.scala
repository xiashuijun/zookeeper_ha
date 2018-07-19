package ha

/**
  * Created by shuijun on 2018/7/19.
  */
abstract  class NextIterator  [U] extends Iterator[U] {
  private var gotNext = false
  private var nextValue: U = _
  private var closed = false
  protected var finished = false

  protected def getNext(): U


  protected def close()


  def closeIfNeeded() {
    if (!closed) {
      closed = true
      close()
    }
  }

  override def hasNext: Boolean = {
    if (!finished) {
      if (!gotNext) {
        nextValue = getNext()
        if (finished) {
          closeIfNeeded()
        }
        gotNext = true
      }
    }
    !finished
  }

  override def next(): U = {
    if (!hasNext) {
      throw new NoSuchElementException("End of stream")
    }
    gotNext = false
    nextValue
  }
}
