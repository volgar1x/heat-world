package org.heat.backend.marshalling;

import org.jboss.marshalling.ClassExternalizerFactory;
import org.jboss.marshalling.Externalizer;

import java.util.Optional;
import java.util.OptionalInt;

public class OptionalExternalizerFactory implements ClassExternalizerFactory {
    @Override
    public Externalizer getExternalizer(Class<?> type) {
        if (Optional.class.equals(type)) {
            return OptionalExternalizer.INSTANCE;
        } else if (OptionalInt.class.equals(type)) {
            return OptionalIntExternalizer.INSTANCE;
        }
        return null;
    }
}
