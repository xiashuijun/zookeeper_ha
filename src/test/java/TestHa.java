import ha.*;

/**
 * Created by shuijun on 2018/7/19.
 */
public class TestHa {

    //1.报错处理 Exception in thread "main" org.apache.zookeeper.KeeperException$UnimplementedException: KeeperErrorCode = Unimplemented for /spark
    // 原因:curator 与 zookeeper的版本不对
    //处理方式:Curator
    // 2.x.x - compatible with both ZooKeeper 3.4.x and ZooKeeper 3.5.x
    // Curator 3.x.x - compatible only with ZooKeeper 3.5.x and includes support for new features such as dynamic reconfiguration, etc.


    //报错2: Starting ZooKeeper LeaderElection agentException in thread "main" java.lang.IllegalArgumentException: Path length must be > 0
    public static void main(String[] args) {
        PersistenceEngine persistenceEngine = null;
        LeaderElectionAgent leaderElectionAgent = null;
        try {
            JavaSerializer serializer = new JavaSerializer();
            ZooKeeperRecoveryModeFactory zkFactory = new ZooKeeperRecoveryModeFactory(serializer);
             persistenceEngine = zkFactory.createPersistenceEngine(); //持久化工具
             leaderElectionAgent = zkFactory.createLeaderElectionAgent(new Master());//leader选举

        }finally {
            persistenceEngine.close();
            leaderElectionAgent.stop();

        }


    }
}
