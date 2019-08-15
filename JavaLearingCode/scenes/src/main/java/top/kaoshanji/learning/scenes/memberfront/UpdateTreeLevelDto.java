package top.kaoshanji.learning.scenes.memberfront;

import java.util.List;
import java.util.Map;

/**
 * @author kaoshanji
 * @time 2019/6/25 15:21
 */
public class UpdateTreeLevelDto {

    private List<String> parentList;
    private Map<String, MemberDistributionDto> parentMap;

    public List<String> getParentList() {
        return parentList;
    }

    public void setParentList(List<String> parentList) {
        this.parentList = parentList;
    }

    public Map<String, MemberDistributionDto> getParentMap() {
        return parentMap;
    }

    public void setParentMap(Map<String, MemberDistributionDto> parentMap) {
        this.parentMap = parentMap;
    }
}
