package com.xsb.study.thrift.tservice;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 同步服务端
 *
 * @author xingshibao@gamil.com
 * @since 2018-01-05 18:31
 */
public class TestTServiceImpl implements TestTService.Iface {

    @Override
    public int getLength(String word) throws TException {
        if (Objects.isNull(word)) {
            return 0;
        }
        return word.length();
    }

    public static void main(String[] args) throws Exception {
        TestTServiceImpl testTService = new TestTServiceImpl();
        TProcessor tProcessor = new TestTService.Processor<TestTService.Iface>(testTService);

        //传输通道 - 非阻塞方式
        TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(8419);
        ExecutorService executorService = Executors.newFixedThreadPool(16);
        //多线程半同步半异步
        TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
        tArgs.selectorThreads(4)
                .executorService(executorService)
                .acceptQueueSizePerThread(50)
                .acceptPolicy(TThreadedSelectorServer.Args.AcceptPolicy.FAST_ACCEPT);
        tArgs.processor(tProcessor);
        tArgs.transportFactory(new TFramedTransport.Factory());
        //二进制协议
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        //多线程半同步半异步的服务模型
        TServer server = new TThreadedSelectorServer(tArgs);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("server stopping...");
            server.stop();
        }));

        server.serve();
        System.out.println("server shutdown.");
    }
}
