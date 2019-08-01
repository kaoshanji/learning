package top.kaoshanji.learning.mybatis.mapper;

import top.kaoshanji.learning.mybatis.entity.MemberDistributionEntity;
import top.kaoshanji.learning.mybatis.vo.MemberDistributionListRequest;

import java.util.List;

/**
 * @author kaoshanji
 * @time 2019/8/1 14:47
 */
public interface MemberDistributionMapper {

    MemberDistributionEntity getMemberDistributionById(Long id);

    List<MemberDistributionEntity> getMemberDistributionList(MemberDistributionListRequest request);

}
