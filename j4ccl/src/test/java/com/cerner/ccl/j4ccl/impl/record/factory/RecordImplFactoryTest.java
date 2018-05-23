package com.cerner.ccl.j4ccl.impl.record.factory;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.cerner.ccl.j4ccl.impl.record.PrimitiveFieldImpl;
import com.cerner.ccl.j4ccl.impl.record.RecordImpl;
import com.cerner.ccl.j4ccl.impl.record.StructureImpl;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit Tests for {@link RecordImplFactory}
 *
 * @author Fred Eckertson
 *
 */
public class RecordImplFactoryTest {

    /**
     * Test the createRootRecord method.
     */
    @Test
    public void testCreateRootRecord() {
        final RecordImplFactory factory = new RecordImplFactory();
        final Map<String, Field> mapRootFields = new HashMap<String, Field>();
        mapRootFields.put("name", new PrimitiveFieldImpl("name", DataType.VC));
        mapRootFields.put("id", new PrimitiveFieldImpl("id", DataType.F8));
        final Structure rootStructure = new StructureImpl(mapRootFields);
        final Record record = factory.createRootRecord("root_record", rootStructure);
        assertThat(RecordImpl.class.isInstance(record));
    }

    /**
     * Test the createRootRecord and examime children.
     */
    @Test
    public void testx() {
        final RecordImplFactory factory = new RecordImplFactory();
        final Map<String, Field> mapRootFields = new HashMap<String, Field>();
        mapRootFields.put("name", new PrimitiveFieldImpl("name", DataType.VC));
        mapRootFields.put("id", new PrimitiveFieldImpl("id", DataType.F8));
        final Structure rootStructure = new StructureImpl(mapRootFields);
        final Record recordRoot = factory.createRootRecord("root_record", rootStructure);

        final Record recordChild = factory.createNestedRecord(rootStructure, recordRoot);
        assertThat(RecordImpl.class.isInstance(recordChild));
        assertThat(recordChild.getStructure()).isEqualTo(rootStructure);
    }
}
