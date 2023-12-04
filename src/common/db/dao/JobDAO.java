package common.db.dao;

import common.db.models.Job;

public class JobDAO extends BaseDAO<Job> {

    public JobDAO() {
        super(Job.class, "job");    
    }

}
