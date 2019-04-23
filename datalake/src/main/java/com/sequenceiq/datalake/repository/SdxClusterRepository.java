package com.sequenceiq.datalake.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sequenceiq.datalake.entity.SdxCluster;

@Repository
public interface SdxClusterRepository extends CrudRepository<SdxCluster, Long> {

    SdxCluster findByAccountIdAndClusterName(String accountId, String clusterName);

}
