package top.kaoshanji.learning.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kaoshanji.learning.common.constant.HttpClientUtilConfigInfo;
import top.kaoshanji.learning.mybatis.entity.MemberDistributionEntity;
import top.kaoshanji.learning.mybatis.mapper.MemberDistributionMapper;
import top.kaoshanji.learning.mybatis.vo.MemberDistributionListRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Mybatis 示例
 * @author kaoshanji
 * @time 2019/8/1 15:47
 */
public class MybatisTest {

    private final static Logger logger = LoggerFactory.getLogger(MybatisTest.class);

    SqlSessionFactory sqlSessionFactory = null;

    @Before
    public void before() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void getMemberDistributionById() {
        try (SqlSession session = sqlSessionFactory.openSession()) {

            MemberDistributionMapper mapper =  session.getMapper(MemberDistributionMapper.class);
            MemberDistributionEntity entity = mapper.getMemberDistributionById(2L);

            logger.info(HttpClientUtilConfigInfo.getGson().toJson(entity));
        }
    }

    @Test
    public void getMemberDistributionList() {
        try (SqlSession session = sqlSessionFactory.openSession()) {

            MemberDistributionMapper mapper =  session.getMapper(MemberDistributionMapper.class);

            MemberDistributionListRequest request = new MemberDistributionListRequest();
            request.setTreeLevel(7);

            List<MemberDistributionEntity> list = mapper.getMemberDistributionList(request);

            logger.info(HttpClientUtilConfigInfo.getGson().toJson(list));
        }
    }


}
