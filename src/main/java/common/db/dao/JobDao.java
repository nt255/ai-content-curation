package main.java.common.db.dao;

import main.java.common.db.models.JobDbModel;

public class JobDao extends BaseDao<JobDbModel> {

    public JobDao() {
        super(JobDbModel.class, "job");    
    }

}
