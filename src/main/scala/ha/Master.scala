package ha



/**
  * Created by shuijun on 2018/7/19.
  */
class Master extends A with  LeaderElectable{


  //必须先extends,然后 with 特质,所以这里我随便弄了个类A,作为过渡

  override def electedLeader() {
     print("i am a leader,next i can do anything") //比如读取zk持久化数据进行恢复
  }

  override def revokedLeadership() {
    //此时我不是leader,就自动退出
    println("i am not a leader ,so i exit ")
//    System.exit(0)
  }
}
