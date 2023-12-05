package common.db.dao;

import common.db.models.Job;

public class JobDao extends BaseDao<Job> {

    public JobDao() {
        super(Job.class, "job");    
    }

}
