package org.heat.login.frontend;

import com.ankamagames.dofus.network.MessageReceiver;
import org.heat.dofus.network.NetworkComponentFactory;
import org.heat.dofus.network.NetworkMessage;
import org.heat.dofus.network.SimpleNetworkComponentFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FrontendMessageReceiver extends SimpleNetworkComponentFactory<NetworkMessage> {
    public FrontendMessageReceiver() {
        super(createNewRegister());
    }

    public static Map<Integer, Supplier<NetworkMessage>> createNewRegister() {
        Map<Integer, Supplier<NetworkMessage>> map = new HashMap<>();
        map.put(ClientAuthenticationMessage.PROTOCOL_ID, ClientAuthenticationMessage::new);
        return map;
    }

    public static NetworkComponentFactory<NetworkMessage> createNewReceiver() {
        return MessageReceiver.createNewReceiver().withFallback(new FrontendMessageReceiver());
    }
}
