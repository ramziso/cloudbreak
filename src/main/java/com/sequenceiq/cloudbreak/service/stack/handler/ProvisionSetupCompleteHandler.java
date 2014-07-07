package com.sequenceiq.cloudbreak.service.stack.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import reactor.event.Event;
import reactor.function.Consumer;

import com.sequenceiq.cloudbreak.conf.ReactorConfig;
import com.sequenceiq.cloudbreak.domain.CloudPlatform;
import com.sequenceiq.cloudbreak.service.stack.event.ProvisionSetupComplete;
import com.sequenceiq.cloudbreak.service.stack.flow.ProvisionContext;

@Component
public class ProvisionSetupCompleteHandler implements Consumer<Event<ProvisionSetupComplete>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionSetupCompleteHandler.class);

    @Autowired
    private ProvisionContext provisionContext;

    @Override
    public void accept(Event<ProvisionSetupComplete> event) {
        ProvisionSetupComplete provisionSetupComplete = event.getData();
        CloudPlatform cloudPlatform = provisionSetupComplete.getCloudPlatform();
        Long stackId = provisionSetupComplete.getStackId();
        LOGGER.info("Accepted {} event.", ReactorConfig.PROVISION_SETUP_COMPLETE_EVENT, stackId);
        provisionContext.buildStack(cloudPlatform, stackId, provisionSetupComplete.getSetupProperties(), provisionSetupComplete.getUserDataParams());
    }
}
