package com.hanover.bordereaux.partition;

import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
/**
 * 
 * @author CTS
 *
 */
public class BordereauxRangePartitioner implements Partitioner {
    @Autowired
	private JdbcOperations jdbcTemplate;
	
  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {
   System.out.println("partition called gridsize= " + gridSize);
	int min = jdbcTemplate.queryForObject("SELECT MIN(ID) from userdetails", Integer.class);
	int max = jdbcTemplate.queryForObject("SELECT MAX(ID) from userdetails", Integer.class);
	int targetSize = (max - min) / gridSize + 1;

	Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();
	int number = 0;
	int start = min;
	int end = start + targetSize - 1;

	while (start <= max) {
		ExecutionContext value = new ExecutionContext();
		result.put("partition" + number, value);

		if (end >= max) {
			end = max;
		}
		value.putInt("fromId", start);
		value.putInt("toId", end);
		start += targetSize;
		end += targetSize;
		number++;
	}

	return result;
  }
}
