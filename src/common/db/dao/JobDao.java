package common.db.dao;

import common.db.models.JobDbModel;

public class JobDao extends BaseDao<JobDbModel> {

    public JobDao() {
        super(JobDbModel.class, "job");    
    }

}
