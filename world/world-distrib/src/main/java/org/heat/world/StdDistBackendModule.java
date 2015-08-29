package org.heat.world;

import com.github.blackrush.acara.EventBus;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.logging.LoggingHandler;
import org.heat.backend.marshalling.OptionalExternalizerFactory;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.rocket.network.ControllerFactory;
import org.rocket.network.NetworkClient;
import org.rocket.network.NetworkClientService;
import org.rocket.network.guice.ClientControllerFactoryModule;
import org.rocket.network.netty.RocketNetty;
import org.slf4j.LoggerFactory;

public class StdDistBackendModule extends PrivateModule {
    private final Module controllersModule;

    public StdDistBackendModule(Module controllersModule) {
        this.controllersModule = controllersModule;
    }

    @Override
    protected void configure() {
        bind(NetworkClient.class).to(Key.get(NetworkClientService.class, Names.named("backend")));
        install(controllersModule);
        install(new ClientControllerFactoryModule());
    }

    @Provides
    @Singleton
    @Exposed
    @Named("backend")
    NetworkClientService provideNetworkClientService(
            EventBus eventBus,
            ControllerFactory controllerFactory,
            Config config,
            ByteBufAllocator allocator
    ) {
        return RocketNetty.newClientService(
                eventBus,
                controllerFactory,
                bootstrap -> {
                    bootstrap.remoteAddress(config.getString("heat.world.backend.host"), config.getInt("heat.world.backend.port"));
                    bootstrap.channelFactory(NioSocketChannel::new);
                    bootstrap.option(ChannelOption.ALLOCATOR, allocator);
                },
                pipeline -> {
                    MarshallerFactory marshaller = Marshalling.getProvidedMarshallerFactory("river");
                    MarshallingConfiguration mConfig = new MarshallingConfiguration();
                    mConfig.setVersion(3);
                    mConfig.setClassExternalizerFactory(new OptionalExternalizerFactory());

                    pipeline.addLast("decoder", new MarshallingDecoder(new DefaultUnmarshallerProvider(marshaller, mConfig)));
                    pipeline.addLast("encoder", new MarshallingEncoder(new DefaultMarshallerProvider(marshaller, mConfig)));

                    pipeline.addLast("logging", new LoggingHandler("backend-network"));
                },
                LoggerFactory.getLogger("backend-network")
        );
    }
}
