package org.heat.world.users;

import com.ankamagames.dofus.datacenter.breeds.Breed;
import org.heat.User;

import java.util.List;

public interface UserCapabilities {
    boolean isTutorialAvailable(User user);
    List<Breed> getVisibleBreeds(User user);
    List<Breed> getAvailableBreeds(User user);
}
