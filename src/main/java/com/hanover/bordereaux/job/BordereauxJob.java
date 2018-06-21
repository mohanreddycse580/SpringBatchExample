package com.hanover.bordereaux.job;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.hanover.bordereaux.modal.User;
import com.hanover.bordereaux.partition.BordereauxRangePartitioner;
import com.hanover.bordereaux.processor.BordereauxProcessor;
import com.hanover.bordereaux.tasklet.ClosingTasklet;

/**
 * 
 * @author CTS
 *
 */
@Configuration
public class BordereauxJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment environment;

	@Bean
	public Job PartitionJob() {
		return jobBuilderFactory.get("partitionJob").incrementer(new RunIdIncrementer()).start(masterStep())
				.next(step2()).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").tasklet(finishTask()).build();
	}

	@Bean
	public ClosingTasklet finishTask() {
		return new ClosingTasklet();
	}

	@Bean
	public Step masterStep() {
		return stepBuilderFactory.get("masterStep").partitioner(slave().getName(), rangePartitioner())
				.partitionHandler(masterSlaveHandler()).build();
	}

	@Bean
	public PartitionHandler masterSlaveHandler() {
		TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
		handler.setGridSize(Integer.parseInt(environment.getProperty("threadSize")));
		handler.setTaskExecutor(taskExecutor());
		handler.setStep(slave());
		try {
			handler.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler;
	}

	@Bean(name = "slave")
	public Step slave() {
		System.out.println("...........called slave .........");
		return stepBuilderFactory.get("slave").<User, User> chunk(100).reader(slaveReader(null, null, null))
				.processor(slaveProcessor(null)).writer(slaveWriter(null, null)).build();
	}

	@Bean
	public BordereauxRangePartitioner rangePartitioner() {
		return new BordereauxRangePartitioner();
	}

	@Bean
	public SimpleAsyncTaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
	@StepScope
	public BordereauxProcessor slaveProcessor(@Value("#{stepExecutionContext[name]}") String name) {
		System.out.println("********called slave processor **********");
		BordereauxProcessor userProcessor = new BordereauxProcessor();
		userProcessor.setThreadName(name);
		return userProcessor;
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<User> slaveReader(@Value("#{stepExecutionContext[fromId]}") final String fromId,
			@Value("#{stepExecutionContext[toId]}") final String toId,
			@Value("#{stepExecutionContext[name]}") final String name) {
		System.out.println("slaveReader start " + fromId + " " + toId);
		JdbcPagingItemReader<User> reader = new JdbcPagingItemReader();
		reader.setDataSource(dataSource);
		reader.setQueryProvider(queryProvider());
		Map<String, Object> parameterValues = new HashMap();
		parameterValues.put("fromId", fromId);
		parameterValues.put("toId", toId);
		System.out.println("Parameter Value " + name + " " + parameterValues);
		reader.setParameterValues(parameterValues);
		reader.setPageSize(1000);
		reader.setRowMapper(new BeanPropertyRowMapper<User>() {
			{
				setMappedClass(User.class);
			}
		});
		System.out.println("slaveReader end " + fromId + " " + toId);
		return reader;
	}

	@Bean
	public PagingQueryProvider queryProvider() {
		System.out.println("queryProvider start====== ");
		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
		provider.setDataSource(dataSource);
		provider.setSelectClause("select id, username, password, age");
		provider.setFromClause("from userdetails");
		provider.setWhereClause("where id >= :fromId and id <= :toId");
		provider.setSortKey("id");
		System.out.println("queryProvider end======= ");
		try {
			return provider.getObject();
		} catch (Exception e) {
			System.out.println("queryProvider exception ");
			e.printStackTrace();
		}

		return null;
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<User> slaveWriter(@Value("#{stepExecutionContext[fromId]}") final String fromId,
			@Value("#{stepExecutionContext[toId]}") final String toId) {
		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
		writer.setSql("INSERT INTO USERDETAILSNEW (ID, USERNAME,PASSWORD,AGE) VALUES (:id, :username,:password,:age)");
		writer.setDataSource(dataSource);
		return writer;
	}

}
