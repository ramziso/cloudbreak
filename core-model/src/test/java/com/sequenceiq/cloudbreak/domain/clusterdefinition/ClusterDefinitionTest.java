package com.sequenceiq.cloudbreak.domain.clusterdefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.hibernate.annotations.Where;
import org.junit.Test;

import com.sequenceiq.cloudbreak.domain.ClusterDefinition;

public class ClusterDefinitionTest {

    @Test
    public void testHasWhereAnnotation() {
        Where whereAnnotation = ClusterDefinition.class.getAnnotation(Where.class);

        assertNotNull(whereAnnotation);
        assertEquals("archived = false", whereAnnotation.clause());
    }
}
