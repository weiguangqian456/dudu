package com.weiwei.base.item;

public class VsAdConfigItem {
	private int ad_id;
	private String title;
	private String image_url;
	private String redirect_type;
	private String target_url;
	private String ad_place_id;
	private int sortid;
	
	private String redirect_target;

	public String getRedirect_target() {
		return redirect_target;
	}

	public void setRedirect_target(String redirect_target) {
		this.redirect_target = redirect_target;
	}

	public int getAdid() {
		return ad_id;
	}

	public String getAdpid() {
		return ad_place_id;
	}

	public void setAdpid(String ad_place_id) {
		this.ad_place_id = ad_place_id;
	}

	public void setAdid(int adid) {
		this.ad_id = adid;
	}

	public String getName() {
		return title;
	}

	public void setName(String name) {
		this.title = name;
	}

	public String getImg() {
		return image_url;
	}

	public void setImg(String img) {
		this.image_url = img;
	}

	public String getAdtype() {
		return redirect_type;
	}

	public void setAdtype(String adtype) {
		this.redirect_type = adtype;
	}

	public String getUrl() {
		return target_url;
	}

	public void setUrl(String url) {
		this.target_url = url;
	}

	public int getSortid() {
		return sortid;
	}

	public void setSortid(int sortid) {
		this.sortid = sortid;
	}
}
