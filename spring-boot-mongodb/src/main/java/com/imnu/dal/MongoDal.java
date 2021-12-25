package com.imnu.dal;

import com.imnu.entry.MongoEntity;

public interface MongoDal {

    void saveMongo(MongoEntity mongoEntity);

    void removeMongo(Long id);

    void updateMongo(MongoEntity mongoEntity);

    MongoEntity findMongoById(Long id);
}
