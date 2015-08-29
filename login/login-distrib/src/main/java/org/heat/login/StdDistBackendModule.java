package org.heat.login;

import com.github.blackrush.acara.EventBus;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.logging.LoggingHandler;
import org.heat.backend.marshalling.OptionalExternalizerFactory;
import org.heat.login.backend.Backend;
import org.heat.login.backend.BackendSupervisor;
import org.heat.login.backend.DefaultBackend;
import org.heat.login.backend.DefaultBackendSupervisor;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.rocket.network.ControllerFactory;
import org.rocket.network.NetworkClient;
import org.rocket.network.NetworkService;
import org.rocket.network.guice.ControllerFactoryModule;
import org.rocket.network.netty.RocketNetty;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;

public final class StdDistBackendModule extends PrivateModule {
    @Override
    protected void configure() {
        install(new StdBackendControllerModule());
        install(new ControllerFactoryModule());
    }

    @Exposed
    @Provides
    @Singleton
    @Named("backend")
    NetworkService provideNetwork(
            Provider<EventBus> eventBusBuilder,
            ControllerFactory controllerFactory,
            Config config,
            ByteBufAllocator allocator
    ) {
        return RocketNetty.newService(
                eventBusBuilder,
                controllerFactory,
                sb -> {
                    sb.channelFactory(NioServerSocketChannel::new);
                    sb.localAddress(config.getInt("heat.login.backend.port"));
                    sb.option(ChannelOption.SO_BACKLOG, config.getInt("heat.login.backend.backlog"));
                    sb.option(ChannelOption.SO_REUSEADDR, true);
                    sb.childOption(ChannelOption.TCP_NODELAY, true);
                    sb.childOption(ChannelOption.ALLOCATOR, allocator);
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

    @Exposed
    @Provides
    @Singleton
    BackendSupervisor provideBackendSupervisor(EventBus builder) {
        return new DefaultBackendSupervisor(builder);
    }

    @Provides
    Backend provideBackend(BackendSupervisor backendSupervisor, NetworkClient client) {
        return new DefaultBackend(backendSupervisor, client);
    }
}
