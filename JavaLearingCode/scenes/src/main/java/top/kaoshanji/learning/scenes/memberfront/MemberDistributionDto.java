package top.kaoshanji.learning.scenes.memberfront;

import java.util.List;

/**
 * @author kaoshanji
 * @time 2019/6/21 17:40
 */
public class MemberDistributionDto {

    private Long id;
    private String memberId;
    private String parentId;
    private List<String> childList;
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

    public List<String> getChildList() {
        return childList;
    }

    public void setChildList(List<String> childList) {
        this.childList = childList;
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
