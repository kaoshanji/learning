package top.kaoshanji.learning.scenes.memberfront;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kaoshanji.learning.common.util.JbdcUtil;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化 用户关系网
 * @author kaoshanji
 * @time 2019/6/21 15:56
 */
public class MemberDistributionInit {

    private final static Logger logger = LoggerFactory.getLogger(MemberDistributionInit.class);


    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {

        logger.info("..............start..................");

        //***************默认的父节点***************treeLevel==2********************************************************
        String treeLevel2Sql = " SELECT id, member_id , parent_id FROM md_member_distribution WHERE parent_id IS NULL OR parent_id = '' ORDER BY id; ";
        List<MemberDistributionDto> treeLevel2List = getMemberDistributionDtoListBysql(treeLevel2Sql);
        List<String> treeLevel2ListUpdateSql = new ArrayList<>();
        String treeLevel2UpdateSql = " UPDATE md_member_distribution SET tree_level = 2, front_ids = 'memberIdv' WHERE id = Idv ";

        // 当前节点的id，下级节点的父id
        List<String> treeLevel2MemberIdList = new ArrayList<>();
        // 子节点继承这里的信息
        Map<String, MemberDistributionDto> treeLevel2Map = new HashMap<>();

        for (MemberDistributionDto dto : treeLevel2List) {

            Long id = dto.getId();
            String memberId = dto.getMemberId();
            treeLevel2MemberIdList.add(memberId);

            treeLevel2ListUpdateSql.add(treeLevel2UpdateSql.replace("memberIdv", id+"").replace("Idv", id+""));

            dto.setTreeLevel(2);
            dto.setFrontIds(id+"");
            treeLevel2Map.put(memberId, dto);
        }

        // 更新 数据库
        JbdcUtil.updateMemberDistribution(treeLevel2ListUpdateSql);

        logger.info("..................tree_level = 2.....处理完成......................");

        logger.info("..................tree_level=3......这个有点特别...参数从上面传来..................");
        UpdateTreeLevelDto treeLevelDto = updateTreeLevel(treeLevel2MemberIdList, treeLevel2Map);

        // 只有两级节点
        for (int i = 0; i < 30; i++) {

            treeLevelDto = updateTreeLevel(treeLevelDto.getParentList(), treeLevelDto.getParentMap());

            if (null==treeLevelDto) {
                break;
            }

            logger.info("..............i..."+i+"..................");
        }

        logger.info("..............end..................");
    }


    /**
     * 计算 tree_level和front_ids 字段的值
     * @param parentList
     * @param parentMap
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private static UpdateTreeLevelDto updateTreeLevel(List<String> parentList, Map<String, MemberDistributionDto> parentMap) throws SQLException, ClassNotFoundException, InterruptedException {

        // 装载传递数据
        UpdateTreeLevelDto result = null;

        List<String> currentMemberIdList = new ArrayList<>();
        Map<String, MemberDistributionDto> currentMap = new HashMap<>();

        String currentUpdateSql = " UPDATE md_member_distribution SET tree_level = treeLevelv, front_ids = 'memberIdv' WHERE id = Idv ";
        List<String> currentListUpdateSql = new ArrayList<>();
        for (String memberId : parentList) {

            String currentSql = " SELECT id, member_id , parent_id FROM md_member_distribution WHERE parent_id = '"+ memberId +"' ORDER BY id; ";
            List<MemberDistributionDto> currentList = getMemberDistributionDtoListBysql(currentSql);
            if (CollectionUtils.isNotEmpty(currentList)) {

                MemberDistributionDto parentdto = parentMap.get(memberId);
                for (MemberDistributionDto currentDto : currentList) {

                    Long currentId = currentDto.getId();
                    String currentMemberId = currentDto.getMemberId();

                    // 继承上级内容
                    String currentFrontIds = parentdto.getFrontIds()+","+currentDto.getId();
                    Integer currentTreeLevel = parentdto.getTreeLevel()+1;

                    currentDto.setTreeLevel(currentTreeLevel);
                    currentDto.setFrontIds(currentFrontIds);

                    logger.info("..................tree_level="+currentTreeLevel+"..........................");

                    currentListUpdateSql.add(currentUpdateSql.replace("treeLevelv", currentTreeLevel+"").replace("memberIdv", currentFrontIds).replace("Idv", currentId+""));

                    // 传递给下一级节点
                    currentMemberIdList.add(currentMemberId);
                    currentMap.put(currentMemberId, currentDto);
                }
            }
        }

        // 更新 数据库
        if (CollectionUtils.isNotEmpty(currentListUpdateSql)) {
            JbdcUtil.updateMemberDistribution(currentListUpdateSql);
        }

        if (CollectionUtils.isNotEmpty(currentMemberIdList)) {
            result = new UpdateTreeLevelDto();
            result.setParentList(currentMemberIdList);
            result.setParentMap(currentMap);
        }

        return result;

    }

    /**
     * 查询转换数据对象
     * @param selectSql
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static List<MemberDistributionDto> getMemberDistributionDtoListBysql(String selectSql) throws SQLException, ClassNotFoundException {

        Connection connection = JbdcUtil.getConnection();
        Statement star = connection.createStatement();
        ResultSet rs = star.executeQuery(selectSql);
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet rowSet = factory.createCachedRowSet();
        rowSet.populate(rs);

        List<MemberDistributionDto> result = new ArrayList<>();
        MemberDistributionDto dto = null;
        while (rowSet.next()) {
            dto = new MemberDistributionDto();

            Long id = rowSet.getLong("id");
            String memberId = rowSet.getString("member_id");
            String parentId = rowSet.getString("parent_id");

            dto.setId(id);
            dto.setMemberId(memberId);
            dto.setParentId(parentId);

            result.add(dto);
        }

        connection.close();

        return result;
    }



    /**
     *
     * ## root
     * # id = 9
     *
     * # 1、2级，最上面
     * SELECT * FROM md_member_distribution WHERE tree_level in (1,2) ORDER BY id ASC
     *
     * # 选择 id=9 作为根节点
     * SELECT * FROM md_member_distribution WHERE id = 9
     *
     * # member_id = 00b96970161b11e9ba6f654ec74cd035
     * # parent_id = 00b96970161b11e9ba6f654ec74cd035
     * # front_ids = 9
     *
     * # 更新 根节点
     * UPDATE md_member_distribution SET tree_level = 1,parent_id = '00b96970161b11e9ba6f654ec74cd035' WHERE id = 9
     *
     * # 更新 2 级节点
     * UPDATE md_member_distribution SET parent_id='00b96970161b11e9ba6f654ec74cd035', front_ids=concat('9,',front_ids) WHERE tree_level = 2;
     *
     * # 更新 3级及以下节点
     * UPDATE md_member_distribution SET front_ids=concat('9,',front_ids) WHERE tree_level >= 3;
     *
     * # 更新 其他字段
     * UPDATE md_member_distribution SET rule_id = 1, rank = 1, rank_name = 'VIP',direct_store = 0, umbrella_store = 0;
     *
     */

}
