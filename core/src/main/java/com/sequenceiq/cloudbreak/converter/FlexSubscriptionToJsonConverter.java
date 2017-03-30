package com.sequenceiq.cloudbreak.converter;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.FlexSubscriptionResponse;
import com.sequenceiq.cloudbreak.api.model.SmartSenseSubscriptionJson;
import com.sequenceiq.cloudbreak.domain.FlexSubscription;

@Component
public class FlexSubscriptionToJsonConverter extends AbstractConversionServiceAwareConverter<FlexSubscription, FlexSubscriptionResponse> {

    @Inject
    private SmartSenseSubscriptionToJsonConverter smartSenseSubscriptionToJsonConverter;

    @Override
    public FlexSubscriptionResponse convert(FlexSubscription source) {
        FlexSubscriptionResponse json = new FlexSubscriptionResponse();
        json.setId(source.getId());
        json.setName(source.getName());
        json.setOwner(source.getOwner());
        json.setAccount(source.getAccount());
        json.setPublicInAccount(source.isPublicInAccount());
        json.setSubscriptionId(source.getSubscriptionId());
        json.setSmartSenseSubscriptionId(source.getSmartSenseSubscription().getId());
        SmartSenseSubscriptionJson smartSenseSubscriptionJson = smartSenseSubscriptionToJsonConverter.convert(source.getSmartSenseSubscription());
        json.setSmartSenseSubscription(smartSenseSubscriptionJson);
        json.setDefault(source.isDefault());
        json.setUsedForController(source.isUsedForController());
        return json;
    }
}
