package me.khtn.model;

import java.util.List;

public class ListPlayerDTO {
	private List<PlayerScore> list;

	/**
	 * @return the list
	 */
	public List<PlayerScore> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<PlayerScore> list) {
		this.list = list;
	}
}
