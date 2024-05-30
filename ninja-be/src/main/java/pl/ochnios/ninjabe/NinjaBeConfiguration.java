package pl.ochnios.ninjabe;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NinjaBeConfiguration {

    @Value("${custom.qdrant.client.host}")
    private String qdrantClientHost;

    @Value("${custom.qdrant.client.port}")
    private int qdrantClientPort;

    @Value("${custom.qdrant.client.tls}")
    private boolean qdrantClientTls;

    @Bean
    public QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient.newBuilder(qdrantClientHost, qdrantClientPort, qdrantClientTls).build()
        );
    }
}
