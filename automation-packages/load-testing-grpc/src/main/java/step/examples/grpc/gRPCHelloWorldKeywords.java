package step.examples.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

import java.util.concurrent.TimeUnit;

public class gRPCHelloWorldKeywords extends AbstractKeyword {

    @Keyword
    public void HelloWorld() throws InterruptedException {
        String user = "world";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        try {
            gRPCHelloWorldClient client = new gRPCHelloWorldClient(channel);
            output.startMeasure("gRPC_SayHello");
            String response = client.greet(user);
            output.stopMeasure();
            output.add("Response", response);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

}
