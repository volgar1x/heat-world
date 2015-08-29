package org.heat.login.frontend;

import org.heat.dofus.network.NetworkMessage;
import org.heat.shared.io.DataReader;
import org.heat.shared.io.DataWriter;

public class ClientAuthenticationMessage extends NetworkMessage {
    public static final int PROTOCOL_ID = 888;

    public String username;
    public byte[] credentials;
    public boolean autoconnect;
    public int serverId;

    public ClientAuthenticationMessage() {
    }

    public ClientAuthenticationMessage(String username, byte[] credentials, boolean autoconnect, int serverId) {
        this.username = username;
        this.credentials = credentials;
        this.autoconnect = autoconnect;
        this.serverId = serverId;
    }

    @Override
    public int getProtocolId() {
        return PROTOCOL_ID;
    }

    @Override
    public void deserialize(DataReader reader) {
        username = reader.read_str();
        credentials = reader.read_array_i8(reader.read_ui16());
        autoconnect = reader.read_bool();
        serverId = reader.read_ui16();
    }

    @Override
    public void serialize(DataWriter writer) {
        writer.write_str(username);
        writer.write_ui16(credentials.length);
        writer.write_array_i8(credentials);
        writer.write_bool(autoconnect);
        writer.write_ui16(serverId);
    }
}
