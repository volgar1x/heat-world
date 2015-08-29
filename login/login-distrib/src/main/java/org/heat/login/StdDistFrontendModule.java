package org.heat.login;

import com.github.blackrush.acara.EventBus;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.heat.dofus.network.DofusProtocol;
import org.heat.dofus.network.NetworkComponentFactory;
import org.heat.dofus.network.NetworkMessage;
import org.heat.dofus.network.netty.DofusDecoder;
import org.heat.dofus.network.netty.DofusEncoder;
import org.heat.login.frontend.FrontendMessageReceiver;
import org.rocket.network.ControllerFactory;
import org.rocket.network.NetworkService;
import org.rocket.network.guice.ControllerFactoryModule;
import org.rocket.network.netty.RocketNetty;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;

public final class StdDistFrontendModule extends PrivateModule {
    @Override
    protected void configure() {
        install(new StdFrontendControllerModule());
        install(new ControllerFactoryModule());
    }

    public static final int INITIAL_RCV_BUF_SIZE = 32;

    @Exposed
    @Provides
    @Named("frontend")
    NetworkService provideNetworkService(
            Provider<EventBus> eventBusBuilder,
            ControllerFactory controllerFactory,
            Config config,
            ByteBufAllocator allocator,
            NetworkComponentFactory<NetworkMessage> messageFactory
    ) {
        return RocketNetty.newService(
                eventBusBuilder,
                controllerFactory,
                sb -> {
                    sb.channelFactory(NioServerSocketChannel::new);
                    sb.localAddress(config.getInt("heat.login.frontend.port"));
                    sb.option(ChannelOption.SO_BACKLOG, config.getInt("heat.login.frontend.backlog"));
                    sb.option(ChannelOption.SO_REUSEADDR, true);
                    sb.childOption(ChannelOption.TCP_NODELAY, true);
                    sb.childOption(ChannelOption.ALLOCATOR, allocator);
                    sb.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(
                            DofusProtocol.MAX_HEADER_LEN,
                            INITIAL_RCV_BUF_SIZE,
                            DofusProtocol.MAX_MESSAGE_LEN
                    ));
                },
                pipeline -> {
                    pipeline.addLast("decoder", new DofusDecoder(messageFactory));
                    pipeline.addLast("encoder", new DofusEncoder());
                    pipeline.addLast("logging", new LoggingHandler("frontend-network"));
                },
                LoggerFactory.getLogger("frontend-network")
        );
    }

    @Provides
    @Singleton
    NetworkComponentFactory<NetworkMessage> provideNetworkMessageFactory() {
        return FrontendMessageReceiver.createNewReceiver();
    }
}
