package com.sptek.kafka.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.sptek.kafka.model.LogCmpMongo;

@Repository
public class MongoRepository {

	@Autowired
    private MongoTemplate mongoTemplate;

    public List<LogCmpMongo> findAll() {
        return mongoTemplate.findAll(LogCmpMongo.class);
    }

    public LogCmpMongo save(LogCmpMongo tomcatLogInfo) {
        mongoTemplate.save(tomcatLogInfo);
        return tomcatLogInfo;
    }

	/*
	 * public Department update(Department department){ Query query = new Query();
	 * query.addCriteria(Criteria.where("id").is(department.getId())); Update update
	 * = new Update(); update.set("name", department.getName());
	 * update.set("description", department.getDescription()); return
	 * mongoTemplate.findAndModify(query, update, Department.class); }
	 */

	/*
	 * public List findDepartmentByName(String deptName){ Query query = new Query();
	 * query.addCriteria(Criteria.where("name").is(deptName)); return
	 * mongoTemplate.find(query, Department.class); }
	 */

	/*
	 * public void deleteById(String deptId) { Query query = new Query();
	 * query.addCriteria(Criteria.where("id").is(deptId));
	 * mongoTemplate.remove(query, Department.class); }
	 */
}
