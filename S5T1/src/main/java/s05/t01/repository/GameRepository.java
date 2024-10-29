package s05.t01.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import s05.t01.model.Game;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}
