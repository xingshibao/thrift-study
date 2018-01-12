package com.xsb.study.thrift.tservice;

import com.wealoha.thrift.PoolConfig;
import com.wealoha.thrift.ServiceInfo;
import com.wealoha.thrift.ThriftClient;
import com.wealoha.thrift.ThriftClientPool;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shibao.xing
 * @since 2018-01-05 19:48
 */
public class SyncClient {

    public static void main(String[] args) throws TException {
        simple();
//        pool();
    }

    private static void simple() throws TException {
        TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8419));
        TProtocol protocol = new TBinaryProtocol(transport);
        transport.open();
        TestTService.Client client = new TestTService.Client(protocol);
        int thrift = client.getLength("thrift");
        System.out.println(thrift);
    }

    /**
     * 线程池的方式
     */
    private static void pool() throws TException {
        List<ServiceInfo> serviceList = new ArrayList<>();
        serviceList.add(new ServiceInfo("127.0.0.1", 8419));

        PoolConfig config = new PoolConfig();
        config.setFailover(false); // optional
        config.setTimeout(200); // optional

        config.setMinIdle(10);
        config.setMaxTotal(20);
        ThriftClientPool<TestTService.Client> pool = new ThriftClientPool<>(
                serviceList,
                transport -> new TestTService.Client(new TBinaryProtocol(new TFramedTransport(transport))),
                config);
        TestTService.Iface iface = pool.iface();
        int thrift = iface.getLength("t");
        System.out.println(thrift);
    }
}
