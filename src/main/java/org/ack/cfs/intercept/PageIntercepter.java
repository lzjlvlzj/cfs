package org.ack.cfs.intercept;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.ack.cfs.common.Page;
import org.ack.cfs.util.ReflectUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;


/**
*
* 分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。
* <p>
* 利用拦截器实现Mybatis分页的原理：
* 要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，而且对应的Sql语句
* 是在Statement之前产生的，所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手。
* <p>
* 在Mybatis中Statement语句是通过RoutingStatementHandler对象的
* prepare方法生成的。所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用
* StatementHandler对象的prepare方法，即调用invocation.proceed()。
* <p>
* 对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少，这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设
* 置参数的功能把Sql语句中的参数进行替换，之后再执行查询记录数的Sql语句进行总记录数的统计。
*
*/
@Intercepts( {@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class}) })
public class PageIntercepter implements Interceptor{
	
	private String dataBaseType;//数据库类型 oralce or mysql

	/**
	 * 拦截后要执行的方法
	 */
	public Object intercept(Invocation invocation) throws Throwable {
		/*对于StatementHandler其实只有两个实现类，一个是RoutingStatementHandler，另一个是抽象类BaseStatementHandler，
	      BaseStatementHandler有三个子类，分别是SimpleStatementHandler，PreparedStatementHandler和CallableStatementHandler，
	    SimpleStatementHandler是用于处理Statement的，PreparedStatementHandler是处理PreparedStatement的，而CallableStatementHandler是
	          处理CallableStatement的。
	      Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个
	    StatementHandler类型的delegate属性，RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、
	    PreparedStatementHandler或CallableStatementHandler，在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。
	          我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候
	          是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象。
	    */
		RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();//取得拦截目标
		StatementHandler delegate = (StatementHandler)ReflectUtil.getFieldValue(handler, "delegate");
		/*获取到当前StatementHandler的 boundSql，这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，因为之前已经说过了
	       RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
	    * 
	    */
	    BoundSql boundSql = delegate.getBoundSql();
	    
	    //拿到当前绑定Sql的参数对象，就是我们在调用对应的Mapper映射语句时所传入的参数对象
	    Object obj = boundSql.getParameterObject();
	    if(obj instanceof Page<?>){
	    	Page<?> page = (Page<?>)obj;
	    	//通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
	    	MappedStatement mappedStatement = (MappedStatement)ReflectUtil.getFieldValue(delegate, "mappedStatement");
            //拦截到的prepare方法参数是一个Connection对象
            Connection connection = (Connection)invocation.getArgs()[0];
            //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
            String sql = boundSql.getSql();
            //给当前的page参数对象设置总记录数
            setTotalRecord(page, mappedStatement, connection);
            //获取分页Sql语句
            String pageSql = getPageSql(page, sql);
            //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
            ReflectUtil.setFieldValue(boundSql, "sql", pageSql);
	    }
	    
		return invocation.proceed();//go on
	}

	private String getPageSql(Page<?> page, String sql) {
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		if ("mysql".equalsIgnoreCase(dataBaseType)) {
			return  getMysqlPageSql(page, sb);
	    } else if ("oracle".equalsIgnoreCase(dataBaseType)) {
	    	String oracle = getOraclePageSql(page, sb);
	    	sb.append(oracle);
	    }
		return sb.toString();
	}

	private String getOraclePageSql(Page<?> page, StringBuilder sb) {
		//计算第一条记录的位置，Oracle分页是通过rownum进行的，而rownum是从1开始的
	    int offSet = (page.getCurrentPage() - 1) * page.getPageSize() + 1;
	    //原始 sql:  select * from user
	    sb.insert(0, " select t.*, rownum r from ( ");
	    sb.append(" )  t where rownum < ");
	    sb.append(offSet + page.getPageSize());//查询结束位置 = 当前位置 + 每页显示条数
	    //上面添加后结果 select t.*, rownum r from (select * from t_user) t where rownum < 31
	    sb.insert(0, " select * from ( ");
	    sb.append(" ) where r >= "); //开始查询位置
	    sb.append(offSet);
	    //上面添加结果 select * from (select t.*, rownum r from (select * from t_user) t where rownum < 31) where r >= 16
	    
		return sb.toString();
	}

	private String getMysqlPageSql(Page<?> page, StringBuilder sb) {
		//计算第一条记录的位置，Mysql中记录的位置是从0开始的。
	    int offSet= (page.getCurrentPage() - 1) * page.getPageSize();
	    sb.append(" limit " + offSet + ", " + page.getPageSize());
	    
		return sb.toString();
	}

	private void setTotalRecord(Page<?> page, MappedStatement mappedStatement,
			Connection connection) {
		//获取对应的BoundSql，这个BoundSql其实跟我们利用StatementHandler获取到的BoundSql是同一个对象。
	    //delegate里面的boundSql也是通过mappedStatement.getBoundSql(paramObj)方法获取到的。
        BoundSql boundSql = mappedStatement.getBoundSql(page);
        //获取到我们自己写在Mapper映射语句中对应的Sql语句
        String sql = boundSql.getSql();
        //通过查询Sql语句获取到对应的计算总记录数的sql语句
        String countSql = getCountSql(sql);
        //通过BoundSql获取对应的参数映射
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //利用Configuration、查询记录数的Sql语句countSql、参数映射关系parameterMappings和参数对象page建立查询记录数对应的BoundSql对象。
        BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, page);
        //通过mappedStatement、参数对象page和BoundSql对象countBoundSql建立一个用于设定参数的ParameterHandler对象
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, countBoundSql);
        //通过connection建立一个countSql对应的PreparedStatement对象。
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(countSql);
            //通过parameterHandler给PreparedStatement对象设置参数
            parameterHandler.setParameters(pstmt);
            //之后就是执行获取总记录数的Sql语句和获取结果了。
            rs = pstmt.executeQuery();
            if (rs.next()) {
               int totalRecord = rs.getInt(1);
               //给当前的参数page对象设置总记录数
               page.setTotalRecord(totalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
               if (rs != null)
                   rs.close();
                if (pstmt != null)
                   pstmt.close();
            } catch (SQLException e) {
               e.printStackTrace();
            }
        }
	}

	/**
	 * @param sql 分页sql
	 * @return 查询数量的sql
	 */
	private String getCountSql(String sql) {
		int index = sql.indexOf("from");
		String countSql = " select count(*) " + sql.substring(index);
		return countSql;
	}

	/**
	 * 封装对象
	 */
	public Object plugin(Object target) {
		
		return Plugin.wrap(target, this);
	}
	/**
	 * 取得自定义的一些属性
	 */
	public void setProperties(Properties properties) {
		
		dataBaseType = properties.getProperty("dataBaseType");
	}

}
