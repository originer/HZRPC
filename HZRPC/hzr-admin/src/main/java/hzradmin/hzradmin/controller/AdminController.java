package hzradmin.hzradmin.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Zz
 **/
@RequestMapping("/admin")
@Controller
public class AdminController {
    @Value("${zookepper.server}")
    private static String ZK_SERVER;

    @Value("${zookeeper.path.prefix}")
    private static String ZK_PATH_PREFIX;


    private CuratorFramework curatorFramework;

    /**
     * 被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
     */
    @PostConstruct
    public void init() {
        //通过curatorFramework [zk的抽象API接口] 来连接zk
        curatorFramework = CuratorFrameworkFactory.newClient(ZK_SERVER, new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
    }

    @RequestMapping("/index")
    public String index(Map<String, Object> model) throws Exception {
        List<String> services = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX);

        System.out.println(services.toString());

        model.put("name", services.toString());
        return "index";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Integer test(@RequestParam(value = "name") Integer name) throws Exception {
        System.out.println(name);
        return name;
    }
}
