package org.heat.datacenter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JaSerFileDatacenterTest {

    private JaSerFileDatacenter datacenter;

    @Before
    public void setUp() throws Exception {
        Config config = ConfigFactory.load().resolve();
        Path dofusPath = Paths.get(config.getString("dofus.path"));
        datacenter = new JaSerFileDatacenter(dofusPath.resolve("d2objects"));
    }

    @Test
    public void testLoad() throws Exception {
        datacenter.load();
    }
}