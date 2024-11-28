package app;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * UserRepository interface to interact with mangodb. Extended from MongoRepository.
 */  
public interface UserRepository extends MongoRepository<User, ObjectId> {
    
  Optional<User> findByName(String name); // name is unique
  
  Optional<User> findByEmail(String email); // unique
    
}
