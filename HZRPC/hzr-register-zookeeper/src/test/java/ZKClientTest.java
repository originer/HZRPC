import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;

import java.util.List;

@Slf4j
public class ZKClientTest {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181",5000);
        log.info("zookeeper connected ");
        String path = "/zk-test";

        // 注册子节点变更监听（此时path节点并不存在，但可以进行监听注册）
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("路径" + parentPath +"下面的子节点变更。子节点为：" + currentChilds );
            }
        });

        // 递归创建子节点（此时父节点并不存在）
        zkClient.createPersistent("/zk-test/a1",true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(zkClient.getChildren(path));
    }

}