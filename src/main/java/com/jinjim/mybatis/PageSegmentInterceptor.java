//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jinjim.mybatis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PageSegmentInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageSegmentInterceptor.class);

    public PageSegmentInterceptor() {}


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object var14;
        try {
            if (invocation.getTarget() instanceof RoutingStatementHandler) {
                PageSegment pageSegment = PageSegment.get();
                if (Objects.nonNull(pageSegment)) {
                    Object[] args = invocation.getArgs();
                    RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
                    StatementHandler delegate = (StatementHandler) PageSegmentInterceptor.FieldReflect
                            .getFieldValue(statementHandler, "delegate");
                    MappedStatement mappedStatement = (MappedStatement) PageSegmentInterceptor.FieldReflect
                            .getFieldValue(delegate, "mappedStatement");
                    Connection connection = (Connection) args[0];
                    BoundSql boundSql = delegate.getBoundSql();
                    String businessSql = boundSql.getSql();
                    this.setTotalRecord(pageSegment, boundSql, mappedStatement, connection);
                    String pageSql = PageSegmentInterceptor.SqlParser.buildPageSql(pageSegment, businessSql);
                    PageSegmentInterceptor.FieldReflect.modify(boundSql, "sql", pageSql);
                }
            }

            var14 = invocation.proceed();
        } finally {
            PageSegment.clear();
        }

        return var14;
    }

    private void setTotalRecord(PageSegment pageSegment, BoundSql boundSql, MappedStatement mappedStatement,
            Connection connection) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String originalSql = boundSql.getSql();
        String countSql = PageSegmentInterceptor.SqlParser.buildCountSql(originalSql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        BoundSql countBoundSql =
                new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, parameterObject);
        DefaultParameterHandler parameterHandler =
                new DefaultParameterHandler(mappedStatement, parameterObject, countBoundSql);

        try {
            pstmt = connection.prepareStatement(countSql);
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int totalRecords = rs.getInt(1);
                pageSegment.setTotalRecords((long) totalRecords);
            }
        } catch (SQLException var17) {
            LOGGER.error("Get total count happen error:", var17);
        } finally {
            PageSegmentInterceptor.DBUtil.close((Statement) pstmt);
            PageSegmentInterceptor.DBUtil.close(rs);
        }

    }

    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    public void setProperties(Properties properties) {}

    private static class FieldReflect {
        private FieldReflect() {}

        private static void modify(Object object, String fieldName, Object newFieldValue) throws Exception {
            Field field = object.getClass().getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & -17);
            field.setAccessible(true);
            field.set(object, newFieldValue);
        }

        private static Object getFieldValue(Object obj, String fieldName) {
            if (obj == null) {
                return null;
            } else {
                Field targetField = getTargetField(obj.getClass(), fieldName);

                try {
                    return FieldUtils.readField(targetField, obj, true);
                } catch (IllegalAccessException var4) {
                    var4.printStackTrace();
                    return null;
                }
            }
        }

        private static Field getTargetField(Class<?> targetClass, String fieldName) {
            Field field = null;

            try {
                if (targetClass == null) {
                    return field;
                }

                if (Object.class.equals(targetClass)) {
                    return field;
                }

                field = FieldUtils.getDeclaredField(targetClass, fieldName, true);
                if (field == null) {
                    field = getTargetField(targetClass.getSuperclass(), fieldName);
                }
            } catch (Exception var4) {}

            return field;
        }
    }

    private static class DBUtil {
        private DBUtil() {}

        private static void close(Statement stmt) throws SQLException {
            if (Objects.nonNull(stmt)) {
                stmt.close();
            }

        }

        private static void close(ResultSet rs) throws SQLException {
            if (Objects.nonNull(rs)) {
                rs.close();
            }

        }
    }

    public static class SqlParser {
        private static final CCJSqlParserManager PARSERMANAGER = new CCJSqlParserManager();
        private static final String GROUPBY_SQL_TEMPLATE = "SELECT COUNT(1) FROM (%s) AS DATA";
        private static final List<SelectItem> COUNTERITEMS = new ArrayList();

        public SqlParser() {}

        public static PlainSelect getPlainSelect(String sql) {
            StringReader sqlReader = new StringReader(sql);

            PlainSelect var4;
            try {
                net.sf.jsqlparser.statement.Statement stmt = PARSERMANAGER.parse(sqlReader);
                if (!(stmt instanceof Select)) {
                    return null;
                }

                Select selectStatement = (Select) stmt;
                var4 = (PlainSelect) selectStatement.getSelectBody();
            } catch (JSQLParserException var8) {
                PageSegmentInterceptor.LOGGER.error("Sql parse happen error:", var8);
                throw new RuntimeException(var8);
            } finally {
                close(sqlReader);
            }

            return var4;
        }

        private static void close(Reader sqlReader) {
            if (Objects.nonNull(sqlReader)) {
                try {
                    sqlReader.close();
                } catch (IOException var2) {
                    PageSegmentInterceptor.LOGGER.error("Close reader happend error.");
                    throw new RuntimeException(var2);
                }
            }

        }

        private static String buildCountSql(String sql) {
            String upperCaseSql = sql.toUpperCase();
            PlainSelect plain = getPlainSelect(upperCaseSql);
            if (Objects.isNull(plain)) {
                throw new RuntimeException("Not query sql.");
            } else if (CollectionUtils.isNotEmpty(plain.getJoins())) {
                throw new RuntimeException("Join not support.");
            } else {
                plain.setOrderByElements((List) null);
                if (CollectionUtils.isNotEmpty(plain.getGroupByColumnReferences())) {
                    return String.format("SELECT COUNT(1) FROM (%s) AS DATA", plain.toString());
                } else {
                    plain.setSelectItems(COUNTERITEMS);
                    return plain.toString();
                }
            }
        }

        private static String buildPageSql(PageSegment pageSegment, String sql) {
            StringBuffer sqlBuffer = new StringBuffer(sql);
            sqlBuffer.append(" LIMIT ").append(pageSegment.getOffset()).append(",").append(pageSegment.getPageSize());
            return sqlBuffer.toString();
        }

        static {
            COUNTERITEMS.add(new SelectExpressionItem(new Column("COUNT(1)")));
        }
    }
}
