import org.apache.commons.lang3.StringUtils;


public class SqlSelect {
	
	private String tableName;
	
	private StringBuilder columnBuilder = new StringBuilder();
	private StringBuilder whereBuilder = new StringBuilder();
	private StringBuilder groupBuilder = new StringBuilder();
	private StringBuilder orderBuilder = new StringBuilder();
	private StringBuilder limitBuilder = new StringBuilder();
	
	/**
	 * 标志sql语句是使用and还是or连接，通过and()和or()方法自动设置
	 */
	private String logicType;
	
	/**
	 * 设置要查询的表名
	 * @param tableName
	 */
	public SqlSelect(String tableName){
		this.tableName = tableName;
	}
	
	/**
	 * where条件
	 * @param whereClause
	 * @return
	 */
	public SqlSelect where(String whereClause){
		whereBuilder.setLength(0);
		whereBuilder.append("where ");
		whereBuilder.append(whereClause);
		return this;
	}
	
	/**
	 * and A = ?
	 * @param clause
	 * @return
	 */
	public SqlSelect and(String clause){
		
		if(whereBuilder.length() == 0){
			throw new RuntimeException("调用and之前必须先调用where方法！");
		}
		
		if(StringUtils.equalsIgnoreCase(logicType, "or")){
			throw new RuntimeException("同一个sql子句不能混用and和or！");
		}
		
		logicType = "and";
		whereBuilder.append(" and ");
		whereBuilder.append(clause);
		
		return this;
	}

	/**
	 * or A = ?
	 * @param clause
	 * @return
	 */
	public SqlSelect or(String clause){
		
		if(whereBuilder.length() == 0){
			throw new RuntimeException("调用or之前必须先调用where方法！");
		}
		
		if(StringUtils.equalsIgnoreCase(logicType, "and")){
			throw new RuntimeException("同一个sql子句不能混用and和or！");
		}
		
		logicType = "or";
		
		whereBuilder.append(" or ");
		whereBuilder.append(clause);
		
		return this;
	}
	
	/**
	 * order by ? desc(asc)
	 * @param field
	 * @param type
	 * @return
	 */
	public SqlSelect orderBy(String field, String type){
		
		if( ! StringUtils.equalsIgnoreCase("desc", type) &&
				! StringUtils.equalsIgnoreCase("asc", type)){
			throw new IllegalArgumentException("order type必须为asc或者desc！");
		}
		
		if(orderBuilder.length() > 0){
			orderBuilder.append(", ");
		}
		
		orderBuilder.append(field);
		orderBuilder.append(" ");
		orderBuilder.append(type);
		
		return this;
	}
	
	/**
	 * 添加需要查询的列
	 * @param column
	 * @return
	 */
	public SqlSelect addColumn(String column){
		if (columnBuilder.length() > 0) {
			columnBuilder.append(", ");
		}
		
		columnBuilder.append(column);
		
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public SqlSelect groupBy(String groupBy){
		if(groupBuilder.length() > 0){
			groupBuilder.append(", ");
		}
		groupBuilder.append(groupBy);
		return this;
	}
	
	/**
	 * limit from, count
	 * @param from
	 * @param count
	 * @return
	 */
	public SqlSelect limit(int from, int count){
		limitBuilder.setLength(0);
		limitBuilder.append("limit ");
		limitBuilder.append(from);
		limitBuilder.append(", ");
		limitBuilder.append(count);
		return this;
	}
	
	/**
	 * 等效于limit 0, count
	 * @param count
	 * @return
	 */
	public SqlSelect limit(int count){
		limitBuilder.setLength(0);
		limitBuilder.append("limit 0, ");
		limitBuilder.append(count);
		return this;
	}
	
	/**
	 * 查询数量，在设置select的查询列为count(*)
	 * @return
	 */
	public SqlSelect count() {
		columnBuilder.setLength(0);
		columnBuilder.append("count(*)");
		return this;
	}
	
	public SqlSelect paginate(@SuppressWarnings("rawtypes") Pagination page){
		
		limit(page.getFromIndex(), page.getPageSize());
		
		return this;
	}
	
	public SqlSelect clearColumn(){
		columnBuilder.setLength(0);
		return this;
	}
	
	/**
	 * 生成sql语句
	 * @return
	 */
	public String getSql(){
		
		if(columnBuilder.length() == 0){
			throw new IllegalArgumentException("必须指定要查询的字段！");
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("select ");
		builder.append(columnBuilder.toString());
		builder.append(" from ");
		builder.append(tableName);
		
		if(whereBuilder.length() > 0){
			builder.append(" ");
			builder.append(whereBuilder);
		}
		
		if(groupBuilder.length() > 0){
			builder.append(" group by ");
			builder.append(groupBuilder);
		}
		
		if(orderBuilder.length() > 0){
			builder.append(" order by ");
			builder.append(orderBuilder);
		}
		
		if(limitBuilder.length() > 0){
			builder.append(" ");
			builder.append(limitBuilder);
		}
		
		return builder.toString();
	}
	
	@Override
	public String toString(){
		return getSql();
	}
}
