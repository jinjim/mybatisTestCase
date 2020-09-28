/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.jinjim.mybatis.PageSegmentInterceptor;
import java.io.Reader;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jinjim.mybatis.BranchMapper;
import com.jinjim.mybatis.PageSegment;
import com.jinjim.mybatis.domain.Branch;
import com.jinjim.mybatis.domain.BranchSearchBo;

public class BranchTest {

    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        // create a SqlSessionFactory
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        reader.close();

        // populate in-memory database
        sqlSessionFactory.getConfiguration().addMapper(BranchMapper.class);
        sqlSessionFactory.getConfiguration().addInterceptor(new PageSegmentInterceptor());
        SqlSession session = sqlSessionFactory.openSession();
        Connection conn = session.getConnection();
        reader = Resources.getResourceAsReader("branch.sql");
        ScriptRunner runner = new ScriptRunner(conn);
        runner.setLogWriter(null);
        runner.runScript(reader);
        reader.close();
        session.close();
    }

    @Test
    public void searchBranches() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        BranchSearchBo searchBo = new BranchSearchBo();
        searchBo.setMainBranchIds(Arrays.asList(1, 3, 4, 5, 7));
        PageSegment pageSegment = PageSegment.build(searchBo.getPageNo(), searchBo.getPageSize());
        BranchMapper mapper = sqlSession.getMapper(BranchMapper.class);
        List<Branch> branches = mapper.listBankBranch(searchBo);


    }

}
