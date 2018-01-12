package com.xsb.study.thrift.tservice;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shibao.xing
 * @since 2018-01-08 16:19
 */
public class AsyncTServiceImpl implements TestTService.AsyncIface {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    @Override
    public void getLength(String word, AsyncMethodCallback resultHandler) throws TException {
        EXECUTOR_SERVICE.submit(() -> {
            if (Objects.isNull(word)) {
                resultHandler.onComplete(0);
            } else {
                resultHandler.onComplete(word.length());
            }
        });
    }

    public static void main(String[] args) throws Exception {
        AsyncTServiceImpl asyncTService = new AsyncTServiceImpl();
        TProcessor tProcessor = new TestTService.AsyncProcessor<TestTService.AsyncIface>(asyncTService);

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
