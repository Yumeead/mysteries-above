package me.vangoo.domain.abilities.context;

import me.vangoo.domain.organizations.Institution;

import java.util.List;
import java.util.UUID;

/**
 * Доступ здібностей до церков (Помилка, Посл. 5: «Личина» — крадіжка таємниць небес).
 * Вузький контракт за зразком {@code IContractContext}: здібність не імпортує
 * {@code application.services}, а сам {@code ChurchService} нічого не знає про шляхи.
 *
 * <p>Личина живе в пам'яті сервісу з абсолютним строком і навмисно НЕ персиститься:
 * 10 хв — рівно те, що має згаснути разом із рестартом.
 */
public interface IChurchContext {

    /** Усі канонічні церкви — під чию личину можна лізти. */
    List<Institution> churches();

    /** Церква на {@code durationMillis} вважає гравця своїм (одна личина за раз). */
    void disguiseAs(UUID playerId, String institutionId, long durationMillis);

    /** Зняти личину достроково (строк спливе й сам). */
    void dropDisguise(UUID playerId);
}
