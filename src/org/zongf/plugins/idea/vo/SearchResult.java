package org.zongf.plugins.idea.vo;

/** 搜索结果
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public class SearchResult {

	// 标题1
    private String title;

    // 版本坐标
    private String groupId;

	// 版本坐标
    private String artifactId;

    // 描述
    private String description;

    // 最新版本发布日期
    private String lastDate;

    // 使用数量
    private String useages;

	public SearchResult() {
        super();
    }

	public SearchResult(String title, String groupId, String artifactId, String description, String lastDate, String useages) {
        super();
		this.title = title;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.description = description;
		this.lastDate = lastDate;
		this.useages = useages;
    }

    public void setTitle(String title){
		this.title=title;
	}

	public String getTitle(){
		return this.title;
	}

    public void setGroupId(String groupId){
		this.groupId=groupId;
	}

	public String getGroupId(){
		return this.groupId;
	}

    public void setArtifactId(String artifactId){
		this.artifactId=artifactId;
	}

	public String getArtifactId(){
		return this.artifactId;
	}

    public void setDescription(String description){
		this.description=description;
	}

	public String getDescription(){
		return this.description;
	}

    public void setLastDate(String lastDate){
		this.lastDate=lastDate;
	}

	public String getLastDate(){
		return this.lastDate;
	}

    public void setUseages(String useages){
		this.useages=useages;
	}

	public String getUseages(){
		return this.useages;
	}

    public String toString() {
		return getClass().getSimpleName() + "@" + hashCode() + ": {title:" + title + ", groupId:" + groupId + ", artifactId:" + artifactId + ", description:" + description + ", lastDate:" + lastDate + ", useages:" + useages  + "}";
	}

}
