package org.heat.backend.marshalling;

import org.jboss.marshalling.Creator;
import org.jboss.marshalling.Externalizer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;

public enum OptionalExternalizer implements Externalizer {
    INSTANCE;

    @Override
    public void writeExternal(Object subject, ObjectOutput output) throws IOException {
        Optional opt = (Optional) subject;
        output.writeBoolean(opt.isPresent());
        if (opt.isPresent()) {
            output.writeObject(opt.get());
        }
    }

    @Override
    public Object createExternal(Class<?> subjectType, ObjectInput input, Creator defaultCreator) throws IOException, ClassNotFoundException {
        if (input.readBoolean()) {
            return Optional.of(input.readObject());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void readExternal(Object subject, ObjectInput input) throws IOException, ClassNotFoundException {}
}
