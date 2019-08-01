package top.kaoshanji.learning.mybatis.vo;

/**
 * @author kaoshanji
 * @time 2019/8/1 15:33
 */
public class MemberDistributionListRequest {

    private String frontIds;
    private Integer treeLevel;

    public String getFrontIds() {
        return frontIds;
    }

    public void setFrontIds(String frontIds) {
        this.frontIds = frontIds;
    }

    public Integer getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(Integer treeLevel) {
        this.treeLevel = treeLevel;
    }
}
