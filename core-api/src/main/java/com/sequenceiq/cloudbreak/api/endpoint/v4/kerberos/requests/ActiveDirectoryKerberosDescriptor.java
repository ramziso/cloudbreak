package com.sequenceiq.cloudbreak.api.endpoint.v4.kerberos.requests;

import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.KERBEROS_ADMIN_URL;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.KERBEROS_CONTAINER_DN;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.KERBEROS_LDAP_URL;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.KERBEROS_REALM;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.KERBEROS_URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.sequenceiq.cloudbreak.type.KerberosType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ActiveDirectoryKerberosDescriptor extends KerberosTypeBase {

    @ApiModelProperty(value = KERBEROS_URL, required = true)
    @NotNull
    @NotEmpty
    private String url;

    @ApiModelProperty(value = KERBEROS_ADMIN_URL, required = true)
    @NotNull
    @NotEmpty
    private String adminUrl;

    @ApiModelProperty(value = KERBEROS_REALM, required = true)
    @NotNull
    @NotEmpty
    private String realm;

    @ApiModelProperty(value = KERBEROS_LDAP_URL, required = true)
    @NotNull
    @NotEmpty
    private String ldapUrl;

    @ApiModelProperty(value = KERBEROS_CONTAINER_DN, required = true)
    @NotNull
    @NotEmpty
    private String containerDn;

    @ApiModelProperty(hidden = true)
    @Override
    public KerberosType getType() {
        return KerberosType.ACTIVE_DIRECTORY;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getContainerDn() {
        return containerDn;
    }

    public void setContainerDn(String containerDn) {
        this.containerDn = containerDn;
    }

}
