package com.xsb.study.thrift.tservice;

import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;

/**
 * @author shibao.xing
 * @since 2018-01-08 17:55
 */
public class AsyncClient {

    public static void main(String[] args) throws Exception {
        TNonblockingTransport transport = new TNonblockingSocket("127.0.0.1", 8419);
        TestTService.AsyncClient asyncClient = new TestTService.AsyncClient(new TBinaryProtocol.Factory(), new TAsyncClientManager(), transport);
        asyncClient.getLength("thrift", new AsyncMethodCallback<TestTService.AsyncClient.getLength_call>() {
            @Override
            public void onComplete(TestTService.AsyncClient.getLength_call getLength_call) {
                try {
                    System.out.println(getLength_call.getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(100);
    }
}
