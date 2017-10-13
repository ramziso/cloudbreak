package com.sequenceiq.cloudbreak.service.image;

import static com.sequenceiq.cloudbreak.cloud.model.Platform.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.api.model.InstanceGroupType;
import com.sequenceiq.cloudbreak.client.PkiUtil;
import com.sequenceiq.cloudbreak.cloud.PlatformParameters;
import com.sequenceiq.cloudbreak.cloud.model.AmbariRepo;
import com.sequenceiq.cloudbreak.cloud.model.HDPInfo;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.Platform;
import com.sequenceiq.cloudbreak.common.type.ComponentType;
import com.sequenceiq.cloudbreak.core.CloudbreakImageNotFoundException;
import com.sequenceiq.cloudbreak.domain.Component;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.json.Json;
import com.sequenceiq.cloudbreak.service.CloudbreakServiceException;
import com.sequenceiq.cloudbreak.service.ComponentConfigProvider;

@Service
@Transactional
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    @Inject
    private ImageNameUtil imageNameUtil;

    @Inject
    private HdpInfoSearchService hdpInfoSearchService;

    @Inject
    private UserDataBuilder userDataBuilder;

    @Inject
    private ComponentConfigProvider componentConfigProvider;

    public Image getImage(Long stackId) throws CloudbreakImageNotFoundException {
        return componentConfigProvider.getImage(stackId);
    }

    @Transactional(TxType.NEVER)
    public void create(Stack stack, PlatformParameters params, String ambariVersion, String hdpVersion, String imageCatalog, Optional<String> customImage)
            throws CloudbreakImageNotFoundException {
        try {
            Platform platform = platform(stack.cloudPlatform());
            String platformString = platform(stack.cloudPlatform()).value().toLowerCase();
            String imageName = imageNameUtil.determineImageName(platformString, stack.getRegion(), ambariVersion, hdpVersion, customImage);
            String cbPrivKey = stack.getSecurityConfig().getCloudbreakSshPrivateKeyDecoded();
            String cbSshKey = stack.getSecurityConfig().getCloudbreakSshPublicKeyDecoded();
            byte[] cbSshKeyDer = PkiUtil.getPublicKeyDer(cbPrivKey);
            String sshUser = stack.getStackAuthentication().getLoginUserName();
            Map<InstanceGroupType, String> userData = userDataBuilder.buildUserData(platform, cbSshKeyDer, cbSshKey, sshUser, params,
                    stack.getSecurityConfig().getSaltBootPassword());
            HDPInfo hdpInfo = hdpInfoSearchService.searchHDPInfo(platformString, ambariVersion, hdpVersion, imageCatalog);
            if (hdpInfo != null) {
                String specificImage = imageNameUtil.determineImageName(hdpInfo, platformString, stack.getRegion(), customImage);
                if (specificImage == null) {
                    LOGGER.warn("Cannot find image in the catalog, fallback to default image, ambari: {}, hdp: {}", ambariVersion, hdpVersion);
                    hdpInfo = null;
                } else {
                    LOGGER.info("Determined image from catalog: {}", specificImage);
                    imageName = specificImage;
                }
            }
            List<Component> components = new ArrayList<>();
            Image image;
            if (hdpInfo == null) {
                image = new Image(imageName, userData);
            } else {
                AmbariRepo ambariRepo = new AmbariRepo();
                ambariRepo.setPredefined(Boolean.TRUE);
                Component ambariRepoComponent = new Component(ComponentType.AMBARI_REPO_DETAILS, ComponentType.AMBARI_REPO_DETAILS.name(),
                        new Json(ambariRepo), stack);
                components.add(ambariRepoComponent);
                Component hdpRepoComponent = new Component(ComponentType.HDP_REPO_DETAILS, ComponentType.HDP_REPO_DETAILS.name(),
                        new Json(hdpInfo.getRepo()), stack);
                components.add(hdpRepoComponent);
                image = new Image(imageName, userData);
            }
            Component component = new Component(ComponentType.IMAGE, ComponentType.IMAGE.name(), new Json(image), stack);
            components.add(component);
            componentConfigProvider.store(components);
        } catch (JsonProcessingException e) {
            throw new CloudbreakServiceException("Failed to create json", e);
        }
    }


}
