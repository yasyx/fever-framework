package com.github.fanfever.fever.config.writer;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.fanfever.fever.item.User;

/**
 * 
 * @author fanfever
 * @email fanfeveryahoo@gmail.com
 * @url https://github.com/fanfever
 * @date 2016年8月5日
 */
@Configuration
public class SimpleWriterConfiguration {

	@Autowired
	public DataSource dataSource;

	@Bean
	@StepScope
	public JdbcBatchItemWriter<User> simpleWriter() {
		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<User>();
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
		writer.setSql("UPDATE user SET username = :username WHERE id = :id");
		return writer;
	}
}
