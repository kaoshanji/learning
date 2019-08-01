package top.kaoshanji.learning.mybatis.entity;

/**
 * @author kaoshanji
 * @time 2019/8/1 15:24
 */
public class MemberDistributionEntity {

    private Long id;
    private String memberId;
    private String parentId;
    private String frontIds;
    private Integer treeLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

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
