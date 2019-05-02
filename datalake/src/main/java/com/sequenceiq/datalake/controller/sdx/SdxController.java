package com.sequenceiq.datalake.controller.sdx;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;

import com.sequenceiq.datalake.api.endpoint.sdx.RedeploySdxClusterRequest;
import com.sequenceiq.datalake.api.endpoint.sdx.SdxClusterRequest;
import com.sequenceiq.datalake.api.endpoint.sdx.SdxClusterResponse;
import com.sequenceiq.datalake.api.endpoint.sdx.SdxEndpoint;
import com.sequenceiq.datalake.service.sdx.SdxService;
import com.sequenceiq.datalake.util.RestRequestThreadLocalService;

@Controller
public class SdxController implements SdxEndpoint {

    @Inject
    private RestRequestThreadLocalService restRequestThreadLocalService;

    @Inject
    private SdxService sdxService;

    @Override
    public SdxClusterResponse create(Long envId, @Valid SdxClusterRequest createSdxClusterRequest) {
        String userCrn = restRequestThreadLocalService.getUserCrn();
        return sdxService.createSdx(userCrn, createSdxClusterRequest);
    }

    @Override
    public void delete(Long envId) {
        String userCrn = restRequestThreadLocalService.getUserCrn();
        sdxService.deleteSdx(userCrn);
    }

    @Override
    public void redeploy(Long envId, @Valid RedeploySdxClusterRequest redeploySdxClusterRequest) {

    }

    @Override
    public SdxClusterResponse get(Long envId) {
        return null;
    }

    @Override
    public List<SdxClusterResponse> list(Long envId) {
        String userCrn = restRequestThreadLocalService.getUserCrn();
        return sdxService.listSdx(userCrn);
    }

}
