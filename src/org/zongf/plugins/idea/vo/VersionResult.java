package org.zongf.plugins.idea.vo;

/** 版本对象
 * @since 1.0
 * @author zongf
 * @created 2019-08-10
 */
public class VersionResult {

    // 版本号
    private String version;

    // 发布日期
    private String publishDate;

    // 使用数量
    private String Usages;

    // 所属仓库
    private String repository;

	public VersionResult() {
        super();
    }

	public VersionResult(String version, String publishDate, String Usages, String repository) {
        super();
		this.version = version;
		this.publishDate = publishDate;
		this.Usages = Usages;
		this.repository = repository;
    }

    public void setVersion(String version){
		this.version=version;
	}

	public String getVersion(){
		return this.version;
	}

    public void setPublishDate(String publishDate){
		this.publishDate=publishDate;
	}

	public String getPublishDate(){
		return this.publishDate;
	}

    public void setUsages(String Usages){
		this.Usages=Usages;
	}

	public String getUsages(){
		return this.Usages;
	}

    public void setRepository(String repository){
		this.repository=repository;
	}

	public String getRepository(){
		return this.repository;
	}

    public String toString() {
		return getClass().getSimpleName() + "@" + hashCode() + ": {version:" + version + ", publishDate:" + publishDate + ", Usages:" + Usages + ", repository:" + repository  + "}";
	}

}
