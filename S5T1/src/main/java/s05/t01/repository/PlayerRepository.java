package s05.t01.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import s05.t01.model.Player;


@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Integer>{
}
