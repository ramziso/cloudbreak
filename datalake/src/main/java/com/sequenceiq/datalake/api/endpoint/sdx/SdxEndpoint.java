package com.sequenceiq.datalake.api.endpoint.sdx;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/sdx")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/sdx", protocols = "http,https")
public interface SdxEndpoint {

    @POST
    @Path("{envId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create SDX cluster", produces = "application/json", nickname = "createSdx")
    SdxClusterResponse create(@PathParam("envId") Long envId, @Valid SdxClusterRequest createSdxClusterRequest);

    @DELETE
    @Path("{envId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "delete SDX cluster", produces = "application/json", nickname = "deleteSdx")
    void delete(@PathParam("envId") Long envId);

    @POST
    @Path("{envId}/redeploy")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "redeploy SDX cluster", produces = "application/json", nickname = "redeploySdx")
    void redeploy(@PathParam("envId") Long envId, @Valid RedeploySdxClusterRequest redeploySdxClusterRequest);

    @GET
    @Path("{envId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "get SDX cluster", produces = "application/json", nickname = "getSdx")
    SdxClusterResponse get(@PathParam("envId") Long envId);

    @GET
    @Path("{envId}/list")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "list SDX clusters", produces = "application/json", nickname = "listSdx")
    List<SdxClusterResponse> list(@PathParam("envId") Long envId);
}
