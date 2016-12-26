package com.way.app;


import android.app.Fragment;

/**
 * @author sbk
 * @createdate 2014-4-15 下午2:06:00
 * @Description: 栏目，展示用
 */
public class ColumnDiyVo {

	private Fragment frgament;
	private String cTitle;
	private String id;

	public String getCTitle() {
		return cTitle;
	}

	public void setCTitle(String cTitle) {
		this.cTitle = cTitle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Fragment getFrgament() {
		return frgament;
	}

	public void setFrgament(Fragment frgament) {
		this.frgament = frgament;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((frgament == null) ? 0 : frgament.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((cTitle == null) ? 0 : cTitle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		ColumnDiyVo other = (ColumnDiyVo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
