package org.heat.backend.marshalling;

import org.jboss.marshalling.Creator;
import org.jboss.marshalling.Externalizer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.OptionalInt;

public enum OptionalIntExternalizer implements Externalizer {
    INSTANCE;

    @Override
    public void writeExternal(Object subject, ObjectOutput output) throws IOException {
        OptionalInt opt = (OptionalInt) subject;
        output.writeBoolean(opt.isPresent());
        if (opt.isPresent()) {
            output.writeInt(opt.getAsInt());
        }
    }

    @Override
    public Object createExternal(Class<?> subjectType, ObjectInput input, Creator defaultCreator) throws IOException, ClassNotFoundException {
        if (input.readBoolean()) {
            return OptionalInt.of(input.readInt());
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public void readExternal(Object subject, ObjectInput input) throws IOException, ClassNotFoundException {}
}
