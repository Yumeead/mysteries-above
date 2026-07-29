package me.vangoo.application.services.context;

import me.vangoo.application.services.ChurchService;
import me.vangoo.domain.abilities.context.IChurchContext;
import me.vangoo.domain.organizations.Institution;

import java.util.List;
import java.util.UUID;

public class ChurchContext implements IChurchContext {

    private final ChurchService churchService;

    public ChurchContext(ChurchService churchService) {
        this.churchService = churchService;
    }

    @Override
    public List<Institution> churches() {
        return churchService.registry().churches();
    }

    @Override
    public void disguiseAs(UUID playerId, String institutionId, long durationMillis) {
        churchService.disguiseAs(playerId, institutionId, durationMillis);
    }

    @Override
    public void dropDisguise(UUID playerId) {
        churchService.dropDisguise(playerId);
    }
}
