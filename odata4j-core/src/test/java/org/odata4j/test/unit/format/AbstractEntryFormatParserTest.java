package org.odata4j.test.unit.format;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.odata4j.core.ODataVersion;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.format.Entry;
import org.odata4j.format.FormatParser;
import org.odata4j.format.FormatParserFactory;
import org.odata4j.format.FormatType;
import org.odata4j.format.Settings;

public abstract class AbstractEntryFormatParserTest {

  protected static final String DATETIME_NAME = "DateTime";
  protected static final String DATETIMEOFFSET_NAME = "DateTimeOffset";
  protected static final String TIME_NAME = "Time";
  protected static final String ENTITYSET_NAME = "EntitySet";

  protected static final LocalDateTime DATETIME = new LocalDateTime(2005, 4, 3, 1, 2);
  protected static final LocalDateTime DATETIME_WITH_SECONDS = new LocalDateTime(2006, 5, 4, 1, 2, 3);
  protected static final LocalDateTime DATETIME_WITH_MILLIS = new LocalDateTime(2007, 6, 5, 1, 2, 3, 4);

  protected static final DateTime DATETIME_BEFORE_1970_NO_OFFSET = new DateTime(1969, 8, 7, 5, 6, 0, 0, DateTimeZone.UTC);
  protected static final DateTime DATETIME_WITH_SECONDS_POSITIVE_OFFSET = new DateTime(2006, 5, 4, 1, 2, 3, 0, DateTimeZone.forOffsetHours(7)); // => 2006-05-03T18:02:03Z, 1146679323000
  protected static final DateTime DATETIME_WITH_MILLIS_NEGATIVE_OFFSET = new DateTime(2007, 6, 5, 1, 2, 3, 4, DateTimeZone.forOffsetHours(-8)); // => 2007-06-05T09:02:03Z, 1181034123004

  protected static final LocalTime TIME = new LocalTime(1, 2, 3);
  protected static final LocalTime TIME_WITH_MILLIS = new LocalTime(1, 2, 3, 4);

  protected static FormatParser<Entry> formatParser;

  protected static void createFormatParser(FormatType format) {
    formatParser = FormatParserFactory.getParser(Entry.class, format, getSettings());
  }

  protected void verifyDateTimePropertyValue(Entry entry, LocalDateTime dateTime) {
    assertThat((LocalDateTime) entry.getEntity().getProperty(DATETIME_NAME).getValue(), is(dateTime));
  }

  protected void verifyDateTimeOffsetPropertyValue(Entry entry, DateTime dateTime) {
    assertThat((DateTime) entry.getEntity().getProperty(DATETIMEOFFSET_NAME).getValue(), is(dateTime));
  }

  protected void verifyTimePropertyValue(Entry entry, LocalTime time) {
    assertThat((LocalTime) entry.getEntity().getProperty(TIME_NAME).getValue(), is(time));
  }

  private static Settings getSettings() {
    return new Settings(ODataVersion.V1, getMetadata(), ENTITYSET_NAME, null, null);
  }

  private static EdmDataServices getMetadata() {
    EdmProperty.Builder dateTimeProperty = EdmProperty.newBuilder(DATETIME_NAME).setType(EdmSimpleType.DATETIME);
    EdmProperty.Builder dateTimeOffsetProperty = EdmProperty.newBuilder(DATETIMEOFFSET_NAME).setType(EdmSimpleType.DATETIMEOFFSET);
    EdmProperty.Builder timeProperty = EdmProperty.newBuilder(TIME_NAME).setType(EdmSimpleType.TIME);
    EdmEntityType.Builder entityType = new EdmEntityType.Builder().setName("EntityType").addKeys("EntityKey").addProperties(dateTimeProperty, dateTimeOffsetProperty, timeProperty);
    EdmEntitySet.Builder entitySet = new EdmEntitySet.Builder().setName(ENTITYSET_NAME).setEntityType(entityType);
    EdmEntityContainer.Builder container = new EdmEntityContainer.Builder().addEntitySets(entitySet);
    EdmSchema.Builder schema = new EdmSchema.Builder().addEntityContainers(container).addEntityTypes(entityType);
    return new EdmDataServices.Builder().addSchemas(schema).build();
  }
}