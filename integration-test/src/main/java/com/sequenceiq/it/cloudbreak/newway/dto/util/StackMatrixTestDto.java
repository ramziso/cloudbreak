package com.sequenceiq.it.cloudbreak.newway.dto.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.util.responses.StackMatrixV4Response;
import com.sequenceiq.it.cloudbreak.newway.CloudbreakClient;
import com.sequenceiq.it.cloudbreak.newway.Prototype;
import com.sequenceiq.it.cloudbreak.newway.context.TestContext;
import com.sequenceiq.it.cloudbreak.newway.dto.AbstractCloudbreakTestDto;

@Prototype
public class StackMatrixTestDto extends AbstractCloudbreakTestDto<Object, StackMatrixV4Response, StackMatrixTestDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackMatrixTestDto.class);

    protected StackMatrixTestDto(TestContext testContext) {
        super(null, testContext);
    }

    @Override
    public StackMatrixTestDto valid() {
        return this;
    }

    @Override
    public void cleanUp(TestContext context, CloudbreakClient cloudbreakClient) {
        LOGGER.debug("this entry point does not have any clean up operation");
    }

    @Override
    public int order() {
        return 500;
    }

}
