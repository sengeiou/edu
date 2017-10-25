package com.ubt.alpha1e.data.model;

public class CommentInfo extends BaseModel {

	public String user_name;
	public long comment_id;
	public String comment_context;
	public String user_image;
	public long comment_time;
	public String comment_new_time;

	private CommentInfo thiz;

	@Override
	public CommentInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, CommentInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

}
