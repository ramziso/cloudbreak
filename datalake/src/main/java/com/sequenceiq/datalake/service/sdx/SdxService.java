package com.sequenceiq.datalake.service.sdx;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.InstanceGroupType;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.RecoveryMode;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.StackV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.authentication.StackAuthenticationV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.ClusterV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.environment.EnvironmentSettingsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.environment.placement.PlacementSettingsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.image.ImageSettingsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.InstanceGroupV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.securitygroup.SecurityGroupV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.InstanceTemplateV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.volume.RootVolumeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.instancegroup.template.volume.VolumeV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.network.NetworkV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.tags.TagsV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.response.StackV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.util.requests.SecurityRuleV4Request;
import com.sequenceiq.cloudbreak.client.CloudbreakUserCrnClient;
import com.sequenceiq.datalake.api.endpoint.sdx.SdxClusterRequest;
import com.sequenceiq.datalake.api.endpoint.sdx.SdxClusterResponse;
import com.sequenceiq.datalake.controller.exception.BadRequestException;
import com.sequenceiq.datalake.entity.SdxCluster;
import com.sequenceiq.datalake.entity.SdxClusterStatus;
import com.sequenceiq.datalake.repository.SdxClusterRepository;

@Service
public class SdxService {

    public static final String CLUSTER_DEFINITION = "CDH 6.2 - Data Science: Apache Spark, Apache Hive, Impala";

    private static final Logger LOGGER = LoggerFactory.getLogger(SdxService.class);

    private static final String SDX_CLUSTER_NAME = "sdx-cluster";

    public static final String TODO_FROM_UMS = "TODO from UMS";

    @Inject
    private CloudbreakUserCrnClient cloudbreakClient;

    @Inject
    private SdxClusterRepository sdxClusterRepository;

    public SdxClusterResponse createSdx(String userCrn, String envName, SdxClusterRequest sdxClusterRequest) {
        StackV4Request stackV4Request = new StackV4Request();

        SdxCluster sdxCluster = new SdxCluster();
        sdxCluster.setClusterName(SDX_CLUSTER_NAME);
        sdxCluster.setAccountId(TODO_FROM_UMS);
        sdxCluster.setStatus(SdxClusterStatus.REQUESTED);
        sdxCluster = sdxClusterRepository.save(sdxCluster);

        LOGGER.info("sdx cluster requested");

        stackV4Request.setName(SDX_CLUSTER_NAME);
        TagsV4Request tags = new TagsV4Request();
        tags.setUserDefined(sdxClusterRequest.getTags());
        stackV4Request.setTags(tags);

        EnvironmentSettingsV4Request environment = new EnvironmentSettingsV4Request();
        environment.setName(envName);
        stackV4Request.setEnvironment(environment);

        PlacementSettingsV4Request placementSettingsV4Request = new PlacementSettingsV4Request();
        placementSettingsV4Request.setRegion("eu-west-1");
        placementSettingsV4Request.setAvailabilityZone("eu-west-1b");
        stackV4Request.setPlacement(placementSettingsV4Request);

        StackAuthenticationV4Request stackAuthenticationV4Request = new StackAuthenticationV4Request();
        stackAuthenticationV4Request.setPublicKey("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC0Rfl2G2vDs6yc19RxCqReunFgpYj+ucyLobpTCBtfDwzIbJot2Fmife6M42mBtiTmAK6x8kcUEeab6CB4MUzsqF7vGTFUjwWirG/XU5pYXFUBhi8xzey+KS9KVrQ+UuKJh/AN9iSQeMV+rgT1yF5+etVH+bK1/37QCKp3+mCqjFzPyQOrvkGZv4sYyRwX7BKBLleQmIVWpofpjT7BfcCxH877RzC5YMIi65aBc82Dl6tH6OEiP7mzByU52yvH6JFuwZ/9fWj1vXCWJzxx2w0F1OU8Zwg8gNNzL+SVb9+xfBE7xBHMpYFg72hBWPh862Ce36F4NZd3MpWMSjMmpDPh centos");
        stackV4Request.setAuthentication(stackAuthenticationV4Request);

        ClusterV4Request clusterV4Request = new ClusterV4Request();
        clusterV4Request.setBlueprintName(CLUSTER_DEFINITION);
        clusterV4Request.setUserName("admin");
        clusterV4Request.setPassword("admin123");
        clusterV4Request.setValidateBlueprint(false);
        stackV4Request.setCluster(clusterV4Request);

        ImageSettingsV4Request image = new ImageSettingsV4Request();
        image.setOs("redhat7");
        stackV4Request.setImage(image);

        InstanceGroupV4Request masterInstanceGroupV4Request = new InstanceGroupV4Request();
        masterInstanceGroupV4Request.setName("master");

        InstanceTemplateV4Request template = new InstanceTemplateV4Request();
        HashSet<VolumeV4Request> attachedVolumes = new HashSet<>();
        VolumeV4Request volumeV4Request = new VolumeV4Request();
        volumeV4Request.setSize(100);
        volumeV4Request.setCount(2);
        volumeV4Request.setType("standard");
        attachedVolumes.add(volumeV4Request);
        template.setAttachedVolumes(attachedVolumes);
        template.setInstanceType("m5.2xlarge");
        RootVolumeV4Request rootVolume = new RootVolumeV4Request();
        rootVolume.setSize(100);
        template.setRootVolume(rootVolume);

        masterInstanceGroupV4Request.setTemplate(template);
        masterInstanceGroupV4Request.setNodeCount(1);
        masterInstanceGroupV4Request.setType(InstanceGroupType.GATEWAY);
        masterInstanceGroupV4Request.setRecoveryMode(RecoveryMode.MANUAL);

        SecurityGroupV4Request securityGroup = new SecurityGroupV4Request();
        ArrayList<SecurityRuleV4Request> securityRules = new ArrayList<>();
        SecurityRuleV4Request securityRule9443 = new SecurityRuleV4Request();
        securityRule9443.setSubnet(sdxClusterRequest.getAccessCidr());
        ArrayList<String> ports = new ArrayList<>();
        ports.add("443");
        ports.add("9443");
        ports.add("22");
        securityRule9443.setPorts(ports);
        securityRule9443.setProtocol("tcp");
        securityRules.add(securityRule9443);
        securityGroup.setSecurityRules(securityRules);

        masterInstanceGroupV4Request.setSecurityGroup(securityGroup);

        InstanceGroupV4Request workerInstanceGroupV4Request = new InstanceGroupV4Request();
        workerInstanceGroupV4Request.setName("worker");
        workerInstanceGroupV4Request.setTemplate(template);
        workerInstanceGroupV4Request.setNodeCount(3);
        workerInstanceGroupV4Request.setType(InstanceGroupType.CORE);
        workerInstanceGroupV4Request.setRecoveryMode(RecoveryMode.MANUAL);
        workerInstanceGroupV4Request.setSecurityGroup(securityGroup);

        InstanceGroupV4Request computeInstanceGroupV4Request = new InstanceGroupV4Request();
        computeInstanceGroupV4Request.setName("compute");
        computeInstanceGroupV4Request.setTemplate(template);
        computeInstanceGroupV4Request.setNodeCount(1);
        computeInstanceGroupV4Request.setType(InstanceGroupType.CORE);
        computeInstanceGroupV4Request.setRecoveryMode(RecoveryMode.MANUAL);
        computeInstanceGroupV4Request.setSecurityGroup(securityGroup);

        ArrayList<InstanceGroupV4Request> instanceGroups = new ArrayList<>();
        instanceGroups.add(masterInstanceGroupV4Request);
        instanceGroups.add(workerInstanceGroupV4Request);
        instanceGroups.add(computeInstanceGroupV4Request);
        stackV4Request.setInstanceGroups(instanceGroups);

        NetworkV4Request network = new NetworkV4Request();
        network.setSubnetCIDR("10.0.0.0/16");
        stackV4Request.setNetwork(network);

        LOGGER.info("Call cloudbreak with stackrequest");
        StackV4Response stackV4Response = cloudbreakClient.withCrn(userCrn).stackV4Endpoint().post(0L, stackV4Request);
        sdxCluster.setStackId(stackV4Response.getId());
        sdxCluster.setStatus(SdxClusterStatus.REQUESTED_FROM_CLOUDBREAK);
        sdxClusterRepository.save(sdxCluster);
        LOGGER.info("Sdx cluster updated");

        SdxClusterResponse sdxClusterResponse = new SdxClusterResponse();
        sdxClusterResponse.setSdxName(SDX_CLUSTER_NAME);
        return sdxClusterResponse;
    }

    public List<SdxClusterResponse> listSdx(String userCrn) {
        return new ArrayList<>();
    }

    public void deleteSdx(String userCrn) {
        LOGGER.info("Delete sdx");
        SdxCluster sdxCluster = sdxClusterRepository.findByAccountIdAndClusterName(TODO_FROM_UMS, SDX_CLUSTER_NAME);
        if (sdxCluster == null) {
            LOGGER.info("Can not find sdx cluster");
            throw new BadRequestException("Can not find sdx cluster");
        }
        cloudbreakClient.withCrn(userCrn).stackV4Endpoint().delete(0L, sdxCluster.getClusterName(), false, true);
        sdxClusterRepository.delete(sdxCluster);
        LOGGER.info("sdx deleted");
    }
}
