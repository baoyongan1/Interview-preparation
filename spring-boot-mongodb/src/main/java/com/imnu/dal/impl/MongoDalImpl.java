package com.imnu.dal.impl;

import com.imnu.dal.MongoDal;
import com.imnu.entry.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class MongoDalImpl implements MongoDal {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveMongo(MongoEntity mongoEntity) {

        //若新增数据的主键已经存在，则会抛 org.springframework.dao.DuplicateKeyException 异常提示主键重复，不保存当前数据。
        //可以一次性插入一整个列表，而不用进行遍历操作，效率相对较高
        mongoTemplate.insert(mongoEntity);

        //若新增数据的主键已经存在，则会对当前已经存在的数据进行修改操作。
        //需要遍历列表，进行一个个的插入
        mongoTemplate.save(mongoEntity);
    }

    @Override
    public void removeMongo(Long id) {

        //根据id删除
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), MongoEntity.class);
    }

    @Override
    public void updateMongo(MongoEntity mongoEntity) {

        //updateFirst根据id修改，修改符合条件第一条记录
        mongoTemplate.updateFirst(Query.query(Criteria.where("id")
                .is(mongoEntity.getId()))
                ,Update.update("title",mongoEntity.getTitle())
                ,MongoEntity.class);
        //updateMulti根据url修改,修改符合条件的所有
        mongoTemplate.updateMulti(Query.query(Criteria.where("url")
                        .is(mongoEntity.getUrl()))
                ,Update.update("title",mongoEntity.getTitle())
                ,MongoEntity.class);

        //upsert根据id修改，实现数据存在就更新，不存在就插入数据
        mongoTemplate.upsert(Query.query(Criteria.where("id")
                        .is(mongoEntity.getId()))
                ,Update.update("title",mongoEntity.getTitle())
                ,MongoEntity.class);

    }

    @Override
    public MongoEntity findMongoById(Long id) {

        //gte大于等于、gt大于、lt小于、lte小于等于
        mongoTemplate.find(Query.query(Criteria.where("id").is(id).gte(1).gt(1).lt(3).lte(3)), MongoEntity.class);

        //and、or、in
        mongoTemplate.find(Query.query(Criteria.where("id").is(id).and("title").is(1)), MongoEntity.class);

        //正则表达
        mongoTemplate.find(Query.query(Criteria.where("id").regex("正则表达式")), MongoEntity.class);

        //查询总数
        mongoTemplate.count(Query.query(Criteria.where("id").is(id)), MongoEntity.class);


        // 满足所有条件的数据
        mongoTemplate.find(Query.query(Criteria.where("id").is(id)), MongoEntity.class);

        //分组查询(根据性别分组)
        /**
         * 分组
         */
        mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.group("sex").count().as("男"))
                , "mongoEntitys",MongoEntity.class);
//        mongoTemplate.group("mongoEntitys", GroupBy.key("sex").initialDocument("").reduceFunction(""), MongoEntity.class);

        //排序
        mongoTemplate.find(Query.query(Criteria.where("id").is(id)).with(Sort.by(Sort.Order.desc("age"))), MongoEntity.class);

        //分页
        // limit限定查询2条
        List<MongoEntity> result = mongoTemplate.find(Query.query(Criteria.where("title").is("title")).with(Sort.by("age")).limit(2), MongoEntity.class, "mongoEntitys");
        System.out.println("query: "  + " | limitPageQuery " + result);


        // skip()方法来跳过指定数量的数据
        result = mongoTemplate.find(Query.query(Criteria.where("title").is("title")).with(Sort.by("age")).skip(2), MongoEntity.class, "mongoEntitys");
        System.out.println("query: "  + " | skipPageQuery " + result);

        // 查询一条满足条件的数据
        MongoEntity mongoEntity = mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), MongoEntity.class);
        return mongoEntity;
    }
}
