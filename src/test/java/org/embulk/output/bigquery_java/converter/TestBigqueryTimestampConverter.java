package org.embulk.output.bigquery_java.converter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import org.embulk.config.ConfigSource;
import org.embulk.output.bigquery_java.BigqueryJavaOutputPlugin;
import org.embulk.output.bigquery_java.BigqueryUtil;
import org.embulk.output.bigquery_java.config.BigqueryColumnOption;
import org.embulk.output.bigquery_java.config.BigqueryColumnOptionType;
import org.embulk.output.bigquery_java.config.PluginTask;
import org.embulk.spi.OutputPlugin;
import org.embulk.spi.time.Timestamp;
import org.embulk.test.TestingEmbulk;
import org.embulk.util.config.ConfigMapper;
import org.embulk.util.config.ConfigMapperFactory;
import org.junit.Rule;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class TestBigqueryTimestampConverter {
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
    public void testConvertTimestampToInteger() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "INTEGER");
        configSource.set("name", "key");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.INTEGER, columnOption, task);
        assertEquals(1588291200000L, node.get("key").asLong());
    }

    @Test
    public void testConvertTimestampToFloat() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "FLOAT");
        configSource.set("name", "key");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.FLOAT, columnOption, task);
        assertEquals(1588291200000L, node.get("key").asLong());
    }

    @Test
    public void testConvertTimestampToTimestampColumnOption() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.TIMESTAMP, null, task);
        assertEquals("2020-05-01 00:00:00.000000 +00:00", node.get("key").asText());
    }


    @Test
    public void testConvertTimestampToString() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "STRING");
        configSource.set("name", "key");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.STRING, columnOption, task);
        assertEquals("2020-05-01 00:00:00.000000 +00:00", node.get("key").asText());
    }

    @Test
    public void testConvertTimestampToString_withTimestampFormat() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "STRING");
        configSource.set("name", "key");
        configSource.set("timestamp_format", "%Y/%m/%d");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.STRING, columnOption, task);
        assertEquals("2020/05/01", node.get("key").asText());
    }

    @Test
    public void testConvertTimestampToDate() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "STRING");
        configSource.set("name", "key");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.DATE, columnOption, task);
        assertEquals("2020-05-01", node.get("key").asText());
    }

    @Test
    public void testConvertTimestampToDatetime() {
        ObjectNode node = BigqueryUtil.getObjectMapper().createObjectNode();
        config = loadYamlResource(embulk, "base.yml");
        ImmutableList.Builder<ConfigSource> builder = ImmutableList.builder();
        ConfigSource configSource = embulk.newConfig();
        configSource.set("type", "STRING");
        configSource.set("name", "key");
        builder.add(configSource);
        config.set("column_options",builder.build());
        BigqueryColumnOption columnOption = CONFIG_MAPPER.map(configSource, BigqueryColumnOption.class);
        final PluginTask task = CONFIG_MAPPER.map(config, PluginTask.class);
        // Fri May 01 2020 00:00:00
        Instant ts = Instant.ofEpochMilli(1588291200000L);

        BigqueryTimestampConverter.convertAndSet(node, "key", ts, BigqueryColumnOptionType.DATETIME, columnOption, task);
        assertEquals("2020-05-01 00:00:00.000000", node.get("key").asText());
    }
}
