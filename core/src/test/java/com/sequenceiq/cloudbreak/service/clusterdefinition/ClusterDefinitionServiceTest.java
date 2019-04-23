package com.sequenceiq.cloudbreak.service.clusterdefinition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.api.endpoint.v4.common.ResourceStatus;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.Status;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.domain.ClusterDefinition;
import com.sequenceiq.cloudbreak.domain.stack.cluster.Cluster;
import com.sequenceiq.cloudbreak.domain.workspace.Workspace;
import com.sequenceiq.cloudbreak.repository.ClusterDefinitionRepository;
import com.sequenceiq.cloudbreak.service.Clock;
import com.sequenceiq.cloudbreak.service.cluster.ClusterService;

@RunWith(MockitoJUnitRunner.class)
public class ClusterDefinitionServiceTest {

    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private ClusterService clusterService;

    @Mock
    private ClusterDefinitionRepository clusterDefinitionRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private ClusterDefinitionService underTest;

    private final ClusterDefinition clusterDefinition = new ClusterDefinition();

    @Before
    public void setup() {
        clusterDefinition.setName("name");
        clusterDefinition.setWorkspace(new Workspace());
        clusterDefinition.setStatus(ResourceStatus.USER_MANAGED);
    }

    @Test
    public void testDeletionWithZeroClusters() {
        when(clusterService.findNotDeletedByClusterDefinition(any())).thenReturn(Collections.emptySet());

        ClusterDefinition deleted = underTest.delete(clusterDefinition);

        assertNotNull(deleted);
    }

    @Test
    public void testDeletionWithNonTerminatedCluster() {
        Cluster cluster = new Cluster();
        cluster.setName("c1");
        cluster.setId(1L);
        cluster.setClusterDefinition(clusterDefinition);
        cluster.setStatus(Status.AVAILABLE);
        exceptionRule.expect(BadRequestException.class);
        exceptionRule.expectMessage("c1");
        when(clusterService.findNotDeletedByClusterDefinition(any())).thenReturn(Set.of(cluster));

        underTest.delete(clusterDefinition);
    }

    @Test
    public void testDeleteByIdArchives() {
        ClusterDefinition clusterDefinition = new ClusterDefinition();
        clusterDefinition.setArchived(false);
        clusterDefinition.setStatus(ResourceStatus.USER_MANAGED);
        when(clusterDefinitionRepository.findById(2L)).thenReturn(Optional.of(clusterDefinition));

        underTest.delete(2L);

        assertTrue(clusterDefinition.isArchived());
        verify(clusterDefinitionRepository).save(clusterDefinition);
        verify(clusterDefinitionRepository, never()).delete(clusterDefinition);
    }

    @Test
    public void testDeletionWithTerminatedClusters() {
        when(clusterService.findNotDeletedByClusterDefinition(any())).thenReturn(Set.of());

        ClusterDefinition deleted = underTest.delete(clusterDefinition);

        assertNotNull(deleted);
    }

    @Test
    public void testDeletionWithTerminatedAndNonTerminatedClusters() {
        Cluster cluster1 = new Cluster();
        cluster1.setName("c1");
        cluster1.setId(1L);
        cluster1.setClusterDefinition(clusterDefinition);
        cluster1.setStatus(Status.AVAILABLE);

        when(clusterService.findNotDeletedByClusterDefinition(any())).thenReturn(Set.of(cluster1));

        try {
            underTest.delete(clusterDefinition);
        } catch (BadRequestException e) {
            assertTrue(e.getMessage().contains("c1"));
        }
    }
}