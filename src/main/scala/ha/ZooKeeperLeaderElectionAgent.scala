/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ha
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.leader.LeaderLatch
import org.apache.curator.framework.recipes.leader.LeaderLatchListener
import org.apache.curator.framework.recipes.leader.{LeaderLatch, LeaderLatchListener}



private class ZooKeeperLeaderElectionAgent(val masterInstance: LeaderElectable
                                                  ) extends LeaderLatchListener with LeaderElectionAgent  {

//  val WORKING_DIR = conf.get("spark.deploy.zookeeper.dir", "/spark") + "/leader_election"
 val WORKING_DIR = "/spark/leader_election" // master 选择目录
  private var zk: CuratorFramework = _
  private var leaderLatch: LeaderLatch = _
  private var status = LeadershipStatus.NOT_LEADER

  start()

  private def start() {
    print("Starting ZooKeeper LeaderElection agent")
    zk = SparkCuratorUtil.newClient()
    leaderLatch = new LeaderLatch(zk, WORKING_DIR)
    leaderLatch.addListener(this)
    leaderLatch.start()
  }

  override def stop() {
    leaderLatch.close()
    zk.close()
  }



  // LeaderlatchListener，它在LeaderLatch状态变化的时候被通知：
  // 在该节点被选为Leader的时候，调用isLeader()
  // 在节点被剥夺Leader的时候， 调用notLeader()
  override def isLeader() {
    synchronized {
      // could have lost leadership by now.
      if (!leaderLatch.hasLeadership) {
        return
      }

//      logInfo("We have gained leadership")
      updateLeadershipStatus(true)
    }
  }

  override def notLeader() {
    synchronized {

      //由于通知是异步的，因此有可能在接口被调用的时候，这个状态是准确的，需要确认一下LeaderLatch的hasLeadership()是否的确是true/false
      // could have gained leadership by now.
      if (leaderLatch.hasLeadership) {
        return
      }

//      logInfo("We have lost leadership")
      updateLeadershipStatus(false)
    }
  }

  private def updateLeadershipStatus(isLeader: Boolean) {
    if (isLeader && status == LeadershipStatus.NOT_LEADER) {
      status = LeadershipStatus.LEADER
      masterInstance.electedLeader()
    } else if (!isLeader && status == LeadershipStatus.LEADER) {
      status = LeadershipStatus.NOT_LEADER
      masterInstance.revokedLeadership()
    }
  }

  private object LeadershipStatus extends Enumeration {
    type LeadershipStatus = Value
    val LEADER, NOT_LEADER = Value
  }
}
