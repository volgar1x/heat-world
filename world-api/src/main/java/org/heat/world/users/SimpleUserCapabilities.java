package org.heat.world.users;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import com.google.common.collect.ImmutableList;
import org.heat.User;
import org.heat.datacenter.Datacenter;
import org.rocket.Service;
import org.rocket.ServicePath;
import org.rocket.StartReason;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SimpleUserCapabilities implements UserCapabilities, Service {
    private List<Breed> breeds;

    @Inject Datacenter datacenter;

    @Override
    public ServicePath path() {
        return ServicePath.absolute("user/capabilities");
    }

    @Override
    public ServicePath dependsOn() {
        return ServicePath.absolute("datacenter");
    }

    @Override
    public void start(StartReason reason) {
        this.breeds = ImmutableList.copyOf(datacenter.findAll(Breed.class).get().values());
    }

    @Override
    public void stop() { }

    @Override
    public boolean isTutorialAvailable(User user) {
        return true;
    }

    @Override
    public List<Breed> getVisibleBreeds(User user) {
        return breeds;
    }

    @Override
    public List<Breed> getAvailableBreeds(User user) {
        return breeds;
    }
}
