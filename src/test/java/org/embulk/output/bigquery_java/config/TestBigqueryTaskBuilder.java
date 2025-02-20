package org.embulk.output.bigquery_java.config;

import org.embulk.config.ConfigSource;
import org.embulk.output.bigquery_java.BigqueryJavaOutputPlugin;
import org.embulk.spi.OutputPlugin;
import org.embulk.test.TestingEmbulk;
import org.embulk.util.config.ConfigMapper;
import org.embulk.util.config.ConfigMapperFactory;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBigqueryTaskBuilder {
    private static final ConfigMapperFactory CONFIG_MAPPER_FACTORY = ConfigMapperFactory
            .builder()
            .addDefaultModules()
            .build();
    private static final ConfigMapper CONFIG_MAPPER = CONFIG_MAPPER_FACTORY.createConfigMapper();

    private ConfigSource config;
    private static final String BASIC_RESOURCE_PATH = "java/org/embulk/output/bigquery_java/";

    private static ConfigSource loadYamlResource(TestingEmbulk embulk, String fileName) {
        return embulk.loadYamlResource(BASIC_RESOURCE_PATH + fileName);
    }

    @Rule
    public TestingEmbulk embulk = TestingEmbulk.builder()
            .registerPlugin(OutputPlugin.class, "bigquery_java", BigqueryJavaOutputPlugin.class)
            .build();

    @Test
    public void setAbortOnError_DefaultMaxBadRecord_True() {
        config = loadYamlResource(embulk, "base.yml");
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        BigqueryTaskBuilder.setAbortOnError(task);

        assertEquals(0, task.getMaxBadRecords());
        assertEquals(true, task.getAbortOnError().get());
    }

    // TODO jsonl without compression, csv with/out compression
    @Test
    public void setFileExt_JSONL_GZIP_JSONL_GZ() {
        config = loadYamlResource(embulk, "base.yml");
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);

        BigqueryTaskBuilder.setFileExt(task);
        assertEquals("NEWLINE_DELIMITED_JSON", task.getSourceFormat());
        assertEquals("GZIP", task.getCompression());
        assertEquals(".jsonl.gz", task.getFileExt().get());
    }
}
