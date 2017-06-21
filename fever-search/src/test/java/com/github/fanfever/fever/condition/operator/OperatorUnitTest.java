package com.github.fanfever.fever.condition.operator;

import com.github.fanfever.fever.condition.ConditionUtils;
import com.github.fanfever.fever.condition.request.DataBaseConditionRequest;
import com.github.fanfever.fever.condition.request.MemoryConditionRequest;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static com.github.fanfever.fever.DataSource.ELASTICSEARCH;
import static com.github.fanfever.fever.DataSource.MYSQL;
import static com.github.fanfever.fever.condition.operator.Operator.*;
import static com.github.fanfever.fever.condition.type.ValueType.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by fanfever on 2017/6/20.
 * Email fanfeveryahoo@gmail.com
 * Url https://github.com/fanfever
 */
public class OperatorUnitTest {

    @Test
    public void memoryIsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, IS, "fever");
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, IS, 1);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, IS, LocalDateTime.of(2017, 1, 1, 1, 1));
        MemoryConditionRequest comma_split = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, IS, "1,2");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string, bigDecimal, localDateTime, comma_split));
        assertThat(result, is(true));
    }

    @Test
    public void databaseIsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, IS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, IS, "longText", null);
        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, IS, "2017-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, IS, 1, null);
        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, IS, "1,2", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text = 'text')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(longText = 'longText')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(3, "(time = '2017-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(4, "(numeric = '1')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(5, "(comma_split = '1,2')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(3), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(4), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(5), "$..value"), notNullValue());
    }

    @Test
    public void memoryNotTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, NOT, "feve");
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, NOT, 2);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, NOT, LocalDateTime.of(2017, 1, 1, 1, 2));
        MemoryConditionRequest comma_split = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, NOT, "1");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string, bigDecimal, localDateTime, comma_split));
        assertThat(result, is(true));
    }

    @Test
    public void databaseNotTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, NOT, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, NOT, "longText", null);
        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, NOT, "2017-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, NOT, 1, null);
        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, NOT, "1,2", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text <> 'text')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(longText <> 'longText')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(3, "(time <> '2017-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(4, "(numeric <> '1')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(5, "(comma_split <> '1,2')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(3), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(4), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(5), "$..value"), notNullValue());
    }

    @Test
    public void memoryPrefixContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, PREFIX_CONTAINS, "fe");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databasePrefixContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, PREFIX_CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, PREFIX_CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text LIKE 'text%')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryPrefixNotContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, PREFIX_NOT_CONTAINS, "ef");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databasePrefixNotContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, PREFIX_NOT_CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, PREFIX_NOT_CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text NOT LIKE 'text%')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memorySuffixContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, SUFFIX_CONTAINS, "er");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseSuffixContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, SUFFIX_CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, SUFFIX_CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text LIKE '%text')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memorySuffixNotContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, SUFFIX_NOT_CONTAINS, "re");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseSuffixNotContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, SUFFIX_NOT_CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, SUFFIX_NOT_CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text NOT LIKE '%text')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, CONTAINS, "ve");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text LIKE '%text%')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryNotContainsTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, NOT_CONTAINS, "re");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseNotContainsTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, NOT_CONTAINS, "text", null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, NOT_CONTAINS, "longText", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text NOT LIKE '%text%')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryIsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2,3").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, IS_ANY, "1,4");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseIsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, IS_ANY, "1,2", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(FIND_IN_SET('1', comma_split) OR FIND_IN_SET('2', comma_split))"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryNotAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2,3,4").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, NOT_ANY, "1,5");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(false));
    }

    @Test
    public void databaseNotAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, NOT_ANY, "1,2", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(NOT FIND_IN_SET('1', comma_split) AND NOT FIND_IN_SET('2', comma_split))"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, CONTAINS_ANY, "gle,tfo");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, CONTAINS_ANY, "google,microsoft", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryNotContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, NOT_CONTAINS_ANY, "gle,tfo");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(false));
    }

    @Test
    public void databaseNotContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, NOT_CONTAINS_ANY, "google,micro", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryPrefixContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, PREFIX_CONTAINS_ANY, "mic");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databasePrefixContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, PREFIX_CONTAINS_ANY, "google1", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryPrefixNotContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, PREFIX_NOT_CONTAINS_ANY, "mic");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(false));
    }

    @Test
    public void databasePrefixNotContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, PREFIX_NOT_CONTAINS_ANY, "google2", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memorySuffixContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, SUFFIX_CONTAINS_ANY, "gle");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(true));
    }

    @Test
    public void databaseSuffixContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, SUFFIX_CONTAINS_ANY, "google3", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memorySuffixNotContainsAnyTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("google,microsoft").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, SUFFIX_NOT_CONTAINS_ANY, "oft");

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string));
        assertThat(result, is(false));
    }

    @Test
    public void databaseSuffixNotContainsAnyTest() throws Exception {
        //conditions

        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, SUFFIX_NOT_CONTAINS_ANY, "google4", null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, ""));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryIsNullTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, IS_NULL, null);
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, IS_NULL, null);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, IS_NULL, null);
        MemoryConditionRequest comma_split = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, IS_NULL, null);

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string, bigDecimal, localDateTime, comma_split));
        assertThat(result, is(false));
    }

    @Test
    public void databaseIsNullTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, IS_NULL, null, null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, IS_NULL, null, null);
        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, IS_NULL, null, null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, IS_NULL, null, null);
        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, IS_NULL, null, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text IS NULL OR text = '')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(longText IS NULL OR longText = '')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(3, "(time IS NULL)"));
        assertThat(mysqlConditionWrapperMap, hasEntry(4, "(numeric IS NULL)"));
        assertThat(mysqlConditionWrapperMap, hasEntry(5, "(comma_split IS NULL)"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(3), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(4), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(5), "$..value"), notNullValue());
    }

    @Test
    public void memoryIsNotNullTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest string = MemoryConditionRequest.of(bean, "string", TEXT, IS_NOT_NULL, null);
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, IS_NOT_NULL, null);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, IS_NOT_NULL, null);
        MemoryConditionRequest comma_split = MemoryConditionRequest.of(bean, "comma_split", COMMA_SPLIT, IS_NOT_NULL, null);

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(string, bigDecimal, localDateTime, comma_split));
        assertThat(result, is(true));
    }

    @Test
    public void databaseIsNotNullTest() throws Exception {
        //conditions

        DataBaseConditionRequest text = DataBaseConditionRequest.of("text", TEXT, IS_NOT_NULL, null, null);
        DataBaseConditionRequest longText = DataBaseConditionRequest.of("longText", LONG_TEXT, IS_NOT_NULL, null, null);
        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, IS_NOT_NULL, null, null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, IS_NOT_NULL, null, null);
        DataBaseConditionRequest comma_split = DataBaseConditionRequest.of("comma_split", COMMA_SPLIT, IS_NOT_NULL, null, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(text IS NOT NULL OR text <> '')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(longText IS NOT NULL OR longText <> '')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(3, "(time IS NOT NULL)"));
        assertThat(mysqlConditionWrapperMap, hasEntry(4, "(numeric IS NOT NULL)"));
        assertThat(mysqlConditionWrapperMap, hasEntry(5, "(comma_split IS NOT NULL)"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(text, longText, time, numeric, comma_split));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(3), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(4), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(5), "$..value"), notNullValue());
    }

    @Test
    public void memoryGreaterThanTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, GREATER_THAN, 0.9);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, GREATER_THAN, LocalDateTime.of(2016, 1, 1, 1, 1));

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(bigDecimal, localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseGreaterThanTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, GREATER_THAN, "2017-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, GREATER_THAN, 0.9, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time, numeric));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(time > '2017-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(numeric > '0.9')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time, numeric));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryGreaterThanEqTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, GREATER_THAN_EQ, 1.0);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, GREATER_THAN_EQ, LocalDateTime.of(2017, 1, 1, 1, 1));

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(bigDecimal, localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseGreaterThanEqTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, GREATER_THAN_EQ, "2016-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, GREATER_THAN_EQ, 0.9, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time, numeric));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(time >= '2016-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(numeric >= '0.9')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time, numeric));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryLessThanTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, LESS_THAN, 1.1);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, LESS_THAN, LocalDateTime.of(2018, 1, 1, 1, 1));

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(bigDecimal, localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseLessThanTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, LESS_THAN, "2017-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, LESS_THAN, 0.9, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time, numeric));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(time < '2017-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(numeric < '0.9')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time, numeric));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryLessThanEqTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.of(2017, 1, 1, 1, 1)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest bigDecimal = MemoryConditionRequest.of(bean, "bigDecimal", NUMERIC, LESS_THAN_EQ, 1.0);
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, LESS_THAN_EQ, LocalDateTime.of(2018, 1, 1, 1, 1));

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(bigDecimal, localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseLessThanEqTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, LESS_THAN_EQ, "2016-01-01 00:00:00", null);
        DataBaseConditionRequest numeric = DataBaseConditionRequest.of("numeric", NUMERIC, LESS_THAN_EQ, 0.9, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time, numeric));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(time <= '2016-01-01 00:00:00')"));
        assertThat(mysqlConditionWrapperMap, hasEntry(2, "(numeric <= '0.9')"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time, numeric));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(2), "$..value"), notNullValue());
    }

    @Test
    public void memoryTodayTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.now()).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, TODAY, null);

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseTodayTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, TODAY, null, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(DATE(time) = DATE(NOW()))"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryYesterdayTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.now().minusDays(1L)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, YESTERDAY, null);

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseYesterdayTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, YESTERDAY, null, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(DATE(time) = DATE(NOW() - INTERVAL 1 DAY))"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

    @Test
    public void memoryTomorrowTest() throws Exception {
        //javaBean
        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.now().plusDays(1L)).comma_split("1,2").build();
        //conditions
        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, TOMORROW, null);

        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(localDateTime));
        assertThat(result, is(true));
    }

    @Test
    public void databaseTomorrowTest() throws Exception {
        //conditions

        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, TOMORROW, null, null);

        //mysql
        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time));
        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(DATE(time) = DATE(NOW() + INTERVAL 1 DAY))"));

        //elasticSearch
        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time));
        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
    }

//    @Test
//    public void memoryNextSevenDayTest() throws Exception {
//        //javaBean
//        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.now()).comma_split("1,2").build();
//        //conditions
//        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, NEXT_SEVEN_DAY, LocalDateTime.now().minusDays(6L));
//
//        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(localDateTime));
//        assertThat(result, is(true));
//    }
//
//    @Test
//    public void databaseNextSevenDayTest() throws Exception {
//        //conditions
//
//        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, NEXT_SEVEN_DAY, null, null);
//
//        //mysql
//        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time));
//        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(DATE(time) = DATE(NOW() + INTERVAL 1 DAY))"));
//
//        //elasticSearch
//        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time));
//        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
//    }
//
//    @Test
//    public void memoryLastSevenDayTest() throws Exception {
//        //javaBean
//        WaitValidBean bean = WaitValidBean.builder().string("fever").bigDecimal(new BigDecimal(1)).localDateTime(LocalDateTime.now().plusDays(1L)).comma_split("1,2").build();
//        //conditions
//        MemoryConditionRequest localDateTime = MemoryConditionRequest.of(bean, "localDateTime", TIME, TOMORROW, null);
//
//        boolean result = ConditionUtils.memoryValidate(Lists.newArrayList(localDateTime));
//        assertThat(result, is(true));
//    }
//
//    @Test
//    public void databaseLastSevenDayTest() throws Exception {
//        //conditions
//
//        DataBaseConditionRequest time = DataBaseConditionRequest.of("time", TIME, TOMORROW, null, null);
//
//        //mysql
//        Map<Integer, String> mysqlConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(MYSQL, Lists.newArrayList(time));
//        assertThat(mysqlConditionWrapperMap, hasEntry(1, "(DATE(time) = DATE(NOW() + INTERVAL 1 DAY))"));
//
//        //elasticSearch
//        Map<Integer, String> elasticSearchConditionWrapperMap = ConditionUtils.databaseSnippetConditionWrapper(ELASTICSEARCH, Lists.newArrayList(time));
//        assertThat(JsonPath.read(elasticSearchConditionWrapperMap.get(1), "$..value"), notNullValue());
//    }

    @Data
    @Builder
    private static class WaitValidBean {
        private BigDecimal bigDecimal;
        private String string;
        private LocalDateTime localDateTime;
        private String comma_split;
    }
}
